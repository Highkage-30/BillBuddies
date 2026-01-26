package com.billbuddies.billbuddies_backend.exception;

public class MemberAlreadyInGroupException extends RuntimeException {
    public MemberAlreadyInGroupException(String message) {
        super(message);
    }
}
