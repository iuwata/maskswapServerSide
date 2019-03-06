package com.elefthes.maskswap.dto.response;

import java.util.ArrayList;
import java.util.List;

public class FindOrdersResponse extends StatusResponse{
    private List<OrderData> orders;

    public FindOrdersResponse() {
        orders = new ArrayList<OrderData>();
    }
    
    public List<OrderData> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderData> orders) {
        this.orders = orders;
    }
}
