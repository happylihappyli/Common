package com.funnyai.data_Old;

/**
 *
 * @author happyli
 */
public class S_Array {
    public static boolean String_In_Array(String[] strArray,String strCompare){
        for (int i=0;i<strArray.length;i++){
            if (strCompare.equals(strArray[i])){
                return true;
            }
        }
        return false;
    }
}
