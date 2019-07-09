
package com.funnyai.netso;

import com.funnyai.data.Treap;
import com.funnyai.string.Old.S_Strings;
import java.util.ArrayList;

/**
 *
 * @author happyli
 */
public class C_Train_Example {
    public ArrayList<C_Struct_Active> pKeys=null;
    public String Topic="";
    public String Topic2="";//第二个分类
    public String Topic_Real="";
    public Treap<C_Vector_Item> pVector=null;
    public C_Sentence pSentence=null;
    public String ID="";
    public int Adjust_Count=0;//训练的时候调整的次数
    public String Reason="";//识别为这个topic的理由
    public String AI_Tag_Tmp="";//AI识别出的类别
    public String Topic_Tag_Name="";//意图的名称
    public String Memo="";

    public C_Train_Example(String ID,
            String strTopic,String strTopic2,String strMemo,
            C_Sentence pSentence,ArrayList<C_Struct_Active> pKeys) {
        this.ID=ID;
        this.pKeys=pKeys;
        this.Topic=strTopic;
        this.Topic2=strTopic2;
        this.Memo=strMemo;
        this.pSentence=pSentence;
    }
    
    /**
     * 结构转换为字符串
     * @return 
     */
    public String Keys_String(){
        
        String strLine="";
        for (int j=0;j<this.pKeys.size();j++){
            C_Struct_Active pKey=this.pKeys.get(j);
            strLine+=pKey.Name+",";
        }
        strLine=S_Strings.cut_end_string(strLine,",");
        return strLine;
    }
}
