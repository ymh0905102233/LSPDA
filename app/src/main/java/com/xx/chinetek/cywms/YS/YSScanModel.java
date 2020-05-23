package com.xx.chinetek.cywms.YS;

import android.content.Context;
import android.os.Message;

import com.android.volley.Request;
import com.xx.chinetek.base.BaseActivity;
import com.xx.chinetek.base.BaseApplication;
import com.xx.chinetek.cywms.R;
import com.xx.chinetek.model.BaseResultInfo;
import com.xx.chinetek.model.Material.BarCodeInfo;
import com.xx.chinetek.model.Receiption.ReceiptDetail_Model;
import com.xx.chinetek.model.Receiption.Receipt_Model;
import com.xx.chinetek.model.URLModel;
import com.xx.chinetek.model.User.UerInfo;
import com.xx.chinetek.util.Network.NetworkError;
import com.xx.chinetek.util.Network.RequestHandler;
import com.xx.chinetek.util.dialog.ToastUtil;
import com.xx.chinetek.util.function.ArithUtil;
import com.xx.chinetek.util.function.GsonUtil;
import com.xx.chinetek.util.hander.MyHandler;
import com.xx.chinetek.util.listener.NetCallBackListener;
import com.xx.chinetek.util.log.LogUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.xx.chinetek.util.function.GsonUtil.parseModelToJson;

/**
 * @ Des:
 * @ Created by yangyiqing on 2019/11/28.
 */
public class YSScanModel {
    MyHandler<BaseActivity> mHandler;
    Context                 mContext;
    String                  TAG_GetTYSDetailListByHeaderIDADF = "YSScanModel_GetT_GetTYSDetailListByHeaderIDADF";
    String                  TAG_GetOutBarCodeForYS            = "YSScanModel_GetT_PalletDetailByBarCodeADF";
    String                  TAG_YSPost                        = "YSScanModel_SaveT_TAG_YSPost";
    private final int                              RESULT_Msg_GetTYSDetailListByHeaderIDADF = 101;
    private final int                              RESULT_Msg_GetOutBarCodeForYS            = 102;
    private final int                              RESULT_Msg_YSPost                        = 103;
    private       Map<String, NetCallBackListener> mNetMap                                  = new HashMap<>();
    private       List<ReceiptDetail_Model>        mDetailsList                             = new ArrayList<>();  //预留数据表体
    public final  String                           SCAN_TYPE_HISTORY                        = "scan_type_history";
    public final  String                           SCAN_TYPE_NEW_CREATED                    = "scan_type_new_created";
    public final  String                           SCAN_TYPE_NEW_UPDATE                     = "scan_type_update";
    private       List<BarCodeInfo>                mCheckList                               = new ArrayList<>();  //校验条码重复
    private       ReceiptDetail_Model              mCurrentDetailInfo                       = null;
    private       UUID                             mUuid                                    = null;
    private       BarCodeInfo      mBarCodeInfo=null;
    public YSScanModel(Context context, MyHandler<BaseActivity> handler) {
        mContext = context;
        mHandler = handler;
    }


