package com.elefthes.maskswap.service;

import com.elefthes.maskswap.entity.OrderDstVideosEntity;
import com.elefthes.maskswap.entity.OrderSrcVideosEntity;
import com.elefthes.maskswap.util.StatusCode;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@ApplicationScoped
public class OrderVideoService {
    @PersistenceContext(unitName = "maskswapGeneral")
    private EntityManager entityManager;  
    
    @Inject 
    UserService userService;
    
    @Transactional
    public void uploadVideo(ByteArrayInputStream srcFile, 
                                  ByteArrayInputStream dstFile, 
                                  long orderId, 
                                  long userId) throws IOException, RuntimeException{
        Logger logger = Logger.getLogger("com.elefthes.maskswap.service.OrderVideoService");
        
        //srcファイルをアップロード
        BufferedInputStream srcInputStream = new BufferedInputStream(srcFile);
        int size;
        int maxByte = 1024 * 128;
        for(int i = 0; true; i++) {
            byte[] buffer = new byte[maxByte]; //128kb
            size = srcInputStream.read(buffer, 0, buffer.length);
            if(size == -1) {
                srcInputStream.close();
                break;
            } else {
                OrderSrcVideosEntity orderSrcVideo = new OrderSrcVideosEntity();
                orderSrcVideo.setOrderId(orderId);
                orderSrcVideo.setUserId(userId);
                orderSrcVideo.setSize(size);
                orderSrcVideo.setStorageOrder(i);
                orderSrcVideo.setVideo(buffer);
                
                entityManager.persist(orderSrcVideo);
                entityManager.flush();
                entityManager.clear();
            }
        }
        
        //dstファイルをアップロード
        BufferedInputStream dstInputStream = new BufferedInputStream(dstFile);
        for(int i = 0; true; i++) {
            byte[] buffer = new byte[maxByte];
            size = dstInputStream.read(buffer, 0, buffer.length);
            if(size == -1) {
                dstInputStream.close();
                break;
            } else {
                OrderDstVideosEntity orderDstVideo = new OrderDstVideosEntity();
                orderDstVideo.setOrderId(orderId);
                orderDstVideo.setUserId(userId);
                orderDstVideo.setSize(size);
                orderDstVideo.setStorageOrder(i);
                orderDstVideo.setVideo(buffer);
                
                entityManager.persist(orderDstVideo);
                entityManager.flush();
                entityManager.clear();
            }
        }
    }
}
