package com.funnyai.io.Old;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class S_File_Text {

    public static String Read_Last(String strFile,String strEncode,int MaxLine) {

        ArrayList pArray=new ArrayList();
        //从Txt文件中读取内容

        if ("".equals(strEncode)){
            strEncode="UTF-8";
        }
        
        boolean bFileExist = S_File.Exists(strFile);
        if (bFileExist == false) {
            return  "";  // strFile
        }
        
        InputStreamReader pFS = null;
        BufferedReader pSR = null;
        try {
            pFS = new InputStreamReader(new FileInputStream(strFile), strEncode);
            pSR = new BufferedReader(pFS);// 文件输入流为

            String strLine = pSR.readLine();
            while (strLine != null) {
                pArray.add(strLine);
                while (pArray.size()>MaxLine){
                    pArray.remove(0);
                }
                strLine = pSR.readLine();
            }
        } catch (IOException ex) {
        } finally{
            try {
                if (pSR!=null) pSR.close();
            } catch (IOException ex) {
                Logger.getLogger(S_File_Text.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                if (pFS!=null) pFS.close();
            } catch (IOException ex) {
                Logger.getLogger(S_File_Text.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        String strReturn = "";
        for (int i=0;i<pArray.size();i++){
                strReturn += "\r\n" + pArray.get(i);
        }
        return strReturn;
    }
    
    
    public static String Read(String strFile,String strEncode,int MaxLine) {

        //从Txt文件中读取内容
        String strReturn = "";

        if ("".equals(strEncode)){
            strEncode="UTF-8";
        }
        
        boolean bFileExist = S_File.Exists(strFile);
        if (bFileExist == false) {
            return  "";  // strFile
        }
        
        InputStreamReader pFS = null;
        BufferedReader pSR = null;
        try {
            pFS = new InputStreamReader(new FileInputStream(strFile), strEncode);
            pSR = new BufferedReader(pFS);// 文件输入流为

            int LineCount=1;
            String strLine = pSR.readLine();
            if (strLine!=null && strLine.length() > 1) {
                if ((int) strLine.charAt(0) == 65279) {
                    strLine = strLine.substring(1);
                }
            }
            strReturn += strLine;
            while (strLine != null) {
                if (LineCount>=MaxLine){
                    break;
                }
                strLine = pSR.readLine();
                LineCount+=1;
                if (strLine != null) {
                    strReturn += "\r\n" + strLine;
                }
            }
        } catch (IOException ex) {
        } finally{
            try {
                if (pSR!=null) pSR.close();
            } catch (IOException ex) {
                Logger.getLogger(S_File_Text.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                if (pFS!=null) pFS.close();
            } catch (IOException ex) {
                Logger.getLogger(S_File_Text.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return strReturn;
    }

    public static String Append(String strFile, String strContent)
    {
        if (strFile.equals("")) {
            return "";
        }
        String strReturn = "";
        FileOutputStream pFS=null ;
        Writer pSW=null;
        try {
            pFS = new FileOutputStream(new File(strFile), true);
            pSW = new OutputStreamWriter(pFS, "UTF-8");
            pSW.write(strContent + "\r\n");
        } catch (IOException ex) {
            //strReturn = ex.Message;
        }finally{
            try{
                if (pSW!=null) pSW.close();
            }catch (IOException ex) {
                
            }
            try{
                if (pFS!=null) pFS.close();
            }catch (IOException ex) {
                
            }
        }
        return strReturn;
    }
    
    public static String Write(String strFile, String strContent)
    {

        if (strFile.equals("")) {
            return "";
        }
        
        FileOutputStream pFS = null;
        Writer pSW = null;
        String strReturn = "";
        try {
            String strDir=new File(strFile).getParent();
            S_Dir.InitDir(strDir);
            pFS = new FileOutputStream(new File(strFile),false);
            pSW = new OutputStreamWriter(pFS, "UTF-8");

            pSW.write(strContent);
        } catch (IOException ex) {
            //strReturn = ex.Message;
        }finally{
            
            try {
                if (pSW!=null) pSW.close();
            } catch (IOException ex) {
                Logger.getLogger(S_File_Text.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                if (pFS!=null) pFS.close();
            } catch (IOException ex) {
                Logger.getLogger(S_File_Text.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return strReturn;
    }

}