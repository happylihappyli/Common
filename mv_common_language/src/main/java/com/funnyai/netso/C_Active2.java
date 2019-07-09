package com.funnyai.netso;

import com.funnyai.data.Treap;

/**
 * C_Active2 是激活的概念 NetSO用
 * @author happyli
 */
public class C_Active2 {
    
    public static int Active_Max=0;
    public int ID = 0;
//    public double db_Weight=0;//临时变量，激活当前概念的权重
    public double dbPower = 0;
    public double dbPower_Old = 0;
    public double dbAdd = 0;
    public C_Concept2 pConcept = null;
    public boolean bChecked = false;
    
    public Treap<C_Active2_From> pTreap_Active_From = new Treap<>();//激活当前这个节点的有几个词汇

    
    public C_Active2(C_Concept2 pConceptIn) {
        pConcept = pConceptIn;
        Active_Max++;
        ID = Active_Max;
    }
    
    
}
