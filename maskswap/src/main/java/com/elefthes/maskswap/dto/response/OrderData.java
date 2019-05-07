package com.elefthes.maskswap.dto.response;

public class OrderData {
    private long orderId;
    private int progress;
    private String orderDate;
    private String endDate;
    private int completed;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public int getCompleted() {
        return completed;
    }

}
