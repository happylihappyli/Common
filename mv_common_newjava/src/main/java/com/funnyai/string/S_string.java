package com.funnyai.string;

import com.funnyai.data.C_Var_Java;
import static com.funnyai.string.S_string.Str_ReplacePredeal;
import java.io.UnsupportedEncodingException;
import static java.lang.System.out;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

public class S_string {

    public static void main(String[] args) {
        out.println(isNumeric("12.3"));
    }

    public static C_Var_Java get_from(C_Var_Java... a){
        if (a.length>1){
            String str=(String) a[0].pObj;
            String strFind=(String) a[1].pObj;
            int index=str.indexOf(strFind);
            return new C_Var_Java("String",str.substring(index));
        }else{
            return new C_Var_Java("String","");
        }
    }
    
    public static String combine(String... a){
        String strReturn="";
        for (String a1 : a) {
            strReturn += a1;
        }
        return strReturn;
    }
    
    public static C_Var_Java split_to_int(C_Var_Java... a){
        if (a.length>0){
            String strLine=(String) a[0].pObj;
            String strSep=",";
            if (a.length>1) strSep=(String) a[1].pObj;
            String[] strSplit=strLine.split(strSep);
            int[] b=new int[strSplit.length];
            for (int i=0;i<strSplit.length;i++){
                b[i]=Integer.valueOf(strSplit[i]);
            }
            return new C_Var_Java("int[]",b);
        }else{
            return null;
        }
    }
    
    public static C_Var_Java match(C_Var_Java... a){
        if (a.length>1){
            String strPattern=(String) a[0].pObj;
            String strLine=(String) a[1].pObj;
            Pattern p = Pattern.compile(strPattern);//"\\{@(\\d+)\\}");
            Matcher m = p.matcher(strLine); // 获取 matcher 对象
            return new C_Var_Java("Matcher",m);
        }else{
            return null;
        }
    }
    
    public static C_Var_Java string_array(C_Var_Java... a){
        if (a.length>1){
            String strLine=(String) a[0].pObj;
            String strSep=(String) a[1].pObj;
            String[] strSplit=strLine.split(strSep);
            ArrayList pArrayList=new ArrayList();
            for (int i=0;i<strSplit.length;i++){
                pArrayList.add(new C_Var_Java("String",strSplit[i]));
            }
            return new C_Var_Java("Array",pArrayList);
        }else{
            return new C_Var_Java("String","");
        }
        
    }
    
    /**
     * 格式化文本数组
     * @param strTemplate
     * @param strTextArray
     * @return 
     */
    public static String string_format_txt_array(String strTemplate,String strTextArray){
        String strContent="";
        strTextArray=strTextArray.replace("\r\n", "\n");
        
        String[] strSplit=strTextArray.split("\n");
        for(int i=0;i<strSplit.length;i++){
            String strLine=strTemplate;
            String[] strSplit2=strSplit[i].split(",");
            for(int j=0;j<strSplit2.length;j++){
                strLine=strLine.replace("{"+j+"}",strSplit2[j]);
            }
            strContent+=strLine+"\n";
        }
        if (strContent.endsWith("\n")){
            strContent=strContent.substring(0,strContent.length()-1);
        }
       
        return strContent;
    }
    
    public static C_Var_Java json_array(C_Var_Java... a){
        String strReturn="[";
        for (C_Var_Java pObj : a) {
            String strTmp="";
            if (pObj!=null){
                strTmp=pObj.pObj.toString();
            }
            strTmp=strTmp.replace("\\", "\\\\");
            strTmp=strTmp.replace("\n", "\\n");
            strTmp=strTmp.replace("\"", "\\\"");
            strReturn+="\""+strTmp+"\",";
        }
        strReturn=S_string.cut_end_string(strReturn,",");
        strReturn+="]";
        return new C_Var_Java("String",strReturn);
    }
    
    public static String[] split(Object... a){
        if (a.length>1){
            String strLine=(String) a[0];
            String strSep=(String) a[1];
            String[] strSplit=strLine.split(strSep);//"\t");
            return strSplit;
        }else{
            return null;
        }
    }
    
