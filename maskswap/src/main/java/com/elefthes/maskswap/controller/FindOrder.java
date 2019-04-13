package com.elefthes.maskswap.controller;

import com.elefthes.maskswap.dto.request.RequestWithToken;
import com.elefthes.maskswap.dto.response.FindOrdersResponse;
import com.elefthes.maskswap.dto.response.OrderData;
import com.elefthes.maskswap.entity.OrdersEntity;
import com.elefthes.maskswap.exception.CustomException;
import com.elefthes.maskswap.service.OrderService;
import com.elefthes.maskswap.util.DateFormatter;
import com.elefthes.maskswap.util.StatusCode;
import com.google.gson.Gson;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("find")
public class FindOrder {
    @Inject 
    OrderService orderService;
    
    @POST
    @Path("orders")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getOrders(RequestWithToken requestData, @Context HttpServletRequest req) {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.FindOrder");
        
        FindOrdersResponse responseData = new FindOrdersResponse();
        Gson gson = new Gson();
        
        try {
            //トークンチェック
            HttpSession session = req.getSession(false);
            if(session == null) {
                logger.info("セッションが存在しません");
                throw new CustomException(StatusCode.NeedLogin);
            }
            if(!(session.getAttribute("token").equals(requestData.getToken()))){
                logger.info("トークンが存在しません");
                throw new CustomException(StatusCode.NeedLogin);
            }

            List<OrdersEntity> orders = orderService.getOrders((long)session.getAttribute("userId"));
            for(OrdersEntity order : orders) {
                OrderData orderData = new OrderData();
                orderData.setOrderId(order.getOrderId());
                orderData.setProgress(order.getProgress());

                Date orderDate = new Date(order.getOrderDate().getTime());
                orderData.setOrderDate(DateFormatter.convertSlash(orderDate));

                Timestamp endTimestamp = order.getEndDate();
                if(endTimestamp != null) {
                    Date endDate = new Date(order.getEndDate().getTime());
                    orderData.setEndDate(DateFormatter.convertSlash(endDate));
                }
                responseData.getOrders().add(orderData);
            }
            responseData.setResult(StatusCode.Success);
        } catch(CustomException e) {
            responseData.setResult(e.getCode());
        }
        
        return gson.toJson(responseData);
    }
}
