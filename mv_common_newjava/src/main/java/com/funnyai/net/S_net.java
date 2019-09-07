package com.funnyai.net;

import com.funnyai.data.C_Var_Java;
import com.funnyai.string.S_string;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

public class S_net {
    
    
    public static S_net main=new S_net();
    
    public  String getLocalIP(){
        String ip="";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(S_net.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ip;
    }
    
    
    /**
        * 连接sftp服务器
        * @param host 主机
        * @param port 端口
        * @param username 用户名
        * @param password 密码
        * @param uploadFile 要上传的文件
        * @param directory 上传的目录
     * @return 
    */
    public  String SFtp_Upload(String host, 
            int port, String username,String password,
            String uploadFile,String directory) {
        try {
            ChannelSftp sftp;
            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            Session sshSession = jsch.getSession(username, host, port);
            System.out.println("Session created.");
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            out.println("Session connected.");
            out.println("Opening Channel.");
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            out.println("Connected to " + host + ".");
                 
            sftp.cd(directory);
            File file=new File(uploadFile);
            out.println("upload...");
            
            SystemOutProgressMonitor progress = new SystemOutProgressMonitor();
            sftp.put(new FileInputStream(file), file.getName(),progress);
            
            out.println("upload finished");
            sftp.disconnect();
            return "OK";
        } catch (JSchException | SftpException | FileNotFoundException e) {
            return e.toString();
        }
    }

    /* 
     * 从SFTP服务器下载文件 
     *  
     * @param ftpHost SFTP IP地址 
     *  
     * @param ftpUserName SFTP 用户名 
     *  
     * @param ftpPassword SFTP用户名密码 
     *  
     * @param ftpPort SFTP端口 
     *  
     * @param ftpPath SFTP服务器中文件所在路径 格式： ftptest/aa 
     *  
     * @param localPath 下载到本地的位置 格式：H:/download 
     *  
     * @param fileName 文件名称 
     */  
    public  void SFtp_Download(
            String ftpHost,int ftpPort, 
            String ftpUserName,  
            String ftpPassword, 
            String ftpFilePath, 
            String localPath) throws JSchException {  
            Session session = null;  
            Channel channel = null;  

        JSch jsch = new JSch();  
        session = jsch.getSession(ftpUserName, ftpHost, ftpPort);  
        session.setPassword(ftpPassword);  
        session.setTimeout(100000);  
        Properties config = new Properties();  
        config.put("StrictHostKeyChecking", "no");  
        session.setConfig(config);  
        session.connect();  

        channel = session.openChannel("sftp");  
        channel.connect();  
        ChannelSftp chSftp = (ChannelSftp) channel;  

        try {  
            chSftp.get(ftpFilePath, localPath);  
        } catch (Exception e) {  
            e.printStackTrace();
        } finally {
            chSftp.quit();  
            channel.disconnect();  
            session.disconnect();  
        }  

    }  


    public  String sftp_upload(String... a){
        String strSite=a[0];
        int iPort=22;
        if (a.length>1){
            iPort=S_string.getIntFromStr(a[1],0);
        }
        String strUser="root";
        if (a.length>2){
            strUser=a[2];
        }
        String strPassword="123";
        if (a.length>3){
            strPassword=a[3];
        }
        String strFile="file";
        if (a.length>4){
            strFile=a[4];
        }
        String strPath="path";
        if (a.length>5){
            strPath=a[5];
        }
        
        String strReturn=S_net.main.SFtp_Upload(strSite,iPort,strUser,strPassword,strFile,strPath);

        return strReturn;
    }
    /**
     * sftp 上传
     * @param a
     * @return 
     */
    public  C_Var_Java sftp_upload(C_Var_Java... a){
        String strSite=(String) a[0].pObj;
        int iPort=22;
        if (a.length>1){
            iPort=S_string.getIntFromStr((String) a[1].pObj,0);
        }
        String strUser="root";
        if (a.length>2){
            strUser=(String) a[2].pObj;
        }
        String strPassword="123";
        if (a.length>3){
            strPassword=(String) a[3].pObj;
        }
        String strFile="file";
        if (a.length>4){
            strFile=(String) a[4].pObj;
        }
        String strPath="path";
        if (a.length>5){
            strPath=(String) a[5].pObj;
        }
        
        String strReturn=S_net.main.SFtp_Upload(strSite,iPort,strUser,strPassword,strFile,strPath);
        //S_net.SFtp_Upload("www.funnyai.com",22,"root","",(String)a[0],"/root/");
        return new C_Var_Java("String",strReturn);
    }
    
    public  String http_get(String url) {
       
        String strMethod="GET";
        String param="";
        String strEncode="utf-8";
        String reference_url="";
        
        String result = "";
        try {
            URL httpurl = new URL(url);

            HttpURLConnection httpConn= (HttpURLConnection) httpurl.openConnection();
            httpConn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.1) Gecko/2008070208 Firefox/3.0.1");
            httpConn.setRequestProperty("Accept-Charset", "utf-8;q=0.7,*;q=0.7");
            httpConn.setRequestProperty("Referer", reference_url);
            
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream(), strEncode));
            String line;
            while ((line = in.readLine()) != null) {
                result += line + "\r\n";
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            result = e.toString();
        }
        return result;
    }
    
    
    public  String http_post(String url,String strData) {
        String strEncode="utf-8";
        String reference_url="";
        String result = "";
        try {
            URL httpurl = new URL(url);

            HttpURLConnection httpConn= (HttpURLConnection) httpurl.openConnection();
            httpConn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.1) Gecko/2008070208 Firefox/3.0.1");
            httpConn.setRequestProperty("Accept-Charset", "utf-8;q=0.7,*;q=0.7");
            httpConn.setRequestProperty("Referer", reference_url);

            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            PrintWriter out = new PrintWriter(httpConn.getOutputStream());
            out.print(strData);
            out.flush();
            out.close();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream(), strEncode));
            String line;
            while ((line = in.readLine()) != null) {
                result += line + "\r\n";
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            result = e.toString();
        }
        return result;
    }
    
    public  C_Var_Java http(C_Var_Java... a) {
        String url="";
        String strMethod="GET";
        String param="";
        String strEncode="utf-8";
        String reference_url="";
        if (a.length>0){
            url=(String) a[0].pObj;
            out.println("url="+url);
            if (a.length>1){
                strEncode=(String) a[1].pObj;
                if ("".equals(strEncode)){
                    strEncode="utf-8";
                }
                if (a.length>2){
                    strMethod=(String) a[2].pObj;
                    if (a.length>3){
                        param=(String) a[3].pObj;
                        out.println(param);
                        if (a.length>4){
                        reference_url=(String) a[4].pObj;
                        }
                    }
                }
            }
        }
        String result = "";
        try {
            URL httpurl = new URL(url);

            HttpURLConnection httpConn= (HttpURLConnection) httpurl.openConnection();
            httpConn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.1) Gecko/2008070208 Firefox/3.0.1");
            httpConn.setRequestProperty("Accept-Charset", "utf-8;q=0.7,*;q=0.7");
            httpConn.setRequestProperty("Referer", reference_url);
            
            if (strMethod.equals("POST")) {
                httpConn.setDoOutput(true);
                httpConn.setDoInput(true);
                PrintWriter out = new PrintWriter(httpConn.getOutputStream());
                out.print(param);
                out.flush();
                out.close();
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream(), strEncode));
            String line;
            while ((line = in.readLine()) != null) {
                result += line + "\r\n";
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            result = e.toString();
        }
        return new C_Var_Java("String",result);
    }

    public  void main(String[] args) {
        System.out.println(ip2Long("116.232.105.203"));
        System.out.println(ip2Long("218.88.145.213"));

    }

    public  long ip2Long(String strIP) {
        if ("".equals(strIP)){
            return 0;
        }
        long[] ip = new long[4];
        //先找到IP地址字符串中.的位置
        int position1 = strIP.indexOf(".");
        int position2 = strIP.indexOf(".", position1 + 1);
        int position3 = strIP.indexOf(".", position2 + 1);
        //将每个.之间的字符串转换成整型
        ip[0] = Long.parseLong(strIP.substring(0, position1));
        ip[1] = Long.parseLong(strIP.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(strIP.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(strIP.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

    //将10进制整数形式转换成127.0.0.1形式的IP地址
    public  String long2IP(long longIP) {
        StringBuffer sb = new StringBuffer("");
        //直接右移24位
        sb.append(String.valueOf(longIP >>> 24));
        sb.append(".");
        //将高8位置0，然后右移16位
        sb.append(String.valueOf((longIP & 0x00FFFFFF) >>> 16));
        sb.append(".");
        sb.append(String.valueOf((longIP & 0x0000FFFF) >>> 8));
        sb.append(".");
        sb.append(String.valueOf(longIP & 0x000000FF));
        return sb.toString();
    }
    
}
