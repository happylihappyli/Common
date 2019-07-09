package com.funnyai.Segmentation;

import com.funnyai.data.C_K_Int;
import com.funnyai.data.TreapEnumerator;
import com.funnyai.data.Treap;
import com.funnyai.data.C_K_Str;
import com.funnyai.data.Treap_String;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.funnyai.io.Old.S_File;
import com.funnyai.string.Old.S_Strings;

public class C_Filter_Invert {

	public Treap_String Key_Index; 
	public Treap glb_Treap_URL_Check; 
	public static int ReserveSpace = 1024 * 2; 
	public Treap_String pTreapWord = new Treap_String(); 
	public Treap TreapFilter = null;
	
	public void loadFilter(){
		if (TreapFilter==null){
			TreapFilter=new Treap();
		}
		
		String[] strFiles={"Filter1.Filter"};
		
		for (int i=0;i<strFiles.length;i++){
			System.out.println(C_Filter_Invert.class.getResource("/").toString());
			System.out.println("File:"+strFiles[i]);
			InputStream url=C_Filter_Invert.class.getResourceAsStream("/data/"+strFiles[i]);
			try {
				loadFilterFile(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String loadFilterFile(String strFile) throws IOException{
		//读取过滤文件
		return loadFilterFile(new FileInputStream(strFile));
	}
	
	public void loadFilterFromContent(String strLine){
		//读取Filter词汇到TreapFilter
		if (TreapFilter==null){
			TreapFilter=new Treap();
		}
		TreapFilter.insert(new C_K_Str(strLine), strLine);
	}
	
	public String loadFilterFile(InputStream in) throws IOException{
		if (TreapFilter==null){
			TreapFilter=new Treap();
		}
	    //把过滤单词读取到内存，这样就可以分词了,从Txt文件中读取内容。 
		InputStreamReader FS = new InputStreamReader(in, "UTF-8");
		BufferedReader SR = new BufferedReader(FS); // 文件输入流为
		String strLine;
	    int Count = 0;
	    
		strLine = SR.readLine();
		if (strLine.length()>1){
			if ((int)strLine.charAt(0)==65279){
				strLine=strLine.substring(1);
			}
		}
		
		while (strLine != null){
			if (strLine.length()>0){
	            TreapFilter.insert(new C_K_Str(strLine), strLine);
	            Count += 1;
			}
			strLine = SR.readLine();
		}
	
		SR.close();
		FS.close();
	
		SR = null;
		FS = null;
		System.out.println("TreapFilterCount:" + TreapFilter.Size()); 
	    
	    return "词库初始化完毕！";
	}

	public String Segmentation_List(String strContent, boolean bNumFilter)
	{
	    
	    Treap pTreap = Segmentation(strContent, true, bNumFilter);
	    TreapEnumerator p = pTreap.Elements(true);
	    String strKey;
	    String strReturn = "";
	    
	    while ((p.HasMoreElements())) {
	        strKey = (String)p.NextElement();
	        strReturn += strKey + " ";
	    }
	    
	    return strReturn.trim();
	    
	}

	public String FilterWord(String strContent){
		if (TreapFilter==null){
			loadFilter();
			System.out.println("Loading Filter Word");
		}
		String[] strSplit=strContent.split("\\ +");
	
		String strReturn="";
		for (int i=0;i<strSplit.length;i++){
			if (TreapFilter.find(new C_K_Str(strSplit[i]))==null){
				strReturn+=" "+strSplit[i];
			}
		}
		
		return strReturn.trim();
	}
	
	public String InitDictionary(String strFile) throws IOException{
		if (S_File.Exists(strFile) == true) {
			System.out.println(strFile+"..."); 
			return InitDictionary(new FileInputStream(strFile));
		}
	    else { 
	        return "词库文件 "+strFile+" 不存在"; 
	    } 
	}
	
//	public String loadDicFile_FromContent(String strContent) {
//
//		String strLeft,strLine;
//        int Count = 0; 
//        Treap pTreap; 
//        C_Word_Convert pWord;
//		
//		String[] strSplit=strContent.split(",");
//		
//		for (int i=0;i<strSplit.length;i++){
//			strLine=strSplit[i];
//			if (strLine.length()>0){
//				strLeft = strLine.substring(0,1); 
//	            pTreap = (Treap) pTreapWord.find(new C_K_Str(strLeft)); 
//	            if (pTreap == null) { 
//	                pTreap = new Treap(); 
//	                pTreapWord.insert(new C_K_Str(strLeft), pTreap); 
//	            } 
//	            pWord = new C_Word_Convert(strLine); 
//	            pTreap.insert(new C_K_Str(pWord.Word), pWord); 
//	            if (strLine.length() > pTreap.KeyMaxLen) { 
//	                pTreap.KeyMaxLen = strLine.length(); 
//	            } 
//	            Count += 1;
//			}
//		}
//		System.out.println("ListCount:" + pTreapWord.Size() + ",WordCount:" + Count); 
//        
//        return "词库初始化完毕！";
//	} 
	
	
	public String InitDictionary(InputStream in) throws IOException{
	    //把单词读取到内存，这样就可以分词了,从Txt文件中读取内容。 
    	InputStreamReader FS = new InputStreamReader(in, "UTF-8");
		BufferedReader SR = new BufferedReader(FS); // 文件输入流为
		
        int Count = 0;
        
        String strLine = SR.readLine();
		if (strLine.length()>1){
			if ((int)strLine.charAt(0)==65279){
				strLine=strLine.substring(1);
			}
		}
		
		while (strLine != null){
			read_A_Line_FromDictionary(strLine);
			Count += 1;
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
		String strInput="NOKIA 诺基亚 6120C 直板手机 黑色 行货";
		C_Filter_Invert pSeg=new C_Filter_Invert();

		pSeg.InitDictionary();
		strInput=pSeg.segmentationList(strInput);
		System.out.println(strInput);
	}

	public void InitDictionary() {
		String [] strFiles={"Spell.TXT","Brand.TXT","HardWare.TXT"};
		InitDictionary(strFiles);
	}
	
	public void InitDictionary(String[] strFiles) {
		for (int i=0;i<strFiles.length;i++){
			System.out.println("File:"+strFiles[i]);
			System.out.println(C_Filter_Invert.class.getResource("/").toString());
			InputStream url=C_Filter_Invert.class.getResourceAsStream("/data/"+strFiles[i]);
			try {
				if (url!=null){
					InitDictionary(url);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadDicFileInPath(String strDictionaryDir) { 
        File[] pFiles= S_File.GetFiles(strDictionaryDir,"");//*.txt
        
	    int i; 
	    for (i = 0; i < pFiles.length; i++) { 
	        try{
	        	InitDictionary(pFiles[i].getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
	    } 
	}
 
	public String Segmentation_CompareWord(Treap pTreap, String[] strSplit, int i, int MaxI) 
	{ 
	    
	    C_Word_Convert pWordConvert; 
	    String strTmp = ""; 
	    String strReturn = ""; 
	    int j; 
	    
	    for (j = i; j < Math.min(MaxI, i + pTreap.KeyMaxLen); j++) { 
	    	strTmp +=S_Strings.ReplaceNLP_Invert(strSplit[j]);
	    } 
	    
	    for (j = strTmp.length(); j >= 2; j += -1) { 
	    	pWordConvert = (C_Word_Convert) pTreap.find(new C_K_Str(strTmp.substring(0, j))); 
	        if ((pWordConvert != null)) { 
	            if (pWordConvert.OutPutWord!=null && pWordConvert.OutPutWord.equals("")==false) { 
	                strReturn = pWordConvert.OutPutWord; 
	            } 
	            else { 
	                strReturn = pWordConvert.Word; 
	            } 
	            break; 
	        } 
	    } 
	    
	    return strReturn; 
	} 


	public String segmentationList(String strContent) { 
	    Treap pTreap = Segmentation(strContent,true); 
	    TreapEnumerator p = pTreap.Elements(true); 
	    String strKey; 
	    String strReturn = ""; 
	    
	    while ((p.HasMoreElements())) { 
	        strKey = (String)p.NextElement(); 
	        strReturn +=strKey+" ";
	    } 
	    
	    return strReturn.trim(); 
	} 

	private Treap Segmentation(String strContent,boolean bList) 
	{ 
	    //分词，返回结果为一棵词树，这棵树都是以词汇的第一个汉字为Key，他的子树势这个汉字对应的词 
	    //String[] strSplit = strContent.split(","); 
	    Treap pTreapReturn = new Treap(); 
	    
	    pTreapReturn = Segmentation_Sub(pTreapReturn,strContent,bList); 

	    return pTreapReturn; 
	} 

	public Treap Segmentation(String strContent, boolean bList, boolean bNumFilter)
	{
	    Treap pTreapReturn = new Treap();
	    pTreapReturn = Segmentation_Sub(pTreapReturn, strContent, bList, bNumFilter);
	    
	    return pTreapReturn;
	}

	public Treap Segmentation_Sub(
			Treap pTreapReturn,String strContent,
			boolean bList, boolean bNumFilter)
	{
	    
	    strContent = S_Strings.Blank_Chinese(strContent);
	    String[] strSplit = strContent.split("\\ +");
	    int i;
	    int j;
	    String strReturnWord = "";
	    String[] strSplitTmp;
	    Treap pTreap;
	    int MaxI = strSplit.length ;
	    for (i = 0; i <MaxI; i++) {
	        if (strSplit[i].length() == 1) {
	            //Èç¹ûÊÇÖÐÎÄ
	            pTreap = (Treap) pTreapWord.find(new C_K_Str(strSplit[i]));
	            if ((pTreap != null)) {
	                strReturnWord = Segmentation_CompareWord(pTreap, strSplit, i, MaxI);
	                if (strReturnWord != "") {
	                    strSplitTmp = strReturnWord.split("\\ +");
	                    for (j = 0; j <strSplitTmp.length; j++) {
	                        if (bList) {
	                            pTreapReturn.insert(new C_K_Int(i * 10 + j), strSplitTmp[j]);
	                        }
	                        else {
	                            pTreapReturn.insert(new C_K_Str(strSplitTmp[j]), strSplitTmp[j]);
	                        }
	                        i = i + strSplitTmp[j].length();
	                    }
	                    i = i - 1;
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
	        else if (strSplit[i].length() > 1) {
	            strReturnWord = strSplit[i];
	            if (bNumFilter) {
	                //¹ýÂËÊý×Ö
	                if (S_Strings.isNumeric(strReturnWord) == false) {
	                    if (bList) {
	                        pTreapReturn.insert(new C_K_Int(i * 10), strReturnWord);
	                    }
	                    else {
	                        pTreapReturn.insert(new C_K_Str(strReturnWord), strReturnWord);
	                    }
	                }
	            }
	            else {
	                if (bList) {
	                    pTreapReturn.insert(new C_K_Int(i * 10), strReturnWord);
	                }
	                else {
	                    pTreapReturn.insert(new C_K_Str(strReturnWord), strReturnWord);
	                }
	            }
	            
	        }
	    }
	    return pTreapReturn;
	}

	public void read_A_Line_FromDictionary(String strLine)
	{
            strLine = strLine.trim();
            if (strLine.startsWith("\'")) {
                return;
            }
            if (("".equals(strLine))) {
                return;
            }
            String strLeft;
            C_Word_Convert pWord;
            strLeft = strLine.substring(0, 1);
            Treap pTreap = (Treap) pTreapWord.find(new C_K_Str(strLeft));
            if ((pTreap == null)) {
                pTreap = new Treap();
                pTreapWord.insert(new C_K_Str(strLeft), pTreap);
            }
            pWord = new C_Word_Convert(strLeft, "", "");
            pTreap.insert(new C_K_Str(pWord.Word), pWord);
            if (strLine.length() > pTreap.KeyMaxLen) {
                pTreap.KeyMaxLen = strLine.length();
            }
	}

	
	public boolean ifHasThisWord(String strWord){
		Treap  pTreap= (Treap) pTreapWord.find(new C_K_Str(strWord.substring(0,1))); 
        if (pTreap != null){
        	C_Word_Convert pWordConvert = (C_Word_Convert) pTreap.find(new C_K_Str(strWord)); 
	        if (pWordConvert != null) { 
	        	return true;
	        } 
        }
		return false;
	}
	
	public String Filter(String strInput)
	{
	    String[] strSplit = strInput.split("\\ +");
	    String strReturn = "";
	    for (int i = 0; i < strSplit.length; i++) {
	        if (this.ifHasThisWord(strSplit[i])) {
	            strReturn += strSplit[i] + " ";
	        }
	    }
	    
	    return strReturn.trim();
	}

	
	private Treap Segmentation_Sub(
				Treap pTreapReturn, String strContent,
				boolean bList) 
	{ 
	    
	    strContent = S_Strings.Blank_Chinese(strContent); 
	    String[] strSplit = strContent.split(" "); 
	    int i,j; 
	    String strReturnWord = ""; 
	    String[] strSplitTmp; 
	    Treap pTreap; 
	    int MaxI = strSplit.length; 
	    for (i = 0; i < MaxI; i++) { 
	        if (strSplit[i].length() == 1) { 
	            //如果是中文 
	            pTreap = (Treap) pTreapWord.find(new C_K_Str(strSplit[i])); 
	            if ((pTreap != null)) { 
	                strReturnWord = Segmentation_CompareWord(pTreap, strSplit, i, MaxI); 
	                if (strReturnWord !=null && strReturnWord.length()>0) { 
	                    ;
	                }else{
	                	strReturnWord=strSplit[i];
	                }
	                
	                strSplitTmp = strReturnWord.split(" "); 
                    for (j = 0; j <strSplitTmp.length; j++) { 
                    	strReturnWord=strSplitTmp[j];
                        if (bList){
    	            		pTreapReturn.insert(new C_K_Int(i*10+j), strReturnWord);
    	            	}else{
    	            		pTreapReturn.insert(new C_K_Str(strReturnWord), strReturnWord);
    	            	}
                        i = i + strReturnWord.length();
                    } 
                    i = i- 1; 
	            } 
	        } 
	        else if (strSplit[i].length() > 1) { 
	            strReturnWord = strSplit[i]; 
	            if (S_Strings.isNumeric(strReturnWord) == false) {
	            	if (bList){
	            		pTreapReturn.insert(new C_K_Int(i*10), strReturnWord);
	            	}else{
	            		pTreapReturn.insert(new C_K_Str(strReturnWord), strReturnWord);
	            	}
	            } 
	        } 
	    } 
	    return pTreapReturn; 
	}

}
