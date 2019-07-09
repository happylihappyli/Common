package com.funnyai.Enviroment;

import java.net.InetAddress;

public class S_SYS {

	public static String computerName(){
		String strReturn = null;
		try {
			strReturn = InetAddress.getLocalHost().getHostName();
		}
		catch(Exception ex){
			
		}
		return strReturn;
	}
	
}
