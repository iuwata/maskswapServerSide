package com.elefthes.maskswap.dto.response;

public class SetPlanResponse extends OrderStatusResponse{
    private int amount;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
