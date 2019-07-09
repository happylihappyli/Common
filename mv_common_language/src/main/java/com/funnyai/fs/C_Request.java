package com.funnyai.fs;

import java.io.UnsupportedEncodingException;

import com.funnyai.data.C_K_Str;
import com.funnyai.data.Treap;

public class C_Request {

    Treap pParam = new Treap();

    public String GetValue(String string) {
        return getParam(string);
    }

    public void analysis(String strInput) {
        //分析参数
        if (strInput == null) {
            return;
        }

        int Pos = strInput.indexOf("?");
        if (Pos > 0) {
            strInput = strInput.substring(Pos + 1);
        }
        
        Pos = strInput.indexOf(" ");
        if (Pos > 0) {
            strInput = strInput.substring(0,Pos);
        }

        String[] strSplit = strInput.split("&");
        for (String strSplit1 : strSplit) {
            String[] strSplit2 = strSplit1.split("=");
            if (strSplit2.length > 1) {
                String strKey = strSplit2[0];
                String strValue = strSplit2[1];
                String strValue2="";
//                Tools.Write_DebugLog("request","data=\n"+strValue);
                try {
                    if (!"".equals(strValue)){
                        strValue2 = java.net.URLDecoder.decode(strValue, "utf-8");
                    }
                } catch (UnsupportedEncodingException ex) {
                    Tools.Write_DebugLog("request","error=\n"+strValue2);
                    //Logger.getLogger(C_Request.class.getName()).log(Level.SEVERE, null, ex);
                }
//                Tools.Write_DebugLog("request","key=\n"+strKey);
//                Tools.Write_DebugLog("request","value=\n"+strValue2);
                pParam.insert(new C_K_Str(strKey), strValue2);
            }
        }
    }

    //get 
    public String getParam(String strKey) {
        String strValue = (String) pParam.find(new C_K_Str(strKey));

        if (strValue != null) {
            return strValue;
        } else {
            return "";
        }
    }
}
