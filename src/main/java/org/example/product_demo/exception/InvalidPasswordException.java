package org.example.product_demo.exception;

public class InvalidPasswordException extends BadRequestException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}