package database.service;

public class ServiceConstants {
    public static final String ENTITY_IS_NOT_FOUND = "Entity with provided Id does not exist";
    public static final String ID_PROVIDED_MANUALLY = "User cannot provide id manually. Ids are filled automatically.";
    public static final String INVALID_PARAMETER_VALUE = "Invalid parameter value. " +
            "Limit value should be in(0..{MAX_LIMIT_VALUE}), offset value should be >= 0";

    private ServiceConstants() {
    }
}
