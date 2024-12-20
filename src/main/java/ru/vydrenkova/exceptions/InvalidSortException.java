package ru.vydrenkova.exceptions;

public class InvalidSortException extends RuntimeException{
    public InvalidSortException(String message) {
        super(message);
    }
}
