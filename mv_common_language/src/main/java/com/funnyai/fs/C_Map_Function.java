/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.funnyai.fs;

import com.funnyai.common.AI_Var2;
import com.funnyai.common.S_Debug;
import com.funnyai.net.Old.S_Net;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Administrator
 */
public class C_Map_Function {
    
    private int Session_ID=0;
    private int Node_ID=0;
    
    public String Var1="";//Var1
    public String Var2="";//Var2
    public String Var3="";//Var3
    
    public C_Map_Function(C_Run_Session pSession,int ID){
        Session_ID=pSession.ID;
        Node_ID=ID;
        Read_Json();
    }
    
    public final void Read_Json(){
        String strURL= AI_Var2.Site
               +"/funnyscript/json_read_map_function.php?session="+this.Session_ID+"&id="+this.Node_ID;
        String strData="";
        String strJSON=S_Net.http_GET(strURL,strData, "utf-8", "",20);
        int index=strJSON.indexOf("{");
        if (index>-1){
            strJSON=strJSON.substring(index);
            if (strJSON.length()>10){
                JSONObject pObj=new JSONObject(strJSON);
                try{
                    this.Var1=pObj.getString("Var1");
                    this.Var2=pObj.getString("Var2");
                    this.Var3=pObj.getString("Var3");
                    
                }catch(JSONException e){
                    S_Debug.Write_DebugLog("json", "url:"+strURL+"\nlength:"+strJSON.length()+",json:"+strJSON+"\n"+e.toString());
                }
            }else{
                S_Debug.Write_DebugLog("json", "url:"+strURL+"\n json:"+strJSON);
            }
        }else{
            System.out.println("read error, ID="+this.Session_ID+"&id="+this.Node_ID);
        }
    }
    
    
    public void Save_Var() {
        String strURL= AI_Var2.Site
               +"/funnyscript/save_map_function_var.php";
        String strData="";
        try {
            strData = "id="+URLEncoder.encode(this.Node_ID+"","utf-8")
                    +"&session="+URLEncoder.encode(Session_ID+"","utf-8")
                    +"&var1="+URLEncoder.encode(this.Var1,"utf-8")
                    +"&var2="+URLEncoder.encode(this.Var2,"utf-8")
                    +"&var3="+URLEncoder.encode(this.Var3,"utf-8");
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(C_Job.class.getName()).log(Level.SEVERE, null, ex);
        }

        String strContent=S_Net.http_post(strURL,strData);
        S_Debug.Write_DebugLog("save_map_function_var.php",Session_ID+","+this.Node_ID+strContent);
    }
}
