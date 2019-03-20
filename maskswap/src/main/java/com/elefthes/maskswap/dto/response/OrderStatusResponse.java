package com.elefthes.maskswap.dto.response;

public class OrderStatusResponse extends StatusResponse{
    private long orderId;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
}
