package com.funnyai.Math.Old;

//克罗地亚文
import java.util.Vector;
import java.text.DecimalFormat;
import java.math.BigDecimal;

final public class Calculate{//Izracunavanje
	//计算
	//返回第一的位置括号内的封闭矢量
	// vraca poziciju prve zatvorene zagrade u vektoru "tokeni"
   // ako je ne nade vraca 0
   // provjera : da
	public static int pozicija_prve_zatvorene_zagrade( Vector tokeni )
   	{
		Token			t;

		for ( int i=0; i<tokeni.size(); i++ )
			{
			t=(Token)tokeni.elementAt( i );
			if ( t.oznaka==')' )
            return i;
			}
		return 0;
		}

	// vraca poziciju otvorene zagrade koja odgovara pronadenoj zatvorenoj zagradi
	// poziv ima smisla samo ako funkcija "pozicija_prve_zatvorene_zagrade" nade zatvorenu zagradu
	// provjera : da
	public static int pozicija_otvorene_zagrade( Vector tokeni, int pozicija_zatvorene_zagrade )
   	{
		int		i;
		Token		t;

		i=pozicija_zatvorene_zagrade-2;

		while ( i>=0 )
			{
			t=(Token)tokeni.elementAt( i );
			if ( t.oznaka=='(' )
				{
            return i;
				}
			i--;
			}
		return 0;
   	}

 	// vraca raspon zagrada koji ce se izracunati
   // otvorena i zatvorena zagrada ulaze u raspon
   // ako nema zagrada vraca null
	// provjera : da
	static public Raspon raspon_zagrada( Vector tokeni )
   	{
		int		poz_otvor_zagrade;
		int		poz_zatvor_zagrade;
		Raspon	raspon = null;

		poz_zatvor_zagrade=pozicija_prve_zatvorene_zagrade( tokeni );
		if ( poz_zatvor_zagrade!=0 )
			{
			poz_otvor_zagrade=pozicija_otvorene_zagrade( tokeni, poz_zatvor_zagrade );
			raspon = new Raspon( poz_otvor_zagrade, poz_zatvor_zagrade );
			}
		return raspon;
   	}

	// iz dijelova koji se nalaze u vektoru formira string
	// provjera : da
	public static String spoj_tokena( Vector tokeni )
   	{
		String	izraz=new String();
		Token		t;

		for ( int i=0; i<tokeni.size(); i++ )
			{
			t=(Token)tokeni.elementAt( i );

         if ( t.oznaka=='D' )
            {
            double broj=((Double)t.token).doubleValue();
            izraz=izraz + formatirani_broj( broj );
            }
         else
            izraz=izraz + t.token;

   		if ( izraz.endsWith( ".0" ) )
     			izraz=izraz.substring( 0, izraz.length()-2 );
         izraz=izraz + " ";
			}
		return izraz;
   	}

   public static String formatirani_broj( double d )
      {
      if ( 1E-15<Math.abs(d) && Math.abs(d)<1E+15 )
         {
         DecimalFormat df = new DecimalFormat( "#.###############################" );
         return df.format( d ).replace( ',', '.' );
         }
      else
         return String.valueOf( d );
      }

	// vraca poziciju unutar raspona zagrada gdje se nalazi operator s najvisim prioritetom
	// ako unutar raspona zagrada nema niti jednog operatora vraca 0
   // provjera : da
	public static int pozicija_operatora( Vector tokeni, Raspon raspon_zagrada )
   	{
		byte     max_prioritet=Byte.MAX_VALUE;
      int      max_pozicija=0;

      byte     prioritet;
      String	operator;

		Token		t;

		for ( int i=raspon_zagrada.pocetak+2; i<=raspon_zagrada.kraj-2; i++ )
			{
			t=(Token)tokeni.elementAt( i );
         if ( t.oznaka!='P' )
            continue;
         prioritet=((Operator)t.token).prioritet;
         operator=((Operator)t.token).operator;

         if ( prioritet < max_prioritet || ( operator.equals("^") ||
            operator.equals("**") ) && prioritet == max_prioritet )
         	{
				max_prioritet=prioritet;
            max_pozicija=i;
            }
			}
		return max_pozicija;
   	}

