package com.elefthes.maskswap.service;

import com.elefthes.maskswap.dto.request.OrderConversionRequest;
import com.elefthes.maskswap.entity.ChargesEntity;
import com.elefthes.maskswap.entity.OrderDstVideosEntity;
import com.elefthes.maskswap.entity.OrderSrcVideosEntity;
import com.elefthes.maskswap.entity.OrdersEntity;
import com.elefthes.maskswap.exception.CustomException;
import com.elefthes.maskswap.util.AdminStatusCode;
import com.elefthes.maskswap.util.DateFormatter;
import com.elefthes.maskswap.util.StatusCode;
import com.stripe.Stripe;
import com.stripe.exception.ApiConnectionException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.RateLimitException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Refund;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

@ApplicationScoped
public class OrderService {
    @PersistenceContext(unitName = "maskswapGeneral")
    private EntityManager entityManager; 
     
    @Inject 
    UserService userService;
    
    @Inject 
    OrderVideoService orderVideoService;
    
    @Inject
    FaceImageService faceImageService;
    
    
    
    
    public List<OrdersEntity> getOrders(long userId) {
        List<OrdersEntity> result = entityManager.createNamedQuery("Orders.byUserId", OrdersEntity.class)
                                            .setParameter("userId", userId).getResultList();
        return result;
    }
    
    public List<OrdersEntity> getOrdersEntityEndDateNull(long userId) {
        List<OrdersEntity> result = entityManager.createNamedQuery("Orders.endDateNull", OrdersEntity.class)
                                            .setParameter("userId", userId).getResultList();
        return result;
    }
    
    //@Transactional
    public OrdersEntity getConvertOrder(int typeId) throws IOException, NoResultException {
        OrdersEntity result = entityManager.createNamedQuery("Orders.orderById", OrdersEntity.class)
                                        .setParameter("typeId", typeId)
                                        .setFirstResult(0)
                                        .setMaxResults(1).getSingleResult();
        return result;
    }
    
    public OrdersEntity getOrderByOrderId(long orderId) {
        OrdersEntity result = entityManager.createNamedQuery("Orders.byOrderId", OrdersEntity.class)
                                        .setParameter("orderId", orderId)
                                        .getSingleResult();
        return result;
    }
    
    @Transactional
    public void setConverting(OrdersEntity order) {
        order.setIsConverting(true);
        entityManager.persist(order);
        entityManager.flush();
    }
    
    @Transactional
    public void updateProgress(long orderId, int progress) {
        OrdersEntity order = this.getOrderByOrderId(orderId);
        order.setProgress(progress);
        entityManager.persist(order);
        entityManager.flush();
    }
    
    @Transactional
    public void setPlan(long orderId, int plan) {
        OrdersEntity order = this.getOrderByOrderId(orderId);
        order.setTypeId(plan);
        entityManager.persist(order);
        entityManager.flush();
    }
    
    //@Transactional 
    public int getAmount(long orderId) {
        OrdersEntity order = this.getOrderByOrderId(orderId);
        
        if(order.getTypeId() == null) {
            throw new CustomException(StatusCode.NoPlan);
        }
        int duration;
        if(order.getDstDuration() == null) {
            throw new CustomException(StatusCode.NoDstVideo);
        } else {
            duration = order.getDstDuration();
        }
        int amount = 0;
        switch(order.getTypeId()) {
            case 1 : //スタンダード
                //追加金
                if(duration > 15) {
                    amount += ((duration - 15) / 10 + 1) * 100;
                }
                break;
            case 2 : //プレミアム
                //基本価格
                amount += 300;
                //追加金
                if(duration > 15) {
                    amount += ((duration - 15) / 10 + 1) * 150;
                }
                break;
        }
        
        return amount;
    }
    
    @Transactional
    public long create(long userId) {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.service.OrderService");
        
        if(this.getOrdersEntityEndDateNull(userId).size() != 0) {
            throw new CustomException(StatusCode.OrderAlreadyExist);
        }
        
        OrdersEntity order = new OrdersEntity();
        order.setUserId(userId);
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        order.setIsConverting(false);
        
        entityManager.persist(order);
        entityManager.flush();
        
        long orderId = order.getOrderId();
        return orderId;
    }
    
