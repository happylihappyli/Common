package com.funnyai.data;

public final class C_K_Str_Long implements IComparable{
	
    
    private String key; 
    private long iLong; 

    public C_K_Str_Long(String key,long iLong) 
    { 
    	this.key = key;
        this.iLong=iLong;
    } 

    public int compareTo(IComparable key) 
    { 
        int intReturn=0;
        
        String key2=((C_K_Str_Long)key).key;
        if (this.key!=null && key2!=null){
            intReturn = this.key.compareToIgnoreCase(key2); 
        }else{
        	if (this.key==null){
        		return -1;
        	}else{
        		return 1;
        	}
        }
        
        if (intReturn==0){
        	long iTmp=iLong-((C_K_Str_Long)key).iLong;
        	if (iTmp>0){
        		return 1;
        	}else if (iTmp<0){
        		return -1;
        	}else{
        		return 0;
        	}
        }else{
        	return intReturn;
        }
    } 
}
