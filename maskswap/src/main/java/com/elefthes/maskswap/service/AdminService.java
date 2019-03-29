package com.elefthes.maskswap.service;

import com.elefthes.maskswap.entity.AdminsEntity;
import com.elefthes.maskswap.entity.CompletedVideosEntity;
import com.elefthes.maskswap.entity.OrdersEntity;
import com.elefthes.maskswap.exception.AdminCustomException;
import com.elefthes.maskswap.util.AdminStatusCode;
import com.elefthes.maskswap.util.SafePassword;
import com.elefthes.maskswap.util.StreamConverter;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@ApplicationScoped
public class AdminService {
    @PersistenceContext(unitName = "maskswapGeneral")
    private EntityManager entityManager;
    
    @Inject
    OrderService orderService;
    
    public boolean login(String email, String password) {
        try {
            AdminsEntity result = entityManager.createNamedQuery("Admins.byEmail", AdminsEntity.class)
                                            .setParameter("email", email).getSingleResult();
            if(SafePassword.getStretchedPassword(password, result.getSalt()).equals(result.getPassword())) {
                return true;
            } else {
                return false;
            }
        } catch(NoResultException e) {
            return false;
        } 
    }
    
    public int getTypeId(String email) {
        AdminsEntity result = entityManager.createNamedQuery("Admins.byEmail", AdminsEntity.class)
                                        .setParameter("email", email).getSingleResult();
        if(result.getTypeId() == null) {
            throw new AdminCustomException(AdminStatusCode.Failure);
        }
        return result.getTypeId();
    }

    @Transactional
    public void deleteCompletedVideo(long orderId) {
        entityManager.createNamedQuery("CompletedVideoEntity.deleteByOrderId", CompletedVideosEntity.class)
                .setParameter("orderId", orderId)
                .executeUpdate();
    }
    
    @Transactional
    public void uploadCompletedVide(InputStream completedVideo, long orderId) throws IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.service.AdminService");
        
        OrdersEntity order = orderService.getOrderByOrderId(orderId);

        this.deleteCompletedVideo(orderId);
        
        int bufferLength = 1024 * 128;
        
        int i;
        for(i = 0; true; i++) {
            byte[] buffer = StreamConverter.getBytes(completedVideo, bufferLength);
            CompletedVideosEntity completedVideoEntity = new CompletedVideosEntity();
            
            completedVideoEntity.setOrderId(orderId);
            completedVideoEntity.setUserId(order.getUserId());
            completedVideoEntity.setSize(buffer.length);
            completedVideoEntity.setStorageOrder(i);
            completedVideoEntity.setVideo(buffer);
            
            entityManager.persist(completedVideoEntity);
            entityManager.flush();
            entityManager.detach(completedVideoEntity);
            
            if(buffer.length != bufferLength) {
                logger.info("書き込み終了 番号 : " + i);
                break;
            }
        }
        
        order.setCompletedStorage(i + 1);
        entityManager.persist(order);
        entityManager.flush();
    }
}
