package com.xx.chinetek.Pallet;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.mylibrary.LPK130;
import com.xx.chinetek.model.URLModel;
import com.xx.chinetek.model.WMS.Print.PrintBean;
import com.xx.chinetek.util.dialog.MessageBox;

public class CombinModel {
    private Context mContext;

    public CombinModel(Context context) {
        mContext = context;
    }

    /**
     * @desc: 外箱打印
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/5/6 15:25
     */
    public void ptintLPK130PalletNo(PrintBean bean) {
        LPK130 lpk130 = new LPK130();
        lpk130.closeDevice();
        if (lpk130.openDevice(URLModel.MacAdress) >= 0) {

                try {
                    int pageHeight = 340; //页面高度
                    int pageWeight = 576;  //页面宽度
                    int fontHeight = 16;  //字体高度
                    int growthWeight = 40;  //增加宽度
                    int growthHeight = 20;  //增加高度
                    int currentY = 0;   //当前Y轴高度
                    int maxSingleLength = 16; //一行最大字数
                    if (lpk130 == null) return;
                    if (bean == null || bean.getPalletNo()==null ) {
                        Toast.makeText(mContext, "打印的托盘条码数据不能为空",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String palletNo = bean.getPalletNo() != null ? bean.getPalletNo() : "";
                    lpk130.NFCP_createPage(pageWeight, pageHeight);//起始设置
                    currentY+=growthHeight*3;
                    lpk130.NFCP_Page_setText((int) (growthWeight*(5.8)),currentY,"托盘标签",2, 0, 1, false, false);
                    currentY += fontHeight + growthHeight;
                    lpk130.NFCP_Page_printQrCode(growthWeight*5, currentY, 0, 5, 2, palletNo);
                    currentY += fontHeight + growthHeight*7.4;
                    lpk130.NFCP_Page_setText(growthWeight*5, currentY,  palletNo, 2, 0, 1, false, false);

                    lpk130.NFCP_printPage(0, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("TAG", e.getMessage());
                    MessageBox.Show(mContext, e.getMessage().toString());
                }


        } else {
            Toast.makeText(mContext, "设备连接失败，请重新连接！",
                    Toast.LENGTH_SHORT).show();
        }
    }


}
