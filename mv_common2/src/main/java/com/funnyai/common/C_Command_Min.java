package com.funnyai.common;

import com.funnyai.Time.Old.S_Time;
import com.funnyai.data.C_K_Str;
import com.funnyai.net.Old.S_Net;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class C_Command_Min extends Thread{
    public int ID=0;
    public String strCommand="";
    public String Param="";//运行shell任务的参数
    public int CPU_Time=0;
    public int Alert_ID=0;//提醒模板ID
    public boolean Run_Error=false;//运行时错误，就会自动中断
    
    public C_Run_Session_Min pRun_Session=null;
    //C_Job pJob;
    
    C_Log pSB_Output; 
    C_Log pSB_Error; 
    public String Encode="utf-8";
    
    Process process = null;
    
    public C_Command_Min(C_Run_Session_Min pRun_Session,int ID){
        this.ID=ID;
        this.pRun_Session=pRun_Session;
        pSB_Output=new C_Log(ID,pRun_Session.ID,"out"); 
        pSB_Error=new C_Log(ID,pRun_Session.ID,"error"); 
    }
    
    @Override
    public void run() {
        pSB_Output.clear(); 
        pSB_Error.clear(); 
        
        try {
            if (!"".equals(Param)){
                RunShell(strCommand+" "+Param);
            }else{
                RunShell(strCommand);
            }
        } catch (IOException ex) {
            S_Debug.Write_DebugLog("error", ex.toString());
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
    
    public static String RunShell_Return(String shStr) throws Exception {
        StringBuilder pSB=new StringBuilder();
        
        Process process_static = Runtime.getRuntime().exec(new String[]{"/bin/sh","-c",shStr},null,null);
        InputStreamReader ir = new InputStreamReader(process_static
                        .getInputStream());
        LineNumberReader input = new LineNumberReader(ir);
        String line;
        process_static.waitFor();
        while ((line = input.readLine()) != null){
            pSB.append(line);
        }
        return pSB.toString();
    }
    
    /**
     * Shell执行命令
     * @param shStr
     * @return
     * @throws Exception 
     */
    public static List RunShell_Return2(String shStr) throws Exception {
        List<String> strList = new ArrayList();
        
        Process process_static = Runtime.getRuntime().exec(new String[]{"/bin/sh","-c",shStr},null,null);
        InputStreamReader ir = new InputStreamReader(process_static.getInputStream());
        LineNumberReader input = new LineNumberReader(ir);
        String line;
        process_static.waitFor();
        while ((line = input.readLine()) != null){
            strList.add(line+"\n");
        }
        strList.add("\n===output.error===\n");
        ir = new InputStreamReader(process_static.getErrorStream());
        input = new LineNumberReader(ir);
        while ((line = input.readLine()) != null){
            strList.add(line+"\n");
        }
        
        return strList;
    }

    /**
     * 运行任务
     * @param cmdString
     * @return 
     * @throws IOException 
     */
    public String RunShell(String cmdString) throws IOException {
        
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
            S_Debug.Write_DebugLog("Try_Times","S="+pRun_Session.ID+",Job=0,"+Try_Times);

            exitCode = process.waitFor();
            
            if (Run_Error && exitCode==0) exitCode=10000;

            if (exitCode>0){
                if (Run_Error){
                    S_Debug.Write_DebugLog("error"," Run_Error=true:\n"+
                            pRun_Session.ID+",0,"+Try_Times+"\n"+
                            cmdString);
                }else{
                    S_Debug.Write_DebugLog("error"," exitCode>0:" +
                            pRun_Session.ID+",0"+Try_Times+"\n"+
                            exitCode+"\n 有错误！0\n"+cmdString);
                }
            }else{
                S_Debug.Write_DebugLog("","运行成功！");
            }
        } catch (InterruptedException ex) {
            S_Debug.Write_DebugLog("error","Error 1:" + ex.toString());
        }
        return pSB_Output.toString();
    }

    private Thread Output_Thread(
            final BufferedReader in, final String threadName) {
        Thread copyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                
                try {
                    
                    StringBuilder pStr=new StringBuilder();
                    //Date pTime=S_Time.now();
                    pSB_Output.addLine(S_Time.now_YMD_Hms()+"\n");
                    String line;
                    while ((line = in.readLine()) != null){
                        if (line.startsWith("FAILED:")){
                            S_Debug.Write_DebugLog("error","startsWith FAILED::\n");
                            Run_Error=true;
                        }
                        
                        if (line.contains("FS.Run")){
                            pStr.append(line).append("\n");
                            S_Debug.Write_DebugLog("FS.Run",line);
                        }else{
                            S_Debug.Write_DebugLog("Command",line);
                        }
                        
                        pSB_Output.addLine(line+"\n");
                    }
                    line=pStr.toString();
                    if (!line.equals("")){
                        Pattern p = Pattern.compile("FS.Run\\((.*?)\\)");//
                        Matcher m = p.matcher(line);
                        while (m.find()){
                            int ID2=0;
                            line=m.group(1);
                            String[] strSplit=line.split(":");
                            if (strSplit.length>0) ID2=Integer.valueOf(strSplit[0]);

                            C_Run_Session_Min pRun_Session2 = C_Run_Session_Min.Get_New_Session(ID2,10);
                            S_Debug.Write_DebugLog("FS.Run","Session.ID="+pRun_Session2.ID);

                            if (strSplit.length>1) pRun_Session2.Param1=strSplit[1];
                            if (strSplit.length>2) pRun_Session2.Param2=strSplit[2];

                            pRun_Session2.Save_To_DB(pRun_Session.Time_Run);//把参数存储到数据库

                            pSB_Output.addLine(ID2+",pRun_Session2.Param,"+pRun_Session2.Param2+"\n");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                S_Debug.Write_DebugLog("error", ex.toString());
                            }
                        }
                        
                        
                        p = Pattern.compile("FS.Run.Program\\((.*?)\\)");//[\\d\\w:]+
                        m = p.matcher(line);
                        while (m.find()){
                            int ID2=0;
                            line=m.group(1);
                            String[] strSplit=line.split(":");
                            if (strSplit.length>0) ID2=Integer.valueOf(strSplit[0]);

                            C_Run_Session_Min pRun_Session2 = C_Run_Session_Min.Get_New_Session(ID2,10);
                            S_Debug.Write_DebugLog("FS.Run.Program","Session.ID="+pRun_Session2.ID);

                            
                            List<String> pListParam = new ArrayList<>();
                            for (int i=1;i<10;i++){
                                if (strSplit.length>i) pListParam.add(strSplit[i]);
                            }

                            pSB_Output.addLine(ID2+","+pListParam.toString()+"\n");
                        }
                    }
                    
                    S_Debug.Write_DebugLog("FS.Run.Function",line);
                    //如果不到60秒
                } catch (IOException e) {
                    e.printStackTrace();
                    S_Debug.Write_DebugLog("error","读取Job结果的错误输出流时发生异常: " + threadName
                        +e.toString());
                }
            }
        }, threadName);
        copyThread.setDaemon(true);
        copyThread.start();
        return copyThread;
    }

    
    private Thread Error_Thread(
            final BufferedReader in, final String threadName) {
        Thread copyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                
                int Try_Times=0;//重试次数
                int Function_Call=0;
//                if (pJob!=null){
//                    if (pJob.pRun!=null){
//                        Try_Times=pJob.pRun.Try_Times;
//                        Function_Call=pJob.pRun.Function_Call;
//                    }
//                }
                try {
                    Date pTime=S_Time.now();
                    pSB_Error.addLine(S_Time.now_YMD_Hms()+"\n");
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println("****************************\n"+line);
                        if (line.contains("FAILED:")){
                            S_Debug.Write_DebugLog("error"," startsWith FAILED::\n");
//                            Tools.Send_Msg_To_DingDing(S_Net.Proxy_IP_Watch,S_Net.Proxy_Port_Watch,"J="+pJob.ID +",S="+pJob.pRun_Session.ID+"<br>"+ line);
                            Run_Error=true;
                        }
                        if (line.contains("Cannot allocate memory")){
//                            Tools.Send_Msg_To_DingDing(S_Net.Proxy_IP_Watch,S_Net.Proxy_Port_Watch,"J="+pJob.ID +",S="+pJob.pRun_Session.ID+"<br>"+ line);
                            Run_Error=true;
                        }
                        if (line.contains("Exception in")){
//                            Tools.Send_Msg_To_DingDing(S_Net.Proxy_IP_Watch,S_Net.Proxy_Port_Watch,"J="+pJob.ID +",S="+pJob.pRun_Session.ID+"<br>"+ line);
                            Run_Error=true;
                        }
                        if (line.startsWith("Error ")){
//                            Tools.Send_Msg_To_DingDing(S_Net.Proxy_IP_Watch,S_Net.Proxy_Port_Watch,"J="+pJob.ID +",S="+pJob.pRun_Session.ID+"<br>"+ line);
                            Run_Error=true;
                        }
                        
                        if (line.contains("Starting Job = job_")){
                            S_Debug.Write_DebugLog("test",line);
                            Pattern p = Pattern.compile("Starting Job = (job_.*?), Tracking URL =");
                            Matcher m = p.matcher(line);
                            while (m.find()){
                                S_Debug.Write_DebugLog("test","matched");
//                                if (pJob.pRun!=null){
//                                    pJob.pRun.Job_ID = m.group(1);
//                                    S_Debug.Write_DebugLog("test","job_id"+pJob.pRun.Job_ID);
//                                    pJob.pRun.Save_Job_ID(pJob.pRun.Job_ID,"");
//                                }else{
//                                    S_Debug.Write_DebugLog("test","pJob.pMap_Run=null");
//                                }
                            }
                        }
                        
                        if (line.contains("MapReduce Total cumulative CPU time:")){
                            Pattern p = Pattern.compile("MapReduce Total cumulative CPU time: (\\d+) seconds.*");
                            Matcher m = p.matcher(line);
                            
//                            if (pJob.pRun!=null){
//                                Try_Times=pJob.pRun.Try_Times;
//                                Function_Call=pJob.pRun.Function_Call;
//                            }
                        
                            if (m.matches()){
                                CPU_Time+=Integer.valueOf(m.group(1));
//                                C_Log.Save_CPU(pRun_Session, pJob.ID,Function_Call,Try_Times,CPU_Time);
                            }else{
                                p = Pattern.compile("MapReduce Total cumulative CPU time: (\\d+) minutes (\\d+) seconds.*");
                                m = p.matcher(line);
                                if (m.matches()){
                                    CPU_Time+=60*Integer.valueOf(m.group(1))+Integer.valueOf(m.group(2));
//                                    C_Log.Save_CPU(pRun_Session, pJob.ID,Function_Call,Try_Times,CPU_Time);
                                }else{
                                    p = Pattern.compile("MapReduce Total cumulative CPU time: (\\d+) hours (\\d+) minutes (\\d+) seconds.*");
                                    m = p.matcher(line);
                                    if (m.matches()){
                                        CPU_Time+=60*60*Integer.valueOf(m.group(1))+60*Integer.valueOf(m.group(2))+Integer.valueOf(m.group(3));
//                                        C_Log.Save_CPU(pRun_Session, pJob.ID,Function_Call,Try_Times,CPU_Time);
                                    }else{
                                        p = Pattern.compile("MapReduce Total cumulative CPU time: (\\d+) days (\\d+) hours (\\d+) minutes (\\d+) seconds.*");
                                        m = p.matcher(line);
                                        if (m.matches()){
                                            CPU_Time+=24*60*60*Integer.valueOf(m.group(1))+60*60*Integer.valueOf(m.group(2))+60*Integer.valueOf(m.group(3))+Integer.valueOf(m.group(4));
//                                            C_Log.Save_CPU(pRun_Session, pJob.ID,Function_Call,Try_Times,CPU_Time);
                                        }
                                    }
                                }
                            }
                            //MapReduce Total cumulative CPU time: 0 days 22 hours 6 minutes 59 seconds
                        }
                        pSB_Error.addLine(line+"\n");
//                        if (pJob!=null){
//                            if (S_Time.now().getTime()/1000-pTime.getTime()/1000>6){
//                                pTime=S_Time.now();
//                                pJob.pRun.Save_Time();//状态
////                                C_Log.Save_Output(pRun_Session.ID, pJob.ID,Function_Call,Try_Times,true,"",pSB_Error.toString());
//                            }
//                        }
                    }
                    //如果不到60秒
//                    C_Log.Save_Output(pRun_Session.ID,pJob.ID,Function_Call,Try_Times,false,"",pSB_Output.toString());
//                    C_Log.Save_Output(pRun_Session.ID, pJob.ID,Function_Call,Try_Times,true,"",pSB_Error.toString());
                } catch (IOException e) {
                    S_Debug.Write_DebugLog("error","读取Job结果的错误输出流时发生异常: " + threadName 
                            +e.toString());
                } finally {
                }
            }
        }, threadName);
        copyThread.setDaemon(true);
        copyThread.start();
        return copyThread;
    }
}
