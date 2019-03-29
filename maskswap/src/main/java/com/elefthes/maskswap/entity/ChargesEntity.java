package com.elefthes.maskswap.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "charges")
public class ChargesEntity implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "order_id")
    private long orderId;
    
    @Column(name = "user_id")
    private long userId;
    
    @Column(name = "charge_id")
    private String chargeId;
    
    @Column(name = "refund_id")
    private String refundId;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getChargeId() {
        return chargeId;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }
}
