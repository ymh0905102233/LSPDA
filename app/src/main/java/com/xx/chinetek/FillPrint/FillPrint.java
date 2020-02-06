package com.xx.chinetek.FillPrint;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
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
import com.xx.chinetek.Pallet.CombinPallet;
import com.xx.chinetek.PrintConnectActivity;
import com.xx.chinetek.adapter.wms.Pallet.PalletItemAdapter;
import com.xx.chinetek.base.BaseActivity;
import com.xx.chinetek.base.BaseApplication;
import com.xx.chinetek.base.ToolBarTitle;
import com.xx.chinetek.cywms.R;
import com.xx.chinetek.cywms.Review.ReviewScan;
import com.xx.chinetek.cywms.Stock.AdjustStock;
import com.xx.chinetek.model.Base_Model;
import com.xx.chinetek.model.Box.Boxing;
import com.xx.chinetek.model.Material.BarCodeInfo;
import com.xx.chinetek.model.Pallet.PalletDetail_Model;
import com.xx.chinetek.model.ReturnMsgModel;
import com.xx.chinetek.model.ReturnMsgModelList;
import com.xx.chinetek.model.URLModel;
import com.xx.chinetek.model.WMS.Inventory.Barcode_Model;
import com.xx.chinetek.model.WMS.OffShelf.OutStockTaskInfo_Model;
import com.xx.chinetek.model.WMS.Review.OutStockDetailInfo_Model;
import com.xx.chinetek.model.WMS.Review.OutStock_Model;
import com.xx.chinetek.model.WMS.Stock.StockInfo_Model;
import com.xx.chinetek.util.Network.NetworkError;
import com.xx.chinetek.util.Network.RequestHandler;
import com.xx.chinetek.util.dialog.MessageBox;
import com.xx.chinetek.util.dialog.ToastUtil;
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
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_fill_print)
public class FillPrint extends PrintConnectActivity {

    String TAG_GetStockModelADF = "FillPrint_GetStockModelADF";
    String TAG_PrintLpkPalletAndroid = "FillPrint_TAG_PrintLpkPalletAndroid";

    private final int RESULT_Msg_GetStockModelADF = 101;
    private final int RESULT_PrintLpkPalletAndroid = 102;

    String TAG_PrintT = "FillPrint_TAG_PrintT";//成品托打印
    private final int RESULT_PrintT = 103;//成品托打印

    String TAG_GetCPT = "FillPrint_TAG_GetCPT";//获取成品托
    private final int RESULT_GetCPT = 104;//获取成品托

    String TAG_GetInfoBySerial = "AdjustStock_GetInfoBySerial";
    private final int RESULT_GetInfoBySerial = 105;

    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case RESULT_GetCPT:
                AnalysisGetCPTADFJson((String) msg.obj);
                break;
            case RESULT_Msg_GetStockModelADF:
                AnalysisGetStockADFJson((String) msg.obj);
                break;
            case RESULT_PrintLpkPalletAndroid:
                AnalysisPrintLpkPalletAndroid((String) msg.obj);
                break;
            case RESULT_PrintT:
                PrintTAnalysis((String) msg.obj, TAG_PrintT);
                break;
            case RESULT_GetInfoBySerial:
                AnalysisGetInfoBySerialJson((String) msg.obj);
                break;
            case NetworkError.NET_ERROR_CUSTOM:
                ToastUtil.show("获取请求失败_____" + msg.obj);
                CommonUtil.setEditFocus(edtLabelScanbarcode);
                break;
        }
    }

    void AnalysisGetCPTADFJson(String result) {
        LogUtil.WriteLog(FillPrint.class, TAG_GetCPT, result);
        ReturnMsgModel<Base_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<Base_Model>>() {
        }.getType());
        if (returnMsgModel.getHeaderStatus().equals("S")) {
            txtPalletNo.setText(returnMsgModel.getTaskNo());
        } else {
            MessageBox.Show(context, returnMsgModel.getMessage());
        }
        CommonUtil.setEditFocus(edtLabelScanbarcode);
    }


    Context context = FillPrint.this;

    @ViewInject(R.id.txt_MaterialName)
    TextView txtMaterialName;
    @ViewInject(R.id.txt_BatchNo)
    TextView txtBatchNo;
    @ViewInject(R.id.txt_PalletNo)
    TextView txtPalletNo;
    @ViewInject(R.id.tb_Box)
    ToggleButton tbBox;
    @ViewInject(R.id.tb_Pallet)
    ToggleButton tbPallet;
    //    @ViewInject(R.id.tb_PalletY)
