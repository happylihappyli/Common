/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.language;

import com.funnyai.data.Treap;
import com.funnyai.fs.C_Job;
import com.funnyai.fs.C_Map_Item;
import com.funnyai.netso.C_Session_AI;
import org.dom4j.Node;

/**
 *
 * @author Administrator
 */
public class Tools_Write {

    /**
     * 写函数
     *
     * @param pSession
     * @param Function_Call
     * @param pTreap_PNode
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
        String strReturn = "write.node\uff1a";
        strFunction = strFunction.toLowerCase();
        switch (strFunction) {
            case "write.node": {
                int Node_ID = 0;
                String strProgram = "";

                C_Function_Return pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);

                for (int i = 0; i < pReturn.pList.size(); i++) {
                    switch (i) {
                        case 0:
                            Node_ID = Integer.valueOf(pReturn.pList.get(i).toString());
                            break;
                        case 1:
                            strProgram = pReturn.pList.get(i).toString();
                            break;
                    }
                }

                C_Job pJob = pMap_Item.pRun_Session.Read_Job(Function_Call, Node_ID);
                strReturn += Node_ID;
                pJob.Save_Program(strProgram);
            }
            break;
        }
        return strReturn;
    }

}
