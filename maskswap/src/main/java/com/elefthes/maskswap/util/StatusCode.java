package com.elefthes.maskswap.util;

public enum StatusCode {
    Failure(0),
    Success(1),
    NeedToLogin(2),
    emailAlreadyExist(3),
    IncompleteEmail(4),
    IncompletePassword(5);
    
    private int id;
    
    private StatusCode(int id) {
        this.id = id;
    }
    
    public int getId() {
        return this.id;
    }
}
