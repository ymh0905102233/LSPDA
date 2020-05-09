package com.xx.chinetek.cywms.YS;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.adapter.wms.Receiption.ReceiptScanDetailAdapter;
import com.xx.chinetek.base.BaseActivity;
import com.xx.chinetek.base.BaseApplication;
import com.xx.chinetek.base.ToolBarTitle;
import com.xx.chinetek.cywms.R;
import com.xx.chinetek.cywms.Receiption.ReceiptionBillDetail;
import com.xx.chinetek.model.Base_Model;
import com.xx.chinetek.model.Material.BarCodeInfo;
import com.xx.chinetek.model.Receiption.ReceiptDetail_Model;
import com.xx.chinetek.model.Receiption.Receipt_Model;
import com.xx.chinetek.model.ReturnMsgModel;
import com.xx.chinetek.model.ReturnMsgModelList;
import com.xx.chinetek.model.URLModel;
import com.xx.chinetek.model.User.UerInfo;
import com.xx.chinetek.util.Network.NetworkError;
import com.xx.chinetek.util.Network.RequestHandler;
import com.xx.chinetek.util.dialog.MessageBox;
import com.xx.chinetek.util.function.ArithUtil;
import com.xx.chinetek.util.function.CommonUtil;
import com.xx.chinetek.util.function.DoubleClickCheck;
import com.xx.chinetek.util.function.GsonUtil;
import com.xx.chinetek.util.log.LogUtil;

import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.xx.chinetek.cywms.R.id.lsv_ReceiptScan;
import static com.xx.chinetek.util.dialog.ToastUtil.show;
import static com.xx.chinetek.util.function.GsonUtil.parseModelToJson;


@ContentView(R.layout.activity_receiption_scan)
public class YSScan2 extends BaseActivity {

