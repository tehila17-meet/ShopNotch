package com.example.shopnotch.Utilities.UserUtils;

import android.util.Log;
import android.widget.EditText;


public class Validator {
    private static String check;

    public static boolean validateNumber(EditText mobileNumber) {

        check = mobileNumber.getText().toString();
        Log.e("checking mobile number", check.length() + " ");
        if (check.length() > 10) {
            return false;
        } else if (check.length() < 10) {
            return false;
        }
        return true;
    }

    public static boolean validateCnfPass(EditText cnfPass, EditText password) {

        check = cnfPass.getText().toString();

        return check.equals(password.getText().toString());
    }

    public static boolean validatePass(EditText password) {


        check = password.getText().toString();

        if (check.length() < 4 || check.length() > 20) {
            return false;
        } else if (!check.matches("^[A-za-z0-9@]+")) {
            return false;
        }
        return true;
    }

    public static boolean validateEmail(EditText userEmail) {

        check = userEmail.getText().toString();

        if (check.length() < 4 || check.length() > 40) {
            return false;
        } else if (!check.matches("^[A-za-z0-9.@]+")) {
            return false;
        } else if (!check.contains("@") || !check.contains(".")) {
            return false;
        }

        return true;
    }

    public static boolean validateName(EditText name) {

        check = name.getText().toString();

        return !(check.length() < 4 || check.length() > 20);

    }
}
