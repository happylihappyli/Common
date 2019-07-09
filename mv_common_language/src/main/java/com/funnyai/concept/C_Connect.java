/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.concept;

import com.funnyai.netso.Connect_Type;

/**
 *
 * @author Administrator
 */
public class C_Connect {
    public static int ID_Count=0;
    
    public String Name="";
    public double dbScale;
    public C_Concept pFrom;
    public C_Concept pTo;
    Connect_Type.Connect_Value Type;
    int ID=0;
    
    public C_Connect(){
        C_Connect.ID_Count += 1;
        ID = C_Connect.ID_Count;
   }
}
