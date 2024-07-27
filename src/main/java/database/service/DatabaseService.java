package database.service;

import database.entity.BaseEntity;

import java.util.Map;

public interface DatabaseService {

    boolean createTable(Class<? extends BaseEntity> entityClass);

    boolean deleteTable(Class<? extends BaseEntity> entityClass);

    <T extends BaseEntity> T addNewRecordToTable(T entity);

    <T extends BaseEntity, I> T updateRecordInTable(T entity, I id);

    <I> boolean removeRecordFromTable(Class<? extends BaseEntity> entityClass, I id);

    void removeAllRecordsFromTable(Class<? extends BaseEntity> entityClass);

    <T extends BaseEntity> T getById(Class<? extends BaseEntity> entityClass, Integer id);

    <T extends BaseEntity> Iterable<T> getAllRecordsFromTable(Class<? extends BaseEntity> entityClass);

    <T extends BaseEntity, V> Iterable<T> getByFilters(Class<? extends BaseEntity> entityClass, Map<String, V> filters);
}
