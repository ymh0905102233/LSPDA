package com.xx.chinetek.cywms.Query;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.adapter.wms.Query.AddProductAdapter;
import com.xx.chinetek.adapter.wms.Query.AddProductQHAdapter;
import com.xx.chinetek.base.BaseActivity;
import com.xx.chinetek.base.BaseApplication;
import com.xx.chinetek.base.ToolBarTitle;
import com.xx.chinetek.cywms.R;
import com.xx.chinetek.cywms.Receiption.ReceiptBillChoice;
import com.xx.chinetek.model.Base_Model;
import com.xx.chinetek.model.ReturnMsgModel;
import com.xx.chinetek.model.ReturnMsgModelList;
import com.xx.chinetek.model.URLModel;
import com.xx.chinetek.model.WMS.Stock.MoveTaskDetailInfo_Model;
import com.xx.chinetek.util.Network.NetworkError;
import com.xx.chinetek.util.Network.RequestHandler;
import com.xx.chinetek.util.dialog.MessageBox;
import com.xx.chinetek.util.dialog.ToastUtil;
import com.xx.chinetek.util.function.GsonUtil;
import com.xx.chinetek.util.log.LogUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_add_product)
public class AddProductQHActivity extends BaseActivity {

    Context context = AddProductQHActivity.this;
    String TAG_GetT_MoveTaskInfo = "GetT_MoveTaskInfo";
    private final int RESULT_GetT_MoveTaskInfo = 101;

    String TAG_Save_MoveTask = "Save_MoveTask";
    private final int RESULT_Save_MoveTask = 102;

    @ViewInject(R.id.lv_add_product)
    ListView lvAddProduct;

    @ViewInject(R.id.btn_addproduct_addtask)
    Button btnAddTask;

    List<MoveTaskDetailInfo_Model> listMoveDetail = null;
    AddProductQHAdapter addProductAdapter;

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.context = context;
        BaseApplication.toolBarTitle = new ToolBarTitle("欠货补货" + "-" + BaseApplication.userInfo.getWarehouseName(), false);
        x.view().inject(this);

    }

    @Override
    protected void initData() {
        super.initData();
        GetT_InStockList();
    }

    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case RESULT_GetT_MoveTaskInfo:
                AnalysisGetT_InStockList((String) msg.obj);
                break;
            case RESULT_Save_MoveTask:
                AnalysisSave_MoveTask((String) msg.obj);
                break;
            case NetworkError.NET_ERROR_CUSTOM:
                ToastUtil.show("获取请求失败_____" + msg.obj);
                break;
        }
    }

    @Event(value = R.id.btn_addproduct_addtask, type = View.OnClickListener.class)
    private void onClick(View v) {
        if (listMoveDetail == null || listMoveDetail.size() == 0) {
            MessageBox.Show(context, "没有要保存的补货任务");
            return;
        }
        Save_MoveTask();
    }

    void GetT_InStockList() {
        try {

            MoveTaskDetailInfo_Model moveDetailInfo_model = new MoveTaskDetailInfo_Model();
            moveDetailInfo_model.setFromErpWarehouse(String.valueOf(BaseApplication.userInfo.getWarehouseID()));
            String ModelJson = GsonUtil.parseModelToJson(moveDetailInfo_model);
            Map<String, String> params = new HashMap<>();
            params.put("UserJson", GsonUtil.parseModelToJson(BaseApplication.userInfo));
            params.put("ModelJson", ModelJson);
            LogUtil.WriteLog(ReceiptBillChoice.class, TAG_GetT_MoveTaskInfo, ModelJson);
            RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_GetT_MoveTaskInfo, "获取补货清单", context, mHandler, RESULT_GetT_MoveTaskInfo, null, URLModel.GetURL().GetT_MoveTaskScatInfo, params, null);
        } catch (Exception ex) {

            MessageBox.Show(context, ex.getMessage());
        }
    }

    void AnalysisGetT_InStockList(String result) {
        try {

            ReturnMsgModelList<MoveTaskDetailInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<MoveTaskDetailInfo_Model>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                listMoveDetail = returnMsgModel.getModelJson();
                 addProductAdapter = new AddProductQHAdapter(context, listMoveDetail);
                lvAddProduct.setAdapter(addProductAdapter);
            }else{
                MessageBox.Show(context,returnMsgModel.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void Save_MoveTask() {
        try {
            MoveTaskDetailInfo_Model moveDetailInfo_model = new MoveTaskDetailInfo_Model();
            if (listMoveDetail!=null&&listMoveDetail.size()>0){
                for (int i=0;i<listMoveDetail.size();i++){
                    listMoveDetail.get(0).setToErpWarehouse(BaseApplication.userInfo.getWarehouseID()+"");
                }
            }

            String ModelJson = GsonUtil.parseModelToJson(listMoveDetail);
            Map<String, String> params = new HashMap<>();
            params.put("UserJson", GsonUtil.parseModelToJson(BaseApplication.userInfo));
            params.put("ModelJson", ModelJson);
            LogUtil.WriteLog(ReceiptBillChoice.class, TAG_Save_MoveTask, ModelJson);
            RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_Save_MoveTask, "保存补货任务", context, mHandler, RESULT_Save_MoveTask, null, URLModel.GetURL().Save_MoveTaskScat, params, null);
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
        }
    }

    void AnalysisSave_MoveTask(String result) {
        try {

//            ReturnMsgModelList<MoveDetailInfo_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<MoveDetailInfo_Model>>() {
//            }.getType());
            ReturnMsgModel<Base_Model> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<Base_Model>>() {
            }.getType());
                if (returnMsgModel.getHeaderStatus().equals("S")) {
                    new AlertDialog.Builder(context).setTitle("提示").setCancelable(false).setIcon(android.R.drawable.ic_dialog_info).setMessage(returnMsgModel.getMessage())
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO 自动生成的方法
                                    btnAddTask.setEnabled(false);
                                }
                            }).show();
                } else {
                    MessageBox.Show(context, returnMsgModel.getMessage());
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
