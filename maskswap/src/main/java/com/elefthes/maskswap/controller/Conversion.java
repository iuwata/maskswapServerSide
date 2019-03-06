package com.elefthes.maskswap.controller;

import com.elefthes.maskswap.dto.request.OrderConversionRequest;
import com.elefthes.maskswap.dto.response.OrderConversionResponse;
import com.elefthes.maskswap.exception.CustomException;
import com.elefthes.maskswap.service.OrderService;
import com.elefthes.maskswap.util.StatusCode;
import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.media.multipart.FormDataParam;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

@ApplicationScoped
@Path("conversion")
public class Conversion {
    @Inject 
    OrderService orderService;
    
    @POST
    @Path("order")
    @Consumes(MediaType.MULTIPART_FORM_DATA) 
    @Produces(MediaType.APPLICATION_JSON)
    //public String orderConversion(OrderConversionRequest requestData, @Context HttpServletRequest req) {
    public String orderConversion(@FormDataParam("token") String token, 
                                  @FormDataParam("srcFile") InputStream srcFile,
                                  @FormDataParam("dstFile") InputStream dstFile,
                                  @Context HttpServletRequest req) throws IOException {
    
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Conversion");
        logger.info("Conversionが呼び出されました");
        
        OrderConversionResponse responseData = new OrderConversionResponse();
        Gson gson = new Gson();
        
        try {
            //トークンチェック
            HttpSession session = req.getSession(false);
            if(session == null) {
                logger.info("セッションが存在しません");
                throw new CustomException(StatusCode.NeedLogin);
            }
            if(!(session.getAttribute("token").equals(token))){
                logger.info("トークンが存在しません");
                throw new CustomException(StatusCode.NeedLogin);
            }

            if(dstFile == null) {
                logger.info("dstFileがnullです");
                throw new CustomException(StatusCode.NoDstVideo);
            }
            if(srcFile == null) {
                logger.info("srcFileがnullです");
                throw new CustomException(StatusCode.NoSrcVideo);
            }
            logger.info("Conversion2");
            
            //ウイルスチェック
            /*ClamavClient client = new ClamavClient("localhost");
            ScanResult scanSrcResult = client.scan(srcFile);
            ScanResult scanDstResult = client.scan(dstFile);
            if(scanDstResult instanceof ScanResult.VirusFound || scanSrcResult instanceof ScanResult.VirusFound) {
                logger.info("ウイルスが見つかりました");
            }*/
            logger.info("Conversion3");
            long userId = (long)session.getAttribute("userId");

            long orderId = orderService.create(srcFile, dstFile, userId);
            logger.info("Conversion6");
            responseData.setOrderId(orderId);
            responseData.setResult(StatusCode.Success);
            logger.info("動画アップロード完了");
        } catch(IOException e) {
            responseData.setResult(StatusCode.Failure);
            logger.info("動画アップロード失敗1");
        } catch(CustomException e) {
            responseData.setResult(e.getCode());
        } catch(RuntimeException e) {
            responseData.setResult(StatusCode.Failure);
            e.printStackTrace();
            logger.info("動画アップロード失敗2");
        } finally {
            if(srcFile != null) {
                srcFile.close();
            }
            if(dstFile != null) {
                dstFile.close();
            }
        }
        
        return gson.toJson(responseData);
    }
}