	// provjera : da
	public static double rezultat_operacije( double broj1, double broj2, String op )
   	{
		double rezultat=0;

      if ( op.equals( "^" ) || op.equals( "**" ) )
			// rezultat=Math.pow( broj1, broj2 );
         rezultat=Matematika.potenciranje( broj1, broj2 );
		else if ( op.equals( "*" ) )
         // rezultat=broj1*broj2;
         rezultat=Matematika.mnozenje( broj1, broj2 );
		else if ( op.equals( "/" ) )
         // rezultat=broj1/broj2;
         rezultat=Matematika.dijeljenje( broj1, broj2 );
		else if ( op.equals( "mod" ) )
			rezultat=Math.IEEEremainder( broj1, broj2 );
		else if ( op.equals( "%" ) )
			rezultat=broj1%broj2;
		else if ( op.equals( "+" ) )
         // rezultat=broj1+broj2;
         rezultat=Matematika.zbrajanje( broj1, broj2 );
		else if ( op.equals( "-" ) )
         // rezultat=broj1-broj2;
         rezultat=Matematika.oduzimanje( broj1, broj2 );
		else if ( op.equals( "<<" ) )
			rezultat=(long)broj1<<(long)broj2;
		else if ( op.equals( ">>" ) )
			rezultat=(long)broj1>>(long)broj2;
		else if ( op.equals( ">>>" ) )
			rezultat=(long)broj1>>>(long)broj2;
		else if ( op.equals( "<" ) )
			rezultat=broj1<broj2 ? 1:0;
		else if ( op.equals( ">" ) )
			rezultat=broj1>broj2 ? 1:0;
		else if ( op.equals( "<=" ) )
			rezultat=broj1<=broj2 ? 1:0;
		else if ( op.equals( ">=" ) )
			rezultat=broj1>=broj2 ? 1:0;
		else if ( op.equals( "==" ) )
			rezultat=broj1==broj2 ? 1:0;
		else if ( op.equals( "!=" ) || op.equals( "<>" ) )
			rezultat=broj1!=broj2 ? 1:0;
		else if ( op.equals( "and" ) )
			rezultat=(long)broj1 & (long)broj2;
		else if ( op.equals( "xor" ) )
			rezultat=(long)broj1 ^ (long)broj2;
		else if ( op.equals( "or" ) )
			rezultat=(long)broj1 | (long)broj2;
		else if ( op.equals( "&&" ) )
			rezultat=(broj1!=0 && broj2!=0) ? 1:0;
		else if ( op.equals( "||" ) )
			rezultat=(broj1!=0 || broj2!=0) ? 1:0;

		return rezultat;
   	}

	// provjera : da
	public static double vrijednost_operanda( Token t )
 		{
		if ( t.oznaka=='V' )
			return ((Varijabla)t.token).vrijednost;
		else if ( t.oznaka=='D' )
			return ((Double)t.token).doubleValue();
		else if ( t.oznaka=='H' )
			return Matematika.baza_broj( ((String)t.token).substring(2), 16 );
		else if ( t.oznaka=='O' )
			return Matematika.baza_broj( ((String)t.token).substring(2), 8 );
		else if ( t.oznaka=='B' )
			return Matematika.baza_broj( ((String)t.token).substring(2), 2 );
		return 0;
   	}

