package com.xx.chinetek.cywms.UpShelf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.xx.chinetek.adapter.wms.Upshelf.UpShelfScanDetailAdapter;
import com.xx.chinetek.base.BaseActivity;
import com.xx.chinetek.base.BaseApplication;
import com.xx.chinetek.base.ToolBarTitle;
import com.xx.chinetek.cywms.R;
import com.xx.chinetek.model.Base_Model;
import com.xx.chinetek.model.ReturnMsgModel;
import com.xx.chinetek.model.ReturnMsgModelList;
import com.xx.chinetek.model.URLModel;
import com.xx.chinetek.model.WMS.Stock.AreaInfo_Model;
import com.xx.chinetek.model.WMS.Stock.StockInfo_Model;
import com.xx.chinetek.model.WMS.UpShelf.InStockTaskDetailsInfo_Model;
import com.xx.chinetek.model.WMS.UpShelf.InStockTaskInfo_Model;
import com.xx.chinetek.util.Network.NetworkError;
import com.xx.chinetek.util.Network.RequestHandler;
import com.xx.chinetek.util.dialog.MessageBox;
import com.xx.chinetek.util.dialog.ToastUtil;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.xx.chinetek.cywms.R.id.edt_StockScan;
import static com.xx.chinetek.util.function.GsonUtil.parseModelToJson;

@ContentView(R.layout.activity_up_shelf_scan)
public class UpShelfScanActivity_2 extends BaseActivity {

    String TAG_GetT_InTaskDetailListByHeaderIDADF = "UpShelfScanActivity_GetT_InTaskDetailListByHeaderIDADF";
    String TAG_GetT_ScanInStockModelADF = "UpShelfScanActivity_GetT_ScanInStockModelADF";
    String TAG_GetAreaModelADF = "UpShelfScanActivity_GetAreaModelADF";
    String TAG_SaveT_InStockTaskDetailADF = "UpShelfBillChoice_SaveT_InStockTaskDetailADF";

    private final int RESULT_Msg_GetT_InTaskDetailListByHeaderIDADF = 101;
    private final int RESULT_Msg_GetT_ScanInStockModelADF = 102;
    private final int RESULT_Msg_SaveT_InStockTaskDetailADF = 103;
    private final int RESULT_Msg_GetAreaModelADF = 104;

    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case RESULT_Msg_GetT_InTaskDetailListByHeaderIDADF:
                AnalysisGetT_InTaskDetailListByHeaderIDADFJson((String) msg.obj);
                break;
            case RESULT_Msg_GetT_ScanInStockModelADF:
                AnalysisetT_PalletDetailByBarCodeJson((String) msg.obj);
                break;
            case RESULT_Msg_SaveT_InStockTaskDetailADF:
                AnalysisSaveT_InStockTaskDetailADFJson((String) msg.obj);
                break;
            case RESULT_Msg_GetAreaModelADF:
                AnalysisGetAreaModelADFJson((String) msg.obj);
                break;
            case NetworkError.NET_ERROR_CUSTOM:
                ToastUtil.show("获取请求失败_____" + msg.obj);
                CommonUtil.setEditFocus(edtUpShelfScanBarcode);
                break;
        }
    }

    Context context = UpShelfScanActivity_2.this;
    @ViewInject(R.id.lsv_UpShelfScan)
    ListView lsvUpShelfScan;
    @ViewInject(R.id.txt_VoucherNo)
    TextView txtVoucherNo;
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
    @ViewInject(R.id.txt_UpShelfNum)
    TextView txtUpShelfNum;
    @ViewInject(R.id.txt_UpShelfScanNum)
    TextView txtUpShelfScanNum;
    @ViewInject(R.id.edt_UpShelfScanBarcode)
    EditText edtUpShelfScanBarcode;
    @ViewInject(edt_StockScan)
    EditText edtStockScan;

    @ViewInject(R.id.edt_up_scanqty)
    EditText edtUpScanQty;
    @ViewInject(R.id.txtAll)
    TextView txtAll1;

    ArrayList<InStockTaskDetailsInfo_Model> inStockTaskDetailsInfoModels;
    InStockTaskInfo_Model inStockTaskInfoModel = null;
    ArrayList<StockInfo_Model> stockInfoModels = new ArrayList<StockInfo_Model>();
    AreaInfo_Model areaInfoModel = null;//扫描库位
    UpShelfScanDetailAdapter upShelfScanDetailAdapter;
    float barcodeQty = 0;//扫描条码的数量
    boolean isInStock = false;//录入数量是否已写入

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.context = context;
        BaseApplication.toolBarTitle = new ToolBarTitle(getString(R.string.UpShelfscan_subtitle) + "-" + BaseApplication.userInfo.getWarehouseName(), true);
        x.view().inject(this);
        BaseApplication.isCloseActivity = false;
    }

    @Override
    protected void initData() {
        super.initData();
        inStockTaskInfoModel = getIntent().getParcelableExtra("inStockTaskInfoModel");
        //  stockInfoModels=getIntent().getParcelableArrayListExtra("stockInfoModels");
        CommonUtil.setEditFocus(edtStockScan);
        GetInStockTaskDetail(inStockTaskInfoModel);

        txtCompany.setText("");
        txtBatch.setText("");
        txtEDate.setText("");
        txtStatus.setText("");
        txtMaterialName.setText("");
    }

