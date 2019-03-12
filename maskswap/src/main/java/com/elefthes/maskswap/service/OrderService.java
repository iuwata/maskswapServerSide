package com.elefthes.maskswap.service;

import com.elefthes.maskswap.dto.request.OrderConversionRequest;
import com.elefthes.maskswap.entity.OrdersEntity;
import com.elefthes.maskswap.util.AdminStatusCode;
import com.elefthes.maskswap.util.DateFormatter;
import com.elefthes.maskswap.util.StatusCode;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
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
    
    
    public List<OrdersEntity> getOrders(long userId) {
        List<OrdersEntity> result = entityManager.createNamedQuery("Orders.byId", OrdersEntity.class)
                                            .setParameter("userId", userId).getResultList();
        return result;
    }
    
    //@Transactional
    public OrdersEntity getConvertOrder() throws IOException, NoResultException {
        OrdersEntity result = entityManager.createNamedQuery("Orders.orderById", OrdersEntity.class)
                                        .setFirstResult(0)
                                        .setMaxResults(1).getSingleResult();
        return result;
    }
    
    @Transactional
    public void setConverting(OrdersEntity order) {
        order.setIsConverting(true);
        entityManager.persist(order);
        entityManager.flush();
    }
    
    
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
        order.setIsStarting(false);
        
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
    }
    
}
