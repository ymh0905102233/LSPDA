package com.xx.chinetek.cywms.Car;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.Parcel;
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
import com.xx.chinetek.adapter.wms.CarList.CarListAdapter;
import com.xx.chinetek.adapter.wms.Pallet.PalletItemAdapter;
import com.xx.chinetek.base.BaseActivity;
import com.xx.chinetek.base.BaseApplication;
import com.xx.chinetek.base.ToolBarTitle;
import com.xx.chinetek.cyproduct.LineStockIn.LineStockInProduct;
import com.xx.chinetek.cywms.R;
import com.xx.chinetek.cywms.Receiption.ReceiptionScan;
import com.xx.chinetek.model.Car.TransportSupplier;
import com.xx.chinetek.model.Material.BarCodeInfo;
import com.xx.chinetek.model.ReturnMsgModel;
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

@ContentView(R.layout.activity_line_stock_out_product)
public class CarOut extends BaseActivity {

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


    Context context=CarOut.this;

    @ViewInject(R.id.lsv_LineStockOutProduct)
    ListView lsvLineStockOutProduct;
    @ViewInject(R.id.edt_LineOutStockNum)
    EditText edtLineOutStockNum;
    @ViewInject(R.id.edt_LineStockOutScanBarcode)
    EditText edtLineStockOutScanBarcode;
    @ViewInject(R.id.edt_car)
    EditText edtcar;


//    ArrayList<BarCodeInfo> SumbitbarCodeInfos=new ArrayList<>();
    CarListAdapter carListAdapter;
    String carno="";
    String palletno="";

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.context = context;
        BaseApplication.toolBarTitle = new ToolBarTitle( getString(R.string.Product_ProductStockout_subtitleYMH)+ "-"+BaseApplication.userInfo.getUserName(), true);
        x.view().inject(this);
        BaseApplication.isCloseActivity=false;
        CommonUtil.setEditFocus(edtcar);
    }


    @Event(value =R.id.edt_car,type = View.OnKeyListener.class)
    private  boolean edtcarClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            carno = edtcar.getText().toString().trim();
            if (TextUtils.isEmpty(carno)) {
                MessageBox.Show(context,"车牌号不能为空！");
                return true;
            }
        }
        return false;
    }



    @Event(value =R.id.edt_LineStockOutScanBarcode,type = View.OnKeyListener.class)
    private  boolean edtLineStockOutScanBarcodeClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            carno = edtcar.getText().toString().trim();
            if (TextUtils.isEmpty(carno)) {
                MessageBox.Show(context,"车牌号不能为空！");
                return true;
            }

            String code = edtLineStockOutScanBarcode.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                CommonUtil.setEditFocus(edtLineStockOutScanBarcode);
                return true;
            }
            final Map<String, String> params = new HashMap<String, String>();
            params.put("PalletNo", code);
            LogUtil.WriteLog(ReceiptionScan.class, TAG_GetT_PalletDetailByBarCodeADF, code);
            RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetT_PalletDetailByBarCodeADF, getString(R.string.Msg_GetT_SerialNoByPalletADF), context, mHandler, RESULT_Msg_GetT_PalletDetailByBarCode, null,  URLModel.GetURL().GetPalletInfoByPalletNo, params, null);
        }
        return false;
    }

    @Event(value =R.id.edt_LineOutStockNum,type = View.OnKeyListener.class)
    private  boolean edt_LineOutStockNumClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            try{
                String code = edtLineOutStockNum.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    CommonUtil.setEditFocus(edtLineStockOutScanBarcode);
                    return true;
                }
                if (SumbittransportSuppliers==null||palletno.equals("")){
                    MessageBox.Show(context,"先扫描物流标签！");
                    return true;
                }
                for (int i=0;i<SumbittransportSuppliers.size();i++){
                    if (SumbittransportSuppliers.get(i).getPalletNo().equals(palletno)){
                        //限制改过数量的不能被再次改动
                        if(!SumbittransportSuppliers.get(i).getGUID().equals("1")){
                            float fnum =1;
                            float scannum = CommonUtil.convertToFloat(edtLineOutStockNum.getText().toString(),fnum );
                            float oldnnum = CommonUtil.convertToFloat(SumbittransportSuppliers.get(i).getBoxCount(),fnum );
                            if (oldnnum<scannum){
                                MessageBox.Show(context,"箱数不能大于实物数量！");
                                return true;
                            }
                            SumbittransportSuppliers.get(i).setBoxCount(edtLineOutStockNum.getText().toString());
                            SumbittransportSuppliers.get(i).setGUID("1");
                        }
                    }
                }
                CommonUtil.setEditFocus(edtLineStockOutScanBarcode);
                BindListVIew(SumbittransportSuppliers);
                return true;
            }catch (Exception ex){
                MessageBox.Show(context,ex.toString());
            }

        }
        return false;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_postcar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            if (DoubleClickCheck.isFastDoubleClick(context)) {
                return false;
            }
            //提交
            if(SumbittransportSuppliers!=null && SumbittransportSuppliers.size()!=0){
                for(int i=0;i<SumbittransportSuppliers.size();i++){
                    SumbittransportSuppliers.get(i).setCreater(BaseApplication.userInfo.getUserName());
                }

                final Map<String, String> params = new HashMap<String, String>();
                params.put("ModelJson", GsonUtil.parseModelToJson(SumbittransportSuppliers));
                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetT_SaveBarCodeADF, getString(R.string.Msg_GetT_SaveStouckOutADF),
                        context, mHandler, RESULT_Msg_GetT_SaveBarCode, null,  URLModel.GetURL().SaveTransportSupplierListADF, params, null);

            }else{
                MessageBox.Show(context,"没有需要装车的信息！");
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
        LogUtil.WriteLog(CarOut.class, TAG_GetT_SaveBarCodeADF,result);
        ReturnMsgModelList<String> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<String>>() {}.getType());
        try {
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                ClearFrm();
                MessageBox.Show(context,returnMsgModel.getMessage());
                CommonUtil.setEditFocus(edtcar);
            } else {
                MessageBox.Show(context,returnMsgModel.getMessage());
            }
        }catch (Exception ex){
            MessageBox.Show(context,ex.toString());
        }
        CommonUtil.setEditFocus(edtLineStockOutScanBarcode);
    }






    void AnalysisetT_PalletDetailByBarCodeJson(String result){
        try {
            LogUtil.WriteLog(LineStockInProduct.class, TAG_GetT_PalletDetailByBarCodeADF,result);
            ReturnMsgModelList<BarCodeInfo> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<BarCodeInfo>>() {}.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                ArrayList<BarCodeInfo> barCodeInfos = returnMsgModel.getModelJson();
                palletno=edtLineStockOutScanBarcode.getText().toString();
                Bindbarcode(barCodeInfos);
                CommonUtil.setEditFocus(edtLineOutStockNum);
            } else {
                MessageBox.Show(context,returnMsgModel.getMessage());
            }
        }catch (Exception ex){
            MessageBox.Show(context,ex.toString());
        }
        CommonUtil.setEditFocus(edtLineStockOutScanBarcode);
    }

    ArrayList<TransportSupplier> SumbittransportSuppliers=new ArrayList<>();
    void Bindbarcode(final ArrayList<BarCodeInfo> barCodeInfos){
        if (barCodeInfos != null && barCodeInfos.size() != 0) {
            try {
                //barcode转TransportSupplier
                ArrayList<TransportSupplier> transportSuppliers=new ArrayList<>();
                for (int i=0;i<barCodeInfos.size();i++){
                    TransportSupplier transportSupplier = new TransportSupplier();
                    transportSupplier.setPlateNumber(carno);
                    transportSupplier.setRemark(barCodeInfos.get(i).getPalletno());
                    transportSupplier.setErpVoucherNo(barCodeInfos.get(i).getErpVoucherNo());
                    transportSupplier.setCustomerName(barCodeInfos.get(i).getSupName());
                    transportSupplier.setBoxCount(barCodeInfos.get(i).getOutCount()+"");
                    transportSupplier.setType("1");
                    transportSupplier.setPalletNo(barCodeInfos.get(i).getPalletNo());
                    transportSupplier.setCreater(BaseApplication.userInfo.getUserNo());

                    transportSupplier.setContact(barCodeInfos.get(i).getContact());
                    transportSupplier.setPhone(barCodeInfos.get(i).getPhone());
                    transportSupplier.setAddress(barCodeInfos.get(i).getAddress());
                    transportSupplier.setAddress1(barCodeInfos.get(i).getAddress1());
                    transportSupplier.setGUID("0");
                    transportSuppliers.add(transportSupplier);

                }

//                if(SumbittransportSuppliers.indexOf(transportSuppliers.get(0))!=-1){
//                    MessageBox.Show(context,"该箱码已被扫描");
//                    return;
//                }
                for (int j=0;j<SumbittransportSuppliers.size();j++){
                    if(SumbittransportSuppliers.get(j).getPalletNo().equals(barCodeInfos.get(0).getPalletNo())){
                        MessageBox.Show(context,"该箱码已被扫描");
                        return;
                    }
                }

                int sumAll=0;
                for (TransportSupplier model : transportSuppliers) {
                    SumbittransportSuppliers.add(0,model);
                    sumAll=sumAll+ Integer.parseInt(model.getBoxCount());
                }
                edtLineOutStockNum.setText(String.valueOf(sumAll));
                for (int j=0;j<SumbittransportSuppliers.size();j++){
                    if(SumbittransportSuppliers.get(j).getPalletNo().equals(palletno)&&!SumbittransportSuppliers.get(j).getGUID().equals("1")){
                        SumbittransportSuppliers.get(j).setBoxCount(sumAll+"");
                    }
                }
                BindListVIew(SumbittransportSuppliers);
            }catch (Exception ex){
                MessageBox.Show(context,ex.getMessage());
                CommonUtil.setEditFocus(edtLineStockOutScanBarcode);
            }

        }
    }

    private void BindListVIew(ArrayList<TransportSupplier> transportSupplier) {
        carListAdapter=new CarListAdapter(context,transportSupplier);
        lsvLineStockOutProduct.setAdapter(carListAdapter);
    }



    void ClearFrm(){
        carno="";
        palletno="";
        SumbittransportSuppliers = new ArrayList<>();
        edtLineStockOutScanBarcode.setText("");
        edtLineOutStockNum.setText("");
        edtcar.setText("");
        BindListVIew(SumbittransportSuppliers);
    }

}