    public void onHandleMessage(Message msg) {
        NetCallBackListener<String> listener = null;
        switch (msg.what) {
            case RESULT_Msg_GetTYSDetailListByHeaderIDADF:
                listener = mNetMap.get("TAG_GetTYSDetailListByHeaderIDADF");
                break;
            case RESULT_Msg_GetOutBarCodeForYS:
                listener = mNetMap.get("TAG_GetOutBarCodeForYS");
                break;
            case RESULT_Msg_YSPost:
                listener = mNetMap.get("TAG_YSPost");
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
     * @desc: 查询预留条码
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2019/11/20 21:05
     */
    public void requestYSBarcodeInfoQuery(String barcode, NetCallBackListener<String> callBackListener) {
        mNetMap.put("TAG_GetOutBarCodeForYS", callBackListener);
        final Map<String, String> params = new HashMap<String, String>();
        params.put("BarCode", barcode);
        LogUtil.WriteLog(YSScan.class, TAG_GetOutBarCodeForYS, barcode);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetOutBarCodeForYS, mContext.getResources().getString(R.string.Msg_GetT_SerialNoByPalletADF), mContext, mHandler, RESULT_Msg_GetOutBarCodeForYS, null, URLModel.GetURL().GetOutBarCodeForYS, params, null);

    }

    /**
     * @desc: 获取预留明细
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/5/9 7:44
     */
    public void requestYSDetailsQuery(Receipt_Model receiptModel, NetCallBackListener<String> callBackListener) {
        mNetMap.put("TAG_GetTYSDetailListByHeaderIDADF", callBackListener);
        final ReceiptDetail_Model receiptDetailModel = new ReceiptDetail_Model();
        receiptDetailModel.setHeaderID(receiptModel.getID());
        receiptDetailModel.setErpVoucherNo(receiptModel.getErpVoucherNo());
        receiptDetailModel.setVoucherType(receiptModel.getVoucherType());
        final Map<String, String> params = new HashMap<String, String>();
        params.put("ModelDetailJson", parseModelToJson(receiptDetailModel));
        String para = (new JSONObject(params)).toString();
        LogUtil.WriteLog(YSScan.class, TAG_GetTYSDetailListByHeaderIDADF, para);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetTYSDetailListByHeaderIDADF, mContext.getResources().getString(R.string.Msg_GetT_YSDetailListByHeaderIDADF), mContext, mHandler, RESULT_Msg_GetTYSDetailListByHeaderIDADF, null, URLModel.GetURL().GetTYSDetailListByHeaderIDADF, params, null);

    }

    /**
     * @desc: 预留释放提交
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/5/9 7:54
     */
    public void requestSaveYSDetails(List<ReceiptDetail_Model> list, NetCallBackListener<String> callBackListener)  {
        try {
        mNetMap.put("TAG_YSPost", callBackListener);
        final Map<String, String> params = new HashMap<String, String>();
        String ModelJson = GsonUtil.parseModelToJson(list);
//        String UserJson = GsonUtil.parseModelToJson(BaseApplication.userInfo);
        UerInfo userInfo=null;

            userInfo = BaseApplication.userInfo.clone();

        userInfo.setWarehouseID(mBarCodeInfo.getWareHouseID());
        userInfo.setReceiveHouseID(mBarCodeInfo.getHouseID());
        userInfo.setReceiveAreaID(mBarCodeInfo.getAreaID());
        String UserJson = GsonUtil.parseModelToJson(userInfo);
        params.put("UserJson", UserJson);
        params.put("ModelJson", ModelJson);
        LogUtil.WriteLog(YSScan.class, TAG_YSPost, ModelJson);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_YSPost, mContext.getString(R.string.Msg_SaveT_InStockDetailADF), mContext, mHandler, RESULT_Msg_YSPost, null, URLModel.GetURL().YSPost, params, null);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

    }

    public void setList(List<ReceiptDetail_Model> list) {
        mDetailsList.clear();
        mDetailsList.addAll(list);
    }

    public List<ReceiptDetail_Model> getList() {
        return mDetailsList;
    }

    public void onClear() {
        mDetailsList.clear();
    }

    public BarCodeInfo getBarCodeInfo() {
        return mBarCodeInfo;
    }

    public void setBarCodeInfo(BarCodeInfo mBarCodeInfo) {
        this.mBarCodeInfo = mBarCodeInfo;
    }

