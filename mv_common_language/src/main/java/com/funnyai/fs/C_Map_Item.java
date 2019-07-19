/*
 * C_Table C_Job 的基类。
 * C_Table 代表数据，C_Job代表程序
 */
package com.funnyai.fs;

import com.funnyai.common.AI_Var2;
import com.funnyai.common.S_Debug;
import com.funnyai.language.Tools_FS;
import com.funnyai.language.Tools_GetParam;
import com.funnyai.language.Tools_SYS;
import com.funnyai.language.Tools_File;
import com.funnyai.language.Tools_String;
import com.funnyai.language.Tools_Array;
import com.funnyai.language.Tools_Cassandra;
import com.funnyai.language.Tools_Write;
import com.funnyai.language.Tools_CN;
import com.funnyai.language.Tools_Net;
import com.funnyai.language.Tools_Math;
import com.funnyai.language.Tools_SubFunction;
import com.funnyai.language.C_Function_Return;
import com.funnyai.data.C_K_Str;
import com.funnyai.data.Treap;
import com.funnyai.data.TreapEnumerator;
import com.funnyai.data.C_K_Int;
import static com.funnyai.fs.C_Map_Item.Make_Android_UI;
import static com.funnyai.fs.C_Map_Item.Make_Program_By_Time;
import com.funnyai.io.Old.S_File_Text;
import com.funnyai.net.Old.S_Net;
import com.funnyai.netso.C_Session_AI;
import com.funnyai.string.Old.S_Strings;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.dom4j.*;
import org.json.*;

/**
 *
 * @author happyli
 */
public class C_Map_Item {

    public int ID = 0;
    public String Name = "";//名称
    public String Program = "";
    public String Program_Param = "";//这个是参数设置，这样程序可以更加抽象，不是运行参数

    public C_Run_Session pRun_Session = null;
    public C_Map_Run pRun = null;

    
    public String strNexts = "";//后续节点IDs

    public String DrawType = "";//绘图类型

    public String ItemType = "";//自定义类型
    public String Var_Type="String";//变量类型，默认为String
    
    private String strFroms = "";//输入节点IDs
    private String strTos = "";//输出节点IDs
    
    
    /**
     * 输入节点
     * @return 
     */
    public String Get_From_IDs(){
        String strReturn = strFroms;
        ArrayList<C_Map_Item_Connect> pList = C_Map_Item_Connect.Get_Connects_To(ID);//读取所有的输出箭头

        for (int i=0;i<pList.size();i++){
            C_Map_Item_Connect pConnect = pList.get(i);
            if (strReturn.equals("")){
                strReturn = pConnect.From_ID+"";
            }else{
                strReturn+=","+pConnect.From_ID;
            }
        }
        return strReturn;
    }
    
    /**
     * 输出节点
     * @return 
     */
    public String Get_To_IDs(){
        String strReturn = strTos;
        ArrayList<C_Map_Item_Connect> pList_Out = C_Map_Item_Connect.Get_Connects_From(ID);//读取所有的输出箭头

        for (int i=0;i<pList_Out.size();i++){
            C_Map_Item_Connect pConnect = pList_Out.get(i);
            if (strReturn.equals("")){
                strReturn = pConnect.To_ID+"";
            }else{
                strReturn+=","+pConnect.To_ID;
            }
        }

        return strReturn;
    }
    
    
    public void set_To_IDs(String strTmp) {
        strTos=strTmp;
    }
    
    
    public C_Map_Item(int ID, C_Run_Session pRun_Session, int Function_Call) {
        this.pRun_Session = pRun_Session;
        this.ID = ID;
        Read_Json();
        if (pRun_Session != null) {
            pRun = new C_Map_Run(pRun_Session, this.ID, Function_Call);
            pRun.Read_Json();
        }
    }

    
    /**
     * 读取当前变量的类型
     * @return 
     */
    public String Get_Type(){
        String strVar_Type="String";
        //所有的输出类型，只要有一个设置了#，就用这个类型
        if (this.Name.startsWith("#")){
            strVar_Type=this.Name.substring(1);
        }
        return strVar_Type;
    }
    
    
    public static void main(String[] args) {
        System.out.println("=========== test ===========");

        C_Run_Session pRun_Session = new C_Run_Session(0, 100);
        //Treap pTreap=new Treap();
        String strReturn = C_Map_Item.Make_Android_UI(pRun_Session, 898);

        System.out.println(strReturn);
    }

    public String Check_Type() {
        Read_Json();
        return this.DrawType;
    }

    public String Read_Json2() {
        return Read_Json();
    }

    private String Read_Json() {
        String strURL = AI_Var2.Site
                + "/funnyscript/json_read_map_item.php?id=" + this.ID;
        String strData = "";
        String strJSON = S_Net.http_GET(strURL,strData, "utf-8", "",20);
        int index = strJSON.indexOf("{");
        
        if (index > -1) {
            int index2 = strJSON.indexOf("}",index);
            if (index2>index+10){
                strJSON = strJSON.substring(index);
                S_Debug.Write_DebugLog("read.json", strJSON, false);
                try {
                    JSONObject pObj = new JSONObject(strJSON);
                    this.Name = pObj.getString("Name");
                    this.Program = pObj.getString("Program");
                    this.Program_Param = pObj.getString("Program_Param");
                    this.DrawType = pObj.getString("DrawType");
                    this.strFroms = pObj.getString("Connect_From");
                    if (",".equals(this.strFroms) || ",".equals(this.strFroms)){
                        this.strFroms="";
                    }
                    this.strNexts = pObj.getString("Connect_Next");
                    this.ItemType = pObj.getString("ItemType");
                } catch (Exception ex) {
                    System.out.println(ex.toString());
                }
                return strJSON;
            }else{
                System.out.println("read error, ID=" + this.ID);
                return "{}";
            }
        } else {
            System.out.println("read error, ID=" + this.ID);
            return "{}";
        }
    }

    /**
     * 读取远程的脚本
     *
     * @param ID
     * @return
     */
    public static String Read_Remote_Program(int ID) {
        String strURL = AI_Var2.Site + "/funnyscript/json_read_remote_map_item.php?id=" + ID;
        String strData = "";
        String strJSON = S_Net.http_GET(strURL,strData, "utf-8", "",20);
        int index = strJSON.indexOf("{");
        if (index > -1) {
            strJSON = strJSON.substring(index);
            JSONObject pObj = new JSONObject(strJSON);
            return pObj.getString("Program_Output");
        } else {
            return "";
        }
    }

    
    /**
     * 如果Table有输入，也可以看作是一个函数
     *
     * @param pTreap
     * @param pRun_Session
     * @param ID
     * @return
     */
    public static String Make_Program_Table(
            Treap pTreap,
            C_Run_Session pRun_Session,
            int ID) {

        int Function_Call = 0;
        if (pTreap.find(new C_K_Int(ID)) != null) {
            return "";
        }

        pTreap.insert(new C_K_Int(ID), ID);

        C_Table pTable = pRun_Session.Read_Table(Function_Call, ID);//获取task信息

        StringBuilder strXML = new StringBuilder();
        if ("template".equals(pTable.ItemType)) {
            strXML.append("<item id=\"").append(ID).append("\">\n");
            strXML.append("<input>\n<count>1</count>\n");
            strXML.append("<v1>\n");
            strXML.append(S_Strings.CData_Encode(pTable.Get_Program(), false));
            strXML.append("</v1>\n");
            strXML.append("</input>");
            strXML.append("<fun>=</fun>\n</item>\n");
            return strXML.toString();
        }
        String[] strSplit1 = pTable.Get_From_IDs().split(",");

        strXML.append("<item id=\"").append(ID).append("\">\n");
        C_Table pTable2;
        strXML.append("<input>\n");

        StringBuilder strXML2 = new StringBuilder();
        Document document;
        try {
            document = DocumentHelper.parseText("<item></item>");
            Node pNode = document.selectSingleNode("item");
            strXML.append("<count>").append(strSplit1.length).append("</count>\n");
            for (int i = 0; i < strSplit1.length; i++) {
                int k = i + 1;
                int ID2 = S_Strings.getIntFromStr(strSplit1[i], 0);//输入的ID

                Node pNodeTmp = pNode.selectSingleNode("v" + k);
                String strParam = "";
                if (pNodeTmp != null) {
                    strParam = pNodeTmp.getText();
                }
                if (ID2 > 0) {
                    C_Map_Item pMap = new C_Map_Item(ID2, pRun_Session, Function_Call);
                    String strType = pMap.Check_Type().toLowerCase();
                    int ID3 = 0;
                    switch (strType) {
                        case "node.var": {
                            pTable2 = pRun_Session.Read_Table(Function_Call, ID2);
                            ID3 = pTable2.Get_Pre_Job_ID();
                            strXML.append("<v").append(k).append(" param=\"").append(pTable2.Program_Param).append("\" ");
                            String strValue = "";
                            if (ID3 > 0) {
                                strValue = C_Map_Item.Make_Program(pTreap, pRun_Session, ID3);
                                strXML2.append(strValue);
                                strXML.append(" call=\"").append(ID3).append("\">");
                            } else if ("".equals(pTable2.Get_From_IDs())) {//如果 node.var 没有输入。
                                strXML.append(">");
                                strValue = pTable2.Get_Program();
                                if (strParam.equals("@name")) {//读取下一个节点的名称作为程序的内容
                                    strValue = S_Strings.CData_Encode(pTable2.Name, false);
                                }
                                strXML.append(S_Strings.CData_Encode(strValue, false));
                            } else {//如果有输入就当作一个String.Format节点
                                strValue = C_Map_Item.Make_Program_Table(pTreap, pRun_Session, ID2);
                                strXML2.append(strValue);
                                strXML.append(" call=\"").append(ID2).append("\">");
                            }
                            strXML.append("</v").append(k).append(">\n");
                            break;
                        }
                        case "node.function": {
                            C_Job pJob2 = pRun_Session.Read_Job(Function_Call, ID2);
                            ID3 = ID2;
                            strXML.append("<v").append(k);
                            String strValue = pJob2.Program;
                            if (ID3 > 0) {
                                strValue = C_Map_Item.Make_Program(pTreap, pRun_Session, ID3);
                                strXML2.append(strValue);
                                strXML.append(" call=\"").append(ID3).append("\">");
                            } else {
                                strXML.append(">");
                                if (strParam.equals("@name")) {//读取下一个节点的名称作为程序的内容
                                    strValue = S_Strings.CData_Encode(pJob2.Name, false);
                                }
                                strXML.append(S_Strings.CData_Encode(strValue, false));
                            }
                            strXML.append("</v").append(k).append(">\n");
                            break;
                        }
                    }
                } else//如果是0就从Program里读取
                {
                    if (pNodeTmp != null) {
                        strXML.append("<v").append(k).append(">");
                        strXML.append(S_Strings.CData_Encode(strParam, false));
                        strXML.append("</v").append(k).append(">\n");
                    } else {
                        strXML.append("<v").append(k).append("></v").append(k).append(">\n");
                    }
                }
            }
        } catch (DocumentException e) {
        }

        strXML.append("</input>\n");
        strXML.append("<fun>string.format</fun>\n");
        strXML.append("<output id=\"0\">");
        strXML.append(S_Strings.CData_Encode(pTable.Program, false));
        strXML.append("</output>\n");
        strXML.append("</item>\n");

        return strXML.toString() + "\n"
                + strXML2.toString();
    }

