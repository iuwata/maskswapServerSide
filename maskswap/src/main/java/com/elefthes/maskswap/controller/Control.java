package com.elefthes.maskswap.controller;

import com.elefthes.maskswap.dto.request.FindOrderByAdminRequest;
import com.elefthes.maskswap.dto.request.GetImageByAdminRequest;
import com.elefthes.maskswap.dto.request.GetVideoByAdminRequest;
import com.elefthes.maskswap.dto.request.SetConvertingByAdminRequest;
import com.elefthes.maskswap.dto.request.UpdateProgressByAdminRequest;
import com.elefthes.maskswap.dto.response.AdminStatusResponse;
import com.elefthes.maskswap.dto.response.FindOrderByAdminResponse;
import com.elefthes.maskswap.dto.response.OrderConversionResponse;
import com.elefthes.maskswap.dto.response.StatusResponse;
import com.elefthes.maskswap.entity.OrdersEntity;
import com.elefthes.maskswap.exception.AdminCustomException;
import com.elefthes.maskswap.service.AdminService;
import com.elefthes.maskswap.service.FaceImageService;
import com.elefthes.maskswap.service.ImageStreamingOutput;
import com.elefthes.maskswap.service.OrderService;
import com.elefthes.maskswap.service.OrderVideoService;
import com.elefthes.maskswap.service.VideoStreamingOutput;
import com.elefthes.maskswap.util.AdminStatusCode;
import com.elefthes.maskswap.util.DateFormatter;
import com.elefthes.maskswap.util.StreamConverter;
import com.elefthes.maskswap.util.VirusChecker;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

@ApplicationScoped
@Path("h82sdyb9g3yxbfk2c32khbne")
public class Control {
    @Inject
    AdminService adminService;
    
    @Inject
    OrderService orderService;
    
    @Inject 
    OrderVideoService orderVideoService;
    
    @Inject
    FaceImageService faceImageService;
    
    @POST
    @Path("m2swetg3j4i8sh3vy794g6z2")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String findOrder(FindOrderByAdminRequest requestData) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Control");
        
        FindOrderByAdminResponse responseData = new FindOrderByAdminResponse();
        Gson gson = new Gson();
        
        try {
            //ログインチェック
            if(adminService.login(requestData.getEmail(), requestData.getPassword()) == false) {
                //失敗時の処理
                logger.info("アドミンログイン失敗");
                throw new AdminCustomException(AdminStatusCode.Failure);
            }
            
            OrdersEntity order = orderService.getConvertOrder(adminService.getTypeId(requestData.getEmail()));
            logger.info("補足4");
            String orderDate = DateFormatter.convertSlash(new Date(order.getOrderDate().getTime()));
            int plan = order.getTypeId();
            
            responseData.setOrderDate(orderDate);
            responseData.setOrderId(order.getOrderId());
            responseData.setPlan(plan);
            responseData.setResult(AdminStatusCode.Success);
            
            //orderService.setConverting(order.getOrderId());
        } catch(NoResultException e) {
            responseData.setResult(AdminStatusCode.NoOrder);
        } catch(AdminCustomException e) {
            responseData.setResult(e.getCode());
        } catch(RuntimeException e) {
            responseData.setResult(AdminStatusCode.Failure);
        }
        
