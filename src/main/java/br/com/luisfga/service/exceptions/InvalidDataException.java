package br.com.luisfga.service.exceptions;

public class InvalidDataException extends Exception {
    public InvalidDataException() {}
    public InvalidDataException(Throwable ex) {
        addSuppressed(ex);
    }
}