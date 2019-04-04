package com.elefthes.maskswap.dto.request;

public class CreateChargeRequest extends RequestWithToken{
    private String stripeToken;
    private long orderId;

    public String getStripeToken() {
        return stripeToken;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public void setStripeToken(String stripeToken) {
        this.stripeToken = stripeToken;
    }
}
