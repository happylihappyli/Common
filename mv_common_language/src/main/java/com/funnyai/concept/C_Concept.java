/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.concept;

import com.funnyai.data.C_K_Str;
import com.funnyai.data.Treap;
import com.funnyai.data.C_K_Int;
import com.funnyai.netso.Connect_Type;

/**
 *
 * @author happyli
 */
public class C_Concept {
    public static int ID_Count=0;
    
    public int ID=0;
    
    public C_Active pLast_Active=null;//最后一次激活的Active
    public Treap pInputs=new Treap();//pAsFrom As New Treap ’输出概念 （父集）
    public Treap pOutputs=new Treap();//pAsTo As New Treap '当前概念是指针指向的 输入概念 （子集）

    public String Name = "";
    public String Type = "";

    
    public C_Concept() {
        C_Concept.ID_Count++;
        ID = C_Concept.ID_Count;
    }
    
    /**
     * 获取激活的概念
     * @param pTreapActive
     * @return 
     */
    public C_Active getActive(Treap pTreapActive) {
        C_Active pActiveNew=(C_Active) pTreapActive.find(new C_K_Int(this.ID));

        if (pActiveNew == null) {
            pActiveNew = new C_Active(this);
            this.pLast_Active=pActiveNew;//最后一次激活的概念
            pTreapActive.insert(new C_K_Int(this.ID), pActiveNew);
        }
        return pActiveNew;
    }
    
    public void Add_Concept(C_Concept pChild, double dbScale, Connect_Type.Connect_Value Type) {
        
        pInputs.remove(new C_K_Str(pChild.Name));
        pChild.pOutputs.remove(new C_K_Str(this.Name));
        
//        C_Connect pConnect2;
//        TreapEnumerator p = pInputs.Elements(true);
//        while (p.HasMoreElements()) {
//            pConnect2 = (C_Connect)(p.NextElement());
//            if (pConnect2.pFrom.ID == pConcept_Child.ID) {
//                pConnect2.dbScale = dbScale;
//                break;
//            }
//        }
//        p = pConcept_Child.pOutputs.Elements(true);
//        while (p.HasMoreElements()) {
//            pConnect2 = ((C_Connect)(p.NextElement()));
//            if (pConnect2.pTo.ID == this.ID) {
//                pConnect2.dbScale = dbScale;
//                break;
//            }
//        }
        //添加两个
        C_Connect pConnect = new C_Connect();
        pConnect.pFrom = pChild;
        pConnect.pTo = this;
        pConnect.Type = Type;
        pConnect.dbScale = dbScale;
        pInputs.insert(new C_K_Str(pChild.Name), pConnect);
        pChild.pOutputs.insert(new C_K_Str(this.Name), pConnect);
    }
    
//    public void Remove_Concept(C_Concept pConcept2, Connect_Type.Connect_Value iType) {
//        //  d*{+
//        TreapEnumerator p;
//        C_Connect pConnect;
//        p = pConcept2.pTreapOutput.Elements(true);
//        while (p.HasMoreElements()) {
//            pConnect = ((C_Connect)(p.NextElement()));
//            if (pConnect.Type == iType
//                    && pConnect.pTo.ID == this.ID){
//                pConnect.pTo.pTreapInput.remove(new C_K_Int(pConnect.ID));
//            }
//        }
//        p = this.pTreapInput.Elements(true);
//        while (p.HasMoreElements()) {
//            pConnect = ((C_Connect)(p.NextElement()));
//            if (((pConnect.Type == iType) 
//                        && (pConnect.pFrom.ID == pConcept2.ID))) {
//                pConnect.pFrom.pTreapOutput.remove(new C_K_Int(pConnect.ID));
//            }
//        }
//    }
    

}
