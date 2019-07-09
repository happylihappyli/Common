package com.funnyai.math;

import com.funnyai.data.C_Var_Java;
import com.funnyai.string.S_string;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class S_math {

    public static boolean bInitCalculate = false;
    public static ArrayList<Variable> variable = new ArrayList<>();
    public static ArrayList<Operator> operatori = new ArrayList<>();
    public static ArrayList<Function> pfun = new ArrayList<>();

    public static C_Var_Java sum(C_Var_Java... a) {
        double r = 0;
        for (C_Var_Java a1 : a) {
            String strTmp="0";
            strTmp=(String) a1.pObj;
            r += S_string.getDouble_FromStr(strTmp, 0.0);
        }
        return new C_Var_Java("String",r + "");
    }

    public static double Rnd() {
        //Random pRnd=new Random();

        return Math.random();//pRnd.random0_1();
    }

    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0;) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String currencyFormat(double db) {
        String str = db + "";
        DecimalFormat fmt = new DecimalFormat("##,###,###,###,##0.00");
        String outStr = null;
        double d;
        try {
            d = Double.parseDouble(str);
            outStr = fmt.format(d);
        } catch (Exception e) {
        }
        return outStr;
    }

    public static String currencyFormat(String str) {
        DecimalFormat fmt = new DecimalFormat("##,###,###,###,##0.00");
        String outStr = null;
        double d;
        try {
            d = Double.parseDouble(str);
            outStr = fmt.format(d);
        } catch (Exception e) {
        }
        return outStr;
    }

    public static void Init() {

        if (bInitCalculate == false) {
            variable = new ArrayList<Variable>();
            operatori = new ArrayList<Operator>();
            pfun = new ArrayList<Function>();

            bInitCalculate = true;

            variable.add(new Variable("STEFAN_BOLTZMANN", 5.6704E-8));
            variable.add(new Variable("SPEED_OF_LIGHT", 299792458));
            variable.add(new Variable("RYDBERG", 10973731.568549));
            variable.add(new Variable("PYTHAGORAS", Math.sqrt(2)));
            variable.add(new Variable("PROTON_MASS", 1.67262158E-27));
            variable.add(new Variable("PROTON_ELECTRON_MASS_RATIO", 1836.1526675));
            variable.add(new Variable("PLANCK", 6.62606876E-34));
            variable.add(new Variable("PI", Math.PI));
            variable.add(new Variable("NEWTON", 6.673E-11));
            variable.add(new Variable("MOLAR_GAS", 8.314472));
            variable.add(new Variable("MIN_INT", Integer.MIN_VALUE));
            variable.add(new Variable("MIN_DEC", -Double.MAX_VALUE));
            variable.add(new Variable("MB", 1048576));
            variable.add(new Variable("MAX_INT", Integer.MAX_VALUE));
            variable.add(new Variable("MAX_DEC", Double.MAX_VALUE));
            variable.add(new Variable("MAGNETIC_FLUX", 2.067833636E-15));
            variable.add(new Variable("MAGNETIC", 4E-7 * Math.PI));
            variable.add(new Variable("MADELUNG", 2.0531987328));
            variable.add(new Variable("KHINTCHINE", 2.685452001));
            variable.add(new Variable("KB", 1024));
            variable.add(new Variable("INVERSE_FINE_STRUCTURE", 137.03599976));
            variable.add(new Variable("IMAGINARY_UNIT", Math.sqrt(-1)));
            variable.add(new Variable("GRAVITY", 9.80665));
            variable.add(new Variable("GB", 1073741824));
            variable.add(new Variable("FINE_STRUCTURE", 7.297352533E-3));
            variable.add(new Variable("FEIGENBAUM", 2.50290787509589282228390287272909));
            variable.add(new Variable("FARADAY", 96485.3415));
            variable.add(new Variable("EULER", 0.57721566490153286060651209008240243104215933593992));
            variable.add(new Variable("ELEMENTARY_CHARGE", 1.602176462E-19));
            variable.add(new Variable("ELECTRON_VOLT", 1.602176462E-19));
            variable.add(new Variable("ELECTRON_MASS", 9.10938188E-31));
            variable.add(new Variable("ELECTRIC_CONSTANT", 8.854187817E-12));
            variable.add(new Variable("E", Math.E));
            variable.add(new Variable("CONDUCTANCE_QUANTUM", 7.748091696E-5));
            variable.add(new Variable("CATALAN", 0.915965594));
            variable.add(new Variable("BOLTZMANN", 1.3806503E-23));
            variable.add(new Variable("AVOGADRO", 6.02214199E+23));
            variable.add(new Variable("ATOMIC_MASS", 1.66053873E-27));
            variable.add(new Variable("APERY", 1.20205690315959428539973816151144999076498629234049));

            operatori.add(new Operator("**", (byte) 1));
            operatori.add(new Operator("^", (byte) 1));

            operatori.add(new Operator("||", (byte) 11));
            operatori.add(new Operator("xor", (byte) 8));
            operatori.add(new Operator("or", (byte) 9));
            operatori.add(new Operator("mod", (byte) 2));
            operatori.add(new Operator("and", (byte) 7));

            operatori.add(new Operator(">>>", (byte) 4));
            operatori.add(new Operator(">>", (byte) 4));
            operatori.add(new Operator(">=", (byte) 5));
            operatori.add(new Operator(">", (byte) 5));
            operatori.add(new Operator("==", (byte) 6));
            operatori.add(new Operator("<>", (byte) 6));
            operatori.add(new Operator("<=", (byte) 5));
            operatori.add(new Operator("<<", (byte) 4));
            operatori.add(new Operator("<", (byte) 5));
            operatori.add(new Operator("/", (byte) 2));
            operatori.add(new Operator("-", (byte) 3));
            operatori.add(new Operator("+", (byte) 3));
            operatori.add(new Operator("*", (byte) 2));
            operatori.add(new Operator("&&", (byte) 10));
            operatori.add(new Operator("%", (byte) 2));
            operatori.add(new Operator("!=", (byte) 6));

            pfun.add(new Function("varr", 2));
            pfun.add(new Function("var", 2));
            pfun.add(new Function("trunc", 2));
            pfun.add(new Function("tanh", 1));
            pfun.add(new Function("tan", 1));
            pfun.add(new Function("sum", 0));
            pfun.add(new Function("stddev", 0));
            pfun.add(new Function("sqrt", 1));
            pfun.add(new Function("sinh", 1));
            pfun.add(new Function("sin", 1));
            pfun.add(new Function("sign", 1));
            pfun.add(new Function("sech", 1));
            pfun.add(new Function("sec", 1));
            pfun.add(new Function("round", 2));
            pfun.add(new Function("rnd", 1));
            pfun.add(new Function("random", 1));
            pfun.add(new Function("rad", 1));
            pfun.add(new Function("pow", 2));
            pfun.add(new Function("permr", 2));
            pfun.add(new Function("perm", 1));
            pfun.add(new Function("min", 0));
            pfun.add(new Function("max", 0));
            pfun.add(new Function("log2", 1));
            pfun.add(new Function("log10", 1));
            pfun.add(new Function("log", 1));
            pfun.add(new Function("hypot", 2));
            pfun.add(new Function("frac", 1));
            pfun.add(new Function("floor", 1));
            pfun.add(new Function("fact", 1));
            pfun.add(new Function("exp2", 1));
            pfun.add(new Function("exp10", 1));
            pfun.add(new Function("exp", 1));
            pfun.add(new Function("deg", 1));
            pfun.add(new Function("cur", 1));
            pfun.add(new Function("csch", 1));
            pfun.add(new Function("csc", 1));
            pfun.add(new Function("count", 0));
            pfun.add(new Function("coth", 1));
            pfun.add(new Function("cot", 1));
            pfun.add(new Function("cosh", 1));
            pfun.add(new Function("cos", 1));
            pfun.add(new Function("combr", 2));
            pfun.add(new Function("comb", 2));
            pfun.add(new Function("cint", 1));
            pfun.add(new Function("ceil", 1));
            pfun.add(new Function("avg", 0));
            pfun.add(new Function("atanh", 1));
            pfun.add(new Function("atan2", 2));
            pfun.add(new Function("atan", 1));
            pfun.add(new Function("asinh", 1));
            pfun.add(new Function("asin", 1));
            pfun.add(new Function("asech", 1));
            pfun.add(new Function("asec", 1));
            pfun.add(new Function("acsch", 1));
            pfun.add(new Function("acsc", 1));
            pfun.add(new Function("acoth", 1));
            pfun.add(new Function("acot", 1));
            pfun.add(new Function("acosh", 1));
            pfun.add(new Function("acos", 1));
            pfun.add(new Function("abs", 1));

        }
    }

    public static void main(String[] args) {
        Init();

        String s = "1<2";//1+2*3+sin(1+cos(1))";
        ArrayList<Token> tokeni = Tokenizacija.rastavljanje_izraza(s, variable, operatori, pfun);
        ArrayList rez = Calculate.rezultat(tokeni);
        String x = ((String) rez.get(rez.size() - 1)).trim();
        System.out.println(x);
    }

    public static String calculate(Object... a) {
        String strReturn="";
        if (a.length>0){
            String strInput=(String) a[0];
            Init();
            ArrayList tokeni = Tokenizacija.rastavljanje_izraza(strInput, variable, operatori, pfun);
            ArrayList rez = Calculate.rezultat(tokeni);
            strReturn = ((String) rez.get(rez.size() - 1)).trim();
        }
        return strReturn;
    }
}