    public static ArrayList<C_Var_Java> split_json_array(C_Var_Java... strArray){
        ArrayList<C_Var_Java> pList=new ArrayList<>();
        String strTmp=(String) strArray[0].pObj;
        JSONArray p2=new JSONArray(strTmp);
        for (int i=0;i<p2.length();i++){
            pList.add(new C_Var_Java("String",p2.getString(i)));
        }
        return pList;
    }
    
    
    public static C_Var_Java format(C_Var_Java... a){
        if (a.length>1){
            String str=(String) a[0].pObj;
            for (int i=1;i<a.length;i++){
                str=str.replace("{"+(i-1)+"}",(String) a[i].pObj);
            }
            return new C_Var_Java("String",str);
        }else{
            return new C_Var_Java("String","");
        }
    }
    
    public static String format(String... a){
        if (a.length>1){
            String str=(String) a[0];
            for (int i=1;i<a.length;i++){
                str=str.replace("{"+(i-1)+"}",(String) a[i]);
            }
            return str;
        }else{
            return "";
        }
    }
    /**
     * 解密
     * @param pwd
     * @return 
     */
    public static String Base64_Decode(String pwd)
    {
        byte[] debytes = Base64.decodeBase64(new String(pwd).getBytes());
        return new String(debytes);
    }

    /**
     * 加密
     * @param pwd
     * @return 
     */
    public static String Base64_Encode(String pwd)
    {
        byte[] enbytes = Base64.encodeBase64Chunked(pwd.getBytes());
        return new String(enbytes);
    }
    
    public static boolean isNumeric(String str) {
        if (str.equalsIgnoreCase("")) {
            return false;
        }

        Pattern pattern = Pattern.compile("-{0,1}([0-9]|)*(\\.*)([0-9]|)*E{0,1}([0-9]|)*");
        return pattern.matcher(str).matches();
    }

    public static Double getDouble_FromStr(String strInput, Double Default) {
        Double iReturn = Default;
        if (strInput != null && strInput.length() > 0) {
            if (S_string.isNumeric(strInput)) {
                iReturn = Double.valueOf(strInput);
            }
        }
        return iReturn;
    }
    
    public static int getIntFromStr(Object strInput, int Default) {
        if (strInput instanceof Integer) return (int)strInput;
        String strTmp=(String) strInput;
        int iReturn = Default;
        if (strTmp != null && strTmp.length() > 0) {
            if (S_string.isNumeric(strTmp)) {
                iReturn = (int)Math.round(Double.valueOf(strTmp));
            }
        }
        return iReturn;
    }
    
    
    public static int getIntFromStr(C_Var_Java pVar, int Default) {
        if (pVar==null) return 0;
        
        String strTmp="";
        if (pVar.pObj instanceof Integer){
            strTmp=(int) pVar.pObj+"";
        }else{
            strTmp=(String) pVar.pObj;
        }
        int iReturn = Default;
        if (strTmp != null && strTmp.length() > 0) {
            if (S_string.isNumeric(strTmp)) {
                iReturn = (int)Math.round(Double.valueOf(strTmp));
            }
        }
        return iReturn;
    }

    public static long getLongFromStr(String strInput, int Default) {
        long iLong = Default;
        if (strInput != null && strInput.length() > 0) {
            if (S_string.isNumeric(strInput)) {
                iLong = Long.valueOf(strInput);
            }
        }
        return iLong;
    }


