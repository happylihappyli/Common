
package com.funnyai.fs;

import com.funnyai.common.S_Debug;
import com.funnyai.language.Tools_String;
import com.funnyai.net.Old.S_Net;
import com.funnyai.string.Old.S_Strings;
import java.util.ArrayList;
import java.util.TreeMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 存储调度系统的数据
 * @author happyli
 */
public class C_Table extends C_Map_Item {
    public C_Run_Session pSession;

    public int D4=0;
    public String Color="";
    public boolean bActive=false;//代表激活与否，激活的话，意思就是这个表数据刚生成

    public String Database="";//hive库
    public TreeMap From_Nodes=new TreeMap();
    
    public String Get_Name(){
        String[] strSplit=Name.split("\\|");
        return strSplit[0];
    }
    
    public C_Table(int ID2,C_Run_Session pRun_Session,int Function_Call) {
        super(ID2,pRun_Session,Function_Call);
    }

    
    
    //获取其他信息
    public void Update_Info(C_Run_Session pRun_Session)
    {
        String strJSON=this.Read_Json2();
        int index=strJSON.indexOf("{");
        strJSON=strJSON.substring(index);
        try{
            JSONObject pObj=new JSONObject(strJSON);
//            this.Name=pObj.getString("Name");
            this.Color=pObj.getString("Color");
            this.D4=Integer.valueOf(pObj.getString("D4"));
            this.Program=pObj.getString("Program");
            this.Program_Param=pObj.getString("Program_Param");
            this.Database=pObj.getString("Database");
            
            if (pObj.getString("Active_Status").equals("1")){
                this.bActive=true;
                pRun.Save_Status("active");
            }
        }
        catch(JSONException | NumberFormatException ex){
            ex.printStackTrace();
            System.out.println("*****json error******");
            System.out.println("read error, ID=" + this.ID);
            System.out.println(strJSON);
            System.out.println("***********");
        }
        pRun.Read_Json();
    }

    public static void main(String[] args) {
        System.out.println("=========== test ===========");
        
        C_Run_Session pRun_Session=new C_Run_Session(0,100);
        C_Table pTable =  pRun_Session.Read_Table(0, 1703);
        System.out.println(pTable.Get_Program());
    }
    
    /**
     * 获取 Program
     * @return 
     */
    public String Get_Program(){
        String strReturn=this.Program;
        if ("template".equals(this.ItemType)){
            strReturn="";
            if (!"".equals(this.Get_From_IDs())){
                String[] strSplit=this.Get_From_IDs().split(",");
                for (int i=0;i<strSplit.length;i++){
                    int ID2=Integer.valueOf(strSplit[i]);
                    C_Table pTable = pRun_Session.Read_Table(0, ID2);
                    strReturn+=Tools_String.String_Format_Array(this.Program, pTable.Program, this.Program_Param);
                }
            }
        }else{
            if (!"".equals(this.Get_From_IDs())){
                String[] strSplit=this.Get_From_IDs().split(",");
                for (int i=0;i<strSplit.length;i++){
                    int ID2=Integer.valueOf(strSplit[i]);
                    C_Table pTable = pRun_Session.Read_Table(0, ID2);
                    String strValue=pTable.Get_Program();
                    strReturn=strReturn.replace("{"+i+"}", strValue);
                }
            }
        }
        return strReturn;
    }
    
    
    
