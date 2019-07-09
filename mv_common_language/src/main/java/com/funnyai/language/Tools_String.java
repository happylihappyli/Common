package com.funnyai.language;

import com.funnyai.common.AI_Var2;
import com.funnyai.data.Treap;
import com.funnyai.fs.C_Map_Item;
import com.funnyai.netso.C_Session_AI;
import com.funnyai.html.S_HTML;
import com.funnyai.net.Old.S_Net;
import com.funnyai.string.Old.S_Strings;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.dom4j.Node;
import org.json.*;

/**
 *
 * @author happyli
 */
public class Tools_String {

    /**
     * 字符串函数
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
        
        C_Function_Return pReturn = null;
        String strReturn = "";
        strFunction = strFunction.toLowerCase();
        switch (strFunction) {
            case "string.format.array": 
                strReturn=String_Format_Array(pSession,pTreap_PNode,Function_Call, strFunction,pNode_Input,pNode_Output,pMap_Item);
                break;
            case "string.format":
                strReturn=String_Format(pSession,pTreap_PNode,Function_Call, strFunction,pNode_Input,pNode_Output,pMap_Item);
                break;
            case "string.combine":
                strReturn=String_Combine(pSession,pTreap_PNode,Function_Call, strFunction,pNode_Input,pNode_Output,pMap_Item);
                break;
            case "string.replace":
                strReturn=String_Replace(pSession,pTreap_PNode,Function_Call, strFunction,pNode_Input,pNode_Output,pMap_Item);
                break;
            case "string.html.table.get":
                strReturn=String_HTML_Table_Get(pSession,pTreap_PNode,Function_Call, strFunction,pNode_Input,pNode_Output,pMap_Item);
                break;
            case "string.urlencode":
            {
                pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);
                if (pReturn.pList.size() > 0) {
                    strReturn = pReturn.pList.get(0).toString();
                }
                strReturn=S_Strings.URL_Encode(strReturn);
                String strOutput = pNode_Output.getText();//读取模板
                if (!"".equals(strOutput)){
                    strReturn=strOutput.replace("{0}", strReturn);
                }
            }
                break;
            case "string.split":
            {
                String strSep="\n";
                pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);
                if (pReturn.pList.size() > 0) {
                    strReturn = pReturn.pList.get(0).toString();
                }
                if (pReturn.pList.size() > 1) {
                    strSep = pReturn.pList.get(1).toString();
                }
                strSep = strSep.replace("\\n", "\n");
                String[] strSplit=strReturn.split(strSep);
                ArrayList<String> arrayList = new ArrayList<>();
                for (String strSplit1 : strSplit) {
                    arrayList.add(strSplit1);
                }
                JSONArray pArray=new JSONArray(arrayList);
                strReturn=pArray.toString();
            }
                break;
            case "string.html2text":
                pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);

                if (pReturn.pList.size() > 0) {
                    strReturn = pReturn.pList.get(0).toString();
                }
                strReturn = S_HTML.html2text(strReturn);
                break;
            case "string.json.read":
                strReturn=String_JSON_Read(pSession,pTreap_PNode,Function_Call, strFunction,pNode_Input,pNode_Output,pMap_Item);
                break;
            case "string.regex.get": {
                pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);
                String strTemplate="";
                String strPattern="";
                String strIndex="";
                if (pReturn.pList.size() > 2) {
                    strTemplate = pReturn.pList.get(0).toString();
                    strPattern = pReturn.pList.get(1).toString();
                    strIndex = pReturn.pList.get(2).toString();
                }
                Pattern pPattern = Pattern.compile(strPattern);
                int index = Integer.valueOf(strIndex);
                strReturn = strTemplate;
                Matcher pMatch = pPattern.matcher(strReturn);
                ArrayList<String> arrayList = new ArrayList<>();
                while (pMatch.find()) {
                    strReturn = pMatch.group(index);
                    arrayList.add(strReturn);
                }
                JSONArray pArray=new JSONArray(arrayList);
                strReturn=pArray.toString();
            }
            break;
        }
        return strReturn;
    }
    
    
    
    public static String String_JSON_Read(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            Node pNode_Output,
            C_Map_Item pMap_Item){
                

        String strReturn = "";
        C_Function_Return pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);
        String strInput="";
        String strProgram="";
        if (pReturn.pList.size() > 1) {
            strInput = pReturn.pList.get(0).toString();
            strProgram = pReturn.pList.get(1).toString();
        }
        System.out.println("input="+strInput);
        System.out.println("program="+strProgram);
        String msg=strInput;
        int index=msg.indexOf("{");
        
        if (index<0) return "";
        
        msg=msg.substring(index);

        JSONObject pObj = new JSONObject(msg);

        String[] strSplit=strProgram.split(";");
        for (int i=0;i<strSplit.length;i++){
            String[] strSplit2=strSplit[i].split(",");
            if (strSplit2.length==1){
                if (i==strSplit.length-1){
                    strReturn=pObj.getString(strSplit2[0]);
                }else{
                    pObj=pObj.getJSONObject(strSplit2[0]);
                }
            }else if (strSplit2.length==2){
                index=Integer.valueOf(strSplit2[1]);
                pObj=pObj.getJSONArray(strSplit2[0]).getJSONObject(index);
            }
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
     * @param pNode_Output
     * @param pMap_Item
     * @return 
     */
    public static String String_Format_Array(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            Node pNode_Output,
            C_Map_Item pMap_Item){
        
        String strTemplate = "";//读取模板
        String strArray = "";
        String strSep_Combine = "";

        C_Function_Return pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);

        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    strTemplate = pReturn.pList.get(i).toString();
                    break;
                case 1:
                    strArray = pReturn.pList.get(i).toString();
                    break;
                case 2:
                    strSep_Combine = pReturn.pList.get(i).toString();
                    break;
            }
        }
        
        return String_Format_Array(strTemplate,strArray,strSep_Combine);
    }
    
    
    /**
     * 读取电子表格中数据
     * @param ID
     * @return 
     */
    public static String Get_Table_From_ID(int ID){
        String strReturn="";
        String url=AI_Var2.Site+"/funnyai/json_read_grid.php?id="+ID;
        strReturn = S_Net.http_GET(url,"", "utf-8","",20);
        
        JSONObject pObject = new JSONObject(strReturn);
        strReturn=pObject.getJSONArray("data").getJSONObject(0).getString("Content");
        
        JSONArray pArray = new JSONArray(strReturn);
        pArray=pArray.getJSONObject(0).getJSONArray("rows");
        
        
        ArrayList<ArrayList<String>> arrayList = new ArrayList<>();
        for (int i=0;i<pArray.length();i++){
            JSONObject pObject2=pArray.getJSONObject(i);
            JSONArray pArray2=pObject2.getJSONArray("columns");
            ArrayList<String> pLine= new ArrayList<>();
            for (int j=0;j<pArray2.length();j++){
                if (pArray2.getJSONObject(j).has("value")){
                    String strValue=pArray2.getJSONObject(j).getString("value");
                    pLine.add(strValue);
                }
            }
            arrayList.add(pLine);
        }
        JSONArray pArray2=new JSONArray(arrayList);
        strReturn=pArray2.toString();
        
        return strReturn;
    }
    
    public static String String_Format_Array(String strTemplate,String strArray,String strSep_Combine){
        JSONArray pArray = null;
        if (strArray.startsWith("@table#")){
            int ID=Integer.valueOf(strArray.substring(7));
            strArray=Get_Table_From_ID(ID);
        }
        pArray = new JSONArray(strArray);
        
        
        if ("".equals(strSep_Combine)) {
            strSep_Combine = "\n";
        } else {
            strSep_Combine = strSep_Combine.replace("\\n", "\n");
        }
        String strReturn = "";
        String strTmp;
        for (int i=0;i<pArray.length();i++) {
           strTmp = strTemplate;
            if (pArray.get(i) instanceof  JSONArray){
                JSONArray pArray2=pArray.getJSONArray(i);
                for (int j = 0; j <pArray2.length(); j++) {
                    strTmp = strTmp.replace("{" + j + "}", pArray2.getString(j));
                }
            }else{
                strTmp = strTmp.replace("{0}", pArray.getString(i));
            }
            if (i < pArray.length() - 1) {
                strReturn += strTmp + strSep_Combine;
            } else {
                strReturn += strTmp;
            }
        }
        return strReturn;
    }
    
    /**
     * 字符串格式化
     * @param pSession
     * @param pTreap_PNode
     * @param Function_Call
     * @param strFunction
     * @param pNode_Input
     * @param pNode_Output
     * @param pMap_Item
     * @return 
     */
    public static String String_Format(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            Node pNode_Output,
            C_Map_Item pMap_Item){
        String strReturn = pNode_Output.getText();//读取模板
        C_Function_Return pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);
        String strValue;
        for (int i = 0; i < pReturn.pList.size(); i++) {
            strValue = pReturn.pList.get(i).toString();
            strReturn = strReturn.replace("{" + i + "}", strValue);
        }
        System.out.println(strReturn);
        return strReturn;
    }
    
    public static String String_HTML_Table_Get(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            Node pNode_Output,
            C_Map_Item pMap_Item){
        String strReturn = "";//读取模板
        C_Function_Return pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);

        String strInput="";
        int Row_Index=0;
        int Col_Index=0;
        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    strInput = pReturn.pList.get(i).toString();
                    break;
                case 1:
                    Row_Index = Integer.valueOf(pReturn.pList.get(i).toString());
                    break;
                case 2:
                    Col_Index =  Integer.valueOf(pReturn.pList.get(i).toString());
                    break;
            }
        }
        String[][] pTable=S_HTML.HTML_TO_Array(strInput);
        
        if (Row_Index>=0 && Row_Index<pTable.length){
            if (Col_Index>=0 && Col_Index<pTable[0].length){
                strReturn=pTable[Row_Index][Col_Index];
            }
        }
        
        return strReturn;
    }

    
    public static void main(String[] args) {
        String html = "<table id=\"metricsoverview\">\n" +
"              <thead class=\"ui-widget-header\">\n" +
"                <tr>\n" +
"                  <th class=\"ui-state-default\">\n" +
"                    Apps Submitted\n" +
"                  <th class=\"ui-state-default\">\n" +
"                    Apps Pending\n" +
"                  <th class=\"ui-state-default\">\n" +
"                    Apps Running\n" +
"                  <th class=\"ui-state-default\">\n" +
"                    Apps Completed\n" +
"                  <th class=\"ui-state-default\">\n" +
"                    Containers Running\n" +
"                  <th class=\"ui-state-default\">\n" +
"                    Memory Used\n" +
"                  <th class=\"ui-state-default\">\n" +
"                    Memory Total\n" +
"                  <th class=\"ui-state-default\">\n" +
"                    Memory Reserved\n" +
"                  <th class=\"ui-state-default\">\n" +
"                    Active Nodes\n" +
"                  <th class=\"ui-state-default\">\n" +
"                    Decommissioned Nodes\n" +
"                  <th class=\"ui-state-default\">\n" +
"                    Lost Nodes\n" +
"                  <th class=\"ui-state-default\">\n" +
"                    Unhealthy Nodes\n" +
"                  <th class=\"ui-state-default\">\n" +
"                    Rebooted Nodes\n" +
"              <tbody class=\"ui-widget-content\">\n" +
"                <tr>\n" +
"                  <td>\n" +
"                    171762\n" +
"                  <td>\n" +
"                    0\n" +
"                  <td>\n" +
"                    23\n" +
"                  <td>\n" +
"                    171739\n" +
"                  <td>\n" +
"                    68\n" +
"                  <td>\n" +
"                    226 GB\n" +
"                  <td>\n" +
"                    500 GB\n" +
"                  <td>\n" +
"                    0 B\n" +
"                  <td>\n" +
"                    <a href=\"/cluster/nodes\">10</a>\n" +
"                  <td>\n" +
"                    <a href=\"/cluster/nodes/decommissioned\">0</a>\n" +
"                  <td>\n" +
"                    <a href=\"/cluster/nodes/lost\">2</a>\n" +
"                  <td>\n" +
"                    <a href=\"/cluster/nodes/unhealthy\">0</a>\n" +
"                  <td>\n" +
"                    <a href=\"/cluster/nodes/rebooted\">0</a>\n" +
"              </tbody>\n" +
"            </table>";
        
        String[][] pTable=S_HTML.HTML_TO_Array(html);
        System.out.println(pTable[1][1]);
    }
    
    /**
     * 字符串替换
     * @param pSession
     * @param pTreap_PNode
     * @param Function_Call
     * @param strFunction
     * @param pNode_Input
     * @param pNode_Output
     * @param pMap_Item
     * @return 
     */
    public static String String_Replace(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            Node pNode_Output,
            C_Map_Item pMap_Item){
        String strReturn = "";//读取模板
        C_Function_Return pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);

        String strInput="";
        String strFind="";
        String strReplace="";
        for (int i = 0; i < pReturn.pList.size(); i++) {
            switch (i) {
                case 0:
                    strInput = pReturn.pList.get(i).toString();
                    break;
                case 1:
                    strFind = pReturn.pList.get(i).toString();
                    break;
                case 2:
                    strReplace = pReturn.pList.get(i).toString();
                    break;
            }
        }
        strReturn=strInput.replace(strFind,strReplace);
        
        return strReturn;
    }
    
    /**
     * 字符串合并
     * @param pSession
     * @param Function_Call
     * @param pTreap_PNode
     * @param strFunction
     * @param pNode_Input 输出参数
     * @param pNode_Output
     * @param pMap_Item
     * @return 
     */
    public static String String_Combine(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call,
            String strFunction,
            Node pNode_Input,
            Node pNode_Output,
            C_Map_Item pMap_Item){
        String strReturn = "";//读取模板
        C_Function_Return pReturn = Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call, pNode_Input, pMap_Item);

        String strCombine = pMap_Item.Program_Param;// pNode_Output.getText();//读取模板
        if (!"".equals(strCombine) && strCombine!=null){
            strCombine=strCombine.replaceAll("\\\\n", "\n");
        }
        
        String strValue;
        for (int i = 0; i < pReturn.pList.size()-1; i++) {
            if (pReturn.pList.get(i)!=null){
                strValue = pReturn.pList.get(i).toString();
            }else{
                strValue="";
            }
            strReturn = strReturn+strValue+strCombine;
        }
        int k=pReturn.pList.size()-1;
        strValue = pReturn.pList.get(k).toString();
        strReturn = strReturn+strValue;
        
        return strReturn;
    }

    /**
     * 添加头部的空格
     * @param strValue
     * @param strHead
     * @return 
     */
    private static String AddHead(String strValue, String strHead) {
        String strReturn="";
        String[] strSplit=strValue.split("\n");
        for (int i=0;i<strSplit.length-1;i++){
            strReturn+=strHead+strSplit[i]+"\n";
        }
        int k=strSplit.length-1;
        strReturn+=strHead+strSplit[k];
        return strReturn;
    }

}
