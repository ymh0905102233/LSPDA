package com.xx.chinetek.cywms.Bu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.adapter.wms.OffShelf.OffShelfScanDetailAdapter;
import com.xx.chinetek.base.BaseActivity;
import com.xx.chinetek.base.BaseApplication;
import com.xx.chinetek.base.ToolBarTitle;
import com.xx.chinetek.cywms.Query.Query;
import com.xx.chinetek.cywms.R;
import com.xx.chinetek.cywms.Truck.TruckLoad;
import com.xx.chinetek.model.Base_Model;
import com.xx.chinetek.model.ReturnMsgModel;
import com.xx.chinetek.model.ReturnMsgModelList;
import com.xx.chinetek.model.URLModel;
import com.xx.chinetek.model.WMS.OffShelf.OutStockTaskDetailsInfo_Model;
import com.xx.chinetek.model.WMS.OffShelf.OutStockTaskInfo_Model;
import com.xx.chinetek.model.WMS.Review.OutStockDetailInfo_Model;
import com.xx.chinetek.model.WMS.Review.OutStock_Model;
import com.xx.chinetek.model.WMS.Stock.AreaInfo_Model;
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

import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xx.chinetek.util.function.GsonUtil.parseModelToJson;


@ContentView(R.layout.activity_offshelf_scan)
public class BuScan extends BaseActivity {

    String TAG_GetT_OutTaskDetailListByHeaderIDADF="OffshelfScan_Single_GetT_OutTaskDetailListByHeaderIDADF";
    String TAG_GetStockModelADF="OffshelfScan_Single_GetStockModelADF";
    String TAG_SaveT_OutStockTaskDetailADF="OffshelfScan_Single_SaveT_OutStockTaskDetailADF";
    String TAG_SaveT_BarCodeToStockADF="OffshelfScan_Single_SaveT_BarCodeToStockADF";
    String TAG_SaveT_SingleErpvoucherADF="OffshelfScan_Single_OutStockReviewDetailADF";
    String TAG_SaveT_OutStockReviewPalletDetailADF="OffshelfScan_SaveT_OutStockReviewPalletDetailADF";
    String TAG_SaveT_OutStockReviewPalletDetailForLanyaADF="OffshelfScan_SaveT_OutStockReviewPalletDetailForLanyaADF";
    String TAG_SaveT_BarCodeToStockLanyaADF= "Boxing_SaveT_BarCodeToStockLanyaADF";


    private final int RESULT_SaveT_BarCodeToStockLanyaADF = 108;


    private final int RESULT_Msg_GetT_OutTaskDetailListByHeaderIDADF=101;
    private final int RESULT_Msg_GetStockModelADF=102;
    private final int RESULT_Msg_SaveT_OutStockTaskDetailADF=103;
    private final int RESULT_SaveT_BarCodeToStockADF = 104;
    private final int RESULT_SaveT_SingleErpvoucherADF = 105;
//    private final int RESULT_Msg_SaveT_OutStockReviewPalletDetailADF=106;
//    private final int RESULT_Msg_SaveT_OutStockReviewPalletDetailForLanyaADF=107;


    String TAG_GetAreano="TAG_GetAreano";
    private final int RESULT_GetAreano = 110;

    String TAG_GetCar="TAG_GetCar";
    private final int RESULT_GetCar = 112;


    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
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
            case  RESULT_SaveT_SingleErpvoucherADF:
                AnalysisSaveT_SingleErpvoucherADF((String) msg.obj);
                break;
//            case RESULT_Msg_SaveT_OutStockReviewPalletDetailADF:
//                AnalysisetT_SaveT_OutStockReviewPalletDetailADFJson((String) msg.obj);
//                break;
//            case RESULT_Msg_SaveT_OutStockReviewPalletDetailForLanyaADF:
//                AnalysisetT_SaveT_OutStockReviewPalletDetailForLanyaADF((String) msg.obj);
//                break;
//            case RESULT_SaveT_BarCodeToStockLanyaADF:
//                AnalysisSaveT_BarCodeToStockADF((String) msg.obj);
//                break;
            case RESULT_GetAreano:
                AnalysisGetAreanoADF((String) msg.obj);
                break;

