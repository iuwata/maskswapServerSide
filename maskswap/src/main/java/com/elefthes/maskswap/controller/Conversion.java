package com.elefthes.maskswap.controller;

import com.elefthes.maskswap.dto.request.CreateChargeRequest;
import com.elefthes.maskswap.dto.request.DeleteVideoRequest;
import com.elefthes.maskswap.dto.request.GetAmountRequest;
import com.elefthes.maskswap.dto.request.OrderConversionRequest;
import com.elefthes.maskswap.dto.request.RequestWithToken;
import com.elefthes.maskswap.dto.request.SetPlanRequest;
import com.elefthes.maskswap.dto.response.GetAmountResponse;
import com.elefthes.maskswap.dto.response.OrderConversionResponse;
import com.elefthes.maskswap.dto.response.OrderStatusResponse;
import com.elefthes.maskswap.dto.response.SetPlanResponse;
import com.elefthes.maskswap.dto.response.StatusResponse;
import com.elefthes.maskswap.entity.OrdersEntity;
import com.elefthes.maskswap.exception.CustomException;
import com.elefthes.maskswap.service.ChargeService;
import com.elefthes.maskswap.service.OrderService;
import com.elefthes.maskswap.service.OrderVideoService;
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

@ApplicationScoped
@Path("conversion")
public class Conversion {
    @Inject 
    OrderService orderService;
    
    @Inject
    ChargeService chargeService;
    
    @POST
    @Path("order/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getOrderStatus(RequestWithToken requestData, @Context HttpServletRequest req) {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Conversion.getOrderStatus");
        logger.info("依頼状況取得開始");
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
            
            List<OrdersEntity> orders = orderService.getOrdersEntityEndDateNull((long)session.getAttribute("userId"));
            
            //変換未完了の依頼が存在しない
            if(orders.size() == 0) {
                responseData.setResult(StatusCode.NoOrder);
                return gson.toJson(responseData);
            }
            
