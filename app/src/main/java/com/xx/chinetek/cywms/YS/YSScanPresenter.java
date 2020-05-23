package com.xx.chinetek.cywms.YS;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.base.BaseActivity;
import com.xx.chinetek.model.BaseResultInfo;
import com.xx.chinetek.model.Base_Model;
import com.xx.chinetek.model.Material.BarCodeInfo;
import com.xx.chinetek.model.Receiption.ReceiptDetail_Model;
import com.xx.chinetek.model.Receiption.Receipt_Model;
import com.xx.chinetek.model.ReturnMsgModel;
import com.xx.chinetek.model.ReturnMsgModelList;
import com.xx.chinetek.util.dialog.MessageBox;
import com.xx.chinetek.util.function.GsonUtil;
import com.xx.chinetek.util.hander.MyHandler;
import com.xx.chinetek.util.listener.NetCallBackListener;
import com.xx.chinetek.util.log.LogUtil;

import java.util.List;

/**
 * @ Des:
 * @ Created by yangyiqing on 2019/11/28.
 */
public class YSScanPresenter {
    private Context     mContext;
    private YSScanModel mModel;
    private IYSScanView mView;


    public YSScanPresenter(Context context, IYSScanView view, MyHandler<BaseActivity> handler) {
        this.mContext = context;
        this.mView = view;
        this.mModel = new YSScanModel(context, handler);

    }

    public YSScanModel getModel() {
        return mModel;
    }

    /**
     * @desc: 扫描预留条码
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/5/9 7:34
     */
    public void onScan(String barcode) {
        if (barcode == null || barcode.equals("")) {
            MessageBox.Show(mContext, "条码不能为空!");
            mView.requestScanBarcodeFocus();
            return;
        }

        mModel.requestYSBarcodeInfoQuery(barcode, new NetCallBackListener<String>() {
            @Override
            public void onCallBack(String result) {
                try {
                    LogUtil.WriteLog(mContext.getClass(), mModel.TAG_GetOutBarCodeForYS, result);
                    ReturnMsgModel<BarCodeInfo> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<BarCodeInfo>>() {
                    }.getType());
                    if (returnMsgModel.getHeaderStatus().equals("S")) {
                        BarCodeInfo info = returnMsgModel.getModelJson();
                        if (info != null) {
                            BaseResultInfo<Boolean, Void> resultInfo = mModel.checkBarcode(info);
                            if (resultInfo.getHeaderStatus()) {
                                BaseResultInfo<Boolean, Void> resultBindInfo = mModel.bindBarcode(info);
                                if (resultBindInfo.getHeaderStatus()) {
                                    if(info.getOriginalCode()==null||info.getOriginalCode()!=null && !info.getOriginalCode().equals("1")  ){
                                        if (mModel.getBarCodeInfo()==null){
                                            mModel.setBarCodeInfo(info);
                                        }

                                    }
                                    mView.setBarcodeInfo(info);
                                    mView.bindListView(mModel.getList());
                                    mView.requestScanBarcodeFocus();
                                } else {
                                    MessageBox.Show(mContext, resultBindInfo.getMessage());
                                    mView.requestScanBarcodeFocus();
                                    return;
                                }
                            } else {
                                MessageBox.Show(mContext, resultInfo.getMessage());
                                mView.requestScanBarcodeFocus();
                                return;
                            }


                        } else {
                            MessageBox.Show(mContext, "从服务端查询的条码信息为空");
                            return;
                        }
                    } else {
                        MessageBox.Show(mContext, returnMsgModel.getMessage());
                        return;
                    }
                } catch (Exception ex) {
                    MessageBox.Show(mContext, ex.getMessage());
                    return;
                } finally {
                    mView.requestScanBarcodeFocus();
                }
            }
        });
    }

    /**
     * @desc: 获取预留明细
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/5/9 7:48
     */
    public void onRequestYSDetails(final Receipt_Model receipt_model) {
        if (receipt_model != null) {
            mModel.requestYSDetailsQuery(receipt_model, new NetCallBackListener<String>() {
                @Override
                public void onCallBack(String result) {
                    LogUtil.WriteLog(YSScan.class, mModel.TAG_GetTYSDetailListByHeaderIDADF, result);
                    try {
                        ReturnMsgModelList<ReceiptDetail_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<ReceiptDetail_Model>>() {
                        }.getType());
                        if (returnMsgModel.getHeaderStatus().equals("S")) {
                            List<ReceiptDetail_Model> list = returnMsgModel.getModelJson();
                            if (list != null) {
                                mModel.setList(list);
                            }

                            if (mModel.getList() != null && mModel.getList().size() > 0) {
                                mView.bindListView(mModel.getList());
                                mView.setOrderNo(receipt_model.getErpVoucherNo());
                                mView.requestScanBarcodeFocus();
                            } else {
                                MessageBox.Show(mContext, returnMsgModel.getMessage());
                            }
                        } else {
                            MessageBox.Show(mContext, returnMsgModel.getMessage());
                        }
                    } catch (Exception ex) {
                        MessageBox.Show(mContext, ex.getMessage());
                    }
                    mView.requestScanBarcodeFocus();
                }
            });
        } else {
            MessageBox.Show(mContext, "获取预留释放单明细为空!");
        }


    }

    /**
     * @desc: 提交
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/1/2 13:33
     */
    public void onRefer() {
        if (mModel.getBarCodeInfo()==null){
            MessageBox.Show(mContext,"校验提交参数失败,mBarCodeInfo没有数据");
            return;
        }
        BaseResultInfo<Boolean, Void> resultInfo = mModel.isMaterialRowScanedFinish();
          if (resultInfo.getHeaderStatus()){

              mModel.requestSaveYSDetails(mModel.getList(), new NetCallBackListener<String>() {
                  @Override
                  public void onCallBack(String result) {
                      LogUtil.WriteLog(YSScan.class, mModel.TAG_YSPost, result);
                      try {
                          final ReturnMsgModel<Base_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<Base_Model>>() {
                          }.getType());
                          if (returnMsgModel.getHeaderStatus().equals("S")) {
                              new AlertDialog.Builder(mContext).setTitle("提示").setCancelable(false).setIcon(android.R.drawable.ic_dialog_info).setMessage(returnMsgModel.getMessage())
                                      .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                          @Override
                                          public void onClick(DialogInterface dialog, int which) {
                                              mView.closeActivity();
                                          }
                                      }).show();
                          } else {
                              MessageBox.Show(mContext, returnMsgModel.getMessage());
                          }
                      } catch (Exception ex) {
                          MessageBox.Show(mContext, ex.getMessage());
                      }
                      mView.requestScanBarcodeFocus();
                  }
              });
          }else {
              MessageBox.Show(mContext, resultInfo.getMessage());
          }

    }


    /**
     * @desc: 清空
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2019/12/31 14:58
     */
    public void onClear() {
        mView.onClear();
        mModel.onClear();
    }
}
