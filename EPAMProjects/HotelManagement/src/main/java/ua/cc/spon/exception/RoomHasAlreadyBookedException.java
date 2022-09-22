package ua.cc.spon.exception;

public class RoomHasAlreadyBookedException extends Exception {
    public RoomHasAlreadyBookedException() {
        super("roomHasAlreadyBooked");
    }
}
