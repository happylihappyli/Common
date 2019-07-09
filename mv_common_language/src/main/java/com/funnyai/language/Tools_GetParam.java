/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.funnyai.language;

import com.funnyai.data.Treap;
import com.funnyai.data.C_K_Int;
import com.funnyai.fs.C_Map_Item;
import com.funnyai.fs.C_Program_Node;
import com.funnyai.netso.C_Session_AI;
import com.funnyai.string.Old.S_Strings;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *
 * @author happyli
 */
public class Tools_GetParam {
    /**
     * 获取输入变量
     * @param pSession AI处理的一些变量
     * @param pTreap_PNode
     * @param Function_Call
     * @param pNode_Input
     * @param pMap_Item
     * @return
     */
    public static C_Function_Return GetParam(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call, 
            Node pNode_Input,
            C_Map_Item pMap_Item) {

        C_Function_Return pFunction_Return=new C_Function_Return();
         
        int Count=0;
        if (pNode_Input.selectSingleNode("count")!=null){
            Count=S_Strings.getIntFromStr(pNode_Input.selectSingleNode("count").getText(),0);
        }
        
        for (int i=1;i<=Count;i++){
            Element element=(Element) pNode_Input.selectSingleNode("v"+i);
            String strValue;
            
            String strCall = element.attributeValue("call");
            
            if (!"".equals(strCall) && strCall!=null) {
                int iFunction=Integer.valueOf(strCall);
                C_Program_Node node= (C_Program_Node) pTreap_PNode.find(new C_K_Int(iFunction));
                strValue = pMap_Item.Run_Program(pSession,pTreap_PNode,pMap_Item.pRun_Session,Function_Call,0,null,node,null);
            } else {
                strValue = element.getText();
            }
            pFunction_Return.pList.add(strValue);
            
            String strParam = element.attributeValue("param");
            pFunction_Return.pListParam.add(strParam);
            
            
        }

        return pFunction_Return;
    }
}
