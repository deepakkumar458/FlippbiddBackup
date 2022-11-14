package com.flippbidd.Others;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Validator {

    public static Validator VALIDATOR;

    public static Validator getInstance() {
        if (VALIDATOR == null)
            VALIDATOR = new Validator();
        return VALIDATOR;
    }

    public boolean isEmpty(String s) {
        return StringUtils.isEmpty(s);
    }

    public boolean isMinLength(String s, int length) {
        return !(s.length() >= length);
    }

    public boolean isMaxLength(String s, int length) {
        return (s.length() > length);
    }

    public boolean isFirstSpace(String s) {
        try {
            String userName = s;
            String first = userName.charAt(0) + "";
            return first.equals(" ");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean isChar(String s) {
        return !StringUtils.isAlpha(s);
    }

    public boolean isNumber(String s) {
        return !StringUtils.isNumeric(s);
    }

    public boolean isAlphaNumeric(String s) {
        return !StringUtils.isAlphanumeric(s);
    }

    public boolean isEmoji(String s) {
        return !StringUtils.isAsciiPrintable(s);
    }

    public boolean checkEmail(String email) {
        String regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return !matcher.matches();
    }

    public boolean checkEquals(String stringOne, String stringTwo) {
        return !StringUtils.equals(stringOne, stringTwo);
    }

    public boolean checkPhoneNumber(String phone) {
        boolean isValid = false;
        String expression = "^[+1-9][0-9]+$";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(phone);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public boolean checkMobileNo10Digit(String email) {
        String regex = "^[0-9]{10}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return !matcher.matches();
    }

    public boolean checkZipCode6Digit(String email) {
        String regex = "^[0-9]{6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return !matcher.matches();
    }
}
