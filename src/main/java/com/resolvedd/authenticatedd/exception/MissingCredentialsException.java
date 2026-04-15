package com.resolvedd.authenticatedd.exception;

public class MissingCredentialsException extends RuntimeException {

    public MissingCredentialsException(String message) {
        super(message);
    }
}
