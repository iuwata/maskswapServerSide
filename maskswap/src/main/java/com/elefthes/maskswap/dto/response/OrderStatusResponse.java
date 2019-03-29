package com.elefthes.maskswap.dto.response;

public class OrderStatusResponse extends StatusResponse{
    private long orderId;
    private int srcDuration;
    private int dstDuration;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getSrcDuration() {
        return srcDuration;
    }

    public void setSrcDuration(int srcDuration) {
        this.srcDuration = srcDuration;
    }

    public int getDstDuration() {
        return dstDuration;
    }

    public void setDstDuration(int dstDuration) {
        this.dstDuration = dstDuration;
    }
}
