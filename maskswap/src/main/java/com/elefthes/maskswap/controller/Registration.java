package com.elefthes.maskswap.controller;

import com.elefthes.maskswap.dto.request.RegistrationRequest;
import com.elefthes.maskswap.dto.response.StatusResponse;
import com.elefthes.maskswap.exception.CustomException;
import com.elefthes.maskswap.service.UserService;
import com.elefthes.maskswap.util.PasswordChecker;
import com.elefthes.maskswap.util.SendMail;
import com.elefthes.maskswap.util.StatusCode;
import com.google.gson.Gson;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;
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
        Logger logger = Logger.getLogger("com.elefthes.maskswap.controller.Registration");
        String password = requestData.getPassword();
        String email = requestData.getEmail();
        logger.info(email);
        
        Gson gson = new Gson();
        StatusResponse responseData = new StatusResponse();
        try {
            logger.info("Registration.P1");
            //パスワードが使用可能か判定
            if(!PasswordChecker.isPasswordAvailable(password)) { //使用不可能の場合
                throw new CustomException(StatusCode.IncompletePassword);
            }
            //emailが使用可能か判定
            switch(userService.EmailAvailable(email)) {
                case EmailAlreadyExist:
                    //emailが既に存在
                    throw new CustomException(StatusCode.EmailAlreadyExist);
                case IncompleteEmail: 
                    //メールアドレスではない
                    throw new CustomException(StatusCode.IncompleteEmail);
            }
            long userId = userService.create(email, password);
            logger.info("Registration.P2");
            SendMail sendMail = new SendMail();
            StringBuilder mailText = new StringBuilder();
            mailText.append("MaskSwapアカウントにご登録いただき、誠にありがとうございます。");
            mailText.append("\r\n");
            mailText.append("下記のURLよりメールアドレスの認証をお願いいたします。");
            mailText.append("\r\n");
            mailText.append("https://maskswap.com/a/authenticate/email/");
            mailText.append(userId);
            mailText.append("/");
            mailText.append(userService.getUser(userId).getAuthenticationCode());
            mailText.append("\r\n");
            mailText.append("\r\n");
            mailText.append("\r\n");
            mailText.append("MaskSwap運営");
            sendMail.send(email, "MaskSwapメールアドレス認証", mailText.toString());
            logger.info("Rigistration.P3");
            responseData.setResult(StatusCode.Success);
        } catch(CustomException e) {
            responseData.setResult(e.getCode());
        }
        
        return gson.toJson(responseData);
    }
}
