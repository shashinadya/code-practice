package database.service;

import database.entity.BaseEntity;

import java.util.List;
import java.util.Map;

public interface DatabaseService {

    boolean createTable(Class<? extends BaseEntity> entityClass);

    boolean deleteTable(Class<? extends BaseEntity> entityClass);

    <T extends BaseEntity> T addNewRecordToTable(T entity);

    <T extends BaseEntity> Iterable<T> addNewRecordsToTable(Class<? extends BaseEntity> entityClass, List<T> entities);

    <T extends BaseEntity> T updateRecordInTable(T entity, Integer id);

    boolean removeRecordFromTable(Class<? extends BaseEntity> entityClass, Integer id);

    boolean removeSpecificRecords(Class<? extends BaseEntity> entityClass, List<Integer> ids);

    void removeAllRecordsFromTable(Class<? extends BaseEntity> entityClass);

    <T extends BaseEntity> T getById(Class<? extends BaseEntity> entityClass, Integer id);

    <T extends BaseEntity> Iterable<T> getAllRecordsFromTable(Class<? extends BaseEntity> entityClass);

    <T extends BaseEntity> Iterable<T> getAllRecordsFromTable(Class<? extends BaseEntity> entityClass,
                                                              int limit, int offset);

    <T extends BaseEntity> Iterable<T> getByFilters(Class<? extends BaseEntity> entityClass,
                                                    Map<String, List<String>> filters);

    void shutdown();
}
