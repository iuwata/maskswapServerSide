package com.elefthes.maskswap.exception;

import com.elefthes.maskswap.util.AdminStatusCode;

public class AdminCustomException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    
    private AdminStatusCode code;
    
    public AdminCustomException(AdminStatusCode code) {
        this.code = code;
    }
    
    public AdminStatusCode getCode() {
        return code;
    }
}
