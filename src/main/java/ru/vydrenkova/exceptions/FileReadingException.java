package ru.vydrenkova.exceptions;

public class FileReadingException extends RuntimeException{
    public FileReadingException(String message) {
        super(message);
    }
}
