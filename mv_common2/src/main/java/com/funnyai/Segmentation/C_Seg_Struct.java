/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.Segmentation;

import com.funnyai.NLP.C_Token;

/**
 *
 * @author happyli
 */
public class C_Seg_Struct {
    public int iMax = 2;
    
    public String strLine = "";
    
    
    public C_Token[] pFrom=new C_Token[4];
    public int iNext;
    
    public int Size;
    public int Final=0;//是否可以继续分解
    
}
