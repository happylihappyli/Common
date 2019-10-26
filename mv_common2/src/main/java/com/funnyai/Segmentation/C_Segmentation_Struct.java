package com.funnyai.Segmentation;

import com.funnyai.NLP.C_Token;
import com.funnyai.NLP.C_Token_Link;
import com.funnyai.data.C_K_Str;
import com.funnyai.data.Treap;
import com.funnyai.data.TreapEnumerator;
import com.funnyai.io.Old.S_File;
import com.funnyai.string.Old.S_Strings;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;

public class C_Segmentation_Struct {

    public String Path_Struct="";
    public Treap pTreap_Active_MultiKey=new Treap();
    public Struct_Treap pTreapKeys=new Struct_Treap();

    public String InitDictionary(
            C_Convert pConvert,
            String strFile) throws IOException {
        if (S_File.Exists(strFile) == true) {
            System.out.println(strFile + "...");
            return InitDictionary(pConvert, new FileInputStream(strFile));
        } else {
            return "词库文件 " + strFile + " 不存在";
        }
    }

    public String InitDictionary(C_Convert pConvert, InputStream in) throws IOException {
        
        String[] strSplit;
        BufferedReader SR;
        int Count;
        int iNext=0;
        try ( //把单词读取到内存，这样就可以分词了,从Txt文件中读取内容。
            InputStreamReader FS = new InputStreamReader(in, "UTF-8")) {
            SR = new BufferedReader(FS); // 文件输入流为
            String strLine,strConvert;
            Count = 0;
            strLine = SR.readLine();
            if (strLine != null && strLine.length() > 1) {
                if ((int) strLine.charAt(0) == 65279) {
                    strLine = strLine.substring(1);
                }
            }   while (strLine != null) {
                if (strLine.length() > 0) {
                    strLine = strLine.trim();
                    if (strLine.indexOf(" ")>0) {
                        strSplit = strLine.split("==");
                        strLine = strSplit[0];
                        if ((strSplit.length > 1)) {
                            strConvert = strSplit[1];
                        }
                        else {
                            strConvert = "";
                        }
                        if (strSplit.length > 2) {
                            iNext = Integer.valueOf(strSplit[2]);
                        }
                        int iFinal=0;
                        if (strSplit.length > 3) {
                            iFinal = Integer.valueOf(strSplit[3]);
                        }
                        if (!"".equals(strConvert)) {
                            readLine_FromDic(pConvert, strLine, strConvert, iNext,iFinal);
                            Count++;
                        }
                    }
                }
                strLine = SR.readLine();
        }   SR.close();
        } // 文件输入流为

        System.out.println("ListCount:" + pTreapKeys.Size() + ",WordCount:" + Count);

        return "词库初始化完毕！";
    }

    public void readLine_FromDic(
                C_Convert pConvert,
                String strLine, 
                String strConvert,
                int iNext,int iFinal){
        strLine = strLine.trim();
        if (strLine.startsWith("'")) {
            return;
        }
        out.println(strLine+"="+strConvert);
        if ("".equals(strLine)) {
            return;
        }
        
        String[] strSplit;
        String strTmp;
        if (!"".equals(strConvert)) {
            strSplit = strLine.split("\\s");
            pConvert.read_fromDic(strLine, strConvert, "");
            if (iNext > 0  && iNext <= strSplit.length) {
                strTmp = strSplit[iNext - 1];
                strTmp = strTmp.replace("{", "");
                strTmp = strTmp.replace("}", "");
                
                if ("".equals(strTmp)){
                    out.println(strTmp);
                }
                pConvert.read_fromDic(strLine, "{" + strTmp + "s}", "");
            }
        }
        int pos = strLine.indexOf(" ");
        if (pos > 0) {
            strSplit = strLine.split("\\s");
            if (strSplit.length == 2 || strSplit.length == 3) {
                C_Seg_Struct pStruct;
                pStruct = pTreapKeys.find(new C_K_Str(strLine));
                if (pStruct == null) {
                    pStruct = new C_Seg_Struct();
                    pStruct.strLine = strLine;
                    pTreapKeys.insert(new C_K_Str(strLine), pStruct);
                    Treap pSubTreap=null;
                    if (strSplit.length == 2) {
                        pSubTreap=GetSubTree("1." + strSplit[0]);
                        pSubTreap.insert(new C_K_Str(pStruct.strLine), pStruct);
                        pSubTreap=GetSubTree("2." + strSplit[1]);
                        pSubTreap.insert(new C_K_Str(pStruct.strLine), pStruct);
                        pStruct.iMax = 2;
                    }
                    else if (strSplit.length == 3) {
                        pSubTreap=GetSubTree("1." + strSplit[0]);
                        pSubTreap.insert(new C_K_Str(pStruct.strLine), pStruct);
                        pSubTreap=GetSubTree("2." + strSplit[1]);
                        pSubTreap.insert(new C_K_Str(pStruct.strLine), pStruct);
                        pSubTreap=GetSubTree("3." + strSplit[2]);
                        pSubTreap.insert(new C_K_Str(pStruct.strLine), pStruct);
                        pStruct.iMax = 3;
                    }
                }
                pStruct.Size = strSplit.length;
                pStruct.Final=iFinal;
            }
        }
    }

