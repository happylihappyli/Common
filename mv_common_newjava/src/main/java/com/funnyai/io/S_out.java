/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.io;

import com.funnyai.data.C_Var_Java;

/**
 *
 * @author happyli
 */
public class S_out {

    public static C_Var_Java print(C_Var_Java... a) {
        if (a.length>0){
            for (C_Var_Java a1 : a) {
                String strTmp="0";
                if (a1!=null){
                    if (a1.pObj instanceof int[]){
                        int[] b=(int[]) a1.pObj;
                        System.out.print("{");
                        for (int i=0;i<b.length;i++){
                            if (i>0) System.out.print(",");
                            System.out.print(b[i]);
                        }
                        System.out.println("}");
                    }else{
                        if (a1.pObj instanceof String[]){
                            String[] b=(String[]) a1.pObj;
                            System.out.print("{");
                            for (int i=0;i<b.length;i++){
                                if (i>0) System.out.print(",");
                                System.out.print(b[i]);
                            }
                            System.out.println("}");
                        }else if (a1.pObj instanceof Integer){
                            strTmp=a1.pObj+"";
                            System.out.println(strTmp);
                        }else{
                            strTmp=(String) a1.pObj;
                            System.out.println(strTmp);
                        }
                    }
                }
            }
        }
        return new C_Var_Java("String","");
    }
    public static C_Var_Java exit(Object... a){
        System.exit(0);
        return new C_Var_Java("String","");
    }
}