//    @Event(R.id.btn_ShowStock)
//    private void btnShowStockOnclick(View view) {
//        if(referStocks!=null && referStocks.length!=0) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setTitle("推荐库位：");
//            builder.setItems(referStocks,null);
//            builder.show();
//        }
//    }

    @Event(value = R.id.edt_UpShelfScanBarcode, type = View.OnKeyListener.class)
    private boolean edtUpShelfScanBarcodeClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            String code = edtUpShelfScanBarcode.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                CommonUtil.setEditFocus(edtUpShelfScanBarcode);
                return true;
            }

            //   txtReferStock.setText(GetReferStock(inStockTaskDetailsInfoModels.get(index).getLstArea())); //推荐货位

//            String StockCode = edtStockScan.getText().toString().trim();
//            if (TextUtils.isEmpty(StockCode)) {
//
//            }

            if(areaInfoModel ==null){
                CommonUtil.setEditFocus(edtStockScan);
                return true;
            }
            edtStockScan.setText(areaInfoModel.getAreaNo());
            ScanBarcode(code, areaInfoModel.getAreaNo());

        }
        return false;
    }

    @Event(value = R.id.edt_StockScan, type = View.OnKeyListener.class)
    private boolean edtStockScanClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            String StockCode = edtStockScan.getText().toString().trim();
