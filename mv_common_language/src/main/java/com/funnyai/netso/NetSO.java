
package com.funnyai.netso;

import com.funnyai.data.C_K_Str;
import com.funnyai.data.Treap;
import com.funnyai.data.TreapEnumerator;
import com.funnyai.data.C_K_Int;
import com.funnyai.data.C_K_Double;
import com.funnyai.netso_multi_thread.C_Robot;
import com.funnyai.netso_multi_thread.A_NetSO_Save;
import com.funnyai.io.S_File_Text;
import com.funnyai.io.C_File;
import com.funnyai.io.Old.S_File;
import com.funnyai.Segmentation.*;
import com.funnyai.NLP.*;
import com.funnyai.Math.Old.S_Math;
import com.funnyai.common.AI_Var2;
import com.funnyai.common.S_Debug;
import com.funnyai.fs.Tools;
import com.funnyai.io.S_file_sub;
import com.funnyai.net.Old.S_Net;
import java.util.ArrayList;
import org.json.*;
import static java.lang.System.out;
import java.util.concurrent.ConcurrentMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;

/**
 * 1.0
 * @author happyli
 */
public class NetSO {

    public Treap pTreapFilter = new Treap();//  '要过滤的词汇组成的Treap
    public Treap pTreapFilter_Sentence = new Treap();//  '要过滤的句子组成的Treap
    
    public C_Concept_Net2 pNet = new C_Concept_Net2();// 基本的包含关系不在这里，在 pNet0
    
    public Treap pTreapActive = new Treap();//激活的C_Active集合

    public A_NetSO_Save pNetSO_Save=null;
    public C_Robot pRobot=null;

    public Treap pData2 = new Treap(); // 权重数据
    public Treap<String> pTreap_Topic_Name=new Treap<>();//意图ID和意图名称的对应关系

    public boolean bReadFilter = false;
    public String Path_Subset = "";//包含关系数据所在目录

    ArrayList<String> pListFilter = new ArrayList<>();
    ArrayList pListNoExpand = new ArrayList();
    public double MinActive=20.0;

    /**
     * 
     * @param pRobot
     * @param pNetSO_Save
     * @param Path_Subset 
     */
    public NetSO(C_Robot pRobot,
                A_NetSO_Save pNetSO_Save,
                String Path_Subset){
        this.pRobot=pRobot;
        this.Path_Subset = Path_Subset;
        this.pNetSO_Save= pNetSO_Save;
    }
    
    
    /**
     * 初始化包含关系
     * @param Robot_ID
     * @param strTag
     */
    public void Init_Subset(String Robot_ID,String strTag) {
        if (AI_Var2.Init_Concept){
            return ;
        }
        AI_Var2.Init_Concept=true;
        
        String strFile =Path_Subset+"/subset.db";
        String strFile2 = strFile+"."+Robot_ID+"_"+strTag+".db";
        S_File.Copy2File(strFile, strFile2);
        
        out.println(strFile2);
        DB db = DBMaker.fileDB(strFile2).checksumHeaderBypass().make();// .make();
        ConcurrentMap map = db.hashMap("map").open();
        
        String SFrom;
        String STo;
        String Type;
        ArrayList<C_Topic_Key_W> pListAdd=new ArrayList<>();
        int iNext;
        
        int Size=(int) map.get("size");
        for(int i=0;i<=Size;i++){
            try {
                String strReturn=(String) map.get("k"+i);
                
                String strLine;
                try{
                    JSONArray token = new JSONArray(strReturn);
                    int Count = token.length();
                    for (int k=0;k<Count;k++){
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
                                if ("动词".equals(STo)){
                                    strConvert = "{AGT.ACT.OBJs}";
                                    iNext = 2;
                                    strLine = "{" + SFrom + ".AGTs} {"+ SFrom + "s} {" + SFrom + ".OBJs}";
                                    AI_Var2.pStruct.readLine_FromDic(AI_Var2.pConvert, strLine, strConvert, iNext,0);
                                    AI_Var2.pConvert.read_fromDic(SFrom, "{"+ SFrom + "s}", "");
                                }
                                break;
//                            case "AGT":
//                                if (S_Math.isNumeric(STo) == false){
//                                    AI_Var2.pConvert.read_fromDic(SFrom, "{"+ STo + ".AGTs}", STo);
//                                    AI_Var2.pConvert.read_fromDic("{" + SFrom + "s}", "{"+ STo + ".AGTs}", STo);
//                                }
//                                break;
                            case "OBJ":
                                if (S_Math.isNumeric(STo) == false){
//                                    AI_Var2.pConvert.read_fromDic(SFrom, "{"+ STo + ".OBJs}", STo);
//                                    AI_Var2.pConvert.read_fromDic("{"+ SFrom + "s}", "{"+ STo + ".OBJs}", STo);
                                }
                                break;
                            case "属性":
                                strConvert = "{A的B}";
                                strLine = "{"+ STo + "s} 的 " + SFrom;
                                AI_Var2.pStruct.readLine_FromDic(AI_Var2.pConvert, strLine, strConvert, 3,0);
                                strLine = "{" + STo + "s} 的 {"  + SFrom + "s}";
                                AI_Var2.pStruct.readLine_FromDic(AI_Var2.pConvert, strLine, strConvert, 3,0);
                                strConvert = "{A的B}";
                                strLine = "{"+ STo + "s} " + SFrom;
                                iNext = 2;
                                AI_Var2.pStruct.readLine_FromDic(AI_Var2.pConvert, strLine, strConvert, iNext,0);
                                strLine = "{" + STo + "s} {" + SFrom + "s}";
                                iNext = 2;
                                AI_Var2.pStruct.readLine_FromDic(AI_Var2.pConvert, strLine, strConvert, iNext,0);
                                break;
                        }
                    }
                    if (Count < 100) {
                        break; 
                    }
                }catch(JSONException ex){
                    ex.printStackTrace();
                }
            }catch(Exception ex){
                
            }
        }
        db.close();
        
        pNet.addConcept_Bat(pListAdd);
    }
    
