package com.funnyai.math;

public class S_Number {
	public static String GetNumeber(String strInput)
	{
		String strReturn = "";
	    
	    if (strInput.length() == 1) {
	        if  (strInput.equalsIgnoreCase("一")) {
	                strReturn = "1";
	        }else if (strInput.equalsIgnoreCase("二")){
	                strReturn = "2";
	        }else if (strInput.equalsIgnoreCase("三")){
	                strReturn = "3";
	        }else if (strInput.equalsIgnoreCase("四")){
	                strReturn = "4";
	        }else if (strInput.equalsIgnoreCase("五")){
	                strReturn = "5";
	        }else if (strInput.equalsIgnoreCase("六")){
	                strReturn = "6";
	        }else if (strInput.equalsIgnoreCase("七")){
	                strReturn = "7";
	        }else if (strInput.equalsIgnoreCase("八")){
	                strReturn = "8";
	        }else if (strInput.equalsIgnoreCase("九")){
	                strReturn = "9";
	        }else if (strInput.equalsIgnoreCase("十")){
	                strReturn = "10";
	        }
	    }
	    else if (strInput.length() == 2) {
	        strReturn = "";
	        String strTmp="";
	        for (int i = 1; i <= 2; i++) {
	            strTmp = S_Number.GetNumeber(strInput.substring(0, 1));
	            strReturn += strTmp;
	        }
	    }
	    else {
	        strReturn = strInput;
	    }
	    return strReturn;
	}

}
