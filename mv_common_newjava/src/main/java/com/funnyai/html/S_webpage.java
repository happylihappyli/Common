/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.html;

import static java.lang.System.out;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author happyli
 */
public class S_webpage {
    public static Document doc=null;

    
    public static void init_html(){
        String html = "<html><head>"
                + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
                + "<title>test</title></head>"
            + "<body>...</body></html>";
        doc = Jsoup.parse(html);
    }
    /*
     * 把dom文件转换为xml字符串
     */
    public static String html() {
        return doc.html();
    }
    
    
    public static void html(String jQuery,String strHTML) {
        doc.select(jQuery).html(strHTML);
    }
    
    
    public static void add_node(String jQuery,String strHTML){
        doc.select(jQuery).append(strHTML);
    }
    
    
    public static void add_node_pre(String jQuery,String strHTML){
        doc.select(jQuery).prepend(strHTML);
    }
    
    public static void add_node_after(String jQuery,String strHTML){
        doc.select(jQuery).after(strHTML);
    }
    
    public static void add_node_before(String jQuery,String strHTML){
        doc.select(jQuery).before(strHTML);
    }
    
    public static void attribute(String jQuery,String strKey,String strHTML){
        doc.select(jQuery).attr(strKey, strHTML);
    }
}
