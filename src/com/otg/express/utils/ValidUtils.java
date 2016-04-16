package com.otg.express.utils;

import android.text.TextUtils;
import android.widget.EditText;

import java.util.regex.Pattern;

public class ValidUtils {
    private static final String EMAIL_NUMBER_REG = "^[0-9a-zA-Z][a-z0-9A-Z\\._-]{1,}@[a-z0-9-]{1,}[a-z0-9]\\.[a-z\\.]{1,}[a-z]$";
    private static final String ID_CARD_REG = "^(\\d{15}|\\d{18}|\\d{17}[x,X])$";
    private static final String PHONE_NUMBER_REG = "^((\\+{0,1}86){0,1})1[0-9]{10}";

    public static boolean isSame(EditText paramEditText1, EditText paramEditText2) {
        if ((paramEditText1 == null) || (paramEditText2 == null)) {
            throw new NullPointerException("EditText can't null");
        }
        return isSame(paramEditText1.getText().toString(), paramEditText2.getText().toString());
    }

    public static boolean isSame(String paramString1, String paramString2) {
        if ((paramString1 == null) || (paramString2 == null)) {
        }
        while (!paramString2.equals(paramString1)) {
            return false;
        }
        return true;
    }

    public static boolean isValidEmail(EditText paramEditText) {
        if (paramEditText == null) {
            throw new NullPointerException("EditText can't null");
        }
        return isValidEmail(paramEditText.getText().toString());
    }

    public static boolean isValidEmail(String paramString) {
        if (TextUtils.isEmpty(paramString)) {
            return false;
        }
        return Pattern.compile(EMAIL_NUMBER_REG).matcher(paramString).matches();
    }

    public static boolean isValidIdCard(EditText paramEditText) {
        return (paramEditText != null) && (isValidIdCard(paramEditText.getText().toString()));
    }

    public static boolean isValidIdCard(String paramString) {
        return (paramString != null) && (paramString.matches(ID_CARD_REG));
    }

    public static boolean isValidPass(EditText paramEditText) {
        if (paramEditText == null) {
            throw new NullPointerException("EditText can't null");
        }
        return isValidPass(paramEditText.getText().toString());
    }

    public static boolean isValidPass(String paramString) {
        if (paramString == null) {
        }
        int i = paramString.length();
        if ((i < 6) || (i > 16)) {
            return false;
        }
        ;
        return true;
    }

    public static boolean isValidPhoneNumber(EditText paramEditText) {
        if (paramEditText == null) {
            throw new NullPointerException("EditText can't null");
        }
        return isValidPhoneNumber(paramEditText.getText().toString());
    }

    public static boolean isValidPhoneNumber(String paramString) {
        if (TextUtils.isEmpty(paramString)) {
            return false;
        }
        return Pattern.compile(PHONE_NUMBER_REG).matcher(paramString).matches();
    }
}
