package com.xx.chinetek.cywms.Landmark;

import android.content.Context;
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
import com.xx.chinetek.adapter.wms.CarList.CarListAdapter;
import com.xx.chinetek.base.BaseActivity;
import com.xx.chinetek.base.BaseApplication;
import com.xx.chinetek.base.ToolBarTitle;
import com.xx.chinetek.cyproduct.LineStockIn.LineStockInProduct;
import com.xx.chinetek.cywms.R;
import com.xx.chinetek.cywms.Receiption.ReceiptionScan;
import com.xx.chinetek.model.Car.TransportSupplier;
import com.xx.chinetek.model.Landmark.landmark;
import com.xx.chinetek.model.Landmark.landmarkwithtaskno;
import com.xx.chinetek.model.Material.BarCodeInfo;
import com.xx.chinetek.model.ReturnMsgModel;
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

@ContentView(R.layout.activity_landmark)
public class Landmark extends BaseActivity {

    String TAG_SaveTaskwithandmark="LineStockOutProduct_GetT_ScanInStockModelADF";
    private final int RESULT_SaveTaskwithandmark=101;

    String TAG_TaskForLandmark="LineStockOutProduct_GetT_SaveInStockModelADF";
    private final int RESULT_TaskForLandmark=102;

    String TAG_GetLandmark="TAG_GetLandmark";
    private final int RESULT_GetLandmark=103;



    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case RESULT_GetLandmark:
                AnalysisetT_GetLandmark((String) msg.obj);
                break;
            case RESULT_TaskForLandmark:
                AnalysisetT_TaskForLandmark((String) msg.obj);
                break;
            case RESULT_SaveTaskwithandmark:
                AnalysisetT_SaveTaskwithandmark((String) msg.obj);
                break;
            case NetworkError.NET_ERROR_CUSTOM:
                ToastUtil.show("获取请求失败_____"+ msg.obj);
                CommonUtil.setEditFocus(edtbarcode);
                break;
        }
    }


    Context context=Landmark.this;


    @ViewInject(R.id.edt_landmark)
    EditText edtlandmark;
    @ViewInject(R.id.edt_barcode)
    EditText edtbarcode;
    @ViewInject(R.id.textErpvoucherno)
    TextView textErpvoucherno;
    @ViewInject(R.id.textTaskno)
    TextView textTaskno;




    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.context = context;
        BaseApplication.toolBarTitle = new ToolBarTitle( "地标绑定-"+BaseApplication.userInfo.getWarehouseName(), true);
        x.view().inject(this);
        BaseApplication.isCloseActivity=false;
        CommonUtil.setEditFocus(edtlandmark);
    }


    @Event(value =R.id.edt_landmark,type = View.OnKeyListener.class)
    private  boolean edtcarClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            String scanno = edtlandmark.getText().toString().trim();
            if (TextUtils.isEmpty(scanno)) {
                MessageBox.Show(context,"地标不能为空！");
                return true;
            }

            final Map<String, String> params = new HashMap<String, String>();
            params.put("ModelJson", scanno);
            params.put("UserJson", GsonUtil.parseModelToJson(BaseApplication.userInfo));
            LogUtil.WriteLog(Landmark.class, TAG_GetLandmark, scanno);
            RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetLandmark, getString(R.string.Msg_GetT_SerialNoByPalletADF), context, mHandler, RESULT_GetLandmark, null,  URLModel.GetURL().GetLandmark, params, null);

        }
        return false;
    }



    @Event(value =R.id.edt_barcode,type = View.OnKeyListener.class)
    private  boolean edtLineStockOutScanBarcodeClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            String scanno = edtlandmark.getText().toString().trim();
            if (TextUtils.isEmpty(scanno)) {
                MessageBox.Show(context,"地标不能为空！");
                return true;
            }

            String code = edtbarcode.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                CommonUtil.setEditFocus(edtbarcode);
                return true;
            }
            final Map<String, String> params = new HashMap<String, String>();
            params.put("ModelJson", code);
            params.put("UserJson", code);
            LogUtil.WriteLog(Landmark.class, TAG_TaskForLandmark, code);
            RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_TaskForLandmark, getString(R.string.Msg_GetT_SerialNoByPalletADF), context, mHandler, RESULT_TaskForLandmark, null,  URLModel.GetURL().GetTaskForLandmark, params, null);
        }
        return false;
    }

    @Event(R.id.btn_change)
    private void btnchange(View view){
        model=new landmark();
        edtlandmark.setText("");
        CommonUtil.setEditFocus(edtlandmark);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_receiptbilldetail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            if (DoubleClickCheck.isFastDoubleClick(context)) {
                return false;
            }
            //提交
            if(postmodel!=null && model!=null){
                if (postmodel.getTaskNo()==null||postmodel.getTaskNo().isEmpty()||model.getID()==0){
                    MessageBox.Show(context,"绑定地标的货物没有任务或者地标扫描错误，请重新操作！");
                }else{
                    final Map<String, String> params = new HashMap<String, String>();
                    params.put("ModelJson", GsonUtil.parseModelToJson(postmodel));
                    params.put("UserJson", GsonUtil.parseModelToJson(BaseApplication.userInfo));
                    RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SaveTaskwithandmark, getString(R.string.Msg_GetT_SaveStouckOutADF),
                            context, mHandler, RESULT_SaveTaskwithandmark, null,  URLModel.GetURL().SaveTaskwithandmark, params, null);
                }

            }else{
                MessageBox.Show(context,"先绑定地标和货物再提交！");
            }
        }
        return super.onOptionsItemSelected(item);
    }


    void AnalysisetT_SaveTaskwithandmark(String result){
        LogUtil.WriteLog(Landmark.class, TAG_SaveTaskwithandmark,result);
        ReturnMsgModelList<String> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<String>>() {}.getType());
        try {
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                ClearFrm();
                MessageBox.Show(context,"提交成功！");
            } else {
                MessageBox.Show(context,returnMsgModel.getMessage());
            }
        }catch (Exception ex){
            MessageBox.Show(context,ex.toString());
        }
        CommonUtil.setEditFocus(edtbarcode);
    }

    landmark model;
    void AnalysisetT_GetLandmark(String result){
        try {
            LogUtil.WriteLog(LineStockInProduct.class, TAG_GetLandmark,result);
            ReturnMsgModel<landmark> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<landmark>>() {}.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                model = returnMsgModel.getModelJson();
                CommonUtil.setEditFocus(edtbarcode);
            } else {
                MessageBox.Show(context,returnMsgModel.getMessage());
                CommonUtil.setEditFocus(edtlandmark);
            }
        }catch (Exception ex){
            MessageBox.Show(context,ex.toString());
            CommonUtil.setEditFocus(edtlandmark);
        }
    }

    landmarkwithtaskno postmodel;
    void AnalysisetT_TaskForLandmark(String result){
        LogUtil.WriteLog(Landmark.class, TAG_TaskForLandmark,result);
        ReturnMsgModel<landmarkwithtaskno> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<landmarkwithtaskno>>() {}.getType());
        try {
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                postmodel = returnMsgModel.getModelJson();
                postmodel.setLandmarkid(model.getID());
                postmodel.setLandmarkno(model.getLandMarkNo());
                textErpvoucherno.setText("ERP单号："+postmodel.getErpVoucherNo());
                textTaskno.setText("任务号："+postmodel.getTaskNo());
                CommonUtil.setEditFocus(edtbarcode);


                //获取成功，直接提交
                if(postmodel!=null && model!=null){
                    if (postmodel.getTaskNo()==null||postmodel.getTaskNo().isEmpty()||model.getID()==0){
                        MessageBox.Show(context,"绑定地标的货物没有任务或者地标扫描错误，请重新操作！");
                    }else{
                        final Map<String, String> params = new HashMap<String, String>();
                        params.put("ModelJson", GsonUtil.parseModelToJson(postmodel));
                        params.put("UserJson", GsonUtil.parseModelToJson(BaseApplication.userInfo));
                        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SaveTaskwithandmark, getString(R.string.Msg_GetT_SaveStouckOutADF),
                                context, mHandler, RESULT_SaveTaskwithandmark, null,  URLModel.GetURL().SaveTaskwithandmark, params, null);
                    }

                }else{
                    MessageBox.Show(context,"先绑定地标和货物再提交！");
                }

            } else {
                MessageBox.Show(context,returnMsgModel.getMessage());
                textErpvoucherno.setText("");
                textTaskno.setText("");
                postmodel=null;
                CommonUtil.setEditFocus(edtbarcode);
            }
        }catch (Exception ex){
            MessageBox.Show(context,ex.toString());
            textErpvoucherno.setText("");
            textTaskno.setText("");
            postmodel=null;
            CommonUtil.setEditFocus(edtbarcode);
        }
    }

    void ClearFrm(){
        postmodel=new landmarkwithtaskno();
//        model=new landmark();
        edtbarcode.setText("");
//        edtlandmark.setText("");
        textTaskno.setText("");
        textErpvoucherno.setText("");

    }

}
