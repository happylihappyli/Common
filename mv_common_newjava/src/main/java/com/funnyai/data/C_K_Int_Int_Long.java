package com.funnyai.data;


public class C_K_Int_Int_Long implements IComparable{
	private int Item1; 
        private int Item2; 
        private long Item3; 

	public C_K_Int_Int_Long(int K1,int K2,long K3) {
		Item1 = K1;
                Item2 = K2;
                Item3 = K3;
	}
        
	public int compareTo(IComparable key) 
	{
	    if (Item1 > (((C_K_Int_Int_Long)key).Item1)) { 
	        return 1; 
	    } 
	    else if (Item1 < (((C_K_Int_Int_Long)key).Item1)) { 
	        return -1; 
	    } 
	    else { 
	        if (Item2 > (((C_K_Int_Int_Long)key).Item2)) { 
                    return 1; 
                } 
                else if (Item2 < (((C_K_Int_Int_Long)key).Item2)) { 
                    return -1; 
                } 
                else { 
                    if (Item3 > (((C_K_Int_Int_Long)key).Item3)) { 
                        return 1; 
                    } 
                    else if (Item3 < (((C_K_Int_Int_Long)key).Item3)) { 
                        return -1; 
                    } 
                    else { 
                        return 0; 
                    }
                }
	    } 
	}
 

}