    String TAG_GetTYSDetailListByHeaderIDADF = "ReceiptionScan_GetT_GetTYSDetailListByHeaderIDADF";
    String TAG_GetOutBarCodeForYS            = "ReceiptionScan_GetT_PalletDetailByBarCodeADF";
    String TAG_YSPost                        = "ReceiptionScan_SaveT_InStockDetailADF";
    private final int RESULT_Msg_GetTYSDetailListByHeaderIDADF = 101;
    private final int RESULT_Msg_GetOutBarCodeForYS            = 102;
    private final int RESULT_Msg_YSPost                        = 103;

    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case RESULT_Msg_GetTYSDetailListByHeaderIDADF:
                AnalysisGetT_YSDetailListJson((String) msg.obj);
                break;
            case RESULT_Msg_GetOutBarCodeForYS:
                AnalysisGetT_PalletDetailByNoADF((String) msg.obj);
                break;
            case RESULT_Msg_YSPost:
                AnalysisSaveT_InStockDetailADFJson((String) msg.obj);
                break;
            case NetworkError.NET_ERROR_CUSTOM:
                show("获取请求失败_____" + msg.obj);
                CommonUtil.setEditFocus(edtRecScanBarcode);
                break;
        }
    }


    Context context = YSScan2.this;
    @ViewInject(R.id.lsv_ReceiptScan)
    ListView lsvReceiptScan;
    @ViewInject(R.id.edt_RecScanBarcode)
    EditText edtRecScanBarcode;
    @ViewInject(R.id.txt_VoucherNo)
    TextView txtVoucherNo;
    @ViewInject(R.id.txt_ReceQTY)
    TextView txtInStockQty;
    @ViewInject(R.id.txt_CanReceQty)
    TextView txtRemainQty;
    @ViewInject(R.id.txt_ReceScanQty)
    TextView txtReceiveQty;
    @ViewInject(R.id.txt_ScanQty)
    TextView txtScanQty;
    @ViewInject(R.id.txt_Company)
    TextView txtCompany;
    @ViewInject(R.id.txt_Batch)
    TextView txtBatch;
    @ViewInject(R.id.txt_Status)
    TextView txtStatus;
    @ViewInject(R.id.txt_EDate)
    TextView txtEDate;
    @ViewInject(R.id.txt_MaterialName)
    TextView txtMaterialName;
    @ViewInject(R.id.txtAll)
    TextView txtAll1;
    @ViewInject(R.id.edt_area_no)
    EditText mAreaNo;
    @ViewInject(R.id.textView33)
    TextView mAreaName;

    ReceiptScanDetailAdapter       receiptScanDetailAdapter;
    ArrayList<ReceiptDetail_Model> receiptDetailModels = new ArrayList<>();
    BarCodeInfo                    mBarCodeInfo        = null;
    Receipt_Model                  receiptModel        = null;
    UUID                           mUuid               = null;

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.context = context;
        BaseApplication.toolBarTitle = new ToolBarTitle(getString(R.string.receiptscan_subtitle) + "-" + BaseApplication.userInfo.getWarehouseName(), true);
        x.view().inject(this);
        BaseApplication.isCloseActivity = false;
        setAreaBar(false);
    }

    @Override
    protected void initData() {
        super.initData();
        receiptModel = getIntent().getParcelableExtra("receiptModel");
        GetYSDetail(receiptModel);

    }


    /**
     * @desc: 条码扫描
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/5/8 20:25
     */
    @Event(value = R.id.edt_RecScanBarcode, type = View.OnKeyListener.class)
    private boolean edtRecScanBarcode(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            keyBoardCancle();
            String code = edtRecScanBarcode.getText().toString().trim();
            final Map<String, String> params = new HashMap<String, String>();
            params.put("BarCode", code);
            LogUtil.WriteLog(YSScan2.class, TAG_GetOutBarCodeForYS, code);
            RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetOutBarCodeForYS, getString(R.string.Msg_GetT_SerialNoByPalletADF), context, mHandler, RESULT_Msg_GetOutBarCodeForYS, null, URLModel.GetURL().GetOutBarCodeForYS, params, null);
        }
        return false;
    }


    @Event(value = lsv_ReceiptScan, type = AdapterView.OnItemClickListener.class)
    private boolean lsv_ReceiptScanItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (id >= 0) {
            ReceiptDetail_Model receiptDetailModel = receiptDetailModels.get(position);
            try {
                if (receiptDetailModel.getLstBarCode() != null && receiptDetailModel.getLstBarCode().size() != 0) {
                    Intent intent = new Intent(context, ReceiptionBillDetail.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("receiptDetailModel", receiptDetailModel);
                    intent.putExtras(bundle);
                    startActivityLeft(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_receiptbilldetail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            if (DoubleClickCheck.isFastDoubleClick(context)) {
                return false;
            }
            try {
                Boolean isFinishReceipt = true;
                if (isFinishReceipt) {
                    if (mUuid == null) {
                        mUuid = UUID.randomUUID();
                    }
                    //receiptDetailModels
//                    for (ReceiptDetail_Model receiptDetail : FirstreceiptDetailModels) {
//                        receiptDetail.setGUID(mUuid.toString());
//                    }
//                    boolean isMaterialRowScanFinish = true;
//                    for (ReceiptDetail_Model materialItem : FirstreceiptDetailModels) {
//                        if (materialItem != null) {
//                            if ((materialItem.getRemainQty() - materialItem.getScanQty()) != 0 || materialItem.getScanQty() == 0) {
//                                isMaterialRowScanFinish = false;
//                                break;
//                            }
//                        }
//                    }
//                    if (receiptDetailModels != null && receiptDetailModels.size() > 0) {
//                        if (receiptDetailModels.get(0).getVoucherType() != 43) {  //成品检验单可以部分提交
//                            if (isMaterialRowScanFinish == false) {
//                                MessageBox.Show(context, "物料没有全部扫描完成,不能提交");
//                                return false;
//                            }
//                        }
//
//                    }

                    UerInfo userInfo = null;
                    userInfo = BaseApplication.userInfo;
                    final Map<String, String> params = new HashMap<String, String>();
                    String ModelJson = GsonUtil.parseModelToJson(FirstreceiptDetailModels);
                    String UserJson = GsonUtil.parseModelToJson(userInfo);
                    params.put("UserJson", UserJson);
                    params.put("ModelJson", ModelJson);
                    LogUtil.WriteLog(YSScan2.class, TAG_YSPost, ModelJson);
                    RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_YSPost, getString(R.string.Msg_SaveT_InStockDetailADF), context, mHandler, RESULT_Msg_YSPost, null, URLModel.GetURL().YSPost, params, null);
                }
            } catch (Exception ex) {
                MessageBox.Show(context, ex.toString());
            }

        }
        return super.onOptionsItemSelected(item);
    }

    /*
    获取收货明细
     */
    void GetYSDetail(final Receipt_Model receiptModel) {

        txtVoucherNo.setText(receiptModel.getErpVoucherNo());
        final ReceiptDetail_Model receiptDetailModel = new ReceiptDetail_Model();
        receiptDetailModel.setHeaderID(receiptModel.getID());
        receiptDetailModel.setErpVoucherNo(receiptModel.getErpVoucherNo());
        receiptDetailModel.setVoucherType(receiptModel.getVoucherType());
        final Map<String, String> params = new HashMap<String, String>();
        params.put("ModelDetailJson", parseModelToJson(receiptDetailModel));
        String para = (new JSONObject(params)).toString();
        LogUtil.WriteLog(YSScan2.class, TAG_GetTYSDetailListByHeaderIDADF, para);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetTYSDetailListByHeaderIDADF, getString(R.string.Msg_GetT_YSDetailListByHeaderIDADF), context, mHandler, RESULT_Msg_GetTYSDetailListByHeaderIDADF, null, URLModel.GetURL().GetTYSDetailListByHeaderIDADF, params, null);


    }


    ArrayList<ReceiptDetail_Model> FirstreceiptDetailModels = new ArrayList<>();

    /**
     * @desc: 处理预留释放明细
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/5/7 10:22
     */
    void AnalysisGetT_YSDetailListJson(String result) {
        LogUtil.WriteLog(YSScan2.class, TAG_GetTYSDetailListByHeaderIDADF, result);
        try {
            ReturnMsgModelList<ReceiptDetail_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<ReceiptDetail_Model>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                FirstreceiptDetailModels = returnMsgModel.getModelJson();
                if (FirstreceiptDetailModels != null) {
                    receiptDetailModels.addAll(FirstreceiptDetailModels);
                }

                if (receiptDetailModels != null && receiptDetailModels.size() > 0) {

                    //自动确认扫描箱号
                    BindListVIew(receiptDetailModels);
                    if (mBarCodeInfo != null) {
                        isDel = false;
                        Bindbarcode(mBarCodeInfo);
                    }
                } else {
                    MessageBox.Show(context, returnMsgModel.getMessage());
                }
            } else {
                MessageBox.Show(context, returnMsgModel.getMessage());
            }
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
        }
        CommonUtil.setEditFocus(edtRecScanBarcode);
    }

    /**
     * @desc:  条码扫描返回结果
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/5/8 20:26
     */
    void AnalysisGetT_PalletDetailByNoADF(String result) {
//        LogUtil.WriteLog(YSScan2.class, TAG_GetOutBarCodeForYS, result);
//        try {
//            ReturnMsgModel<BarCodeInfo> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<BarCodeInfo>>() {
//            }.getType());
//            if (returnMsgModel.getHeaderStatus().equals("S")) {
//               mBarCodeInfo = returnMsgModel.getModelJson();
//                int barcodeCount = barCodeInfosT.size();
//                txtAll1.setText("个数：" + barcodeCount + "数量：" + SumQty);
//
//                isDel = false;
//
//                Bindbarcode(barCodeInfosT);
//            } else {
//                MessageBox.Show(context, returnMsgModel.getMessage());
//            }
//        } catch (Exception ex) {
//            MessageBox.Show(context, ex.toString());
//        }
//        CommonUtil.setEditFocus(edtRecScanBarcode);
    }

    /*
    提交收货
     */
    void AnalysisSaveT_InStockDetailADFJson(String result) {
        try {
            LogUtil.WriteLog(YSScan2.class, TAG_YSPost, result);
            final ReturnMsgModel<Base_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<Base_Model>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                mUuid = null;
                new AlertDialog.Builder(context).setTitle("提示").setCancelable(false).setIcon(android.R.drawable.ic_dialog_info).setMessage(returnMsgModel.getMessage())
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                closeActiviry();
                            }
                        }).show();
            } else {
                MessageBox.Show(context, returnMsgModel.getMessage());
            }
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
        }
    }


    void InitFrm(BarCodeInfo barCodeInfo) {
        if (barCodeInfo != null) {
            txtCompany.setText(barCodeInfo.getStrongHoldCode());
            txtBatch.setText(barCodeInfo.getMaterialNo());
            txtStatus.setText(barCodeInfo.getBatchNo());
            txtMaterialName.setText(barCodeInfo.getMaterialDesc());
            txtEDate.setText(CommonUtil.DateToString(barCodeInfo.getEDate()));
        }
        CommonUtil.setEditFocus(edtRecScanBarcode);
    }

    boolean isDel = false;

    void Bindbarcode(final BarCodeInfo barCodeInfo) {
//            try {
//
//                    if (barCodeInfo != null && receiptDetailModels != null) {
//                        String traceNo=barCodeInfo.getTracNo()!=null? barCodeInfo.getTracNo():"";
//
//                        for (int i=0;i<receiptDetailModels.size();i++){
//                            ReceiptDetail_Model model=receiptDetailModels.get(i);
//                            if (model!=null){
//                                String sTraceNo=model.getTracNo()!=null?model.getTracNo():"";
//                                if (model.getRemainQty()<0){}
//                            }
//
//                        }
////                        ReceiptDetail_Model receiptDetailModel = new ReceiptDetail_Model(barCodeInfo.getMaterialNo(),barCodeInfo.getBatchNo(),barCodeInfo.getInvoiceNo().trim());
//                        ReceiptDetail_Model receiptDetailModel = null;
//                        //ReceiptDetail_Model receiptDetailModel = new ReceiptDetail_Model(barCodeInfo.getMaterialNo(),barCodeInfo.getBatchNo());
//                        if (receiptDetailModels.get(0).getVoucherType() == 30) {
//                            receiptDetailModel = new ReceiptDetail_Model(barCodeInfo.getMaterialNo());
//                        } else {
//                            receiptDetailModel = new ReceiptDetail_Model(barCodeInfo.getMaterialNo(), barCodeInfo.getRowNo());
//
//                        }
//                        receiptDetailModel.setVoucherType(30);
//                        final int index = receiptDetailModels.indexOf(receiptDetailModel);
//                        if (index != -1) {
//
//                            if (receiptDetailModels.get(index).getLstBarCode() == null)
//                                receiptDetailModels.get(index).setLstBarCode(new ArrayList<BarCodeInfo>());
//                            final int barIndex = receiptDetailModels.get(index).getLstBarCode().indexOf(barCodeInfo);
//                            if (barIndex != -1) {
//                                if (isDel) {
//                                    RemoveBarcode(index, barIndex);
//                                } else {
//                                    new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("是否删除已扫描条码？")
//                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    // TODO 自动生成的方法
//                                                    //RemoveBarcode(index, barIndex);
//                                                    isDel = true;
//                                                    Bindbarcode(barCodeInfos);
//                                                }
//                                            }).setNegativeButton("取消", null).show();
//                                    break;
//                                }
//                            } else {
//                                if (!CheckBarcode(barCodeInfo, index))
//                                    break;
//                            }
//                            RefeshFrm(index);
//                        } else {
//                            MessageBox.Show(context, getString(R.string.Error_BarcodeNotInList) + "|" + barCodeInfo.getSerialNo() + "|" + barCodeInfo.getInvoiceNo());
//                            break;
//                        }
//                    }
//
//
//                InitFrm(barCodeInfos.get(0));
//            } catch (Exception ex) {
//                MessageBox.Show(context, ex.getMessage());
//                CommonUtil.setEditFocus(edtRecScanBarcode);
//            }


    }


    boolean CheckBarcode(BarCodeInfo barCodeInfo, int index) {
        boolean isChecked = false;
        if (receiptDetailModels.get(index).getRemainQty() == 0) {
            MessageBox.Show(context, getString(R.string.Error_ReceiveFinish));
            return false;
        }

        if (receiptDetailModels.get(index).getLstBarCode().size() != 0) {
//            if (!barCodeInfo.getBatchNo().equals(receiptDetailModels.get(index).getLstBarCode().get(0).getBatchNo())) {
//                MessageBox.Show(context, getString(R.string.Error_ReceivebatchError) + "|" + barCodeInfo.getSerialNo());
//                return false;
//            }
            if (!barCodeInfo.getSupPrdBatch().equals(receiptDetailModels.get(index).getLstBarCode().get(0).getSupPrdBatch())) {
                MessageBox.Show(context, getString(R.string.Error_ProductbatchError) + "|" + barCodeInfo.getSerialNo());
                return false;
            }
        }
        isChecked = Addbarcode(index, barCodeInfo);
        return isChecked;
    }


    boolean RemoveBarcode(final int index, final int barIndex) {
        float qty = ArithUtil.sub(receiptDetailModels.get(index).getScanQty(), receiptDetailModels.get(index).getLstBarCode().get(barIndex).getQty());
        //receiptDetailModels.get(index).getScanQty()-receiptDetailModels.get(index).getLstBarCode().get(barIndex).getQty();
        receiptDetailModels.get(index).getLstBarCode().remove(barIndex);
        receiptDetailModels.get(index).setScanQty(qty);
        return true;
    }

    boolean Addbarcode(int index, BarCodeInfo barCodeInfo) {
        float qty = ArithUtil.add(receiptDetailModels.get(index).getScanQty(), barCodeInfo.getQty());
        //receiptDetailModels.get(index).getScanQty()+barCodeInfo.getQty();

        if (qty <= receiptDetailModels.get(index).getRemainQty()) {
            receiptDetailModels.get(index).getLstBarCode().add(0, barCodeInfo);
            receiptDetailModels.get(index).setBatchNo(barCodeInfo.getBatchNo());
            receiptDetailModels.get(index).setScanQty(qty);
            return true;
        } else {
            MessageBox.Show(context, getString(R.string.Error_ReceiveOver));
        }
        return false;
    }

    void RefeshFrm(int index) {
        txtInStockQty.setText(receiptDetailModels.get(index).getInStockQty() + "");
        txtReceiveQty.setText(receiptDetailModels.get(index).getReceiveQty() + "");
        txtRemainQty.setText(receiptDetailModels.get(index).getRemainQty() + "");
        txtScanQty.setText(receiptDetailModels.get(index).getScanQty() + "");
        BindListVIew(receiptDetailModels);
    }

    private void BindListVIew(ArrayList<ReceiptDetail_Model> receiptDetailModels) {
        receiptScanDetailAdapter = new ReceiptScanDetailAdapter(context, "采购收货", receiptDetailModels);
        lsvReceiptScan.setAdapter(receiptScanDetailAdapter);
    }

    /**
     * @desc: 是否显示库位栏
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/5/4 11:00
     */
    private void setAreaBar(boolean visiable) {
        if (visiable) {
            if (mAreaNo.getVisibility() == View.GONE) {
                mAreaNo.setVisibility(View.VISIBLE);
            }
            if (mAreaName.getVisibility() == View.GONE) {
                mAreaName.setVisibility(View.VISIBLE);
            }
        } else {
            mAreaNo.setVisibility(View.GONE);
            mAreaName.setVisibility(View.GONE);
        }
    }
}
