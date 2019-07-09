package com.funnyai.data;


public class C_K_Int_Int_Int implements IComparable{
	private int Item1; 
        private int Item2; 
        private int Item3; 

	public C_K_Int_Int_Int(int K1,int K2,int K3) {
		Item1 = K1;
                Item2 = K2;
                Item3 = K3;
	}
        
	public int compareTo(IComparable key) 
	{
	    if (Item1 > (((C_K_Int_Int_Int)key).Item1)) { 
	        return 1; 
	    } 
	    else if (Item1 < (((C_K_Int_Int_Int)key).Item1)) { 
	        return -1; 
	    } 
	    else { 
	        if (Item2 > (((C_K_Int_Int_Int)key).Item2)) { 
                    return 1; 
                } 
                else if (Item2 < (((C_K_Int_Int_Int)key).Item2)) { 
                    return -1; 
                } 
                else { 
                    if (Item3 > (((C_K_Int_Int_Int)key).Item3)) { 
                        return 1; 
                    } 
                    else if (Item3 < (((C_K_Int_Int_Int)key).Item3)) { 
                        return -1; 
                    } 
                    else { 
                        return 0; 
                    }
                }
	    } 
	}
 

}