        return gson.toJson(responseData);
    }
    
    @POST
    @Path("qatp8yy3tgfbsedvzrpnuqms")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String setConverting(SetConvertingByAdminRequest requestData) {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Control.setConverting");
        logger.info("補足１");
        AdminStatusResponse responseData = new AdminStatusResponse();
        Gson gson = new Gson();
        
        try {
            //ログインチェック
            if(adminService.login(requestData.getEmail(), requestData.getPassword()) == false) {
                //失敗時の処理
                //logger.info("アドミンログイン失敗");
                throw new AdminCustomException(AdminStatusCode.Failure);
            }
            if(requestData.getOrderId() == 0) {
                throw new AdminCustomException(AdminStatusCode.Failure);
            }
            
            orderService.setConverting(requestData.getOrderId());
            responseData.setResult(AdminStatusCode.Success);
        } catch(RuntimeException e) {
            responseData.setResult(AdminStatusCode.Failure);
        }
        
        return gson.toJson(responseData);
        
    }
    
    @POST
    @Path("y6akpkwagwwrrqesa99yaysz")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("video/mp4")
    public Response getDstFile(GetVideoByAdminRequest requestData) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Control");
        //ログインチェック
        if(adminService.login(requestData.getEmail(), requestData.getPassword()) == false) {
            //失敗時の処理
            logger.info("アドミンログイン失敗");
            throw new AdminCustomException(AdminStatusCode.Failure);
        }
        
        long orderId = requestData.getOrderId();
        int storage = orderService.getOrderByOrderId(orderId).getDstStorage();
        InputStream is = orderVideoService.getDstVideo(orderId, storage);
        StreamingOutput fileStream = new VideoStreamingOutput(orderId, is);
        
        return Response.ok(fileStream).build();
    }
    
    @POST
    @Path("fmgymzbvyawd9cznw6hyh6em")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("video/mp4")
    public Response getSrcFile(GetVideoByAdminRequest requestData) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Control");
        //ログインチェック
        if(adminService.login(requestData.getEmail(), requestData.getPassword()) == false) {
            //失敗時の処理
            logger.info("アドミンログイン失敗");
            throw new AdminCustomException(AdminStatusCode.Failure);
        }
        
        long orderId = requestData.getOrderId();
        int storage = orderService.getOrderByOrderId(orderId).getSrcStorage();
        InputStream is = orderVideoService.getSrcVideo(orderId, storage);
        StreamingOutput fileStream = new VideoStreamingOutput(orderId, is);
        
        return Response.ok(fileStream).build();
    }
    
    @POST
    @Path("6uqipameruhvwngbyte8nxjs")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("image/png")
    public Response getSrcImage(GetImageByAdminRequest requestData) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Control");
        //ログインチェック
        if(adminService.login(requestData.getEmail(), requestData.getPassword()) == false) {
            //失敗時の処理
            logger.info("アドミンログイン失敗");
            throw new AdminCustomException(AdminStatusCode.Failure);
        }
        
        long orderId = requestData.getOrderId();
        int storage = orderService.getOrderByOrderId(orderId).getSrcFaceStorage();
        if(storage != 0) {
            InputStream is = faceImageService.getSrcFaceImage(orderId, storage);
            StreamingOutput fileStream = new ImageStreamingOutput(orderId, is);
            return Response.ok(fileStream).build();
        } else {
            throw new AdminCustomException(AdminStatusCode.Failure);
        }
    }
    
    @POST
    @Path("pcht2vx43h7ufftsnz39gmyw")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("image/png")
    public Response getDstImage(GetImageByAdminRequest requestData) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Control");
        //ログインチェック
        if(adminService.login(requestData.getEmail(), requestData.getPassword()) == false) {
            //失敗時の処理
            logger.info("アドミンログイン失敗");
            throw new AdminCustomException(AdminStatusCode.Failure);
        }
        
        long orderId = requestData.getOrderId();
        int storage = orderService.getOrderByOrderId(orderId).getDstFaceStorage();
        if(storage != 0) {
            InputStream is = faceImageService.getDstFaceImage(orderId, storage);
            StreamingOutput fileStream = new ImageStreamingOutput(orderId, is);
            return Response.ok(fileStream).build();
        } else {
            throw new AdminCustomException(AdminStatusCode.Failure);
        }
    }
    
    @POST
    @Path("9s9yxjfb4acky927cd2apcqk")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateProgress(UpdateProgressByAdminRequest requestData) {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Control");
        AdminStatusResponse responseData = new AdminStatusResponse();
        Gson gson = new Gson();
        
        try {
            //ログインチェック
            if(adminService.login(requestData.getEmail(), requestData.getPassword()) == false) {
                //失敗時の処理
                logger.info("アドミンログイン失敗");
                throw new AdminCustomException(AdminStatusCode.Failure);
            }
            
            orderService.updateProgress(requestData.getOrderId(), requestData.getProgress());
            responseData.setResult(AdminStatusCode.Success);
        } catch(AdminCustomException e) {
            responseData.setResult(e.getCode());
        } catch(RuntimeException e) {
            responseData.setResult(AdminStatusCode.Failure);
        }
        
        return gson.toJson(responseData);
    }
    
    @POST
    @Path("vbjszhwkeusupatwj97gkuwf")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String uploadCompletedVideo(@FormDataParam("email") String email,
                                     @FormDataParam("password") String password,
                                     @FormDataParam("orderId") long orderId,
                                     @FormDataParam("completedVideo") InputStream originCompletedVideo) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Control");
        AdminStatusResponse responseData = new AdminStatusResponse();
        Gson gson = new Gson();
        
        InputStream completedVideo = null;
        
        try {
            //ログインチェック
            if(adminService.login(email, password) == false) {
                //失敗時の処理
                logger.info("アドミンログイン失敗");
                throw new AdminCustomException(AdminStatusCode.Failure);
            }
            
            //nullチェック
            if(originCompletedVideo == null) {
                logger.info("完成動画がアップロードされていない");
                throw new AdminCustomException(AdminStatusCode.NotUploadCompletedVideo);
            }
            
            java.nio.file.Path tmpFile = StreamConverter.getTmpFile(originCompletedVideo);
            
            //ウイルスチェック
            if(VirusChecker.isVirus(tmpFile)) {
                //ウイルス検知
                throw new AdminCustomException(AdminStatusCode.VirusFound);
            }
            
            if(orderId == 0) {
                logger.info("orderIdが存在しない");
                throw new AdminCustomException(AdminStatusCode.Failure);
            }
            
            completedVideo = StreamConverter.getInputStreamDeleteOnClose(tmpFile);
            adminService.uploadCompletedVideo(completedVideo, orderId);
            responseData.setResult(AdminStatusCode.Success);
        } catch(AdminCustomException e) {
            responseData.setResult(e.getCode());
        } catch(RuntimeException e) {
            e.printStackTrace();
            responseData.setResult(AdminStatusCode.Failure);
        } finally {
            if(completedVideo != null) {
                completedVideo.close();
            }
        }
        
        return gson.toJson(responseData);
    }
    
    
