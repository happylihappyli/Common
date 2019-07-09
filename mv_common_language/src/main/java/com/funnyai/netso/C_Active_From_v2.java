/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.netso;

import com.funnyai.netso_multi_thread.Connect;
import com.funnyai.netso_multi_thread.Node;


/**
 *
 * @author happyli
 */
public class C_Active_From_v2 {
    
    public static int Active_Max=0;
    public int ID = 0;
//    public double db_Weight=0;//临时变量，激活当前概念的权重
    public Node pConcept = null;
    public Connect pConnect = null;

    
    public C_Active_From_v2(Node pNode,Connect pConnect) {
        this.pConcept = pNode;
        this.pConnect = pConnect;
        
        Active_Max++;
        ID = Active_Max;
    }
}
