package com.xx.chinetek.cywms.Car;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request;
import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.adapter.wms.CarList.CarListAdapter;
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

@ContentView(R.layout.activity_line_query)
public class CarQuery extends BaseActivity {

    String TAG_GetT_PalletDetailByBarCode="CarQuery_GetT_PalletDetailByBarCode";
    private final int RESULT_Msg_GetT_PalletDetailByBarCode=101;


    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case RESULT_Msg_GetT_PalletDetailByBarCode:
                AnalysisetT_PalletDetailByBarCodeJson((String) msg.obj);
                break;
            case NetworkError.NET_ERROR_CUSTOM:
                ToastUtil.show("获取请求失败_____"+ msg.obj);
                break;
        }
    }


    Context context=CarQuery.this;

    @ViewInject(R.id.lsv_LineStockOutProduct)
    ListView lsvLineStockOutProduct;
    @ViewInject(R.id.edt_car)
    EditText edtcar;


    CarListAdapter carListAdapter;
    String carno="";

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.context = context;
        BaseApplication.toolBarTitle = new ToolBarTitle(  "查询-"+BaseApplication.userInfo.getUserName(), true);
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

            final Map<String, String> params = new HashMap<String, String>();
            params.put("CarNo", carno);
            LogUtil.WriteLog(ReceiptionScan.class, TAG_GetT_PalletDetailByBarCode, carno);
            RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetT_PalletDetailByBarCode,"获取信息中...", context, mHandler, RESULT_Msg_GetT_PalletDetailByBarCode, null,  URLModel.GetURL().GetTransportSupplierDetailListForQueryADF, params, null);

        }
        return false;
    }

    ArrayList<TransportSupplier> SumbitTransportSuppliers = new ArrayList<>();
    void AnalysisetT_PalletDetailByBarCodeJson(String result){
        LogUtil.WriteLog(LineStockInProduct.class, TAG_GetT_PalletDetailByBarCode,result);
        ReturnMsgModelList<TransportSupplier> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<TransportSupplier>>() {}.getType());
        try {
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                SumbitTransportSuppliers = returnMsgModel.getModelJson();
                BindListVIew(SumbitTransportSuppliers);
            } else {
                MessageBox.Show(context,returnMsgModel.getMessage());
                return;
            }
        }catch (Exception ex){
            MessageBox.Show(context,ex.toString());
        }


    }


    private void BindListVIew(ArrayList<TransportSupplier> transportSupplier) {
        carListAdapter=new CarListAdapter(context,transportSupplier);
        lsvLineStockOutProduct.setAdapter(carListAdapter);
    }

}