    /**
     * 获取函数，去掉分号和换行。
     *
     * @param Call_Count
     * @param strHead
     * @param pTreapVar
     * @param pTreap
     * @param pRun_Session
     * @param ID
     * @return
     */
    public static String Get_Program_No_End(
            int Call_Count,
            String strHead,
            Treap_Var pTreapVar,
            Treap pTreap,
            C_Run_Session pRun_Session,
            int ID) {
        String strLine = C_Map_Item.Make_Program_By_Time(Call_Count, strHead, pTreapVar, pTreap, pRun_Session, ID);
        if (strLine.endsWith("\n")) {
            strLine = S_Strings.cut_end_string(strLine, "\n");
        }
        if (strLine.endsWith(";")) {
            strLine = S_Strings.cut_end_string(strLine, ";");
        }
        return strLine;
    }

    /**
     * 生成顺序程序 (java)
     *
     * @param Call_Count
     * @param strHead
     * @param pTreapVar
     * @param pTreap
     * @param pRun_Session
     * @param ID
     * @return
     */
    public static String Make_Program_By_Time(
            int Call_Count,
            String strHead,
            Treap_Var pTreapVar,
            Treap pTreap,
            C_Run_Session pRun_Session,
            int ID) {

        int Function_Call = 0;
        if (pTreap.find(new C_K_Int(ID)) != null) {
            return "";
        }

        pTreap.insert(new C_K_Int(ID), ID);

        C_Job pJob = pRun_Session.Read_Job(Function_Call, ID);//获取task信息
        String[] strSplit = pJob.Name.split("\\|");
        String[] strSplit2 = pJob.strNexts.split(",");

        String strFunction = strSplit[0];

        StringBuilder strProgram = new StringBuilder();

        C_Item_Function pFun = pJob.GetInput(pTreapVar.pTreap);
        String strLine_Input = pFun.get_input_string();

        String[] strSplit1 = pJob.Get_To_IDs().split(",");
        String strOutput = "";
        ArrayList<C_Var> pArrayOutput = new ArrayList<>();
        boolean bNewVar = false;
        if ("".equals(pJob.Get_To_IDs())) {
            strOutput = "";
        } else {
            for (int i = 0; i < strSplit1.length; i++) {
                if ("".equals(strSplit1[i])) {
                    strOutput = "";
                    if (i > 0) {
                        System.out.println("有变量为空！");
                    }
                } else {
                    int ID2 = Integer.valueOf(strSplit1[i]);

                    C_Map_Item pMap = new C_Map_Item(ID2, pRun_Session, 0);
                    String strType = pMap.Check_Type().toLowerCase();
                    int iFunction = 0;
                    switch (strType) {
                        case "node.var":
                            strOutput = "v" + ID2;
                            break;
                        case "node.function":
                            iFunction = 1;
                            C_Job pFunction = pRun_Session.Read_Job(0, ID2);//获取task信息
                            strOutput = pFunction.Get_Function(pTreapVar.pTreap).toString();
                            break;
                    }
                    C_Var pVar = (C_Var) pTreapVar.pTreap.find(new C_K_Str(strOutput));
                    if (pVar == null) {
                        bNewVar = true;
                        if ("int".equals(pMap.ItemType)) {
                            pVar = new C_Var(pTreapVar, strOutput, "int", iFunction);
                        } else {
                            if ("".equals(pMap.ItemType)) {
                                pMap.ItemType = "string";
                            }
                            pVar = new C_Var(pTreapVar, strOutput, pMap.ItemType, iFunction);
                        }
                        pTreapVar.pTreap.insert(new C_K_Str(pVar.key), pVar);
                    }
                    pArrayOutput.add(pVar);
                }
            }
        }

        if (strFunction.startsWith("+")
                || strFunction.startsWith("-")
                || strFunction.startsWith("=")) {
            if (strOutput.equals("")) {

            } else if (pArrayOutput.size() > 0) {
                for (int i = 0; i < pArrayOutput.size(); i++) {
                    if ("+=".equals(strFunction)) {
                        System.out.println("");
                    }
                    C_Var pVar = pArrayOutput.get(i);
                    pFun.Function = strFunction;
                    String strLine = pFun.toString();// pJob.Get_Function(pTreapVar).toString();
                    strProgram.append(strHead);
                    if (pVar.iFunction == 1) {
                        strProgram.append(pVar.key);
                    } else if (bNewVar) {
                        switch (pVar.type) {
                            case "string":
                                strProgram.append("String s").append(pVar.ID);
                                break;
                            case "int":
                                strProgram.append("int i").append(pVar.ID);
                                break;
                            default:
                                strProgram.append(pVar.type).append(" v").append(pVar.ID);
                                break;
                        }
                    } else {
                        strProgram.append(pVar.Get_Var_Head()).append(pVar.ID);
                    }
                    switch (strFunction) {
                        case "+":
                        case "-":
                            pFun.Operator = strFunction;
                            strProgram.append("=").append(pFun.get_input_string()).append(";");
                            break;
                        case "++":
                            strProgram.append("++;");
                            break;
                        case "=":
                            strProgram.append("=").append(pFun.get_input_string()).append(";");
                            break;
                        case "+=":
                        case "-=":
                            strProgram.append(strLine).append(";");
                            break;
                        default:
                            strProgram.append("=").append(strLine).append(";");
                            break;
                    }
                }
            }
        } else if (strOutput.equals("")) {
            if (strFunction.startsWith("while")
                    || strFunction.startsWith("if")) {

                String strLine = pJob.Name;
                if (pFun.pInput.size() > 0) {
                    strLine = strLine.replace("@object", pFun.pInput.get(0));
                    for (int i = 0; i < pFun.pInput.size(); i++) {
                        int k = i + 1;
                        strLine = strLine.replace("#" + k, pFun.pInput.get(i));
                    }
                }
                strProgram.append(strHead).append(strLine);
            } else {
                strProgram.append(strHead)
                        .append(strFunction).append("(").append(strLine_Input).append(");");
            }
        } else {
            for (int i = 0; i < pArrayOutput.size(); i++) {
                C_Var pVar = pArrayOutput.get(i);
                strProgram.append(strHead);
                strProgram.append(pVar.Get_Var_Head());
                strProgram.append(pVar.ID).append("=")
                        .append(strFunction).append("(").append(strLine_Input).append(");");
            }
        }

        StringBuilder strProgram2 = new StringBuilder();

        int Count = strSplit2.length;
        if (!"".equals(pJob.strNexts)) {
            if (Count >= 1) {
                for (int i = 0; i < Count; i++) {
                    int Next_ID = Integer.valueOf(strSplit2[i]);

                    if (Count > 1 && i < Count - 1) {
                        strProgram2.append(strHead).append("{\n");
                        strProgram2.append(Make_Program_By_Time(Call_Count + 1, strHead + "    ", pTreapVar, pTreap, pRun_Session, Next_ID));
                        strProgram2.append(strHead).append("}\n");
                    } else {
                        strProgram2.append(Make_Program_By_Time(Call_Count + 1, strHead, pTreapVar, pTreap, pRun_Session, Next_ID));
                    }
                }
            }
        }
        if (Call_Count == 0) {
            //变量的定义和原始名称
            TreapEnumerator p = pTreapVar.pTreap.Elements();
            StringBuilder strDim = new StringBuilder();
            while (p.HasMoreElements()) {
                C_Var pVar = (C_Var) p.NextElement();
                strDim.append("//").append(pVar.ID).append("=").append(pVar.key).append("\n");
            }
            return strDim.toString() + "\n"
                    + strProgram.toString() + "\n"
                    + strProgram2.toString();
        } else {

            return strProgram.toString() + "\n"
                    + strProgram2.toString();
        }
    }

