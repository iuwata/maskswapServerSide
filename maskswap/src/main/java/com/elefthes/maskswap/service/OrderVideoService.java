package com.elefthes.maskswap.service;

import com.elefthes.maskswap.entity.OrderDstVideosEntity;
import com.elefthes.maskswap.entity.OrderSrcVideosEntity;
import com.elefthes.maskswap.exception.CustomException;
import com.elefthes.maskswap.util.StatusCode;
import com.elefthes.maskswap.util.StreamConverter;
import com.elefthes.maskswap.util.VirusChecker;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

@ApplicationScoped
public class OrderVideoService {
    @PersistenceContext(unitName = "maskswapGeneral")
    private EntityManager entityManager;  
    
    //@Transactional
    public InputStream getSrcVideo(long orderId, int storage) throws IOException {
        Path tmpPath = Files.createTempFile(Paths.get(System.getProperty("java.io.tmpdir"), "maskswap"), null, null);
        OutputStream os = Files.newOutputStream(tmpPath);
        BufferedOutputStream bos = new BufferedOutputStream(os, 1024 * 128);
        
        for(int i = 0; i < storage; i++) {
            OrderSrcVideosEntity video = entityManager.createNamedQuery("OrderSrcVideos.byId", OrderSrcVideosEntity.class)
                                                .setParameter("orderId", orderId)
                                                .setParameter("storageOrder", i)
                                                .getSingleResult();
            bos.write(video.getVideo());
            bos.flush();
            
            entityManager.detach(video);
        }
        bos.close();
        
        return Files.newInputStream(tmpPath, StandardOpenOption.DELETE_ON_CLOSE);
    }
    
    //@Transactional
    public InputStream getDstVideo(long orderId, int storage) throws IOException {
        Path tmpPath = Files.createTempFile(Paths.get(System.getProperty("java.io.tmpdir"), "maskswap"), null, null);
        OutputStream os = Files.newOutputStream(tmpPath);
        BufferedOutputStream bos = new BufferedOutputStream(os, 1024 * 128);
        
        for(int i = 0; i < storage; i++) {
            OrderDstVideosEntity video = entityManager.createNamedQuery("OrderDstVideos.byId", OrderDstVideosEntity.class)
                                                .setParameter("orderId", orderId)
                                                .setParameter("storageOrder", i)
                                                .getSingleResult();
            bos.write(video.getVideo());
            bos.flush();
            
            entityManager.detach(video);
        }
        bos.close();
        
        return Files.newInputStream(tmpPath, StandardOpenOption.DELETE_ON_CLOSE);
    }
    
    @Transactional
    public int uploadSrcVideo(InputStream srcFile, long orderId, long userId) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.service.OrderVideoService");
        
        int maxLength = 1024 * 128; //128kb
        
        //srcファイルをアップロード
        int i;
        for(i = 0; true; i++) {
            byte[] buffer = StreamConverter.getBytes(srcFile, maxLength);
            if(VirusChecker.isVirus(buffer) == true) {
                logger.info("ウイルスが検知されました");
                throw new CustomException(StatusCode.VirusFound);
            }
            logger.info("src動画サイズ" + i + " : " + buffer.length);
            OrderSrcVideosEntity orderSrcVideo = new OrderSrcVideosEntity();
            orderSrcVideo.setOrderId(orderId);
            orderSrcVideo.setUserId(userId);
            orderSrcVideo.setSize(buffer.length);
            orderSrcVideo.setStorageOrder(i);
            orderSrcVideo.setVideo(buffer);
            
            entityManager.persist(orderSrcVideo);
            entityManager.flush();
            //entityManager.clear();
            entityManager.detach(orderSrcVideo);
            
            if(buffer.length != maxLength) {
                logger.info("src動画書き込み終了 番号:" + i);
                break;
            }
        }
        return i + 1;
    }
    
    @Transactional
    public int uploadDstVideo(InputStream dstFile, long orderId, long userId) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.service.OrderVideoService");
        
        int maxLength = 1024 * 128; //128kb
        
        //dstファイルをアップロード
        int i;
        for( i = 0; true; i++) {
            byte[] buffer = StreamConverter.getBytes(dstFile, maxLength);
            if(VirusChecker.isVirus(buffer) == true) {
                logger.info("ウイルスが検知されました");
                throw new CustomException(StatusCode.VirusFound);
            }
            logger.info("dst動画サイズ" + i + " : " + buffer.length);
            OrderDstVideosEntity orderDstVideo = new OrderDstVideosEntity();
            orderDstVideo.setOrderId(orderId);
            orderDstVideo.setUserId(userId);
            orderDstVideo.setSize(buffer.length);
            orderDstVideo.setStorageOrder(i);
            orderDstVideo.setVideo(buffer);
            
            entityManager.persist(orderDstVideo);
            entityManager.flush();
            //entityManager.clear();
            entityManager.detach(orderDstVideo);
            
            if(buffer.length != maxLength) {
                logger.info("dst動画書き込み終了 番号:" + i);
                break;
            }
        }
        return i + 1;
    }
    
    /*@Transactional
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
            //entityManager.clear();
            entityManager.detach(orderSrcVideo);
            
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
            //entityManager.clear();
            entityManager.detach(orderDstVideo);
            
            if(buffer.length != maxLength) {
                logger.info("dst動画書き込み終了 番号:" + i);
                break;
            }
        }
    }*/
}
