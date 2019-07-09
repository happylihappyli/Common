package com.funnyai.data;

public class C_DB_Treap_URL {

	public Treap pTreap_String=new Treap();
	public Treap pTreap_ID=new Treap();
	public String strT;



	public void insert(C_FunnyNode pURL) 
	{ 
	    
	    if (pURL.ID <= 0) 
	        return; // TODO: might not be correct. Was : Exit Sub 

	    
	    pTreap_String.remove(new C_K_Str(pURL.URL)); 
	    pTreap_ID.remove(new C_K_Int(pURL.ID)); 
	    
	    pTreap_String.insert(new C_K_Str(pURL.URL), pURL); 
	    pTreap_ID.insert(new C_K_Int(pURL.ID), pURL); 
	    
	} 


	public void remove(int ID) 
	{ 
	    
	    C_FunnyNode pURL; 
	    pURL = (C_FunnyNode) pTreap_ID.find(new C_K_Int(ID)); 
	    
	    if ((pURL != null)) {
	        
	        pTreap_ID.remove(new C_K_Int(ID)); 
	        pTreap_String.remove(new C_K_Str(pURL.URL)); 
	    } 
	} 

}
