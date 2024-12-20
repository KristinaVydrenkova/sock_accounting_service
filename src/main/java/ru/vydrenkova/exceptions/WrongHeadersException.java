package ru.vydrenkova.exceptions;

public class WrongHeadersException extends RuntimeException{
    public WrongHeadersException(String message) {
        super(message);
    }
}
