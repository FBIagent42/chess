package service.serviceExceptions;

public class ColorTakenException extends RuntimeException {
    public ColorTakenException(){}
    public ColorTakenException(String message) {
        super(message);
    }
}
