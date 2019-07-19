package com.funnyai.fs;

import com.funnyai.io.Old.S_File;
import com.funnyai.io.C_File;
import com.funnyai.Time.Old.S_Time;
import com.funnyai.common.AI_Var2;
import com.funnyai.common.S_Command;
import com.funnyai.common.S_Debug;
import com.funnyai.data.C_K_Int;
import com.funnyai.data.C_K_Str;
import com.funnyai.math.S_math;
import com.funnyai.net.Old.S_Net;
import com.funnyai.netso.C_Session_AI;
import com.funnyai.string.Old.S_Strings;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.logging.*;
import java.util.regex.*;
import org.json.*;

/**
 * 一个任务，比如一次Hive或Mapreduce或一个shell
 * @author happyli
 */
public class C_Job extends C_Map_Item{
    
    

    public int D4=0;
    public String Color="";
    public boolean bActive=false;//是否跑过，跑过为True
    public boolean bRecover=false;//是否是恢复的重新跑的任务
    public String Run_YMD_HM="";//最近任务运行的x月x日 x小时x分，这样一分钟内任务就不会重复跑
     
    
    private boolean Error_Continue;
    
    public int Template_ID=0;
    public int Retry_Times=0;//重试次数
    public int No_AutoRun=0;//是否调度 =1不调度，=0 自动调度
    public C_Command pCommand=null;
    public String Type_Run="";//默认是空，如果是表的hive查询，这里为 Type=hive
    public int batOnly=0;//=1代表只运行批量的task
    public String Check_SQL="";//hive检查sql
    public Integer Active_Level=0;
    public String Program_Output="";
    public int Call_Cite_Count=0;
    public boolean bSend_QQ=false;
    public String QQ="";
    public String Package_ID="0";
    
    public C_Job(int ID2,C_Run_Session pRun_Session,int Function_Call){
        super(ID2,pRun_Session,Function_Call);
    }
    
    /**
     * 必须所有的输入都激活才跑这个任务！
     * @param strIDs
     * @return 
     */
    boolean Check_Job_Active_Or_Prepare_Active_Level_1(String strIDs) {

        if (strIDs.equals("")){
            strIDs=this.Get_From_IDs();//读取输入节点
        }
        boolean bActive2=true;
        String[] strSplit=strIDs.split(",");
        C_Table pTable;
        for (String strSplit1 : strSplit) {
            int ID2 = Integer.valueOf(strSplit1);
            pTable=pRun_Session.Read_Table(pRun.Function_Call,ID2);//把静态激活的节点读取到funny_map_run表里

            String strURL= AI_Var2.Site
                    +"/funnyscript/json_get_item_from_output_id.php?id="+pTable.ID;
            String strJSON=S_Net.http_GET(strURL,"", "utf-8","",20);
            int index=strJSON.indexOf("{");
            if (index>-1){
                
                strJSON=strJSON.substring(index);
                JSONObject pObj=new JSONObject(strJSON);
                JSONArray pIDS=pObj.getJSONArray("data");

                for(int i=0;i<pIDS.length();i++){
                    int ID3=pIDS.getJSONObject(i).getInt("id");
                    C_Job pJob_From=pRun_Session.Read_Job(pRun.Function_Call,ID3);
                    if (pJob_From.pRun.Status.equals("finished")){
                    }else if (pJob_From.pRun.Status.equals("remove")){
                        this.pRun.Save_Status("remove");
                    }else{
                        S_Debug.Write_DebugLog("Check_Job_Active_Or_Prepare_Active_Level_1",
                                " not active:session="+this.pRun_Session.ID
                                        +",table="+ID2+",job.ID="+pJob_From.ID+" job.Name="+pJob_From.Name);
                        bActive2=false;
                    }
                }
            }else{
                S_Debug.Write_DebugLog("Check_Job_Active_Or_Prepare_Active_Level_1",
                                " json.error json="+strJSON);
            }
        }
        return bActive2;
    }
    
    /**
     * 
     * @param strIDs
     * @param Type_Active =false 代表是预激活模式
     * @return 
     */
    boolean Check_Job_Active_Or_Prepare(String strIDs,boolean Type_Active) {
        //必须所有的输入都激活才跑这个任务！
        if (strIDs.equals("")){
            strIDs=this.Get_From_IDs();
        }
        String[] strSplit=strIDs.split(",");
        for (String strSplit1 : strSplit) {
            int ID2 = S_Strings.getIntFromStr(strSplit1,0);
            if (ID2>0){
                pRun_Session.Read_Table(pRun.Function_Call,ID2);//把静态激活的节点读取到funny_map_run表里
            }
        }
        
        String strURL= AI_Var2.Site
               +"/funnyscript/json_check_item_active.php?ids="+strIDs+"&session="+pRun_Session.ID;
        if (Type_Active==false){
            strURL= AI_Var2.Site
               +"/funnyscript/json_check_item_prepare.php?ids="+strIDs+"&session="+pRun_Session.ID;
        }
        
        String strJSON=S_Net.http_GET(strURL,"", "utf-8", "",20);
        int index=strJSON.indexOf("{");
        strJSON=strJSON.substring(index);
        
        JSONObject pObj=new JSONObject(strJSON);
        String strStatus=pObj.getString("Status");
        
        if (strStatus.equals("0")){
            S_Debug.Write_DebugLog("json_check_item_active_prepare",S_Time.now_HH_mm()+"\n"+strURL);
        }
        
        return "1".equals(strStatus);
    }
    
