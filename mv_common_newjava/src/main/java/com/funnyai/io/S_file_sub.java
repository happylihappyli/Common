/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author happyli
 */
public class S_file_sub {
    
    public static C_File Read_Begin_UTF8(String strFile) {
        return S_file_sub.Read_Begin(strFile,"utf-8");//To change body of generated methods, choose Tools | Templates.
    }
    
    public static C_File Read_Begin(String strFile,String strEncode){
        C_File pFile=new C_File();
        pFile.bRead_First=false;
        try {
            if ("".equals(strEncode)){
                strEncode="UTF-8";
            }
            pFile.pInput = new InputStreamReader(new FileInputStream(strFile),strEncode);
            pFile.pReader = new BufferedReader(pFile.pInput);// 文件输入流为
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            Logger.getLogger(S_file_sub.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pFile;
    }
    
    private static String read_first_line(C_File pFile){
        try {
            pFile.bRead_First=true;
            String strLine = pFile.pReader.readLine();
            if (strLine!=null && strLine.length() > 1) {
                if ((int) strLine.charAt(0) == 65279) {
                    strLine = strLine.substring(1);
                }
            }
            return strLine;
        } catch (IOException ex) {
            Logger.getLogger(S_file_sub.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static String read_line(C_File pFile){
        if (pFile.bRead_First==false){
            return read_first_line(pFile);
        }
        try {
            String strLine = pFile.pReader.readLine();
            return strLine;
        } catch (IOException ex) {
            Logger.getLogger(S_file_sub.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    public static C_File Write_Begin(
                String strFile,
                boolean bAppend,
                String strEncode)
    {
        try {
            if (strFile.equals("")) {
                return null;
            }
            if ("".equals(strEncode)){
                strEncode="UTF-8";
            }
            C_File pFile=new C_File();
            pFile.pFS = new FileOutputStream(new File(strFile), bAppend);
            pFile.pSW = new OutputStreamWriter(pFile.pFS, strEncode);
            return pFile;
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            return null;
        }
    }

    public static void Write_Line(C_File pFile, String strLine)
    {
        try {
            pFile.pSW.write(strLine + "\n");
        } catch (Exception ex) {
            
        }
    }

    public static void Write_End(C_File pFile)
    {
        try {
            pFile.pSW.close();
            pFile.pFS.close();
        } catch (Exception ex){
            
        }
    }
}