	// vrsi jedan korak racunanja izraza i u skladu s tim mijenja vektor tokena
	// ako je izraz izracunat do kraja vraca FALSE, ako nije TRUE.
	// provjera : da
	public static boolean korak_izracunavanja( Vector tokeni )
   	{
		boolean		ima_zagrada=true;
		Token			t;

		Raspon rang=raspon_zagrada( tokeni );

		// ako nema zagrada, rang obuhvaca cijeli izraz
		if ( rang==null )
			{
         // rang ukljucuje virtualne zagrade
			rang=new Raspon( -1, tokeni.size() );
			ima_zagrada=false;
			}

		int poz_max_op=pozicija_operatora( tokeni, rang );

  		// ako nema operatora
		if ( poz_max_op==0 )
			{
			// ako nema zagrada, izraz je izracunat do kraja
			if ( !ima_zagrada )
				{
				return false;
				}
         // funkcijska zagrada
         else
				{
				double	rezultat;
				rezultat=result_function( tokeni, rang.pocetak-1 );
				izbaci_tokene( tokeni, rang.pocetak-1 );

				t = new Token ( new Double(rezultat), 'D', 0, 0 );
				tokeni.setElementAt( t, rang.pocetak-1 );

            izbacivanje_zagrada( tokeni, rang.pocetak-1 );
				return true;
				}
			}

		double operand1, operand2;

		// odredivanje 1. operanda
		t=(Token)tokeni.elementAt( poz_max_op-1 );
		operand1=vrijednost_operanda( t );

		// odredivanje 2. operanda
		t=(Token)tokeni.elementAt( poz_max_op+1 );
		operand2=vrijednost_operanda( t );

		// odredivanje operatora
		t=(Token)tokeni.elementAt( poz_max_op );
		String op=((Operator)t.token).operator;

		double rezultat=rezultat_operacije( operand1, operand2, op );

		tokeni.removeElementAt( poz_max_op+1 );
		tokeni.removeElementAt( poz_max_op );

		t = new Token ( new Double(rezultat), 'D', 0, 0 );
		tokeni.setElementAt( t, poz_max_op-1 );

      izbacivanje_zagrada( tokeni, poz_max_op-1 );
		return true;
   	}

	// izbacuje tokene iz vektora nakon sto se izracuna funkcija
 	// izbacuje se otvorena zagrada, argumenti, zarezi i zatvorena zagrada
	// na mjesto funkcije umetnut ce se kasnije rezultat funkcije
	// provjera : da
	private static void izbaci_tokene( Vector tokeni, int pozicija_funkcije )
   	{
		while ( ((Token)tokeni.elementAt( pozicija_funkcije )).oznaka!=')' )
			{
			tokeni.removeElementAt( pozicija_funkcije );
			}
   	}

   public static void izbacivanje_visestrukih_zagrada( Vector tokeni )
      {
      Vector pomocni=new Vector( tokeni );
      Raspon r;

      while ( (r=raspon_zagrada( pomocni ))!=null )
         {
         if ( r.pocetak!=0 && r.kraj!=pomocni.size()-1 &&
            ((Token)pomocni.elementAt( r.pocetak-1 )).oznaka=='(' &&
            ((Token)pomocni.elementAt( r.kraj+1 )).oznaka==')' )
            {
            pomocni.removeElementAt( r.kraj+1 );
            pomocni.removeElementAt( r.pocetak-1 );
            tokeni.removeElementAt( r.kraj+1 );
            tokeni.removeElementAt( r.pocetak-1 );
            }
         else
            {
            // izbacivanje vanjskih zagrada
            if ( r.pocetak==0 && r.kraj==tokeni.size()-1 )
               {
               tokeni.removeElementAt( r.kraj );
               tokeni.removeElementAt( r.pocetak );
               }
            else
               {
               // dummy tokeni na mjestu zagrada
               pomocni.setElementAt( new Token(null,(char)0,0,0), r.kraj );
               pomocni.setElementAt( new Token(null,(char)0,0,0), r.pocetak );
               }
            }
         }
      return;
      }

