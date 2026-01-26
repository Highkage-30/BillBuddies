package com.billbuddies.billbuddies_backend.exception;

public class MemberNotInGroupException extends RuntimeException {
    public MemberNotInGroupException(String message) {
        super(message);
    }
}
