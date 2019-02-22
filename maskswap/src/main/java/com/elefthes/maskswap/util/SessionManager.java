package com.elefthes.maskswap.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@ApplicationScoped
public class SessionManager{
    // トークンサイズ
    private static int TOKEN_LENGTH = 16;

    /**
    * セッション開始
    * @param req サーブレットリクエスト
     * @param res
    * @param intervalSec セッションの生存期間
    * @param useToken トークンを作成するか
    */
    public static void beginSession(HttpServletRequest req, HttpServletResponse res, int intervalSec, boolean useToken) throws NoSuchAlgorithmException {
        Logger logger = Logger.getLogger("jp.osaka.bigdata.util.SessionManager");
        //セッションが既に存在する場合セッションを破棄
        HttpSession session = req.getSession(false);
        if(session != null){
            session.invalidate();
        }
        //新しくセッションを開始
        session = req.getSession(true);
        logger.info("セッションの初期有効期間は"+ session.getMaxInactiveInterval() + "秒です");
        //セッションの有効期限を設定
        //session.setMaxInactiveInterval(intervalSec);

        //トークンをセットするかどうか
        if(useToken) {
            logger.info("トークン作成開始");
            String token = getCsrfToken();
            //トークンをセッションに保存
            session.setAttribute("token", token);
            Cookie cookie = new Cookie("token", token);
            cookie.setMaxAge(intervalSec);
            cookie.setHttpOnly(false);
            cookie.setPath("/");
            res.addCookie(cookie);
        }
    }

    /**
    * セッション終了
    */
    public void endSession(){
    }
    
    /**
     * セッションチェック
     * @param req
     */
    public static boolean hasSession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if(session == null) {
            return false;
        }
        return true;
    }

    /**
    * トークン作成
    */
    private static String getCsrfToken() throws NoSuchAlgorithmException{
        byte token[] = new byte[TOKEN_LENGTH];
        StringBuffer buf = new StringBuffer();
        SecureRandom random = null;

        random = SecureRandom.getInstance("NativePRNG");
        //乱数を生成
        random.nextBytes(token);

        //トークンを生成
        for(int i = 0; i < token.length; i++){
            buf.append(String.format("%02x", token[i]));
        }
        return buf.toString();
    }
}
