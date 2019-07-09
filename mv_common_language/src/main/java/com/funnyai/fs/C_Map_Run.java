package com.funnyai.fs;

import com.funnyai.common.AI_Var2;
import com.funnyai.net.Old.S_Net;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 运行状态和表 funny_map_run 对应
 * @author happyli
 */
public class C_Map_Run {
    
    private int Session_ID=0;
    private int Node_ID=0;
    public int Function_Call=0;
    //public String Param=""; 去掉，以后直接读取Session里参数
    
    public String Status="";//状态
    public int Continue_Next=0;
    public int iStep=0;
    public int Try_Times=0;
    
    public int Caller_Function_ID=0; //函数最后一个节点的ID
    public int Caller_Function_Call=0;//函数空间ID
    public int JobType=0;//=0 按天的，=1 按小时的
    public String Run_Key="";//唯一的key，用来尽量避免重复
    public String Job_ID="";//hadoop job id
    public int Machine_ID=0;//机器ID
    
    public C_Map_Run(C_Run_Session pSession,int ID,int mFunction_Call){
        Session_ID=pSession.ID;
        Node_ID=ID;
        Function_Call=mFunction_Call;
    }
    
    public void Read_Json(){
        String strURL= AI_Var2.Site
               +"/funnyscript/json_read_map_run.php?session="+this.Session_ID
                +"&id="+this.Node_ID+"&function_call="+this.Function_Call+"&machine="+AI_Var2.machine_id;
        
        String strJSON=S_Net.http_GET(strURL,"", "utf-8", "", 20);
        Tools.Write_DebugLog("map_run.read_json", "map_run:"+strJSON);
        
        int index=strJSON.indexOf("{");
        if (index>-1){
            strJSON=strJSON.substring(index);
            if (strJSON.length()>10){
                JSONObject pObj=new JSONObject(strJSON);
                try{
                    this.Continue_Next=pObj.getInt("Continue_Next");
                    this.iStep=pObj.getInt("Step");
                    this.Status=pObj.getString("Status");
                    this.Job_ID=pObj.getString("Job_ID");
                    this.Try_Times=pObj.getInt("Try_Times");//重试次数
                    this.Machine_ID=pObj.getInt("Run_Machine_ID");
                    this.Caller_Function_ID=pObj.getInt("Function_ID");//返回节点的ID
                    this.Caller_Function_Call=pObj.getInt("Return_Function_Call");//返回节点的函数空间
                }catch(JSONException e){
                    Tools.Write_DebugLog("json", "url:"+strURL+"\n"
                            + "length:"+strJSON.length()+",json:"+strJSON+"\n"+e.toString());
                }
            }else{
                Tools.Write_DebugLog("json", "url:"+strURL+"\n json:"+strJSON);
            }
        }else{
            System.out.println("read error, ID="+this.Function_Call+","+this.Session_ID+"&id="+this.Node_ID);
        }
    }
    
