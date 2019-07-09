package com.funnyai.string.Old;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import static java.lang.System.out;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class S_Strings {

    public static void main(String[] args) {
        String[] strSplit=S_Strings.csvField("\"test\",\"123123\",\"a\"");
        System.out.println(strSplit.toString());
    }

    
    public static String combine(String str1,String str2){
        return str1+str2;
    }
    
    public static Matcher match(String strPattern,String strLine){
        Pattern p = Pattern.compile(strPattern);//"\\{@(\\d+)\\}");
        Matcher m = p.matcher(strLine); // 获取 matcher 对象
        return m;
    }
    
    public static String[] split(String strLine,String strSep){
        String[] strSplit=strLine.split(strSep);//"\t");
        return strSplit;
    }
    
    
    
    /**
     * 
     * @param url
     * @param XPath
     * @return 
     */
    public static String Get_From_XPath(String url,String XPath){
        String strHTML="";
        Document pDoc;
        try {
            //String url=url;//"http://www.zhongyoo.com/name/masanggen_154.html";
            pDoc = Jsoup.connect(url).get();
            
            Elements rs;

            String xpath=XPath;//"div[class=gaishu] div[class=text]"; // span a[href^=http://]
            rs = pDoc.select(xpath);

            for (Object o:rs){
//                if (o instanceof Element){
//                    out.println(((Element) o).html());
////                    System.out.println(index);
//                }
                strHTML+=o.toString();
                //out.println("=======================");
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(S_Strings.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(S_Strings.class.getName()).log(Level.SEVERE, null, ex);
        }
        return strHTML;
    }
    
    /**
     * csv cvs字段分隔
     * @param line
     * @return 
     */
    public static String[] csvField(String line){
        line=line+",";
        List<String> fields = new LinkedList<>();
        char[] alpah = line.toCharArray();
        boolean isFieldStart = true;
        int pos = 0; int len = 0; boolean yinhao = false;
        for(char c : alpah){
            if(isFieldStart){
                len = 0;
                isFieldStart = false;
            }
            if(c == '\"'){
                yinhao = !yinhao;
            }
            if(c == ',' && !yinhao){
                String tmp=new String(alpah, pos - len, len);
                if (tmp.startsWith("\"")){
                    tmp=tmp.substring(1);
                }
                tmp=S_Strings.cut_end_string(tmp,"\"");
                fields.add(tmp);
                isFieldStart = true;
            }
            pos++; len++;
        }
        return fields.toArray(new String[0]);
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

        Pattern pattern = Pattern.compile("-{0,1}([0-9]|)*(\\.*)([0-9]|)*");
        boolean bReturn=pattern.matcher(str).matches();
        if (bReturn==false){
            bReturn=isENum(str);
        }
        return bReturn;
    }

    static String regx = "^((-?\\d+.?\\d*)[Ee]{1}(-?\\d+))$";//科学计数法正则表达式
    static Pattern pattern = Pattern.compile(regx);
    public static boolean isENum(String input){//判断输入字符串是否为科学计数法
        return pattern.matcher(input).matches();
    }
    
    public static int getIntFromStr(String strInput, int Default) {
        int iReturn = Default;
        if (strInput != null && strInput.length() > 0) {
            strInput=strInput.replaceAll("\n","");
            if (S_Strings.isNumeric(strInput)) {
                iReturn = Integer.valueOf(strInput);
            }
        }
        return iReturn;
    }

    public static long getLongFromStr(String strInput, int Default) {
        long iLong = Default;
        if (strInput != null && strInput.length() > 0) {
            if (S_Strings.isNumeric(strInput)) {
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

    /**
     * 删除开头匹配的字符串
     * @param strLine
     * @param strTag
     * @return 
     */
    public static String cut_start_string(String strLine, String strTag) {
        if (strLine.startsWith(strTag)) {
            strLine = strLine.substring(strTag.length());
        }
        return strLine;
    }
    
    /**
     * 上传结尾匹配的字符串
     * @param strLine
     * @param strTag
     * @return 
     */
    public static String cut_end_string(String strLine, String strTag) {
        if (strLine.endsWith(strTag)) {
            strLine = strLine.substring(0,strLine.length()-strTag.length());
        }
        return strLine;
    }

    public static String cutEnd(String strLine, int Count) {
        return strLine.substring(0, strLine.length() - Count);
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

    /**
     * 句子预处理
     * @param strContext
     * @return 
     */
    public static String Blank_Chinese(String strContext) {
        if (strContext == null) {
            return "";
        }
        if (strContext.equals("")) {
            return "";
        }
        
        strContext=strContext.replace("０","0");
        strContext=strContext.replace("１","1");
        strContext=strContext.replace("２","2");
        strContext=strContext.replace("３","3");
        strContext=strContext.replace("４","4");
        strContext=strContext.replace("５","5");
        strContext=strContext.replace("６","6");
        strContext=strContext.replace("７","7");
        strContext=strContext.replace("８","8");
        strContext=strContext.replace("９","9");
        
        
        strContext=strContext.replace("ａ","a");
        strContext=strContext.replace("ｂ","b");
        strContext=strContext.replace("ｃ","c");
        strContext=strContext.replace("ｄ","d");
        strContext=strContext.replace("ｅ","e");
        strContext=strContext.replace("ｆ","f");
        strContext=strContext.replace("ｇ","g");
        strContext=strContext.replace("ｈ","h");
        strContext=strContext.replace("ｉ","i");
        strContext=strContext.replace("ｊ","j");
        strContext=strContext.replace("ｋ","k");
        strContext=strContext.replace("ｌ","l");
        strContext=strContext.replace("ｍ","m");
        strContext=strContext.replace("ｎ","n");
        strContext=strContext.replace("ｏ","o");
        strContext=strContext.replace("ｐ","p");
        strContext=strContext.replace("ｑ","q");
        strContext=strContext.replace("ｒ","r");
        strContext=strContext.replace("ｓ","s");
        strContext=strContext.replace("ｔ","t");
        strContext=strContext.replace("ｕ","u");
        strContext=strContext.replace("ｖ","v");
        strContext=strContext.replace("ｗ","w");
        strContext=strContext.replace("ｘ","x");
        strContext=strContext.replace("ｙ","y");
        strContext=strContext.replace("ｚ","z");
        
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

        String SType="";
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
                    strResult = strResult + " {" + S_Strings.ReplaceNLP(mStr) + "} ";
                    SType="";
                    break;
                default:
                    if ((mStr.charAt(0) >= 65 && mStr.charAt(0) <= 90) || (mStr.charAt(0) >= 97 && mStr.charAt(0) <= 122)) {
                        if ("abc".equals(SType) || "".equals(SType)){
                            strResult = strResult + mStr;
                        }else{
                            strResult = strResult + " " + mStr ;
                        }
                        SType="abc";
                    } else if ((mStr.charAt(0) >= 48 && mStr.charAt(0) <= 57)) {
                        if ("123".equals(SType) || "".equals(SType)){
                            strResult = strResult + mStr;
                        }else{
                            strResult = strResult + " " + mStr ;
                        }
                        SType="123";
                    }else{
                        SType="";
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

        while (("\t".equals(Str.substring(0, 1)) || Str.substring(0, 1) == " ") && Str.length() >= 1) {
            Str = Str.substring(1);
        }

        while ((Str.endsWith("\t") || Str.endsWith(" ")) & Str.length() >= 1) {
            Str = Str.substring(0, Str.length() - 1);
        }

        return Str;
    }

    public static String URL_Encode(String strLine) {
        if (strLine==null) return "";
        try {
            return URLEncoder.encode(strLine, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(S_Strings.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(strLine);
            ex.printStackTrace();
        }
        return "error";
    }
}