   private static void izbacivanje_zagrada( Vector tokeni, int poz )
      {
      if (
         poz>1 &&
         ((Token)tokeni.elementAt( poz-2 )).oznaka!='F' &&
         ((Token)tokeni.elementAt( poz-1 )).oznaka=='(' &&
         ((Token)tokeni.elementAt( poz+1 )).oznaka==')'
         ||
         poz==1 &&
         ((Token)tokeni.elementAt( 0 )).oznaka=='(' &&
         ((Token)tokeni.elementAt( 2 )).oznaka==')'
         )
         {
         tokeni.removeElementAt( poz+1 );
         tokeni.removeElementAt( poz-1 );
         }

      return;
      }

   private static double stupnjevi_izlaz( double x )
      {
	   return x;
//	   
//      if ( Aplet.drg=='D' )
//         return Matematika.rad_to_deg( x );
//      else if ( Aplet.drg=='R' )
//         return x;
//      else if ( Aplet.drg=='G' )
//         return Matematika.rad_to_grad( x );
//      else
//         return 0;
      }


   private static double stupnjevi_ulaz( double x )
      {
	   return x;
      }
//   
//      if ( Aplet.drg=='D' )
//         return Matematika.deg_to_rad( x );
//      else if ( Aplet.drg=='R' )
//         return x;
//      else if ( Aplet.drg=='G' )
//         return Matematika.grad_to_rad( x );
//      else
//         return 0;
//      }

