package com.funnyai.data;


public class C_K_Long implements IComparable{
	private long dbKey; 

	public C_K_Long(long Key) {
		dbKey = Key; 
	}
	public int compareTo(IComparable key) 
	{ 
	    
	    if (dbKey > (((C_K_Long)key).dbKey)) { 
	        return 1; 
	    } 
	    else if (dbKey < (((C_K_Long)key).dbKey)) { 
	        return -1; 
	    } 
	    else { 
	        return 0; 
	    } 
	}
 

}
