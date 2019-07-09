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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Node;

/**
 * 数学
 * @author happyli
 */
public class Tools_Math {
    /**
     * 字符串函数
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
            case "math.cal":
                strReturn=S_Math.Calculate(strLine);
                break;
            default:                // math.sin,或 math.java.sin
                Class c;
                try {
                    c = Class.forName("java.lang.Math");
                    String[] strSplit=strFunction.split("\\.");
                    String strMethod=strSplit[strSplit.length-1];
                    Method m = c.getMethod(strMethod, new Class[] {double.class});
                    double input = Double.valueOf(strLine);
                    double result=(double) m.invoke(c, new Object[] {input});
                    strReturn=result+"";
                } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(Tools_Math.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
        }
        return strReturn;
    }
}
