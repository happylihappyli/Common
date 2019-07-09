package com.funnyai.data;

import com.funnyai.string.S_string;
import static java.lang.System.out;

/**
 *
 * @author happyli
 */
public class S_array {
    public static boolean String_In_Array(String[] strArray,String strCompare){
        for (int i=0;i<strArray.length;i++){
            if (strCompare.equals(strArray[i])){
                return true;
            }
        }
        return false;
    }
    
    public static C_Var_Java size(C_Var_Java... a){
        if (a[0].pObj instanceof int[]){
            int[] pObjects=(int[]) a[0].pObj;
            return new C_Var_Java("",pObjects.length);
        }else{
            Object[] pObjects=(Object[]) a[0].pObj;
            return new C_Var_Java("",pObjects.length);
        }
    }

    public static C_Var_Java set(C_Var_Java[] a) {
        if (a[0].pObj instanceof int[]){
            int[] pObjects=(int[]) a[0].pObj;
            int i=S_string.getIntFromStr(a[1].pObj,0);
            int tmp=(int) a[2].pObj;
            out.println("====a["+i+"]="+tmp);
            pObjects[i]=tmp;
        }else{
            Object[] pObjects=(Object[]) a[0].pObj;
            int i=S_string.getIntFromStr(a[1].pObj,0);
            Object tmp=a[2].pObj;
            pObjects[i]=tmp;
            out.println("====a["+i+"]="+tmp);
        }
        return a[2];
    }
}
