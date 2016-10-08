package com.deft.bus.utils;

/**
 * Created by Administrator on 2016/10/8.
 */
public class TextUtils {

    public static String convertStringArrayToText(String[] array) {
        if (array == null)
            return null;
        StringBuilder text = new StringBuilder("[ ");
        for (String string : array) {
            text.append("\"");
            text.append(string);
            text.append("\" ");
        }
        text.append("]");
        return text.toString();
    }
}
