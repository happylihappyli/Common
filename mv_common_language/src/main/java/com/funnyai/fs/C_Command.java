package com.funnyai.fs;

import com.funnyai.Time.Old.S_Time;
import com.funnyai.common.AI_Var2;
import com.funnyai.common.C_Log;
import com.funnyai.common.S_Debug;
import com.funnyai.common.class_call;
import com.funnyai.data.C_K_Str;
import com.funnyai.data.C_Var_Java;
import com.funnyai.net.Old.S_Net;
import com.funnyai.netso.C_Session_AI;
import com.funnyai.string.Old.S_MD5;
import com.funnyai.string.Old.S_Strings;
import java.io.*;
import static java.lang.System.out;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;

public class C_Command extends Thread{
    public String user="";//Socket.IO用户名，
    public String Command_Raw="";//原始命令
    public String strCommand="";
    public String Param="";//运行shell任务的参数
    public int CPU_Time=0;
    public int ID=0;//job id
    public boolean Run_Error=false;//运行时错误，就会自动中断
    //public boolean bSendDingDing=false;//是否发送到钉钉。
    public C_Run_Session pRun_Session;
    C_Job pJob;
    
    C_Log pSB_Output; 
    C_Log pSB_Error; 
    public String Encode="utf-8";
    
    Process process = null;
    
    public C_Command(C_Run_Session pRun_Session,int ID){
        this.pRun_Session=pRun_Session;
        this.ID=ID;
        pSB_Output=new C_Log(ID,pRun_Session.ID,"out"); 
        pSB_Error=new C_Log(ID,pRun_Session.ID,"error"); 
    }
    
