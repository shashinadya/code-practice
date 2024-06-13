package code.practice.exceptions.database;

public class TableIsNotCompatibleWithEntityException extends RuntimeException {

    public TableIsNotCompatibleWithEntityException(String message) {
        super(message);
    }
}
