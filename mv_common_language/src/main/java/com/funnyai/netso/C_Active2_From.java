/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.netso;


/**
 *
 * @author happyli
 */
public class C_Active2_From {
    
    public static int Active_Max=0;
    public int ID = 0;
//    public double db_Weight=0;//临时变量，激活当前概念的权重
    public C_Concept2 pConcept = null;
    public C_Connect2 pConnect = null;

    
    public C_Active2_From(C_Concept2 pConcept,C_Connect2 pConnect) {
        this.pConcept = pConcept;
        this.pConnect = pConnect;
        
        Active_Max++;
        ID = Active_Max;
    }
}
