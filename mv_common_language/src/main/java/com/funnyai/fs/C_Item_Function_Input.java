/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.fs;

import java.util.ArrayList;

/**
 *
 * @author happyli
 */
public class C_Item_Function_Input {
    private final ArrayList<String> pList=new ArrayList<>();
    private final ArrayList<Integer> pID_List=new ArrayList<>();
    
    public void add(String strLine_Input,Integer ID) {
        pList.add(strLine_Input);
        pID_List.add(ID);
    }

    public void set(int i, String strLine) {
        pList.set(i, strLine);
    }

    public int getID(int i) {
        return pID_List.get(i);
    }

    public int size() {
        return pList.size();
    }

    public String get(int i) {
        return pList.get(i);
    }
    
}
