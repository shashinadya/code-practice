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

    public JsonDatabaseService() throws CreationDatabaseException {
        settings = new Settings();
        objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        entityIds = new HashMap<>();
    }

    @Override
    public boolean createTable(Class<? extends BaseEntity> entityClass) {
        File jsonDatabaseFile = new File(getDatabasePath(entityClass));
        try {
            jsonDatabaseFile.createNewFile();
            return Files.writeString(jsonDatabaseFile.toPath(), EMPTY_BRACKETS_TO_JSON).toFile().exists();
        } catch (IOException e) {
            throw new CreationDatabaseException("Unable to create database file.");
        }
    }

    @Override
    public boolean deleteTable(Class<? extends BaseEntity> entityClass) {
        Path databasePath = Path.of(getDatabasePath(entityClass));
        try {
            Files.delete(databasePath);
        } catch (IOException e) {
            throw new DeletionDatabaseException("Unable to delete database file.");
        }
        return !Files.exists(databasePath);
    }

    @Override
    public <T extends BaseEntity> T addNewRecordToTable(T entity) {
        Class<? extends BaseEntity> entityClass = entity.getClass();
        Path databasePath = Path.of(getDatabasePath(entityClass));

        if (!Files.exists(databasePath)) {
            throw new DatabaseDoesNotExistException("Database does not exist.");
        }

        List<T> entities;
        try {
            entities = deserializeEntities(entityClass, Files.readString(databasePath));
        } catch (IOException e) {
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
            throw new SerializeDatabaseException("Unable to serialize data.");
        }
        return entity;
    }

    @Override
    public <T extends BaseEntity, I> T updateRecordInTable(T entity, I id) {
        Class<? extends BaseEntity> entityClass = entity.getClass();
        Path databasePath = Path.of(getDatabasePath(entityClass));

        List<T> entities;
        try {
            entities = deserializeEntities(entityClass, Files.readString(databasePath));
        } catch (IOException e) {
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
            throw new SerializeDatabaseException("Unable to serialize data.");
        }
        return entityFoundById;
    }

    @Override
    public <I> boolean removeRecordFromTable(Class<? extends BaseEntity> entityClass, I id) {
        Path databasePath = Path.of(getDatabasePath(entityClass));

        List<? extends BaseEntity> entities;
        try {
            entities = deserializeEntities(entityClass, Files.readString(databasePath));
        } catch (IOException e) {
            throw new DeserializeDatabaseException("Unable to deserialize data.");
        }

        boolean isEntityRemoved = entities.removeIf(e -> id.equals(e.getId()));
        if (isEntityRemoved) {
            try {
                objectMapper.writeValue(databasePath.toFile(), entities);
            } catch (IOException e) {
                throw new SerializeDatabaseException("Unable to serialize data.");
            }
        }

        return isEntityRemoved;
    }

    @Override
    public void removeAllRecordsFromTable(Class<? extends BaseEntity> entityClass) {
        Path databasePath = Path.of(getDatabasePath(entityClass));
        try {
            Files.writeString(databasePath, EMPTY_BRACKETS_TO_JSON);
        } catch (IOException e) {
            throw new SerializeDatabaseException("Unable to serialize data.");
        }
    }

    @Override
    public <T extends BaseEntity, I> T getById(Class<? extends BaseEntity> entityClass, I id) {
        Path databasePath = Path.of(getDatabasePath(entityClass));

        List<T> entities;
        try {
            entities = deserializeEntities(entityClass, Files.readString(databasePath));
        } catch (IOException e) {
            throw new DeserializeDatabaseException("Unable to deserialize data.");
        }

        return entities.stream()
                .filter(e -> id.equals(e.getId()))
                .findFirst()
                .orElseThrow(() -> new IdDoesNotExistException("Entity with provided Id does not exist."));
    }

    @Override
    public <T extends BaseEntity> Iterable<T> getAllRecordsFromTable(Class<? extends BaseEntity> entityClass) {
        Path databasePath = Path.of(getDatabasePath(entityClass));

        try {
            return deserializeEntities(entityClass, Files.readString(databasePath));
        } catch (IOException e) {
            throw new DeserializeDatabaseException("Unable to deserialize data.");
        }
    }

    @Override
    public <T extends BaseEntity, V> Iterable<T> getByFilters(Class<? extends BaseEntity> entityClass,
                                                              Set<Pair<String, V>> filters) {
        Path databasePath = Path.of(getDatabasePath(entityClass));
        Field[] fields = entityClass.getDeclaredFields();
        Validator validator = new Validator();

        List<T> entities;
        try {
            entities = deserializeEntities(entityClass, Files.readString(databasePath));
        } catch (IOException e) {
            throw new DeserializeDatabaseException("Unable to deserialize data.");
        }

        return entities.stream()
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
                            e.printStackTrace();
                            return false;
                        } catch (InvocationTargetException | NoSuchMethodException e) {
                            throw new RuntimeException(e);
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
            } catch (IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private <T extends BaseEntity> List<T> deserializeEntities(Class<? extends BaseEntity> entityClass,
                                                               String content) throws IOException {
        var typeFactory = objectMapper.getTypeFactory();
        var genericType = typeFactory.constructType(entityClass);
        var listType = typeFactory.constructCollectionType(List.class, genericType);

        return objectMapper.readValue(content, listType);
    }
}
