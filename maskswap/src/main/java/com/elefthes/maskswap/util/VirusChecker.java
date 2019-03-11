package com.elefthes.maskswap.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Logger;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

public class VirusChecker {
    public static boolean isVirus(InputStream target) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.util.VirusChecker");
        
        Path tmpPath = Files.createTempFile(Paths.get(System.getProperty("java.io.tmpdir"), "maskswap"), null, null);
        Files.copy(target, tmpPath);
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
    }
}
