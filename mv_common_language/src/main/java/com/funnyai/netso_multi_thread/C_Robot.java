package com.funnyai.netso_multi_thread;


import com.funnyai.data.C_K_Str;
import com.funnyai.data.Treap;
import com.funnyai.Segmentation.*;
import com.funnyai.NLP.I_Robot;
import com.funnyai.common.AI_Var2;
import com.funnyai.fs.Tools;
import com.funnyai.io.Old.S_File_Text;
import com.funnyai.net.Old.S_Net;
import com.funnyai.netso.C_Function;
import com.funnyai.netso.C_Return_AI;
import com.funnyai.netso.C_Sentence;
import com.funnyai.netso.C_Session_AI;
import com.funnyai.netso.C_Struct_Active;
import com.funnyai.netso.C_Topic;
import com.funnyai.netso.NetSO;
import com.funnyai.netso.NetSO_Multi_Topic;
import java.util.ArrayList;
import org.json.JSONObject;

/**
 *
 * @author happyli
 */
public class C_Robot extends I_Robot {
    
    public static Treap TreapRobot = new Treap();
    public static Treap pTreapKey =new Treap();//
    public static Treap pTreapKey2 =new Treap();//
    
    public C_Session_AI pSAI3 = null;
    public C_Function pFun3 = null;
    
    public String ID="0";//机器人ID 1 代表ID=1的机器人  "1_1046" 代表ID=1，意图ID=1046的分类
    public NetSO pNetSO =  null;
    public NetSO_Multi_Topic pNetSO2 =  null;
    public NetSO_Multi_Thread pNetSO3 =  null;
    
    public String File_Robot_Example = "";//机器人训练文件
    public String File_Robot_Test="";//机器人测试文件
    
    public String Name="";
    public double Min_Active=0;
    public String Match_String="";
    public int Match_Index=0;

    public C_Robot(
            String ID,
            boolean bFromWeb,
            boolean bExample_FromWeb,
            int NetSO_Version,
            A_NetSO_Save pNetSO_Save){
        this.ID=ID;
        Read_Json(bExample_FromWeb);
        pNetSO3=new NetSO_Multi_Thread(this,pNetSO_Save);// 
        pNetSO2=new NetSO_Multi_Topic(AI_Var2.Path_Subset);
        pNetSO=new NetSO(this,pNetSO_Save,AI_Var2.Path_Subset);
        pNetSO.MinActive=this.Min_Active;
    }
    
    /**
     * 
     * @param bFromWeb 是否从web上读取
     * @param bExample_FromWeb
     * @param ID
     * @param pSeg
     * @param pConvert
     * @param pStruct
     * @param pNetSO_Save
     * @return 
     */
    public static C_Robot GetRobot(
            boolean bFromWeb,
            boolean bExample_FromWeb,
            String ID,
            C_Segmentation pSeg, 
            C_Convert pConvert,
            C_Segmentation_Struct pStruct,
            A_NetSO_Save pNetSO_Save) {
        
        C_Robot pRobot = (C_Robot) C_Robot.TreapRobot.find(new C_K_Str(ID));
        if (pRobot == null) {
            pRobot = new C_Robot(ID,bFromWeb,bExample_FromWeb,AI_Var2.NetSO_Version,pNetSO_Save);
            
            C_Robot.TreapRobot.insert(new C_K_Str(ID), pRobot);
        }
        return pRobot;
    }
    