    /**
     * 保存状态
     * @param strStatus 状态
     */
    public void Save_Status(String strStatus) {
        Save_Status(strStatus,"");
    }
    
    
    /**
     * 保存状态
     * @param strJob_ID  Job ID hadoop job
     * @param strMemo
     */
    public void Save_Job_ID(String strJob_ID,String strMemo) {
        
        String strURL= AI_Var2.Site
               +"/funnyscript/save_job_id.php";
        String strData="";
        try {
            strData = "id="+URLEncoder.encode(this.Node_ID+"","utf-8")
                    +"&session="+URLEncoder.encode(this.Session_ID+"","utf-8")
                    +"&function_call="+URLEncoder.encode(this.Function_Call+"","utf-8")
                    +"&job_id="+URLEncoder.encode(strJob_ID,"utf-8")
                    +"&memo="+URLEncoder.encode(strMemo,"utf-8")
                    +"&step="+URLEncoder.encode(iStep+"","utf-8")
                    +"&jobtype="+URLEncoder.encode(this.JobType+"","utf-8")
                    +"&run_key="+URLEncoder.encode(this.Run_Key,"utf-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(C_Job.class.getName()).log(Level.SEVERE, null, ex);
        }

        String strContent=S_Net.http_post(strURL,strData);
        Tools.Write_DebugLog("Save_Job_ID",this.Function_Call+","+this.Session_ID+","+this.Node_ID+strContent);
    }
    
    
    public void Save_Command_Raw(String strCommand_Raw) {
        
        String strURL= AI_Var2.Site
               +"/funnyscript/save_map_run_command.php";
        String strData="";
        try {
            strData = "id="+URLEncoder.encode(this.Node_ID+"","utf-8")
                    +"&session="+URLEncoder.encode(this.Session_ID+"","utf-8")
                    +"&function_call="+URLEncoder.encode(this.Function_Call+"","utf-8")
                    +"&command="+URLEncoder.encode(strCommand_Raw,"utf-8")
                    +"&machine="+URLEncoder.encode(AI_Var2.machine_id+"","utf-8");
            this.Machine_ID=AI_Var2.machine_id;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(C_Job.class.getName()).log(Level.SEVERE, null, ex);
        }

        String strContent=S_Net.http_post(strURL,strData);
        Tools.Write_DebugLog("Save_Command",this.Function_Call+","+this.Session_ID+","+this.Node_ID+":"+AI_Var2.machine_id+"="+strContent);
    }
    
    
    /**
     * 保存状态
     * @param strStatus 状态
     * @param strMemo  备注
     */
    public void Save_Status(String strStatus,String strMemo) {
        
        String strURL= AI_Var2.Site
               +"/funnyscript/save_map_item_status.php";
        String strData="";
        try {
            strData = "id="+URLEncoder.encode(this.Node_ID+"","utf-8")
                    +"&session="+URLEncoder.encode(this.Session_ID+"","utf-8")
                    +"&function_call="+URLEncoder.encode(this.Function_Call+"","utf-8")
                    +"&status="+URLEncoder.encode(strStatus,"utf-8")
                    +"&memo="+URLEncoder.encode(strMemo,"utf-8")
                    +"&step="+URLEncoder.encode(iStep+"","utf-8")
                    +"&jobtype="+URLEncoder.encode(this.JobType+"","utf-8")
                    +"&run_key="+URLEncoder.encode(this.Run_Key,"utf-8");
        } catch (UnsupportedEncodingException ex) {
            //Logger.getLogger(C_Job.class.getName()).log(Level.SEVERE, null, ex);
            Tools.Write_DebugLog("Save_Status",ex.toString());
        }

        String strContent=S_Net.http_post(strURL,strData);
        Tools.Write_DebugLog("Save_Status",this.Function_Call+","+this.Session_ID+","+this.Node_ID+strContent);
    }
    
    
    /**
     * 错误的时候，把机器的Run_Machine设置为空
     */
    public void Save_Machine_None() {
        
        String strURL= AI_Var2.Site
               +"/funnyscript/save_map_run_machine_none.php";
        String strData="";
        try {
            strData = "id="+URLEncoder.encode(this.Node_ID+"","utf-8")
                    +"&session="+URLEncoder.encode(this.Session_ID+"","utf-8")
                    +"&function_call="+URLEncoder.encode(this.Function_Call+"","utf-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(C_Job.class.getName()).log(Level.SEVERE, null, ex);
        }

        String strContent=S_Net.http_post(strURL,strData);
        Tools.Write_DebugLog("Save_Machine_None",this.Function_Call+","+this.Session_ID+","+this.Node_ID+strContent);
    }
    
     
    /**
     * 存储返回函数ID
     * @param Return_Function_Call 回调节点的函数空间ID
     * @param Return_ID 回调节点的ID
     */
    public void Save_Function_ID(int Return_Function_Call,int Return_ID) {
        String strURL= AI_Var2.Site
               +"/funnyscript/save_map_item_function_id.php";
        String strData="";
        try {
            strData = "id="+URLEncoder.encode(this.Node_ID+"","utf-8")
                    +"&session="+URLEncoder.encode(this.Session_ID+"","utf-8")
                    +"&function_call="+URLEncoder.encode(this.Function_Call+"","utf-8")
                    +"&return_function_call="+URLEncoder.encode(Return_Function_Call+"","utf-8")
                    +"&return_id="+URLEncoder.encode(Return_ID+"","utf-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(C_Job.class.getName()).log(Level.SEVERE, null, ex);
        }

        String strContent=S_Net.http_post(strURL,strData);
        Tools.Write_DebugLog("save_map_item_function_id.php",this.Function_Call+","+this.Session_ID+","+this.Node_ID+strContent);
    }
    
    
    /**
     * 保存：更新时间
     */
    public void Save_Time() {
        
        String strURL= AI_Var2.Site
               +"/funnyscript/save_map_item_time.php";
        String strData="";
        try {
            strData = "id="+URLEncoder.encode(this.Node_ID+"","utf-8")
                    +"&session="+URLEncoder.encode(this.Session_ID+"","utf-8")
                    +"&function_call="+URLEncoder.encode(this.Function_Call+"","utf-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(C_Job.class.getName()).log(Level.SEVERE, null, ex);
        }

        String strContent=S_Net.http_post(strURL,strData);
        Tools.Write_DebugLog("save_time",this.Function_Call+","+this.Session_ID+","+this.Node_ID+strContent);
    }
    
    
    /**
     * 保存继续标志，这样下次运行这个节点的时候会自动检查后面的节点是否要激活
     * @param bContinue 
     */
    public void Save_Continue(boolean bContinue) {
        
        String strURL= AI_Var2.Site
               +"/funnyscript/save_map_item_continue.php";
        String strData="";
        try {
            int iContinue=0;
            if (bContinue){
                iContinue=1;
            }
            
            strData = "id="+URLEncoder.encode(this.Node_ID+"","utf-8")
                +"&session="+URLEncoder.encode(this.Session_ID+"","utf-8")
                +"&function_call="+URLEncoder.encode(this.Function_Call+"","utf-8")
                +"&value="+URLEncoder.encode(iContinue+"","utf-8");
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(C_Job.class.getName()).log(Level.SEVERE, null, ex);
        }

        String strContent=S_Net.http_post(strURL,strData);
        Tools.Write_DebugLog("Save_Continue",this.Function_Call+","+this.Session_ID+","+this.Node_ID+strContent);
    }
    
      
    /**
     * 保存重试次数
     */
    public void Save_Try_Times() {

        String strURL= AI_Var2.Site
            +"/funnyscript/save_map_item_try_times.php";
        String strData="";
        try {
          strData = "id="+URLEncoder.encode(this.Node_ID+"","utf-8")
              +"&session="+URLEncoder.encode(this.Session_ID+"","utf-8")
              +"&function_call="+URLEncoder.encode(this.Function_Call+"","utf-8");
        } catch (UnsupportedEncodingException ex) {
          Logger.getLogger(C_Job.class.getName()).log(Level.SEVERE, null, ex);
        }

        String strContent=S_Net.http_post(strURL,strData);
        Tools.Write_DebugLog("Save_Try_Times",this.Function_Call+","+this.Session_ID+","+this.Node_ID+strContent,false);
    }
    
    public String GetParam(int ID,int i) {
        
        String strURL= AI_Var2.Site
               +"/funnyscript/get_run_item_param.php";
        String strData="";
        try {
            strData = "id="+URLEncoder.encode(ID+"","utf-8")
                    +"&session="+URLEncoder.encode(this.Session_ID+"","utf-8")
                    +"&function_call="+URLEncoder.encode(this.Function_Call+"","utf-8")
                    +"&index="+URLEncoder.encode(i+"","utf-8");
            Tools.Write_DebugLog("GetParam",ID+","+this.Session_ID+","+this.Function_Call+","+i);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(C_Job.class.getName()).log(Level.SEVERE, null, ex);
        }

        String strContent=S_Net.http_post(strURL,strData);
        
        return strContent.trim();
    }   
}
