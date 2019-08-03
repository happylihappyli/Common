package com.funnyai.netso_multi_thread;

import com.funnyai.netso.C_Active_From_v2;
import com.funnyai.netso.C_Struct_Active;
import com.funnyai.netso.C_Train_Example;
import com.funnyai.netso.C_Sentence;
import com.funnyai.netso.NetSO;
import com.funnyai.netso.C_Convert_AI;
import com.funnyai.netso.C_Session_AI;
import com.funnyai.data.C_K_Str;
import com.funnyai.data.Treap;
import com.funnyai.data.TreapEnumerator;
import com.funnyai.data.C_K_Int;
import com.funnyai.data.C_K_Double_Str;
import com.funnyai.NLP.*;
import com.funnyai.Segmentation.C_Token_Link_and_Add;
import com.funnyai.Segmentation.C_Word_Convert;
import com.funnyai.common.AI_Var2;
import com.funnyai.common.S_Debug;
import com.funnyai.fs.Tools;
import com.funnyai.io.C_File;
import com.funnyai.io.Old.S_File;
import com.funnyai.io.S_File_Text;
import com.funnyai.io.S_file;
import com.funnyai.io.S_file_sub;
import com.funnyai.net.Old.S_Net;
import static java.lang.System.out;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author happyli
 */
public class NetSO_Multi_Thread {
    public double trainStep=0.001;
    Treap<Node> pNodes_Input=new Treap<>();
    Treap<Node> pNodes_Output=new Treap<>();
    public ArrayList<C_Train_Example> pList_Error=new ArrayList<>();//错误的样本
    public A_NetSO_Save pNetSO_Save=null;
    public C_Robot pRobot=null;
    
    //以下是从NetSO复制过来的类
    public Treap pTreapFilter = new Treap();//  '要过滤的词汇组成的Treap
    public Treap pTreapFilter_Sentence = new Treap();//  '要过滤的句子组成的Treap

    
    public Treap<C_Topic_Name> pTreap_Topic_Name=new Treap<>();//意图ID和意图名称的对应关系
    
    public NetSO_Multi_Thread(C_Robot pRobot,A_NetSO_Save pNetSO_Save){
        this.pRobot=pRobot;
        this.pNetSO_Save=pNetSO_Save;
    }
    
    public void Topic_Name_Init_Local(String Robot_ID){
        
        String strReturn=S_File_Text.Read(AI_Var2.MapPath("./Map_"+Robot_ID+".txt"), "utf-8",1000);

        int IndexStr  = strReturn.indexOf("[");
        if (IndexStr > -1){
            strReturn = strReturn.substring(IndexStr);
            JSONArray token =  new JSONArray(strReturn);

            for (int k=0;k<token.length();k++){
                JSONObject t=token.getJSONObject(k);
                try{
                    String Intent_ID = t.getString("Intent_ID");
                    String Name = t.getString("Name");
                    out.println(Intent_ID+","+Name);
                    this.Topic_Name_Add(Intent_ID, Name);
                }
                catch(JSONException ex){
                    ex.printStackTrace();
                }
            }
        }else{
            out.println("IndexStr=-1");
        }
        
    }
    
    
    public void Topic_Name_Init_Web(String Robot_ID){
        
        String strURL_Topic_Name= "https://www.funnyai.com/funnyai/json_list_robot_intent.php?robot="+Robot_ID;
        String strReturn = S_Net.http_GET(strURL_Topic_Name,"", "utf-8", "",20);
        int IndexStr  = strReturn.indexOf("[");
        if (IndexStr > -1){
            strReturn = strReturn.substring(IndexStr);
            JSONArray token = new JSONArray(strReturn);
            for (int k=0;k<token.length();k++){
                JSONObject t=token.getJSONObject(k);
                try{
                    String Intent_ID = t.getString("Intent_ID");
                    String Name = t.getString("Name");
                    this.Topic_Name_Add(Intent_ID,Name);
                }
                catch(JSONException ex){
                    out.println(ex.toString());
                }
            }
        }
    }
    
    public void Topic_Name_Add(String strTopic,String strTopic_Name){
        pTreap_Topic_Name.insert(new C_K_Str(strTopic),new C_Topic_Name(strTopic,strTopic_Name));
    }
    
    public String Topic_Name(String Topic_ID){
        C_Topic_Name pTopic=pTreap_Topic_Name.find(new C_K_Str(Topic_ID));
        if (pTopic!=null){
            return pTopic.Name;
        }else{
            return Topic_ID;
        }
    }
    
    public void clear(){
        pNodes_Input.Clear();
        pNodes_Output.Clear();
    }
    
