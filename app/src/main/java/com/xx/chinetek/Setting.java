package com.xx.chinetek;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.xx.chinetek.Pallet.CombinModel;
import com.xx.chinetek.base.BaseActivity;
import com.xx.chinetek.base.BaseApplication;
import com.xx.chinetek.base.ToolBarTitle;
import com.xx.chinetek.cywms.OffShelf.OffshelfBatchScanModel;
import com.xx.chinetek.cywms.R;
import com.xx.chinetek.model.Box.Boxing;
import com.xx.chinetek.model.URLModel;
import com.xx.chinetek.model.WMS.OffShelf.OutStockTaskInfo_Model;
import com.xx.chinetek.model.WMS.Print.PrintBean;
import com.xx.chinetek.model.WMS.Stock.StockInfo_Model;
import com.xx.chinetek.util.Network.RequestHandler;
import com.xx.chinetek.util.SharePreferUtil;
import com.xx.chinetek.util.dialog.LoadingDialog;
import com.xx.chinetek.util.dialog.MessageBox;
import com.xx.chinetek.util.dialog.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@ContentView(R.layout.activity_setting)
public class Setting extends BaseActivity {

    Context context = Setting.this;

    @ViewInject(R.id.edt_IPAdress)
    EditText edtIPAdress;
    @ViewInject(R.id.edt_Port)
    EditText edtPort;
    @ViewInject(R.id.edt_TimeOut)
    EditText edtTimeOut;

    //    @ViewInject(R.id.rb_WMS)
//    RadioButton rbWMS;
//    @ViewInject(R.id.rb_Product)
//    RadioButton rbProduct;
//    @ViewInject(R.id.supplier_checkBox)
//    CheckBox    mCheckBox;
//    @ViewInject(R.id.testBluetooth)
//    Button      mTestBluetoothButton;
    final int LogUploadIndex = 1;

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.context = context;
        BaseApplication.toolBarTitle = new ToolBarTitle(getString(R.string.login_setting), true);
        x.view().inject(this);
        SharePreferUtil.ReadSupplierShare(context);
//        if (URLModel.isSupplier){
//            mCheckBox.setChecked(true);
//            mTestBluetoothButton.setVisibility(View.VISIBLE);
//        }else {
//            mCheckBox.setChecked(false);
//            mTestBluetoothButton.setVisibility(View.INVISIBLE);
//        }


    }

    @Override
    protected void initData() {
        super.initData();
        BaseApplication.DialogShowText = getString(R.string.Msg_UploadLogFile);
        SharePreferUtil.ReadShare(context);
        edtIPAdress.setText(URLModel.IPAdress);
        edtPort.setText(URLModel.Port + "");
//        edtIPAdress.setEnabled(false);
//        edtPort.setEnabled(false);
//        if(URLModel.isWMS) rbWMS.setChecked(true); else rbProduct.setChecked(true);
        edtTimeOut.setText(RequestHandler.SOCKET_TIMEOUT / 1000 + "");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menusetting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            final LoadingDialog dialog = new LoadingDialog(context);
            dialog.show();
            String url = "http://" + URLModel.IPAdress + ":" + URLModel.Port + "/UpLoad.ashx";
            File[] files = new File(Environment.getExternalStorageDirectory() + "/wmshht/").listFiles();
            final List<File> list = Arrays.asList(files);
            Collections.sort(list, new FileComparator());

            for (int i = 0; i < LogUploadIndex; i++) {
                final int index = i;
                RequestParams params = new RequestParams(url);
                params.setMultipart(true);
                params.addBodyParameter("file", new File(list.get(list.size() - i - 1).getAbsolutePath()));
                x.http().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        //加载成功回调，返回获取到的数据
                        if (index == LogUploadIndex - 1) {
                            ToastUtil.show(result);
                        }
                    }

                    @Override
                    public void onFinished() {
                        if (index == LogUploadIndex - 1) {
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        ToastUtil.show(ex.toString());
                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public class FileComparator implements Comparator<File> {
        public int compare(File file1, File file2) {
            if (file1.getName().compareTo(file2.getName()) < 1) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    @Event(R.id.btn_SaveSetting)
    private void btnSetting(View view) {
        String IPAdress = edtIPAdress.getText().toString().trim();

        Integer Port = Integer.parseInt(edtPort.getText().toString().trim());
        Integer TimeOut = Integer.parseInt(edtTimeOut.getText().toString().trim()) * 1000;
        SharePreferUtil.SetShare(context, IPAdress, "", "", Port, TimeOut, true);
        new AlertDialog.Builder(context).setTitle("提示").setCancelable(false).setMessage(getResources().getString(R.string.SaveSuccess)).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                closeActiviry();
            }
        }).show();

    }

    @Event(R.id.btn_SaveMac)
    private void btnTestBluetooth(View view) {
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, 1);
    }

    @Event(R.id.btn_Test)
    private void btnTest(View view) {
        try {
            OutStockTaskInfo_Model model1 = new OutStockTaskInfo_Model();
            ArrayList<Boxing> modellist = new ArrayList<Boxing>();
            StockInfo_Model stockmodel = new StockInfo_Model();
            OffshelfBatchScanModel model = new OffshelfBatchScanModel(Setting.this);
//            PrintBean bean = new PrintBean();
//            bean.setMaterialNo("10100102572");
////            bean.setMaterialDesc("端门把手端门");
//            bean.setMaterialDesc("端门把手端门把手端门把手端门把手端门把手端门把手端门把手端门把手端门把手端门把手10端门把手");
//            bean.setTraceNo("202005061600");
//            bean.setProjectNo("12345");
//            bean.setBarcode("1@JSJC@10100102572@202001@10@2005061432498005607501");
//            bean.setSerialNo("2005061432498005607501");
//            bean.setAreaNo("A01-01");
//            bean.setQty(10);
//
            PrintBean bean2 = new PrintBean();
            bean2.setMaterialNo("10100102572");
            bean2.setMaterialDesc("端门把手端门");
//            bean2.setMaterialDesc("端门把手端门");
            bean2.setTraceNo("202005061600");
            bean2.setProjectNo("12345");
            bean2.setBarcode("1@JSJC@10100102572@202001@10@2005061432498005607501");
            bean2.setSerialNo("2005061432498005607501");
            bean2.setAreaNo("A01-01");
            bean2.setQty(10);
            bean2.setSpec("#规#型#号#");
            bean2.setStandardBox("#库#存#代#码#");
            List<PrintBean> list=new ArrayList<>();
//            list.add(bean2);
//            list.add(bean);
            list.add(bean2);
            model.ptintLPK130OutBarcode(list);
                        PrintBean bean3 = new PrintBean();
                        bean3.setPalletNo("P4200508000863");
            CombinModel combinModel=new CombinModel(Setting.this);
            combinModel.ptintLPK130PalletNo(bean3);
//            LPK130DEMO1Pallet("$3342352523523525");
//            LPK130DEMO(model1,modellist,stockmodel,"");
        } catch (Exception ex) {
            MessageBox.Show(context, ex.toString());
        }

    }


}
