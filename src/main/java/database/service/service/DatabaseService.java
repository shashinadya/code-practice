package database.service.service;

import database.service.entity.BaseEntity;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;

public interface DatabaseService {

    boolean createTable(Class<? extends BaseEntity> entityClass);

    boolean deleteTable(Class<? extends BaseEntity> entityClass);

    <T extends BaseEntity> T addNewRecordToTable(T entity);

    <T extends BaseEntity, I> T updateRecordInTable(T entity, I id);

    <I> boolean removeRecordFromTable(Class<? extends BaseEntity> entityClass, I id);

    void removeAllRecordsFromTable(Class<? extends BaseEntity> entityClass);

    <T extends BaseEntity, I> T getById(Class<? extends BaseEntity> entityClass, I id);

    <T extends BaseEntity> Iterable<T> getAllRecordsFromTable(Class<? extends BaseEntity> entityClass);

    <T extends BaseEntity, V> Iterable<T> getByFilters(Class<? extends BaseEntity> entityClass,
                                                       Set<Pair<String, V>> filters);
}