    public void InitFilter(boolean bFromWeb,String ID){
        String strName;
        String strURL = AI_Var2.Site+"/funnyai/json_list_ai_filter.php";
        String strData;
        
        int p = 0;
        while (p > -1) {
            p++;
            strData = "id="+ID+"&p=" + p;
            String strReturn;
            if (bFromWeb){
                System.out.println("NetSO_Multi_Thread filter.from.web");
                strReturn = S_Net.http_post(strURL,strData);//, "utf-8", "",false);
                S_File_Text.Write(AI_Var2.Path_Filter+ID+"_"+p+".txt", strReturn,"utf-8");
            }else{
                System.out.println("NetSO_Multi_Thread filter.from.local");
                strReturn = S_File_Text.Read(AI_Var2.Path_Filter+ID+"_"+p+".txt", "utf-8",1000);
            }
            System.out.println(strReturn);
            
            int IndexStr = strReturn.indexOf("{");
            if (IndexStr == -1){
                break;
            }
            strReturn = strReturn.substring(IndexStr);
            
            try{
                JSONObject pJobject = new JSONObject(strReturn);
                JSONArray token = pJobject.getJSONArray("data");
                int Count = pJobject.getInt("Count");
                for (int k=0;k<token.length();k++){
                    JSONObject t=token.getJSONObject(k);
                    strName = t.getString("Name").trim();
                    strName = strName.toLowerCase();
                    System.out.println("Filter="+strName);

                    pTreapFilter.insert(new C_K_Str(strName),strName);
                }
                if (Count < 100){
                    break;
                }
            }catch(Exception ex){
                S_Debug.Write_DebugLog("json_error",strReturn);
                S_Debug.Write_DebugLog("json_error",ex.toString());
                
                out.println("strReturn");
                out.println(strReturn);
                ex.printStackTrace();
            }
        }
    }

    
    public String read(boolean bFromWeb,String strFile){
        
        if (S_File.Exists(strFile)==false){
            return "文件不存在！";
        }
        this.InitFilter(bFromWeb,this.pRobot.ID);
                
        this.clear();
        out.println("file="+strFile);
        try {
            C_File pFile=S_file.main.Read_Begin(strFile);
            String strLine;
            String[] strSplit;
            double dbScale;
            strLine = S_file.main.read_line(pFile);// pSR.readLine();
            int iCount=0;
            while (strLine!=null){
                iCount+=1;
                strSplit = strLine.split("\\|");
                if (strSplit.length > 2 ) {
                    if (strSplit[1].endsWith(":2") || strSplit[1].endsWith(":3")){
                    }else{
                        dbScale = Double.valueOf(strSplit[2]);
                        if (Math.abs(dbScale) <  0.00001) {
                        }else {
                            Node pNode1=getInputNode(strSplit[0]);
                            Node pNode2=getOutputNode(strSplit[1]);
                            Connect pConnect=new Connect(pNode1,pNode2,dbScale);
                            this.addConnect(pConnect);
                        }
                    }
                }
                strLine = S_file.main.read_line(pFile);// pSR.readLine();
                if (iCount % 100000 ==0){
                    out.println(iCount);
                }
            }
            pFile.Close();
            return "Read Finished";
        }
        catch (NumberFormatException ex) {
            return ex.toString();
        }
    }
    
    
    /**
     * 从另一个NetSO读取
     * @param pNetSO_Thread 
     */
    public void read(NetSO_Multi_Thread pNetSO_Thread) {
        TreapEnumerator<Node> p = pNetSO_Thread.pNodes_Output.Elements();
        while (p.HasMoreElements()) {
            Node pNode_Output = p.NextElement();
            TreapEnumerator<Connect> p2 = pNode_Output.pTreaps.Elements();// .pOutputs.Elements(true);
            while (p2.HasMoreElements()) {
                Connect pConnect = p2.NextElement();
                Node pNode1=getInputNode(pConnect.From.Name);
                Node pNode2=getOutputNode(pConnect.To.Name);
                Connect pConnect2=new Connect(pNode1,pNode2,pConnect.weight);
                this.addConnect(pConnect2);
            }
        }
        
        TreapEnumerator<C_Topic_Name> p3 = pNetSO_Thread.pTreap_Topic_Name.Elements();
        while (p3.HasMoreElements()) {
            C_Topic_Name pTopic = p3.NextElement();
            pTreap_Topic_Name.insert(new C_K_Str(pTopic.ID),pTopic);
        }
    }
    
    public String save(String strFile) {
        pNetSO_Save.setFile(strFile);
        String strReturn = "";
        try { 
            pNetSO_Save.start(pRobot);
            pNetSO_Save.start_file(this);
            System.out.println("file="+strFile);
            TreapEnumerator<Node> p = pNodes_Output.Elements();
            while (p.HasMoreElements()) {
                Node pNode_Output = p.NextElement();
                TreapEnumerator<Connect> p2 = pNode_Output.pTreaps.Elements();// .pOutputs.Elements(true);
                while (p2.HasMoreElements()) {
                    Connect pConnect = p2.NextElement();
                    if (Math.abs(pConnect.weight) > 0.0000001) {
                        pNetSO_Save.save_line(pConnect.From.Name,pConnect.To.Name,pConnect.weight);
                    }
                }
            }
            pNetSO_Save.end();
        }
        catch (Exception ex) {
            strReturn = ex.toString();
        }
        return strReturn;
    }
    
