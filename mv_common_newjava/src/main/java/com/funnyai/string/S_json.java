/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.string;

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
}
