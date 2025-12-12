package ry.ms.businessLogic.login.exceptions;

public class IncorrectPasswordException extends Exception{
    public IncorrectPasswordException(String message){
        super(message);
    }
}
