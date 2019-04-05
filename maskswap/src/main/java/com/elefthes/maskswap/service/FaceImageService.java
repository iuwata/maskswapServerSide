package com.elefthes.maskswap.service;

import com.elefthes.maskswap.entity.DstFaceImagesEntity;
import com.elefthes.maskswap.entity.SrcFaceImagesEntity;
import com.elefthes.maskswap.util.StreamConverter;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@ApplicationScoped
public class FaceImageService {
    
    private static final int MAX_LENGTH = 1024 * 128; //128kb
    
    @PersistenceContext(unitName = "maskswapGeneral")
    private EntityManager entityManager;  
    
    public InputStream getSrcFaceImage(long orderId, int storage) throws IOException {
        Path tmpPath = Files.createTempFile(Paths.get(System.getProperty("java.io.tmpdir"), "maskswap"), null, null);
        OutputStream os = Files.newOutputStream(tmpPath);
        BufferedOutputStream bos = new BufferedOutputStream(os, 1024 * 128);
        
        for(int i = 0; i < storage; i++) {
            SrcFaceImagesEntity image = entityManager.createNamedQuery("SrcFaceImages.byId", SrcFaceImagesEntity.class)
                                                .setParameter("orderId", orderId)
                                                .setParameter("storage", i)
                                                .getSingleResult();
            bos.write(image.getImage());
            bos.flush();
            
            entityManager.detach(image);
        }
        bos.close();
        
        return Files.newInputStream(tmpPath, StandardOpenOption.DELETE_ON_CLOSE);
    }
    
    public InputStream getDstFaceImage(long orderId, int storage) throws IOException {
        Path tmpPath = Files.createTempFile(Paths.get(System.getProperty("java.io.tmpdir"), "maskswap"), null, null);
        OutputStream os = Files.newOutputStream(tmpPath);
        BufferedOutputStream bos = new BufferedOutputStream(os, 1024 * 128);
        
        for(int i = 0; i < storage; i++) {
            DstFaceImagesEntity image = entityManager.createNamedQuery("DstFaceImages.byId", DstFaceImagesEntity.class)
                                                .setParameter("orderId", orderId)
                                                .setParameter("storage", i)
                                                .getSingleResult();
            bos.write(image.getImage());
            bos.flush();
            
            entityManager.detach(image);
        }
        bos.close();
        
        return Files.newInputStream(tmpPath, StandardOpenOption.DELETE_ON_CLOSE);
    }
    
    @Transactional
    public int uploadSrcFaceImage(InputStream srcImage, long orderId, long userId) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.service.FaceImageService");
        
        int i;
        for(i = 0; true; i++) {
            byte[] buffer = StreamConverter.getBytes(srcImage, MAX_LENGTH);
            SrcFaceImagesEntity srcFaceImage = new SrcFaceImagesEntity();
            srcFaceImage.setOrderId(orderId);
            srcFaceImage.setUserId(userId);
            srcFaceImage.setSize(buffer.length);
            srcFaceImage.setStorageOrder(i);
            srcFaceImage.setImage(buffer);
            
            entityManager.persist(srcFaceImage);
            entityManager.flush();
            entityManager.detach(srcFaceImage);
            
            if(buffer.length != MAX_LENGTH) {
                logger.info("srcImage書き込み終了 番号:" + i);
                break;
            }
        }
        return i + 1;
    }
    
    @Transactional
    public int uploadDstFaceImage(InputStream dstImage, long orderId, long userId) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.service.FaceImageService");
        
        int i;
        for(i = 0; true; i++) {
            byte[] buffer = StreamConverter.getBytes(dstImage, MAX_LENGTH);
            DstFaceImagesEntity dstFaceImage = new DstFaceImagesEntity();
            dstFaceImage.setOrderId(orderId);
            dstFaceImage.setUserId(userId);
            dstFaceImage.setSize(buffer.length);
            dstFaceImage.setStorageOrder(i);
            dstFaceImage.setImage(buffer);
            
            entityManager.persist(dstFaceImage);
            entityManager.flush();
            entityManager.detach(dstFaceImage);
            
            if(buffer.length != MAX_LENGTH) {
                logger.info("srcImage書き込み終了 番号:" + i);
                break;
            }
        }
        return i + 1;
    }
}
