package com.szs.szsrefund.domain.user.exception;

/**
 * 이미 존재하고있는 회원입니다.
 */
public class AlreadyExistsUserException extends RuntimeException {

    private String name;

    public AlreadyExistsUserException(String name) {
        this.name = name;
    }
}
