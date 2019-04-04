package com.elefthes.maskswap.service;

import com.elefthes.maskswap.entity.DstFaceImagesEntity;
import com.elefthes.maskswap.entity.SrcFaceImagesEntity;
import com.elefthes.maskswap.util.StreamConverter;
import java.io.IOException;
import java.io.InputStream;
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
