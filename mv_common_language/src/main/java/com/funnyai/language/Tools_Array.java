
package com.funnyai.language;

import com.funnyai.data.Treap;
import com.funnyai.fs.C_Map_Item;
import com.funnyai.netso.C_Session_AI;
import org.dom4j.Node;
import org.json.JSONArray;

/**
 *
 * @author happyli
 */
public class Tools_Array {

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
            case "array.get": 
                strReturn=Array_Get(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
        }
        return strReturn;
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
    public static String Array_Get(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            C_Map_Item pMap_Item){

        String strArray = "";
        int iIndex = 0;
        C_Function_Return pReturn=null;
        
        pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);

        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    strArray = pReturn.pList.get(i).toString();
                    break;
                case 1:
                    iIndex = Integer.valueOf(pReturn.pList.get(i).toString());
                    break;
            }
        }

        JSONArray pArray = new JSONArray(strArray);
        
        if (iIndex>=0){
            return pArray.getString(iIndex);
        }else{
            return pArray.getString(pArray.length()+iIndex); //-1就是最后一个元素
        }
    }
    

}
