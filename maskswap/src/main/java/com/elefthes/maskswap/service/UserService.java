package com.elefthes.maskswap.service;

import com.elefthes.maskswap.entity.UsersEntity;
import com.elefthes.maskswap.util.EmailAddressChecker;
import com.elefthes.maskswap.util.PasswordChecker;
import com.elefthes.maskswap.util.SafePassword;
import com.elefthes.maskswap.util.StatusCode;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
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
    
    public boolean checkSaltOverlap(String salt) {
        List<UsersEntity> result = entityManager.createNamedQuery("Users.bySalt", UsersEntity.class)
                                            .setParameter("salt", salt).getResultList();
        return result.size() == 0;
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
    
    public StatusCode canLogin(String email, String password) {
        try {
            UsersEntity result = entityManager.createNamedQuery("Users.byEmail", UsersEntity.class)
                                                    .setParameter("email", email).getSingleResult();
            if(SafePassword.getStretchedPassword(password, result.getSalt()).equals(result.getPassword())) {
                return StatusCode.Success;
            } else {
                return StatusCode.PasswordIsIncorrect;
            }
        } catch (NoResultException e) {
            return StatusCode.EmailDoesNotExist;
        }
    }
    
    @Transactional
    public StatusCode create(String email, String password) throws NoSuchAlgorithmException {
        //パスワードが使用可能か判定
        if(!PasswordChecker.isPasswordAvailable(password)) { //使用不可能の場合
            return StatusCode.IncompletePassword;
        }
        //emailが使用可能か判定
        switch(EmailAvailable(email)) {
            case EmailAlreadyExist:
                //emailが既に存在
                return StatusCode.EmailAlreadyExist;
            case IncompleteEmail: 
                //メールアドレスではない
                return StatusCode.EmailAlreadyExist;
        }
        
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
        String verificationCode = RandomStringUtils.randomAlphanumeric(4);
        
        UsersEntity user = new UsersEntity();
        //データを追加
        user.setEmail(email);
        user.setPassword(securePassword);
        user.setSalt(salt);
        user.setVerificationCode(verificationCode);
        user.setStart_date(startDate);
        
        entityManager.persist(user);
        entityManager.flush();
        
        return StatusCode.Success;
    }
}
