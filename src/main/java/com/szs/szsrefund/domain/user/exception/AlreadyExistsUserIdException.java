package com.szs.szsrefund.domain.user.exception;

/**
 * 존재하는 아이디
 */
public class AlreadyExistsUserIdException extends RuntimeException {

    private String userId;

    public AlreadyExistsUserIdException(String userId) {
        this.userId = userId;
    }
}
