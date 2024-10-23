package database.service;

import database.entity.BaseEntity;
import database.exception.CreationDatabaseException;
import database.exception.TableDoesNotExistException;
import database.exception.DatabaseOperationException;
import database.exception.DeletionDatabaseException;
import database.exception.IdDoesNotExistException;
import database.exception.IdProvidedManuallyException;
import database.exception.InvalidParameterValueException;
import database.exception.NullOrEmptyListException;
import database.exception.SetPreparedStatementValueException;
import database.helper.Settings;
import database.helper.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static database.service.ServiceConstants.ENTITIES_LIST_NULL_OR_EMPTY;
import static database.service.ServiceConstants.ENTITY_IS_NOT_FOUND;
import static database.service.ServiceConstants.IDS_LIST_NULL_OR_EMPTY;
import static database.service.ServiceConstants.ID_PROVIDED_MANUALLY;
import static database.service.ServiceConstants.INVALID_PARAMETER_VALUE;

public class SqlDatabaseService implements DatabaseService {
    private static final Logger LOG = LoggerFactory.getLogger(SqlDatabaseService.class);
    private final MySQLConnectionPool connectionPool;
    private final int maxLimitValue;
    private final int batchSize;
    private final String databaseName;
    static final String UNABLE_CREATE_TABLE = "Unable to create table. Please check if it already exists";
    static final String UNABLE_DELETE_TABLE = "Unable to delete table. Please check if table does not exist";
    static final String TABLE_NOT_EXIST = "Table does not exist";
    static final String UNABLE_ADD_NEW_RECORD = "Unable to add new record to table";
    static final String UNABLE_DELETE_RECORD = "Unable to delete record or specific records from table";
    static final String UNABLE_DELETE_ALL_RECORDS = "Unable to delete all records from table";
    static final String ID_PARAMETER_NAME = "id";

    public SqlDatabaseService(Settings settings) {
        this.maxLimitValue = settings.getLimit();
        this.batchSize = settings.getBatchSize();
        this.databaseName = settings.getDatabaseName();
        this.connectionPool = new MySQLConnectionPool(settings);
    }

