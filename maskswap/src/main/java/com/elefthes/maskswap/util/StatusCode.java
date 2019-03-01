package com.elefthes.maskswap.util;

public enum StatusCode {
    Failure(0),
    Success(1),
    NeedLogin(2),
    EmailAlreadyExist(3),
    IncompleteEmail(4),
    IncompletePassword(5),
    EmailDoesNotExist(6),
    IncorrectPassword(7),
    EmailAlreadyAuthenticated(8),
    EmailAuthenticationExpired(9),
    IncorrectAuthenticationCode(10),
    NoSrcVideo(11),
    NoDstVideo(12);
    
    
    private int id;
    
    private StatusCode(int id) {
        this.id = id;
    }
    
    public int getId() {
        return this.id;
    }
}