    @Override
    public void run() {
        pSB_Output.clear(); 
        pSB_Error.clear(); 
        
        try {
            if (!"".equals(Param)){
                Command_Raw=strCommand+" "+Param;
            }else{
                Command_Raw=strCommand;
            }
            pJob.pRun.Save_Command_Raw(Command_Raw);
            RunShell(Command_Raw);
        } catch (IOException ex) {
            Logger.getLogger(C_Command.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public String Output()
    {
        return pSB_Output.toString();
    }
    
    public String Error()
    {
        return pSB_Error.toString();
    }
    
    public void Stop()
    {
        try {
            Thread.sleep(30000);
            
            process.destroy();
            pSB_Output.clear();
            pSB_Error.clear();
        } catch (InterruptedException ex) {
            S_Debug.Write_DebugLog("error", ex.toString());
          
            //Logger.getLogger(C_Command.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    /**
     * 运行任务
     * @param cmdString
     * @throws IOException 
     */
    public void RunShell(String cmdString) throws IOException {
        
        String[] cmdArray = {"/bin/bash", "-c", cmdString};
        
        try {
            process = Runtime.getRuntime().exec(cmdArray);
            
            
        } catch (IOException ex) {
            S_Debug.Write_DebugLog("",ex.toString());
        }
        if (this.Encode==null) this.Encode="utf-8";
        if ("".equals(this.Encode)) this.Encode="utf-8";
        
        BufferedReader rOut = new BufferedReader(new InputStreamReader(process.getInputStream(),this.Encode));
        BufferedReader rErr = new BufferedReader(new InputStreamReader(process.getErrorStream(),this.Encode));
        Output_Thread(rOut, "CopyStdOut: ");
        Error_Thread(rErr, "CopyStdErr: ");
        
        int exitCode;
        try {
            int Try_Times=0;//重试次数
            int Function_Call=0;
            if (pJob!=null){
                if (pJob.pRun!=null) {
                    Try_Times=pJob.pRun.Try_Times;
                    Function_Call=pJob.pRun.Function_Call;
                }
            }
            C_Log.Save_Machine(pRun_Session,pJob.ID,Function_Call,Try_Times);
            
            S_Debug.Write_DebugLog("Try_Times","S="+pRun_Session.ID+",Job="+pJob.ID+","+Try_Times);
            
            exitCode = process.waitFor();
            
            this.pSB_Output.end_write();
            this.pSB_Error.end_write();
            
            if (Run_Error && exitCode==0) exitCode=10000;
            

            if (exitCode>0){
                if (Run_Error){
                    S_Debug.Write_DebugLog("error"," Run_Error=true:\n"+
                            pRun_Session.ID+","+pJob.ID+","+Try_Times+"\n"+
                            cmdString);
                }else{
                    S_Debug.Write_DebugLog("error"," exitCode>0:" +
                            pRun_Session.ID+","+pJob.ID+","+Try_Times+"\n"+
                            exitCode+"\n 有错误！"+pJob.ID+"\n"+cmdString);
                }
                C_Log.Save_ExitCode(pRun_Session,pJob.ID,Function_Call,Try_Times,exitCode);
                if (pJob!=null) pJob.Finished_Callback(pRun_Session,true,"exitCode>0:" + exitCode+"\n 有错误！id="+pJob.ID);
            }else{
                if (pJob!=null) pJob.Finished_Callback(pRun_Session,false,"运行成功！id="+pJob.ID);
                S_Debug.Write_DebugLog("","运行成功！id="+pJob.ID);
            }
            
        } catch (InterruptedException ex) {
            if (pJob!=null) pJob.Finished_Callback(pRun_Session,true,ex.toString());
            S_Debug.Write_DebugLog("error","Error 1:" + ex.toString());
        } finally{
            S_Net.SI_Send("sys_event","","C_Command",this.user, "command finished="+cmdString);
            S_Net.SI_Send("sys_event","","C_Command.Output",this.user,this.Output());
            S_Net.SI_Send("sys_event","","C_Command.Error",this.user,this.Error());
        }
    }

     private Thread Output_Thread(
            final BufferedReader in, final String threadName) {
        Thread copyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                
                try {
                    StringBuilder pStr=new StringBuilder();
                    StringBuilder pStr_Java=new StringBuilder();
                    StringBuilder pStr_Save_Value=new StringBuilder();
                    
                    Date pTime=S_Time.now();
                    pSB_Output.addLine(S_Time.now_YMD_Hms()+"\n");
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.startsWith("FAILED:")){
                            S_Debug.Write_DebugLog("error","startsWith FAILED::\n");
                            Run_Error=true;
                        }
                        
                        if (line.contains("FS.Run")){
                            pStr.append(line).append("\n");
                            S_Debug.Write_DebugLog("FS.Run",line);
                        }else if (line.contains("FS.Save.Value")){
                            pStr_Save_Value.append(line).append("\n");
                            S_Debug.Write_DebugLog("FS.Java",line);
                        }else if (line.contains("FS.Java")){
                            pStr_Java.append(line).append("\n");
                            S_Debug.Write_DebugLog("FS.Java",line);
                        }
                        
                        
                        pSB_Output.addLine(line+"\n");
                        if (pJob!=null){
                            if (S_Time.now().getTime()/1000-pTime.getTime()/1000>6){
                                pTime=S_Time.now();
                                pJob.pRun.Save_Time();//状态
                            }
                        }
                    }
                    
                    
                    line=pStr.toString();
                    if (!line.equals("")){
                        S_Debug.Write_DebugLog("FS.Run",line);
                        Line_Match(line);
                    }
                    
                    line=pStr_Java.toString();
                    if (!line.equals("")){
                        S_Debug.Write_DebugLog("FS.Java",line);
                        Line_Match_Java(line);
                    }
                    
                    
                    line=pStr_Save_Value.toString();
                    if (!line.equals("")){
                        S_Debug.Write_DebugLog("FS.Java",line);
                        Line_Match_Save_Value(line);
                    }
                    
                    
                } catch (IOException e) {
                    e.printStackTrace();
                    S_Debug.Write_DebugLog("error","读取Job结果的错误输出流时发生异常: " + threadName
                        +e.toString());
                } finally {
                    if (pJob!=null){
                        AI_Var2.pCommands.remove(new C_K_Str(pRun_Session.ID+","+pJob.ID));
                        pJob.pCommand.Stop();
                        pJob.pCommand=null;
                    }
                }
            }
        }, threadName);
        copyThread.setDaemon(true);
        copyThread.start();
        return copyThread;
    }

     static class MyClassLoader extends ClassLoader {

