package com.xx.chinetek.cywms.Receiption;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
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
import com.xx.chinetek.cywms.UpShelf.UpShelfScanActivity;
import com.xx.chinetek.model.Base_Model;
import com.xx.chinetek.model.Material.BarCodeInfo;
import com.xx.chinetek.model.Receiption.ReceiptDetail_Model;
import com.xx.chinetek.model.Receiption.Receipt_Model;
import com.xx.chinetek.model.ReturnMsgModel;
import com.xx.chinetek.model.ReturnMsgModelList;
import com.xx.chinetek.model.URLModel;
import com.xx.chinetek.model.User.UerInfo;
import com.xx.chinetek.model.WMS.Stock.AreaInfo_Model;
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
public class ReceiptionScan extends BaseActivity {

    String TAG_GetT_InStockDetailListByHeaderIDADF = "ReceiptionScan_GetT_InStockDetailListByHeaderIDADF";
    String TAG_GetT_PalletDetailByBarCodeADF       = "ReceiptionScan_GetT_PalletDetailByBarCodeADF";
    String TAG_SaveT_InStockDetailADF              = "ReceiptionScan_SaveT_InStockDetailADF";
    String TAG_GetAreaModelADF                     = "ReceiptionScan_GetAreaModelADF";
    private final int RESULT_Msg_GetT_InStockDetailListByHeaderIDADF = 101;
    private final int RESULT_Msg_GetT_PalletDetailByBarCode          = 102;
    private final int RESULT_Msg_SaveT_InStockDetailADF              = 103;
    private final int RESULT_Msg_GetAreaModelADF                     = 104;

    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case RESULT_Msg_GetT_InStockDetailListByHeaderIDADF:
                AnalysisGetT_InStockDetailListJson((String) msg.obj);
                break;
            case RESULT_Msg_GetT_PalletDetailByBarCode:
                AnalysisGetT_PalletDetailByNoADF((String) msg.obj);
                break;
            case RESULT_Msg_SaveT_InStockDetailADF:
                AnalysisSaveT_InStockDetailADFJson((String) msg.obj);
                break;
            case RESULT_Msg_GetAreaModelADF:
                AnalysisGetAreaModelADFJson((String) msg.obj);
                break;
            case NetworkError.NET_ERROR_CUSTOM:
                show("获取请求失败_____" + msg.obj);
                CommonUtil.setEditFocus(edtRecScanBarcode);
                break;
        }
    }


    Context context = ReceiptionScan.this;
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
    ArrayList<ReceiptDetail_Model> receiptDetailModels      = new ArrayList<>();
    ArrayList<BarCodeInfo>         barCodeInfos             = null;
    Receipt_Model                  receiptModel             = null;
    UUID                           mUuid                    = null;
    //  boolean isDel=false;//删除已扫条码
    AreaInfo_Model                 mAreaInfoModel           = null;//扫描库位
    Float                          mSumReaminQty            = 0f; //当前拣货物料剩余拣货数量合计
    ArrayList<ReceiptDetail_Model> mSameLineInStockTaskDetailsInfoModels; //相同行物料集合
    int                            currentPickMaterialIndex = -1;

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.context = context;
        BaseApplication.toolBarTitle = new ToolBarTitle(getString(R.string.receiptscan_subtitle) + "-" + BaseApplication.userInfo.getWarehouseName(), true);
        x.view().inject(this);
        BaseApplication.isCloseActivity = false;


    }

    @Override
    protected void initData() {
        super.initData();
        receiptModel = getIntent().getParcelableExtra("receiptModel");
        this.barCodeInfos = getIntent().getParcelableArrayListExtra("barCodeInfo");
        if (BaseApplication.userInfo.getISVWAREHOUSE() == 0 && receiptModel.getVoucherType() != 39) { //不上架的条码并且不是到货单的
            setAreaBar(true);
            CommonUtil.setEditFocus(mAreaNo);
        } else {
            setAreaBar(false);
            CommonUtil.setEditFocus(edtRecScanBarcode);
        }

        GetReceiptDetail(receiptModel);

    }


    @Event(value = R.id.edt_area_no, type = View.OnKeyListener.class)
    private boolean edtStockScanClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            String StockCode = mAreaNo.getText().toString().trim();
