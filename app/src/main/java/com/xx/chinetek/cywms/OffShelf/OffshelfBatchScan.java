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
import android.widget.Button;
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
import com.xx.chinetek.cywms.SplitZero.SplitZeroScan;
import com.xx.chinetek.cywms.Truck.TruckLoad;
import com.xx.chinetek.model.Base_Model;
import com.xx.chinetek.model.Box.Boxing;
import com.xx.chinetek.model.ReturnMsgModel;
import com.xx.chinetek.model.ReturnMsgModelList;
import com.xx.chinetek.model.URLModel;
import com.xx.chinetek.model.WMS.OffShelf.OutStockTaskDetailsInfo_Model;
import com.xx.chinetek.model.WMS.OffShelf.OutStockTaskInfo_Model;
import com.xx.chinetek.model.WMS.Print.PrintBean;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.xx.chinetek.cywms.R.id.tb_UnboxType;
import static com.xx.chinetek.util.function.GsonUtil.parseModelToJson;

/**
 * @desc: 批量下架
 * @param:
 * @return:
 * @author: Nietzsche
 * @time 2020/4/20 20:47
 */
@ContentView(R.layout.activity_offshelf_scan)
public class OffshelfBatchScan extends BaseActivity {

    String TAG_GetT_OutTaskDetailListByHeaderIDADF = "OffshelfScan_Single_GetT_OutTaskDetailListByHeaderIDADF";
    String TAG_GetStockModelADF                    = "OffshelfScan_Single_GetStockModelADF";
    String TAG_SaveT_OutStockTaskDetailADF         = "OffshelfScan_Single_SaveT_OutStockTaskDetailADF";
    String TAG_SaveT_BarCodeToStockADF             = "OffshelfScan_Single_SaveT_BarCodeToStockADF";
    String TAG_SaveT_SingleErpvoucherADF           = "OffshelfScan_Single_OutStockReviewDetailADF";
    String TAG_UnLockTaskOperUserADF               = "OffShelfBillChoice_UnLockTaskOperUserADF";
    String TAG_GetBarcodeModelForJADF              = "OffshelfScan_TAG_GetBarcodeModelForJADF";
    private final int RESULT_UnLockTaskOperUserADF        = 109;
    private final int RESULT_SaveT_BarCodeToStockLanyaADF = 108;


    private final int RESULT_Msg_GetT_OutTaskDetailListByHeaderIDADF = 101;
    private final int RESULT_Msg_GetStockModelADF                    = 102;
    private final int RESULT_Msg_SaveT_OutStockTaskDetailADF         = 103;
    private final int RESULT_SaveT_BarCodeToStockADF                 = 104;
    private final int result_msg_GetBarcodeModelForJADF              = 105;


    String TAG_GetStockModelFrojianADF = "OffshelfScan_Single_GetStockModelFrojianADF";
    private final int RESULT_Msg_GetStockModelFrojianADF = 110;


    String TAG_PrintListADF = "OffshelfScan_PrintListADFADF";
    private final int RESULT_Msg_PrintListADF = 111;

