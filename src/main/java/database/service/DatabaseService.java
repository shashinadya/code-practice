package database.service;

import database.entity.BaseEntity;

import java.io.IOException;
import java.util.Map;

public interface DatabaseService {

    boolean createTable(Class<? extends BaseEntity> entityClass);

    boolean deleteTable(Class<? extends BaseEntity> entityClass);

    <T extends BaseEntity> T addNewRecordToTable(T entity) throws IOException;

    <T extends BaseEntity, I> T updateRecordInTable(T entity, I id) throws IOException;

    <I> boolean removeRecordFromTable(Class<? extends BaseEntity> entityClass, I id) throws IOException;

    void removeAllRecordsFromTable(Class<? extends BaseEntity> entityClass);

    <T extends BaseEntity, I> T getById(Class<? extends BaseEntity> entityClass, I id) throws IOException;

    <T extends BaseEntity> Iterable<T> getAllRecordsFromTable(Class<? extends BaseEntity> entityClass)
            throws IOException;

    <T extends BaseEntity, V> Iterable<T> getByFilters(Class<? extends BaseEntity> entityClass,
                                                       Map<String, V> filters) throws IOException;
}
