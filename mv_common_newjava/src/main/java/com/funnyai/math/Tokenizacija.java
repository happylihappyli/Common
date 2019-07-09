/*

provjera : da

TOKENI
======
Vrsta                      Oznaka      Tip         Funkcija
--------------------------------------------------------------------------------
Brojevi          				D           Double      decimalni_broj
Binarni brojevi            B           String      binarni_broj
Heksadecimalni brojevi     H           String      heksadecimalni_broj
Oktalni brojevi            O           String      oktalni_broj
Varijable                  V           Varijabla   varijabla
Funkcije                   F           Funkcija    funkcija
Operatori                  P           Operator    operator
Otvorena zagrada           (           String
Zatvorena zagrada          )           String
Zarez                      Z           String
Greska                     G           String

 */
package com.funnyai.math;

import java.util.ArrayList;
import java.util.Vector;

final public class Tokenizacija {

    // ako se na pocetku stringa prepozna binarni broj vraca se njegova
    // duzina, inace vraca 0
    // provjera : da
    public static int binarni_broj(String s) {
        int duzina = 2;
        int ukupna_duzina = s.length();

        if (ukupna_duzina < 3) {
            return 0;
        }

        if (s.substring(0, 2).equals("#b") || s.substring(0, 2).equals("#B")) {
            while (duzina < ukupna_duzina && je_binarna_znamenka(s.charAt(duzina))) {
                duzina++;
            }
        }

        if (duzina == 2) {
            return 0;
        } else {
            return duzina;
        }
    }

    // ako se na pocetku stringa prepozna oktalni broj vraca se njegova
    // duzina, inace vraca 0
    // provjera : da
    public static int oktalni_broj(String s) {
        int duzina = 2;
        int ukupna_duzina = s.length();

        if (ukupna_duzina < 3) {
            return 0;
        }

        if (s.substring(0, 2).equals("#o") || s.substring(0, 2).equals("#O")) {
            while (duzina < ukupna_duzina && je_oktalna_znamenka(s.charAt(duzina))) {
                duzina++;
            }
        }

        if (duzina == 2) {
            return 0;
        } else {
            return duzina;
        }
    }

    // ako se na pocetku stringa prepozna heksadecimalni broj vraca se njegova
    // duzina, inace vraca 0
    // provjera : da
    public static int heksadecimalni_broj(String s) {
        int duzina = 2;
        int ukupna_duzina = s.length();

        if (ukupna_duzina < 3) {
            return 0;
        }

        if (s.substring(0, 2).equals("#h") || s.substring(0, 2).equals("#H")) {
            while (duzina < ukupna_duzina && je_heksadecimalna_znamenka(s.charAt(duzina))) {
                duzina++;
            }
        }

        if (duzina == 2) {
            return 0;
        } else {
            return duzina;
        }
    }

    // provjera : da
    public static boolean je_heksadecimalna_znamenka(char c) {
        if (Character.isDigit(c)) {
            return true;
        }
        if ('A' <= upper(c) && upper(c) <= 'F') {
            return true;
        }

        return false;
    }

    // provjera : da
    public static boolean je_oktalna_znamenka(char c) {
        if ('0' <= c && c <= '7') {
            return true;
        } else {
            return false;
        }
    }

    // provjera : da
    public static boolean je_binarna_znamenka(char c) {
        if (c == '0' || c == '1') {
            return true;
        } else {
            return false;
        }
    }

    private static boolean je_decimalna_znamenka(char znak) {
        if (Character.isDigit(znak) || upper(znak) == 'E'
                || znak == '.' || znak == '+' || znak == '-') {
            return true;
        } else {
            return false;
        }
    }

    // ako se na pocetku stringa prepozna decimalni broj vraca se njegova
    // duzina, inace vraca 0
    // provjera : da
    public static int decimalni_broj(String izraz) {
        double broj;
        String podizraz;
        int duzina_podizraza;
        int duzina = 0;
        int i, j;
        char zadnji_znak;

        for (j = 0; j < izraz.length(); j++) {
            if (!je_decimalna_znamenka(izraz.charAt(j))) {
                break;
            }
        }

        for (i = j; i > 0; i--) {
            podizraz = izraz.substring(0, i);
            // podizraz=podizraz.trim();
            duzina_podizraza = podizraz.length();
            try {
                broj = string_to_double(podizraz);

                // provjera da li je na kraju broja oznaka 'f' (float)
                // ili 'd' (double)
                zadnji_znak = podizraz.charAt(duzina_podizraza - 1);
                if (upper(zadnji_znak) == 'F' || upper(zadnji_znak) == 'D') {
                    duzina = duzina_podizraza - 1;
                } else {
                    duzina = duzina_podizraza;
                }
                break;
            } catch (NumberFormatException e) {
            }
        }
        return duzina;
    }

    // provjera	: da
    private static char lower(char znak) {
        return Character.toLowerCase(znak);
    }

    // provjera	: da
    private static char upper(char znak) {
        return Character.toUpperCase(znak);
    }

    // provjera	: da
    public static double string_to_double(String s) {
        return Double.valueOf(s).doubleValue();
    }

    // provjera	: da
    public static Variable varijabla(String izraz, ArrayList varijable) {
        int i;
        int duzina_varijable;
        Variable v;

        for (i = 0; i < varijable.size(); i++) {
            v = (Variable) varijable.get(i);
            duzina_varijable = v.varijabla.length();

            if (duzina_varijable > izraz.length()) {
                continue;
            }

            if (izraz.substring(0, duzina_varijable).equals(v.varijabla)) {
                return v;
            }
        }
        return null;
    }

