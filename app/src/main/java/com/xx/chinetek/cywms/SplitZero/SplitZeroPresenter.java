package com.xx.chinetek.cywms.SplitZero;

import android.content.Context;
import android.os.Message;

import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.base.BaseActivity;
import com.xx.chinetek.model.ReturnMsgModel;
import com.xx.chinetek.model.ReturnMsgModelList;
import com.xx.chinetek.model.WMS.Stock.StockInfo_Model;
import com.xx.chinetek.util.dialog.MessageBox;
import com.xx.chinetek.util.function.GsonUtil;
import com.xx.chinetek.util.hander.MyHandler;
import com.xx.chinetek.util.listener.NetCallBackListener;
import com.xx.chinetek.util.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @ Des:
 * @ Created by yangyiqing on 2019/11/14.
 */
public class SplitZeroPresenter {
    private Context                 mContext;
    private SplitZeroModel          mModel;
    private ISplitZeroView          mView;
    private MyHandler<BaseActivity> mHandler;


    public SplitZeroPresenter(Context context, ISplitZeroView view, MyHandler<BaseActivity> handler) {
        this.mContext = context;
        this.mView = view;
        this.mModel = new SplitZeroModel(context, handler);
        this.mHandler = handler;
    }
   public SplitZeroModel  getModel(){
        return  mModel;
   }
    /**
     * @desc: 扫描父级条码
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2019/11/14 17:38
     */
    public void scanFatherBarcode(String fatherBarcode) {
        if (fatherBarcode.equals("")) return;
        mModel.requestFatherBarcodeInfoQuery(fatherBarcode, new NetCallBackListener<String>() {
            @Override
            public void onCallBack(String result) {
                LogUtil.WriteLog(mContext.getClass(), mModel.TAG_GetStockModelADF, result);
                try {
                    ReturnMsgModelList<StockInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<StockInfo_Model>>() {
                    }.getType());
                    if (returnMsgModel.getHeaderStatus().equals("S")) {
                        //拆零模式
                        List<StockInfo_Model> stockInfoModels = returnMsgModel.getModelJson();
                        if (stockInfoModels != null && stockInfoModels.size() == 1) {
                            mModel.setFatherInfo(stockInfoModels.get(0));
                            if (mModel.getFatherInfo() != null) {
                                //拆零本体
                                mView.showModuleView(mModel.getFatherInfo().getQty().intValue());
                                mView.bindFatherBarcodeInfo(mModel.getFatherInfo());
                            }

                        } else {
                            MessageBox.Show(mContext, "获取父级条码信息为空");
                            mView.requestFatherBarcodeFocus();
                        }


                    } else {
                        MessageBox.Show(mContext, returnMsgModel.getMessage());
                        mView.requestFatherBarcodeFocus();
                    }
                } catch (Exception ex) {
                    MessageBox.Show(mContext, ex.getMessage());
                    mView.requestFatherBarcodeFocus();
                }
            }

        });
    }

    /**
     * @desc: 扫描子级条码
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2019/11/14 17:39
     */
    public void scanSubBarcode(String subBarcode) {
        if (subBarcode.equals("")) return;

        if (mModel.getFatherInfo() == null) {
            MessageBox.Show(mContext, "请先扫描父级条码");
            return;
        }
        mModel.requestSubBarcodeInfoStockQuery(subBarcode, new NetCallBackListener<String>() {
            @Override
            public void onCallBack(String result) {

                LogUtil.WriteLog(OffshelfBatchScan3.class, mModel.TAG_GetSubStockModelADF, result);
                try {
                    ReturnMsgModelList<StockInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<StockInfo_Model>>() {
                    }.getType());
                    if (returnMsgModel.getHeaderStatus().equals("S")) {
                        List<StockInfo_Model> Models = returnMsgModel.getModelJson();
                        if (Models == null || Models.size() == 0) {
                            MessageBox.Show(mContext, "获取的本体条码不能为空");
                            mView.requestSubBarcodeFocus();
                            return;
                        }

                        mModel.setSubInfo(Models.get(0));
                        if (mModel.getSubInfo() != null) {
                            String fSerialno = mModel.getFatherInfo().getSerialNo();
                            if (fSerialno != null && mModel.getSubInfo().getFserialno() != null) {
                                if (fSerialno.equals(Models.get(0).getFserialno())) {
                                    ArrayList<StockInfo_Model> lists = mModel.getFatherInfo().getLstJBarCode() == null ? new ArrayList<StockInfo_Model>() : mModel.getFatherInfo().getLstJBarCode();
                                    //判断是否已经扫描过的本体
                                    int index = lists.indexOf(Models.get(0));
                                    if (index < 0) {
                                        lists.add(Models.get(0));
                                        mModel.getFatherInfo().setLstJBarCode(lists);
                                    } else {
                                        MessageBox.Show(mContext, "该本体已经被扫描，不能重复扫描");
                                    }
                                } else {
                                    MessageBox.Show(mContext, "本体的父级序列号["+mModel.getSubInfo().getFserialno()+"]和外箱号["+fSerialno+"]不一致");

                                }

                            } else {
                                MessageBox.Show(mContext, "外箱的序列号和本体的父级序列号不能为空");
                            }
                        } else {
                            MessageBox.Show(mContext, "获取的本体条码不能为空");
                            mView.requestSubBarcodeFocus();
                            return;
                        }


                    } else {
                        MessageBox.Show(mContext, returnMsgModel.getMessage());
                        mView.requestSubBarcodeFocus();
                    }
                } catch (Exception ex) {
                    MessageBox.Show(mContext, ex.getMessage());
                    mView.requestSubBarcodeFocus();
                }

            }
        });
    }


    /**
     * @desc: 拆零提交
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/4/21 11:15
     */

    public void onSplitRefer(final int number) {
        if (mModel.getFatherInfo() == null) {
            MessageBox.Show(mContext, "请先扫描父级条码");
            return;
        }
        if (number <= 0) {
            MessageBox.Show(mContext, "输入的数量必须大于0");
            return;
        }
        if (mModel.getFatherInfo().getQty() - number < 0) {
            MessageBox.Show(mContext, "拆零的数量要小于外箱数量");
            return;
        }

        StockInfo_Model newmodel = mModel.getFatherInfo();
        //拆零
        newmodel.setPickModel(3);
        newmodel.setAmountQty(Float.valueOf(number));
        if (!mView.CheckBluetooth()) {
            MessageBox.Show(mContext, "蓝牙打印机连接失败");
            return;
        }
        mModel.requestSplitBarcodeQuery(newmodel, new NetCallBackListener<String>() {
            @Override
            public void onCallBack(String result) {
                try {
                    LogUtil.WriteLog(OffshelfBatchScan3.class, mModel.TAG_SaveT_BarCodeToStockADF, result);
                    ReturnMsgModel<StockInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<StockInfo_Model>>() {
                    }.getType());
                    if (returnMsgModel.getHeaderStatus().equals("S")) {
                        StockInfo_Model stockInfoModel = returnMsgModel.getModelJson();
                        mModel.onPrint(stockInfoModel);
                        onClear();
                    } else {
                        MessageBox.Show(mContext, returnMsgModel.getMessage());
                    }
                } catch (Exception ex) {
                    MessageBox.Show(mContext, ex.getMessage());
                }
                mView.setNumber(0);
                mView.requestFatherBarcodeFocus();

            }
        });
    }


    public void onHandleMessage(Message msg) {
        mModel.onHandleMessage(msg);
    }


    public void onClear() {
        mView.onClear();
        mModel.onClear();

    }
}
