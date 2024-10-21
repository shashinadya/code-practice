package database.service;

public class ServiceConstants {
    public static final String ENTITY_IS_NOT_FOUND = "Entity with provided Id does not exist";
    public static final String ID_PROVIDED_MANUALLY = "User cannot provide id manually. Ids are filled automatically.";
    public static final String INVALID_PARAMETER_VALUE = "Invalid parameter value. " +
            "Limit value should be in(0..{MAX_LIMIT_VALUE}), offset value should be >= 0";
    public static final String ENTITIES_LIST_NULL_OR_EMPTY = "Entities list cannot be null or empty";
    public static final String IDS_LIST_NULL_OR_EMPTY = "IDs list cannot be null or empty";

    private ServiceConstants() {
    }
}