    /**
     * 训练方法1 对的向量已近一些，错的向量移远一些
     * @param pList
     * @param pWriter
     * @param bShowDebug
     * @return 
     */
    public Treap<Modify> TrainAI_Bat(
            ArrayList<C_Train_Example> pList,
            C_File pWriter,
            boolean bShowDebug) {
        
        int iMethod=3;//权重调整方法=3
        
        int iAdjust=0;
        Treap<Modify> pTreapReturn=new Treap<>();
        for (int k=0;k<pList.size();k++){
            C_Train_Example pSample=pList.get(k);
            ArrayList<C_Struct_Active> pKeys=pSample.pKeys;
            if (pKeys==null){
                out.println("error no keyes:");
                if (pSample!=null){
                    if (pSample.pSentence!=null){
                        out.println(pSample.pSentence.Sentence);
                    }
                }
                continue;
            }
            Treap<C_Active> pTreap_Input_Nodes=getActive(pKeys);
            
            Treap<C_Active> pOutpus=this.caculateOuputs(pTreap_Input_Nodes);
            ArrayList<C_Active> pMaxs=this.getMax(pOutpus,1);
            C_Active pMax=null;
            if (pMaxs.size()>0){
                pMax=pMaxs.get(0);
            }
            
            if (pMax==null){
                Add_Sub(pTreap_Input_Nodes,pTreapReturn,"Topic."+pSample.Topic);
                this.Train_One_Switch(iMethod,pTreap_Input_Nodes,pOutpus,"Topic."+pSample.Topic,"Topic.None",pTreapReturn);
                iAdjust+=1;
                pSample.Adjust_Count+=1;
                pList_Error.add(pSample);
            }else if (pMax.pNode.Name.equals("Topic."+pSample.Topic)==false){
                Add_Sub(pTreap_Input_Nodes,pTreapReturn,"Topic."+pSample.Topic);
                this.Train_One_Switch(iMethod,pTreap_Input_Nodes,pOutpus,"Topic."+pSample.Topic,pMax.pNode.Name,pTreapReturn);
                iAdjust+=1;
                pSample.Adjust_Count+=1;
                pList_Error.add(pSample);
                String Topic_Error=this.Topic_Name(pMax.pNode.Name.substring("Topic.".length()));
                String Topic_Tag=this.Topic_Name(pSample.Topic);
                
                pSample.Topic_Tag_Name=Topic_Tag;
                pSample.AI_Tag_Tmp=Topic_Error;
            }
        }
        out.println("iAdjust.sum="+iAdjust+"="+pList_Error.size());
        return pTreapReturn;
    }
    
    public void Add_Sub(Treap<C_Active> pTreap_Input_Nodes,Treap<Modify> pTreapReturn,String strTopic){
        TreapEnumerator<C_Active> p=pTreap_Input_Nodes.Elements();
        while(p.HasMoreElements()){
            C_Active pActive = p.NextElement();// pList_Input_Nodes.get(i);
            String strKey=pActive.pNode.Name+","+strTopic;//"Topic."+pExample.Topic;
            if (pActive.pNode.pTreaps.find(new C_K_Str(strTopic))==null){
                if (pTreapReturn.find(new C_K_Str(strKey))==null){
                    out.println("add:"+strKey);
                    Modify pModify=new Modify(pActive.pNode.Name,strTopic,0.0001);
                    pTreapReturn.insert(new C_K_Str(strKey),pModify);
                }
            }
        }
    }
            
    
    /**
     * 查找输入节点
     * @param Name
     * @return 
     */
    public Node getInputNode(String Name){
        Node pNode=pNodes_Input.find(new C_K_Str(Name));
        if (pNode==null){
            pNode=new Node(Name);
            pNodes_Input.insert(new C_K_Str(Name),pNode);
        }
        return pNode;
    }
    
    /**
     * 查找输出节点
     * @param Name
     * @return 
     */
    public Node getOutputNode(String Name){
        Node pNode=pNodes_Output.find(new C_K_Str(Name));
        if (pNode==null){
            pNode=new Node(Name);
            pNodes_Output.insert(new C_K_Str(Name),pNode);
        }
        return pNode;
    }
    
    /**
     * 添加一个连接
     * @param pConnect 
     */
    public void addConnect(Connect pConnect){
        Node pNodeInput=pConnect.From;
        Node pNodeOutput=pConnect.To;
        pNodeInput.pTreaps.insert(new C_K_Str(pNodeOutput.Name), pConnect);
        pNodeOutput.pTreaps.insert(new C_K_Str(pNodeInput.Name), pConnect);
        
        pNodes_Input.insert(new C_K_Str(pNodeInput.Name), pNodeInput);
        pNodes_Output.insert(new C_K_Str(pNodeOutput.Name), pNodeOutput);
    }
    
    
    /**
     * 计算最大的节点
     * @param pTreap_Input_Nodes
     * @return 
     */
    public ArrayList<C_Active> calculate_max(Treap<C_Active> pTreap_Input_Nodes,int Count){
        Treap<C_Active> pActives=this.caculateOuputs(pTreap_Input_Nodes);
        return this.getMax(pActives,Count);
    }
    
