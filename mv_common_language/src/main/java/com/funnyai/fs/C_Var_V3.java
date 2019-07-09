
package com.funnyai.fs;

/**
 *
 * @author happyli
 */
public class C_Var_V3 {
    
    public String Name="";
    public String Type="";
    public Object Value=null;

    C_Var_V3(String strName, String strType,Object pObject){
        this.Name=strName;
        this.Type=strType;
        this.Value=pObject;
    }
    
    public String Get_Var_Head(){
        if (null != this.Type){
            switch (this.Type) {
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
