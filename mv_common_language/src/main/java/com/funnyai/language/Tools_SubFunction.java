/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.language;

import com.funnyai.data.Treap;
import com.funnyai.fs.C_Map_Item;
import com.funnyai.netso.C_Session_AI;
import java.util.ArrayList;
import java.util.List;
import org.dom4j.Node;

/**
 *
 * @author happyli
 */
public class Tools_SubFunction {

    /**
     * 子函数
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
        strFunction = strFunction.toLowerCase();
        String[] strSplit = strFunction.split("\\.");
        int Node_ID = 0;
        if (strSplit.length > 1) {
            Node_ID = Integer.valueOf(strSplit[1]);
        }

        C_Function_Return pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);
        List<String> pList=new ArrayList<>();
        for (int i = 0; i < pReturn.pList.size(); i++) {
            String strValue = pReturn.pList.get(i).toString();
            pList.add(strValue);
        }

        String strReturn = pMap_Item.Run_Program(
                pSession,pTreap_PNode,pMap_Item.pRun_Session,Function_Call,Node_ID,null,null,pList);
        return strReturn;
    }

}
