package com.funnyai.fs;

import com.funnyai.io.Old.*;
import com.funnyai.Time.Old.S_Time;
import com.funnyai.common.AI_Var2;
import com.funnyai.common.S_Command;
import com.funnyai.common.S_Debug;
import com.funnyai.data.C_K_Int;
import com.funnyai.data.C_K_Str;
import com.funnyai.io.C_File;
import com.funnyai.io.Old.S_File;
import com.funnyai.net.Old.S_Net;
import com.funnyai.string.Old.S_Strings;
import static java.lang.System.out;
import java.util.List;
import java.util.logging.*;
import org.json.*;

/*
*公共的类，变量
*/
public class Tools {
    
    
    public static void Send_Msg_To_Slack(String QQ,String strMsg){
        
        String strCommand=AI_Var2.Path_Send_Msg+" \""+strMsg+"\"";
        String strResult="";
        try {
            strResult = S_Command.RunShell_Return(strCommand,10);
        } catch (Exception ex) {
            strResult=ex.toString();
        }
        System.out.println(strResult);
    }
    
    
    /**
     * 
     * @param strIP
     * @param iPort
     * @param strMsg 
     */
    public static void Send_Msg_To_DingDing(
            String strIP,int iPort,String strMsg){
        
        strMsg=strMsg.replace("\"", "");
        String url="https://oapi.dingtalk.com/robot/send?access_token=ea9474d66ecce3794683568bd705f2705d89f4bf8f68c157cd21bc86a7376bfd";
        String param="{\"msgtype\": \"text\", \n" +
                "    \"text\": {\n" +
                "        \"content\": \""+AI_Var2.local_ip+":"+S_Time.now_YMD_Hms()+":"+strMsg+"\"\n" +
                "     }\n" +
                "  }";
        
        JSONObject p=new JSONObject(param);
        try {
            out.println(param);
            String strLine=S_Net.post_json_extend(strIP,iPort,url,p,null);
            out.println(strLine);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    
    

    /**
     * 保存文件
     * @param strFile
     * @param strContent
     * @return 
     */
    public static String File_Save(String strFile,String strContent){
        String strDir=strFile.substring(0,strFile.lastIndexOf("/"));
        System.out.println("Dir="+strDir);
        S_Dir.InitDir(strDir);
        C_File pFile = S_File.Write_Begin(strFile, false,"");
        S_File.Write_Line(pFile, strContent);
        pFile.Close();

        List pList=null;
        try {
            pList=S_Command.RunShell_Return2("chmod 777 " + strFile);
        } catch (Exception ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (pList==null){
            return "null";
        }else{
            return "写文件:" + strFile+"\n"+pList.toString();
        }
    }
    /**
     * 保存文件
     * @param ID
     * @param strFile
     * @param bReplace
     * @return 
     */
    public static String File_Save(int ID,String strFile,boolean bReplace){
        C_Job pJob2 = AI_Var3.pSessionS.Read_Job(0,ID);
        String strContent = pJob2.GetProgram(AI_Var3.pSessionS,bReplace);
        return File_Save_Content(ID,strFile,strContent);
    }
    
    public static String File_Save_Content(int ID,String strFile,String strContent){
        String strDir=strFile.substring(0,strFile.lastIndexOf("/"));
        System.out.println("Dir="+strDir);
        S_Dir.InitDir(strDir);
        C_File pFile = S_File.Write_Begin(strFile, false,"");
        S_File.Write_Line(pFile, strContent);
        pFile.Close();

        List pList=null;
        try {
            pList=S_Command.RunShell_Return2("chmod 777 " + strFile);
        } catch (Exception ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (pList==null){
            return "null";
        }else{
            return "写文件:" + strFile+"\n"+pList.toString();
        }
    }
    
    
    /**
     * 文件读取
     * @param strFile
     * @return 
     */
    public static String File_Read(String strFile){
        String strContent="";
        strContent = S_File_Text.Read(strFile,"utf-8",3000);

        return strContent;
    }
    
    
    
    
    public static void Stop_Command(long Session_ID,long ID)
    {
        if (AI_Var2.pCommands.containsKey(Session_ID+","+ID)){
            C_Command pCommand=(C_Command) AI_Var2.pCommands.get(Session_ID+","+ID);
            pCommand.Stop();
        }
    }
    
    

    public static int Get_Length(String strSQL) {
        int iCount=0;
        try {
            String strReturn=S_Command.RunShell_Return("hive -e \""+strSQL+"\"",10);
            if (strReturn==null) return 0;
            if ("".equals(strReturn)) return 0;
            
            iCount=strReturn.length();
                    
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return iCount;
    }

    public static int Hadoop_File_Exists(String strFile) {
        int dbValue=0;
        try {
            String strValue = S_Command.RunShell_Return("hadoop fs -test -e "+strFile+";echo $?",10);
            System.out.println(strValue);
            dbValue=S_Strings.getIntFromStr(strValue,-1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dbValue;
    }
    
    //free -m|awk 'NR==2 {print $4}'
    /**
     * 单位M
     * @return 
     */
    public static int Get_Free_Memory() {
        int dbValue=0;
        try {
            String strValue = S_Command.RunShell_Return("free -m|awk 'NR==2 {print $2-$3}'",10);//"free -m|awk 'NR==2 {print $4}'");
            S_Debug.Write_DebugLog("debug"," Get_Free_Memory strValue1="+strValue);
            dbValue+=S_Strings.getIntFromStr(strValue,0);
            strValue = S_Command.RunShell_Return("free -m|awk 'NR==3 {print $4}'",10);
            S_Debug.Write_DebugLog("debug"," Get_Free_Memory strValue2="+strValue);
            dbValue+=S_Strings.getIntFromStr(strValue,0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dbValue;
    }
    
    /*
    *内存使用百分比
    */
    public static float Get_Hadop_Memory() {
        float dbMemory=0;
        try {
            S_Debug.Write_DebugLog("debug"," run command hadoop job -list|awk '{a=a+$9}END{print a}'");
            String strUsed = S_Command.RunShell_Return("hadoop job -list|awk '{a=a+$9}END{print a}'",10);
            S_Debug.Write_DebugLog("debug"," Get_Memory strUsed="+strUsed);
            System.out.println(strUsed);
            if ("".equals(strUsed)) return 0;
            dbMemory=Float.valueOf(strUsed);//(Float.valueOf(strUsed)+Float.valueOf(strRemain));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dbMemory;
    }
    
    
    public static int Get_Prepare_Run_Count() {
        String strURL = (AI_Var2.Site+"/funnyscript/json_get_prepare_count.php");
        String strReturn = S_Net.http_GET(strURL,"","utf-8", "",20);//false);
        int IndexStr = strReturn.indexOf("{");
        int iCount=0;
        if (IndexStr > -1) {
            strReturn = strReturn.substring(IndexStr);
            try {
                JSONObject pJobject = new JSONObject(strReturn);
                iCount = pJobject.getInt("data");
            }catch (Exception ex) {
            }
        }
        return iCount;
    }
    
    /**
     * 
     * @return 
     */
    public static int Get_Job_List_Prepare(){
        int iValue=0;
        try {
            String strValue = S_Command.RunShell_Return("hadoop job -list|awk '{if ($2 ~ /PREP/) a=a+1}END{print a}'",10);
            System.out.println(strValue);
            S_Debug.Write_DebugLog("debug"," Get_Job_List_Prepare strValue="+strValue);
            iValue=S_Strings.getIntFromStr(strValue,0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return iValue;
    }
    
    
    
    /*
    *Container使用百分比
    */
    public static float Get_Process_Count() {
        float dbPercent=0;
        try {
            //String strUsed = C_Command.RunShell_Return("hadoop job -list|awk '{a=a+$7}END{print a}'");
            String strUsed = S_Command.RunShell_Return("ps aux|grep "+AI_Var2.Path_Shell+"|awk '{a=a+1}END{print a}'",10);
            S_Debug.Write_DebugLog("debug"," Get_Process_Count strUsed="+strUsed);
            if ("".equals(strUsed)) return 0;
            dbPercent=Float.valueOf(strUsed);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dbPercent;
    }
    
    /**
     * 挂着排队的任务有多少个
     * @return 
     */
    public static float Get_Apps_Pending() { 
        if ("".equals(AI_Var2.Apps_Pending)) return 0;
        
        float dbValue=0;
        try {
            String[] strSplit=AI_Var2.Apps_Pending.split(",");
            for (int i=0;i<strSplit.length;i++){
                if (!"".equals(strSplit[i]) && !"0".equals(strSplit[i])){
                    String url=AI_Var2.Site+"/funnyscript/funny_php.php?id="+strSplit[i];
                    String strReturn=S_Net.http_GET(url,"","utf-8","",20);
                    strReturn=strReturn.replace("\r","");
                    strReturn=strReturn.replace("\n","");
                    strReturn=strReturn.replace("\\r","");
                    strReturn=strReturn.replace("\\n","");
                    S_Debug.Write_DebugLog("debug"," Get_Apps_Pending strReturn="+strReturn);
                    dbValue+=S_Strings.getIntFromStr(strReturn,0);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dbValue;
    }
    
}