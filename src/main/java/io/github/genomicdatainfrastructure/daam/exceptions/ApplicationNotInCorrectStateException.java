package io.github.genomicdatainfrastructure.daam.exceptions;

public class ApplicationNotInCorrectStateException extends RuntimeException {
    public ApplicationNotInCorrectStateException(String message) {
        super(message);
    }
}