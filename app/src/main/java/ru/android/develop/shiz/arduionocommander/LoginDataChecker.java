package ru.android.develop.shiz.arduionocommander;

import android.util.Log;

/**
 * Created by ultra on 29.01.2016.
 */
public class LoginDataChecker {
    private static boolean loginCheck;

    public static String[] getCredentials() {
        Log.d("CHecked", "Ch: " + loginCheck);
        if (!loginCheck) {
            return null;
        }

        String[] credentials = new String[] {
                "log:pas"
        };

        return credentials;
    }

    public static void setLoginCheck(boolean b) {
        loginCheck = b;
    }
}