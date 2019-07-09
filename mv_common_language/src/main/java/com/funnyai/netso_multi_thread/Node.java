/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.netso_multi_thread;

import com.funnyai.data.Treap;

/**
 *
 * @author happyli
 */
public class Node {
    public Treap<Connect> pTreaps=new Treap<>();
    public String Name="";

    public Node(String Name) {
        this.Name=Name;
    }
}
