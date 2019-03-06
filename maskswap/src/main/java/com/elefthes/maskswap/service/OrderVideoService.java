package com.elefthes.maskswap.service;

import com.elefthes.maskswap.entity.OrderDstVideosEntity;
import com.elefthes.maskswap.entity.OrderSrcVideosEntity;
import com.elefthes.maskswap.util.StatusCode;
import com.elefthes.maskswap.util.StreamConvert;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    
    @Transactional
    public void uploadVideo(InputStream srcFile, 
                                  InputStream dstFile, 
                                  long orderId, 
                                  long userId) throws IOException, RuntimeException{
        Logger logger = Logger.getLogger("com.elefthes.maskswap.service.OrderVideoService");
        
        //int size;
        int maxLength = 1024 * 128;
        
        //srcファイルをアップロード
        for(int i = 0; true; i++) {
            byte[] buffer = StreamConvert.getBytes(srcFile, maxLength);
            logger.info("src動画サイズ" + i + " : " + buffer.length);
            OrderSrcVideosEntity orderSrcVideo = new OrderSrcVideosEntity();
            orderSrcVideo.setOrderId(orderId);
            orderSrcVideo.setUserId(userId);
            orderSrcVideo.setSize(buffer.length);
            orderSrcVideo.setStorageOrder(i);
            orderSrcVideo.setVideo(buffer);
            
            entityManager.persist(orderSrcVideo);
            entityManager.flush();
            entityManager.clear();
            
            if(buffer.length != maxLength) {
                logger.info("src動画書き込み終了 番号:" + i);
                break;
            }
        }
        
        //dstファイルをアップロード
        for(int i = 0; true; i++) {
            byte[] buffer = StreamConvert.getBytes(dstFile, maxLength);
            logger.info("dst動画サイズ" + i + " : " + buffer.length);
            OrderDstVideosEntity orderDstVideo = new OrderDstVideosEntity();
            orderDstVideo.setOrderId(orderId);
            orderDstVideo.setUserId(userId);
            orderDstVideo.setSize(buffer.length);
            orderDstVideo.setStorageOrder(i);
            orderDstVideo.setVideo(buffer);
            
            entityManager.persist(orderDstVideo);
            entityManager.flush();
            entityManager.clear();
            
            if(buffer.length != maxLength) {
                logger.info("dst動画書き込み終了 番号:" + i);
                break;
            }
        }
    }
}
