package com.funnyai.data;

public final class C_K_Str_CaseSensitive implements IComparable{
	
    
    private String strMyKey; 

    public C_K_Str_CaseSensitive(String key) 
    { 
        strMyKey = key; 
    } 
    
    public String getStr(){
    	return strMyKey;
    }

    public int compareTo(IComparable key) 
    { 
        int intReturn;
        String mKey=((C_K_Str_CaseSensitive)key).strMyKey;
        if (strMyKey!=null && mKey!=null){
            intReturn = strMyKey.compareTo(mKey); 
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
