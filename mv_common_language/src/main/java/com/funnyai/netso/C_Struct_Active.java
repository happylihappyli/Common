/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.netso;

import com.funnyai.NLP.C_Token_Key;

/**
 * 每个Key的激活程度
 * @author happyli
 */
public class C_Struct_Active {
    public String Name="";
    public double dbValue=0;
    public C_Token_Key pTokenKey=null;
    

    public C_Struct_Active(String strKey,double dbValue,C_Token_Key pTokenKey) {
        this.Name=strKey;
        this.dbValue=dbValue;
        this.pTokenKey=pTokenKey;
    }
    
    @Override
    public String toString(){
        return this.Name;
    }
}