	// izracunava i vraca rezultat funkcije
	// provjera : da
	private static double result_function(
			Vector tokeni, int pozicija_funkcije )
   	{
		double rezultat=0;
      double operand;
		Token t;
		String fn;

  		t=(Token)tokeni.elementAt( pozicija_funkcije );
		fn=((Function)t.token).funkcija;

		t=(Token)tokeni.elementAt( pozicija_funkcije+2 );
		operand=vrijednost_operanda( t );

      // 1. Trigonometric

		if ( fn.equals("sin") )
			rezultat=Math.sin( stupnjevi_ulaz( operand ) );
		else if ( fn.equals("cos") )
			rezultat=Math.cos( stupnjevi_ulaz( operand ) );
		else if ( fn.equals("tan") )
			rezultat=Math.tan( stupnjevi_ulaz( operand ) );
		else if ( fn.equals("cot") )
			rezultat=Matematika.cot( stupnjevi_ulaz( operand ) );
		else if ( fn.equals("sec") )
			rezultat=Matematika.sec( stupnjevi_ulaz( operand ) );
		else if ( fn.equals("csc") )
			rezultat=Matematika.csc( stupnjevi_ulaz( operand ) );
		else if ( fn.equals("cint") )
			rezultat=(int)stupnjevi_ulaz( operand );
		
      // 2. Inverse trigonometric

		else if ( fn.equals("asin") )
			rezultat=stupnjevi_izlaz( Math.asin( operand ) );
		else if ( fn.equals("acos") )
			rezultat=stupnjevi_izlaz( Math.acos( operand ) );
		else if ( fn.equals("atan") )
			rezultat=stupnjevi_izlaz( Math.atan( operand ) );
		else if ( fn.equals("atan2") )
         {
   		t=(Token)tokeni.elementAt( pozicija_funkcije+4 );
   		double operand1=vrijednost_operanda( t );
			rezultat=Math.atan2( operand, operand1 );
         }
		else if ( fn.equals("acot") )
			rezultat=stupnjevi_izlaz( Matematika.acot( operand ) );
		else if ( fn.equals("asec") )
			rezultat=stupnjevi_izlaz( Matematika.asec( operand ) );
		else if ( fn.equals("acsc") )
			rezultat=stupnjevi_izlaz( Matematika.acsc( operand ) );

      // 3. Hyperbolic

		if ( fn.equals("sinh") )
			rezultat=Matematika.sinh( operand );
		else if ( fn.equals("cosh") )
			rezultat=Matematika.cosh( operand );
		else if ( fn.equals("tanh") )
			rezultat=Matematika.tanh( operand );
		else if ( fn.equals("coth") )
			rezultat=Matematika.coth( operand );
		else if ( fn.equals("sech") )
			rezultat=Matematika.sech( operand );
		else if ( fn.equals("csch") )
			rezultat=Matematika.csch( operand );

      // 4. Inverse hyperbolic

		else if ( fn.equals("asinh") )
			rezultat=Matematika.asinh( operand );
		else if ( fn.equals("acosh") )
			rezultat=Matematika.acosh( operand );
		else if ( fn.equals("atanh") )
			rezultat=Matematika.atanh( operand );
		else if ( fn.equals("acoth") )
			rezultat=Matematika.acoth( operand );
		else if ( fn.equals("asech") )
			rezultat=Matematika.asech( operand );
		else if ( fn.equals("acsch") )
			rezultat=Matematika.acsch( operand );

      // 5. Exponential

		else if ( fn.equals("log10") )
			rezultat=Matematika.log10( operand );
		else if ( fn.equals("log2") )
			rezultat=Matematika.log2( operand );
		else if ( fn.equals("log") )
			rezultat=Math.log( operand );
		else if ( fn.equals("exp10") )
			rezultat=Matematika.exp10( operand );
		else if ( fn.equals("exp2") )
			rezultat=Matematika.exp2( operand );
		else if ( fn.equals("exp") )
			rezultat=Math.exp( operand );
		else if ( fn.equals("sqrt") )
			rezultat=Math.sqrt( operand );
		else if ( fn.equals("cur") )
			rezultat=Matematika.cur( operand );

      // 6. Combinatoric
		else if ( fn.equals("comb") )
			{
			long operand2;
			t=(Token)tokeni.elementAt( pozicija_funkcije+4 );
   		operand2=(long)vrijednost_operanda( t );
			rezultat=Matematika.comb( (long)operand, operand2 );
			}
		else if ( fn.equals("combr") )
			{
			long operand2;
			t=(Token)tokeni.elementAt( pozicija_funkcije+4 );
   		operand2=(long)vrijednost_operanda( t );
			rezultat=Matematika.combr( (long)operand, operand2 );
			}
		else if ( fn.equals("perm") )
			rezultat=Matematika.perm( (long)operand );
		else if ( fn.equals("permr") )
			{
			long operand2;
			t=(Token)tokeni.elementAt( pozicija_funkcije+4 );
   		operand2=(long)vrijednost_operanda( t );
			rezultat=Matematika.permr( (long)operand, operand2 );
			}
		else if ( fn.equals("var") )
			{
			long operand2;
			t=(Token)tokeni.elementAt( pozicija_funkcije+4 );
   		operand2=(long)vrijednost_operanda( t );
			rezultat=Matematika.var( (long)operand, operand2 );
			}
		else if ( fn.equals("varr") )
			{
			long operand2;
			t=(Token)tokeni.elementAt( pozicija_funkcije+4 );
   		operand2=(long)vrijednost_operanda( t );
			rezultat=Matematika.varr( (long)operand, operand2 );
			}

      // 7. Statistical
		else if ( fn.equals("sum") )
			rezultat = Matematika.sum( tokeni, pozicija_funkcije );
		else if ( fn.equals("avg") )
		   rezultat = Matematika.avg( tokeni, pozicija_funkcije );
		else if ( fn.equals("min") )
			rezultat = Matematika.min( tokeni, pozicija_funkcije );
		else if ( fn.equals("max") )
			rezultat = Matematika.max( tokeni, pozicija_funkcije );
		else if ( fn.equals("stddev") )
			rezultat = Matematika.stddev( tokeni, pozicija_funkcije );
		else if ( fn.equals("count") )
			rezultat = Matematika.count( tokeni, pozicija_funkcije );

      // 8. Other
		else if ( fn.equals("abs") )
			rezultat=Math.abs( operand );
		else if ( fn.equals("ceil") )
			rezultat=Math.ceil( operand );
		else if ( fn.equals("fact") )
			rezultat=Matematika.fact( (long)operand );
		else if ( fn.equals("floor") )
			rezultat=Math.floor( operand );
		else if ( fn.equals("pow") )
			{
			double operand2;
			t=(Token)tokeni.elementAt( pozicija_funkcije+4 );
			operand2=vrijednost_operanda( t );
			// rezultat=Math.pow( operand, operand2 );
			rezultat=Matematika.potenciranje( operand, operand2 );
		}else if (fn.equals("random") || fn.equals("rnd")){
			rezultat=Math.random() * operand;
		}
		else if ( fn.equals("round")){
			byte operand2;
			t=(Token)tokeni.elementAt( pozicija_funkcije+4 );
   		operand2=(byte)vrijednost_operanda( t );
         String s=Tools.round( formatirani_broj( operand ), operand2 );
         rezultat=Tokenizacija.string_to_double( s );
        }else if ( fn.equals("trunc") )
         {
			byte operand2;
			t=(Token)tokeni.elementAt( pozicija_funkcije+4 );
   		operand2=(byte)vrijednost_operanda( t );
         String s=Tools.trunc( formatirani_broj( operand ), operand2 );
         rezultat=Tokenizacija.string_to_double( s );
         }
		else if ( fn.equals("sign") )
			rezultat=Matematika.sign( operand );
		else if ( fn.equals("frac") )
			rezultat=Matematika.frac( operand );
		else if ( fn.equals("hypot") )
			{
			double operand2;
			t=(Token)tokeni.elementAt( pozicija_funkcije+4 );
   		operand2=vrijednost_operanda( t );
			rezultat=Matematika.hypot( operand, operand2 );
			}
		else if ( fn.equals("deg") )
			rezultat=Math.toDegrees ( operand );
		else if ( fn.equals("rad") )
			rezultat=Math.toRadians ( operand );

		return rezultat;
   	}

