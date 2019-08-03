/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.common;

import com.funnyai.io.C_Property_File;
import com.funnyai.net.Old.S_Net;
import com.funnyai.string.Old.S_Strings;
import java.io.FileNotFoundException;
import static java.lang.System.out;

/**
 *
 * @author happyli
 */
public class Tools_Init {
    
    public static void Init(String strFileIni){
        C_Property_File pFile;
        try {
            pFile = new C_Property_File(strFileIni);
            
            AI_Var2.Path_Segmentation = pFile.Read("path.segmentation");//分词的路径
            AI_Var2.Path_Subset = pFile.Read("path.subset");//包含关系所在路径
            AI_Var2.Path_Struct = pFile.Read("path.struct");//struct的路径
            AI_Var2.Path_Filter = pFile.Read("path.filter");//过滤词汇的路径

            AI_Var2.Path_Dinding = pFile.Read("path.dingding");//根目录
            AI_Var2.Path_Root = pFile.Read("path.root");//根目录
            AI_Var2.Path_Data = pFile.Read("path.data");//训练目录
            AI_Var2.Path_Example = pFile.Read("path.example");//样本目录
            AI_Var2.Path_Log = pFile.Read("path.log");//日志目录
            AI_Var2.Path_Shell = pFile.Read("path");
            AI_Var2.Path_Java= pFile.Read("path.java");
            AI_Var2.path_www = pFile.Read("path.www");
            AI_Var2.Path_Rename = pFile.Read("path.rename");
            AI_Var2.Path_Send_Msg = pFile.Read("path.send_msg");
            
            
            
                
            AI_Var2.local_ip = S_Net.get_local_ip();
            AI_Var2.Site_NameNode = pFile.Read("site.namenode");
            AI_Var2.Apps_Pending = pFile.Read("hadoop.app.pending");
            AI_Var2.NLP_Robot = Integer.valueOf(pFile.Read("NLP_Robot"));

            try{
                AI_Var2.Path_Root= pFile.Read("path.root");
                for (int i=0;i<10;i++){
                    AI_Var2.Password[i]= pFile.Read("password_"+i);//password
                }
            }catch(Exception ex){

            }
    
            AI_Var2.Site = pFile.Read("site");//AI训练数据库
            AI_Var2.URL_Sample = pFile.Read("url.sample");//样本来源
            
            if (AI_Var2.URL_Sample==null || "".equals(AI_Var2.URL_Sample)){
                AI_Var2.URL_Sample="/funnyai/json_list_ai_example.php";
            }
            
            AI_Var2.Field_ID = pFile.Read("Field_ID");//
            AI_Var2.Field_Memo = pFile.Read("Field_Memo");//
            AI_Var2.Field_Sentence = pFile.Read("Field_Sentence");//
            AI_Var2.Field_Topic = pFile.Read("Field_Topic");//
            
            
            S_Net.Proxy_IP = pFile.Read("proxy.ip");//代理IP
            if (S_Net.Proxy_IP==null) S_Net.Proxy_IP="";
            S_Net.Proxy_Port = S_Strings.getIntFromStr(pFile.Read("proxy.port"),8080);//代理端口
            
            S_Net.Proxy_IP_Watch = pFile.Read("proxy.ip.watch");//代理IP
            if (S_Net.Proxy_IP_Watch==null) S_Net.Proxy_IP_Watch="";
            S_Net.Proxy_Port_Watch = S_Strings.getIntFromStr(pFile.Read("proxy.port.watch"),8080);//代理端口
            
        }catch(FileNotFoundException ex){
            S_Debug.Write_DebugLog("error", ex.toString());
        }
        
        out.println("init");
    }
}
