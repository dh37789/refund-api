package com.szs.szsrefund.domain.user.exception;

public class AlreadyExistsUserException extends RuntimeException {

    private String name;

    public AlreadyExistsUserException(String name) {
        this.name = name;
    }
}
