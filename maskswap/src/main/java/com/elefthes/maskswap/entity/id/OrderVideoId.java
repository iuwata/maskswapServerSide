package com.elefthes.maskswap.entity.id;

import java.io.Serializable;

public class OrderVideoId implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private long orderId;
    private int storageOrder;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getStorageOrder() {
        return storageOrder;
    }

    public void setStorageOrder(int storageOrder) {
        this.storageOrder = storageOrder;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        return super.hashCode(); //To change body of generated methods, choose Tools | Templates.
    }
}
