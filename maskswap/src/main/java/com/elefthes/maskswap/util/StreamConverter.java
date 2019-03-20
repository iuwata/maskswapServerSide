package com.elefthes.maskswap.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.logging.Logger;

public class StreamConverter {
    
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
    
    /**
     * 引数に渡したInputStreamは再利用しないこと
     * 引数に渡したInputStreamはclose()される
     */
    public static Path getTmpFile(InputStream target) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.util.StreamConvert");
        Path tmpPath = Files.createTempFile(Paths.get(System.getProperty("java.io.tmpdir"), "maskswap"), null, null);
        Files.copy(target, tmpPath, StandardCopyOption.REPLACE_EXISTING);
        target.close();
        return tmpPath;
    }
    
    /**
     * ReadOnlyなInputStreamを返す
     * @param tmpPath
     * @return
     * @throws IOException 
     */
    public static InputStream getInpuStreamReadOnly(Path tmpPath) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.util.StreamConvert");
        InputStream is = Files.newInputStream(tmpPath, StandardOpenOption.READ);
        return is;
    }
    
    /**
     * close()すると読み込み元ファイルを削除するInputStreamを返す
     * @param tmpPath
     * @return
     * @throws IOException 
     */
    public static InputStream getInputStreamDeleteOnClose(Path tmpPath) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.util.StreamConvert");
        InputStream is = Files.newInputStream(tmpPath, StandardOpenOption.DELETE_ON_CLOSE);
        return is;
    } 
}
