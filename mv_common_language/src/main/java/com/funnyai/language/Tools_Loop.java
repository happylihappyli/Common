/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.language;

import com.funnyai.Math.Old.S_Math;
import com.funnyai.data.Treap;
import com.funnyai.fs.C_Map_Item;
import com.funnyai.netso.C_Session_AI;
import org.dom4j.Node;

/**
 * 循环
 * @author happyli
 */
public class Tools_Loop {
    
    public static String Function_Call(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call, 
            String strFunction, 
            Node pNode_Input, Node pNode_Output, 
            C_Map_Item pMap_Item) {
        String strReturn = "";
        strFunction = strFunction.toLowerCase();
        
        C_Function_Return pReturn=Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call,pNode_Input,pMap_Item);
        String strLine = "";
        
        for (int i=0;i<pReturn.pList.size();i++){
            switch (i) {
                case 0:
                    strLine = pReturn.pList.get(i).toString();
                    break;
            }
        }
        switch (strFunction) {
            case "loop.repeat":
                {
                    
                    System.out.println("math:"+strLine);
                    strReturn=S_Math.Calculate(strLine);
                }
                break;
            default:
                break;
        }
        return strReturn;
    }
}
