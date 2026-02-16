package service.serviceExceptions;

public class AlreadyTakenException extends RuntimeException {
    public AlreadyTakenException(){}
    public AlreadyTakenException(String message) {
        super(message);
    }
}
