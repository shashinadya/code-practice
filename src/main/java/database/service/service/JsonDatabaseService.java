package database.service.service;

import code.practice.exceptions.CriticalDatabaseException;
import code.practice.exceptions.DatabaseDoesNotExistException;
import code.practice.exceptions.ProvidedIdDoesNotExistException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import database.service.entity.BaseEntity;
import database.service.helper.Settings;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class JsonDatabaseService implements DatabaseService {
    private final Settings settings = new Settings();
    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private Map<String, Integer> entityIds = new HashMap<>();

    public JsonDatabaseService() throws CriticalDatabaseException {
    }

    @Override
    public boolean createTable(Class<? extends BaseEntity> entityClass) throws IOException {
        File jsonDatabaseFile = new File(getDatabasePath(entityClass));
        jsonDatabaseFile.createNewFile();
        Files.writeString(jsonDatabaseFile.toPath(), "[]");
        return Files.exists(jsonDatabaseFile.toPath());
    }

    @Override
    public boolean deleteTable(Class<? extends BaseEntity> entityClass) throws IOException {
        Path databasePath = Path.of(getDatabasePath(entityClass));
        Files.delete(databasePath);
        return !Files.exists(databasePath);
    }

    @Override
    public <T extends BaseEntity> T addNewRecordToTable(T entity) throws DatabaseDoesNotExistException, IOException {
        Class<? extends BaseEntity> entityClass = entity.getClass();
        Path databasePath = Path.of(getDatabasePath(entityClass));

        if (!Files.exists(databasePath)) {
            throw new DatabaseDoesNotExistException("Database does not exist.");
        }

        TypeFactory typeFactory = objectMapper.getTypeFactory();
        JavaType genericType = typeFactory.constructType(entityClass);
        JavaType listType = typeFactory.constructCollectionType(List.class, genericType);

        List<T> entities = objectMapper.readValue(Files.newBufferedReader(databasePath), listType);
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

        objectMapper.writeValue(databasePath.toFile(), entities);
        return entity;
    }

    @Override
    public <T extends BaseEntity, I> T updateRecordInTable(T entity, I id) throws IOException,
            ProvidedIdDoesNotExistException {
        Class<? extends BaseEntity> entityClass = entity.getClass();
        Path databasePath = Path.of(getDatabasePath(entityClass));

        TypeFactory typeFactory = objectMapper.getTypeFactory();
        JavaType genericType = typeFactory.constructType(entityClass);
        JavaType listType = typeFactory.constructCollectionType(List.class, genericType);

        List<T> entities = objectMapper.readValue(Files.newBufferedReader(databasePath), listType);

        Optional<T> optionalEntity = entities.stream().filter(e -> id.equals(e.getId())).findFirst();
        T entityFoundById;
        if (optionalEntity.isPresent()) {
            entityFoundById = optionalEntity.get();
            updateEntityFields(entityFoundById, entity);
        } else {
            throw new ProvidedIdDoesNotExistException("Entity with provided Id does not exist.");
        }

        objectMapper.writeValue(databasePath.toFile(), entities);
        return entityFoundById;
    }

    @Override
    public <I> boolean removeRecordFromTable(Class<? extends BaseEntity> entityClass, I id) throws IOException {
        Path databasePath = Path.of(getDatabasePath(entityClass));

        TypeFactory typeFactory = objectMapper.getTypeFactory();
        JavaType genericType = typeFactory.constructType(entityClass);
        JavaType listType = typeFactory.constructCollectionType(List.class, genericType);

        List<? extends BaseEntity> entities =
                objectMapper.readValue(Files.newBufferedReader(databasePath), listType);

        boolean isEntityRemoved = entities.removeIf(e -> id.equals(e.getId()));

        objectMapper.writeValue(databasePath.toFile(), entities);
        return isEntityRemoved;
    }

    @Override
    public void removeAllRecordsFromTable(Class<? extends BaseEntity> entityClass) throws IOException {
        Path databasePath = Path.of(getDatabasePath(entityClass));
        Files.writeString(databasePath, "[]");
    }

    @Override
    public <T extends BaseEntity, I> T getById(Class<? extends BaseEntity> entityClass, I id) throws IOException,
            ProvidedIdDoesNotExistException {
        Path databasePath = Path.of(getDatabasePath(entityClass));

        TypeFactory typeFactory = objectMapper.getTypeFactory();
        JavaType genericType = typeFactory.constructType(entityClass);
        JavaType listType = typeFactory.constructCollectionType(List.class, genericType);

        List<T> entities =
                objectMapper.readValue(Files.newBufferedReader(databasePath), listType);

        return entities.stream()
                .filter(e -> id.equals(e.getId()))
                .findFirst()
                .orElseThrow(() -> new ProvidedIdDoesNotExistException("Entity with provided Id does not exist."));
    }

    @Override
    public <T extends BaseEntity> Iterable<T> getAllRecordsFromTable(Class<? extends BaseEntity> entityClass)
            throws IOException {
        Path databasePath = Path.of(getDatabasePath(entityClass));

        TypeFactory typeFactory = objectMapper.getTypeFactory();
        JavaType genericType = typeFactory.constructType(entityClass);
        JavaType listType = typeFactory.constructCollectionType(List.class, genericType);

        return objectMapper.readValue(Files.newBufferedReader(databasePath), listType);
    }

    @Override
    public <T extends BaseEntity, V> Iterable<T> getByFilters(Class<? extends BaseEntity> entityClass,
                                                              Set<Pair<String, V>> filters) {
        return null;
    }

    String getDatabasePath(Class<? extends BaseEntity> entityClass) {
        var databaseFolder = settings.getDatabasePath();
        return databaseFolder + File.separator + entityClass.getSimpleName() + "Table" + ".json";
    }

    private <T extends BaseEntity> void updateEntityFields(T outcomeEntity, T incomeEntity) {
        Field[] fields = incomeEntity.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(incomeEntity);
                field.set(outcomeEntity, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
