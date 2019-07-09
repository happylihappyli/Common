package com.funnyai.data;


public class C_K_Long_Long implements IComparable{
	private long dbKey;
	private long dbKey2;

	public C_K_Long_Long(long Key,long Key2) {
		dbKey = Key;
		dbKey2= Key2;
	}
	public int compareTo(IComparable key) 
	{ 
	    
	    if (dbKey > (((C_K_Long_Long)key).dbKey)) { 
	        return 1; 
	    } 
	    else if (dbKey < (((C_K_Long_Long)key).dbKey)) { 
	        return -1; 
	    } 
	    else { 
	    	 if (dbKey2 > (((C_K_Long_Long)key).dbKey2)) { 
	 	        return 1; 
	 	    } 
	 	    else if (dbKey2 < (((C_K_Long_Long)key).dbKey2)) { 
	 	        return -1; 
	 	    } 
	 	    else {
	 	        return 0; 
	 	    } 
	    } 
	}
 

}
