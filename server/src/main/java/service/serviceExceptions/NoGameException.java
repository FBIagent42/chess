package service.serviceExceptions;

public class NoGameException extends RuntimeException {
    public NoGameException(){}
    public NoGameException(String message) {
        super(message);
    }
}