    String TAG_PrintListADF1 = "OffshelfScan_PrintListADFADF";
    private final int RESULT_Msg_PrintListADF1 = 112;
    ArrayList<StockInfo_Model> stockin = new ArrayList<StockInfo_Model>();//扫描内盒的条码
    UUID                       mUuid   = null;
    OffshelfBatchScanModel     mModel;

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
            case result_msg_GetBarcodeModelForJADF:
                AnalysisGetBarcodeModelForJADFJson((String) msg.obj);
                break;
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
                ToastUtil.show("获取请求失败_____" + msg.obj);
                CommonUtil.setEditFocus(mBarcode);
                break;
        }
    }

    Context context = this;
    @ViewInject(R.id.txt_Company)
    TextView     txtCompany;
    @ViewInject(R.id.txt_Batch)
    TextView     txtBatch;
    @ViewInject(R.id.txt_Status)
    TextView     mQty;
    @ViewInject(R.id.txt_EDate)
    TextView     txtEDate;
    @ViewInject(R.id.txt_MaterialName)
    TextView     txtMaterialName;
    @ViewInject(tb_UnboxType)
    ToggleButton tbUnboxType;
    @ViewInject(R.id.tb_BoxType)
    ToggleButton tbBoxType;
    @ViewInject(R.id.edt_OffShelfScanbarcode)
    EditText     mBarcode;
    @ViewInject(R.id.edt_Unboxing)
    EditText     edtUnboxing;
    @ViewInject(R.id.edt_jian)
    EditText     edtjian;
    @ViewInject(R.id.edt_car)
    EditText     edtcar;
    @ViewInject(R.id.txt_SugestStock)
    TextView     txtSugestStock;
    @ViewInject(R.id.txt_OffshelfNum)
    TextView     txtOffshelfNum;
    @ViewInject(R.id.txt_currentPickNum)
    TextView     txtcurrentPickNum;
    @ViewInject(R.id.txt_Unboxing)   //拆零数量
    TextView     txtUnboxing;
    @ViewInject(R.id.txt_jian)    //本体条码
    TextView     txtjian;
    @ViewInject(R.id.btn_OutOfStock)
    TextView     btnOutOfStock;
    @ViewInject(R.id.btn_BillDetail)
    TextView     btnBillDetail;
    @ViewInject(R.id.btn_PrintBox)
    TextView     btnPrintBox;
    @ViewInject(R.id.lsv_PickList)
    ListView     lsvPickList;
    @ViewInject(R.id.edt_StockScan)
    EditText     edtStockScan;
    @ViewInject(R.id.textView133)
    TextView     textView133;
    @ViewInject(R.id.txterpvoucherno)
    TextView     txterpvoucherno;
    @ViewInject(R.id.txtcustomername)
    TextView     txtcustomername;
    @ViewInject(R.id.btn_inner_barcode_button)
    ToggleButton mInnerBarcodeButton;
    @ViewInject(R.id.btn_input_qty_button)
    ToggleButton mInputQtyButton;
    @ViewInject(R.id.btn_TJ)
    Button       mSplitCommitButton;

    ArrayList<OutStockTaskInfo_Model>        outStockTaskInfoModels;
    ArrayList<OutStockTaskDetailsInfo_Model> outStockTaskDetailsInfoModels;
    ArrayList<OutStockTaskDetailsInfo_Model> SameLineoutStockTaskDetailsInfoModels; //相同行物料集合

    ArrayList<Boxing>          BoxingModels             = new ArrayList<>();//拆零清单
    ArrayList<StockInfo_Model> stockInfoModels;//扫描条码
    OffShelfScanDetailAdapter  offShelfScanDetailAdapter;
    Float                      SumReaminQty             = 0f; //当前拣货物料剩余拣货数量合计
    int                        currentPickMaterialIndex = -1;
    String                     IsEdate                  = "";

    String  Erpvoucherno = "";
    Integer Headerid     = 0;
    String  TaskNo       = "";

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.context = context;
        BaseApplication.toolBarTitle = new ToolBarTitle(getString(R.string.OffShelf_subtitle) + "-" + BaseApplication.userInfo.getWarehouseName(), true);
        x.view().inject(this);
        BaseApplication.isCloseActivity = false;
    }

    @Override
    protected void initData() {
        super.initData();
        outStockTaskInfoModels = getIntent().getParcelableArrayListExtra("outStockTaskInfoModel");
        outStockTaskInfoModels.get(0).setWareHouseNo(BaseApplication.userInfo.getWarehouseCode());
        Erpvoucherno = outStockTaskInfoModels.get(0).getErpVoucherNo();
        Headerid = outStockTaskInfoModels.get(0).getHeaderID();
        TaskNo = outStockTaskInfoModels.get(0).getTaskNo();
        GetT_OutTaskDetailListByHeaderIDADF(outStockTaskInfoModels);
        txterpvoucherno.setText(Erpvoucherno);
        txtcustomername.setText(outStockTaskInfoModels.get(0).getSupcusName() == null ? "" : outStockTaskInfoModels.get(0).getSupcusName());
        edtcar.setText(outStockTaskInfoModels.get(0).getCarNo() == null ? "" : outStockTaskInfoModels.get(0).getCarNo());

        tbUnboxType.setChecked(false);
        tbBoxType.setChecked(true);
        ShowUnboxing();
        mModel = new OffshelfBatchScanModel(context);

    }


    /**
     * @desc: 下架操作条码类型
     * @param:
     * @return:
     * @author:
     * @time 2020/4/20 17:16
     */
    @Event(value = {R.id.tb_UnboxType, R.id.tb_PalletType, R.id.tb_BoxType}, type = CompoundButton.OnClickListener.class)
    private void TBonCheckedChanged(View view) {

        tbUnboxType.setChecked(view.getId() == R.id.tb_UnboxType);
        tbBoxType.setChecked(view.getId() == R.id.tb_BoxType);
        ShowUnboxing();
        if (view.getId() == R.id.tb_UnboxType) {
            CommonUtil.setEditFocus(mBarcode);

        }
    }


    /**
     * @desc: 拆零操作条码类型
     * @param:
     * @return:
     * @author:
     * @time 2020/4/20 17:16
     */
    @Event(value = {R.id.btn_inner_barcode_button, R.id.btn_input_qty_button}, type = CompoundButton.OnClickListener.class)
    private void SplitCheckedChanged(View view) {
        if (view.getId() == R.id.tb_UnboxType) {
//            if (!CheckBluetooth()) {
//                MessageBox.Show(context, "蓝牙打印机连接失败");
//                return;
//            }
        }

        mInnerBarcodeButton.setChecked(view.getId() == R.id.btn_inner_barcode_button);
        mInputQtyButton.setChecked(view.getId() == R.id.btn_input_qty_button);
        ShowSplitButton((view.getId() == R.id.btn_inner_barcode_button));

    }

    @Event(value = R.id.lsv_PickList, type = AdapterView.OnItemClickListener.class)
    private void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OutStockTaskDetailsInfo_Model outStockTaskDetailsInfoModel = (OutStockTaskDetailsInfo_Model) offShelfScanDetailAdapter.getItem(position);
        Intent intent = new Intent(context, Query.class);
        intent.putExtra("Type", 1);
        intent.putExtra("MaterialNO", outStockTaskDetailsInfoModel.getMaterialNo());
        startActivityLeft(intent);
    }

    /**
     * @desc: 拣货数量逻辑
     * @param:
     * @return:
     * @author:
     * @time 2020/4/20 17:17
     */
    @Event(value = R.id.edt_Unboxing, type = View.OnKeyListener.class)
    private boolean edtUnboxingClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            try {
                String num = edtUnboxing.getText().toString().trim();  //获取界面拆零数量
                if (stockInfoModels != null && stockInfoModels.size() != 0) {

                    Float qty = Float.parseFloat(num); //输入数量
                    StockInfo_Model newmodel = new StockInfo_Model();
                    newmodel = stockInfoModels.get(0);
                    Float scanQty = newmodel.getQty(); //箱数量
                    if (qty > scanQty) {
                        MessageBox.Show(context, getString(R.string.Error_PackageQtyBiger));
                        CommonUtil.setEditFocus(edtUnboxing);
                        return true;
                    }
                    //ymh整箱发货
                    if (CommonUtil.EqualFloat(qty, scanQty) && tbBoxType.isChecked()) {
                        stockInfoModels = new ArrayList<>();
                        stockInfoModels.add(newmodel);
//                        currentPickMaterialIndex = FindFirstCanPickMaterialByMaterialNo(stockInfoModels.get(0).getMaterialNo(), stockInfoModels.get(0).getBatchNo(), stockInfoModels.get(0).getWarehouseNo());
                        currentPickMaterialIndex = FindFirstCanPickMaterialByMaterialNo(stockInfoModels.get(0).getMaterialNo(), stockInfoModels.get(0).getBatchNo(), stockInfoModels.get(0).getWarehouseNo(), stockInfoModels.get(0).getTracNo());
                        if (currentPickMaterialIndex >= 0) {
                            if (SumReaminQty < qty) {//qty > remainqty ||
                                MessageBox.Show(context, getString(R.string.Error_offshelfQtyBiger)
                                        + "\n需下架数量：" + SumReaminQty
                                        + "\n扫描数量：" + qty
                                        + "\n拆零后剩余数量：" + ArithUtil.sub(qty, SumReaminQty));
                                CommonUtil.setEditFocus(edtUnboxing);
                                return true;
                            }

//                            if (CheckStockInfo()) {  //判断是否拣货完毕、是否指定批次 2020-08-28
                                ShowPickMaterialInfo();

//                                //打印拆零标签
//                                if (edtOffShelfScanbarcode.getText().toString().contains("@")){
//                                    stockInfoModels.get(0).setSN(txterpvoucherno.getText().toString());
//                                    LPK130DEMO(stockInfoModels.get(0),"Jian");
//                                }
                                txtEDate.setText(CommonUtil.DateToString(stockInfoModels.get(0).getEDate()));
//                                mQty.setText(stockInfoModels.get(0).getStrStatus());
                                mQty.setText(stockInfoModels.get(0).getQty() + "");
                                SetOutStockTaskDetailsInfoModels(newmodel.getQty(), 3);

                                if (!checkdetail(outStockTaskDetailsInfoModels)) {
                                    MessageBox.Show(context, "提交的数据异常，退出重新扫描！");
                                    return true;
                                }


//                            }
                        } else {
                            if (currentPickMaterialIndex == -2) {
                                MessageBox.Show(context, getString(R.string.Error_NotPickWarehouse));
                                CommonUtil.setEditFocus(mBarcode);
                                return false;
                            }
                            if (currentPickMaterialIndex == -3) {
                                MessageBox.Show(context, getString(R.string.Error_NotTrace));
                                CommonUtil.setEditFocus(mBarcode);
                                return false;
                            }
                            MessageBox.Show(context, getString(R.string.Error_NotPickMaterial));
                            CommonUtil.setEditFocus(mBarcode);
                        }
                        return true;
                    } else {
                        //拆零模式提交
                        onSplitRefer();
                    }


                } else {
                    MessageBox.Show(context, getString(R.string.Hit_ScanBarcode));
                    edtUnboxing.setText("");
                    CommonUtil.setEditFocus(mBarcode);
                    return true;
                }

            } catch (Exception ex) {
                MessageBox.Show(context, ex.toString());
                return false;
            }
        }
        return false;

    }

    /**
     * @desc: 拆零提交
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/4/21 11:15
     */
    private void onSplitRefer() {
        String num = edtUnboxing.getText().toString().trim();  //获取界面拆零数量
        if (stockInfoModels != null && stockInfoModels.size() != 0) {
            Float qty = Float.parseFloat(num); //输入数量
            StockInfo_Model newmodel = new StockInfo_Model();
            newmodel = stockInfoModels.get(0);
            Float scanQty = newmodel.getQty(); //箱数量
            if (qty == scanQty) {
                MessageBox.Show(context, "拆零的数量不能等于外箱数量,请使用整箱扫描功能");
                return;
            }
            if (qty > scanQty) {
                MessageBox.Show(context, "拆零的数量不能大于外箱数量,请减小输入的数量");
                return;
            }

            if (currentPickMaterialIndex >= 0) {
                //检查蓝牙打印机是否连上
                if (mBarcode.getText().toString().contains("@")) {
//                            if(!CheckBluetooth()){
//                                MessageBox.Show(context, "蓝牙打印机连接失败");
//                                return true;
//                            }
                }

//                    Float remainqty = ArithUtil.sub(outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getRePickQty(),
//                            outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getScanQty());
                if (SumReaminQty < qty) {//qty > remainqty ||
                    MessageBox.Show(context, getString(R.string.Error_offshelfQtyBiger)
                            + "\n需下架数量：" + SumReaminQty
                            + "\n扫描数量：" + qty
                            + "\n拆零后剩余数量：" + ArithUtil.sub(qty, SumReaminQty));
                    CommonUtil.setEditFocus(edtUnboxing);
                    return;
                }

                //拆零
                newmodel.setPickModel(3);
                newmodel.setAmountQty(qty);
                String userJson = GsonUtil.parseModelToJson(BaseApplication.userInfo);
                newmodel.setHouseProp(HouseProp);
                newmodel.setTaskDetailesID(TaskDetailesID);
                String strOldBarCode = GsonUtil.parseModelToJson(newmodel);
                if (!mBarcode.getText().toString().contains("@")) {
                    newmodel.setBarcode(mBarcode.getText().toString());
                }
                if (!CheckBluetooth()) {
                    MessageBox.Show(context, "蓝牙打印机连接失败");
                    return;
                }
                final Map<String, String> params = new HashMap<String, String>();
                params.put("UserJson", userJson);
                params.put("strOldBarCode", strOldBarCode);
                params.put("strNewBarCode", "");
                params.put("PrintFlag", "2"); //1：打印 2：不打印
                LogUtil.WriteLog(OffshelfBatchScan.class, TAG_SaveT_BarCodeToStockADF, strOldBarCode);
                SharePreferUtil.ReadSupplierShare(context);
                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SaveT_BarCodeToStockADF, getString(R.string.Msg_SaveT_BarCodeToStockADF), context, mHandler, RESULT_SaveT_BarCodeToStockADF, null, URLModel.GetURL().SaveT_BarCodeToStockADF, params, null);

            }
        } else {
            MessageBox.Show(context, getString(R.string.Hit_ScanBarcode));
            edtUnboxing.setText("");
            CommonUtil.setEditFocus(mBarcode);
            return;
        }
    }

    private boolean CheckBluetooth() {
        try {
            boolean flag = CheckBluetoothBase();
            return flag;
        } catch (Exception ex) {
            return false;
        }

    }

    /**
     * @desc: 外箱条码扫描
     * @param:
     * @return:
     * @author:
     * @time 2020/4/20 17:25
     */
    @Event(value = R.id.edt_OffShelfScanbarcode, type = View.OnKeyListener.class)
    private boolean edtOffShelfScanbarcodeClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            keyBoardCancle();
            try {
                //2-整箱或者托发货 3-零数发货
                int Scantype = tbBoxType.isChecked() ? 2 : 3;

                String code = mBarcode.getText().toString().trim();
                final Map<String, String> params = new HashMap<String, String>();
                StockInfo_Model model = new StockInfo_Model();
                model.setBarcode(code);
                model.setScanType(Scantype);
                String Json = GsonUtil.parseModelToJson(model);
                params.put("ModelStockJson", Json);
                LogUtil.WriteLog(OffshelfBatchScan.class, TAG_GetStockModelADF, code);
                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetStockModelADF, getString(R.string.Msg_GetT_SerialNoByPalletADF), context, mHandler, RESULT_Msg_GetStockModelADF, null, URLModel.GetURL().GetStockModelADF, params, null);
                return false;
            } catch (Exception ex) {
                MessageBox.Show(context, ex.toString());
                return true;
            }
        }
        return false;
    }


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
            LogUtil.WriteLog(OffshelfBatchScan.class, TAG_GetStockModelADF, Json);
            RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_PrintListADF, getString(R.string.Msg_Print), context, mHandler, RESULT_Msg_PrintListADF, null, URLModel.GetURL().PrintListADF, params, null);

        } else {
            MessageBox.Show(context, "没有可以打印的清单列表！");
        }
    }

    /**
     * @desc: 拆零提交
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/4/20 19:38
     */
    @Event(R.id.btn_TJ)
    private void edtTJClick(View v) {
        if (DoubleClickCheck.isFastDoubleClick(context)) {
            return;
        }
        try {
            if (stockInfoModels == null || stockInfoModels.size() == 0) {
                MessageBox.Show(context, "请先输入外箱条码");
                return;
            }


            int AmountQty = stockInfoModels.get(0).getLstJBarCode().size();
            if (AmountQty == 0) {
                MessageBox.Show(context, "请扫描本体条码");
                return;
            }
//            stockInfoModels.get(0).setAmountQty((float) AmountQty);
            edtUnboxing.setText(AmountQty + "");
//            KeyEvent key = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER);
//            edtUnboxingClick(null, KeyEvent.KEYCODE_ENTER, key);

            onSplitRefer();
        } catch (Exception ex) {
            MessageBox.Show(context, ex.toString());
        }
    }

    /**
     * @desc: 扫描本体
     * @param:
     * @return:
     * @author:
     * @time 2020/4/21 11:00
     */
    @Event(value = R.id.edt_jian, type = View.OnKeyListener.class)
    private boolean edtjianClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            try {
                //2-整箱或者托发货 3-零数发货
//                String code=edtOffShelfScanbarcode.getText().toString().trim();
                String code = edtjian.getText().toString().trim();
                final Map<String, String> params = new HashMap<String, String>();
                params.put("Serialno", code);
                LogUtil.WriteLog(OffshelfBatchScan.class, TAG_GetStockModelADF, code);
                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetBarcodeModelForJADF, getString(R.string.Msg_GetT_SerialNoByPalletADF), context, mHandler, result_msg_GetBarcodeModelForJADF, null, URLModel.GetURL().GetBarcodeModelForJADF, params, null);
