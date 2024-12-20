package ru.vydrenkova.exceptions;

public class NoSuchSockException extends RuntimeException{
    public NoSuchSockException(String message){
        super(message);
    }
}
