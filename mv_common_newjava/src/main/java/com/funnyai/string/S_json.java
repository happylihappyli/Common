/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.string;

import java.util.Iterator;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author happyli
 */
public class S_json {
    public static String read(String strJSON,String key){
        JSONObject pObj = new JSONObject(strJSON);
        return pObj.getString(key);
    }
    
    
    public static String format(String strTemplate,String strJSON){
        JSONObject pObj = new JSONObject(strJSON);
        String strReturn=strTemplate;
        Set test=pObj.keySet();
        Iterator<String> p = test.iterator();
        while (p.hasNext()) {
            String key = p.next();
            String value = pObj.getString(key);
            strReturn=strReturn.replace("["+key+"]",value);
        }
        
        return strReturn;
    }
}
