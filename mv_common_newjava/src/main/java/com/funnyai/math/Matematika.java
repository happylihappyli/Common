package com.funnyai.math;

import java.util.Vector;
import java.math.BigDecimal;
import java.util.ArrayList;

final public class Matematika {

    public static double potenciranje(double a, double n) {
        if ((int) n != n) {
            return Math.pow(a, n);
        }

        double rezultat = 1;

        for (int i = 1; i <= n; i++) {
            rezultat = mnozenje(rezultat, a);
            if (Double.isInfinite(rezultat)) {
                break;
            }
        }

        return rezultat;
    }

    public static double oduzimanje(double a, double b) {
        double rezultat;

        try {
            BigDecimal x = new BigDecimal(Calculate.formatirani_broj(a));
            BigDecimal y = new BigDecimal(Calculate.formatirani_broj(b));
            rezultat = x.subtract(y).doubleValue();
        } catch (Exception e) {
            rezultat = a - b;
        }

        return rezultat;
    }

    public static double zbrajanje(double a, double b) {
        double rezultat;

        try {
            BigDecimal x = new BigDecimal(Calculate.formatirani_broj(a));
            BigDecimal y = new BigDecimal(Calculate.formatirani_broj(b));
            rezultat = x.add(y).doubleValue();
        } catch (Exception e) {
            rezultat = a + b;
        }

        return rezultat;
    }

    public static double mnozenje(double a, double b) {
        double rezultat;

        try {
            BigDecimal x = new BigDecimal(Calculate.formatirani_broj(a));
            BigDecimal y = new BigDecimal(Calculate.formatirani_broj(b));
            rezultat = x.multiply(y).doubleValue();

            double provjera = a * b;
            if (String.valueOf(provjera).length() < String.valueOf(rezultat).length()) {
                rezultat = provjera;
            }
        } catch (Exception e) {
            rezultat = a * b;
        }

        return rezultat;
    }

    public static double dijeljenje(double a, double b) {
        double rezultat;

        try {
            BigDecimal x = new BigDecimal(Calculate.formatirani_broj(a));
            BigDecimal y = new BigDecimal(Calculate.formatirani_broj(b));
            rezultat = x.divide(y, 17, BigDecimal.ROUND_UNNECESSARY).doubleValue();
        } catch (Exception e) {
            rezultat = a / b;
        }

        return rezultat;
    }

    public static double deg_to_rad(double x) {
        return Math.toRadians(x);
    }

    public static double deg_to_grad(double x) {
        return x * 10 / 9;
    }

    public static double rad_to_deg(double x) {
        return Math.toDegrees(x);
    }

    public static double rad_to_grad(double x) {
        return Math.toDegrees(x) * 10 / 9;
    }

    public static double grad_to_deg(double x) {
        return x * 9 / 10;
    }

    public static double grad_to_rad(double x) {
        return Math.toRadians(x * 9 / 10);
    }

    public static double sign(double x) {
        if (x < 0) {
            return -1;
        } else if (x > 0) {
            return 1;
        }
        return 0;
    }

    public static double hypot(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }

    public static double frac(double x) {
        return oduzimanje(Math.abs(x), Math.rint(Math.abs(x) - 0.5));
    }

    public static double cot(double x) {
        return 1 / Math.tan(x);
    }

    public static double sec(double x) {
        return 1 / Math.cos(x);
    }

    public static double csc(double x) {
        return 1 / Math.sin(x);
    }

    public static double acot(double x) {
        return Math.atan(1 / x);
    }

    public static double asec(double x) {
        return Math.acos(1 / x);
    }

    public static double acsc(double x) {
        return Math.asin(1 / x);
    }

    public static double sinh(double x) {
        return (Math.pow(Math.E, x) - Math.pow(Math.E, -x)) / 2;
    }

    public static double cosh(double x) {
        return (Math.pow(Math.E, x) + Math.pow(Math.E, -x)) / 2;
    }

    public static double tanh(double x) {
        return sinh(x) / cosh(x);
    }

    public static double coth(double x) {
        return cosh(x) / sinh(x);
    }

    public static double sech(double x) {
        return 2 / (Math.pow(Math.E, x) + Math.pow(Math.E, -x));
    }

    public static double csch(double x) {
        return 2 / (Math.pow(Math.E, x) - Math.pow(Math.E, -x));
    }

    public static double asinh(double x) {
        return Math.log(x + Math.sqrt(x * x + 1));
    }

    public static double acosh(double x) {
        return Math.log(x + Math.sqrt(x * x - 1));
    }

    public static double atanh(double x) {
        return Math.log((1 + x) / (1 - x)) / 2;
    }

    public static double acoth(double x) {
        return atanh(1 / x);
    }

    public static double asech(double x) {
        return acosh(1 / x);
    }

    public static double acsch(double x) {
        return asinh(1 / x);
    }

    public static double log10(double x) {
        return Math.log(x) / Math.log(10);
    }

    public static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    public static double exp10(double x) {
        return Math.pow(10, x);
    }

    public static double exp2(double x) {
        return Math.pow(2, x);
    }

    public static double cur(double x) {
        return Math.pow(x, 1. / 3.);
    }

    // vraca faktorijel (n!)
    // provjera : da
    public static double fact(long n) {
        double rezultat = 1;
        int i;

        for (i = 2; i <= n; i++) {
            rezultat = rezultat * i;
            if (Double.isInfinite(rezultat)) {
                break;
            }
        }

        return rezultat;
    }

