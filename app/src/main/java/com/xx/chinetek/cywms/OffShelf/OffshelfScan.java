package com.xx.chinetek.cywms.OffShelf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.DeviceListActivity;
import com.xx.chinetek.adapter.wms.OffShelf.OffShelfScanDetailAdapter;
import com.xx.chinetek.base.BaseActivity;
import com.xx.chinetek.base.BaseApplication;
import com.xx.chinetek.base.ToolBarTitle;
import com.xx.chinetek.cywms.Qc.QCBillChoice;
import com.xx.chinetek.cywms.Query.Query;
import com.xx.chinetek.cywms.R;
import com.xx.chinetek.cywms.Truck.TruckLoad;
import com.xx.chinetek.model.Base_Model;
import com.xx.chinetek.model.Box.Boxing;
import com.xx.chinetek.model.ReturnMsgModel;
import com.xx.chinetek.model.ReturnMsgModelList;
import com.xx.chinetek.model.URLModel;
import com.xx.chinetek.model.WMS.OffShelf.OutStockTaskDetailsInfo_Model;
import com.xx.chinetek.model.WMS.OffShelf.OutStockTaskInfo_Model;
import com.xx.chinetek.model.WMS.Review.OutStockDetailInfo_Model;
import com.xx.chinetek.model.WMS.Review.OutStock_Model;
import com.xx.chinetek.model.WMS.Stock.StockInfo_Model;
import com.xx.chinetek.util.Network.NetworkError;
import com.xx.chinetek.util.Network.RequestHandler;
import com.xx.chinetek.util.SharePreferUtil;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xx.chinetek.cywms.R.id.tb_UnboxType;
import static com.xx.chinetek.util.function.GsonUtil.parseModelToJson;


@ContentView(R.layout.activity_offshelf_scan)
public class OffshelfScan extends BaseActivity {

    String TAG_GetT_OutTaskDetailListByHeaderIDADF="OffshelfScan_Single_GetT_OutTaskDetailListByHeaderIDADF";
    String TAG_GetStockModelADF="OffshelfScan_Single_GetStockModelADF";
    String TAG_SaveT_OutStockTaskDetailADF="OffshelfScan_Single_SaveT_OutStockTaskDetailADF";
    String TAG_SaveT_BarCodeToStockADF="OffshelfScan_Single_SaveT_BarCodeToStockADF";
    String TAG_SaveT_SingleErpvoucherADF="OffshelfScan_Single_OutStockReviewDetailADF";
    String TAG_SaveT_OutStockReviewPalletDetailADF="OffshelfScan_SaveT_OutStockReviewPalletDetailADF";
    String TAG_SaveT_OutStockReviewPalletDetailForLanyaADF="OffshelfScan_SaveT_OutStockReviewPalletDetailForLanyaADF";
    String TAG_SaveT_BarCodeToStockLanyaADF= "Boxing_SaveT_BarCodeToStockLanyaADF";

    String TAG_UnLockTaskOperUserADF = "OffShelfBillChoice_UnLockTaskOperUserADF";
    private final int RESULT_UnLockTaskOperUserADF = 109;
    private final int RESULT_SaveT_BarCodeToStockLanyaADF = 108;


    private final int RESULT_Msg_GetT_OutTaskDetailListByHeaderIDADF=101;
    private final int RESULT_Msg_GetStockModelADF=102;
    private final int RESULT_Msg_SaveT_OutStockTaskDetailADF=103;
    private final int RESULT_SaveT_BarCodeToStockADF = 104;
//    private final int RESULT_SaveT_SingleErpvoucherADF = 105;
//    private final int RESULT_Msg_SaveT_OutStockReviewPalletDetailADF=106;
//    private final int RESULT_Msg_SaveT_OutStockReviewPalletDetailForLanyaADF=107;


//    String TAG_GetAreano="TAG_GetAreano";
//    private final int RESULT_GetAreano = 110;

//    String TAG_GetCar="TAG_GetCar";
//    private final int RESULT_GetCar = 112;

    String TAG_GetStockModelFrojianADF="OffshelfScan_Single_GetStockModelFrojianADF";
    private final int RESULT_Msg_GetStockModelFrojianADF=110;


    String TAG_PrintListADF="OffshelfScan_PrintListADFADF";
    private final int RESULT_Msg_PrintListADF=111;

    String TAG_PrintListADF1="OffshelfScan_PrintListADFADF";
    private final int RESULT_Msg_PrintListADF1=112;


    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case RESULT_UnLockTaskOperUserADF:
                AnalysisGetT_UnLockTaskOperUserADFADFJson((String) msg.obj);
                break;
            case RESULT_Msg_GetT_OutTaskDetailListByHeaderIDADF:
                AnalysisGetT_OutTaskDetailListByHeaderIDADFJson((String) msg.obj);
                break;
            case RESULT_Msg_GetStockModelADF:
                AnalysisGetStockModelADFJson((String) msg.obj);
                break;
            case RESULT_Msg_SaveT_OutStockTaskDetailADF:
                AnalysisSaveT_OutStockTaskDetailADFJson((String) msg.obj);
                break;
            case RESULT_SaveT_BarCodeToStockADF:
                AnalysisSaveT_BarCodeToStockADF((String) msg.obj);
                break;
//            case  RESULT_SaveT_SingleErpvoucherADF:
//                AnalysisSaveT_SingleErpvoucherADF((String) msg.obj);
//                break;
//            case RESULT_Msg_SaveT_OutStockReviewPalletDetailADF:
//                AnalysisetT_SaveT_OutStockReviewPalletDetailADFJson((String) msg.obj);
//                break;
//            case RESULT_Msg_SaveT_OutStockReviewPalletDetailForLanyaADF:
//                AnalysisetT_SaveT_OutStockReviewPalletDetailForLanyaADF((String) msg.obj);
//                break;
//            case RESULT_SaveT_BarCodeToStockLanyaADF:
//                AnalysisSaveT_BarCodeToStockADF((String) msg.obj);
//                break;
//            case RESULT_GetAreano:
//                AnalysisGetAreanoADF((String) msg.obj);
//                break;

//            case RESULT_GetCar:
//                AnalysisGetCarADF((String) msg.obj);
//                break;
            case RESULT_Msg_PrintListADF:
                AnalysisPrintListADF((String) msg.obj);
                break;
            case RESULT_Msg_PrintListADF1:
                AnalysisPrintListADF1((String) msg.obj);
                break;

            case RESULT_Msg_GetStockModelFrojianADF:
                AnalysisGetStockModelFrojianADFJson((String) msg.obj);
                break;

            case NetworkError.NET_ERROR_CUSTOM:
                ToastUtil.show("获取请求失败_____"+ msg.obj);
                CommonUtil.setEditFocus(edtOffShelfScanbarcode);
                break;
        }
    }

    Context context=this;
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
    @ViewInject(tb_UnboxType)
    ToggleButton tbUnboxType;
//    @ViewInject(R.id.tb_PalletType)
//    ToggleButton tbPalletType;
    @ViewInject(R.id.tb_BoxType)
    ToggleButton tbBoxType;
    @ViewInject(R.id.edt_OffShelfScanbarcode)
    EditText edtOffShelfScanbarcode;
    @ViewInject(R.id.edt_Unboxing)
    EditText edtUnboxing;
    @ViewInject(R.id.edt_jian)
    EditText edtjian;
    @ViewInject(R.id.edt_car)
    EditText edtcar;
    @ViewInject(R.id.txt_SugestStock)
    TextView txtSugestStock;
    @ViewInject(R.id.txt_OffshelfNum)
    TextView txtOffshelfNum;
    @ViewInject(R.id.txt_currentPickNum)
    TextView txtcurrentPickNum;
    @ViewInject(R.id.txt_Unboxing)
    TextView txtUnboxing;
    @ViewInject(R.id.txt_jian)
    TextView txtjian;
    @ViewInject(R.id.btn_OutOfStock)
    TextView btnOutOfStock;
    @ViewInject(R.id.btn_BillDetail)
    TextView btnBillDetail;
    @ViewInject(R.id.btn_PrintBox)
    TextView btnPrintBox;
    @ViewInject(R.id.lsv_PickList)
    ListView lsvPickList;
    @ViewInject(R.id.edt_StockScan)
    EditText edtStockScan;
