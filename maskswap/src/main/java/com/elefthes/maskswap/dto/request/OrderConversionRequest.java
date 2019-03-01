package com.elefthes.maskswap.dto.request;

import java.io.ByteArrayInputStream;

public class OrderConversionRequest {
    private long userId;
    private String token;
    
    private ByteArrayInputStream srcFile;
    private ByteArrayInputStream dstFile;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    public ByteArrayInputStream getSrcFile() {
        return srcFile;
    }

    public void setSrcFile(ByteArrayInputStream srcFile) {
        this.srcFile = srcFile;
    }

    public ByteArrayInputStream getDstFile() {
        return dstFile;
    }

    public void setDstFile(ByteArrayInputStream dstFile) {
        this.dstFile = dstFile;
    }
}
