/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.Segmentation;

import com.funnyai.data.Treap;
import com.funnyai.data.C_K_Str;

/**
 *
 * @author happyli
 */
public class Struct_Treap {
    public Treap<C_Seg_Struct> pTreapKeys=new Treap<>();
    public Treap<Treap> pTreapTreap=new Treap<>();

    public int Size() {
        return pTreapKeys.Size();
    }

    public void insert(C_K_Str c_k_Str, C_Seg_Struct pStruct) {
        pTreapKeys.insert(c_k_Str, pStruct);
    }

    public void insert(C_K_Str c_k_Str, Treap pSubTreap) {
        pTreapTreap.insert(c_k_Str, pSubTreap);
    }

    public C_Seg_Struct find(C_K_Str c_k_Str) {
        return pTreapKeys.find(c_k_Str);
    }

    public Treap find_treap(C_K_Str c_k_Str) {
        return pTreapTreap.find(c_k_Str);
    }
}
