package com.cs371m.chuchu.restaurantroulette;

import android.util.Patterns;

/**
 * Created by chuchu on 11/10/15.
 */
public class LoginManager {

    public static String validate(String username, String email, String password, String passwordAgain) {
        StringBuilder result = new StringBuilder("Please ");
        boolean error = false;

        if (username.length() == 0) {
            result.append("enter a username");
            error = true;
        }

        if (email != null && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (error) {
                result.append(", and ");
            }
            result.append("enter a valid email address");
            error = true;
        }

        if (password.length() == 0) {
            if (error) {
                result.append(", and ");
            }
            result.append("enter a password");
            error = true;
        }

        if (passwordAgain != null) {
            if (!password.equals(passwordAgain)) {
                if (error) {
                    result.append(", and ");
                }
                result.append("enter matching passwords");
                error = true;
            }
        }
        result.append(".");

        return error ? result.toString() : "";
    }

}
