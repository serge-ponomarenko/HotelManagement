package ua.cc.spon.exception;

public class UserNotFoundException extends Exception {
    public UserNotFoundException() {
        super("noUserFound");
    }
}