    /**
     * 读取JSON
     */
    private void Read_Json(boolean bFromWeb) {
        String strURL =  AI_Var2.Site
                + "/funnyai/json_read_robot.php?id=" + this.ID;
        String strData = "";
        
        String strReturn ;
        if (bFromWeb){
            System.out.println("robot.from.web");
            System.out.println(strURL);
            strReturn = S_Net.http_GET(strURL,strData, "utf-8", "",20);
        }else{
            System.out.println("robot.from.local");
            strReturn = S_File_Text.Read(AI_Var2.Path_Example+"robot_"+this.ID+ ".txt", "utf-8",1000);
        }
        
        int index = strReturn.indexOf("{");
        if (index > -1) {
            String strJSON = strReturn.substring(index);
            Tools.Write_DebugLog("read.json",strJSON,false);
            try
            {
                JSONObject pObj = new JSONObject(strJSON);
                this.Name=pObj.getString("Name");
                this.Min_Active = pObj.getDouble("Min_Active");
                this.Match_String = pObj.getString("Match_String");
                this.Match_Index = pObj.getInt("Match_Index");
            }catch(Exception ex){
                System.out.println(strJSON);
                System.out.println(ex.toString());
            }
        } else {
            System.out.println(strReturn);
            System.out.println("read error, ID=" + this.ID);
        }
    }
    
    
    
     
    /**
     * 初始化机器人
     * @param bFromWeb
     * @param ID 
     */
    public void Load_TrainData(
            boolean bFromWeb,String ID) {
        if (AI_Var2.bRead_Train_Data==false){ //是否读取数据，如果只是分词等可以不读取
            return ;
        }
        
        this.ID = ID;
        String strFile=AI_Var2.MapPath("Main_"+ ID + ".txt");
        System.out.println(strFile);
        switch (AI_Var2.NetSO_Version) {
            case 3:
                this.pNetSO3.read(bFromWeb,strFile);
                break;
            case 2:
                this.pNetSO2.read(strFile);
                break;
            case 1:
                this.pNetSO.read(strFile);
                break;
            default: //否则不用这个不读取
                break;
        }
    }

    
    public C_Topic GetTopic(C_Sentence pSentence)
    {
        ArrayList<C_Struct_Active> pKeys;
        this.Word_Min_Length = 0;
        pKeys = pNetSO.ITSKeys(this, pSentence);//, "了,的");
        C_Topic pPower = null;
        if (pKeys!=null && pKeys.size() > 0){
            pPower = pNetSO.AI_Topic(pKeys,false);
        }
        return pPower;
    }
    
    
    public C_Return_AI GetTopic_Filter(
            C_Sentence pSentence,boolean bDebug,boolean bSubStruct) 
    {
        this.Word_Min_Length = 0;
        ArrayList<C_Struct_Active> pKeys = pNetSO.ITSKeys_Last(this, pSentence,bSubStruct);
        String strSentence_Sep=pSentence.Sentence_Seg;
        return GetTopic_Filter2(pKeys,strSentence_Sep,bDebug);
    }
    
    
    public C_Return_AI GetTopic_Filter2(
            ArrayList<C_Struct_Active> pKeys,
            String strSentence_Sep,
            boolean bDebug) 
    {
        C_Topic pTopic = null;
        C_Return_AI pReturn=new C_Return_AI();
        if (AI_Var2.NetSO_Version==3){
            Treap<C_Active> pTreap_Input_Nodes=this.pNetSO3.getActive(pKeys);
            Treap<C_Active> pOutpus=this.pNetSO3.caculateOuputs(pTreap_Input_Nodes);
            ArrayList<C_Active> pMaxs=this.pNetSO3.getMax(pOutpus,1);
            C_Active pMax=null;
            if (pMaxs.size()>0){
                pMax=pMaxs.get(0);
            }
            if (pMax!=null){
                pTopic=new C_Topic();// pMax.pNode.Name;
                if (pMax.pNode.Name.startsWith("Topic.")){
                    pTopic.Topic=pMax.pNode.Name.substring(6);
                }else{
                    pTopic.Topic=pMax.pNode.Name;
                }
            }
        }else{
            pTopic = pNetSO.AI_Topic(pKeys,bDebug);
        }
        pReturn.pTopic=pTopic;
        
        pReturn.pArray=pNetSO.Get_Token_List(this, pSAI3, strSentence_Sep);
        return pReturn;
    }
    
    
    
    public ArrayList<C_Topic> GetTopics(
            ArrayList<C_Struct_Active> pKeys,
            C_Sentence pSentence,int iCount) 
    {
        boolean bDebug=true;
        ArrayList<C_Topic> pList=new ArrayList<>();
        
        C_Topic pTopic = null;
        if (AI_Var2.NetSO_Version==3){
            Treap<C_Active> pTreap_Input_Nodes=this.pNetSO3.getActive(pKeys);
            Treap<C_Active> pOutpus=this.pNetSO3.caculateOuputs(pTreap_Input_Nodes);
            ArrayList<C_Active> pMaxs=this.pNetSO3.getMax(pOutpus,iCount);
            if (pMaxs.size()>0){
                for (int i=0;i<pMaxs.size();i++){
                    C_Active pMax=pMaxs.get(0);
                    pTopic=new C_Topic();// pMax.pNode.Name;
                    if (pMax.pNode.Name.startsWith("Topic.")){
                        pTopic.Topic=pMax.pNode.Name.substring(6);
                    }else{
                        pTopic.Topic=pMax.pNode.Name;
                    }
                    pList.add(pTopic);
                }
            }
        }else{
            pTopic = pNetSO.AI_Topic(pKeys,bDebug);
            pList.add(pTopic);
        }
        return pList;
    }
    
    public ArrayList Get_Token_List(
            C_Session_AI pSAI,
            String strSentence_Sep) 
    {
        ArrayList pArray = pNetSO.Get_Token_List(this, pSAI, strSentence_Sep);
        return pArray;
    }

}