//        while (p > -1) {
//            p+=1;
//            strData = "p=" + p;
//            String strReturn;
//            if (bFromWeb){
//                out.println("concept.from.web");
//                strReturn = S_Net.http_GET(strURL+"?"+strData,"", "utf-8", "",20);
//                S_File_Text.Write(AI_Var.Path_Subset+p+".txt",strReturn,"utf-8");
//            }else{
//                out.println("concept.from.local");
//                strReturn = S_File_Text.Read(AI_Var.Path_Subset+p+".txt", "utf-8",1000);
//            }
//            int IndexStr = strReturn.indexOf("[");
//            if (IndexStr == -1){
//                out.println("break no [ ");
//                break_count+=1;
//                if (break_count>5){
//                    break;
//                }
//            }else{
//                break_count=0;
//            }
//            strReturn =map.get("");// strReturn.substring(IndexStr);
//            
//        }
    
    public void InitFilter(boolean bFromWeb,String ID){
        String strName;
        String strURL = AI_Var2.Site+"/funnyai/json_list_ai_filter.php";
        String strData;
        
        if (bFromWeb){
            out.println("filter.from.web");
        }else{
            out.println("filter.from.local");
        }
        int p = 0;
        while (p > -1){
            p++;
            strData = "id=" + ID + "&p=" + p;
            String strReturn;
            if (bFromWeb){
                strReturn = S_Net.http_post(strURL,strData);//, "utf-8", "",false);
                S_File_Text.Write(AI_Var2.Path_Filter+ID+"_"+p+".txt",strReturn,"utf-8");
            }else{
                strReturn = S_File_Text.Read(AI_Var2.Path_Filter+ID+"_"+p+".txt","utf-8",1000);
            }
            
            int IndexStr = strReturn.indexOf("{");
            if (IndexStr == -1){
                break;
            }
            strReturn = strReturn.substring(IndexStr);
            try{
                JSONObject pJobject = new JSONObject(strReturn);
                JSONArray token = pJobject.getJSONArray("data");
                int Count = pJobject.getInt("Count");
                for (int k=0;k<token.length();k++){
                    JSONObject t=token.getJSONObject(k);
                    strName = t.getString("Name").trim();
                    strName = strName.toLowerCase();
                    System.out.println("Filter="+strName);

                    pTreapFilter.insert(new C_K_Str(strName),strName);
                }
                if (Count < 100){
                    break;
                }
            }catch(JSONException ex){
                S_Debug.Write_DebugLog("json_error",strReturn);
                S_Debug.Write_DebugLog("json_error",ex.toString());
                out.println(strReturn);
                ex.printStackTrace();
            }
        }
    }

    
    public void InitFilter_Sentence(boolean bFromWeb,String ID){
        
        String strName;
        String strURL = AI_Var2.Site+"/funnyai/json_list_ai_filter_sentence.php";
        String strData;
        
        int p = 0;
        while (p > -1) {
            p++;
            strData = "id="+ID+"&p=" + p;
            String strReturn;
            if (bFromWeb){
                System.out.println("filter.sentence.from.web");
                strReturn = S_Net.http_post(strURL,strData);//, "utf-8", "",false);
                S_File_Text.Write(AI_Var2.Path_Filter_Sentence+ID+"_"+p+".txt",strReturn,"utf-8");
            }else{
                System.out.println("filter.sentence.from.local");
                strReturn = S_File_Text.Read(AI_Var2.Path_Filter_Sentence+ID+"_"+p+".txt", "utf-8",1000);
            }
            System.out.println(strReturn);
            
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
                strName = t.getString("Name").trim();
                strName = strName.toLowerCase();
                System.out.println("Filter="+strName);
                pTreapFilter_Sentence.insert(new C_K_Str(strName),strName);
            }
            if (Count < 100){
                break;
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
//             把副词等组合到动词上，或形容词整合到名词上，等偏正处理过程，这里称之为Tag过程
//            bAdd = false;
//            pLink = pStruct_Tag.Segmentation_List2(pLink);
//            if (("".equals(pLink.strFrom))) {
//                pLink.strFrom = "Struct_Tag";
//            }
//            C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);
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
//        String strResult = "";
//        if (pSAI.bDebug) {
//            pSAI.pDebug.Append(("========== Get Token List ==========" + "\r\n"));
//            for (Int32 i = (pArray.Count - 1); (i <= 0); i = (i + -1)) {
//                pLink = pArray.Item[i];
//                if ((pLink.Count < 50)) {
//                    pSAI.pDebug.Append((pLink.ToString 
//                                    + (pLink.strFrom + ("=" 
//                                    + (pLink.iStart + ("," 
//                                    + (pLink.iEnd + ("\r\n" + "\r\n"))))))));
//                }
//            }
//        }
//        pLink = (C_Token_Link) pArray.get(pArray.size() - 1);
//        if (pLink.Count() > 1 && pLink.Count() < 30) {
//            String strTmp = "";
//            for (int i = 0; i <pLink.Count(); i++) {
//                pToken = pLink.Item(i);
//                strTmp = strTmp+ (AI_SYS.SYS_Any(pToken, pServerAI) + " ");
//            }
//            pSAI.strSearchKey = strTmp.Trim();
//        }

        return pArray;
    }
    
    /**
     * 得到句子的特征
     * @param pRobot
     * @param pSentence
     * @return 
     */
    public ArrayList<C_Struct_Active> ITSKeys(I_Robot pRobot, C_Sentence pSentence){

        if (pSentence.Sentence.equals("")) {
            return null;
        }
        

        Treap pTreap = new Treap();
        String strLine = "";
        ArrayList<C_Token_Link> pArray = new ArrayList<>();
        String strReturn=pSentence.Sentence_Seg;
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

        C_Token_Link pLink_Old;
        while (true) {
            pLink_Old = new C_Token_Link();
            pLink_Old.Sentence = pSentence.Sentence;
            pLink_Old.Copy(pLink);
            C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);

            //分析递归太多。
            if (pArray.size() > 100) {
                break; // TODO: might not be correct. Was : Exit Do
            }

            while (true) {
                C_Token_Link_and_Add p = pConvert_AI.getConvert2(AI_Var2.pConvert, pTreap_Active, pLink);
                pLink=p.pLink;
                if (pLink.strFrom.equals("")) {
                    pLink.strFrom = "Convert";
                }
                C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);
                if (p.bAdd == false) {
                    break;
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


        Treap pTreapKey = new Treap();
        C_Token_Key pTokenKey;

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

        TreapEnumerator<C_Token_Key> p = pTreap.Elements(true);
        while (p.HasMoreElements()) {
            pTokenKey = p.NextElement();
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
                    pTreapKey.insert(new C_K_Str(pTokenKey.Key), pTokenKey);
                }
            }
        }

        
        ArrayList<C_Struct_Active> pArrayList = new ArrayList<>();
        p = pTreapKey.Elements(true);
        while (p.HasMoreElements()) {
            pTokenKey = (C_Token_Key) p.NextElement();
            if (pTokenKey.Key.startsWith("{any}@")) {
            } else {
                if (pTokenKey.Key.length() < 20) {
                    C_Struct_Active pKey=new C_Struct_Active(pTokenKey.Key,1.0,pTokenKey);
                    pArrayList.add(pKey);//pTokenKey.strKey + "|1.0");
                }
            }
        }
        return pArrayList;
    }
    
    /**
     * 最后一层的Key
     * @param pRobot
     * @param pSentence
     * @param pArray
     * @return 
     */
    public C_Token_Link ITSKeys_Last_Link(
            I_Robot pRobot,
            C_Sentence pSentence,
            ArrayList<C_Token_Link> pArray){

        if (pSentence.Sentence.equals("")) {
            return null;
        }
        

        Treap pTreap = new Treap();
        String strReturn=pSentence.Sentence_Seg;
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

        int iCount=0;
        C_Token_Link pLink_Old;
        while (true) {
            iCount+=1;
            pLink_Old = new C_Token_Link();
            pLink_Old.Sentence = pSentence.Sentence;
            pLink_Old.Copy(pLink);
            C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);

            //分析递归太多。
            if (iCount > 100) {//pArray.size()
                break; 
            }

            while (true) {
                C_Token_Link_and_Add p = pConvert_AI.getConvert2(AI_Var2.pConvert, pTreap_Active, pLink);
                pLink=p.pLink;
                if (pLink.strFrom.equals("")) {
                    pLink.strFrom = "Convert";
                }
                C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);
                if (p.bAdd == false) {
                    break;
                }
            }

            ////////////Struct/////////
            pLink = AI_Var2.pStruct.Segmentation_List3(pLink);
            if (pLink.strFrom.equals("")) {
                pLink.strFrom = "Struct3";
            }
            C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);

            pLink = AI_Var2.pStruct.Segmentation_List2(pLink);
            if (pLink.strFrom.equals("")) {
                pLink.strFrom = "Struct2";
            }
            C_Token_Link.AddToQue(pRobot, pArray, pLink, pTreap);

            if (pLink_Old.Equal(pLink)) {
                break;
            }
        }

        pLink = pArray.get(pArray.size()-1);
        return pLink;
    }
    
    public ArrayList<C_Struct_Active> ITSKeys_Last(I_Robot pRobot, C_Sentence pSentence,boolean bSubStruct){
        
        ArrayList<C_Token_Link> pArray = new ArrayList<>();
        C_Token_Link pLink=ITSKeys_Last_Link(pRobot,pSentence,pArray);
        if (pLink==null){
            return null;
        }
        int iSize=pLink.Count();

        Treap pTreapKey2 = new Treap();
        Treap pTreapKey = new Treap();
        C_Token_Key pTokenKey;
        C_Token pToken1;
        for (int kk=0;kk<pArray.size();kk++){
            pLink = pArray.get(pArray.size()-1-kk);
            if (pLink.Count()>iSize){
                break;
            }
            for (int i = 0; i < pLink.Count(); i++) {
                pToken1 = pLink.Item(i);
                if (!"{any}".equals(pToken1.Name)){
                    for (int j=0;j<pToken1.Count();j++){
                        String strLine2=pToken1.Item(j);
                        pTokenKey = new C_Token_Key(strLine2, pToken1);
                        pTreapKey2.insert(new C_K_Str(pTokenKey.Key), pTokenKey);
                    }
                }else{
                    if (pToken1.Child_Size()==1){
                        pToken1=pToken1.pChild.get(0);
                        pTokenKey = new C_Token_Key(pToken1.Name, pToken1);
                        pTreapKey2.insert(new C_K_Str(pTokenKey.Key), pTokenKey);
                    }
                }
            }
        }
        
        ArrayList<C_Struct_Active> pKeys2 = new ArrayList<>();
        TreapEnumerator<C_Token_Key> p = pTreapKey2.Elements(true);
        while (p.HasMoreElements()) {
            pTokenKey = p.NextElement();
            C_Struct_Active pStruct2 = new C_Struct_Active(pTokenKey.Key, 1.0, pTokenKey);
            pKeys2.add(pStruct2);

            if (bSubStruct) {
                ArrayList<C_Token> pTokens = NetSO.Get_Sub_Child(pStruct2.pTokenKey.pToken);
                for (int j = 0; j < pTokens.size(); j++) {
                    C_Token pToken2 = pTokens.get(j);
                    if (pToken2.Name.contains(" ") == false) {
                        pStruct2 = new C_Struct_Active(pToken2.Name, 1, new C_Token_Key(pToken2.Name, pToken2));
                        pKeys2.add(pStruct2);
                    }
                }
            }
        }

        
        //filter 
        ArrayList<C_Struct_Active> pKeys3 = new ArrayList<>();
        for (int i=0;i<pKeys2.size();i++) {
            pTokenKey = pKeys2.get(i).pTokenKey;
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
                    pKeys3.add(pKeys2.get(i));
                }
            }
        }
        
        
        pKeys2 = new ArrayList<>();
        Treap pTreapKeys=new Treap();
        for (int i=0;i<pKeys3.size();i++) {
            C_Struct_Active pStruct_Active = pKeys3.get(i);
            if (pTreapKeys.find(new C_K_Str(pStruct_Active.Name))==null){
                pTreapKeys.insert(new C_K_Str(pStruct_Active.Name), pStruct_Active);
                pKeys2.add(pStruct_Active);
            }
        }
        return pKeys2;
    }
    
    
    /**
     * 每个Token的子Token
     * @param pToken
     * @return 
     */
    public static ArrayList<C_Token> Get_Sub_Child(C_Token pToken){
        ArrayList<C_Token> pList=new ArrayList<>();
        for (int j=0;j<pToken.pChild.size();j++){
            C_Token pToken2=pToken.pChild.get(j);
            pList.add(pToken2);
            if (pToken2.bFinal==false){
                ArrayList<C_Token> pList2=Get_Sub_Child(pToken2);
                for (int i=0;i<pList2.size();i++){
                    C_Token pToken3=pList2.get(i);
                    pList.add(pToken3);
                }
            }
        }
        return pList;
    }
    
    /**
     * 所有激活的意图
     * @param pKeys
     * @return 
     */
    public ArrayList<C_Topic> AI_Topics(
            ArrayList<C_Struct_Active> pKeys) {

        pTreapActive = new Treap();

        setKeys(pKeys,false);

        ArrayList pList = getTopic(this.MinActive);
        
        ArrayList<C_Topic> pListR=new ArrayList<>();
        for (int i=0;i<pList.size();i++){
            C_Active2 pA = (C_Active2) pList.get(i);
            C_Topic pTopic = new C_Topic();
            pTopic.Topic = pA.pConcept.Name.substring("Topic.".length());
            pTopic.dbActive = pA.dbPower;
            pTopic.Struct_Count=pA.pTreap_Active_From.Size();
            pListR.add(pTopic);
        }
        return pListR;
    }
    
    /**
     * 激活的意图
     * @param pKeys
     * @param bDebug
     * @return 
     */
    public C_Topic AI_Topic(ArrayList<C_Struct_Active> pKeys,boolean bDebug){
        pTreapActive = new Treap();

        setKeys(pKeys,bDebug);

        ArrayList<C_Active2> pList = getTopic(this.MinActive);
        C_Topic pTopic = null;
        if (pList.size() > 0) {
            C_Active2 pActive2 = pList.get(0);
            pTopic=new C_Topic();
            pTopic.Topic = pActive2.pConcept.Name.substring("Topic.".length());
            pTopic.dbActive = pActive2.dbPower;
            pTopic.Struct_Count = pActive2.pTreap_Active_From.Size();// Get_Struct_Count(pA.pTreap_Active_From,pTreapActive,bDebug);
            pTopic.Reason=Get_Reason(pActive2.pTreap_Active_From);
        }
        
        return pTopic;
    }
    
    public String Get_Reason(Treap<C_Active2_From> pTreap_Active_From){
        String strTmp="";
        TreapEnumerator<C_Active2_From> p=pTreap_Active_From.Elements();
        while(p.HasMoreElements()){
            C_Active2_From pA=p.NextElement();
            strTmp+=pA.pConcept.Name +":"+ Math.round(pA.pConnect.Weight*10000)/10000.0+",";
        }
        return strTmp;
    }
    
    
    /**
     * 最后要运行 Add_Topic_Bat ，否则不会添加进去
     * @param pKeys
     * @param strTopicTag 
     * @param pListAdd 
     */
    public void Add_Topic(
            ArrayList<C_Struct_Active> pKeys,
            String strTopicTag,
            ArrayList<C_Topic_Key_W> pListAdd){
        
        double dbWeight=1.0/pKeys.size();
        for (int i=0;i<pKeys.size();i++){
            C_Struct_Active pKey = pKeys.get(i);
            pListAdd.add(new C_Topic_Key_W("Topic." + strTopicTag,pKey.Name,dbWeight));
        }
    }
    
    /**
     * 这个和上面的不一样，直接修改数据
     * @param pKeys
     * @param strTopicTag
     */
    public void Add_Topic_Direct(
            ArrayList<C_Struct_Active> pKeys,
            String strTopicTag){
        ArrayList<C_Topic_Key_W> pListAdd=new ArrayList<>();
        double dbWeight=1.0/pKeys.size();
        for (int i=0;i<pKeys.size();i++){
            C_Struct_Active pKey = pKeys.get(i);
            pListAdd.add(new C_Topic_Key_W("Topic." + strTopicTag,pKey.Name,dbWeight));
        }
        Add_Topic_Bat(pListAdd);
    }
    
    
    
    public void Add_Topic_Bat(
            ArrayList<C_Topic_Key_W> pList){
        this.AddPoint_Bat(pList);
    }
    
    
    
    /**
     * 训练方法1 对的向量已近一些，错的向量移远一些
     * @param pRobot
     * @param pList
     * @param dbAdjust
     * @param pWriter
     * @param bShowDebug
     * @return 
     */
    public C_Train_Return TrainAI_Bat(
            C_Robot pRobot,
            Treap<C_Train_Example> pList,
            double dbAdjust,
            C_File pWriter,
            boolean bShowDebug) {

        
        ArrayList<C_Topic_Key_W> pListAdd=new ArrayList<>();//需要增加连接的所有节点
        
        ArrayList<C_Train_Example> pList_Error=new ArrayList<>();//需要调整的样本
        C_Train_Return pReturn=new C_Train_Return();
        String strReturn = "";
        
        
        TreapEnumerator<C_Train_Example> p=pList.Elements();
        while(p.HasMoreElements()){
            C_Train_Example pSample=p.NextElement();
            ArrayList<C_Struct_Active> pKeys=pSample.pKeys;

            C_Return_AI pReturn2=pRobot.GetTopic_Filter2(pKeys,pSample.pSentence.Sentence_Seg,false);

            if (pReturn2 == null){
                System.out.println("topic.none!!!");
            }else{
                if (pReturn2.pTopic==null){
                    System.out.println("error");
                }else{
                    pSample.Topic_Real = pReturn2.pTopic.Topic;
                    pSample.Reason=pReturn2.pTopic.Reason;
                }
            }

            if ("".equals(pSample.Topic)) {
                pSample.Topic = "None";
            }
            if ("".equals(pSample.Topic_Real)) {
                pSample.Topic_Real = "None";
            }
            
            boolean bAdjust=false;
            if (pSample.Topic_Real == null ? pSample.Topic != null : !pSample.Topic_Real.equals(pSample.Topic)) {
                if (pSample.Topic2!=null && !"".equals(pSample.Topic2) ){
                    if (!pSample.Topic_Real.equals(pSample.Topic2)){
                        bAdjust=true;
                    }
                }else{
                    bAdjust=true;
                }
            }
            
            
            if (bAdjust){
                pReturn.iAdjust+=1;

                if (pKeys.size() > 0) {
                    Treap pVectorTreap = new Treap();
                    if (pKeys.size() > 0) {
                        C_Vector_Item pItem;
                        C_Struct_Active pItem2;
                        for (int i = 0; i < pKeys.size(); i++) {
                            pItem2 = pKeys.get(i);
                            pItem = new C_Vector_Item();
                            pItem.Key = pItem2.Name;//pConcept.Name;
                            pItem.Weight = pItem2.dbValue;
                            pVectorTreap.insert(new C_K_Str(pItem2.Name), pItem);
                        }
                    }
                    pSample.pVector=pVectorTreap;
                    pList_Error.add(pSample);
                    pSample.Adjust_Count+=1;//调整次数
                }

                String strLine="t=" + getTopic_Name(pSample.Topic) + ",t2=" + getTopic_Name(pSample.Topic2) + ",e=" +getTopic_Name(pSample.Topic_Real);
                out.println("\n" + strLine+",s="+pList_Error.size());
                out.println(pSample.ID+"="+pSample.pSentence.Sentence);
                out.println(pSample.Reason);

                S_File.Write_Line(pWriter, "\n"+strLine+"\t\t\t"+ pSample.ID+","+pSample.pSentence.Sentence);
                S_File.Write_Line(pWriter, pSample.Reason);
                if (bShowDebug){
                    S_File.Write_Line(pWriter, pKeys.toString());
                }
            }else{
                pReturn.iNoAdjust+=1;
            }
        }
        
        pNet.addConcept_Bat(pListAdd);

        //"训练结束"+strReturn; 
        if (pList_Error.size()>0) {
            pListAdd =new ArrayList<>();
            ArrayList<C_From_To_Weight> pListAdjust=new ArrayList<>();
            for (int k=0;k<pList_Error.size();k++){
                C_Train_Example pExample=pList_Error.get(k);
                if (pExample.pVector!=null){
                    strReturn += Adjust_Connect_Weight(pExample.pVector, pExample.Topic, pExample.Topic_Real, dbAdjust,pListAdjust);
                    if (pExample.Topic2!=null && !"".equals(pExample.Topic2)){
                        strReturn += Adjust_Connect_Weight(pExample.pVector, pExample.Topic2, pExample.Topic_Real, dbAdjust,pListAdjust);
                    }
                }else{
                    S_File.Write_Line(pWriter, "noVector:"+pExample.Topic+pExample.pSentence.Sentence);
                }
            }
            this.AdjustWeight_Bat(pListAdjust, pListAdd);
            pNet.addConcept_Bat(pListAdd);
            pReturn.strResult=strReturn;
        } else {
            pReturn.strResult="==";
        }
        
        return pReturn;
    }
    
    /**
     * Topic Name
     * @param strTopic
     * @return 
     */
    public String getTopic_Name(String strTopic){
        String pStr=pTreap_Topic_Name.find(new C_K_Str(strTopic));
        if (pStr==null){
            return strTopic;
        }else{
            return pStr;
        }
    }
    
    
    public Treap GetVector_FromTopic(
            String strTopic,
            ArrayList<C_Connect2> pVector1) {
        
        Treap pVectorTreap=new Treap();
        C_Connect2 pConnect;
        C_Concept2 pTopic;
        pTopic = pNet.pTreap.find(new C_K_Str("Topic." + strTopic));
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
     * 调整连接权重 方法2，放大，缩小
     * @param pVector
     * @param strTopic_Right
     * @param strTopic_Error
     * @param dbAdjust
     * @param pListAdd
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

        Treap pVector_Right_Treap=GetVector_FromTopic(strTopic_Right, pVector_Right); ;//目标向量
        Treap pVector_Error_Treap=GetVector_FromTopic(strTopic_Error, pVector_Error); ;//

        ArrayList<C_From_To_Weight> pListAdjust=new ArrayList<>();
        //对的向量放大一点
        for (int i=0;i<pVector_Right.size();i++){
            pConnect =pVector_Right.get(i);
            if (pConnect != null) {
                pConnect.Weight = pConnect.Weight*(1+dbAdjust);
            } else {
                pListAdjust.add(new C_From_To_Weight(pConnect.pFrom.Name, "Topic." + strTopic_Right, 0.1));
//                C_Topic_Key_W pTK=AdjustWeight(pConnect.pFrom.Name, "Topic." + strTopic_Right, 0.1);//
//                if (pTK!=null) pListAdd.add(pTK);
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
                    pListAdjust.add(new C_From_To_Weight(pConnect.pFrom.Name, "Topic." + strTopic_Right, -0.1));
//                    C_Topic_Key_W pTK=AdjustWeight(pConnect.pFrom.Name, "Topic." + strTopic_Error, -0.1);
//                    if (pTK!=null) pListAdd.add(pTK);
                }
            }
        }
        AdjustWeight_Bat(pListAdjust,pListAdd);
        return strReturn;
    }

    /**
     * 调整连接权重
     * @param pVector
     * @param strTopic_Right
     * @param strTopic_Error
     * @param dbAdjust
     * @param pListAdjust
     * @return 
     */
    public String Adjust_Connect_Weight(
            Treap<C_Vector_Item> pVector,
            String strTopic_Right,
            String strTopic_Error,
            double dbAdjust,
            ArrayList<C_From_To_Weight> pListAdjust) {

        if (strTopic_Error.equals("None")){
            TreapEnumerator<C_Vector_Item> p=pVector.Elements();
            while(p.HasMoreElements()){
                C_Vector_Item pItem = p.NextElement();
                pListAdjust.add(new C_From_To_Weight(pItem.Key,"Topic." + strTopic_Right, 0.1));
            }
            return "";
        }
        
        String strReturn="";
        C_Connect2 pConnect;
        ArrayList<C_Connect2> pVector_Right = new ArrayList<>();//目标向量
        ArrayList<C_Connect2> pVector_Error = new ArrayList<>();//
        //步长=(目标向量-移动向量)/100

        Treap pVector_Right_Treap=GetVector_FromTopic(strTopic_Right, pVector_Right);

        Treap pVector_Error_Treap=GetVector_FromTopic(strTopic_Error, pVector_Error);

        double Distance1 = Get_Vector_Length(pVector_Right_Treap);
        if (Distance1 > 0.001) { //正规化
            for (int i=0;i<pVector_Right.size();i++) {
                pConnect = pVector_Right.get(i);
                double newWeight = pConnect.Weight / Distance1;
                pConnect.Weight=(pConnect.Weight+newWeight)/2;//不要一下子切换过去，慢慢调整过去
            }
        }
        

        double Distance2 = Get_Vector_Length(pVector_Error_Treap);
        if (Distance2 > 0.001) {//正规化
            for (int i=0;i<pVector_Error.size();i++) {
                pConnect = pVector_Error.get(i);
                double newWeight = pConnect.Weight / Distance2;
                pConnect.Weight=(pConnect.Weight+newWeight)/2;//不要一下子切换过去，慢慢调整过去
            }
        }

        double Distance = 0;//计算向量长度
        TreapEnumerator<C_Vector_Item> p=pVector.Elements();
        while(p.HasMoreElements()){
            C_Vector_Item pItem = p.NextElement();
            Distance += pItem.Weight * pItem.Weight;
        }
        Distance = Math.sqrt(Distance);

        if (Distance > 0.001) {//规范化
            p=pVector.Elements();
            while(p.HasMoreElements()){
                C_Vector_Item pItem = p.NextElement();//pVector.get(i);// C_Vector_Item) p.NextElement();
                pItem.Weight = pItem.Weight / Distance;
            }
        }

        //移动到正确的方向 pVector_Right 往 pVector移动
        strReturn+=MoveVector(pVector, pVector_Right_Treap, strTopic_Right, dbAdjust,pListAdjust);
            

        //离错误的方向远 pVector_Error 远离 pVector
//        if (!"None".equals(strTopic_Error)) { //如果相差很小就不移动了
        strReturn+=LeaveVector(pVector, pVector_Error_Treap, strTopic_Error, dbAdjust,pListAdjust); // dbAdjust/ 10
//        }
  
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

    public String save(String strFile, boolean bFilter) {
        pNetSO_Save.setFile(strFile);
        String strReturn = "";
        try { 
            pNetSO_Save.start(pRobot);
            pNetSO_Save.start_file(null);
            System.out.println("file="+strFile);
            String strTo;
            String strTmp = "";
            TreapEnumerator p = pNet.pTreap.Elements();
            while (p.HasMoreElements()) {
                C_Concept2 pConceptMain = (C_Concept2) p.NextElement();
                TreapEnumerator p2 = pConceptMain.pOutputs.Elements(true);
                while (p2.HasMoreElements()) {
                    C_Connect2 pConnect = (C_Connect2) p2.NextElement();
                    C_Concept2 pConcept = pConnect.pFrom;
                    C_Concept2 pConceptTo = pConnect.pTo;
                    strTo = pConceptTo.Name;
                    if (strTo.startsWith("Topic.") && Math.abs(pConnect.Weight) > 0.0000001) {
                        pNetSO_Save.save_line(pConcept.Name,strTo,pConnect.Weight);
                    }
                }
            }
            pNetSO_Save.end();
            strReturn = strTmp;
        }
        catch (Exception ex) {
            strReturn = ex.toString();
        }
        return strReturn;
    }

    /**
     * 设置激活的节点
     * @param pKeys
     * @param bDebug
     */
    public void setKeys(ArrayList<C_Struct_Active> pKeys,
            boolean bDebug) {
        pKeys.forEach((pKey) -> {
            setItem(pTreapActive,pKey.Name,pKey.dbValue,bDebug);
        });
    }
    
    /**
     * pTreapActive 最终激活的概念
     * @param pTreapActive
     * @param strActive
     * @param dbScale 
     */
    private void setItem(
            Treap pTreapActive, 
            String strActive, 
            double dbScale,
            boolean bDebug) {
        C_Concept2 pConcept;
        TreapEnumerator<C_Active2> p;
        
        C_Connect2 pConnect;
        p = pTreapActive.Elements(true);
        while (p.HasMoreElements()){    //激活的概念都标记为未检查
            C_Active2 pActiveTmp=p.NextElement();
            pActiveTmp.bChecked = false;
        }
        Treap pTreapCheck = new Treap();
        Treap pTreapCheck2;
        C_Concept2 pConceptActive = pNet.pTreap.find(new C_K_Str(strActive));
        if (pConceptActive == null){
            if (bDebug) System.out.println("no.c="+strActive);
            return;
        }else{
            if (bDebug) System.out.println("has.c="+strActive);
        }
        
        C_Active2 pActive = pConceptActive.getActive(pTreapActive);
        pActive.dbPower += 100 * dbScale;
        
        pTreapCheck.insert(new C_K_Int(pActive.ID), pActive);
        for (int j = 0;j <4; j++) {
            p = pTreapCheck.Elements(true);
            pTreapCheck2 = new Treap();
            while (p.HasMoreElements()) {
                pActive = (C_Active2) p.NextElement();
                if (pActive.bChecked == false  && pActive.pConcept.pOutputs!=null) {
                    pActive.bChecked = true;
                    
                    TreapEnumerator<C_Connect2> p2= pActive.pConcept.pOutputs.Elements(true);
                    while (p2.HasMoreElements()) {
                        pConnect= p2.NextElement();
                        pConcept = pConnect.pTo;
                        C_Active2 pActiveTopic = pConcept.getActive(pTreapActive);
                        pActiveTopic.dbPower +=pActive.dbPower * pConnect.Weight;//* (0.6 * pConnect.dbScale);
                        
                        C_Active2_From pActiveTmp=new C_Active2_From(pActive.pConcept,pConnect);//这个Active就是记录一下从哪里激活
                        pActiveTopic.pTreap_Active_From.insert(new C_K_Int(pActiveTmp.ID), pActiveTmp);
                        
                        pTreapCheck2.insert(new C_K_Int(pActiveTopic.ID), pActiveTopic);
                    }
                }
            }
            pTreapCheck = pTreapCheck2;
        }
        
    }
    
    
    public ArrayList<C_Active2> getTopic(double dbPower) {
        C_Active2 pA;
        String strName;
        Treap<C_Active2> pTreap = new Treap();
        TreapEnumerator<C_Active2> p = pTreapActive.Elements(false);
        while (p.HasMoreElements()) {
            pA = p.NextElement();
            strName = pA.pConcept.Name.toLowerCase();
            if (pA.dbPower > dbPower && strName.startsWith("topic.")) {
                pTreap.insert(new C_K_Double(pA.dbPower), pA);
            }
        }
        ArrayList<C_Active2> pList = new ArrayList();
        p = pTreap.Elements(false);
        while (p.HasMoreElements()) {
            pA = p.NextElement();
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
        TreapEnumerator<C_Active2> p = pTreapActive.Elements(false);
        C_Active2 pA;
        //最后一次激活的C_Active 移除
        while (p.HasMoreElements()) {
            pA = p.NextElement();
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
     * @param pListAdjust
     * @return 
     */
    public String MoveVector(
            Treap<C_Vector_Item> pVector, 
            Treap pVector_Right_Treap, String strTopic, 
            double dbMove,
            ArrayList<C_From_To_Weight> pListAdjust) {
        C_Connect2 pConnect;
        C_Vector_Item pItem;
        double dbScale;
        
        StringBuilder strReturn =new StringBuilder();
        
        TreapEnumerator<C_Vector_Item> p=pVector.Elements();
        while(p.HasMoreElements()){
            pItem = p.NextElement();
            pConnect = (C_Connect2) pVector_Right_Treap.find(new C_K_Str(pItem.Key));
            if (pConnect != null) {
                dbScale = (pItem.Weight - pConnect.Weight);// / m;
                strReturn.append(pItem.Key).append("+=").append(dbScale * dbMove);
            } else {
                dbScale = pItem.Weight;// / m;
                strReturn.append(pItem.Key).append("++");
            }
            pListAdjust.add(new C_From_To_Weight(pItem.Key,"Topic." + strTopic, dbScale * dbMove));
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
     * @param pListAdjust
     * @return 
     */
    public String LeaveVector(Treap<C_Vector_Item> pVector,
            Treap pVector_Error_Treap, String strTopic, 
            double dbMove,
            ArrayList<C_From_To_Weight> pListAdjust){
        
        StringBuilder strReturn =new StringBuilder();
        C_Connect2 pConnect;
        double dbScale;
        C_Vector_Item pItem;
        
        TreapEnumerator<C_Vector_Item> p=pVector.Elements();
        while(p.HasMoreElements()){
            pItem = p.NextElement(); //pVector.get(i);//C_Vector_Item) p.NextElement();
            pConnect = (C_Connect2) pVector_Error_Treap.find(new C_K_Str(pItem.Key));
            if (pConnect != null) {
                dbScale = (pConnect.Weight - pItem.Weight);// / m;
                double delta=dbScale * dbMove;
                strReturn.append(pItem.Key).append("+=").append(delta);
            }else{
                dbScale= - pItem.Weight;
                //错误的向量远离
            }
            pListAdjust.add(new C_From_To_Weight(pItem.Key,"Topic." + strTopic, dbScale * dbMove));
        }
        return strReturn.toString();
    }

    /**
     * 计算向量的模
     * @param pVector1
     * @return 
     */
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
    
    
    public void AdjustWeight_Bat(
            ArrayList<C_From_To_Weight> pList,
            ArrayList<C_Topic_Key_W> pListAdd) {
        
        for (int i=0;i<pList.size();i++){
            C_From_To_Weight pItem=pList.get(i);
            C_Topic_Key_W pTK=AdjustWeight(pItem.From,pItem.To,pItem.Weight);
            if (pTK!=null) pListAdd.add(pTK); 
        }
    }

    private C_Topic_Key_W AdjustWeight(String strFrom, String strTo, double dbIncrease) {
        
        C_Concept2 pCActive = pNet.pTreap.find(new C_K_Str(strFrom));

        if (pCActive == null) {
            return new C_Topic_Key_W(strTo,strFrom,dbIncrease);//strReturn
        }

        C_Connect2 pConnect = pCActive.pOutputs.find(new C_K_Str(strTo));
        
        if (pConnect!=null){
            pConnect.Weight += dbIncrease;
        }else{
            return new C_Topic_Key_W(strTo,strFrom,dbIncrease);
        }
        return null;
    }
    
    
    /**
     * 批量添加概念
     * @param pList 
     */
    public void AddPoint_Bat(ArrayList<C_Topic_Key_W> pList) {
        
        ArrayList<C_Topic_Key_W> pList2=new ArrayList<>();//List2是List中未过滤的部分
        for (int k=0;k<pList.size();k++){
            C_Topic_Key_W pTK=pList.get(k);
            
            if ("".equals(pTK.Key)) {
                continue;
            }

            //如果pTreapFilter 中存在这个词汇或结构，就过滤掉。
            if (pTreapFilter.find(new C_K_Str(pTK.Key.toLowerCase())) == null) {
                int index=pTK.Key.indexOf("@");
                if (index>0){
                    String strKey=pTK.Key.substring(0,index)+"@*";
                    if (pTreapFilter.find(new C_K_Str(strKey.toLowerCase())) == null){
                        strKey="*@"+pTK.Key.substring(index+1);
                        if (pTreapFilter.find(new C_K_Str(strKey.toLowerCase())) == null){
                            pList2.add(pTK);
                        }
                    }
                }else{
                    pList2.add(pTK);
                }
            }
        }
        
        pNet.addConcept_Bat(pList2);
    }
    
    /**
     * 读取训练的数据
     * @param strFile
     * @return 
     */
    public String read(String strFile) {
        if (S_File.Exists(strFile)==false){
            return "";
        }
        pNet.Clear();
        System.out.println("file="+strFile);
        try {
            ArrayList<C_Topic_Key_W> pListAdd=new ArrayList<>();
            C_File pFile=S_file_sub.Read_Begin_UTF8(strFile);

            String strLine;
            String[] strSplit;
            double dbScale;
            strLine = S_file_sub.read_line(pFile);// pSR.readLine();
            while (strLine!=null){
                strSplit = strLine.split("\\|");
                if (strSplit.length > 2 ) {
                    if (strSplit[1].endsWith(":2") || strSplit[1].endsWith(":3")){
                        
                    }else{
                        dbScale = Double.valueOf(strSplit[2]);
                        if (Math.abs(dbScale) <  0.00001) {
                            // Me.pTreapFilter.insert(New C_K_Str(strSplit(0)), strSplit(0))
                        }else {
                            pListAdd.add(new C_Topic_Key_W(strSplit[1],strSplit[0],dbScale));
                        }
                    }
                }
                strLine = S_file_sub.read_line(pFile);// pSR.readLine();
            }
            this.AddPoint_Bat(pListAdd);
            pFile.Close();
            return "Read Finished";
        }
        catch (NumberFormatException ex) {
            return ex.toString();
        }
    }

}




    /**
     * private是为了方便转化为Spark程序，参考AddPoint_Bat
     * @param strFrom
     * @param strTo
     * @param dbScale 
     */
//    private void AddPoint(String strFrom, String strTo, double dbScale) {
//        if ("".equals(strFrom)) {
//            return;
//        }
//        
//        if (pTreapFilter.find(new C_K_Str(strFrom.toLowerCase())) == null) {
//            int index=strFrom.indexOf("@");
//            if (index>0){
//                String strKey=strFrom.substring(0,index)+"@*";
//                if (pTreapFilter.find(new C_K_Str(strKey.toLowerCase())) == null){
//                    strKey="*@"+strFrom.substring(index+1);
//                    if (pTreapFilter.find(new C_K_Str(strKey.toLowerCase())) == null){
//                        pNet.addConcept(strFrom, strTo, dbScale);//, Connect_Type.Connect_Value.Class_Type);
//                    }
//                }
//            }else{
//                pNet.addConcept(strFrom, strTo, dbScale);
//            }
//        }
//    }


//     public void initFilter(String strFile_Filter,String strFileNoExpand) {
//        // pTreapKeys
//        ReadToList(pListFilter, strFile_Filter);
//        ReadToList(pListNoExpand, strFileNoExpand);
//    }
    
//    public void ReadToList(ArrayList pList, String strFile) {
//        bReadFilter = true;
//        if (S_File.Exists(strFile)) {
//            InputStreamReader pFS = null;
//            try {
//                pFS = new InputStreamReader(new FileInputStream(strFile), "UTF-8");
//                BufferedReader pSR = new BufferedReader(pFS);// 文件输入流为
//                
//                pList = new ArrayList();
//                String strLine = pSR.readLine();
//                while (strLine!=null){
//                    if (!"".equals(strLine)){
//                        pList.add(strLine);
//                    }
//                    strLine = pSR.readLine();
//                }
//                pSR.close();
//                pFS.close();
//            } catch (IOException ex) {
//                //Logger.getLogger(NetSO.class.getName()).log(Level.SEVERE, null, ex);
//            } finally {
//                try {
//                    pFS.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(NetSO.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//    }

    
//    /**
//     * 训练方法1 对的向量已近一些，错的向量移远一些
//     * @param pKeys
//     * @param strTopicTag
//     * @param dbAdjust
//     * @return 
//     */
//    public C_Train_Return TrainAI(
//            ArrayList<C_Struct_Active> pKeys,
//            String strTopicTag,
//            double dbAdjust) {
//
//        C_Topic pTopic=AI_Topic(pKeys);
//        C_Train_Return pReturn=new C_Train_Return();
//        
//        String strTopicReal = "";
//        if (pTopic==null){
//            System.out.println("topic.none!!!!!!!!!!!!");
//        }else{
//            strTopicReal = pTopic.Topic;
//        }
//        String strReturn = "";
//
//        if ("".equals(strTopicTag)) {
//            strTopicTag = "None";
//        }
//        if ("".equals(strTopicReal)) {
//            strTopicReal = "None";
//        }
//        pReturn.strTopic=strTopicReal;
//        
////        System.out.println("R="+strTopicReal+",T="+strTopicTag);
//        
//        boolean bAdjust = false;
//        if (strTopicReal == null ? strTopicTag != null : !strTopicReal.equals(strTopicTag)) {
//            
//            bAdjust = true;
//
//            if (pKeys.size() > 0) {
//                C_Concept2 pConcept;
//                C_Vector_Item pItem;
//                C_Struct_Active pItem2;
//                ArrayList<C_Vector_Item> pVector = new ArrayList<>();
//                Treap pVectorTreap = new Treap();
//                for (int i = 0; i < pKeys.size(); i++) {
//                    pItem2 = pKeys.get(i);
//                    pConcept = (C_Concept2) pNet.pTreap.find(new C_K_Str(pItem2.Name));
//                    if (pConcept == null) {
//                        pNet.addConcept(pItem2.Name, "Topic." + strTopicTag, dbAdjust);//, Connect_Type.Connect_Value.Class_Type);
//                        pConcept = (C_Concept2) pNet.pTreap.find(new C_K_Str(pItem2.Name));
//                    }
//
//                    pItem = new C_Vector_Item();
//                    pItem.strKey = pConcept.Name;
//                    pItem.dbPower = pItem2.dbValue;
//                    pVectorTreap.insert(new C_K_Str(pConcept.Name), pItem);
//                }
//                
//                TreapEnumerator p=pVectorTreap.Elements();
//                while(p.HasMoreElements()){
//                    pItem = (C_Vector_Item) p.NextElement();
//                    pVector.add(pItem);
//                }
//
//                strReturn += Adjust_Connect_Weight(pVector, strTopicTag, strTopicReal, dbAdjust);
//            }
//        }
//
//        //"训练结束"+strReturn; 
//        if (bAdjust) {
//            pReturn.strResult=strReturn;
//        } else {
//            pReturn.strResult="==";
//        }
//        
//        return pReturn;
//
//    }