//            if (areaInfoModel != null && stockInfoModels != null && !areaInfoModel.getAreaNo().equals(StockCode)) {
//                MessageBox.Show(context, getString(R.string.Error_Upshelf_HasStcokNotSubmit));
//                CommonUtil.setEditFocus(edtStockScan);
//                return true;
//            }
            edtUpShelfScanBarcode.setText("");
            edtUpScanQty.setText("");
            stockInfoModels = null;
            if (TextUtils.isEmpty(StockCode)) {
                CommonUtil.setEditFocus(edtStockScan);
                return true;
            }
            final Map<String, String> params = new HashMap<String, String>();
            params.put("AreaNo", StockCode);
            params.put("UserJson", GsonUtil.parseModelToJson(BaseApplication.userInfo));
            LogUtil.WriteLog(UpShelfScanActivity_2.class, TAG_GetAreaModelADF, StockCode);
            RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetAreaModelADF, getString(R.string.Msg_GetAreaModelADF), context, mHandler, RESULT_Msg_GetAreaModelADF, null, URLModel.GetURL().GetAreaModelADF, params, null);
        }
        return false;
    }

    @Event(value = R.id.edt_up_scanqty, type = View.OnKeyListener.class)
    private boolean edtUpScanQtyClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {

            try {
                if (edtUpScanQty.getText().toString().equals("")) {
//                    MessageBox.Show(context, "请录入上架数量");
//                    CommonUtil.setEditFocus(edtUpScanQty);
                    return false;
                }
                String value = edtUpScanQty.getText().toString();
                Float scanQty = Float.valueOf(edtUpScanQty.getText().toString());
                if (scanQty <= 0) {
                    MessageBox.Show(context, "请输入正确的数量信息");
                    CommonUtil.setEditFocus(edtUpScanQty);
                    return true;
                }
                if (barcodeQty < scanQty) {
                    MessageBox.Show(context, "上架数量不能大于条码剩余数量");
                    CommonUtil.setEditFocus(edtUpScanQty);
                    return true;
                }
                if (stockInfoModels != null && stockInfoModels.size() != 0) {
                    for (StockInfo_Model stockInfoModel : stockInfoModels) {

                        if (!isInStock) {
                            if (!CheckBarcode(stockInfoModel, stockInfoModel.getQty())) {
                                isInStock = true;
                            }
//                            else {
//                                break;
//                            }
                        }
                    }
                }
//
//                //帅选已经扫描的行
////                         InsertStock();
//                        ArrayList<InStockTaskDetailsInfo_Model> listInStock = new ArrayList<InStockTaskDetailsInfo_Model>();
//                        for (InStockTaskDetailsInfo_Model stockTask :inStockTaskDetailsInfoModels) {
//                            if (stockTask.getLstStockInfo() != null && stockTask.getLstStockInfo().size() > 0) {
//                                listInStock.add(stockTask);
//                            }
//                        }
////
////                        ArrayList<StockInfo_Model> models = new ArrayList<>();
////                        for (int i=0;i<listInStock.size();i++){
////                            if(listInStock.get(i).getLstStockInfo()!=null){
////                                for (int j=0;j<listInStock.get(i).getLstStockInfo().size();j++){
////                                    if((listInStock.get(i).getLstStockInfo().get(j).getFserialno()!=null)&&(!listInStock.get(i).getLstStockInfo().get(j).getFserialno().equals(""))){
////
////                                        StockInfo_Model model = new StockInfo_Model();
////                                        String Fserialno=listInStock.get(i).getLstStockInfo().get(j).getFserialno();
////                                        model.setBarcode(Fserialno);
////                                        model.setSerialNo(Fserialno.substring(Fserialno.length()-14,Fserialno.length()));
////                                        model.setStrongHoldCode("ABH");
////                                        model.setStrongHoldName("ABH");
////                                        model.setCompanyCode("10");
////                                        model.setBarCodeType(5);
////                                        model.setQty(1f);
////                                        model.setAmountQty(1f);
////                                        model.setSupPrdBatch("");
////                                        model.setSupPrdDate(new Date());
////
////                                        Date date = new Date();
////                                        Calendar cal = Calendar.getInstance();
////                                        cal.setTime(date);//设置起时间
////                                        cal.add(Calendar.YEAR, 100);//增加100年
////                                        model.setEDate(cal.getTime());
////                                        model.setBatchNo("");
////                                        model.setAreaID(listInStock.get(i).getLstStockInfo().get(j).getAreaID());
////                                        model.setIsPalletOrBox(listInStock.get(i).getLstStockInfo().get(j).getIsPalletOrBox());
////                                        model.setPalletNo(listInStock.get(i).getLstStockInfo().get(j).getPalletNo());
////                                        if(models.indexOf(model)==-1){
////                                            models.add(model);
////                                        }
////                                    }else{
////                                        models.add(listInStock.get(i).getLstStockInfo().get(j));
////                                    }
////                                }
////                            }
////                            listInStock.get(i).setLstStockInfo(new ArrayList<StockInfo_Model>());
////                        }
////
////                        if(models==null||models.size()==0){
////                            return false;
////                        }
//
//                        //混箱提交处理
////                        listInStock.get(0).setLstStockInfo(models);
////一个个提交
//                        final Map<String, String> params = new HashMap<String, String>();
//                        String ModelJson = GsonUtil.parseModelToJson(listInStock);
//                        params.put("UserJson", GsonUtil.parseModelToJson(BaseApplication.userInfo));
//                        params.put("ModelJson", ModelJson);
////                        LogUtil.WriteLog(UpShelfScanActivity.class, TAG_SaveT_InStockTaskDetailADF, ModelJson);
////                        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SaveT_InStockTaskDetailADF, getString(R.string.Msg_SaveT_InStockTaskDetailADF), context, mHandler, RESULT_Msg_SaveT_InStockTaskDetailADF, null, URLModel.GetURL().SaveT_InStockTaskDetailADF, params, null);
////

                    //  InitFrm(stockInfoModels.get(0));
                    // BindListVIew(inStockTaskDetailsInfoModels);
                    // CommonUtil.setEditFocus(edtUpShelfScanBarcode);
                    CommonUtil.setEditFocus(edtUpScanQty);

            } catch (Exception e) {
                MessageBox.Show(context, "异常" + e.toString());
                CommonUtil.setEditFocus(edtUpScanQty);
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_receiptbilldetail, menu);
        menu.findItem(R.id.action_filter).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            if (DoubleClickCheck.isFastDoubleClick(context)) {
                return false;
            }
            if (areaInfoModel != null && stockInfoModels != null) {
                //筛选已经扫描的行
                ArrayList<InStockTaskDetailsInfo_Model> listInStock = new ArrayList<InStockTaskDetailsInfo_Model>();
                for (InStockTaskDetailsInfo_Model stockTask :inStockTaskDetailsInfoModels) {
                    if (stockTask.getLstStockInfo() != null && stockTask.getLstStockInfo().size() > 0) {
                        listInStock.add(stockTask);
                    }
                }
                final Map<String, String> params = new HashMap<String, String>();
                String ModelJson = GsonUtil.parseModelToJson(listInStock);
                params.put("UserJson", GsonUtil.parseModelToJson(BaseApplication.userInfo));
                params.put("ModelJson", ModelJson);
                LogUtil.WriteLog(UpShelfScanActivity_2.class, TAG_SaveT_InStockTaskDetailADF, ModelJson);
                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SaveT_InStockTaskDetailADF, getString(R.string.Msg_SaveT_InStockTaskDetailADF), context, mHandler, RESULT_Msg_SaveT_InStockTaskDetailADF, null, URLModel.GetURL().SaveT_InStockTaskDetailADF, params, null);

            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Event(value = R.id.lsv_UpShelfScan, type = AdapterView.OnItemClickListener.class)
    private void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        InStockTaskDetailsInfo_Model inStockTaskDetailsInfoModel = (InStockTaskDetailsInfo_Model) upShelfScanDetailAdapter.getItem(position);
        String[] referStocks = GetReferStockArray(inStockTaskDetailsInfoModel.getLstArea());
        if (referStocks != null && referStocks.length != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("推荐库位：");
            builder.setCancelable(true);
            builder.setItems(referStocks, null);
            builder.show();
        }
    }

    /*
   处理收货明细
    */
    void AnalysisGetT_InTaskDetailListByHeaderIDADFJson(String result) {
        try {
            LogUtil.WriteLog(UpShelfScanActivity_2.class, TAG_GetT_InTaskDetailListByHeaderIDADF, result);
            ReturnMsgModelList<InStockTaskDetailsInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<InStockTaskDetailsInfo_Model>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                inStockTaskDetailsInfoModels = returnMsgModel.getModelJson();
                boolean isUpFull = true;
                for (InStockTaskDetailsInfo_Model model :
                        inStockTaskDetailsInfoModels) {
//                    if (model.getRemainQty() > 0) {
                    if (model.getTaskQty1() > 0) {
                        isUpFull = false;
                        break;
                    }
                }
                if (isUpFull) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("提示");
                    builder.setMessage("当前任务已全部上架完成");
                    builder.setPositiveButton("返回重选任务", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            closeActiviry();
                        }
                    });
                    builder.setNeutralButton("留在本任务", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
                }
               if (inStockTaskDetailsInfoModels!=null){
                   Collections.sort(inStockTaskDetailsInfoModels, new InStockTaskDetailsInfo_Model());
                   BindListVIew(inStockTaskDetailsInfoModels);
               }else {

               }

