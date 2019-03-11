package com.elefthes.maskswap.exception;

import com.elefthes.maskswap.util.StatusCode;

public class CustomException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    
    private StatusCode code;
    
    public CustomException(StatusCode code) {
        this.code = code;
    }

    public StatusCode getCode() {
        return code;
    }
}
