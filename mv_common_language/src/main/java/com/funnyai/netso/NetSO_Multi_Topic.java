
package com.funnyai.netso;

import com.funnyai.data.C_K_Str;
import com.funnyai.data.Treap;
import com.funnyai.data.TreapEnumerator;
import com.funnyai.data.C_K_Int;
import com.funnyai.data.C_K_Double;
import com.funnyai.io.S_File_Text;
import com.funnyai.io.Old.S_File;
import com.funnyai.Segmentation.*;
import com.funnyai.NLP.*;
import com.funnyai.Math.Old.S_Math;
import com.funnyai.common.AI_Var;
import com.funnyai.common.AI_Var2;
import com.funnyai.net.Old.S_Net;
import com.funnyai.string.Old.S_Strings;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.*;
import org.json.*;

/**
 * 这个和NetSO的区别是，可以有多个Topic,只要一个分类对了即可
 * @author happyli
 */
public class NetSO_Multi_Topic {

    public Treap pTreapFilter = new Treap();//  '要过滤的词汇组成的Treap
    
    
    public String Path_Concept="";//包含关系所在的路径
    public C_Concept_Net2 pNet = new C_Concept_Net2();
    
    public Treap pTreapActive = new Treap();//激活的C_Active集合
//    public C_Segmentation pSeg = null;
//    public C_Convert pConvert = null;
//    public C_Segmentation_Struct pStruct = null;

    // 权重数据
    public Treap pData2 = new Treap();

    public boolean bReadFilter = false;

    ArrayList<String> pListFilter = new ArrayList<>();
    ArrayList pListNoExpand = new ArrayList();

    public NetSO_Multi_Topic(String Path_Concept ) {
//        this.pSeg = pSeg;
//        this.pConvert = pConvert;
//        this.pStruct = pStruct;
        this.Path_Concept = Path_Concept;
    }
    
    public boolean bInit=false;
    public void InitFilter(boolean bFromWeb,String ID){
        if (this.bInit){
            return ;
        }
        bInit=true;
        
        String strName;
        String strURL = "https://www.funnyai.com/funnyai/json_list_ai_filter.php";
        String strData;
        
        int p = 1;
        while (p > 0) {
            strData = "id="+ID+"&p=" + p;
            p++;
//            String strReturn = S_Net.sendPost(strURL, "POST", strData, "utf-8", "",false);
            String strReturn;
            if (bFromWeb){
                strReturn = S_Net.http_post(strURL,strData);//, "utf-8", "",false);
                S_File_Text.Write(AI_Var2.Path_Filter+p+".txt", strReturn,"utf-8");
            }else{
                strReturn = S_File_Text.Read(AI_Var2.Path_Filter+p+".txt", "utf-8",1000);
            }
            
            int IndexStr = strReturn.indexOf("{");
            if (IndexStr == -1){
                break;
            }
            strReturn = strReturn.substring(IndexStr);
            
            JSONObject pJobject = new JSONObject(strReturn);
            JSONArray token = pJobject.getJSONArray("data");
            int Count = pJobject.getInt("Count");
            for (int k=0;k<token.length();k++){
                JSONObject t=token.getJSONObject(k);
                strName = t.getString("Name");
                strName = strName.toLowerCase();
                pTreapFilter.insert(new C_K_Str(strName),strName);
            }
            if (Count < 100){
                break;
            }
        }
    }

    public void Filter_Topic(Treap pTreap_Topic) {
        C_Concept2 pConceptMain=null;

        TreapEnumerator p = pNet.pTreap.Elements(true);
        while (p.HasMoreElements()){
            pConceptMain = (C_Concept2) p.NextElement();
            if (pConceptMain.Name.startsWith("Topic.")){
                if (pTreap_Topic.find(new C_K_Str(pConceptMain.Name.substring(6)))==null){
                    pConceptMain.pInputs = new Treap();
                }
            }
        }
    }

    public String Replace_Some(String strLine) {
        for (int i = 0; (i <= 9); i++) {
            strLine = strLine.replace(String.valueOf('０' + i), String.valueOf('0' + i));
        }
        for (int i = 0; (i <= 25); i++) {
            strLine = strLine.replace(String.valueOf('ａ' + i), String.valueOf('a' + i));
        }
        for (int i = 0; (i <= 25); i++) {
            strLine = strLine.replace(String.valueOf('Ａ' + i), String.valueOf('A' + i));
        }
        return strLine;
    }
    
    /**
     * 分析句子
     * @param pRobot
     * @param pSAI
     * @param strSentence_Sep
     * @return 
     */
    public ArrayList Get_Token_List(I_Robot pRobot, C_Session_AI pSAI, String strSentence_Sep) {
        strSentence_Sep = Replace_Some(strSentence_Sep);
        if ("".equals(strSentence_Sep)) {
            return null;
        }
        ArrayList pArray = new ArrayList();
        String strReturn = strSentence_Sep;
        C_Token_Link pLink = new C_Token_Link();
        pLink.Init(strReturn);
        int iMax = pLink.Count();
        C_Token_Link pLink_Old;
        Treap pTreap = new Treap();

        C_Token pToken;
        C_Word_Convert pConvert_Word;
        Treap pTreap_Active = new Treap();
        C_Convert_AI pConvert_AI = new C_Convert_AI();
        for (int i = 0; i< pLink.Count(); i++) {
            pToken = pLink.Item(i);// Item[i];
            Treap pTreapTmp = AI_Var2.pConvert.GetTreap(pToken.Name);
            if (pTreapTmp!=null) {
                TreapEnumerator p2 = pTreapTmp.Elements();
                while (p2.HasMoreElements()) {
                    pConvert_Word = (C_Word_Convert) p2.NextElement();
                    pTreap_Active.insert(new C_K_Int(pConvert_Word.ID), pConvert_Word);
                }
            }
        }
        while (true) {
            pLink_Old = new C_Token_Link();
            pLink_Old.Sentence = pLink.Sentence;
            pLink_Old.Copy(pLink);
            C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);
            if (pArray.size()> iMax * 1.5 + 10) {// 分析递归太多
                break; //Warning!!! Review that break works as 'Exit Do' as it could be in a nested instruction like switch
            }
            //  'Convert一次就Struct一次，否则有的匹配就错过了
            //  '比如 {某时间段} {n点} {n分}
            C_Token_Link_and_Add p = pConvert_AI.getConvert2(AI_Var2.pConvert, pTreap_Active, pLink);// .getConvert2(pConvert, pTreap_Active, pLink, bAdd);
            if (p.bAdd) {
                pLink=p.pLink;
                if ("".equals(pLink.strFrom)) {
                    pLink.strFrom = "Convert";
                }
                C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);
            }

