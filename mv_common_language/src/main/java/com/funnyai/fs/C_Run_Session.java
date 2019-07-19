
package com.funnyai.fs;

import com.funnyai.common.AI_Var2;
import com.funnyai.common.C_Run_Session_Min;
import com.funnyai.common.S_Debug;
import com.funnyai.net.Old.S_Net;
import com.funnyai.string.Old.S_Strings;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.TreeMap;
import org.json.JSONObject;

/**
 * 每次调用会启用一个Session，这样就保证了函数被多个调用也没有问题。
 * @author happyli
 */
public class C_Run_Session extends C_Run_Session_Min {
//    public int ID = 0; //Session 的ID，从数据库中读取，也是job的实例的Session_ID
//    public int RunJob_Count=0;//这个Session里正在运行的Job数。
//    public boolean bAlone = false;//bAlone=true 单独运行
//    public int Start_ID = 0 ;//开始运行的节点，这个节点不需要判断是否激活
//    
//    public String Param1="";//运行shell任务的参数
//    public String Param2="";
//    
//    public int Bat=0;//=1代表只运行批量的task
//    public int Creator=0;//如果是批量任务，这里就是批量任务的创建者
//    
//    public TreeMap pTables=new TreeMap();//用到的表
//    public TreeMap pJobs=new TreeMap();//用到的程序节点
//
//    public String Time_Run="";//运行的相对时间
//    public String DB="";//数据库，默认空
//    public String Run_Type="default";//time bat default 运行的队列
//    public int Continue=0;
//    public int Upload_ID=0;
//    
    public C_Run_Session(int ID,int priority){
        super(ID,priority);
    }
//        String strURL= AI_Var2.Site
//               +"/funnyscript/json_get_map_session.php?id="+ID+"&priority="+priority+"&ip="+S_Strings.URL_Encode(AI_Var2.local_ip);
//        String strJSON=S_Net.http_GET(strURL,"", "utf-8", "",20);
//        
//        S_Debug.Write_DebugLog("Session.C_Run_Session",ID+"\n"+strJSON);
//        int index=strJSON.indexOf("{");
//        strJSON=strJSON.substring(index);
//        JSONObject pObj=new JSONObject(strJSON);
//        
//        this.ID=pObj.getInt("id");
//        if (pObj.has("Start_ID")){
//            this.Start_ID=pObj.getInt("Start_ID");
//        }
//        this.Run_Type=pObj.getString("Run_Type");
//        this.Continue=pObj.getInt("Continue");
//        this.Bat=pObj.getInt("Bat");
//
//        this.DB=pObj.getString("DB");
//        this.Creator=S_Strings.getIntFromStr(pObj.getString("Creator"),0);
//        if (pObj.has("Upload_ID")){
//            this.Upload_ID=S_Strings.getIntFromStr(pObj.getString("Upload_ID"),0);
//        }
//    }
//    
//    
//    /**
//     * 保存Session的时间,各种参数
//     * @param strTime 相对时间,这个参数没有用了
//     */
//    public void Save_To_DB(String strTime) {
//        this.Time_Run=strTime;
//        String strURL= AI_Var2.Site
//               +"/funnyscript/save_session_time.php";
//        String strData="";
//        try {
//            strData = "id="+URLEncoder.encode(this.ID+"","utf-8")
//                    +"&time="+URLEncoder.encode(this.Time_Run,"utf-8")
//                    +"&start="+URLEncoder.encode(this.Start_ID+"","utf-8")
//                    +"&param="+URLEncoder.encode(this.Param1,"utf-8")
//                    +"&param2="+URLEncoder.encode(this.Param2,"utf-8")
//                    +"&bat="+URLEncoder.encode(this.Bat+"","utf-8")
//                    +"&creator="+URLEncoder.encode(this.Creator+"","utf-8")
//                    +"&continue="+URLEncoder.encode(this.Continue+"","utf-8")
//                    +"&run_type="+URLEncoder.encode(this.Run_Type+"","utf-8")
//                    +"&Upload_ID="+URLEncoder.encode(this.Upload_ID+"","utf-8")
//                    +"&DB="+URLEncoder.encode(this.DB+"","utf-8");
//            S_Debug.Write_DebugLog("Session.Save_To_DB",strURL);
//            S_Debug.Write_DebugLog("Session.Save_To_DB",strData);
//            S_Net.http_post(strURL,strData);//, "utf-8","", false);
//        } catch (UnsupportedEncodingException ex) {
//            S_Debug.Write_DebugLog("Save_To_DB",ex.toString());
//        }
//
//    }
//    
//    /** 
//     */
//    public void Read_Json(){
//        
//        String strURL= AI_Var2.Site
//               +"/funnyscript/json_read_session.php?id="+this.ID;
//        String strData="";
//        String strJSON=S_Net.http_GET(strURL,strData, "utf-8", "",20);// false);
//        int index=strJSON.indexOf("{");
//        if (index>-1){
//            strJSON=strJSON.substring(index);
//            try{
//                JSONObject pObj=new JSONObject(strJSON);
//                if (pObj.has("Time_Run")){
//                    if (pObj.getString("Time_Run").equals("0000-00-00 00:00:00")){
//
//                    }else{
//                        this.Time_Run=pObj.getString("Time_Run");
//                    }
//                }
//                this.Param1=pObj.getString("Param");
//                this.Param2=pObj.getString("Param2");
//                this.Bat=pObj.getInt("Bat");
//                this.Run_Type=pObj.getString("Run_Type");
//            }catch(Exception ex){
//                S_Debug.Write_DebugLog("Session.Read_JSON",strURL);
//                S_Debug.Write_DebugLog("Session.Read_JSON",strJSON);
//                S_Debug.Write_DebugLog("Session.Read_JSON",ex.toString());
//                System.out.println(strJSON);
//                ex.printStackTrace();
//            }
//        }else{
//            System.out.println("read error, ID="+this.ID);
//        }
//    }
    
