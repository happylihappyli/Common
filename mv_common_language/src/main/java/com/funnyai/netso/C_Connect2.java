package com.funnyai.netso;
/**
 *
 * @author happyli
 */
public class C_Connect2 {
    public static int ID_Count=0;
    
    public String Name="";
    public double Weight;
    public C_Concept2 pFrom;
    public C_Concept2 pTo;
    int ID=0;
    
    public C_Connect2(){
        C_Connect2.ID_Count += 1;
        ID = C_Connect2.ID_Count;
   }
}
