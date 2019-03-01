package com.elefthes.maskswap.dto.response;

public class LoginResponse extends StatusResponse{
    private long userId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
