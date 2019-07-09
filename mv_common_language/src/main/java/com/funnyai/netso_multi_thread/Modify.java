/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.netso_multi_thread;

/**
 * 权重调整
 * @author happyli
 */
public class Modify {
    public String From="";
    public String To="";
    public double value=0;

    Modify(String From, String To, double value) {
        this.From=From;
        this.To=To;
        this.value=value;
    }
}