        public synchronized Class<?> loadClass(String name, File file) throws FileNotFoundException {
            Class<?> cls = findLoadedClass(name);
            if(cls != null) {
                return cls;
            }
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            try {
                while (true) {
                    len = fis.read(buffer);
                    if (len == -1) {
                        break;
                    }
                    baos.write(buffer, 0, len);
                }
                byte[] data = baos.toByteArray();
                return defineClass(null, data, 0, data.length);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
     

    public static C_Var_Java Call_Class(String strTag1, String strTag2,String[] strParams){
        C_Var_Java strReturn=null;
        try {
            File file = new File(strTag1);
            MyClassLoader myClassLoader = new MyClassLoader();
            Class<?> myClass1 = myClassLoader.loadClass(strTag2,file);
            Object o = myClass1.newInstance();
            class_call pClass = (class_call) o;
            out.println("=== call java.class");
            C_Var_Java[] strParams2=new C_Var_Java[strParams.length];
            for (int i=0;i<strParams2.length;i++){
                strParams2[i]=new C_Var_Java("String",strParams[i]);
            }
            strReturn=pClass.call(strParams2);
            out.println(strReturn);
            out.println("=== end call");
        } catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException | FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return strReturn;
    }
    
    

    public static String Read_Value(String XPath){
        String strValue="";
        String strURL="";
        try {
            strURL = AI_Var2.Site
                    +"/funnyai/read_value.php?XPath="+URLEncoder.encode(XPath,"UTF-8");
                    
            String strJSON=S_Net.http_GET(strURL,"","utf-8","",20);
            int index=strJSON.indexOf("{");
            if (index>=0){
                strJSON=strJSON.substring(index);
                JSONObject pObj=new JSONObject(strJSON);
                strValue=pObj.getString("value");
            }
        } catch (UnsupportedEncodingException ex) {
            S_Debug.Write_DebugLog("Read_Value",strURL);
        }
            
        return  strValue;
    }
    
    
    public static void Save_Value_Admin(String XPath,String Class_ID,String Value){
        //funnywatch 用，要测试一下
        String strURL= AI_Var2.Site
                    +"/funnyai/save_value_admin.php";
        String strData="XPath="+S_Strings.URL_Encode(XPath)
                +"&Value="+S_Strings.URL_Encode(Value)+"&Class_ID="+S_Strings.URL_Encode(Class_ID)
                +"&key="+S_MD5.getMD5("funnyai");
        S_Debug.Write_DebugLog("Save_Value",strURL);
        strData=S_Net.http_post(strURL, strData);
        
        S_Debug.Write_DebugLog("Save_Value",strData);
    }
    
    public void Line_Match_Save_Value(String line){
        Pattern p = Pattern.compile("FS.Save.Value\\((.*?)\\)");//
        Matcher m = p.matcher(line);
        while (m.find()){
            String XPath="";
            String Value="";
            String Class_ID="";
            
            line=m.group(1);
            String[] strSplit=line.split(":");
            if (strSplit.length>0) XPath=strSplit[0];
            if (strSplit.length>1) Class_ID=strSplit[1];
            if (strSplit.length>2) Value=strSplit[2];

            C_Command.Save_Value_Admin(XPath,Class_ID,Value);
        
            S_Debug.Write_DebugLog("FS.Save.Value","XPath="+XPath);
            S_Debug.Write_DebugLog("FS.Save.Value",Value);
            
            pSB_Output.addLine("XPath="+XPath+"\n");
            pSB_Output.addLine(Value+"\n");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
     
    public void Line_Match_Java(String line){
        Pattern p = Pattern.compile("FS.Java\\((.*?)\\)");//
        Matcher m = p.matcher(line);
        while (m.find()){
            int ID2=0;
            line=m.group(1);
            String[] strSplit=line.split(":");
            if (strSplit.length>0) ID2=Integer.valueOf(strSplit[0]);

            String strLine="";
            for (int i=1;i<strSplit.length;i++){
                strLine+=strSplit[i]+":";
            }
            String[] strParams=strLine.split(":");
            C_Var_Java pVar=C_Command.Call_Class(AI_Var2.Path_Root+"java/fs/bin/cn/magicwindow/test/C_"+ID2+".class","cn.magicwindow.test.C_"+ID2,strParams);
        
            out.println("ID="+ID2);
            out.println(pVar.pObj);
            
            S_Debug.Write_DebugLog("FS.Java","ID="+ID2);
            S_Debug.Write_DebugLog("FS.Java",pVar.pObj.toString());
            
            pSB_Output.addLine("ID="+ID2+"\n");
            pSB_Output.addLine(pVar.pObj+"\n");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void Line_Match(String line){
        
        Pattern p = Pattern.compile("FS.Run\\((.*?)\\)");//
        Matcher m = p.matcher(line);
        while (m.find()){
            int ID2=0;
            line=m.group(1);
            String[] strSplit=line.split(":");
            if (strSplit.length>0) ID2=Integer.valueOf(strSplit[0]);

            C_Run_Session pRun_Session2 = C_Run_Session.Get_New_Session(ID2,10);
            S_Debug.Write_DebugLog("FS.Run","Session.ID="+pRun_Session2.ID);

            if (strSplit.length>1) pRun_Session2.Param1=strSplit[1];
            if (strSplit.length>2) pRun_Session2.Param2=strSplit[2];

            pRun_Session2.Continue=1;
            pRun_Session2.Save_To_DB(pRun_Session.Time_Run);//把参数存储到数据库

            C_Job pJob2 = pRun_Session2.Read_Job(pJob.pRun.Function_Call,ID2);//获取task信息
            pJob2.pRun.Save_Status("prepare_run","FS.Run");
            pSB_Output.addLine(ID2+","+pRun_Session2.Param1+","+pRun_Session2.Param2+"\n");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        p = Pattern.compile("FS.Run.Program\\((.*?)\\)");//[\\d\\w:]+
        m = p.matcher(line);
        while (m.find()){
            int ID2=0;
            line=m.group(1);
            String[] strSplit=line.split(":");
            if (strSplit.length>0) ID2=Integer.valueOf(strSplit[0]);

            C_Run_Session pRun_Session2 = C_Run_Session.Get_New_Session(ID2,10);
            S_Debug.Write_DebugLog("FS.Run.Program","Session.ID="+pRun_Session2.ID);


            List<String> pListParam = new ArrayList<>();
            for (int i=1;i<10;i++){
                if (strSplit.length>i) pListParam.add(strSplit[i]);
            }

            //pRun_Session2.Save_To_DB(pRun_Session.Time_Run);//把参数存储到数据库
            C_Job pJob2 = pRun_Session2.Read_Job(pJob.pRun.Function_Call,ID2);//获取task信息

            C_Session_AI pSession = new C_Session_AI();
            String strContent=pJob2.Run_Program(pSession,null,pRun_Session,0,ID2,null,null,pListParam);
            S_Debug.Write_DebugLog("FS.Run.Program",strContent);
            pSB_Output.addLine(ID2+","+pListParam.toString()+"\n");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    
    private Thread Error_Thread(
            final BufferedReader in, final String threadName) {
        Thread copyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                
                int Try_Times=0;//重试次数
                int Function_Call=0;
                if (pJob!=null){
                    if (pJob.pRun!=null){
                        Try_Times=pJob.pRun.Try_Times;
                        Function_Call=pJob.pRun.Function_Call;
                    }
                }
                try {
                    Date pTime=S_Time.now();
                    pSB_Error.addLine(S_Time.now_YMD_Hms()+"\n");
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println("****************************\n"+line);
                        if (line.contains("FAILED:")){
                            S_Debug.Write_DebugLog("error"," startsWith FAILED::\n");
                            S_Net.SI_Send("sys_event","error","error",C_Command.this.user,"J="+pJob.ID +",S="+pJob.pRun_Session.ID+"<br>"+ line);
                            Run_Error=true;
                        }
                        if (line.contains("Cannot allocate memory")){
                            S_Net.SI_Send("sys_event","error","error",C_Command.this.user,"J="+pJob.ID +",S="+pJob.pRun_Session.ID+"<br>"+ line);
                            Run_Error=true;
                        }
                        if (line.contains("Exception in")){
                            S_Net.SI_Send("sys_event","error","error",C_Command.this.user,"J="+pJob.ID +",S="+pJob.pRun_Session.ID+"<br>"+ line);
                            Run_Error=true;
                        }
                        if (line.startsWith("Error ")){
                            S_Net.SI_Send("sys_event","error","error",C_Command.this.user,"J="+pJob.ID +",S="+pJob.pRun_Session.ID+"<br>"+ line);
                            Run_Error=true;
                        }
                        
                        if (line.contains("Starting Job = job_")){
                            S_Debug.Write_DebugLog("test",line);
                            Pattern p = Pattern.compile("Starting Job = (job_.*?), Tracking URL =");
                            Matcher m = p.matcher(line);
                            while (m.find()){
                                S_Debug.Write_DebugLog("test","matched");
                                if (pJob.pRun!=null){
                                    pJob.pRun.Job_ID = m.group(1);
                                    S_Debug.Write_DebugLog("test","job_id"+pJob.pRun.Job_ID);
                                    pJob.pRun.Save_Job_ID(pJob.pRun.Job_ID,"");
                                }else{
                                    S_Debug.Write_DebugLog("test","pJob.pMap_Run=null");
                                }
                            }
                        }
                        
                        if (line.contains("MapReduce Total cumulative CPU time:")){
                            Pattern p = Pattern.compile("MapReduce Total cumulative CPU time: (\\d+) seconds.*");
                            Matcher m = p.matcher(line);
                            
                            if (pJob.pRun!=null){
                                Try_Times=pJob.pRun.Try_Times;
                                Function_Call=pJob.pRun.Function_Call;
                            }
                        
                            if (m.matches()){
                                CPU_Time+=Integer.valueOf(m.group(1));
                                C_Log.Save_CPU(pRun_Session, pJob.ID,Function_Call,Try_Times,CPU_Time);
                            }else{
                                p = Pattern.compile("MapReduce Total cumulative CPU time: (\\d+) minutes (\\d+) seconds.*");
                                m = p.matcher(line);
                                if (m.matches()){
                                    CPU_Time+=60*Integer.valueOf(m.group(1))+Integer.valueOf(m.group(2));
                                    C_Log.Save_CPU(pRun_Session, pJob.ID,Function_Call,Try_Times,CPU_Time);
                                }else{
                                    p = Pattern.compile("MapReduce Total cumulative CPU time: (\\d+) hours (\\d+) minutes (\\d+) seconds.*");
                                    m = p.matcher(line);
                                    if (m.matches()){
                                        CPU_Time+=60*60*Integer.valueOf(m.group(1))+60*Integer.valueOf(m.group(2))+Integer.valueOf(m.group(3));
                                        C_Log.Save_CPU(pRun_Session, pJob.ID,Function_Call,Try_Times,CPU_Time);
                                    }else{
                                        p = Pattern.compile("MapReduce Total cumulative CPU time: (\\d+) days (\\d+) hours (\\d+) minutes (\\d+) seconds.*");
                                        m = p.matcher(line);
                                        if (m.matches()){
                                            CPU_Time+=24*60*60*Integer.valueOf(m.group(1))+60*60*Integer.valueOf(m.group(2))+60*Integer.valueOf(m.group(3))+Integer.valueOf(m.group(4));
                                            C_Log.Save_CPU(pRun_Session, pJob.ID,Function_Call,Try_Times,CPU_Time);
                                        }
                                    }
                                }
                            }
                            //MapReduce Total cumulative CPU time: 0 days 22 hours 6 minutes 59 seconds
                        }
                        pSB_Error.addLine(line+"\n");
                        if (pJob!=null){
                            if (S_Time.now().getTime()/1000-pTime.getTime()/1000>6){
                                pTime=S_Time.now();
                                pJob.pRun.Save_Time();//状态
//                                C_Log.Save_Output(pRun_Session.ID, pJob.ID,Function_Call,Try_Times,true,"",pSB_Error.toString());
                            }
                        }
                    }
                    //如果不到60秒
                } catch (IOException e) {
                    S_Debug.Write_DebugLog("error","读取Job结果的错误输出流时发生异常: " + threadName 
                            +e.toString());
                } finally {
                    if (pJob!=null){
                        AI_Var2.pCommands.remove(new C_K_Str(pRun_Session.ID+","+pJob.ID));
                        pJob.pCommand.Stop();
                        pJob.pCommand=null;
                    }
                }
            }
        }, threadName);
        copyThread.setDaemon(true);
        copyThread.start();
        return copyThread;
    }
}