//    @ViewInject(R.id.txt_getbatch)
//    TextView txtgetbatch;
    @ViewInject(R.id.textView133)
    TextView textView133;
    @ViewInject(R.id.txterpvoucherno)
    TextView txterpvoucherno;
    @ViewInject(R.id.txtcustomername)
    TextView txtcustomername;


    ArrayList<OutStockTaskInfo_Model> outStockTaskInfoModels;
    ArrayList<OutStockTaskDetailsInfo_Model> outStockTaskDetailsInfoModels;
    ArrayList<OutStockTaskDetailsInfo_Model> SameLineoutStockTaskDetailsInfoModels; //相同行物料集合

    ArrayList<Boxing> BoxingModels=new ArrayList<>();//拆零清单
    ArrayList<StockInfo_Model> stockInfoModels;//扫描条码
    OffShelfScanDetailAdapter offShelfScanDetailAdapter;
    Float SumReaminQty=0f; //当前拣货物料剩余拣货数量合计
    int currentPickMaterialIndex=-1;
    String IsEdate="";

    String Erpvoucherno="";
    Integer Headerid=0;
    String TaskNo="";
    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.context = context;
        BaseApplication.toolBarTitle = new ToolBarTitle( getString(R.string.OffShelf_subtitle)+ "-" + BaseApplication.userInfo.getWarehouseName(), true);
        x.view().inject(this);
        BaseApplication.isCloseActivity=false;
    }

    @Override
    protected void initData() {
        super.initData();
        outStockTaskInfoModels=getIntent().getParcelableArrayListExtra("outStockTaskInfoModel");
        Erpvoucherno=outStockTaskInfoModels.get(0).getErpVoucherNo();
        Headerid=outStockTaskInfoModels.get(0).getHeaderID();
        TaskNo=outStockTaskInfoModels.get(0).getTaskNo();
        GetT_OutTaskDetailListByHeaderIDADF(outStockTaskInfoModels);
        txterpvoucherno.setText(Erpvoucherno);
        txtcustomername.setText(outStockTaskInfoModels.get(0).getSupcusName()==null?"":outStockTaskInfoModels.get(0).getSupcusName());
        edtcar.setText(outStockTaskInfoModels.get(0).getCarNo()==null?"":outStockTaskInfoModels.get(0).getCarNo());

//        if(!CheckBluetooth()){
//            MessageBox.Show(context, "蓝牙打印机连接失败");
//        }

        tbUnboxType.setChecked(true);
        tbBoxType.setChecked(false);
        ShowUnboxing(true);

//        String UserJson=GsonUtil.parseModelToJson(BaseApplication.userInfo);
    }



    @Event(value ={R.id.tb_UnboxType,R.id.tb_PalletType,R.id.tb_BoxType} ,type = CompoundButton.OnClickListener.class)
    private void TBonCheckedChanged(View view) {
        if(view.getId()== R.id.tb_UnboxType){
//            if(!CheckBluetooth()){
//                MessageBox.Show(context, "蓝牙打印机连接失败");
//                return;
//            }
        }

        tbUnboxType.setChecked(view.getId()== R.id.tb_UnboxType);
        tbBoxType.setChecked(view.getId()== R.id.tb_BoxType);
        ShowUnboxing(view.getId()== R.id.tb_UnboxType);
    }

    @Event(value =R.id.lsv_PickList,type =AdapterView.OnItemClickListener.class )
    private void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OutStockTaskDetailsInfo_Model outStockTaskDetailsInfoModel=(OutStockTaskDetailsInfo_Model)offShelfScanDetailAdapter.getItem(position);
        Intent intent = new Intent(context, Query.class);
        intent.putExtra("Type",1);
        intent.putExtra("MaterialNO",outStockTaskDetailsInfoModel.getMaterialNo());
        startActivityLeft(intent);
    }

    @Event(value =R.id.edt_Unboxing,type = View.OnKeyListener.class)
    private  boolean edtUnboxingClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            try{
                String num=edtUnboxing.getText().toString().trim();
                if (stockInfoModels != null && stockInfoModels.size() != 0) {

                    Float qty = Float.parseFloat(num); //输入数量
                    StockInfo_Model newmodel= new StockInfo_Model();
                    newmodel = stockInfoModels.get(0);
                    Float scanQty = newmodel.getQty(); //箱数量
                    if (qty >scanQty) {
                        MessageBox.Show(context, getString(R.string.Error_PackageQtyBiger));
                        CommonUtil.setEditFocus(edtUnboxing);
                        return true;
                    }
                    //ymh整箱发货
                    if (CommonUtil.EqualFloat(qty ,scanQty) && tbBoxType.isChecked()) {
                        stockInfoModels = new ArrayList<>();
                        stockInfoModels.add(newmodel);
                        currentPickMaterialIndex = FindFirstCanPickMaterialByMaterialNo(stockInfoModels.get(0).getMaterialNo(),stockInfoModels.get(0).getBatchNo(),stockInfoModels.get(0).getWarehouseNo());
                        if (currentPickMaterialIndex != -1) {
                            if (SumReaminQty < qty) {//qty > remainqty ||
                                MessageBox.Show(context, getString(R.string.Error_offshelfQtyBiger)
                                        +"\n需下架数量："+SumReaminQty
                                        +"\n扫描数量："+qty
                                        +"\n拆零后剩余数量："+ArithUtil.sub(qty,SumReaminQty));
                                CommonUtil.setEditFocus(edtUnboxing);
                                return true;
                            }

                            if (CheckStockInfo()) {  //判断是否拣货完毕、是否指定批次
                                ShowPickMaterialInfo();

//                                //打印拆零标签
//                                if (edtOffShelfScanbarcode.getText().toString().contains("@")){
//                                    stockInfoModels.get(0).setSN(txterpvoucherno.getText().toString());
//                                    LPK130DEMO(stockInfoModels.get(0),"Jian");
//                                }
                                txtEDate.setText(CommonUtil.DateToString(stockInfoModels.get(0).getEDate()));
                                txtStatus.setText(stockInfoModels.get(0).getStrStatus());
                                SetOutStockTaskDetailsInfoModels(newmodel.getQty(), 3);

                                if(!checkdetail(outStockTaskDetailsInfoModels)){
                                    MessageBox.Show(context,"提交的数据异常，退出重新扫描！");
                                    return true;
                                }


                                //提交数据
                                final Map<String, String> params = new HashMap<String, String>();
                                String ModelJson = GsonUtil.parseModelToJson(outStockTaskDetailsInfoModels);
                                String UserJson=GsonUtil.parseModelToJson(BaseApplication.userInfo);
                                params.put("UserJson",UserJson );
                                params.put("ModelJson", ModelJson);
                                LogUtil.WriteLog(OffshelfScan.class, TAG_SaveT_OutStockTaskDetailADF, ModelJson);
                                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SaveT_OutStockTaskDetailADF, getString(R.string.Msg_SaveT_OutStockTaskDetailADF), context, mHandler, RESULT_Msg_SaveT_OutStockTaskDetailADF, null,  URLModel.GetURL().SaveT_OutStockTaskDetailADF, params, null);
                            }
                        } else {
                            MessageBox.Show(context, getString(R.string.Error_NotPickMaterial));
                            CommonUtil.setEditFocus(edtOffShelfScanbarcode);
                        }
                        return true;
                    }
                    if (currentPickMaterialIndex != -1) {
                        //检查蓝牙打印机是否连上
                        if (edtOffShelfScanbarcode.getText().toString().contains("@")){
//                            if(!CheckBluetooth()){
//                                MessageBox.Show(context, "蓝牙打印机连接失败");
//                                return true;
//                            }
                        }

//                    Float remainqty = ArithUtil.sub(outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getRePickQty(),
//                            outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getScanQty());
                        if (SumReaminQty < qty) {//qty > remainqty ||
                            MessageBox.Show(context, getString(R.string.Error_offshelfQtyBiger)
                                    +"\n需下架数量："+SumReaminQty
                                    +"\n扫描数量："+qty
                                    +"\n拆零后剩余数量："+ArithUtil.sub(qty,SumReaminQty));
                            CommonUtil.setEditFocus(edtUnboxing);
                            return true;
                        }

                        //拆零
                        newmodel.setPickModel(3);
                        newmodel.setAmountQty(qty);

                        String userJson = GsonUtil.parseModelToJson(BaseApplication.userInfo);
                        newmodel.setHouseProp(HouseProp);
                        newmodel.setTaskDetailesID(TaskDetailesID);
                        String strOldBarCode = GsonUtil.parseModelToJson(newmodel);
                        if (!edtOffShelfScanbarcode.getText().toString().contains("@")){
                            newmodel.setBarcode(edtOffShelfScanbarcode.getText().toString());
                        }
                        final Map<String, String> params = new HashMap<String, String>();
                        params.put("UserJson", userJson);
                        params.put("strOldBarCode", strOldBarCode);
                        params.put("strNewBarCode", "");
                        params.put("PrintFlag", "2"); //1：打印 2：不打印
                        LogUtil.WriteLog(OffshelfScan.class, TAG_SaveT_BarCodeToStockADF, strOldBarCode);
                        SharePreferUtil.ReadSupplierShare(context);
                        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SaveT_BarCodeToStockADF, getString(R.string.Msg_SaveT_BarCodeToStockADF), context, mHandler, RESULT_SaveT_BarCodeToStockADF, null, URLModel.GetURL().SaveT_BarCodeToStockADF, params, null);

                    }

                } else {
                    MessageBox.Show(context, getString(R.string.Hit_ScanBarcode));
                    edtUnboxing.setText("");
                    CommonUtil.setEditFocus(edtOffShelfScanbarcode);
                    return true;
                }

            }catch(Exception ex){
                MessageBox.Show(context, ex.toString());
                return false;
            }
        }
        return false;

    }

    private boolean CheckBluetooth(){
        try{
            boolean flag=CheckBluetoothBase();
            return flag;
        }catch(Exception ex){
            return false;
        }

    }


    @Event(value =R.id.edt_OffShelfScanbarcode,type = View.OnKeyListener.class)
    private  boolean edtOffShelfScanbarcodeClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            keyBoardCancle();
            try{
                //2-整箱或者托发货 3-零数发货
                int Scantype = tbBoxType.isChecked()?2:3;

                String code=edtOffShelfScanbarcode.getText().toString().trim();
                final Map<String, String> params = new HashMap<String, String>();
                StockInfo_Model model = new StockInfo_Model();
                model.setBarcode(code);
                model.setScanType(Scantype);
                String Json=GsonUtil.parseModelToJson(model);
                params.put("ModelStockJson",Json);
                LogUtil.WriteLog(OffshelfScan.class, TAG_GetStockModelADF, code);
                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetStockModelADF, getString(R.string.Msg_GetT_SerialNoByPalletADF), context, mHandler, RESULT_Msg_GetStockModelADF, null, URLModel.GetURL().GetStockModelADF, params, null);
                return false;
            }catch(Exception ex){
                MessageBox.Show(context,ex.toString());
                return true;
            }
        }
        return false;
    }


//    @Event(value =R.id.edt_StockScan,type = View.OnKeyListener.class)
//    private  boolean edtStockScanClick(View v, int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
//        {
//            keyBoardCancle();
//            String areaNo=edtStockScan.getText().toString().trim();
//            if (!TextUtils.isEmpty(areaNo)) {
//                final Map<String, String> params = new HashMap<String, String>();
//                String UserJson=GsonUtil.parseModelToJson(BaseApplication.userInfo);
//                params.put("UserJson", UserJson);
//                params.put("AreaNo", areaNo);
//                String para = (new JSONObject(params)).toString();
//                LogUtil.WriteLog(OffshelfScan.class, TAG_GetAreano, para);
//                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetAreano, getString(R.string.Msg_GetAreanobyCheckno2), context, mHandler, RESULT_GetAreano, null, URLModel.GetURL().GetAreano, params, null);
//            }
//
//        }
//        return false;
//    }


