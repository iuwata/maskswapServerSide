package com.elefthes.maskswap.service;

import com.elefthes.maskswap.entity.UsersEntity;
import com.elefthes.maskswap.exception.CustomException;
import com.elefthes.maskswap.util.EmailAddressChecker;
import com.elefthes.maskswap.util.PasswordChecker;
import com.elefthes.maskswap.util.SafePassword;
import com.elefthes.maskswap.util.StatusCode;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;

@ApplicationScoped
public class UserService {
    @PersistenceContext(unitName = "maskswapGeneral")
    private EntityManager entityManager;
    
    public static final int VERSION = 1;
    
    public UsersEntity getUser(String email) {
        UsersEntity result = entityManager.createNamedQuery("Users.byEmail", UsersEntity.class)
                                    .setParameter("email", email).getSingleResult();
        return result;
    }
    
    @Transactional
    public UsersEntity getUser(long userId) { 
        UsersEntity result = entityManager.createNamedQuery("Users.byId", UsersEntity.class)
                                    .setParameter("userId", userId).getSingleResult();
        return result;
    }
    
    public boolean checkSaltOverlap(String salt) {
        List<UsersEntity> result = entityManager.createNamedQuery("Users.bySalt", UsersEntity.class)
                                            .setParameter("salt", salt).getResultList();
        return result.size() == 0;
    }
    
    @Transactional
    public void authenticate(long userId) {
        UsersEntity user = this.getUser(userId);
        user.setAuthentication(true);
        entityManager.persist(user);
        entityManager.flush();
    }
    
    
    
    //@Transactional
    public void authenticateEmail(long userId, String authenticationCode) throws CustomException{
        Logger logger = Logger.getLogger("com.elefthes.maskswap.service.UserService.authenticateEmail");
        UsersEntity user = this.getUser(userId);
        
        //認証済みかどうかを確認
        if(user.getAuthentication()) {
            throw new CustomException(StatusCode.EmailAlreadyAuthenticated);
        }
        
        //認証コードを確認
        logger.info(user.getAuthenticationCode());
        logger.info("送信された認証コード" + authenticationCode);
        if(!user.getAuthenticationCode().equals(authenticationCode)) {
            //this.authenticate(user);
            throw new CustomException(StatusCode.IncorrectAuthenticationCode);
        }
    }
    
    public StatusCode EmailAvailable(String email) {
        if(EmailAddressChecker.isEmailAddress(email)) {   //emailアドレスであるか判定
            List<UsersEntity> result = entityManager.createNamedQuery("Users.byEmail", UsersEntity.class)
                                                .setParameter("email", email).getResultList();
            if(result.size() == 0) {    //すでに存在しているか判定
                return StatusCode.Success;
            } else {
                return StatusCode.EmailAlreadyExist;
            }
        } else {
            return StatusCode.IncompleteEmail;
        }
    }
    
    public boolean login(String email, String password) {
        try {
            UsersEntity result = entityManager.createNamedQuery("Users.byEmail", UsersEntity.class)
                                                    .setParameter("email", email).getSingleResult();
            if(SafePassword.getStretchedPassword(password, result.getSalt()).equals(result.getPassword())) {
                if(result.getAuthentication() == true) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (NoResultException e) {
            return false;
        }
    }
    
    @Transactional
    public long create(String email, String password) throws NoSuchAlgorithmException, CustomException{
        /*//パスワードが使用可能か判定
        if(!PasswordChecker.isPasswordAvailable(password)) { //使用不可能の場合
            throw new CustomException(StatusCode.IncompletePassword);
        }
        //emailが使用可能か判定
        switch(EmailAvailable(email)) {
            case EmailAlreadyExist:
                //emailが既に存在
                throw new CustomException(StatusCode.EmailAlreadyExist);
            case IncompleteEmail: 
                //メールアドレスではない
                throw new CustomException(StatusCode.IncompleteEmail);
        }*/
        
        //パスワードの暗号化
        String salt = SafePassword.getSalt();
        //saltの重複判定
        while(!checkSaltOverlap(salt)) {
            salt = SafePassword.getSalt();
        }
        String securePassword = SafePassword.getStretchedPassword(password, salt);
        
        //現在時刻を取得
        Timestamp startDate = new Timestamp(System.currentTimeMillis());
        
        //認証コードを生成
        String authenticationCode = RandomStringUtils.randomAlphanumeric(4);
        
        UsersEntity user = new UsersEntity();
        //データを追加
        user.setEmail(email);
        user.setPassword(securePassword);
        user.setSalt(salt);
        user.setAuthenticationCode(authenticationCode);
        user.setStart_date(startDate);
        
        entityManager.persist(user);
        entityManager.flush();
        
        return user.getUserId();
    }
}

