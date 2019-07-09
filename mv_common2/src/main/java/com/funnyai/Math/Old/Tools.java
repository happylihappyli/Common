package com.funnyai.Math.Old;


final public class Tools
	{
	private static String broj;
	private static String decimale;
	private static String eksponent;
	private static int pozicija_tocke;

	// ---------------------------------------------------------------------------------------
	public static String rpad( String s, char znak, int duzina )
		{
		int n=duzina-s.length();

		for ( int i=0; i<n; i++ )
			s = s + znak;

		return s;
		}
	// ---------------------------------------------------------------------------------------
	public static String trunc( String s, int p )
		{
		char predznak='+';

		rastavljanje( s );

		// s negativnim brojevima radi kao i s pozitivnim
		if ( s.charAt(0)=='-' )
			{
			broj=broj.substring( 1 );
			predznak='-';
			pozicija_tocke--;
			}

		if ( broj.charAt(0)!='0' )
			{
			broj="0"+broj;
			pozicija_tocke++;
			}

		String spoj;

		spoj=broj+decimale;

		if ( p>=decimale.length() )
			return s;
		if ( p+pozicija_tocke<1 )
			return "0";

		broj=spoj.substring( 0, pozicija_tocke+p );
		decimale=spoj.substring( pozicija_tocke+p );

		if ( broj.length()<pozicija_tocke )
			broj=rpad( broj, '0', pozicija_tocke );

		s=broj.substring( 0, pozicija_tocke ) + "." + broj.substring( pozicija_tocke )
         + "E" + eksponent;
		return predznak+s;

		}
   // ---------------------------------------------------------------------------------------
	public static String round( String s, int p )
		{
		char predznak='+';

		rastavljanje( s );

		// s negativnim brojevima radi kao i s pozitivnim
		if ( s.charAt(0)=='-' )
			{
			broj=broj.substring( 1 );
			predznak='-';
			pozicija_tocke--;
			}

		if ( broj.charAt(0)!='0' )
			{
			broj="0"+broj;
			pozicija_tocke++;
			}

		// System.out.println( "=======================" );
		// System.out.println( "Broj : " + broj );
		// System.out.println( "Decimale : " + decimale );
		// System.out.println( "Eksponent : " + eksponent );
		// System.out.println( "Pozicija : " + pozicija_tocke );
		// System.out.println( "Predznak : " + predznak );

		String spoj;

		spoj=broj+decimale;

		// System.out.println( "Spoj : " + spoj );

		if ( p>=decimale.length() )
			return s;
		if ( p+pozicija_tocke<1 )
			return "0";

		broj=spoj.substring( 0, pozicija_tocke+p );
		decimale=spoj.substring( pozicija_tocke+p );

		// System.out.println( "=======================" );
		// System.out.println( "Broj : " + broj );
		// System.out.println( "Decimale : " + decimale );
		// System.out.println( "Pozicija : " + pozicija_tocke );
		// System.out.println( "Predznak : " + predznak );

		char z=decimale.charAt(0);

		if ( z>'4' )
			{
			int duzina=broj.length();
			long b=Long.parseLong( broj );
			b=b+1;
			broj=rpad( "0", '0', duzina-String.valueOf( b ).length() )+String.valueOf( b );

			if ( String.valueOf( broj ).length() > duzina )
				pozicija_tocke++;
			}

		if ( broj.length()<pozicija_tocke )
			broj=rpad( broj, '0', pozicija_tocke );

		s=broj.substring( 0, pozicija_tocke ) + "." + broj.substring( pozicija_tocke )
         + "E" + eksponent;
		return predznak+s;

		}
	// ---------------------------------------------------------------------------------------
	// Rastavlja broj na cijeli dio, decimale i eksponent
	// ako neki dio fali taj je prazan

	// Primjer : 123.456E+789

	// Cijeli dio : 123
	// Decimale   : 456
	// Eksponent  : +789

   // provjera : da
	public static void rastavljanje( String s )
		{
		int pozicija_eksponenta;

      broj="";
      decimale="";
      eksponent="0";

		pozicija_eksponenta=s.indexOf( "E" );
		// ako je broj u eksponencijalnom obliku
		if ( pozicija_eksponenta!=-1 )
			{
			eksponent=s.substring( pozicija_eksponenta+1 );
			s=s.substring( 0, pozicija_eksponenta );
			}

		pozicija_tocke=s.indexOf( "." );
		// ako je broj decimalan
		if ( pozicija_tocke!=-1 )
			{
			decimale=s.substring( pozicija_tocke+1 );
			broj=s.substring( 0, pozicija_tocke );
			}
		else
			{
			broj=s;
			pozicija_tocke=broj.length();
			}

		// System.out.println( "Broj : " + broj );
		// System.out.println( "Decimale : " + decimale );
		// System.out.println( "Eksponent : " + eksponent );
		// System.out.println( "Pozicija : " + pozicija_tocke );

		}
	// ---------------------------------------------------------------------------------------
	}