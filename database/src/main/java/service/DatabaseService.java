package service;

import entity.BaseEntity;

import java.util.Map;

public interface DatabaseService {

    boolean createTable(Class<? extends BaseEntity> entityClass);

    boolean deleteTable(Class<? extends BaseEntity> entityClass);

    <T extends BaseEntity> T addNewRecordToTable(T entity);

    <T extends BaseEntity> T updateRecordInTable(T entity, Integer id);

    boolean removeRecordFromTable(Class<? extends BaseEntity> entityClass, Integer id);

    void removeAllRecordsFromTable(Class<? extends BaseEntity> entityClass);

    <T extends BaseEntity> T getById(Class<? extends BaseEntity> entityClass, Integer id);

    <T extends BaseEntity> Iterable<T> getAllRecordsFromTable(Class<? extends BaseEntity> entityClass);

    <T extends BaseEntity> Iterable<T> getAllRecordsFromTable(Class<? extends BaseEntity> entityClass,
                                                              int limit, int offset);

    <T extends BaseEntity, V> Iterable<T> getByFilters(Class<? extends BaseEntity> entityClass, Map<String, V> filters);
}
