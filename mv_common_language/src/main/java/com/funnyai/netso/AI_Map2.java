package com.funnyai.netso;

import com.funnyai.Segmentation.C_Segmentation_Struct;
import com.funnyai.Segmentation.C_Convert;
import com.funnyai.Segmentation.C_Segmentation;
import com.funnyai.string.Old.S_Strings;
import java.util.ArrayList;

/**
 *
 * @author happyli
 */
public class AI_Map2 {

    public static String MakeAMap(
            C_Segmentation pSeg,
            C_Concept_Net2 pCN,
            C_Convert pConvert,
            C_Segmentation_Struct pStruct,
            String strMap, String strFrom, String strTo, 
            String strMap2, String strScale,
            ArrayList<C_Topic_Key_W> pListAdd) {
        String strReturn = "";
        switch (strMap.toLowerCase()) {
            case "funnyai.concept":
                if ((strFrom.startsWith("{") == false)) {
                    pSeg.readLine_fromDic(strFrom);
                }
                pConvert.read_fromDic(strFrom, strTo, "");
                break;
            case "concept.map"://概念网络做的映射
                if (strMap2.startsWith("Concept.")) {
                    strReturn = strReturn + MakeAMap(pSeg,pCN,pConvert,pStruct,
                            strMap2, strFrom, strTo, "", strScale,pListAdd);
                }
                else {
                    String strNewConcept = strFrom + "," + strMap2;
                    if (pCN!=null) {
                        pListAdd.add(new C_Topic_Key_W(strFrom,strNewConcept,Double.valueOf(strScale)));
//                        pCN.addConcept(strNewConcept, strFrom, Double.valueOf(strScale));//, Connect_Type.Connect_Value.Class_Expand_Type);
                        pListAdd.add(new C_Topic_Key_W(strMap2,strNewConcept,Double.valueOf(strScale)));
//                        pCN.addConcept(strNewConcept, strMap2, Double.valueOf(strScale));//, Connect_Type.Connect_Value.Class_Type);
                        pListAdd.add(new C_Topic_Key_W(strNewConcept,strTo,Double.valueOf(strScale)));
//                        pCN.addConcept(strTo, strNewConcept, Double.valueOf(strScale));//, Connect_Type.Connect_Value.Class_Type);
                    }
                }
                break;
            case "concept.class.expand":
                if (pCN!=null) {
                    pListAdd.add(new C_Topic_Key_W(strTo,strFrom,Double.valueOf(strScale)));
//                    pCN.addConcept(strFrom, strTo, Double.valueOf(strScale));//, Connect_Type.Connect_Value.Class_Expand_Type);
                }
                break;
            case "concept.subset":
                if (pCN!=null) {
                    pListAdd.add(new C_Topic_Key_W(strTo,strFrom,Double.valueOf(strScale)));
//                    pCN.addConcept(strFrom, strTo, Double.valueOf(strScale));//, Connect_Type.Connect_Value.Class_Type);
                }
                break;
            case "its.fire":
                if (strTo.toLowerCase().startsWith("topic.")) {
                    // Dim ID As Int32 = AI.FW_Topic.NewID("ID")
                    // AI.FW_Topic.Key_insert("", strTo, ID, 1, False)
                    // AI.FW_Topic.saveContent("", ID, "content", strTo, False)
                }
                // Dim strScale2 As String = AI.FW.readString("", "ITS/" + strFrom + "/" + strTo)
                // AI_ITS.pNetSO.AddPoint(strFrom, strTo, 1)
                break;
            case "cn.c":
                pSeg.readLine_fromDic(strFrom);
                pSeg.readLine_fromDic(strTo);
                if (pCN!=null) {
                    pListAdd.add(new C_Topic_Key_W(strTo,strFrom,Double.valueOf(strScale)));
                }
                switch(strTo){
                    case "all":
                    case "root":
                    case "词汇":
                    case "":
                        break;
                    default:
                        pConvert.read_fromDic(strFrom, "{"+ strTo + "s}", "");
                        pConvert.read_fromDic("{"+ strFrom + "s}", "{"+ strTo + "s}", "");
                        break;
                }
                break;
            case "cn.e":
                pSeg.readLine_fromDic(strTo);
                if (strTo.indexOf(" ")>0){
                    strTo=strTo.replace(" ","");
                }
                if (strFrom.indexOf(" ")>0){
                    String strFrom2=strFrom.replace(" ","");
                    pStruct.readLine_FromDic(pConvert, strFrom, strFrom2,0,0);
                    pSeg.readLine_fromDic(strFrom2);
                    if (pCN!=null){
                        pListAdd.add(new C_Topic_Key_W(strTo,strFrom2,Double.valueOf(strScale)));
//                        pCN.addConcept(strFrom2, strTo, Double.valueOf(strScale));//, Connect_Type.Connect_Value.Element_Type);
                    }
                    pConvert.read_fromDic(strFrom2, "{"+ strTo + "s}", "");
                }else{
                    if (S_Strings.isNumeric(strFrom)==false){
                        pSeg.readLine_fromDic(strFrom);
                        if (pCN!=null){
                            pListAdd.add(new C_Topic_Key_W(strTo,strFrom,Double.valueOf(strScale)));
//                            pCN.addConcept(strFrom, strTo, Double.valueOf(strScale));//, Connect_Type.Connect_Value.Element_Type);
                        }
                        pConvert.read_fromDic(strFrom, "{"+ strTo + "s}", "");
                    }
                }
                break;
        }
        return strReturn;
    }
    
}
