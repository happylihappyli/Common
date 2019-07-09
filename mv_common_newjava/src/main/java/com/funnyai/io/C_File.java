package com.funnyai.io;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author happyli
 */
public class C_File {
    public InputStreamReader pInput=null;
    public BufferedReader pReader=null;
    
    /**
     * 是否读取了第一行
     */
    public boolean bRead_First=false;
    
    public FileOutputStream pFS = null;
    public Writer pSW = null;
    public String Tag="";//存储一些临时信息

    public void Close() {
        try {
            if (pReader!=null) pReader.close();
            if (pInput!=null) pInput.close();
            if (pSW!=null) pSW.close();
            if (pFS!=null) pFS.close();
        } catch (IOException ex) {
            Logger.getLogger(C_File.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
