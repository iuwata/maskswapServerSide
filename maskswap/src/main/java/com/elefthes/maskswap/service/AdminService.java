package com.elefthes.maskswap.service;

import com.elefthes.maskswap.entity.AdminsEntity;
import com.elefthes.maskswap.util.SafePassword;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@ApplicationScoped
public class AdminService {
    @PersistenceContext(unitName = "maskswapGeneral")
    private EntityManager entityManager;
    
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
}
