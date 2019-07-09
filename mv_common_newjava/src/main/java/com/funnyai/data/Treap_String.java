package com.funnyai.data;

import com.funnyai.data.TreapEnumerator;
import java.util.ArrayList;


public class Treap_String<T> extends Treap<T> { 
    
    public Object pExtend; 
    //是否需要刷新 比如是否不想要保存到文件 
    public boolean bNeedRefresh = false; 
    //当前Treap对应的文件
    
    public String strKey; 
    //Treap 对应的数组 
    
    private ArrayList<T> mArrayList; 
    
    public ArrayList<T> pArrayList() { 
        if (mArrayList == null) { 
            mArrayList = new ArrayList<T>(); 
            T pItem;
            TreapEnumerator<T> p = this.Elements(true); 
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