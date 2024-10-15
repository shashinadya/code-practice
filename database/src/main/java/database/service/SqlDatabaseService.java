package database.service;

import database.entity.BaseEntity;
import database.exception.CreationDatabaseException;
import database.exception.DatabaseDoesNotExistException;
import database.exception.DatabaseOperationException;
import database.exception.DeletionDatabaseException;
import database.exception.IdDoesNotExistException;
import database.exception.IdProvidedManuallyException;
import database.exception.InvalidParameterValueException;
import database.exception.SetPreparedStatementValueException;
import database.helper.Settings;
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

import static database.service.ServiceConstants.ENTITY_IS_NOT_FOUND;
import static database.service.ServiceConstants.ID_PROVIDED_MANUALLY;
import static database.service.ServiceConstants.INVALID_PARAMETER_VALUE;

public class SqlDatabaseService implements DatabaseService {
    private static final Logger LOG = LoggerFactory.getLogger(SqlDatabaseService.class);
    private final MySQLConnectionPool connectionPool;
    private final int maxLimitValue;
    private final String databaseName;
    static final String UNABLE_CREATE_TABLE = "Unable to create table. Please check if it already exists";
    static final String UNABLE_DELETE_TABLE = "Unable to delete table. Please check if table does not exist";
    static final String TABLE_NOT_EXIST = "Table does not exist";
    static final String UNABLE_ADD_NEW_RECORD = "Unable to add new record to table";
    static final String UNABLE_DELETE_RECORD = "Unable to delete record from table";
    static final String UNABLE_DELETE_ALL_RECORDS = "Unable to delete all records from table";
    static final String ID_PARAMETER_NAME = "id";

    public SqlDatabaseService(Settings settings) {
        this.maxLimitValue = settings.getLimit();
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
        if (entity.getId() != null) {
            throw new IdProvidedManuallyException(ID_PROVIDED_MANUALLY);
        }

        Class<? extends BaseEntity> entityClass = entity.getClass();
        String tableName = entityClass.getSimpleName();

        Connection connection = connectionPool.getConnection();

        try {
            if (checkTableExists(databaseName, tableName, connection)) {
                String insertSQL = generateInsertSQL(entity, tableName);
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
                        if (generatedKeys.next()) {
                            entity.setId(generatedKeys.getInt(1));
                        } else {
                            LOG.error("Creating record failed in table: {}, {}", tableName, insertSQL);
                            throw new SQLException("Creating record failed, no ID obtained.");
                        }
                    }
                }
            } else {
                throw new DatabaseDoesNotExistException(TABLE_NOT_EXIST + ": " + tableName);
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
        String deleteRecordSQL = "DELETE FROM " + tableName + " WHERE id = ?";

        LOG.info("Executing SQL: {}", deleteRecordSQL);

        Connection connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteRecordSQL)) {
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
        String tableName = entityClass.getSimpleName();
        String selectAllRecordsSQL = "SELECT * FROM " + tableName + " LIMIT ?";

        List<T> entities = new ArrayList<>();

        LOG.info("Executing SQL: {}", selectAllRecordsSQL);

        Connection connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectAllRecordsSQL)) {
            preparedStatement.setInt(1, maxLimitValue);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    entities.add(createAndFillEntity(entityClass, resultSet));
                }
            }
        } catch (SQLException | ReflectiveOperationException e) {
            LOG.error("Error retrieving all records from table: {}, {}", tableName, e.getMessage());
            throw new DatabaseOperationException("Error retrieving all records from table" + tableName + ", " +
                    e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return entities;
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
    public <T extends BaseEntity, V> Iterable<T> getByFilters(Class<? extends BaseEntity> entityClass,
                                                              Map<String, V> filters) {
        String tableName = entityClass.getSimpleName();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM " + tableName);

        if (!filters.isEmpty()) {
            sqlBuilder.append(" WHERE ");
            List<String> conditions = new ArrayList<>();

            for (Map.Entry<String, V> filter : filters.entrySet()) {
                conditions.add(filter.getKey() + " = ?");
            }

            sqlBuilder.append(String.join(" AND ", conditions));
        }

        String selectRecordsByFiltersSQL = sqlBuilder.toString();

        LOG.info("Executing SQL: {}", selectRecordsByFiltersSQL);

        Connection connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectRecordsByFiltersSQL)) {
            int index = 1;
            for (Map.Entry<String, V> filter : filters.entrySet()) {
                preparedStatement.setObject(index++, filter.getValue());
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<T> entities = new ArrayList<>();
                while (resultSet.next()) {
                    entities.add(createAndFillEntity(entityClass, resultSet));
                }
                return entities;
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

    private String generateInsertSQL(BaseEntity entity, String tableName) {
        StringBuilder fields = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        List<Field> entityFields = getAllFields(entity.getClass());
        for (Field field : entityFields) {
            field.setAccessible(true);

            if (field.getName().equals(ID_PARAMETER_NAME)) {
                continue;
            }

            fields.append(field.getName()).append(", ");
            placeholders.append("?, ");
        }

        fields.setLength(fields.length() - 2);
        placeholders.setLength(placeholders.length() - 2);

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

    private <T extends BaseEntity> T createAndFillEntity(Class<? extends BaseEntity> entityClass, ResultSet
            resultSet)
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
}