            if (pLink.Count() > 1) {
                pLink = AI_Var2.pStruct.Segmentation_List3(pLink); //匹配3个
                if (("".equals(pLink.strFrom))) {
                    pLink.strFrom = "Struct3";
                }
                C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);
                pLink = AI_Var2.pStruct.Segmentation_List2(pLink); //匹配2个
                if ("".equals(pLink.strFrom)) {
                    pLink.strFrom = "Struct2";
                }
                C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);
            }
            if (pLink_Old.Equal(pLink)) {
                break; 
            }
        }

        pLink = (C_Token_Link) pArray.get(pArray.size() - 1);

        return pArray;
    }
    
    /**
     * 得到句子的特征
     * @param pRobot
     * @param strSentence
     * @return 
     */
    public ArrayList<C_Struct_Active> ITSKeys(I_Robot pRobot, String strSentence){//, String strKeys_Filter) {

        if (strSentence.equals("")) {
            return null;
        }

        Treap pTreap = new Treap();
        String strLine = "";
        ArrayList<C_Token_Link> pArray = new ArrayList<>();
        String strReturn="";
        try{
            strReturn = AI_Var2.pSeg.Segmentation_List(strSentence,0,false,false);
        }
        catch(Exception ex){
            System.out.println(ex.toString());
        }
        C_Token_Link pLink_Old;
        C_Token_Link pLink = new C_Token_Link();
        pLink.Init(strReturn);
        C_Convert_AI pConvert_AI = new C_Convert_AI();
        Treap pTreap_Active = new Treap();
        //激活的结构
        C_Token pToken;
        C_Word_Convert pConvert_Word;

        for (int i = 0; i < pLink.Count(); i++) {
            pToken = pLink.Item(i);
            Treap pTreapTmp = (Treap) AI_Var2.pConvert.pTreapActive.find(new C_K_Str(pToken.Name));
            if (pTreapTmp != null) {
                TreapEnumerator p2 = pTreapTmp.Elements();
                while (p2.HasMoreElements()) {
                    pConvert_Word = (C_Word_Convert) p2.NextElement();
                    pTreap_Active.insert(new C_K_Int(pConvert_Word.ID), pConvert_Word);
                }
            }
        }

        while (true) {
            pLink_Old = new C_Token_Link();
            pLink_Old.Sentence = strSentence;
            pLink_Old.Copy(pLink);
            C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);

            //分析递归太多。
            if (pArray.size() > 100) {
                break; // TODO: might not be correct. Was : Exit Do
            }

            C_Token_Link_and_Add p=null;
            while (true) {
                p = pConvert_AI.getConvert2(AI_Var2.pConvert, pTreap_Active, pLink);
                pLink=p.pLink;
                if (pLink.strFrom.equals("")) {
                    pLink.strFrom = "Convert";
                }
                C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);
                if (p.bAdd == false) {
                    break; // TODO: might not be correct. Was : Exit Do
                }
            }

            ////////////Struct/////////
            pLink = AI_Var2.pStruct.Segmentation_List3(pLink);
            if (pLink.strFrom.equals("")) {
                pLink.strFrom = "Struct";
            }
            C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);

            pLink = AI_Var2.pStruct.Segmentation_List2(pLink);
            if (pLink.strFrom.equals("")) {
                pLink.strFrom = "Struct";
            }
            C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);

            if (pLink_Old.Equal(pLink)) {
                break; // TODO: might not be correct. Was : Exit Do
            }
        }

//        pLink = (C_Token_Link) pArray.get(pArray.size() - 1);

        Treap pTreapKey = new Treap();
        C_Token_Key pTokenKey = null;

        C_Token pToken1, pToken2;
        for (C_Token_Link pArray1 : pArray) {
            pLink = pArray1;
            for (int i = 0; i < pLink.Count(); i++) {
                pToken1 = pLink.Item(i);
                for (int j = i + 1; j < pLink.Count(); j++) {
                    pToken2 = pLink.Item(j);
                    strLine = pToken1.Name + "&" + pToken2.Name;
                    
                    if (AI_Var2.pStruct.pTreap_Active_MultiKey.find(new C_K_Str(strLine)) != null){
                        strLine = pToken2.Name + "&" + pToken1.Name;
                        if (AI_Var2.pStruct.pTreap_Active_MultiKey.find(new C_K_Str(strLine)) != null){
                            pTokenKey = new C_Token_Key(strLine, pToken1);
                            pTreapKey.insert(new C_K_Str(pTokenKey.Key), pTokenKey);
                        }
                    }
                }
            }
        }

        TreapEnumerator p = pTreap.Elements(true);
        while ((p.HasMoreElements())) {
            pTokenKey = (C_Token_Key) p.NextElement();
            if (!pTokenKey.Key.contains(" ") && pTreapFilter.find(new C_K_Str(pTokenKey.Key.toLowerCase())) == null) {
                int index=pTokenKey.Key.indexOf("@");
                if (index>0){
                    String strKey=pTokenKey.Key.substring(0,index)+"@*";
                    if (pTreapFilter.find(new C_K_Str(strKey.toLowerCase())) == null){
                        strKey="*"+pTokenKey.Key.substring(index);
                        if (pTreapFilter.find(new C_K_Str(strKey.toLowerCase())) == null){
                            pTreapKey.insert(new C_K_Str(pTokenKey.Key), pTokenKey);
                        }
                    }
                }else{
                    if (S_Strings.isNumeric(pTokenKey.Key)==false){
                        pTreapKey.insert(new C_K_Str(pTokenKey.Key), pTokenKey);
                    }
                }
            }
        }

