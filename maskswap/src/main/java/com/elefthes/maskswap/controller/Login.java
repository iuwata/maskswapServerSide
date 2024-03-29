package com.elefthes.maskswap.controller;

import com.elefthes.maskswap.dto.request.LoginRequest;
import com.elefthes.maskswap.dto.response.StatusResponse;
import com.elefthes.maskswap.service.UserService;
import com.elefthes.maskswap.util.SessionManager;
import com.elefthes.maskswap.util.StatusCode;
import com.google.gson.Gson;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("login")
public class Login {
    @Inject
    UserService userService;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String loginOnWeb(LoginRequest requestData, @Context HttpServletResponse res, @Context HttpServletRequest req) throws NoSuchAlgorithmException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Login");
        logger.info("ServerName : " + req.getServerName());
        logger.info("RemoteAddr : " + req.getRemoteAddr());
        logger.info("RemoteHost : " + req.getRemoteHost());
        logger.info("X-Forwarded-For" + req.getHeader("X-Forwarded-For"));
        String email = requestData.getEmail();
        String password = requestData.getPassword();
        
        StatusResponse responseData = new StatusResponse();
        
        boolean result = userService.login(email, password);
        if(result == true) {
            long userId = userService.getUser(email).getUserId();
            int intervalSec = 3600 * 24 * 7;
            //セッション開始
            SessionManager.beginSession(req, res, intervalSec, true);
            HttpSession session = req.getSession(false);
            session.setAttribute("userId", userId);
            responseData.setResult(StatusCode.Success);
        } else {
            responseData.setResult(StatusCode.Failure);
        }
        Gson gson = new Gson();
        return gson.toJson(responseData);
    }
    
    @POST
    @Path("check")
    @Produces(MediaType.APPLICATION_JSON)
    public String checkLogin(@Context HttpServletRequest req) {
        StatusResponse responseData = new StatusResponse();
        if(SessionManager.hasSession(req)) {
            responseData.setResult(StatusCode.Success);
        } else {
            responseData.setResult(StatusCode.Failure);
        }
        Gson gson = new Gson();
        return gson.toJson(responseData);
    }
}