    public void Run_NextTask(C_Run_Session pRun_Session){
        
        String strIDs=Get_To_IDs();
        S_Debug.Write_DebugLog("finished","id="+this.ID+" Get_To_IDs="+strIDs);
        String[] strSplit=strIDs.split(",");
        
        for(int i=0;i<strSplit.length;i++){
            int ID2=S_Strings.getIntFromStr(strSplit[i],0);// pIDS.getJSONObject(i).getInt("id");
            if (ID2==0) continue;
            C_Job pJob = pRun_Session.Read_Job(pRun.Function_Call,ID2);//获取task信息
            
            if (pJob.Active_Level==1){//必须所有的输入的表的输入任务激活才跑这个任务！
                S_Debug.Write_DebugLog("Run_NextTask"," Run_NextTask Active=1,ID="+pJob.ID);
                if (pJob.Check_Job_Active_Or_Prepare_Active_Level_1("")==false){
                    S_Debug.Write_DebugLog("Check_Job_Active_Or_Prepare_Active_Level_1"," not active:session="+this.pRun_Session.ID+",ID="+pJob.ID);
                }else{
                    if (!"running".equals(pJob.pRun.Status)){
                        if (pJob.pCommand!=null) pJob.pCommand.Stop();
                        pJob.pRun.iStep=pRun.iStep+1;
                        pJob.pRun.Save_Status("prepare_run");
                    }
                }
            }else{
                S_Debug.Write_DebugLog("Run_NextTask"," Run_NextTask Active<>1,ID="+pJob.ID);
                if (pJob.Check_Job_Active_Or_Prepare("",true)==false){
                    S_Debug.Write_DebugLog("Run_NextTask","not active:"+ID2);
                }else{
                    if (!"running".equals(pJob.pRun.Status)){
                        if (pJob.pCommand!=null) pJob.pCommand.Stop();
                        pJob.pRun.iStep=pRun.iStep+1;
                        pJob.pRun.Save_Status("prepare_run");
                    }
                }
            }
        }
        
        pRun.Read_Json();
        if (pRun.Caller_Function_ID>0){//如果当前是子函数的最后节点，返回到上级运行节点 
            S_Debug.Write_DebugLog("function","子函数，返回上级函数 step1");
            C_Job pJob_Return=(C_Job) pRun_Session.Read_Job(pRun.Caller_Function_Call,pRun.Caller_Function_ID);// this.pTreap_Return.find(new C_K_Long(pRun_Session.ID));
            if (pJob_Return!=null){
                S_Debug.Write_DebugLog("function","子函数，返回上级函数 step2");
                JSONObject pObj2=new JSONObject(pJob_Return.Program);
                String strOutput=pObj2.getString("output");//output里的表都要激活才能返回
                
                if (pJob_Return.Check_Job_Active_Or_Prepare(strOutput,true)==false){
                    System.out.println("not active:"+this.ID);
                }else{
                    pJob_Return.pRun.iStep=pRun.iStep+1;
                    pJob_Return.pRun.Save_Status("finished");
                    pJob_Return.bActive=true;
                    pJob_Return.Continue_Run_Next(pRun_Session);
                }
            }
        }
    }


    /**
     * 预激活
     * @param pRun_Session 
     * @param iStep  上级Job的iStep
     * @param iCheckNext 是否继续检查下一个
     * @param Zero_Save_Continue
     */
    public void Check_Next_Task(C_Run_Session pRun_Session,int iStep,int iCheckNext,boolean Zero_Save_Continue) {
        S_Debug.Write_DebugLog("prepare","id="+this.ID+" step="+iStep);
        iStep+=1;
        pRun.iStep=iStep;
        pRun.Save_Status("prepare");

        String strIDs=Get_To_IDs();
        S_Debug.Write_DebugLog("finished","id="+this.ID+" Get_To_IDs="+strIDs);
        String[] strSplit=strIDs.split(",");
        
        for(int i=0;i<strSplit.length;i++){
            int ID2=S_Strings.getIntFromStr(strSplit[i],0);//pIDS.getJSONObject(i).getInt("id");
            if (ID2==0) continue;
            C_Job pJob = pRun_Session.Read_Job(pRun.Function_Call,ID2);//获取task信息
            
            if (this.pRun_Session.Bat==1){
                if (pJob.Check_Job_Active_Or_Prepare("",false)==false){
                    S_Debug.Write_DebugLog("prepare","not active:"+ID2);
                }else{
                    if (pJob.batOnly==1){
                        pJob.pRun.Save_Status("prepare");
                        pJob.Check_Next_Task(pRun_Session,iStep+1,iCheckNext,Zero_Save_Continue,true);
                    }else{
                        S_Debug.Write_DebugLog("prepare","not active:"+ID2);
                    }
                }
            }else{
                if (pJob.Check_Job_Active_Or_Prepare("",false)==false){
                    S_Debug.Write_DebugLog("prepare","not active:"+ID2);
                }else{
                    pJob.pRun.Save_Status("prepare");
                    pJob.Check_Next_Task(pRun_Session,iStep+1,iCheckNext,Zero_Save_Continue,true);
                }
            }
        }
        
        pRun.Read_Json(); //int Function_ID=Get_Function_ID(pRun_Session);
        if (pRun.Caller_Function_ID>0){//如果当前是子函数的最后节点，返回到上级运行节点 
            C_Job pJob_Return=(C_Job) pRun_Session.Read_Job(pRun.Caller_Function_Call,pRun.Caller_Function_ID);
            if (pJob_Return!=null){
                JSONObject pObj2=new JSONObject(pJob_Return.Program);
                String strOutput=pObj2.getString("output");//output里的表都要激活才能返回
                
                if (pJob_Return.Check_Job_Active_Or_Prepare(strOutput,false)==false){
                    System.out.println("not active:"+this.ID);
                }else{
                    S_Debug.Write_DebugLog("function","子函数，返回上级函数 step2");
                    pJob_Return.Check_Next_Task(pRun_Session,iStep+1,iCheckNext,Zero_Save_Continue,false);
                }
            }
        }
    }

}
