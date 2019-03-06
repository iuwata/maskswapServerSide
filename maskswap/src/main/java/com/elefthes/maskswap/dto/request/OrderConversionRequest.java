package com.elefthes.maskswap.dto.request;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.glassfish.jersey.media.multipart.FormDataParam;

public class OrderConversionRequest {
    //private long userId;
    @FormDataParam("token") 
    private String token;
    
    @FormDataParam("srcFile") 
    private InputStream srcFile;
    
    @FormDataParam("dstFile")
    private InputStream dstFile;

    public OrderConversionRequest() {}
    
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public InputStream getSrcFile() {
        return srcFile;
    }

    public void setSrcFile(InputStream srcFile) {
        this.srcFile = srcFile;
    }

    public InputStream getDstFile() {
        return dstFile;
    }
    
    
    public void setDstFile(InputStream dstFile) {
        this.dstFile = dstFile;
    }
}
