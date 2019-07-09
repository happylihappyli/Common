package com.funnyai.NLP;

import java.util.ArrayList;

public class C_Token {

    public static int MaxID = 1;

    public int ID;

    public int iStart = 0;
    public int iEnd = 0;

    public String Tag;
    public String Name;

    public ArrayList<String> pList = new ArrayList<>();

    /**
     * A_B_C 可以分解为
     * A B C
     */
    public ArrayList<C_Token> pChild = new ArrayList<>();
    
    public C_Token pNextToken2;
    public C_Token pNextToken3;
    public boolean bCheckConvert;
    public boolean bFinal=false;//如果True，就不分解了
    public C_Token pFrom=null;//从哪个结构转化过来

    public String Item(int index) {
        if (index < pList.size()) {
            return (String) pList.get(index);
        } else {
            return "";
        }
    }

    public void RemoveAt(int index) {
        pList.remove(index);
    }

    public void Add(String strItem) {
        Name = strItem;
        pList.add(strItem);
    }

    public int Count() {
        return pList.size();
    }

    public String ToString() {
        String strReturn = "";
        for (int i = 0; i < pList.size(); i++) {
            strReturn += pList.get(i) + "|";
        }
        if (strReturn.endsWith("\\|")) {
            strReturn = strReturn.substring(0, strReturn.length() - 1);
        }
        return strReturn;
    }

    public String ToString(boolean showID) {
        String strReturn = "";
        for (int i = 0; i < pList.size(); i++) {
            strReturn += pList.get(i) + "|";
        }
        if (strReturn.endsWith("\\|")) {
            strReturn = strReturn.substring(0, strReturn.length() - 1);
        }
        return ID + "=" + strReturn;
    }

    private void Init() {
        ID = MaxID;
        MaxID += 1;
    }

    public C_Token() {
        Init();
    }
    
    public int Child_Size(){
        return pChild.size();
    }
    
    public C_Token(String strInput, int iStart, int iEnd,boolean bFinal) {
    	//单词的开始和结束

        this.iStart = iStart;
        this.iEnd = iEnd;
        this.bFinal = bFinal;

        if (strInput.equals("|")){
            Add("{c竖线}");
        }else{
            String[] strSplit = strInput.split("\\|");

            for (int i = 0; i < strSplit.length; i++) {
                Add(strSplit[i]);
            }
        }

        Init();
    }

    public void Copy(C_Token pTokenIn, int iStart) {
        for (int i = iStart; i < pTokenIn.pList.size(); i++) {
            Add((String) pTokenIn.pList.get(i));
        }
    }

    public boolean Equal(C_Token pTokenIn) {
        if (pTokenIn != null) {
            if (pTokenIn.pList.size() != pList.size()) {
                return false;
            } else {
                for (int i = 0; i < pList.size(); i++) {
                    if (pTokenIn.pList.get(i).equals(pList.get(i)) == false) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    //检查
    public boolean checkExits(String strWord) {

        for (int i = 0; i < pList.size(); i++) {
            if (pList.get(i).equals(strWord)) {
                return true;
            }
        }

        return false;
    }

}
