package database.service;

import database.exception.CreationDatabaseException;
import database.exception.DatabaseDoesNotExistException;
import database.exception.DeletionDatabaseException;
import database.exception.DeserializeDatabaseException;
import database.exception.IdDoesNotExistException;
import database.exception.SerializeDatabaseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import database.entity.BaseEntity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonDatabaseService implements DatabaseService {
    private final Settings settings;
    private final ObjectMapper objectMapper;
    private final Map<String, Integer> entityIds;
    private static final Logger logger = LoggerFactory.getLogger(Settings.class);
    public static final String EMPTY_BRACKETS_TO_JSON = "[]";

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
                throw new CreationDatabaseException("Unable to create database file.");
            }
            return Files.writeString(jsonDatabaseFile.toPath(), EMPTY_BRACKETS_TO_JSON).toFile().exists();
        } catch (IOException e) {
            logger.error("Unable to create database file.");
            throw new CreationDatabaseException("Unable to create database file.");
        }
    }

    @Override
    public boolean deleteTable(Class<? extends BaseEntity> entityClass) {
        Path databasePath = Path.of(getDatabasePath(entityClass));
        try {
            Files.delete(databasePath);
        } catch (IOException e) {
            logger.error("Unable to delete database file at {}", databasePath.toAbsolutePath());
            throw new DeletionDatabaseException("Unable to delete database file.");
        }
        return !Files.exists(databasePath);
    }

    @Override
    public <T extends BaseEntity> T addNewRecordToTable(T entity) throws IOException {
        Class<? extends BaseEntity> entityClass = entity.getClass();
        Path databasePath = Path.of(getDatabasePath(entityClass));

        if (!Files.exists(databasePath)) {
            logger.error("Database file {} does not exist.", databasePath.toAbsolutePath());
            throw new DatabaseDoesNotExistException("Database does not exist.");
        }

        List<T> entities = deserializeEntities(entityClass, Files.readString(databasePath));

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

        serializeEntities(entities, databasePath);
        return entity;
    }

    @Override
    public <T extends BaseEntity, I> T updateRecordInTable(T entity, I id) throws IOException {
        Class<? extends BaseEntity> entityClass = entity.getClass();
        Path databasePath = Path.of(getDatabasePath(entityClass));

        List<T> entities = deserializeEntities(entityClass, Files.readString(databasePath));

        T entityFoundById = entities.stream()
                .filter(e -> id.equals(e.getId()))
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("Entity with id {} not found.", id);
                    return new IdDoesNotExistException("Entity with provided Id does not exist.");
                });

        updateEntityFields(entityFoundById, entity);

        serializeEntities(entities, databasePath);
        return entityFoundById;
    }

    @Override
    public <I> boolean removeRecordFromTable(Class<? extends BaseEntity> entityClass, I id) throws IOException {
        Path databasePath = Path.of(getDatabasePath(entityClass));

        List<? extends BaseEntity> entities = deserializeEntities(entityClass, Files.readString(databasePath));


        boolean isEntityRemoved = entities.removeIf(e -> id.equals(e.getId()));
        if (isEntityRemoved) {
            serializeEntities(entities, databasePath);
        }
        return isEntityRemoved;
    }

    @Override
    public void removeAllRecordsFromTable(Class<? extends BaseEntity> entityClass) {
        Path databasePath = Path.of(getDatabasePath(entityClass));
        try {
            Files.writeString(databasePath, EMPTY_BRACKETS_TO_JSON);
        } catch (IOException e) {
            logger.error("Unable to serialize data at " + databasePath.toAbsolutePath());
            throw new SerializeDatabaseException("Unable to serialize data.");
        }
    }

    @Override
    public <T extends BaseEntity, I> T getById(Class<? extends BaseEntity> entityClass, I id) throws IOException {
        Path databasePath = Path.of(getDatabasePath(entityClass));

        List<T> entities = deserializeEntities(entityClass, Files.readString(databasePath));

        return entities.stream()
                .filter(e -> id.equals(e.getId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public <T extends BaseEntity> Iterable<T> getAllRecordsFromTable(Class<? extends BaseEntity> entityClass)
            throws IOException {
        Path databasePath = Path.of(getDatabasePath(entityClass));

        return deserializeEntities(entityClass, Files.readString(databasePath));
    }

    @Override
    public <T extends BaseEntity, V> Iterable<T> getByFilters(Class<? extends BaseEntity> entityClass,
                                                              Map<String, V> filters) throws IOException {
        Path databasePath = Path.of(getDatabasePath(entityClass));
        Field[] fields = entityClass.getDeclaredFields();
        Validator.validateDatabaseFilters(fields, filters);

        List<T> entities = deserializeEntities(entityClass, Files.readString(databasePath));

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
                        } catch (IllegalAccessException e) {
                            logger.error("Unable to access property " + fieldName);
                            e.printStackTrace();
                            return false;
                        } catch (InvocationTargetException | NoSuchMethodException e) {
                            logger.error("No such method for " + fieldName);
                            throw new RuntimeException(e);
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    public String getDatabasePath(Class<? extends BaseEntity> entityClass) {
        var databaseFolder = settings.getDatabasePath();
        return databaseFolder + File.separator + entityClass.getSimpleName() + "Table" + ".json";
    }

    private <T extends BaseEntity> void updateEntityFields(T outcomeEntity, T incomeEntity) {
        Field[] fields = incomeEntity.getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            String getterName = getGetterName(incomeEntity.getClass(), fieldName);

            try {
                Method getterMethod = incomeEntity.getClass().getMethod(getterName);
                Object value = getterMethod.invoke(incomeEntity);

                Method setterMethod = outcomeEntity.getClass().getMethod(setterName, field.getType());
                setterMethod.invoke(outcomeEntity, value);
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
                                                               String content) {
        var typeFactory = objectMapper.getTypeFactory();
        var genericType = typeFactory.constructType(entityClass);
        var listType = typeFactory.constructCollectionType(List.class, genericType);

        try {
            return objectMapper.readValue(content, listType);
        } catch (IOException e) {
            logger.error("Unable to deserialize data at " + content);
            throw new DeserializeDatabaseException("Unable to deserialize data.");
        }
    }

    private <T extends BaseEntity> void serializeEntities(List<T> entities, Path databasePath) {
        try {
            objectMapper.writeValue(databasePath.toFile(), entities);
        } catch (IOException e) {
            logger.error("Unable to serialize data at " + databasePath.toAbsolutePath());
            throw new SerializeDatabaseException("Unable to serialize data.");
        }
    }

    private String getGetterName(Class<?> entityClass, String fieldName) {
        Field field;
        try {
            field = entityClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
            return "is" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        } else {
            return "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        }
    }
}
