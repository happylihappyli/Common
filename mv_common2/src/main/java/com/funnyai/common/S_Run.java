/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.common;

import java.io.IOException;
import static java.lang.System.out;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author happyli
 */
public class S_Run {
    
    /**
     * 
     * @param strUser
     * @param strFile
     * @param strMax
     * @param SQL
     * @param strSep
     * @param Output 
     */
    public static void File_SQL(
            String strUser,
            String strFile,
            String strMax,
            String SQL,
            String strSep,
            String Output){
        try {
            switch(strSep){
                case "v":
                    strSep="|";
                    break;
                case "t":
                    strSep="\t";
                    break;
            }
            SQL=SQL.replace("\n", " ");
            C_Run_Session_Min pSession=C_Run_Session_Min.Get_New_Session(0, 0);
            
            C_Command_Min pCommand=new C_Command_Min(pSession,0);
            String strLine="java -jar "+AI_Var2.Path_Root+"funny_sql.jar \""
                    +strUser+"\" \""+strFile+"\" \""+strMax+"\" \""+SQL+"\" \""+strSep+"\" \""+Output+"\"";
            out.println(strLine);
            pCommand.RunShell(strLine);
            out.println("file_sql finished");
        } catch (IOException ex) {
            S_Debug.Write_DebugLog("error_sql", ex.toString());
        }
    }
}
