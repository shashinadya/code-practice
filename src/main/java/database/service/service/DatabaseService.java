package database.service.service;

import code.practice.exceptions.DatabaseDoesNotExistException;
import code.practice.exceptions.TableIsNotCompatibleWithEntityException;
import database.service.entity.BaseEntity;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

public interface DatabaseService {

    boolean createTable(Class<? extends BaseEntity> entityClass) throws IOException;

    boolean deleteTable(Class<? extends BaseEntity> entityClass) throws IOException;

    <T extends BaseEntity> T addNewRecordToTable(T entity) throws DatabaseDoesNotExistException, TableIsNotCompatibleWithEntityException, IOException;

    <T, I> void updateRecordInTable(T entity, I id);

    <I> boolean removeRecordFromTable(Class<? extends BaseEntity> entityClass, I id);

    boolean removeAllRecordsFromTable(Class<? extends BaseEntity> entityClass);

    <T, I> T getById(Class<? extends BaseEntity> entityClass, I id);

    <T> Iterable<T> getAllRecordsFromTable(Class<? extends BaseEntity> entityClass);

    <T, V> Iterable<T> getByFilters(Class<? extends BaseEntity> entityClass, Set<Pair<String, V>> filters);
}