    /*
    *读取表格
    */
    public C_Table Read_Table(int Function_Call,int ID2)
    {
        C_Table pTable;
        if (pTables.containsKey(Function_Call+","+ID2)){
            pTable=(C_Table) pTables.get(Function_Call+","+ID2);
        }else{
            pTable=new C_Table(ID2,this,Function_Call);
            pTable.pSession=this;
            pTables.put(Function_Call+","+ID2, pTable);
        }
        pTable.Update_Info(this);
        return pTable;
    }
    
    /*
    * 读取Job
    */
    public C_Job Read_Job(int Function_Call,int Job_ID)//,String strParam)
    {
        C_Job pJob;
        if (pJobs.containsKey(Function_Call+","+Job_ID)){
            pJob =(C_Job) pJobs.get(Function_Call+","+Job_ID);
        }else{
            pJob=new C_Job(Job_ID,this,Function_Call);
            pJob.pRun_Session=this;
            pJobs.put(Function_Call+","+Job_ID, pJob);
        }
        if (Job_ID>0){
            boolean bError=pJob.Update_Info();
            if (bError){//如果有错误再读,第2次
                bError=pJob.Update_Info();
                if (bError){//如果有错误再读没，第3次
                    pJob.Update_Info();
                }
            }
            pJob.pRun.Read_Json();
        }
        return pJob;
    }

    public static C_Run_Session Get_New_Session_From_ID(int ID,int priority){
        
        C_Run_Session pRun_Session;
        
        if (AI_Var2.pRun_Sessions.containsKey(ID)){
            pRun_Session=new C_Run_Session(ID,priority);
            AI_Var2.pRun_Sessions.put(pRun_Session.ID,pRun_Session);
        }else{
            pRun_Session=(C_Run_Session) AI_Var2.pRun_Sessions.get(ID);
        }
        return pRun_Session;
    }
    
    public static C_Run_Session Get_New_Session(int ID,int priority){
        C_Run_Session pRun_Session;
        pRun_Session=new C_Run_Session(0,priority);
        pRun_Session.Start_ID=ID;
        AI_Var2.pRun_Sessions.put(pRun_Session.ID,pRun_Session);
        return pRun_Session;
    }
    
}
