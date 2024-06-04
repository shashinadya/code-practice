package database.service.service;

import code.practice.exceptions.DatabaseDoesNotExistException;
import code.practice.exceptions.ProvidedIdDoesNotExistException;
import code.practice.exceptions.TableIsNotCompatibleWithEntityException;
import database.service.entity.BaseEntity;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

public interface DatabaseService {

    boolean createTable(Class<? extends BaseEntity> entityClass) throws IOException;

    boolean deleteTable(Class<? extends BaseEntity> entityClass) throws IOException;

    <T extends BaseEntity> T addNewRecordToTable(T entity) throws DatabaseDoesNotExistException,
            TableIsNotCompatibleWithEntityException, IOException;

    <T extends BaseEntity, I> T updateRecordInTable(T entity, I id) throws IOException,
            ProvidedIdDoesNotExistException;

    <I> boolean removeRecordFromTable(Class<? extends BaseEntity> entityClass, I id);

    boolean removeAllRecordsFromTable(Class<? extends BaseEntity> entityClass);

    <T extends BaseEntity, I> T getById(Class<? extends BaseEntity> entityClass, I id);

    <T extends BaseEntity> Iterable<T> getAllRecordsFromTable(Class<? extends BaseEntity> entityClass);

    <T extends BaseEntity, V> Iterable<T> getByFilters(Class<? extends BaseEntity> entityClass,
                                                       Set<Pair<String, V>> filters);
}
