package com.elefthes.maskswap.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SafePassword {
    private static final int STRETCH_COUNT = 5000;
    private static final int SALT_LENGTH = 64;

    /**
    * 文字列からSHA256のハッシュ値を取得
    * @param target 対象の文字列
     * @return ハッシュ値
    */
    public static String getSha256(String target){
        MessageDigest md = null;
        StringBuffer buf = new StringBuffer();
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(target.getBytes());
            // ハッシュ値を取得
            byte[] digest = md.digest();
            for(int i = 0; i < digest.length; i++) {
                //16進数に変換してバッファに追加
                buf.append(String.format("%02x", digest[i]));
            }
        } catch(NoSuchAlgorithmException e) {
        }

        return buf.toString();
    }
    
     /**
    * バイト配列からSHA256のハッシュ値を取得
    * @param target 対象のバイト配列
     * @return ハッシュ値
    */
    public static String getSha256(byte[] target){
        MessageDigest md = null;
        StringBuffer buf = new StringBuffer();
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(target);
            // ハッシュ値を取得
            byte[] digest = md.digest();
            for(int i = 0; i < digest.length; i++) {
                //16進数に変換してバッファに追加
                buf.append(String.format("%02x", digest[i]));
            }
        } catch(NoSuchAlgorithmException e) {
        }

        return buf.toString();
    }

    public static String getSalt() throws NoSuchAlgorithmException {
        byte salt[] = new byte[SALT_LENGTH];
        StringBuffer buf = new StringBuffer();
        SecureRandom random;

        random = SecureRandom.getInstance("NativePRNG");
        //乱数を生成
        random.nextBytes(salt);

        return getSha256(salt);
    }

    /**
    * saltを使いストレッチングしたパスワードを取得
    * @param password パスワード
    * @param salt ストレッチングに用いるソルト
    * @return ストレッチングしたパスワード
    */
    public static String getStretchedPassword(String password, String salt) {
        String hash = "";
        for (int i = 0; i < STRETCH_COUNT; i++) {
            hash = getSha256(hash + salt + password);
        }

        return hash;
    }
}