//            //自动确认扫描箱号 删除，上架需要扫描库位
//            if(stockInfoModels!=null ) {
//                for (StockInfo_Model stockInfoModel : stockInfoModels) {
//                    if(!CheckBarcode(stockInfoModel)) break;
//                    InitFrm(stockInfoModel);
//                }
//                edtUpShelfScanBarcode.setEnabled(false);
//                CommonUtil.setEditFocus(edtStockScan);
//            }
            } else {
                if (returnMsgModel.getMessage().contains("获取上架任务表体数据列表为空")){
                    new AlertDialog.Builder(context).setTitle("提示").setCancelable(false).setIcon(android.R.drawable.ic_dialog_info).setMessage("当前任务已全部上架完成,返回重选任务")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO 自动生成的方法
                                    closeActiviry();
                                }
                            }).show();
                }else {
                    MessageBox.Show(context, returnMsgModel.getMessage());
                }


            }
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
        }
        CommonUtil.setEditFocus(edtStockScan);
    }


    void ScanBarcode(String code, String StockCode) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("SerialNo", code);
        params.put("ERPVoucherNo", inStockTaskInfoModel.getErpVoucherNo());
        params.put("TaskNo", inStockTaskInfoModel.getTaskNo());
        params.put("AreaNo", StockCode);
        params.put("WareHouseID", BaseApplication.userInfo.getWarehouseID() + "");
        LogUtil.WriteLog(UpShelfScanActivity_2.class, TAG_GetT_ScanInStockModelADF, code);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetT_ScanInStockModelADF, getString(R.string.Msg_GetT_SerialNoByPalletADF), context, mHandler, RESULT_Msg_GetT_ScanInStockModelADF, null, URLModel.GetURL().GetT_ScanInStockModelADF, params, null);
    }

    /*
    获取收货明细
     */
    void GetInStockTaskDetail(InStockTaskInfo_Model inStockTaskInfoModel) {
        if (inStockTaskInfoModel != null) {
            txtVoucherNo.setText(inStockTaskInfoModel.getErpVoucherNo());
            InStockTaskDetailsInfo_Model inStockTaskDetailsInfoModel = new InStockTaskDetailsInfo_Model();
            inStockTaskDetailsInfoModel.setHeaderID(inStockTaskInfoModel.getID());
            inStockTaskDetailsInfoModel.setTaskNo(inStockTaskInfoModel.getTaskNo());
            inStockTaskDetailsInfoModel.setErpVoucherNo(inStockTaskInfoModel.getErpVoucherNo());
            inStockTaskDetailsInfoModel.setVoucherType(inStockTaskInfoModel.getVoucherType());
            final Map<String, String> params = new HashMap<String, String>();
            params.put("ModelDetailJson", parseModelToJson(inStockTaskDetailsInfoModel));
            String para = (new JSONObject(params)).toString();
            LogUtil.WriteLog(UpShelfScanActivity_2.class, TAG_GetT_InTaskDetailListByHeaderIDADF, para);
            RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetT_InTaskDetailListByHeaderIDADF, getString(R.string.Msg_GetT_InTaskDetailListByHeaderIDADF), context, mHandler, RESULT_Msg_GetT_InTaskDetailListByHeaderIDADF, null, URLModel.GetURL().GetT_InTaskDetailListByHeaderIDADF, params, null);
        }
    }

    /*
   扫描条码
    */
    void AnalysisetT_PalletDetailByBarCodeJson(String result) {
        LogUtil.WriteLog(UpShelfScanActivity_2.class, TAG_GetT_ScanInStockModelADF, result);
        ReturnMsgModelList<StockInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<StockInfo_Model>>() {
        }.getType());
        if (returnMsgModel.getHeaderStatus().equals("S")) {
//            stockInfoModels = returnMsgModel.getModelJson();
            stockInfoModels = returnMsgModel.getModelJson();;
//            ArrayList<StockInfo_Model> stockInfoModelsT=returnMsgModel.getModelJson();
            //ymh混箱处理
            //float SumQty=0f;//计算数量和总数
//            for(int i=0;i<stockInfoModelsT.size();i++){
//                if (stockInfoModelsT.get(i).getLstHBarCode()!=null&&stockInfoModelsT.get(i).getLstHBarCode().size()>0){
//                    for(int j=0;j<stockInfoModelsT.get(i).getLstHBarCode().size();j++){
//                        SumQty=SumQty+stockInfoModelsT.get(i).getLstHBarCode().get(j).getQty();
//                        stockInfoModelsT.get(i).getLstHBarCode().get(j).setAreaID(stockInfoModelsT.get(i).getAreaID());
//                        stockInfoModels.add(stockInfoModelsT.get(i).getLstHBarCode().get(j));
//                    }
//                }else{
//                    SumQty=SumQty+stockInfoModelsT.get(i).getQty();
//                    stockInfoModels.add(stockInfoModelsT.get(i));
//                }
//            }
            //混箱处理
            //计算数量和总数
//            int barcodeCount=stockInfoModelsT.size();
//            txtAll1.setText("个数："+barcodeCount+"数量："+SumQty);

            if (stockInfoModels != null && stockInfoModels.size() != 0) {
                barcodeQty = stockInfoModels.get(0).getQty();
                edtUpScanQty.setText(stockInfoModels.get(0).getQty().toString());
                txtCompany.setText(stockInfoModels.get(0).getMaterialNo());
                txtBatch.setText(stockInfoModels.get(0).getBatchNo());
                txtEDate.setText(CommonUtil.DateToString(stockInfoModels.get(0).getEDate()));
                txtMaterialName.setText(stockInfoModels.get(0).getMaterialDesc());
                if (areaInfoModel.getHouseProp().equals("2")) {
                    CommonUtil.setEditFocus(edtUpScanQty);
                } else {
//                   private boolean edtUpScanQtyClick(View v, int keyCode, KeyEvent event) {
//                       if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
                    KeyEvent key = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER);
                    //KeyEvent.changeAction(key, KeyEvent.ACTION_UP);
                    edtUpScanQtyClick(null, KeyEvent.KEYCODE_ENTER, key);
                }

                Collections.sort(inStockTaskDetailsInfoModels, new InStockTaskDetailsInfo_Model());
                BindListVIew(inStockTaskDetailsInfoModels);
                CommonUtil.setEditFocus(edtUpShelfScanBarcode);
            } else {
                MessageBox.Show(context, "未获取到条码信息");
                CommonUtil.setEditFocus(edtUpShelfScanBarcode);
            }
        } else {
            MessageBox.Show(context, returnMsgModel.getMessage());
            CommonUtil.setEditFocus(edtUpShelfScanBarcode);
        }
    }


    /*
    扫描库位
     */
    void AnalysisGetAreaModelADFJson(String result) {
        try {
            LogUtil.WriteLog(UpShelfScanActivity_2.class, TAG_GetAreaModelADF, result);
            ReturnMsgModel<AreaInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<AreaInfo_Model>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                areaInfoModel = returnMsgModel.getModelJson();
//                if (!TextUtils.isEmpty(edtUpShelfScanBarcode.getText().toString().trim())) {
//                    String code = edtUpShelfScanBarcode.getText().toString().trim();
//                    String StockCode = edtStockScan.getText().toString().trim();
//                    ScanBarcode(code, StockCode);
//                }
                edtUpShelfScanBarcode.setText("");
                edtUpScanQty.setText("");
                stockInfoModels = null;
                CommonUtil.setEditFocus(edtUpShelfScanBarcode);
            } else {
                MessageBox.Show(context, returnMsgModel.getMessage());
                CommonUtil.setEditFocus(edtStockScan);
            }
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
            CommonUtil.setEditFocus(edtStockScan);
        }
    }


    /*
   提交收货
    */
    void AnalysisSaveT_InStockTaskDetailADFJson(String result) {
        try {
            LogUtil.WriteLog(UpShelfScanActivity_2.class, TAG_SaveT_InStockTaskDetailADF, result);
            ReturnMsgModel<Base_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<Base_Model>>() {
            }.getType());
            if(returnMsgModel.getHeaderStatus().equals("S")){
                new AlertDialog.Builder(context).setTitle("提示").setCancelable(false).setIcon(android.R.drawable.ic_dialog_info).setMessage(returnMsgModel.getMessage())
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO 自动生成的方法
                                ClearFrm();
                                GetInStockTaskDetail(inStockTaskInfoModel);
                            }
                        }).show();


            }else{
                MessageBox.Show(context, returnMsgModel.getMessage());
            }