            for(OrdersEntity order : orders) {
                //変換未完了かつ決済がまだ済んでいない
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
                            responseData.setDstDuration(order.getDstDuration());
                            return gson.toJson(responseData);
                        }
                    } else {
                        //srcがアップロードされている
                        if(order.getDstStorage() == 0) {
                            //srcがアップロードされているかつdstがアップロードされていない
                            responseData.setResult(StatusCode.SrcVideoUploaded);
                            responseData.setSrcDuration(order.getSrcDuration());
                            return gson.toJson(responseData);
                        } else {
                            //src,dstがアップロードされている
                            responseData.setResult(StatusCode.VideosUploaded);
                            responseData.setDstDuration(order.getDstDuration());
                            responseData.setSrcDuration(order.getSrcDuration());
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
        
        logger.info("依頼状況取得完了");
        logger.info("result : " + responseData.getResult());
        
        return gson.toJson(responseData);
    }
    
    @POST
    @Path("order/frame")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String orderConversionFrame(RequestWithToken requestData, @Context HttpServletRequest req) {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Conversion.orderConversionFrame");
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
            
            /*if(orderService.getOrdersEntityEndDateNull((long)session.getAttribute("userId")).size() != 0) {
                throw new CustomException(StatusCode.OrderAlreadyExist);
            }*/
            
            orderService.create((long)session.getAttribute("userId"));
            responseData.setResult(StatusCode.Success);
        } catch(CustomException e) {
            responseData.setResult(e.getCode());
            e.printStackTrace();
        } catch(RuntimeException e) {
            responseData.setResult(StatusCode.Failure);
            e.printStackTrace();
        }
        
        return gson.toJson(responseData);
        
    }
    
    @POST
    @Path("order/plan")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String setPlan(SetPlanRequest requestData, @Context HttpServletRequest req) {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Conversion.setPlan");
        logger.info("プラン変更開始");
        SetPlanResponse responseData = new SetPlanResponse();
        Gson gson = new Gson();
        
        logger.info("オーダーID : " +  requestData.getOrderId());
        logger.info("プラン : " + requestData.getPlan());
        
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
            
            /*if(requestData.getPlan() == 0) {
                throw new CustomException(StatusCode.NoPlan);
            }*/
            
            orderService.setPlan(requestData.getOrderId(), requestData.getPlan());
            responseData.setAmount(orderService.getAmount(requestData.getOrderId()));
            responseData.setResult(StatusCode.Success);
        } catch(CustomException e) {
            responseData.setResult(e.getCode());
            e.printStackTrace();
        } catch(RuntimeException e) {
            logger.info("プラン変更失敗");
            responseData.setResult(StatusCode.Failure);
            e.printStackTrace();
        }
        
        return gson.toJson(responseData);
    }
    
    @POST
    @Path("order/amount")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getAmount(GetAmountRequest requestData, @Context HttpServletRequest req) {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Conversion");
        GetAmountResponse responseData = new GetAmountResponse();
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
            
            OrdersEntity order = orderService.getOrderByOrderId(requestData.getOrderId());
            responseData.setAmount(orderService.getAmount(requestData.getOrderId()));
            responseData.setResult(StatusCode.Success);
        } catch(CustomException e) {
            responseData.setResult(e.getCode());
            e.printStackTrace();
        } catch(RuntimeException e) {
            responseData.setResult(StatusCode.Failure);
            e.printStackTrace();
        }
        
        return gson.toJson(responseData);
    }
    
    @POST
    @Path("order/src-file")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String setSrcVideo(@FormDataParam("token") String token,
                              @FormDataParam("srcFile") InputStream originSrcFile,
                              @FormDataParam("duration") int duration,
                              @FormDataParam("orderId") long orderId,
                              @Context HttpServletRequest req) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Conversion");
        InputStream srcFile = null;
        StatusResponse responseData = new StatusResponse();
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
            
            //データチェック
            if(originSrcFile == null) {
                logger.info("srcFileがnullです");
                throw new CustomException(StatusCode.NoSrcVideo);
            }
            
            /*if(orderService.getOrderByOrderId(orderId).getSrcStorage() != 0) {
                throw new CustomException(StatusCode.VideoAlreadyExist);
            }*/
            
            java.nio.file.Path srcTmpFile =  StreamConverter.getTmpFile(originSrcFile);
            
            srcFile = StreamConverter.getInputStreamDeleteOnClose(srcTmpFile);
            
            orderService.uploadSrcVideo(srcFile, orderId, duration);
            responseData.setResult(StatusCode.Success);
        } catch(IOException e) {
            responseData.setResult(StatusCode.Failure);
            e.printStackTrace();
        } catch(CustomException e) {
            responseData.setResult(e.getCode());
            e.printStackTrace();
        } catch(RuntimeException e) {
            responseData.setResult(StatusCode.Failure);
            e.printStackTrace();
        } finally {
            if(srcFile != null) {
                srcFile.close();
            }
        }
        
        return gson.toJson(responseData);
    } 
    
    @POST
    @Path("order/dst-file")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String setDstVideo(@FormDataParam("token") String token,
                              @FormDataParam("dstFile") InputStream originDstFile,
                              @FormDataParam("duration") int duration,
                              @FormDataParam("orderId") long orderId,
                              @Context HttpServletRequest req) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Conversion");
        InputStream dstFile = null;
        StatusResponse responseData = new StatusResponse();
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
            
            //データチェック
            if(originDstFile == null) {
                logger.info("dstFileがnullです");
                throw new CustomException(StatusCode.NoDstVideo);
            }
            
            if(orderService.getOrderByOrderId(orderId).getDstStorage() != 0) {
                throw new CustomException(StatusCode.VideoAlreadyExist);
            }
            
            java.nio.file.Path dstTmpFile =  StreamConverter.getTmpFile(originDstFile);
            
            dstFile = StreamConverter.getInputStreamDeleteOnClose(dstTmpFile);
            
            orderService.uploadDstVideo(dstFile, orderId, duration);
            responseData.setResult(StatusCode.Success);
        } catch(IOException e) {
            responseData.setResult(StatusCode.Failure);
            e.printStackTrace();
        } catch(CustomException e) {
            responseData.setResult(e.getCode());
            e.printStackTrace();
        } catch(RuntimeException e) {
            responseData.setResult(StatusCode.Failure);
            e.printStackTrace();
        } finally {
            if(dstFile != null) {
                dstFile.close();
            }
        }
        
        return gson.toJson(responseData);
    } 
    
    @POST
    @Path("order/src-face-image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String setSrcFaceImage(@FormDataParam("token") String token,
                                  @FormDataParam("srcImage") InputStream originSrcImage,
                                  @FormDataParam("orderId") long orderId,
                                  @Context HttpServletRequest req) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Conversion.setSrcFaceImage");
        //InputStream srcImage = null;
        StatusResponse responseData = new StatusResponse();
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
            
            //データチェック
            if(originSrcImage == null) {
                logger.info("srcImageがnullです");
                throw new CustomException(StatusCode.NoSrcImage);
            }
            
            if(orderService.getOrderByOrderId(orderId).getSrcFaceStorage() != 0) {
                throw new CustomException(StatusCode.ImageAlreadyExist);
            }
            
            orderService.uploadSrcImage(originSrcImage, orderId);
            responseData.setResult(StatusCode.Success);
        } catch(IOException e) {
            responseData.setResult(StatusCode.Failure);
            e.printStackTrace();
        } catch(CustomException e) {
            responseData.setResult(e.getCode());
            e.printStackTrace();
        } catch(RuntimeException e) {
            responseData.setResult(StatusCode.Failure);
            e.printStackTrace();
        } finally {
            if(originSrcImage != null) {
                originSrcImage.close();
            }
        }
        
        return gson.toJson(responseData);
    }
    
    
    @POST
    @Path("order/dst-face-image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String setDstFaceImage(@FormDataParam("token") String token,
                                  @FormDataParam("dstImage") InputStream originDstImage,
                                  @FormDataParam("orderId") long orderId,
                                  @Context HttpServletRequest req) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Conversion.setDstFaceImage");
        StatusResponse responseData = new StatusResponse();
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
            
            //データチェック
            if(originDstImage == null) {
                logger.info("dstImageがnullです");
                throw new CustomException(StatusCode.NoDstImage);
            }
            
            if(orderService.getOrderByOrderId(orderId).getDstFaceStorage() != 0) {
                throw new CustomException(StatusCode.ImageAlreadyExist);
            }
            
            orderService.uploadDstImage(originDstImage, orderId);
            responseData.setResult(StatusCode.Success);
        } catch(IOException e) {
            responseData.setResult(StatusCode.Failure);
            e.printStackTrace();
        } catch(CustomException e) {
            responseData.setResult(e.getCode());
            e.printStackTrace();
        } catch(RuntimeException e) {
            responseData.setResult(StatusCode.Failure);
            e.printStackTrace();
        } finally {
            if(originDstImage != null) {
                originDstImage.close();
            }
        }
        
        return gson.toJson(responseData);
    }
    
    @POST
    @Path("delete/src-file") 
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteSrcFile(DeleteVideoRequest requestData, @Context HttpServletRequest req) {
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
            
            orderService.deleteSrcFile(requestData.getOrderId());
            responseData.setResult(StatusCode.Success);
        } catch(CustomException e) {
            responseData.setResult(e.getCode());
            e.printStackTrace();
        } catch(RuntimeException e) {
            responseData.setResult(StatusCode.Failure);
            e.printStackTrace();
        }
        
        return gson.toJson(responseData);
    }  
    
    @POST
    @Path("delete/dst-file") 
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteDstFile(DeleteVideoRequest requestData, @Context HttpServletRequest req) {
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
            
            orderService.deleteDstFile(requestData.getOrderId());
            responseData.setResult(StatusCode.Success);
        } catch(CustomException e) {
            responseData.setResult(e.getCode());
            e.printStackTrace();
        } catch(RuntimeException e) {
            responseData.setResult(StatusCode.Failure);
            e.printStackTrace();
        }
        
        return gson.toJson(responseData);
    } 
    
    @POST
    @Path("order/charge")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String createCharge(CreateChargeRequest requestData, @Context HttpServletRequest req) {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Conversion.createCharge");
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
            
            chargeService.payment(requestData.getOrderId(), requestData.getStripeToken());
            responseData.setResult(StatusCode.Success);
        } catch(CustomException e) {
            responseData.setResult(e.getCode());
            e.printStackTrace();
        } catch(RuntimeException e) {
            responseData.setResult(StatusCode.Failure);
            e.printStackTrace();
        }
        
        return gson.toJson(responseData);
    }
}
