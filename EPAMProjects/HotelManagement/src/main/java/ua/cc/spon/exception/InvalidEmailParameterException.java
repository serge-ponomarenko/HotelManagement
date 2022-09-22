package ua.cc.spon.exception;

public class InvalidEmailParameterException extends IllegalArgumentException {
    public InvalidEmailParameterException() {
        super("invalidEmail");
    }
}
