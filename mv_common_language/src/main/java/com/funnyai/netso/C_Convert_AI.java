/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.netso;

import com.funnyai.NLP.*;
import com.funnyai.Segmentation.*;
import com.funnyai.data.Treap;
import static java.lang.System.out;

/**
 *
 * @author happyli
 */
public class C_Convert_AI {

    public C_Token_Link_and_Add getConvert2(C_Convert pConvert, Treap pTreap_Active, 
            C_Token_Link pToken_Link) {//, boolean bConvert
        
        boolean bConvert = false;//返回值先设置为
        C_Token_Link pLinkR = new C_Token_Link();
        pLinkR.Sentence = pToken_Link.Sentence;
        C_Token pToken_Old;
        for (int i = 0; i<pToken_Link.Count(); i++) {
            pToken_Old = pToken_Link.Item(i);
            if (pToken_Old.bCheckConvert == false){//如果没有转化过
                C_Token_Link_and_Add pLinkNew2 = pConvert.getConvert_Token(pTreap_Active, pToken_Old);//多个token组成的link，因为Convert可能会把一个单词变为多个单词
                if (pLinkNew2!=null){
                    C_Token_Link pLinkNew=pLinkNew2.pLink;
                    if (pLinkNew2.bAdd) {//如果刚才有添加新Token行为，则表示发生了转化
                        bConvert = true;
                        if (pLinkNew.Count() == 1) { //如果转化后仍旧是一个单词
                            C_Token pToken = pLinkNew.Item(0);
                            pToken.pChild.add(pToken_Old);
                            pToken.iStart = pToken_Old.iStart;
                            pToken.iEnd = pToken_Old.iEnd;
                            pLinkR.Add(pToken);
                        }else if (pLinkNew.Count() > 1){  //如果转化后为多个单词，比如，抗癌=治疗 癌症
                            for (int j = 0; j<= (pLinkNew.Count() - 1); j++) {
                                pLinkR.Add(pLinkNew.Item(j));
                            }
                        }else{  //0个 错误，一般不会到这
                            pLinkR.Add(pToken_Old);
                        }
                    }else{
                        pLinkR.Add(pToken_Old);
                    }
                }
            }
            else {
                pLinkR.Add(pToken_Old);
            }
        }
        if (bConvert) {
            //代表已经替换过
            pLinkR.iStart = pLinkR.Item(0).iStart;
            pLinkR.iEnd = pLinkR.Item(pLinkR.Count() - 1).iEnd;
        }
        else {
            //如果相同的则用原先的
            pLinkR = pToken_Link;
        }
        C_Token_Link_and_Add p=new C_Token_Link_and_Add();
        p.pLink = pLinkR;
        p.bAdd = bConvert;
        return p;//pLinkR;
    }
    
}
