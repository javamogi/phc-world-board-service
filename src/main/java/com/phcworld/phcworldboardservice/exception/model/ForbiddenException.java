package com.phcworld.phcworldboardservice.exception.model;

public class ForbiddenException extends CustomBaseException{
    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }

    public ForbiddenException(){
        super(ErrorCode.FORBIDDEN);
    }
}