    /**
     * 计算最大激活节点
     * @param pTreap_Input_Nodes
     * @return 
     */
    public Treap<C_Active> caculateOuputs(Treap<C_Active> pTreap_Input_Nodes){
        Treap<C_Active> pActives=new Treap<>();
        TreapEnumerator<C_Active> p=pTreap_Input_Nodes.Elements();
        while(p.HasMoreElements()){
            C_Active pInput=p.NextElement();
            TreapEnumerator<Connect> p2=pInput.pNode.pTreaps.Elements();
            while(p2.HasMoreElements()){
                Connect pConnect=p2.NextElement();
                Node pOutput=pConnect.To;
                C_Active pActive=pActives.find(new C_K_Str(pOutput.Name));
                if (pActive==null){
                    pActive=new C_Active(pOutput,0);
                    pActives.insert(new C_K_Str(pOutput.Name), pActive);
                }
                pActive.value+=pInput.value*pConnect.weight;
                
                C_Active_From_v2 pActiveTmp=new C_Active_From_v2(pConnect.From,pConnect);//这个Active就是记录一下从哪里激活
                pActive.pTreap_Active_From.insert(new C_K_Int(pActiveTmp.ID), pActiveTmp);
            }
        }
        return pActives;
    }
    
    
    
    /**
     * 获取Treap中最大的一个
     * @param pActives
     * @param Count 个数
     * @return 
     */
    public ArrayList<C_Active> getMax(Treap<C_Active> pActives,int Count){
        ArrayList<C_Active> pReturn=new ArrayList<>();
        Treap<C_Active> pTreap=new Treap<>();
        TreapEnumerator<C_Active> p2=pActives.Elements();
        while(p2.HasMoreElements()){
            C_Active pActive=p2.NextElement();
            pTreap.insert(new C_K_Double_Str(pActive.value,pActive.pNode.Name), pActive);
        }
        TreapEnumerator<C_Active> p1=pTreap.Elements(false);
        while(p1.HasMoreElements()){
            C_Active pActive=p1.NextElement();
            
            pActive.Reason=Get_Reason(pActive.pTreap_Active_From);
            
            pReturn.add(pActive);
            if (pReturn.size()>=Count) break;
        }
        
        return pReturn;
    }
    
    
    public String Get_Reason(Treap<C_Active_From_v2> pTreap_Active_From){
        String strTmp="";
        TreapEnumerator<C_Active_From_v2> p=pTreap_Active_From.Elements();
        while(p.HasMoreElements()){
            C_Active_From_v2 pA=p.NextElement();
            strTmp+=pA.pConcept.Name +":"+ Math.round(pA.pConnect.weight*10000)/10000.0+",";
        }
        return strTmp;
    }
    
    /*
        SoftMax训练算法
        float max = output.get(0), z = 0.0f;
        for (int i = 1; i < osz_; i++) {
            max = Math.max(output.get(i), max);
        }
        for (int i = 0; i < osz_; i++) {
            output.set(i, (float) Math.exp(output.get(i) - max));
            z += output.get(i);
        }
        for (int i = 0; i < osz_; i++) {
            output.set(i, output.get(i) / z);
        }
    */
    public Treap<Modify> Train_One_M3(
            Treap<C_Active> pInputs,
            Treap<C_Active> pOutputs,
            String strTopic,
            Treap<Modify> pTreap_return){
        
        //get max
        double max = -10000, z = 0.0f;
        TreapEnumerator<C_Active> p1=pOutputs.Elements();
        while(p1.HasMoreElements()){
            C_Active pActive_Output=p1.NextElement();
            max = Math.max(pActive_Output.value, max);
        }
        
        p1=pOutputs.Elements();
        while(p1.HasMoreElements()){
            C_Active pActive_Output=p1.NextElement();
            pActive_Output.value = Math.exp(pActive_Output.value - max);
            z+=pActive_Output.value;
        }
        
        p1=pOutputs.Elements();
        while(p1.HasMoreElements()){
            C_Active pActive_Output=p1.NextElement();
            pActive_Output.value = pActive_Output.value/z;
        }
        
        
        //更新权重
        p1=pOutputs.Elements();
        while(p1.HasMoreElements()){
            C_Active pActive_Output=p1.NextElement();
            if (pActive_Output.pNode.Name.equals(strTopic)){
                pActive_Output.error=1-pActive_Output.value;
            }else{
                pActive_Output.error=0-pActive_Output.value;
            }
            

            TreapEnumerator<C_Active> p2=pInputs.Elements();
            while(p2.HasMoreElements()){
                C_Active pActive_Input=p2.NextElement();
                Connect pConnect = pActive_Input.pNode.pTreaps.find(new C_K_Str(pActive_Output.pNode.Name));
                if (pConnect==null){//如果没有连接，增加一个连接
                    Modify pModify=pTreap_return.find(new C_K_Str(pActive_Input.pNode.Name+","+pActive_Output.pNode.Name));
                    if (pModify==null){
                        pModify=new Modify(pActive_Input.pNode.Name,pActive_Output.pNode.Name,this.trainStep);
                        pTreap_return.insert(new C_K_Str(pActive_Input.pNode.Name+","+pActive_Output.pNode.Name),pModify);
                    }
                }else{
                    Modify pModify=pTreap_return.find(new C_K_Str(pConnect.From.Name+","+pConnect.To.Name));
                    if (pModify==null){
                        pModify=new Modify(pConnect.From.Name,pConnect.To.Name,0);
                        pTreap_return.insert(new C_K_Str(pConnect.From.Name+","+pConnect.To.Name),pModify);
                    }
                    pModify.value+=pActive_Input.value*pActive_Output.error*this.trainStep;
                }
            }
        }
        
        return pTreap_return;
    }
    
