package com.funnyai.Math.Old;

final public class Token
   {
   public Object token;
   public char oznaka;
   public int pozicija;
   public int duzina;

   public Token ( Object token, char oznaka, int pozicija, int duzina )
      {
      this.token=token;
      this.oznaka=oznaka;
      this.pozicija=pozicija;
      this.duzina=duzina;
      }

   public String toString()
      {
      return token.toString()+" ; "+oznaka+" ; "+pozicija+" ; "+duzina+"\n";
      // return token.toString()+" ";
      }

   }
