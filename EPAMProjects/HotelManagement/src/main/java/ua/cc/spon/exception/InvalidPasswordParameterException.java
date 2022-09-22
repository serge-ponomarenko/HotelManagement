package ua.cc.spon.exception;

public class InvalidPasswordParameterException extends IllegalArgumentException {
    public InvalidPasswordParameterException() {
        super("invalidPassword");
    }
}
