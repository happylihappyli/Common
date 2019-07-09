/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.netso_multi_thread;

/**
 *
 * @author happyli
 */
public class Connect {

    public Node From;
    public Node To;
    public double weight=0;
    
    public Connect(Node From,Node To,double weight){
        this.From=From;
        this.To=To;
        this.weight=weight;
    }
}
