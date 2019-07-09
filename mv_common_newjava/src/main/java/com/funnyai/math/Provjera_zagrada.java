// provjera : da

package com.funnyai.math;

/*
Klasa provjerava da li je u izrazu raspored zagrada korektan. Podrzane su tri
vrste zagrada : obicna "( )", uglata "[ ]" i viticasta "{ }".
*/

import java.util.Stack;

final public class Provjera_zagrada
   {
	// Provjera	: Da
	public static boolean je_otvorena_zagrada( char znak )
		{
		if ( znak=='(' || znak=='[' || znak=='{' )
			return true;
		else
			return false;
    	}
	// Provjera	: Da
	public static boolean je_zatvorena_zagrada( char znak )
		{
		if ( znak==')' || znak==']' || znak=='}' )
			return true;
		else
			return false;
		}
	// Utvrduje da li otvorena zagrada odgovara zatvorenoj zagradi
	// Provjera	: Da
	private static boolean poklapanje_zagrada( char otv_zagrada, char zatv_zagrada )
		{
		if ( otv_zagrada=='(' && zatv_zagrada==')' )
			return true;
		else if ( otv_zagrada=='[' && zatv_zagrada==']' )
			return true;
		else if ( otv_zagrada=='{' && zatv_zagrada=='}' )
			return true;
		else
			return false;
		}
	// Provjera	: Da
	public static boolean zagrade_pravilne( String izraz )
		{
		Stack			s = new Stack();
		int			i;
		char			znak;
		char			trenutni_znak;
		Character	c;
		boolean 		povrat=true;

		for ( i=0; i<izraz.length(); i++ )
			{

			trenutni_znak=izraz.charAt(i);

			if ( je_otvorena_zagrada(trenutni_znak) )
				{
				c=new Character(trenutni_znak);
				s.push( c );
				}
			else if ( je_zatvorena_zagrada(trenutni_znak) )
				{
				if ( s.isEmpty() )
					{
					povrat=false;
					break;
					}
				else
					{
					c=(Character)s.pop();
					znak=c.charValue();
					if ( !poklapanje_zagrada(znak, trenutni_znak) )
						{
						povrat=false;
						break;
						}
					}
				}
			}

		if ( !s.isEmpty() )
			povrat=false;

		return povrat;

		}
   }