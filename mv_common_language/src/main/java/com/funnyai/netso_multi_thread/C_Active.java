/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.netso_multi_thread;

import com.funnyai.data.Treap;
import com.funnyai.netso.C_Active_From_v2;

/**
 * 激活的输出
 *
 * @author happyli
 */
public class C_Active {
    public double value = 0;
    public Node pNode = null;
    public double error;
    public String Reason = "";
    Treap<C_Active_From_v2> pTreap_Active_From=new Treap<>();

    public C_Active(Node pNode, double value) {
        this.pNode = pNode;
        this.value = value;
    }
}