    public static double comb(long n, long r) {
        return fact(n) / (fact(r) * fact(n - r));
    }

    public static double combr(long n, long r) {
        return comb(n + r - 1, r);
    }

    public static double perm(long n) {
        return fact(n);
    }

    public static double permr(long n, long r) {
        return fact(n) / fact(r);
    }

    public static double var(long n, long r) {
        return comb(n, r) * fact(r);
    }

    public static double varr(long n, long r) {
        return Math.pow(n, r);
    }

    public static double max(ArrayList<Token> tokeni, int pozicija_funkcije) {
        int i;
        double broj;
        double maksimum;
        Token t;
        int kraj;

        // pozicija prvog argumenta
        i = pozicija_funkcije + 2;

        // prvi argument je pocetni maksimum
        t = (Token) tokeni.get(i);
        maksimum = Calculate.vrijednost_operanda(t);

        kraj = Provjera_izraza.kraj_funkcije(tokeni, pozicija_funkcije);

        for (i = i + 2; i < kraj; i = i + 2) {
            t = (Token) tokeni.get(i);
            broj = Calculate.vrijednost_operanda(t);
            if (broj > maksimum) {
                maksimum = broj;
            }
        }

        return maksimum;
    }

    public static double min(ArrayList<Token> tokeni, int pozicija_funkcije) {
        int i;
        double broj;
        double minimum;
        Token t;
        int kraj;

        // pozicija prvog argumenta
        i = pozicija_funkcije + 2;

        // prvi argument je pocetni minimum
        t = (Token) tokeni.get(i);
        minimum = Calculate.vrijednost_operanda(t);

        kraj = Provjera_izraza.kraj_funkcije(tokeni, pozicija_funkcije);

        for (i = i + 2; i < kraj; i = i + 2) {
            t = (Token) tokeni.get(i);
            broj = Calculate.vrijednost_operanda(t);
            if (broj < minimum) {
                minimum = broj;
            }
        }

        return minimum;
    }

    public static double sum(ArrayList<Token> tokeni, int pozicija_funkcije) {
        int i;
        double broj;
        double suma = 0;
        Token t;
        int kraj;

        kraj = Provjera_izraza.kraj_funkcije(tokeni, pozicija_funkcije);

        for (i = pozicija_funkcije + 2; i < kraj; i = i + 2) {
            t = (Token) tokeni.get(i);
            broj = Calculate.vrijednost_operanda(t);
            suma = zbrajanje(suma, broj);
        }
        return suma;
    }

    public static double avg(ArrayList<Token> tokeni, int pozicija_funkcije) {
        int i;
        int n = 0;
        double broj;
        double suma = 0;
        Token t;
        int kraj;

        kraj = Provjera_izraza.kraj_funkcije(tokeni, pozicija_funkcije);

        for (i = pozicija_funkcije + 2; i < kraj; i = i + 2) {
            t = (Token) tokeni.get(i);
            broj = Calculate.vrijednost_operanda(t);
            suma = zbrajanje(suma, broj);
            n = n + 1;
        }
        return dijeljenje(suma, n);
    }

    public static double count(ArrayList<Token> tokeni, int pozicija_funkcije) {
        int i;
        int n = 0;
        Token t;
        int kraj;

        kraj = Provjera_izraza.kraj_funkcije(tokeni, pozicija_funkcije);

        for (i = pozicija_funkcije + 2; i < kraj; i = i + 2) {
            n = n + 1;
        }

        return n;
    }

    public static double stddev(ArrayList<Token> tokeni, int pozicija_funkcije) {
        int i;
        int n = 0;
        double broj;
        double suma = 0;
        double suma_kvadrata = 0;
        Token t;
        int kraj;

        kraj = Provjera_izraza.kraj_funkcije(tokeni, pozicija_funkcije);

        for (i = pozicija_funkcije + 2; i < kraj; i = i + 2) {
            t = (Token) tokeni.get(i);
            broj = Calculate.vrijednost_operanda(t);
            suma = suma + broj;
            suma_kvadrata = suma_kvadrata + broj * broj;
            n = n + 1;
        }
        return Math.sqrt((n * suma_kvadrata - suma * suma) / (n * (n - 1)));
    }

    public static char znamenka(int x) {
        switch (x) {
            case 10:
                return 'A';
            case 11:
                return 'B';
            case 12:
                return 'C';
            case 13:
                return 'D';
            case 14:
                return 'E';
            case 15:
                return 'F';
            default:
                return (char) (x + 48);
        }
    }

    public static String broj_baza(long b, int baza) {
        String s = new String();

        b = Math.abs(b);

        if (b == 0) {
            return "0";
        }

        while (b != 0) {
            s = znamenka((int) (b % baza)) + s;
            b = b / baza;
        }
        return s;
    }

    public static long baza_broj(String s, int baza) {
        long r = 0;
        int i, j;

        for (i = s.length() - 1, j = 0; i >= 0; i--, j++) {
            r = r + tezina_znamenke(s.charAt(i)) * (long) Math.pow(baza, j);
        }

        if (r > 4294967295L) {
            return Integer.MAX_VALUE;
        } else if (r > Integer.MAX_VALUE) {
            return r - 4294967296L;
        } else {
            return r;
        }
    }

    public static int tezina_znamenke(char c) {
        if (Character.isDigit(c)) {
            return c - 48;
        } else if ('A' <= c && c <= 'F') {
            return c - 55;
        } else if ('a' <= c && c <= 'f') {
            return c - 87;
        }
        return -1;
    }

} // kraj klase