            case RESULT_GetCar:
                AnalysisGetCarADF((String) msg.obj);
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
//    @ViewInject(tb_UnboxType)
//    ToggleButton tbUnboxType;
//    @ViewInject(R.id.tb_PalletType)
//    ToggleButton tbPalletType;
//    @ViewInject(R.id.tb_BoxType)
//    ToggleButton tbBoxType;
    @ViewInject(R.id.edt_OffShelfScanbarcode)
    EditText edtOffShelfScanbarcode;
    @ViewInject(R.id.edt_Unboxing)
    EditText edtUnboxing;
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
    @ViewInject(R.id.txt_getbatch)
    TextView txtgetbatch;
    @ViewInject(R.id.textView133)
    TextView textView133;
    @ViewInject(R.id.txterpvoucherno)
    TextView txterpvoucherno;

    ArrayList<OutStockTaskInfo_Model> outStockTaskInfoModels;
    ArrayList<OutStockTaskDetailsInfo_Model> outStockTaskDetailsInfoModels;
    ArrayList<OutStockTaskDetailsInfo_Model> SameLineoutStockTaskDetailsInfoModels; //相同行物料集合


    List<StockInfo_Model> stockInfoModels;//扫描条码
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
        BaseApplication.toolBarTitle = new ToolBarTitle( getString(R.string.OffShelf_subtitle), true);
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
//        String UserJson=GsonUtil.parseModelToJson(BaseApplication.userInfo);
    }



    @Event(value ={R.id.tb_UnboxType,R.id.tb_PalletType,R.id.tb_BoxType} ,type = CompoundButton.OnClickListener.class)
    private void TBonCheckedChanged(View view) {
//        tbUnboxType.setChecked(view.getId()== R.id.tb_UnboxType);
//        tbPalletType.setChecked(view.getId()== R.id.tb_PalletType);
//        tbBoxType.setChecked(view.getId()== R.id.tb_BoxType);
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
//                    CheckNumRefMaterial checkNumRefMaterial = CheckMaterialNumFormat(num, stockInfoModels.get(0).getUnitTypeCode(), stockInfoModels.get(0).getDecimalLngth());
//                    if (!checkNumRefMaterial.ischeck()) {
//                        MessageBox.Show(context, checkNumRefMaterial.getErrMsg());
//                        CommonUtil.setEditFocus(edtUnboxing);
//                        return true;
//                    }
                    if (stockInfoModels==null||stockInfoModels.size()==0){
                        MessageBox.Show(context, "先扫描条码选择批次");
                        return true;
                    }

                    Float qty = Float.parseFloat(num); //输入数量
                    StockInfo_Model newmodel= new StockInfo_Model();
                    if(edtOffShelfScanbarcode.getText().toString().contains("@")){
                        newmodel = stockInfoModels.get(0);
                    }else{
                        if(stockInfoModels.size()==1){
                            newmodel = stockInfoModels.get(0);
                        }else{
                            newmodel = stockInfoModels.get(BatchType);
                        }
                    }
                    Float scanQty = newmodel.getQty(); //箱数量
                    if (qty >scanQty) {
                        MessageBox.Show(context, getString(R.string.Error_PackageQtyBiger));
                        CommonUtil.setEditFocus(edtUnboxing);
                        return true;
                    }
                    //ymh整箱发货
                    if (CommonUtil.EqualFloat(qty ,scanQty) ) {
                        stockInfoModels = new ArrayList<>();
                        stockInfoModels.add(newmodel);
                        currentPickMaterialIndex = FindFirstCanPickMaterialByMaterialNo(stockInfoModels.get(0).getMaterialNo(), stockInfoModels.get(0).getStrongHoldCode(),stockInfoModels.get(0).getWarehouseNo());
                        if (currentPickMaterialIndex != -1) {
//                            if (SumReaminQty < qty) {//qty > remainqty ||
//                                MessageBox.Show(context, getString(R.string.Error_offshelfQtyBiger)
//                                        +"\n需下架数量："+SumReaminQty
//                                        +"\n扫描数量："+qty
//                                        +"\n拆零后剩余数量："+ArithUtil.sub(qty,SumReaminQty));
//                                CommonUtil.setEditFocus(edtUnboxing);
//                                return true;
//                            }

                            if (CheckStockInfo()) {  //判断是否拣货完毕、是否指定批次
                                ShowPickMaterialInfo();
                                txtEDate.setText(CommonUtil.DateToString(stockInfoModels.get(0).getEDate()));
                                txtStatus.setText(stockInfoModels.get(0).getStrStatus());
                                SetOutStockTaskDetailsInfoModels(newmodel.getQty(), 3);

                                if(!checkdetail(outStockTaskDetailsInfoModels)){
                                    MessageBox.Show(context,"提交的数据异常，退出重新扫描！");
                                    return true;
                                }

                                for (int j=0;j<outStockTaskDetailsInfoModels.size();j++){
                                    outStockTaskDetailsInfoModels.get(j).setVoucherType(3);
                                }

                                //提交数据
                                final Map<String, String> params = new HashMap<String, String>();
                                String ModelJson = GsonUtil.parseModelToJson(outStockTaskDetailsInfoModels);
                                String UserJson=GsonUtil.parseModelToJson(BaseApplication.userInfo);
                                params.put("UserJson",UserJson );
                                params.put("ModelJson", ModelJson);
                                LogUtil.WriteLog(BuScan.class, TAG_SaveT_OutStockTaskDetailADF, ModelJson);
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
                            if(!CheckBluetooth()){
                                MessageBox.Show(context, "蓝牙打印机连接失败");
                                return true;
                            }
                        }

//                    Float remainqty = ArithUtil.sub(outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getRePickQty(),
//                            outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getScanQty());
//                        if (SumReaminQty < qty) {//qty > remainqty ||
//                            MessageBox.Show(context, getString(R.string.Error_offshelfQtyBiger)
//                                    +"\n需下架数量："+SumReaminQty
//                                    +"\n扫描数量："+qty
//                                    +"\n拆零后剩余数量："+ArithUtil.sub(qty,SumReaminQty));
//                            CommonUtil.setEditFocus(edtUnboxing);
//                            return true;
//                        }

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
                        LogUtil.WriteLog(BuScan.class, TAG_SaveT_BarCodeToStockADF, strOldBarCode);
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
            String code=edtOffShelfScanbarcode.getText().toString().trim();
            final Map<String, String> params = new HashMap<String, String>();
            StockInfo_Model model = new StockInfo_Model();
            model.setHouseProp(outStockTaskDetailsInfoModels.get(0).getHouseProp());
            if (!code.contains("@")){
                if(AreaModel==null){
                    MessageBox.Show(context,getString(R.string.Error_StockInCorrect));
                    return true;
                }
                model.setBarcode(code);
                model.setScanType(2);
                model.setAreaID(AreaModel.getID());
                model.setWareHouseID(AreaModel.getWarehouseID());
            }else{
                model.setBarcode(code);
                model.setScanType(2);
            }
            String Json=GsonUtil.parseModelToJson(model);
            params.put("ModelStockJson",Json);
            LogUtil.WriteLog(BuScan.class, TAG_GetStockModelADF, code);
            RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetStockModelADF, getString(R.string.Msg_GetT_SerialNoByPalletADF), context, mHandler, RESULT_Msg_GetStockModelADF, null, URLModel.GetURL().GetStockModelADF, params, null);
        }
        return false;
    }


    @Event(value =R.id.edt_StockScan,type = View.OnKeyListener.class)
    private  boolean edtStockScanClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            keyBoardCancle();
            String areaNo=edtStockScan.getText().toString().trim();
            if (!TextUtils.isEmpty(areaNo)) {
                final Map<String, String> params = new HashMap<String, String>();
                String UserJson=GsonUtil.parseModelToJson(BaseApplication.userInfo);
                params.put("UserJson", UserJson);
                params.put("AreaNo", areaNo);
                String para = (new JSONObject(params)).toString();
                LogUtil.WriteLog(BuScan.class, TAG_GetAreano, para);
                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetAreano, getString(R.string.Msg_GetAreanobyCheckno2), context, mHandler, RESULT_GetAreano, null, URLModel.GetURL().GetAreano, params, null);
            }

        }
        return false;
    }


    @Event(value =R.id.edt_car,type = View.OnKeyListener.class)
    private  boolean edtcarClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            keyBoardCancle();
            String car=edtcar.getText().toString().trim();
            if (!TextUtils.isEmpty(car)) {
                final Map<String, String> params = new HashMap<String, String>();
                String UserJson=GsonUtil.parseModelToJson(BaseApplication.userInfo);
                params.put("strUserNo", UserJson);
                params.put("TaskNo", TaskNo);
                params.put("CarNo", car);
                String para = (new JSONObject(params)).toString();
                LogUtil.WriteLog(BuScan.class, TAG_GetCar, para);
                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetCar, getString(R.string.Msg_GetAreanobyCheckno2), context, mHandler, RESULT_GetCar, null, URLModel.GetURL().GetCarModelADF, params, null);
            }

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


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_receiptbilldetail, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_filter) {
//            if (DoubleClickCheck.isFastDoubleClick(context)) {
//                return false;
//            }
//            final Map<String, String> params = new HashMap<String, String>();
//            String ModelJson = GsonUtil.parseModelToJson(outStockTaskDetailsInfoModels);
//            String UserJson=GsonUtil.parseModelToJson(BaseApplication.userInfo);
//            params.put("UserJson",UserJson );
//            params.put("ModelJson", ModelJson);
//            LogUtil.WriteLog(OffshelfScan.class, TAG_SaveT_OutStockTaskDetailADF, ModelJson);
//            RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SaveT_OutStockTaskDetailADF, getString(R.string.Msg_SaveT_OutStockTaskDetailADF), context, mHandler, RESULT_Msg_SaveT_OutStockTaskDetailADF, null,  URLModel.GetURL().SaveT_OutStockTaskDetailADF, params, null);
//        }
//        return super.onOptionsItemSelected(item);
//    }


    /*
    下架明细获取
     */
    void GetT_OutTaskDetailListByHeaderIDADF(ArrayList<OutStockTaskInfo_Model> outStockTaskInfoModels){
        if(outStockTaskInfoModels!=null) {
            IsEdate=outStockTaskInfoModels.get(0).getIsEdate();
            final Map<String, String> params = new HashMap<String, String>();
            String modelJson= parseModelToJson(outStockTaskInfoModels);
            params.put("ModelDetailJson",modelJson);
            LogUtil.WriteLog(BuScan.class, TAG_GetT_OutTaskDetailListByHeaderIDADF, modelJson);
            RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetT_OutTaskDetailListByHeaderIDADF, getString(R.string.Msg_QualityDetailListByHeaderIDADF), context, mHandler, RESULT_Msg_GetT_OutTaskDetailListByHeaderIDADF, null,  URLModel.GetURL().GetT_OutTaskDetailListByHeaderIDADF, params, null);
        }
    }




    /*处理下架明细*/
    void AnalysisGetT_OutTaskDetailListByHeaderIDADFJson(String result){
        LogUtil.WriteLog(BuScan.class, TAG_GetT_OutTaskDetailListByHeaderIDADF,result);
        try {
            ReturnMsgModelList<OutStockTaskDetailsInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<OutStockTaskDetailsInfo_Model>>() {
            }.getType());
            outStockTaskDetailsInfoModels=new ArrayList<>();
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                outStockTaskDetailsInfoModels = returnMsgModel.getModelJson();
                int size=outStockTaskDetailsInfoModels.size();
                MoveIndex=size;
                //处理拣货数量为0的
                for(int i=0;i<size;i++) {
                    if(ArithUtil.sub(outStockTaskDetailsInfoModels.get(i).getRePickQty(),
                            outStockTaskDetailsInfoModels.get(i).getScanQty())==0f)
                        Collections.swap(outStockTaskDetailsInfoModels, i, size-1);
                }
                currentPickMaterialIndex = FindFirstCanPickMaterial(); //查找需要拣货物料行
                ShowPickMaterialInfo();//显示需要拣货物料

                //光标定位
                if (outStockTaskDetailsInfoModels.get(0).getHouseProp()==2){
                    CommonUtil.setEditFocus(edtcar);
                }else{
                    edtcar.setVisibility(View.INVISIBLE);
                    textView133.setVisibility(View.INVISIBLE);
                    CommonUtil.setEditFocus(edtOffShelfScanbarcode);
                }

            } else {
                MessageBox.Show(context, returnMsgModel.getMessage());
            }
        }catch (Exception ex){
            MessageBox.Show(context,ex.getMessage());
        }
    }

    /*
    扫描条码
     */
    void AnalysisGetStockModelADFJson(String result) {
        LogUtil.WriteLog(BuScan.class, TAG_GetStockModelADF, result);
        try {
            ReturnMsgModelList<StockInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<StockInfo_Model>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                stockInfoModels = returnMsgModel.getModelJson();
                if (stockInfoModels != null && stockInfoModels.size() != 0) {
                    //判断是扫描EAN还是条码
                    if (edtOffShelfScanbarcode.getText().toString().trim().contains("@")){
                        insertStockInfo();
                    }else{
                        if (stockInfoModels.size()>1){
                            Batchs = new String[stockInfoModels.size()];
                            for (int i=0;i<stockInfoModels.size();i++){
                                Batchs[i]=stockInfoModels.get(i).getBatchNo()+","+stockInfoModels.get(i).getMaterialNo();
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("选择批次和物料");
                            builder.setItems(Batchs, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    String getbatch ="";
                                    getbatch =Batchs[which].toString();
                                    txtgetbatch.setText(getbatch);
                                    BatchType=which;


                                    //扫描69码得到的实体类
                                    stockInfoModelsnew =stockInfoModels;
                                    StockInfo_Model stockInfoModel= stockInfoModels.get(which);
                                    stockInfoModels = new ArrayList<>();
                                    stockInfoModels.add(stockInfoModel);
                                    insertStockInfo();
                                    stockInfoModels = stockInfoModelsnew;
                                }
                            });
                            builder.show();


                        }else{
                            txtgetbatch.setText(stockInfoModels.get(0).getBatchNo()+","+stockInfoModels.get(0).getMaterialNo());
                            //直接调用
                            insertStockInfo();
                        }



                    }


                    //判断条码是否已经扫描
//                    final int index = CheckBarcodeScaned();
//                    if (index == -1) {
//                        insertStockInfo();
//                    } else {
//                        MessageBox.Show(context, getString(R.string.Error_Barcode_hasScan));
//                        CommonUtil.setEditFocus(edtOffShelfScanbarcode);
//                        //RemoveStockInfo(index);
//                    }
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

    void AnalysisSaveT_OutStockTaskDetailADFJson(String result){
        try {
            LogUtil.WriteLog(BuScan.class, TAG_SaveT_OutStockTaskDetailADF,result);
            ReturnMsgModelList<Base_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<Base_Model>>() {}.getType());
            if(returnMsgModel.getHeaderStatus().equals("S")){
                clearFrm();
                GetT_OutTaskDetailListByHeaderIDADF(outStockTaskInfoModels);

                if (outStockTaskDetailsInfoModels.get(0).getHouseProp()==2){
                    CommonUtil.setEditFocus(edtStockScan);
                }else{
                    edtcar.setVisibility(View.INVISIBLE);
                    textView133.setVisibility(View.INVISIBLE);
                    CommonUtil.setEditFocus(edtOffShelfScanbarcode);
                }
//                new AlertDialog.Builder(context).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage(returnMsgModel.getMessage())
//                        .setCancelable(false)
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // TODO 自动生成的方法
////
//                            }
//                        }).show();
            }else {
                MessageBox.Show(context, returnMsgModel.getMessage());
            }
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
            LogUtil.WriteLog(BuScan.class,"error",ex.getMessage());
        }
    }





//    @Event(R.id.but_Pallet)
//    private void btnCombinepalletClick(View view){
//        if (DoubleClickCheck.isFastDoubleClick(context)) {
//            return;
//        }
//        ArrayList<OutStockDetailInfo_Model> palletDetailModels=GetPalletModels();
//        if(palletDetailModels.size()!=0){
//            final Map<String, String> params = new HashMap<String, String>();
//            String ModelJson = parseModelToJson(palletDetailModels);
//            params.put("UserJson", parseModelToJson(BaseApplication.userInfo));
//            params.put("ModelJson", ModelJson);
//            LogUtil.WriteLog(OffshelfScan.class, TAG_SaveT_OutStockReviewPalletDetailADF, ModelJson);
//
//            if (URLModel.isSupplier){
//                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SaveT_OutStockReviewPalletDetailForLanyaADF, getString(R.string.Msg_SaveT_PalletDetailADF), context, mHandler, RESULT_Msg_SaveT_OutStockReviewPalletDetailForLanyaADF, null,  URLModel.GetURL().SaveT_OutStockReviewPalletDetailForLanyaADF, params, null);
//            }else {
//                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SaveT_OutStockReviewPalletDetailADF, getString(R.string.Msg_SaveT_PalletDetailADF), context, mHandler, RESULT_Msg_SaveT_OutStockReviewPalletDetailADF, null,  URLModel.GetURL().SaveT_OutStockReviewPalletDetailADF, params, null);
//            }
//
//        }
//    }


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

AreaInfo_Model AreaModel;
    void AnalysisGetAreanoADF(String result){
        LogUtil.WriteLog(BuScan.class, TAG_GetAreano,result);
        ReturnMsgModel<AreaInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<AreaInfo_Model>>() {}.getType());
        if(returnMsgModel.getHeaderStatus().equals("S")){
            AreaModel=returnMsgModel.getModelJson();
            //判断库位是否和单据一致
            if (!AreaModel.getHouseProp().equals(outStockTaskDetailsInfoModels.get(0).getHouseProp())){
                MessageBox.Show(context,"扫描的储位所属仓库属性和单据仓库属性不一致！");
                AreaModel= new AreaInfo_Model();
            }
            CommonUtil.setEditFocus(edtOffShelfScanbarcode);
        }else{
            MessageBox.Show(context,returnMsgModel.getMessage());
        }
    }


    void AnalysisGetCarADF(String result){
        LogUtil.WriteLog(BuScan.class, TAG_GetCar,result);
        ReturnMsgModel<OutStockTaskDetailsInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<OutStockTaskDetailsInfo_Model>>() {}.getType());
        if(returnMsgModel.getHeaderStatus().equals("S")){
            CommonUtil.setEditFocus(edtStockScan);
        }else{
            MessageBox.Show(context,returnMsgModel.getMessage());
        }
    }

    String[] Batchs;
    int BatchType=-1;
    List<StockInfo_Model> stockInfoModelsnew =new ArrayList<>();
    @Event(value = R.id.txt_getbatch,type =View.OnClickListener.class )
    private void txtgetbatch(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("选择批次和物料");
        builder.setItems(Batchs, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String getbatch ="";
                getbatch =Batchs[which].toString();
                txtgetbatch.setText(getbatch);
                BatchType=which;


                //扫描69码得到的实体类
               stockInfoModelsnew =stockInfoModels;
                StockInfo_Model stockInfoModel= stockInfoModels.get(which);
                stockInfoModels = new ArrayList<>();
                stockInfoModels.add(stockInfoModel);
                insertStockInfo();
                stockInfoModels = stockInfoModelsnew;
            }
        });
        builder.show();

    }

    /*拆箱提交*/
    void AnalysisSaveT_BarCodeToStockADF(String result) {
        try {
            LogUtil.WriteLog(BuScan.class, TAG_SaveT_BarCodeToStockADF, result);
            ReturnMsgModel<StockInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<StockInfo_Model>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                StockInfo_Model stockInfoModel = returnMsgModel.getModelJson();
                //打印拆零标签
                if (edtOffShelfScanbarcode.getText().toString().contains("@")){
                    stockInfoModel.setSN(txterpvoucherno.getText().toString());
//                    LPK130DEMO(stockInfoModel,"Jian");
                }
                stockInfoModels = new ArrayList<>();
                stockInfoModels.add(stockInfoModel);
                currentPickMaterialIndex = FindFirstCanPickMaterialByMaterialNo(stockInfoModels.get(0).getMaterialNo(), stockInfoModels.get(0).getStrongHoldCode(),stockInfoModels.get(0).getWarehouseNo());
                if (currentPickMaterialIndex != -1) {
                    if (CheckStockInfo()) {  //判断是否拣货完毕、是否指定批次
                        ShowPickMaterialInfo();
                        txtEDate.setText(CommonUtil.DateToString(stockInfoModels.get(0).getEDate()));
                        txtStatus.setText(stockInfoModels.get(0).getStrStatus());
                        SetOutStockTaskDetailsInfoModels(stockInfoModel.getQty(), 3);

                        if(!checkdetail(outStockTaskDetailsInfoModels)){
                            MessageBox.Show(context,"拆零完成，提交的数据异常，退出重新扫描！");
                            return;
                        }
                        for (int j=0;j<outStockTaskDetailsInfoModels.size();j++){
                            outStockTaskDetailsInfoModels.get(j).setVoucherType(3);
                        }
                        //提交数据
                        final Map<String, String> params = new HashMap<String, String>();
                        String ModelJson = GsonUtil.parseModelToJson(outStockTaskDetailsInfoModels);
                        String UserJson=GsonUtil.parseModelToJson(BaseApplication.userInfo);
                        params.put("UserJson",UserJson );
                        params.put("ModelJson", ModelJson);
                        LogUtil.WriteLog(BuScan.class, TAG_SaveT_OutStockTaskDetailADF, ModelJson);
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
        int count=0;
        if (models!=null&&models.size()>0){
            for (int i=0;i<models.size();i++){
                if (models.get(i).getLstStockInfo()!=null&&models.get(i).getLstStockInfo().size()>0){
                    count=count+models.get(i).getLstStockInfo().size();
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
        LogUtil.WriteLog(BuScan.class, TAG_SaveT_SingleErpvoucherADF,result);
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
        currentPickMaterialIndex=FindFirstCanPickMaterialByMaterialNo(stockInfoModels.get(0).getMaterialNo(), stockInfoModels.get(0).getStrongHoldCode(),stockInfoModels.get(0).getWarehouseNo());
        if (currentPickMaterialIndex != -1) {
            if (CheckStockInfo()) {  //判断是否拣货完毕、是否指定批次
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
//        if (SumReaminQty<scanQty ) {
//            MessageBox.Show(context, getString(R.string.Error_offshelfQtyBiger)
//                    +"\n需下架数量："+SumReaminQty
//                    +"\n扫描数量："+scanQty
//                    +"\n拆零后剩余数量："+ArithUtil.sub(scanQty,SumReaminQty));
//            stockInfoModels=new ArrayList<>();
//            CommonUtil.setEditFocus(edtOffShelfScanbarcode);
//            return;
//        }
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
        txtgetbatch.setText("");
        edtStockScan.setText("");
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
        stockInfoModels=new ArrayList<>();
        currentPickMaterialIndex=FindFirstCanPickMaterial();
        ShowPickMaterialInfo(); //显示下一拣货物料
    }


    void AddSameLineMaterialNum(Float ScanReaminQty){
        for(int i=0;i<SameLineoutStockTaskDetailsInfoModels.size();i++){
            if(ScanReaminQty==0f) break;
            Float remainQty=ArithUtil.sub(SameLineoutStockTaskDetailsInfoModels.get(i).getRePickQty(),SameLineoutStockTaskDetailsInfoModels.get(i).getScanQty());
            Float addQty=remainQty<ScanReaminQty?remainQty:ScanReaminQty;
            SameLineoutStockTaskDetailsInfoModels.get(i)
                    .setScanQty(ArithUtil.add( SameLineoutStockTaskDetailsInfoModels.get(i).getScanQty(),addQty));

//            SameLineoutStockTaskDetailsInfoModels.get(i).setVoucherType(99961);
            SameLineoutStockTaskDetailsInfoModels.get(i).setFromErpAreaNo( stockInfoModels.get(0).getAreaNo());
            SameLineoutStockTaskDetailsInfoModels.get(i).setFromErpWarehouse( stockInfoModels.get(0).getWarehouseNo());
            SameLineoutStockTaskDetailsInfoModels.get(i).setFromBatchNo( stockInfoModels.get(0).getBatchNo());

            ScanReaminQty=ArithUtil.sub(ScanReaminQty,addQty);
            remainQty=ArithUtil.sub(SameLineoutStockTaskDetailsInfoModels.get(i).getRePickQty(),SameLineoutStockTaskDetailsInfoModels.get(i).getScanQty());
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
            FindSumQtyByMaterialNo(outStockTaskDetailsInfoModel.getMaterialNo());
            //"库："+outStockTaskDetailsInfoModel.getStockQty() + "/
            // txtOffshelfNum.setText("剩余拣货数：" + SumReaminQty);
            txtcurrentPickNum.setText(SumReaminQty+"");

            BindListVIew(outStockTaskDetailsInfoModels);
        }
        else {
            MessageBox.Show(context, getString(R.string.Error_PickingFinish));
            BindListVIew(outStockTaskDetailsInfoModels);
            CommonUtil.setEditFocus(edtOffShelfScanbarcode);
        }

    }

    void ShowUnboxing(Boolean show){
        int visiable=show? View.VISIBLE:View.GONE;
        txtUnboxing.setVisibility(visiable);
        edtUnboxing.setVisibility(visiable);
        // btnPrintBox.setVisibility(visiable);
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
        if (currentOustStock.getRePickQty().compareTo(currentOustStock.getScanQty()) == 0) {
            btnOutOfStock.setEnabled(false);
            MessageBox.Show(context, getString(R.string.Error_MaterialPickFinish));
            CommonUtil.setEditFocus(edtOffShelfScanbarcode);
            return  false;
        }
//        //判断是否指定批次
//        if(currentOustStock.getIsSpcBatch().toUpperCase().equals("Y")){
//            if(!currentOustStock.getFromBatchNo().equals(stockInfoModels.get(0).getBatchNo())){
//                MessageBox.Show(context, getString(R.string.Error_batchNONotMatch)+"|批次号："+currentOustStock.getFromBatchNo());
//                CommonUtil.setEditFocus(edtOffShelfScanbarcode);
//                return false;
//            }
//        }
        return true;
    }

    /*
    分配拣货数量，优先满足第一个拣货数量不满的物料
     */
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
    void FindSumQtyByMaterialNo(String MaterialNo){
        SumReaminQty=0.0f;
        ErpVoucherno="";
        SameLineoutStockTaskDetailsInfoModels=new ArrayList<>();
        // for(int i=outStockTaskDetailsInfoModels.size()-1;i>=0;i--){
        for(int i=0;i<outStockTaskDetailsInfoModels.size();i++){
            if(outStockTaskDetailsInfoModels.get(i).getMaterialNo().equals(MaterialNo)) {
                if (TextUtils.isEmpty(ErpVoucherno))
                    ErpVoucherno = outStockTaskDetailsInfoModels.get(i).getErpVoucherNo();
                if (ErpVoucherno.equals(outStockTaskDetailsInfoModels.get(i).getErpVoucherNo())){
                    SameLineoutStockTaskDetailsInfoModels.add(outStockTaskDetailsInfoModels.get(i));
                    SumReaminQty = ArithUtil.add(SumReaminQty, ArithUtil.sub(outStockTaskDetailsInfoModels.get(i).getRePickQty(), outStockTaskDetailsInfoModels.get(i).getScanQty()));
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

    int FindFirstCanPickMaterialByMaterialNo(String MaterialNo,String StrongHoldCode,String warehouseNo){
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
                    && outStockTaskDetailsInfoModels.get(i).getStrongHoldCode().equals(StrongHoldCode)
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
        }
    }

}
