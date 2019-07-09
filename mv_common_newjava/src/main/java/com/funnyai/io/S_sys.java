/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.io;

/**
 *
 * @author happyli
 */
public class S_sys {
    public static String judge(Object... a){
        if (a.length>2){
            boolean b=(boolean) a[0];
            if (b){
                return (String) a[1];
            }else{
                return (String) a[2];
            }
        }
        return null;
    }
}
