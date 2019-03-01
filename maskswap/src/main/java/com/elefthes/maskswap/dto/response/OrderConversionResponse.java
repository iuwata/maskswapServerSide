package com.elefthes.maskswap.dto.response;

public class OrderConversionResponse extends StatusResponse{
    long orderId;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
}
