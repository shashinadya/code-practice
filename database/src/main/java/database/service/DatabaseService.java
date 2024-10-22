package database.service;

import database.entity.BaseEntity;

import java.util.List;
import java.util.Map;

/**
 * The {@code DatabaseService} interface defines the contract for a service
 * that manages database operations such as creating tables, adding, updating,
 * retrieving, and removing records for entities extending {@link BaseEntity}.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public interface DatabaseService {

    /**
     * Creates a new table in the database based on the provided entity class.
     *
     * @param entityClass the class of the entity representing the table structure
     * @return {@code true} if the table is created successfully, {@code false} otherwise
     */
    boolean createTable(Class<? extends BaseEntity> entityClass);

    /**
     * Deletes the table corresponding to the provided entity class.
     *
     * @param entityClass the class of the entity representing the table to be deleted
     * @return {@code true} if the table is deleted successfully, {@code false} otherwise
     */
    boolean deleteTable(Class<? extends BaseEntity> entityClass);

    /**
     * Adds a new record to the table corresponding to the entity's class.
     *
     * @param entity the entity to be added as a new record
     * @param <T>    the type of the entity extending {@link BaseEntity}
     * @return the entity that was added, potentially updated with an ID or other metadata
     */
    <T extends BaseEntity> T addNewRecordToTable(T entity);

    /**
     * Adds multiple new records to the table corresponding to the provided entity class.
     *
     * @param entityClass the class of the entity representing the table
     * @param entities    a list of entities to be added as new records
     * @param <T>         the type of the entities extending {@link BaseEntity}
     * @return an {@link Iterable} containing the entities that were added
     */
    <T extends BaseEntity> Iterable<T> addNewRecordsToTable(Class<? extends BaseEntity> entityClass, List<T> entities);

    /**
     * Updates an existing record in the table corresponding to the entity's class.
     *
     * @param entity the entity containing the updated data
     * @param id     the ID of the record to be updated
     * @param <T>    the type of the entity extending {@link BaseEntity}
     * @return the updated entity
     */
    <T extends BaseEntity> T updateRecordInTable(T entity, Integer id);

    /**
     * Removes a record with the specified ID from the table corresponding to the provided entity class.
     *
     * @param entityClass the class of the entity representing the table
     * @param id          the ID of the record to be removed
     * @return {@code true} if the record was removed successfully, {@code false} otherwise
     */
    boolean removeRecordFromTable(Class<? extends BaseEntity> entityClass, Integer id);

    /**
     * Removes multiple records with the specified IDs from the table corresponding to the provided entity class.
     *
     * @param entityClass the class of the entity representing the table
     * @param ids         a list of IDs of the records to be removed
     * @return {@code true} if the records were removed successfully, {@code false} otherwise
     */
    boolean removeSpecificRecords(Class<? extends BaseEntity> entityClass, List<Integer> ids);

    /**
     * Removes all records from the table corresponding to the provided entity class.
     *
     * @param entityClass the class of the entity representing the table
     */
    void removeAllRecordsFromTable(Class<? extends BaseEntity> entityClass);

    /**
     * Retrieves a record with the specified ID from the table corresponding to the provided entity class.
     *
     * @param entityClass the class of the entity representing the table
     * @param id          the ID of the record to retrieve
     * @param <T>         the type of the entity extending {@link BaseEntity}
     * @return the entity corresponding to the specified ID, or {@code null} if not found
     */
    <T extends BaseEntity> T getById(Class<? extends BaseEntity> entityClass, Integer id);

    /**
     * Retrieves all records from the table corresponding to the provided entity class.
     *
     * @param entityClass the class of the entity representing the table
     * @param <T>         the type of the entities extending {@link BaseEntity}
     * @return an {@link Iterable} containing all records from the table
     */
    <T extends BaseEntity> Iterable<T> getAllRecordsFromTable(Class<? extends BaseEntity> entityClass);

    /**
     * Retrieves a limited number of records from the table corresponding to the provided entity class,
     * starting from a specific offset.
     *
     * @param entityClass the class of the entity representing the table
     * @param limit       the maximum number of records to retrieve
     * @param offset      the starting point for retrieval
     * @param <T>         the type of the entities extending {@link BaseEntity}
     * @return an {@link Iterable} containing the records within the specified range
     */
    <T extends BaseEntity> Iterable<T> getAllRecordsFromTable(Class<? extends BaseEntity> entityClass,
                                                              int limit, int offset);

    /**
     * Retrieves records from the table corresponding to the provided entity class based on the specified filters.
     *
     * @param entityClass the class of the entity representing the table
     * @param filters     a map containing filter criteria where the key is the field name
     *                    and the value is a list of acceptable field values
     * @param <T>         the type of the entities extending {@link BaseEntity}
     * @return an {@link Iterable} containing the records matching the filters
     */
    <T extends BaseEntity> Iterable<T> getByFilters(Class<? extends BaseEntity> entityClass,
                                                    Map<String, List<String>> filters);

    /**
     * Shuts down the database service, releasing any resources or connections.
     */
    void shutdown();
}
