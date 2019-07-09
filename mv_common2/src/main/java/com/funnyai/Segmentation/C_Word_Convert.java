package com.funnyai.Segmentation;

public class C_Word_Convert 
{ 
    /**
     * 单词 
     */
    public String Word; 
    
    /**
     * 分词后的单词，比如 Word=发展中国, OutPutWord=发展 中国  
     * 如果为空，则无分词 
     */
    public String OutPutWord=""; 
    public String OutPutWord_New=""; //如果这个非空，则上面的就不需要了，覆盖 OutPutWord

    /**
     * 激活的单词，这个单词如果有，就把当前 C_Word_Convert 复制到 Session 中，如果没有，就不激活这个 C_Word_Convert
     */
    public String Active_Word="";
    
    public static int ID_Count = 0;//ID 增量
    public int ID=0;
    
     
    public C_Word_Convert(String strLeft, String strRight, String strActive) {
        C_Word_Convert.ID_Count++;
        this.ID = C_Word_Convert.ID_Count;
        this.Word = strLeft;
        this.OutPutWord = strRight;
        this.Active_Word = strActive;
    }
} 