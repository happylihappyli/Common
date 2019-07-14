/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.common;

import com.funnyai.Time.Old.S_Time;
import com.funnyai.io.Old.S_File_Text;
import com.funnyai.net.Old.S_Net;
import com.funnyai.string.Old.S_Strings;
import static java.lang.System.out;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author happyli
 */
public class S_Debug {
    
//    public static void Write_DebugLog(String File_Tag,String strLog)
//    {
//        Write_DebugLog(File_Tag,strLog,true);
//    }
//    
//    public static void Write_DebugLog(String File_TagString strLog,boolean bShow)
//    {
//        if (bShow){
//            System.out.println(S_Time.now_YMD_Hms()+"\t\t"+strLog);
//        }
//        
//        S_File_Text.Append("./funnyserver_debug_"+File_Tag+".txt",S_Time.now_YMD_Hms()+"\t\t"+strLog);
//    }
//    
    public static void Write_DebugLog(String File_Tag,String strLog)
    {
        Write_DebugLog(File_Tag,strLog,true);
    }
    
    public static void Write_DebugLog(String File_Tag,String strLog,boolean bShow)
    {
        if (bShow){
            out.println(S_Time.now_YMD_Hms()+"\t\t"+strLog);
        }
        S_File_Text.Append(AI_Var2.Path_Log+File_Tag+".txt",S_Time.now_YMD_Hms()+"\t\t"+strLog);
    }
}
