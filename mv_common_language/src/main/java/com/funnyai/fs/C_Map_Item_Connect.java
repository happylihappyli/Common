package com.funnyai.fs;

import com.funnyai.common.AI_Var2;
import com.funnyai.net.Old.S_Net;
import com.funnyai.string.Old.S_Strings;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author happyli
 */
public class C_Map_Item_Connect {
    public int From_ID=0;
    public int To_ID=0;
    public String From_Port="";
    public String To_Port="";

    C_Map_Item_Connect(int From_ID, int To_ID, String From_Port, String To_Port) {
        this.From_ID=From_ID;
        this.To_ID=To_ID;
        this.From_Port=From_Port;
        this.To_Port=To_Port;
    }
    
    public int From_Port_Index(){
        String strTmp="";
        if (this.From_Port.startsWith("left")){
            strTmp=this.From_Port.substring(4);
        }else if (this.From_Port.startsWith("right")){
            strTmp=this.From_Port.substring(5);
        }else if (this.From_Port.startsWith("top")){
            strTmp=this.From_Port.substring(3);
        }else if (this.From_Port.startsWith("bottom")){
            strTmp=this.From_Port.substring(6);
        }
        return S_Strings.getIntFromStr(strTmp,0);
    }
    
    
    public int To_Port_Index(){
        String strTmp="";
        if (this.To_Port.startsWith("left")){
            strTmp=this.To_Port.substring(4);
        }else if (this.To_Port.startsWith("right")){
            strTmp=this.To_Port.substring(5);
        }else if (this.To_Port.startsWith("top")){
            strTmp=this.To_Port.substring(3);
        }else if (this.To_Port.startsWith("bottom")){
            strTmp=this.To_Port.substring(6);
        }
        return S_Strings.getIntFromStr(strTmp,0);
    }
    
    /**
     * 读取所有这样的连接: From=ID，To=*
     * @param ID
     * @return 
     */
    public static ArrayList<C_Map_Item_Connect> Get_Connects_From(int ID) {
        ArrayList<C_Map_Item_Connect> pList=new ArrayList<>();
        
        String strURL = AI_Var2.Site
                + "/funnyscript/json_read_connect_from.php?id=" + ID;
        String strData = "";
        String strJSON = S_Net.http_GET(strURL,strData, "utf-8", "",20);
        int index = strJSON.indexOf("{");
        if (index > -1) {
            strJSON = strJSON.substring(index);
            Tools.Write_DebugLog("read.json",strJSON,false);
            try
            {
                JSONObject pObj = new JSONObject(strJSON);
                JSONArray pArray=pObj.getJSONArray("linkDataArray");
                for (int i=0;i<pArray.length();i++){
                    JSONObject pItem=(JSONObject) pArray.get(i);
                    int from = pItem.getInt("from");
                    int to = pItem.getInt("to");
                    String fromPort=pItem.getString("fromPort");
                    String toPort=pItem.getString("toPort");
                    pList.add(new C_Map_Item_Connect(from,to,fromPort,toPort));
                }
            }catch(Exception ex){
                System.out.println(ex.toString());
            }
        } else {
            System.out.println("read error, ID=" + ID);
        }
        
        return pList;
    }
    
    
    /**
     * 读取所有这样的连接: From=*，To=ID
     * @param ID
     * @return 
     */
    public static ArrayList<C_Map_Item_Connect> Get_Connects_To_Left(int ID) {
        ArrayList<C_Map_Item_Connect> pList=Get_Connects_To(ID);
        ArrayList<C_Map_Item_Connect> pList2=new ArrayList<>();
        
        for(int i=0;i<pList.size();i++){
            C_Map_Item_Connect pItem=pList.get(i);
            if (pItem.To_Port.startsWith("left")){
                pList2.add(pItem);
            }
        }
        
        return pList2;
    }
    
    
    
    /**
     * 读取所有这样的连接: From=*，To=ID
     * @param ID
     * @return 
     */
    public static ArrayList<C_Map_Item_Connect> Get_Connects_To(int ID) {
        ArrayList<C_Map_Item_Connect> pList=new ArrayList<>();
        
        String strURL = AI_Var2.Site
                + "/funnyscript/json_read_connect_to.php?id=" + ID;
        String strData = "";
        String strJSON = S_Net.http_GET(strURL, strData, "utf-8", "", 20);
        int index = strJSON.indexOf("{");
        if (index > -1) {
            strJSON = strJSON.substring(index);
            Tools.Write_DebugLog("read.json",strJSON,false);
            try
            {
                JSONObject pObj = new JSONObject(strJSON);
                JSONArray pArray=pObj.getJSONArray("linkDataArray");
                for (int i=0;i<pArray.length();i++){
                    JSONObject pItem=(JSONObject) pArray.get(i);
                    int from = pItem.getInt("from");
                    int to = pItem.getInt("to");
                    String fromPort=pItem.getString("fromPort");
                    String toPort=pItem.getString("toPort");
                    pList.add(new C_Map_Item_Connect(from,to,fromPort,toPort));
                }
            }catch(Exception ex){
                System.out.println(ex.toString());
            }
        } else {
            System.out.println("read error, ID=" + ID);
        }
        
        return pList;
    }
}