    /**
     * 调整一个,用以前的方法，向量差来调整
     * @param pInputs
     * @param pOutputs 所有激活的节点
     * @param strTopic 其中的目标节点（如果不是最大就要调整，最大为1,最小为0）
     * @param strTopic_Error
     * @param pTreap_return
     * @return 
     */
    public Treap<Modify> Train_One_M2(
            Treap<C_Active> pInputs,
            Treap<C_Active> pOutputs,
            String strTopic,String strTopic_Error,
            Treap<Modify> pTreap_return){
        //e1=(e-x)
        //e=e+step*e1
        //e2=(x-r)
        //r=r+step*e2
        
//        C_Active pActive_Error=pOutputs.find(new C_K_Str(strTopic_Error));//e
        TreapEnumerator<C_Active> p2=pInputs.Elements();
        while(p2.HasMoreElements()){
            C_Active pActive_Input=p2.NextElement();
            Connect pConnect = pActive_Input.pNode.pTreaps.find(new C_K_Str(strTopic_Error));
            if (pConnect==null){//如果没有连接，增加一个连接
                Modify pModify=pTreap_return.find(new C_K_Str(pActive_Input.pNode.Name+","+strTopic_Error));
                if (pModify==null){
                    pModify=new Modify(pActive_Input.pNode.Name,strTopic_Error,0);
                    pTreap_return.insert(new C_K_Str(pActive_Input.pNode.Name+","+strTopic_Error),pModify);
                }
            }else{
                Modify pModify=pTreap_return.find(new C_K_Str(pConnect.From.Name+","+pConnect.To.Name));
                if (pModify==null){
                    pModify=new Modify(pConnect.From.Name,pConnect.To.Name,0);
                    pTreap_return.insert(new C_K_Str(pConnect.From.Name+","+pConnect.To.Name),pModify);
                }
                pModify.value+=(pConnect.weight-100)*this.trainStep;
            }
        }
        
        
        
//        C_Active pActive_Right=pOutputs.find(new C_K_Str(strTopic));//r
        p2=pInputs.Elements();
        while(p2.HasMoreElements()){
            C_Active pActive_Input=p2.NextElement();
            Connect pConnect = pActive_Input.pNode.pTreaps.find(new C_K_Str(strTopic));
            if (pConnect==null){//如果没有连接，增加一个连接
                Modify pModify=pTreap_return.find(new C_K_Str(pActive_Input.pNode.Name+","+strTopic_Error));
                if (pModify==null){
                    pModify=new Modify(pActive_Input.pNode.Name,strTopic_Error,0);
                    pTreap_return.insert(new C_K_Str(pActive_Input.pNode.Name+","+strTopic_Error),pModify);
                }
            }else{
                Modify pModify=pTreap_return.find(new C_K_Str(pConnect.From.Name+","+pConnect.To.Name));
                if (pModify==null){
                    pModify=new Modify(pConnect.From.Name,pConnect.To.Name,0);
                    pTreap_return.insert(new C_K_Str(pConnect.From.Name+","+pConnect.To.Name),pModify);
                }
                pModify.value+=(100-pConnect.weight)*this.trainStep;
            }
        }
        
        
        return pTreap_return;
    }
    
    
    