    /**
     * 生成程序
     *
     * @param pRun_Session
     * @param ID
     * @return
     */
    public static String Make_Android_UI(
            C_Run_Session pRun_Session,
            int ID) {

        int Function_Call = 0;

        C_Job pJob = pRun_Session.Read_Job(Function_Call, ID);//获取task信息

        String[] strSplit1 = pJob.Get_From_IDs().split(",");

        String strType = pJob.ItemType;/// Check_Type().toLowerCase();
        String strHead = "";
        String strEnd = "";

        switch (strType) {
            case "apk.linearlayout":
                strHead = "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n"
                        + "              android:layout_width=\"fill_parent\" \n"
                        + "              android:layout_height=\"fill_parent\" \n"
                        + "              android:orientation=\"" + pJob.Program_Param + "\" >";
                strEnd = "</LinearLayout>";
                break;
            case "apk.text":
                strHead = "<TextView\n"
                        + "    android:layout_width=\"fill_parent\"\n"
                        + "    android:layout_height=\"wrap_content\"\n"
                        + "    android:text=\"" + pJob.Name + "\"\n"
                        + "    >";
                strEnd = "</TextView>";
                break;
            case "apk.button":
                strHead = "<Button\n"
                        + "        android:id=\"@+id/button_camera\"\n"
                        + "        android:layout_width=\"wrap_content\"\n"
                        + "        android:layout_height=\"wrap_content\"\n"
                        + "        android:text=\"" + pJob.Name + "\"\n"
                        + "        android:layout_gravity=\"center_horizontal\" >";
                strEnd = "</Button>";
                break;
        }

        StringBuilder strXML = new StringBuilder();
        strXML.append(strHead).append("\n");
        for (int i = 0; i < strSplit1.length; i++) {
            int ID2 = S_Strings.getIntFromStr(strSplit1[i], 0);//输入的ID

            if (ID2 > 0) {
                String strItem = Make_Android_UI(pRun_Session, ID2);
                strXML.append(strItem).append("\n");

            }
        }

        strXML.append(strEnd).append("\n");

        return strXML.toString();
    }

    /*
    * 读取Java代码
     */
    public static String Read_Java(int ID) {
        String strURL =  AI_Var2.Site
                + "/funnyscript/read_java.php?id=" + ID;

        String strReturn = S_Net.http_GET(strURL,"", "utf-8", "",20);
        return strReturn;
    }