    public static void main(String[] args) {
        String strInput = "NOKIA 诺基亚 6120C 直板手机 黑色 行货";
        C_Segmentation_Struct pSeg = new C_Segmentation_Struct();

        pSeg.InitDictionary();

    }

    public void InitDictionary() {
        String[] strFiles = {"Struct.TXT"};
        InitDictionary(null, strFiles);
    }
    
    public void Init_Struct(C_Convert pConvert,String Robot_ID,String strTag){//,boolean bFromWeb){
        
        
        String strFile =Path_Struct+"/struct.db";
        strFile=strFile.replace("//","/");
        
        String strFile2 = strFile+"."+Robot_ID+"_"+strTag+".db";
        S_File.Copy2File(strFile, strFile2);
        
        DB db = DBMaker.fileDB(strFile2).checksumHeaderBypass().make();// .make();
        ConcurrentMap map = db.hashMap("map").open();
        out.println("初始化结构:"+strFile);
        pTreapKeys = new Struct_Treap();
        String strStruct ;
        int PageSize = 1000;
        int iNext = 0;
        Object pObj=map.get("size");
        int Size=0;
        out.println("type="+pObj.getClass().getName());
        switch(pObj.getClass().getName()){
            case "java.lang.Double":
                Size=((Double)pObj).intValue();
                break;
            case "java.lang.String":
                Size=Integer.parseInt((String)pObj);
                break;
            case "java.lang.Integer":
                Size=(Integer)pObj;
                break;
            case "java.lang.Long":
                Size=((Long)pObj).intValue();
                break;
        }
        out.println("Struct.Page.size="+Size);
        for(int i=1;i<=Size;i++){
            try {
                String strReturn;
                strReturn=(String) map.get("k"+i);
                
                int iFinal=0;
                int Count =0;
                JSONObject pJobject = new JSONObject(strReturn);
                JSONArray token = pJobject.getJSONArray("data");
                Count = pJobject.getInt("Count");
                String strLine = "";
                for (int k=0;k<token.length();k++){
                    JSONObject t=token.getJSONObject(k);
                    strStruct = t.getString("Name");
                    strLine = t.getString("Struct");
                    String Struct_Convert = t.getString("Struct_Convert");
                    iNext = t.getInt("Inherit_Pos");
                    iFinal = t.getInt("Final");
                    if (strStruct.startsWith("{")==false){
                        strStruct="{"+strStruct+"}";
                    }
                    readLine_FromDic(pConvert,strLine,strStruct,iNext,iFinal);
                    if (!"".equals(Struct_Convert)){
                        pConvert.read_fromDic(strStruct, "{"+Struct_Convert+"}", "");
                    }
                }
                
                if (Count < PageSize){
                    break; 
                }
            }
            catch (Exception ex) {
            }
        }
        db.close();
    }
    
    

