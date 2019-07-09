package com.funnyai.math;

/**
 *
 * @author lihaibin
 */
public class MathTools {
    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
