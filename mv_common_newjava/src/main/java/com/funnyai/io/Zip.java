package com.funnyai.io;

import java.util.*;

import java.util.zip.*;

import java.io.*;


public class Zip {
    static final int BUFFER = 2048;

    public static void ZipFile(
    		String strFileOut,
    		String strFileInput) {
        try {
        	//"E:\\test\\myfiles.zip";
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(strFileOut);
            ZipOutputStream out = new ZipOutputStream(
            		new BufferedOutputStream(dest));
            byte data[] = new byte[BUFFER];

            File pFile=new File(strFileInput);
            FileInputStream fi = new FileInputStream(pFile);
            origin = new BufferedInputStream(fi, BUFFER);
            ZipEntry entry = new ZipEntry(pFile.getName());
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();
            
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void ZipDir(String strFileOut) {
        try {
        	//"E:\\test\\myfiles.zip";
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(strFileOut);
            ZipOutputStream out = new ZipOutputStream(
            		new BufferedOutputStream(dest));
            byte data[] = new byte[BUFFER];
            File f = new File("e:\\test\\a\\");
            File files[] = f.listFiles();

            for (int i = 0; i < files.length; i++) {
                FileInputStream fi = new FileInputStream(files[i]);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(files[i].getName());
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
