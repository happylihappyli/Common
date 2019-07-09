package com.funnyai.netso_multi_thread;


/**
 *
 * @author happyli
 */
public abstract class A_NetSO_Save {
    public C_Robot pRobot=null;
    private String strFile="";
    
    public void start(C_Robot pRobot){
        this.pRobot=pRobot;
    }
    
    public abstract void start_file(NetSO_Multi_Thread pNetSO);
    public abstract void save_line(String strFrom,String strTo,double dbWeight);
    public abstract void end();
    
    public void setFile(String strFile){
        this.strFile=strFile;
    }
    public String getFile(){
        return this.strFile;
    }
    
}