    @Transactional
    public void uploadSrcVideo(InputStream srcFile, long orderId, int duration) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.service.OrderService");
        
        OrdersEntity order = this.getOrderByOrderId(orderId);
        
        /*if(order.getSrcStorage() != 0) {
            throw new CustomException(StatusCode.VideoAlreadyExist);
        }*/
        
        int srcStorage = orderVideoService.uploadSrcVideo(srcFile, orderId, order.getUserId());
        
        order.setSrcStorage(srcStorage);
        order.setSrcDuration(duration);
        entityManager.persist(order);
        entityManager.flush();
    }
    
    @Transactional
    public void uploadSrcImage(InputStream srcImage, long orderId) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.service.OrderService");
        
        OrdersEntity order = this.getOrderByOrderId(orderId);
        
        int srcFaceStorage = faceImageService.uploadSrcFaceImage(srcImage, orderId, order.getUserId());
        
        order.setSrcFaceStorage(srcFaceStorage);
        entityManager.persist(order);
        entityManager.flush();
    }
    
    @Transactional
    public void uploadDstImage(InputStream dstImage, long orderId) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.service.OrderService");
        
        OrdersEntity order = this.getOrderByOrderId(orderId);
        
        int dstFaceStorage = faceImageService.uploadDstFaceImage(dstImage, orderId, order.getUserId());
        
        order.setDstFaceStorage(dstFaceStorage);
        entityManager.persist(order);
        entityManager.flush();
    }
    
    @Transactional
    public void uploadDstVideo(InputStream dstFile, long orderId, int duration) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.service.OrderService");
        
        OrdersEntity order = this.getOrderByOrderId(orderId);
        
        /*if(order.getDstStorage() != 0) {
            throw new CustomException(StatusCode.VideoAlreadyExist);
        }*/
        
        int dstStorage = orderVideoService.uploadDstVideo(dstFile, orderId, order.getUserId());
        
        order.setDstStorage(dstStorage);
        order.setDstDuration(duration);
        entityManager.persist(order);
        entityManager.flush();
    }
    
    @Transactional 
    public void deleteSrcFile(long orderId) {
        entityManager.createNamedQuery("OrderSrcVideos.deleteByOrderId", OrderSrcVideosEntity.class)
                        .setParameter("orderId", orderId)
                        .executeUpdate();
        OrdersEntity order = this.getOrderByOrderId(orderId);
        order.setSrcStorage(0);
        entityManager.persist(order);
        entityManager.flush();
    }
    
    @Transactional 
    public void deleteDstFile(long orderId) {
        entityManager.createNamedQuery("OrderDstVideos.deleteByOrderId", OrderDstVideosEntity.class)
                        .setParameter("orderId", orderId)
                        .executeUpdate();
        OrdersEntity order = this.getOrderByOrderId(orderId);
        order.setDstStorage(0);
        entityManager.persist(order);
        entityManager.flush();
    }
            
    /*
    
    @Transactional
    public long create(InputStream srcFile, 
                             InputStream dstFile, 
                             long userId) throws IOException, RuntimeException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.service.OrderService");
        
        OrdersEntity order = new OrdersEntity();
        //order.setUser(userService.getUser(userId));
        order.setUserId(userId);
        order.setProgress(0);
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        order.setIsConverting(false);
        
        //テスト用
        order.setPaymentDate(new Timestamp(System.currentTimeMillis()));
        //order.setIsStarting(false);
        
        //テスト用
        order.setTypeId(1);
        
        entityManager.persist(order);
        entityManager.flush();
        logger.info("Conversion4");
        long orderId = order.getOrderId();
        
        //orderVideoService.uploadVideo(srcFile, dstFile, orderId, userId);
        int srcStorage = orderVideoService.uploadSrcVideo(srcFile, orderId, userId);
        int dstStorage = orderVideoService.uploadDstVideo(dstFile, orderId, userId);
        order.setSrcStorage(srcStorage);
        order.setDstStorage(dstStorage);
        entityManager.persist(order);
        entityManager.flush();
        logger.info("Conversion5");
        return orderId;
    }*/
    
}
