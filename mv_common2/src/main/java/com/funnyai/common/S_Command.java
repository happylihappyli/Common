/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.common;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author happyli
 */
public class S_Command {
    
    public static String RunShell_Return(String shStr,int MaxTime) {
        try {
            StringBuilder pSB=new StringBuilder();
            //sh
            Process process_static = Runtime.getRuntime().exec(new String[]{"/bin/bash","-c",shStr},null,null);
            InputStreamReader ir = new InputStreamReader(process_static
                    .getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line="";
            process_static.waitFor();
            boolean bAdd_Line=false;
            
            long t1 = System.currentTimeMillis();
            while ((line = input.readLine()) != null){
                long t2 = System.currentTimeMillis();
                if(t2-t1 > MaxTime*1000){
                    break;
                }
                if (bAdd_Line) pSB.append("\n");
                pSB.append(line);
                bAdd_Line=true;
            }
            
            return pSB.toString();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(S_Command.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    
    
    /**
     * Shell执行命令
     * @param shStr
     * @return
     * @throws Exception 
     */
    public static List RunShell_Return2(String shStr) throws Exception {
        List<String> strList = new ArrayList();
        //sh
        Process process_static = Runtime.getRuntime().exec(new String[]{"/bin/bash","-c",shStr},null,null);
        InputStreamReader ir = new InputStreamReader(process_static.getInputStream());
        LineNumberReader input = new LineNumberReader(ir);
        String line;
        process_static.waitFor();
        while ((line = input.readLine()) != null){
            strList.add(line+"\n");
        }
        strList.add("\n===output.error===\n");
        ir = new InputStreamReader(process_static.getErrorStream());
        input = new LineNumberReader(ir);
        while ((line = input.readLine()) != null){
            strList.add(line+"\n");
        }
        
        return strList;
    }
}
