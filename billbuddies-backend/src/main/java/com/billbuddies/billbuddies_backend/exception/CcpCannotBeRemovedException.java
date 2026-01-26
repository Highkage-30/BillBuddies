package com.billbuddies.billbuddies_backend.exception;

public class CcpCannotBeRemovedException extends RuntimeException {
    public CcpCannotBeRemovedException(String message) {
        super(message);
    }
}
