package com.xx.chinetek.cywms.OffShelf;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.mylibrary.LPK130;
import com.xx.chinetek.model.URLModel;
import com.xx.chinetek.model.WMS.Print.PrintBean;
import com.xx.chinetek.util.dialog.MessageBox;

import java.util.List;

public class OffshelfBatchScanModel {
    private Context mContext;

    public OffshelfBatchScanModel(Context context) {
        mContext = context;
    }

    /**
     * @desc: 外箱打印
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/5/6 15:25
     */
    public void ptintLPK130OutBarcode(List<PrintBean> list) {
        LPK130 lpk130 = new LPK130();
        lpk130.closeDevice();
        if (lpk130.openDevice(URLModel.MacAdress) >= 0) {
            for (PrintBean bean : list) {
                try {
                    int pageHeight = 340; //页面高度
                    int pageWeight = 576;  //页面宽度
                    int fontHeight = 16;  //字体高度
                    int growthWeight = 40;  //增加宽度
                    int growthHeight = 15;  //增加高度
                    int currentY = 0;   //当前Y轴高度
                    int maxSingleLength = 25; //一行最大字数
                    int maxSingleLength2 = 20; //一行最大字数
                    if (lpk130 == null) return;
                    if (bean == null) {
                        Toast.makeText(mContext, "打印的外箱条码数据不能为空",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String materialNo = bean.getMaterialNo() != null ? bean.getMaterialNo() : "";
                    String materialDesc = bean.getMaterialDesc() != null ? bean.getMaterialDesc() : "";
                    String materialDesc1 = "";
                    String materialDesc2 = "";
                    String spec=bean.getSpec()!=null? bean.getSpec():"";
                    String spec1="";
                    String spec2="";
                    if (materialDesc.length() < maxSingleLength) {
                        if (!materialDesc.equals("")) {
                            materialDesc1 = materialDesc.substring(0, materialDesc.length());
                        }

                    } else {
                        materialDesc1 = materialDesc.substring(0, maxSingleLength);
                        materialDesc2 = materialDesc.substring(maxSingleLength, materialDesc.length());
                    }
                    if (spec.length() < maxSingleLength2) {
                        if (!spec.equals("")) {
                            spec1 = spec.substring(0, spec.length());
                        }

                    } else {
                        spec1 = spec.substring(0, maxSingleLength2);
                        spec2 = spec.substring(maxSingleLength2, spec.length());
                    }
                    String traceNo = bean.getTraceNo() != null ? bean.getTraceNo() : "";
                    String projectNo = bean.getProjectNo() != null ? bean.getProjectNo() : "";
                    String serialNo = bean.getSerialNo() != null ? bean.getSerialNo() : "";
                    String barcode = bean.getBarcode() != null ? bean.getBarcode() : "";
                    String standardBox=bean.getStandardBox()!=null?bean.getStandardBox():"";
                    String qty = bean.getQty() + "";
                    lpk130.NFCP_createPage(pageWeight, pageHeight);//起始设置
                    lpk130.NFCP_Page_setText(growthWeight, currentY, "物料编号:" + materialNo, 2, 0, 1, false, false);
                    currentY += fontHeight + growthHeight;
                    lpk130.NFCP_Page_setText(growthWeight, currentY, "物料名称:" + materialDesc1, 2, 0, 1, false, false);
                    if (!materialDesc2.isEmpty()) {
                        currentY += fontHeight + growthHeight;
                        lpk130.NFCP_Page_setText(growthWeight, currentY, materialDesc2, 2, 0, 1, false, false);

                    }
                    currentY += fontHeight + growthHeight;
                    lpk130.NFCP_Page_setText(growthWeight, currentY, "规格:" + spec1, 2, 0, 1, false, false);
                    if (!spec2.isEmpty()) {
                        currentY += fontHeight + growthHeight;
                        lpk130.NFCP_Page_setText(growthWeight, currentY, spec2, 2, 0, 1, false, false);

                    }

                    currentY += fontHeight + growthHeight;
                    lpk130.NFCP_Page_printQrCode(growthWeight, currentY, 0, 5, 2, barcode);
                    lpk130.NFCP_Page_setText(growthWeight * 5, currentY,  projectNo, 2, 0, 1, false, false);
                    currentY += fontHeight + growthHeight;
                    lpk130.NFCP_Page_setText(growthWeight * 5, currentY, "需求跟踪号:" + traceNo, 2, 0, 1, false, false);
                    currentY += fontHeight + growthHeight;
                    lpk130.NFCP_Page_setText(growthWeight * 5, currentY, "存货代码:" + standardBox, 2, 0, 1, false, false);
                    currentY += fontHeight + growthHeight;
                    lpk130.NFCP_Page_setText(growthWeight * 5, currentY, "数量:" + qty, 2, 0, 1, false, false);

                    //                    currentY += fontHeight + growthHeight * (4.5);
//                    currentY += fontHeight + growthHeight * (2.6);
                    currentY += fontHeight + growthHeight;
                    lpk130.NFCP_Page_setText(growthWeight * 5, currentY, "序列号:" + serialNo, 2, 0, 1, false, false);
                    lpk130.NFCP_printPage(0, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("TAG", e.getMessage());
                    MessageBox.Show(mContext, e.getMessage().toString());
                }
            }

        } else {
            Toast.makeText(mContext, "设备连接失败，请重新连接！",
                    Toast.LENGTH_SHORT).show();
        }
    }


}
