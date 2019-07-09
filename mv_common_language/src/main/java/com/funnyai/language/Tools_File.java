/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.language;

import com.funnyai.data.Treap;
import com.funnyai.common.AI_Var2;
import com.funnyai.io.C_File;
import com.funnyai.io.Old.S_File;
import com.funnyai.io.Old.S_File_Text;
import com.funnyai.net.Old.S_Net;
import com.funnyai.fs.C_Map_Item;
import com.funnyai.fs.Tools;
import com.funnyai.netso.C_Session_AI;
import com.funnyai.string.Old.S_Strings;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.dom4j.Node;

/**
 *
 * @author happyli
 */
public class Tools_File {

    /**
     * 写函数
     *
     * @param pSession
     * @param pTreap_PNode
     * @param Function_Call
     * @param strFunction
     * @param pNode_Input
     * @param pNode_Output
     * @param pMap_Item
     * @return
     */
    public static String Function_Call(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input, Node pNode_Output,
            C_Map_Item pMap_Item) {
        String strReturn = "";
        strFunction = strFunction.toLowerCase();
        

        switch (strFunction) {
            case "file.save.db"://zzz
                strReturn=File_Save_DB(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
            case "file.list": 
                strReturn=File_List(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
            case "file.save":
                strReturn=File_Save(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
            case "file.read":
                strReturn=File_Read(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
            case "file.exists":
                strReturn=File_Exists(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
            case "file.exists.hadoop":
                strReturn=File_Exists_Hadoop(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
            case "file.replace.start":
                strReturn=File_Replace_Start(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
            case "file.replace.column"://替换某一列字符串
                strReturn=File_Replace_Start(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
            case "file.delete":
                strReturn=File_Delete(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
        }
        return strReturn;
    }
    
    
    /**
     * 替换文件中某个开头的行
     * @param pSession
     * @param pTreap_PNode
     * @param Function_Call
     * @param strFunction
     * @param pNode_Input
     * @param pMap_Item
     * @return 
     */
    public static String File_Replace_Column(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            C_Map_Item pMap_Item){
        
        String strPath="";
        String strPath2="";
        String strReturn="替换某一列";
        String strSep="";
        String strSepCombine="";
        String strFind="";
        String strReplace="";
        int index=0;//第几列
        
        C_Function_Return pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);
        
        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    strPath = pReturn.pList.get(i).toString();
                    break;
                case 1:
                    strPath2 = pReturn.pList.get(i).toString();
                    break;
                case 2:
                    strSep = pReturn.pList.get(i).toString();//分隔符
                    break;
                case 3:
                    index = Integer.valueOf(pReturn.pList.get(i).toString());//分隔符
                    break;
                case 4:
                    strSepCombine = pReturn.pList.get(i).toString();//分隔符
                    break;
                case 5:
                    strFind = pReturn.pList.get(i).toString();
                    break;
                case 6:
                    strReplace = pReturn.pList.get(i).toString();
                    break;
            }
        }

        Tools.Write_DebugLog("file.replace.column","file="+strPath);
        
        strReturn=S_File.File_Replace_Column(strPath,strPath2,strSep,index,strSepCombine,strFind,strReplace);
        System.out.println(strReturn);
        return strReturn;
    }
    
    
    
    /**
     * 替换文件中某个开头的行
     * @param pSession
     * @param pTreap_PNode
     * @param Function_Call
     * @param strFunction
     * @param pNode_Input
     * @param pMap_Item
     * @return 
     */
    public static String File_Replace_Start(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            C_Map_Item pMap_Item){
        
        String strPath="";
        String strReturn="替换文件";
        String strStart="";
        String strReplace="";
        
        C_Function_Return pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);
        
        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    strPath = pReturn.pList.get(i).toString();
                    break;
                case 1:
                    strStart = pReturn.pList.get(i).toString();
                    break;
                case 2:
                    strReplace = pReturn.pList.get(i).toString();
                    break;
            }
        }

        Tools.Write_DebugLog("file.replace.start","file"+strPath);
        
        if (S_File.Exists(strPath)){
            S_File.File_Replace_With_Start(strPath, strStart, strReplace);
        }else{
            strReturn="没有这个文件";
        }

        return strReturn;
    }
    
    
    /**
     * 删除文件
     * @param pSession
     * @param pTreap_PNode
     * @param Function_Call
     * @param strFunction
     * @param pNode_Input
     * @param pMap_Item
     * @return 
     */
    public static String File_Delete(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            C_Map_Item pMap_Item){
        
        String strPath="";
        String strReturn="删除文件";
        
        C_Function_Return pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);
        
        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    strPath = pReturn.pList.get(i).toString();
                    break;
            }
        }

        Tools.Write_DebugLog("fs.delete","file.exists="+strPath);
        
        if (S_File.Exists(strPath)){
            S_File.Delete(strPath);
        }else{
            strReturn="没有这个文件";
        }

        return strReturn;
    }
    
    
    public static String File_Exists_Hadoop(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            C_Map_Item pMap_Item){
        
        String strPath="";
        String strReturn="";
        
        C_Function_Return pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);
        
        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    strPath = pReturn.pList.get(i).toString();
                    break;
            }
        }

        Tools.Write_DebugLog("fs.run","file.exists.hadoop="+strPath);
        
        if (Tools.Hadoop_File_Exists(strPath)==0){
            strReturn="1";
        }else{
            strReturn="0";
        }
        return strReturn;
    }
            
    public static String File_Exists(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            C_Map_Item pMap_Item){
        
        String strPath="";
        String strReturn="";
        
        
        C_Function_Return pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);
        
        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    strPath = pReturn.pList.get(i).toString();
                    break;
            }
        }

        Tools.Write_DebugLog("fs.run","file.exists="+strPath);
        
        if (S_File.Exists(strPath)){
            strReturn="1";
        }else{
            strReturn="0";
        }

        return strReturn;
    }
    
    public static String File_Save_DB(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            C_Map_Item pMap_Item){
        
        String strReturn="";
        String strPath = "";
        String strID="";
        String strEncode="utf-8";
        C_Function_Return pReturn=null;
        
        pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);

        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    strPath = pReturn.pList.get(i).toString();
                    break;
                case 1:
                    strID = pReturn.pList.get(i).toString();
                    break;
                case 2:
                    strEncode = pReturn.pList.get(i).toString();
                    break;
            }
        }

        

        try {
            boolean bFileExist = S_File.Exists(strPath);
            if (bFileExist == true) {
                InputStreamReader pFS = new InputStreamReader(
                        new FileInputStream(strPath), strEncode);
                BufferedReader pSR = new BufferedReader(pFS); // 文件输入流为

                String strLine = pSR.readLine();
                if (strLine.length() > 1) {
                    if ((int) strLine.charAt(0) == 65279) {
                        strLine = strLine.substring(1);
                    }
                }

                int field_count=0;
                int iCount=0;
                String[] strSplit=null;
                while (strLine != null){
                    String strData = "";
                    strSplit=strLine.split(",");
                    int iValue=0;
                    for (int i=0;i<strSplit.length;i++){
                        if (strSplit[i].equals("")==false){
                            iValue=1;
                        }
                    }
                    if (iValue==1){
                        iCount+=1;
                        if (iCount==1){
                            field_count=strSplit.length;
                            String strURL = AI_Var2.Site
                                    + "/funnyscript/create_table.php?id=" + strID
                                    + "&count="+field_count;
                            strData=S_Net.http_GET(strURL,strData, "utf-8", "", 20);
                            strReturn += strData;
                        }else{
                            String strURL =  AI_Var2.Site
                                    + "/funnyscript/data_save.php?id=" + strID
                                    +"&count="+field_count+"&a="+S_Strings.URL_Encode(strLine);
                            strData=S_Net.http_GET(strURL,strData, "utf-8", "", 20);
                            strReturn += strData;
                        }
                        if (strData.indexOf("Error:")>0){
                            System.out.println(strLine);
                            strReturn += strLine+"<br>";
                        }
                        System.out.println(iCount);
                        System.out.println(strData);
                        strReturn += "<br>";
                    }
                    strLine = pSR.readLine();
                }
                pSR.close();
                pFS.close();
            } else {
                strReturn = "";  // strFile
            }
        } catch (IOException ex) {

        }
        return strReturn;
    }
    
    
    /**
     * 文件读取
     * @param pSession
     * @param pTreap_PNode
     * @param Function_Call
     * @param strFunction
     * @param pNode_Input
     * @param pMap_Item
     * @return 
     */
    public static String File_Read(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            C_Map_Item pMap_Item){
        
        String strPath="";
        String strReturn="";
        String strEncode="utf-8";
        
        C_Function_Return pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);
        
        int MaxLine=800;
        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    strPath = pReturn.pList.get(i).toString();
                    break;
                case 1:
                    strEncode=pReturn.pList.get(i).toString();
                    break;
                case 2:
                    MaxLine=Integer.valueOf(pReturn.pList.get(i).toString());
                    break;
            }
        }

        strReturn=S_File_Text.Read(strPath,strEncode,MaxLine);
        return strReturn;
    }
    
    /**
     * 文件保存
     * @param pSession
     * @param pTreap_PNode
     * @param Function_Call
     * @param strFunction
     * @param pNode_Input
     * @param pMap_Item
     * @return 
     */
    public static String File_Save(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            C_Map_Item pMap_Item){
        
        String strPath="";
        String strContent="";
        String strEncode="utf-8";
        
        C_Function_Return pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);
        
        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    strPath = pReturn.pList.get(i).toString();
                    break;
                case 1:
                    strContent = pReturn.pList.get(i).toString();
                    break;
                case 2:
                    strEncode=pReturn.pList.get(i).toString();
            }
        }

        C_File pFile=S_File.Write_Begin(strPath,false,strEncode);
        S_File.Write_Line(pFile, strContent);
        pFile.Close();
        String strReturn="文件已保存！path="+strPath;
        
        return strReturn;
    }
    
    /**
     * 罗列某个目录的子文件，或子目录
     * @param pSession
     * @param pTreap_PNode
     * @param Function_Call
     * @param strFunction
     * @param pNode_Input
     * @param pMap_Item
     * @return 
     */
    public static String File_List(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            C_Map_Item pMap_Item){
        
        String strPath="";
        String strReturn="";
        String strFilter="";
        C_Function_Return pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);
        
        int iType=0;
        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    strPath = pReturn.pList.get(i).toString();
                    break;
                case 1:
                    strFilter = pReturn.pList.get(i).toString();
                    break;
                case 2:
                    String strTmp=pReturn.pList.get(i).toString();
                    if (!"".equals(strTmp)){
                        iType = Integer.valueOf(strTmp);
                    }
                    break;
            }
        }

        File[] strFile= S_File.GetFiles(strPath, strFilter);
        for (File strFile1 : strFile) {
            if (iType==0) {
                if (strFile1.isDirectory() == false) {
                    strReturn += strFile1.getName() + "\n";
                }
            } else {
                if (strFile1.isDirectory()) {
                    strReturn += strFile1.getName() + "\n";
                }
            }
        }
        if (strReturn.endsWith("\n")){
            strReturn=strReturn.substring(0,strReturn.length()-1);
        }
        return strReturn;
    }

}