    /**
     * @desc: 验证条码的物料是否在明细中, 检查条码是否重复，是否超出数量
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2019/9/20 15:49
     */
    public BaseResultInfo<Boolean, Void> checkBarcode(BarCodeInfo barcode) {
        int Index = -1;
        mCurrentDetailInfo = null;
        BaseResultInfo<Boolean, Void> resultInfo = new BaseResultInfo<>();
        try {
            if (barcode == null || barcode.getSerialNo() == null) {
                resultInfo.setHeaderStatus(false);
                resultInfo.setMessage("校验条码失败,条码信息不能为空 ");
                return resultInfo;
            }

            if (mDetailsList == null && mDetailsList.size() == 0) {
                resultInfo.setHeaderStatus(false);
                resultInfo.setMessage("获取的预留释放单表体明细为空!");
                return resultInfo;
            }

            if (mCheckList.contains(barcode)) {  //检查是否重复
                resultInfo.setHeaderStatus(false);
                resultInfo.setMessage("条码:" + barcode.getSerialNo() + "不能重复扫描");
                return resultInfo;
            }
            String barcodeTrace = barcode.getTracNo() != null ? barcode.getTracNo() : "";
            String originalCode = barcode.getOriginalCode() != null ? (!barcode.getOriginalCode().isEmpty() ? barcode.getOriginalCode().trim() : "") : "";
            String materialNo = barcode.getMaterialNo() != null ? barcode.getMaterialNo() : "";
            for (int i = 0; i < mDetailsList.size(); i++) {
                ReceiptDetail_Model info = mDetailsList.get(i);
                boolean isYSType = false; //是否是预留类型   true   数量大于0  是  ；false 数量小于0 不是
                if (info != null) {
                    isYSType = info.getRemainQty() > 0 ? true : false;
                    String sTraceNo = info.getTracNo() != null ? info.getTracNo() : "";
                    String sMaterialNo = info.getMaterialNo() != null ? info.getMaterialNo() : "";
                    if (isYSType && originalCode.equals("1") || !isYSType && originalCode.equals("")) {
                        if (sTraceNo.equals(barcodeTrace) && materialNo.equals(sMaterialNo)) {
                            Index = i;
                            break;
                        }

                    }
                }
            }

            if (Index != -1) {
                mCurrentDetailInfo = mDetailsList.get(Index);
                BaseResultInfo<Boolean, Void> resultInfo1 = checkBarcodeQtyInMaterial(barcode, mCurrentDetailInfo);
                if (resultInfo1.getHeaderStatus() == false) {
                    resultInfo.setHeaderStatus(false);
                    resultInfo.setMessage(resultInfo1.getMessage());
                    return resultInfo;
                }
                if (mCurrentDetailInfo != null) {
                    resultInfo.setHeaderStatus(true);
                } else {
                    resultInfo.setHeaderStatus(false);
                    resultInfo.setMessage("验证条码出现异常,找到行号所对应的物料信息为空");
                    return resultInfo;
                }


            } else {
                resultInfo.setHeaderStatus(false);
                resultInfo.setMessage("条码:" + barcode.getBarCode() + "的物料编号:" + barcode.getMaterialNo() + "不在该预留释放单中");
                return resultInfo;
            }

        } catch (Exception e) {
            resultInfo.setHeaderStatus(false);
            resultInfo.setMessage("验证条码的物料信息出现异常:" + e.getMessage());
            return resultInfo;
        }

        return resultInfo;
    }

    /**
     * @desc: 验证条码数量是否超出物料行所需的数量
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2019/9/22 17:03
     */
    public BaseResultInfo<Boolean, Void> checkBarcodeQtyInMaterial(BarCodeInfo barcode, ReceiptDetail_Model currentDetailInfo) {
        BaseResultInfo<Boolean, Void> resultInfo = new BaseResultInfo<>();
        try {
            if (currentDetailInfo != null && barcode != null) {
                float scanQty = barcode.getQty();
                float taskQty = currentDetailInfo.getRemainQty() == null ? 0 : Math.abs(currentDetailInfo.getRemainQty());
                float upQty = currentDetailInfo.getScanQty() == null ? 0 : currentDetailInfo.getScanQty();
                float sumScanQty = ArithUtil.add(upQty, scanQty);
                float remainQty = ArithUtil.sub(taskQty, upQty);
                if (remainQty > 0) {
                    float secondRemainQty = ArithUtil.sub(remainQty, scanQty);
                    if (secondRemainQty >= 0) {
                        resultInfo.setHeaderStatus(true);
                    } else {
                        resultInfo.setHeaderStatus(false);
                        resultInfo.setMessage("扫描条码的数量 (" + barcode.getQty() + ")超出物料:" + currentDetailInfo.getMaterialNo() + "实际需要的数量" + Math.abs(secondRemainQty));
                        return resultInfo;
                    }

                } else {
                    if (remainQty == 0) {
                        resultInfo.setHeaderStatus(false);
                        resultInfo.setMessage("当前物料行:" + currentDetailInfo.getMaterialNo() + "已扫描完毕");
                        return resultInfo;
                    } else if (remainQty < 0) {
                        resultInfo.setHeaderStatus(false);
                        resultInfo.setMessage("扫描条码出现异常:已扫描数量 (" + upQty + ") 超过任务数量:(" + taskQty + ")");
                        return resultInfo;
                    }

                }
            } else {
                resultInfo.setHeaderStatus(false);
                StringBuilder builder = new StringBuilder();
                if (barcode == null) {
                    builder.append(" 扫描条码信息为空");
                }
                if (currentDetailInfo == null) {
                    builder.append(" 条码所在物料号信息为空");
                }
                resultInfo.setMessage("验证条码数量是否超出物料行失败" + builder.toString());
                return resultInfo;
            }
        } catch (Exception e) {
            resultInfo.setHeaderStatus(false);
            resultInfo.setMessage("验证条码出现意料之外的异常：" + e.getMessage());
        }

        return resultInfo;
    }

