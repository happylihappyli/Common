
package com.funnyai.fs;

/**
 *
 * @author happyli
 */
public class C_Var {
    
    public int ID=0;
    public String key="";
    public String type="";
    public int iFunction=0;//=1代表左边是函数，比如 a[1]=@array(a,1)

    C_Var(Treap_Var pTreap_Var,String strKey, String strType,int iFunction) {
        pTreap_Var.ID_Count++;
        
        this.iFunction=iFunction;
        this.ID=pTreap_Var.ID_Count;
        this.key=strKey;
        this.type=strType;
    }
    
    public String Get_Var_Head(){
        if (null != this.type){
            switch (this.type) {
                case "string":
                    return "s";
                case "int":
                    return "i";
                default:
                    return "v";
            }
        }
        return "v";
    }
}
