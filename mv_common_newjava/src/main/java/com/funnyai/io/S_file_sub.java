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
    
    public void Delete(String strFile) {
        if (S_file.main.Exists(strFile)) {
            (new File(strFile)).delete();
        }
    }
    
    public void Copy2File(String srcFile, String strFile2) {
        try {
            File file = new File(srcFile);
            FileInputStream inputStream = new FileInputStream(srcFile);
            FileOutputStream outputStream = new FileOutputStream(strFile2);

            byte[] readBytes = new byte[1024];
            int readLength = inputStream.read(readBytes);
            while (readLength != -1)// 读取数据到文件输出流
            {
                outputStream.write(readBytes, 0, readLength);
                outputStream.flush();
                readLength = inputStream.read(readBytes);
            }
            // 关闭相关对象
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean Exists(String strFile) {

        return (new File(strFile)).exists();
    }
    
    public C_File Read_Begin(String strFile) {
        return Read_Begin(strFile,"utf-8");//To change body of generated methods, choose Tools | Templates.
    }
    
    public C_File Read_Begin(String strFile,String strEncode){
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
    
    private String read_first_line(C_File pFile){
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
    
    public String read_line(C_File pFile){
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
    
    
    public C_File Write_Begin(
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

    public void Write_Line(C_File pFile, String strLine)
    {
        try {
            pFile.pSW.write(strLine + "\n");
        } catch (Exception ex) {
            
        }
    }

    public void Write_End(C_File pFile)
    {
        try {
            pFile.pSW.close();
            pFile.pFS.close();
        } catch (Exception ex){
            
        }
    }
}
