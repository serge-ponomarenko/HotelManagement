package ua.cc.spon.exception;

public class NoUserFoundException extends Exception {

    public NoUserFoundException() {
        super("User not found!");
    }
}