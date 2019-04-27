package com.elefthes.maskswap.controller;

import com.elefthes.maskswap.dto.response.StatusResponse;
import com.elefthes.maskswap.exception.CustomException;
import com.elefthes.maskswap.service.UserService;
import com.elefthes.maskswap.util.StatusCode;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@ApplicationScoped
@Path("authenticate")
public class AuthenticateEmail {
    @Inject 
    UserService userService;
    
    @GET
    @Path("email/{userId}/{authenticationCode}")
    //@Produces(MediaType.TEXT_HTML)
    public Response authenticate(@PathParam("userId") long userId,
                                    @PathParam("authenticationCode") String authenticationCode) throws ServletException, IOException {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.AuthenticateEmail");
        
        URI uri;
        String uriStr = "/confirm-email.html";
        
        if(authenticationCode == null) {
            uri = UriBuilder.fromPath(uriStr).queryParam("result", 0).build();
            return Response.seeOther(uri).build();
        }
        
        try {
            userService.authenticateEmail(userId, authenticationCode);
            userService.authenticate(userId);
            uri = UriBuilder.fromPath(uriStr).queryParam("result", 1).build();
        } catch (CustomException e) {
            uri = UriBuilder.fromPath(uriStr).queryParam("result", e.getCode().getId()).build();
        }
        return Response.seeOther(uri).build();
    } 
}
