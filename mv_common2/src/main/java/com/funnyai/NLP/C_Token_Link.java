package com.funnyai.NLP;

import com.funnyai.data.C_K_Str;
import com.funnyai.data.Treap;
import java.util.ArrayList;

public class C_Token_Link {
    public static int MaxID = 1;

    public int iStart=0;//结构涵盖的开始和结尾。
    public int iEnd=0;
    
    public int ID;
    public String Tag = "";
    
    public String strFrom = "";
    
    ArrayList<C_Token> pList = new ArrayList<>(); 
    public String Sentence="";//要分析的句子
    
    public C_Token Item(int index) {
       return (C_Token) pList.get(index);
    }
    
    public void Add(C_Token pToken)
    {
        pList.add(pToken);
    }
    
    public int Count()
    {
        return pList.size();
    }
    
    
    public C_Token_Link()
    {
        ID = MaxID;
        MaxID += 1;
    }
    
    public void Init(String strInput){
        Sentence = strInput;
        String[] strSplit  = strInput.split("\\s");//空格

        for (int i= 0;i<strSplit.length;i++){
            C_Token pToken = new C_Token(strSplit[i], i, i + 1,false);//, 1);
            pList.add(pToken);
        }

        this.iStart = pList.get(0).iStart;
        this.iEnd = pList.get(pList.size() - 1).iEnd;
    }
    
    public static void AddKeyToTreap(I_Robot pRobot, C_Token_Link pLink, Treap pTreap) {
        C_Token pToken;
        String strLine;
        for (int i = 0; i <pLink.Count(); i++) {
            //Topic 激活 (单词激活) 
            //有时候整个句子识别了也要激活一下句子中的词汇
            // 以方便后面的调用。
            pToken = pLink.Item(i);
            for (int j = 0; j<pToken.Count(); j++) {
                strLine = pToken.Item(j);
                if (strLine.length() > pRobot.Word_Min_Length) {
                    pTreap.insert(new C_K_Str(strLine), new C_Token_Key(strLine, pToken));
                    if (pToken.Name.startsWith("Word.L.")) {
                        
                    }else if (pToken.Name == null ? strLine != null : !pToken.Name.equals(strLine)) {
                        strLine = pToken.Name + "@" + strLine;
                        pTreap.insert(new C_K_Str(strLine), new C_Token_Key(strLine, pToken));
                    }
                }
            }
        }
    }
    
    
    public static void AddToQue(I_Robot pRobot,ArrayList<C_Token_Link> pArray,C_Token_Link pLink,Treap pTreap) {
        if (pLink == null) {
            return;
        }
        if (pArray.isEmpty()){
            pArray.add(pLink);
            AddKeyToTreap(pRobot, pLink, pTreap);
            pLink.clearNextToken();
        }
        else if (pLink.ID != pArray.get(pArray.size() - 1).ID) {
            pArray.add(pLink);
            AddKeyToTreap(pRobot, pLink, pTreap);
            pLink.clearNextToken();
        }
    }
    
//    public C_Token_Link(String strInput){
//        String[] strSplit = strInput.split("\\ +");
//        
//        for (int i = 0; i <strSplit.length; i++) {
//            C_Token pToken = new C_Token(strSplit[i],i,i+1);
//            pList.add(pToken);
//        }
//        
//    	this.iStart=pList.get(0).iStart;
//    	this.iEnd=pList.get(pList.size()-1).iEnd;
//    }
    
    public void Copy(C_Token_Link pTokenIn)
    {
        for (int i = 0; i <pTokenIn.pList.size(); i++) {
            C_Token pToken = new C_Token();
            pToken.Copy(pTokenIn.pList.get(i),0);
            pList.add(pToken);
        }
        
        this.iStart=pList.get(0).iStart;
    	this.iEnd=pList.get(pList.size()-1).iEnd;
    }
    
    public String ToString()
    {
        C_Token pToken;
        String strReturn = "";
        for (int i = 0; i <pList.size(); i++) {
            pToken = (C_Token) pList.get(i);
            strReturn += pToken.ToString(true) + " ";
        }
        
        return ID + ": " + strReturn.trim();
    }
    
    public boolean Equal(C_Token_Link pToken_Link2)
    {
        //±È½ÏÊÇ·ñÏàÍ¬
        if (pToken_Link2.pList.size() != pList.size()) {
            return false;
        }
        else {
            for (int i = 0; i <pList.size(); i++) {
                if (pToken_Link2.pList.get(i).Equal(pList.get(i)) == false) {
                    return false;
                }
            }
        }
        return true;
    }

    public void clearNextToken() {
        C_Token pToken;
        for (int i = 0; i <pList.size(); i++) {
            pToken = pList.get(i);
            pToken.pNextToken2 = null;
            pToken.pNextToken3 = null;
        }
    }
 
}
