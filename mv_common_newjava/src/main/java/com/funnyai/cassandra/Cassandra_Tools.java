package com.funnyai.cassandra;

import com.funnyai.Time.S_Time;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import static java.lang.System.out;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author happyli
 */
public class Cassandra_Tools {
    
    private Cluster cluster;
    public Session session;
    
    public static void main(String[] args){
        String strIP="10.168.44.66";
        int iPort=9042;
        String strUser="content_writer_default";
        String strPassword="9e8775ef0f";
        
        
        Cassandra_Tools pCassandra=new Cassandra_Tools();
        pCassandra.connect(strIP,iPort,strUser,strPassword);
        ResultSet a=pCassandra.executeSQL("select * from magic_content_rev2.content_searchable_rev1 limit 10");
        
        Iterator<Row> iterator = a.iterator();
        
        int iCount=0;
        while(iterator.hasNext())
        {
            iCount+=1;
            if (iCount % 1000==1){
                out.println(iCount);
            }
            Row row = iterator.next();
            if (row.getBool("is_deleted")==false){
                String account_key = row.getString("account_key");
                String product_key = row.getString("product_key");
                String content_id = row.getString("content_id");
                String long_title= row.getString("long_title");
                String summary= row.getString("summary");
                String title= row.getString("title");

                long_title=long_title.replaceAll("\r", " ");
                long_title=long_title.replaceAll("\n", " ");
                long_title=long_title.replaceAll(",", " ");

                summary=summary.replaceAll("\r", " ");
                summary=summary.replaceAll("\n", " ");
                summary=summary.replaceAll(",", " ");
                
                title=title.replaceAll("\r", " ");
                title=title.replaceAll("\n", " ");
                title=title.replaceAll(",", " ");
                
                String strLine="False,"+account_key+","+product_key+","+content_id+","+long_title+","+summary+","+title;;

//                S_File.Write_Line(pFile, strLine);
            }else{
//                out.println("is_deleted");
            }
        }
        
        
    }


    public void connect(String nodes,int iPort,String username,String password) {
        String[] strSplit=nodes.split(",");
        Builder p=Cluster.builder();
        for (String node : strSplit) {
            out.println("node="+node);
            p = p.addContactPoint(node);
        }
        cluster = p.withPort(iPort).withCredentials(username, password).build();
        
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n",
                metadata.getClusterName());
        for (Host host : metadata.getAllHosts()) {
            System.out.printf("Datatacenter: %s; Host: %s; Rack: %s\n",
                    host.getDatacenter(), host.getAddress(), host.getRack());
        }
        this.session = cluster.connect();
    }
    
    public void close(){
        cluster.close();
    }
    
    public String Query(String strSQL,String strFields,String strTypes,int Max){
        ResultSet a=this.executeSQL(strSQL);
        
        String strReturn="";
        String result="";
        Iterator<Row> iterator = a.iterator();
        int Count=0;
        while(iterator.hasNext())
        {
            Count+=1;
            Row row = iterator.next();
            String strLine="";
            String [] strSplit=strFields.split(",");
            String [] Type_Split=strTypes.split(",");
            for (int i=0;i<strSplit.length;i++){
                String strField=strSplit[i];
                result="";
                switch (Type_Split[i]){
                    case "string":
                        result=row.getString(strField);
                        break;
                    case "int":
                        result=row.getInt(strField)+"";
                        break;
                    case "timestamp":
                        result=S_Time.formatYMD_Hms(row.getTimestamp(strField));
                        break;
                    case "list":
                        List<String> pList=row.getList(strField,String.class);
                        Object[] pObj3=pList.toArray();
                        for (Object pItem : pObj3) {
                            result += pItem + ",";
                        }
                        if (result.endsWith(",")){
                            result=result.substring(0,result.length()-1);
                        }
                        break;
                    case "set":
                        Set<String> p2=row.getSet(strField,String.class);
                        Object[] p3=p2.toArray();
                        for (Object pItem : p3) {
                            result += pItem + ",";
                        }
                        if (result.endsWith(",")){
                            result=result.substring(0,result.length()-1);
                        }
                        break;
                }
                strLine+=result+"|";
            }
            if (Count>Max) break;
            strReturn+=strLine+"\n";
        }

        return strReturn;
    }
    
    public ResultSet executeSQL(String strSQL) {
        ResultSet a=this.session.execute(strSQL);
        
        return a;
    }
    
    public void executeSQL_Async(String strSQL) {
        this.session.executeAsync(strSQL);
    }
}
