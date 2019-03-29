package com.elefthes.maskswap.dto.request;

public class GetAmountRequest extends RequestWithToken{
    private long orderId;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
}