//            if (areaInfoModel != null && stockInfoModels != null && !areaInfoModel.getAreaNo().equals(StockCode)) {
//                MessageBox.Show(context, getString(R.string.Error_Upshelf_HasStcokNotSubmit));
//                CommonUtil.setEditFocus(edtStockScan);
//                return true;
//            }
            edtRecScanBarcode.setText("");

            if (TextUtils.isEmpty(StockCode)) {
                CommonUtil.setEditFocus(mAreaNo);
                return true;
            }
            final Map<String, String> params = new HashMap<String, String>();
            params.put("AreaNo", StockCode);
            params.put("UserJson", GsonUtil.parseModelToJson(BaseApplication.userInfo));
            LogUtil.WriteLog(UpShelfScanActivity.class, TAG_GetAreaModelADF, StockCode);
            RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetAreaModelADF, getString(R.string.Msg_GetAreaModelADF), context, mHandler, RESULT_Msg_GetAreaModelADF, null, URLModel.GetURL().GetAreaModelADF, params, null);
        }
        return false;
    }


    @Event(value = R.id.edt_RecScanBarcode, type = View.OnKeyListener.class)
    private boolean edtRecScanBarcode(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {

            if (BaseApplication.userInfo.getISVWAREHOUSE() == 0 && receiptModel.getVoucherType() != 39) { //不上架的条码并且不是到货单的
                if (mAreaInfoModel == null) {
                    MessageBox.Show(context, "库位信息不能为空!请扫描库位");
                    return false;
                }
            }
                keyBoardCancle();
                String code = edtRecScanBarcode.getText().toString().trim();
                final Map<String, String> params = new HashMap<String, String>();
                params.put("BarCode", code);
//            params.put("BarCode", "1@SHJC@13109100019@20200411@2@2004111149541989665601");
                params.put("UserJson", GsonUtil.parseModelToJson(BaseApplication.userInfo));
                LogUtil.WriteLog(ReceiptionScan.class, TAG_GetT_PalletDetailByBarCodeADF, code);
                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetT_PalletDetailByBarCodeADF, getString(R.string.Msg_GetT_SerialNoByPalletADF), context, mHandler, RESULT_Msg_GetT_PalletDetailByBarCode, null, URLModel.GetURL().GetT_PalletDetailByBarCodeADF, params, null);
            }
            return false;
        }

    /*
    扫描库位
     */
        void AnalysisGetAreaModelADFJson (String result){
            try {
                LogUtil.WriteLog(UpShelfScanActivity.class, TAG_GetAreaModelADF, result);
                ReturnMsgModel<AreaInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<AreaInfo_Model>>() {
                }.getType());
                if (returnMsgModel.getHeaderStatus().equals("S")) {
                    mAreaInfoModel = returnMsgModel.getModelJson();
                    if (mAreaInfoModel!=null){
                        mAreaNo.setText(mAreaInfoModel.getAreaNo());
                    }

//                stockInfoModels = null;
                    CommonUtil.setEditFocus(edtRecScanBarcode);
                } else {
                    MessageBox.Show(context, returnMsgModel.getMessage());
                    CommonUtil.setEditFocus(mAreaNo);
                }
            } catch (Exception ex) {
                MessageBox.Show(context, ex.getMessage());
                CommonUtil.setEditFocus(mAreaNo);
            }
        }

        @Event(value = lsv_ReceiptScan, type = AdapterView.OnItemClickListener.class)
        private boolean lsv_ReceiptScanItemClick (AdapterView < ? > parent, View view,int position,
        long id){
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
        public boolean onCreateOptionsMenu (Menu menu){
            getMenuInflater().inflate(R.menu.menu_receiptbilldetail, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            if (item.getItemId() == R.id.action_filter) {
                if (DoubleClickCheck.isFastDoubleClick(context)) {
                    return false;
                }
                try {
                    Boolean isFinishReceipt = true;
                    //非采购订单不能多次收货
//                if (receiptDetailModels != null && receiptDetailModels.get(0).getVoucherType() != 22) {
//                    for (ReceiptDetail_Model receiptDetail : receiptDetailModels) {
//                        receiptDetail.setGUID(uuid.toString());
//                        if (receiptDetail.getScanQty().compareTo(receiptDetail.getRemainQty()) != 0) {
//                            MessageBox.Show(context, getString(R.string.Error_CannotReceipt));
//                            isFinishReceipt = false;
//                            break;
//                        }
//                    }
//                }
                    if (isFinishReceipt) {
//                    ArrayList<BarCodeInfo> BarCodeInfos =new ArrayList<BarCodeInfo>();
                        //2020-5-19
//                    //混箱提交处理
//                    ArrayList<BarCodeInfo> models = new ArrayList<>();
//                    for (int i = 0; i < receiptDetailModels.size(); i++) {
//                        if (receiptDetailModels.get(i).getLstBarCode() != null) {
//                            for (int j = 0; j < receiptDetailModels.get(i).getLstBarCode().size(); j++) {
//                                if ((receiptDetailModels.get(i).getLstBarCode().get(j).getFserialno() != null) && (!receiptDetailModels.get(i).getLstBarCode().get(j).getFserialno().equals(""))) {
//
//                                    BarCodeInfo model = new BarCodeInfo();
//                                    String Fserialno = receiptDetailModels.get(i).getLstBarCode().get(j).getFserialno();
//                                    model.setBarCode(Fserialno);
//                                    model.setSerialNo(Fserialno.substring(Fserialno.length() - 14, Fserialno.length()));
//                                    model.setStrongHoldCode("ABH");
//                                    model.setStrongHoldName("ABH");
//                                    model.setCompanyCode("10");
//                                    model.setBarcodeType(5);
//                                    model.setQty(1f);
//                                    model.setSupPrdBatch("");
//                                    model.setSupPrdDate(new Date());
//                                    Date date = new Date();
//                                    Calendar cal = Calendar.getInstance();
//                                    cal.setTime(date);//设置起时间
//                                    cal.add(Calendar.YEAR, 100);//增加100年
//                                    model.setEDate(cal.getTime());
//                                    model.setBatchNo("");
////                                    model.setPalletNo(receiptDetailModels.get(i).getLstBarCode().get(j).getPalletNo());
//                                    if (models.indexOf(model) == -1) {
//                                        models.add(model);
////                                    receiptDetailModels.get(i).getLstBarCode().add(model);
//                                    }
////                                receiptDetailModels.get(i).getLstBarCode().remove(receiptDetailModels.get(i).getLstBarCode().get(j));
//                                } else {
//                                    models.add(receiptDetailModels.get(i).getLstBarCode().get(j));
//                                }
//                            }
//                        }
//
//                    }
//                    //混箱提交处理
//
//
//                    //自动还原行
//                    for (int i = 0; i < receiptDetailModels.size(); i++) {
////                        //条码
////                        if(receiptDetailModels.get(i).getLstBarCode()!=null){
////                            BarCodeInfos.addAll(receiptDetailModels.get(i).getLstBarCode());
////                        }
//
//                        if (receiptDetailModels.get(i).getScanQty() > 0) {
//                            float RemainQty = receiptDetailModels.get(i).getScanQty();
//                            for (int j = 0; j < FirstreceiptDetailModels.size(); j++) {
//                                if (j > 0)
//                                    FirstreceiptDetailModels.get(j).setLstBarCode(new ArrayList<BarCodeInfo>());
//                                if (RemainQty > 0 && receiptDetailModels.get(i).getMaterialNo().equals(FirstreceiptDetailModels.get(j).getMaterialNo()) && receiptDetailModels.get(i).getFromBatchNo().equals(FirstreceiptDetailModels.get(j).getFromBatchNo()) && receiptDetailModels.get(i).getInvoiceNo().equals(FirstreceiptDetailModels.get(j).getInvoiceNo())) {
//                                    if (RemainQty > FirstreceiptDetailModels.get(j).getRemainQty()) {
//                                        FirstreceiptDetailModels.get(j).setScanQty(FirstreceiptDetailModels.get(j).getRemainQty());
//                                        RemainQty = RemainQty - FirstreceiptDetailModels.get(j).getRemainQty();
//                                    } else {
//                                        FirstreceiptDetailModels.get(j).setScanQty(RemainQty);
//                                        RemainQty = 0f;
//                                    }
//                                }
//                            }
//                        }
//
//                    }
//
//                    for (int k = 0; k < FirstreceiptDetailModels.size(); k++) {
//                        if (FirstreceiptDetailModels.get(k).getScanQty() > 0) {
//                            FirstreceiptDetailModels.get(k).setLstBarCode(models);
//                            break;
//                        }
//                    }
                        //2020-5-19
                        if (mUuid == null) {
                            mUuid = UUID.randomUUID();
                        }
                        //receiptDetailModels
                        for (ReceiptDetail_Model receiptDetail : receiptDetailModels) {
                            receiptDetail.setGUID(mUuid.toString());
                        }
                        boolean isMaterialRowScanFinish = true;
                        for (ReceiptDetail_Model materialItem : receiptDetailModels) {
                            if (materialItem != null) {
                                if ((materialItem.getRemainQty() - materialItem.getScanQty()) != 0 || materialItem.getScanQty() == 0) {
                                    isMaterialRowScanFinish = false;
                                    break;
                                }
                            }
                        }
                        if (receiptDetailModels != null && receiptDetailModels.size() > 0) {
                            if (receiptDetailModels.get(0).getVoucherType() != 43) {  //成品检验单可以部分提交
                                if (isMaterialRowScanFinish == false) {
                                    MessageBox.Show(context, "物料没有全部扫描完成,不能提交");
                                    return false;
                                }
                            }

                        }

                        UerInfo userInfo = null;
                        if (BaseApplication.userInfo.getISVWAREHOUSE() == 0 && receiptModel.getVoucherType() != 39) { //不上架的条码并且不是到货单的
                            if (mAreaInfoModel == null) {
                                MessageBox.Show(context, "库位信息不能为空!请扫描库位");
                                return false;
                            }

                            userInfo = BaseApplication.userInfo.clone();
                            userInfo.setWarehouseID(mAreaInfoModel.getWarehouseID());
                            userInfo.setReceiveHouseID(mAreaInfoModel.getHouseID());
                            userInfo.setReceiveAreaID(mAreaInfoModel.getID());
                            userInfo.setReceiveWareHouseNo(mAreaInfoModel.getWarehouseNo());
                            userInfo.setReceiveHouseNo(mAreaInfoModel.getHouseNo());
                            userInfo.setReceiveAreaNo(mAreaInfoModel.getAreaNo());
                            userInfo.setReceiveWareHouseName(mAreaInfoModel.getWarehouseName());


                        } else {
                            userInfo = BaseApplication.userInfo;
                        }

                        final Map<String, String> params = new HashMap<String, String>();
                        String ModelJson = GsonUtil.parseModelToJson(FirstreceiptDetailModels);
                        String UserJson = GsonUtil.parseModelToJson(userInfo);
                        params.put("UserJson", UserJson);
                        params.put("ModelJson", ModelJson);
                        params.put("Guid", mUuid.toString());
                        LogUtil.WriteLog(ReceiptionScan.class, TAG_SaveT_InStockDetailADF, ModelJson);
                        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SaveT_InStockDetailADF, getString(R.string.Msg_SaveT_InStockDetailADF), context, mHandler, RESULT_Msg_SaveT_InStockDetailADF, null, URLModel.GetURL().SaveT_InStockDetailADF, params, null);
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
        void GetReceiptDetail ( final Receipt_Model receiptModel){
            txtVoucherNo.setText(receiptModel.getErpVoucherNo());
            final ReceiptDetail_Model receiptDetailModel = new ReceiptDetail_Model();
            receiptDetailModel.setHeaderID(receiptModel.getID());
            receiptDetailModel.setErpVoucherNo(receiptModel.getErpVoucherNo());
            receiptDetailModel.setVoucherType(receiptModel.getVoucherType());
            final Map<String, String> params = new HashMap<String, String>();
            params.put("ModelDetailJson", parseModelToJson(receiptDetailModel));
            String para = (new JSONObject(params)).toString();
            LogUtil.WriteLog(ReceiptionScan.class, TAG_GetT_InStockDetailListByHeaderIDADF, para);
            RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetT_InStockDetailListByHeaderIDADF, getString(R.string.Msg_GetT_InStockDetailListByHeaderIDADF), context, mHandler, RESULT_Msg_GetT_InStockDetailListByHeaderIDADF, null, URLModel.GetURL().GetT_InStockDetailListByHeaderIDADF, params, null);
        }


        ArrayList<ReceiptDetail_Model> FirstreceiptDetailModels = new ArrayList<>();

    /*
    处理收货明细
     */
        void AnalysisGetT_InStockDetailListJson (String result){
            LogUtil.WriteLog(ReceiptionScan.class, TAG_GetT_InStockDetailListByHeaderIDADF, result);
            try {
                ReturnMsgModelList<ReceiptDetail_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<ReceiptDetail_Model>>() {
                }.getType());
                if (returnMsgModel.getHeaderStatus().equals("S")) {
                    FirstreceiptDetailModels = returnMsgModel.getModelJson();
                    //receiptDetailModels = returnMsgModel.getModelJson();
//                //合并相同行
//                for (int i = 0; i < FirstreceiptDetailModels.size(); i++) {
//                    int index = receiptDetailModels.indexOf(FirstreceiptDetailModels.get(i));
//                    if (index < 0) {
//                        receiptDetailModels.add(FirstreceiptDetailModels.get(i).clone());
//                    } else {
//                        Float remainqty = receiptDetailModels.get(index).getRemainQty() + FirstreceiptDetailModels.get(i).getRemainQty();
//                        receiptDetailModels.get(index).setRemainQty(remainqty);
//                    }
//                }
                    receiptDetailModels = FirstreceiptDetailModels;

                    if (receiptDetailModels != null && receiptDetailModels.size() > 0) {
//                    if(!receiptDetailModels.get(0).getToErpWarehouse().equals(BaseApplication.userInfo.getWarehouseCode())){
//                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        builder.setTitle("提示");
//                        builder.setMessage("当前单据仓库与登陆仓库不符，是否继续进行收货?");
//                        builder.setPositiveButton("返回", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                closeActiviry();
//                            }
//                        });
//                        builder.show();
//                    }


                        //自动确认扫描箱号
                        BindListVIew(receiptDetailModels);
                        if (barCodeInfos != null) {
                            isDel = false;
                            Bindbarcode(barCodeInfos);
                        }
                    } else {
                        MessageBox.Show(context, returnMsgModel.getMessage());
                    }
                }
            } catch (Exception ex) {
                MessageBox.Show(context, ex.getMessage());
            }

            if (BaseApplication.userInfo.getISVWAREHOUSE() == 0 && receiptModel.getVoucherType() != 39) { //不上架的条码并且不是到货单的
                if (mAreaInfoModel == null) {
                    CommonUtil.setEditFocus(mAreaNo);
                }else {
                    CommonUtil.setEditFocus(edtRecScanBarcode);
                }
            }
//            CommonUtil.setEditFocus(edtRecScanBarcode);
        }

    /*
    扫描条码
     */
        void AnalysisGetT_PalletDetailByNoADF (String result){
            LogUtil.WriteLog(ReceiptionScan.class, TAG_GetT_PalletDetailByBarCodeADF, result);
            try {
                ReturnMsgModelList<BarCodeInfo> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<BarCodeInfo>>() {
                }.getType());
                if (returnMsgModel.getHeaderStatus().equals("S")) {
                    ArrayList<BarCodeInfo> barCodeInfos = returnMsgModel.getModelJson();

                    //ymh混箱处理
                    float SumQty = 0f;//计算数量和总数
                    int Rownum = barCodeInfos.size();
                    ArrayList<BarCodeInfo> barCodeInfosT = new ArrayList<>();
                    for (int i = 0; i < Rownum; i++) {
                        if (barCodeInfos.get(i).getLstBarCode() != null && barCodeInfos.get(i).getLstBarCode().size() > 0) {
                            for (int j = 0; j < barCodeInfos.get(i).getLstBarCode().size(); j++) {
                                SumQty = SumQty + barCodeInfos.get(i).getLstBarCode().get(j).getQty();
                                barCodeInfosT.add(barCodeInfos.get(i).getLstBarCode().get(j));
                            }
//                        barCodeInfos.remove(barCodeInfos.get(i));
                        } else {
                            SumQty = SumQty + barCodeInfos.get(i).getQty();
                            barCodeInfosT.add(barCodeInfos.get(i));
                        }
                    }
                    //混箱处理
                    //计算数量和总数
                    int barcodeCount = barCodeInfosT.size();
                    txtAll1.setText("个数：" + barcodeCount + "数量：" + SumQty);

                    isDel = false;

                    Bindbarcode(barCodeInfosT);
                } else {
                    MessageBox.Show(context, returnMsgModel.getMessage());
                }
            } catch (Exception ex) {
                MessageBox.Show(context, ex.toString());
            }
            CommonUtil.setEditFocus(edtRecScanBarcode);
        }

    /*
    提交收货
     */
        void AnalysisSaveT_InStockDetailADFJson (String result){
            try {
                LogUtil.WriteLog(ReceiptionScan.class, TAG_SaveT_InStockDetailADF, result);
                final ReturnMsgModel<Base_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<Base_Model>>() {
                }.getType());
                if (returnMsgModel.getHeaderStatus().equals("S")) {
                    mUuid = null;
                    new AlertDialog.Builder(context).setTitle("提示").setCancelable(false).setIcon(android.R.drawable.ic_dialog_info).setMessage(returnMsgModel.getMessage())
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO 自动生成的方法
//                                                if (receiptDetailModels.get(0).getVoucherType() == 22) {
//                                                    Intent intent = new Intent(context, QCMaterialChoice.class);
//                                                    String ErpVourcherNo = returnMsgModel.getMaterialDoc();
//                                                    intent.putExtra("ErpVourcherNo", ErpVourcherNo);
//                                                    startActivityLeft(intent);
//                                                }
                                    closeActiviry();
                                }
                            }).show();
                } else {
                    mUuid = null;
                    MessageBox.Show(context, returnMsgModel.getMessage());
                }
            } catch (Exception ex) {
                MessageBox.Show(context, ex.getMessage());
            }
        }


        void InitFrm (BarCodeInfo barCodeInfo){
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

        void Bindbarcode ( final ArrayList<BarCodeInfo> barCodeInfos){
            if (barCodeInfos != null && barCodeInfos.size() != 0) {
                try {
                    for (BarCodeInfo barCodeInfo : barCodeInfos) {
                        if (barCodeInfo != null && receiptDetailModels != null) {
                            if (CheckBarcode(barCodeInfo)) {
                                AddSameLineMaterialNum(barCodeInfo);
                                RefeshFrm(currentPickMaterialIndex);
                            } else {
                                break;
                            }
////                        ReceiptDetail_Model receiptDetailModel = new ReceiptDetail_Model(barCodeInfo.getMaterialNo(),barCodeInfo.getBatchNo(),barCodeInfo.getInvoiceNo().trim());
//                        ReceiptDetail_Model receiptDetailModel = null;
//                        //ReceiptDetail_Model receiptDetailModel = new ReceiptDetail_Model(barCodeInfo.getMaterialNo(),barCodeInfo.getBatchNo());
//                        receiptDetailModel = new ReceiptDetail_Model(barCodeInfo.getMaterialNo());
//                        receiptDetailModel.setTracNo(barCodeInfo.getTracNo() != null ? barCodeInfo.getTracNo() : "");
////                        receiptDetailModel.setVoucherType(30);
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
//                                if (CheckBarcode(barCodeInfo)){
//                                    AddSameLineMaterialNum(barCodeInfo);
//                                }else {
//                                    break;
//                                }
//
//                            }
//                            RefeshFrm(index);
//                        } else {
//                            MessageBox.Show(context, getString(R.string.Error_BarcodeNotInList) + "|" + barCodeInfo.getSerialNo() + "|" + barCodeInfo.getInvoiceNo());
//                            break;
//                        }
                        }

                    }
                    InitFrm(barCodeInfos.get(0));
                } catch (Exception ex) {
                    MessageBox.Show(context, ex.getMessage());
                    CommonUtil.setEditFocus(edtRecScanBarcode);
                }

            }
        }


//    boolean CheckBarcode(BarCodeInfo barCodeInfo, int index) {
//        boolean isChecked = false;
//        if (receiptDetailModels.get(index).getRemainQty() == 0) {
//            MessageBox.Show(context, getString(R.string.Error_ReceiveFinish));
//            return false;
//        }
//
//        if (receiptDetailModels.get(index).getLstBarCode().size() != 0) {
////            if (!barCodeInfo.getBatchNo().equals(receiptDetailModels.get(index).getLstBarCode().get(0).getBatchNo())) {
////                MessageBox.Show(context, getString(R.string.Error_ReceivebatchError) + "|" + barCodeInfo.getSerialNo());
////                return false;
////            }
//            if (!barCodeInfo.getSupPrdBatch().equals(receiptDetailModels.get(index).getLstBarCode().get(0).getSupPrdBatch())) {
//                MessageBox.Show(context, getString(R.string.Error_ProductbatchError) + "|" + barCodeInfo.getSerialNo());
//                return false;
//            }
//        }
//        isChecked = Addbarcode(index, barCodeInfo);
//        return isChecked;
//    }


        boolean RemoveBarcode ( final int index, final int barIndex){
            float qty = ArithUtil.sub(receiptDetailModels.get(index).getScanQty(), receiptDetailModels.get(index).getLstBarCode().get(barIndex).getQty());
            //receiptDetailModels.get(index).getScanQty()-receiptDetailModels.get(index).getLstBarCode().get(barIndex).getQty();
            receiptDetailModels.get(index).getLstBarCode().remove(barIndex);
            receiptDetailModels.get(index).setScanQty(qty);
            return true;
        }


        void RefeshFrm ( int index){
            txtInStockQty.setText(receiptDetailModels.get(index).getInStockQty() + "");
            txtReceiveQty.setText(receiptDetailModels.get(index).getReceiveQty() + "");
            txtRemainQty.setText(receiptDetailModels.get(index).getRemainQty() + "");
            txtScanQty.setText(receiptDetailModels.get(index).getScanQty() + "");
            BindListVIew(receiptDetailModels);
        }


        private void BindListVIew (ArrayList < ReceiptDetail_Model > receiptDetailModels) {
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
        private void setAreaBar ( boolean visiable){
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


        boolean Addbarcode ( int index, BarCodeInfo barCodeInfo){
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


        /**
         * @desc: 统计相同行物料剩余拣货数量
         * @param:
         * @return:
         * @author: Nietzsche
         * @time 2020/5/14 11:09
         */
        void FindSumQtyByMaterialNo (String MaterialNo, String traceNo){
            if (traceNo == null) traceNo = "";
            mSumReaminQty = 0.0f;
            mSameLineInStockTaskDetailsInfoModels = new ArrayList<>();
            for (int i = 0; i < receiptDetailModels.size(); i++) {
                ReceiptDetail_Model model = receiptDetailModels.get(i);
                if (model != null) {
                    String sMaterialNo = model.getMaterialNo() != null ? model.getMaterialNo() : "";
                    String sTraceNo = model.getTracNo() != null ? model.getTracNo() : "";
                    String sStrongHoldNo=model.getStrongHoldCode()!=null?model.getStrongHoldCode():"";
                    if (sStrongHoldNo.contains("SHJC")){
                        if (sMaterialNo.equals(MaterialNo) && sTraceNo.equals(traceNo)) {
                            mSameLineInStockTaskDetailsInfoModels.add(model);
                            mSumReaminQty = ArithUtil.add(mSumReaminQty, ArithUtil.sub(model.getRemainQty(), model.getScanQty()));
                        }
                    }else {
                        if (sMaterialNo.equals(MaterialNo)) {
                            mSameLineInStockTaskDetailsInfoModels.add(model);
                            mSumReaminQty = ArithUtil.add(mSumReaminQty, ArithUtil.sub(model.getRemainQty(), model.getScanQty()));
                        }
                    }

                }

            }
        }

        /**
         * @desc: 自动行分配  插入条码的时候总是插在第一行
         * @param:
         * @return:
         * @author:
         * @time 2020/5/14 11:11
         */
        void AddSameLineMaterialNum (BarCodeInfo barCodeInfo){
            try {
                boolean hasInsertBarcodeInfo = false;
                float ScanReaminQty = barCodeInfo.getQty();
                for (int i = 0; i < mSameLineInStockTaskDetailsInfoModels.size(); i++) {
                    if (ScanReaminQty == 0f) break;
                    ReceiptDetail_Model model = mSameLineInStockTaskDetailsInfoModels.get(i);
                    if (model != null) {
                        Float remainQty = ArithUtil.sub(model.getRemainQty(), model.getScanQty());
                        Float addQty = remainQty < ScanReaminQty ? remainQty : ScanReaminQty;
                        model.setScanQty(ArithUtil.add(model.getScanQty(), addQty));
                        ScanReaminQty = ArithUtil.sub(ScanReaminQty, addQty);
                        if (!hasInsertBarcodeInfo) {
                            model.getLstBarCode().add(0, barCodeInfo);
                            hasInsertBarcodeInfo = true;

                        }
                        currentPickMaterialIndex = i;
                    }
                }
            } catch (Exception e) {
                MessageBox.Show(context, "分配物料行出现预期之外的异常:" + e.getMessage());
            }

        }

        /**
         * @desc: 校验条码
         * @param:
         * @return:
         * @author: Nietzsche
         * @time 2020/5/14 11:41
         */
        boolean CheckBarcode (BarCodeInfo barCodeInfo){
            boolean isChecked = true;
            FindSumQtyByMaterialNo(barCodeInfo.getMaterialNo(), barCodeInfo.getTracNo());
            //校验条码的物料号和需求跟踪号是否在物料行中
            if (mSameLineInStockTaskDetailsInfoModels == null || mSameLineInStockTaskDetailsInfoModels.size() == 0) {
                MessageBox.Show(context, "校验条码失败：没有找到 物料编号:" + barCodeInfo.getMaterialNo() + "  ,需求跟踪号:[" + barCodeInfo.getTracNo() + "]的物料行");
                return false;
            }
            //校验条码对应的物料行是否已经拣货完成
            if (mSumReaminQty == 0) {
                MessageBox.Show(context, "校验条码失败： 物料编号:" + barCodeInfo.getMaterialNo() + "  ,需求跟踪号:[" + barCodeInfo.getTracNo() + "]的物料行已扫描完毕！");
                return false;
            }
            //校验条码是否超出物料行的待收货数量
            if (mSumReaminQty < barCodeInfo.getQty()) {
                MessageBox.Show(context, "校验条码失败： 物料编号:" + barCodeInfo.getMaterialNo() + "  ,需求跟踪号:[" + barCodeInfo.getTracNo() + "]的物料行的待收货数量为:" + mSumReaminQty + ",条码数量为:" + barCodeInfo.getQty());
                return false;
            }
            // 校验条码是否重复
            for (ReceiptDetail_Model model : mSameLineInStockTaskDetailsInfoModels) {
                if (model.getLstBarCode() == null)
                    model.setLstBarCode(new ArrayList<BarCodeInfo>());
                final int barIndex = model.getLstBarCode().indexOf(barCodeInfo);
                if (barIndex != -1) {
                    MessageBox.Show(context, "校验条码失败：序列号:" + barCodeInfo.getSerialNo() + "已扫描");
                    return false;
                }
            }
            return isChecked;
        }


    }
