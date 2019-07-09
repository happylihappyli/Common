package com.funnyai.data;


public class C_K_Double_Int  implements IComparable{

	private double dbValue; 
	private int lngID;
	
	public C_K_Double_Int(double dbPower, int ID) {
	    lngID = ID; 
	    dbValue = dbPower; 
	}

	public int compareTo(IComparable key) 
	{ 
	    
	    if (dbValue > (((C_K_Double_Int)key).dbValue)) { 
	        return 1; 
	    } 
	    else if (dbValue < (((C_K_Double_Int)key).dbValue)) { 
	        return -1; 
	    } 
	    else { 
	        if (lngID > (((C_K_Double_Int)key).lngID)) { 
	            return 1; 
	        } 
	        else if (lngID < (((C_K_Double_Int)key).lngID)) { 
	            return -1; 
	        } 
	        else { 
	            return 0; 
	        } 
	    } 
	    
	} 
}
