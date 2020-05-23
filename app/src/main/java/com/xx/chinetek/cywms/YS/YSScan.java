package com.xx.chinetek.cywms.YS;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.xx.chinetek.adapter.wms.Receiption.ReceiptScanDetailAdapter;
import com.xx.chinetek.base.BaseActivity;
import com.xx.chinetek.base.BaseApplication;
import com.xx.chinetek.base.ToolBarTitle;
import com.xx.chinetek.cywms.R;
import com.xx.chinetek.cywms.Receiption.ReceiptionBillDetail;
import com.xx.chinetek.cywms.SplitZero.SplitZeroScan;
import com.xx.chinetek.model.Material.BarCodeInfo;
import com.xx.chinetek.model.Receiption.ReceiptDetail_Model;
import com.xx.chinetek.model.Receiption.Receipt_Model;
import com.xx.chinetek.util.dialog.MessageBox;
import com.xx.chinetek.util.function.CommonUtil;
import com.xx.chinetek.util.function.DoubleClickCheck;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.xx.chinetek.cywms.R.id.lsv_ReceiptScan;


@ContentView(R.layout.activity_receiption_scan)
public class YSScan extends BaseActivity implements IYSScanView {
    Context context = YSScan.this;
    @ViewInject(R.id.lsv_ReceiptScan)
    ListView mListView;
    @ViewInject(R.id.edt_RecScanBarcode)
    EditText mBarcode;
    @ViewInject(R.id.txt_VoucherNo)
    TextView txtVoucherNo;
    @ViewInject(R.id.txt_ReceQTY)
    TextView txtInStockQty;
    @ViewInject(R.id.txt_CanReceQty)
    TextView txtRemainQty;
    @ViewInject(R.id.txt_ReceScanQty)
    TextView txtReceiveQty;
    @ViewInject(R.id.txt_ScanQty)
    TextView txtScanQty;
    @ViewInject(R.id.txt_Company)
    TextView txtCompany;
    @ViewInject(R.id.txt_Batch)
    TextView txtBatch;
    @ViewInject(R.id.txt_Status)
    TextView txtStatus;
    @ViewInject(R.id.txt_EDate)
    TextView txtEDate;
    @ViewInject(R.id.txt_MaterialName)
    TextView txtMaterialName;
    @ViewInject(R.id.txtAll)
    TextView txtAll1;
    @ViewInject(R.id.edt_area_no)
    EditText mAreaNo;
    @ViewInject(R.id.textView33)
    TextView mAreaName;
    ReceiptScanDetailAdapter       mAdapter;
    ArrayList<ReceiptDetail_Model> receiptDetailModels = new ArrayList<>();
    Receipt_Model                  receiptModel        = null;
    UUID                           mUuid               = null;

    YSScanPresenter mPresenter;

    @Override
    public void onHandleMessage(Message msg) {
        super.onHandleMessage(msg);
        if (mPresenter != null) {
            mPresenter.getModel().onHandleMessage(msg);
        }
    }


    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.context = context;
        BaseApplication.toolBarTitle = new ToolBarTitle(getString(R.string.ys_title) + "-" + BaseApplication.userInfo.getWarehouseName(), true);
        x.view().inject(this);
        BaseApplication.isCloseActivity = false;
        setAreaBar(false);
        setViewStatus();
    }

    /**
     * @desc: 这些是不要的控件
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/5/9 21:37
     */
    private void setViewStatus() {
        txtInStockQty.setVisibility(View.GONE);
        txtRemainQty.setVisibility(View.GONE);
        txtReceiveQty.setVisibility(View.GONE);
        txtScanQty.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        super.initData();
        receiptModel = getIntent().getParcelableExtra("receiptModel");
        mPresenter = new YSScanPresenter(context, this, mHandler);
        if (mPresenter != null) {
            mPresenter.onRequestYSDetails(receiptModel);
        }

    }


    /**
     * @desc: 条码扫描
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/5/8 20:25
     */
    @Event(value = R.id.edt_RecScanBarcode, type = View.OnKeyListener.class)
    private boolean edtRecScanBarcode(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            keyBoardCancle();
            String code = mBarcode.getText().toString().trim();
            if (mPresenter != null) {
                mPresenter.onScan(code);
            }

        }
        return false;
    }

    /**
     * @desc: 条码明细
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/5/9 7:42
     */
    @Event(value = lsv_ReceiptScan, type = AdapterView.OnItemClickListener.class)
    private boolean lsv_ReceiptScanItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (id >= 0) {
            ReceiptDetail_Model receiptDetailModel = receiptDetailModels.get(position);
            try {
                if (receiptDetailModel.getLstBarCode() != null && receiptDetailModel.getLstBarCode().size() != 0) {
                    Intent intent = new Intent(context, ReceiptionBillDetail.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("receiptDetailModel", receiptDetailModel);
                    intent.putExtras(bundle);
                    startActivityLeft(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ys, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            if (DoubleClickCheck.isFastDoubleClick(context)) {
                return false;
            }
            try {

                mPresenter.onRefer();
            } catch (Exception ex) {
                MessageBox.Show(context, ex.toString());
            }

        }else if (item.getItemId() == R.id.action_split){
            Intent intent=new Intent();
            intent.setClass(YSScan.this, SplitZeroScan.class);
            startActivityLeft(intent);
        }
            return super.onOptionsItemSelected(item);
    }

    /**
     * @desc: 是否显示库位栏
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/5/4 11:00
     */
    private void setAreaBar(boolean visiable) {
        if (visiable) {
            if (mAreaNo.getVisibility() == View.GONE) {
                mAreaNo.setVisibility(View.VISIBLE);
            }
            if (mAreaName.getVisibility() == View.GONE) {
                mAreaName.setVisibility(View.VISIBLE);
            }
        } else {
            mAreaNo.setVisibility(View.GONE);
            mAreaName.setVisibility(View.GONE);
        }
    }

    @Override
    public void requestScanBarcodeFocus() {
        CommonUtil.setEditFocus(mBarcode);
    }

    @Override
    public void onClear() {
        mBarcode.setText("");
        requestScanBarcodeFocus();
        bindListView(null);
    }

    @Override
    public void bindListView(List<ReceiptDetail_Model> list) {
        if (mListView.getVisibility() == View.GONE) {
            mListView.setVisibility(View.VISIBLE);
        }
        if (list == null || list.size() == 0) {
            mListView.setVisibility(View.GONE);
            return;
        } else {
            mListView.setVisibility(View.VISIBLE);
        }
        mAdapter = new ReceiptScanDetailAdapter(context, "", list);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void closeActivity() {
        closeActiviry();
    }

    @Override
    public void setOrderNo(String orderNo) {
        txtVoucherNo.setText(orderNo);
    }

    @Override
    public void setBarcodeInfo(BarCodeInfo barCodeInfo) {
        if (barCodeInfo != null) {
            txtCompany.setText(barCodeInfo.getStrongHoldCode());
            txtBatch.setText(barCodeInfo.getMaterialNo());
            txtStatus.setText(barCodeInfo.getBatchNo());
            txtMaterialName.setText(barCodeInfo.getMaterialDesc());
            txtEDate.setText(CommonUtil.DateToString(barCodeInfo.getEDate()));
        }
        requestScanBarcodeFocus();
    }
}
