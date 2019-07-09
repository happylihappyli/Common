package com.funnyai.netso;

import com.funnyai.data.C_K_Str;
import com.funnyai.data.Treap;
import com.funnyai.data.C_K_Int;

/**
 * NetSO用
 * @author happyli
 */
public class C_Concept2 {
    public static int ID_Count=0;
    
    public int ID=0;
    
    public C_Active2 pLast_Active=null;//最后一次激活的Active
    public Treap<C_Connect2> pInputs=new Treap();//pAsFrom As New Treap ’输出概念 （父集）
    public Treap<C_Connect2> pOutputs=new Treap();//pAsTo As New Treap '当前概念是指针指向的 输入概念 （子集）

    public String Name = "";
    public String Type = "";

    
    public C_Concept2() {
        C_Concept2.ID_Count++;
        ID = C_Concept2.ID_Count;
    }
    
    /**
     * 获取激活的概念
     * @param pTreapActive
     * @return 
     */
    public C_Active2 getActive(Treap pTreapActive) {
        C_Active2 pActiveNew=(C_Active2) pTreapActive.find(new C_K_Int(this.ID));

        if (pActiveNew == null) {
            pActiveNew = new C_Active2(this);
            this.pLast_Active=pActiveNew;//最后一次激活的概念
            pTreapActive.insert(new C_K_Int(this.ID), pActiveNew);
        }
        return pActiveNew;
    }
    
    public void Add_Concept(C_Concept2 pChild, double dbScale){

        pInputs.remove(new C_K_Str(pChild.Name));
        pChild.pOutputs.remove(new C_K_Str(this.Name));
        
        //添加两个
        C_Connect2 pConnect = new C_Connect2();
        pConnect.pFrom = pChild;
        pConnect.pTo = this;
        pConnect.Weight = dbScale;
        
        pInputs.insert(new C_K_Str(pChild.Name), pConnect);
        pChild.pOutputs.insert(new C_K_Str(this.Name), pConnect);
        
        
        if ("Topic.1".equals(this.Name)){
            System.out.println("Topic.1.child="+pChild.Name);
            System.out.println("Topic.1.input.size="+this.pInputs.Size());
        }
    }
    

}
