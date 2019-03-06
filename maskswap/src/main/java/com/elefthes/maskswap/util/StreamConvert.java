package com.elefthes.maskswap.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class StreamConvert {
    /**
     * InputStreamの読み込みが終わったかは戻り値のバイト配列の長さから調べること。
     * 
     * @param is 
     * @param maxLength 読み込むバイト数
     * @return 
     * @throws IOException 
     */
    public static byte[] getBytes(InputStream is, int maxLength) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.util.StreamConvert");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream os = new BufferedOutputStream(baos, maxLength);

        try {
            for(int i = 0; i < maxLength; i++) {
                int c = is.read();
                if(c == -1) {
                    logger.info("読み込み終了 番号 : " + i);
                    break;
                } else {
                    os.write(c);
                }
            }
        } finally {
            if(os != null) {
                os.flush();
                os.close();
            }
        }
        
        return baos.toByteArray();
    }
}
