// provjera : da

package com.funnyai.Math.Old;

final public class Operator
   {
	public String operator;
	public byte prioritet;

	public Operator( String operator, byte prioritet )
		{
		this.operator=operator;
		this.prioritet=prioritet;
		}

	public String toString()
		{
		return operator;
		}

   }