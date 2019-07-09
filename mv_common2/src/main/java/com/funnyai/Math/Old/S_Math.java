package com.funnyai.Math.Old;

import java.text.DecimalFormat;
import java.util.Vector;

//import com.Funny.Common.DataStructures.Random;

public class S_Math {

	public static boolean bInitCalculate=false;
	public static Vector<Varijabla> varijable=new Vector<Varijabla>();
	public static Vector<Operator> operatori=new Vector<Operator>();
	public static Vector<Function> pfun=new Vector<Function>();
	
	public static double Rnd(){
		//Random pRnd=new Random();
		
		return Math.random();//pRnd.random0_1();
	}
        
        public static boolean isNumeric(String str){  
            for (int i = str.length(); --i >= 0;) {
                if (!Character.isDigit(str.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
	
	public static  String currencyFormat(double db){
		String str=db+"";
        DecimalFormat   fmt=new  DecimalFormat("##,###,###,###,##0.00");     
        String outStr = null;   
        double d;   
        try {   
            d = Double.parseDouble(str);   
            outStr = fmt.format(d);   
        } catch (Exception e) {   
        }   
        return outStr;   
    }
	
	public static  String currencyFormat(String str){   
        DecimalFormat   fmt=new  DecimalFormat("##,###,###,###,##0.00");     
        String outStr = null;   
        double d;   
        try {   
            d = Double.parseDouble(str);   
            outStr = fmt.format(d);   
        } catch (Exception e) {   
        }   
        return outStr;   
    }
	
	public static void Init(){
		
		if (bInitCalculate==false){
			varijable=new Vector<Varijabla>();
			operatori=new Vector<Operator>();
			pfun=new Vector<Function>();
	   
			bInitCalculate=true;
			
			varijable.addElement( new Varijabla( "TB", 1099511627776L ) );
		    varijable.addElement( new Varijabla( "STEFAN_BOLTZMANN", 5.6704E-8 ) );
		    varijable.addElement( new Varijabla( "SPEED_OF_LIGHT", 299792458 ) );
		    varijable.addElement( new Varijabla( "RYDBERG", 10973731.568549 ) );
		    varijable.addElement( new Varijabla( "PYTHAGORAS", Math.sqrt(2) ) );
		    varijable.addElement( new Varijabla( "PROTON_MASS", 1.67262158E-27 ) );
		    varijable.addElement( new Varijabla( "PROTON_ELECTRON_MASS_RATIO", 1836.1526675 ) );
		    varijable.addElement( new Varijabla( "PLANCK", 6.62606876E-34 ) );
		    varijable.addElement( new Varijabla( "PI", Math.PI ) );
		    varijable.addElement( new Varijabla( "NEWTON", 6.673E-11 ) );
		    varijable.addElement( new Varijabla( "MOLAR_GAS", 8.314472 ) );
		    varijable.addElement( new Varijabla( "MIN_INT", Integer.MIN_VALUE ) );
		    varijable.addElement( new Varijabla( "MIN_DEC", -Double.MAX_VALUE ) );
		    varijable.addElement( new Varijabla( "MB", 1048576 ) );
		    varijable.addElement( new Varijabla( "MAX_INT", Integer.MAX_VALUE ) );
		    varijable.addElement( new Varijabla( "MAX_DEC", Double.MAX_VALUE ) );
		    varijable.addElement( new Varijabla( "MAGNETIC_FLUX", 2.067833636E-15 ) );
		    varijable.addElement( new Varijabla( "MAGNETIC", 4E-7 * Math.PI ) );
		    varijable.addElement( new Varijabla( "MADELUNG", 2.0531987328 ) );
		    varijable.addElement( new Varijabla( "KHINTCHINE", 2.685452001 ) );
		    varijable.addElement( new Varijabla( "KB", 1024 ) );
		    varijable.addElement( new Varijabla( "INVERSE_FINE_STRUCTURE", 137.03599976 ) );
		    varijable.addElement( new Varijabla( "IMAGINARY_UNIT", Math.sqrt(-1) ) );
		    varijable.addElement( new Varijabla( "GRAVITY", 9.80665 ) );
		    varijable.addElement( new Varijabla( "GB", 1073741824 ) );
		    varijable.addElement( new Varijabla( "FINE_STRUCTURE", 7.297352533E-3 ) );
		    varijable.addElement( new Varijabla( "FEIGENBAUM", 2.50290787509589282228390287272909 ) );
		    varijable.addElement( new Varijabla( "FARADAY", 96485.3415 ) );
		    varijable.addElement( new Varijabla( "EULER", 0.57721566490153286060651209008240243104215933593992 ) );
		    varijable.addElement( new Varijabla( "ELEMENTARY_CHARGE", 1.602176462E-19 ) );
		    varijable.addElement( new Varijabla( "ELECTRON_VOLT", 1.602176462E-19 ) );
		    varijable.addElement( new Varijabla( "ELECTRON_MASS", 9.10938188E-31 ) );
		    varijable.addElement( new Varijabla( "ELECTRIC_CONSTANT", 8.854187817E-12 ) );
		    varijable.addElement( new Varijabla( "E", Math.E ) );
		    varijable.addElement( new Varijabla( "CONDUCTANCE_QUANTUM", 7.748091696E-5 ) );
		    varijable.addElement( new Varijabla( "CATALAN", 0.915965594 ) );
		    varijable.addElement( new Varijabla( "BOLTZMANN", 1.3806503E-23 ) );
		    varijable.addElement( new Varijabla( "AVOGADRO", 6.02214199E+23 ) );
		    varijable.addElement( new Varijabla( "ATOMIC_MASS", 1.66053873E-27 ) );
		    varijable.addElement( new Varijabla( "APERY", 1.20205690315959428539973816151144999076498629234049 ) );
	
			operatori.addElement( new Operator( "**",     (byte)1 ) );
			operatori.addElement( new Operator( "^",      (byte)1 ) );
			      
			operatori.addElement( new Operator( "||",     (byte)11 ) );
			operatori.addElement( new Operator( "xor",    (byte)8 ) );
			operatori.addElement( new Operator( "or",     (byte)9 ) );
			operatori.addElement( new Operator( "mod",    (byte)2 ) );
			operatori.addElement( new Operator( "and",    (byte)7 ) );
			  
			operatori.addElement( new Operator( ">>>",    (byte)4 ) );
			operatori.addElement( new Operator( ">>",     (byte)4 ) );
			operatori.addElement( new Operator( ">=",     (byte)5 ) );
			operatori.addElement( new Operator( ">",      (byte)5 ) );
			operatori.addElement( new Operator( "==",     (byte)6 ) );
			operatori.addElement( new Operator( "<>",     (byte)6 ) );
			operatori.addElement( new Operator( "<=",     (byte)5 ) );
			operatori.addElement( new Operator( "<<",     (byte)4 ) );
			operatori.addElement( new Operator( "<",      (byte)5 ) );
			operatori.addElement( new Operator( "/",      (byte)2 ) );
			operatori.addElement( new Operator( "-",      (byte)3 ) );
			operatori.addElement( new Operator( "+",      (byte)3 ) );
			operatori.addElement( new Operator( "*",      (byte)2 ) );
			operatori.addElement( new Operator( "&&",     (byte)10 ) );
			operatori.addElement( new Operator( "%",      (byte)2 ) );
			operatori.addElement( new Operator( "!=",     (byte)6 ) );
	
	
		      pfun.addElement( new Function("varr", 2) );
		      pfun.addElement( new Function("var", 2) );
		      pfun.addElement( new Function("trunc", 2) );
		      pfun.addElement( new Function("tanh", 1) );
		      pfun.addElement( new Function("tan", 1) );
		      pfun.addElement( new Function("sum", 0) );
		      pfun.addElement( new Function("stddev", 0) );
		      pfun.addElement( new Function("sqrt", 1) );
		      pfun.addElement( new Function("sinh", 1) );
		      pfun.addElement( new Function("sin", 1) );
		      pfun.addElement( new Function("sign", 1) );
		      pfun.addElement( new Function("sech", 1) );
		      pfun.addElement( new Function("sec", 1) );
		      pfun.addElement( new Function("round", 2) );
		      pfun.addElement( new Function("rnd", 1) );
		      pfun.addElement( new Function("random", 1) );		      
		      pfun.addElement( new Function("rad", 1) );
		      pfun.addElement( new Function("pow", 2) );
		      pfun.addElement( new Function("permr", 2) );
		      pfun.addElement( new Function("perm", 1) );
		      pfun.addElement( new Function("min", 0) );
		      pfun.addElement( new Function("max", 0) );
		      pfun.addElement( new Function("log2", 1) );
		      pfun.addElement( new Function("log10", 1) );
		      pfun.addElement( new Function("log", 1) );
		      pfun.addElement( new Function("hypot", 2) );
		      pfun.addElement( new Function("frac", 1) );
		      pfun.addElement( new Function("floor", 1) );
		      pfun.addElement( new Function("fact", 1) );
		      pfun.addElement( new Function("exp2", 1) );
		      pfun.addElement( new Function("exp10", 1) );
		      pfun.addElement( new Function("exp", 1) );
		      pfun.addElement( new Function("deg", 1) );
		      pfun.addElement( new Function("cur", 1) );
		      pfun.addElement( new Function("csch", 1) );
		      pfun.addElement( new Function("csc", 1) );
		      pfun.addElement( new Function("count", 0) );
		      pfun.addElement( new Function("coth", 1) );
		      pfun.addElement( new Function("cot", 1) );
		      pfun.addElement( new Function("cosh", 1) );
		      pfun.addElement( new Function("cos", 1) );
		      pfun.addElement( new Function("combr", 2) );
		      pfun.addElement( new Function("comb", 2) );
		      pfun.addElement( new Function("cint", 1) );
		      pfun.addElement( new Function("ceil", 1) );
		      pfun.addElement( new Function("avg", 0) );
		      pfun.addElement( new Function("atanh", 1) );
		      pfun.addElement( new Function("atan2", 2) );
		      pfun.addElement( new Function("atan", 1) );
		      pfun.addElement( new Function("asinh", 1) );
		      pfun.addElement( new Function("asin", 1) );
		      pfun.addElement( new Function("asech", 1) );
		      pfun.addElement( new Function("asec", 1) );
		      pfun.addElement( new Function("acsch", 1) );
		      pfun.addElement( new Function("acsc", 1) );
		      pfun.addElement( new Function("acoth", 1) );
		      pfun.addElement( new Function("acot", 1) );
		      pfun.addElement( new Function("acosh", 1) );
		      pfun.addElement( new Function("acos", 1) );
		      pfun.addElement( new Function("abs", 1) );
			
		}
	}
	
	public static void main(String[] args){
		Init();
		
		String s="1<2";//1+2*3+sin(1+cos(1))";
		Vector tokeni=Tokenizacija.rastavljanje_izraza (s, varijable,operatori, pfun );
		Vector rez=Calculate.rezultat(tokeni);
		String x=((String)rez.elementAt(rez.size()-1)).trim();
		System.out.println(x);
	}
	
	public static String Calculate(String strInput){
		Init();
		Vector tokeni=Tokenizacija.rastavljanje_izraza(
				strInput, varijable,operatori, pfun);
		Vector rez=Calculate.rezultat(tokeni);
		String strReturn=((String)rez.elementAt(rez.size()-1)).trim();
		
		return strReturn;
	}
}
