/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.string;

import com.funnyai.data.C_Var_Java;
import static java.lang.System.out;
import org.json.JSONArray;

/**
 * template
 * @author happyli
 */
public class S_t {
    
    public static C_Var_Java format(String template,C_Var_Java... a){
        String strReturn=template;
        for (int i=0;i<a.length;i++){
            strReturn=strReturn.replace("{"+i+"}",(String)a[i].pObj);
        }
        return new C_Var_Java("String",strReturn);
    }
    
    public static C_Var_Java format_table(String template,C_Var_Java... a){
        String strReturn="";
        String strTmp=(String) a[0].pObj;
        JSONArray arr = new JSONArray(strTmp);
        String strSep="\n";
        if (a.length>1){
            strSep=(String) a[1].pObj;
        }
        for (int i=0;i<arr.length()-1;i++){
            String strLine=template;
            JSONArray pArray=arr.getJSONArray(i);
            for (int j=0;j<pArray.length();j++){
                strLine=strLine.replace("{"+j+"}",pArray.optString(j));//.getString(j));
            }
            if (i<arr.length()-1){
                strReturn+=strLine+strSep;
            }else{
                strReturn+=strLine;
            }
        }
        return new C_Var_Java("String",strReturn);
    }
}
