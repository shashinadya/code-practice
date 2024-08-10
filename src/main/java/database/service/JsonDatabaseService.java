package database.service;

import database.exception.CreationDatabaseException;
import database.exception.DatabaseDoesNotExistException;
import database.exception.DeletionDatabaseException;
import database.exception.DeserializeDatabaseException;
import database.exception.IdDoesNotExistException;
import database.exception.IdProvidedManuallyException;
import database.exception.ReadFileException;
import database.exception.SerializeDatabaseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import database.entity.BaseEntity;
import database.exception.WriteFileException;
import database.helper.Settings;
import database.helper.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonDatabaseService implements DatabaseService {
    private final Settings settings;
    private final ObjectMapper objectMapper;
    private final Map<String, Integer> entityIds;
    private static final Logger LOG = LoggerFactory.getLogger(JsonDatabaseService.class);
    static final String EMPTY_BRACKETS_TO_JSON = "[]";
    static final String UNABLE_CREATE_DB_FILE = "Unable to create database file. Please check if file already exists.";
    static final String UNABLE_DELETE_DB_FILE = "Unable to delete database file. Please check if file does not exist.";
    static final String DB_FILE_NOT_EXIST = "Database file does not exist";
    static final String UNABLE_SERIALIZE_DATA = "Unable to serialize data";
    static final String UNABLE_DESERIALIZE_DATA = "Unable to deserialize data";
    static final String UNABLE_ACCESS_PROPERTY = "Unable to access property";
    static final String ENTITY_DOES_NOT_EXIST = "Entity with provided Id does not exist";
    static final String ID_PROVIDED_MANUALLY = "User cannot provide id manually. Ids are filled automatically.";

    public JsonDatabaseService() throws CreationDatabaseException {
        settings = new Settings();
        objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        entityIds = new HashMap<>();
    }

    public JsonDatabaseService(String propertyFileName) throws CreationDatabaseException {
        settings = new Settings(propertyFileName);
        objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        entityIds = new HashMap<>();
    }

    @Override
    public boolean createTable(Class<? extends BaseEntity> entityClass) {
        File jsonDatabaseFile = new File(getDatabasePath(entityClass));
        try {
            if (!jsonDatabaseFile.createNewFile()) {
                throw new CreationDatabaseException(UNABLE_CREATE_DB_FILE);
            }
            return Files.writeString(jsonDatabaseFile.toPath(), EMPTY_BRACKETS_TO_JSON).toFile().exists();
        } catch (IOException e) {
            LOG.error(UNABLE_CREATE_DB_FILE + ": {}", jsonDatabaseFile.toPath());
            throw new CreationDatabaseException(UNABLE_CREATE_DB_FILE);
        }
    }

    @Override
    public boolean deleteTable(Class<? extends BaseEntity> entityClass) {
        Path databasePath = Path.of(getDatabasePath(entityClass));
        try {
            Files.delete(databasePath);
            entityIds.remove(entityClass.getName());
        } catch (IOException e) {
            LOG.error(UNABLE_DELETE_DB_FILE + ": {}", databasePath.toAbsolutePath());
            throw new DeletionDatabaseException(UNABLE_DELETE_DB_FILE);
        }
        return !Files.exists(databasePath);
    }

    @Override
    public <T extends BaseEntity> T addNewRecordToTable(T entity) {
        if (entity.getId() != null) {
            throw new IdProvidedManuallyException(ID_PROVIDED_MANUALLY);
        }

        Class<? extends BaseEntity> entityClass = entity.getClass();
        Path databasePath = Path.of(getDatabasePath(entityClass));

        if (!Files.exists(databasePath)) {
            LOG.error(DB_FILE_NOT_EXIST + ": {}", databasePath.toAbsolutePath());
            throw new DatabaseDoesNotExistException(DB_FILE_NOT_EXIST);
        }

        List<T> entities = deserializeEntities(entityClass, readDatabaseFile(databasePath));

        String entityClassName = entityClass.getName();
        var lastUsedId = entityIds.get(entityClassName);

        if (lastUsedId == null) {
            if (entities.isEmpty()) {
                entityIds.put(entityClassName, -1);
            } else {
                int lastEntityId = entities.get(entities.size() - 1).getId();
                entityIds.put(entityClassName, lastEntityId);
            }
        }

        entity.setId(entityIds.merge(entityClassName, 1, Integer::sum));
        entities.add(entity);

        saveEntitiesToDatabase(entities, databasePath);
        return entity;
    }

    @Override
    public <T extends BaseEntity> T updateRecordInTable(T entity, Integer id) {
        Class<? extends BaseEntity> entityClass = entity.getClass();
        Path databasePath = Path.of(getDatabasePath(entityClass));

        List<T> entities = deserializeEntities(entityClass, readDatabaseFile(databasePath));

        T entityFoundById = entities.stream()
                .filter(e -> id.equals(e.getId()))
                .findFirst()
                .orElseThrow(() -> new IdDoesNotExistException(ENTITY_DOES_NOT_EXIST));

        updateEntityFields(entityFoundById, entity);

        saveEntitiesToDatabase(entities, databasePath);
        return entityFoundById;
    }

    @Override
    public boolean removeRecordFromTable(Class<? extends BaseEntity> entityClass, Integer id) {
        Path databasePath = Path.of(getDatabasePath(entityClass));

        List<? extends BaseEntity> entities = deserializeEntities(entityClass, readDatabaseFile(databasePath));

        boolean isEntityRemoved = entities.removeIf(e -> id.equals(e.getId()));
        if (isEntityRemoved) {
            saveEntitiesToDatabase(entities, databasePath);
        }
        return isEntityRemoved;
    }

    @Override
    public void removeAllRecordsFromTable(Class<? extends BaseEntity> entityClass) {
        Path databasePath = Path.of(getDatabasePath(entityClass));
        try {
            Files.writeString(databasePath, EMPTY_BRACKETS_TO_JSON);
            entityIds.put(entityClass.getName(), -1);
        } catch (IOException e) {
            LOG.error("Unable to remove all data from file {}", databasePath.toAbsolutePath());
            throw new WriteFileException("Unable to write content to file.");
        }
    }

    @Override
    public <T extends BaseEntity> T getById(Class<? extends BaseEntity> entityClass, Integer id) {
        Path databasePath = Path.of(getDatabasePath(entityClass));

        List<T> entities = deserializeEntities(entityClass, readDatabaseFile(databasePath));

        return entities.stream()
                .filter(e -> id.equals(e.getId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public <T extends BaseEntity> Iterable<T> getAllRecordsFromTable(Class<? extends BaseEntity> entityClass) {
        Path databasePath = Path.of(getDatabasePath(entityClass));

        return deserializeEntities(entityClass, readDatabaseFile(databasePath));
    }

    @Override
    public <T extends BaseEntity, V> Iterable<T> getByFilters(Class<? extends BaseEntity> entityClass,
                                                              Map<String, V> filters) {
        Path databasePath = Path.of(getDatabasePath(entityClass));
        Field[] fields = entityClass.getDeclaredFields();
        Validator.validateDatabaseFilters(fields, filters);

        List<T> entities = deserializeEntities(entityClass, readDatabaseFile(databasePath));

        return entities.stream()
                .filter(entity -> {
                    for (Map.Entry<String, V> filter : filters.entrySet()) {
                        String fieldName = filter.getKey();
                        V expectedValue = filter.getValue();
                        String getterName = getGetterName(entityClass, fieldName);

                        try {
                            Method getterMethod = entityClass.getMethod(getterName);
                            Object actualValue = getterMethod.invoke(entity);

                            if (!expectedValue.equals(actualValue)) {
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

    String getDatabasePath(Class<? extends BaseEntity> entityClass) {
        var databaseFolder = settings.getDatabasePath();
        return databaseFolder + File.separator + entityClass.getSimpleName() + "Table" + ".json";
    }

    private <T extends BaseEntity> void updateEntityFields(T outcomeEntity, T incomeEntity) {
        Field[] fields = Arrays.stream(incomeEntity.getClass().getDeclaredFields())
                .filter(f -> !f.getName().equals("Id"))
                .toArray(Field[]::new);

        for (Field field : fields) {
            String fieldName = field.getName();
            String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            String getterName = getGetterName(incomeEntity.getClass(), fieldName);

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

    private String getGetterName(Class<?> entityClass, String fieldName) {
        Field field;
        try {
            field = entityClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            LOG.error("Unable to get declared field {}", fieldName);
            throw new RuntimeException(e);
        }
        if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
            return "is" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        } else {
            return "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        }
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
