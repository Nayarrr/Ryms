package ry.ms.businessLogic.user.login.exceptions;

public class UserDoesntExistException extends Exception{
    public UserDoesntExistException(String message) {
        super(message);
    }
}