//            if (returnMsgModel.getHeaderStatus().equals("S")) {
//                ClearFrm();
////                Boolean isFinish = true;
////                for (InStockTaskDetailsInfo_Model inStockTaskDetail : inStockTaskDetailsInfoModels) {
////                    if (inStockTaskDetail.getScanQty().compareTo(inStockTaskDetail.getRemainQty()) != 0) {
////                        isFinish = false;
////                        break;
////                    }
////                }
////                if (isFinish) {
////                    closeActiviry();
////                } else {
//
//                // }
//            } else {
//                ClearFrm();
//            }
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());

        }
    }

    void InitFrm(StockInfo_Model stockInfoModel) {
        try {
            if (stockInfoModel != null) {
                txtCompany.setText(stockInfoModel.getStrongHoldName());
                txtBatch.setText(stockInfoModel.getBatchNo());
                txtStatus.setText("");
                txtMaterialName.setText(stockInfoModel.getMaterialDesc());
                txtEDate.setText(CommonUtil.DateToString(stockInfoModel.getEDate()));

            }
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
            CommonUtil.setEditFocus(edtUpShelfScanBarcode);
        }
    }


    boolean CheckBarcode(StockInfo_Model StockInfo_Model, float upQty) {
        if (StockInfo_Model != null && inStockTaskDetailsInfoModels != null) {

            InStockTaskDetailsInfo_Model inStockTaskDetailsInfoModel = new InStockTaskDetailsInfo_Model(StockInfo_Model.getMaterialNo(),upQty);
            int index = -1;
            try {
                index = inStockTaskDetailsInfoModels.indexOf(inStockTaskDetailsInfoModel);
            } catch (Exception ex) {
                String value = ex.toString();
            }
            try{
                if (index != -1) {
                    if (areaInfoModel != null) {
                        inStockTaskDetailsInfoModels.get(index).setAreaID(areaInfoModel.getID());
                        inStockTaskDetailsInfoModels.get(index).setHouseID(areaInfoModel.getHouseID());
                        inStockTaskDetailsInfoModels.get(index).setWarehouseID(areaInfoModel.getWarehouseID());
                        inStockTaskDetailsInfoModels.get(index).setWareHouseNo(areaInfoModel.getWarehouseNo());
                        inStockTaskDetailsInfoModels.get(index).setToErpAreaNo(areaInfoModel.getAreaNo());
                        inStockTaskDetailsInfoModels.get(index).setAreaNo(areaInfoModel.getAreaNo());
                        inStockTaskDetailsInfoModels.get(index).setToErpWarehouse(areaInfoModel.getWarehouseNo());
                    }
                    if (inStockTaskDetailsInfoModels.get(index).getLstStockInfo() == null)
                        inStockTaskDetailsInfoModels.get(index).setLstStockInfo(new ArrayList<StockInfo_Model>());
                    if (!inStockTaskDetailsInfoModels.get(index).getLstStockInfo().contains(StockInfo_Model)) {
                        Float qty = ArithUtil.add(inStockTaskDetailsInfoModels.get(index).getScanQty(), upQty);
//                        if (qty <= inStockTaskDetailsInfoModels.get(index).getRemainQty()) {
                        if (qty <= inStockTaskDetailsInfoModels.get(index).getTaskQty1()) {
                            if (inStockTaskDetailsInfoModels.get(index).getFromErpWarehouse().equals(inStockTaskDetailsInfoModels.get(index).getToErpWarehouse())) {//不同仓库需要触发调拨业务员
//                                inStockTaskDetailsInfoModels.get(index).setVoucherType(999);//不生成调拨
                            } else {
//                                inStockTaskDetailsInfoModels.get(index).setVoucherType(9996);//生成调拨单   9996
                                inStockTaskDetailsInfoModels.get(index).setErpVoucherType("DB6");
                                inStockTaskDetailsInfoModels.get(index).setFromErpAreaNo("");
                                inStockTaskDetailsInfoModels.get(index).setToErpAreaNo("");
                                inStockTaskDetailsInfoModels.get(index).setFromBatchNo(StockInfo_Model.getBatchNo());
                            }

                            inStockTaskDetailsInfoModels.get(index).setScanQty(qty);
                            txtUpShelfNum.setText(inStockTaskDetailsInfoModels.get(index).getTaskQty1() + "");
                            txtUpShelfScanNum.setText(inStockTaskDetailsInfoModels.get(index).getScanQty() + "");
                            // edtUpShelfScanBarcode.setText(StockInfo_Model.getBarcode()+"");
                            //StockInfo_Model.setAreaNo(edtStockScan.getText().toString().trim());
                            inStockTaskDetailsInfoModels.get(index).getLstStockInfo().add(0, StockInfo_Model);
                        } else {
                            MessageBox.Show(context, getString(R.string.Error_UpshelfQtyBiger));
                            return false;
                        }
                    } else {
                        MessageBox.Show(context, getString(R.string.Error_BarcodeScaned) + "|" + StockInfo_Model.getSerialNo());
                        return false;
                    }
                } else {
                    MessageBox.Show(context, getString(R.string.Error_BarcodeNotInList) + "|" + StockInfo_Model.getSerialNo());
                    CommonUtil.setEditFocus(edtUpShelfScanBarcode);
                    return true;
                }
            }catch(Exception ex){
                MessageBox.Show(context, ex.toString());
                return true;
            }

        }
        return true;
    }

