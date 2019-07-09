package com.funnyai.data;

import java.util.Date;

public class C_K_Date implements IComparable{
	
    
    private Date MyDate; 

    public C_K_Date(Date key) 
    { 
    	MyDate.setTime(key.getTime()); 
    } 

    public int compareTo(IComparable key) 
    { 
    	
        int intReturn;
        Date mKey=((C_K_Date)key).MyDate;
        if (MyDate!=null && mKey!=null){
            intReturn = MyDate.compareTo(mKey); 
        }else{
        	if (MyDate==null){
        		return -1;
        	}else{
        		return 1;
        	}
        }
        return intReturn;
    } 
}