    public static String string2Json(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    
    public static String cut_end_string(String strLine, String strTag) {
        if (strLine.endsWith(strTag)) {
            strLine = strLine.substring(0,strLine.length()-strTag.length());
        }
        return strLine;
    }

    public static String cutEnd(String strLine, int Count) {
        return strLine.substring(0, strLine.length() - Count);
    }

    /**
     * 字符串合并
     * @param strSep 组合字符串的间隔符
     * @param a
     * @return 
     */
    public static C_Var_Java combine(String strSep,C_Var_Java... a) {
        StringBuilder r=new StringBuilder();
        for (C_Var_Java a1 : a) {
            if (a1==null){
                
            }else{
                if (a1.pObj instanceof ArrayList){
                    ArrayList pList=(ArrayList) a1.pObj;
                    for (int i=0;i<pList.size();i++){
                        C_Var_Java pTmp=(C_Var_Java) pList.get(i);
                        if (pTmp.pObj instanceof Integer){
                            r.append((int) pTmp.pObj);
                        }else{
                            r.append((String) pTmp.pObj);
                        }
                        if ("".equals(strSep)==false){
                            r.append(strSep);
                        }
                    }
                }else if (a1.pObj instanceof Integer){
                    r.append((int) a1.pObj);
                }else{
                    r.append((String) a1.pObj);
                }
            }
            if ("".equals(strSep)==false){
                r.append(strSep);
            }
        }
        return new C_Var_Java("String",r.toString());
    }

    

    /**
     * 字符串合并,用字符串
     * @param a
     * @return 
     */
    public static C_Var_Java combine_with(C_Var_Java... a) {
        StringBuilder r=new StringBuilder();
        String strSep=(String) a[0].pObj;
        strSep=strSep.replaceAll("\\\\n", "\n");
        for (int i=1;i<a.length;i++){
            C_Var_Java a1=a[i];
            if (a1==null){
                
            }else{
                if (a1.pObj instanceof ArrayList){
                    ArrayList pList=(ArrayList) a1.pObj;
                    for (int j=0;j<pList.size();j++){
                        C_Var_Java pTmp=(C_Var_Java) pList.get(j);
                        if (pTmp.pObj instanceof Integer){
                            r.append((int) pTmp.pObj);
                        }else{
                            r.append((String) pTmp.pObj);
                        }
                        if ("".equals(strSep)==false){
                            r.append(strSep);
                        }
                    }
                }else if (a1.pObj instanceof Integer){
                    r.append((int) a1.pObj);
                }else{
                    r.append((String) a1.pObj);
                }
            }
            if ("".equals(strSep)==false){
                r.append(strSep);
            }
            
        }
        return new C_Var_Java("String",r.toString());
    }
    
    
    public static String urlencode(String a) {
        String strLine=a;
        try {
            return URLEncoder.encode(strLine, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return "";
    }
    
    /**
     * urlEncode编码
     * @param a
     * @return 
     */
    public static C_Var_Java urlencode(C_Var_Java... a) {
        String strLine="";
        if (a.length>0){
            strLine=(String) a[0].pObj;
        }
        try {
            if ("".equals(strLine)){
                return new C_Var_Java("String","");
            }else{
                return new C_Var_Java("String",URLEncoder.encode(strLine, "utf-8"));
            }
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return new C_Var_Java("String","error");
    }

    public static C_Var_Java JSONObject(C_Var_Java... a) {
        if (a.length>0){
            String strTmp=(String) a[0].pObj;
            return new C_Var_Java("",new JSONObject(strTmp));
        }else{
            return new C_Var_Java("",null);
        }
    }

    public static C_Var_Java add(Object... a) {
        StringBuilder r=new StringBuilder();
        Double sum=0.0;
        for (Object a1 : a) {
            String strTmp="0";
            if (a1 instanceof C_Var_Java) {
                strTmp = (String) ((C_Var_Java) a1).pObj;
            } else if (a1 instanceof String) {
                strTmp = (String) a1;
            }
            if ("".equals(strTmp)){
                
            }else{
                sum+=Double.parseDouble(strTmp);
            }
        }
        return new C_Var_Java("String",sum.toString());
    }


    public String HTMLEncode(String fString) {
        String functionReturnValue = null;
        if (fString != null) {
            fString = fString.replace(">", "&gt;");
            fString = fString.replace("<", "&lt;");
            char[] pChar = new char[1];
            pChar[0] = (char) 34;
            fString = fString.replace(new String(pChar), "&quot;");
            pChar[0] = (char) 39;
            fString = fString.replace(new String(pChar), "&#39;");
            functionReturnValue = fString;
        }
        return functionReturnValue;
    }

    public static boolean isEnglish(String charaString){ 
      return charaString.matches("^[a-zA-Z]*"); 
    } 
    
    public static String cdata_encode(String Str, String strInvert) {
        return CData_Encode(Str,"1".equals(strInvert));
    }
    
    public static String CData_Encode(String Str, boolean bInvert) {
        if (Str == null) {
            return "";
        }
        if (bInvert == false) {
            Str = Str.replace("&", "&amp;");
            Str = Str.replace("<", "&lt;");
            Str = Str.replace(">", "&gt;");
            Str = Str.replace("'", "&apos;");
            Str = Str.replace("\"", "&quot;");
        } else {
            Str = Str.replace("&lt;", "<");
            Str = Str.replace("&gt;", ">");
            Str = Str.replace("&apos;", "'");
            Str = Str.replace("&quot;", "\"");
            Str = Str.replace("&amp;", "&");
        }
        return Str;
    }

    public static String ReplaceNLP_Invert(String strValue) {
        String strReturn = strValue;
        strReturn = strReturn.replace(" {C.dian} ", ".");
        strReturn = strReturn.replace(" {C.wen} ", "?");
        strReturn = strReturn.replace(" {C.mao} ", ":");
        strReturn = strReturn.replace(" {C.na} ", "\\");
        strReturn = strReturn.replace(" {C.pie} ", "/");

        strReturn = strReturn.replace(" {C.dian} ", ".");
        strReturn = strReturn.replace(" {C.wen} ", "?");
        strReturn = strReturn.replace(" {C.mao} ", ":");
        strReturn = strReturn.replace(" {C.na} ", "\\");
        strReturn = strReturn.replace(" {C.pie} ", "/");

        strReturn = strReturn.replace(" {C.dian} ", ".");
        strReturn = strReturn.replace(" {C.wen} ", "?");
        strReturn = strReturn.replace(" {C.mao} ", ":");
        strReturn = strReturn.replace(" {C.na} ", "\\");
        strReturn = strReturn.replace(" {C.pie} ", "/");

        strReturn = strReturn.replace(" {C.dian} ", ".");
        strReturn = strReturn.replace(" {C.wen} ", "?");
        strReturn = strReturn.replace(" {C.mao} ", ":");
        strReturn = strReturn.replace(" {C.na} ", "\\");
        strReturn = strReturn.replace(" {C.pie} ", "/");

        return strReturn;
    }

    public static String ReplaceNLP(String strReturn) {
        strReturn = strReturn.replace(".", "C.dian");
        strReturn = strReturn.replace("?", "C.wen");
        strReturn = strReturn.replace(":", "C.mao");

        strReturn = strReturn.replace("\\", "C.na");
        strReturn = strReturn.replace("/", "C.pie");
        strReturn = strReturn.replace("*", "C.xing");
        strReturn = strReturn.replace("\"", "C.yin");
        strReturn = strReturn.replace("<", "C.xiaoyu");
        strReturn = strReturn.replace(">", "C.dayu");
        strReturn = strReturn.replace("|", "C.shu");
        strReturn = strReturn.replace(",", "C.dou");

        strReturn = strReturn.replace("=", "C.deng");

        return strReturn;
    }

    public static String ReplacePath(String StrPath) {
        StrPath = StrPath.replace("?", "_1_");
        StrPath = StrPath.replace(":", "_2_");
        StrPath = StrPath.replace("\\\\", "_3_");
        StrPath = StrPath.replace("/", "_4_");
        StrPath = StrPath.replace("*", "_5_");
        StrPath = StrPath.replace("\\", "_6_");
        StrPath = StrPath.replace("<", "_7_");
        StrPath = StrPath.replace(">", "_8_");
        StrPath = StrPath.replace("|", "_9_");
        return StrPath;
    }

    public String FilterInvalidPathChar(String StrPath) {
        StrPath = StrPath.replace("?", "");
        StrPath = StrPath.replace(":", "");
        StrPath = StrPath.replace("/", "");
        StrPath = StrPath.replace("*", "");
        StrPath = StrPath.replace("\"", "");
        StrPath = StrPath.replace("<", "");
        StrPath = StrPath.replace(">", "");
        StrPath = StrPath.replace("|", "");
        return StrPath;

    }

    public static String Str_ReplacePredeal(String mWord) {
        mWord = mWord.replace("：", ":");
        mWord = mWord.replace("）", ")");
        mWord = mWord.replace("（", "(");
        mWord = mWord.replace("？", "?");
        mWord = mWord.replace("，", ",");
        mWord = mWord.replace("！", "!");

        return mWord.toLowerCase();

    }

    /**
     * 判断一个字符串是不是中文字或词，不能有空格。
     * @param strContext
     * @return 
     */
    public static boolean ifChineseWord(String strContext){
        char pChar;
        for (int i = 0; i < strContext.length(); i++) {
            pChar = strContext.charAt(i);
            if ((int) pChar <= 128) {
                return false;
            }
        }
        return true;
    }

    public static String Blank_Chinese(String strContext) {
        if (strContext == null) {
            return "";
        }
        if (strContext.equals("")) {
            return "";
        }
        //在汉字前面后面加空格,以及在数字符号前后加空格
        String mStr;
        String strResult = "";

        StringBuilder sb = new StringBuilder();
        boolean bBlank = false;
        char pChar;
        for (int i = 0; i < strContext.length(); i++) {
            pChar = strContext.charAt(i);
            switch (strContext.charAt(i)) {
                case ' ':
                    bBlank = true;
                    break;
                default:
                    if (bBlank) {
                        sb.append(" ");
                        bBlank = false;
                    }
                    sb.append(pChar);
                    break;
            }
        }

        strContext = sb.toString().trim();
        while (strContext.contains("  ")) {
            strContext = strContext.replace("(\\ +){2,10}", "\\ ");
        }

        strContext = Str_ReplacePredeal(strContext);
        strContext = ReplaceFirst(strContext);

        for (int i = 0; i < strContext.length(); i++) {
            mStr = strContext.substring(i, i + 1);
            switch (mStr.charAt(0)) {
                case '+':
                case '-':
                case '*':
                case '\\':
                case '^':
                case '(':
                case ')':
                case ',':
                case '/':
                case '=':
                case ':':
                case '&':
                case '#':
                case '"':
                case '\'':
                case '?':
                case '.':
                case '@':
                case '%':
                case '<':
                case '>':
                case '{':
                case '}':
                case ';':
                case '$':
                case '[':
                case ']':
                    strResult = strResult + " {" + S_string.ReplaceNLP(mStr) + "} ";
                    break;
                default:
                    if (mStr.charAt(0) >= 0 & mStr.charAt(0) <= 255) {
                        strResult = strResult + mStr;
                    } else {
                        strResult = strResult + " " + mStr + " ";
                    }

                break;
            }
        }
        strResult = strResult.trim();

        //去掉两个空格的
        while (strResult.contains("  ")) {
            strResult = strResult.replace("  ", " ");
        }

        return strResult;
    }

    public static String Blank_Chinese_Invert(String strInput) {
        if (strInput.equals("")) {
            return "";
        }

        String[] strSplit = strInput.split(" ");

        //汉字前面后面空格去掉
        int i;
        String strResult = "";
        boolean bEnglish_JustNow = false;

        for (i = 0; i < strSplit.length; i++) {
            if (strSplit[i].length() > 2) {
                if (bEnglish_JustNow == true) {
                    strResult += " " + strSplit[i];
                } else {
                    strResult += strSplit[i];
                }
                bEnglish_JustNow = true;
            } else {
                if (strSplit[i].equals("") == false && strSplit[i].charAt(0) >= 0 && strSplit[i].charAt(0) <= 255) {
                    if (bEnglish_JustNow == true) {
                        strResult += " " + strSplit[i];
                    } else {
                        strResult += strSplit[i];
                    }
                    bEnglish_JustNow = true;
                } else {
                    strResult += strSplit[i];
                    bEnglish_JustNow = false;
                }
            }
        }
        strResult = strResult.trim();

        return strResult;
    }

    /**
     * 替换
     * @param args
     * @return 
     */
    public static C_Var_Java replace(C_Var_Java... args){
        if (args.length>2){
            String[] a=new String[3];
            a[0]=(String) args[0].pObj;
            a[1]=(String) args[1].pObj;
            a[2]=(String) args[2].pObj;
            return new C_Var_Java("String",a[0].replace(a[1],a[2]));
        }else{
            return new C_Var_Java("String","");
        }
    }
    
    /**
     * 
     * @param arg1
     * @param arg2
     * @param arg3
     * @return 
     */
    public static String replace(String arg1,String arg2,String arg3){
        String strLine=arg1;
        return strLine.replace(arg2,arg3);
    }
    
    
    public static C_Var_Java replace(C_Var_Java arg1,String arg2,String arg3){
        String strLine=(String) arg1.pObj;
        return new C_Var_Java("String",strLine.replace(arg2,arg3));
    }
    
    public static C_Var_Java replace(C_Var_Java arg1,String arg2,C_Var_Java arg3){
        String strLine=(String) arg1.pObj;
        if (arg3==null){
            return new C_Var_Java("String",strLine);
        }
        return new C_Var_Java("String",strLine.replace(arg2,(String)arg3.pObj));
    }
    
    public static String ReplaceFirst(String mWord) {
        //你被设计为多大？
        mWord = mWord.replace("？", "?");
        //将?变成"|{?}|"
        mWord = mWord.replace("?", " ? ");
        //将?变成"|{?}|"
        mWord = mWord.replace("!", " ! ");
        //将?变成"|{?}|"
        mWord = mWord.replace(",", " , ");
        //将?变成"|{?}|"
        mWord = mWord.replace("~", " ~ ");
        //将?变成"|{?}|"
        mWord = mWord.replace("。", ".");
        //将?变成"|{?}|"
        //mWord = Replace(mWord, ".", " {.} ") '将?变成"|{?}|"
        //mWord = Replace(mWord, "{:}", " {/_1} ")
        mWord = mWord.replace("\"", " {\"} ");
        //将.变成"|{"}|"

        mWord = mWord.replace("'s", " 's");
        mWord = mWord.replace("'re", " 're");
        mWord = mWord.replace("I'm", "I 'm");
        mWord = mWord.replace("can't", "can 't");
        mWord = mWord.replace("isn't", "is not");

        mWord = mWord.replace("I'd", "I 'd");
        mWord = mWord.replace("wouldn't", "would 't");

        mWord = mWord.replace("\r\n", "");

        mWord = mWord.replace("am not", "not am");
        //ChinEnglish
        mWord = mWord.replace("can not", "not can");
        //ChinEnglish
        mWord = mWord.replace("would not", "not would");

        mWord = mWord.replace("\r\n", " ");
        return mWord;

    }

    public String Filter_Tab_Space_Crlf(String Str) {
        Str = Str.replace("\t", "");
        Str = Str.replace("\r\n", "");
        Str = Str.replace(" ", "");
        return Str;
    }

    public boolean If_FileName(String StrFile) {
        int i;
        String[] Str;
        boolean bValid = true;

        if ("".equals(StrFile)) {
            bValid = false;
        } else {
            Str = "\\ / : * ? \" < > |".split(" ");
            for (i = 0; i < Str.length; i++) {
                if (!"".equals(Str[i])) {
                    if (StrFile.contains(Str[i])) {
                        bValid = false;
                    }
                }
            }
        }
        return bValid;

    }

    public String TrimLeft_Tab_Space(String Str) {
        //去掉左边 Tab 和 空格
        while (("\t".equals(Str.substring(0, 1)) || " ".equals(Str.substring(0, 1))) && Str.length() >= 1) {
            Str = Str.substring(0, Str.length() - 1);
        }
        return Str;
    }

    public String TrimSql(String Str) {
        //去掉左边 Tab 和 空格
        Str = Str.replace("\r\n", " ");

        while (("\t".equals(Str.substring(0, 1)) || " ".equals(Str.substring(0, 1))) && Str.length() >= 1) {
            Str = Str.substring(1);
        }

        while ((Str.endsWith("\t") || Str.endsWith(" ")) & Str.length() >= 1) {
            Str = Str.substring(0, Str.length() - 1);
        }

        return Str;
    }
}
