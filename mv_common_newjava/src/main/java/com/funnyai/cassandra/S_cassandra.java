/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.cassandra;

import com.datastax.driver.core.ResultSet;

/**
 *
 * @author happyli
 */
public class S_cassandra {
    public static Cassandra_Tools pTools=new Cassandra_Tools();
    
    public static void connect(Object... a){
        String nodes=(String) a[0];
        int iPort=(int) a[1];
        String username=(String) a[2];
        String password=(String) a[3];
        pTools.connect(nodes, iPort, username, password);
    }
    
    public static ResultSet executeSQL(Object... a){
        String strSQL=(String) a[0];
        ResultSet b=pTools.session.execute(strSQL);
        
        return b;
    }
}
