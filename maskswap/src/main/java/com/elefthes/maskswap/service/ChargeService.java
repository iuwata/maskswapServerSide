package com.elefthes.maskswap.service;

import com.elefthes.maskswap.entity.ChargesEntity;
import com.elefthes.maskswap.entity.OrdersEntity;
import com.elefthes.maskswap.exception.CustomException;
import com.elefthes.maskswap.util.StatusCode;
import com.stripe.Stripe;
import com.stripe.exception.ApiConnectionException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.RateLimitException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Refund;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@ApplicationScoped
public class ChargeService {
    
    @PersistenceContext(unitName = "maskswapGeneral")
    private EntityManager entityManager; 
    
    @Inject
    OrderService orderService;
    
    private static final String API_KEY = "sk_test_nRuEveFzfu5Vjl34uYllES34001aRYCLjc";
    
    public List<ChargesEntity> getCharges(long orderId) {
        List<ChargesEntity> result = entityManager.createNamedQuery("Charges.byOrderId", ChargesEntity.class)
                                        .setParameter("orderId", orderId).getResultList();
        
        return result;
    }
    
    public ChargesEntity getCharge(long orderId) {
        ChargesEntity result = entityManager.createNamedQuery("Charges.byOrderId", ChargesEntity.class)
                                        .setParameter("orderId", orderId).getSingleResult();
        return result;
    }
    
    //@Transactional
    public boolean isAlreadyPaid(long orderId) {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.service.ChargeService.isAlreadyPaid");
        OrdersEntity order = orderService.getOrderByOrderId(orderId);
        if(order.getPaymentDate() != null) {
            //支払い処理完了
            return true;
        } else {
            List<ChargesEntity> chargeList = this.getCharges(orderId);
            if(chargeList.size() == 0) {
                return false;
            } else {
                ChargesEntity chargeEntity = chargeList.get(0);
                String chargeId = chargeEntity.getChargeId();
                if(chargeId != null) {
                    try {
                        Charge charge = Charge.retrieve(chargeId);
                        if(charge.getPaid() == true && charge.getCaptured()) {
                            return true;
                        }
                    } catch(CardException e) {
                        logger.info(e.getCode());
                        logger.info(e.getMessage());
                        throw new CustomException(StatusCode.CheckPaymentFailure);
                    } catch(RateLimitException e) {
                        throw new CustomException(StatusCode.CheckPaymentFailure);
                    } catch(InvalidRequestException e) {
                        throw new CustomException(StatusCode.CheckPaymentFailure);
                    } catch(AuthenticationException e) {
                        throw new CustomException(StatusCode.CheckPaymentFailure);
                    } catch(ApiConnectionException e) {
                        throw new CustomException(StatusCode.CheckPaymentFailure);
                    } catch(StripeException e) {
                        throw new CustomException(StatusCode.CheckPaymentFailure);
                    }
                }
            }
        }
        return false;
    }
    