//    @Event(value =R.id.edt_car,type = View.OnKeyListener.class)
//    private  boolean edtcarClick(View v, int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
//        {
//            keyBoardCancle();
//            String car=edtcar.getText().toString().trim();
//            if (!TextUtils.isEmpty(car)) {
//                final Map<String, String> params = new HashMap<String, String>();
//                String UserJson=GsonUtil.parseModelToJson(BaseApplication.userInfo);
//                params.put("strUserNo", UserJson);
//                params.put("TaskNo", TaskNo);
//                params.put("CarNo", car);
//                String para = (new JSONObject(params)).toString();
//                LogUtil.WriteLog(OffshelfScan.class, TAG_GetCar, para);
//                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetCar, getString(R.string.Msg_GetAreanobyCheckno2), context, mHandler, RESULT_GetCar, null, URLModel.GetURL().GetCarModelADF, params, null);
//            }
//
//        }
//        return false;
//    }

    @Event(R.id.btn_Print)
    private void edtPrint(View v) {
        if (DoubleClickCheck.isFastDoubleClick(context)) {
            return;
        }
//        if(!CheckBluetooth()){
//            MessageBox.Show(context, "蓝牙打印机连接失败");
//            return;
//        }

        if (BoxingModels != null && BoxingModels.size() > 0) {
            final Map<String, String> params = new HashMap<String, String>();
            String Json = GsonUtil.parseModelToJson(BoxingModels);
            params.put("ModelJson", Json);
            params.put("UserJson", GsonUtil.parseModelToJson(BaseApplication.userInfo));

            LogUtil.WriteLog(OffshelfScan.class, TAG_GetStockModelADF, Json);
            RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_PrintListADF, getString(R.string.Msg_Print), context, mHandler, RESULT_Msg_PrintListADF, null, URLModel.GetURL().PrintListADF, params, null);

        } else {
            MessageBox.Show(context, "没有可以打印的清单列表！");
        }
    }


//    @Event(value =R.id.btn_Print,type = View.OnKeyListener.class)
//    private  boolean edtPrint(View v, int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
//        {
//            if (BoxingModels != null && BoxingModels.size() > 0) {
//
//                final Map<String, String> params = new HashMap<String, String>();
//                String Json = GsonUtil.parseModelToJson(BoxingModels);
//                params.put("ModelJson", Json);
//                params.put("UserJson", GsonUtil.parseModelToJson(BaseApplication.userInfo));
//
//                LogUtil.WriteLog(OffshelfScan.class, TAG_GetStockModelADF, Json);
//                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_PrintListADF, getString(R.string.Msg_Print), context, mHandler, RESULT_Msg_PrintListADF, null, URLModel.GetURL().PrintListADF, params, null);
//
//            } else {
//                MessageBox.Show(context, "没有可以打印的清单列表！");
//            }
//            return false;
//        }
//        return false;
//    }

    @Event(R.id.btn_TJ)
    private void edtTJClick(View v) {
        if (DoubleClickCheck.isFastDoubleClick(context)) {
            return;
        }
        try{
            int AmountQty = stockInfoModels.get(0).getLstJBarCode().size();
//            stockInfoModels.get(0).setAmountQty((float) AmountQty);
            edtUnboxing.setText(AmountQty+"");
            KeyEvent key = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER);
            edtUnboxingClick(null, KeyEvent.KEYCODE_ENTER, key);
        }catch(Exception ex){
            MessageBox.Show(context,ex.toString());
        }
    }


    @Event(value =R.id.edt_jian,type = View.OnKeyListener.class)
    private  boolean edtjianClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            try{
                String jbarcode = edtjian.getText().toString().trim();
                if(jbarcode.contains("https:")){
                    jbarcode=jbarcode.replace("https://www.albionapplet.com/api/Values/CustomerQuery?barcode=","");
                }
                if (jbarcode.length()!=42){
                    MessageBox.Show(context,"肩箱条码位数不正确！");
                    return false;
                }

                //2-整箱或者托发货 3-零数发货
                String code=edtOffShelfScanbarcode.getText().toString().trim();
                final Map<String, String> params = new HashMap<String, String>();
                StockInfo_Model model = new StockInfo_Model();
                model.setBarcode(code);
                model.setJBarCode(edtjian.getText().toString().trim());
                model.setScanType(2);
                String Json=GsonUtil.parseModelToJson(model);
                params.put("ModelStockJson",Json);
                LogUtil.WriteLog(OffshelfScan.class, TAG_GetStockModelADF, code);
                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetStockModelFrojianADF, getString(R.string.Msg_GetT_SerialNoByPalletADF), context, mHandler, RESULT_Msg_GetStockModelFrojianADF, null, URLModel.GetURL().GetStockModelADF, params, null);
            }catch(Exception ex){
                MessageBox.Show(context,ex.toString());
            }
            return false;
        }
        return false;
    }




    @Event(R.id.btn_OutOfStock)
    private void btnOutofStockClick(View view) {
        if (DoubleClickCheck.isFastDoubleClick(context)) {
            return;
        }
        if (currentPickMaterialIndex!=-1) {
            final String MaterialDesc = outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getMaterialDesc();
            final String MaterialNo = outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getMaterialNo();
            new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("是否跳过物料：\n" +MaterialNo+"\n"+MaterialDesc + "\n拣货？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO 自动生成的方法
                            outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).setOutOfstock(true);
                            currentPickMaterialIndex=FindFirstCanPickMaterial();
                            ShowPickMaterialInfo();
                        }
                    }).setNegativeButton("取消", null).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lanya, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            if (DoubleClickCheck.isFastDoubleClick(context)) {
                return false;
            }
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, 1);
        }
        return super.onOptionsItemSelected(item);
    }


    /*
    下架明细获取
     */
    void GetT_OutTaskDetailListByHeaderIDADF(ArrayList<OutStockTaskInfo_Model> outStockTaskInfoModels){
        if(outStockTaskInfoModels!=null) {
            IsEdate=outStockTaskInfoModels.get(0).getIsEdate();
            final Map<String, String> params = new HashMap<String, String>();
            String modelJson= parseModelToJson(outStockTaskInfoModels);
            params.put("ModelDetailJson",modelJson);
            LogUtil.WriteLog(OffshelfScan.class, TAG_GetT_OutTaskDetailListByHeaderIDADF, modelJson);
            RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetT_OutTaskDetailListByHeaderIDADF, getString(R.string.Msg_QualityDetailListByHeaderIDADF), context, mHandler, RESULT_Msg_GetT_OutTaskDetailListByHeaderIDADF, null,  URLModel.GetURL().GetT_OutTaskDetailListByHeaderIDADF, params, null);
        }
    }


    void AnalysisGetT_UnLockTaskOperUserADFADFJson(String result){
        try {
            LogUtil.WriteLog(QCBillChoice.class, TAG_UnLockTaskOperUserADF, result);
            ReturnMsgModel<OutStockTaskInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<OutStockTaskInfo_Model>>() {
            }.getType());
            if (!returnMsgModel.getHeaderStatus().equals("S")) {
                MessageBox.Show(context, returnMsgModel.getMessage());
            }

            //锁表离开之前打印全部的清单明细
            if (BoxingModels != null && BoxingModels.size() > 0) {
                final Map<String, String> params = new HashMap<String, String>();
                String Json = GsonUtil.parseModelToJson(BoxingModels);
                params.put("ModelJson", Json);
                params.put("UserJson", GsonUtil.parseModelToJson(BaseApplication.userInfo));
                LogUtil.WriteLog(OffshelfScan.class, TAG_GetStockModelADF, Json);
                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_PrintListADF1, getString(R.string.Msg_Print), context, mHandler, RESULT_Msg_PrintListADF1, null, URLModel.GetURL().PrintListADF, params, null);
            }else{
                closeActiviry();
            }