//    void InsertStock() {
//        for (StockInfo_Model StockInfo_Model : stockInfoModels) {
//            if (StockInfo_Model != null && inStockTaskDetailsInfoModels != null) {
//                InStockTaskDetailsInfo_Model inStockTaskDetailsInfoModel = new InStockTaskDetailsInfo_Model(StockInfo_Model.getMaterialNo());
//                int index = inStockTaskDetailsInfoModels.indexOf(inStockTaskDetailsInfoModel);
//                if (index != -1) {
//                    if (areaInfoModel != null) {
//                        inStockTaskDetailsInfoModels.get(index).setAreaID(areaInfoModel.getID());
//                        inStockTaskDetailsInfoModels.get(index).setHouseID(areaInfoModel.getHouseID());
//                        inStockTaskDetailsInfoModels.get(index).setWarehouseID(areaInfoModel.getWarehouseID());
//                        inStockTaskDetailsInfoModels.get(index).setToErpAreaNo(areaInfoModel.getAreaNo());
//                        inStockTaskDetailsInfoModels.get(index).setToErpWarehouse(areaInfoModel.getWarehouseNo());
//                    }
//                }
//            }
//        }
//    }


    private void BindListVIew(ArrayList<InStockTaskDetailsInfo_Model> inStockTaskDetailsInfoModels) {
        upShelfScanDetailAdapter = new UpShelfScanDetailAdapter(context, inStockTaskDetailsInfoModels);
        lsvUpShelfScan.setAdapter(upShelfScanDetailAdapter);
    }


    void ClearFrm() {

        stockInfoModels = new ArrayList<>();
        areaInfoModel = null;
        inStockTaskDetailsInfoModels =new ArrayList<InStockTaskDetailsInfo_Model>();
        edtStockScan.setText("");
        edtUpShelfScanBarcode.setText("");
        // txtUpShelfScanNum.setText("");
        // txtUpShelfScanNum.setText("");

        edtUpScanQty.setText("");
        barcodeQty = 0;
        isInStock = false;
        CommonUtil.setEditFocus(edtStockScan);
    }

    String[] GetReferStockArray(ArrayList<AreaInfo_Model> areaInfoModels) {
        String[] referStocks = new String[areaInfoModels.size()];
        if (areaInfoModels != null) {
            int i = 0;
            for (AreaInfo_Model areaInfoModel : areaInfoModels) {
                referStocks[i++] = areaInfoModel.getAreaNo();
            }
        }
        return referStocks;
    }


}