    /*
    public void payment(long orderId, String stripeToken) {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.service.ChargeService.payment");
        
        if(this.isAlreadyPaid(orderId) == false) {
            Charge charge = this.createCharge(orderId, stripeToken);
            this.captureCharge(orderId, charge);
        } else { 
            throw new CustomException(StatusCode.AlreadyPaid);
        }
    }*/
    
    
    //@Transactional
    public Charge createCharge(long orderId, String stripeToken) {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.service.ChargeService.createCharge");
        Stripe.apiKey = API_KEY;
        
        logger.info("ストライプトークン : " + stripeToken);
        
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", orderService.getAmount(orderId));
        chargeParams.put("currency", "jpy");
        chargeParams.put("source", stripeToken);
        chargeParams.put("capture", false);
        
        Charge charge = null;
        
        try {
            charge = Charge.create(chargeParams);
        } catch(CardException e) {
            logger.info(e.getCode());
            logger.info(e.getMessage());
            //this.refund(orderId, charge);
            throw new CustomException(StatusCode.PaymentFailure);
        } catch(RateLimitException e) {
            //this.refund(orderId, charge);
            e.printStackTrace();
            throw new CustomException(StatusCode.PaymentFailure);
        } catch(InvalidRequestException e) {
            //this.refund(orderId, charge);
            e.printStackTrace();
            throw new CustomException(StatusCode.PaymentFailure);
        } catch(AuthenticationException e) {
            //this.refund(orderId, charge);
            e.printStackTrace();
            throw new CustomException(StatusCode.PaymentFailure);
        } catch(ApiConnectionException e) {
            //this.refund(orderId, charge);
            e.printStackTrace();
            throw new CustomException(StatusCode.PaymentFailure);
        } catch(StripeException e) {
            //this.refund(orderId, charge);
            e.printStackTrace();
            throw new CustomException(StatusCode.PaymentFailure);
        } catch(RuntimeException e) {
            e.printStackTrace();
            throw new CustomException(StatusCode.PaymentFailure);
        }
        /*
        try {
            this.addChargeToDataBase(orderId, charge.getId());
        } catch(RuntimeException e) {
            e.printStackTrace();
            throw new CustomException(StatusCode.PaymentFailure);
        }*/
        
        return charge;
    }
    
    
    @Transactional
    public void addChargeToDataBase(long orderId, String chargeId) {
            Logger logger = Logger.getLogger("com.elefthes.maskswap.service.ChargeService");
            OrdersEntity order = orderService.getOrderByOrderId(orderId);

        try {
            List<ChargesEntity> result = this.getCharges(orderId);
            
            ChargesEntity chargeEntity;
            logger.info("支払い追跡1");

            if(result.isEmpty()) {
                logger.info("支払い追跡2");
                chargeEntity =  new ChargesEntity();
                chargeEntity.setOrderId(orderId);
                chargeEntity.setUserId(order.getUserId());
            } else {
                logger.info("支払い追跡3");
                chargeEntity = result.get(0);
            }
            logger.info("支払い追跡4");
            logger.info("チャージID : " + chargeId);
            chargeEntity.setChargeId(chargeId);
            entityManager.persist(chargeEntity);
            entityManager.flush();
        } catch(RuntimeException e) {
            e.printStackTrace();
        }
    }
    
    //@Transactional
    public void captureCharge(long orderId, Charge charge) {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.service.ChargeService.captureCharge");
        Stripe.apiKey = API_KEY;
        
        try {
            //支払い
            charge.capture();
        } catch(CardException e) {
            logger.info(e.getCode());
            logger.info(e.getMessage());
            this.refund(orderId, charge);
            e.printStackTrace();
            throw new CustomException(StatusCode.PaymentFailure);
        } catch(RateLimitException e) {
            this.refund(orderId, charge);
            e.printStackTrace();
            throw new CustomException(StatusCode.PaymentFailure);
        } catch(InvalidRequestException e) {
            this.refund(orderId, charge);
            e.printStackTrace();
            throw new CustomException(StatusCode.PaymentFailure);
        } catch(AuthenticationException e) {
            this.refund(orderId, charge);
            e.printStackTrace();
            throw new CustomException(StatusCode.PaymentFailure);
        } catch(ApiConnectionException e) {
            this.refund(orderId, charge);
            e.printStackTrace();
            throw new CustomException(StatusCode.PaymentFailure);
        } catch(StripeException e) {
            this.refund(orderId, charge);
            e.printStackTrace();
            throw new CustomException(StatusCode.PaymentFailure);
        }
        
        /*try {
            this.completePayment(orderId);
        } catch(RuntimeException e) {
            throw new CustomException(StatusCode.PaymentDataBaseFailure);
        }*/
    }
    
    @Transactional
    public void completePayment(long orderId) {
        OrdersEntity order = orderService.getOrderByOrderId(orderId);

        order.setPaymentDate(new Timestamp(System.currentTimeMillis()));
        entityManager.persist(orderId);
        entityManager.flush();
    }
    
    @Transactional
    public void refund(long orderId, Charge charge) {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.service.ChargeService.refund");
        
        Stripe.apiKey = API_KEY;
        
        Map<String, Object> refundParams = new HashMap<>();
        refundParams.put("charge", charge.getId());
        
        Refund refund = null;
        
        try {
            refund = Refund.create(refundParams);
        } catch(CardException e) {
            logger.info(e.getCode());
            logger.info(e.getMessage());
        } catch(RateLimitException e) {
            e.printStackTrace();
        } catch(InvalidRequestException e) {
            e.printStackTrace();
        } catch(AuthenticationException e) {
            e.printStackTrace();
        } catch(ApiConnectionException e) {
            e.printStackTrace();
        } catch(StripeException e) {
            e.printStackTrace();
        }
        
        /*OrdersEntity order = orderService.getOrderByOrderId(orderId);
        
        ChargesEntity chargeEntity = this.getCharge(orderId);
        chargeEntity.setChargeId(null);
        chargeEntity.setRefundId(charge.getId());
        entityManager.persist(chargeEntity);
        entityManager.flush();*/
    }
}
