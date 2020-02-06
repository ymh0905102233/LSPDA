package com.xx.chinetek.cywms.InnerMove;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.adapter.wms.InnerMove.InnerMoveAdapter;
import com.xx.chinetek.base.BaseActivity;
import com.xx.chinetek.base.BaseApplication;
import com.xx.chinetek.base.ToolBarTitle;
import com.xx.chinetek.cywms.R;
import com.xx.chinetek.cywms.UpShelf.UpShelfScanActivity;
import com.xx.chinetek.model.CheckNumRefMaterial;
import com.xx.chinetek.model.Material.BarCodeInfo;
import com.xx.chinetek.model.ReturnMsgModel;
import com.xx.chinetek.model.ReturnMsgModelList;
import com.xx.chinetek.model.URLModel;
import com.xx.chinetek.model.WMS.Stock.AreaInfo_Model;
import com.xx.chinetek.model.WMS.Stock.StockInfo_Model;
import com.xx.chinetek.util.Network.NetworkError;
import com.xx.chinetek.util.Network.RequestHandler;
import com.xx.chinetek.util.dialog.MessageBox;
import com.xx.chinetek.util.dialog.ToastUtil;
import com.xx.chinetek.util.function.ArithUtil;
import com.xx.chinetek.util.function.CommonUtil;
import com.xx.chinetek.util.function.DoubleClickCheck;
import com.xx.chinetek.util.function.GsonUtil;
import com.xx.chinetek.util.log.LogUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ContentView(R.layout.activity_inner_move_scan)
public class InnerMoveScan extends BaseActivity {

    String TAG_GetStockModelADF = "InnerMoveScan_GetStockModelADF";
    String TAG_GetAreaModelByMoveStockADF = "UpShelfScanActivity_GetAreaModelADF";
    String TAG_SaveT_StockADF = "MoveScanActivity_SaveT_StockADF";
    String TAG_GetAreaNOModelByMoveStockADF = "UpShelfScanActivity_GetAreaNOModelADF";