    /**
     * @desc: 绑定条码
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/5/9 13:23
     */
    public BaseResultInfo<Boolean, Void> bindBarcode(BarCodeInfo info) {
        BaseResultInfo<Boolean, Void> resultInfo = new BaseResultInfo<>();
        try {
            if (mCurrentDetailInfo != null) {
                if (mCurrentDetailInfo.getLstBarCode() == null) {
                    mCurrentDetailInfo.setLstBarCode(new ArrayList<BarCodeInfo>());
                }
                final int barIndex = mCurrentDetailInfo.getLstBarCode().indexOf(info);
                if (barIndex == -1) {
                    mCurrentDetailInfo.getLstBarCode().add(info);
                    float scanQty = info.getQty();
                    float upQty = mCurrentDetailInfo.getScanQty() == null ? 0 : mCurrentDetailInfo.getScanQty();
                    float sumScanQty = ArithUtil.add(upQty, scanQty);
                    mCurrentDetailInfo.setScanQty(sumScanQty);
                    mCheckList.add(info);
                    if (info.getOriginalCode()!=null && info.getOriginalCode().equals("")&& mBarCodeInfo==null){
                        mBarCodeInfo=info;
                    }
                    resultInfo.setHeaderStatus(true);
                } else {
                    resultInfo.setHeaderStatus(false);
                    resultInfo.setMessage("绑定条码失败:" + info.getSerialNo() + "不能重复扫描");
                    return resultInfo;
                }

            } else {
                resultInfo.setHeaderStatus(false);
                resultInfo.setMessage("绑定条码失败:没有获取到当前物料行信息");
                return resultInfo;
            }


        } catch (Exception e) {
            resultInfo.setHeaderStatus(false);
            resultInfo.setMessage("绑定条码出现意料之外的异常：" + e.getMessage());
        }
        return resultInfo;
    }

    /**
     * @desc: 物料行是否拣货完毕
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/5/9 13:39
     */
    public BaseResultInfo<Boolean, Void> isMaterialRowScanedFinish() {
        BaseResultInfo<Boolean, Void> resultInfo = new BaseResultInfo<>();
        boolean isMaterialRowScanFinish = true;
        try {
            if (mDetailsList != null && mDetailsList.size() > 0) {
                for (ReceiptDetail_Model model : mDetailsList) {
                    if (model != null) {
                        float taskQty = model.getRemainQty() == null ? 0 : Math.abs(model.getRemainQty());
                        float upQty = model.getScanQty() == null ? 0 : model.getScanQty();
                        float remainQty = ArithUtil.sub(taskQty, upQty);
                        if (remainQty != 0) {
                            isMaterialRowScanFinish = false;
                            break;
                        }
                    }

                }
            }

            if (isMaterialRowScanFinish) {
                resultInfo.setHeaderStatus(true);
            } else {
                resultInfo.setHeaderStatus(false);
                resultInfo.setMessage("预留释放单没有扫描完毕,不能提交");

            }
        } catch (Exception e) {
            resultInfo.setHeaderStatus(false);
            resultInfo.setMessage("绑定条码出现意料之外的异常：" + e.getMessage());
        }
        return resultInfo;

    }


}
