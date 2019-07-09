package com.funnyai.concept;

import com.funnyai.Segmentation.*;
import com.funnyai.netso.Connect_Type;

/**
 *
 * @author happyli
 */
public class AI_Map {

    public static String MakeAMap(
            C_Segmentation pSeg,
            C_Concept_Net pCN,
            C_Convert pConvert,
            C_Segmentation_Struct pStruct,
            String strMap, String strFrom, String strTo, 
            String strMap2, String strScale) {
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
                            strMap2, strFrom, strTo, "", strScale);
                }else{
                    String strNewConcept = strFrom + "," + strMap2;
                    if (pCN!=null) {
                        pCN.addConcept(strNewConcept, strFrom, Double.valueOf(strScale), Connect_Type.Connect_Value.Class_Expand_Type);
                        pCN.addConcept(strNewConcept, strMap2, Double.valueOf(strScale), Connect_Type.Connect_Value.Class_Type);
                        pCN.addConcept(strTo, strNewConcept, Double.valueOf(strScale), Connect_Type.Connect_Value.Class_Type);
                    }
                }
                break;
            case "concept.class.expand":
                if (pCN!=null) {
                    pCN.addConcept(strFrom, strTo, Double.valueOf(strScale), Connect_Type.Connect_Value.Class_Expand_Type);
                }
                break;
            case "concept.subset":
                if (pCN!=null) {
                    pCN.addConcept(strFrom, strTo, Double.valueOf(strScale), Connect_Type.Connect_Value.Class_Type);
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
                    pCN.addConcept(strFrom, strTo, Double.valueOf(strScale),Connect_Type.Connect_Value.Class_Type);
                }
//                pConvert.read_fromDic(strFrom, "{"+ strTo + "}", "");
                pConvert.read_fromDic(strFrom, "{"+ strTo + "s}", "");
                //if (strFrom.indexOf(" ")>1){
                    pConvert.read_fromDic("{"+ strFrom + "s}", "{"+ strTo + "s}", "");
                //}
//                pConvert.read_fromDic(strFrom, "{集s}", "");
                break;
        }
        return strReturn;
    }
    
}
