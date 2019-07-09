package com.funnyai.Segmentation;

import com.funnyai.data.C_K_Int;
import com.funnyai.data.TreapEnumerator;
import com.funnyai.data.Treap;
import com.funnyai.data.C_K_Str;
import com.funnyai.data.Treap_String;
import com.funnyai.io.C_File;
import com.funnyai.io.Old.S_File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.funnyai.net.Old.S_Net;
import com.funnyai.string.Old.S_Strings;
import static java.lang.System.out;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mapdb.DB;
import org.mapdb.DBMaker;

public class C_Segmentation {

    public Treap_String Key_Index;
    public Treap glb_Treap_URL_Check;
    public static int ReserveSpace = 1024 * 2;
    public Treap<Treap<C_Word_Seg>> pTreapWord = new Treap<>();
    public Treap TreapFilter = null;
    public int iWord_Count=0;//单词个数

    public void loadFilter() {
        if (TreapFilter == null) {
            TreapFilter = new Treap();
        }

        String[] strFiles = {"Filter1.Filter"};

        for (String strFile : strFiles) {
            System.out.println(C_Segmentation.class.getResource("/").toString());
            System.out.println("File:" + strFile);
            InputStream url = C_Segmentation.class.getResourceAsStream("/data/" + strFile);
            try {
                loadFilterFile(url);
            } catch (IOException ex) {
                Logger.getLogger(C_Segmentation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String loadFilterFile(String strFile) throws IOException {
        //读取过滤文件
        return loadFilterFile(new FileInputStream(strFile));
    }

    public void loadFilterFromContent(String strLine) {
        //读取Filter词汇到TreapFilter
        if (TreapFilter == null) {
            TreapFilter = new Treap();
        }
        TreapFilter.insert(new C_K_Str(strLine), strLine);
    }

    public String loadFilterFile(InputStream in) throws IOException {
        if (TreapFilter == null) {
            TreapFilter = new Treap();
        }
        //把过滤单词读取到内存，这样就可以分词了,从Txt文件中读取内容。 
        InputStreamReader FS = new InputStreamReader(in, "UTF-8");
        BufferedReader SR = new BufferedReader(FS); // 文件输入流为
        String strLine;

        strLine = SR.readLine();
        if (strLine.length() > 1) {
            if ((int) strLine.charAt(0) == 65279) {
                strLine = strLine.substring(1);
            }
        }

        while (strLine != null) {
            if (strLine.length() > 0) {
                TreapFilter.insert(new C_K_Str(strLine), strLine);
            }
            strLine = SR.readLine();
        }

        SR.close();
        FS.close();
        System.out.println("TreapFilterCount:" + TreapFilter.Size());

        return "词库初始化完毕！";
    }

    public String Segmentation_List(
            String strContent, int iMinLength,
            boolean bFilter_Symbol,
            boolean bNumFilter) {

        Treap pTreap = Segmentation(strContent, true, bNumFilter);
        TreapEnumerator p = pTreap.Elements(true);
        String strKey;
        String strReturn = "";

        while ((p.HasMoreElements())) {
            strKey = (String) p.NextElement();
            if (strKey.length() > iMinLength) {
                if (bFilter_Symbol) {
                    if (strKey.startsWith("{") == false) {
                        if (strKey.startsWith("&") == false) {
                            strReturn += strKey + " ";
                        }
                    }
                } else {
                    strReturn += strKey + " ";
                }
            }
        }

        return strReturn.trim();

    }

    public String FilterWord(String strContent) {
        if (TreapFilter == null) {
            loadFilter();
            System.out.println("Loading Filter Word");
        }
        String[] strSplit = strContent.split("\\ +");

        String strReturn = "";
        for (int i = 0; i < strSplit.length; i++) {
            if (TreapFilter.find(new C_K_Str(strSplit[i])) == null) {
                strReturn += " " + strSplit[i];
            }
        }

        return strReturn.trim();
    }

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

        while (strLine != null) {
            readLine_fromDic(strLine);
            Count += 1;
            strLine = SR.readLine();
        }

        SR.close();
        FS.close();
        System.out.println("ListCount:" + pTreapWord.Size() + ",WordCount:" + Count);

        return "词库初始化完毕！";
    }

    public static void main(String[] args) {
        String strInput = "？";
        C_Segmentation pSeg = new C_Segmentation();
        pSeg.InitDictionary();
        String strResult=pSeg.Segmentation_List(strInput,1,false,false);
        System.out.println(strResult);
    }

    public void InitDictionary() {
        String[] strFiles = {"Spell.TXT", "Brand.TXT", "HardWare.TXT"};
        InitDictionary(strFiles);
    }

    public void InitDictionary(String[] strFiles) {
        for (int i = 0; i < strFiles.length; i++) {
            System.out.println("File:" + strFiles[i]);
            System.out.println(C_Segmentation.class.getResource("/").toString());
            InputStream url = C_Segmentation.class.getResourceAsStream("/data/" + strFiles[i]);
            try {
                if (url != null) {
                    InitDictionary(url);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从seg_db中读取文件
     * @param strDictionaryDir
     * @param Robot_ID
     * @param strTag
     * @return 
     */
    public String InitDictionary_FromPath(String strDictionaryDir,String Robot_ID,String strTag) {
        String strFile =strDictionaryDir+"/seg.db";
        String strFile2=strFile+"."+Robot_ID+"_"+strTag+".db";
        S_File.Copy2File(strFile, strFile2);
        
        DB db = DBMaker.fileDB(strFile2).checksumHeaderBypass().make();
        ConcurrentMap map = db.hashMap("map").open();
        
        out.println("Segmentation Path="+strDictionaryDir);
        
        int Count_Pages=(int) map.get("size");
        
        for (int i=1;i<=Count_Pages;i++){
            String strReturn=(String) map.get("k"+i);
       
            try{
                JSONArray token = new  JSONArray(strReturn);
                int Count=token.length();
                if (Count>0){
                    for (int k=0;k<token.length();k++){
                        JSONObject t=token.getJSONObject(k);
                        String strLine = t.getString("Name");
                        readLine_fromDic(strLine);
                    }
                }
            }catch(Exception ex){
                out.println(strReturn);
            }
        }
        db.close();
        S_File.Delete(strFile2);
        return strFile2;
    }

    public void Init_Seg_FromWeb(String Path_Segmentation) {
        String strURL = "https://www.funnyai.com/funnyai/json_list_ai_word.php";
        String strData;
        int PageSize = 5000;
        int p = 0;
        while (p > -1) {
            try {
                p++;
                strData = "p=" + p;
            
                String strReturn;
                System.out.println("seg.from.web p="+p);
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
                        String strLine = t.getString("Name");
                        this.readLine_fromDic(strLine);
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
    
    
    /**
     * 从大到小匹配
     * @param pTreapWord
     * @param strSplit
     * @param i
     * @param Token_Max
     * @return 
     */
    public C_Segmentation_Return Segmentation_CompareWord(
            Treap pTreapWord, String[] strSplit, int i, int Token_Max) {
        C_Word_Seg pWord_Seg;
        String strTmp = "";
        String strReturn = "";
        String[] strTokens=new String[Token_Max + 10];
        int k = 1;
        int Token_Max_Now = Math.min(Token_Max, (i + pTreapWord.KeyMaxLen));
        for (int j = i; j <= Token_Max_Now; j++){
            strTmp =strTmp+strSplit[j];
            strTokens[k] = strTmp;
            k++;
        }
        int Count = 0;
        String strKey;
        for (int j = Token_Max_Now + 1; j >= 1; j = j -1){
            strKey = strTokens[j];
            pWord_Seg = (C_Word_Seg) pTreapWord.find(new C_K_Str(strKey));

            if (pWord_Seg != null) {
                Count = j;
                if (!"".equals(pWord_Seg.OutPutWord)) {
                    strReturn = pWord_Seg.OutPutWord;
                }else{
                    strReturn = pWord_Seg.Word;
                }
                break;
            }
        }
        
        C_Segmentation_Return R=new C_Segmentation_Return();
        R.Word=strReturn;
        R.Count=Count;
        return R;
    }
    
    
    /**
     * 分词，返回结果为一棵词树，
     * 这棵树都是以词汇的第一个汉字为Key，
     * 它的子树势这个汉字对应的词
     * @param strContent
     * @param bList
     * @param bNumFilter
     * @return 
     **/
    public Treap Segmentation(String strContent, boolean bList, boolean bNumFilter) {
        Treap pTreapReturn = new Treap();
        pTreapReturn = Segmentation_Sub(pTreapReturn, strContent, bList, bNumFilter);
        return pTreapReturn;
    }
    


    public void Add_To_Treap(
            Treap pTreapReturn,
            boolean bNumFilter,
            boolean bList,
            int index,
            String strReturnWord) {
        if (bNumFilter) {
            if (S_Strings.isNumeric(strReturnWord) == false) {
                if (bList) {
                    pTreapReturn.insert(new C_K_Int(index), strReturnWord);
                } else {
                    pTreapReturn.insert(new C_K_Str(strReturnWord), strReturnWord);
                }
            }
        } else {
            if (bList) {
                pTreapReturn.insert(new C_K_Int(index), strReturnWord);
            } else {
                pTreapReturn.insert(new C_K_Str(strReturnWord), strReturnWord);
            }
        }
    }

    public void readLine_fromDic(String strLine) {
        
        if (strLine == null) {
            return;
        }
        if (strLine.startsWith("\'")) {
            return;
        }
        if ("".equals(strLine)) {
            return;
        }
        Treap<C_Word_Seg> pTreap;
        String strLeft;
        C_Word_Seg pWord;
        String strLine2 = S_Strings.Blank_Chinese(strLine);
        String[] strSplit = strLine2.split("\\s");
        strLeft = strSplit[0];
        pTreap = pTreapWord.find(new C_K_Str(strLeft));//第一个汉字或英文单词
        if (pTreap == null) {
            pTreap = new Treap();
            pTreapWord.insert(new C_K_Str(strLeft), pTreap);
        }
        if (strLine.length() > pTreap.KeyMaxLen) {
            pTreap.KeyMaxLen = strSplit.length;
        }
        String[] strSplit2 = strLine.split("=");
        if (strSplit2.length > 1) {
            pWord = new C_Word_Seg(strSplit2[0], strSplit2[1], "");
        }
        else {
            pWord = new C_Word_Seg(strSplit2[0], "", "");
        }
        if (pWord.Word.contains(" ")) {
            return;
        }
        C_Word_Seg pWord_Seg = pTreap.find(new C_K_Str(pWord.Word));
        if (pWord_Seg!=null){
            if ("".equals(pWord_Seg.OutPutWord)) {
                pTreap.insert(new C_K_Str(pWord.Word), pWord);
            }
        }
        else {
            this.iWord_Count+=1;
            pTreap.insert(new C_K_Str(pWord.Word), pWord);
        }
    }

    public boolean ifHasThisWord(String strWord) {
        Treap<C_Word_Seg> pTreap = pTreapWord.find(new C_K_Str(strWord.substring(0, 1)));
        if (pTreap != null) {
            C_Word_Seg pWord =  pTreap.find(new C_K_Str(strWord));
            if (pWord != null) {
                return true;
            }
        }
        return false;
    }

    public Treap Segmentation_Sub(
            Treap pTreapReturn,
            String strContent, 
            boolean bList, 
            boolean bNumFilter) {
        strContent = S_Strings.Blank_Chinese(strContent);
        String[] strSplit = strContent.split("\\s");
        int i;
        int j;
        String strReturnWord = "";
        String[] strSplitTmp;
        Treap pTreap;
        int Token_Max = strSplit.length-1;
        for (i = 0; i <= Token_Max; i++) {
            pTreap = (Treap) pTreapWord.find(new C_K_Str(strSplit[i]));
            if (pTreap != null) {
                C_Segmentation_Return pSegReturn = Segmentation_CompareWord(pTreap, strSplit, i, Token_Max);
                strReturnWord = pSegReturn.Word;
                int Count = pSegReturn.Count;
                if ((!"".equals(strReturnWord))) {
                    strSplitTmp = strReturnWord.split("\\s");
                    for (j = 0; j <strSplitTmp.length; j++) {
                        if (bList) {
                            pTreapReturn.insert(new C_K_Int(i * 10 + j), strSplitTmp[j]);
                        }
                        else {
                            pTreapReturn.insert(new C_K_Str(strSplitTmp[j]), strSplitTmp[j]);
                        }
                    }
                    i = i + (Count - 1);
                }
                else {
                    strReturnWord = strSplit[i];
                    if (bList) {
                        pTreapReturn.insert(new C_K_Int(i * 10), strReturnWord);
                    }
                    else {
                        pTreapReturn.insert(new C_K_Str(strReturnWord), strReturnWord);
                    }
                }
            }
            else {
                strReturnWord = strSplit[i];
                if (bList) {
                    pTreapReturn.insert(new C_K_Int(i * 10), strReturnWord);
                }
                else {
                    pTreapReturn.insert(new C_K_Str(strReturnWord), strReturnWord);
                }
            }
        }
        return pTreapReturn;
    }
    
}
