package ry.ms.businessLogic.login.exceptions;

public class UserDoesntExistException extends Exception{
    public UserDoesntExistException(String message) {
        super(message);
    }
}
