package com.elefthes.maskswap.dto.response;

import com.elefthes.maskswap.util.AdminStatusCode;

public class AdminStatusResponse {
    private int result;
    
    public AdminStatusResponse() {}
    
    public AdminStatusResponse(AdminStatusCode result) {
        this.result = result.getId();
    }
    
    public void setResult(AdminStatusCode result) {
        this.result = result.getId();
    }
}