    private final int RESULT_Msg_GetStockModelADF = 101;
    private final int RESULT_GetAreaModelByMoveStockADF = 102;
    private final int RESULT_GetAreaNOModelByMoveStockADF = 104;
    private final int RESULT_SaveT_StockADF = 103;

    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case RESULT_Msg_GetStockModelADF:
                AnalysisGetStockModelADFJson((String) msg.obj);
                break;
            case RESULT_GetAreaModelByMoveStockADF:
                AnalysisGetAreaModelByMoveStockADFJson((String) msg.obj);
                break;
            case RESULT_SaveT_StockADF:
                AnalysisSaveT_StockADFJson((String) msg.obj);
                break;
            case RESULT_GetAreaNOModelByMoveStockADF:
                AnalysisGetInAreaModelByMoveStockADFJson((String) msg.obj);
                break;
            case NetworkError.NET_ERROR_CUSTOM:
                ToastUtil.show("获取请求失败_____" + msg.obj);
                break;
        }
    }

    Context context = InnerMoveScan.this;
    @ViewInject(R.id.lsv_InnerMoveDetail)
    ListView lsvInnerMoveDetail;

    @ViewInject(R.id.edt_movescan_inarea)
    EditText edtMoveInArea;
    @ViewInject(R.id.edt_movescan_outarea)
    EditText edtMoveOutArea;
    @ViewInject(R.id.edt_MoveScanBarcode)
    EditText edtMoveScanBarcode;
    @ViewInject(R.id.edt_movescan_qty)
    EditText edtMoveScanQty;

    @ViewInject(R.id.cb_movescan_inlock)
    CheckBox cbMoveInLock;
    @ViewInject(R.id.cb_movescan_outlock)
    CheckBox cbMoveOutLock;
    @ViewInject(R.id.cb_movescan_box)
    CheckBox cbMoveBox;
    @ViewInject(R.id.cb_movescan_area)
    CheckBox cbMoveArea;

    @ViewInject(R.id.txt_Company)
    TextView txtCompany;
    @ViewInject(R.id.txt_Stock)
    TextView txtStock;
    @ViewInject(R.id.txt_Batch)
    TextView txtBatch;
    @ViewInject(R.id.txt_Status)
    TextView txtStatus;
    @ViewInject(R.id.txt_MaterialName)
    TextView txtMaterialName;
    @ViewInject(R.id.txt_EDate)
    TextView txtEDate;


    List<StockInfo_Model> stockInfoModels;
    AreaInfo_Model OutAreaInfoModel = null;//扫描移出库位
    AreaInfo_Model InAreaInfoModel = null;//扫描移入库位
    InnerMoveAdapter innerMoveAdapter;
    ArrayList<StockInfo_Model> currentStockInfo;
    ArrayList<StockInfo_Model> ShowStock = new ArrayList<>();
    StockInfo_Model moveStockInfo = new StockInfo_Model();
    int FunctionType = 0;

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.context = context;
        BaseApplication.toolBarTitle = new ToolBarTitle(getString(R.string.InnerMove_subtitle) + "-" + BaseApplication.userInfo.getWarehouseName(), true);
        x.view().inject(this);
        BaseApplication.isCloseActivity = false;
    }

    @Override
    protected void initData() {
        super.initData();
        stockInfoModels = new ArrayList<>();
        FunctionType = getIntent().getIntExtra("FunctionType", 0);
        txtStatus.setText("");
        CommonUtil.setEditFocus(edtMoveOutArea);
    }

    @Event(value = R.id.edt_movescan_outarea, type = View.OnKeyListener.class)
    private boolean edtMoveOutArea(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            keyBoardCancle();
            String areaNO = edtMoveOutArea.getText().toString().trim();
            if (!TextUtils.isEmpty(areaNO)) {
                final Map<String, String> params = new HashMap<String, String>();
                params.put("areaNO", areaNO);
                String UserJson = GsonUtil.parseModelToJson(BaseApplication.userInfo);
                params.put("UserJson", UserJson);
                LogUtil.WriteLog(InnerMoveScan.class, TAG_GetAreaModelByMoveStockADF, areaNO + "|" + UserJson);
                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetAreaModelByMoveStockADF, getString(R.string.Msg_GetAreaModelADF), context, mHandler, RESULT_GetAreaModelByMoveStockADF, null, URLModel.GetURL().GetT_AreaInfoADF, params, null);
            }

        }
        return false;
    }

    @Event(value = R.id.edt_movescan_qty, type = View.OnKeyListener.class)
    private boolean edtMoveScanQty(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            keyBoardCancle();
            if (moveStockInfo == null) {
                MessageBox.Show(context, "请先扫描移库条码信息");
                CommonUtil.setEditFocus(edtMoveScanBarcode);
                return true;
            }
            if (TextUtils.isEmpty(edtMoveScanQty.getText().toString().trim())) {
                MessageBox.Show(context, "请输入数量");
                CommonUtil.setEditFocus(edtMoveScanQty);
                return true;
            }
            try {
                float scanqty = Float.valueOf(edtMoveScanQty.getText().toString().trim());
                if (scanqty <= 0 || scanqty > moveStockInfo.getQty()) {
                    MessageBox.Show(context, "请输入正确的数量");
                    CommonUtil.setEditFocus(edtMoveScanQty);
                    return true;
                }
                moveStockInfo.setAmountQty(scanqty);
                if (cbMoveInLock.isChecked() && InAreaInfoModel != null) {
                    saveMoveBarcode();//过账
                } else {
                    CommonUtil.setEditFocus(edtMoveInArea);
                    return true;
                }


            } catch (Exception e) {
                e.printStackTrace();
                MessageBox.Show(context, "数量异常" + e.toString());
                CommonUtil.setEditFocus(edtMoveScanQty);
            }

        }
        return false;
    }

    @Event(value = R.id.edt_movescan_inarea, type = View.OnKeyListener.class)
    private boolean edtMoveInArea(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            keyBoardCancle();
            String areaNO = edtMoveInArea.getText().toString().trim();
            if (!TextUtils.isEmpty(areaNO)) {
                final Map<String, String> params = new HashMap<String, String>();
                params.put("areaNO", areaNO);
                LogUtil.WriteLog(InnerMoveScan.class, TAG_GetAreaNOModelByMoveStockADF, areaNO);
                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetAreaNOModelByMoveStockADF, getString(R.string.Msg_GetAreaModelADF), context, mHandler, RESULT_GetAreaNOModelByMoveStockADF, null, URLModel.GetURL().GetT_AreaNOInfoADF, params, null);
            }
        }
        return false;
    }

    @Event(value = R.id.cb_movescan_area, type = View.OnClickListener.class)
    private void cbMoveAreaClick(View view) {

        if (cbMoveArea.isChecked()) {
            cbMoveBox.setChecked(false);
            cbMoveInLock.setChecked(false);
            cbMoveOutLock.setChecked(false);
        }
    }

    @Event(value = R.id.lsv_InnerMoveDetail, type = AdapterView.OnItemClickListener.class)
    private boolean lsv_InnerMoveDetailItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (id >= 0) {
            StockInfo_Model stockInfoModel = ShowStock.get(position);
            ArrayList<BarCodeInfo> barCodeInfos = new ArrayList<>();
            if (stockInfoModel != null && stockInfoModels != null) {
                for (StockInfo_Model temp : stockInfoModels) {
                    if (stockInfoModel.getMaterialNo().equals(temp.getMaterialNo())) {
                        BarCodeInfo barCodeInfo = new BarCodeInfo();
                        barCodeInfo.setSerialNo(temp.getSerialNo());
                        barCodeInfos.add(barCodeInfo);
                    }
                }
                if (barCodeInfos.size() != 0) {
                    Intent intent = new Intent(context, InnerMoveDetail.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("barCodeInfos", barCodeInfos);
                    intent.putExtras(bundle);
                    startActivityLeft(intent);
                }
            }

        }
        return true;
    }


    @Event(value = R.id.edt_MoveScanBarcode, type = View.OnKeyListener.class)
    private boolean edtMoveScanBarcode(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            keyBoardCancle();
            String barcode = edtMoveScanBarcode.getText().toString().trim();
            if (!TextUtils.isEmpty(barcode)) {
//                String isEDate=BaseApplication.userInfo.getReceiveWareHouseNo().toUpperCase().trim().equals("AD09") ||
//                        BaseApplication.userInfo.getReceiveWareHouseNo().toUpperCase().trim().equals("AD04")?"1":"2";2

                StockInfo_Model stock = null;
                try {
                    if (OutAreaInfoModel == null) {
                        MessageBox.Show(context, "请先确认移出库位");
                        CommonUtil.setEditFocus(edtMoveOutArea);
                        return true;
                    }
                    stock = new StockInfo_Model();
                    if (barcode.split("@").length > 2) {
                        stock.setBarcode(barcode);
                        stock.setSerialNo(barcode.split("@")[barcode.split("@").length - 1]);
                    } else {
                        stock.setEAN(barcode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stock.setAreaNo(OutAreaInfoModel.getAreaNo());
                stock.setAreaID(OutAreaInfoModel.getID());
                final Map<String, String> params = new HashMap<String, String>();
                String ModelJson = GsonUtil.parseModelToJson(stock);
                params.put("ModelJson", ModelJson);
                String UserJson = GsonUtil.parseModelToJson(BaseApplication.userInfo);
                params.put("UserJson", UserJson);
                LogUtil.WriteLog(InnerMoveScan.class, TAG_GetStockModelADF, barcode);
                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetStockModelADF, getString(R.string.Msg_GetT_SerialNoByPalletADF), context, mHandler, RESULT_Msg_GetStockModelADF, null, URLModel.GetURL().GetT_StockInfoADF, params, null);
            }
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_receiptbilldetail, menu);
        menu.findItem(R.id.action_filter).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            if (DoubleClickCheck.isFastDoubleClick(context)) {
                return false;
            }
            for (int i = 0; i < stockInfoModels.size(); i++) {
                stockInfoModels.get(i).setVoucherType(9996);
                //是AD09
                // 仓库，ERPVoucherType设置为zf1
                if (BaseApplication.userInfo.getReceiveWareHouseNo().toUpperCase().trim().equals("AD09") ||
                        BaseApplication.userInfo.getReceiveWareHouseNo().toUpperCase().trim().equals("AD04")) {
                    stockInfoModels.get(i).setERPVoucherType("ZF1");
                }
            }
            final Map<String, String> params = new HashMap<String, String>();
            String ModelJson = GsonUtil.parseModelToJson(stockInfoModels);
            params.put("UserJson", GsonUtil.parseModelToJson(BaseApplication.userInfo));
            params.put("ModelJson", ModelJson);
            LogUtil.WriteLog(InnerMoveScan.class, TAG_SaveT_StockADF, ModelJson);
            if (FunctionType == 0)
                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SaveT_StockADF, getString(R.string.Msg_SaveT_StockADF), context, mHandler, RESULT_SaveT_StockADF, null, URLModel.isWMS ? URLModel.GetURL().SaveT_StockADF : URLModel.GetURL().SaveT_StockADF_Product, params, null);
            else
                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SaveT_StockADF, getString(R.string.Msg_SaveT_StockADF), context, mHandler, RESULT_SaveT_StockADF, null, URLModel.GetURL().SaveMoveStockToOutADF, params, null);

        }
        return super.onOptionsItemSelected(item);
    }


    /*
   扫描条码
    */
    void AnalysisGetStockModelADFJson(String result) {
        try {
            LogUtil.WriteLog(InnerMoveScan.class, TAG_GetStockModelADF, result);
            ReturnMsgModelList<StockInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<StockInfo_Model>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                currentStockInfo = returnMsgModel.getModelJson();
                if (currentStockInfo != null && currentStockInfo.size() > 0) {

                    if (currentStockInfo.size() > 1) {
                        ArrayList<String> listString = new ArrayList<>();
                        for (StockInfo_Model model :
                                currentStockInfo) {
                            listString.add(model.getStrongHoldCode() + "  " + model.getBatchNo() + "  " + model.getStrEDate());//.getEDate().toString()
                        }
                        String[] items = (String[]) listString.toArray(new String[listString.size()]);
                        AlertDialog alertDialog3 = new AlertDialog.Builder(this)
                                .setTitle("选择库存")
                                .setItems(items, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        setMaterailPackInfo(currentStockInfo.get(i));
                                        return;
                                    }
                                }).create();
                        alertDialog3.show();
                    } else {
                        setMaterailPackInfo(currentStockInfo.get(0));
                    }

                }

            } else {
                MessageBox.Show(context, returnMsgModel.getMessage());
                CommonUtil.setEditFocus(edtMoveScanBarcode);
            }
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
            CommonUtil.setEditFocus(edtMoveScanBarcode);
        }
    }

    void setMaterailPackInfo(StockInfo_Model stockModle) {
        moveStockInfo = stockModle;
        txtCompany.setText(stockModle.getStrongHoldName());
        txtStatus.setText(stockModle.getStrStatus());
        txtEDate.setText(CommonUtil.DateToString(stockModle.getEDate()));
        txtBatch.setText(stockModle.getBatchNo());
        txtMaterialName.setText(stockModle.getMaterialDesc());
        edtMoveScanQty.setText(String.valueOf(stockModle.getQty()));
        if (cbMoveBox.isChecked()) {
            if (cbMoveInLock.isChecked()) {
                if (InAreaInfoModel != null) {
                    saveMoveBarcode();
                    return;
                }
            }
            CommonUtil.setEditFocus(edtMoveInArea);
        } else {
            CommonUtil.setEditFocus(edtMoveScanQty);
        }
    }

    /*
   扫描移出库位
    */
    void AnalysisGetAreaModelByMoveStockADFJson(String result) {
        try {
            LogUtil.WriteLog(UpShelfScanActivity.class, TAG_GetAreaModelByMoveStockADF, result);
            ReturnMsgModel<AreaInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<AreaInfo_Model>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                OutAreaInfoModel = returnMsgModel.getModelJson();
                if (cbMoveArea.isChecked()) {
                    CommonUtil.setEditFocus(edtMoveInArea);
                } else {
                    CommonUtil.setEditFocus(edtMoveScanBarcode);
                }


            } else {
                MessageBox.Show(context, returnMsgModel.getMessage());
                CommonUtil.setEditFocus(edtMoveOutArea);
            }
        } catch (Exception ex) {
            MessageBox.Show(context, ex.toString());
            CommonUtil.setEditFocus(edtMoveOutArea);
        }

    }

    /*
扫描移入库位
 */
    void AnalysisGetInAreaModelByMoveStockADFJson(String result) {
        try {
            LogUtil.WriteLog(UpShelfScanActivity.class, TAG_GetAreaModelByMoveStockADF, result);
            ReturnMsgModel<AreaInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<AreaInfo_Model>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                InAreaInfoModel = returnMsgModel.getModelJson();
                saveMoveBarcode();
            } else {
                MessageBox.Show(context, returnMsgModel.getMessage());
                CommonUtil.setEditFocus(edtMoveInArea);
            }
        } catch (Exception ex) {
            MessageBox.Show(context, ex.toString());
            CommonUtil.setEditFocus(edtMoveInArea);
        }

    }

    /*移库提交后处理*/
    void AnalysisSaveT_StockADFJson(String result) {
        try {
            LogUtil.WriteLog(InnerMoveScan.class, TAG_SaveT_StockADF, result);
            ReturnMsgModel<List<StockInfo_Model>> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<List<StockInfo_Model>>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
//                if (!InAreaInfoModel.getHouseProp().equals("2")) {
//                    List<StockInfo_Model> listStock = returnMsgModel.getModelJson();
//                    if (listStock != null && listStock.size() > 0) {
//
//                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
//                        builder.setTitle("提示");
//                        builder.setMessage("移库过程中产生了新标签是否打印？");
//                        builder.setPositiveButton("现在打印", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        });
//                        builder.setNeutralButton("不打印", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        });
//                        builder.show();
//                    }
//                }
                intiFrm();
            } else {
                MessageBox.Show(context, returnMsgModel.getMessage());
                CommonUtil.setEditFocus(edtMoveInArea);
            }
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
            CommonUtil.setEditFocus(edtMoveInArea);
        }

    }


    void saveMoveBarcode() {
        if (InAreaInfoModel == null) {
            MessageBox.Show(context, "请确定移入库位信息");
            CommonUtil.setEditFocus(edtMoveInArea);
            return;
        }

        if (OutAreaInfoModel == null) {
            MessageBox.Show(context, "请确定移出库位信息");
            CommonUtil.setEditFocus(edtMoveInArea);
            return;
        }

        if(InAreaInfoModel.getAreaNo().equals(OutAreaInfoModel.getAreaNo())){
            MessageBox.Show(context, "移出库位与移入库位一致，请重新扫描");
            CommonUtil.setEditFocus(edtMoveInArea);
            return;
        }
        if (!cbMoveArea.isChecked()) {
            if (moveStockInfo == null) {
                MessageBox.Show(context, "请扫描条码信息");
                CommonUtil.setEditFocus(edtMoveScanBarcode);
                return;
            }

            if (moveStockInfo.getAmountQty() == 0) {
                if (cbMoveBox.isChecked()) {
                    moveStockInfo.setAmountQty(moveStockInfo.getQty());
                } else {
                    MessageBox.Show(context, "请确认移库数量");
                    CommonUtil.setEditFocus(edtMoveScanBarcode);
                    return;
                }
            }

            if (!moveStockInfo.getWarehouseNo().equals(InAreaInfoModel.getWarehouseNo())) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                builder.setTitle("提示");
                builder.setMessage("库存当前仓库" + moveStockInfo.getWarehouseNo() + " 与移入仓库" + InAreaInfoModel.getWarehouseNo() + " 不符，确定要移库并生成调拨业务?");
                builder.setPositiveButton("返回重扫库位", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        InAreaInfoModel = null;
                        CommonUtil.setEditFocus(edtMoveInArea);
                    }
                });
                builder.setNeutralButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        submit();
                    }
                });
                builder.show();
            } else {
                submit();
            }
        }else{
            if(OutAreaInfoModel.getWarehouseID()!=InAreaInfoModel.getWarehouseID()){
                MessageBox.Show(context,"不同仓库不允许整库转移");
                return;
            }
            if(OutAreaInfoModel.getHouseProp().equals("2")||InAreaInfoModel.equals("2")){
                MessageBox.Show(context,"零箱区不允许整库转移");
                return;
            }
            if(!OutAreaInfoModel.getHouseProp().equals(InAreaInfoModel.getHouseProp())){
                MessageBox.Show(context,"转入转出库位库区属性不一致");
                return;
            }

            moveStockInfo =new StockInfo_Model();
            moveStockInfo.setAreaID(OutAreaInfoModel.getID());
            moveStockInfo.setAreaNo(OutAreaInfoModel.getAreaNo());
            moveStockInfo.setFromAreaNo(OutAreaInfoModel.getAreaNo());
            moveStockInfo.setHouseID(OutAreaInfoModel.getHouseID());
            moveStockInfo.setWarehouseNo(OutAreaInfoModel.getWarehouseNo());
            moveStockInfo.setWareHouseID(OutAreaInfoModel.getWarehouseID());
            submit();
        }


    }

    void submit() {
        final Map<String, String> params = new HashMap<String, String>();
        String ModelJson = GsonUtil.parseModelToJson(moveStockInfo);
        params.put("StockJson", ModelJson);
        String UserJson = GsonUtil.parseModelToJson(BaseApplication.userInfo);
        params.put("UserJson", UserJson);
        String AreaJson = GsonUtil.parseModelToJson(InAreaInfoModel);
        params.put("AreaJson", AreaJson);
        LogUtil.WriteLog(InnerMoveScan.class, TAG_SaveT_StockADF, ModelJson + "|" + InAreaInfoModel.getAreaNo());
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SaveT_StockADF, getString(R.string.Msg_SaveT_StockADF), context, mHandler, RESULT_SaveT_StockADF, null, URLModel.GetURL().Save_MoveInfoADF, params, null);

    }

    private void BindListVIew(List<StockInfo_Model> stockInfo_models) {
        innerMoveAdapter = new InnerMoveAdapter(context, stockInfo_models);
        lsvInnerMoveDetail.setAdapter(innerMoveAdapter);
    }


    void intiFrm() {
        txtCompany.setText("");
        txtBatch.setText("");
        txtEDate.setText("");
        txtStatus.setText("");
        txtStock.setText("");
        txtMaterialName.setText("");


        if (ShowStock == null) {
            ShowStock = new ArrayList<>();
        }
        moveStockInfo.setToErpAreaNo(InAreaInfoModel.getAreaNo());
        moveStockInfo.setToErpWarehouse(InAreaInfoModel.getWarehouseNo());
        ShowStock.add(moveStockInfo);
        BindListVIew(ShowStock);

        if (!cbMoveInLock.isChecked()) {
            InAreaInfoModel = null;
            edtMoveInArea.setText("");
        }

        currentStockInfo = null;
        moveStockInfo = null;
        edtMoveScanBarcode.setText("");
        edtMoveScanQty.setText("");
        if (!cbMoveOutLock.isChecked()) {
            OutAreaInfoModel = null;
            edtMoveOutArea.setText("");
            CommonUtil.setEditFocus(edtMoveOutArea);
        } else {
            CommonUtil.setEditFocus(edtMoveScanBarcode);
        }
    }


}
