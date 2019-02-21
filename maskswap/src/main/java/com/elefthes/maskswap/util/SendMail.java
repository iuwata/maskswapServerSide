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
    
    public void send(String addr, String subject, String text) {
        Logger logger = Logger.getLogger("com.elefthes.maskswap.util.SendMail");
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", "smtp.gmail.com"); //SMTP
        props.setProperty("mail.smtp.port", "587"); //SMTPポート
        props.setProperty("mail.smtp.auth", "true"); //認証
        props.setProperty("mail.smtp.connectiontimeout", "30000"); //タイムアウト
        props.setProperty("mail.smtp.timeout", "30000");
        //TLSの設定
        props.setProperty("mail.transport.protocol", "smtps");
        props.setProperty("mail.smtp.starttls", "true");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAutentication() {
                //return new PasswordAuthentication("自分のメールアドレス", "パスワード");
                return new PasswordAuthentication("info@elefthes.com", "vaqVryGUzFHv");
            }
        });
        
        MimeMessage message = new MimeMessage(session);
        
        try {
            Address addrFrom = new InternetAddress("info@elefthes.com", "Elefthes", ENCODE);
            message.setFrom(addrFrom);
            Address addrTo = new InternetAddress(addr, addr, ENCODE);
            message.setRecipient(Message.RecipientType.TO, addrTo);
            //メールの題名
            message.setSubject(subject);
            //メール本文
            message.setText(text);
            //日付
            message.setSentDate(new Date());
        } catch (MessagingException e){
            logger.info("例外1");
        } catch (UnsupportedEncodingException e) {
            logger.info("例外2");
        }
        
        try {
            Transport.send(message);
        } catch (AuthenticationFailedException e) {
            logger.info("例外3");
            //認証失敗
        } catch (MessagingException e) {
            logger.info("例外4");
            //smtpサーバーへの接続失敗
        }
    }
}
