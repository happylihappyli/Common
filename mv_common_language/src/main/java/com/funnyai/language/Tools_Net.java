/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.funnyai.language;

import com.funnyai.data.Treap;
import com.funnyai.net.Old.S_Net;
import com.funnyai.fs.C_Map_Item;
import com.funnyai.netso.C_Session_AI;
import org.dom4j.Node;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;


/**
 * @author happyli
 */
public class Tools_Net {
     /**
     * 字符串函数
     * @param pSession
     * @param pTreap_PNode
     * @param Function_Call
     * @param strFunction
     * @param pNode_Input
     * @param pNode_Output
     * @param pMap_Item
     * @return
     */
    public static String Function_Call(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call, 
            String strFunction, 
            Node pNode_Input,
            Node pNode_Output, 
            C_Map_Item pMap_Item) {
        String strReturn = "";
        strFunction = strFunction.toLowerCase();
        switch (strFunction) {
            case "net.sftp.upload":
                strReturn=Net_SFtp_Upload(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
            case "net.http":
                strReturn=Net_Http(pSession,pTreap_PNode,Function_Call,strFunction,pNode_Input,pMap_Item);
                break;
        }
        return strReturn;
    }
    
    
    public static String Net_SFtp_Upload(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call, 
            String strFunction, 
            Node pNode_Input,
            C_Map_Item pMap_Item) {
        
        String strIP = null;
        int iPort = 80;
        String strUser = null;
        String strPassword = null;
        String strFile = null;
        String strPath = null;

        C_Function_Return pReturn=Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call,pNode_Input,pMap_Item);

        for (int i=0;i<pReturn.pList.size();i++){
            switch (i) {
                case 0:
                    strIP = pReturn.pList.get(i).toString();
                    break;
                case 1:
                    iPort = Integer.valueOf(pReturn.pList.get(i).toString());
                    break;
                case 2:
                    strUser = pReturn.pList.get(i).toString();
                    break;
                case 3:
                    strPassword = pReturn.pList.get(i).toString();
                    break;
                case 4:
                    strFile = pReturn.pList.get(i).toString();
                    break;
                case 5:
                    strPath = pReturn.pList.get(i).toString();
                    break;
            }
        }

        String strReturn=Tools_Net.SFtp_Upload(strIP,iPort,strUser,strPassword,strFile,strPath);
        return strReturn;
    }
    
    
    public static String Net_Http(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            int Function_Call, 
            String strFunction, 
            Node pNode_Input,
            C_Map_Item pMap_Item) {
        
        
        String strURL = null;
        String strMethod = null;
        String strData = null;
        String strEncode = null;
        String strReference = null;

        C_Function_Return pReturn=Tools_GetParam.GetParam(pSession,pTreap_PNode,Function_Call,pNode_Input,pMap_Item);

        for (int i=0;i<pReturn.pList.size();i++){
            switch (i) {
                case 0:
                    strURL = pReturn.pList.get(i).toString();
                    break;
                case 1:
                    strMethod = pReturn.pList.get(i).toString();
                    break;
                case 2:
                    strData = pReturn.pList.get(i).toString();
                    break;
                case 3:
                    strEncode = pReturn.pList.get(i).toString();
                    break;
                case 4:
                    strReference = pReturn.pList.get(i).toString();
                    break;
            }
        }
        String strReturn=S_Net.sendPost(strURL,strMethod, strData, strEncode,strReference);
        return strReturn;
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
    public static String SFtp_Upload(String host, 
            int port, String username,String password,
            String uploadFile,String directory) {
        ChannelSftp sftp;
        try {
            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            Session sshSession = jsch.getSession(username, host, port);
            System.out.println("Session created.");
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            System.out.println("Session connected.");
            System.out.println("Opening Channel.");
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            System.out.println("Connected to " + host + ".");
                 
            sftp.cd(directory);
            File file=new File(uploadFile);
            sftp.put(new FileInputStream(file), file.getName());
            return "OK";
        } catch (Exception e) {
            return e.toString();
        }
    }

    
}
