package com.funnyai.math;

final public class Izrazi
   {
   // vraca raspon trenutnog izraza ( onog koji se nalazi pod kursorom )
   // provjera : da
   public static Raspon raspon_trenutnog_izraza( String izrazi, int pozicija )
      {
      int p=pocetak( izrazi, pozicija );
      int k=kraj( izrazi, pozicija );

      if ( k<=p )
      	return null;
		else
      	return new Raspon( p, k );
      }

   // provjera : da
   private static int pocetak( String izrazi, int pozicija )
      {
      int pocetak;
      int duzina;

      pocetak=izrazi.lastIndexOf( "\n\n", pozicija-1 );
      duzina=izrazi.length();

      if ( pocetak==-1 )
         pocetak=0;

      while ( pocetak<duzina && Character.isWhitespace( izrazi.charAt( pocetak )) )
         pocetak=pocetak+1;

      return pocetak;
      }

   // provjera : da
   private static int kraj( String izrazi, int pozicija )
      {
      int kraj;

      kraj=izrazi.indexOf( "\n\n", pozicija );

      if ( kraj==-1 )
         kraj=izrazi.length()-1;

      while ( kraj>=0 && Character.isWhitespace( izrazi.charAt( kraj )) )
         kraj=kraj-1;

      return kraj+1;
      }

   }