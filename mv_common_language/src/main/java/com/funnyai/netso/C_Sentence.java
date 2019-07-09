package com.funnyai.netso;

import com.funnyai.data.TreapEnumerator;
import com.funnyai.Segmentation.C_Segmentation;
import static java.lang.System.out;

/**
 *
 * @author happyli
 */
public class C_Sentence {
    
    public String Sentence="";
    public String Sentence_Seg="";//分词后的句子
    public int WordCount=0;//分词后个数
    
    
    public C_Sentence(String strSentence,NetSO pNetSO){
        TreapEnumerator<String> pTreapSentence = pNetSO.pTreapFilter_Sentence.Elements();
        while(pTreapSentence.HasMoreElements()){
            String strName=pTreapSentence.NextElement();
            //out.println(strName);
            strSentence = strSentence.replace(strName,"");
            //out.println(strSentence);
        }
        this.Sentence=strSentence;
    }
    
    /**
     * 分词
     * @param pSeg 
     * @param bFilter_Symbol 
     * @param bNumFilter 
     */
    public void Segmentation(
            C_Segmentation pSeg,
            boolean bFilter_Symbol,
            boolean bNumFilter) {
        try{
            if (!"".equals(this.Sentence)){
                this.Sentence_Seg = pSeg.Segmentation_List(this.Sentence,0,bFilter_Symbol,bNumFilter);//false,false);
                this.WordCount=this.Sentence_Seg.split(" ").length;
            }
        }
        catch(Exception ex){
            System.out.println(ex.toString());
        }
    }
}
