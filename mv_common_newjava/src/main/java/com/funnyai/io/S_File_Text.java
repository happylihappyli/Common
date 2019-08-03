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
import java.io.Writer;
import static java.lang.System.out;

public class S_File_Text {

    public static String Read(String strFile,String strEncode,int MaxLine) {

        //从Txt文件中读取内容
        String strReturn = "";

        if ("".equals(strEncode)){
            strEncode="UTF-8";
        }
        
        InputStreamReader pFS = null;
        BufferedReader pSR = null;// 文件输入流为
        try {
            out.println(strFile);
            boolean bFileExist = S_file.main.Exists(strFile);
            if (bFileExist == true) {
                pFS = new InputStreamReader(new FileInputStream(strFile), strEncode);
                pSR = new BufferedReader(pFS);// 文件输入流为

                int LineCount=1;
                String strLine = pSR.readLine();
                if (strLine.length() > 1) {
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

            } else {
                strReturn = "";  // strFile
            }
        } catch (IOException ex) {
        }finally{
            try {
                if (pSR!=null) pSR.close();
            } catch (IOException ex) {
            }
            try {
                if (pFS!=null) pFS.close();
            } catch (IOException ex) {
            }
        }

        return strReturn;
    }

    public static String Append(String strFile, String strContent) // ERROR: Unsupported modifier : Ref, Optional System.Text.Encoding pEncoding)
    {

        String strReturn = "";
        try {
            if (strFile.equals("")) {
                return "";
            }
            FileOutputStream pFS = new FileOutputStream(new File(strFile), true);
            Writer pSW = new OutputStreamWriter(pFS, "UTF-8");

            pSW.write(strContent + "\r\n");
            pSW.close();
            pFS.close();
            pSW = null;
            pFS = null;
        } catch (Exception ex) {
            //strReturn = ex.Message;
        }
        return strReturn;
    }
    
    public static String Write(String strFile, String strContent,String strEncode)
    {

        String strReturn = "写文件:"+strFile;
        try {
            if (strFile.equals("")) {
                return "";
            }
            FileOutputStream pFS = new FileOutputStream(new File(strFile),false);
            Writer pSW = new OutputStreamWriter(pFS,strEncode);

            pSW.write(strContent);
            pSW.close();
            pFS.close();
        } catch (Exception ex) {
            //strReturn = ex.Message;
        }
        return strReturn;
    }

    
}