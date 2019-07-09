/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.language;

import com.funnyai.io.C_File;
import com.funnyai.io.Old.S_File;
import com.funnyai.net.Old.S_Net;

/**
 *
 * @author happyli
 */
public class Thread_Download_Seg extends Thread {

    public String strPath = "";

    @Override
    public void run() {
        //AI_Var.MapPath("/Data/Segmentation/"
        DownLoad_Seg();
    }

    public void DownLoad_Seg() {
        String strURL = "https://www.funnyai.com/funnyai/json_list_ai_word.php";
        String strData;
        int p = 1;
        while (p > 0) {
            strData = ("p=" + p);
            try {
                String strReturn = S_Net.http_post(strURL,strData);//, "utf-8", "", false);
                int IndexStr = strReturn.indexOf("{");
                strReturn = strReturn.substring(IndexStr);
                C_File pFile = S_File.Write_Begin(strPath +p + ".txt", false, "utf-8");//AI_Var.MapPath("/Data/Segmentation/" + 
                S_File.Write_Line(pFile, strReturn);
                pFile.Close();
                System.out.println(p);
                if (strReturn.length() < 100) {
                    break; //Warning!!! Review that break works as 'Exit Do' as it could be in a nested instruction like switch
                }
            } catch (Exception ex) {
            }
            p++;
        }
    }
}
