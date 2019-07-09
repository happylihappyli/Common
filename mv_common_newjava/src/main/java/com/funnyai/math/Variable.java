// provjera : da

package com.funnyai.math;

final public class Variable
   {
	public String varijabla;
	public double vrijednost;

	public Variable( String varijabla, double vrijednost )
    	{
    	this.varijabla=varijabla;
	 	this.vrijednost=vrijednost;
    	}

	public String toString()
		{
		return varijabla;
		}
   }