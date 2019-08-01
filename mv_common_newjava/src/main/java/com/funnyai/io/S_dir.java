package com.funnyai.io;

import com.funnyai.string.S_string;
import java.io.File;
import static java.lang.System.out;

public class S_dir {

    public static void InitDir(String strDir) {
        File f = new File(strDir);
        f.mkdirs();
    }
    
    
    public static String get_sub_file(Object... a){
        String strDir=(String) a[0];
        
        if ("0".equals(S_dir.exists(strDir))){
            return "目录不存在";
        }
                

        File path = new File(strDir);
        File[] pFiles = path.listFiles();//取得所有文件信息
        StringBuilder pStr=new StringBuilder();
        for (File pFile : pFiles) {
            if (pFile.isDirectory()==false) {
                pStr.append(pFile.getName()).append("\n");
            } 
        }
        
        return S_string.cut_end_string(pStr.toString(),"\n");
    }
    
    
    /**
     * 读取子目录
     * @param a
     * @return 
     */
    public static String get_sub_dir(Object... a){
        String strDir=(String) a[0];
        
        if ("0".equals(S_dir.exists(strDir))){
            return "目录不存在";
        }
                

        File path = new File(strDir);
        File[] pFiles = path.listFiles();//取得所有文件信息
        StringBuilder pStr=new StringBuilder();
        for (File pFile : pFiles) {
            if (pFile.isDirectory()) {
                pStr.append(pFile.getName()).append("\n");
            } 
        }
        
        return S_string.cut_end_string(pStr.toString(),"\n");
    }
    
    
    public static String exists(Object... a){
        if (a.length>0){
            String strPath=(String) a[0];
            out.println("path="+strPath);
            File file =new File(strPath);
            //如果文件夹不存在
            if (file==null){
                return "0";
            }
            if  (!file.exists()  && !file.isDirectory())      
            {       
                return "0";
            }else{  
                return "1";
            }
        }else{
            return "0";
        }
    }

    public static void main(String[] args) {
        String strPath = null;

        if (args.length > 0) {
            strPath = args[0];
        } else {
            strPath = "D:/Happy_NewEgg/Java/lib";
        }
        File[] strFile;
        strFile = S_file.GetFiles(strPath, ".jar");

        C_File pFile=S_file_sub.main.Write_Begin(strPath + "/bat.txt", true,"");
        for (int i = 0; i < strFile.length; i++) {
            S_file_sub.main.Write_Line(pFile,
                    ";./../lib/" + strFile[i].getName());
        }
        
        S_file_sub.main.Write_End(pFile);
    }
}
