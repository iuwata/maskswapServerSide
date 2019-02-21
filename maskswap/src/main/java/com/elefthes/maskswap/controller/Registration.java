package com.elefthes.maskswap.controller;

import com.elefthes.maskswap.dto.request.RegistrationRequest;
import com.elefthes.maskswap.dto.response.StatusResponse;
import com.elefthes.maskswap.service.UserService;
import com.elefthes.maskswap.util.SendMail;
import com.elefthes.maskswap.util.StatusCode;
import com.google.gson.Gson;
import java.security.NoSuchAlgorithmException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("signup")
public class Registration {
    @Inject
    UserService userService;
    
    @POST
    @Path("registration")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String registrationOnWeb(RegistrationRequest requestData) throws NoSuchAlgorithmException {
        String password = requestData.getPassword();
        String email = requestData.getEmail();
        
        Gson gson = new Gson();
        StatusResponse responseData = new StatusResponse();
        
        StatusCode result = userService.create(email, password);
        if(result == StatusCode.Success) {
            SendMail sendMail = new SendMail();
            sendMail.send(email, "MaskSwapメールアドレス認証", "ご登録ありがとうございます");
        }
        responseData.setResult(result);
        return gson.toJson(responseData);
    }
}
