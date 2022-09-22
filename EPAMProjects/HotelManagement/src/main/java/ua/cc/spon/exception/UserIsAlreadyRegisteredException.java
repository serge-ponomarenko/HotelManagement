package ua.cc.spon.exception;

public class UserIsAlreadyRegisteredException extends Exception {
    public UserIsAlreadyRegisteredException() {
        super("userAlreadyRegistered");
    }
}