    /**
     * 编译Java程序，抽象一下以后可以用来编译任何语言。
     *
     * @param pRun_Session
     * @param Template_Compile 编译脚本模板
     * @param ID
     * @return
     */
    public static String Compile_XXX(
            C_Run_Session pRun_Session,
            String Template_Compile,
            int ID) {

        String url= AI_Var2.Site+"/funnyscript/get_files.php?id="+ID;
        String strFiles=S_Net.http_GET(url,"", "utf-8", "",20);
        
        int index=strFiles.indexOf(":");
        if (index>0){
            strFiles=strFiles.substring(index+1);
        }
        strFiles=strFiles.replace("\r", "");
        strFiles=strFiles.replace("\n", "");
        
        String strCode=strFiles.replace("|", " ");
        String strCommand =Template_Compile;
        strCommand=strCommand.replace("[[[code]]]", strCode);
        strCommand=strCommand.replace("[[[ID]]]", ID+"");
        String strReturn = "";
        try {
            String strFile_Node=AI_Var2.Path_Shell+"node_"+ID+".sh";
            Tools.File_Save_Content(1,strFile_Node,strCommand);
            strCommand=strFile_Node+" 1";
            System.out.println(strCommand);
            C_Job pJob=new C_Job(ID,pRun_Session,0);
            strReturn=pJob.Run_Shell_Command(true,pRun_Session,strCommand,"utf-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return strReturn;
    }
    
//    public static String Compile_Scala(
//            C_Run_Session pRun_Session,
//            int ID) {
//
//        String url= AI_Var2.Site+"/funnyscript/get_files.php?id="+ID;
//        String strFiles=S_Net.http_GET(url,"", "utf-8", "",20);
//        
//        int index=strFiles.indexOf(":");
//        if (index>0){
//            strFiles=strFiles.substring(index+1);
//        }
//        strFiles=strFiles.replace("\r", "");
//        strFiles=strFiles.replace("\n", "");
//        
//        String strCode=strFiles.replace("|", " ");
//        
//        String strCommand = "source ./shell/lib/library.sh\n"
//                + "project=\"fs\"\n"
//                + "cd " + AI_Var2.Path_Root + "\n"
//                + "scalac s"+ID+".scala "+strFiles+"\n";
//        String strReturn = "";
//        try {
//            String strFile_Node=AI_Var2.Path_Shell+"node_"+ID+".sh";
//            Tools.File_Save_Content(1,strFile_Node,strCommand);
//            strCommand=strFile_Node+" 1";
//            System.out.println(strCommand);
//            C_Job pJob=new C_Job(ID,pRun_Session,0);
//            strReturn=pJob.Run_Shell_Command(pRun_Session,true,strCommand,"utf-8");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        return strReturn;
//    }
    
    
//    public static String Compile_Go(
//            C_Run_Session pRun_Session,
//            int ID) {
//
//        String url= AI_Var2.Site+"/funnyscript/get_files.php?id="+ID;
//        String strFiles=S_Net.http_GET(url,"", "utf-8", "",20);
//        
//        int index=strFiles.indexOf(":");
//        if (index>0){
//            strFiles=strFiles.substring(index+1);
//        }
//        strFiles=strFiles.replace("\r", "");
//        strFiles=strFiles.replace("\n", "");
//        
//        String strCode=strFiles.replace("|", " ");
//        
//        String strCommand = "source ./shell/lib/library.sh\n"
//                + "project=\"fs\"\n"
//                + "cd " + AI_Var2.Path_Root + "\n"
//                + "go build "+ID+".go "+strFiles+"\n";
//        String strReturn = "";
//        try {
//            String strFile_Node=AI_Var2.Path_Shell+"node_"+ID+".sh";
//            Tools.File_Save_Content(1,strFile_Node,strCommand);
//            strCommand=strFile_Node+" 1";
//            System.out.println(strCommand);
//            C_Job pJob=new C_Job(ID,pRun_Session,0);
//            strReturn=pJob.Run_Shell_Command(pRun_Session,true,strCommand,"utf-8");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        return strReturn;
//    }
    
    
//    
//    
//    /**
//     * 运行Python
//     * @param pRun_Session
//     * @param ID
//     * @param strParam
//     * @return 
//     */
//    public static String Run_Python(
//            C_Run_Session pRun_Session,
//            int ID,
//            String strParam) {
//
//        //"+AI_Var2.Path_Root+"lib/common_newjava.jar:
//        String strCommand="source ./shell/lib/library.sh\n" +
//"project=\"fs\"\n" +
//"cd "+AI_Var2.Path_Root +"\n" +
//"echo $common\n" +
//"python "+ID+".py $1 $2 $3";
//        String strReturn = "";
//        try {
//            String strFile_Node=AI_Var2.Path_Shell+"run_python_"+ID+".sh";
//            Tools.File_Save_Content(1,strFile_Node,strCommand);
//            strCommand=strFile_Node+" "+strParam;
//            System.out.println(strCommand);
//            C_Job pJob=new C_Job(ID,pRun_Session,0);
//            strReturn=pJob.Run_Shell_Command(true,pRun_Session,strCommand,"utf-8");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        return strReturn;
//    }



    /**
     * 运行java
     *
     * @param pRun_Session
     * @param ID
     * @param strCommand
     * @return
     */
    public static String Run_XXX(
            C_Run_Session pRun_Session,
            int ID,
            String strCommand) {

        //"+AI_Var2.Path_Root+"lib/common_newjava.jar:
        String strReturn = "";
        try {
            String strFile_Node=AI_Var2.Path_Shell+"run_xxx_"+ID+".sh";
            Tools.File_Save_Content(1,strFile_Node,strCommand);
            //strCommand=strFile_Node+" "+strParam;
            System.out.println(strCommand);
            C_Job pJob=new C_Job(ID,pRun_Session,0);
            strReturn=pJob.Run_Shell_Command(true,pRun_Session,strFile_Node,"utf-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return strReturn;
    }
    
//    public static String Run_Go(
//            C_Run_Session pRun_Session,
//            int ID,
//            String strParam) {
//
//        //"+AI_Var2.Path_Root+"lib/common_newjava.jar:
//        String strCommand="source ./shell/lib/library.sh\n" +
//"project=\"fs\"\n" +
//"cd "+AI_Var2.Path_Root +"\n" +
//"./"+ID+" $1 $2 $3";
//        String strReturn = "";
//        try {
//            String strFile_Node=AI_Var2.Path_Shell+"run_go_"+ID+".sh";
//            Tools.File_Save_Content(1,strFile_Node,strCommand);
//            strCommand=strFile_Node+" "+strParam;
//            System.out.println(strCommand);
//            C_Job pJob=new C_Job(ID,pRun_Session,0);
//            strReturn=pJob.Run_Shell_Command(pRun_Session,true,strCommand,"utf-8");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        return strReturn;
//    }
    
    
//    public static String Run_Shell(
//            C_Run_Session pRun_Session,
//            int ID,
//            String strParam) {
//
//        //. /root/happyli/shell/lib/library.sh
//        String strCommand="source ./shell/lib/library.sh\n" +
//"project=\"fs\"\n" +
//"cd "+AI_Var2.Path_Root +"\n" +
//"sh ./s"+ID+".sh $1 $2 $3";
//        String strReturn = "";
//        try {
//            String strFile_Node=AI_Var2.Path_Shell+"run_shell_"+ID+".sh";
//            Tools.File_Save_Content(1,strFile_Node,strCommand);
//            strCommand=strFile_Node+" "+strParam;
//            System.out.println(strCommand);
//            C_Job pJob=new C_Job(ID,pRun_Session,0);
//            strReturn=pJob.Run_Shell_Command(pRun_Session,true,strCommand,"utf-8");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        return strReturn;
//    }
    
//    public static String Run_Scala(
//            C_Run_Session pRun_Session,
//            int ID,
//            String strParam) {
//
//        //"+AI_Var2.Path_Root+"lib/common_newjava.jar:
//        String strCommand="source ./shell/lib/library.sh\n" +
//"project=\"fs\"\n" +
//"cd "+AI_Var2.Path_Root +"\n" +
//"./scala s"+ID+" $1 $2 $3";
//        String strReturn = "";
//        try {
//            String strFile_Node=AI_Var2.Path_Shell+"run_scala_"+ID+".sh";
//            Tools.File_Save_Content(1,strFile_Node,strCommand);
//            strCommand=strFile_Node+" "+strParam;
//            System.out.println(strCommand);
//            C_Job pJob=new C_Job(ID,pRun_Session,0);
//            strReturn=pJob.Run_Shell_Command(pRun_Session,true,strCommand,"utf-8");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        return strReturn;
//    }


    
    public C_Map_Item_Connect Get_Pre_Job_ID_V3() {

        ArrayList<C_Map_Item_Connect> pList2 = C_Map_Item_Connect.Get_Connects_To(this.ID);
        for (int i = 0; i < pList2.size(); i++) {
            return pList2.get(i);
        }
        return null;
    }
    
    public int Get_Pre_Job_ID() {

        String strURL =  AI_Var2.Site
                + "/funnyscript/json_read_pre_node_from_table_id.php?id=" + this.ID;
        String strData = "";
        String strJSON = S_Net.http_GET(strURL,strData, "utf-8", "",20);
        int index = strJSON.indexOf("{");
        strJSON = strJSON.substring(index);
        JSONObject pObj = new JSONObject(strJSON);

        JSONArray pIDS = pObj.getJSONArray("data");
        for (int i = 0; i < pIDS.length(); i++) {
            int ID2 = pIDS.getJSONObject(0).getInt("id");
            return ID2;
        }

        ArrayList<C_Map_Item_Connect> pList2 = C_Map_Item_Connect.Get_Connects_To(this.ID);
        for (int i = 0; i < pList2.size(); i++) {
            int ID2 = pList2.get(i).From_ID;
            return ID2;
        }
        return 0;
    }

    public int Get_Pre_Job_ID(String strType) {

        ArrayList<C_Map_Item_Connect> pList2 = C_Map_Item_Connect.Get_Connects_To(this.ID);
        for (int i = 0; i < pList2.size(); i++) {
            int ID2 = pList2.get(i).From_ID;
            if (pList2.get(i).To_Port.startsWith(strType)) {
                return ID2;
            }
        }
        return 0;
    }

    /**
     * 子程序
     *
     * @param pTreap_Make 看看是否已经生成过这个函数或代码
     * @param pRun_Session
     * @param pItem
     * @param strParam
     * @param ID
     * @param Function_Call
     * @return
     */
    public static String Make_Program_V3_Loop(
            Treap<String> pTreap_Make,
            C_Run_Session pRun_Session,
            C_Map_Item pItem,
            String strParam,
            int ID,
            int Function_Call) {
        
        //如果已经生成，不需要再生成！
        if (pTreap_Make.find(new C_K_Str("loop_"+ID)) != null) {
            return "";
        }
        pTreap_Make.insert(new C_K_Str("loop_"+ID), "loop_"+ID);
        
        StringBuilder strCode2 = new StringBuilder();
        StringBuilder strCode = new StringBuilder();

        Treap<C_Var_V3> pTreap_NewVar = new Treap<>();
        Treap<C_Var_V3> pTreap_NewVar_Boolean = new Treap<>();

        //查找循环的输入变量和类型
        ArrayList<C_Map_Item_Connect> pList_Left = C_Map_Item_Connect.Get_Connects_To_Left(ID);//读取所有的输入箭头
        if (pList_Left.size()>0){
            int ID2=pList_Left.get(0).From_ID;
            ArrayList<C_Map_Item_Connect> pList = C_Map_Item_Connect.Get_Connects_To(ID2);//读取所有的输入箭头
            if (pList.size()>0){
                int ID3=pList.get(0).From_ID;
                C_Map_Item pItem2 = new C_Map_Item(ID3, pRun_Session, Function_Call);//获取节点信息
                if ("loop.start".equals(pItem2.ItemType)){
                    ArrayList<C_Map_Item_Connect> pList_Top = C_Map_Item_Connect.Get_Connects_To(ID3);//读取所有的输入箭头
                    Treap<C_Var_V3> pTreap_Input=new Treap<>();
                    for (int k=0;k<pList_Top.size();k++){
                        C_Map_Item_Connect pConnect2=pList_Top.get(k);
                        C_Map_Item pItem3 = new C_Map_Item(pConnect2.From_ID, pRun_Session, Function_Call);//获取节点信息
                        String strVar="var_loop_"+pConnect2.To_Port_Index();
                        C_Var_V3 pVar=new C_Var_V3(strVar,pItem3.Get_Type(),null);
                        pTreap_NewVar.insert(new C_K_Str(strVar), pVar);
                        pTreap_Input.insert(new C_K_Int(pConnect2.To_Port_Index()), pVar);
                    }
                    for (int i=0;i<6;i++){
                        String strVar="var_loop_"+i;
                        if (pTreap_Input.find(new C_K_Int(i))==null){
                            String strVar_Type="String";
                            if (i==0) strVar_Type="int";
                            C_Var_V3 pVar=new C_Var_V3(strVar,strVar_Type,null);
                            pTreap_NewVar.insert(new C_K_Str(strVar), pVar);
                            pTreap_Input.insert(new C_K_Int(i), pVar);
                        }
                    }
                }
            }
        }
        
        ArrayList<C_Map_Item_Connect> pList2 = C_Map_Item_Connect.Get_Connects_To(ID);
        for (int j = 0; j < pList2.size(); j++) {
            C_Map_Item_Connect pConnect2 = pList2.get(j);
            if (pConnect2.To_Port.startsWith("top")) {
                int index=pConnect2.To_Port_Index();// S_Strings.getIntFromStr(pConnect2.To_Port.substring(3),0);
                C_Map_Item pItem2 = new C_Map_Item(pConnect2.From_ID, pRun_Session, Function_Call);

                switch (pItem2.DrawType.toLowerCase()) {
                    case "node.var":
                        switch(pItem2.ItemType){
                            case "text":
                                if (true){
                                    String strProgram = Get_String(pItem2.Program);
                                    Matcher m = S_Strings.match("\\{@(\\d+)\\}",strProgram); // 获取 matcher 对象
                                    strCode.append("        a[").append(index).append("]=\"").append(strProgram).append("\";\n");
                                    while(m.find()) {
                                        strCode.append("        a[").append(index).append("]=S_string.replace(a[").append(index).append("],\"").append(m.group(0)).append("\",sys_args[").append(m.group(1)).append("]);\n");
                                    }
                                    ArrayList<C_Map_Item_Connect> pList3 = C_Map_Item_Connect.Get_Connects_To(pItem2.ID);//读取连接
                                    for (int k = 0; k < pList3.size(); k++) {
                                        C_Map_Item_Connect pConnect3 = pList3.get(k);
                                        if (pConnect3.To_Port.startsWith("top")){
                                            int index2=S_Strings.getIntFromStr(pConnect3.To_Port.substring(3),0);
                                            strCode.append("        var_").append(pConnect3.From_ID).append("=f_").append(pConnect3.From_ID).append("();\n");
                                            strCode.append("        a[").append(index).append("]=S_string.replace(a[").append(index).append("],\"{").append(index2).append("}\",var_").append(pConnect3.From_ID).append(");\n");
                                            strCode2.append(C_Map_Item.Make_Program_V3_Function(pTreap_Make,pTreap_NewVar,pTreap_NewVar_Boolean, pRun_Session, pConnect3.From_ID));
                                        }
                                    }
                                }
                                break;
                            case "loop.start":
                                
                                break;
                            default:
                                {
                                    int ID3 = pItem2.Get_Pre_Job_ID();
                                    C_Map_Item pItem3 = new C_Map_Item(ID3, pRun_Session, Function_Call);
                                    switch (pItem3.DrawType) {
                                        case "node.function"://如果是一个函数
                                            strCode2.append(C_Map_Item.Make_Program_V3_Function(pTreap_Make,pTreap_NewVar,pTreap_NewVar_Boolean, pRun_Session, pItem3.ID));
                                            strCode.append("        var2.var_loop_").append(index).append("=f_").append(pItem3.ID).append("();\n");
                                            break;
                                        default:
                                            strCode.append("        var2.var_loop_").append(index).append("=var_").append(pItem2.ID).append(";\n");
                                            break;
                                    }
                                }
                                break;
                        }
                        break;
                    default:
                        strCode.append("        var2.var_loop_").append(index).append("=f_").append(pItem2.ID).append("();\n");
                        strCode2.append(C_Map_Item.Make_Program_V3_Function(pTreap_Make,pTreap_NewVar,pTreap_NewVar_Boolean, pRun_Session, pItem2.ID));
                        break;
                }
            }
        }

        String strVars = "";
        String strVars_Sub_Class = "";
        TreapEnumerator<C_Var_V3> p = pTreap_NewVar.Elements();
        while (p.HasMoreElements()) {
            C_Var_V3 pVar = p.NextElement();
            if (pVar.Name.startsWith("var_loop_")){
                switch(pVar.Type){
                    case "String":
                        strVars_Sub_Class += "        public "+pVar.Type+" " + pVar.Name + "=\"\";\n";
                        break;
                    case "Boolean":
                        strVars_Sub_Class += "        public "+pVar.Type+" " + pVar.Name + "=false;\n";
                        break;
                    case "int":
                        strVars_Sub_Class += "        public "+pVar.Type+" " + pVar.Name + "=0;\n";
                        break;
                    default:
                        strVars_Sub_Class += "        public "+pVar.Type+" " + pVar.Name + ";\n";
                        break;
                }
                
            }else{
                switch(pVar.Type){
                    case "String":
                        strVars += "    public "+pVar.Type+" " + pVar.Name + "=\"\";\n";
                        break;
                    case "Boolean":
                        strVars += "    public "+pVar.Type+" " + pVar.Name + "=false;\n";
                        break;
                    default:
                        strVars += "    public "+pVar.Type+" " + pVar.Name + ";\n";
                        break;
                }
            }
        }
        
        String strVars_Boolean="";
        p = pTreap_NewVar_Boolean.Elements();
        while (p.HasMoreElements()) {
            C_Var_V3 pVar = p.NextElement();
            strVars_Boolean += "        " + pVar.Name + "=false;\n";
        }

        String strVar_Loop_Copy="";
        for (int i=0;i<6;i++){
            strVar_Loop_Copy+="        var2.var_loop_"+i+"=var.var_loop_"+i+";\n";
        }
        String strCodeLoop = "\n"
                + "package com.funnyai.test;\n"
                + "import com.funnyai.io.*;\n"
                + "import com.funnyai.math.*;\n"
                + "import com.funnyai.string.*;\n"
                + "import org.json.*;\n"
                + "import java.util.regex.*;\n"
                + "import java.util.*;\n"
                + "\n"
                + "public class Loop_" + ID + " {\n"
                + "    public class Loop_" + ID + "_Return {\n"
                +strVars_Sub_Class
                +"    }\n"
                + strVars
                + "    public Loop_" + ID + "_Return var=null;\n"
                + "\n"
                + "    public void clear(){\n" 
                + strVars_Boolean
                + "    }\n"
                + "    public Loop_" + ID + "_Return Loop(Loop_" + ID + "_Return var) {\n"
                + "        Loop_" + ID + "_Return var2=new Loop_" + ID + "_Return();\n"
                + strVar_Loop_Copy
                +"        this.var=var;\n"
                + strCode + ";\n"
                + "        return var2;\n"
                + "   }\n"
                + "\n"
                + strCode2
                + "}";
        System.out.println("=============");
        System.out.println(strCodeLoop);
        String strFile = AI_Var2.Path_FromRoot("java/fs/Loop_" + ID + ".java");
        S_File_Text.Write(strFile,strCodeLoop);
        System.out.println("=============");
        return strCode.toString();
    }

    /**
     * 生成程序 调用Loop类
     *
     * @param pTreap_Make 代码是否生成过
     * @param pTreap_NewVar 函数需要的新变量
     * @param pTreap_NewVar_Boolean
     * @param pRun_Session
     * @param ID_Loop_End
     * @param ID
     * @return
     */
    public static String Make_Program_V3_Call_Loop(
            Treap<String> pTreap_Make,
            Treap<C_Var_V3> pTreap_NewVar,
            Treap<C_Var_V3> pTreap_NewVar_Boolean,
            C_Run_Session pRun_Session,
            int ID_Loop_End,
            int ID) {
        int Function_Call = 0;
        if (pTreap_Make.find(new C_K_Str("call_loop_"+ID)) != null) {
            return "";
        }
        pTreap_Make.insert(new C_K_Str("call_loop_"+ID), "call_loop_"+ID);
        
        C_Job pJob = pRun_Session.Read_Job(Function_Call, ID);//获取task信息

        StringBuilder strCode = new StringBuilder();
        StringBuilder strCode2 = new StringBuilder();

        int ID_Loop_Start = pJob.Get_Pre_Job_ID();

        ArrayList<C_Map_Item_Connect> pList = C_Map_Item_Connect.Get_Connects_To(ID_Loop_Start);//读取连接到循环的输入节点。

        C_Var_V3 pVar_Loop=new C_Var_V3("pLoop_"+ID_Loop_End,"Loop_"+ID_Loop_End,"new Loop_"+ID_Loop_End+"()");
        pTreap_NewVar.insert(new C_K_Str(pVar_Loop.Name),pVar_Loop);
        
        
        C_Var_V3 pVar=new C_Var_V3("b_"+ID,"Boolean",false);
        pTreap_NewVar_Boolean.insert(new C_K_Str(pVar.Name),pVar);
        
        strCode.append("    public boolean b_").append(ID).append("=false;\n")
               .append("    public void loop_").append(ID).append("(){\n")
               .append("        if (b_").append(ID).append(") return ;\n")
               .append("        b_").append(ID).append("=true;\n")
               .append("        Loop_").append(ID_Loop_End).append(".Loop_").append(ID_Loop_End)
               .append("_Return a = new Loop_").append(ID_Loop_End).append("().new Loop_").append(ID_Loop_End).append("_Return();\n");

        for (int i = 0; i < pList.size(); i++) {
            int k = i + 1;
            C_Map_Item_Connect pConnect = pList.get(i);
            int ID2 = pConnect.From_ID;//输入的ID

            if (ID2 > 0) {
                C_Map_Item pItem2 = new C_Map_Item(ID2, pRun_Session, Function_Call);
                if (pConnect.To_Port.startsWith("top")) {
                    int index = pConnect.To_Port_Index();// S_Strings.getIntFromStr(pConnect.To_Port.substring(3), 0);
                    int ID3 = pItem2.Get_Pre_Job_ID();
                    if (ID3 > 0) {
                        C_Map_Item pItem3 = new C_Map_Item(ID3, pRun_Session, Function_Call);
                        switch (pItem3.DrawType) {
                            case "node.function"://如果是一个函数
                                strCode2.append(C_Map_Item.Make_Program_V3_Function(pTreap_Make,pTreap_NewVar,pTreap_NewVar_Boolean, pRun_Session, pItem3.ID));
                                strCode.append("        f_").append(pItem3.ID).append("();\n");
                                break;
                            default:
                                break;
                        }
                        C_Var_V3 pVar2=new C_Var_V3("var_"+ID2,pItem2.Get_Type(),"");
                        pTreap_NewVar.insert(new C_K_Str(pVar2.Name),pVar2);
                        strCode.append("        a.var_loop_").append(index).append("=").append(pVar2.Name).append(";\n");
                    } else {
                        strCode.append("        a.var_loop_").append(index).append("=\"").append(pItem2.Name).append("\";\n");
                    }
                } else {
                    out.println("error!");
                }
            }
        }

        String strCondition=pJob.Name;
        for (int i=0;i<9;i++){
            strCondition=strCondition.replace("$"+i,"a.var_loop_"+i);
        }
        strCode.append("        a.var_loop_0=0;\n")
               .append("        pLoop_").append(ID_Loop_End).append(".var=a;\n")
               .append("        while (").append(strCondition).append("){\n")
               .append("            pLoop_").append(ID_Loop_End).append(".clear();\n")
               .append("            a=pLoop_").append(ID_Loop_End).append(".Loop(a);\n")
               .append("            a.var_loop_0++;\n")
               .append("        }\n")
               .append("    }\n");

        return strCode.toString() + "\n"
                + strCode2.toString() + "\n";
    }

    /**
     * 生成函数
     *
     * @param pTreap_Make 如果代码已经生成，这个集合里就会有标记
     * @param pTreap_NewVar 函数需要的新变量 var_xxx
     * @param pTreap_NewVar_Boolean 函数需要的boolean变量 bRun_xxx
     * @param pRun_Session
     * @param ID
     * @return
     */
    public static String Make_Program_V3_Function(
            Treap<String> pTreap_Make,
            Treap<C_Var_V3> pTreap_NewVar,
            Treap<C_Var_V3> pTreap_NewVar_Boolean,
            C_Run_Session pRun_Session,
            int ID) {

        out.println("function="+ID);
        int Function_Call = 0;
        if (pTreap_Make.find(new C_K_Str("f_"+ID)) != null) {
            return "";
        }
        out.println("function.1="+ID);
        if (ID==2363){
            out.println("break");
        }
        pTreap_Make.insert(new C_K_Str("f_"+ID), "f_"+ID);
        
        C_Map_Item pItem = new C_Map_Item(ID, pRun_Session, Function_Call);//获取task信息

        ArrayList<C_Map_Item_Connect> pList = C_Map_Item_Connect.Get_Connects_To(ID);//读取所有的输入箭头
        ArrayList<C_Map_Item_Connect> pList_Out = C_Map_Item_Connect.Get_Connects_From(ID);//读取所有的输出箭头
        
        int Var_Count=1;//变量个数
        for (int i = 0; i < pList.size(); i++) {
            C_Map_Item_Connect pConnect = pList.get(i);
            if (pConnect.To_Port_Index()>=Var_Count){
                Var_Count=pConnect.To_Port_Index()+1;
            }
        }
        
        
        String strVar = "var_" + ID;
        String strVar_Type="String";
        //所有的输出类型，只要有一个设置了#，就用这个类型
        for (int i = 0; i < pList_Out.size(); i++) {
            C_Map_Item_Connect pConnect = pList_Out.get(i);
            C_Map_Item pItem3 = new C_Map_Item(pConnect.To_ID, pRun_Session, Function_Call);
            if (pItem3.Name.startsWith("#")){
                strVar_Type=pItem3.Name.substring(1);
            }
        }
        pItem.Var_Type=strVar_Type;//存储一下，以后其他输出需要，可以读取这个！
        C_Var_V3 pVar=new C_Var_V3(strVar,pItem.Var_Type,null);
        pTreap_NewVar.insert(new C_K_Str(strVar), pVar);
        
        String strFunction = pItem.Name;//函数名称

        StringBuilder strCode = new StringBuilder();
        
        C_Var_V3 pVar2=new C_Var_V3("b_"+ID,"Boolean",false);
        pTreap_NewVar_Boolean.insert(new C_K_Str(pVar2.Name),pVar2);
        strCode.append("    public boolean ").append(pVar2.Name).append("=false;\n" 
                + "    public ").append(pItem.Var_Type).append(" f_").append(ID).append("(){\n"
                + "        if (").append(pVar2.Name).append(") return var_").append(ID).append(";\n"
                + "        ").append(pVar2.Name).append("=true;\n");

        if (strFunction.startsWith("s.")){
            strCode.append("        String[] a = new String[").append(Var_Count).append("];\n");
            String SubClass=strFunction.substring(2);
            strCode.append("        C_").append(SubClass).append(" p=new C_").append(SubClass).append("();\n");
        }else{
            strCode.append("        Object[] a = new Object[").append(Var_Count).append("];\n");
        }
        
        
        StringBuilder strCode2 = new StringBuilder();

        for (int i = 0; i < pList.size(); i++) {
            C_Map_Item_Connect pConnect = pList.get(i);
            int index=pConnect.To_Port_Index();
            int ID2 = pConnect.From_ID;//输入的ID
            if (ID2 > 0) {
                C_Map_Item pItem2 = new C_Map_Item(ID2, pRun_Session, Function_Call);
                switch (pItem2.DrawType.toLowerCase()) {
                    case "node.var":
                        switch (pItem2.ItemType.toLowerCase()) {
                            case "loop.start":
                                if (pConnect.From_Port.startsWith("bottom")) {
                                    strCode.append("        a[").append(index).append("]=var.var_loop_").append(pConnect.From_Port_Index()).append(";\n");
                                } else {
                                    out.println("error!");
                                }
                                break;
                            case "loop.end": 
                                if (true){
                                    int ID3 = pItem2.Get_Pre_Job_ID("left");
                                    strCode2.append(C_Map_Item.Make_Program_V3_Call_Loop(pTreap_Make,pTreap_NewVar,pTreap_NewVar_Boolean, pRun_Session, ID2, ID3));
                                    C_Map_Item.Make_Program_V3_Loop(pTreap_Make,pRun_Session, pItem2,"",ID2, Function_Call);
                                    if (pConnect.From_Port.startsWith("bottom")) {
                                        strCode.append("        loop_").append(ID3).append("();//调用循环\n");

                                        strCode.append("        a[").append(index).append("]=pLoop_").append(ID2).append(".var.var_loop_").append(pConnect.From_Port_Index()).append(";\n");
                                    } else {
                                        out.println("error!");
                                    }
                                }
                                break;
                            case "text":
                                if (true){
                                    String strProgram = Get_String(pItem2.Program);
                                    Pattern p = Pattern.compile("\\{@(\\d+)\\}");
                                    Matcher m = p.matcher(strProgram); // 获取 matcher 对象
                                    strCode.append("        a[").append(index).append("]=\"").append(strProgram).append("\";\n");
                                    while(m.find()) {
                                        strCode.append("        a[").append(index).append("]=S_string.replace(a[").append(index).append("],\"").append(m.group(0)).append("\",sys_args[").append(m.group(1)).append("]);\n");
                                    }
                                    ArrayList<C_Map_Item_Connect> pList2 = C_Map_Item_Connect.Get_Connects_To(ID2);//读取连接
                                    for (int j = 0; j < pList2.size(); j++) {
                                        C_Map_Item_Connect pConnect2 = pList2.get(j);
                                        if (pConnect2.To_Port.startsWith("top")){
                                            int index2=pConnect2.To_Port_Index();
                                            strCode.append("        var_").append(pConnect2.From_ID).append("=f_").append(pConnect2.From_ID).append("();\n");
                                            strCode.append("        a[").append(index).append("]=S_string.replace(a[").append(index).append("],\"{").append(index2).append("}\",var_").append(pConnect2.From_ID).append(");\n");
                                            strCode2.append(C_Map_Item.Make_Program_V3_Function(pTreap_Make,pTreap_NewVar,pTreap_NewVar_Boolean, pRun_Session, pConnect2.From_ID));
                                        }
                                    }
                                }
                                break;
                            default: 
                                if (true){
                                    C_Map_Item_Connect pConnect3 = pItem2.Get_Pre_Job_ID_V3();
                                    if (pConnect3.From_ID > 0) {
                                        C_Map_Item pItem3 = new C_Map_Item(pConnect3.From_ID, pRun_Session, Function_Call);
                                        switch (pItem3.DrawType) {
                                            case "node.function"://如果是一个函数
                                                strCode2.append(C_Map_Item.Make_Program_V3_Function(pTreap_Make, pTreap_NewVar,pTreap_NewVar_Boolean, pRun_Session, pItem3.ID));
                                                strCode.append("        f_").append(pItem3.ID).append("();\n");
                                                strCode.append("        a[").append(index).append("]=var_").append(pItem2.ID).append(";\n");
                                                break;
                                            default:
                                                String strVar3="var_"+ID2;
                                                C_Var_V3 pVar3=new C_Var_V3(strVar3,pItem2.Get_Type(),null);
                                                pTreap_NewVar.insert(new C_K_Str(pVar3.Name), pVar3);
                                                switch(pItem3.ItemType){
                                                    case "loop.start":
                                                        String strVar_Loop="var_loop_"+pConnect3.From_Port_Index();
                                                        C_Var_V3 pVarLoop=pTreap_NewVar.find(new C_K_Str(strVar_Loop));
                                                        if (pVar3.Type.equals(pVarLoop.Type)){
                                                            strCode.append("        ").append(strVar3).append("=var.").append(strVar_Loop).append(";\n");
                                                        }else{
                                                            if ("int".equals(pVar3.Type) && "String".equals(pVarLoop.Type)){
                                                                strCode.append("        ").append(strVar3).append("=S_string.getIntFromStr(var.").append(strVar_Loop).append(",0);\n");
                                                            }else{
                                                                strCode.append("        ").append(strVar3).append("=(").append(pVar3.Type).append(")var.").append(strVar_Loop).append(";\n");
                                                            }
                                                        }
                                                        break;
                                                }
                                                strCode.append("        a[").append(index).append("]=").append(pVar3.Name).append(";\n");
                                                break;
                                        }
                                    } else if ("table".equals(pItem2.ItemType)) {
                                        if (strFunction.equals("t.format")) {
                                            strFunction = "t.format_table";
                                        }
                                        String strProgram = Get_String(pItem2.Program);
                                        Pattern p = Pattern.compile("\\{@(\\d+)\\}");
                                        Matcher m = p.matcher(strProgram); // 获取 matcher 对象
                                        strCode.append("        a[").append(index).append("]=\"").append(strProgram).append("\";\n");
                                        while(m.find()) {
                                            strCode.append("        a[").append(index).append("]=S_string.replace(a[").append(index).append("],\"").append(m.group(0)).append("\",sys_args[").append(m.group(1)).append("]);\n");
                                        }
                                    } else {
                                        Pattern p = Pattern.compile("\\{@(\\d+)\\}");
                                        Matcher m = p.matcher(pItem2.Name); // 获取 matcher 对象
                                        strCode.append("        a[").append(i).append("]=\"").append(pItem2.Name).append("\";\n");
                                        while(m.find()) {
                                            strCode.append("        a[").append(index).append("]=S_string.replace(a[").append(index).append("],\"").append(m.group(0)).append("\",sys_args[").append(m.group(1)).append("]);\n");
                                        }
                                        strCode.append("        a[").append(i).append("]=\"").append(pItem2.Name).append("\";\n");
                                    }
                                }
                                break;
                        }
                        break;
                    case "node.function": 
                        if (true){
                            C_Job pJob2 = pRun_Session.Read_Job(Function_Call, ID2);
                            int ID3 = ID2;
                            strCode.append("        a[").append(index).append("]=");
                            String strValue = pJob2.Program;
                            if (ID3 > 0) {
                                strCode2.append(C_Map_Item.Make_Program_V3_Function(pTreap_Make,pTreap_NewVar,pTreap_NewVar_Boolean, pRun_Session, ID3));
                                strCode.append("f_").append(ID3).append("()");
                            } else {
                                strCode.append("\"").append(S_Strings.CData_Encode(strValue, false)).append("\"");
                            }
                            strCode.append(";\n");
                        }
                        break;
                }
            } else {//如果是0就从Program里读取
                strCode.append("a[i]=\"\";\n");
            }
        }

        
        boolean b_Var_Function=false;//带参数的函数
        if (strFunction.startsWith("@")){
            b_Var_Function=true;
            strFunction=strFunction.substring(1);
            for (int i = 0; i < pList.size(); i++) {
                C_Map_Item_Connect pConnect = pList.get(i);
                if (pConnect.To_Port.startsWith("top")){
                    int index2=pConnect.To_Port_Index();
                    C_Map_Item pItem2 = new C_Map_Item(pConnect.From_ID, pRun_Session, Function_Call);
                    if ("loop.start".equals(pItem2.ItemType)){
                        strFunction=strFunction.replace("$"+index2, "var.var_loop_"+pConnect.From_Port_Index());
                    }else if ("loop.end".equals(pItem2.ItemType)){
                        strFunction=strFunction.replace("$"+index2, "Loop_"+pConnect.From_ID+".var_loop_"+pConnect.From_Port_Index());
                    }else{
                        strFunction=strFunction.replace("$"+index2, "var_"+pConnect.From_ID);
                    }
                }
            }
        }else if (strFunction.startsWith("#")){
            strFunction="new "+strFunction.substring(1);
        }else if (strFunction.startsWith("s.")){
            strFunction="p.call";
        }else{
            strFunction="S_"+strFunction;
        }
        
        switch (strFunction) {
            case "S_t.format_table":
                if (true){
                    String strProgram = Get_String(pItem.Program);
                    strCode.append("        String template=\"").append(strProgram).append("\";\n");
                    strCode.append("        ").append(strVar).append("=").append(strFunction).append("(template,a);\n");
                }
                break;
            case "S_t.format":
                if (true){
                    String strProgram = Get_String(pItem.Program);
                    strCode.append("        String template=\"").append(strProgram).append("\";\n");
                    strCode.append("        ").append(strVar).append("=").append(strFunction).append("(template,a);\n");
                }
                break;
            case "S_string.split_json_array":
                if (true){
                    strCode.append("        ArrayList<String> str=S_string.split_json_array(a);\n");
                    String strVar3=null;
                    for (int i = 0; i < pList_Out.size(); i++) {
                        C_Map_Item_Connect pConnect = pList_Out.get(i);
                        C_Map_Item pItem3 = new C_Map_Item(pConnect.To_ID, pRun_Session, Function_Call);
                        strVar3="var_"+pConnect.To_ID;
                        C_Var_V3 pVar3=new C_Var_V3(strVar3,pItem3.Get_Type(),null);
                        pTreap_NewVar.insert(new C_K_Str(pVar3.Name),pVar3);
                        strCode.append("        ").append(strVar3).append("=").append("str.get("+pConnect.From_Port_Index()+")").append(";\n");
                    }
                }
                break;
            default:
                if ("node.var".equals(pItem.DrawType)){
                    switch(pItem.ItemType){
                        case "text":
                            if (true){
                                String strProgram = Get_String(pItem.Program);
                                Pattern p = Pattern.compile("\\{@(\\d+)\\}");
                                Matcher m = p.matcher(strProgram); // 获取 matcher 对象
                                strCode.append("        ").append(strVar).append("=\"").append(strProgram).append("\";\n");
                                while(m.find()) {
                                    strCode.append("        ").append(strVar).append("=S_string.replace(").append(strVar).append(",\"").append(m.group(0)).append("\",sys_args[").append(m.group(1)).append("]);\n");
                                }
                                ArrayList<C_Map_Item_Connect> pList2 = C_Map_Item_Connect.Get_Connects_To(ID);//读取连接
                                for (int j = 0; j < pList2.size(); j++) {
                                    C_Map_Item_Connect pConnect2 = pList2.get(j);
                                    if (pConnect2.To_Port.startsWith("top")){
                                        int index=S_Strings.getIntFromStr(pConnect2.To_Port.substring(3),0);
                                        strCode.append("        var_").append(pConnect2.From_ID).append("=f_").append(pConnect2.From_ID).append("();\n");
                                        strCode.append("        ").append(strVar).append("=S_string.replace(").append(strVar).append(",\"{").append(index).append("}\",var_").append(pConnect2.From_ID).append(");\n");
                                        strCode2.append(C_Map_Item.Make_Program_V3_Function(pTreap_Make, pTreap_NewVar, pTreap_NewVar_Boolean,pRun_Session, pConnect2.From_ID));
                                    }
                                }
                            }
                            break;
                        default:
                            if (true){
                                String strProgram = Get_String(pItem.Name);
                                Pattern p = Pattern.compile("\\{@(\\d+)\\}");
                                Matcher m = p.matcher(strProgram); // 获取 matcher 对象
                                strCode.append("        ").append(strVar).append("=\"").append(strProgram).append("\";\n");
                                while(m.find()) {
                                    strCode.append("        ").append(strVar).append("=S_string.replace(strVar,\"").append(m.group(0)).append("\",sys_args[").append(m.group(1)).append("]);\n");
                                }
                            }
                            break;
                    }
                }else{
                    if (b_Var_Function){
                        String strVar3=null;
                        for (int i = 0; i < pList_Out.size(); i++) {
                            String strFunction2=strFunction;
                            C_Map_Item_Connect pConnect = pList_Out.get(i);
                            C_Map_Item pItem3 = new C_Map_Item(pConnect.To_ID, pRun_Session, Function_Call);
                            strFunction2=strFunction2.replace("{out.name}", pItem3.Name);
                            
                            strVar3="var_"+pConnect.To_ID;
                            C_Var_V3 pVar3=new C_Var_V3(strVar3,pItem3.Get_Type(),null);
                            pTreap_NewVar.insert(new C_K_Str(pVar3.Name),pVar3);
                            strCode.append("        ").append(strVar3).append("=").append(strFunction2).append(";\n");
                        }
                        strCode.append("        ").append(strVar).append("=").append(strVar3).append(";\n");
                    }else{
                        if (strFunction.endsWith(";")){
                            strCode.append("        ").append(strVar).append("=").append(strFunction).append("\n");
                        }else{
                            if (b_Var_Function){
                                strCode.append("        ").append(strVar).append("=").append(strFunction).append(";\n");
                            }else{
                                strCode.append("        ").append(strVar).append("=").append(strFunction).append("(a);\n");
                            }
                        }
                        for (int i = 0; i < pList_Out.size(); i++) {
                            C_Map_Item_Connect pConnect = pList_Out.get(i);
                            C_Map_Item pItem3 = new C_Map_Item(pConnect.To_ID, pRun_Session, Function_Call);
                            strVar_Type="String";
                            if (pItem3.Name.startsWith("#")){
                                strVar_Type=pItem3.Name.substring(1);
                            }
                            C_Var_V3 pVar3=new C_Var_V3("var_"+pConnect.To_ID,strVar_Type,null);
                            pTreap_NewVar.insert(new C_K_Str(pVar3.Name), pVar3);
                            strCode.append("        ").append(pVar3.Name).append("=").append(strVar).append(";\n");
                        }
                    }
                }
                break;
        }
        strCode.append("        return ").append(strVar).append(";\n");
        strCode.append("    }\n");

        return strCode.toString() + "\n"
                + strCode2.toString() + "\n";
    }

    public static String Get_String(String strProgram) {
        strProgram = strProgram.replace("\n", "\\n");
        strProgram = strProgram.replace("\"", "\\\"");
        return strProgram;
    }

    /**
     * 生成程序
     *
     * @param pTreap
     * @param pRun_Session
     * @param ID
     * @return
     */
    public static String Make_Program(
            Treap pTreap,
            C_Run_Session pRun_Session,
            int ID) {
        
        int Function_Call=0;
        if (pTreap.find(new C_K_Int(ID))!=null){
            return "";
        }
        
        pTreap.insert(new C_K_Int(ID), ID);
        
        C_Job pFunction = pRun_Session.Read_Job(Function_Call, ID);//获取task信息
        String strType=pFunction.Check_Type().toLowerCase();
        String[] strSplit = pFunction.Name.split("\\|");
        String strFunction=strSplit[0].toLowerCase();
        String strOutput = "";
        
        StringBuilder strXML = new StringBuilder();
        strXML.append("<item id=\"").append(ID).append("\">\n");
        C_Table pTable;
        strXML.append("<input>\n");
        
        StringBuilder strXML2 = new StringBuilder(); 
        Document document;
        try {
            document = DocumentHelper.parseText("<item>"+S_Strings.CData_Encode(pFunction.Program, false) +"</item>");
            Node pNode = document.selectSingleNode("item");
            
            ArrayList<C_Map_Item_Connect> pList_Input = C_Map_Item_Connect.Get_Connects_To(ID);//读取所有的输入的箭头

            
            strXML.append("<count>").append(pList_Input.size()).append("</count>\n");
            
            for (int i=0;i<pList_Input.size();i++){
                C_Map_Item_Connect pConnect = pList_Input.get(i);
                int k= pConnect.To_Port_Index()+1;
                int ID2 = pConnect.From_ID;
                
                Node pNodeTmp=pNode.selectSingleNode("v"+k);
                String strParam="";
                if (pNodeTmp!=null){
                    strParam=pNodeTmp.getText();
                }
                if (ID2 > 0) {
                    C_Map_Item pMap2=new C_Map_Item(ID2,pRun_Session,Function_Call);
                    String strType2=pMap2.Check_Type().toLowerCase();
                    int ID3=0;
                    switch (strType2) {
                        case "node.var":
                            {
                                if (pMap2.ItemType.equals("text")){//如果是文本节点
                                    C_Job pJob2 = pRun_Session.Read_Job(Function_Call, ID2);
                                    ID3 = ID2;
                                    strXML.append("<v").append(k);
                                    if (ID3 > 0) {
                                        String strValue=C_Map_Item.Make_Program(pTreap,pRun_Session, ID3);
                                        strXML2.append(strValue);
                                        strXML.append(" call=\"").append(ID3).append("\">");
                                    }else{
                                        strXML.append(">");
                                    }
                                    strXML.append("</v").append(k).append(">\n");
                                }else{
                                    pTable = pRun_Session.Read_Table(Function_Call, ID2);
                                    ID3 = pTable.Get_Pre_Job_ID();
                                    strXML.append("<v").append(k).append(" param=\"").append(pTable.Program_Param).append("\" ");
                                    String strValue="";
                                    if (ID3 > 0) {
                                        strValue=C_Map_Item.Make_Program(pTreap,pRun_Session,ID3);
                                        strXML2.append(strValue);
                                        strXML.append(" call=\"").append(ID3).append("\">");
                                    }else{
                                        if ("".equals(pTable.Get_From_IDs())){//如果 node.var 没有输入。
                                            strXML.append(">");
                                            strValue=pTable.Get_Program();
                                            if (strParam.equals("@name")){//读取下一个节点的名称作为程序的内容
                                                strValue=S_Strings.CData_Encode(pTable.Name,false);
                                            }
                                            strXML.append(S_Strings.CData_Encode(strValue,false));                                
                                        }else{//如果有输入就当作一个String.Format节点
                                            strValue=C_Map_Item.Make_Program_Table(pTreap,pRun_Session,ID2);
                                            strXML2.append(strValue);
                                            strXML.append(" call=\"").append(ID2).append("\">");
                                        }
                                    }       
                                    strXML.append("</v").append(k).append(">\n");
                                }
                                break;
                            }
                        case "node.function":
                            {
                                C_Job pJob2 = pRun_Session.Read_Job(Function_Call, ID2);
                                ID3 = ID2;
                                strXML.append("<v").append(k);
                                String strValue=pJob2.Program;
                                if (ID3 > 0) {
                                    strValue=C_Map_Item.Make_Program(pTreap,pRun_Session, ID3);
                                    strXML2.append(strValue);
                                    strXML.append(" call=\"").append(ID3).append("\">");
                                }else{
                                    strXML.append(">");
                                    if (strParam.equals("@name")){//读取下一个节点的名称作为程序的内容
                                        strValue=S_Strings.CData_Encode(pJob2.Name,false);
                                    }
                                    strXML.append(S_Strings.CData_Encode(strValue,false));
                                }
                                strXML.append("</v").append(k).append(">\n");
                                break;
                            }
                    }
                }else{//如果是0就从Program里读取
                    if (pNodeTmp!=null){
                        strXML.append("<v").append(k).append(">");
                        strXML.append(S_Strings.CData_Encode(strParam, false));
                        strXML.append("</v").append(k).append(">\n");
                    }else{
                        strXML.append("<v").append(k).append("></v").append(k).append(">\n");
                    }
                }
            }
        } catch (DocumentException e) {
        }

        strXML.append("</input>\n");

        if (pFunction.ItemType.equals("text")){//如果是文本节点
            strFunction="string.format";
            strOutput=pFunction.Program;
        }
        strXML.append("<fun>");
        strXML.append(strFunction);
        strXML.append("</fun>\n");
        
        

        strXML.append("<output id=\"0\">");
        strXML.append(S_Strings.CData_Encode(strOutput,false));
        strXML.append("</output>\n");

        strXML.append("</item>\n");
        return strXML.toString()+"\n"
                +strXML2.toString();
    }
    
    
    /**
     * 运行程序
     *
     * @param pSession
     * @param pTreap_PNode
     * @param pRun_Session
     * @param Function_Call
     * @param ID
     * @param strProgram
     * @param pPNode
     * @param strParam
     * @return
     */
    public String Run_Program(
            C_Session_AI pSession,
            Treap pTreap_PNode,
            C_Run_Session pRun_Session,
            int Function_Call,
            int ID,
            String strProgram,
            C_Program_Node pPNode,
            List<String> strParam) {

        String strReturn = "";
        Document document2;
        int FirstID = 0;
        try {
            if (ID > 0) {
                C_Job pJob = pRun_Session.Read_Job(Function_Call, ID);//获取task信息
                strProgram = pJob.Program_Output;
                S_Debug.Write_DebugLog("Run_Program", "ID=" + ID + "\n" + strProgram, false);
            }

            if (strProgram != null && !"".equals(strProgram)) {
                if (strParam != null) {
                    for (int i = 0; i < strParam.size(); i++) {
                        strProgram = strProgram.replace("{@" + i + "}",
                                S_Strings.CData_Encode(strParam.get(i), false));
                    }
                }

                for (int i = 0; i < 10; i++) {
                    if (AI_Var2.Password[i] != null && strProgram != null) {
                        strProgram = strProgram.replace("{@password_" + i + "}", AI_Var2.Password[i]);
                    }
                }
                document2 = DocumentHelper.parseText("<data>" + strProgram + "</data>");

                List<Node> pNodes = document2.selectNodes("data/item");

                if (pTreap_PNode == null) {
                    pTreap_PNode = new Treap();
                }

                for (int i = 0; i < pNodes.size(); i++) {
                    Element element = (Element) pNodes.get(i);
                    int ID2 = Integer.valueOf(element.attributeValue("id"));
                    if (i == 0) {
                        FirstID = ID2;
                    }

                    C_Program_Node pPNode2 = new C_Program_Node();
                    pPNode2.pInput = element.selectSingleNode("input");
                    pPNode2.pFun = element.selectSingleNode("fun");
                    pPNode2.pOutput = element.selectSingleNode("output");

                    pTreap_PNode.insert(new C_K_Int(ID2), pPNode2);
                }
                pPNode = (C_Program_Node) pTreap_PNode.find(new C_K_Int(FirstID)); //document2.selectSingleNode("data/item");
            }

            if (pPNode == null) {
                return "";
            }

            Node pNode_Input = pPNode.pInput;
            Node pNode_Fun = pPNode.pFun;
            Node pNode_Output = pPNode.pOutput;

            String strFunction = pNode_Fun.getText().toLowerCase();
            String[] strSplit_Function = strFunction.split("\\.");

            System.out.println(strFunction);
            switch (strSplit_Function[0]) {
                case "=":
                    C_Function_Return pReturn = Tools_GetParam.GetParam(pSession, pTreap_PNode, Function_Call, pNode_Input, this);
                    if (pReturn.pList.size() > 0) {
                        strReturn = pReturn.pList.get(0).toString();
                    }
                    break;
                case "string":
                    strReturn = Tools_String.Function_Call(pSession, pTreap_PNode, Function_Call, strFunction, pNode_Input, pNode_Output, this);
                    break;
                case "write":
                    strReturn = Tools_Write.Function_Call(pSession, pTreap_PNode, Function_Call, strFunction, pNode_Input, pNode_Output, this);
                    break;
                case "f":
                case "function"://子函数
                    strReturn = Tools_SubFunction.Function_Call(pSession, pTreap_PNode, Function_Call, strFunction, pNode_Input, pNode_Output, this);
                    break;
                case "cassandra":
                    strReturn = Tools_Cassandra.Function_Call(pSession, pTreap_PNode, Function_Call, strFunction, pNode_Input, pNode_Output, this);
                    break;
                case "math":
                    strReturn = Tools_Math.Function_Call(pSession, pTreap_PNode, Function_Call, strFunction, pNode_Input, pNode_Output, this);
                    break;
                case "net":
                    strReturn = Tools_Net.Function_Call(pSession, pTreap_PNode, Function_Call, strFunction, pNode_Input, pNode_Output, this);
                    break;
                case "file":
                    strReturn = Tools_File.Function_Call(pSession, pTreap_PNode, Function_Call, strFunction, pNode_Input, pNode_Output, this);
                    break;
                case "cn":
                    strReturn = Tools_CN.Function_Call(pSession, pTreap_PNode, Function_Call, strFunction, pNode_Input, pNode_Output, this);
                    break;
                case "sys":
                    strReturn = Tools_SYS.Function_Call(pSession, pTreap_PNode, Function_Call, strFunction, pNode_Input, pNode_Output, this);
                    break;
                case "fs":
                    strReturn = Tools_FS.Function_Call(pSession, pTreap_PNode, Function_Call, strFunction, pNode_Input, pNode_Output, this);
                    break;
                case "array":
                    strReturn = Tools_Array.Function_Call(pSession, pTreap_PNode, Function_Call, strFunction, pNode_Input, pNode_Output, this);
                    break;
            }

        } catch (DocumentException e) {
            e.printStackTrace();
            System.out.println(strProgram);
        }

        System.out.println(strReturn);
        return strReturn;
    }

    /**
     * 生成函数调用
     *
     * @param pTreapVar
     * @return
     */
    public C_Item_Function Get_Function(Treap pTreapVar) {
        C_Item_Function pFunction = this.GetInput(pTreapVar);
        pFunction.Function = this.Name;
        return pFunction;
    }

    /**
     * 生成函数调用的所有输入参数
     *
     * @param pTreapVar
     * @return
     */
    public C_Item_Function GetInput(Treap pTreapVar) {
        C_Item_Function pReturn = new C_Item_Function();

        C_Job pJob = pRun_Session.Read_Job(0, ID);//获取task信息
        String[] strSplit1 = pJob.Get_From_IDs().split(",");

        String strLine_Input = "";
        for (int i = 0; i < strSplit1.length; i++) {
            int ID2 = S_Strings.getIntFromStr(strSplit1[i], 0);//输入的ID
            if (ID2 > 0) {
                C_Map_Item pMap = new C_Map_Item(ID2, pRun_Session, 0);
                String strType = pMap.Check_Type().toLowerCase();
                switch (strType) {
                    case "node.var":
                        if (pMap.Name.startsWith("#")) {
                            strLine_Input = pMap.Name.substring(1);
                        } else if (pMap.Name.startsWith("##")) {
                            strLine_Input = "\"" + pMap.Name.substring(2) + "\"";
                        } else {
                            C_Var pVar = (C_Var) pTreapVar.find(new C_K_Str("v" + ID2));
                            if (pVar != null) {
                                strLine_Input = pVar.Get_Var_Head() + pVar.ID;
                            } else {
                                System.out.println("GetInput");
                                System.out.println("ID2=" + ID2);
                            }
                        }
                        break;
                    case "node.function":
                        strLine_Input = pMap.Get_Function(pTreapVar).toString();
                        break;
                }
                pReturn.pInput.add(strLine_Input, ID2);
            }
        }

        return pReturn;
    }

    

}