   // provjera : da
	//结果
	public static Vector rezultat( Vector tokeni )
   	{
		Vector	v = new Vector();
		String 	medurezultat;

      izbacivanje_visestrukih_zagrada( tokeni );

      for ( int j=0; j<tokeni.size(); j++ )
         izbacivanje_zagrada( tokeni, j );

		// postepeno racunanje izraza
		do
			{
			medurezultat = spoj_tokena( tokeni );
			v.addElement( medurezultat );
			}
		while ( korak_izracunavanja( tokeni ) );

		Token t=(Token)tokeni.elementAt( 0 );
      if ( t.oznaka!='D' )
         {
			double x=vrijednost_operanda( t );
   		tokeni.setElementAt( new Token( new Double(x), 'D', 0, 0 ), 0 );
   		v.setElementAt( spoj_tokena( tokeni ), 0 );
         }

		return v;
   	}

	// provjera : da
	public static Vector ispis_tokena( Vector tokeni )
   	{
		Vector rezultat=new Vector();
      String izraz=null;
		Token t;

		for ( int i=0; i<tokeni.size(); i++ )
			{
			t=(Token)tokeni.elementAt( i );
			izraz=(t.token).toString();
   		if ( izraz.endsWith( ".0" ) )
     			izraz=izraz.substring( 0, izraz.length()-2 );
         izraz=izraz + " ; " + Provjera_izraza.naziv_tokena( t.oznaka );
         rezultat.addElement( izraz );
         izraz=null;
			}
		return rezultat;
   	}

   public static int dodjeljivanje( String s )
      {
      int i=0;
      try
         {
         while ( Character.isDigit( s.charAt( i ) ) ||
            Character.isUpperCase( s.charAt( i ) ) ||
            Character.isWhitespace( s.charAt( i ) ))
            i++;
         if ( s.charAt( i )=='=' && s.charAt( i+1 )!='=' &&
            Character.isUpperCase( s.charAt( 0 ) ) )
            return i;
         }
      catch ( Exception e )
         {
         return 0;
         }
      return 0;
      }

   } // kraj klase