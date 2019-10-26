
package com.funnyai.language;

import com.funnyai.NLP.C_Token;
import com.funnyai.NLP.C_Token_Link;
import com.funnyai.data.C_K_Int;
import com.funnyai.data.Treap;
import com.funnyai.fs.C_Map_Item;
import com.funnyai.net.Old.S_Net;
import com.funnyai.netso.C_Session_AI;
import com.funnyai.string.Old.S_Strings;
import static java.lang.System.out;
import java.util.ArrayList;
import org.dom4j.Node;

/**
 *
 * @author Administrator
 */
public class Tools_CN {

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
            case "cn.sentence": 
                strReturn=pSession.strSentence;
                break;
            case "cn.sentence.remove":
                strReturn=Get_Sentence_Remove(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
            case "cn.sentence.left":
                strReturn=Get_Sentence_Left(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
            case "cn.sentence.right":
                strReturn=Get_Sentence_Right(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
            case "cn.getword.child":
                strReturn=Get_Word_Child(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
            case "cn.getword":  //查找某个Token，以及这个Token下面的某个Token
                strReturn=Get_Word(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
            case "cn.getfrom":
                strReturn=Get_From(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
            case "cn.getto":
                strReturn=Get_To(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
        }
        return strReturn;
    }
    
    
    /**
     * 
     * @param pSession
     * @param pTreap_PNode
     * @param Function_Call
     * @param strFunction
     * @param pNode_Input
     * @param pMap_Item
     * @return 
     */
    public static String Get_To(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            C_Map_Item pMap_Item){
        if (pSession.pArray==null) return "{none}";
        
        String strFrom="";
        String strType = "";
        C_Function_Return pReturn=null;
        
        pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);

        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    strFrom = pReturn.pList.get(i).toString();
                    break;
                case 1:
                    strType = pReturn.pList.get(i).toString();
                    break;
            }
        }

        String url="http://www.funnyai.com/funnyai/json_read_node_to.php?from="
                +S_Strings.URL_Encode(strFrom)+"&from_type="+S_Strings.URL_Encode(strType);
        System.out.println(url);
        String strReturn=S_Net.http_get(url);
        return strReturn;//{none}
    }
    
    
    /**
     * 
     * @param pSession
     * @param pTreap_PNode
     * @param Function_Call
     * @param strFunction
     * @param pNode_Input
     * @param pMap_Item
     * @return 
     */
    public static String Get_From(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            C_Map_Item pMap_Item){
        if (pSession.pArray==null) return "{none}";
        
        String strTo="";
        String strType = "";
        C_Function_Return pReturn=null;
        
        pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);

        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    strTo = pReturn.pList.get(i).toString();
                    break;
                case 1:
                    strType = pReturn.pList.get(i).toString();
                    break;
            }
        }

