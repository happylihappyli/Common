/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.common;

import com.funnyai.net.Old.S_Net;
import com.funnyai.string.Old.S_Strings;

/**
 *
 * @author happyli
 */
public class S_Save {
    
    
    public static void Save_Value(String XPath,String Value){
        //funnywatch 用，要测试一下
        String strURL= AI_Var2.Site
                    +"/funnyai/save_value.php";
        String strData="XPath="+S_Strings.URL_Encode(XPath)
                +"&Value="+S_Strings.URL_Encode(Value);
        strData=S_Net.http_post(strURL,strData);
        System.out.println(strData);
    }
    
}