    /**
     * 调整一个
     * @param pInputs
     * @param pOutputs 所有激活的节点
     * @param strTopic 其中的目标节点（如果不是最大就要调整，最大为1,最小为0）
     * @param strTopic_Error
     * @param pTreap_return
     * @return 
     */
    public Treap<Modify> Train_One_M1(
            Treap<C_Active> pInputs,
            Treap<C_Active> pOutputs,
            String strTopic,String strTopic_Error,
            Treap<Modify> pTreap_return){
        //e=e+a(e-x)
//        C_Vector pError=new C_Vector();
        
        TreapEnumerator<C_Active> p1=pOutputs.Elements();
        while(p1.HasMoreElements()){
            C_Active pActive_Output=p1.NextElement();
            if (pActive_Output.pNode.Name.equals(strTopic)){
                pActive_Output.error=100-pActive_Output.value;
            }else{
                pActive_Output.error=0-pActive_Output.value;
            }
            
            TreapEnumerator<C_Active> p2=pInputs.Elements();
            while(p2.HasMoreElements()){
                C_Active pActive_Input=p2.NextElement();
                Connect pConnect = pActive_Input.pNode.pTreaps.find(new C_K_Str(pActive_Output.pNode.Name));
                if (pConnect==null){//如果没有连接，增加一个连接
                    Modify pModify=pTreap_return.find(new C_K_Str(pActive_Input.pNode.Name+","+pActive_Output.pNode.Name));
                    if (pModify==null){
                        pModify=new Modify(pActive_Input.pNode.Name,pActive_Output.pNode.Name,0);
                        pTreap_return.insert(new C_K_Str(pActive_Input.pNode.Name+","+pActive_Output.pNode.Name),pModify);
                    }
                }else{
                    Modify pModify=pTreap_return.find(new C_K_Str(pConnect.From.Name+","+pConnect.To.Name));
                    if (pModify==null){
                        pModify=new Modify(pConnect.From.Name,pConnect.To.Name,0);
                        pTreap_return.insert(new C_K_Str(pConnect.From.Name+","+pConnect.To.Name),pModify);
                    }
                    pModify.value+=pActive_Input.value*pActive_Output.error*this.trainStep;
                }
            }
        }
        return pTreap_return;
    }

    public Treap<C_Active> getActive(ArrayList<C_Struct_Active> pKeys) {
        //ArrayList<C_Struct_Active> pKeys=pExample.pKeys;
        Treap<C_Active> pTreap_Input_Nodes=new Treap<>();
        for (int i=0;i<pKeys.size();i++){
            C_Struct_Active pStruct_Active=pKeys.get(i);
            Node pNode=getInputNode(pStruct_Active.Name);
            pTreap_Input_Nodes.insert(new C_K_Str(pNode.Name),new C_Active(pNode,100));
        }
        return pTreap_Input_Nodes;
    }
    
    
    
    
    /**
     * 最后一层的Key
     * @param pRobot
     * @param pSentence
     * @param pArray
     * @return 
     */
    public C_Token_Link ITSKeys_Last_Link(
            I_Robot pRobot,
            C_Sentence pSentence,
            ArrayList<C_Token_Link> pArray){

        if (pSentence.Sentence.equals("")) {
            return null;
        }
        

        Treap pTreap = new Treap();
        //String strLine = "";
        String strReturn=pSentence.Sentence_Seg;
        C_Token_Link pLink = new C_Token_Link();
        pLink.Init(strReturn);
        C_Convert_AI pConvert_AI = new C_Convert_AI();
        Treap pTreap_Active = new Treap();
        //激活的结构
        C_Token pToken;
        C_Word_Convert pConvert_Word;

        for (int i = 0; i < pLink.Count(); i++) {
            pToken = pLink.Item(i);
            Treap pTreapTmp = (Treap) AI_Var2.pConvert.pTreapActive.find(new C_K_Str(pToken.Name));
            if (pTreapTmp != null) {
                TreapEnumerator p2 = pTreapTmp.Elements();
                while (p2.HasMoreElements()) {
                    pConvert_Word = (C_Word_Convert) p2.NextElement();
                    pTreap_Active.insert(new C_K_Int(pConvert_Word.ID), pConvert_Word);
                }
            }
        }

        C_Token_Link pLink_Old;
        while (true) {
            pLink_Old = new C_Token_Link();
            pLink_Old.Sentence = pSentence.Sentence;
            pLink_Old.Copy(pLink);
            C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);

            //分析递归太多。
            if (pArray.size() > 100) {
                break; 
            }

            while (true) {
                C_Token_Link_and_Add p = pConvert_AI.getConvert2(AI_Var2.pConvert, pTreap_Active, pLink);
                pLink=p.pLink;
                if (pLink.strFrom.equals("")) {
                    pLink.strFrom = "Convert";
                }
                C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);
                if (p.bAdd == false) {
                    break;
                }
            }

