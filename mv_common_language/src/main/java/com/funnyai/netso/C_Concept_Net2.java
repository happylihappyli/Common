/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.netso;

import com.funnyai.data.C_K_Str;
import com.funnyai.data.Treap;
import java.util.ArrayList;

/**
 * 这个和 C_Concept_Net 区别是没有Type，适合NetSO用
 * @author happyli
 */
public class C_Concept_Net2 {
    public Treap<C_Concept2> pTreap = null;
    
    public C_Concept_Net2(){
        this.Clear();
    }
    
    public final void Clear(){
        pTreap = new Treap();
    }

    /**
     * 为转成spark方便，这个方法只能私有
     * @param strChild
     * @param strParent
     * @param dbScale 
     */
    private void addConcept(String strChild, String strParent, double dbScale) {
        C_Concept2 pChild = pTreap.find(new C_K_Str(strChild));
        if (pChild == null) {
            pChild = new C_Concept2();
            pChild.Name = strChild;
            if (strChild.toLowerCase().startsWith("topic.")) {
                pChild.Type = "Topic";
            }
            pTreap.insert(new C_K_Str(strChild), pChild);
        }
        C_Concept2 pParent = pTreap.find(new C_K_Str(strParent));
        if (pParent == null) {
            pParent = new C_Concept2();
            pParent.Name = strParent;
            int pos = strParent.indexOf(":");
            if (pos > -1) {
                pParent.Type = pParent.Name.substring(0, pos);
            }
            if (strParent.toLowerCase().startsWith("topic.")) {
                pParent.Type = "Topic";
            }
            pTreap.insert(new C_K_Str(strParent), pParent);
        }
        if ("Topic.1".equals(strParent)){
            System.out.println("Topic.1.child="+strChild);
        }
        pParent.Add_Concept(pChild, dbScale);
    }
    
    /**
     * 批量添加概念
     * @param pListAdd 
     */
    public void addConcept_Bat(ArrayList<C_Topic_Key_W> pListAdd) {
        for (int k=0;k<pListAdd.size();k++){
            C_Topic_Key_W pTK=pListAdd.get(k);
            if (pTK!=null){
                addConcept(pTK.Key,pTK.Topic,pTK.dbWeight);
            }else{
                System.out.println("null");
            }
        }
        
    }
    
}
