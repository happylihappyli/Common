package com.funnyai.data;


public class C_K_Double_Long  implements IComparable{

	private double dbValue; 
	private long lngID;
	
	public C_K_Double_Long(double dbPower, long ID) {
	    lngID = ID; 
	    dbValue = dbPower; 
	}

	public int compareTo(IComparable key) 
	{ 
	    
	    if (dbValue > (((C_K_Double_Long)key).dbValue)) { 
	        return 1; 
	    } 
	    else if (dbValue < (((C_K_Double_Long)key).dbValue)) { 
	        return -1; 
	    } 
	    else { 
	        if (lngID > (((C_K_Double_Long)key).lngID)) { 
	            return 1; 
	        } 
	        else if (lngID < (((C_K_Double_Long)key).lngID)) { 
	            return -1; 
	        } 
	        else { 
	            return 0; 
	        } 
	    } 
	    
	} 
}