            ////////////Struct/////////
            pLink = AI_Var2.pStruct.Segmentation_List3(pLink);
            if (pLink.strFrom.equals("")) {
                pLink.strFrom = "Struct3";
            }
            C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);

            pLink = AI_Var2.pStruct.Segmentation_List2(pLink);
            if (pLink.strFrom.equals("")) {
                pLink.strFrom = "Struct2";
            }
            C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);

            if (pLink_Old.Equal(pLink)) {
                break;
            }
        }

        pLink = pArray.get(pArray.size()-1);
        return pLink;
    }
    
    
    public String Replace_Some(String strLine) {
        for (int i = 0; (i <= 9); i++) {
            strLine = strLine.replace(String.valueOf('０' + i), String.valueOf('0' + i));
        }
        for (int i = 0; (i <= 25); i++) {
            strLine = strLine.replace(String.valueOf('ａ' + i), String.valueOf('a' + i));
        }
        for (int i = 0; (i <= 25); i++) {
            strLine = strLine.replace(String.valueOf('Ａ' + i), String.valueOf('A' + i));
        }
        return strLine;
    }
    
    
    /**
     * 分析句子
     * @param pRobot
     * @param pSAI
     * @param strSentence_Sep
     * @return 
     */
    public ArrayList Get_Token_List(I_Robot pRobot, C_Session_AI pSAI, String strSentence_Sep) {
        strSentence_Sep = Replace_Some(strSentence_Sep);
        if ("".equals(strSentence_Sep)) {
            return null;
        }
        ArrayList pArray = new ArrayList();
        String strReturn = strSentence_Sep;
        C_Token_Link pLink = new C_Token_Link();
        pLink.Init(strReturn);
        int iMax = pLink.Count();
        C_Token_Link pLink_Old;
        Treap pTreap = new Treap();

        C_Token pToken;
        C_Word_Convert pConvert_Word;
        Treap pTreap_Active = new Treap();
        C_Convert_AI pConvert_AI = new C_Convert_AI();
        for (int i = 0; i< pLink.Count(); i++) {
            pToken = pLink.Item(i);// Item[i];
            Treap pTreapTmp = AI_Var2.pConvert.GetTreap(pToken.Name);
            if (pTreapTmp!=null) {
                TreapEnumerator p2 = pTreapTmp.Elements();
                while (p2.HasMoreElements()) {
                    pConvert_Word = (C_Word_Convert) p2.NextElement();
                    pTreap_Active.insert(new C_K_Int(pConvert_Word.ID), pConvert_Word);
                }
            }
        }
        while (true) {
            pLink_Old = new C_Token_Link();
            pLink_Old.Sentence = pLink.Sentence;
            pLink_Old.Copy(pLink);
            C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);
            if (pArray.size()> iMax * 1.5 + 10) {// 分析递归太多
                break; //Warning!!! Review that break works as 'Exit Do' as it could be in a nested instruction like switch
            }
            //  'Convert一次就Struct一次，否则有的匹配就错过了
            //  '比如 {某时间段} {n点} {n分}
            C_Token_Link_and_Add p = pConvert_AI.getConvert2(AI_Var2.pConvert, pTreap_Active, pLink);// .getConvert2(pConvert, pTreap_Active, pLink, bAdd);
            if (p.bAdd) {
                pLink=p.pLink;
                if ("".equals(pLink.strFrom)) {
                    pLink.strFrom = "Convert";
                }
                C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);
            }