    public void InitDictionary(C_Convert pConvert, String[] strFiles) {
        for (int i = 0; i < strFiles.length; i++) {
            System.out.println("File:" + strFiles[i]);
            System.out.println(C_Segmentation_Struct.class.getResource("/").toString());
            InputStream url = C_Segmentation_Struct.class.getResourceAsStream("/data/" + strFiles[i]);
            try {
                if (url != null) {
                    InitDictionary(pConvert, url);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadDicFileInPath(String strDictionaryDir) {
        File[] pFiles = S_File.GetFiles(strDictionaryDir, "");//*.txt

        int i;
        for (i = 0; i < pFiles.length; i++) {
            try {
                InitDictionary(null, pFiles[i].getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public String getSubString(Treap pTreap, String[] strSplit, int i, int MaxI) {
        int j = 0;
        String strTmp = "";
        for (j = i; j < Math.min(MaxI, i + pTreap.KeyMaxLen); j++) {
            strTmp += S_Strings.ReplaceNLP_Invert(strSplit[j]) + " ";
        }
        return strTmp.trim();
    }

    
    public C_Token_Link Segmentation_List3(C_Token_Link pLink) {
        int iCount = 3;
        C_Token_Link pLinkR = new C_Token_Link();
        pLinkR.Sentence = pLink.Sentence;
        C_Token pToken;
        C_Token pToken_Old;
        int Count = 0;
        for (int i = 0; i< pLink.Count(); i++) {
            pToken = null;
            pToken = Segmentation_CompareWord3(pLink, i, iCount);
            if (pToken!=null) {
                pLinkR.Add(pToken);
                for (int k = 0; k<pToken.Item(0).split("\\s").length; k++) {
                    pToken_Old = pLink.Item(Count);
                    if (pToken_Old == null) {
                        // zzzzzzzzzz
                        // Stop
                        // pToken = Segmentation_CompareWord3(pToken_Link, i, iCount)
                    }else {
                        pToken.pChild.add(pToken_Old);
                        Count++;
                    }
                }
                pToken.iStart = pToken.pChild.get(0).iStart;
                pToken.iEnd = pToken.pChild.get(pToken.pChild.size() - 1).iEnd;
                i = (Count - 1);
            }else {
                pToken_Old = pLink.Item(i);
                pLinkR.Add(pToken_Old);
                Count++;
            }
        }
        if (pLinkR.Count() == pLink.Count()) {
            return pLink;
        }
        else {
            pLinkR.iStart = pLinkR.Item(0).iStart;
            pLinkR.iEnd = pLinkR.Item(pLinkR.Count() - 1).iEnd;
            return pLinkR;
        }
    }
    
    // '' <summary>
    // '' 2个元的结构
    // '' </summary>
    // '' <param name="pLink"></param>
    // '' <returns></returns>
    // '' <remarks></remarks>
    public C_Token_Link Segmentation_List2(C_Token_Link pLink) {
        int iCount = 2;
        C_Token_Link pLinkR = new C_Token_Link();
        pLinkR.Sentence = pLink.Sentence;
        C_Token pToken;
        C_Token pToken_Old;
        int Count = 0;
        for (int i = 0; i<pLink.Count(); i++) {
            pToken = null;
            pToken = Segmentation_CompareWord2(pLink, i, iCount);
            if (pToken!=null) {
                pLinkR.Add(pToken);
                for (int k = 0; k <pToken.Item(0).split("\\s").length; k++) {
                    pToken_Old = pLink.Item(Count);
                    if (pToken_Old != null){
                        pToken.pChild.add(pToken_Old);
                        Count++;
                    }
                }
                pToken.iStart = pToken.pChild.get(0).iStart;
                pToken.iEnd = pToken.pChild.get(pToken.pChild.size() - 1).iEnd;
                i = Count - 1;
            }else{
                pToken_Old = pLink.Item(i);
                pLinkR.Add(pToken_Old);
                Count++;
            }
        }
        if (pLinkR.Count() == pLink.Count()) {
            return pLink;
        }else {
            pLinkR.iStart = pLinkR.Item(0).iStart;
            pLinkR.iEnd = pLinkR.Item(pLinkR.Count() - 1).iEnd;
            return pLinkR;
        }
    }


    public ArrayList<String> getSubString(
            C_Token_Link pToken_Link, C_Token pToken,
            int i, int MaxI,
            boolean bReplaceX) {

        int j = 0;
        ArrayList<String> pArray = new ArrayList<>();

        AddString(pArray, pToken, bReplaceX);
        for (j = i + 1; j < MaxI; j++) {
            AddString(pArray, pToken_Link.Item(j), bReplaceX);
        }
        return pArray;
    }

    //这个Treap是用来识别结构用的，可以查找当前函数的引用
    private Treap GetSubTree(String strKey) {
        Treap pSubTreap = pTreapKeys.find_treap(new C_K_Str(strKey));
        if (pSubTreap == null) {
            pSubTreap = new Treap();
            pTreapKeys.insert(new C_K_Str(strKey), pSubTreap);
        }
        return pSubTreap;
    }
     
    
    public void AddString(ArrayList<String> pArray, C_Token pToken, boolean bReplace) {
        String strTmp = "";
        int Count;
        if (pArray.size() > 0) {
            Count = pArray.size();
            for (int m = 0; m < pToken.Count() - 1; m++) {
                for (int k = 0; k <= Count - 1; k++) {
                    pArray.add(pArray.get(k));
                }
            }

            Count = pArray.size();
            for (int k = 0; k < pArray.size(); k++) {
                strTmp = S_Strings.ReplaceNLP_Invert(pToken.Item(k / (pArray.size() / pToken.Count())));
                if (bReplace) {
                    strTmp = ReplaceX(strTmp);
                }
                pArray.set(k, pArray.get(k) + " " + strTmp);
            }
        } else {
            for (int m = 0; m < pToken.Count(); m++) {
                strTmp = S_Strings.ReplaceNLP_Invert(pToken.Item(m));
                if (bReplace) {
                    strTmp = ReplaceX(strTmp);
                }
                pArray.add(strTmp);
            }
        }
    }

    public String ReplaceX(String strTmp2) {
        if (strTmp2.startsWith("{") && strTmp2.endsWith(".类}")) {
            strTmp2 = "{x.类}";
        }
        return strTmp2;
    }
    
    //标记一下，代表已经计算过
    public C_Token Segmentation_CompareWord3(C_Token_Link pLink, int index, int iCount) {
        if (pLink == null || pLink.Count()< index + 2) {
            return null;
        }
        
        if (pLink.Item(index + 0).pNextToken3 != null && 
                pLink.Item(index + 0).pNextToken3.ID == pLink.Item(index + 1).ID){
            return null;
        }
        Treap pTreapCache = new Treap();
        
        C_Token[] pToken_Array=new C_Token[iCount];
        int MaxI = pLink.Count();
        C_Seg_Struct pStruct;
        int iStart=0;
        int iEnd=0;
        String strTmp;
        TreapEnumerator p;
        int iMax = (Math.min(iCount, (MaxI - index)) - 1);
        for (int j = 0;j <= iMax; j++) {
            pToken_Array[j]=pLink.Item(index + j);
            if (j == 0){
                iStart = pToken_Array[j].iStart;
            }
            if (j == iMax){
                iEnd = pToken_Array[j].iEnd;
            }
            Treap pSubTree;
            for (int k = 0;k< pToken_Array[j].Count(); k++) {
                strTmp = (j + 1) + "." + pToken_Array[j].Item(k);
                pSubTree = this.GetSubTree(strTmp);
                if (pSubTree!=null){
                    p = pSubTree.Elements();
                    while (p.HasMoreElements()) {
                        pStruct = (C_Seg_Struct) p.NextElement();
                        if (j < pStruct.strLine.split("\\s").length
                                    && (pStruct.strLine.split("\\s")[j].equals(pToken_Array[j].Item(k)))) {
                            pStruct.pFrom[j]=pToken_Array[j];//后面要清除掉
                            pTreapCache.insert(new C_K_Str(pStruct.strLine), pStruct);
                        }
                    }
                }
            }
        }
        // 标记一下，代表已经计算过
        pLink.Item(index + 0).pNextToken3 = pLink.Item(index + 1);
        
        boolean bFinal=false;
        boolean All_Child_Active;
        String strReturn = "";
        p = pTreapCache.Elements();
        while (p.HasMoreElements()) {
            pStruct = (C_Seg_Struct) p.NextElement();
            if (index + 2 < pLink.Count() && pStruct.iMax == 3) {
                All_Child_Active = true;
                for (int i = 0; i <3; i++) {
                    if (pStruct.pFrom[i] == null){
                        All_Child_Active = false;
                    }
                }
                if (All_Child_Active){
                    strReturn=strReturn+pStruct.strLine + "|";
                    if (pStruct.Final==1) bFinal=true;
                    break;
                }
            }
        }
         // 清空无用数据
        p = pTreapCache.Elements();
        while (p.HasMoreElements()) {
            pStruct = (C_Seg_Struct) p.NextElement();
            for (int i=0;i<4;i++){
                pStruct.pFrom[i]=null;
            }
        }
        if (!"".equals(strReturn)) {
            return new C_Token(strReturn, iStart,iEnd,bFinal);
        }
        return null;
    }
    
    public C_Token Segmentation_CompareWord2(C_Token_Link pLink, int index, int iCount) {
        //标记一下，代表已经计算过
        if ((pLink == null|| pLink.Count()< index + 2)) {
            return null;
        }
        Treap pTreapCache = new Treap();
        C_Token[] pTokenArray=new C_Token[iCount];
        int MaxI = pLink.Count();
        C_Seg_Struct pStruct;
        int iStart=0;
        int iEnd=0;
        String strTmp;
        TreapEnumerator p;
        int iMax = Math.min(iCount, MaxI - index) - 1;
        for (int j = 0; j <= iMax; j++) {
            pTokenArray[j]=pLink.Item(index + j);
            if (j == 0) {
                iStart = pTokenArray[j].iStart;
            }
            if (j == iMax) {
                iEnd = pTokenArray[j].iEnd;
            }
            Treap pSubTree;
            for (int k = 0; k <= (pTokenArray[j].Count() - 1); k++) {
                strTmp = (j + 1) + "." + pTokenArray[j].Item(k);
                pSubTree = this.GetSubTree(strTmp);//要匹配的结构树，一个个比较
                 
                if (pSubTree!=null) {
                    p = pSubTree.Elements();
                    while (p.HasMoreElements()) {
                        pStruct = (C_Seg_Struct) p.NextElement();
                        if (j < pStruct.strLine.split("\\s").length
                                    && pStruct.strLine.split("\\s")[j].equals(pTokenArray[j].Item(k))) {
                            pStruct.pFrom[j]=pTokenArray[j];//修改后，最后要清除掉
                            pTreapCache.insert(new C_K_Str(pStruct.strLine), pStruct);
                        }
                    }
                }
            }
        }
        //标记一下，代表已经计算过
        pLink.Item(index + 0).pNextToken2 = pLink.Item(index + 1);
        String strReturn;
        boolean b_All_Child_Active;
        boolean bFinal=false;
        strReturn = "";
        p = pTreapCache.Elements();
        while (p.HasMoreElements()) {
            pStruct = (C_Seg_Struct) p.NextElement();
            if (index + 1 < pLink.Count() && pStruct.iMax == 2) { // pStruct.iCount == 2
                b_All_Child_Active = true;
                for (int i = 0; i <2; i++) {
                    if (pStruct.pFrom[i] == null) {
                        b_All_Child_Active = false;
                    }
                }
                if (b_All_Child_Active) {
                    strReturn+=pStruct.strLine + "|";
                    if (pStruct.Final==1) bFinal=true;
                    break; 
                }
            }
        }
        // 清空无用数据
        p = pTreapCache.Elements();
        while (p.HasMoreElements()) {
            pStruct = (C_Seg_Struct) p.NextElement();
            for (int i=0;i<4;i++){
                pStruct.pFrom[i]=null;
            }
        }
        if (!"".equals(strReturn)) {    
            return new C_Token(strReturn,iStart,iEnd,bFinal);
        }
        return null;
    }


}
