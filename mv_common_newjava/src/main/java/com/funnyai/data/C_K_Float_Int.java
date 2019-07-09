package com.funnyai.data;


public class C_K_Float_Int  implements IComparable{

	private float dbValue; 
	private int ID;
	
	public C_K_Float_Int(float dbPower, int ID) {
	    dbValue = dbPower; 
	    this.ID = ID; 
	}

	public int compareTo(IComparable key) 
	{ 
	    
	    if (dbValue > (((C_K_Float_Int)key).dbValue)) { 
	        return 1; 
	    } 
	    else if (dbValue < (((C_K_Float_Int)key).dbValue)) { 
	        return -1; 
	    } 
	    else { 
	        if (ID > (((C_K_Float_Int)key).ID)) { 
	            return 1; 
	        } 
	        else if (ID < (((C_K_Float_Int)key).ID)) { 
	            return -1; 
	        } 
	        else { 
	            return 0; 
	        } 
	    } 
	    
	} 
}
