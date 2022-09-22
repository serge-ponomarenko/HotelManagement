package ua.cc.spon.exception;

public class IllegalPasswordException extends Exception {
    public IllegalPasswordException() {
        super("wrongPassword");
    }
}
