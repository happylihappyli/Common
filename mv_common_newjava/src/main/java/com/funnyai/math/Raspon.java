package com.funnyai.math;

final public class Raspon
   {
   public int pocetak;
   public int kraj;

	public Raspon()
		{
		this.pocetak=0;
		this.kraj=0;
		}

	public Raspon (int pocetak, int kraj)
		{
		this.pocetak=pocetak;
		this.kraj=kraj;
		}

	public String toString()
		{
		String s;
		s="( "+String.valueOf(pocetak)+", "+String.valueOf(kraj)+" )";
		return s;
		}

	}