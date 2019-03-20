package com.elefthes.maskswap.controller;

import com.elefthes.maskswap.dto.request.FindOrderByAdminRequest;
import com.elefthes.maskswap.dto.request.GetVideoByAdminRequest;
import com.elefthes.maskswap.dto.response.AdminStatusResponse;
import com.elefthes.maskswap.dto.response.FindOrderByAdminResponse;
import com.elefthes.maskswap.dto.response.OrderConversionResponse;
import com.elefthes.maskswap.dto.response.StatusResponse;
import com.elefthes.maskswap.entity.OrdersEntity;
import com.elefthes.maskswap.exception.AdminCustomException;
import com.elefthes.maskswap.service.AdminService;
import com.elefthes.maskswap.service.OrderService;
import com.elefthes.maskswap.service.OrderVideoService;
import com.elefthes.maskswap.service.VideoStreamingOutput;
import com.elefthes.maskswap.util.AdminStatusCode;
import com.elefthes.maskswap.util.DateFormatter;
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
            
            OrdersEntity order = orderService.getConvertOrder();
            String orderDate = DateFormatter.convertSlash(new Date(order.getOrderDate().getTime()));
            int plan = order.getTypeId();
            
            responseData.setOrderDate(orderDate);
            responseData.setOrderId(order.getOrderId());
            responseData.setPlan(plan);
            responseData.setResult(AdminStatusCode.Success);
            
            orderService.setConverting(order);
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
        StreamingOutput fileStream = new VideoStreamingOutput(requestData.getOrderId(), is);
        
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
        StreamingOutput fileStream = new VideoStreamingOutput(requestData.getOrderId(), is);
        
        return Response.ok(fileStream).build();
    }
    
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String uploadCompletedVideo(@FormDataParam("email") String email,
                                     @FormDataParam("password") String password,
                                     @FormDataParam("completedVideo") InputStream completedVideo) {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Control");
        AdminStatusResponse responseData = new AdminStatusResponse();
        Gson gson = new Gson();
        
        try {
            //ログインチェック
            if(adminService.login(email, password) == false) {
                //失敗時の処理
                logger.info("アドミンログイン失敗");
                throw new AdminCustomException(AdminStatusCode.Failure);
            }
        } catch(AdminCustomException e) {
            responseData.setResult(e.getCode());
        } catch(RuntimeException e) {
            responseData.setResult(AdminStatusCode.Failure);
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