//                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetStockModelFrojianADF, getString(R.string.Msg_GetT_SerialNoByPalletADF), context, mHandler, RESULT_Msg_GetStockModelFrojianADF, null, URLModel.GetURL().GetStockModelADF, params, null);
            } catch (Exception ex) {
                MessageBox.Show(context, ex.toString());
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_outstock, menu);
        return true;
    }

    /**
     * @desc: 提交下架扫描信息
     * @param:
     * @return:
     * @author:
     * @time 2020/4/21 9:34
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            if (outStockTaskDetailsInfoModels != null && outStockTaskDetailsInfoModels.size() > 0) {
                if (!checkdetail(outStockTaskDetailsInfoModels)) {
                    MessageBox.Show(context, "提交的数据异常，退出重新扫描！");
                    return false;
                }
                if (outStockTaskDetailsInfoModels.get(0).getVoucherType() != 40 && outStockTaskDetailsInfoModels.get(0).getVoucherType() != 41 && outStockTaskDetailsInfoModels.get(0).getVoucherType() != 32) { //生产领料申请单，委外领料申请单，采购退货单 可以部分下架
                    boolean isMaterialRowScanFinish = true;
                    if (outStockTaskDetailsInfoModels != null && outStockTaskDetailsInfoModels.size() > 0) {
                        for (OutStockTaskDetailsInfo_Model model : outStockTaskDetailsInfoModels) {
                            Float remainQty = ArithUtil.sub(model.getRemainQty(), model.getScanQty());
                            if (remainQty != 0) {
                                isMaterialRowScanFinish = false;
                                break;
                            }
                        }
                    }
                    if (isMaterialRowScanFinish == false) {
                        MessageBox.Show(context, "物料行没有全部扫描完成,请全部扫描后再提交");
                        return false;
                    }
                }

                if (mUuid == null) {
                    mUuid = UUID.randomUUID();
                }

                //提交数据
                final Map<String, String> params = new HashMap<String, String>();
                String ModelJson = GsonUtil.parseModelToJson(outStockTaskDetailsInfoModels);
                String UserJson = GsonUtil.parseModelToJson(BaseApplication.userInfo);
                params.put("UserJson", UserJson);
                params.put("ModelJson", ModelJson);
                params.put("Guid", mUuid.toString());
                LogUtil.WriteLog(OffshelfBatchScan.class, TAG_SaveT_OutStockTaskDetailADF, ModelJson);
                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SaveT_OutStockTaskDetailADF, getString(R.string.Msg_SaveT_OutStockTaskDetailADF), context, mHandler, RESULT_Msg_SaveT_OutStockTaskDetailADF, null, URLModel.GetURL().SaveT_OutStockTaskDetailADF, params, null);
            }

        } else if (item.getItemId() == R.id.action_bluetooth) {
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, 1);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @desc: 外箱条码扫描结果（整箱和拆零模式）
     * @param:
     * @return:
     * @author:
     * @time 2020/4/20 17:25
     */
    void AnalysisGetStockModelADFJson(String result) {
        LogUtil.WriteLog(OffshelfBatchScan.class, TAG_GetStockModelADF, result);
        try {
            ReturnMsgModelList<StockInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<StockInfo_Model>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
//                if (stockInfoModels != null && stockInfoModels.size() != 0) {
                //区分整箱/托 还有零
                if (tbBoxType.isChecked()) {
                    ArrayList<StockInfo_Model> StockInfos = returnMsgModel.getModelJson();
                    stockInfoModels = StockInfos;
                    if (stockInfoModels != null && stockInfoModels.size() > 0) {
                        for (StockInfo_Model model : stockInfoModels) {
                            float qty = model.getQty();
                            //ymh整箱发货
                            currentPickMaterialIndex = FindFirstCanPickMaterialByMaterialNo(model.getMaterialNo(), model.getBatchNo(), model.getWarehouseNo(), model.getTracNo());
                            if (currentPickMaterialIndex >= 0) {
                                if (CheckStockInfo()) {  //判断是否拣货完毕
                                    ShowPickMaterialInfo();
                                    if (SumReaminQty < qty) {//qty > remainqty ||
                                        MessageBox.Show(context, getString(R.string.Error_offshelfQtyBiger)
                                                + "\n需下架数量：" + SumReaminQty
                                                + "\n扫描数量：" + qty
                                                + "\n拆零后剩余数量：" + ArithUtil.sub(qty, SumReaminQty));
                                        CommonUtil.setEditFocus(mBarcode);
                                        return;
                                    }
//
                                    mQty.setText(model.getQty() + "");
                                    ArrayList<StockInfo_Model> list = new ArrayList<>();
                                    list.add(model);
//                                    if (checkBarcode(stockInfoModels)) {
                                    if (checkBarcode(list)) {
                                        SetOutStockTaskDetailsInfoModels(model.getQty(), model);
                                    }

                                }
                            } else {
                                if (stockInfoModels != null && stockInfoModels.size() > 1) {
                                    barcodeRollBack(stockInfoModels);
                                }
                                if (currentPickMaterialIndex == -2) {
                                    MessageBox.Show(context, getString(R.string.Error_NotPickWarehouse));
                                    CommonUtil.setEditFocus(mBarcode);
                                    return;
                                }
                                if (currentPickMaterialIndex == -3) {
                                    MessageBox.Show(context, getString(R.string.Error_NotTrace));
                                    CommonUtil.setEditFocus(mBarcode);
                                    return;
                                }
                                MessageBox.Show(context, getString(R.string.Error_NotPickMaterial));
                                CommonUtil.setEditFocus(mBarcode);
                                return;
                            }

                        }
                    }

                    CommonUtil.setEditFocus(mBarcode);
                    return;
                } else {
                    //拆零模式
                    stockInfoModels = returnMsgModel.getModelJson();
                    insertStockInfo();
//                    CommonUtil.setEditFocus(edtUnboxing);
                    //拆零本体
                    if (stockInfoModels.size() == 1 && tbUnboxType.isChecked()) {
                        if (mInnerBarcodeButton.isChecked()) {
                            ShowSplitButton(mInnerBarcodeButton.isChecked());
                            CommonUtil.setEditFocus(edtjian);
                        }
                        //拆零输数量
                        if (mInputQtyButton.isChecked()) {
                            ShowSplitButton(mInnerBarcodeButton.isChecked());
                            edtUnboxing.setText(stockInfoModels.get(0).getQty() + "");
                            CommonUtil.setEditFocus(edtUnboxing);
                        }


                    }

//                    if (stockInfoModels.size() == 1 && tbUnboxType.isChecked() && stockInfoModels.get(0).getISJ().equals("2")) {

                }


            } else {
                MessageBox.Show(context, returnMsgModel.getMessage());
                CommonUtil.setEditFocus(mBarcode);
            }
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
            CommonUtil.setEditFocus(mBarcode);
        }

    }

    @Event(R.id.btn_OutOfStock)
    private void btnOutofStockClick(View view) {
        if (DoubleClickCheck.isFastDoubleClick(context)) {
            return;
        }
        if (currentPickMaterialIndex >= 0) {
            final String MaterialDesc = outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getMaterialDesc();
            final String MaterialNo = outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getMaterialNo();
            new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("是否跳过物料：\n" + MaterialNo + "\n" + MaterialDesc + "\n拣货？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO 自动生成的方法
                            outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).setOutOfstock(true);
                            currentPickMaterialIndex = FindFirstCanPickMaterial();
                            ShowPickMaterialInfo();
                        }
                    }).setNegativeButton("取消", null).show();
        }
    }


    /**
     * @desc: 获取下架明细
     * @param:
     * @return:
     * @author:
     * @time 2020/4/21 9:35
     */
    void GetT_OutTaskDetailListByHeaderIDADF(ArrayList<OutStockTaskInfo_Model> outStockTaskInfoModels) {
        if (outStockTaskInfoModels != null) {
            IsEdate = outStockTaskInfoModels.get(0).getIsEdate();
            final Map<String, String> params = new HashMap<String, String>();
            String modelJson = parseModelToJson(outStockTaskInfoModels);
            params.put("ModelDetailJson", modelJson);
            LogUtil.WriteLog(OffshelfBatchScan.class, TAG_GetT_OutTaskDetailListByHeaderIDADF, modelJson);
            RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetT_OutTaskDetailListByHeaderIDADF, getString(R.string.Msg_QualityDetailListByHeaderIDADF), context, mHandler, RESULT_Msg_GetT_OutTaskDetailListByHeaderIDADF, null, URLModel.GetURL().GetT_OutTaskDetailListByHeaderIDADF, params, null);
        }
    }


    void AnalysisGetT_UnLockTaskOperUserADFADFJson(String result) {
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
                LogUtil.WriteLog(OffshelfBatchScan.class, TAG_GetStockModelADF, Json);
                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_PrintListADF1, getString(R.string.Msg_Print), context, mHandler, RESULT_Msg_PrintListADF1, null, URLModel.GetURL().PrintListADF, params, null);
            } else {
                closeActiviry();
            }

