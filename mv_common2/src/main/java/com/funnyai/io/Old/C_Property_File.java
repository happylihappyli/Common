/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.io.Old;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hbli
 */
public class C_Property_File {
    
    Properties p = new Properties();

    public C_Property_File(String strFile) throws FileNotFoundException {

        File file = new File(strFile);
        InputStream inputStream = new FileInputStream(file);         
        
        InputStreamReader FS;
        try {
            FS = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader SR = new BufferedReader(FS);
            try {   
                p.load(SR);   
            } catch (IOException e1) {   
                e1.printStackTrace();   
            }   
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(C_Property_File.class.getName()).log(Level.SEVERE, null, ex);
        }
                    
                    
        //System.out.println("ip:"+p.getProperty("ip")+",port:"+p.getProperty("port")); 
    }
    
    public String Read(String strField){
        return p.getProperty(strField);
    }
}
