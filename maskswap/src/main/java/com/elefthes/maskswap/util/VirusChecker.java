package com.elefthes.maskswap.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.logging.Logger;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

public class VirusChecker {
    /*public static boolean isVirus(InputStream target) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.util.VirusChecker");
        
        Path tmpPath = Files.createTempFile(Paths.get(System.getProperty("java.io.tmpdir"), "maskswap"), null, null);
        Files.copy(target, tmpPath, StandardCopyOption.REPLACE_EXISTING);
        InputStream is = Files.newInputStream(tmpPath, StandardOpenOption.DELETE_ON_CLOSE);
        BufferedInputStream bis = new BufferedInputStream(is);
        
        ClamavClient client = new ClamavClient("localhost");
        ScanResult scanSrcResult = client.scan(bis);
        bis.close();
        if(scanSrcResult instanceof ScanResult.VirusFound) {
            logger.info("ウイルスが見つかりました");
            return true;
        }
        
        return false;
    }*/
    
    public static boolean isVirus(Path tmpPath) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.util.VirusChecker");
        InputStream is = null;
        try {
            is = StreamConverter.getInpuStreamReadOnly(tmpPath);
            BufferedInputStream bis = new BufferedInputStream(is);

            ClamavClient client = new ClamavClient("localhost");
            ScanResult scanResult = client.scan(bis);
            bis.close();
            if(scanResult instanceof ScanResult.VirusFound) {
                logger.info("ウイルスが見つかりました");
                return true;
            }

            return false;
        } finally {
            if(is != null) {
                is.close();
            }
        }
    }
    
    public static boolean isVirus(byte[] target) {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.util.VirusChecker");
        
        int maxLength = 1024 * 128; //128kb
        
        ByteArrayInputStream is = new ByteArrayInputStream(target);
        BufferedInputStream bis = new BufferedInputStream(is, maxLength);
        
        ClamavClient client = new ClamavClient("localhost");
        ScanResult result = client.scan(is);
        
        if(result instanceof ScanResult.VirusFound) {
            logger.info("ウイルスが見つかりました");
            return true;
        }
        
        return false;
    }
}
