package com.xx.chinetek.cywms.Query.PatchBarcode;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.adapter.wms.Query.PatchBarcodeItemAdapter;
import com.xx.chinetek.base.BaseActivity;
import com.xx.chinetek.base.BaseApplication;
import com.xx.chinetek.cywms.R;
import com.xx.chinetek.model.Material.BarCodeInfo;
import com.xx.chinetek.model.ReturnMsgModelList;
import com.xx.chinetek.model.URLModel;
import com.xx.chinetek.util.Network.NetworkError;
import com.xx.chinetek.util.Network.RequestHandler;
import com.xx.chinetek.util.dialog.MessageBox;
import com.xx.chinetek.util.dialog.ToastUtil;
import com.xx.chinetek.util.function.CommonUtil;
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

@ContentView(R.layout.activity_query)
public class PatchBarcode extends BaseActivity {

    String TAG_GetOutBarCodeForPrint = "Query_GetOutBarCodeForPrint";

    private final int Result_Msg_GetOutBarCodeForPrint = 101;

    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case Result_Msg_GetOutBarCodeForPrint:
                AnalysisGetOutBarCodeForPrintJson((String) msg.obj);
                break;
            case NetworkError.NET_ERROR_CUSTOM:
                ToastUtil.show("获取请求失败_____" + msg.obj);
                CommonUtil.setEditFocus(edtqueryScanBarcode);
                break;
        }
    }

    Context context = PatchBarcode.this;
    @ViewInject(R.id.txtname)
    TextView txtname;
    @ViewInject(R.id.lsvQuery)
    ListView lsvQuery;
    @ViewInject(R.id.edt_queryScanBarcode)
    EditText edtqueryScanBarcode;

    PatchBarcodeItemAdapter queryItemAdapter;
    int                     Type = -1;

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.context = context;
        x.view().inject(this);
        String Context = getIntent().getStringExtra("MaterialNO");
        txtname.setText("条码号：");
        Type = getIntent().getIntExtra("Type", -1);
        if (!TextUtils.isEmpty(Context)) {
            txtname.setVisibility(View.GONE);
            edtqueryScanBarcode.setVisibility(View.GONE);
            GetBarcodeInfo(Context);
        }
    }


    @Event(value = R.id.edt_queryScanBarcode, type = View.OnKeyListener.class)
    private boolean edtqueryScanBarcodeClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
            keyBoardCancle();
            String barcode = edtqueryScanBarcode.getText().toString().trim();
            GetBarcodeInfo(barcode);
        }
        return false;
    }

    /**
     * @desc: 获取条码信息
     * @param:
     * @return:
     * @author:
     * @time 2020/4/26 11:29
     */
    void GetBarcodeInfo(String barcode) {
        if (!TextUtils.isEmpty(barcode)) {
            final Map<String, String> params = new HashMap<String, String>();
            params.put("BarCode", barcode);
            String para = (new JSONObject(params)).toString();
            LogUtil.WriteLog(PatchBarcode.class, TAG_GetOutBarCodeForPrint, para);
            RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetOutBarCodeForPrint, String.format(getString(R.string.Msg_QueryStockInfo), BaseApplication.toolBarTitle.Title), context, mHandler, Result_Msg_GetOutBarCodeForPrint, null, URLModel.GetURL().GetOutBarCodeForPrint, params, null);
        }
    }

    /**
     * @desc: 获取条码补打信息返回结果
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/4/26 11:34
     */
    void AnalysisGetOutBarCodeForPrintJson(String result) {
        LogUtil.WriteLog(PatchBarcode.class, TAG_GetOutBarCodeForPrint, result);
        try {
            List<BarCodeInfo> list = new ArrayList<>();
            ReturnMsgModelList<BarCodeInfo> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<BarCodeInfo>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                list = returnMsgModel.getModelJson();
            } else {
                MessageBox.Show(context, returnMsgModel.getMessage());
            }
            queryItemAdapter = new PatchBarcodeItemAdapter(context, list);
            lsvQuery.setAdapter(queryItemAdapter);
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());

        }
        CommonUtil.setEditFocus(edtqueryScanBarcode);
    }
}
