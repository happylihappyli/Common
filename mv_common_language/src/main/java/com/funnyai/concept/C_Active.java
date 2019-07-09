/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.concept;

/**
 * C_Active 是激活的概念
 * @author happyli
 */
public class C_Active {
    
    public static int Active_Max=0;
    public int ID = 0;
    public double dbPower = 0;
    public double dbPower_Old = 0;
    public double dbAdd = 0;
    public C_Concept pConcept = null;
    public boolean bChecked = false;

    
    public C_Active(C_Concept pConceptIn) {
        pConcept = pConceptIn;
        
        Active_Max++;
        ID = Active_Max;
    }
    
    
}
