package com.funnyai.math;

import java.util.ArrayList;
import java.util.Vector;

final public class Provjera_izraza {

    // provjera : da
    static final String dozvoljene_kombinacije[] = {
        "DP", "D)", "DZ",
        "BP", "B)", "BZ",
        "HP", "H)", "HZ",
        "OP", "O)", "OZ",
        "VP", "V)", "VZ",
        "F(",
        "PD", "PB", "PH", "PO", "PV", "PF", "P(",
        "(D", "(B", "(H", "(O", "(V", "(F", "((",
        ")P", "))", ")Z",
        "ZD", "ZB", "ZH", "ZO", "ZV", "ZF", "Z("
    };

    // provjera : da
    public static boolean pokretanje(ArrayList<Token> tokeni, Raspon raspon_greske,
            StringBuffer tekst_greske) {
        boolean izraz_dobar;

        izraz_dobar = provjera_ispravnosti(tokeni, raspon_greske, tekst_greske);
        if (!izraz_dobar) {
            return false;
        }

        izraz_dobar = provjera_pocetka_izraza(tokeni, raspon_greske, tekst_greske);
        if (!izraz_dobar) {
            return false;
        }

        izraz_dobar = provjera_kraja_izraza(tokeni, raspon_greske, tekst_greske);
        if (!izraz_dobar) {
            return false;
        }

        izraz_dobar = provjera_redoslijeda(tokeni, raspon_greske, tekst_greske);
        if (!izraz_dobar) {
            return false;
        }

        izraz_dobar = provjera_funkcija(tokeni, raspon_greske, tekst_greske);
        if (!izraz_dobar) {
            return false;
        }

        izraz_dobar = provjera_zareza(tokeni, raspon_greske, tekst_greske);
        if (!izraz_dobar) {
            return false;
        }

        return true;
    }

    // provjera : da
    private static boolean zarez_dobar(ArrayList<Token> tokeni, int poz) {
        int i = 0;
        Token t;

        while (poz >= 0) {
            t = (Token) tokeni.get(poz);
            if (t.oznaka == '(') {
                if (i == 0) {
                    break;
                } else {
                    i--;
                }
            } else if (t.oznaka == ')') {
                i++;
            }
            poz--;
        } // while

        if (poz <= 0) {
            return false;
        } else {
            t = (Token) tokeni.get(poz - 1);
            if (t.oznaka != 'F') {
                return false;
            }
        }
        return true;
    }

    // zarez se koristi samo za odvajanje argumenata funkcije
    // provjera : da
    private static boolean provjera_zareza(ArrayList<Token> tokeni, Raspon raspon_greske,
            StringBuffer tekst_greske) {
        Token t;
        boolean povrat = true;

        ArrayList<Token> pomocni = (ArrayList<Token>) tokeni.clone();
        Calculate.izbacivanje_visestrukih_zagrada(pomocni);

        for (int i = 0; i < pomocni.size(); i++) {
            t = (Token) pomocni.get(i);
            if (t.oznaka == 'Z' && !zarez_dobar(pomocni, i)) {
                raspon_greske.pocetak = t.pozicija;
                raspon_greske.kraj = t.pozicija + 1;
                tekst_greske.append(Poruke.provjera_zareza);
                povrat = false;
                break;
            }
        }
        return povrat;
    }

    // vraca poziciju zatvorene zagrade koja oznacava kraj funkcije
    // ulazni parametar je pozicija u vektoru "tokeni" na kojem se nalazi funkcija
    // provjera : da
    public static int kraj_funkcije(ArrayList<Token> tokeni, int pozicija_funkcije) {
        int i = pozicija_funkcije + 2;
        int brojac_zagrada = 1;
        Token t;

        while (brojac_zagrada != 0) {
            t = (Token) tokeni.get(i);
            if (t.oznaka == '(') {
                brojac_zagrada++;
            } else if (t.oznaka == ')') {
                brojac_zagrada--;
            }
            i++;
        }
        return i - 1;
    }

    // vraca broj argumenata koji ima funkcija u izrazu
    // provjera : da
    private static int broj_argumenata_funkcije(ArrayList<Token> tokeni, int pozicija_funkcije) {
        int i = pozicija_funkcije + 2;
        int broj_zareza = 0;
        Token t;

        while (i < kraj_funkcije(tokeni, pozicija_funkcije)) {
            t = (Token) tokeni.get(i);
            if (t.oznaka == 'F') {
                i = kraj_funkcije(tokeni, i);
            } else if (t.oznaka == 'Z') {
                broj_zareza = broj_zareza + 1;
            }
            i = i + 1;
        }
        return broj_zareza + 1;
    }

    // provjerava da li sve funkcije unutar izraza imaju dobar broj argumenata
    // provjera : da
    private static boolean provjera_funkcija(ArrayList<Token> tokeni, Raspon raspon_greske,
            StringBuffer tekst_greske) {
        int i;
        Function fn;
        boolean povrat = true;
        Token t;

        for (i = 0; i < tokeni.size(); i++) {
            t = tokeni.get(i);

            // preskoci sve tokene koji nisu funkcije
            if (t.oznaka != 'F') {
                continue;
            }

            fn = (Function) t.token;

            // preskoci funkcije s varijabilnim brojem argumenata
            if (fn.broj_argumenata != 0
                    && broj_argumenata_funkcije(tokeni, i) != fn.broj_argumenata) {
                raspon_greske.pocetak = t.pozicija;
                raspon_greske.kraj = t.pozicija + t.duzina;
                tekst_greske.append(Poruke.funkcija + " \"" + fn + "\"" + Poruke.ima
                        + fn.broj_argumenata + Poruke.argument);
                if (fn.broj_argumenata != 1) {
                    tekst_greske.append("s");
                }
                povrat = false;
                break;
            }
        }
        return povrat;
    }

