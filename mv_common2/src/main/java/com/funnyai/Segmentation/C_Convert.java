package com.funnyai.Segmentation;

import com.funnyai.data.C_K_Int;
import com.funnyai.data.TreapEnumerator;
import com.funnyai.data.Treap;
import com.funnyai.data.C_K_Str;
import com.funnyai.data.Treap_String;
import com.funnyai.Math.Old.S_Math;
import java.io.*;

import com.funnyai.io.Old.S_File;
import com.funnyai.io.C_File;
import com.funnyai.string.Old.S_Strings;
import com.funnyai.NLP.C_Token;
import com.funnyai.NLP.C_Token_Link;
import com.funnyai.net.Old.S_Net;
import static java.lang.System.out;
import org.json.JSONArray;
import org.json.JSONObject;

public class C_Convert {

    public Treap_String pTreapWord = new Treap_String();
    public Treap pTreapActive = new Treap();

    public String InitDictionary(String strFile) throws IOException {
        if (S_File.Exists(strFile) == true) {
            System.out.println(strFile + "...");
            return InitDictionary(new FileInputStream(strFile));
        } else {
            return "词库文件 " + strFile + " 不存在";
        }
    }

    public String InitDictionary(InputStream in) throws IOException {
        //把单词读取到内存，这样就可以分词了,从Txt文件中读取内容。 
        InputStreamReader FS = new InputStreamReader(in, "UTF-8");
        BufferedReader SR = new BufferedReader(FS); // 文件输入流为

        int Count = 0;
        String strLine = SR.readLine();
        if (strLine != null && strLine.length() > 1) {
            if ((int) strLine.charAt(0) == 65279) {
                strLine = strLine.substring(1);
            }
        }

        int pos = 0;
        while (strLine != null) {
            if (strLine.length() > 0) {
                pos = strLine.indexOf("=");
                if (pos > -1) {
                    this.read_FromLine(strLine);
                    Count += 1;
                }
            }
            strLine = SR.readLine();
        }

        SR.close();
        FS.close();

        SR = null;
        FS = null;
        System.out.println("ListCount:" + pTreapWord.Size() + ",WordCount:" + Count);

        return "词库初始化完毕！";
    }

    public static void main(String[] args) {
    }
    
    
    public void Init_FromWeb(String Path_Segmentation) {
        String strURL = "https://www.funnyai.com/funnyai/json_list_ai_rename.php";
        String strData;
        int PageSize = 5000;
        int p = 0;
        while (p > -1) {
            try {
                p++;
                strData = "p=" + p;
            
                String strReturn;
                System.out.println("rename.from.web p="+p);
                strReturn = S_Net.sendPost(strURL, "POST", strData, "utf-8", "");
                C_File pFile=S_File.Write_Begin(Path_Segmentation+p+".txt",false,"utf-8");
                
                
                int Count =0;
                int IndexStr = strReturn.indexOf("{");
                if (IndexStr>-1){
                    strReturn = strReturn.substring(IndexStr);

                    JSONObject pJobject = new JSONObject(strReturn);
                    Count = pJobject.getInt("Count");
                    
                    JSONArray token = pJobject.getJSONArray("data");

                    for (int k=0;k<token.length();k++){
                        JSONObject t=token.getJSONObject(k);
                        String strName = t.getString("Name");
                        String strConcept = t.getString("Concept");
                        String strLine=strName+"="+strConcept;
                        this.read_FromLine(strLine);
                        S_File.Write_Line(pFile, strLine);
                    }
                }
                pFile.Close();
                
                if (Count < PageSize){
                    break; 
                }
            }
            catch (Exception ex) {
            }
        }
    }
    

    public void InitDictionary() {
        String[] strFiles = {"Convert.TXT"};
        InitDictionary(strFiles);
    }