    // provjera	: da
    public static Function funkcija(String izraz, ArrayList funkcije) {
        int i;
        int duzina_funkcije;
        Function f;

        for (i = 0; i < funkcije.size(); i++) {
            f = (Function) funkcije.get(i);
            duzina_funkcije = f.funkcija.length();

            if (duzina_funkcije > izraz.length()) {
                continue;
            }

            if (izraz.substring(0, duzina_funkcije).equals(f.funkcija)) {
                return f;
            }
        }
        return null;
    }

    // provjera	: da
    public static Operator operator(String izraz, ArrayList<Operator> operatori) {
        int i;
        int duzina_operatora;
        Operator o;

        for (i = 0; i < operatori.size(); i++) {
            o = operatori.get(i);
            duzina_operatora = o.operator.length();

            if (duzina_operatora > izraz.length()) {
                continue;
            }

            if (izraz.substring(0, duzina_operatora).equals(o.operator)) {
                return o;
            }
        }
        return null;
    }

    //	拆卸的表达
    public static ArrayList<Token> rastavljanje_izraza(
            String s,
            ArrayList varijable,
            ArrayList operatori,
            ArrayList funkcije) {
        ArrayList<Token> tokeni = new ArrayList<>();
        int duzina;
        boolean zastavica = false;
        boolean status;
        int pozicija = 0;

        while (!s.equals("")) {
            // u izrazu mogu biti i praznine
            pozicija = pozicija + (s.length() - s.trim().length());
            s = s.trim();

            // HEKSADECIMALNI BROJ
            duzina = Tokenizacija.heksadecimalni_broj(s);
            if (duzina > 0) {
                String dio = s.substring(0, duzina);
                tokeni.add(new Token(dio, 'H', pozicija, duzina));
                s = s.substring(duzina);
                zastavica = true;
                pozicija = pozicija + duzina;
                continue;
            }

            // OKTALNI BROJ
            duzina = Tokenizacija.oktalni_broj(s);
            if (duzina > 0) {
                String dio = s.substring(0, duzina);
                tokeni.add(new Token(dio, 'O', pozicija, duzina));
                s = s.substring(duzina);
                zastavica = true;
                pozicija = pozicija + duzina;
                continue;
            }

            // BINARNI BROJ
            duzina = Tokenizacija.binarni_broj(s);
            if (duzina > 0) {
                String dio = s.substring(0, duzina);
                tokeni.add(new Token(dio, 'B', pozicija, duzina));
                s = s.substring(duzina);
                zastavica = true;
                pozicija = pozicija + duzina;
                continue;
            }

            // DECIMALNI ILI CIJELI BROJ
            duzina = decimalni_broj(s);
            if ((duzina > 0 && !zastavica)
                    || (duzina > 0 && s.charAt(0) != '-' && s.charAt(0) != '+')) {
                String dio = s.substring(0, duzina);
                Double broj = new Double(string_to_double(dio));
                tokeni.add(new Token(broj, 'D', pozicija, duzina));

                s = s.substring(duzina);
                zastavica = true;
                pozicija = pozicija + duzina;
                continue;
            }

            // VARIJABLA
            Variable v = Tokenizacija.varijabla(s, varijable);
            if (v != null) {
                duzina = v.varijabla.length();
                tokeni.add(new Token(v, 'V', pozicija, duzina));
                s = s.substring(duzina);
                zastavica = true;
                pozicija = pozicija + duzina;
                continue;
            }

            // FUNKCIJA
            Function f = Tokenizacija.funkcija(s, funkcije);
            if (f != null) {
                duzina = f.funkcija.length();
                tokeni.add(new Token(f, 'F', pozicija, duzina));
                s = s.substring(duzina);
                zastavica = true;
                pozicija = pozicija + duzina;
                continue;
            }

            // OPERATOR
            Operator o = Tokenizacija.operator(s, operatori);
            if (o != null) {
                duzina = o.operator.length();
                tokeni.add(new Token(o, 'P', pozicija, duzina));
                s = s.substring(duzina);
                zastavica = false;
                pozicija = pozicija + duzina;
                continue;
            }

            // OTVORENA ZAGRADA
            status = Provjera_zagrada.je_otvorena_zagrada(s.charAt(0));
            if (status) {
                String dio = s.substring(0, 1);
                tokeni.add(new Token(dio, '(', pozicija, 1));
                s = s.substring(1);
                zastavica = false;
                pozicija = pozicija + 1;
                continue;
            }

            // ZATVORENA ZAGRADA
            status = Provjera_zagrada.je_zatvorena_zagrada(s.charAt(0));
            if (status) {
                String dio = s.substring(0, 1);
                tokeni.add(new Token(dio, ')', pozicija, 1));
                s = s.substring(1);
                zastavica = true;
                pozicija = pozicija + 1;
                continue;
            }

            // ZAREZ
            if (s.charAt(0) == ',') {
                String dio = ",";
                tokeni.add(new Token(dio, 'Z', pozicija, 1));
                s = s.substring(1);
                zastavica = false;
                pozicija = pozicija + 1;
                continue;
            }

            tokeni.add(new Token(s.substring(0, 1), 'G', pozicija, 1));
            zastavica = false;
            s = s.substring(1);
            pozicija = pozicija + 1;

        } // kraj-while

        return tokeni;

    }

} // kraj klase Tokenizacija
