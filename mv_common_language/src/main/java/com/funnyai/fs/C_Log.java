/*
 * 日志类
 */
package com.funnyai.fs;

import com.funnyai.common.AI_Var2;
import com.funnyai.io.C_File;
import com.funnyai.io.Old.S_File;
import com.funnyai.net.Old.S_Net;
import com.funnyai.string.Old.S_Strings;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 日志处理
 * @author happyli
 */
public class C_Log {
    private final ArrayList pArray=new ArrayList();
    private int maxLine=3000;
    public C_File pFile=null;
    public String strFile="";
    public int Item_ID=0;
    public int Session_ID=0;
    
    public C_Log(int Item_ID,int Session_ID,String strHead){
        this.Item_ID=Item_ID;
        this.Session_ID=Session_ID;
        
        strFile=AI_Var2.Path_Log+Item_ID+"_"+Session_ID+"."+strHead+".log";
        Tools.Write_DebugLog("FileName", "file="+strFile);
        pFile = S_File.Write_Begin(strFile, true, "utf-8");
    }
    
    public void setMaxLine(int iMax)
    {
        maxLine=iMax;
    }
    
    public void addLine(String strLine){
        S_File.Write_Line(pFile, strLine);
        pArray.add(strLine);
        while (pArray.size()>this.maxLine){
            pArray.remove(0);
        }
    }
    
    public void clear(){
        System.out.println("clear log");
        pArray.clear();
    }
    
    @Override
    public String toString(){
        StringBuilder pSB=new StringBuilder();
        pSB.append(pArray.size()).append("\n");
        for (Object pArray1 : pArray) {
            pSB.append(pArray1);
        }
        return pSB.toString();
    }
    
    public static void Save_ExitCode(
            C_Run_Session pRun_Session,
            int Item_ID,int Function_Call,
            int Try_Times,int exitCode)
    {
        String strURL=AI_Var2.Site
                +"/funnyscript/save_log_exitcode.php?id="+Item_ID
                +"&session="+pRun_Session.ID+"&function_call="+Function_Call+"&try="+Try_Times+"&exitcode="+exitCode;
        String strContent=S_Net.http_GET(strURL,"", "utf-8","",20);
        Tools.Write_DebugLog("exitcode", strURL);
        Tools.Write_DebugLog("exitcode", strContent);
    }
    
    
    public static String getBASE64(String s) { 
        if (s == null) return null; 
        return S_Strings.Base64_Encode(s);
    }
    
    /**
     * 
     */
    public void Save_FileName()
    {
        try {
            String strURL=AI_Var2.Site
                    +"/funnyscript/save_log_file_name.php?id="+Item_ID
                    +"&session="+Session_ID+"&function_call=0&try="+"0";
            String strData="";
            strData = "filename="+URLEncoder.encode(AI_Var2.Path_Log+Item_ID+"_"+Session_ID,"UTF-8")+"&machine="+AI_Var2.machine_id;
            String strReturn=S_Net.http_post(strURL,strData);
            Tools.Write_DebugLog("FileName","F=" + strFile);
            Tools.Write_DebugLog("FileName","AI_Var2.machine_id=" + AI_Var2.machine_id);
            Tools.Write_DebugLog("FileName","R=" + strReturn);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(C_Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 保存每次计算的CPU秒数
     * @param pRun_Session
     * @param Item_ID
     * @param Function_Call
     * @param Try_Times
     * @param cpu 
     */
    public static void Save_CPU(
            C_Run_Session pRun_Session,
            int Item_ID,int Function_Call,int Try_Times,
            int cpu)
    {
        String strURL=AI_Var2.Site
                +"/funnyscript/save_log_cpu.php?id="+Item_ID
                +"&session="+pRun_Session.ID+"&function_call="+Function_Call+"&try="+Try_Times+"&cpu="+cpu;
        String strContent=S_Net.http_GET(strURL,"","utf-8","",20);
        Tools.Write_DebugLog("cpu", strURL);
        Tools.Write_DebugLog("cpu", strContent);
    }
    
    public static void Save_Machine(
            C_Run_Session pRun_Session,
            int Item_ID,int Function_Call,int Try_Times)
    {
        String strURL=AI_Var2.Site
                +"/funnyscript/save_log_machine.php?id="+Item_ID
                +"&session="+pRun_Session.ID+"&function_call="+Function_Call
                +"&try="+Try_Times+"&value="+AI_Var2.local_ip+"&machine="+AI_Var2.machine_id;;
        String strContent=S_Net.http_GET(strURL,"", "utf-8","",20);
        Tools.Write_DebugLog("cpu", strURL);
        Tools.Write_DebugLog("cpu", strContent);
    }

    public void end_write() {
        pFile.Close();
    }
}
