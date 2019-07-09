package com.funnyai.language;


import com.funnyai.cassandra.Cassandra_Tools;
import com.funnyai.data.Treap;
import com.funnyai.fs.C_Map_Item;
import com.funnyai.netso.C_Session_AI;
import org.dom4j.Node;

/**
 *
 * @author happyli
 */
public class Tools_Cassandra {

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
        
        switch (strFunction){
            case "cassandra.query":
                strReturn=Cassandra_Query(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
        }
        return strReturn;
    }
    
    
    public static String Cassandra_Query(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            C_Map_Item pMap_Item){

        String strIP="";
        String strUser = "";
        String strPassword="";
        String strSQL = "select * from magic_content_rev2.content_ref_rev4 "
                        + " Where account_key='ae2fdd870efa4e59acd57c2db50f0df6'"
                        + " AND product_key='YOGRD290'"
                        + " AND content_id='10226493428852';";
        String strFields="ref_content_ids";
        String strTypes="list";
        
        int Port =0;
        C_Function_Return pReturn  = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);

        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    strIP = pReturn.pList.get(i).toString();
                    break;
                case 1:
                    Port = Integer.valueOf(pReturn.pList.get(i).toString());
                    break;
                case 2:
                    strUser = pReturn.pList.get(i).toString();
                    break;
                case 3:
                    strPassword = pReturn.pList.get(i).toString();
                    break;
                case 4:
                    strSQL = pReturn.pList.get(i).toString();
                    break;
                case 5:
                    strFields = pReturn.pList.get(i).toString();
                    break;
                case 6:
                    strTypes = pReturn.pList.get(i).toString();
                    break;
            }
        }

        Cassandra_Tools pCassandra=new Cassandra_Tools();
        pCassandra.connect(strIP,Port,strUser,strPassword);
        
        String strReturn = pCassandra.Query(strSQL, strFields, strTypes, 10);
        pCassandra.close();
        
        return strReturn;
    }
    
    

}