        String url="http://www.funnyai.com/funnyai/json_read_node_from.php?to="
                +S_Strings.URL_Encode(strTo)+"&from_type="+S_Strings.URL_Encode(strType);
        System.out.println(url);
        String strReturn=S_Net.http_get(url);
        return strReturn;//{none}
    }
    
    /**
     * 
     * @param pSession
     * @param pTreap_PNode
     * @param Function_Call
     * @param strFunction
     * @param pNode_Input
     * @param pMap_Item
     * @return 
     */
    public static String Get_Sentence_Remove(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            C_Map_Item pMap_Item){
        if (pSession.pArray==null) return "{none}";
        
        String strFinds="";
        C_Function_Return pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);

        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    strFinds = pReturn.pList.get(i).toString();
                    break;
            }
        }
        
        String[] strSplit=strFinds.split(",");
        C_Token_Link pLink;
        C_Token pToken;
        ArrayList pArray=pSession.pArray;
        String strReturn="";
        
        Treap pTreap=new Treap();
        for (int m=0;m<strSplit.length;m++){
            boolean bFind=false;
            for (int i = 0; i< pArray.size(); i++) {
                pLink = (C_Token_Link) pArray.get(i);
                for (int j = 0; j <pLink.Count(); j++) {
                    pToken = pLink.Item(j);
                    for (int k = 0; k <pToken.Count(); k++) {
                        if (strSplit[m] == null ? pToken.Item(k) == null : strSplit[m].equals(pToken.Item(k))) {
                            bFind=true;
                            for (int x=pToken.iStart;x<pToken.iEnd;x++){
                                out.println("insert:"+x);
                                pTreap.insert(new C_K_Int(x), x);
                            }
                            break;
                        }
                    }
                    if (bFind) break;
                }
                if (bFind) break;
            }
        }
        pLink = (C_Token_Link) pArray.get(0);
        out.println("pArray.get(0)");
        out.println(pLink.strFrom);// .toString());
        for (int j = 0; j <pLink.Count(); j++) {
            pToken = pLink.Item(j);
            out.println("pLink.Item("+j+").Name:");
            out.println(pToken.Name);
            if (pTreap.find(new C_K_Int(pToken.iStart))!=null){
                out.println("not_none:"+pToken.iStart);
            }else{
                out.println("none:"+pToken.iStart);
                strReturn += Tools_SYS.SYS_Any(pToken) + " ";
            }
        }

        return strReturn;
    }

    
    public static String Get_Sentence_Left(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            C_Map_Item pMap_Item){
        if (pSession.pArray==null) return "{none}";
        
        String strFind="";
        String strToken = "";
        int iIndex =0;
        C_Function_Return pReturn=null;
        
        pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);

        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    strFind = pReturn.pList.get(i).toString();
                    break;
                case 1:
                    iIndex = Integer.valueOf(pReturn.pList.get(i).toString());
                    break;
                case 2:
                    strToken = pReturn.pList.get(i).toString();
                    break;
            }
        }

        int iCount = 0;
        C_Token_Link pLink;
        C_Token pToken,pToken2;
        ArrayList pArray=pSession.pArray;
        String strReturn="";
        
        for (int i = 0; i< pArray.size(); i++) {
            pLink = (C_Token_Link) pArray.get(i);
            for (int j = 0; j <pLink.Count(); j++) {
                pToken = pLink.Item(j);
                for (int k = 0; k <pToken.Count(); k++) {
                    if (strFind == null ? pToken.Item(k) == null : strFind.equals(pToken.Item(k))) {
                        iCount++;
                        if (iCount == iIndex) {
                            if ("".equals(strToken)) {
                                for (int m=0;m<j;m++){
                                    C_Token pToken3 = pLink.Item(m);
                                    strReturn += Tools_SYS.SYS_Any(pToken3) + " ";
                                }
                                return strReturn.trim();
                            }else {
                                for (int ii = 0; ii < pArray.size(); ii++) {
                                    pLink = (C_Token_Link) pArray.get(ii);
                                    for (int jj = 0; jj <pLink.Count(); jj++) {
                                        pToken2 = pLink.Item(jj);
                                        if (pToken2.iEnd > pToken.iStart) {
                                            continue ;
                                        }
                                        for (int kk = 0; kk <pToken2.Count(); kk++) {
                                            if (pToken2.Item(kk) == null ? strToken == null : pToken2.Item(kk).equals(strToken)) {
                                                return Tools_SYS.SYS_Any(pToken2);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return "";//{none}
    }
    
    public static String Get_Sentence_Right(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            C_Map_Item pMap_Item){
        if (pSession.pArray==null) return "{none}";
        
        String strFind="";
        String strToken = "";
        int iIndex =0;
        C_Function_Return pReturn=null;
        
        pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);

        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    strFind = pReturn.pList.get(i).toString();
                    break;
                case 1:
                    iIndex = Integer.valueOf(pReturn.pList.get(i).toString());
                    break;
                case 2:
                    strToken = pReturn.pList.get(i).toString();
                    break;
            }
        }

        int iCount = 0;
        C_Token_Link pLink;
        C_Token pToken,pToken2;
        ArrayList pArray=pSession.pArray;
        String strReturn="";
        
        for (int i = 0; i< pArray.size(); i++) {
            pLink = (C_Token_Link) pArray.get(i);
            for (int j = 0; j <pLink.Count(); j++) {
                pToken = pLink.Item(j);
                for (int k = 0; k <pToken.Count(); k++) {
                    if (strFind == null ? pToken.Item(k) == null : strFind.equals(pToken.Item(k))) {
                        iCount++;
                        if (iCount == iIndex) {
                            if ("".equals(strToken)) {
                                for (int m=j + 1;m<pLink.Count();m++){
                                    C_Token pToken3 = pLink.Item(m);
                                    strReturn += Tools_SYS.SYS_Any(pToken3) + " ";
                                }
                                return strReturn.trim();
                            }else {
                                for (int ii = 0; ii < pArray.size(); ii++) {
                                    pLink = (C_Token_Link) pArray.get(ii);
                                    for (int jj = 0; jj <pLink.Count(); jj++) {
                                        pToken2 = pLink.Item(jj);
                                        if (pToken2.iStart < pToken.iEnd) {
                                            continue ;
                                        }
                                        for (int kk = 0; kk <pToken2.Count(); kk++) {
                                            if (pToken2.Item(kk) == null ? strToken == null : pToken2.Item(kk).equals(strToken)) {
                                                return Tools_SYS.SYS_Any(pToken2);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return "";//{none}
    }
    
        
            
    /**
     * 查找某个Token，以及这个Token下面的某个Token
     * @param pSession
     * @param pTreap_PNode
     * @param Function_Call
     * @param strFunction
     * @param pNode_Input
     * @param pMap_Item
     * @return 
     */
    public static String Get_Word(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            C_Map_Item pMap_Item){
        if (pSession.pArray==null) return "{none}";
        
        String strFind="";
        String strToken = "";
        int iIndex =0;
        C_Function_Return pReturn=null;
        
        pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);

        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    strFind = pReturn.pList.get(i).toString();
                    break;
                case 1:
                    iIndex = Integer.valueOf(pReturn.pList.get(i).toString());
                    break;
                case 2:
                    strToken = pReturn.pList.get(i).toString();
                    break;
            }
        }

        int iCount = 0;
        C_Token_Link pLink;
        C_Token pToken,pToken2;
        ArrayList pArray=pSession.pArray;
        
        for (int i = pArray.size()-1; i>=0; i--) {
            pLink = (C_Token_Link) pArray.get(i);
            for (int j = 0; j <pLink.Count(); j++) {
                pToken = pLink.Item(j);
                for (int k = 0; k <pToken.Count(); k++) {
                    if (strFind == null ? pToken.Item(k) == null : strFind.equals(pToken.Item(k))) {
                        if (iCount == iIndex) {
                            if ("".equals(strToken)) {
                                return Tools_SYS.SYS_Any(pToken);
                            }else {
                                for (int ii = 0; ii < pArray.size(); ii++) {
                                    pLink = (C_Token_Link) pArray.get(ii);
                                    for (int jj = 0; jj <pLink.Count(); jj++) {
                                        pToken2 = pLink.Item(jj);
                                        if (pToken2.iStart >= pToken.iStart && pToken2.iEnd <= pToken.iEnd) {
                                            for (int kk = 0; kk <pToken2.Count(); kk++) {
                                                if (pToken2.Item(kk) == null ? strToken == null : pToken2.Item(kk).equals(strToken)) {
                                                    return Tools_SYS.SYS_Any(pToken2);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        iCount++;
                    }
                }
            }
        }
        
        return "{none}";
    }
    
    /**
     * 获取一个Token的子节点
     * @param pSession
     * @param pTreap_PNode
     * @param Function_Call
     * @param strFunction
     * @param pNode_Input
     * @param pMap_Item
     * @return 
     */
    public static String Get_Word_Child(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            C_Map_Item pMap_Item){
        if (pSession.pArray==null) return "{none}";
        
        String strFind = "";
        int iIndex = 0,iIndex2=0;
        C_Function_Return pReturn=null;
        
        pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);

        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    strFind = pReturn.pList.get(i).toString();
                    break;
                case 1:
                    iIndex = Integer.valueOf(pReturn.pList.get(i).toString());
                    break;
                case 2:
                    iIndex2 = Integer.valueOf(pReturn.pList.get(i).toString());
                    break;
            }
        }

        ArrayList pArray=pSession.pArray;
        int iCount = 0;
        C_Token_Link pLink;
        C_Token pToken;
        for (Object pArray1 : pArray) {
            pLink = (C_Token_Link) pArray1;
            for (int j = 0; j < pLink.Count() ; j++) {
                pToken = pLink.Item(j);// .Item[j];
                for (int k = 0; k <pToken.Count(); k++) {
                    if (strFind == null ? pToken.Item(k) != null : !strFind.equals(pToken.Item(k))) {
                        continue ;
                    }
                    iCount++;
                    if (iCount == iIndex) {
                        while (pToken.pChild.size()<2){
                            pToken=pToken.pChild.get(0);
                        }
                        if (iIndex2 < pToken.pChild.size() && iIndex2 >= 0) {
                            C_Token pToken2 = pToken.pChild.get(iIndex2);
                            return Tools_SYS.SYS_Any(pToken2);//AI_SYS.SYS_Any(pToken, pServerAI);
                        }
                        else if (iIndex2 < 0 && iIndex2 > pToken.pChild.size() * -1) {
                            C_Token pToken2 = pToken.pChild.get(pToken.pChild.size() + iIndex2);
                            return Tools_SYS.SYS_Any(pToken2);//, pServerAI);
                        }
                        else {
                            return "第3个参数超出数组下标!";
                        }
                    }
                }
            }
        }

        return "{none}";
    }
    

}
