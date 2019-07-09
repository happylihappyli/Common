/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.concept;

import com.funnyai.data.C_K_Str;
import com.funnyai.data.Treap;
import com.funnyai.netso.Connect_Type;

/**
 *
 * @author happyli
 */
public class C_Concept_Net {
    public Treap pTreap = null;
    
    public C_Concept_Net(){
        pTreap = new Treap();
    }

    public void addConcept(String strChild, String strParent, double dbScale, Connect_Type.Connect_Value Type) {
        C_Concept pChild;
        C_Concept pParent;
        pChild = (C_Concept) pTreap.find(new C_K_Str(strChild));
        if (pChild == null) {
            pChild = new C_Concept();
            pChild.Name = strChild;
            if (strChild.toLowerCase().startsWith("topic.")) {
                pChild.Type = "Topic";
            }
            pTreap.insert(new C_K_Str(strChild), pChild);
        }
        pParent = (C_Concept) pTreap.find(new C_K_Str(strParent));
        if (pParent == null) {
            pParent = new C_Concept();
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
        pParent.Add_Concept(pChild, dbScale, Type);
    }
    
}
