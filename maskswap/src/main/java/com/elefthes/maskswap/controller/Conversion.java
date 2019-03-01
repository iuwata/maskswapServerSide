package com.elefthes.maskswap.controller;

import com.elefthes.maskswap.dto.request.OrderConversionRequest;
import com.elefthes.maskswap.dto.response.OrderConversionResponse;
import com.elefthes.maskswap.service.OrderService;
import com.elefthes.maskswap.util.StatusCode;
import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

@ApplicationScoped
@Path("conversion")
public class Conversion {
    @Inject 
    OrderService orderService;
    
    @POST
    @Path("order")
    @Consumes(MediaType.APPLICATION_JSON) 
    @Produces(MediaType.APPLICATION_JSON)
    public String orderConversion(OrderConversionRequest requestData, @Context HttpServletRequest req) {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Registration");
        
        OrderConversionResponse responseData = new OrderConversionResponse();
        Gson gson = new Gson();
        
        try {
            //トークンチェック
            HttpSession session = req.getSession(false);
            if(!(session.getAttribute("token").equals(requestData.getToken()))){
                logger.info("トークンが存在しません");
            }

            ByteArrayInputStream srcFile = requestData.getSrcFile();
            ByteArrayInputStream dstFile = requestData.getDstFile();


            //ウイルスチェック
            ClamavClient client = new ClamavClient("localhost");
            ScanResult scanSrcResult = client.scan(srcFile);
            ScanResult scanDstResult = client.scan(dstFile);
            if(scanDstResult instanceof ScanResult.VirusFound || scanSrcResult instanceof ScanResult.VirusFound) {
                logger.info("ウイルスが見つかりました");
            } 

            long userId = (long)session.getAttribute("userId");

            long orderId = orderService.create(srcFile, dstFile, userId);
            responseData.setOrderId(orderId);
            responseData.setResult(StatusCode.Success);
        } catch(IOException e) {
            responseData.setResult(StatusCode.Failure);
        } catch(RuntimeException e) {
            responseData.setResult(StatusCode.Failure);
        }
        
        return gson.toJson(responseData);
    }
}
