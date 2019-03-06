package com.elefthes.maskswap.service;

import com.elefthes.maskswap.dto.request.OrderConversionRequest;
import com.elefthes.maskswap.entity.OrdersEntity;
import com.elefthes.maskswap.util.StatusCode;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@ApplicationScoped
public class OrderService {
    @PersistenceContext(unitName = "maskswapGeneral")
    private EntityManager entityManager;  
    
    @Inject 
    UserService userService;
    
    @Inject 
    OrderVideoService orderVideoService;
    
    @Transactional
    public long create(InputStream srcFile, 
                             InputStream dstFile, 
                             long userId) throws IOException, RuntimeException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.service.OrderService");
        
        //byte[] buf = new byte[8];
        //logger.info("１回目抽出" + Integer.toString(requestData.getSrcFile().read(buf, 0, buf.length)));
        OrdersEntity order = new OrdersEntity();
        order.setUser(userService.getUser(userId));
        order.setProgress(0);
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        order.setIsFree(true);
        
        entityManager.persist(order);
        entityManager.flush();
        logger.info("Conversion4");
        long orderId = order.getOrderId();
        
        orderVideoService.uploadVideo(srcFile, dstFile, orderId, userId);
        logger.info("Conversion5");
        return orderId;
    }
}