//            closeActiviry();
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
        }
    }

    ArrayList<OutStockTaskDetailsInfo_Model> outStockTaskDetailsInfoModelsForprint = new ArrayList<OutStockTaskDetailsInfo_Model>();

    /**
     * @desc: 处理下架明细
     * @param:
     * @return:
     * @author:
     * @time 2020/4/21 9:35
     */
    void AnalysisGetT_OutTaskDetailListByHeaderIDADFJson(String result) {
        LogUtil.WriteLog(OffshelfBatchScan.class, TAG_GetT_OutTaskDetailListByHeaderIDADF, result);
        try {
            ReturnMsgModelList<OutStockTaskDetailsInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<OutStockTaskDetailsInfo_Model>>() {
            }.getType());
            outStockTaskDetailsInfoModels = new ArrayList<>();
            if (returnMsgModel != null && returnMsgModel.getHeaderStatus().equals("S")) {
                outStockTaskDetailsInfoModels = returnMsgModel.getModelJson();
                if (outStockTaskDetailsInfoModels != null && outStockTaskDetailsInfoModels.size() > 0) {
                    outStockTaskDetailsInfoModelsForprint = (ArrayList<OutStockTaskDetailsInfo_Model>) outStockTaskDetailsInfoModels.clone();
                }
                int size = outStockTaskDetailsInfoModels.size();
                txterpvoucherno.setText(Erpvoucherno + "  （行数：" + size + "）");
                MoveIndex = size;
                //处理拣货数量为0的
//                for(int i=0;i<size;i++) {
//                    if(ArithUtil.sub(outStockTaskDetailsInfoModels.get(i).getRePickQty(),
//                            outStockTaskDetailsInfoModels.get(i).getScanQty())==0f)
//                        Collections.swap(outStockTaskDetailsInfoModels, i, size-1);
//                }
                currentPickMaterialIndex = FindFirstCanPickMaterial(); //查找需要拣货物料行
                ShowPickMaterialInfo();//显示需要拣货物料

                //光标定位
                if (outStockTaskDetailsInfoModels.get(0).getHouseProp() == 2) {
                    if (mBarcode.getText().toString().length() > 0) {
                        CommonUtil.setEditFocus(edtStockScan);
                    } else {
                        CommonUtil.setEditFocus(edtcar);
                    }

                } else {
                    edtcar.setVisibility(View.INVISIBLE);
                    textView133.setVisibility(View.INVISIBLE);
                    CommonUtil.setEditFocus(mBarcode);
                }

            } else {
                MessageBox.Show(context, returnMsgModel.getMessage(), 1);
            }
            mBarcode.setText("");
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage(), 1);
        }
    }


    void AnalysisGetStockModelFrojianADFJson(String result) {
        LogUtil.WriteLog(OffshelfBatchScan.class, TAG_GetStockModelADF, result);
        try {
            ReturnMsgModelList<StockInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<StockInfo_Model>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                List<StockInfo_Model> Models = returnMsgModel.getModelJson();

//                ArrayList<StockInfo_Model> lists= stockInfoModels.get(0).getLstJBarCode()==null?new  ArrayList<StockInfo_Model>():stockInfoModels.get(0).getLstJBarCode();
                ArrayList<StockInfo_Model> lists = new ArrayList<StockInfo_Model>();
                //判断是否已经扫描过的肩箱
                int index = lists.indexOf(Models.get(0));
                if (index < 0) {
                    lists.add(Models.get(0));
                    stockInfoModels.get(0).setLstJBarCode(lists);
                } else {
                    MessageBox.Show(context, "该本体已经被扫描，不能重复扫描");
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

    /**
     * @desc: 获取本体信息返回结果
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/4/20 19:33
     */
    void AnalysisGetBarcodeModelForJADFJson(String result) {
        LogUtil.WriteLog(OffshelfBatchScan.class, TAG_GetStockModelADF, result);
        try {
            ReturnMsgModelList<StockInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<StockInfo_Model>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                List<StockInfo_Model> Models = returnMsgModel.getModelJson();
                if (stockInfoModels == null || stockInfoModels.size() == 0) {
                    MessageBox.Show(context, "拆零操作--请先扫描父级条码");
                    return;
                }
                String fSerialno = stockInfoModels.get(0).getSerialNo();
                if (fSerialno != null && Models != null && Models.get(0) != null && Models.get(0).getFserialno() != null) {
                    if (fSerialno.equals(Models.get(0).getFserialno())) {
                        ArrayList<StockInfo_Model> lists = stockInfoModels.get(0).getLstJBarCode() == null ? new ArrayList<StockInfo_Model>() : stockInfoModels.get(0).getLstJBarCode();
//                ArrayList<StockInfo_Model> lists = new ArrayList<StockInfo_Model>();
                        //判断是否已经扫描过的本体
                        int index = lists.indexOf(Models.get(0));
                        if (index < 0) {
                            lists.add(Models.get(0));
                            stockInfoModels.get(0).setLstJBarCode(lists);
                        } else {
                            MessageBox.Show(context, "该本体已经被扫描，不能重复扫描");
                        }
                    } else {
                        MessageBox.Show(context, "本体的父级序列号和外箱不一致");

                    }

                } else {
                    MessageBox.Show(context, "外箱的序列号和本体的父级序列号不能为空");
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

    void AnalysisSaveT_OutStockTaskDetailADFJson(String result) {
        try {
            LogUtil.WriteLog(OffshelfBatchScan.class, TAG_SaveT_OutStockTaskDetailADF, result);
            ReturnMsgModelList<Base_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<Base_Model>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                mUuid = null;
//                MessageBox.Show(context, returnMsgModel.getMessage(), 1);
                if (outStockTaskDetailsInfoModels.get(0).getVoucherType() != 40 && outStockTaskDetailsInfoModels.get(0).getVoucherType() != 41 && outStockTaskDetailsInfoModels.get(0).getVoucherType() != 32) {
                    new AlertDialog.Builder(context).setTitle("提示").setCancelable(false).setIcon(android.R.drawable.ic_dialog_info).setMessage(returnMsgModel.getMessage())
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO 自动生成的方法
                                    closeActiviry();
                                }
                            }).setNegativeButton("取消", null).show();
                } else {
                    GetT_OutTaskDetailListByHeaderIDADF(outStockTaskInfoModels);
                    MessageBox.Show(context, returnMsgModel.getMessage(), 1);
                    return;
                }


            } else {
                mUuid = null;
                MessageBox.Show(context, returnMsgModel.getMessage());
                return;
            }
//            clearFrm();
//            closeActiviry();
//            stockInfoModels = new ArrayList<>();
//            GetT_OutTaskDetailListByHeaderIDADF(outStockTaskInfoModels);
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
            LogUtil.WriteLog(OffshelfBatchScan.class, "error", ex.getMessage());
        }
    }


    ArrayList<OutStockDetailInfo_Model> GetPalletModels() {
        ArrayList<OutStockDetailInfo_Model> palletDetailModels = new ArrayList<>();
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
                            StockInfo_Model stockInfoModel = stockModel;
                            int index = palletDetailModels.indexOf(palletDetail_model);
                            if (stockInfoModel.getStockBarCodeStatus() == 0) {
                                if (index == -1) {
                                    palletDetail_model.getLstStock().add(0, stockInfoModel);
                                    palletDetailModels.add(palletDetail_model);
                                } else {
                                    int stockIndex = palletDetailModels.get(index).getLstStock().indexOf(stockInfoModel);
                                    if (stockIndex == -1) {
                                        palletDetailModels.get(index).getLstStock().add(0, stockInfoModel);
                                    } else {
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
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
            palletDetailModels = new ArrayList<>();
        }
        return palletDetailModels;
    }


    /*提交装箱清单*/
    void AnalysisPrintListADF(String result) {
        try {
            LogUtil.WriteLog(OffshelfBatchScan.class, TAG_PrintListADF, result);
            ReturnMsgModelList<Boxing> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<Boxing>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                ArrayList<Boxing> BoxingList = returnMsgModel.getModelJson();
                //打印清单
                StockInfo_Model stockmodel = new StockInfo_Model();
                outStockTaskInfoModels.get(0).setCustomerName(outStockTaskDetailsInfoModelsForprint.get(0).getFromErpWareHouseName());

                if (outStockTaskInfoModels.get(0).getErpVoucherNo().contains("C")) {
                    outStockTaskInfoModels.get(0).setCarNo(outStockTaskDetailsInfoModelsForprint.get(0).getSupCusName());
                } else {
                    outStockTaskInfoModels.get(0).setCarNo(outStockTaskDetailsInfoModelsForprint.get(0).getToErpWareHouseName());
                }
                LPK130DEMO(outStockTaskInfoModels.get(0), BoxingList, stockmodel, "LList");
                BoxingModels = new ArrayList<>();//清空装箱数据
            } else {
                MessageBox.Show(context, returnMsgModel.getMessage());
            }
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
            BoxingModels = new ArrayList<>();//清空装箱数据
        }

        CommonUtil.setEditFocus(mBarcode);

    }

    /*提交装箱清单*/
    void AnalysisPrintListADF1(String result) {
        try {
            LogUtil.WriteLog(OffshelfBatchScan.class, TAG_PrintListADF, result);
            ReturnMsgModelList<Boxing> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<Boxing>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                ArrayList<Boxing> BoxingList = returnMsgModel.getModelJson();
                if (BoxingList == null || BoxingList.size() == 0) {
                    MessageBox.Show(context, "保存成功，返回清单为空！");
                } else {
                    //打印清单
                    StockInfo_Model stockmodel = new StockInfo_Model();
                    outStockTaskInfoModels.get(0).setCustomerName(outStockTaskDetailsInfoModels.get(0).getFromErpWareHouseName());
                    if (outStockTaskInfoModels.get(0).getErpVoucherNo().contains("C")) {
                        outStockTaskInfoModels.get(0).setCarNo(outStockTaskDetailsInfoModelsForprint.get(0).getSupCusName());
                    } else {
                        outStockTaskInfoModels.get(0).setCarNo(outStockTaskDetailsInfoModelsForprint.get(0).getToErpWareHouseName());
                    }
                    LPK130DEMO(outStockTaskInfoModels.get(0), BoxingList, stockmodel, "LList");
                    BoxingModels = new ArrayList<>();//清空装箱数据
                }
                closeActiviry();
            } else {
                MessageBox.Show(context, returnMsgModel.getMessage());
            }
            closeActiviry();
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
            BoxingModels = new ArrayList<>();//清空装箱数据
            closeActiviry();
        }

        CommonUtil.setEditFocus(mBarcode);

    }


    boolean isChai = false;//是否是拆零的标记，便于打印清单

    /**
     * @desc: 拆零提交返回结果
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/4/21 11:20
     */
    void AnalysisSaveT_BarCodeToStockADF(String result) {
        float qty = stockInfoModels.get(0).getQty();
        //ymh整箱发货
        try {
            LogUtil.WriteLog(OffshelfBatchScan.class, TAG_SaveT_BarCodeToStockADF, result);
            ReturnMsgModel<StockInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<StockInfo_Model>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                StockInfo_Model stockInfoModel = returnMsgModel.getModelJson();
                //打印拆零标签
//                if (edtOffShelfScanbarcode.getText().toString().contains("@")){
                stockInfoModel.setSN(txterpvoucherno.getText().toString());
//                onPrint(stockInfoModel);
//                    LPK130DEMO(stockInfoModel,"Jian");
//                }
                stockInfoModels = new ArrayList<>();
                stockInfoModels.add(stockInfoModel);
                currentPickMaterialIndex = FindFirstCanPickMaterialByMaterialNo(stockInfoModels.get(0).getMaterialNo(), stockInfoModels.get(0).getBatchNo(), stockInfoModels.get(0).getWarehouseNo(), stockInfoModels.get(0).getTracNo());
//                if (currentPickMaterialIndex != -1) {
                if (currentPickMaterialIndex >= 0) {
                    onPrint(stockInfoModel);
                    if (CheckStockInfo()) {  //判断是否拣货完毕、是否指定批次
                        ShowPickMaterialInfo();
                        txtEDate.setText(CommonUtil.DateToString(stockInfoModels.get(0).getEDate()));
//                        mQty.setText(stockInfoModels.get(0).getStrStatus());
                        mQty.setText(stockInfoModels.get(0).getQty() + "");
                        if (checkBarcode(stockInfoModels)) {
                            SetOutStockTaskDetailsInfoModels(stockInfoModel.getQty(), 3);
                        }
                        isChai = true;

                    }
                } else {
                    if (currentPickMaterialIndex == -2) {
                        MessageBox.Show(context, getString(R.string.Error_NotPickWarehouse));
                        CommonUtil.setEditFocus(mBarcode);
                        return;
                    }
                    if (currentPickMaterialIndex == -3) {
                        MessageBox.Show(context, getString(R.string.Error_NotTrace));
                        CommonUtil.setEditFocus(mBarcode);
                        return;
                    }
                    MessageBox.Show(context, getString(R.string.Error_NotPickMaterial));
                    CommonUtil.setEditFocus(mBarcode);
                }

            } else {
                MessageBox.Show(context, returnMsgModel.getMessage());
            }
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
        }
        edtUnboxing.setText("");
        CommonUtil.setEditFocus(mBarcode);

    }

    //检查提交列表是否存在超条码现象
    private boolean checkdetail(ArrayList<OutStockTaskDetailsInfo_Model> models) {
        ArrayList<StockInfo_Model> checkstocks = new ArrayList<>();
        int count = 0;
        if (models != null && models.size() > 0) {
            for (int i = 0; i < models.size(); i++) {
                if (models.get(i).getLstStockInfo() != null && models.get(i).getLstStockInfo().size() > 0) {
                    count = count + models.get(i).getLstStockInfo().size();
                    for (int j = 0; j < models.get(i).getLstStockInfo().size(); j++) {
                        if (checkstocks.indexOf(models.get(i).getLstStockInfo().get(j)) != -1) {
                            return false;
                        } else {
                            checkstocks.add(models.get(i).getLstStockInfo().get(j));
                        }
                    }
                }
            }
        }

        return true;
    }


    void AnalysisSaveT_SingleErpvoucherADF(String result) {
        LogUtil.WriteLog(OffshelfBatchScan.class, TAG_SaveT_SingleErpvoucherADF, result);
        ReturnMsgModelList<OutStock_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<OutStock_Model>>() {
        }.getType());
        if (returnMsgModel.getHeaderStatus().equals("S")) {
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
        } else {
            MessageBox.Show(context, returnMsgModel.getMessage());
        }
    }

    /**
     * @desc: 拆零模式下找到外箱扫描所定位的物料行
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/4/21 10:10
     */
    void insertStockInfo() {
        try {
            currentPickMaterialIndex = FindFirstCanPickMaterialByMaterialNo(stockInfoModels.get(0).getMaterialNo(), stockInfoModels.get(0).getBatchNo(), stockInfoModels.get(0).getWarehouseNo(), stockInfoModels.get(0).getTracNo());
            if (currentPickMaterialIndex >= 0) {
                if (CheckStockInfo()) {  //判断是否拣货完毕
                    ShowPickMaterialInfo();
                    txtEDate.setText(CommonUtil.DateToString(stockInfoModels.get(0).getEDate()));
//                    mQty.setText(stockInfoModels.get(0).getStrStatus());
                    mQty.setText(stockInfoModels.get(0).getQty() + "");
                    CommonUtil.setEditFocus(edtUnboxing);
                }
            } else {
                if (currentPickMaterialIndex == -2) {
                    MessageBox.Show(context, getString(R.string.Error_NotPickWarehouse));
                    CommonUtil.setEditFocus(mBarcode);
                    return;
                }
                if (currentPickMaterialIndex == -3) {
                    MessageBox.Show(context, getString(R.string.Error_NotTrace));
                    CommonUtil.setEditFocus(mBarcode);
                    return;
                }
                MessageBox.Show(context, getString(R.string.Error_NotPickMaterial));
                CommonUtil.setEditFocus(mBarcode);
            }
        } catch (Exception ex) {
            MessageBox.Show(context, ex.toString());
        }

    }


    boolean isDel = false;

    /**
     * @desc: 绑定条码
     * @param:
     * @return:
     * @author:
     * @time 2020/4/21 9:55
     */
    boolean Bindbarcode(final ArrayList<StockInfo_Model> barCodeInfos) {
        boolean isBindBarcode = true;
        if (barCodeInfos != null && barCodeInfos.size() != 0) {
            try {
                for (StockInfo_Model StockInfo : barCodeInfos) {
                    if (StockInfo != null && outStockTaskDetailsInfoModels != null) {
//                        OutStockTaskDetailsInfo_Model outStockTaskDetailsInfo_Model = new OutStockTaskDetailsInfo_Model(StockInfo.getMaterialNo(),StockInfo.getBatchNo());
//                        OutStockTaskDetailsInfo_Model outStockTaskDetailsInfo_Model = new OutStockTaskDetailsInfo_Model(StockInfo.getMaterialNo(), StockInfo.getProjectNo(), StockInfo.getTracNo());
                        OutStockTaskDetailsInfo_Model outStockTaskDetailsInfo_Model = new OutStockTaskDetailsInfo_Model();
                        outStockTaskDetailsInfo_Model.setMaterialNo(StockInfo.getMaterialNo());

                        final int index = outStockTaskDetailsInfoModels.indexOf(outStockTaskDetailsInfo_Model);
                        if (index != -1) {
                            if (outStockTaskDetailsInfoModels.get(index).getLstStockInfo() == null) {
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
                            isBindBarcode = false;
                            MessageBox.Show(context, getString(R.string.Error_BarcodeNotInList) + "|" + StockInfo.getSerialNo());
                            break;
                        }
                    }

                }

//                InitFrm(barCodeInfos.get(0));
            } catch (Exception ex) {
                isBindBarcode = false;
                MessageBox.Show(context, ex.getMessage());
                CommonUtil.setEditFocus(edtStockScan);
            }

        }

        return isBindBarcode;
    }

    /**
     * @desc: 回滚条码
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/5/15 10:28
     */
    public void barcodeRollBack(ArrayList<StockInfo_Model> rollBackList) {
        if (rollBackList != null && rollBackList.size() > 0) {
            for (StockInfo_Model barcodeInfo : rollBackList) {
                if (barcodeInfo != null && outStockTaskDetailsInfoModels != null) {
                    for (int i = 0; i < outStockTaskDetailsInfoModels.size(); i++) {
                        OutStockTaskDetailsInfo_Model detailInfo = outStockTaskDetailsInfoModels.get(i);
                        if (detailInfo != null) {
                            if (detailInfo.getMaterialNo().equals(barcodeInfo.getMaterialNo())) {
                                if (outStockTaskDetailsInfoModels.get(i).getLstStockInfo() == null) {
                                    outStockTaskDetailsInfoModels.get(i).setLstStockInfo(new ArrayList<StockInfo_Model>());
                                }
                                final int barIndex = outStockTaskDetailsInfoModels.get(i).getLstStockInfo().indexOf(barcodeInfo);
                                if (barIndex != -1) {
                                    RemoveBarcode(i, barIndex);
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    /**
     * @desc: 校验条码重复
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/4/27 10:59
     */
    boolean checkBarcode(final ArrayList<StockInfo_Model> barCodeInfos) {
        boolean checkResult = true;
        boolean hasMaterial = false;
        String barcode = "";
        if (barCodeInfos != null && barCodeInfos.size() != 0) {
            try {
                for (StockInfo_Model StockInfo : barCodeInfos) {
                    String materialNo = StockInfo.getMaterialNo() != null ? StockInfo.getMaterialNo() : "";
                    String traceNo = StockInfo.getTracNo() != null ? StockInfo.getTracNo() : "";
                    if (StockInfo != null && outStockTaskDetailsInfoModels != null) {
                        for (int i = 0; i < outStockTaskDetailsInfoModels.size(); i++) {
                            String sMaterialNo = outStockTaskDetailsInfoModels.get(i).getMaterialNo() != null ? outStockTaskDetailsInfoModels.get(i).getMaterialNo() : "";
                            String sTraceNo = outStockTaskDetailsInfoModels.get(i).getTracNo() != null ? outStockTaskDetailsInfoModels.get(i).getTracNo() : "";
                            String sStrongHoldNo = outStockTaskDetailsInfoModels.get(i).getStrongHoldCode() != null ? outStockTaskDetailsInfoModels.get(i).getStrongHoldCode() : "";
                            if (sStrongHoldNo.contains("SHJC")) {
                                if (sMaterialNo.equals(materialNo) && sTraceNo.equals(traceNo)) {
                                    hasMaterial = true;
                                    if (outStockTaskDetailsInfoModels.get(i).getLstStockInfo() == null) {
                                        outStockTaskDetailsInfoModels.get(i).setLstStockInfo(new ArrayList<StockInfo_Model>());
                                    }
                                    final int barIndex = outStockTaskDetailsInfoModels.get(i).getLstStockInfo().indexOf(StockInfo);

                                    if (barIndex != -1) {
                                        checkResult = false;
                                        barcode = StockInfo.getSerialNo();
                                        MessageBox.Show(context, getString(R.string.Error_BarcodeScaned) + "|" + StockInfo.getSerialNo());
                                        break;
                                    }
                                }
                            } else {
                                if (sMaterialNo.equals(materialNo)) {
                                    hasMaterial = true;
                                    if (outStockTaskDetailsInfoModels.get(i).getLstStockInfo() == null) {
                                        outStockTaskDetailsInfoModels.get(i).setLstStockInfo(new ArrayList<StockInfo_Model>());
                                    }
                                    final int barIndex = outStockTaskDetailsInfoModels.get(i).getLstStockInfo().indexOf(StockInfo);

                                    if (barIndex != -1) {
                                        checkResult = false;
                                        barcode = StockInfo.getSerialNo();
                                        MessageBox.Show(context, getString(R.string.Error_BarcodeScaned) + "|" + StockInfo.getSerialNo());
                                        break;
                                    }
                                }
                            }


                        }
                    } else {
                        MessageBox.Show(context, "物料明细不能为空");
                        return false;
                    }
                    if (!hasMaterial) {
                        checkResult = false;
                        MessageBox.Show(context, "物料编号:" + StockInfo.getMaterialNo() + ",需求跟踪号:[" + StockInfo.getTracNo() + "]的条码:" + StockInfo.getSerialNo() + "不在列表中");
                        return checkResult;
                    }
                }

            } catch (Exception ex) {
                checkResult = false;
                MessageBox.Show(context, ex.getMessage());
                CommonUtil.setEditFocus(edtStockScan);
            }

        }

        return checkResult;
    }

    boolean RemoveBarcode(final int index, final int barIndex) {
        float qty = ArithUtil.sub(outStockTaskDetailsInfoModels.get(index).getScanQty(), outStockTaskDetailsInfoModels.get(index).getLstStockInfo().get(barIndex).getQty());
        outStockTaskDetailsInfoModels.get(index).getLstStockInfo().remove(barIndex);
        outStockTaskDetailsInfoModels.get(index).setScanQty(qty);
        return true;
    }

    /**
     * @desc: 校验条码并插入物料行的条码集合中
     * @param:
     * @return:
     * @author:
     * @time 2020/4/21 9:59
     */
    boolean CheckBarcode(StockInfo_Model StockInfo, int index) {
        boolean isChecked = false;
        if (outStockTaskDetailsInfoModels.get(index).getRemainQty() == 0) {
            MessageBox.Show(context, "该行物料已经拣货完毕");
            return false;
        }

        isChecked = Addbarcode(index, StockInfo);
        return isChecked;
    }


    /**
     * @desc: 插入物料行的条码集合中
     * @param:
     * @return:
     * @author:
     * @time 2020/4/21 9:59
     */
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


    void RemoveStockInfo(final int index) {
        currentPickMaterialIndex = FindFirstCanPickMaterialByMaterialNoDelete(stockInfoModels.get(0).getMaterialNo(), stockInfoModels.get(0).getStrongHoldCode());
        if (currentPickMaterialIndex >= 0) {
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
                            CommonUtil.setEditFocus(mBarcode);

                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    stockInfoModels = null;
                    CommonUtil.setEditFocus(mBarcode);
                }
            }).show();
        } else {
            MessageBox.Show(context, getString(R.string.Error_NotPickMaterial));
            CommonUtil.setEditFocus(mBarcode);
        }
    }


    void clearFrm() {
//        outStockTaskInfoModels=new ArrayList<>();
//        outStockTaskDetailsInfoModels=new ArrayList<>();
        stockInfoModels = new ArrayList<>();
        SumReaminQty = 0f; //当前拣货物料剩余拣货数量合计
        currentPickMaterialIndex = -1;
        IsEdate = "";
        edtStockScan.setText("");
        edtUnboxing.setText("");
//        txtgetbatch.setText("");
//        edtOffShelfScanbarcode.setText("");
        BindListVIew(outStockTaskDetailsInfoModels);
    }


    //赋值
    void SetOutStockTaskDetailsInfoModels(Float scanQty, int type) {
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
//                outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).
//                        setScanQty(ArithUtil.add(
//                                outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getScanQty(), scanQty));
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
        currentPickMaterialIndex = FindFirstCanPickMaterial();
        ShowPickMaterialInfo(); //显示下一拣货物料
    }

    //赋值
    void SetOutStockTaskDetailsInfoModels(Float scanQty, StockInfo_Model model) {
        //箱子
        AddSameLineMaterialNum(scanQty);
        stockInfoModels.get(0).setPickModel(2);
        outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getLstStockInfo().add(0, model);
        currentPickMaterialIndex = FindFirstCanPickMaterial();
        ShowPickMaterialInfo(); //显示下一拣货物料
    }

    /**
     * @desc: 自动行分配
     * @param:
     * @return:
     * @author:
     * @time 2020/4/27 8:03
     */
    void AddSameLineMaterialNum(Float ScanReaminQty) {
        try {
            for (int i = 0; i < SameLineoutStockTaskDetailsInfoModels.size(); i++) {
                if (ScanReaminQty == 0f) break;
                Float remainQty = ArithUtil.sub(SameLineoutStockTaskDetailsInfoModels.get(i).getRemainQty(), SameLineoutStockTaskDetailsInfoModels.get(i).getScanQty());
                Float addQty = remainQty < ScanReaminQty ? remainQty : ScanReaminQty;
                SameLineoutStockTaskDetailsInfoModels.get(i).setScanQty(ArithUtil.add(SameLineoutStockTaskDetailsInfoModels.get(i).getScanQty(), addQty));
                SameLineoutStockTaskDetailsInfoModels.get(i).setFromErpAreaNo(stockInfoModels.get(0).getAreaNo());
                SameLineoutStockTaskDetailsInfoModels.get(i).setFromErpWarehouse(stockInfoModels.get(0).getWarehouseNo());
                SameLineoutStockTaskDetailsInfoModels.get(i).setFromBatchNo(stockInfoModels.get(0).getBatchNo());

                ScanReaminQty = ArithUtil.sub(ScanReaminQty, addQty);
                remainQty = ArithUtil.sub(SameLineoutStockTaskDetailsInfoModels.get(i).getRemainQty(), SameLineoutStockTaskDetailsInfoModels.get(i).getScanQty());
                if (remainQty == 0f)
                    SameLineoutStockTaskDetailsInfoModels.get(i).setPickFinish(true);
            }
        } catch (Exception e) {
            MessageBox.Show(context, "分配物料行出现预期之外的异常:" + e.getMessage());
        }

    }

    void RemoveSameLineMaterialNum(Float ScanReaminQty) {
        for (int i = 0; i < SameLineoutStockTaskDetailsInfoModels.size(); i++) {
            if (ScanReaminQty == 0f) break;
            Float remainQty = SameLineoutStockTaskDetailsInfoModels.get(i).getScanQty();
            Float removeQty = remainQty < ScanReaminQty ? remainQty : ScanReaminQty;
            SameLineoutStockTaskDetailsInfoModels.get(i)
                    .setScanQty(ArithUtil.sub(SameLineoutStockTaskDetailsInfoModels.get(i).getScanQty(), removeQty));
            ScanReaminQty = ArithUtil.sub(ScanReaminQty, removeQty);
            remainQty = ArithUtil.sub(SameLineoutStockTaskDetailsInfoModels.get(i).getRePickQty(), SameLineoutStockTaskDetailsInfoModels.get(i).getScanQty());
            if (remainQty != 0f)
                SameLineoutStockTaskDetailsInfoModels.get(i).setPickFinish(false);
        }
    }

    int TaskDetailesID = 0;
    int HouseProp      = 0;

    /**
     * @desc: 刷新界面
     * @param:
     * @return:
     * @author:
     * @time 2020/4/27 8:09
     */
    void ShowPickMaterialInfo() {
        btnOutOfStock.setEnabled(true);
        if (currentPickMaterialIndex >= 0) {
            if (outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).getLstStockInfo() == null)
                outStockTaskDetailsInfoModels.get(currentPickMaterialIndex).setLstStockInfo(new ArrayList<StockInfo_Model>());
            OutStockTaskDetailsInfo_Model outStockTaskDetailsInfoModel = outStockTaskDetailsInfoModels.get(currentPickMaterialIndex);
            TaskDetailesID = outStockTaskDetailsInfoModel.getID();
            HouseProp = outStockTaskDetailsInfoModel.getHouseProp();

            txtCompany.setText(outStockTaskDetailsInfoModel.getStrongHoldName());
            txtBatch.setText(outStockTaskDetailsInfoModel.getFromBatchNo());
//            mQty.setText(outStockTaskDetailsInfoModel.getStrStatus());
            txtMaterialName.setText(outStockTaskDetailsInfoModel.getMaterialDesc());
            txtSugestStock.setText(outStockTaskDetailsInfoModel.getAreaNo());
            txtEDate.setText("");
            //   Float qty = ArithUtil.sub(outStockTaskDetailsInfoModel.getRePickQty(),outStockTaskDetailsInfoModel.getScanQty());
            FindSumQtyByMaterialNo(outStockTaskDetailsInfoModel.getMaterialNo(), outStockTaskDetailsInfoModel.getBatchNo());
            //"库："+outStockTaskDetailsInfoModel.getStockQty() + "/
            // txtOffshelfNum.setText("剩余拣货数：" + SumReaminQty);
            txtcurrentPickNum.setText(SumReaminQty + "");

            BindListVIew(outStockTaskDetailsInfoModels);
        } else {
            MessageBox.Show(context, getString(R.string.Error_PickingFinish), 1);
            BindListVIew(outStockTaskDetailsInfoModels);
            CommonUtil.setEditFocus(mBarcode);
//            GetT_OutTaskDetailListByHeaderIDADF(outStockTaskInfoModels);

        }

    }

    /**
     * @desc: 如果是外箱模式 隐藏  拆零模式中的 本体和数量 模式按钮，以及本体和数量扫描框;
     * 如果是拆零模式下，优先显示本体扫描框，数量扫描框隐藏
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/4/21 9:29
     */
    void ShowUnboxing() {
        if (tbUnboxType.isChecked()) {
            Intent intent = new Intent();
            intent.setClass(context, SplitZeroScan.class);
            startActivityLeft(intent);
//            txtUnboxing.setVisibility(View.VISIBLE );
//            edtUnboxing.setVisibility(View.VISIBLE );
//            mInputQtyButton.setVisibility(View.VISIBLE );
//            mInnerBarcodeButton.setVisibility(View.VISIBLE );
//            mSplitCommitButton.setVisibility(View.GONE);
//            txtjian.setVisibility(View.GONE);
//            edtjian.setVisibility(View.GONE);
        } else {
            mInputQtyButton.setVisibility(View.GONE);
            mInnerBarcodeButton.setVisibility(View.GONE);
            txtUnboxing.setVisibility(View.GONE);
            edtUnboxing.setVisibility(View.GONE);
            mSplitCommitButton.setVisibility(View.GONE);
            txtjian.setVisibility(View.GONE);
            edtjian.setVisibility(View.GONE);
        }
        CommonUtil.setEditFocus(mBarcode);

    }

    /**
     * @desc: 拆零模式
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/4/21 10:29
     */
    void ShowSplitButton(boolean show) {
        int visiable = show ? View.VISIBLE : View.GONE;
        int invisiable = show ? View.GONE : View.VISIBLE;
        if (mInnerBarcodeButton.isChecked()) {
            txtUnboxing.setVisibility(View.GONE);
            edtUnboxing.setVisibility(View.GONE);
            txtjian.setVisibility(View.VISIBLE);
            edtjian.setVisibility(View.VISIBLE);
            mSplitCommitButton.setVisibility(View.VISIBLE);

        } else {
            txtUnboxing.setVisibility(View.VISIBLE);
            edtUnboxing.setVisibility(View.VISIBLE);
            txtjian.setVisibility(View.GONE);
            edtjian.setVisibility(View.GONE);
            mSplitCommitButton.setVisibility(View.GONE);


        }
    }

    /*
    判断物料是否已经扫描
     */
    int CheckBarcodeScaned() {
        for (int i = 0; i < outStockTaskDetailsInfoModels.size(); i++) {
            if (outStockTaskDetailsInfoModels.get(i).getLstStockInfo() != null) {
                if (outStockTaskDetailsInfoModels.get(i).getLstStockInfo().indexOf(stockInfoModels.get(0)) != -1) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * @desc: 判断是否拣货完毕
     * @param:
     * @return:
     * @author:
     * @time 2020/4/20 20:58
     */
    Boolean CheckStockInfo() {
        OutStockTaskDetailsInfo_Model currentOustStock = outStockTaskDetailsInfoModels.get(currentPickMaterialIndex);
        //判断是否拣货完毕   --2020-8-28  去掉拣货完毕的判断  单据出现多个相同的物料号  ，由于数量是自动分配物料行 ，即使拣货完毕也会把条码挂在这一行，所以不能用这个判断
//        if (currentOustStock.getRemainQty().compareTo(currentOustStock.getScanQty()) == 0) {
//            btnOutOfStock.setEnabled(false);
//            MessageBox.Show(context, getString(R.string.Error_MaterialPickFinish), 1);
//            CommonUtil.setEditFocus(mBarcode);
//            return false;
//        }
        return true;
    }

    /*分配拣货数量，优先满足第一个拣货数量不满的物料*/
    void DistributionPickingNum(String MaterialNo, Float PickNum) {
        for (int i = 0; i < outStockTaskDetailsInfoModels.size(); i++) {
            if (outStockTaskDetailsInfoModels.get(i).getMaterialNo().equals(MaterialNo)) {
                Float remainQty = ArithUtil.sub(outStockTaskDetailsInfoModels.get(i).getRemainQty(), outStockTaskDetailsInfoModels.get(i).getScanQty());
                if (remainQty == 0f) {
                    continue;
                }
                if (PickNum >= remainQty) {
                    outStockTaskDetailsInfoModels.get(i).setScanQty(ArithUtil.add(outStockTaskDetailsInfoModels.get(i).getScanQty(), remainQty));
                    PickNum = ArithUtil.sub(PickNum, remainQty);
                } else {
                    outStockTaskDetailsInfoModels.get(i).setScanQty(ArithUtil.add(outStockTaskDetailsInfoModels.get(i).getScanQty(), PickNum));
                    break;
                }

            }
        }
    }

    String ErpVoucherno = "";


    /**
     * @desc: 统计相同行物料剩余拣货数量
     * @param:
     * @return:
     * @author:
     * @time 2020/4/27 8:01
     */
    void FindSumQtyByMaterialNo(String MaterialNo, String BatchNo) {

        SumReaminQty = 0.0f;
        ErpVoucherno = "";
        SameLineoutStockTaskDetailsInfoModels = new ArrayList<>();
        // for(int i=outStockTaskDetailsInfoModels.size()-1;i>=0;i--){
        for (int i = 0; i < outStockTaskDetailsInfoModels.size(); i++) {
//            if (outStockTaskDetailsInfoModels.get(i).getMaterialNo().equals(MaterialNo) && outStockTaskDetailsInfoModels.get(i).getFromBatchNo().equals(BatchNo)) {
            if (outStockTaskDetailsInfoModels.get(i).getMaterialNo().equals(MaterialNo)) {
                if (TextUtils.isEmpty(ErpVoucherno))
                    ErpVoucherno = outStockTaskDetailsInfoModels.get(i).getErpVoucherNo();
                if (ErpVoucherno.equals(outStockTaskDetailsInfoModels.get(i).getErpVoucherNo())) {
                    SameLineoutStockTaskDetailsInfoModels.add(outStockTaskDetailsInfoModels.get(i));
                    SumReaminQty = ArithUtil.add(SumReaminQty, ArithUtil.sub(outStockTaskDetailsInfoModels.get(i).getRemainQty(), outStockTaskDetailsInfoModels.get(i).getScanQty()));
                }
            }
        }
    }


    /**
     * @desc: 查找需要拣货物料位置，拣货数量为0，且不是缺货状态 ，找到第一个物料行就返回,将已完成的物料行放到末尾
     * @param:
     * @return:
     * @author:
     * @time 2020/4/21 9:42
     */
    int FindFirstCanPickMaterial() {
        int size = outStockTaskDetailsInfoModels.size();
        MovePickFinishMaterial();
        int index = -1;
        for (int i = 0; i < size; i++) {
//            if(outStockTaskDetailsInfoModels.get(i).getScanQty()!=null
//            && (outStockTaskDetailsInfoModels.get(i).getScanQty()!=outStockTaskDetailsInfoModels.get(i).getTaskQty()
//            // && ArithUtil.sub(outStockTaskDetailsInfoModels.get(i).getRemainQty(),outStockTaskDetailsInfoModels.get(i).getScanQty())!=0
//             && ArithUtil.sub(outStockTaskDetailsInfoModels.get(i).getRePickQty(),outStockTaskDetailsInfoModels.get(i).getScanQty())!=0
//            ) && !outStockTaskDetailsInfoModels.get(i).getOutOfstock() ){
            if (!outStockTaskDetailsInfoModels.get(i).getPickFinish()
                    && !outStockTaskDetailsInfoModels.get(i).getOutOfstock()) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * @desc: 找到物料行
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/4/20 20:35
     */
    int FindFirstCanPickMaterialByMaterialNo(String MaterialNo, String BatchNo, String warehouseNo, String traceNo) {
        int size = outStockTaskDetailsInfoModels.size();
        int index = -1;
        if (traceNo == null) traceNo = "";
        for (int i = 0; i < size; i++) {
//            if(outStockTaskDetailsInfoModels.get(i).getScanQty()!=null
//                    && (outStockTaskDetailsInfoModels.get(i).getScanQty()!=outStockTaskDetailsInfoModels.get(i).getTaskQty()
//                   // &&ArithUtil.sub(outStockTaskDetailsInfoModels.get(i).getRemainQty(),outStockTaskDetailsInfoModels.get(i).getScanQty())!=0
//                    &&ArithUtil.sub(outStockTaskDetailsInfoModels.get(i).getRePickQty(),outStockTaskDetailsInfoModels.get(i).getScanQty())!=0
//            ) && outStockTaskDetailsInfoModels.get(i).getMaterialNo().equals(MaterialNo)
//                    && outStockTaskDetailsInfoModels.get(i).getStrongHoldCode().equals(StrongHoldCode)){

//            if ((!outStockTaskDetailsInfoModels.get(i).getPickFinish())//没有拣货完毕 2020-08-28
            if (outStockTaskDetailsInfoModels.get(i).getMaterialNo().equals(MaterialNo))
//                    && outStockTaskDetailsInfoModels.get(i).getFromBatchNo().equals(BatchNo)
//                    && outStockTaskDetailsInfoModels.get(i).getStrongHoldCode().equals(StrongHoldCode)
            {
                String fromErpWareHouse = outStockTaskDetailsInfoModels.get(i).getFromErpWarehouse();
                String sTraceNo = outStockTaskDetailsInfoModels.get(i).getTracNo() != null ? outStockTaskDetailsInfoModels.get(i).getTracNo() : "";
                String sStrongHoldNo = outStockTaskDetailsInfoModels.get(i).getStrongHoldCode() != null ? outStockTaskDetailsInfoModels.get(i).getStrongHoldCode() : "";
                if (fromErpWareHouse != null && !fromErpWareHouse.isEmpty()) { //物料明细有仓库的就校验仓库 没有就不校验
                    if (outStockTaskDetailsInfoModels.get(i).getFromErpWarehouse().equals(warehouseNo)) {
                        if (sStrongHoldNo.contains("SHJC")) {
                            if (sTraceNo.equals(traceNo)) {
                                ErpVoucherno = outStockTaskDetailsInfoModels.get(i).getErpVoucherNo();
                                index = i;
                                break;
                            } else {
                                index = -3;
                            }
                        } else {
                            ErpVoucherno = outStockTaskDetailsInfoModels.get(i).getErpVoucherNo();
                            index = i;
                            break;
                        }


                    } else {
                        index = -2;
                    }
                } else {
                    if (sStrongHoldNo.contains("SHJC")) {
                        if (sTraceNo.equals(traceNo)) {
                            ErpVoucherno = outStockTaskDetailsInfoModels.get(i).getErpVoucherNo();
                            index = i;
                            break;
                        } else {
                            index = -3;
                        }
                    } else {
                        ErpVoucherno = outStockTaskDetailsInfoModels.get(i).getErpVoucherNo();
                        index = i;
                        break;
                    }

                }
            }
        }
        return index;
    }

    int FindFirstCanPickMaterialByMaterialNoDelete(String MaterialNo, String StrongHoldCode) {
        int size = outStockTaskDetailsInfoModels.size();
        int index = -1;
        for (int i = 0; i < size; i++) {
            if (outStockTaskDetailsInfoModels.get(i).getMaterialNo().equals(MaterialNo)
                    && outStockTaskDetailsInfoModels.get(i).getStrongHoldCode().equals(StrongHoldCode)) {
                ErpVoucherno = outStockTaskDetailsInfoModels.get(i).getErpVoucherNo();
                index = i;
                break;
            }
        }
        return index;
    }

    private void BindListVIew(ArrayList<OutStockTaskDetailsInfo_Model> outStockTaskDetailsInfoModels) {
        offShelfScanDetailAdapter = new OffShelfScanDetailAdapter(context, outStockTaskDetailsInfoModels);
        lsvPickList.setAdapter(offShelfScanDetailAdapter);
    }

    int MoveIndex = -1;

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
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {

            if (BoxingModels != null && BoxingModels.size() > 0) {
                MessageBox.Show(context, "有清单没被打印，先打印清单再退出！");
                return true;
            }

            if (BaseApplication.isCloseActivity) {
                backCheckTask();
            } else {
                BackAlter();
            }
        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return true;
    }


    public void BackAlter() {
        new AlertDialog.Builder(context).setTitle("提示").setCancelable(false).setIcon(android.R.drawable.ic_dialog_info).setMessage("是否返回上一页面？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 自动生成的方法
                        backCheckTask();
                    }
                }).setNegativeButton("取消", null).show();
    }


    public void backCheckTask() {
        //解表
        Map<String, String> params = new HashMap<>();
        String UserModel = GsonUtil.parseModelToJson(BaseApplication.userInfo);
        String TaskOutStockModelJson = GsonUtil.parseModelToJson(outStockTaskInfoModels.get(0));
        params.put("UserJson", UserModel);
        params.put("TaskOutStockModelJson", TaskOutStockModelJson);
        LogUtil.WriteLog(OffShelfBillChoice.class, TAG_UnLockTaskOperUserADF, UserModel);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_UnLockTaskOperUserADF, "查看权限中", context, mHandler, RESULT_UnLockTaskOperUserADF, null, URLModel.GetURL().UnLockTaskOperUser, params, null);
    }

    /**
     * @desc: 打印外箱条码
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/5/7 11:05
     */
    private void onPrint(StockInfo_Model info) {
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
            if (mModel != null) {
                List<PrintBean> list = new ArrayList<>();
                list.add(bean);
                mModel.ptintLPK130OutBarcode(list);
            }

        } else {
            MessageBox.Show(context, "拆零生成的外箱条码数据不能为空");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CommonUtil.setEditFocus(mBarcode);
    }
}
