package com.xx.chinetek.cywms.Car;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.adapter.wms.CarList.CarInListAdapter;
import com.xx.chinetek.adapter.wms.Pallet.PalletItemAdapter;
import com.xx.chinetek.base.BaseActivity;
import com.xx.chinetek.base.BaseApplication;
import com.xx.chinetek.base.ToolBarTitle;
import com.xx.chinetek.cyproduct.LineStockIn.LineStockInProduct;
import com.xx.chinetek.cywms.R;
import com.xx.chinetek.cywms.Receiption.ReceiptionScan;
import com.xx.chinetek.model.Car.TransportSupplier;
import com.xx.chinetek.model.Material.BarCodeInfo;
import com.xx.chinetek.model.ReturnMsgModelList;
import com.xx.chinetek.model.URLModel;
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
import java.util.Map;

@ContentView(R.layout.activity_carout)
public class CarIn extends BaseActivity {

    String TAG_GetT_PalletDetailByBarCodeADF="LineStockOutProduct_GetT_ScanInStockModelADF";
    private final int RESULT_Msg_GetT_PalletDetailByBarCode=102;

    String TAG_GetT_SaveBarCodeADF="LineStockOutProduct_GetT_SaveInStockModelADF";
    private final int RESULT_Msg_GetT_SaveBarCode=103;




    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case RESULT_Msg_GetT_PalletDetailByBarCode:
                AnalysisetT_PalletDetailByBarCodeJson((String) msg.obj);
                break;
            case RESULT_Msg_GetT_SaveBarCode:
                AnalysisetT_SaveBarCodeJson((String) msg.obj);
                break;
            case NetworkError.NET_ERROR_CUSTOM:
                ToastUtil.show("获取请求失败_____"+ msg.obj);
                CommonUtil.setEditFocus(edtLineStockOutScanBarcode);
                break;
        }
    }


    Context context=CarIn.this;

    @ViewInject(R.id.lsv_LineStockOutProduct)
    ListView lsvLineStockOutProduct;
    @ViewInject(R.id.edtorderno)
    EditText edtorderno;
    @ViewInject(R.id.edt_LineStockOutScanBarcode)
    EditText edtLineStockOutScanBarcode;
    @ViewInject(R.id.edtmoney)
    EditText edtmoney;
    @ViewInject(R.id.textView79)
    TextView textView79;


    CarInListAdapter carInListAdapter;
    String FEIGHT="";
    String orderno="";

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.context = context;
        BaseApplication.toolBarTitle = new ToolBarTitle("物流扫描"+ "-"+BaseApplication.userInfo.getUserName(), true);
        x.view().inject(this);
        BaseApplication.isCloseActivity=false;

    }




    @Event(value =R.id.edtmoney,type = View.OnKeyListener.class)
    private  boolean edtmoneyClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            if(SumbitTransportSuppliers==null || SumbitTransportSuppliers.size()==0){
                MessageBox.Show(context,"先扫描物流标签！");
                return false;
            }
            FEIGHT=edtmoney.getText().toString();
        }
        return false;
    }

    @Event(value =R.id.edtorderno,type = View.OnKeyListener.class)
    private  boolean edtordernoClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            if(SumbitTransportSuppliers==null || SumbitTransportSuppliers.size()==0){
                MessageBox.Show(context,"先扫描物流标签！");
                return false;
            }
            orderno=edtorderno.getText().toString();
        }
        return false;
    }

    @Event(value =R.id.edt_LineStockOutScanBarcode,type = View.OnKeyListener.class)
    private  boolean edtLineStockOutScanBarcodeClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            SumbitTransportSuppliers = new ArrayList<>();
            String code = edtLineStockOutScanBarcode.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                CommonUtil.setEditFocus(edtLineStockOutScanBarcode);
                return true;
            }
            final Map<String, String> params = new HashMap<String, String>();
            params.put("PalletNo", code);
            LogUtil.WriteLog(ReceiptionScan.class, TAG_GetT_PalletDetailByBarCodeADF, code);
            RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetT_PalletDetailByBarCodeADF, getString(R.string.Msg_GetT_SerialNoByPalletADF), context, mHandler, RESULT_Msg_GetT_PalletDetailByBarCode, null,  URLModel.GetURL().GetTransportSupplierDetailListADF, params, null);
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_postcar, menu);
        return true;
    }


    String guid="";
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            if (DoubleClickCheck.isFastDoubleClick(context)) {
                return false;
            }
            //提交
            if(SumbitTransportSuppliers!=null && SumbitTransportSuppliers.size()!=0){
                if(orderno.equals("")){
                    MessageBox.Show(context,"输入物流单号！");
                    return  false;
                }
                if(FEIGHT.equals("")&&!SumbitTransportSuppliers.get(0).getTradingConditionsCode().contains("MS0")){
                    MessageBox.Show(context,"输入物流费！");
                    return  false;
                }
                String strguid="";
                if (guid.equals("")){
                    guid=java.util.UUID.randomUUID().toString();
                }
                strguid=guid;
                for(int i=0;i<SumbitTransportSuppliers.size();i++){
                    SumbitTransportSuppliers.get(i).setType("2");
                    if(!SumbitTransportSuppliers.get(0).getTradingConditionsCode().contains("MS0")){
                        SumbitTransportSuppliers.get(i).setFeight(FEIGHT);
                    }
                    SumbitTransportSuppliers.get(i).setGUID(strguid);
                    SumbitTransportSuppliers.get(i).setVoucherNo(orderno);
                    SumbitTransportSuppliers.get(i).setCreater(BaseApplication.userInfo.getUserName());
                }


                final Map<String, String> params = new HashMap<String, String>();
                params.put("ModelJson", GsonUtil.parseModelToJson(SumbitTransportSuppliers));
                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetT_SaveBarCodeADF, getString(R.string.Msg_GetT_SaveStouckOutADF),
                        context, mHandler, RESULT_Msg_GetT_SaveBarCode, null,  URLModel.GetURL().SaveTransportSupplierListADF, params, null);

            }else{
                MessageBox.Show(context,"先扫描物流标签！");
            }
        }
        if (item.getItemId() == R.id.action_detail) {
            if (DoubleClickCheck.isFastDoubleClick(context)) {
                return false;
            }
            Intent intent = new Intent();
            intent.setClass(context, CarQuery.class);
            startActivityLeft(intent);

        }
        return super.onOptionsItemSelected(item);
    }


    void AnalysisetT_SaveBarCodeJson(String result){
        LogUtil.WriteLog(LineStockInProduct.class, TAG_GetT_SaveBarCodeADF,result);
        ReturnMsgModelList<String> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<String>>() {}.getType());
        try {
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                ClearFrm();
                MessageBox.Show(context,returnMsgModel.getMessage());
                guid="";
            } else {
                MessageBox.Show(context,"错误信息："+returnMsgModel.getMessage()+"再次提交或者退出重新扫描！");
                edtmoney.setVisibility(View.INVISIBLE);
                edtLineStockOutScanBarcode.setVisibility(View.INVISIBLE);
                edtorderno.setVisibility(View.INVISIBLE);
            }
        }catch (Exception ex){
            MessageBox.Show(context,ex.toString());
        }
        CommonUtil.setEditFocus(edtLineStockOutScanBarcode);
    }

    ArrayList<TransportSupplier> SumbitTransportSuppliers = new ArrayList<>();
    void AnalysisetT_PalletDetailByBarCodeJson(String result){
        LogUtil.WriteLog(LineStockInProduct.class, TAG_GetT_PalletDetailByBarCodeADF,result);
        ReturnMsgModelList<TransportSupplier> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<TransportSupplier>>() {}.getType());
        try {
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                SumbitTransportSuppliers = returnMsgModel.getModelJson();
                Bindbarcode(SumbitTransportSuppliers);
            } else {
                MessageBox.Show(context,returnMsgModel.getMessage());
                return;
            }

            if (SumbitTransportSuppliers.get(0).getTradingConditionsCode().contains("MS0")){
                edtmoney.setVisibility(View.INVISIBLE);
                textView79.setVisibility(View.INVISIBLE);
                CommonUtil.setEditFocus(edtorderno);
            }else{
                edtmoney.setVisibility(View.VISIBLE);
                textView79.setVisibility(View.VISIBLE);
                CommonUtil.setEditFocus(edtmoney);
            }
        }catch (Exception ex){
            MessageBox.Show(context,ex.toString());
        }


    }

    void Bindbarcode(final ArrayList<TransportSupplier> TransportSuppliers){
        if (TransportSuppliers != null && TransportSuppliers.size() != 0) {
            try {
                BindListVIew(TransportSuppliers);
            }catch (Exception ex){
                MessageBox.Show(context,ex.getMessage());
                CommonUtil.setEditFocus(edtLineStockOutScanBarcode);
            }

        }
    }


    private void BindListVIew(ArrayList<TransportSupplier> transportSuppliers) {
        carInListAdapter=new CarInListAdapter(context,transportSuppliers);
        lsvLineStockOutProduct.setAdapter(carInListAdapter);
    }



    void ClearFrm(){
        SumbitTransportSuppliers = new ArrayList<>();
        edtLineStockOutScanBarcode.setText("");
        edtmoney.setText("");
        edtorderno.setText("");
        orderno="";
        FEIGHT="";
        BindListVIew(SumbitTransportSuppliers);
    }

}
