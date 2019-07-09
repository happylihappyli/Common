package com.funnyai.io.Old;

import com.funnyai.io.C_File;
import java.io.File;

public class S_Dir {

    public static void InitDir(String strDir) {
        File f = new File(strDir);
        f.mkdirs();
    }

    public static void main(String[] args) {
        String strPath = null;

        if (args.length > 0) {
            strPath = args[0];
        } else {
            strPath = "D:/Happy_NewEgg/Java/lib";
        }
        //"D:\\2008_7_28\\2008_7_28.log";
        File[] strFile;
        strFile = S_File.GetFiles(strPath, ".jar");

        C_File pFile=S_File.Write_Begin(strPath + "/bat.txt", true,"");
        for (int i = 0; i < strFile.length; i++) {
            S_File.Write_Line(pFile,
                    ";./../lib/" + strFile[i].getName());
        }
        
        pFile.Close();
    }
}
