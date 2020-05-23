package com.xx.chinetek.cywms.SplitZero;

import android.content.Context;
import android.os.Message;
import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.xx.chinetek.base.BaseActivity;
import com.xx.chinetek.base.BaseApplication;
import com.xx.chinetek.cywms.OffShelf.OffshelfBatchScanModel;
import com.xx.chinetek.cywms.R;
import com.xx.chinetek.model.URLModel;
import com.xx.chinetek.model.WMS.Print.PrintBean;
import com.xx.chinetek.model.WMS.Stock.StockInfo_Model;
import com.xx.chinetek.util.Network.NetworkError;
import com.xx.chinetek.util.Network.RequestHandler;
import com.xx.chinetek.util.SharePreferUtil;
import com.xx.chinetek.util.dialog.MessageBox;
import com.xx.chinetek.util.dialog.ToastUtil;
import com.xx.chinetek.util.function.GsonUtil;
import com.xx.chinetek.util.hander.MyHandler;
import com.xx.chinetek.util.listener.NetCallBackListener;
import com.xx.chinetek.util.log.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @ Des:
 * @ Created by yangyiqing on 2019/11/14.
 */
public class SplitZeroModel {
    MyHandler<BaseActivity> mHandler;
    Context                 mContext;

    public        String                           TAG_GetStockModelADF              = "SplitZeroModel_Single_GetStockModelADF";
    public        String                           TAG_SaveT_BarCodeToStockADF       = "SplitZeroModel_Single_SaveT_BarCodeToStockADF";
    public        String                           TAG_GetSubStockModelADF              = "SplitZeroModel_Single_GetStockModelADF";
    private final int                              RESULT_Msg_GetStockModelADF       = 102;
    private final int                              RESULT_SaveT_BarCodeToStockADF    = 103;
    private final int                              result_msg_GetBarcodeModelForJADF = 104;
    private       Map<String, NetCallBackListener> mNetMap                           = new HashMap<>();


    private StockInfo_Model mFatherInfo;
    private StockInfo_Model mSubInfo;
    OffshelfBatchScanModel mPrintModel;

    public SplitZeroModel(Context context, MyHandler<BaseActivity> handler) {
        mContext = context;
        mHandler = handler;
        mPrintModel = new OffshelfBatchScanModel(context);

    }


    public void onHandleMessage(Message msg) {
        NetCallBackListener<String> listener = null;
        switch (msg.what) {
            case RESULT_Msg_GetStockModelADF:
                listener = mNetMap.get("TAG_GetStockModelADF");
                break;
            case RESULT_SaveT_BarCodeToStockADF:
                listener = mNetMap.get("TAG_SaveT_BarCodeToStockADF");
                break;
            case result_msg_GetBarcodeModelForJADF:
                listener = mNetMap.get("TAG_GetSubStockModelADF");
                break;
            case NetworkError.NET_ERROR_CUSTOM:
                ToastUtil.show("获取请求失败_____" + msg.obj);

                break;
        }
        if (listener != null) {
            listener.onCallBack(msg.obj.toString());
        }
    }

    /**
     * @desc: 查询父级WMS库存信息
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2019/11/20 21:05
     */
    public void requestFatherBarcodeInfoQuery(String fatherBarcode, NetCallBackListener<String> callBackListener) {
        mNetMap.put("TAG_GetStockModelADF", callBackListener);
        //2-整箱或者托发货 3-零数发货
        int Scantype = 3;
        final Map<String, String> params = new HashMap<String, String>();
        StockInfo_Model model = new StockInfo_Model();
        model.setBarcode(fatherBarcode);
        model.setScanType(Scantype);
        String Json = GsonUtil.parseModelToJson(model);
        params.put("ModelStockJson", Json);
        LogUtil.WriteLog(mContext.getClass(), TAG_GetStockModelADF, fatherBarcode);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetStockModelADF, "正在查询父级条码信息...", mContext, mHandler, RESULT_Msg_GetStockModelADF, null, URLModel.GetURL().GetStockModelADF, params, null);
    }


    /**
     * @desc: 查询子级WMS库存信息
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2019/11/20 21:06
     */
    public void requestSubBarcodeInfoStockQuery(String subBarcode, NetCallBackListener<String> callBackListener) {
        mNetMap.put("TAG_GetSubStockModelADF", callBackListener);
        final Map<String, String> params = new HashMap<String, String>();
        params.put("Serialno", subBarcode);
        LogUtil.WriteLog(mContext.getClass(), TAG_GetSubStockModelADF, subBarcode);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetSubStockModelADF, mContext.getString(R.string.Msg_GetT_SerialNoByPalletADF), mContext, mHandler, result_msg_GetBarcodeModelForJADF, null, URLModel.GetURL().GetBarcodeModelForJADF, params, null);
    }

    /**
     * @desc: 提交拆零数据
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2019/11/20 21:06
     */
    public void requestSplitBarcodeQuery(StockInfo_Model createInfo, NetCallBackListener<String> callBackListener) {
        mNetMap.put("TAG_SaveT_BarCodeToStockADF", callBackListener);
        String strOldBarCode = GsonUtil.parseModelToJson(createInfo);
        String userJson = GsonUtil.parseModelToJson(BaseApplication.userInfo);
        final Map<String, String> params = new HashMap<String, String>();
        params.put("UserJson", userJson);
        params.put("strOldBarCode", strOldBarCode);
        params.put("strNewBarCode", "");
        params.put("PrintFlag", "2"); //1：打印 2：不打印
        LogUtil.WriteLog(mContext.getClass(), TAG_SaveT_BarCodeToStockADF, strOldBarCode);
        SharePreferUtil.ReadSupplierShare(mContext);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SaveT_BarCodeToStockADF, mContext.getString(R.string.Msg_SaveT_BarCodeToStockADF), mContext, mHandler, RESULT_SaveT_BarCodeToStockADF, null, URLModel.GetURL().SaveT_BarCodeToStockADF, params, null);

    }




    /**
     * @desc: 保存父级条码信息
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2019/11/20 11:01
     */
    public void setFatherInfo(@NonNull StockInfo_Model fatherInfo) {
        mFatherInfo = fatherInfo;
    }


    public StockInfo_Model getFatherInfo() {
        return mFatherInfo;
    }

    public void setSubInfo(@NonNull StockInfo_Model subInfo) {
        mSubInfo = subInfo;

    }

    public StockInfo_Model getSubInfo() {
        return mSubInfo;
    }


    public void onClear() {
        mFatherInfo = null;
        mSubInfo = null;

    }


    /**
     * @desc: 打印外箱条码
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/5/7 11:05
     */
    public void onPrint(StockInfo_Model info) {
        if (info != null) {
            PrintBean bean = new PrintBean();
            bean.setMaterialNo(info.getMaterialNo());
            bean.setMaterialDesc(info.getMaterialDesc());
            bean.setBarcode(info.getBarcode());
            bean.setSerialNo(info.getSerialNo());
            bean.setQty(info.getQty().intValue());
            bean.setProjectNo(info.getProjectNo());
            bean.setTraceNo(info.getTracNo());
            bean.setSpec(info.getSpec());
            if (mPrintModel != null) {
                List<PrintBean> list = new ArrayList<>();
                list.add(bean);
                mPrintModel.ptintLPK130OutBarcode(list);
            }

        } else {
            MessageBox.Show(mContext, "拆零生成的外箱条码数据不能为空");
        }
    }
}
