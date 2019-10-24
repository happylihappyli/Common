package com.funnyai.net.Old;

import io.socket.client.IO;
import io.socket.client.Socket;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Proxy;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;


public class S_Net {
    
    public static String Proxy_IP="";
    public static int Proxy_Port=8080;
    
    
    public static String Proxy_IP_Watch="";
    public static int Proxy_Port_Watch=8080;
    
    public static String get_local_ip(){
        try {
            Enumeration<NetworkInterface> nets;
            nets = NetworkInterface.getNetworkInterfaces();
            String IP;
            for (NetworkInterface netint : Collections.list(nets)){
                IP=displayInterfaceInformation(netint);
                if (IP.length()>0){
                    return IP;
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return "127.0.0.1";
    }
    
    public static String displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        
//        out.printf("Display name: %s\n", netint.getDisplayName());
//        out.printf("Name: %s\n", netint.getName());
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        String IP="";
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            IP=inetAddress.getHostAddress();
//            out.printf("InetAddress: %s\n", IP);//inetAddress);
        }
//        out.printf("\n");
        if (netint.getName().equals("eth0")){
            return IP;
        }else{
            return "";
        }
     }

    
    /**
     * 
     * @param url
     * @param param
     * @return 
     */
    public static String http_post(
            String url, 
            String param) {
        String result = "";
        String reference_url="";
        String strEncode="utf-8";
        String strMethod="POST";
        try {
            URL httpurl = new URL(url);
            HttpURLConnection httpConn;
                
            boolean bProxy=true;
            if ("".equals(S_Net.Proxy_IP)) bProxy=false;
            if (bProxy) {
                out.println("proxy.ip");
                out.println(S_Net.Proxy_IP);
                out.println("proxy.port");
                out.println(S_Net.Proxy_Port);
                InetSocketAddress addr = new InetSocketAddress(S_Net.Proxy_IP,S_Net.Proxy_Port);// "10.16.202.90", 8081);
                Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理
                httpConn = (HttpURLConnection) httpurl.openConnection(proxy);
            } else {
                httpConn = (HttpURLConnection) httpurl.openConnection();
            }
            
            httpConn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.1) Gecko/2008070208 Firefox/3.0.1");
            httpConn.setRequestProperty("Accept-Charset", "utf-8;q=0.7,*;q=0.7");
            httpConn.setRequestProperty("Referer", reference_url);
            
            if (strMethod.equals("POST")) {
                httpConn.setDoOutput(true);
                httpConn.setDoInput(true);
                PrintWriter out = new PrintWriter(httpConn.getOutputStream());
                out.print(param);
                out.flush();
                out.close();
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream(), strEncode));
            String line;
            while ((line = in.readLine()) != null) {
                result += line + "\r\n";
            }
            in.close();
        } catch (Exception e) {
            out.println("Encode="+strEncode);
            out.println("url="+url);
            e.printStackTrace();
            result = "";//e.toString();
        }
        return result;
    }
    
    /**
     * 
     * @param pHead
     * @param url
     * @param param
     * @return 
     */
    public static String http_post_and_head(
            ArrayList<Http_Head> pHead,
            String url, 
            String param) {
        String result = "";
        String strEncode="utf-8";
        String strMethod="POST";
        try {
            URL httpurl = new URL(url);
            HttpURLConnection httpConn;
                
            boolean bProxy=true;
            if ("".equals(S_Net.Proxy_IP)) bProxy=false;
            if (bProxy) {
                out.println("proxy.ip");
                out.println(S_Net.Proxy_IP);
                out.println("proxy.port");
                out.println(S_Net.Proxy_Port);
                InetSocketAddress addr = new InetSocketAddress(S_Net.Proxy_IP,S_Net.Proxy_Port);// "10.16.202.90", 8081);
                Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理
                httpConn = (HttpURLConnection) httpurl.openConnection(proxy);
            } else {
                httpConn = (HttpURLConnection) httpurl.openConnection();
            }
            
            //httpConn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.1) Gecko/2008070208 Firefox/3.0.1");
            //httpConn.setRequestProperty("Accept-Charset", "utf-8;q=0.7,*;q=0.7");
            //httpConn.setRequestProperty("Referer", reference_url);
            
            for (int i=0;i<pHead.size();i++){
                Http_Head pItem=pHead.get(i);
                httpConn.setRequestProperty(pItem.Key,pItem.Value);
            }
            
            if (strMethod.equals("POST")) {
                httpConn.setDoOutput(true);
                httpConn.setDoInput(true);
                PrintWriter out = new PrintWriter(httpConn.getOutputStream());
                out.print(param);
                out.flush();
                out.close();
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream(), strEncode));
            String line;
            while ((line = in.readLine()) != null) {
                result += line + "\r\n";
            }
            in.close();
        } catch (Exception e) {
            out.println("Encode="+strEncode);
            out.println("url="+url);
            e.printStackTrace();
            result = "";//e.toString();
        }
        return result;
    }
    
    private static final String APPLICATION_JSON = "application/json";
    
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";

    public static void http_post_with_json(String url, String json) throws Exception {
        // 将JSON进行UTF-8编码,以便传输中文
        String encoderJson = URLEncoder.encode(json,"utf-8");
        
        CloseableHttpClient httpClient =  HttpClientBuilder.create().build(); //HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", APPLICATION_JSON);
        
        StringEntity se = new StringEntity(encoderJson);
        se.setContentType(CONTENT_TYPE_TEXT_JSON);
        se.setContentEncoding(new BasicHeader("Content-Type", APPLICATION_JSON));
        httpPost.setEntity(se);
        httpClient.execute(httpPost);

    }
    
    public static String post_json(
            String url, JSONObject json, Map<String, String> headers) {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json");
        if (headers != null) {
            Set<String> keys = headers.keySet();
            for (Iterator<String> i = keys.iterator(); i.hasNext();) {
                String key = (String) i.next();
                post.addHeader(key, headers.get(key));
            }
        }

        try {
            StringEntity s = new StringEntity(json.toString(), "utf-8");
            s.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
            post.setEntity(s);

            boolean bProxy=true;
            if ("".equals(S_Net.Proxy_IP)) bProxy=false;
            if (bProxy) {
                out.println("proxy.ip");
                out.println(S_Net.Proxy_IP);
                out.println("proxy.port");
                out.println(S_Net.Proxy_Port);
                
                RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(5000)
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .build();
                RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig).setProxy(new HttpHost(S_Net.Proxy_IP, S_Net.Proxy_Port)).build();
                post.setConfig(requestConfig);
            }
            
            HttpResponse httpResponse = client.execute(post);
            InputStream inStream = httpResponse.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
            StringBuilder strber = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                strber.append(line + "\n");
            }
            inStream.close();
            return strber.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String post_json_extend(
            String strProxy_IP,int iProxy_Port,
            String url, JSONObject json, Map<String, String> headers) {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json");
        if (headers != null) {
            Set<String> keys = headers.keySet();
            for (Iterator<String> i = keys.iterator(); i.hasNext();) {
                String key = (String) i.next();
                post.addHeader(key, headers.get(key));
            }
        }

        try {
            StringEntity s = new StringEntity(json.toString(), "utf-8");
            s.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
            post.setEntity(s);

            boolean bProxy=true;
            if ("".equals(strProxy_IP)) bProxy=false;
            if (bProxy) {
                out.println("proxy.ip");
                out.println(strProxy_IP);
                out.println("proxy.port");
                out.println(iProxy_Port);
                
                RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(5000)
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .build();
                RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig).setProxy(new HttpHost(strProxy_IP, iProxy_Port)).build();
                post.setConfig(requestConfig);
            }
            
            HttpResponse httpResponse = client.execute(post);
            InputStream inStream = httpResponse.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
            StringBuilder strber = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                strber.append(line + "\n");
            }
            inStream.close();
            return strber.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public static String sendPost(
            String url, String strMethod,
            String param, String strEncode,
            String reference_url) {
        String result = "";
        try {
            URL httpurl = new URL(url);

            HttpURLConnection httpConn;
            
            boolean bProxy=true;
            if ("".equals(S_Net.Proxy_IP)) bProxy=false;
            if (bProxy) {
                out.println("proxy.ip");
                out.println(S_Net.Proxy_IP);
                out.println("proxy.port");
                out.println(S_Net.Proxy_Port);
                InetSocketAddress addr = new InetSocketAddress(S_Net.Proxy_IP,S_Net.Proxy_Port);// "10.16.202.90", 8081);
                Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理
                httpConn = (HttpURLConnection) httpurl.openConnection(proxy);
            } else {
                httpConn = (HttpURLConnection) httpurl.openConnection();
            }

            httpConn.setConnectTimeout(25000);
            httpConn.setReadTimeout(25000);
            httpConn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.1) Gecko/2008070208 Firefox/3.0.1");
            httpConn.setRequestProperty("Accept-Charset", "utf-8;q=0.7,*;q=0.7");
            httpConn.setRequestProperty("Referer", reference_url);
            
            if (strMethod.equals("POST")) {
                httpConn.setDoOutput(true);
                httpConn.setDoInput(true);
                PrintWriter out = new PrintWriter(httpConn.getOutputStream());
                out.print(param);
                out.flush();
                out.close();
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream(), strEncode));
            String line;
            while ((line = in.readLine()) != null) {
                result += line + "\r\n";
            }
            in.close();
        } catch (Exception e) {
            out.println("Encode="+strEncode);
            out.println("url="+url);
            out.println("param="+param);
            e.printStackTrace();
            result = "";//e.toString();
        }
        return result;
    }
    
    public static String http_get(
            String url) {
        String strEncode="utf-8";
        String reference_url="";
        int TimeOut=10;
        String result = "";
        try {
            URL httpurl = new URL(url);
            HttpURLConnection httpConn;

            boolean bProxy=true;
            if (S_Net.Proxy_IP==null || "".equals(S_Net.Proxy_IP)) bProxy=false;
            if (bProxy) {
                out.println("proxy.ip");
                out.println(S_Net.Proxy_IP);
                out.println("proxy.port");
                out.println(S_Net.Proxy_Port);
                InetSocketAddress addr = new InetSocketAddress(S_Net.Proxy_IP,S_Net.Proxy_Port);// "10.16.202.90", 8081);
                Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理
                httpConn = (HttpURLConnection) httpurl.openConnection(proxy);
            } else {
                httpConn = (HttpURLConnection) httpurl.openConnection();
            }
                
            httpConn.setConnectTimeout(TimeOut*1000);
            httpConn.setReadTimeout(TimeOut*1000);
            httpConn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.1) Gecko/2008070208 Firefox/3.0.1");
            httpConn.setRequestProperty("Accept-Charset", "utf-8;q=0.7,*;q=0.7");
            httpConn.setRequestProperty("Referer", reference_url);
            
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream(), strEncode));
            String line;
            while ((line = in.readLine()) != null) {
                result += line + "\r\n";
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            //S_Net.SI_Send("sys_event","error","http_get","*",e.toString());
            out.println("Encode="+strEncode);
            out.println("url="+url);
            e.printStackTrace();
        }
        return result;
    }
    
    public static String Server_Socket="http://robot6.funnyai.com:8000";
    public static Socket socket;
    public static void set_socket_server(String strURL){
        S_Net.Server_Socket=strURL;
    }
    
    
    private static void Send_Msg2(
            String event_type,
            String strType,
            String From,
            String To,
            String strMsg){
        if ("".equals(To)){
            To="*";
        }
        if (socket==null){
            try {
                S_Net.socket = IO.socket(S_Net.Server_Socket);
                S_Net.socket.on(Socket.EVENT_DISCONNECT, (Object... args1) -> {
                    S_Net.socket.connect();
                });
                S_Net.socket.on("sys_event", (Object... args1) -> {
                    for(int i=0;i<args1.length;i++){
                        JSONObject obj = (JSONObject) args1[i];
                        out.println(obj.toString());
                    }
                });
                S_Net.socket.connect();
                try {
                    Thread.sleep(3*1000);
                } catch (InterruptedException ex) {
                }
            } catch (URISyntaxException ex) {
                Logger.getLogger(S_Net.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        JSONObject obj = new JSONObject();
        obj.put("type", strType);
        obj.put("from", From);
        obj.put("to", To);
        obj.put("message", strMsg);
        socket.emit(event_type, obj);
    }
    
    //"sys_event"
    public static void SI_Send2(
            String event_type,
            String strType,
            String From,
            String To,
            String strMsg){
        if (socket==null){
            try {
                S_Net.socket = IO.socket(S_Net.Server_Socket);
                S_Net.socket.on(Socket.EVENT_DISCONNECT, (Object... args1) -> {
                    S_Net.socket.connect();
                });
                S_Net.socket.on("sys_event", (Object... args1) -> {
                    for(int i=0;i<args1.length;i++){
                        JSONObject obj = (JSONObject) args1[i];
                        out.println(obj.toString());
                    }
                });
                S_Net.socket.connect();
                try {
                    Thread.sleep(3*1000);
                } catch (InterruptedException ex) {
                }
            } catch (URISyntaxException ex) {
                Logger.getLogger(S_Net.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (socket.connected()){
            Send_Msg2(event_type,strType,From,To,strMsg);
        }else{
            socket.connect();
            try {
                Thread.sleep(6*1000);
            } catch (InterruptedException ex) {
            }
            Send_Msg2(event_type,strType,From,To,strMsg);
        }
    }
    
    /** 
     * 从输入流中获取数据 
     * @param inStream 输入流 
     * @return 
     * @throws Exception 
     */  
    public static byte[] readInputStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = 0;  
        while( (len=inStream.read(buffer)) != -1 ){  
            outStream.write(buffer, 0, len);  
        }  
        inStream.close();  
        return outStream.toByteArray();  
    }  
    
    
    /** 
     * 根据地址获得数据的字节流 
     * @param strUrl 网络连接地址 
     * @return 
     */  
    public static byte[] get_image_from_url(String strUrl){  
        try {  
            URL url = new URL(strUrl);  
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
            conn.setRequestMethod("GET");  
            conn.setConnectTimeout(5 * 1000);  
            InputStream inStream = conn.getInputStream();//通过输入流获取图片数据  
            byte[] btImg = readInputStream(inStream);//得到图片的二进制数据  
            return btImg;  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
    
    public static String http_GET(
            String url,
            String param, String strEncode,
            String reference_url,
            int TimeOut) {
        String result = "";
        try {
            URL httpurl = new URL(url);
            HttpURLConnection httpConn;

            boolean bProxy=true;
            if ("".equals(S_Net.Proxy_IP)) bProxy=false;
            if (bProxy) {
                out.println("proxy.ip");
                out.println(S_Net.Proxy_IP);
                out.println("proxy.port");
                out.println(S_Net.Proxy_Port);
                InetSocketAddress addr = new InetSocketAddress(S_Net.Proxy_IP,S_Net.Proxy_Port);// "10.16.202.90", 8081);
                Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理
                httpConn = (HttpURLConnection) httpurl.openConnection(proxy);
            } else {
                httpConn = (HttpURLConnection) httpurl.openConnection();
            }
                
            httpConn.setConnectTimeout(TimeOut*1000);
            httpConn.setReadTimeout(TimeOut*1000);
            httpConn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.1) Gecko/2008070208 Firefox/3.0.1");
            httpConn.setRequestProperty("Accept-Charset", "utf-8;q=0.7,*;q=0.7");
            httpConn.setRequestProperty("Referer", reference_url);
            
            String strMethod="GET";
            
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream(), strEncode));
            String line;
            while ((line = in.readLine()) != null) {
                result += line + "\r\n";
            }
            in.close();
        } catch (Exception e) {
            out.println("Encode="+strEncode);
            out.println("url="+url);
            out.println("param="+param);
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
//        String url="http://www.google.cn/search?complete=1&hl=zh-CN&rlz=1B3GGGL_zh-CNTW288TW288&q=%E4%B8%8A%E6%B5%B7%E5%A4%A9%E6%B0%94&btnG=Google+%E6%90%9C%E7%B4%A2&meta=&aq=f";
//        String strReturn=S_Net.sendPost(url,
//                        "GET", "", "utf-8", false);
//        System.out.println(strReturn);
        System.out.println(ip2Long("116.232.105.203"));
        System.out.println(ip2Long("218.88.145.213"));

    }

    public static long ip2Long(String strIP) {
        if ("".equals(strIP)){
            return 0;
        }
        long[] ip = new long[4];
        //先找到IP地址字符串中.的位置
        int position1 = strIP.indexOf(".");
        int position2 = strIP.indexOf(".", position1 + 1);
        int position3 = strIP.indexOf(".", position2 + 1);
        //将每个.之间的字符串转换成整型
        ip[0] = Long.parseLong(strIP.substring(0, position1));
        ip[1] = Long.parseLong(strIP.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(strIP.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(strIP.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

    //将10进制整数形式转换成127.0.0.1形式的IP地址
    public static String long2IP(long longIP) {
        StringBuffer sb = new StringBuffer("");
        //直接右移24位
        sb.append(String.valueOf(longIP >>> 24));
        sb.append(".");
        //将高8位置0，然后右移16位
        sb.append(String.valueOf((longIP & 0x00FFFFFF) >>> 16));
        sb.append(".");
        sb.append(String.valueOf((longIP & 0x0000FFFF) >>> 8));
        sb.append(".");
        sb.append(String.valueOf(longIP & 0x000000FF));
        return sb.toString();
    }
}
