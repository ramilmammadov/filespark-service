package com.filespark.storage.infra.exception;

public class FileNotOwnedException extends RuntimeException {
    public FileNotOwnedException(String message) {
        super(message);
    }
}
