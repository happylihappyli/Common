package com.funnyai.data;

import java.util.ArrayList;


public class C_Treap_Funny extends Treap { 
    
    public Object pExtend; 
    //是否需要刷新 比如是否不想要保存到文件 
    public boolean bNeedRefresh = false; 
    //当前Treap对应的文件
    
    public String strFile; 
    //Treap 对应的数组 
    
    private ArrayList<Object> mArrayList; 
    
    public ArrayList<Object> pArrayList() { 
        if (mArrayList == null) { 
            mArrayList = new ArrayList<Object>(); 
            Object pItem; 
            TreapEnumerator p = this.Elements(true); 
            while ((p.HasMoreElements())) { 
                pItem = p.NextElement(); 
                mArrayList.add(pItem); 
            } 
        } 
        return mArrayList; 
    } 
    
    public void pArrayList_Clear() 
    { 
        mArrayList = null; 
    } 
} 