//            closeActiviry();
        }catch (Exception ex){
            MessageBox.Show(context, ex.getMessage());
        }
    }

    ArrayList<OutStockTaskDetailsInfo_Model> outStockTaskDetailsInfoModelsForprint = new ArrayList<OutStockTaskDetailsInfo_Model>();
    /*处理下架明细*/
    void AnalysisGetT_OutTaskDetailListByHeaderIDADFJson(String result){
        LogUtil.WriteLog(OffshelfScan.class, TAG_GetT_OutTaskDetailListByHeaderIDADF,result);
        try {
            ReturnMsgModelList<OutStockTaskDetailsInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<OutStockTaskDetailsInfo_Model>>() {
            }.getType());
            outStockTaskDetailsInfoModels=new ArrayList<>();
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                outStockTaskDetailsInfoModels = returnMsgModel.getModelJson();
                if(outStockTaskDetailsInfoModels!=null&&outStockTaskDetailsInfoModels.size()>0){
                    outStockTaskDetailsInfoModelsForprint = (ArrayList<OutStockTaskDetailsInfo_Model>)outStockTaskDetailsInfoModels.clone();
                }
                int size=outStockTaskDetailsInfoModels.size();
                txterpvoucherno.setText(Erpvoucherno+"  （行数："+size+"）");
                MoveIndex=size;
                //处理拣货数量为0的
//                for(int i=0;i<size;i++) {
//                    if(ArithUtil.sub(outStockTaskDetailsInfoModels.get(i).getRePickQty(),
//                            outStockTaskDetailsInfoModels.get(i).getScanQty())==0f)
//                        Collections.swap(outStockTaskDetailsInfoModels, i, size-1);
//                }
                currentPickMaterialIndex = FindFirstCanPickMaterial(); //查找需要拣货物料行
                ShowPickMaterialInfo();//显示需要拣货物料

                //光标定位
                if (outStockTaskDetailsInfoModels.get(0).getHouseProp()==2){
                    if(edtOffShelfScanbarcode.getText().toString().length()>0){
                        CommonUtil.setEditFocus(edtStockScan);
                    }else{
                        CommonUtil.setEditFocus(edtcar);
                    }

                }else{
                    edtcar.setVisibility(View.INVISIBLE);
                    textView133.setVisibility(View.INVISIBLE);
                    CommonUtil.setEditFocus(edtOffShelfScanbarcode);
                }

            } else {
                MessageBox.Show(context, returnMsgModel.getMessage());
            }
            edtOffShelfScanbarcode.setText("");
        }catch (Exception ex){
            MessageBox.Show(context,ex.getMessage());
        }
    }

    /*扫描条码*/
    void AnalysisGetStockModelADFJson(String result) {
        LogUtil.WriteLog(OffshelfScan.class, TAG_GetStockModelADF, result);
        try {
            ReturnMsgModelList<StockInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<StockInfo_Model>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
//                if (stockInfoModels != null && stockInfoModels.size() != 0) {
                    //区分整箱/托 还有零
                    if(tbBoxType.isChecked()){
                        ArrayList<StockInfo_Model> StockInfos = returnMsgModel.getModelJson();
                        //ymh混箱处理
                        int Rownum=StockInfos.size();
                        stockInfoModels = new ArrayList<>();
                        for(int i=0;i<Rownum;i++){
                            if (StockInfos.get(i).getLstHBarCode()!=null&&StockInfos.get(i).getLstHBarCode().size()>0){
                                for(int j=0;j<StockInfos.get(i).getLstHBarCode().size();j++){
                                    stockInfoModels.add(StockInfos.get(i).getLstHBarCode().get(j));
                                }
                            }else{
                                stockInfoModels.add(StockInfos.get(i));
                            }
                        }
                        //混箱处理
                        Bindbarcode(stockInfoModels);
                        //混箱提交处理
                        ArrayList<StockInfo_Model> models = new ArrayList<>();
                        for (int i=0;i<outStockTaskDetailsInfoModels.size();i++){
                            if(outStockTaskDetailsInfoModels.get(i).getLstStockInfo()!=null){
                                for (int j=0;j<outStockTaskDetailsInfoModels.get(i).getLstStockInfo().size();j++){
                                    if((outStockTaskDetailsInfoModels.get(i).getLstStockInfo().get(j).getFserialno()!=null)&&(!outStockTaskDetailsInfoModels.get(i).getLstStockInfo().get(j).getFserialno().equals(""))){

                                        StockInfo_Model model = new StockInfo_Model();
                                        String Fserialno=outStockTaskDetailsInfoModels.get(i).getLstStockInfo().get(j).getFserialno();
                                        model.setBarcode(Fserialno);
                                        model.setSerialNo(Fserialno.substring(Fserialno.length()-14,Fserialno.length()));
                                        model.setStrongHoldCode("ABH");
                                        model.setStrongHoldName("ABH");
                                        model.setCompanyCode("10");
                                        model.setBarCodeType(5);
                                        model.setQty(1f);
                                        model.setIsAmount(1);
                                        model.setSupPrdBatch("");
                                        model.setSupPrdDate(new Date());
                                        Date date = new Date();
                                        Calendar cal = Calendar.getInstance();
                                        cal.setTime(date);//设置起时间
                                        cal.add(Calendar.YEAR, 100);//增加100年
                                        model.setEDate(cal.getTime());
                                        model.setID(0);
                                        model.setMaterialNo("");
                                        model.setMaterialDesc("");
                                        model.setBatchNo("");
                                        model.setEAN("");
                                        model.setToErpWarehouse("");
                                        model.setToErpAreaNo("");
                                        model.setStatus(3);
                                        model.setWarehouseNo(outStockTaskDetailsInfoModels.get(i).getLstStockInfo().get(j).getWarehouseNo());
                                        model.setWareHouseID(outStockTaskDetailsInfoModels.get(i).getLstStockInfo().get(j).getWareHouseID());
                                        model.setHouseID(outStockTaskDetailsInfoModels.get(i).getLstStockInfo().get(j).getHouseID());
                                        model.setAreaID(outStockTaskDetailsInfoModels.get(i).getLstStockInfo().get(j).getAreaID());
                                        model.setIsPalletOrBox(outStockTaskDetailsInfoModels.get(i).getLstStockInfo().get(j).getIsPalletOrBox());
                                        model.setPalletNo(outStockTaskDetailsInfoModels.get(i).getLstStockInfo().get(j).getPalletNo());

//                                        outStockTaskDetailsInfoModels.get(i).getLstStockInfo().remove(outStockTaskDetailsInfoModels.get(i).getLstStockInfo().get(j));
                                        if(models.indexOf(model)==-1){
                                            outStockTaskDetailsInfoModels.get(i).getLstStockInfo().add(model);
                                            models.add(model);
                                        }
                                    }
                                }
                            }

                        }

                        for (int i=0;i<outStockTaskDetailsInfoModels.size();i++){
                            if(outStockTaskDetailsInfoModels.get(i).getLstStockInfo()!=null){
                                ArrayList<StockInfo_Model> StockInfomodels= new ArrayList<StockInfo_Model>();
                                for (int j=0;j<outStockTaskDetailsInfoModels.get(i).getLstStockInfo().size();j++){
                                    if(outStockTaskDetailsInfoModels.get(i).getLstStockInfo().get(j).getFserialno()==null||outStockTaskDetailsInfoModels.get(i).getLstStockInfo().get(j).getFserialno().equals("")){
                                        StockInfomodels.add(outStockTaskDetailsInfoModels.get(i).getLstStockInfo().get(j));
                                    }
                                }
                                outStockTaskDetailsInfoModels.get(i).setLstStockInfo(new ArrayList<StockInfo_Model>());
                                outStockTaskDetailsInfoModels.get(i).setLstStockInfo(StockInfomodels);
                            }
                        }
                        //混箱提交处理

                        final Map<String, String> params = new HashMap<String, String>();
                        String ModelJson = GsonUtil.parseModelToJson(outStockTaskDetailsInfoModels);
                        String UserJson=GsonUtil.parseModelToJson(BaseApplication.userInfo);
                        params.put("UserJson",UserJson );
                        params.put("ModelJson", ModelJson);
                        LogUtil.WriteLog(OffshelfScan.class, TAG_SaveT_OutStockTaskDetailADF, ModelJson);
                        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SaveT_OutStockTaskDetailADF, getString(R.string.Msg_SaveT_OutStockTaskDetailADF), context, mHandler, RESULT_Msg_SaveT_OutStockTaskDetailADF, null,  URLModel.GetURL().SaveT_OutStockTaskDetailADF, params, null);

                    }else{
                        stockInfoModels = returnMsgModel.getModelJson();
                        insertStockInfo();
                        CommonUtil.setEditFocus(edtUnboxing);
                        //拆零扫肩箱
                        if (stockInfoModels.size()==1&&tbUnboxType.isChecked()&&stockInfoModels.get(0).getISJ().equals("1")){
                            txtUnboxing.setVisibility(View.GONE);
                            edtUnboxing.setVisibility(View.GONE);
                            txtjian.setVisibility(View.VISIBLE);
                            edtjian.setVisibility(View.VISIBLE);
                            CommonUtil.setEditFocus(edtjian);
                        }
                        //拆零输数量
                        if (stockInfoModels.size()==1&&tbUnboxType.isChecked()&&stockInfoModels.get(0).getISJ().equals("2")){
                            txtUnboxing.setVisibility(View.VISIBLE);
                            edtUnboxing.setVisibility(View.VISIBLE);
                            txtjian.setVisibility(View.GONE);
                            edtjian.setVisibility(View.GONE);
                            CommonUtil.setEditFocus(edtUnboxing);
                            edtUnboxing.setText(stockInfoModels.get(0).getQty()+"");
                            CommonUtil.setEditFocus(edtUnboxing);
                        }
                    }


            } else {
                MessageBox.Show(context, returnMsgModel.getMessage());
                CommonUtil.setEditFocus(edtOffShelfScanbarcode);
            }
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
            CommonUtil.setEditFocus(edtOffShelfScanbarcode);
        }

    }


    void AnalysisGetStockModelFrojianADFJson(String result) {
        LogUtil.WriteLog(OffshelfScan.class, TAG_GetStockModelADF, result);
        try {
            ReturnMsgModelList<StockInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<StockInfo_Model>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                List<StockInfo_Model> Models = returnMsgModel.getModelJson();

                ArrayList<StockInfo_Model> lists= stockInfoModels.get(0).getLstJBarCode()==null?new  ArrayList<StockInfo_Model>():stockInfoModels.get(0).getLstJBarCode();

                //判断是否已经扫描过的肩箱
                int index = lists.indexOf(Models.get(0));
                if(index<0){
                    lists.add(Models.get(0));
                    stockInfoModels.get(0).setLstJBarCode(lists);
                }else{
                    MessageBox.Show(context,"该肩箱码已经被扫描，不能重复扫描");
                }
                CommonUtil.setEditFocus(edtjian);

            } else {
                MessageBox.Show(context, returnMsgModel.getMessage());
                CommonUtil.setEditFocus(edtjian);
            }
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
            CommonUtil.setEditFocus(edtjian);
        }

    }


    void AnalysisSaveT_OutStockTaskDetailADFJson(String result){
        try {
            LogUtil.WriteLog(OffshelfScan.class, TAG_SaveT_OutStockTaskDetailADF,result);
            ReturnMsgModelList<Base_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<Base_Model>>() {}.getType());
            if(returnMsgModel.getHeaderStatus().equals("S")){

                //拆零的装入清单
                if(isChai){
                    Boxing model= new Boxing();
                    model.setMaterialNo(stockInfoModels.get(0).getMaterialNo());
                    model.setMaterialName(stockInfoModels.get(0).getMaterialDesc());
                    model.setQty(stockInfoModels.get(0).getQty());
                    model.setErpVoucherNo(outStockTaskDetailsInfoModels.get(0).getErpVoucherNo());
                    model.setTaskNo(outStockTaskDetailsInfoModels.get(0).getTaskNo());
                    BoxingModels.add(model);
                    isChai=false;
                }


                //刷新页面
//                for(int i=0;i<outStockTaskDetailsInfoModels.size();i++){
//                    if(outStockTaskDetailsInfoModels.get(i).getScanQty()>0){
//                        outStockTaskDetailsInfoModels.get(i).setRemainQty(outStockTaskDetailsInfoModels.get(i).getRemainQty()-outStockTaskDetailsInfoModels.get(i).getScanQty());
//                    }
//                    outStockTaskDetailsInfoModels.get(i).setLstStockInfo(new ArrayList<StockInfo_Model>());
//                }
//                currentPickMaterialIndex = FindFirstCanPickMaterial(); //查找需要拣货物料行
//                ShowPickMaterialInfo();//显示需要拣货物料



            }else {
                MessageBox.Show(context, returnMsgModel.getMessage());
            }
            clearFrm();
            stockInfoModels=new ArrayList<>();
            GetT_OutTaskDetailListByHeaderIDADF(outStockTaskInfoModels);
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
            LogUtil.WriteLog(OffshelfScan.class,"error",ex.getMessage());
        }
    }



    ArrayList<OutStockDetailInfo_Model> GetPalletModels(){
        ArrayList<OutStockDetailInfo_Model> palletDetailModels=new ArrayList<>();
        try {
            if (outStockTaskDetailsInfoModels != null) {
                for (OutStockTaskDetailsInfo_Model outStockTaskDetailsInfo_Model : outStockTaskDetailsInfoModels) {
                    if (outStockTaskDetailsInfo_Model.getLstStockInfo() != null) {
                        OutStockDetailInfo_Model palletDetail_model = new OutStockDetailInfo_Model();
                        palletDetail_model.setErpVoucherNo(outStockTaskDetailsInfo_Model.getErpVoucherNo());
                        palletDetail_model.setVoucherNo(outStockTaskDetailsInfo_Model.getVoucherNo());
                        palletDetail_model.setRowNo(outStockTaskDetailsInfo_Model.getRowNo());
                        palletDetail_model.setRowNoDel(outStockTaskDetailsInfo_Model.getRowNoDel());
                        palletDetail_model.setCompanyCode(outStockTaskDetailsInfo_Model.getCompanyCode());
                        palletDetail_model.setStrongHoldCode(outStockTaskDetailsInfo_Model.getStrongHoldCode());
                        palletDetail_model.setStrongHoldName(outStockTaskDetailsInfo_Model.getStrongHoldName());
                        palletDetail_model.setVoucherType(999);
                        palletDetail_model.setMaterialNo(outStockTaskDetailsInfo_Model.getMaterialNo());
                        palletDetail_model.setMaterialNoID(outStockTaskDetailsInfo_Model.getMaterialNoID());
                        palletDetail_model.setMaterialDesc(outStockTaskDetailsInfo_Model.getMaterialDesc());
                        if (palletDetail_model.getLstStock() == null)
                            palletDetail_model.setLstStock(new ArrayList<StockInfo_Model>());
                        //  ArrayList<StockInfo_Model> tempStockModels = new ArrayList<>();
                        for (StockInfo_Model stockModel : outStockTaskDetailsInfo_Model.getLstStockInfo()) {
                            StockInfo_Model stockInfoModel=stockModel;
                            int index = palletDetailModels.indexOf(palletDetail_model);
                            if (stockInfoModel.getStockBarCodeStatus() == 0) {
                                if (index == -1) {
                                    palletDetail_model.getLstStock().add(0, stockInfoModel);
                                    palletDetailModels.add(palletDetail_model);
                                } else {
                                    int stockIndex = palletDetailModels.get(index).getLstStock().indexOf(stockInfoModel);
                                    if(stockIndex==-1){
                                        palletDetailModels.get(index).getLstStock().add(0, stockInfoModel);
                                    }else {
                                        palletDetailModels.get(index).getLstStock().get(stockIndex).setQty(
                                                ArithUtil.add(palletDetailModels.get(index).getLstStock().get(stockIndex).getQty(), stockModel.getQty())
                                        );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception ex){
            MessageBox.Show(context,ex.getMessage());
            palletDetailModels=new ArrayList<>();
        }
        return palletDetailModels;
    }


    /*提交装箱清单*/
    void AnalysisPrintListADF(String result){
        try {
            LogUtil.WriteLog(OffshelfScan.class, TAG_PrintListADF, result);
            ReturnMsgModelList<Boxing> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<Boxing>>() {}.getType());
            if(returnMsgModel.getHeaderStatus().equals("S")){
                ArrayList<Boxing> BoxingList = returnMsgModel.getModelJson();
                //打印清单
                StockInfo_Model stockmodel = new StockInfo_Model();
                outStockTaskInfoModels.get(0).setCustomerName(outStockTaskDetailsInfoModelsForprint.get(0).getFromErpWareHouseName());

                if(outStockTaskInfoModels.get(0).getErpVoucherNo().contains("C")){
                    outStockTaskInfoModels.get(0).setCarNo(outStockTaskDetailsInfoModelsForprint.get(0).getSupCusName());
                }else{
                    outStockTaskInfoModels.get(0).setCarNo(outStockTaskDetailsInfoModelsForprint.get(0).getToErpWareHouseName());
                }
                LPK130DEMO(outStockTaskInfoModels.get(0),BoxingList,stockmodel,"LList");
                BoxingModels= new ArrayList<>();//清空装箱数据
            }
            else{
                MessageBox.Show(context, returnMsgModel.getMessage());
            }
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
            BoxingModels= new ArrayList<>();//清空装箱数据
        }

        CommonUtil.setEditFocus(edtOffShelfScanbarcode);

    }

    /*提交装箱清单*/
    void  AnalysisPrintListADF1(String result){
        try {
            LogUtil.WriteLog(OffshelfScan.class, TAG_PrintListADF, result);
            ReturnMsgModelList<Boxing> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<Boxing>>() {}.getType());
            if(returnMsgModel.getHeaderStatus().equals("S")){
                ArrayList<Boxing> BoxingList = returnMsgModel.getModelJson();
                if(BoxingList==null||BoxingList.size()==0){
                    MessageBox.Show(context, "保存成功，返回清单为空！");
                }else{
                    //打印清单
                    StockInfo_Model stockmodel = new StockInfo_Model();
                    outStockTaskInfoModels.get(0).setCustomerName(outStockTaskDetailsInfoModels.get(0).getFromErpWareHouseName());
                    if(outStockTaskInfoModels.get(0).getErpVoucherNo().contains("C")){
                        outStockTaskInfoModels.get(0).setCarNo(outStockTaskDetailsInfoModelsForprint.get(0).getSupCusName());
                    }else{
                        outStockTaskInfoModels.get(0).setCarNo(outStockTaskDetailsInfoModelsForprint.get(0).getToErpWareHouseName());
                    }
                    LPK130DEMO(outStockTaskInfoModels.get(0),BoxingList,stockmodel,"LList");
                    BoxingModels= new ArrayList<>();//清空装箱数据
                }
                closeActiviry();
            }
            else{
                MessageBox.Show(context, returnMsgModel.getMessage());
            }
            closeActiviry();
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
            BoxingModels= new ArrayList<>();//清空装箱数据
            closeActiviry();
        }

        CommonUtil.setEditFocus(edtOffShelfScanbarcode);

    }




//    /*拆箱提交*/
//    void AnalysisSaveT_BarCodeToStockADF(String result){
//        try {
//            LogUtil.WriteLog(OffshelfScan.class, TAG_SaveT_BarCodeToStockADF, result);
//            ReturnMsgModel<StockInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<StockInfo_Model>>() {
//            }.getType());
//            if(returnMsgModel.getHeaderStatus().equals("S")){
//                StockInfo_Model stockInfoModel=returnMsgModel.getModelJson();
//                stockInfoModels=new ArrayList<>();
//                stockInfoModels.add(stockInfoModel);
//                currentPickMaterialIndex=FindFirstCanPickMaterialByMaterialNo(stockInfoModels.get(0).getMaterialNo(), stockInfoModels.get(0).getStrongHoldCode());
//                if (currentPickMaterialIndex != -1) {
//                    if (CheckStockInfo()) {  //判断是否拣货完毕、是否指定批次
//                        ShowPickMaterialInfo();
//                        txtEDate.setText(CommonUtil.DateToString(stockInfoModels.get(0).getEDate()));
//                        txtStatus.setText(stockInfoModels.get(0).getStrStatus());
//                        SetOutStockTaskDetailsInfoModels(stockInfoModel.getQty(), 3);
//                    }
//                }else {
//                    MessageBox.Show(context, getString(R.string.Error_NotPickMaterial));
//                    CommonUtil.setEditFocus(edtOffShelfScanbarcode);
//                }
//            }
//            else{
//                MessageBox.Show(context, returnMsgModel.getMessage());
//            }
//        } catch (Exception ex) {
//            MessageBox.Show(context, ex.getMessage());
//        }
//        edtUnboxing.setText("");
//        CommonUtil.setEditFocus(edtOffShelfScanbarcode);
//
//    }
    boolean isChai=false;//是否是拆零的标记，便于打印清单
    /*拆箱提交*/
    void AnalysisSaveT_BarCodeToStockADF(String result) {
        try {
            LogUtil.WriteLog(OffshelfScan.class, TAG_SaveT_BarCodeToStockADF, result);
            ReturnMsgModel<StockInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<StockInfo_Model>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                StockInfo_Model stockInfoModel = returnMsgModel.getModelJson();
                //打印拆零标签
//                if (edtOffShelfScanbarcode.getText().toString().contains("@")){
                    stockInfoModel.setSN(txterpvoucherno.getText().toString());
//                    LPK130DEMO(stockInfoModel,"Jian");
//                }
                stockInfoModels = new ArrayList<>();
                stockInfoModels.add(stockInfoModel);
                currentPickMaterialIndex = FindFirstCanPickMaterialByMaterialNo(stockInfoModels.get(0).getMaterialNo(),stockInfoModels.get(0).getBatchNo(), stockInfoModels.get(0).getWarehouseNo());
                if (currentPickMaterialIndex != -1) {
                    if (CheckStockInfo()) {  //判断是否拣货完毕、是否指定批次
                        ShowPickMaterialInfo();
                        txtEDate.setText(CommonUtil.DateToString(stockInfoModels.get(0).getEDate()));
                        txtStatus.setText(stockInfoModels.get(0).getStrStatus());
                        SetOutStockTaskDetailsInfoModels(stockInfoModel.getQty(), 3);

                        if(!checkdetail(outStockTaskDetailsInfoModels)){
                            MessageBox.Show(context,"拆零完成，提交的数据异常，退出重新扫描！");
//                            LPK130DEMO1Scan(stockInfoModels.get(0).getBarcode());
                            return;
                        }
                        isChai=true;

                        //提交数据
                        final Map<String, String> params = new HashMap<String, String>();
                        String ModelJson = GsonUtil.parseModelToJson(outStockTaskDetailsInfoModels);
                        String UserJson=GsonUtil.parseModelToJson(BaseApplication.userInfo);
                        params.put("UserJson",UserJson );
                        params.put("ModelJson", ModelJson);
                        LogUtil.WriteLog(OffshelfScan.class, TAG_SaveT_OutStockTaskDetailADF, ModelJson);
                        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SaveT_OutStockTaskDetailADF, getString(R.string.Msg_SaveT_OutStockTaskDetailADF), context, mHandler, RESULT_Msg_SaveT_OutStockTaskDetailADF, null,  URLModel.GetURL().SaveT_OutStockTaskDetailADF, params, null);
                    }
                } else {
                    MessageBox.Show(context, getString(R.string.Error_NotPickMaterial));
                    CommonUtil.setEditFocus(edtOffShelfScanbarcode);
                }
            } else {
                MessageBox.Show(context, returnMsgModel.getMessage());
            }
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
        }
        edtUnboxing.setText("");
        CommonUtil.setEditFocus(edtOffShelfScanbarcode);

    }

    //检查提交列表是否存在超条码现象
    private boolean checkdetail(ArrayList<OutStockTaskDetailsInfo_Model> models){
        ArrayList<StockInfo_Model> checkstocks = new ArrayList<>();
        int count=0;
        if (models!=null&&models.size()>0){
            for (int i=0;i<models.size();i++){
                if (models.get(i).getLstStockInfo()!=null&&models.get(i).getLstStockInfo().size()>0){
                    count=count+models.get(i).getLstStockInfo().size();

                    for(int j=0;j<models.get(i).getLstStockInfo().size();j++){
                        if(checkstocks.indexOf(models.get(i).getLstStockInfo().get(j))!=-1){
                            return false;
                        }else{
                            checkstocks.add(models.get(i).getLstStockInfo().get(j));
                        }
                    }
                }
            }
        }
        if (count!=1){return false;}else{return true;}

    }

//    void AnalysisetT_SaveT_OutStockReviewPalletDetailADFJson(String result){
//        try {
//            LogUtil.WriteLog(OffshelfScan.class, TAG_SaveT_OutStockReviewPalletDetailADF,result);
//            ReturnMsgModel<Base_Model> returnMsgModel =  GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<Base_Model>>() {
//            }.getType());
//            if(returnMsgModel.getHeaderStatus().equals("S")){
//                MessageBox.Show(context,returnMsgModel.getMessage());
//                //更改实体类组托状态
//                for (int i=0;i<outStockTaskDetailsInfoModels.size();i++) {
//                    if(outStockTaskDetailsInfoModels.get(i).getLstStockInfo()!=null) {
//                        for (int j = 0; j < outStockTaskDetailsInfoModels.get(i).getLstStockInfo().size(); j++) {
//                            outStockTaskDetailsInfoModels.get(i).getLstStockInfo().get(j).setStockBarCodeStatus(1);
//                        }
//                    }
////                    outStockTaskDetailsInfoModels.get(i).setOustockStatus(0);
//                }
////                BindListVIew(outStockDetailInfoModels);
//            }else
//            {
//                MessageBox.Show(context,returnMsgModel.getMessage());
//            }
//        } catch (Exception ex) {
//            MessageBox.Show(context, ex.getMessage());
//        }
//        CommonUtil.setEditFocus(edtOffShelfScanbarcode);
//    }

//    void AnalysisetT_SaveT_OutStockReviewPalletDetailForLanyaADF(String result){
//        try {
//            LogUtil.WriteLog(OffshelfScan.class, TAG_SaveT_OutStockReviewPalletDetailForLanyaADF,result);
//            ReturnMsgModel<Base_Model> returnMsgModel =  GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<Base_Model>>() {
//            }.getType());
//            if(returnMsgModel.getHeaderStatus().equals("S")){
//                String command=returnMsgModel.getMessage();
//                if (!command.isEmpty()){
//                    onPrint(command);
//                }
//                //更改实体类组托状态
//                for (int i=0;i<outStockTaskDetailsInfoModels.size();i++) {
//                    if(outStockTaskDetailsInfoModels.get(i).getLstStockInfo()!=null) {
//                        for (int j = 0; j < outStockTaskDetailsInfoModels.get(i).getLstStockInfo().size(); j++) {
//                            outStockTaskDetailsInfoModels.get(i).getLstStockInfo().get(j).setStockBarCodeStatus(1);
//                        }
//                    }
////                    outStockTaskDetailsInfoModels.get(i).setOustockStatus(0);
//                }
////                BindListVIew(outStockDetailInfoModels);
//            }else
//            {
//                MessageBox.Show(context,returnMsgModel.getMessage());
//            }
//        } catch (Exception ex) {
//            MessageBox.Show(context, ex.getMessage());
//        }
//        CommonUtil.setEditFocus(edtOffShelfScanbarcode);
//    }

    void AnalysisSaveT_SingleErpvoucherADF(String result){
        LogUtil.WriteLog(OffshelfScan.class, TAG_SaveT_SingleErpvoucherADF,result);
        ReturnMsgModelList<OutStock_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<OutStock_Model>>() {}.getType());
        if(returnMsgModel.getHeaderStatus().equals("S")) {
            new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage(returnMsgModel.getMessage())
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO 自动生成的方法
                            Intent intent = new Intent(context, TruckLoad.class);
                            intent.putExtra("VoucherNo", Erpvoucherno);
                            startActivityLeft(intent);
                            closeActiviry();
                        }
                    }).show();
        }else
        {
            MessageBox.Show(context,returnMsgModel.getMessage());
        }
    }

    void insertStockInfo(){
        try{
            currentPickMaterialIndex=FindFirstCanPickMaterialByMaterialNo(stockInfoModels.get(0).getMaterialNo(),stockInfoModels.get(0).getBatchNo(), stockInfoModels.get(0).getWarehouseNo());
            if (currentPickMaterialIndex != -1) {
                    if (CheckStockInfo()) {  //判断是否拣货完毕
                    ShowPickMaterialInfo();
                    txtEDate.setText(CommonUtil.DateToString(stockInfoModels.get(0).getEDate()));
                    txtStatus.setText(stockInfoModels.get(0).getStrStatus());
//                if (tbPalletType.isChecked()) {//整托
//                    Float scanQty = stockInfoModels.get(0).getPalletQty();
//                    checkQTY(scanQty, true);
//                } else if (tbBoxType.isChecked()) { //整箱
//                    Float scanQty = stockInfoModels.get(0).getQty();
//                    checkQTY(scanQty, false);
//                }
//                Float scanQty = stockInfoModels.get(0).getQty();
//                checkQTY(scanQty, false);
//                CommonUtil.setEditFocus(tbUnboxType.isChecked() ? edtUnboxing : edtOffShelfScanbarcode);
//                edtUnboxing.setText("0");
                }
            } else {
                MessageBox.Show(context, getString(R.string.Error_NotPickMaterial));
                CommonUtil.setEditFocus(edtOffShelfScanbarcode);
            }
        }catch(Exception ex){
            MessageBox.Show(context, ex.toString());
        }

    }

    boolean isDel = false;
    void Bindbarcode(final ArrayList<StockInfo_Model> barCodeInfos) {
        if (barCodeInfos != null && barCodeInfos.size() != 0) {
            try {
                for (StockInfo_Model StockInfo : barCodeInfos) {
                    if (StockInfo != null && outStockTaskDetailsInfoModels != null) {
                        OutStockTaskDetailsInfo_Model outStockTaskDetailsInfo_Model = new OutStockTaskDetailsInfo_Model(StockInfo.getMaterialNo(),StockInfo.getBatchNo());
                        final int index = outStockTaskDetailsInfoModels.indexOf(outStockTaskDetailsInfo_Model);
                        if (index != -1) {
                            if (outStockTaskDetailsInfoModels.get(index).getLstStockInfo() == null){
                                outStockTaskDetailsInfoModels.get(index).setLstStockInfo(new ArrayList<StockInfo_Model>());
                            }
                            final int barIndex = outStockTaskDetailsInfoModels.get(index).getLstStockInfo().indexOf(StockInfo);
                            if (barIndex != -1) {
                                if (isDel) {
                                    RemoveBarcode(index, barIndex);
                                } else {
                                    new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("是否删除已扫描条码？")
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO 自动生成的方法
                                                    //RemoveBarcode(index, barIndex);
                                                    isDel = true;
                                                    Bindbarcode(barCodeInfos);
                                                }
                                            }).setNegativeButton("取消", null).show();
                                    break;
                                }
                            } else {
                                //插入数据
                                if (!CheckBarcode(StockInfo, index))
                                    break;
                            }
//                            RefeshFrm(index);
                        } else {
                            MessageBox.Show(context, getString(R.string.Error_BarcodeNotInList) + "|" + StockInfo.getSerialNo());
                            break;
                        }
                    }

                }
