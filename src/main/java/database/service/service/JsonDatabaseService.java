package database.service.service;

import code.practice.exceptions.CriticalDatabaseException;
import code.practice.exceptions.DatabaseDoesNotExistException;
import code.practice.exceptions.TableIsNotCompatibleWithEntityException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import database.service.entity.BaseEntity;
import database.service.helper.Settings;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public <T extends BaseEntity> T addNewRecordToTable(T entity) throws DatabaseDoesNotExistException,
            TableIsNotCompatibleWithEntityException, IOException {
        Class<? extends BaseEntity> entityClass = entity.getClass();
        Path databasePath = Path.of(getDatabasePath(entityClass));

        if (!Files.exists(databasePath)) {
            throw new DatabaseDoesNotExistException("Database does not exist" + databasePath);
        }

        if (isTableCompatibleWithEntity(databasePath, entityClass)) {
            throw new TableIsNotCompatibleWithEntityException("Table " + databasePath +
                    " is not compatible with entity " + entityClass);
        }

        List<T> entities = objectMapper.readValue(Files.newBufferedReader(databasePath), new TypeReference<>() {});
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
    public <T, I> void updateRecordInTable(T entity, I id) {

    }

    @Override
    public <I> boolean removeRecordFromTable(Class<? extends BaseEntity> entityClass, I id) {
        return false;
    }

    @Override
    public boolean removeAllRecordsFromTable(Class<? extends BaseEntity> entityClass) {
        return false;
    }

    @Override
    public <T, I> T getById(Class<? extends BaseEntity> entityClass, I id) {
        return null;
    }

    @Override
    public <T> Iterable<T> getAllRecordsFromTable(Class<? extends BaseEntity> entityClass) {
        return null;
    }

    @Override
    public <T, V> Iterable<T> getByFilters(Class<? extends BaseEntity> entityClass, Set<Pair<String, V>> filters) {
        return null;
    }

    private String getDatabasePath(Class<? extends BaseEntity> entityClass) {
        var databaseFolder = settings.getDatabasePath();
        return databaseFolder + File.separator + entityClass.getSimpleName() + "Table" + ".json";
    }

    private <T extends BaseEntity> boolean isTableCompatibleWithEntity(Path databasePath, Class<T> entityClass) {
        String entityClassName = entityClass.getSimpleName();
        return databasePath.toString().startsWith(entityClassName);
    }
}
