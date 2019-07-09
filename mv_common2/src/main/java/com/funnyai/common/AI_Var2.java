/*
 * 一些公共变量
 */
package com.funnyai.common;

import com.funnyai.Segmentation.C_Convert;
import com.funnyai.Segmentation.C_Segmentation;
import com.funnyai.Segmentation.C_Segmentation_Struct;
import com.funnyai.data.Treap;
import java.util.TreeMap;

/**
 * 一些公共变量
 * @author happyli
 */
public class AI_Var2 {
    public static String Site_NameNode="hadoop.kascend.com";//用户的调度器资源地址 
    public static String Apps_Pending="";//FS 判断 Apps_Pending 的节点ID,多个用，分开
    
    public static String Site="http://www.funnyai.com";//121.40.208.159";//www.funnyai.com";//hadoop.kascend.com"; 
    public static String local_ip="";//本地IP
    public static String Server_Socket="";//Socket.IO地址
    
    public static String Path_Shell="/home/hadoop/";
    public static String Path_Segmentation="D:/Funny/FunnyAI/Data/Dic/Segmentation/";//分词路径
    
    public static String Path_Root="D:/Funny/FunnyAI/Data/Tag/";
    public static String Path_Subset="D:/Funny/FunnyAI/Data/Dic/Subset/";
    public static String Path_Filter="D:/Funny/FunnyAI/Data/Dic/Filter/";
    public static String Path_Filter_Sentence="D:/Funny/FunnyAI/Data/Dic/Filter_Sentence/";
    public static String Path_Struct="D:/Funny/FunnyAI/Data/Dic/Struct/";
    public static String Path_Example="D:/Funny/FunnyAI/Data/Dic/Example/";
    public static String Path_Rename="D:/Funny/FunnyAI/Data/Dic/Rename/";
    public static String Path_Data="D:/Funny/FunnyAI/Data/";
    public static String Path_Log="D:/Funny/FunnyAI/Log/";
    
    public static String Cassandra_IP="121.40.195.177";
    public static String Cassandra_User="magic";
    public static String Cassandra_Password="bcf33e92dd93135";
    public static String Cassandra_User_Read="magic";
    public static String Cassandra_Password_Read="bcf33e92dd93135";
    
    public static int Cassandra_Port=9042;
    
    public static String[] Password=new String[10];
    
    public static int Max_Prepare=10;//hadoop排队数量
    public static int Hadoop_Max_Memory=3000;//M hadoop 最大可用内存
    public static int Min_Memory=1024;//单个机器最小内存
    public static int Max_Process=60;//最大任务数
    public static int Max_Appending=0;//最大等待任务数
    public static int Max_Memory=30;//G
    public static int Max_PrepareRun=100;//最大PrepareRun任务数
    //http://121.40.208.159/funnyscript/json_get_prepare_count.php
    
    public static int NLP_Robot=18;//机器人ID 
    public static boolean Debug=false;
    public static String QQ_Admin="";
    public static String Path_Send_Msg="/root/go/send_msg_to_slack/send_msg_to_slack";
    public static String machine_group_id="0";//服务器分组ID
    public static int machine_id=0;//机器ID
    public static TreeMap pRun_Sessions=new TreeMap();
    //public static Treap pCommands=new Treap();//所有用到的命令，其实一个Job一个Command 这里为了查找方便
    
    public static TreeMap pCommands=new TreeMap();//所有用到的命令，其实一个Job一个Command 这里为了查找方便
//    
    public static C_Segmentation pSeg = null;
    public static C_Convert pConvert = null;
    public static C_Segmentation_Struct pStruct = null;
    public static int NetSO_Version=3;
    public static boolean bRead_Train_Data=true;
    public static String URL_Sample="/funnyai/mw_json_list_ai_example.php";
    public static String Field_ID="content_id";
    public static String Field_Sentence="title";
    public static String Field_Topic="a_tags";
    public static String Field_Memo="updator";
    public static String Path_Dinding="/root/funny_dingding.jar";
    public static int Train_Index=0;//除以10取其中余数为Train_Index的样本来训练
    
    public static boolean Init_Concept=false;//包含关系是否已经读取
    
    public static String Http_Head="http://";//zzz
    public static boolean bRead_Seg=true;//读取分词等信息
    public static String Path_Java="D:/Funny/FunnyAI/Java/";
    public static String path_www="/root/";
    
    public static String MapPath(String strPath){
        return AI_Var2.Path_Data+strPath;
    }
    
    public static String Path_FromRoot(String strPath){
        return AI_Var2.Path_Root+strPath;
    }
}
