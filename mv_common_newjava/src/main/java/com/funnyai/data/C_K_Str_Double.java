package com.funnyai.data;

public final class C_K_Str_Double implements IComparable{
	
    
    private String key; 
    private double dbValue; 

    public C_K_Str_Double(String key,double dbValue) 
    { 
    	this.key = key;
        this.dbValue=dbValue;
    } 

    @Override
    public int compareTo(IComparable key) 
    { 
        int intReturn;
        
        String key2=((C_K_Str_Double)key).key;
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
            double iTmp=dbValue-((C_K_Str_Double)key).dbValue;
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
