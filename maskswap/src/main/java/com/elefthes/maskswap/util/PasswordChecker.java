package com.elefthes.maskswap.util;

public class PasswordChecker {
    public static boolean isPasswordAvailable(String password) {
        String pattern = "^[a-zA-Z0-9!-/:-@\\[-`\\{-~]{8,64}$";
        return password.matches(pattern);
    }
}