    /**
     * 判断是否满足运行条件，如果满足就运行!
     * alone 是否单独运行，如果单独运行(alone=true)则一个节点运行完毕不会运行下一个。
     * 必须依赖的表都已经是绿色。
     * @param pRun_Session
     * @param bActive  是否已经激活，已经激活的，比如未完成的节点，就不在判断它的输入是否已激活
     * @param Start_From 从哪里启动,比如 bat,json_get_map_task_prepare_run
    */
    public void Run(C_Run_Session pRun_Session,boolean bActive,String Start_From){
        pRun.Read_Json();
        
        pRun.Save_Status("start_running",Start_From);
        
        if ("remove".equals(pRun.Status)){//如果状态为remove，就不运行了！
            Remove_Process();
            return ;
        }
        
        S_Debug.Write_DebugLog("Try_Times","s="+this.pRun_Session.ID+",ID="+this.ID+":"+pRun.Try_Times+","+this.Retry_Times);
        if (pRun.Try_Times>=this.Retry_Times){
            Remove_Process();
            S_Debug.Write_DebugLog("Try_Times","s="+this.pRun_Session.ID+",ID="+this.ID+":重试超过"+this.Retry_Times);
            return ;
        }
        
        if (this.Active_Level==1){//必须所有的输入的表的输入任务激活才跑这个任务！
            S_Debug.Write_DebugLog("Run","Run step1 Active=1,ID="+this.ID);
            if (this.Check_Job_Active_Or_Prepare_Active_Level_1("")==false){
                S_Debug.Write_DebugLog("Active_Level_1","not active:session="+this.pRun_Session.ID+",ID="+this.ID);
                return ;
            }else{
                if (!"running".equals(this.pRun.Status)){
                    if (this.pCommand!=null) this.pCommand.Stop();
                    this.pRun.Save_Status("prepare_run");            
                }
            }
        }
        
        if (pRun.Continue_Next==1){//如果Continue_Next==1 就激活后面的一层节点
            this.Check_Next_Task(pRun_Session,pRun.iStep, 1,true,true);
            pRun.Save_Continue(false);
        }
        this.Update_Info();//重新读取数据库内容
        if (this.No_AutoRun==1){
            S_Debug.Write_DebugLog("","!!! 设置过不调度 ID="+ID);
            pRun.Save_Status("设置过不调度");
            return ;//如果设置过不调度，则不自动运行
        }

        int Function_Call=0;
        if (this.pRun!=null){
            Function_Call=this.pRun.Function_Call;
        }
        boolean bRun=true;
        String strError="";
        if (bActive){
            S_Debug.Write_DebugLog(""," bActive=true, ID="+ID);
        }else if (pRun_Session.bAlone){// 如果是单独运行，这个节点的状态就是激活的
            S_Debug.Write_DebugLog("","!!! 单独任务启动 ID="+ID);
        }else if (pRun_Session.Start_ID == ID){
            S_Debug.Write_DebugLog("","!!! 第一个节点启动 ID="+ID);
        }else{
            if (pRun_Session.Bat==1){  //如果是跑批量任务
                S_Debug.Write_DebugLog("","准备跑批量任务 ID="+ID);
            }else{  //如果不是跑批量任务
                S_Debug.Write_DebugLog("","如果不是跑批量任务 ID="+ID);
                if (this.bActive && S_Time.now_YMD_HM().equals(this.Run_YMD_HM)){
                    S_Debug.Write_DebugLog("","!!! 如果跑过了且是同1分钟，则不再运行 ID="+ID);
                    return ;
                }
            }
            
            if (this.Active_Level==1){//必须所有的输入的表的输入任务激活才跑这个任务！
                S_Debug.Write_DebugLog("Run",S_Time.now_HH_mm()
                        + "Run Step2 Active=1,ID="+this.ID);
                if (Check_Job_Active_Or_Prepare_Active_Level_1("")==false){
                    S_Debug.Write_DebugLog("Check_Job_Active_Or_Prepare_Active_Level_1","not active,level=1:"+this.ID);
                    bRun=false;
                }
            }else{//必须所有的输入都激活才跑这个任务！
                if (Check_Job_Active_Or_Prepare("",true)==false){
                    System.out.println("not active:"+this.ID);
                    bRun=false;
                }
            }
        }
        
        if (pRun_Session.Bat==1){//如果只跑批量的
            if (this.batOnly!=1){//不是批量的就不跑
                bRun=false;
                strError = strError + ";当前任务不是批量任务";
                pRun.Save_Status("不是批量任务");//子函数不需要恢复！
                
                //this.pCommand.pSB_Error.Save_FileName(pRun_Session.ID,this.ID,Function_Call,this.pMap_Run.Try_Times);//,true, pRun_Session.Param, strError);
                S_Debug.Write_DebugLog("error",strError);
            }else{
                S_Debug.Write_DebugLog("","准备跑批量任务");
            }
        }

        if (bRun){//如果所有的依赖表都已经激活,则开始运行
            Run_Sub1(Function_Call,this.pRun.Try_Times,Start_From);//,strParam
        }
    }
    
