package com.funnyai.data;

public final class C_K_Double implements IComparable{
	
    public C_K_Double(double dbValue){
    	value=dbValue;
    }
    
    double value;
    
    @Override
    public int compareTo(IComparable rhs ){
    	if (rhs==null){//|| rhs==Treap.nullNode
    		return 0;
    	}else{
    		double a=value-((C_K_Double)rhs).value;
    		if (a>0){
        		return 1;
    		}else if(a==0){
        		return 0;
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
