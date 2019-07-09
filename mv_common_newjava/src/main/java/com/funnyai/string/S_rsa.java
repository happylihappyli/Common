/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.string;

import ch.ethz.ssh2.crypto.Base64;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.BASE64Decoder;

/**
 *
 * @author happyli
 */
public class S_rsa {
    
    
    /** 
     * 从文件中加载私钥 
     * @param in
     * @return 是否成功 
     * @throws Exception  
     */  
    public static RSAPrivateKey loadPrivateKey(InputStream in) throws Exception{  
        try {  
            BufferedReader br= new BufferedReader(new InputStreamReader(in));  
            String readLine= null;  
            StringBuilder sb= new StringBuilder();  
            while((readLine= br.readLine())!=null){  
                if(readLine.charAt(0)=='-'){  
                    continue;  
                }else{  
                    sb.append(readLine);  
//                    sb.append('\r');  
                }  
            }  
            out.println(sb.toString());
            return loadPrivateKey(sb.toString());  
        } catch (IOException e) {  
            throw new Exception("私钥数据读取错误");  
        } catch (NullPointerException e) {  
            throw new Exception("私钥输入流为空");  
        }  
    }  
  
    public static RSAPrivateKey loadPrivateKey(String privateKeyStr) throws Exception{  
        try {  
            BASE64Decoder base64Decoder= new BASE64Decoder();  
            byte[] buffer= base64Decoder.decodeBuffer(privateKeyStr);  
            PKCS8EncodedKeySpec keySpec= new PKCS8EncodedKeySpec(buffer);  
            KeyFactory keyFactory= KeyFactory.getInstance("RSA");  
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此算法");  
        } catch (InvalidKeySpecException e) {  
            throw new Exception("私钥非法");  
        } catch (IOException e) {  
            throw new Exception("私钥数据内容读取错误");  
        } catch (NullPointerException e) {  
            throw new Exception("私钥数据为空");  
        }  
        
    }
    
    
    /** 
     * 从文件中输入流中加载公钥 
     * @param in 公钥输入流 
     * @throws Exception 加载公钥时产生的异常 
     */
    public static PublicKey loadPublicKey(InputStream in) throws Exception{  
        try {  
            BufferedReader br= new BufferedReader(new InputStreamReader(in));  
            String readLine= null;  
            StringBuilder sb= new StringBuilder();  
            while((readLine= br.readLine())!=null){  
                if(readLine.charAt(0)=='-'){  
                    continue;  
                }else{  
                    sb.append(readLine);  
                    sb.append('\r');  
                }  
            }  
            return loadPublicKey(sb.toString());  
        } catch (IOException e) {  
            throw new Exception("公钥数据流读取错误");  
        } catch (NullPointerException e) {  
            throw new Exception("公钥输入流为空");  
        }  
    }  
    
    /** 
     * 从字符串中加载公钥 
     * @param publicKeyStr 公钥数据字符串 
     * @throws Exception 加载公钥时产生的异常 
     */  
    public static PublicKey loadPublicKey(String publicKeyStr) throws Exception{  
        try {  
            BASE64Decoder base64Decoder= new BASE64Decoder();  
            byte[] buffer= base64Decoder.decodeBuffer(publicKeyStr);  
            KeyFactory keyFactory= KeyFactory.getInstance("RSA");  
            X509EncodedKeySpec keySpec= new X509EncodedKeySpec(buffer);  
            return keyFactory.generatePublic(keySpec);  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此算法");  
        } catch (InvalidKeySpecException e) {  
            throw new Exception("公钥非法");  
        } catch (IOException e) {  
            throw new Exception("公钥数据内容读取错误");  
        } catch (NullPointerException e) {  
            throw new Exception("公钥数据为空");  
        }  
    }  
  
  

    /**
     * 保存PublicKey
     * @param rsaPublicKey
     * @param fileName 
     */
    public static void Save_PublicKey(RSAPublicKey rsaPublicKey,String fileName){
        PrintWriter pw1;
        try {
            pw1 = new PrintWriter(new FileOutputStream(fileName));
            pw1.print(new String(Base64.encode(rsaPublicKey.getEncoded())));
            pw1.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(S_rsa.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 保存PrivateKey
     * @param rsaPrivateKey
     * @param fileName 
     */
    public static void Save_PrivateKey(RSAPrivateKey rsaPrivateKey,String fileName){
        PrintWriter pw1;
        try {
            pw1 = new PrintWriter(new FileOutputStream(fileName));
            pw1.print(new String(Base64.encode(rsaPrivateKey.getEncoded())));
            pw1.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(S_rsa.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