//             把副词等组合到动词上，或形容词整合到名词上，等偏正处理过程，这里称之为Tag过程
//            bAdd = false;
//            pLink = pStruct_Tag.Segmentation_List2(pLink);
//            if (("".equals(pLink.strFrom))) {
//                pLink.strFrom = "Struct_Tag";
//            }
//            C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);
            if (pLink.Count() > 1) {
                pLink = AI_Var2.pStruct.Segmentation_List3(pLink); //匹配3个
                if (("".equals(pLink.strFrom))) {
                    pLink.strFrom = "Struct3";
                }
                C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);
                pLink = AI_Var2.pStruct.Segmentation_List2(pLink); //匹配2个
                if ("".equals(pLink.strFrom)) {
                    pLink.strFrom = "Struct2";
                }
                C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);
            }
            if (pLink_Old.Equal(pLink)) {
                break; 
            }
        }

        return pArray;
    }
    
    
    public ArrayList<C_Struct_Active> ITSKeys_Last(I_Robot pRobot, C_Sentence pSentence,boolean bSubStruct){
        
        ArrayList<C_Token_Link> pArray = new ArrayList<>();
        C_Token_Link pLink=ITSKeys_Last_Link(pRobot,pSentence,pArray);
        if (pLink==null){
            return null;
        }
        int iSize=pLink.Count();

        Treap pTreapKey2 = new Treap();
        Treap pTreapKey = new Treap();
        C_Token_Key pTokenKey;
        C_Token pToken1;
        for (int kk=0;kk<pArray.size();kk++){
            pLink = pArray.get(pArray.size()-1-kk);
            if (pLink.Count()>iSize){
                break;
            }
            for (int i = 0; i < pLink.Count(); i++) {
                pToken1 = pLink.Item(i);
                if (!"{any}".equals(pToken1.Name)){
                    for (int j=0;j<pToken1.Count();j++){
                        String strLine2=pToken1.Item(j);
                        pTokenKey = new C_Token_Key(strLine2, pToken1);
                        pTreapKey2.insert(new C_K_Str(pTokenKey.Key), pTokenKey);
                    }
                }else{
                    if (pToken1.Child_Size()==1){
                        pToken1=pToken1.pChild.get(0);
                        pTokenKey = new C_Token_Key(pToken1.Name, pToken1);
                        pTreapKey2.insert(new C_K_Str(pTokenKey.Key), pTokenKey);
                    }
                }
            }
        }
        
        ArrayList<C_Struct_Active> pKeys2 = new ArrayList<>();
        TreapEnumerator<C_Token_Key> p = pTreapKey2.Elements(true);
        while (p.HasMoreElements()) {
            pTokenKey = p.NextElement();
            C_Struct_Active pStruct2 = new C_Struct_Active(pTokenKey.Key, 1.0, pTokenKey);
            pKeys2.add(pStruct2);

            if (bSubStruct) {
                ArrayList<C_Token> pTokens = NetSO.Get_Sub_Child(pStruct2.pTokenKey.pToken);
                for (int j = 0; j < pTokens.size(); j++) {
                    C_Token pToken2 = pTokens.get(j);
                    if (pToken2.Name.contains(" ") == false) {
                        pStruct2 = new C_Struct_Active(pToken2.Name, 1, new C_Token_Key(pToken2.Name, pToken2));
                        pKeys2.add(pStruct2);
                    }
                }
            }
        }

        
        //filter 
        ArrayList<C_Struct_Active> pKeys3 = new ArrayList<>();
        for (int i=0;i<pKeys2.size();i++) {
            pTokenKey = pKeys2.get(i).pTokenKey;
            if (!pTokenKey.Key.contains(" ") && pTreapFilter.find(new C_K_Str(pTokenKey.Key.toLowerCase())) == null) {
                int index=pTokenKey.Key.indexOf("@");
                if (index>0){
                    String strKey=pTokenKey.Key.substring(0,index)+"@*";
                    if (pTreapFilter.find(new C_K_Str(strKey.toLowerCase())) == null){
                        strKey="*"+pTokenKey.Key.substring(index);
                        if (pTreapFilter.find(new C_K_Str(strKey.toLowerCase())) == null){
                            pTreapKey.insert(new C_K_Str(pTokenKey.Key), pTokenKey);
                        }
                    }
                }else{
                    pKeys3.add(pKeys2.get(i));
                }
            }
        }
        
        
        pKeys2 = new ArrayList<>();
        Treap pTreapKeys=new Treap();
        for (int i=0;i<pKeys3.size();i++) {
            C_Struct_Active pStruct_Active = pKeys3.get(i);
            if (pTreapKeys.find(new C_K_Str(pStruct_Active.Name))==null){
                pTreapKeys.insert(new C_K_Str(pStruct_Active.Name), pStruct_Active);
                pKeys2.add(pStruct_Active);
            }
        }
        return pKeys2;
    }

    public void Train_One_Switch(
            int iMethod,
            Treap<C_Active> pTreap_Input_Nodes, Treap<C_Active> pOutpus, 
            String Topic, String Topic_Error, Treap<Modify> pTreapReturn) {
        switch(iMethod){
            case 1:
                Train_One_M1(pTreap_Input_Nodes,pOutpus,Topic,Topic_Error,pTreapReturn);
                break;
            case 2:
                Train_One_M2(pTreap_Input_Nodes,pOutpus,Topic,Topic_Error,pTreapReturn);
                break;
            case 3:
                Train_One_M3(pTreap_Input_Nodes,pOutpus,Topic,pTreapReturn);
                break;
        }
    }

    public Topic_Return Tag(ArrayList<C_Struct_Active> pKeys) {
        Topic_Return p=new Topic_Return();
        Treap<C_Active> pTreap_Input_Nodes=getActive(pKeys);
        ArrayList<C_Active> pMaxs=this.calculate_max(pTreap_Input_Nodes,1);
        C_Active pMax=null;
        if (pMaxs.size()>0) pMax=pMaxs.get(0);
        
        if (pMax==null){
            p.Topic="";
            return p;
        }
        p.Reason=pMax.Reason;
        
        String strTopic=pMax.pNode.Name;
        if (strTopic.startsWith("Topic.")){
            p.Topic=strTopic.substring("Topic.".length());
        }else{
            p.Topic=strTopic;
        }
        return p;
    }
    
    public ArrayList<String> Tags(ArrayList<C_Struct_Active> pKeys,int Count) {
        ArrayList<String> pArray=new ArrayList<>();
        Treap<C_Active> pTreap_Input_Nodes=getActive(pKeys);
        ArrayList<C_Active> pMaxs=this.calculate_max(pTreap_Input_Nodes,Count);
        for (int i=0;i<Math.min(pMaxs.size(),Count);i++){
            String strTopic=pMaxs.get(i).pNode.Name;
            if (strTopic.startsWith("Topic.")){
                pArray.add(strTopic.substring("Topic.".length()));
            }else{
                pArray.add(strTopic);
            }
        }
        return pArray;
    }

}
