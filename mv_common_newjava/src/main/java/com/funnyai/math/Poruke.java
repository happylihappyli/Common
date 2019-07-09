package com.funnyai.math;

final public class Poruke
	{
	final public static String nepravilan_raspored_zagrada="invalid number or order of parentheses";

	final public static String poslije="after";
	final public static String broj="number";
	final public static String binarni_broj="binary number";
	final public static String heksadecimalni_broj="hexadecimal number";
	final public static String oktalni_broj="octal number";
	final public static String varijabla="variable";
	final public static String funkcija="function";
	final public static String operator="operator";
	final public static String otvorena_zagrada="open parenthesis";
	final public static String zatvorena_zagrada="closed parenthesis";
	final public static String zarez="comma";
	final public static String greska="error";

   final public static String pocetak="expression must not begin with ";
   final public static String pocetak_operator=pocetak + operator;
   final public static String pocetak_zagrada=pocetak+zatvorena_zagrada;
   final public static String pocetak_zarez=pocetak+zarez;

   final public static String kraj="expression must not end with ";
   final public static String kraj_operator=kraj + operator;
   final public static String kraj_funkcija=kraj + funkcija;
   final public static String kraj_zagrada=kraj + otvorena_zagrada;
   final public static String kraj_zarez=kraj + zarez;

   final public static String provjera_zareza="comma is used only for separating arguments of functions";
   final public static String provjera_funkcija="wrong number of arguments";

   final public static String nepoznati_dio="unknown part of expression";
   final public static String argument=" argument";
   final public static String ima=" has ";

	}