    public void Run_Sub1(int Function_Call,int Try_Times,String Start_From){
        this.bActive=true;
        this.Run_YMD_HM=S_Time.now_YMD_HM();
        S_Debug.Write_DebugLog("","准备运行Task="+ID);
//        C_Log.Save_Output(pRun_Session.ID,this.ID,Function_Call,Try_Times,false,
//                pRun_Session.Param,"session:"+this.pRun_Session.ID);

        if (this.ItemType!=null && this.ItemType.toLowerCase().equals("function")){
            Run_Sub1_Function(Function_Call,Try_Times,Start_From);
        }else{
            Run_Sub1_Main(Function_Call,Try_Times,Start_From);
        }
    }
    
    public void Remove_Process(){
        
        if (AI_Var2.machine_id!=this.pRun.Machine_ID){
            return ;
        }
        
        pRun.Save_Status("remove","重试超过"+this.Retry_Times);
            
        String strFile_Node=AI_Var2.Path_Shell+"node_"+ID+".sh";
        if (S_File.Exists(strFile_Node)==false){
            String strContent=Tools.File_Save(ID,strFile_Node,true);
            S_Debug.Write_DebugLog("save", strContent);
        }
        String strFile=AI_Var2.Path_Shell+"t_"+pRun.Function_Call+"_"+pRun_Session.ID+"_"+ID+".sh";
        //输入节点替换
        S_Debug.Write_DebugLog("job_id","file="+strFile);
        S_Debug.Write_DebugLog("job_id","jid="+this.pRun.Job_ID);
        if (!"".equals(this.pRun.Job_ID)){
            try {
                S_Command.RunShell_Return2("hadoop job -kill "+this.pRun.Job_ID);
            } catch (Exception ex) {
                Logger.getLogger(C_Job.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            S_Command.RunShell_Return2("ps aux|grep "+strFile+"|grep -v grep|awk '{ print $2 }'|xargs kill -9");
        } catch (Exception ex) {
            Logger.getLogger(C_Job.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 
     * @param Function_Call
     * @param Try_Times
     * @param Start_From 
     */
    public void Run_Sub1_Main(int Function_Call,int Try_Times,String Start_From){
        
        String strFile_Node=AI_Var2.Path_Shell+"node_"+ID+".sh";
        if (S_File.Exists(strFile_Node)==false){
            String strContent=Tools.File_Save(ID,strFile_Node,true);
            S_Debug.Write_DebugLog("save", strContent);
        }
        String strFile=AI_Var2.Path_Shell+"t_"+pRun.Function_Call+"_"+pRun_Session.ID+"_"+ID+".sh";
        C_File pFile = S_File.Write_Begin(strFile, false,"");
        //输入节点替换
        String strContent="";
        S_Debug.Write_DebugLog("job_id","file="+strFile);
        S_Debug.Write_DebugLog("job_id","jid="+this.pRun.Job_ID);
        Remove_Process();
        String [] strSplit=this.Get_From_IDs().split(",");
        for (int i=0;i<strSplit.length;i++)
        {
            if (strSplit[i].equals("")) continue;
            if (S_math.isNumeric(strSplit[i])){
                int ID2=Integer.valueOf(strSplit[i]);
                C_Table pTable2=pRun_Session.Read_Table(pRun.Function_Call,ID2);

                strContent+="sys_input_"+(i+1)+"=$(read_value "+pTable2.ID+" 'value')\n"
                        + "echo $sys_input_"+(i+1)+"\n";
            }
        }

        if (pRun_Session.Run_Type.equals("")){
            pRun_Session.Run_Type="default";
        }

        if (pRun.Function_Call>0){ //替换函数变量
            C_Map_Function pFunction=new C_Map_Function(pRun_Session,pRun.Function_Call);
            pFunction.Read_Json();
            strContent = "sys_session_id="+pRun_Session.ID+"\n"
                    +"sys_run_type=\""+pRun_Session.Run_Type+"\"\n"
                    +"sys_upload_id=\""+pRun_Session.Upload_ID+"\"\n"
                    +"sys_creator=\""+pRun_Session.Creator+"\"\n"
                    +"sys_id=\""+pRun.Function_Call+"\"\n"
                    +"sys_ip=\""+AI_Var2.local_ip+"\"\n"
                    +"sys_param1=\""+pRun_Session.Param1+"\"\n"
                    +"sys_param2=\""+pRun_Session.Param2+"\"\n"
                    +"sys_var1=\""+pFunction.Var1+"\"\n"
                    +"sys_var2=\""+pFunction.Var2+"\"\n"
                    +"sys_var3=\""+pFunction.Var3+"\"\n"
                    +"sys_db=\""+pRun_Session.DB+"\"\n"
                    +"source "+AI_Var2.Path_Shell+"lib/library.sh\n"
                    +strContent;
        }else{
            strContent = "sys_session_id="+pRun_Session.ID+"\n"
                +"sys_run_type=\""+pRun_Session.Run_Type+"\"\n"
                +"sys_upload_id=\""+pRun_Session.Upload_ID+"\"\n"
                +"sys_creator=\""+pRun_Session.Creator+"\"\n"
                +"sys_id=\"0\"\n"
                +"sys_ip=\""+AI_Var2.local_ip+"\"\n"
                +"sys_param1=\""+pRun_Session.Param1+"\"\n"
                +"sys_param2=\""+pRun_Session.Param2+"\"\n"
                +"sys_var1=\"\"\n"
                +"sys_var2=\"\"\n"
                +"sys_var3=\"\"\n"
                +"sys_db=\""+pRun_Session.DB+"\"\n"
                +"source "+AI_Var2.Path_Shell+"lib/library.sh\n"
                +strContent;
        }
        
        String strCommand="source";
        if ("expect".equals(this.ItemType)){
            strCommand="expect";
        }

        if ("".equals(pRun_Session.Time_Run)){
            strContent+=strCommand+" "+AI_Var2.Path_Shell+"node_"+ID+".sh "+pRun_Session.Param1+" ''";
        }else{
            if ("".equals(pRun_Session.Param1)){
                strContent+=strCommand+" "+AI_Var2.Path_Shell+"node_"+ID+".sh '"+pRun_Session.Param1+"' '"+pRun_Session.Time_Run+"'";
            }else{
                strContent+=strCommand+" "+AI_Var2.Path_Shell+"node_"+ID+".sh "+pRun_Session.Param1+" '"+pRun_Session.Time_Run+"'";
            }
        }

        S_Debug.Write_DebugLog("",strContent);
        S_File.Write_Line(pFile, strContent);
        pFile.Close();
        try {
            S_Command.RunShell_Return2("chmod 777 " + strFile);
        } catch (Exception ex) {
            Logger.getLogger(C_Job.class.getName()).log(Level.SEVERE, null, ex);
        }

        pRun_Session.RunJob_Count+=1;
        S_Debug.Write_DebugLog("run",pRun_Session.ID+","+ID+","+pRun.Try_Times+","+Start_From);

        if (this.ItemType.equals("fs.switch")){// || this.ItemType.equals("fs.check")){
            strContent= this.GetProgram(pRun_Session,true);
            strContent=strContent.replace("\r","\n");
            strContent=strContent.replace("\n\n","\n");
            strSplit=strContent.split("\n");

            int Run_ID=0;
            String strFunction="";
            if (strSplit.length>1){
                String strTmp=strSplit[0];
                if (strTmp.startsWith("fs=")){
                    strTmp=strTmp.substring(3);
                }
                Run_ID=S_Strings.getIntFromStr(strTmp,0);
                if (Run_ID==0){
                    strFunction=strTmp;
                }
            }
            strContent="fs.run id=" + Run_ID;
            S_Debug.Write_DebugLog("fs.switch","FS.Run="+Run_ID);
            String strLog=strContent+"\n";
            
            String strProgram="";
            if (Run_ID == 0) {
                strProgram="<item id=\"1\">\n" +
"<input>\n" +
"<count>3</count>\n" +
"<v1 param=\"\">{@0}</v1>\n" +
"<v1 param=\"\">{@1}</v1>\n" +
"<v1 param=\"\">{@2}</v1>\n" +
"</input>\n" +
"<fun>"+strFunction+"</fun>\n" +
"<output id=\"1\"></output>\n" +
"</item>\n";
            }
            C_Run_Session pRun_Session2=(C_Run_Session) C_Run_Session.Get_New_Session(Run_ID,0);
            pRun_Session2.Param1="";//strParam;
            pRun_Session2.Start_ID=Run_ID;
            pRun_Session2.Save_To_DB(S_Time.now_YMD_Hms());
            C_Session_AI pSession=new C_Session_AI();
            List<String> pList=new ArrayList<>();
            for  (int i=1;i<strSplit.length;i++){
                pList.add(strSplit[i]);
            }
            strContent=this.Run_Program(pSession,null,pRun_Session2,0,Run_ID,strProgram,null,pList);
            strLog+=strContent+"\n";
            S_Debug.Write_DebugLog("fs.switch","fs.switch="+strContent);
            int iSwitch=S_Strings.getIntFromStr(strContent,0);

            strSplit=this.Get_To_IDs().split(",");
            if (iSwitch>=0 && iSwitch<strSplit.length){
                int ID2 = S_Strings.getIntFromStr(strSplit[iSwitch],0);
                if (ID2>0){
                    C_Table pTable=pRun_Session.Read_Table(pRun.Function_Call,ID2);
                    pTable.From_Nodes.put(this.ID,this);
                    pTable.bActive=true;
                    pTable.pRun.iStep=pRun.iStep;
                    pTable.pRun.Save_Status("active");//设置表的状态为激活
                    pTable.Run_NextTask(pRun_Session);//跑表格有关的下一个任务
                }
            }
            
            pRun.Save_Status("finished");//状态
        }else{
            pRun.Save_Status("running",Start_From);
            pRun.Save_Try_Times();
            S_Debug.Write_DebugLog("shell_command",pRun_Session.ID+","+ID+","+strFile);
            strContent=this.Run_Shell_Command(true,pRun_Session,strFile,"");
        }
        S_Debug.Write_DebugLog("",strContent);
        
        
    }
    
    
    
    /**
     * 
     * @param bOut 是否调用外部执行器
     * @param pRun_Session
     * @param strContent
     * @param Encode
     * @return 
     */
    public String Run_Shell_Command(
            boolean bOut,
            C_Run_Session pRun_Session,
            String strContent,String Encode)
    {
//        if (bOut){
//            return "";
//        }else{
        
            C_Command pCommand2= new C_Command(pRun_Session,this.ID);
            pCommand2.pJob=this;
            this.pCommand=pCommand2;
            pCommand2.strCommand=strContent;
            pCommand2.Param=pRun_Session.Param1;
            pCommand2.pRun_Session=pRun_Session;
            pCommand.pSB_Output.Save_FileName();

            S_Debug.Write_DebugLog("","pJob<>null",false);

            pCommand2.Encode=Encode;//gb2312

            if (pCommand2.pJob.ID>0){
                AI_Var2.pCommands.put(pRun_Session.ID+","+ pCommand2.pJob.ID,pCommand2);
            }
            pCommand2.start();

            strContent ="session:"+pRun_Session.ID+ ",id:" + pCommand2.pJob.ID+ ",command:" + strContent;

            System.out.println(strContent);
            return strContent;
//        }
    }
    
    
    public void Run_Sub1_Function(int Function_Call,int Try_Times,String Start_From){
        pRun.Save_Status("子函数不需要恢复");//子函数不需要恢复！
        S_Debug.Write_DebugLog("","运行 function 子函数调用");
        //函数映射功能！激活子空间的调度图
        JSONObject pObj=new JSONObject(this.Program);
        String strInput=pObj.getString("input");
        String strOutput=pObj.getString("output");
        if (pObj.has("var1")){//传递函数空间的变量
            C_Map_Function pFunction=new C_Map_Function(pRun_Session,this.ID);
            pFunction.Read_Json();
            pFunction.Var1=pObj.getString("var1");
            if (pObj.has("var2")){
                pFunction.Var2=pObj.getString("var2");
            }
            if (pObj.has("var3")){
                pFunction.Var3=pObj.getString("var3");
            }
            pFunction.Save_Var();
        }

        //计算完毕要激活的节点
        String[] strSplit=strOutput.split(",");//这些节点激活，代表计算完毕！
        for (String strSplit1 : strSplit) {
            int ID2 = Integer.valueOf(strSplit1);
            C_Table pTable=pRun_Session.Read_Table(this.ID,ID2);
            pTable.pRun.Save_Function_ID(this.pRun.Function_Call,this.ID);//给要返回节点，设置函数ID
            //这里为了给输出节点计算完毕的时候返回函数
        }

        //开始计算子函数要激活的节点
        strSplit=strInput.split(",");//把映射的激活
        for (String strSplit1 : strSplit) {
            int ID2 = Integer.valueOf(strSplit1);
            C_Table pTable=pRun_Session.Read_Table(this.ID,ID2);
            pTable.bActive=true;
            pTable.pRun.Function_Call=this.ID;
            pTable.pRun.iStep=pRun.iStep;
            pTable.pRun.Save_Status("active");
            pTable.Run_NextTask(pRun_Session);//跑表格有关的下一个任务
        }
    }
    
    public String GetProgram(C_Run_Session pRun_Session,boolean bReplace){
        String strContent=this.Program;
        if (strContent.startsWith("{sys.download}")){
            String[] strSplit=strContent.split("\n");
            if (strSplit.length>1){
                strContent=S_Net.http_get(strSplit[1]);
            }else{
                strContent="url错误！";
            }
        }
        int ID2;
        
        if (this.ItemType!=null && this.ItemType.toLowerCase().equals("param")){
            //输入节点替换
            String[] strSplit=null; 
            strSplit=this.Get_From_IDs().split(",");
            for (int i=0;i<strSplit.length;i++)
            {
                if (strSplit[i].equals("")) continue;

                Pattern p = Pattern.compile("[^\\d]*(\\d+)[^\\d]*");
                Matcher m = p.matcher(strSplit[i]);
                if (m.matches()){
                    ID2=Integer.valueOf(m.group(1));
                    if (ID2<=0){
                        System.out.println("ID2="+ID2);
                    }else{
                        C_Table pTable2=pRun_Session.Read_Table(pRun.Function_Call,ID2);
                        if (bReplace){
                            if ("".equals(pTable2.Database)){
                                strContent=strContent.replace("{输入"+(i+1)+"}",pTable2.Get_Name());
                            }else{
                                strContent=strContent.replace("{输入"+(i+1)+"}",pTable2.Database+"."+pTable2.Get_Name());
                            }
                            strContent=strContent.replace("{输入"+(i+1)+".Program}",pTable2.Program);
                            strContent=strContent.replace("{输入"+(i+1)+".ID}",pTable2.ID+"");
                            strContent=strContent.replace("{输入"+(i+1)+".Value}","${sys_input_"+(i+1)+"}");
                        }
                    }
                }
            } 

            //输出节点替换 
            strSplit=this.Get_To_IDs().split(",");
            for (int i=0;i<strSplit.length;i++)
            {
                if (strSplit[i].equals("")) continue;

                Pattern p = Pattern.compile("[^\\d]*(\\d+)[^\\d]*");
                Matcher m = p.matcher(strSplit[i]);
                if (m.matches()){
                    ID2=Integer.valueOf(m.group(1));
                    if (ID2<=0){
                        System.out.println("ID2="+ID2);
                    }else{
                        C_Table pTable2=pRun_Session.Read_Table(pRun.Function_Call,ID2);
                        if (bReplace){
                            strContent=strContent.replace("{输出"+(i+1)+"}",pTable2.Get_Name());
                            strContent=strContent.replace("{输出"+(i+1)+".Program}",pTable2.Program);
                            strContent=strContent.replace("{输出"+(i+1)+".ID}",pTable2.ID+"");
                        }
                    }
                }
            } 
            
            
            String strSep=this.Program_Param;
            strSplit=strContent.split(strSep);
            
            if (this.Template_ID>0){
                C_Table pTable=pRun_Session.Read_Table(pRun.Function_Call,this.Template_ID);
                strContent=pTable.Program;
                for (int i=0;i<strSplit.length;i++){
                    strContent=strContent.replace("{"+i+"}", "{content_"+i+"}");
                }
                for (int i=0;i<strSplit.length;i++){
                    String strTmp=strSplit[i];
                    while(strTmp.startsWith("\r") || strTmp.startsWith("\n")){
                        strTmp=strTmp.substring(1);
                    }
                    while(strTmp.endsWith("\r") || strTmp.endsWith("\n")){
                        strTmp=strTmp.substring(0,strTmp.length()-1);
                    }
                    strContent=strContent.replace("{content_"+i+"}", strTmp);
                }
                Call_Cite_Count=0;
                strContent=Replace_Cite(strContent);//引用程序
            }

            
            
        }else{
            Call_Cite_Count=0;
            strContent=Replace_Cite(strContent);//引用程序

            String[] strSplit;
            if (!"".equals(Program_Param)){ //如果参数非空 //参数替换
                strSplit=Program_Param.split(",");
                for (int i=0;i<strSplit.length;i++)
                {
                    strContent=strContent.replace("{参数"+(i+1)+"}",strSplit[i]);
                }
            }

            //输入节点替换 
            strSplit=Get_From_IDs().split(",");
            for (int i=0;i<strSplit.length;i++)
            {
                if (strSplit[i].equals("")) continue;

                Pattern p = Pattern.compile("[^\\d]*(\\d+)[^\\d]*");
                Matcher m = p.matcher(strSplit[i]);
                if (m.matches()){
                    ID2=Integer.valueOf(m.group(1));
                    if (ID2<=0){
                        System.out.println("ID2="+ID2);
                    }else{
                        C_Table pTable2=pRun_Session.Read_Table(pRun.Function_Call,ID2);
                        if (bReplace){
                            if ("".equals(pTable2.Database)){
                                strContent=strContent.replace("{输入"+(i+1)+"}",pTable2.Get_Name());
                            }else{
                                strContent=strContent.replace("{输入"+(i+1)+"}",pTable2.Database+"."+pTable2.Get_Name());
                            }
                            strContent=strContent.replace("{输入"+(i+1)+".Program}",pTable2.Program);
                            strContent=strContent.replace("{输入"+(i+1)+".ID}",pTable2.ID+"");
                            strContent=strContent.replace("{输入"+(i+1)+".Value}","${sys_input_"+(i+1)+"}");
                        }
                    }
                }
            } 

            
            //输出节点替换 
            strSplit=this.Get_To_IDs().split(",");
            for (int i=0;i<strSplit.length;i++)
            {
                if (strSplit[i].equals("")) continue;

                Pattern p = Pattern.compile("[^\\d]*(\\d+)[^\\d]*");
                Matcher m = p.matcher(strSplit[i]);
                if (m.matches()){
                    ID2=Integer.valueOf(m.group(1));
                    if (ID2<=0){
                        System.out.println("ID2="+ID2);
                    }else{
                        C_Table pTable2=pRun_Session.Read_Table(pRun.Function_Call,ID2);
                        if (bReplace){
                            strContent=strContent.replace("{输出"+(i+1)+"}",pTable2.Get_Name());
                            strContent=strContent.replace("{输出"+(i+1)+".Program}",pTable2.Program);
                            strContent=strContent.replace("{输出"+(i+1)+".ID}",pTable2.ID+"");
                        }
                    }
                }
            } 

            if (bReplace){
                if (this.Template_ID>0){
                    C_Table pTable=pRun_Session.Read_Table(pRun.Function_Call,this.Template_ID);
                    String strTemplate=pTable.Program;
                    strContent=strTemplate.replace("{0}", strContent);
                    strContent=Replace_Cite(strContent);//引用程序
                }

                strContent=strContent.replace("  ", " ");
            }
        }
        
        
        
        if (bReplace){
//            strContent=strContent.replace("{程序ID}", this.ID+"");
//            strContent=strContent.replace("{ID}", this.ID+"");
//            strContent=strContent.replace("{名称}", this.Name);
//            strContent=strContent.replace("{包ID}", this.Package_ID);
            strContent=strContent.replace("{程序ID}", this.ID+"");
            strContent=strContent.replace("{名称}", this.Name);
            strContent=strContent.replace("{ID}", this.ID+"");
            strContent=strContent.replace("{包ID}", this.Package_ID);
            strContent=strContent.replace("{删除表}", " Drop table if exists ");
            strContent=strContent.replace("{创建表}", " Create Table if not exists ");
            strContent=strContent.replace("{禁止转化join}", " set hive.auto.convert.join=false; ");
            //strContent=strContent.replace("  ", " ");
        }

        return strContent;
    }
    
    public static void main(String[] args) {
        C_Job p=new C_Job(0,null,0);
        String str=p.Replace_Cite_AI("{ai:main函数头}");
        System.out.println(str);
    }
    
    public String Replace_Cite_AI(String strContent){
        Pattern pMatchs = Pattern.compile("\\{ai"+":.*?\\}");//中间必须有+号，否则web编译的时候，会被替换掉

        Matcher pMatch = pMatchs.matcher(strContent);
        while (pMatch.find()){
            String strFind = pMatch.group(0);
            String url = AI_Var2.Site+"/funnyscript/fs_ai_reply_program.php?id=" + AI_Var2.NLP_Robot;
            String data = "key=" + S_Strings.URL_Encode(strFind);
            String strProgram = S_Net.http_post(url, data);
            
            while (strProgram.endsWith("\r") || strProgram.endsWith("\n")){
                strProgram=strProgram.substring(0,strProgram.length()-1);
            }
            strContent = strContent.replace(strFind,strProgram);
        }
        return strContent;
    }
    /**
     * 引用程序处理 {cite:xxx}
     * @param strContent
     * @return 
     */
    public String Replace_Cite(String strContent){
        Call_Cite_Count+=1;
        if (Call_Cite_Count>100) return strContent;
        
        strContent=Replace_Cite_AI(strContent);
        
        Pattern pMatchs = Pattern.compile("\\{cite:(\\d+)\\}");
        Matcher pMatch = pMatchs.matcher(strContent);
        while (pMatch.find()) {
            String strFind = pMatch.group(1);
            int ID=Integer.valueOf(strFind);
            C_Job pJob = pRun_Session.Read_Job(pRun.Function_Call,ID);
            String strProgram=pJob.Program;
            strProgram=Replace_Cite(strProgram);
            strContent = strContent.replace("{cite:"+ID+"}",strProgram);
        }
        
        pMatchs = Pattern.compile("\\{cite:(\\d+):.*?\\}");

        pMatch = pMatchs.matcher(strContent);
        while (pMatch.find()) {
            String strMatch = pMatch.group(0);
            String strFind = pMatch.group(1);
            int ID=Integer.valueOf(strFind);
            C_Job pJob = pRun_Session.Read_Job(pRun.Function_Call,ID);
            String strProgram=pJob.Program;
            strProgram=Replace_Cite(strProgram);
            strContent = strContent.replace(strMatch,strProgram);//"{cite:"+ID+"}"
        }
        
        
        pMatchs = Pattern.compile("\\{函数引用\\}");

        pMatch = pMatchs.matcher(strContent);
        if (pMatch.find()) {
            String strMatch = pMatch.group(0);
            String strProgram_Sum="";
            
            ArrayList<Integer> pList= Get_Function_IDs();
            for(int ID:pList){
                C_Job pJob = pRun_Session.Read_Job(pRun.Function_Call,ID);

                String strProgram=pJob.Program;
//                if (ID==1808){
//                    System.out.println("test");
//                }
                strProgram=Replace_Cite(strProgram);
                strProgram_Sum+=strProgram+"\n";
            }
            strContent = strContent.replace(strMatch,strProgram_Sum);
        }
        return strContent;
    }
    
    public ArrayList<Integer> Get_Function_IDs(){
        ArrayList pList=new ArrayList();
         String strURL =  AI_Var2.Site
                + "/funnyscript/json_read_function_ids.php?id=" + this.ID;
        String strData = "";
        String strJSON = S_Net.http_GET(strURL,strData,"utf-8", "",20);
        int index = strJSON.indexOf("[");
        if (index > -1) {
            strJSON = strJSON.substring(index);
            System.out.println(strJSON);
            try
            {
                JSONArray pObj = new JSONArray(strJSON);
                for (int i=0;i<pObj.length();i++){
                    pList.add(pObj.getJSONObject(i).getInt("ID"));
                }
            }catch(Exception ex){
                System.out.println(ex.toString());
            }
            return pList;
        } else {
            return pList;
        }
    }

    //任务结束回调
    public void Finished_Callback(C_Run_Session pRun_Session,boolean bError, String strMsg) {
        //运行库里先删除掉
        pRun.Save_Status("finished");//状态
        if (pRun_Session.Continue==0){
            S_Debug.Write_DebugLog("finished","Continue=0 id="+this.ID);
            return ;
        }
        
        Tools.Send_Msg_To_Slack(this.QQ, strMsg);
        pRun_Session.RunJob_Count-=1;
        if (pRun_Session.RunJob_Count<0){
            AI_Var2.pRun_Sessions.remove(new C_K_Int(pRun_Session.ID));
        }
        
        if (bError){  //有问题，设置为黑色
            if (this.Error_Continue){//有错仍旧继续
                Continue_Run_Next(pRun_Session);
            }else{
                pRun.Save_Machine_None();
                pRun.Save_Status("prepare_run");//重试
            }
        }else{
            if (pRun_Session.bAlone==false){//如果不是单独运行的Job,就运行后面的东西
                Continue_Run_Next(pRun_Session);
            }else{
                S_Debug.Write_DebugLog("finished","pRun_Session.bAlone=true id="+this.ID);
            }
        }
    }
    
    /**
     * 预激活
     * @param pRun_Session 
     * @param iStep 激活步骤
     * @param iCheckNext 是否继续检查下一个,=0不检查，=1检查后面1级，=2检查后面两级
     * @param Zero_Save_Continue 如果iCheckNext==0 Zero_Save_Continue==true就会自动保存Continue标志
     * @param bCheck_Sub_Function true 就优先先检查子函数，=false 代表子函数返回
     */
    public void Check_Next_Task(C_Run_Session pRun_Session,int iStep,int iCheckNext,
            boolean Zero_Save_Continue,boolean bCheck_Sub_Function){
        if (this.No_AutoRun==1){
            S_Debug.Write_DebugLog("prepare","不调度 id="+this.ID+" step="+iStep);
            return ;
        }
        S_Debug.Write_DebugLog("prepare","id="+this.ID+" step="+iStep);
        
        if (bCheck_Sub_Function && this.ItemType != null && this.ItemType.toLowerCase().equals("function")) {
            S_Debug.Write_DebugLog("", "check 子函数调用");
            //函数映射功能！激活子空间的调度图
            JSONObject pObj = new JSONObject(this.Program);
            String strInput = pObj.getString("input");
            String strOutput = pObj.getString("output");

            //函数调用，计算完毕要激活的节点
            String[] strSplit = strOutput.split(",");//这些节点激活，代表计算完毕！
            for (String strSplit1 : strSplit) {
                int ID2 = Integer.valueOf(strSplit1);
                C_Table pTable = pRun_Session.Read_Table(this.ID,ID2);
                pTable.pRun.Save_Function_ID(this.pRun.Function_Call,this.ID);
                //这里为了给输出节点计算完毕的时候返回函数
            }

            //开始计算子函数要激活的节点
            strSplit = strInput.split(",");//把映射的激活
            for (String strSplit1 : strSplit) {
                int ID2 = Integer.valueOf(strSplit1);
                C_Table pTable = pRun_Session.Read_Table(this.ID,ID2);
                pTable.pRun.Save_Status("prepare"); //Save_Function_Call(pRun_Session,this.ID);//设置函数空间(调用函数ID)
                pTable.Check_Next_Task(pRun_Session, iStep, iCheckNext - 1, Zero_Save_Continue);//跑表格有关的下一个任务
            }

        } else {
            pRun.iStep=iStep;
            pRun.Save_Status("prepare");
            if (iCheckNext > 0 && !"fs.switch".equals(this.ItemType)) {//如果不是 fs.switch 则激活后面的表
                String[] strSplit = Get_To_IDs().split(",");
                for (String strSplit1 : strSplit) {
                    if (!"".equals(strSplit1)) {
                        int ID2 = Integer.valueOf(strSplit1);
                        if (ID2 > 0) {
                            C_Table pTable = pRun_Session.Read_Table(pRun.Function_Call,ID2);
                            pTable.pRun.Save_Status("prepare");//.Save_Function_Call(pRun_Session,pMap_Run.Function_Call);//设置函数空间(调用函数ID)
                            pTable.Check_Next_Task(pRun_Session, iStep, iCheckNext - 1, Zero_Save_Continue);//跑表格有关的下一个任务
                        }
                    }
                }
                
            } else {
                if (Zero_Save_Continue) {
                    pRun.Save_Continue(true);
                }
            }
        }
    }
    
    /**
     * 运行后面的节点
     * @param pRun_Session 
     */
    public void Continue_Run_Next(C_Run_Session pRun_Session){
        if ("hive".equals(this.Type_Run)){//如果是表的hive查询,注意不是hive节点
            S_Debug.Write_DebugLog("finished","hive return id="+this.ID);
            return ;
        }else{
            S_Debug.Write_DebugLog("finished","not hive return id="+this.ID);
        }
        
        String strIDs=Get_To_IDs();
        S_Debug.Write_DebugLog("finished","id="+this.ID+" Get_To_IDs="+strIDs);
        String[] strSplit=strIDs.split(",");
        for (String strSplit1 : strSplit) {
            if ("".equals(strSplit1)) {
            } else {
                int ID2 = Integer.valueOf(strSplit1);
                if (ID2>0){
                    C_Table pTable=pRun_Session.Read_Table(pRun.Function_Call,ID2);
                    pTable.From_Nodes.put(this.ID,this);
                    pTable.bActive=true;
                    pTable.pRun.iStep=pRun.iStep;
                    pTable.pRun.Save_Status("active");//设置表的状态为激活
                    pTable.Run_NextTask(pRun_Session);//跑表格有关的下一个任务
                }
            }
        }
    }

    //获取下一个表格等其他信息
    public boolean Update_Info()
    {
        boolean bError=false;
        String strJSON=this.Read_Json2();
        JSONObject pObj;
        try{
            pObj=new JSONObject(strJSON);
            this.set_To_IDs(pObj.getString("Connect_To"));
            this.Color=pObj.getString("Color");
            this.D4=Integer.valueOf(pObj.getString("D4"));
            this.Program=pObj.getString("Program");
            this.Program_Output=pObj.getString("Program_Output");
            this.Check_SQL=pObj.getString("Check_SQL");
            this.Template_ID=Integer.valueOf(pObj.getString("Template_ID"));

            this.Package_ID=pObj.getString("Package_ID");
            this.Error_Continue=("1".equals(pObj.getString("Error_Continue")));
            this.Retry_Times=Integer.valueOf(pObj.getString("Retry_Times"));
            this.No_AutoRun=Integer.valueOf(pObj.getString("No_AutoRun"));
            this.batOnly=Integer.valueOf(pObj.getString("Bat"));
            this.Active_Level=Integer.valueOf(pObj.getString("Active_Level"));

        }catch(JSONException e){
            bError=true;
            System.out.println("strJSON="+strJSON);
            e.printStackTrace();
            S_Debug.Write_DebugLog("C_Job.json.error",
                        "\n===json error===\n"+e.toString());
        }
        pRun.Read_Json();
        return bError;
    }

    public void Save_Program(String strProgram) {
        String strURL= AI_Var2.Site
               +"/funnyscript/save_map_item_program.php";
        String strData="";
        try {
            strData = "id="+this.ID
                    +"&key="+URLEncoder.encode("funnyai.mathfan.kascend.123edsaq.!@#EDSAQ","utf-8")
                    +"&Program="+URLEncoder.encode(strProgram,"utf-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(C_Job.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("save_map_item_program \n program= "+strProgram);
        String strContent=S_Net.http_post(strURL,strData);
        System.out.println("id="+this.ID+"\n"+strContent);
    }
}
