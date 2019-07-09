package com.funnyai.io.Old;

import com.funnyai.io.C_File;
import com.funnyai.string.Old.S_Strings;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;

public class S_File {

    public static boolean Exists(String strFile) {

        return (new File(strFile)).exists();
    }

    public static void Delete(String strFile) {
        if (Exists(strFile)) {
            (new File(strFile)).delete();
        }
    }
    
    public static String Base64Encode(String strFile){
        StringBuilder pictureBuffer = new StringBuilder();
        InputStream input;
        try {
            input = new FileInputStream(new File(strFile));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] temp = new byte[1024];
            for(int len = input.read(temp); len != -1;len = input.read(temp)){
                out.write(temp, 0, len);
                pictureBuffer.append(new String(Base64.encodeBase64Chunked(out.toByteArray())));
                out.reset();
                out.close();
            }
            input.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(S_File.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(S_File.class.getName()).log(Level.SEVERE, null, ex);
        }

        return pictureBuffer.toString();
    }

    
    public static C_File Write_Begin(
                String strFile,
                boolean bAppend,
                String strEncode)
    {
        C_File pFile=new C_File();
        try {
            if (strFile.equals("")) {
                return null;
            }
            if ("".equals(strEncode)){
                strEncode="UTF-8";
            }
            pFile.pFS = new FileOutputStream(new File(strFile), bAppend);
            pFile.pSW = new OutputStreamWriter(pFile.pFS, strEncode);
            return pFile;
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return null;
        } finally{
//            try {
//                if (pFile.pFS!=null) pFile.pFS.close();
//            } catch (IOException ex) {
//                Logger.getLogger(S_File.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            try {
//                if (pFile.pSW!=null) pFile.pSW.close();
//            } catch (IOException ex) {
//                Logger.getLogger(S_File.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
    }

    public static void Write_Line(C_File pFile, String strLine)
    {
        try {
            pFile.pSW.write(strLine + "\n");
        } catch (Exception ex) {
            
        } finally{
//            try {
//                if (pFile.pSW!=null) pFile.pSW.close();
//            } catch (IOException ex) {
//                Logger.getLogger(S_File.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
    }

//    public static void Write_End(C_File pFile)
//    {
//        try {
//            if (pFile.pSW!=null) pFile.pSW.close();
//            if (pFile.pFS!=null) pFile.pFS.close();
//        } catch (Exception ex){
//            
//        }
//    }
    
    public static C_File Read_Begin2(String strFile) {
        return Read_Begin(strFile,"utf-8");//To change body of generated methods, choose Tools | Templates.
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
            Logger.getLogger(S_File.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(S_File.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(S_File.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    public static File[] GetFiles(String strDir, String filter) {

        File path = new File(strDir); //"AI/Data"
        File[] pFiles;
        if (filter.length() == 0) {
//            System.out.println("\nAll file:");
            pFiles = path.listFiles();//取得所有文件信息 
        } else {
//            System.out.println("\nAll file including " + filter);
            //取得满足查询条件的文件信息 
            pFiles = path.listFiles(new DirFilter(filter));
        }
        Arrays.sort(pFiles); 
        return pFiles;
    }
    
    public static ArrayList<File> GetDirs(String strDir, String filter) {

        File path = new File(strDir); //"AI/Data"
        File[] pFiles;
        if (filter.length() == 0) {
//            System.out.println("\nAll file:");
            pFiles = path.listFiles();//取得所有文件信息 
        } else {
//            System.out.println("\nAll file including " + filter);
            //取得满足查询条件的文件信息 
            pFiles = path.listFiles(new DirFilter(filter));
        }
        ArrayList<File> pList=new ArrayList<>();
        for(int j=0;j<pFiles.length;j++) 
        { 
            if(pFiles[j].isDirectory()){
                pList.add(pFiles[j]);
            } 
        }
        return pList;
    }

    public static String getFileNameWithoutExtension(File file) {
        String fileName = file.getName();
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot != -1) {
            fileName = fileName.substring(0, lastDot);
        }
        return fileName;
    }
    
    /**
     * 替换文本文件中的某一列
     * @param strFile
     * @param strFileOutput
     * @param strSep
     * @param index
     * @param strSepCombine
     * @param strReplace 
     * @param strFind 
     * @return  
     */
    public static String File_Replace_Column(String strFile,String strFileOutput,
            String strSep,int index,
            String strSepCombine,String strFind,String strReplace){
        //从Txt文件中读取内容
        strSep=strSep.replace("\\t","\t");
        strSepCombine=strSepCombine.replace("\\t","\t");
        strReplace=strReplace.replace("\\none","");
        //strSep=strSep.replace("\\t","\\s+");
        
        StringBuilder pStr=new StringBuilder();
        try {
            boolean bFileExist = S_File.Exists(strFile);
            if (bFileExist == true) {
                InputStreamReader pFS = new InputStreamReader(new FileInputStream(strFile), "UTF-8");
                BufferedReader pSR = new BufferedReader(pFS);// 文件输入流为

                String strLine = pSR.readLine();
                if (strLine.length() > 1) {
                    if ((int) strLine.charAt(0) == 65279) {
                        strLine = strLine.substring(1);
                    }
                }
                String strResult="";
                while (strLine != null) {
                    String[] strSplit=strLine.split(strSep);
                    if (index<strSplit.length){
                        strResult=strSplit[index].replaceAll(strFind, strReplace);
                    }
                    strLine = "";
                    for (int i=0;i<strSplit.length;i++){
                        if (i==0){
                            if (i==index){
                                strLine = strResult+strSepCombine;
                            }else{
                                strLine = strSplit[i]+strSepCombine;
                            }
                        }else{
                            if (i==index){
                                strLine += strResult+strSepCombine;
                            }else{
                                strLine += strSplit[i]+strSepCombine;
                            }
                        }
                    }
                    strLine=S_Strings.cut_end_string(strLine, strSepCombine);
                    pStr.append(strLine).append("\n");
                    strLine = pSR.readLine();
                }

                pSR.close();
                pFS.close();
                
                S_File_Text.Write(strFileOutput, pStr.toString());
                return "写完毕！";
            }else{
                return "文件不存在！";
            }
        } catch (Exception ex) {
            return "error:"+ex.toString();
        }
    }
    
    

    /**
     * 替换文本文件，可以替换属性
     * @param strFile
     * @param strStart 某个开头的
     * @param strReplace 
     */
    public static void File_Replace_With_Start(String strFile,String strStart,String strReplace){
        //从Txt文件中读取内容
        StringBuilder pStr=new StringBuilder();
        try {
            boolean bFileExist = S_File.Exists(strFile);
            if (bFileExist == true) {
                InputStreamReader pFS = new InputStreamReader(new FileInputStream(strFile), "UTF-8");
                BufferedReader pSR = new BufferedReader(pFS);// 文件输入流为

                String strLine = pSR.readLine();
                if (strLine.length() > 1) {
                    if ((int) strLine.charAt(0) == 65279) {
                        strLine = strLine.substring(1);
                    }
                }
                while (strLine != null) {
                    if (strLine.startsWith(strStart)){
                        pStr.append(strReplace).append("\n");
                    }else{
                        pStr.append(strLine).append("\n");
                    }
                    strLine = pSR.readLine();
                }

                pSR.close();
                pFS.close();
                
                S_File_Text.Write(strFile, pStr.toString());
            }
        } catch (Exception ex) {
        }
    }
    
    public static void Copy2File(String srcFile, String strFile2) {
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

    public static void Copy2Path(String srcFile, String destPath) {
        try {
            File file = new File(srcFile);
            File dir = new File(destPath);
            String tagFileName = (new File(dir, file.getName())).getAbsolutePath();
            FileInputStream inputStream = new FileInputStream(srcFile);
            FileOutputStream outputStream = new FileOutputStream(tagFileName);

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

    public static boolean move(String srcFile, String destPath) {
        // File (or directory) to be moved
        File file = new File(srcFile);

        // Destination directory
        File dir = new File(destPath);

        // Move file to new directory
        boolean success = file.renameTo(new File(dir, file.getName()));

        return success;
    }

}

class DirFilter implements FilenameFilter {

    String afn;//存放查询条件 

    DirFilter(String afn) {
        this.afn = afn;
    }
    //满足查询条件，返回true 

    public boolean accept(File dir, String name) {
        return name.toLowerCase().indexOf(afn.toLowerCase()) != -1;
    }
}
