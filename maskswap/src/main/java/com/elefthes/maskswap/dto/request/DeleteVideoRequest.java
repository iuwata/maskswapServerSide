package com.elefthes.maskswap.dto.request;

public class DeleteVideoRequest extends RequestWithToken{
    private long orderId;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
}
