package database.service.service;

import code.practice.exceptions.database.CreationDatabaseException;
import code.practice.exceptions.database.DatabaseDoesNotExistException;
import code.practice.exceptions.database.DeletionDatabaseException;
import code.practice.exceptions.database.DeserializeDatabaseException;
import code.practice.exceptions.database.IdDoesNotExistException;
import code.practice.exceptions.database.SerializeDatabaseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import database.service.entity.BaseEntity;
import database.service.helper.Settings;
import database.service.helper.Validator;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonDatabaseService implements DatabaseService {
    private final Settings settings;
    private final ObjectMapper objectMapper;
    static final String EMPTY_BRACKETS_TO_JSON = "[]";
    private Map<String, Integer> entityIds;
    private static final Logger logger = LogManager.getLogger(Settings.class);

    public JsonDatabaseService() throws CreationDatabaseException {
        settings = new Settings();
        objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        entityIds = new HashMap<>();
    }

    @Override
    public boolean createTable(Class<? extends BaseEntity> entityClass) {
        logger.info("Creating table " + entityClass.getSimpleName());
        File jsonDatabaseFile = new File(getDatabasePath(entityClass));
        try {
            jsonDatabaseFile.createNewFile();
            logger.info("Table created" + jsonDatabaseFile.getAbsolutePath());
            return Files.writeString(jsonDatabaseFile.toPath(), EMPTY_BRACKETS_TO_JSON).toFile().exists();
        } catch (IOException e) {
            logger.error("Unable to create database file.");
            throw new CreationDatabaseException("Unable to create database file.");
        }
    }

    @Override
    public boolean deleteTable(Class<? extends BaseEntity> entityClass) {
        logger.info("Deleting table " + entityClass.getSimpleName());
        Path databasePath = Path.of(getDatabasePath(entityClass));
        try {
            Files.delete(databasePath);
            logger.info("Table " + entityClass.getSimpleName() + " deleted.");
        } catch (IOException e) {
            logger.error("Unable to delete database file at " + databasePath.toAbsolutePath());
            throw new DeletionDatabaseException("Unable to delete database file.");
        }
        return !Files.exists(databasePath);
    }

    @Override
    public <T extends BaseEntity> T addNewRecordToTable(T entity) {
        logger.info("Adding new record to table " + entity.getClass().getSimpleName());
        Class<? extends BaseEntity> entityClass = entity.getClass();
        Path databasePath = Path.of(getDatabasePath(entityClass));

        if (!Files.exists(databasePath)) {
            logger.error("Database file " + databasePath.toAbsolutePath() + " does not exist.");
            throw new DatabaseDoesNotExistException("Database does not exist.");
        }

        List<T> entities;
        try {
            entities = deserializeEntities(entityClass, Files.readString(databasePath));
        } catch (IOException e) {
            logger.error("Unable to deserialize data at " + databasePath.toAbsolutePath());
            throw new DeserializeDatabaseException("Unable to deserialize data.");
        }
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

        try {
            objectMapper.writeValue(databasePath.toFile(), entities);
        } catch (IOException e) {
            logger.error("Unable to serialize data at " + databasePath.toAbsolutePath());
            throw new SerializeDatabaseException("Unable to serialize data.");
        }
        logger.info("Added new record to table " + entityClass.getSimpleName());
        return entity;
    }

    @Override
    public <T extends BaseEntity, I> T updateRecordInTable(T entity, I id) {
        logger.info("Updating record in table " + entity.getClass().getSimpleName());
        Class<? extends BaseEntity> entityClass = entity.getClass();
        Path databasePath = Path.of(getDatabasePath(entityClass));

        List<T> entities;
        try {
            entities = deserializeEntities(entityClass, Files.readString(databasePath));
        } catch (IOException e) {
            logger.error("Unable to deserialize data at " + databasePath.toAbsolutePath());
            throw new DeserializeDatabaseException("Unable to deserialize data.");
        }

        T entityFoundById = entities.stream()
                .filter(e -> id.equals(e.getId()))
                .findFirst()
                .orElseThrow(() -> new IdDoesNotExistException("Entity with provided Id does not exist."));

        updateEntityFields(entityFoundById, entity);

        try {
            objectMapper.writeValue(databasePath.toFile(), entities);
        } catch (IOException e) {
            logger.error("Unable to serialize data at " + databasePath.toAbsolutePath());
            throw new SerializeDatabaseException("Unable to serialize data.");
        }
        logger.info("Updated record in table " + entityClass.getSimpleName());
        return entityFoundById;
    }

    @Override
    public <I> boolean removeRecordFromTable(Class<? extends BaseEntity> entityClass, I id) {
        logger.info("Removing record from table " + entityClass.getSimpleName());
        Path databasePath = Path.of(getDatabasePath(entityClass));

        List<? extends BaseEntity> entities;
        try {
            entities = deserializeEntities(entityClass, Files.readString(databasePath));
        } catch (IOException e) {
            logger.error("Unable to deserialize data at " + databasePath.toAbsolutePath());
            throw new DeserializeDatabaseException("Unable to deserialize data.");
        }

        boolean isEntityRemoved = entities.removeIf(e -> id.equals(e.getId()));
        if (isEntityRemoved) {
            try {
                objectMapper.writeValue(databasePath.toFile(), entities);
            } catch (IOException e) {
                logger.error("Unable to serialize data at " + databasePath.toAbsolutePath());
                throw new SerializeDatabaseException("Unable to serialize data.");
            }
        }
        logger.info("Removed record from table " + entityClass.getSimpleName());
        return isEntityRemoved;
    }

    @Override
    public void removeAllRecordsFromTable(Class<? extends BaseEntity> entityClass) {
        logger.info("Removing all records from table " + entityClass.getSimpleName());
        Path databasePath = Path.of(getDatabasePath(entityClass));
        try {
            Files.writeString(databasePath, EMPTY_BRACKETS_TO_JSON);
            logger.info("Removed all records from table " + entityClass.getSimpleName());
        } catch (IOException e) {
            logger.error("Unable to serialize data at " + databasePath.toAbsolutePath());
            throw new SerializeDatabaseException("Unable to serialize data.");
        }
    }

    @Override
    public <T extends BaseEntity, I> T getById(Class<? extends BaseEntity> entityClass, I id) {
        logger.info("Retrieving entity by id from table " + entityClass.getSimpleName());
        Path databasePath = Path.of(getDatabasePath(entityClass));

        List<T> entities;
        try {
            entities = deserializeEntities(entityClass, Files.readString(databasePath));
        } catch (IOException e) {
            logger.error("Unable to deserialize data at " + databasePath.toAbsolutePath());
            throw new DeserializeDatabaseException("Unable to deserialize data.");
        }

        T entityGetById = entities.stream()
                .filter(e -> id.equals(e.getId()))
                .findFirst()
                .orElseThrow(() -> new IdDoesNotExistException("Entity with provided Id does not exist."));

        logger.info("Retrieved entity by id from table " + entityClass.getSimpleName());
        return entityGetById;
    }

    @Override
    public <T extends BaseEntity> Iterable<T> getAllRecordsFromTable(Class<? extends BaseEntity> entityClass) {
        logger.info("Retrieving all records from table " + entityClass.getSimpleName());
        Path databasePath = Path.of(getDatabasePath(entityClass));

        try {
            logger.info("Retrieved all records from table " + entityClass.getSimpleName());
            return deserializeEntities(entityClass, Files.readString(databasePath));
        } catch (IOException e) {
            logger.error("Unable to deserialize data at " + databasePath.toAbsolutePath());
            throw new DeserializeDatabaseException("Unable to deserialize data.");
        }
    }

    @Override
    public <T extends BaseEntity, V> Iterable<T> getByFilters(Class<? extends BaseEntity> entityClass,
                                                              Set<Pair<String, V>> filters) {
        logger.info("Retrieving records by filters from table " + entityClass.getSimpleName());
        Path databasePath = Path.of(getDatabasePath(entityClass));
        Field[] fields = entityClass.getDeclaredFields();
        Validator validator = new Validator();

        List<T> entities;
        try {
            entities = deserializeEntities(entityClass, Files.readString(databasePath));
        } catch (IOException e) {
            logger.error("Unable to deserialize data at " + databasePath.toAbsolutePath());
            throw new DeserializeDatabaseException("Unable to deserialize data.");
        }

        List<T> retrievedRecordsByFilters = entities.stream()
                .filter(entity -> {
                    for (Pair<String, V> filter : filters) {
                        String propertyName = filter.getKey();
                        V expectedValue = filter.getValue();
                        validator.validateDatabaseFilters(fields, filters);

                        try {
                            String getterName = "get" + Character.toUpperCase(propertyName.charAt(0)) +
                                    propertyName.substring(1);
                            Method getterMethod = entityClass.getMethod(getterName);

                            Object actualValue = getterMethod.invoke(entity);

                            if (!expectedValue.equals(actualValue)) {
                                return false;
                            }
                        } catch (IllegalAccessException e) {
                            logger.error("Unable to access property " + propertyName);
                            e.printStackTrace();
                            return false;
                        } catch (InvocationTargetException | NoSuchMethodException e) {
                            logger.error("No such method for " + propertyName);
                            throw new RuntimeException(e);
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
        logger.info("Retrieved records by filters from table " + entityClass.getSimpleName());
        return retrievedRecordsByFilters;
    }

    String getDatabasePath(Class<? extends BaseEntity> entityClass) {
        logger.info("Retrieving database path for entity " + entityClass.getSimpleName());
        var databaseFolder = settings.getDatabasePath();
        var databasePath = databaseFolder + File.separator + entityClass.getSimpleName() + "Table" + ".json";
        logger.info("Retrieved database path " + databasePath);
        return databasePath;
    }

    private <T extends BaseEntity> void updateEntityFields(T outcomeEntity, T incomeEntity) {
        logger.info("Updating entity fields for entity " + outcomeEntity);
        Field[] fields = incomeEntity.getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            String getterName;

            if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
                getterName = "is" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            } else {
                getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            }

            try {
                Method getterMethod = incomeEntity.getClass().getMethod(getterName);
                Object value = getterMethod.invoke(incomeEntity);

                Method setterMethod = outcomeEntity.getClass().getMethod(setterName, field.getType());
                setterMethod.invoke(outcomeEntity, value);
                logger.info("Updated entity field " + fieldName + " to " + value);
            } catch (IllegalAccessException | NoSuchMethodException e) {
                logger.error("Unable to access property " + fieldName);
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                logger.error("Invocation Target Exception for " + fieldName);
                throw new RuntimeException(e);
            }
        }
    }

    private <T extends BaseEntity> List<T> deserializeEntities(Class<? extends BaseEntity> entityClass,
                                                               String content) throws IOException {
        logger.info("Deserializing entity " + entityClass.getSimpleName());
        var typeFactory = objectMapper.getTypeFactory();
        var genericType = typeFactory.constructType(entityClass);
        var listType = typeFactory.constructCollectionType(List.class, genericType);

        List<T> readValue = objectMapper.readValue(content, listType);
        logger.info("Deserialized entity " + entityClass.getSimpleName());
        return readValue;
    }
}
