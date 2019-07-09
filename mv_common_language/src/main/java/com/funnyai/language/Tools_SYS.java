
package com.funnyai.language;

import com.funnyai.data.Treap;
import com.funnyai.data.C_K_Int;
import com.funnyai.NLP.C_Token;
import com.funnyai.common.AI_Var2;
import com.funnyai.common.S_Command;
import com.funnyai.fs.C_Map_Item;
import com.funnyai.fs.C_Program_Node;
import com.funnyai.io.C_File;
import com.funnyai.io.Old.S_File;
import com.funnyai.netso.C_Session_AI;
import com.funnyai.string.Old.S_Strings;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *
 * @author happyli
 */
public class Tools_SYS {

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
            case "sys.qq": 
                strReturn=pSession.QQ;
                break;
            case "sys.train": 
                strReturn=File_List(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
            case "sys.seg.download":
                Thread_Download_Seg p=new Thread_Download_Seg();
                p.strPath=AI_Var2.Path_Segmentation;//AI_Var2.MapPath("/Data/Segmentation/"
                p.start();
                strReturn="正在下载！";
                break;
            case "sys.topic":
                //strReturn=File_Save(Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
            case "sys.shell":
                strReturn=SYS_Shell(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
            case "sys.switch":
                strReturn=SYS_Switch(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
            case "sys.any":
                //strReturn=SYS_Any(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
        }
        return strReturn;
    }
    
    
     public static String SYS_Any(C_Token pToken){
        String strReturn = "";
        String strTmp2 = "";
        String strTmp = "";
        if (pToken.pChild.size() > 0) {
            strReturn = "";
            for (C_Token pChild : pToken.pChild) {
                strTmp = SYS_Any(pChild);
                strReturn = strReturn + strTmp + " ";
            }
        }
        else {
            strReturn = pToken.Name;
            if ((!"".equals(strTmp2))) {
                strReturn = strTmp2;
            }
        }
        if ((!"".equals(strReturn))) {
            strReturn = strReturn.trim();
        }
        if ((!"".equals(strReturn))) {
            strReturn = S_Strings.ReplaceNLP_Invert(strReturn);
        }
        return strReturn;
    }
     
     
    /**
     * 切换函数
     * @param pSession
     * @param pTreap_PNode
     * @param Function_Call
     * @param strFunction
     * @param pNode_Input
     * @param pMap_Item
     * @return 
     */
    public static String SYS_Switch(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            C_Map_Item pMap_Item){
        
        String strReturn="";
        
        Element element=(Element) pNode_Input.selectSingleNode("v1");
        
        String strSwitch;//要判断的条件
        
        String strCall = element.attributeValue("call");
            
        if (!"".equals(strCall) && strCall!=null) {
            C_Program_Node node= (C_Program_Node) pTreap_PNode.find(new C_K_Int(Integer.valueOf(strCall)));
            strSwitch = pMap_Item.Run_Program(pSession,pTreap_PNode,pMap_Item.pRun_Session,Function_Call,0,null,node,null);
        } else {
            strSwitch = element.getText();
        }

        int Count=S_Strings.getIntFromStr(pNode_Input.selectSingleNode("count").getText(),0);
        
        boolean bRun=false;
        for (int i=2;i<=Count;i++){
            element=(Element) pNode_Input.selectSingleNode("v"+i);
            String strParam = element.attributeValue("param");
            if (strSwitch.equals(strParam)){
                bRun=true;
                strCall = element.attributeValue("call");
                if (!"".equals(strCall) && strCall!=null) {
                    C_Program_Node node= (C_Program_Node) pTreap_PNode.find(new C_K_Int(Integer.valueOf(strCall)));
                    strReturn = pMap_Item.Run_Program(
                            pSession,pTreap_PNode,pMap_Item.pRun_Session, 
                            Function_Call,0,null,node,null);
                } else {
                    strReturn = element.getText();
                }
            }
            if (bRun==false){
                if ("{else}".equals(strParam)){
                    bRun=true;
                    strCall = element.attributeValue("call");
                    if (!"".equals(strCall) && strCall!=null) {
                        C_Program_Node node= (C_Program_Node) pTreap_PNode.find(new C_K_Int(Integer.valueOf(strCall)));
                        strReturn = pMap_Item.Run_Program(
                                pSession,pTreap_PNode,pMap_Item.pRun_Session, 
                                Function_Call,0,null,node,null);
                    } else {
                        strReturn = element.getText();
                    }
                }
            }
        }
        
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
    public static String SYS_Shell(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            C_Map_Item pMap_Item){
        
        String strShell="";
        String strReturn="";
        C_Function_Return pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);
        
        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    strShell = pReturn.pList.get(i).toString();
                    break;
            }
        }

        try {
            System.out.println(strShell);
            List pList=S_Command.RunShell_Return2(strShell);
            strReturn ="shell运行成功！\n";
            for (Object pList1 : pList) {
                strReturn += pList1;
            }
        } catch (Exception ex) {
            Logger.getLogger(Tools_SYS.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        String strReturn="";
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
        strReturn="文件已保存！"+strPath;
        
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
