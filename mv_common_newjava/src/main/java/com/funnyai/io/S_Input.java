package com.funnyai.io;

import java.io.IOException;

public class S_Input {

	public static String GetInput(String strTip){
		System.out.println(strTip);
		byte buffer[] = new byte[512]; //输入缓冲区
		
		try {
			System.in.read(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		} //读取标准输入流
		
		String strInput=new String(buffer);
		int pos=strInput.indexOf("\r");
		
		if (pos>0){
			strInput=strInput.substring(0,pos);
		}else{
			strInput="";
		}
		
		return strInput;
	}
}
