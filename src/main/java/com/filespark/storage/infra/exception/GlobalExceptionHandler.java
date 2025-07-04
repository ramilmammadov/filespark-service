package com.filespark.storage.infra.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateFileException.class)
    public ResponseEntity<String> handleDuplicate(DuplicateFileException ex) {
        return ResponseEntity.status(409).body(ex.getMessage());
    }

    @ExceptionHandler(FileNotOwnedException.class)
    public ResponseEntity<String> handleNotOwned(FileNotOwnedException ex) {
        return ResponseEntity.status(403).body(ex.getMessage());
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<String> handleNotFound(FileNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}
