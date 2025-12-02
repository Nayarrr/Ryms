package ry.ms.Exceptions;

public class UserDoesntExistException extends Exception{
    public UserDoesntExistException(String message) {
        super(message);
    }
}
