package com.funnyai.language;

import com.funnyai.data.Treap;
import com.funnyai.fs.C_Map_Item;
import com.funnyai.netso.C_Session_AI;
import org.dom4j.Node;

/**
 *
 * @author happyli
 */
public class Tools_FS {

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
            case "fs.run.get.param":
                strReturn=FS_Get_Param(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
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
    public static String FS_Get_Param(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            C_Map_Item pMap_Item){
        
        int ID=0;
        int index=1;
        
        C_Function_Return pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);
        
        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    ID = Integer.valueOf(pReturn.pList.get(i).toString());
                    break;
                case 1:
                    index = Integer.valueOf(pReturn.pList.get(i).toString());
                    break;
            }
        }
        
        return pMap_Item.pRun.GetParam(ID,index);
    }
    

}
