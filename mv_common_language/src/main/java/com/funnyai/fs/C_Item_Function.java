/*
 * 生成程序的时候，每个函数
 */
package com.funnyai.fs;

import com.funnyai.string.Old.S_Strings;

/**
 *
 * @author happyli
 */
public class C_Item_Function {
    public String Function="";
    public String Operator=",";
    public C_Item_Function_Input pInput=new C_Item_Function_Input();
    
    @Override
    public String toString(){
        StringBuilder strReturn=new StringBuilder();
        switch (Function){
            case "++":
                strReturn.append(this.Function);
                break;
            case "+=":
            case "-=":
                strReturn.append(this.Function).append(this.get_input_string());
                break;
            case "+":
            case "-":
            case "*":
            case "/":
            case "^":
            case "<":
            case "<=":
            case "=":
            case ">":
            case ">=":
                this.Operator=this.Function;
                strReturn.append(this.get_input_string());
                break;
            default:
                if (Function.startsWith("@object")){
                    if (pInput.size()>0){
                        String strLine=Function;
                        strLine=strLine.replace("@object", pInput.get(0));
                        if (pInput.size()>1){
                            for (int i=1;i<pInput.size();i++){
                                strLine=strLine.replace("#"+i, pInput.get(i));
                            }
                        }
                        strReturn.append(strLine);
                    }
                }else{
                    this.Operator=",";
                    strReturn.append(this.Function).append("(").append(this.get_input_string()).append(")");
                }
                break;
        }
        return strReturn.toString();
    }
    
    public String get_input_string(){
        String strReturn="";
        for (int i=0;i<pInput.size();i++){
            strReturn+=pInput.get(i)+Operator;
        }
        strReturn=S_Strings.cut_end_string(strReturn,Operator);
        return strReturn;
    }
}
