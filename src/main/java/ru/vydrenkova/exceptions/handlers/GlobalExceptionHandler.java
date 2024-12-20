package ru.vydrenkova.exceptions.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.vydrenkova.exceptions.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalAmountException.class, EmptyFileException.class,
            FileReadingException.class, WrongFormatException.class, WrongHeadersException.class})
    public ResponseEntity<String> handleBadRequestException(IllegalAmountException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(NoSuchSockException.class)
    public ResponseEntity<String> handleNotFoundException(NoSuchSockException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Внутренняя ошибка сервера: " + e.getMessage());
    }
}
