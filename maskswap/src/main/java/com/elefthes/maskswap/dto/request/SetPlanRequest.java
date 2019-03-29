package com.elefthes.maskswap.dto.request;

public class SetPlanRequest extends RequestWithToken{    
    private int plan;
    private long orderId;

    public int getPlan() {
        return plan;
    }

    public void setPlan(int plan) {
        this.plan = plan;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
}
