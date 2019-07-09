
package com.funnyai.data;

/**
 *
 * @author happyli
 */
public class S_Compare {
    public static boolean compareString(Object a,Object r,Object b){
        double d1=0;
        if (a instanceof C_Var_Java){
            C_Var_Java pVar=(C_Var_Java)a;
            if (pVar.pObj instanceof Integer){
                d1=0.0+(int)pVar.pObj;
            }else{
                d1=Double.parseDouble((String) pVar.pObj);
            }
        }else if (a instanceof String){
            d1=Double.parseDouble((String) a);
        }
        
        double d2=0;
        if (b instanceof C_Var_Java){
            C_Var_Java b1=(C_Var_Java)b;
            if (b1.pObj instanceof Integer){
                d2=(int)b1.pObj;
            }else{
                d2=Double.parseDouble((String)((C_Var_Java)b).pObj);
            }
        }else if (b instanceof String){
            d2=Double.parseDouble((String) b);
        }
        switch((String)r){
            case "<":
                return d1<d2;
            case "<=":
                return d1<=d2;
            case ">":
                return d1>d2;
            case ">=":
                return d1>=d2;
            case "=":
                return d1==d2;
        }
        return false;
    }

    public static boolean compareString(Boolean var_loop_2) {
        return var_loop_2;
    }
    
    
    public static boolean compareString(C_Var_Java var_loop_2) {
//        if ("Boolean".equals(var_loop_2.Type)){
            return (boolean) var_loop_2.pObj;
//        }else{
//            return false;
//        }
    }
}
