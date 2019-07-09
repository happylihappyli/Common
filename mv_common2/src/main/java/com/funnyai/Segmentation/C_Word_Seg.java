/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.Segmentation;

/**
 *
 * @author Administrator
 */
public class C_Word_Seg {
    
    public static int ID_Count = 0;
    
    public int ID = 0;
    
    //激活的单词，这个单词如果有，就把当前 C_Word_Convert 复制到 Session 中，如果没有，就不激活这个 C_Word_Convert
    public String Active_Word;
    
    // 单词
    public String Word;
    
    public String OutPutWord;
    
    public C_Word_Seg(String strLeft, String strRight, String strActive) {
        C_Word_Convert.ID_Count++;
        this.ID = C_Word_Convert.ID_Count;
        this.Word = strLeft;
        this.OutPutWord = strRight;
        this.Active_Word = strActive;
    }
}
