package com.funnyai.data;

/**
 * 忽略大小写
 * @author Administrator
 */
public final class C_K_Str implements IComparable{
	
    
    private final String strMyKey; 

    public C_K_Str(String key) 
    { 
        strMyKey = key; 
    } 
    
    public String getStr(){
    	return strMyKey;
    }

    public int compareTo(IComparable key) 
    { 
        int intReturn;
        String mKey=((C_K_Str)key).strMyKey;
        if (strMyKey!=null && mKey!=null){
            intReturn = strMyKey.compareToIgnoreCase(mKey); 
        }else{
        	if (strMyKey==null){
        		return -1;
        	}else{
        		return 1;
        	}
        }
        return intReturn;
    } 
}
