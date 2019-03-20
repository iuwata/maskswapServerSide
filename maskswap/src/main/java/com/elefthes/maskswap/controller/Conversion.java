package com.elefthes.maskswap.controller;

import com.elefthes.maskswap.dto.request.OrderConversionRequest;
import com.elefthes.maskswap.dto.request.RequestWithToken;
import com.elefthes.maskswap.dto.response.OrderConversionResponse;
import com.elefthes.maskswap.dto.response.OrderStatusResponse;
import com.elefthes.maskswap.dto.response.StatusResponse;
import com.elefthes.maskswap.entity.OrdersEntity;
import com.elefthes.maskswap.exception.CustomException;
import com.elefthes.maskswap.service.OrderService;
import com.elefthes.maskswap.util.StatusCode;
import com.elefthes.maskswap.util.StreamConverter;
import com.elefthes.maskswap.util.VirusChecker;
import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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
    @Path("status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getStatus(RequestWithToken requestData, @Context HttpServletRequest req) {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Conversion");
        
        StatusResponse responseData = new StatusResponse();
        Gson gson = new Gson();
        
        try {
            //トークンチェック
            HttpSession session = req.getSession(false);
            if(session == null) {
                logger.info("セッションが存在しません");
                throw new CustomException(StatusCode.NeedLogin);
            }
            if(!(session.getAttribute("token").equals(requestData.getToken()))){
                logger.info("トークンが存在しません");
                throw new CustomException(StatusCode.NeedLogin);
            }
            
            List<OrdersEntity> orders = orderService.getOrders((long)session.getAttribute("userId"));
            
            if(orders.size() == 0) {
                responseData.setResult(StatusCode.NoOrder);
                return gson.toJson(responseData);
            }
            
            for(OrdersEntity order : orders) {
                if(order.getPaymentDate() == null) {
                    responseData.setResult(StatusCode.IncompleteOrder);
                    return gson.toJson(responseData);
                }
            }
            responseData.setResult(StatusCode.CompleteOrder);
        } catch(CustomException e) {
            responseData.setResult(e.getCode());
            e.printStackTrace();
        } catch(RuntimeException e) {
            responseData.setResult(StatusCode.Failure);
            logger.info("依頼状況取得失敗");
            e.printStackTrace();
        }
        
        return gson.toJson(responseData);
    }
    
    @POST
    @Path("order/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getOrderStatus(RequestWithToken requestData, @Context HttpServletRequest req) {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Conversion");
        OrderStatusResponse responseData = new OrderStatusResponse();
        Gson gson = new Gson();
        
        try {
            //トークンチェック
            HttpSession session = req.getSession(false);
            if(session == null) {
                logger.info("セッションが存在しません");
                throw new CustomException(StatusCode.NeedLogin);
            }
            if(!(session.getAttribute("token").equals(requestData.getToken()))){
                logger.info("トークンが存在しません");
                throw new CustomException(StatusCode.NeedLogin);
            }
            
            List<OrdersEntity> orders = orderService.getOrders((long)session.getAttribute("userId"));
            
            if(orders.size() == 0) {
                responseData.setResult(StatusCode.NoOrder);
                return gson.toJson(responseData);
            }
            
            for(OrdersEntity order : orders) {
                if(order.getPaymentDate() == null) {
                    responseData.setOrderId(order.getOrderId());
                    if(order.getSrcStorage() == 0) {
                        //srcがアップロードされていない
                        if(order.getDstStorage() == 0) {
                            //src,dstがアップロードされていない
                            responseData.setResult(StatusCode.VideosNotUploaded);
                            return gson.toJson(responseData);
                        } else {
                            //srcがアップロードされていないかつdstがアップロードされている
                            responseData.setResult(StatusCode.DstVideoUploaded);
                            return gson.toJson(responseData);
                        }
                    } else {
                        //srcがアップロードされている
                        if(order.getDstStorage() == 0) {
                            //srcがアップロードされているかつdstがアップロードされていない
                            responseData.setResult(StatusCode.SrcVideoUploaded);
                            return gson.toJson(responseData);
                        } else {
                            //src,dstがアップロードされている
                            responseData.setResult(StatusCode.VideosNotUploaded);
                            return gson.toJson(responseData);
                        }
                    } 
                }
            }
            responseData.setResult(StatusCode.CompleteOrder);
        } catch(CustomException e) {
            responseData.setResult(e.getCode());
            e.printStackTrace();
        } catch(RuntimeException e) {
            responseData.setResult(StatusCode.Failure);
            logger.info("依頼状況取得失敗");
            e.printStackTrace();
        }
        
        return gson.toJson(responseData);
    }
    
    
    @POST
    @Path("order")
    @Consumes(MediaType.MULTIPART_FORM_DATA) 
    @Produces(MediaType.APPLICATION_JSON)
    public String orderConversion(@FormDataParam("token") String token, 
                                  @FormDataParam("srcFile") InputStream originSrcFile,
                                  @FormDataParam("dstFile") InputStream originDstFile,
                                  @Context HttpServletRequest req) throws IOException {
    
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Conversion");
        logger.info("Conversionが呼び出されました");
        
        OrderConversionResponse responseData = new OrderConversionResponse();
        InputStream srcFile = null;
        InputStream dstFile = null;
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

            if(originDstFile == null) {
                logger.info("dstFileがnullです");
                throw new CustomException(StatusCode.NoDstVideo);
            }
            if(originSrcFile == null) {
                logger.info("srcFileがnullです");
                throw new CustomException(StatusCode.NoSrcVideo);
            }
            logger.info("Conversion2");
            
            java.nio.file.Path srcTmpFile =  StreamConverter.getTmpFile(originSrcFile);
            java.nio.file.Path dstTmpFile =  StreamConverter.getTmpFile(originDstFile);
            
            //ウイルスチェック
            if(VirusChecker.isVirus(srcTmpFile) || VirusChecker.isVirus(dstTmpFile)) {
                //ウイルス検知
                throw new CustomException(StatusCode.VirusFound);
            }
            
            srcFile = StreamConverter.getInputStreamDeleteOnClose(srcTmpFile);
            dstFile = StreamConverter.getInputStreamDeleteOnClose(dstTmpFile);
            
            
            logger.info("Conversion3");
            long userId = (long)session.getAttribute("userId");

            long orderId = orderService.create(srcFile, dstFile, userId);
            logger.info("Conversion6");
            responseData.setOrderId(orderId);
            responseData.setResult(StatusCode.Success);
            logger.info("動画アップロード完了");
        } catch(IOException e) {
            responseData.setResult(StatusCode.Failure);
            e.printStackTrace();
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
    
    /*@POST
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON) 
    public String conversionStatus(@Context HttpServletRequest req) {
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
    }*/
}
