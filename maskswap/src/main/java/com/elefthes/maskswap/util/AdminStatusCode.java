package com.elefthes.maskswap.util;

public enum AdminStatusCode {
    Failure(0),
    Success(1),
    NoOrder(2);
    
    private int id;
    
    private AdminStatusCode(int id) {
        this.id = id;
    }
    
    public int getId() {
        return this.id;
    }
}
