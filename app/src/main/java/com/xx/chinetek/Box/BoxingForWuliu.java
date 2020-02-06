package com.xx.chinetek.Box;

import android.content.Context;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import com.xx.chinetek.model.Box.Boxing;
import com.android.volley.Request;
import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.PrintConnectActivity;
import com.xx.chinetek.adapter.wms.Pallet.PalletItemAdapter;
import com.xx.chinetek.base.BaseApplication;
import com.xx.chinetek.base.ToolBarTitle;
import com.xx.chinetek.cywms.R;
import com.xx.chinetek.model.Base_Model;
import com.xx.chinetek.model.CheckNumRefMaterial;
import com.xx.chinetek.model.Material.BarCodeInfo;
import com.xx.chinetek.model.ReturnMsgModel;
import com.xx.chinetek.model.ReturnMsgModelList;
import com.xx.chinetek.model.URLModel;
import com.xx.chinetek.model.WMS.Stock.StockInfo_Model;
import com.xx.chinetek.util.Network.NetworkError;
import com.xx.chinetek.util.Network.RequestHandler;
import com.xx.chinetek.util.SharePreferUtil;
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
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activityboxingforwuliu)
public class BoxingForWuliu extends PrintConnectActivity {

    String  TAG_Analysis_getListADF="Boxing_Analysis_getListADF";
    private  final  int RESULT_Analysis_getListADF=102;

    String  TAG_SaveT_BarCodeToStockLanyaADF="Boxing_SaveT_BarCodeToStockLanyaADF";
    private  final  int RESULT_SaveT_BarCodeToStockLanyaADF=103;

    @Override
    public void onHandleMessage(Message msg) {

        switch (msg.what) {
            case RESULT_Analysis_getListADF:
                Analysis_getListADF((String) msg.obj);
                break;
            case RESULT_SaveT_BarCodeToStockLanyaADF:
                Analysis_SaveT_BarCodeToStockLanyaADF((String) msg.obj);
                break;
            case NetworkError.NET_ERROR_CUSTOM:
                ToastUtil.show("获取请求失败_____"+ msg.obj);
                break;
        }
    }

    Context context=BoxingForWuliu.this;

    PalletItemAdapter palletItemAdapter;
//    @ViewInject(R.id.edt_BoxCode)
//    EditText edtBoxCode;
//    @ViewInject(R.id.edt_UnboxCode)
//    EditText edtUnboxCode;
    @ViewInject(R.id.edt_Pallet)
    EditText edtPallet;
    @ViewInject(R.id.lsv_PalletDetail)
    ListView lsvPalletDetail;


    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.context = context;
        BaseApplication.toolBarTitle = new ToolBarTitle("拼箱", false);
        x.view().inject(this);
        BaseApplication.isCloseActivity=false;
    }

    @Override
    protected void initData() {
        super.initData();

    }



    @Event(value ={R.id.edt_Pallet} ,type = View.OnKeyListener.class)
    private  boolean edtPalletKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            final Map<String, String> params = new HashMap<String, String>();
            params.put("strSerialNo", edtPallet.getText().toString().trim());
            SharePreferUtil.ReadSupplierShare(context);
            RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_Analysis_getListADF, "获取清单信息", context, mHandler, RESULT_Analysis_getListADF, null,  URLModel.GetURL().GetBoxListADF, params, null);
            CommonUtil.setEditFocus(edtPallet);
            return false;
        }
        return false;
    }

    private void BindListVIew(List<Boxing> Boxinginfos) {
        ArrayList<BarCodeInfo> BarCodeInfos = new ArrayList<>();
        for (int i=0;i<Boxinginfos.size();i++){
            BarCodeInfo model = new BarCodeInfo();
            model.setBarCode(Boxinginfos.get(i).getSerialNo());
            model.setMaterialDesc(Boxinginfos.get(i).getTaskNo());
            model.setBatchNo(Boxinginfos.get(i).getSerialNo());
            BarCodeInfos.add(model);
        }

        palletItemAdapter = new PalletItemAdapter(context, BarCodeInfos);
        lsvPalletDetail.setAdapter(palletItemAdapter);
    }

    @Event(R.id.btn_BoxConfig)
    private void BtnBoxConfigClick(View v){
        if (DoubleClickCheck.isFastDoubleClick(context)) {
            return;
        }
        String userJson = GsonUtil.parseModelToJson(BaseApplication.userInfo);
        String modelJson = GsonUtil.parseModelToJson(models);
        final Map<String, String> params = new HashMap<String, String>();
        params.put("UserJson", userJson);
        params.put("ModelJson", modelJson);
        SharePreferUtil.ReadSupplierShare(context);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SaveT_BarCodeToStockLanyaADF, "保存数据", context, mHandler, RESULT_SaveT_BarCodeToStockLanyaADF, null,  URLModel.GetURL().CombinBoxListADF, params, null);

    }



    ArrayList<Boxing> models = new ArrayList<Boxing>();
    void  Analysis_getListADF(String result){
        try {
            LogUtil.WriteLog(BoxingForWuliu.class, TAG_SaveT_BarCodeToStockLanyaADF, result);
            ReturnMsgModel<Boxing> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<Boxing>>() {
            }.getType());
            if(returnMsgModel.getHeaderStatus().equals("S")){
                Boxing model = returnMsgModel.getModelJson();
                //检验清单是否是属于同一个客户的
                if(models.size()>0){
                    if(!models.get(0).getCustomerNo().equals(model.getCustomerNo())){
                        MessageBox.Show(context,"扫描的清单的客户和之前扫描的不一致，不能拼箱！");
                        return;
                    }

                    for(int i=0;i<models.size();i++){
                        if(models.get(i).getSerialNo().equals(model.getSerialNo())){
                            MessageBox.Show(context,"已经被扫描！");
                            return;
                        }
                    }
                }

                models.add(model);
                BindListVIew(models);
            }else{
                MessageBox.Show(context,returnMsgModel.getMessage());
            }
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
        }
        CommonUtil.setEditFocus(edtPallet);
    }



  void  Analysis_SaveT_BarCodeToStockLanyaADF(String result){
      try {
          LogUtil.WriteLog(BoxingForWuliu.class, TAG_SaveT_BarCodeToStockLanyaADF, result);
          ReturnMsgModelList<Boxing> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<Boxing>>() {
          }.getType());
          if(returnMsgModel.getHeaderStatus().equals("S")){
              MessageBox.Show(context,"操作成功！");
              models = new ArrayList<Boxing>();
              BindListVIew(models);
          }
      } catch (Exception ex) {
          MessageBox.Show(context, ex.getMessage());
      }
      CommonUtil.setEditFocus(edtPallet);
  }


}
