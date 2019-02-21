package com.elefthes.maskswap.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailAddressChecker {
    public static boolean isEmailAddress(String emailAddress) {
        String pattern = "^(([0-9a-zA-Z!#\\$%&'\\*\\+\\-/=\\?\\^_`\\{\\}\\|~]+(\\.[0-9a-zA-Z!#\\$%&'\\*\\+\\-/=\\?\\^_`\\{\\}\\|~]+)*)|(\"[^\"]*\"))"
                        + "@[0-9a-zA-Z!#\\$%&'\\*\\+\\-/=\\?\\^_`\\{\\}\\|~]+"
                        + "(\\.[0-9a-zA-Z!#\\$%&'\\*\\+\\-/=\\?\\^_`\\{\\}\\|~]+)*$";
        return emailAddress.matches(pattern);
    }
}