//                InitFrm(barCodeInfos.get(0));
            } catch (Exception ex) {
                MessageBox.Show(context, ex.getMessage());
                CommonUtil.setEditFocus(edtStockScan);
            }

        }
    }

    boolean RemoveBarcode(final int index, final int barIndex) {
        float qty = ArithUtil.sub(outStockTaskDetailsInfoModels.get(index).getScanQty(), outStockTaskDetailsInfoModels.get(index).getLstStockInfo().get(barIndex).getQty());
        outStockTaskDetailsInfoModels.get(index).getLstStockInfo().remove(barIndex);
        outStockTaskDetailsInfoModels.get(index).setScanQty(qty);
        return true;
    }

    boolean CheckBarcode(StockInfo_Model StockInfo, int index) {
        boolean isChecked = false;
        if (outStockTaskDetailsInfoModels.get(index).getRemainQty() == 0) {
            MessageBox.Show(context, "该行物料已经拣货完毕");
            return false;
        }

        isChecked = Addbarcode(index, StockInfo);
        return isChecked;
    }

    boolean Addbarcode(int index, StockInfo_Model StockInfo) {
        float qty = ArithUtil.add(outStockTaskDetailsInfoModels.get(index).getScanQty(), StockInfo.getQty());
        if (qty <= outStockTaskDetailsInfoModels.get(index).getRemainQty()) {
            outStockTaskDetailsInfoModels.get(index).getLstStockInfo().add(0, StockInfo);
            outStockTaskDetailsInfoModels.get(index).setScanQty(qty);
            return true;
        } else {
            MessageBox.Show(context, "该物料拣货数量数量大于需要拣货数量");
        }
        return false;
    }


    void RemoveStockInfo(final int index){
        currentPickMaterialIndex=FindFirstCanPickMaterialByMaterialNoDelete(stockInfoModels.get(0).getMaterialNo(), stockInfoModels.get(0).getStrongHoldCode());
        if (currentPickMaterialIndex != -1) {
            new AlertDialog.Builder(context).setTitle("提示")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage("是否删除已扫描条码？")
                    .setCancelable(false)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO 自动生成的方法
                            ShowPickMaterialInfo();
                            for (StockInfo_Model stockInfoModel : stockInfoModels) {
//                            outStockTaskDetailsInfoModels.get(index).
//                                    setScanQty(ArithUtil.sub(outStockTaskDetailsInfoModels.get(index).getScanQty(), stockInfoModel.getQty()));
                                RemoveSameLineMaterialNum(stockInfoModel.getQty());
                                outStockTaskDetailsInfoModels.get(index).getLstStockInfo().remove(stockInfoModel);
                            }
                            currentPickMaterialIndex = FindFirstCanPickMaterial();
                            ShowPickMaterialInfo(); //显示下一拣货物料
                            CommonUtil.setEditFocus(edtOffShelfScanbarcode);

                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    stockInfoModels = null;
                    CommonUtil.setEditFocus(edtOffShelfScanbarcode);
                }
            }).show();
        } else {
            MessageBox.Show(context, getString(R.string.Error_NotPickMaterial));
            CommonUtil.setEditFocus(edtOffShelfScanbarcode);
        }
    }

    void checkQTY(float scanQty,Boolean isPallet) {
        //根据物料查询扫描剩余数量的总数
        // Float qty=ArithUtil.sub(outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getRemainQty(),
//       Float qty=ArithUtil.sub(outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getRePickQty(),
//               outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getScanQty());
//        if (qty< scanQty ||  SumReaminQty<scanQty ) {
        if (SumReaminQty<scanQty ) {
            MessageBox.Show(context, getString(R.string.Error_offshelfQtyBiger)
                    +"\n需下架数量："+SumReaminQty
                    +"\n扫描数量："+scanQty
                    +"\n拆零后剩余数量："+ArithUtil.sub(scanQty,SumReaminQty));
            stockInfoModels=new ArrayList<>();
            CommonUtil.setEditFocus(edtOffShelfScanbarcode);
            return;
        }
        SetOutStockTaskDetailsInfoModels(scanQty,isPallet?1:2);

    }

    void clearFrm(){
//        outStockTaskInfoModels=new ArrayList<>();
//        outStockTaskDetailsInfoModels=new ArrayList<>();
        stockInfoModels=new ArrayList<>();
        SumReaminQty=0f; //当前拣货物料剩余拣货数量合计
        currentPickMaterialIndex=-1;
        IsEdate="";
        edtStockScan.setText("");
        edtUnboxing.setText("");
//        txtgetbatch.setText("");
//        edtOffShelfScanbarcode.setText("");
        BindListVIew(outStockTaskDetailsInfoModels);
    }


    //赋值
    void  SetOutStockTaskDetailsInfoModels(Float scanQty,int type) {
        switch (type) {
            case 1: //托盘
                AddSameLineMaterialNum(scanQty);
                for (StockInfo_Model stockInfoModel : stockInfoModels) {
                    stockInfoModel.setPickModel(1);
//                   outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).
//                           setScanQty(ArithUtil.add(outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getScanQty(),stockInfoModel.getQty()));
                    outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getLstStockInfo().add(0, stockInfoModel);
                }
                break;
            case 2://箱子
                AddSameLineMaterialNum(scanQty);
                stockInfoModels.get(0).setPickModel(2);
//               outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).
//                       setScanQty(ArithUtil.add(
//                               outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getScanQty() , scanQty));
                outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getLstStockInfo().add(0, stockInfoModels.get(0));
                break;
            case 3: //拆零
                AddSameLineMaterialNum(scanQty);
                stockInfoModels.get(0).setPickModel(3);
//               outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).
//                       setScanQty(ArithUtil.add(outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getScanQty(),scanQty));
                outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getLstStockInfo().add(0, stockInfoModels.get(0));
                break;
        }
//        stockInfoModels=new ArrayList<>();
        currentPickMaterialIndex=FindFirstCanPickMaterial();
        ShowPickMaterialInfo(); //显示下一拣货物料
    }


    void AddSameLineMaterialNum(Float ScanReaminQty){
        for(int i=0;i<SameLineoutStockTaskDetailsInfoModels.size();i++){
            if(ScanReaminQty==0f) break;
            Float remainQty=ArithUtil.sub(SameLineoutStockTaskDetailsInfoModels.get(i).getRemainQty(),SameLineoutStockTaskDetailsInfoModels.get(i).getScanQty());
            Float addQty=remainQty<ScanReaminQty?remainQty:ScanReaminQty;
            SameLineoutStockTaskDetailsInfoModels.get(i)
                    .setScanQty(ArithUtil.add( SameLineoutStockTaskDetailsInfoModels.get(i).getScanQty(),addQty));

            SameLineoutStockTaskDetailsInfoModels.get(i).setVoucherType(99961);
            SameLineoutStockTaskDetailsInfoModels.get(i).setFromErpAreaNo( stockInfoModels.get(0).getAreaNo());
            SameLineoutStockTaskDetailsInfoModels.get(i).setFromErpWarehouse( stockInfoModels.get(0).getWarehouseNo());
            SameLineoutStockTaskDetailsInfoModels.get(i).setFromBatchNo( stockInfoModels.get(0).getBatchNo());

            ScanReaminQty=ArithUtil.sub(ScanReaminQty,addQty);
            remainQty=ArithUtil.sub(SameLineoutStockTaskDetailsInfoModels.get(i).getRemainQty(),SameLineoutStockTaskDetailsInfoModels.get(i).getScanQty());
            if(remainQty==0f)
                SameLineoutStockTaskDetailsInfoModels.get(i).setPickFinish(true);
        }
    }

    void RemoveSameLineMaterialNum(Float ScanReaminQty){
        for(int i=0;i<SameLineoutStockTaskDetailsInfoModels.size();i++){
            if(ScanReaminQty==0f) break;
            Float remainQty= SameLineoutStockTaskDetailsInfoModels.get(i).getScanQty();
            Float removeQty=remainQty<ScanReaminQty?remainQty:ScanReaminQty;
            SameLineoutStockTaskDetailsInfoModels.get(i)
                    .setScanQty(ArithUtil.sub( SameLineoutStockTaskDetailsInfoModels.get(i).getScanQty(),removeQty));
            ScanReaminQty=ArithUtil.sub(ScanReaminQty,removeQty);
            remainQty=ArithUtil.sub(SameLineoutStockTaskDetailsInfoModels.get(i).getRePickQty(),SameLineoutStockTaskDetailsInfoModels.get(i).getScanQty());
            if(remainQty!=0f)
                SameLineoutStockTaskDetailsInfoModels.get(i).setPickFinish(false);
        }
    }

    int TaskDetailesID=0;
    int HouseProp=0;
    /*刷新界面*/
    void ShowPickMaterialInfo(){
        btnOutOfStock.setEnabled(true);
        if(currentPickMaterialIndex!=-1) {
            if (outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getLstStockInfo() == null)
                outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).setLstStockInfo(new ArrayList<StockInfo_Model>());
            OutStockTaskDetailsInfo_Model outStockTaskDetailsInfoModel = outStockTaskDetailsInfoModels.get(currentPickMaterialIndex);
            TaskDetailesID=outStockTaskDetailsInfoModel.getID();
            HouseProp=outStockTaskDetailsInfoModel.getHouseProp();

            txtCompany.setText(outStockTaskDetailsInfoModel.getStrongHoldName());
            txtBatch.setText(outStockTaskDetailsInfoModel.getFromBatchNo());
            txtStatus.setText(outStockTaskDetailsInfoModel.getStrStatus());
            txtMaterialName.setText(outStockTaskDetailsInfoModel.getMaterialDesc());
            txtSugestStock.setText(outStockTaskDetailsInfoModel.getAreaNo());
            txtEDate.setText("");
            //   Float qty = ArithUtil.sub(outStockTaskDetailsInfoModel.getRePickQty(),outStockTaskDetailsInfoModel.getScanQty());
            FindSumQtyByMaterialNo(outStockTaskDetailsInfoModel.getMaterialNo(),outStockTaskDetailsInfoModel.getBatchNo());
            //"库："+outStockTaskDetailsInfoModel.getStockQty() + "/
            // txtOffshelfNum.setText("剩余拣货数：" + SumReaminQty);
            txtcurrentPickNum.setText(SumReaminQty+"");

            BindListVIew(outStockTaskDetailsInfoModels);
        }
        else {
            MessageBox.Show(context, getString(R.string.Error_PickingFinish));
            BindListVIew(outStockTaskDetailsInfoModels);
            CommonUtil.setEditFocus(edtOffShelfScanbarcode);
            GetT_OutTaskDetailListByHeaderIDADF(outStockTaskInfoModels);
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);builder.setTitle("提示");builder.setMessage("拣货完成是否退出界面");
//            builder.setPositiveButton("退出界面", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    backCheckTask();//ymh拣货完毕自动关闭
//                }
//            });
//            builder.setNeutralButton("留在本任务", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//
//                }
//            });
//            builder.show();
        }

    }

    void ShowUnboxing(Boolean show){
        int visiable=show? View.VISIBLE:View.GONE;
        txtUnboxing.setVisibility(visiable);
        edtUnboxing.setVisibility(visiable);
        txtjian.setVisibility(visiable);
        edtjian.setVisibility(visiable);
    }

    /*
    判断物料是否已经扫描
     */
    int CheckBarcodeScaned(){
        for (int i=0;i<outStockTaskDetailsInfoModels.size();i++) {
            if(outStockTaskDetailsInfoModels.get(i).getLstStockInfo()!=null) {
                if (outStockTaskDetailsInfoModels.get(i).getLstStockInfo().indexOf(stockInfoModels.get(0)) != -1) {
                    return i;
                }
            }
        }
        return -1;
    }

    Boolean CheckStockInfo(){
        OutStockTaskDetailsInfo_Model currentOustStock = outStockTaskDetailsInfoModels.get(currentPickMaterialIndex);
        //判断是否拣货完毕
        if (currentOustStock.getRemainQty().compareTo(currentOustStock.getScanQty()) == 0) {
            btnOutOfStock.setEnabled(false);
            MessageBox.Show(context, getString(R.string.Error_MaterialPickFinish));
            CommonUtil.setEditFocus(edtOffShelfScanbarcode);
            return  false;
        }
//        //判断是否指定批次
        if(currentOustStock.getIsSpcBatch().toUpperCase().equals("Y")){
            if(!currentOustStock.getFromBatchNo().equals(stockInfoModels.get(0).getBatchNo())){
                MessageBox.Show(context, getString(R.string.Error_batchNONotMatch)+"|批次号："+currentOustStock.getFromBatchNo());
                CommonUtil.setEditFocus(edtOffShelfScanbarcode);
                return false;
            }
        }
        return true;
    }

    /*分配拣货数量，优先满足第一个拣货数量不满的物料*/
    void DistributionPickingNum(String MaterialNo,Float PickNum){
        for(int i=0;i<outStockTaskDetailsInfoModels.size();i++){
            if(outStockTaskDetailsInfoModels.get(i).getMaterialNo().equals(MaterialNo)){
                Float remainQty=ArithUtil.sub(outStockTaskDetailsInfoModels.get(i).getRemainQty(),outStockTaskDetailsInfoModels.get(i).getScanQty());
                if(remainQty==0f){
                    continue;
                }
                if(PickNum>=remainQty){
                    outStockTaskDetailsInfoModels.get(i).setScanQty(ArithUtil.add(outStockTaskDetailsInfoModels.get(i).getScanQty(),remainQty));
                    PickNum=ArithUtil.sub(PickNum,remainQty);
                }else{
                    outStockTaskDetailsInfoModels.get(i).setScanQty(ArithUtil.add(outStockTaskDetailsInfoModels.get(i).getScanQty(),PickNum));
                    break;
                }

            }
        }
    }
    String ErpVoucherno="";
    /*
    统计相同行物料剩余拣货数量
     */
    void FindSumQtyByMaterialNo(String MaterialNo,String BatchNo){
        SumReaminQty=0.0f;
        ErpVoucherno="";
        SameLineoutStockTaskDetailsInfoModels=new ArrayList<>();
        // for(int i=outStockTaskDetailsInfoModels.size()-1;i>=0;i--){
        for(int i=0;i<outStockTaskDetailsInfoModels.size();i++){
            if(outStockTaskDetailsInfoModels.get(i).getMaterialNo().equals(MaterialNo)&&outStockTaskDetailsInfoModels.get(i).getFromBatchNo().equals(BatchNo)) {
                if (TextUtils.isEmpty(ErpVoucherno))
                    ErpVoucherno = outStockTaskDetailsInfoModels.get(i).getErpVoucherNo();
                if (ErpVoucherno.equals(outStockTaskDetailsInfoModels.get(i).getErpVoucherNo())){
                    SameLineoutStockTaskDetailsInfoModels.add(outStockTaskDetailsInfoModels.get(i));
                    SumReaminQty = ArithUtil.add(SumReaminQty, ArithUtil.sub(outStockTaskDetailsInfoModels.get(i).getRemainQty(), outStockTaskDetailsInfoModels.get(i).getScanQty()));
                }
            }
        }
    }


    /*
    查找需要拣货物料位置，拣货数量为0，且不是缺货状态
     */
    int FindFirstCanPickMaterial(){
        int size=outStockTaskDetailsInfoModels.size();
        MovePickFinishMaterial();
        int index=-1;
        for(int i=0;i<size;i++){
//            if(outStockTaskDetailsInfoModels.get(i).getScanQty()!=null
//            && (outStockTaskDetailsInfoModels.get(i).getScanQty()!=outStockTaskDetailsInfoModels.get(i).getTaskQty()
//            // && ArithUtil.sub(outStockTaskDetailsInfoModels.get(i).getRemainQty(),outStockTaskDetailsInfoModels.get(i).getScanQty())!=0
//             && ArithUtil.sub(outStockTaskDetailsInfoModels.get(i).getRePickQty(),outStockTaskDetailsInfoModels.get(i).getScanQty())!=0
//            ) && !outStockTaskDetailsInfoModels.get(i).getOutOfstock() ){
            if(!outStockTaskDetailsInfoModels.get(i).getPickFinish()
                    && !outStockTaskDetailsInfoModels.get(i).getOutOfstock() ){
                index= i;
                break;
            }
        }
        return index;
    }

    int FindFirstCanPickMaterialByMaterialNo(String MaterialNo,String BatchNo,String warehouseNo){
        int size=outStockTaskDetailsInfoModels.size();
        int index=-1;
        for(int i=0;i<size;i++){
//            if(outStockTaskDetailsInfoModels.get(i).getScanQty()!=null
//                    && (outStockTaskDetailsInfoModels.get(i).getScanQty()!=outStockTaskDetailsInfoModels.get(i).getTaskQty()
//                   // &&ArithUtil.sub(outStockTaskDetailsInfoModels.get(i).getRemainQty(),outStockTaskDetailsInfoModels.get(i).getScanQty())!=0
//                    &&ArithUtil.sub(outStockTaskDetailsInfoModels.get(i).getRePickQty(),outStockTaskDetailsInfoModels.get(i).getScanQty())!=0
//            ) && outStockTaskDetailsInfoModels.get(i).getMaterialNo().equals(MaterialNo)
//                    && outStockTaskDetailsInfoModels.get(i).getStrongHoldCode().equals(StrongHoldCode)){

            if( (!outStockTaskDetailsInfoModels.get(i).getPickFinish())//没有拣货完毕
                    && outStockTaskDetailsInfoModels.get(i).getMaterialNo().equals(MaterialNo)
                    &&outStockTaskDetailsInfoModels.get(i).getFromBatchNo().equals(BatchNo)
//                    && outStockTaskDetailsInfoModels.get(i).getStrongHoldCode().equals(StrongHoldCode)
                    && outStockTaskDetailsInfoModels.get(i).getFromErpWarehouse().equals(warehouseNo)){
                // && outStockTaskDetailsInfoModels.get(i).getHeaderID()==HeadID){
                ErpVoucherno= outStockTaskDetailsInfoModels.get(i).getErpVoucherNo();
                index= i;
                break;
            }
        }
        return index;
    }

    int FindFirstCanPickMaterialByMaterialNoDelete(String MaterialNo,String StrongHoldCode){
        int size=outStockTaskDetailsInfoModels.size();
        int index=-1;
        for(int i=0;i<size;i++){
            if(outStockTaskDetailsInfoModels.get(i).getMaterialNo().equals(MaterialNo)
                    && outStockTaskDetailsInfoModels.get(i).getStrongHoldCode().equals(StrongHoldCode))
            {
                ErpVoucherno= outStockTaskDetailsInfoModels.get(i).getErpVoucherNo();
                index= i;
                break;
            }
        }
        return index;
    }

    private void BindListVIew(ArrayList<OutStockTaskDetailsInfo_Model> outStockTaskDetailsInfoModels) {
        offShelfScanDetailAdapter=new OffShelfScanDetailAdapter(context,outStockTaskDetailsInfoModels);
        lsvPickList.setAdapter(offShelfScanDetailAdapter);
    }

    int MoveIndex=-1;
    /*
   移动拣货完毕物料至末尾
    */
    void MovePickFinishMaterial() {
        // int size = outStockTaskDetailsInfoModels.size();

        for (int i = 0; i < MoveIndex; i++) {
            if (outStockTaskDetailsInfoModels.get(i).getPickFinish()) {
                Collections.swap(outStockTaskDetailsInfoModels, i, MoveIndex - 1);
                MoveIndex = MoveIndex - 1;
            }

            //}
        }

    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_UP) {

            if (BoxingModels != null && BoxingModels.size() > 0) {
                MessageBox.Show(context, "有清单没被打印，先打印清单再退出！");
                return true;
            }

            if(BaseApplication.isCloseActivity){
                backCheckTask();
            }else{
                BackAlter();
            }
        }
        if(keyCode== KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return true;
    }


    public void BackAlter(){
        new AlertDialog.Builder(context).setTitle("提示").setCancelable(false).setIcon(android.R.drawable.ic_dialog_info).setMessage("是否返回上一页面？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 自动生成的方法
                        backCheckTask();
                    }
                }).setNegativeButton("取消", null).show();
    }


    public void backCheckTask(){
        //解表
        Map<String, String> params = new HashMap<>();
        String UserModel = GsonUtil.parseModelToJson(BaseApplication.userInfo);
        String TaskOutStockModelJson = GsonUtil.parseModelToJson(outStockTaskInfoModels.get(0));
        params.put("UserJson", UserModel);
        params.put("TaskOutStockModelJson", TaskOutStockModelJson);
        LogUtil.WriteLog(OffShelfBillChoice.class, TAG_UnLockTaskOperUserADF, UserModel);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_UnLockTaskOperUserADF, "查看权限中", context, mHandler, RESULT_UnLockTaskOperUserADF, null, URLModel.GetURL().UnLockTaskOperUser, params, null);
    }


}
