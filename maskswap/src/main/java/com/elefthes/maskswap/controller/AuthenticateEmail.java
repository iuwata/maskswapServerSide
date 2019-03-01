package com.elefthes.maskswap.controller;

import com.elefthes.maskswap.dto.response.StatusResponse;
import com.elefthes.maskswap.exception.CustomException;
import com.elefthes.maskswap.service.UserService;
import com.elefthes.maskswap.util.StatusCode;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("authenticate")
public class AuthenticateEmail {
    @Inject 
    UserService userService;
    
    @POST
    @Path("email/{userId}/{authenticateCode}")
    @Produces(MediaType.TEXT_HTML)
    public void authenticate(@Context HttpServletRequest req, 
                        @Context HttpServletResponse res, 
                        @PathParam("authenticationCode") String authenticationCode,
                        @PathParam("userId") long userId) throws ServletException, IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.AuthenticateEmail");
        StatusResponse responseData = new StatusResponse();
        Gson gson = new Gson();
        try {
            userService.authenticateEmail(userId, authenticationCode);
            responseData.setResult(StatusCode.Success);
        } catch (CustomException e) {
            responseData.setResult(e.getCode());
        }
    } 
}