    public void InitDictionary(String[] strFiles) {
        for (int i = 0; i < strFiles.length; i++) {
            System.out.println("File:" + strFiles[i]);
            System.out.println(C_Convert.class.getResource("/").toString());
            InputStream url = C_Convert.class.getResourceAsStream("/data/" + strFiles[i]);
            try {
                if (url != null) {
                    InitDictionary(url);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadDicFileInPath(String strDictionaryDir) {
        File[] pFiles = S_File.GetFiles(strDictionaryDir, ".txt");//*.txt

        for (int i = 0; i < pFiles.length; i++) {
            try {
                InitDictionary(pFiles[i].getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //
    public C_Token_Link getConvert(C_Token_Link pToken_Link) {
        C_Token_Link pLinkR = new C_Token_Link();
        C_Token pToken;
        C_Token pToken_Old;

        for (int i = 0; i < pToken_Link.Count(); i++) {
            pToken_Old = pToken_Link.Item(i);
            pToken = getConvert_Sub(pToken_Old);

            if (pToken.Item(0).indexOf("_") > 0) {
                for (int j = 0; j < pToken_Old.pChild.size(); j++) {
                    pToken.pChild.add(pToken_Old.pChild.get(j));
                }
            } else {
                pToken.pChild.add(pToken_Old);
            }
            pToken.iStart = pToken_Old.iStart;
            pToken.iEnd = pToken_Old.iEnd;

            pLinkR.Add(pToken);
        }

        if (pLinkR.Equal(pToken_Link)) {
            pLinkR.iStart = pLinkR.Item(0).iStart;
            pLinkR.iEnd = pLinkR.Item(pLinkR.Count() - 1).iEnd;
            pLinkR = pToken_Link;
        }

        return pLinkR;
    }

    public C_Token getConvert_Sub(C_Token pToken) {

        String strTmp = "";
        String strKey = pToken.Item(0);

        Treap pTreap = new Treap();
        if (strKey.split("_").length == 1) {
            for (int i = 0; i < pToken.Count(); i++) {
                strKey = pToken.Item(i);
                pTreap.insert(new C_K_Str(strKey), strKey);
            }
        }

        for (int i = 0; i < pToken.Count(); i++) {
            strKey = pToken.Item(i);

            Treap pTreapReplace = (Treap) pTreapWord.find(new C_K_Str(strKey));

            if (S_Strings.isNumeric(strKey)) {
                strKey = "{INT}";
                pTreap.insert(new C_K_Str(strKey), strKey);
            }

            if (pTreapReplace != null) {
                TreapEnumerator p = pTreapReplace.Elements(true);

                int iCount = 0;
                while ((p.HasMoreElements())) {
                    strTmp = (String) p.NextElement();
                    iCount += 1;
                    pTreap.insert(new C_K_Str(strTmp), strTmp);
                }
            }
        }

        C_Token pTokenReturn = new C_Token();

        if (pTreap.Size() > 0) {
            TreapEnumerator p = pTreap.Elements(false);
            while ((p.HasMoreElements())) {
                strTmp = (String) p.NextElement();
                pTokenReturn.Add(strTmp);
            }
        } else {
            pTokenReturn.Copy(pToken, 0);
        }

        return pTokenReturn;
    }

    public void read_FromLine(String strLine) {
        if ("".equals(strLine)) {
            return;
        }

        if (strLine.startsWith("'")) {
            return;
        }

        int pos = strLine.indexOf("=");

        if (pos == -1) {
            return;
        }

        String strLeft = strLine.substring(0, pos);
        String strRight = strLine.substring(pos + 1);
        read_fromDic(strLeft, strRight, "");
    }

    public int iCount_Read = 0;

    public void read_fromDic(String strLeft, String strRight, String strActive) {
        strLeft = strLeft.trim();
        if ("".equals(strLeft)) {
            return;
        }
        if (strLeft.startsWith("'")) {
            return;
        }
        iCount_Read += 1;
        
        if ("{s}".equals(strRight)){
            out.println(strRight);
        }
        
        if (iCount_Read % 1000 ==0){
            out.println("CRL=" + iCount_Read + ":" + strLeft + ":" + strRight + ":" + strActive);
        }
        
        Treap pTreapReplace = (Treap) pTreapWord.find(new C_K_Str(strLeft));
        if (pTreapReplace == null){
            pTreapReplace = new Treap();
            pTreapWord.insert(new C_K_Str(strLeft), pTreapReplace);
        }
        C_Word_Convert pWord = new C_Word_Convert(strLeft, strRight, strActive);
        pTreapReplace.insert(new C_K_Str(strRight), pWord);

        //同一个word激活的结构放在一起
        Treap pTreapWord_Convert = GetTreap(strActive);
        if (pTreapWord_Convert == null){
            pTreapWord_Convert = new Treap();
            pTreapActive.insert(new C_K_Str(strActive), pTreapWord_Convert);
        }
        pTreapWord_Convert.insert(new C_K_Int(pWord.ID), pWord);
    }

    public Treap GetTreap(String strKey) {
        return (Treap) pTreapActive.find(new C_K_Str(strKey));
    }

    public boolean ifHasThisRule(String strLeft) {
        String strTmp = (String) pTreapWord.find(new C_K_Str(strLeft));
        return strTmp != null && strTmp.equalsIgnoreCase("") == false;
    }

    public C_Token_Link_and_Add getConvert_Token(
            Treap pTreap_Active,
            C_Token pToken) {
        boolean bAdd=false;
        if (pToken.Count() == 0) {
            return null;
        }
        pToken.bCheckConvert = true;
        String strKey = pToken.Item(0);
        Treap pTreap_Token = new Treap();
        C_Word_Convert pConvert;
        if (strKey.split("\\s").length == 1) {
            for (int i = 0; i<pToken.Count(); i++) {
                strKey = pToken.Item(i);
                pConvert = new C_Word_Convert(strKey, "", "");
                pTreap_Token.insert(new C_K_Str(strKey), pConvert);
            }
        }
        C_Token_Link pToken_Link = new C_Token_Link();
        for (int i = 0; i < pToken.Count(); i++) {
            strKey = pToken.Item(i);
            if (S_Math.isNumeric(strKey)) {
                String strOutput = "{INT}";
                if ((pTreap_Token.find(new C_K_Str(strOutput)) == null)) {
                    //如果是数字，给它放到 {INT} 集合里
                    bAdd = true;
                    pConvert = new C_Word_Convert(strKey, strOutput, "");
                    pTreap_Token.insert(new C_K_Str(strOutput), pConvert);
                }
            }
            Treap pTreapReplace = (Treap) pTreapWord.find(new C_K_Str(strKey));
            // 查找是否有其他从 可以泛化 的集合
            if (pTreapReplace != null) {
                TreapEnumerator p;
                p = pTreapReplace.Elements(true);
                while (p.HasMoreElements()) {
                    pConvert = (C_Word_Convert) p.NextElement();
                    pConvert.OutPutWord_New=pConvert.OutPutWord;
                    C_Token pToken_Old=pToken.pFrom;
                    if (pToken_Old!=null){
                        String strName="";
                        switch (pConvert.OutPutWord_New) {
                            case "{@1}":
                                if (pToken_Old.pChild.size()>0 && pToken_Old.pChild.get(0).pChild.size()>1){
                                    strName=pToken_Old.pChild.get(0).pChild.get(0).Item(0);
                                }   if (strName.startsWith("{")==false && "".equals(strName)==false){
                                    strName="{"+strName+"s}";
                                }
                                pConvert.OutPutWord_New=strName;
                                break;
                            case "{@2}":
                                if (pToken_Old.pChild.size()>0 && pToken_Old.pChild.get(0).pChild.size()>1){
                                    strName=pToken_Old.pChild.get(0).pChild.get(1).Item(0);
                                }   
                                if (strName.startsWith("{")==false && "".equals(strName)==false){
                                    strName="{"+strName+"s}";
                                }
                                pConvert.OutPutWord_New=strName;
                                break;
                            case "{@3}":
                                if (pToken_Old.pChild.size()>0 && pToken_Old.pChild.get(0).pChild.size()>2){
                                    strName=pToken_Old.pChild.get(0).pChild.get(2).Item(0);
                                }   
                                if (strName.startsWith("{")==false && "".equals(strName)==false){
                                    strName="{"+strName+"s}";
                                }
                                pConvert.OutPutWord_New=strName;
                                break;
                            default:
                                break;
                        }
                    }
                    if (pTreap_Token.find(new C_K_Str(pConvert.OutPutWord_New)) == null
                            && ("".equals(pConvert.Active_Word)
                            || pTreap_Active.find(new C_K_Int(pConvert.ID)) != null)) {
                        if (pConvert.OutPutWord_New.equals("{s}")==false && pConvert.OutPutWord_New.equals("")==false){
                            bAdd = true;
                            pTreap_Token.insert(new C_K_Str(pConvert.OutPutWord_New), pConvert);
                        }
                    }
                }
            }
        }
        String[] strSplit;
        C_Token pTokenR = new C_Token();
        pTokenR.pFrom=pToken;
        if (pTreap_Token.Size() > 0){
            TreapEnumerator p;
            p = pTreap_Token.Elements(false);
            while (p.HasMoreElements()) {
                pConvert = (C_Word_Convert) p.NextElement();
                strSplit = pConvert.OutPutWord.split("\\s");
                if (strSplit.length > 1){
                    for (String strSplit1 : strSplit) {
                        pToken_Link.Add(new C_Token(strSplit1, 0, 0, true));
                    }
                } else if ((!"".equals(pConvert.OutPutWord_New))) {
                    pTokenR.Add(pConvert.OutPutWord_New);
                } else if ((!"".equals(pConvert.OutPutWord))) {
                    pTokenR.Add(pConvert.OutPutWord);
                } else {
                    pTokenR.Add(pConvert.Word);
                }
            }
            if (pToken_Link.Count() == 0){
                pToken_Link.Add(pTokenR);
            }
        } else {
            pTokenR.Copy(pToken, 0);
            pToken_Link.Add(pTokenR);
        }
        C_Token_Link_and_Add pToken_Link_and_Add=new C_Token_Link_and_Add();
        pToken_Link_and_Add.pLink= pToken_Link;
        pToken_Link_and_Add.bAdd = bAdd;
        return pToken_Link_and_Add;
    }

}
