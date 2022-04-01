package com.szs.szsrefund.domain.user.exception;

public class AlreadyExistsUserIdException extends RuntimeException {

    private String userId;

    public AlreadyExistsUserIdException(String userId) {
        this.userId = userId;
    }
}
