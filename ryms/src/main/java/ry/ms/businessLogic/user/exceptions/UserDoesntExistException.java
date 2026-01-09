package ry.ms.businessLogic.user.exceptions;

public class UserDoesntExistException extends Exception{
    public UserDoesntExistException(String message) {
        super(message);
    }
}
