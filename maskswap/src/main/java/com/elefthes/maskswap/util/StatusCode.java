package com.elefthes.maskswap.util;

public enum StatusCode {
    Failure(0),
    Success(1),
    NeedToLogin(2),
    IncompleteForm(3),
    emailAlreadyExist(4),
    IncompleteEmail(5),
    IncompletePassword(6);
    
    private int id;
    
    private StatusCode(int id) {
        this.id = id;
    }
    
    public int getId() {
        return this.id;
    }
}
