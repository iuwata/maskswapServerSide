package com.elefthes.maskswap.util;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {
    private static final String ENCODE = "UTF-8";
    
    public class MailAuthenticator extends Authenticator {
            protected PasswordAuthentication getPasswordAutentication() {
                //return new PasswordAuthentication("自分のメールアドレス", "パスワード");
                return new PasswordAuthentication("info@elefthes.com", "vaqVryGUzFHv");
            }
    }
    
    public void send(String addr, String subject, String text) {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.util.SendMail");
        Properties props = new Properties();
        
        /*props.setProperty("mail.transport.protocol", "smtps");
        props.setProperty("mail.smtps.host", "s11.coreserver.jp"); //SMTP
        //props.setProperty("mail.smtps.port", "465"); //SMTPポート
        props.setProperty("mail.smtps.port", "587"); //SMTPポート
        props.setProperty("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtps.socketFactory.fallback", "false");
        props.setProperty("mail.smtps.auth", "true"); //認証
        props.setProperty("mail.smtps.connectiontimeout", "30000"); //タイムアウト
        props.setProperty("mail.smtps.timeout", "30000");
        props.setProperty("mail.smtps.starttls.enable", "true");
        props.setProperty("mail.debug", "true");*/
        //props.setProperty("mail.smtp.host", "s11.coreserver.jp"); //SMTP
        props.setProperty("mail.smtp.host", "202.172.28.12");
        //props.setProperty("mail.smtp.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.port", "587"); //SMTPポート
        props.setProperty("mail.smtp.auth", "true"); //認証
        props.setProperty("mail.smtp.connectiontimeout", "10000"); //タイムアウト
        props.setProperty("mail.smtp.timeout", "10000");
        props.setProperty("mail.smtp.starttls.enable", "true");
        
        Authenticator auth = new MailAuthenticator();
        
        Session session = Session.getInstance(props, auth);
        
        logger.info("SendMail.P1");
        MimeMessage message = new MimeMessage(session);
        
        try {
            //送信元の設定
            Address addrFrom = new InternetAddress("info@elefthes.com");
            //Address addrFrom = new InternetAddress("iuwata0701@gmail.com", "Elefthes", ENCODE);
            message.setFrom(addrFrom);
            
            //宛先の設定
            Address addrTo = new InternetAddress(addr);
            message.setRecipient(Message.RecipientType.TO, addrTo);
            
            //題名、本文の設定
            message.setSubject(subject);
            message.setText(text);
            
            //変更を保存
            message.saveChanges();
            
            //日付
            //message.setSentDate(new Date());
            logger.info("SendMail.P2");
            
            Transport.send(message, "info@elefthes.com", "vaqVryGUzFHv");
            logger.info("SendMail.P3");
        } catch (AuthenticationFailedException e) {
            logger.info("例外1");
            e.printStackTrace();
            //認証失敗
        } catch (MessagingException e){
            logger.info("例外2");
            e.printStackTrace();
        }
    }
}
