package com.phcworld.phcworldboardservice.exception.model;

public class NotMatchUserException extends CustomBaseException{
    public NotMatchUserException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }

    public NotMatchUserException(){
        super(ErrorCode.FORBIDDEN);
    }
}