    @Override
    public boolean createTable(Class<? extends BaseEntity> entityClass) {
        String tableName = entityClass.getSimpleName();
        StringBuilder createTableSQL = new StringBuilder("CREATE TABLE " + tableName + " (");

        createTableSQL.append("id INT AUTO_INCREMENT PRIMARY KEY, ");

        List<Field> fields = getAllFields(entityClass);

        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName.equals(ID_PARAMETER_NAME)) {
                continue;
            }
            String fieldType = getSQLType(field.getType());
            createTableSQL.append(fieldName).append(" ").append(fieldType).append(", ");
        }

        createTableSQL.setLength(createTableSQL.length() - 2);
        createTableSQL.append(")");

        LOG.info("Executing SQL: {}", createTableSQL);

        Connection connection = connectionPool.getConnection();

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSQL.toString());
            return checkTableExists(databaseName, tableName, connection);
        } catch (SQLException e) {
            LOG.error("Unable to create new table: {}", e.getMessage());
            throw new CreationDatabaseException(UNABLE_CREATE_TABLE + ": " + e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public boolean deleteTable(Class<? extends BaseEntity> entityClass) {
        String tableName = entityClass.getSimpleName();
        String dropTableSQL = "DROP TABLE IF EXISTS " + tableName;

        LOG.info("Executing SQL: {}", dropTableSQL);

        Connection connection = connectionPool.getConnection();

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(dropTableSQL);
            return !checkTableExists(databaseName, tableName, connection);
        } catch (SQLException e) {
            LOG.error("Unable to delete table: {}, {}", tableName, e.getMessage());
            throw new DeletionDatabaseException(UNABLE_DELETE_TABLE + ": " + tableName + ", " + e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public <T extends BaseEntity> T addNewRecordToTable(T entity) {
        validateIdNotProvidedManually(entity);

        Class<? extends BaseEntity> entityClass = entity.getClass();
        String tableName = entityClass.getSimpleName();
        Connection connection = connectionPool.getConnection();

        try {
            if (checkTableExists(databaseName, tableName, connection)) {
                String insertSQL = generateInsertSQL(entityClass, tableName);
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL,
                        Statement.RETURN_GENERATED_KEYS)) {

                    setPreparedStatementValuesForInsert(preparedStatement, entity);

                    LOG.info("Executing SQL: {}", insertSQL);

                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows == 0) {
                        LOG.error("Creating record failed in table: {}, {}", tableName, insertSQL);
                        throw new SQLException("Creating record failed, no rows affected.");
                    }

                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        assignGeneratedKeys(generatedKeys, entity, tableName);
                    }
                }
            } else {
                throw new TableDoesNotExistException(TABLE_NOT_EXIST + ": " + tableName);
            }
        } catch (SQLException e) {
            LOG.error(UNABLE_ADD_NEW_RECORD + ": {}, {}", tableName, e.getMessage());
            throw new DatabaseOperationException(UNABLE_ADD_NEW_RECORD + ": " + e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return entity;
    }

    @Override
    public <T extends BaseEntity> Iterable<T> addNewRecordsToTable(Class<? extends BaseEntity> entityClass,
                                                                   List<T> entities) {
        validateEntities(entities);

        String tableName = entityClass.getSimpleName();
        Connection connection = connectionPool.getConnection();

        try {
            connection.setAutoCommit(false);

            if (checkTableExists(databaseName, tableName, connection)) {
                String insertSQL = generateInsertSQL(entityClass, tableName);

                try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL,
                        Statement.RETURN_GENERATED_KEYS)) {
                    executeBatchInsert(preparedStatement, entities, batchSize, insertSQL);

                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        for (T entity : entities) {
                            assignGeneratedKeys(generatedKeys, entity, tableName);
                        }
                    }
                }
                connection.commit();
            } else {
                throw new TableDoesNotExistException(TABLE_NOT_EXIST + ": " + tableName);
            }
        } catch (SQLException e) {
            rollbackTransaction(connection, tableName, e);
            throw new DatabaseOperationException(UNABLE_ADD_NEW_RECORD + ": " + e.getMessage());
        } finally {
            resetConnection(connection);
        }
        return entities;
    }

    @Override
    public <T extends BaseEntity> T updateRecordInTable(T entity, Integer id) {
        Class<? extends BaseEntity> entityClass = entity.getClass();
        String tableName = entityClass.getSimpleName();

        String updateSQL = generateUpdateSQL(entity, tableName);

        LOG.info("Executing SQL: {}", updateSQL);

        Connection connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            setPreparedStatementValuesForUpdate(preparedStatement, entity, id);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                LOG.error("Updating record failed in table {}: {}", tableName, updateSQL);
                throw new IdDoesNotExistException(ENTITY_IS_NOT_FOUND);
            }
        } catch (SQLException e) {
            LOG.error("Unable to update record in table {}: {}", tableName, e.getMessage());
            throw new DatabaseOperationException("Unable to update record in table " + ": " + tableName +
                    ", " + e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return entity;
    }

    @Override
    public boolean removeRecordFromTable(Class<? extends BaseEntity> entityClass, Integer id) {
        String tableName = entityClass.getSimpleName();
        String deleteSQL = generateDeleteSQL(tableName);

        LOG.info("Executing SQL: {}", deleteSQL);

        Connection connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            LOG.error(UNABLE_DELETE_RECORD + ": {}, {}", tableName, e.getMessage());
            throw new DeletionDatabaseException(UNABLE_DELETE_RECORD + ": " + tableName + ", " + e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public boolean removeSpecificRecordsFromTable(Class<? extends BaseEntity> entityClass, List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new NullOrEmptyListException(IDS_LIST_NULL_OR_EMPTY);
        }

        String tableName = entityClass.getSimpleName();
        Connection connection = connectionPool.getConnection();

        try {
            connection.setAutoCommit(false);

            if (checkTableExists(databaseName, tableName, connection)) {
                String deleteSQL = generateDeleteSQL(tableName);

                try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
                    executeBatchDelete(preparedStatement, ids, batchSize, deleteSQL);
                }
                connection.commit();
            } else {
                throw new TableDoesNotExistException(TABLE_NOT_EXIST + ": " + tableName);
            }
        } catch (SQLException e) {
            rollbackTransaction(connection, tableName, e);
            LOG.error(UNABLE_DELETE_RECORD + ": {}, {}", tableName, e.getMessage());
            throw new DeletionDatabaseException(UNABLE_DELETE_RECORD + ": " + tableName + ", " + e.getMessage());
        } finally {
            resetConnection(connection);
        }
        return true;
    }

    @Override
    public void removeAllRecordsFromTable(Class<? extends BaseEntity> entityClass) {
        String tableName = entityClass.getSimpleName();
        String deleteAllRecordsSQL = "DELETE FROM " + tableName;

        LOG.info("Executing SQL: {}", deleteAllRecordsSQL);

        Connection connection = connectionPool.getConnection();

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(deleteAllRecordsSQL);
        } catch (SQLException e) {
            LOG.error(UNABLE_DELETE_ALL_RECORDS + ": {}, {}", tableName, e.getMessage());
            throw new DeletionDatabaseException(UNABLE_DELETE_ALL_RECORDS + ": " + tableName + ", " + e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public <T extends BaseEntity> T getById(Class<? extends BaseEntity> entityClass, Integer id) {
        String tableName = entityClass.getSimpleName();
        String selectRecordByIdSQL = "SELECT * FROM " + tableName + " WHERE id = ?";

        LOG.info("Executing SQL: {}", selectRecordByIdSQL);

        Connection connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectRecordByIdSQL)) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return createAndFillEntity(entityClass, resultSet);
                } else {
                    return null;
                }
            }
        } catch (SQLException | ReflectiveOperationException e) {
            LOG.error(ENTITY_IS_NOT_FOUND + ": {}, {}, {}", id, tableName, e.getMessage());
            throw new DatabaseOperationException("Error retrieving record by id " + id + ", " + tableName +
                    ", " + e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public <T extends BaseEntity> Iterable<T> getAllRecordsFromTable(Class<? extends BaseEntity> entityClass) {
        return getAllRecordsFromTable(entityClass, maxLimitValue, 0);
    }

    @Override
    public <T extends BaseEntity> Iterable<T> getAllRecordsFromTable(Class<? extends BaseEntity> entityClass, int limit,
                                                                     int offset) {
        if (limit < 0 || limit > maxLimitValue || offset < 0) {
            LOG.error("Invalid value for limit {} or offset {} parameter", limit, offset);
            throw new InvalidParameterValueException(INVALID_PARAMETER_VALUE
                    .replace("{MAX_LIMIT_VALUE}", String.valueOf(maxLimitValue)));
        }

        String tableName = entityClass.getSimpleName();
        String selectAllRecordsWithParamsSQL = "SELECT * FROM " + tableName + " LIMIT ? OFFSET ?";

        List<T> entities = new ArrayList<>();

        LOG.info("Executing SQL: {}", selectAllRecordsWithParamsSQL);

        Connection connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectAllRecordsWithParamsSQL)) {
            preparedStatement.setInt(1, limit);
            preparedStatement.setInt(2, offset);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    entities.add(createAndFillEntity(entityClass, resultSet));
                }
            }
        } catch (SQLException | ReflectiveOperationException e) {
            LOG.error("Error retrieving records from table: {}, {}", tableName, e.getMessage());
            throw new DatabaseOperationException("Error retrieving records from table: " + tableName + ", "
                    + e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return entities;
    }

    @Override
    public <T extends BaseEntity> Iterable<T> getByFilters(Class<? extends BaseEntity> entityClass,
                                                           Map<String, List<String>> filters) {
        List<Field> fields = getAllFields(entityClass);
        Validator.validateDatabaseFilters(fields, filters);

        String tableName = entityClass.getSimpleName();
        String selectRecordsByFiltersSQL = "SELECT * FROM " + tableName + buildWhereClause(filters);

        LOG.info("Executing SQL: {}", selectRecordsByFiltersSQL);

        Connection connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectRecordsByFiltersSQL)) {
            setPreparedStatementParametersForFilters(preparedStatement, filters);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return createAndFillEntities(entityClass, resultSet);
            }
        } catch (SQLException | ReflectiveOperationException e) {
            LOG.error("Error retrieving records by filters: {}", e.getMessage());
            throw new DatabaseOperationException("Error retrieving records by filters");
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public void shutdown() {
        connectionPool.closePool();
        LOG.info("Service is stopped");
    }

    boolean checkTableExists(String databaseName, String tableName, Connection connection) throws SQLException {
        String checkTableSQL = "SELECT COUNT(*) FROM information_schema.tables " +
                "WHERE table_schema = ? AND table_name = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(checkTableSQL)) {
            preparedStatement.setString(1, databaseName);
            preparedStatement.setString(2, tableName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) == 1;
            }
        }
    }

    private List<Field> getAllFields(Class<?> entityClass) {
        List<Field> fields = new ArrayList<>();
        while (entityClass != null && entityClass != Object.class) {
            fields.addAll(Arrays.asList(entityClass.getDeclaredFields()));
            entityClass = entityClass.getSuperclass();
        }
        return fields;
    }

    private String getSQLType(Class<?> fieldType) {
        if (fieldType == int.class || fieldType == Integer.class) {
            return "INT";
        } else if (fieldType == long.class || fieldType == Long.class) {
            return "BIGINT";
        } else if (fieldType == String.class) {
            return "VARCHAR(255)";
        } else if (fieldType == boolean.class || fieldType == Boolean.class) {
            return "BOOLEAN";
        } else if (fieldType == double.class || fieldType == Double.class) {
            return "DOUBLE";
        } else if (fieldType == java.util.Date.class || fieldType == java.sql.Date.class) {
            return "DATE";
        }
        return "VARCHAR(255)";
    }

    private void setPreparedStatementValuesForInsert(PreparedStatement preparedStatement, BaseEntity entity) {
        setPreparedStatementValues(preparedStatement, entity, false, null);
    }

    private void setPreparedStatementValuesForUpdate(PreparedStatement preparedStatement, BaseEntity entity,
                                                     Integer id) {
        setPreparedStatementValues(preparedStatement, entity, true, id);
    }

    private void setPreparedStatementValues(PreparedStatement preparedStatement, BaseEntity entity, boolean includeId,
                                            Integer id) {
        List<Field> entityFields = getAllFields(entity.getClass());
        int parameterIndex = 1;
        String currentFieldName = "";

        try {
            for (Field field : entityFields) {
                currentFieldName = field.getName();
                if (field.getName().equals(ID_PARAMETER_NAME)) {
                    continue;
                }

                field.setAccessible(true);
                Object value = field.get(entity);

                setPreparedStatementValue(preparedStatement, parameterIndex, value);
                parameterIndex++;
            }

            if (includeId && id != null) {
                preparedStatement.setInt(parameterIndex, id);
            }
        } catch (IllegalAccessException | SQLException e) {
            LOG.error("Failed to set prepared statement values for field: {}, {}", currentFieldName, e.getMessage());
            throw new SetPreparedStatementValueException("Error setting prepared statement values: "
                    + e.getMessage());
        }
    }

    private void setPreparedStatementValue(PreparedStatement preparedStatement, int parameterIndex, Object value)
            throws SQLException {
        if (value != null) {
            if (value instanceof String) {
                preparedStatement.setString(parameterIndex, (String) value);
            } else if (value instanceof Integer) {
                preparedStatement.setInt(parameterIndex, (Integer) value);
            } else if (value instanceof Long) {
                preparedStatement.setLong(parameterIndex, (Long) value);
            } else if (value instanceof Date) {
                preparedStatement.setDate(parameterIndex, new java.sql.Date(((Date) value).getTime()));
            } else if (value instanceof Boolean) {
                preparedStatement.setBoolean(parameterIndex, (Boolean) value);
            } else {
                preparedStatement.setObject(parameterIndex, value);
            }
        } else {
            preparedStatement.setNull(parameterIndex, java.sql.Types.NULL);
        }
    }

    private void setPreparedStatementParametersForFilters(PreparedStatement preparedStatement,
                                                          Map<String, List<String>> filters) throws SQLException {
        int index = 1;
        for (Map.Entry<String, List<String>> filter : filters.entrySet()) {
            for (String value : filter.getValue()) {
                preparedStatement.setObject(index++, value);
            }
        }
    }

    private String buildWhereClause(Map<String, List<String>> filters) {
        if (filters.isEmpty()) {
            return "";
        }

        List<String> conditions = new ArrayList<>();
        for (Map.Entry<String, List<String>> filter : filters.entrySet()) {
            if (filter.getValue().size() > 1) {
                String placeholders = filter.getValue().stream()
                        .map(v -> "?")
                        .collect(Collectors.joining(", "));
                conditions.add(filter.getKey() + " IN (" + placeholders + ")");
            } else {
                conditions.add(filter.getKey() + " = ?");
            }
        }

        return " WHERE " + String.join(" AND ", conditions);
    }

    private String generateInsertSQL(Class<? extends BaseEntity> entityClass, String tableName) {
        List<String> fieldsAndPlaceholders = generateFieldsAndPlaceholders(entityClass);
        String fields = fieldsAndPlaceholders.get(0);
        String placeholders = fieldsAndPlaceholders.get(1);

        return "INSERT INTO " + tableName + " (" + fields + ") VALUES (" + placeholders + ")";
    }

    private <T extends BaseEntity> String generateUpdateSQL(T entity, String tableName) {
        StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");

        List<Field> entityFields = getAllFields(entity.getClass());
        for (Field field : entityFields) {
            if (field.getName().equals(ID_PARAMETER_NAME)) {
                continue;
            }
            sql.append(field.getName()).append(" = ?, ");
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE ").append(ID_PARAMETER_NAME).append(" = ?");

        return sql.toString();
    }

    private String generateDeleteSQL(String tableName) {
        return "DELETE FROM " + tableName + " WHERE id = ?";
    }

    private List<String> generateFieldsAndPlaceholders(Class<? extends BaseEntity> entityClass) {
        StringBuilder fields = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        List<Field> entityFields = getAllFields(entityClass);
        for (Field field : entityFields) {
            field.setAccessible(true);

            if (field.getName().equals(ID_PARAMETER_NAME)) {
                continue;
            }

            fields.append(field.getName()).append(", ");
            placeholders.append("?, ");
        }

        if (!fields.isEmpty()) {
            fields.setLength(fields.length() - 2);
        }
        if (!placeholders.isEmpty()) {
            placeholders.setLength(placeholders.length() - 2);
        }

        return List.of(fields.toString(), placeholders.toString());
    }

    private <T extends BaseEntity> T createAndFillEntity(Class<? extends BaseEntity> entityClass, ResultSet resultSet)
            throws ReflectiveOperationException, SQLException {
        T entity = (T) entityClass.getDeclaredConstructor().newInstance();
        List<Field> entityFields = getAllFields(entityClass);

        for (Field field : entityFields) {
            field.setAccessible(true);
            String columnName = field.getName();
            Object value = resultSet.getObject(columnName);
            field.set(entity, value);
        }
        return entity;
    }

    private <T extends BaseEntity> List<T> createAndFillEntities(Class<? extends BaseEntity> entityClass,
                                                                 ResultSet resultSet)
            throws ReflectiveOperationException, SQLException {
        List<T> entities = new ArrayList<>();
        while (resultSet.next()) {
            entities.add(createAndFillEntity(entityClass, resultSet));
        }
        return entities;
    }

    private <T extends BaseEntity> void validateEntities(List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            throw new NullOrEmptyListException(ENTITIES_LIST_NULL_OR_EMPTY);
        }
        for (T entity : entities) {
            validateIdNotProvidedManually(entity);
        }
    }

    private <T extends BaseEntity> void validateIdNotProvidedManually(T entity) {
        if (entity.getId() != null) {
            throw new IdProvidedManuallyException(ID_PROVIDED_MANUALLY);
        }
    }

    private <T extends BaseEntity> void executeBatchInsert(PreparedStatement preparedStatement, List<T> entities,
                                                           int batchSize, String insertSQL) throws SQLException {
        int count = 0;

        for (T entity : entities) {
            setPreparedStatementValues(preparedStatement, entity, false, null);
            preparedStatement.addBatch();

            if (++count % batchSize == 0) {
                preparedStatement.executeBatch();
            }
        }

        LOG.info("Executing SQL: {}", insertSQL);
        preparedStatement.executeBatch();
    }

    private void executeBatchDelete(PreparedStatement preparedStatement, List<Integer> ids, int batchSize,
                                    String deleteSQL) throws SQLException {
        int count = 0;

        for (Integer id : ids) {
            preparedStatement.setInt(1, id);
            preparedStatement.addBatch();

            if (++count % batchSize == 0) {
                preparedStatement.executeBatch();
            }
        }

        LOG.info("Executing SQL: {}", deleteSQL);
        preparedStatement.executeBatch();
    }

    private <T extends BaseEntity> void assignGeneratedKeys(ResultSet generatedKeys, T entity, String tableName)
            throws SQLException {
        if (generatedKeys.next()) {
            entity.setId(generatedKeys.getInt(1));
        } else {
            LOG.error("Creating record failed in table: {}", tableName);
            throw new SQLException("Creating record failed, no ID obtained.");
        }
    }

    private void rollbackTransaction(Connection connection, String tableName, SQLException e) {
        LOG.error(UNABLE_ADD_NEW_RECORD + ": {}, {}", tableName, e.getMessage());
        try {
            if (connection != null) {
                connection.rollback();
            }
        } catch (SQLException rollbackEx) {
            LOG.error("Rollback failed: {}", rollbackEx.getMessage());
        }
    }

    private void resetConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.setAutoCommit(true);
                connectionPool.releaseConnection(connection);
            } catch (SQLException e) {
                LOG.error("Failed to reset auto-commit: {}", e.getMessage());
            }
        }
    }
}
