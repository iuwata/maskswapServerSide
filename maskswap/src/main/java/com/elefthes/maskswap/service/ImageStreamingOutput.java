package com.elefthes.maskswap.service;

import static com.elefthes.maskswap.service.VideoStreamingOutput.MAX_LENGTH;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

public class ImageStreamingOutput implements StreamingOutput{
    static final int MAX_LENGTH = 1024 * 128;
    
    private long orderId;
    
    private InputStream is;
    
    public ImageStreamingOutput(long orderId, InputStream is) {
        this.orderId = orderId;
        this.is = is;
    }

    @Override
    public void write(OutputStream os) throws IOException, WebApplicationException {
        BufferedOutputStream bos = new BufferedOutputStream(os, MAX_LENGTH);
        
        int c = 0;
        try{
            while(c != -1) {
                for(int i = 0; i < MAX_LENGTH; i++) {
                    c = is.read();
                    if(c == -1) {
                        break;
                    } else {
                        os.write(c);
                    }
                }
                os.flush();
            }
        } finally {
            os.flush();
            is.close();
        }
    }
}
