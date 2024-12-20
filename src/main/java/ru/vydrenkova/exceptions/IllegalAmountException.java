package ru.vydrenkova.exceptions;

public class IllegalAmountException extends RuntimeException{
    public IllegalAmountException(String message) {
        super(message);
    }
}
