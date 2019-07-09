package com.funnyai.data;

public final class C_K_Long_Str implements IComparable{
	
    
    private String key; 
    private long iLong; 

    public C_K_Long_Str(long iLong,String key) 
    { 
    	this.key = key;
        this.iLong=iLong;
    } 

    public int compareTo(IComparable key) 
    { 
        int intReturn=0;
        
        long iTmp=iLong-((C_K_Long_Str)key).iLong;
    	if (iTmp>0){
    		return 1;
    	}else if (iTmp<0){
    		return -1;
    	}else{
    		String key2=((C_K_Long_Str)key).key;
            if (this.key!=null && key2!=null){
                intReturn = this.key.compareToIgnoreCase(key2); 
            }else{
            	if (this.key==null){
            		return -1;
            	}else{
            		return 1;
            	}
            }
            
            return intReturn;
    	}
    } 
}
