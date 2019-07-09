/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.net;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.funnyai.data.C_Var_Java;
import com.funnyai.string.S_string;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author happyli
 */
public class S_ssh {

    public static C_Var_Java run(C_Var_Java... a) {
        String hostname = (String) a[0].pObj;
        int iPort = S_string.getIntFromStr(a[1].pObj, 22);
        String strEncode = (String) a[2].pObj;
        String username = (String) a[3].pObj;
        String password = (String) a[4].pObj;
        String strCommand = (String) a[5].pObj;

        StringBuilder pStr = new StringBuilder();
        try {
            Connection conn = new Connection(hostname, iPort);
            conn.connect();
            boolean isAuthenticated = conn.authenticateWithPassword(username, password);

            if (isAuthenticated == false) {
                throw new IOException("Authentication failed.");
            }

            Session sess = conn.openSession();

            sess.execCommand(strCommand);//"uname -a && date && uptime && who && echo '你好'");

            System.out.println("Here is some information about the remote host:");

            /* 
                 * This basic example does not handle stderr, which is sometimes dangerous
                 * (please read the FAQ).
             */
            InputStream stdout = new StreamGobbler(sess.getStdout());

            BufferedReader br = new BufferedReader(new InputStreamReader(stdout, strEncode));

            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                System.out.println(line);
                pStr.append(line + "\n");
            }

            System.out.println("ExitCode: " + sess.getExitStatus());

            sess.close();
            conn.close();

        } catch (IOException e) {
            e.printStackTrace(System.err);
            System.exit(2);
        }
        return new C_Var_Java("String",pStr.toString());
    }
}
