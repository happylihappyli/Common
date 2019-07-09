package com.funnyai.data;


public class C_K_Long_Long_Long_Long implements IComparable{
	private long Item1; 
        private long Item2; 
        private long Item3;
        private long Item4;

	public C_K_Long_Long_Long_Long(long K1,long K2,long K3,long K4) {
		Item1 = K1;
                Item2 = K2;
                Item3 = K3;
                Item4 = K4;
	}
        
	public int compareTo(IComparable key) 
	{
	    if (Item1 > (((C_K_Long_Long_Long_Long)key).Item1)) { 
	        return 1; 
	    }else if (Item1 < (((C_K_Long_Long_Long_Long)key).Item1)) { 
	        return -1; 
	    }else { 
	        if (Item2 > (((C_K_Long_Long_Long_Long)key).Item2)) { 
                    return 1; 
                }else if (Item2 < (((C_K_Long_Long_Long_Long)key).Item2)) { 
                    return -1;
                }else { 
                    if (Item3 > (((C_K_Long_Long_Long_Long)key).Item3)) { 
                        return 1; 
                    } else if (Item3 < (((C_K_Long_Long_Long_Long)key).Item3)) { 
                        return -1; 
                    } else { 
                        if (Item4 > (((C_K_Long_Long_Long_Long)key).Item4)) { 
                            return 1;
                        }else if (Item4 < (((C_K_Long_Long_Long_Long)key).Item4)) { 
                            return -1; 
                        }else { 
                            return 0; 
                        }
                    }
                }
	    } 
	}
 

}

