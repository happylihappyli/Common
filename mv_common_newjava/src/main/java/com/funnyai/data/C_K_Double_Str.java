package com.funnyai.data;


public final class C_K_Double_Str implements IComparable{

    String key;
    double value;
    
    public C_K_Double_Str(double dbValue,String strKey){
    	value=dbValue;
    	key=strKey;
    }
    
    public int compareTo(IComparable rhs ){
    	if (rhs==null){// || rhs==Treap.nullNode
    		return 0;
    	}else{
    		double a=value-((C_K_Double_Str)rhs).value;
    		if (a>0){
        		return 1;
    		}else if(a==0){
        		return key.compareTo(((C_K_Double_Str)rhs).key);
    		}else{
        		return -1;
    		}
    	}
    }
    
    public String toString()
    {
    	return String.valueOf(value);
    }
}
