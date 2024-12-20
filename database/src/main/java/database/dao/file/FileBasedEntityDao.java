package database.dao.file;

import database.dao.EntityDao;
import database.dao.EntityDaoBase;
import database.exception.CreationDatabaseException;
import database.exception.TableDoesNotExistException;
import database.exception.DeletionDatabaseException;
import database.exception.DeserializeDatabaseException;
import database.exception.IdDoesNotExistException;
import database.exception.InvalidParameterValueException;
import database.exception.NullOrEmptyListException;
import database.exception.ReadFileException;
import database.exception.SerializeDatabaseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import database.entity.BaseEntity;
import database.exception.WriteFileException;
import database.helper.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * FileBasedEntityDao extends {@link EntityDaoBase} and implements the {@link EntityDao} interface, providing
 * operations to manage JSON-based databases for entities that extend {@link BaseEntity}.
 * This class handles the creation, deletion, and manipulation of records in a file-based JSON database.
 *
 * <p>This dao uses the Jackson library for JSON serialization and deserialization
 * of entities. Each entity type is stored in a separate file, and the dao provides
 * methods for basic CRUD (Create, Read, Update, Delete) operations as well as filtering
 * and bulk data operations.
 *
 * <p>The dao supports assigning unique auto-incremented IDs to each new entity and
 * ensures that no entity has its ID manually assigned. It also provides mechanisms for
 * managing the underlying database files, including verifying the existence of database
 * files and handling exceptions related to file access and serialization issues.
 *
 * <p>Logging is performed using SLF4J, and the class includes detailed logging for error
 * scenarios such as file access issues, serialization errors, and invalid parameter values.
 *
 * <p>Key constants and exception messages are used throughout the class to ensure
 * consistent error handling and message logging. A {@link Settings} object is used
 * to configure dao properties such as the maximum limit of records retrieved
 * and the base path for storing database files.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public class FileBasedEntityDao extends EntityDaoBase {
    private final ObjectMapper objectMapper;
    private final Map<String, AtomicInteger> entityIds;
    private final int maxLimitValue;
    private final Path databasePath;
    private static final Logger LOG = LoggerFactory.getLogger(FileBasedEntityDao.class);
    static final String EMPTY_BRACKETS_TO_JSON = "[]";
    static final String UNABLE_CREATE_DB_FILE = "Unable to create database file. Please check if file already exists.";
    static final String UNABLE_DELETE_DB_FILE = "Unable to delete database file. Please check if file does not exist.";
    static final String DB_FILE_NOT_EXIST = "Database file does not exist";
    static final String UNABLE_SERIALIZE_DATA = "Unable to serialize data";
    static final String UNABLE_DESERIALIZE_DATA = "Unable to deserialize data";
    static final String UNABLE_ACCESS_PROPERTY = "Unable to access property";
    static final int ID_COUNTER_INITIAL_VALUE = -1;

    public FileBasedEntityDao(Settings settings) throws CreationDatabaseException {
        this.maxLimitValue = settings.getLimit();
        this.databasePath = settings.getDatabasePath();
        objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        entityIds = new ConcurrentHashMap<>();
    }

    @Override
    public boolean createTable(Class<? extends BaseEntity> entityClass) {
        File jsonDatabaseFile = new File(getDatabasePath(entityClass));
        try {
            if (!jsonDatabaseFile.createNewFile()) {
                throw new CreationDatabaseException(UNABLE_CREATE_DB_FILE);
            }
            entityIds.put(entityClass.getName(), new AtomicInteger(ID_COUNTER_INITIAL_VALUE));
            return Files.writeString(jsonDatabaseFile.toPath(), EMPTY_BRACKETS_TO_JSON).toFile().exists();
        } catch (IOException e) {
            LOG.error(UNABLE_CREATE_DB_FILE + ": {}", jsonDatabaseFile.toPath());
            throw new CreationDatabaseException(UNABLE_CREATE_DB_FILE);
        }
    }

    @Override
    public boolean deleteTable(Class<? extends BaseEntity> entityClass) {
        Path databasePath = Path.of(getDatabasePath(entityClass));
        verifyDatabaseExists(databasePath);

        try {
            Files.delete(databasePath);
            entityIds.remove(entityClass.getName());
            LOG.info("Database file deleted: {}", databasePath.toAbsolutePath());
        } catch (IOException e) {
            LOG.error(UNABLE_DELETE_DB_FILE + ": {}", databasePath.toAbsolutePath());
            throw new DeletionDatabaseException(UNABLE_DELETE_DB_FILE);
        }
        return !Files.exists(databasePath);
    }

    @Override
    public <T extends BaseEntity> T addNewRecordToTable(T entity) {
        validateIdNotProvidedManually(entity);

        Class<? extends BaseEntity> entityClass = entity.getClass();
        Path databasePath = Path.of(getDatabasePath(entityClass));
        verifyDatabaseExists(databasePath);

        List<T> entities = deserializeEntities(entityClass, readDatabaseFile(databasePath));
        String entityClassName = entityClass.getName();

        assignEntityId(entity, entityClassName);
        entities.add(entity);

        saveEntitiesToDatabase(entities, databasePath);
        return entity;
    }

    @Override
    public <T extends BaseEntity> Iterable<T> addNewRecordsToTable(Class<? extends BaseEntity> entityClass,
                                                                   List<T> entities) {
        validateEntities(entities);

        Path databasePath = Path.of(getDatabasePath(entityClass));
        verifyDatabaseExists(databasePath);

        List<T> existingEntities = deserializeEntities(entityClass, readDatabaseFile(databasePath));
        String entityClassName = entityClass.getName();

        for (T entity : entities) {
            validateIdNotProvidedManually(entity);
            assignEntityId(entity, entityClassName);
            existingEntities.add(entity);
        }

        saveEntitiesToDatabase(existingEntities, databasePath);
        return entities;
    }

    @Override
    public <T extends BaseEntity> T updateRecordInTable(T entity, Integer id) {
        Class<? extends BaseEntity> entityClass = entity.getClass();
        Path databasePath = Path.of(getDatabasePath(entityClass));
        verifyDatabaseExists(databasePath);

        List<T> entities = deserializeEntities(entityClass, readDatabaseFile(databasePath));

        T entityFoundById = entities.stream()
                .filter(e -> id.equals(e.getId()))
                .findFirst()
                .orElseThrow(() -> new IdDoesNotExistException(ENTITY_IS_NOT_FOUND));

        updateEntityFields(entityFoundById, entity);

        saveEntitiesToDatabase(entities, databasePath);
        return entityFoundById;
    }

    @Override
    public boolean removeRecordFromTable(Class<? extends BaseEntity> entityClass, Integer id) {
        Path databasePath = Path.of(getDatabasePath(entityClass));
        verifyDatabaseExists(databasePath);

        List<? extends BaseEntity> entities = deserializeEntities(entityClass, readDatabaseFile(databasePath));

        boolean isEntityRemoved = entities.removeIf(e -> id.equals(e.getId()));
        if (isEntityRemoved) {
            saveEntitiesToDatabase(entities, databasePath);
        }
        return true;
    }

    @Override
    public boolean removeSpecificRecordsFromTable(Class<? extends BaseEntity> entityClass, List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new NullOrEmptyListException(IDS_LIST_NULL_OR_EMPTY);
        }

        Path databasePath = Path.of(getDatabasePath(entityClass));
        verifyDatabaseExists(databasePath);

        List<? extends BaseEntity> entities = deserializeEntities(entityClass, readDatabaseFile(databasePath));

        boolean isAnyEntityRemoved = entities.removeIf(e -> ids.contains(e.getId()));

        if (isAnyEntityRemoved) {
            saveEntitiesToDatabase(entities, databasePath);
        }
        return true;
    }

    @Override
    public void removeAllRecordsFromTable(Class<? extends BaseEntity> entityClass) {
        Path databasePath = Path.of(getDatabasePath(entityClass));
        verifyDatabaseExists(databasePath);

        try {
            Files.writeString(databasePath, EMPTY_BRACKETS_TO_JSON);
        } catch (IOException e) {
            LOG.error("Unable to remove all data from file {}", databasePath.toAbsolutePath());
            throw new WriteFileException("Unable to write content to file.");
        }
    }

    @Override
    public <T extends BaseEntity> T getById(Class<? extends BaseEntity> entityClass, Integer id) {
        Path databasePath = Path.of(getDatabasePath(entityClass));
        verifyDatabaseExists(databasePath);

        List<T> entities = deserializeEntities(entityClass, readDatabaseFile(databasePath));

        return entities.stream()
                .filter(e -> id.equals(e.getId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public <T extends BaseEntity> Iterable<T> getAllRecordsFromTable(Class<? extends BaseEntity> entityClass) {
        return getAllRecordsFromTable(entityClass, maxLimitValue, 0);
    }

    @Override
    public <T extends BaseEntity> Iterable<T> getAllRecordsFromTable(Class<? extends BaseEntity> entityClass,
                                                                     int limit, int offset) {
        if (limit < 0 || limit > maxLimitValue || offset < 0) {
            LOG.error("Invalid value for limit {} or offset {} parameter", limit, offset);
            throw new InvalidParameterValueException(INVALID_PARAMETER_VALUE
                    .replace("{MAX_LIMIT_VALUE}", String.valueOf(maxLimitValue)));
        }

        Path databasePath = Path.of(getDatabasePath(entityClass));
        verifyDatabaseExists(databasePath);

        List<T> entities = deserializeEntities(entityClass, readDatabaseFile(databasePath));
        return entities.stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public <T extends BaseEntity> Iterable<T> getByFilters(Class<? extends BaseEntity> entityClass,
                                                           Map<String, List<String>> filters) {
        Path databasePath = Path.of(getDatabasePath(entityClass));
        verifyDatabaseExists(databasePath);

        List<Field> fields = getAllFields(entityClass);
        validateDatabaseFilters(fields, filters);

        List<T> entities = deserializeEntities(entityClass, readDatabaseFile(databasePath));

        return entities.stream()
                .filter(entity -> {
                    for (Map.Entry<String, List<String>> filter : filters.entrySet()) {
                        String fieldName = filter.getKey();
                        List<String> expectedValues = filter.getValue();
                        Field field = fields.stream()
                                .filter(e -> e.getName().equals(fieldName))
                                .findFirst()
                                .orElse(null);
                        String getterName = getGetterName(field);

                        try {
                            Method getterMethod = entityClass.getMethod(getterName);
                            Object actualValue = getterMethod.invoke(entity);

                            if (!expectedValues.contains(actualValue.toString())) {
                                return false;
                            }
                        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                            LOG.error(UNABLE_ACCESS_PROPERTY + ": {}", fieldName);
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void shutdown() {
        LOG.info(SHUTDOWN_MESSAGE);
    }

    String getDatabasePath(Class<? extends BaseEntity> entityClass) {
        return databasePath + File.separator + entityClass.getSimpleName() + "Table" + ".json";
    }

    private <T extends BaseEntity> void updateEntityFields(T outcomeEntity, T incomeEntity) {
        List<Field> fields = getAllFields(incomeEntity.getClass())
                .stream()
                .filter(f -> !f.getName().equalsIgnoreCase("id"))
                .toList();

        for (Field field : fields) {
            String fieldName = field.getName();
            String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            String getterName = getGetterName(field);

            try {
                Method getterMethod = incomeEntity.getClass().getMethod(getterName);
                Object value = getterMethod.invoke(incomeEntity);

                Method setterMethod = outcomeEntity.getClass().getMethod(setterName, field.getType());
                setterMethod.invoke(outcomeEntity, value);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                LOG.error(UNABLE_ACCESS_PROPERTY + " {}", fieldName);
                throw new RuntimeException(e);
            }
        }
    }

    private <T extends BaseEntity> List<T> deserializeEntities(Class<? extends BaseEntity> entityClass,
                                                               String content) {
        var typeFactory = objectMapper.getTypeFactory();
        var genericType = typeFactory.constructType(entityClass);
        var listType = typeFactory.constructCollectionType(List.class, genericType);

        try {
            return objectMapper.readValue(content, listType);
        } catch (IOException e) {
            LOG.error(UNABLE_DESERIALIZE_DATA + " {}", entityClass);
            throw new DeserializeDatabaseException(UNABLE_DESERIALIZE_DATA);
        }
    }

    private <T extends BaseEntity> void saveEntitiesToDatabase(List<T> entities, Path databasePath) {
        try {
            objectMapper.writeValue(databasePath.toFile(), entities);
        } catch (IOException e) {
            LOG.error(UNABLE_SERIALIZE_DATA + ": {}", databasePath.toAbsolutePath());
            throw new SerializeDatabaseException(UNABLE_SERIALIZE_DATA);
        }
    }

    private String getGetterName(Field field) {
        if (field == null) {
            LOG.error("Unable to get name for null field.");
            throw new RuntimeException(new NoSuchFieldException());
        }

        String fieldName = field.getName();
        if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
            return "is" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        } else {
            return "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        }
    }

    private void verifyDatabaseExists(Path databasePath) {
        if (!Files.exists(databasePath)) {
            LOG.error(DB_FILE_NOT_EXIST + ": {}", databasePath.toAbsolutePath());
            throw new TableDoesNotExistException(DB_FILE_NOT_EXIST);
        }
    }

    private <T extends BaseEntity> void assignEntityId(T entity, String entityClassName) {
        entity.setId(entityIds.get(entityClassName).incrementAndGet());
    }

    public String readDatabaseFile(Path databasePath) {
        try {
            return Files.readString(databasePath);
        } catch (IOException e) {
            LOG.error("Unable to read content from database file: {}", databasePath);
            throw new ReadFileException("Unable to read content from database file.");
        }
    }
}
