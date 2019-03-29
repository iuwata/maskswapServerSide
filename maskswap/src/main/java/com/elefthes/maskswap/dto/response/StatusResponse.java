package com.elefthes.maskswap.dto.response;

import com.elefthes.maskswap.util.StatusCode;

public class StatusResponse {
    private int result;

    public StatusResponse() {}
    
    public StatusResponse(StatusCode result) {
        this.result = result.getId();
    }

    public void setResult(StatusCode result) {
        this.result = result.getId();
    }

    public int getResult() {
        return result;
    }
    
}
