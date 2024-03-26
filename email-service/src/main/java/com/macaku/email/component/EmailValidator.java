package com.macaku.email.component;

public class EmailValidator {

    public final static String EMAIL_PATTERN = "^[\\w\\.-]+@[a-zA-Z\\d\\.-]+\\.[a-zA-Z]{2,}$";

    public static boolean isEmailAccessible(String email) {
        return email.matches(EMAIL_PATTERN);
    }

}