    // provjera : da
    private static boolean redoslijed_los(String spoj) {
        boolean povrat = true;

        for (int i = 0; i < dozvoljene_kombinacije.length; i++) {
            if (dozvoljene_kombinacije[i].equals(spoj)) {
                povrat = false;
                break;
            }
        }
        return povrat;
    }

    // provjerava da li je redoslijed tokena ispravan
    // provjera : da
    private static boolean provjera_redoslijeda(ArrayList<Token> tokeni, Raspon raspon_greske,
            StringBuffer tekst_greske) {
        boolean povrat = true;
        Token t1;
        Token t2;
        StringBuffer spoj = new StringBuffer("  ");

        for (int i = 0; i < tokeni.size() - 1; i++) {
            t1 = (Token) tokeni.get(i);
            t2 = (Token) tokeni.get(i + 1);

            spoj.setCharAt(0, t1.oznaka);
            spoj.setCharAt(1, t2.oznaka);

            if (redoslijed_los(spoj.toString())) {
                raspon_greske.pocetak = t2.pozicija;
                raspon_greske.kraj = t2.pozicija + t2.duzina;
                tekst_greske.append(nedozvoljena_kombinacija(spoj.toString()));
                povrat = false;
                break;
            }
        }
        return povrat;
    }

    // provjera : da
    private static String nedozvoljena_kombinacija(String kombinacija) {
        char znak1, znak2;

        znak1 = kombinacija.charAt(0);
        znak2 = kombinacija.charAt(1);

        return naziv_tokena(znak2) + " " + Poruke.poslije + " " + naziv_tokena(znak1);
    }

    // provjera : da
    public static String naziv_tokena(char oznaka) {
        switch (oznaka) {
            case 'D':
                return Poruke.broj;
            case 'B':
                return Poruke.binarni_broj;
            case 'H':
                return Poruke.heksadecimalni_broj;
            case 'O':
                return Poruke.oktalni_broj;
            case 'V':
                return Poruke.varijabla;
            case 'F':
                return Poruke.funkcija;
            case 'P':
                return Poruke.operator;
            case '(':
                return Poruke.otvorena_zagrada;
            case ')':
                return Poruke.zatvorena_zagrada;
            case 'Z':
                return Poruke.zarez;
            default:
                return Poruke.greska;
        }
    }

    // provjerava da li izraz pocinje sa legalnim tokenom
    // ako ne, vraca false i puni raspon_grekse i tekst_greske
    // provjera : da
    private static boolean provjera_pocetka_izraza(ArrayList<Token> tokeni, Raspon raspon_greske,
            StringBuffer tekst_greske) {
        char oznaka;
        Token t;

        t = (Token) tokeni.get(0);
        oznaka = t.oznaka;

        switch (oznaka) {
            case 'P':
                tekst_greske.append(Poruke.pocetak_operator);
                break;
            case ')':
                tekst_greske.append(Poruke.pocetak_zagrada);
                break;
            case 'Z':
                tekst_greske.append(Poruke.pocetak_zarez);
                break;
            default:
                return true;
        }

        raspon_greske.pocetak = 0;
        raspon_greske.kraj = t.duzina;
        return false;
    }

    // provjerava da li izraz zavrsava sa legalnim tokenom
    // ako ne, vraca false i puni raspon_grekse i tekst_greske
    // provjera : da
    private static boolean provjera_kraja_izraza(ArrayList<Token> tokeni, Raspon raspon_greske,
            StringBuffer tekst_greske) {
        char oznaka;
        Token t;

        t = (Token) tokeni.get(tokeni.size() - 1);
        oznaka = t.oznaka;

        if (oznaka == 'P') {
            tekst_greske.append(Poruke.kraj_operator);
        } else if (oznaka == 'F') {
            tekst_greske.append(Poruke.kraj_funkcija);
        } else if (oznaka == '(') {
            tekst_greske.append(Poruke.kraj_zagrada);
        } else if (oznaka == 'Z') {
            tekst_greske.append(Poruke.kraj_zarez);
        } else {
            return true;
        }

        raspon_greske.pocetak = t.pozicija;
        raspon_greske.kraj = t.pozicija + t.duzina;
        return false;
    }

    // provjerava sintakticku ispravnost izraza
    // ako pronade neki nevazeci token, vraca false
    // i puni raspon_greske i tekst_greske, inace vraca true
    // provjera : da
    private static boolean provjera_ispravnosti(ArrayList<Token> tokeni, Raspon raspon_greske,
            StringBuffer tekst_greske) {
        boolean greska = false;
        int pocetak;
        int kraj;
        Token t;

        // nadi pocetak pogresnog dijela izraza
        for (pocetak = 0; pocetak < tokeni.size(); pocetak++) {
            t = (Token) tokeni.get(pocetak);
            if (t.oznaka == 'G') {
                greska = true;
                break;
            }
        }

        if (greska) {
            // nadi kraj pogresnog dijela izraza
            for (kraj = pocetak + 1; kraj < tokeni.size()
                    && ((Token) (tokeni.get(kraj))).oznaka == 'G'; kraj++);

            t = (Token) tokeni.get(pocetak);
            pocetak = t.pozicija;
            t = (Token) tokeni.get(kraj - 1);
            kraj = t.pozicija;

            raspon_greske.pocetak = pocetak;
            raspon_greske.kraj = kraj + 1;
            tekst_greske.append(Poruke.nepoznati_dio);
        }

        return !greska;
    }

}