//        String[] strSplit = strKeys_Filter.split(",");
//        for (String strSplit1 : strSplit) {
//            pTreapKey.remove(new C_K_Str(strSplit1));
//        }

        ArrayList<C_Struct_Active> pArrayList = new ArrayList<>();
        p = pTreapKey.Elements(true);
        while ((p.HasMoreElements())) {
            pTokenKey = (C_Token_Key) p.NextElement();
            if (pTokenKey.Key.startsWith("{any}@")) {
            } else {
                if ((pTokenKey.Key.length() < 20)) {
                    C_Struct_Active pKey=new C_Struct_Active(pTokenKey.Key,1.0,pTokenKey);
                    pArrayList.add(pKey);//pTokenKey.strKey + "|1.0");
                }
            }
        }
        return pArrayList;
        
    }
    
    
    public ArrayList<C_Power2> AI_Topics(
            ArrayList<C_Struct_Active> pKeys,
            double dbAdjuste,
            Object pCallBack) {

        pTreapActive = new Treap();

        setKeys(pKeys,pCallBack);

        ArrayList<C_Power2> pListR=new ArrayList<>();
        ArrayList pList = getTopic(0);
        
        for (int i=0;i<pList.size()-1;i++){
            C_Active2 pA = (C_Active2) pList.get(i);
            C_Power2 pPower = new C_Power2();
            pPower.strKey = pA.pConcept.Name.substring("Topic.".length());
            pPower.dbPower = pA.dbPower;
            pPower.pActive = pA;
            if (pList.size() > 1){
                pA = (C_Active2) pList.get(i+1);//第二个和第一个激活程度差
                pPower.dbPower = pPower.dbPower - pA.dbPower;
            }
            pListR.add(pPower);
        }
        return pListR;
    }
    

    public C_Power2 AI_Topic(
            ArrayList<C_Struct_Active> pKeys,
            double dbAdjuste,
            Object pCallBack) {

        pTreapActive = new Treap();

        setKeys(pKeys,pCallBack);

        ArrayList pList = getTopic(0);
        C_Power2 pPower = new C_Power2();
        if (pList.size() > 0) {
            C_Active2 pA = (C_Active2) pList.get(0);
            String strTopic  = pA.pConcept.Name.substring("Topic.".length());
            pPower.strKey = strTopic;
            pPower.dbPower = pA.dbPower;
            pPower.pActive = pA;
            if (pList.size() > 1) {
                pA = (C_Active2) pList.get(1);//第二个和第一个激活程度差
                pPower.dbPower = pPower.dbPower - pA.dbPower;
            }
        }
        return pPower;
    }

    public boolean Topic_Equal(
            String strTopic1,
            String strTopic2){
        if (strTopic1==null) return false;
        if (strTopic2==null) return false;
        
        String[] strSplit = strTopic1.split(":");
        strTopic1=strSplit[0];
        
        strSplit = strTopic2.split(":");
        strTopic2=strSplit[0];
        
        if (strTopic1.equals(strTopic2)){
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * 训练方法1 对的向量已近一些，错的向量移远一些
     * @param pKeys
     * @param strTopicTag_Pre
     * @param dbAdjust
     * @param pCallBack
     * @return 
     */
    public C_Train_Return TrainAI(
            ArrayList<C_Struct_Active> pKeys,
            String strTopicTag_Pre,
            double dbAdjust, Object pCallBack) {

        String[] strSplit;
        //strTopicReal_Pre 是没有过滤掉 :xx 后面的东西的
        String strTopicReal_Pre = AI_Topic(pKeys, dbAdjust, pCallBack).strKey;
        String strTopicTag=strTopicTag_Pre;
        
        ArrayList<C_Active2> pList=this.getTopic(strTopicTag_Pre);
        if (pList.size()>0){
            strTopicTag=pList.get(0).pConcept.Name;
            strSplit=strTopicTag.split("\\.");
            strTopicTag=strSplit[1];
        }
        
        strSplit = strTopicReal_Pre.split(":");
        String strTopicReal = strSplit[0];

        double dbValue = 0;
        C_Train_Return pReturn=new C_Train_Return();
        String strReturn = "";

        if ("".equals(strTopicTag_Pre)) {
            strTopicTag_Pre = "None";
        }
        if ("".equals(strTopicReal_Pre)) {
            strTopicReal_Pre = "None";
        }
        pReturn.strTopic=strTopicReal_Pre;
        
        ArrayList<C_Topic_Key_W> pListAdd = new ArrayList<>();
        boolean bEqual = Topic_Equal(strTopicReal,strTopicTag);
        if (bEqual==false) {
            if (pKeys.size() > 0) {
                C_Concept2 pConcept;
                C_Vector_Item pItem;
                C_Struct_Active pItem2;
                ArrayList<C_Vector_Item> pVector = new ArrayList<>();
                Treap pVectorTreap = new Treap();
                for (int i = 0; i < pKeys.size(); i++) {
                    pItem2 = pKeys.get(i);
                    dbValue =pItem2.dbValue;
                    pConcept = (C_Concept2) pNet.pTreap.find(new C_K_Str(pItem2.Name));
                    if (pConcept == null) {
                        pListAdd.add(new C_Topic_Key_W("Topic." + strTopicTag_Pre,pItem2.Name,dbAdjust));
//                        pNet.addConcept(pItem2.Name, "Topic." + strTopicTag_Pre, dbAdjust);
                        pConcept = (C_Concept2) pNet.pTreap.find(new C_K_Str(pItem2.Name));
                    }

                    pItem = new C_Vector_Item();
                    pItem.Key = pConcept.Name;
                    pItem.Weight = dbValue;
                    pVectorTreap.insert(new C_K_Str(pConcept.Name), pItem);
                }
                
                TreapEnumerator p=pVectorTreap.Elements();
                while(p.HasMoreElements()){
                    pItem = (C_Vector_Item) p.NextElement();
                    pVector.add(pItem);
                }

                strReturn += Adjust_Connect_Weight(pVector, strTopicTag, strTopicReal_Pre, dbAdjust,pListAdd);
            }
        }

        
        pNet.addConcept_Bat(pListAdd);
        
        //"训练结束"+strReturn; 
        if (bEqual) {
            pReturn.strResult="==";
        } else {
            pReturn.strResult=strReturn;
        }
        
        return pReturn;
    }

    public Treap GetVector(
            String strTopic,
            ArrayList<C_Connect2> pVector1) {
        
        Treap pVectorTreap=new Treap();
        C_Connect2 pConnect;
        C_Concept2 pTopic;
        pTopic = (C_Concept2) pNet.pTreap.find(new C_K_Str("Topic." + strTopic));
        if (pTopic != null) {
            TreapEnumerator p = pTopic.pInputs.Elements(true);//所有指向当前这个Topic概念的子集
            while (p.HasMoreElements()) {
                pConnect = (C_Connect2) p.NextElement();
                C_Concept2 pConcept = pConnect.pFrom;
                pVectorTreap.insert(new C_K_Str(pConcept.Name), pConnect);
            }
        }
        
        //转化为数组的形式
        pVector1.clear();
        TreapEnumerator p=pVectorTreap.Elements();
        while(p.HasMoreElements()){
            pConnect = (C_Connect2) p.NextElement();
            pVector1.add(pConnect);
        }
        return pVectorTreap;
    }

    
    
    /**
     * 调整连接权重 方法2
     * @param pVector
     * @param strTopic_Right
     * @param strTopic_Error
     * @param dbAdjust
     * @return 
     */
    public String Adjust_Connect_Weight_Method2(
            ArrayList<C_Vector_Item> pVector,
            String strTopic_Right,
            String strTopic_Error,
            double dbAdjust,
            ArrayList<C_Topic_Key_W> pListAdd) {

        String strReturn="";
        C_Connect2 pConnect;
        ArrayList<C_Connect2> pVector_Right = new ArrayList<>();//目标向量
        ArrayList<C_Connect2> pVector_Error = new ArrayList<>();//
        //步长=(目标向量-移动向量)/100

        Treap pVector_Right_Treap ;//目标向量
        Treap pVector_Error_Treap ;//

        pVector_Right_Treap=GetVector(strTopic_Right, pVector_Right);
        pVector_Error_Treap=GetVector(strTopic_Error, pVector_Error);


        //对的向量放大一点
        for (int i=0;i<pVector_Right.size();i++){
            pConnect =pVector_Right.get(i);
            if (pConnect != null) {
                pConnect.Weight = pConnect.Weight*(1+dbAdjust);
            } else {
                C_Topic_Key_W pTK=AdjustWeight(pConnect.pFrom.Name, "Topic." + strTopic_Right, 0.1);
                if (pTK!=null) pListAdd.add(pTK);
            }
        }
        
        
        //错的向量缩小一点
        //离错误的方向远 pVector_Error 远离 pVector
        if (!"None".equals(strTopic_Error)) { //如果相差很小就不移动了
            for (int i=0;i<pVector_Error.size();i++){
                pConnect =pVector_Error.get(i);
                if (pConnect != null) {
                    pConnect.Weight = pConnect.Weight/(1+dbAdjust);
                } else {
                    C_Topic_Key_W pTK=AdjustWeight(pConnect.pFrom.Name, "Topic." + strTopic_Error, -0.1);//
                    if (pTK!=null) pListAdd.add(pTK);
                }
            }
        }
  
        return strReturn;
    }

    //调整连接权重
    public String Adjust_Connect_Weight(
            ArrayList<C_Vector_Item> pVector,
            String strTopic_Right,
            String strTopic_Error,
            double dbAdjust,
            ArrayList<C_Topic_Key_W> pListAdd) {

        String strReturn="";
        C_Connect2 pConnect;
        ArrayList<C_Connect2> pVector_Right = new ArrayList<>();//目标向量
        ArrayList<C_Connect2> pVector_Error = new ArrayList<>();//
        //步长=(目标向量-移动向量)/100

        Treap pVector_Right_Treap ;//目标向量
        Treap pVector_Error_Treap ;//

        pVector_Right_Treap=GetVector(strTopic_Right, pVector_Right);
        pVector_Error_Treap=GetVector(strTopic_Error, pVector_Error);

        double Distance1 = Get_Vector_Length(pVector_Right_Treap);
        double Distance2 = Get_Vector_Length(pVector_Error_Treap);
        if (Distance1 > 0.001) { //TreapEnumerator p = pVector_Right.Elements();
            for (int i=0;i<pVector_Right.size();i++) {
                pConnect = pVector_Right.get(i);
                pConnect.Weight = pConnect.Weight / Distance1;
            }
        }

        if (Distance2 > 0.001) {
            for (int i=0;i<pVector_Error.size();i++) {
                pConnect = pVector_Error.get(i);
                pConnect.Weight = pConnect.Weight / Distance2;
            }
        }

        double Distance = 0;
        for (int i=0;i<pVector.size();i++){// p.HasMoreElements()) {
            C_Vector_Item pItem = pVector.get(i);//C_Vector_Item) p.NextElement();
            Distance += pItem.Weight * pItem.Weight;
        }
        Distance = Math.sqrt(Distance);

        if (Distance > 0.001) {//规范化
            for (int i=0;i<pVector.size();i++){
                C_Vector_Item pItem = pVector.get(i);// C_Vector_Item) p.NextElement();
                pItem.Weight = pItem.Weight / Distance;
            }
        }

        //移动到正确的方向 pVector_Right 往 pVector移动
        if (Distance1 > 0.001) { //如果相差很小就不移动了
            strReturn+=MoveVector(pVector, pVector_Right_Treap, strTopic_Right, dbAdjust,pListAdd);
        }

        //离错误的方向远 pVector_Error 远离 pVector
        if (!"None".equals(strTopic_Error)) { //如果相差很小就不移动了
            strReturn+=LeaveVector(pVector, pVector_Error_Treap, strTopic_Error, dbAdjust,pListAdd); // dbAdjust/ 10
        }
  
        //规范化
        Distance1 = Get_Vector_Length(pVector_Right_Treap);
        Distance2 = Get_Vector_Length(pVector_Error_Treap);

        if (Distance1 > 0.001) {
            for (int i=0;i<pVector_Right.size();i++) {
                pConnect = pVector_Right.get(i);
                pConnect.Weight = pConnect.Weight / Distance1;
            }
        }

        if (Distance2 > 0.001) {
            for (int i=0;i<pVector_Error.size();i++) {
                pConnect = pVector_Error.get(i);
                pConnect.Weight = pConnect.Weight / Distance2;
            }
        }
        
        return strReturn;
    }
    
    
    public String read(String strFile) {
        
        try {
            
            ArrayList<C_Topic_Key_W> pListAdd=new ArrayList<>();
            InputStreamReader pFS = new InputStreamReader(new FileInputStream(strFile), "UTF-8");
            BufferedReader pSR = new BufferedReader(pFS);// 文件输入流为

            String strLine;
            String[] strSplit;
            double dbScale;
            strLine = pSR.readLine();
            String strVersion="";
            if (strLine.startsWith("version:2:")){
                strVersion="2";
            }
            if (strLine.startsWith("version:3:")){
                strVersion="3";
            }
            strLine = pSR.readLine();
            while (strLine!=null){
                strSplit = strLine.split("\\|");
                if (strSplit.length > 2) {
                    dbScale = Double.valueOf(strSplit[2]);
                    if (Math.abs(dbScale) <  0.00001) {
                        // Me.pTreapFilter.insert(New C_K_Str(strSplit(0)), strSplit(0))
                    }else{
                        if (strVersion.equals("3")){
                            pListAdd.add(new C_Topic_Key_W(strSplit[1],strSplit[0],dbScale));
//                            add_Connect_to_Topic(strSplit[0], strSplit[1], dbScale);
                        }else if (strVersion.equals("2")){
                            pListAdd.add(new C_Topic_Key_W(strSplit[1],strSplit[0],dbScale));
//                            add_Connect_to_Topic(strSplit[0], strSplit[1], dbScale);
                            if (strSplit[1].endsWith(":2")){
                                pListAdd.add(new C_Topic_Key_W(strSplit[1].replaceAll(":2",":3"),strSplit[0],dbScale));
//                                add_Connect_to_Topic(strSplit[0], strSplit[1].replaceAll(":2",":3"), dbScale);
                            }
                        }else{
                            pListAdd.add(new C_Topic_Key_W(strSplit[1],strSplit[0],dbScale));
//                            add_Connect_to_Topic(strSplit[0], strSplit[1], dbScale);
                            pListAdd.add(new C_Topic_Key_W(strSplit[1]+":2",strSplit[0],dbScale));
//                            add_Connect_to_Topic(strSplit[0], strSplit[1]+":2", dbScale);
                            pListAdd.add(new C_Topic_Key_W(strSplit[1]+":3",strSplit[0],dbScale));
//                            add_Connect_to_Topic(strSplit[0], strSplit[1]+":3", dbScale);
                        }
                    }
                }
                strLine = pSR.readLine();
            }
            pSR.close();
            pFS.close();
            this.AddPoint_Bat(pListAdd);
            return "Read Finished";
        }
        catch (IOException | NumberFormatException ex) {
            return ex.toString();
        }
    }

    public String save(String strFile, boolean bFilter) {
        
        OutputStreamWriter pSW;
        FileOutputStream pFS;
        String strReturn;
        try { 
            pFS = new FileOutputStream(new File(strFile), false);
            pSW = new OutputStreamWriter(pFS, "utf-8");
            pSW.write("version:3:\n");
            TreapEnumerator p = pNet.pTreap.Elements();
            C_Concept2 pConcept;
            C_Concept2 pConceptMain;
            C_Concept2 pConceptTo;
            C_Connect2 pConnect;
            String strTo;
            String strTmp = "";
            TreapEnumerator p2;
            while (p.HasMoreElements()) {
                pConceptMain = (C_Concept2) p.NextElement();
                p2 = pConceptMain.pOutputs.Elements(true);
                while (p2.HasMoreElements()) {
                    pConnect = (C_Connect2) p2.NextElement();
                    pConcept = pConnect.pFrom;
                    pConceptTo = pConnect.pTo;
                    strTo = pConceptTo.Name;
                    if (strTo.startsWith("Topic.") && Math.abs(pConnect.Weight) > 0.0000001) {
                        pSW.write(pConcept.Name + "|"+ strTo + "|" + pConnect.Weight+"\n");
                    }
                }
                pSW.write("\n");
            }
            pSW.close();
            pFS.close();
            strReturn = strTmp;
        }catch (Exception ex){
            strReturn = ex.toString();
        }
        return strReturn;
    }


    /**
     * 设置激活的节点
     * @param pKeys
     * @param pCallBack 
     */
    public void setKeys(ArrayList<C_Struct_Active> pKeys,Object pCallBack) {
//        String[] strSplit2;
        for (C_Struct_Active pKey : pKeys) {
//            strSplit2 = pKey.split("\\|");
//            if (strSplit2.length > 1) {
//                dbValue = Double.valueOf(strSplit2[1]);
//            }else {
//                dbValue = 1;
//            }
            setItem(pTreapActive,pKey.Name, pKey.dbValue, pCallBack);
        }
    }

    
    //pTreapActive 最终激活的概念
    
    private void setItem(Treap pTreapActive, 
            String strActive, 
            double dbScale, Object pCallBack) {
        C_Concept2 pConcept = null;
        TreapEnumerator p,p2;
        C_Active2 pActiveTmp = null;
        
        C_Connect2 pConnect;
        p = pTreapActive.Elements(true);
        while (p.HasMoreElements()){
            pActiveTmp=(C_Active2) p.NextElement();
            pActiveTmp.bChecked = false;
        }
        Treap pTreapCheck = new Treap();
        Treap pTreapCheck2 = null;
        C_Concept2 pConceptActive = (C_Concept2) pNet.pTreap.find(new C_K_Str(strActive));
        if (pConceptActive == null){
            return;
        }
        
        C_Active2 pActive = pConceptActive.getActive(pTreapActive);
        pActive.dbPower = pActive.dbPower + 100 * dbScale;
//        pActive.bActive = true;
        
        //==========概念的扩张==========
//        if (pActive.pConcept.pTreapInput!=null) {
//            p = pActive.pConcept.pTreapInput.Elements(true);
//            while (p.HasMoreElements()){
//                pConnect = (C_Connect2) p.NextElement();
//                if (pConnect.Type == Connect_Type.Connect_Value.Class_Expand_Type){
//                    pConcept = pConnect.pFrom;
//                    pActiveTmp = pConcept.getActive(pTreapActive);
//                    pActiveTmp.dbPower = pActiveTmp.dbPower + pActive.dbPower * 0.9;
////                    pActiveTmp.bActive = true;
//                    pTreapCheck.insert(new C_K_Int(pActiveTmp.ID), pActiveTmp);
//                }
//            }
//        }

        pTreapCheck.insert(new C_K_Int(pActive.ID), pActive);
        for (int j = 0;j <4; j++) {
            p = pTreapCheck.Elements(true);
            pTreapCheck2 = new Treap();
            while (p.HasMoreElements()) {
                pActive = (C_Active2) p.NextElement();
                if (pActive.bChecked == false
                        && pActive.pConcept.pOutputs!=null) {

                    pActive.bChecked = true;
                        p2 = pActive.pConcept.pOutputs.Elements(true);
                        while (p2.HasMoreElements()) {
                            pConnect=(C_Connect2) p2.NextElement();
                            pConcept = pConnect.pTo;
                            pActiveTmp = pConcept.getActive(pTreapActive);
                            pActiveTmp.dbPower +=pActive.dbPower * pConnect.Weight;//* (0.6 * pConnect.dbScale);
                            pTreapCheck2.insert(new C_K_Int(pActiveTmp.ID), pActiveTmp);
                        }
                }
            }
            pTreapCheck = pTreapCheck2;
        }
    }
    
    public ArrayList getTopic(String strTopic) {
        C_Active2 pA;
        String strName;
        Treap pTreap = new Treap();
        TreapEnumerator p = pTreapActive.Elements(false);
        while (p.HasMoreElements()) {
            pA = (C_Active2) p.NextElement();
            strName = pA.pConcept.Name.toLowerCase();
            if (strName.startsWith("topic." + strTopic)) {
                pTreap.insert(new C_K_Double(pA.dbPower), pA);
            }
        }
        ArrayList pList = new ArrayList();
        p = pTreap.Elements(false);
        while (p.HasMoreElements()) {
            pA = (C_Active2) p.NextElement();
            pList.add(pA);
            if (pList.size() > 10) {
                break;
            }
        }
        
        return pList;
    }
    
    public ArrayList getTopic(double dbPower) {
        C_Active2 pA = null;
        String strName;
        Treap pTreap = new Treap();
        TreapEnumerator p = pTreapActive.Elements(false);
        while (p.HasMoreElements()) {
            pA = (C_Active2) p.NextElement();
            strName = pA.pConcept.Name.toLowerCase();
            if (pA.dbPower > dbPower && strName.startsWith("topic.")) {
                pTreap.insert(new C_K_Double(pA.dbPower), pA);
            }
        }
        ArrayList pList = new ArrayList();
        p = pTreap.Elements(false);
        while (p.HasMoreElements()) {
            pA = (C_Active2) p.NextElement();
            pList.add(pA);
            if (pList.size() > 10) {
                break;
            }
        }
        
        return pList;
    }
    
    /**
     * 清除最后一次激活的
     */
    public void clearActive(){
        TreapEnumerator p = pTreapActive.Elements(false);
        C_Active2 pA = null;
        //最后一次激活的C_Active 移除
        while (p.HasMoreElements()) {
            pA = (C_Active2) p.NextElement();
            pA.pConcept.pLast_Active=null;
        }
        pTreapActive=new Treap();
    }

    /**
     * pVector_Right 往 pVector 方向移动
     *
     * @param pVector
     * @param pVector_Right_Treap
     * @param strTopic
     * @param dbMove
     * @return 
     */
    public String MoveVector(
            ArrayList<C_Vector_Item> pVector, 
            Treap pVector_Right_Treap, String strTopic, double dbMove,
            ArrayList<C_Topic_Key_W> pListAdd) {
        C_Connect2 pConnect;
        double dbScale;
        C_Vector_Item pItem;

        StringBuilder strReturn =new StringBuilder();
        
        for (int i=0;i<pVector.size();i++){
            pItem =pVector.get(i);
            pConnect = (C_Connect2) pVector_Right_Treap.find(new C_K_Str(pItem.Key));
            if (pConnect != null) {
                dbScale = (pItem.Weight - pConnect.Weight);// / m;
                double delta =dbScale * dbMove;
                pConnect.Weight += delta;
                strReturn.append(pItem.Key).append("+=").append(delta);
            } else {
                dbScale = pItem.Weight;// / m;
                C_Topic_Key_W pTK=AdjustWeight(pItem.Key, "Topic." + strTopic, dbScale * dbMove);
                if (pTK!=null) pListAdd.add(pTK);
            }
        }
        return strReturn.toString();
    }

    /**
     * pVector_Error 远离 pVector
     *
     * @param pVector
     * @param pVector_Error_Treap
     * @param strTopic
     * @param dbMove
     * @param pListAdd
     * @return 
     */
    public String LeaveVector(ArrayList<C_Vector_Item> pVector,
            Treap pVector_Error_Treap, String strTopic, double dbMove,
            ArrayList<C_Topic_Key_W> pListAdd) {
        StringBuilder strReturn =new StringBuilder();
        C_Connect2 pConnect;
        double dbScale;
        C_Vector_Item pItem;
        for (int i=0;i<pVector.size();i++){
            pItem = pVector.get(i);//C_Vector_Item) p.NextElement();
            if (pVector_Error_Treap.find(new C_K_Str(pItem.Key)) != null) {
                pConnect = (C_Connect2) pVector_Error_Treap.find(new C_K_Str(pItem.Key));
                dbScale = (pConnect.Weight - pItem.Weight);// / m;
                double delta=dbScale * dbMove;
                pConnect.Weight += delta;
                strReturn.append(pItem.Key).append("+=").append(delta);
            }else{
                //zzz may error 
                //错误的向量远离
                C_Topic_Key_W pTK=new C_Topic_Key_W(strTopic,pItem.Key,-dbMove);
                pListAdd.add(pTK);
            }
        }
        return strReturn.toString();
    }

    public double Get_Vector_Length(Treap pVector1) {
        double Distance = 0;
        C_Connect2 pConnect;

        TreapEnumerator p = pVector1.Elements();
        while (p.HasMoreElements()) {
            pConnect = (C_Connect2) p.NextElement();
            Distance += pConnect.Weight * pConnect.Weight;
        }
        return Math.sqrt(Distance);
    }

    private C_Topic_Key_W AdjustWeight(String strFrom, String strTopic, double dbIncrease) {
        C_Topic_Key_W pTK=null;
        C_Concept2 pCActive = (C_Concept2) pNet.pTreap.find(new C_K_Str(strFrom));

        if (pCActive == null) {
//            add_Connect_to_Topic(strFrom, strTopic, dbIncrease);
//            return ;//strReturn
            pTK=new C_Topic_Key_W(strTopic,strFrom,dbIncrease);
            return pTK;//strReturn
        }

        boolean bFind = false;// '是否已经存在。
        C_Concept2 pConcept;
        C_Connect2 pConnect;
        TreapEnumerator p = pCActive.pOutputs.Elements();

        while (p.HasMoreElements()) {
            pConnect = (C_Connect2) p.NextElement();
            pConcept = pConnect.pTo;
            if (pConcept.Name == null ? strTopic == null : pConcept.Name.equals(strTopic)) {
                pConnect.Weight += dbIncrease;
                bFind = true;
            }
        }

        if (bFind == false) {
//            add_Connect_to_Topic(strFrom, strTopic, dbIncrease);
            pTK=new C_Topic_Key_W(strTopic,strFrom,dbIncrease);
            return pTK;//strReturn
        }
        return null;
    }

    /**
     * 
     * @param bFromWeb 
     */
    public void Init_Concept(boolean bFromWeb) {
        String SFrom;
        String STo;
        String Type;
        String strURL = "https://www.funnyai.com/funnyai/json_list_ai_node.php";
        String strData;
        String Path_Struct="";
        ArrayList<C_Topic_Key_W> pListAdd=new ArrayList<>();
        
        int iNext;
        int p = 0;
        while (p > -1) {
            p+=1;
            strData = "p=" + p;
            String strReturn;
            if (bFromWeb){
                strReturn = S_Net.http_post(strURL,strData);//, "utf-8", "",false);
                S_File_Text.Write(Path_Struct+p+".txt", strReturn, "utf-8");
            }else{
                strReturn = S_File_Text.Read(Path_Concept+p+".txt", "utf-8",1000);
            }
            int IndexStr = strReturn.indexOf("{");
            if (IndexStr == -1){
                break;
            }
            strReturn = strReturn.substring(IndexStr);
            
            String strLine;
            JSONObject pJobject = new JSONObject(strReturn);
            JSONArray token = pJobject.getJSONArray("data");
            int Count = pJobject.getInt("Count");
            for (int k=0;k<token.length();k++){
                JSONObject t=token.getJSONObject(k);
                SFrom = t.getString("SFrom");
                STo = t.getString("STo");
                Type = t.getString("Type");
                if ("zzz".equals(STo) || "zzz".equals(SFrom)) {
                    continue;
                }
                String strConvert ="";
                switch (Type.toUpperCase()) {
                    case "子集":
                        AI_Map2.MakeAMap(AI_Var2.pSeg,pNet,AI_Var2.pConvert,AI_Var2.pStruct,"CN.C", SFrom, STo, "", "1",pListAdd);
                        break;
                    case "元素":
                        AI_Map2.MakeAMap(AI_Var2.pSeg,pNet,AI_Var2.pConvert,AI_Var2.pStruct,"CN.E", SFrom, STo, "", "1",pListAdd);
                        if ("动词".equals(STo)){
                            strConvert = "{AGT.ACT.OBJ.类}";
                            iNext = 2;
                            strLine = "{" + SFrom + ".AGT.类} {"+ SFrom + ".类} {" + SFrom + ".OBJ.类}";
                            AI_Var2.pStruct.readLine_FromDic(AI_Var2.pConvert, strLine, strConvert, iNext,0);
                            AI_Var2.pConvert.read_fromDic(SFrom, "{"+ SFrom + ".类}", "");
                        }
                        break;
                    case "TAG":
                        iNext = 2;
//                        strConvert = "{"+ STo + ".类}";
//                        strLine = SFrom + " {" + STo + ".类.E}";
//                        pStruct_Tag.readLine_fromDic(AI_Var2.pConvert, strLine, strConvert, iNext);
                        break;
                    case "AGT":
                        if (S_Math.isNumeric(STo) == false) {
                            AI_Var2.pConvert.read_fromDic(SFrom, "{"+ STo + ".AGT.类}", STo);
                            AI_Var2.pConvert.read_fromDic("{" + SFrom + ".类}", "{"+ STo + ".AGT.类}", STo);
                            AI_Var2.pConvert.read_fromDic("{"+ SFrom + ".类.E}", "{"+ STo + ".AGT.类}", STo);
                        }
                        break;
                    case "OBJ":
                        if (S_Math.isNumeric(STo) == false) {
                            AI_Var2.pConvert.read_fromDic(SFrom, "{"+ STo + ".OBJ.类}", STo);
                            AI_Var2.pConvert.read_fromDic("{"+ SFrom + ".类}", "{"+ STo + ".OBJ.类}", STo);
                            AI_Var2.pConvert.read_fromDic("{"+ SFrom + ".类.E}", "{"+ STo + ".OBJ.类}", STo);
                        }
                        break;
                    case "属性":
                        strConvert = "{A的B}";
                        strLine = "{"+ STo + ".类.E} 的 " + SFrom;
                        AI_Var2.pStruct.readLine_FromDic(AI_Var2.pConvert, strLine, strConvert, 3,0);
                        strLine = "{"+ STo + ".类} 的 " + SFrom;
                        AI_Var2.pStruct.readLine_FromDic(AI_Var2.pConvert, strLine, strConvert, 3,0);
                        strLine = "{" + STo + ".类} 的 {"  + SFrom + ".类}";
                        AI_Var2.pStruct.readLine_FromDic(AI_Var2.pConvert, strLine, strConvert, 3,0);
                        strConvert = "{A的B}";
                        strLine = "{"  + STo + ".类.E} " + SFrom;
                        iNext = 2;
                        AI_Var2.pStruct.readLine_FromDic(AI_Var2.pConvert, strLine, strConvert, iNext,0);
                        strLine = "{"+ STo + ".类} " + SFrom;
                        iNext = 2;
                        AI_Var2.pStruct.readLine_FromDic(AI_Var2.pConvert, strLine, strConvert, iNext,0);
                        strLine = "{" + STo + ".类} {" + SFrom + ".类}";
                        iNext = 2;
                        AI_Var2.pStruct.readLine_FromDic(AI_Var2.pConvert, strLine, strConvert, iNext,0);
                        break;
                }
            }
            if (Count < 100) {
                break; 
            }
        }
    }
    
    
    /**
     * 批量添加概念
     * @param pList 
     */
    public void AddPoint_Bat(ArrayList<C_Topic_Key_W> pList) {
        ArrayList<C_Topic_Key_W> pList2=new ArrayList<>();
        for (int k=0;k<pList.size();k++){
            C_Topic_Key_W pTK=pList.get(k);
            
            if ("".equals(pTK.Key)) {
                continue;
            }

            if (pTreapFilter.find(new C_K_Str(pTK.Key.toLowerCase())) == null) {
                int index=pTK.Key.indexOf("@");
                if (index>0){
                    String strKey=pTK.Key.substring(0,index)+"@*";
                    if (pTreapFilter.find(new C_K_Str(strKey.toLowerCase())) == null){
                        strKey="*@"+pTK.Key.substring(index+1);
                        if (pTreapFilter.find(new C_K_Str(strKey.toLowerCase())) == null){
//                            pNet.addConcept(pTK.Key, pTK.Topic, pTK.dbWeight);//, Connect_Type.Connect_Value.Class_Type);
                            pList2.add(pTK);
                        }
                    }
                }else{
//                    pNet.addConcept(pTK.Key, pTK.Topic, pTK.dbWeight);
                    pList2.add(pTK);
                }
            }
        }
        
        pNet.addConcept_Bat(pList2);
        
    }
    
//    private void AddPoint(String strFrom, String strTo, double dbScale) {
//        if (strTo.startsWith("Topic.")){
//            return ;
//        }
//        if ("".equals(strFrom)) {
//            return ;
//        }
//        
//        if (pTreapFilter.find(new C_K_Str(strFrom.toLowerCase())) == null) {
//            int index=strFrom.indexOf("@");
//            if (index>0){
//                String strKey=strFrom.substring(0,index)+"@*";
//                if (pTreapFilter.find(new C_K_Str(strKey.toLowerCase())) == null){
//                    strKey="*"+strFrom.substring(index);
//                    if (pTreapFilter.find(new C_K_Str(strKey.toLowerCase())) == null){
//                        pNet.addConcept(strFrom, strTo, dbScale);//, Connect_Type.Connect_Value.Class_Type);
//                    }
//                }
//            }else{
//                pNet.addConcept(strFrom, strTo, dbScale);//, Connect_Type.Connect_Value.Class_Type);
//            }
//        }
//    }
    
//    public void add_Connect_to_Topic(String strFrom, String strTo, double dbScale) {
//        if (strTo.startsWith("Topic.")==false){
//            return ;
//        }
//        if ("".equals(strFrom)) {
//            return ;
//        }
//        
//        if (pTreapFilter.find(new C_K_Str(strFrom.toLowerCase())) == null) {
//            int index=strFrom.indexOf("@");
//            if (index>0){
//                String strKey=strFrom.substring(0,index)+"@*";
//                if (pTreapFilter.find(new C_K_Str(strKey.toLowerCase())) == null){
//                    strKey="*"+strFrom.substring(index);
//                    if (pTreapFilter.find(new C_K_Str(strKey.toLowerCase())) == null){
//                        pNet.addConcept(strFrom, strTo, dbScale);//, Connect_Type.Connect_Value.Class_Type);
//                    }
//                }
//            }else{
//                pNet.addConcept(strFrom, strTo, dbScale);//, Connect_Type.Connect_Value.Class_Type);
//            }
//        }
//    }

    

     public void initFilter(String strFile_Filter,String strFileNoExpand) {
        // pTreapKeys
        ReadToList(pListFilter, strFile_Filter);
        ReadToList(pListNoExpand, strFileNoExpand);
    }
    
    public void ReadToList(ArrayList pList, String strFile) {
        bReadFilter = true;
        if (S_File.Exists(strFile)) {
            InputStreamReader pFS = null;
            try {
                pFS = new InputStreamReader(new FileInputStream(strFile), "UTF-8");
                BufferedReader pSR = new BufferedReader(pFS);// 文件输入流为
                
                pList = new ArrayList();
                String strLine = pSR.readLine();
                while (strLine!=null){
                    if (!"".equals(strLine)){
                        pList.add(strLine);
                    }
                    strLine = pSR.readLine();
                }   
                pSR.close();
                pFS.close();
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(NetSO_Multi_Topic.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(NetSO_Multi_Topic.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    pFS.close();
                } catch (IOException ex) {
                    Logger.getLogger(NetSO_Multi_Topic.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }


}
