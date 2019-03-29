package com.elefthes.maskswap.dto.response;

public class GetAmountResponse extends StatusResponse{
    private int amount;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}