//    ToggleButton tbPalletY;
    @ViewInject(R.id.tb_Sample)
    ToggleButton tbSample;
    @ViewInject(R.id.edt_LabelScanbarcode)
    EditText edtLabelScanbarcode;
    @ViewInject(R.id.edt_scan)
    EditText edtscan;

    @ViewInject(R.id.lsv_PalletDetail)
    ListView lsvPalletDetail;


    List<StockInfo_Model> stockInfoModels;//扫描条码
    PalletItemAdapter palletItemAdapter;

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.context = context;
        BaseApplication.toolBarTitle = new ToolBarTitle(getString(R.string.Product_fillPrint_subtitle), true);
        x.view().inject(this);
        BaseApplication.isCloseActivity = false;
    }

    @Event(value = R.id.edt_LabelScanbarcode, type = View.OnKeyListener.class)
    private boolean edtLabelScanbarcodeClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            keyBoardCancle();
            String flag = tbBox.isChecked() ? "2" : (tbPallet.isChecked() ? "1" : "3");
            String barcode = edtLabelScanbarcode.getText().toString().trim();
            if (!barcode.equals("")) {
                final Map<String, String> params = new HashMap<String, String>();
                params.put("filter", barcode);
                params.put("flag", flag);
                String para = (new JSONObject(params)).toString();
                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetInfoBySerial, getString(R.string.Msg_GetT_SerialNoByPalletADF), context, mHandler, RESULT_GetInfoBySerial, null, URLModel.GetURL().GetMessageForPrint, params, null);
            }

        }
        return false;
    }

    ArrayList<Boxing> boxings = new ArrayList<>();

    void AnalysisGetInfoBySerialJson(String result) {
        try {
            ReturnMsgModelList<Boxing> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<Boxing>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                boxings = returnMsgModel.getModelJson();
                if(boxings==null||boxings.size()==0){
                    MessageBox.Show(context,"获取数据失败！");
                    return;
                }
                if (tbPallet.isChecked()) {
                    txtPalletNo.setText(boxings.get(0).getSerialNo());
                    txtMaterialName.setText("");
                    txtBatchNo.setText("");
                }
                if (tbBox.isChecked()) {
                    txtPalletNo.setText("");
                    txtMaterialName.setText(boxings.get(0).getMaterialName());
                    txtBatchNo.setText(boxings.get(0).getQty() + "");
                }
                if (tbSample.isChecked()) {
                    txtPalletNo.setText("");
                    txtMaterialName.setText("");
                    txtBatchNo.setText("");
                    BindListVIew(boxings);
                }


            } else
                MessageBox.Show(context, returnMsgModel.getMessage());
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
        }
    }

    private void BindListVIew(List<Boxing> Boxinginfos) {
        ArrayList<BarCodeInfo> BarCodeInfos = new ArrayList<>();
        for (int i = 0; i < Boxinginfos.size(); i++) {
            BarCodeInfo model = new BarCodeInfo();
            model.setBatchNo(Boxinginfos.get(i).getTaskNo());
            model.setSerialNo(Boxinginfos.get(i).getSerialNo());
            model.setMaterialDesc(Boxinginfos.get(i).getMaterialName());
            BarCodeInfos.add(model);
        }

        palletItemAdapter = new PalletItemAdapter(context, BarCodeInfos);
        lsvPalletDetail.setAdapter(palletItemAdapter);
    }


    private boolean CheckBluetooth(){
        try{
            boolean flag=CheckBluetoothBase();
            return flag;
        }catch(Exception ex){
            return false;
        }

    }

    @Event(R.id.btn_labelPrint)
    private void btnlabelPrintClick(View view) {

       if(!CheckBluetooth()){
            MessageBox.Show(context, "蓝牙打印机连接失败");
            return;
        }

        if(!edtscan.getText().toString().equals("")){
            LPK130DEMO1Scan(edtscan.getText().toString());
            return;
        }

        if (tbPallet.isChecked()) {
            LPK130DEMO1Pallet(txtPalletNo.getText().toString());
        }
        if (tbBox.isChecked()) {
            if (boxings != null && boxings.size() > 0) {
                ArrayList<Boxing> BoxingList = new ArrayList<>();
                OutStock_Model model = new OutStock_Model();
                String[] Msg = boxings.get(0).getRemark1().split(";",-1);
                model.setErpVoucherNo(Msg[0]);
                model.setStrLandmark(Msg[1].equals("") ? Msg[3] : Msg[1]);
                model.setCustomerName(Msg[2]);
                model.setERPNote(Msg[4]);
                StockInfo_Model stockmodel = new StockInfo_Model();
                stockmodel.setMaterialDesc(boxings.get(0).getMaterialName());
                stockmodel.setMaterialNo(boxings.get(0).getMaterialNo());
                stockmodel.setQty(boxings.get(0).getQty());
                stockmodel.setSerialNo(boxings.get(0).getSerialNo());
                LPK130DEMO1(model, BoxingList, stockmodel, "ZList");
            } else {
                MessageBox.Show(context, "先扫描，再打印");
            }
        }
    }

    @Event(value = {R.id.tb_Box, R.id.tb_Pallet, R.id.tb_Sample}, type = CompoundButton.OnClickListener.class)
    private void TBonCheckedChanged(View view) {
        tbBox.setChecked(view.getId() == R.id.tb_Box);
        tbPallet.setChecked(view.getId() == R.id.tb_Pallet);
        tbSample.setChecked(view.getId() == R.id.tb_Sample);
//        tbPalletY.setChecked(view.getId()== R.id.tb_PalletY);
        initFrm();
        CommonUtil.setEditFocus(edtLabelScanbarcode);
    }

    void AnalysisGetStockADFJson(String result) {
        LogUtil.WriteLog(FillPrint.class, TAG_GetStockModelADF, result);
        ReturnMsgModelList<StockInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<StockInfo_Model>>() {
        }.getType());
        if (returnMsgModel.getHeaderStatus().equals("S")) {
            stockInfoModels = returnMsgModel.getModelJson();
            if (stockInfoModels != null && stockInfoModels.size() > 0) {
                txtMaterialName.setText(stockInfoModels.get(0).getMaterialDesc());
                txtBatchNo.setText(stockInfoModels.get(0).getBatchNo());
                txtPalletNo.setText(stockInfoModels.get(0).getPalletNo());
            }
        } else {
            MessageBox.Show(context, returnMsgModel.getMessage());
        }
        CommonUtil.setEditFocus(edtLabelScanbarcode);
    }

    void AnalysisPrintLpkPalletAndroid(String result) {
        try {
            LogUtil.WriteLog(CombinPallet.class, TAG_PrintLpkPalletAndroid, result);
            ReturnMsgModel<Base_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<Base_Model>>() {
            }.getType());
            MessageBox.Show(context, returnMsgModel.getMessage());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                if (tbBox.isChecked() && URLModel.isSupplier) {
                    String command = returnMsgModel.getMessage();
                    if (!command.isEmpty()) {
                        onPrint(command);
                    }
                }
                initFrm();
            }

        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
        }
        CommonUtil.setEditFocus(edtLabelScanbarcode);
    }

    /*打印托盘标签*/
    void PrintTAnalysis(String result, String Tag) {
        try {
            ReturnMsgModel<Base_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<Base_Model>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                initFrm();
            }
            MessageBox.Show(context, returnMsgModel.getMessage());

        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
        }
    }

    @Event(value = R.id.lsv_PalletDetail, type = AdapterView.OnItemClickListener.class)
    private void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final BarCodeInfo model = (BarCodeInfo) palletItemAdapter.getItem(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage("打印清单");
        builder.setPositiveButton("打印该清单", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ArrayList<Boxing> newboxings = new ArrayList<>();
                OutStockTaskInfo_Model OutStockTaskInfo = new OutStockTaskInfo_Model();
                for(int j=0;j<boxings.size();j++)
                {
                    if(boxings.get(j).getSerialNo().equals(model.getSerialNo())){
                        newboxings.add(boxings.get(j));
                    }


                    String[] Msg = boxings.get(j).getRemark1().split(";",-1);
                    OutStockTaskInfo.setErpVoucherNo(Msg[0]);
                    OutStockTaskInfo.setCustomerName(Msg[2]);
                    OutStockTaskInfo.setCarNo(Msg[1].equals("") ? Msg[3] : Msg[1]);
                    OutStockTaskInfo.setERPNote(Msg[4]);
                }

                StockInfo_Model stockmodel = new StockInfo_Model();
                LPK130DEMO(OutStockTaskInfo,newboxings,stockmodel,"LList");


            }
        });
        builder.setNeutralButton("取消打印", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();

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



    void initFrm(){
        boxings= new ArrayList<>();
        txtPalletNo.setText("");
        txtBatchNo.setText("");
        txtMaterialName.setText("");
    }
}
