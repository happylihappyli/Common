package com.funnyai.data;


public class C_K_Int_Int implements IComparable{
	private int dbKey;
	private int dbKey2;

	public C_K_Int_Int(int Key,int Key2) {
		dbKey = Key;
		dbKey2= Key2;
	}
	public int compareTo(IComparable key) 
	{ 
	    
	    if (dbKey > (((C_K_Int_Int)key).dbKey)) { 
	        return 1; 
	    } 
	    else if (dbKey < (((C_K_Int_Int)key).dbKey)) { 
	        return -1; 
	    } 
	    else { 
	    	 if (dbKey2 > (((C_K_Int_Int)key).dbKey2)) { 
	 	        return 1; 
	 	    } 
	 	    else if (dbKey2 < (((C_K_Int_Int)key).dbKey2)) { 
	 	        return -1; 
	 	    } 
	 	    else {
	 	        return 0; 
	 	    } 
	    } 
	}
 

}
