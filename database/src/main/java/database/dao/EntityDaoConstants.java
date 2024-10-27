package database.dao;

/**
 * The {@code EntityDaoConstants} class contains constant values used throughout
 * the {@link EntityDao} implementations. These constants represent standardized
 * error and validation messages that are used for exception handling and logging.
 *
 * <p>This class is designed to provide a centralized location for common messages to
 * ensure consistency and maintainability across the codebase. By using these constants,
 * it reduces the chance of typos or inconsistent error messages, and it makes future
 * changes easier to implement.
 *
 * <p>This class cannot be instantiated as it only serves as a holder for constants.
 * The private constructor prevents instantiation and enforces the utility nature of
 * the class.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public class EntityDaoConstants {
    public static final String ENTITY_IS_NOT_FOUND = "Entity with provided Id does not exist";
    public static final String ID_PROVIDED_MANUALLY = "User cannot provide id manually. Ids are filled automatically.";
    public static final String INVALID_PARAMETER_VALUE = "Invalid parameter value. " +
            "Limit value should be in(0..{MAX_LIMIT_VALUE}), offset value should be >= 0";
    public static final String ENTITIES_LIST_NULL_OR_EMPTY = "Entities list cannot be null or empty";
    public static final String IDS_LIST_NULL_OR_EMPTY = "IDs list cannot be null or empty";

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private EntityDaoConstants() {
    }
}
