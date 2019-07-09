package com.funnyai.data;


public class C_K_Int implements IComparable{
	private int dbKey; 

	public C_K_Int(int Key) {
		dbKey = Key; 
	}
	public int compareTo(IComparable key) 
	{ 
	    
	    if (dbKey > (((C_K_Int)key).dbKey)) { 
	        return 1; 
	    } 
	    else if (dbKey < (((C_K_Int)key).dbKey)) { 
	        return -1; 
	    } 
	    else { 
	        return 0; 
	    } 
	}
 

}
