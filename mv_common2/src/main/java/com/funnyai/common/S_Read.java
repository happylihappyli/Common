/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.common;

import com.funnyai.net.Old.S_Net;
import com.funnyai.string.Old.S_Strings;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author happyli
 */
public class S_Read {
    
    public static int Read_Machine_ID(String ip){
        String strURL =  AI_Var2.Site
                + "/funnyai/json_read_machine_group.php?ip="+S_Strings.URL_Encode(ip);
        S_Debug.Write_DebugLog("Read_Machine_Group",strURL);
        String strJSON = S_Net.http_GET(strURL,"", "utf-8", "",20);
        
        int index = strJSON.indexOf("{");
        strJSON = strJSON.substring(index);
        S_Debug.Write_DebugLog("Read_Machine_ID",strJSON);
        try
        {
            JSONObject pObj = new JSONObject(strJSON);
            int Machine_ID= pObj.getInt("ID");
            return Machine_ID;
        }catch(JSONException e){
        }
        return 0;
    }
    
    
    public static String Read_Machine_Group(String ip){
        String strURL =  AI_Var2.Site
                + "/funnyai/json_read_machine_group.php?ip="+S_Strings.URL_Encode(ip);
        
        String strJSON = S_Net.http_GET(strURL,"", "utf-8", "", 20);
        
        int index = strJSON.indexOf("{");
        strJSON = strJSON.substring(index);
        try
        {
            JSONObject pObj = new JSONObject(strJSON);
            String Group_ID= pObj.getString("Group_ID");
            return Group_ID;
        }catch(JSONException e){
        }
        return "0";
    }
    
}
