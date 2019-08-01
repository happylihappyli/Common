package com.funnyai.io;

import com.funnyai.data.C_Var_Java;
import com.funnyai.string.S_string;
import java.io.*;
import static java.lang.System.out;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;

public class S_file {

    public static boolean Exists(String strFile) {

        return (new File(strFile)).exists();
    }

    public static void Delete(String strFile) {
        if (Exists(strFile)) {
            (new File(strFile)).delete();
        }
    }

    /**
     * 文件重复
     * @param strFile
     * @param strRepeat_Max
     * @param strFile2 
     */
    public static void File_Repeat(
            String strFile,
            String strRepeat_Max,
            String strFile2){
        String strEncode="UTF-8";
        
        out.println(strFile);
        C_File pFile=S_file_sub.main.Write_Begin(strFile2, false, "utf-8");
        boolean bFileExist = S_file.Exists(strFile);
        if (bFileExist == false) {
            return ;
        }
        int Repeat_Max=Integer.parseInt(strRepeat_Max);
        for (int i=0;i<Repeat_Max;i++){
            C_File pFile2 = S_file_sub.main.Read_Begin(strFile, strEncode);
            
            String strLine = S_file_sub.main.read_line(pFile2);
            while (strLine != null) {
                S_file_sub.main.Write_Line(pFile, strLine);
                strLine =S_file_sub.main.read_line(pFile2);
            }
            pFile2.Close();
        }
        S_file_sub.main.Write_End(pFile);
    }
    
    
    
    public static String Read_Col(
            String strFile1,String strSep,String Col_Index){
        
        String strOutput="";
        //从Txt文件中读取内容
        String strEncode="UTF-8";
        
        out.println(strFile1);
        boolean bFileExist = S_file.Exists(strFile1);
        if (bFileExist == false) {
            return "文件不存在";
        }

        C_File pFile1=S_file_sub.main.Read_Begin(strFile1, strEncode);

        int Index =Integer.parseInt(Col_Index);
        String strLine = S_file_sub.main.read_line(pFile1);
        while (strLine != null) {
            String[] strSplit=strLine.split(strSep);
            strOutput+=strSplit[Index]+",";
            strLine = S_file_sub.main.read_line(pFile1);
        }
        pFile1.Close();

        if (strOutput.endsWith(",")) 
            strOutput=strOutput.substring(0, strOutput.length()-1);
        return strOutput;
    }
    
    
    /**
     * 文件合并
     * @param strFile1
     * @param strFile2
     * @param strFile_Out 
     */
    public static void File_Combine(
            String strFile1,String strFile2,String strFile_Out){
        
        //从Txt文件中读取内容
        String strEncode="UTF-8";
        
        out.println(strFile1);
        C_File pFile_Output=S_file_sub.main.Write_Begin(strFile_Out, false, "utf-8");
        boolean bFileExist = S_file.Exists(strFile1);
        if (bFileExist == false) {
            return ;
        }

        C_File pFile1=S_file_sub.main.Read_Begin(strFile1, strEncode);

        String strLine = S_file_sub.main.read_line(pFile1);
        while (strLine != null) {
            S_file_sub.main.Write_Line(pFile_Output, strLine);
            strLine = S_file_sub.main.read_line(pFile1);
        }
        pFile1.Close();


        pFile1=S_file_sub.main.Read_Begin(strFile2, strEncode);

        strLine = S_file_sub.main.read_line(pFile1);
        while (strLine != null) {
            S_file_sub.main.Write_Line(pFile_Output, strLine);
            strLine = S_file_sub.main.read_line(pFile1);
        }
        pFile1.Close();

        S_file_sub.main.Write_End(pFile_Output);
            
    }
    
    /**
     *
     * @param a
     * @return
     * @deprecated  use S_file_sub.read_begin
     */
    @Deprecated
    public static BufferedReader read_begin(Object... a){
        InputStreamReader pFS = null;
        BufferedReader pSR = null;
        try {
            String strFile=(String) a[0];
            pFS = new InputStreamReader(new FileInputStream(strFile), "UTF-8");
            pSR = new BufferedReader(pFS);// 文件输入流为
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return pSR;
    }
    
    /**
     *
     * @param a
     * @return
     * @deprecated use S_file_sub.read_line
     */
    @Deprecated
    public static String read_first_line(Object... a){
        try {
            if (a.length>0){
                BufferedReader pSR=(BufferedReader) a[0];
                String strLine = pSR.readLine();
                if (strLine.length() > 1) {
                    if ((int) strLine.charAt(0) == 65279) {
                        strLine = strLine.substring(1);
                    }
                }
                return strLine;
            }else{
                return null;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     *
     * @param a
     * @return
     * @deprecated use S_file_sub.read_line
     */
    @Deprecated
    public static String read_line(Object... a){
        try {
            BufferedReader pSR=(BufferedReader) a[0];
            String strLine = pSR.readLine();
            return strLine;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
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
            Logger.getLogger(S_string.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(S_string.class.getName()).log(Level.SEVERE, null, ex);
        }

        return pictureBuffer.toString();
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
            boolean bFileExist = S_file.Exists(strFile);
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
                    strLine=S_string.cut_end_string(strLine, strSepCombine);
                    pStr.append(strLine).append("\n");
                    strLine = pSR.readLine();
                }

                pSR.close();
                pFS.close();
                
                S_File_Text.Write(strFileOutput, pStr.toString(),"utf-8");
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
            boolean bFileExist = S_file.Exists(strFile);
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
                
                S_File_Text.Write(strFile, pStr.toString(),"utf-8");
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

    /**
     * 读取文件
     * @param a
     * @return 
     */
    public static String read(Object... a) {
        if (a.length>0){
            String strFile=(String) a[0];
            out.println(strFile);
            return S_File_Text.Read(strFile,"utf-8",10000);
        }else{
            return "{none}";
        }
    }

    public static String save(String... a) {  
        String strFile="";
        if (a.length>0){
            strFile=a[0];
        }
        if (a.length>1){
            String strContent=a[1];
            String strEncode="utf-8";
            if (a.length>2){
                strEncode=a[2];
            }
            S_File_Text.Write(strFile,strContent,strEncode);
        }
        return strFile;
    }
    
    public static C_Var_Java save(C_Var_Java... a) {
        String strReturn="";
        if (a.length>1){
            String strFile=(String) a[0].pObj;
            String strContent=(String) a[1].pObj;
            String strEncode="utf-8";
            if (a.length>2){
                strEncode=(String) a[2].pObj;
            }
            strReturn = S_File_Text.Write(strFile,strContent,strEncode);
        }else{
            strReturn = "{none}";
        }
        return new C_Var_Java("String",strReturn);
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