/*    
    @POST
    @Path("m2swetg3j4i8sh3vy794g6z2")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("multipart/mixed")
    //@Transactional
    public MultiPart findOrderByAdmin(FindOrderByAdminRequest requestData) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Control");
        logger.info("アクセス検知");
        
        int result;
        FormDataMultiPart multipart = new FormDataMultiPart();
        InputStream sis = null;
        InputStream dis = null;
        
        try {
            //ログインチェック
            if(adminService.login(requestData.getEmail(), requestData.getPassword()) == false) {
                //失敗時の処理
                logger.info("アドミンログイン失敗");
                throw new AdminCustomException(AdminStatusCode.Failure);
            }
            
            OrdersEntity order = orderService.getConvertOrder();
            String orderDate = DateFormatter.convertSlash(new Date(order.getOrderDate().getTime()));
            int plan = order.getTypeId();
            logger.info("findOrderByAdmin.ポイント１");
            
            //レスポンスデータの構成
            sis = orderVideoService.getSrcVideo(order.getOrderId(), order.getSrcStorage());
            dis = orderVideoService.getDstVideo(order.getOrderId(), order.getDstStorage());
            StreamDataBodyPart srcFile = new StreamDataBodyPart("srcFile", sis);
            StreamDataBodyPart dstFile = new StreamDataBodyPart("dstFile", dis);
            logger.info("findOrderByAdmin.ポイント2");
            
            //FormDataMultiPart multipart = new FormDataMultiPart();
            multipart.field("result", String.valueOf(AdminStatusCode.Success.getId()))
                    .field("orderDate", orderDate)
                    .field("plan", String.valueOf(plan))
                    .bodyPart(srcFile)
                    .bodyPart(dstFile);
            
            orderService.setConverting(order);
        } catch(NoResultException e) {
            logger.info("動画取得失敗1");
            //AdminStatusCode status = AdminStatusCode.NoOrder;
            multipart.field("result", String.valueOf(AdminStatusCode.NoOrder.getId()));
        } catch(AdminCustomException e) {
            logger.info("動画取得失敗2");
            multipart.field("result", String.valueOf(e.getCode().getId()));
        } catch(RuntimeException e) {
            logger.info("動画取得失敗3");
            multipart.field("result", String.valueOf(AdminStatusCode.Failure.getId()));
            e.printStackTrace();
        } finally {
            if(sis != null) {
                sis.close();
            }
            if(dis != null) {
                dis.close();
            }
        }
        
        return multipart;
    }
*/
}
