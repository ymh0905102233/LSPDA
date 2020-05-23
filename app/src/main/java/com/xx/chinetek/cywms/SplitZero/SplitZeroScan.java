package com.xx.chinetek.cywms.SplitZero;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.xx.chinetek.DeviceListActivity;
import com.xx.chinetek.base.BaseActivity;
import com.xx.chinetek.base.BaseApplication;
import com.xx.chinetek.base.ToolBarTitle;
import com.xx.chinetek.cywms.OffShelf.OffshelfBatchScanModel;
import com.xx.chinetek.cywms.R;
import com.xx.chinetek.model.WMS.Stock.StockInfo_Model;
import com.xx.chinetek.util.dialog.MessageBox;
import com.xx.chinetek.util.function.CommonUtil;
import com.xx.chinetek.util.function.DoubleClickCheck;
import com.xx.chinetek.util.printutils.interfaces.PrintCallBackListener;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import static com.xx.chinetek.cywms.R.id.tb_UnboxType;

/**
 * @ Des:拆零模块
 * * @ Created by yangyiqing on 2019/11/14.
 */
@ContentView(R.layout.activity_split_zero_scan)
public class SplitZeroScan extends BaseActivity implements ISplitZeroView {
    @ViewInject(R.id.txt_Company)
    TextView     txtCompany;
    @ViewInject(R.id.txt_Batch)
    TextView     txtBatch;
    @ViewInject(R.id.txt_Status)
    TextView     mQty;
    @ViewInject(R.id.txt_EDate)
    TextView     txtEDate;
    @ViewInject(R.id.txt_MaterialName)
    TextView     txtMaterialName;
    @ViewInject(tb_UnboxType)
    ToggleButton tbUnboxType;
    @ViewInject(R.id.tb_BoxType)
    ToggleButton tbBoxType;
    @ViewInject(R.id.edt_OffShelfScanbarcode)
    EditText     mFatherBarcode;
    @ViewInject(R.id.edt_Unboxing)
    EditText     mNumber;
    @ViewInject(R.id.edt_jian)
    EditText     mSubBarcode;
    @ViewInject(R.id.edt_car)
    EditText     edtcar;
    @ViewInject(R.id.txt_SugestStock)
    TextView     txtSugestStock;
    @ViewInject(R.id.txt_OffshelfNum)
    TextView     txtOffshelfNum;
    @ViewInject(R.id.txt_currentPickNum)
    TextView     txtcurrentPickNum;
    @ViewInject(R.id.txt_Unboxing)   //拆零数量
    TextView     txtUnboxing;
    @ViewInject(R.id.txt_jian)    //本体条码
    TextView     txtjian;
    @ViewInject(R.id.btn_OutOfStock)
    TextView     btnOutOfStock;
    @ViewInject(R.id.btn_BillDetail)
    TextView     btnBillDetail;
    @ViewInject(R.id.btn_PrintBox)
    TextView     btnPrintBox;
    @ViewInject(R.id.lsv_PickList)
    ListView     lsvPickList;
    @ViewInject(R.id.edt_StockScan)
    EditText     edtStockScan;
    @ViewInject(R.id.textView133)
    TextView     textView133;
    @ViewInject(R.id.txterpvoucherno)
    TextView     txterpvoucherno;
    @ViewInject(R.id.txtcustomername)
    TextView     txtcustomername;
    @ViewInject(R.id.btn_inner_barcode_button)
    ToggleButton mInnerBarcodeButton;
    @ViewInject(R.id.btn_input_qty_button)
    ToggleButton mInputQtyButton;
    @ViewInject(R.id.btn_TJ)
    Button       mSplitCommitButton;

    private Context            mContext;
    private SplitZeroPresenter mPresenter;
    OffshelfBatchScanModel mModel;

    @Override
    public void onHandleMessage(Message msg) {
        super.onHandleMessage(msg);
        if (mPresenter != null) {
            mPresenter.onHandleMessage(msg);
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = this;
        BaseApplication.context = mContext;
        BaseApplication.toolBarTitle = new ToolBarTitle("拆零" + "-" + BaseApplication.userInfo.getWarehouseName(), true);
        x.view().inject(this);
        BaseApplication.isCloseActivity = false;
        ShowUnboxing();
    }

    @Override
    protected void initData() {
        super.initData();
        tbUnboxType.setChecked(false);
        tbBoxType.setChecked(true);
        ShowUnboxing();
        mPresenter = new SplitZeroPresenter(mContext, this, mHandler);
        onClear();
    }


    /**
     * @desc: 拆零操作条码类型
     * @param:
     * @return:
     * @author:
     * @time 2020/4/20 17:16
     */
    @Event(value = {R.id.btn_inner_barcode_button, R.id.btn_input_qty_button}, type = CompoundButton.OnClickListener.class)
    private void SplitCheckedChanged(View view) {
        if (view.getId() == R.id.tb_UnboxType) {
//            if (!CheckBluetooth()) {
//                MessageBox.Show(context, "蓝牙打印机连接失败");
//                return;
//            }
        }

        mInnerBarcodeButton.setChecked(view.getId() == R.id.btn_inner_barcode_button);
        mInputQtyButton.setChecked(view.getId() == R.id.btn_input_qty_button);
        ShowSplitButton();

    }

    /**
     * @desc: 外箱条码扫描
     * @param:
     * @return:
     * @author:
     * @time 2020/4/20 17:25
     */
    @Event(value = R.id.edt_OffShelfScanbarcode, type = View.OnKeyListener.class)
    private boolean edtOffShelfScanbarcodeClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            keyBoardCancle();
            try {
                mPresenter.scanFatherBarcode(mFatherBarcode.getText().toString().trim());
                return false;
            } catch (Exception ex) {
                MessageBox.Show(mContext, ex.toString());
                return true;
            }
        }
        return false;
    }

    /**
     * @desc: 拣货数量逻辑
     * @param:
     * @return:
     * @author:
     * @time 2020/4/20 17:17
     */
    @Event(value = R.id.edt_Unboxing, type = View.OnKeyListener.class)
    private boolean edtUnboxingClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            try {
                String num = mNumber.getText().toString().trim();  //获取界面拆零数量
                int qty = Integer.parseInt(num);
                //拆零模式提交
                mPresenter.onSplitRefer(qty);
            } catch (Exception ex) {
                MessageBox.Show(mContext, ex.toString());
                return false;
            }
        }
        return false;

    }

    /**
     * @desc: 拆零提交
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/4/20 19:38
     */
    @Event(R.id.btn_TJ)
    private void edtTJClick(View v) {
        if (DoubleClickCheck.isFastDoubleClick(mContext)) {
            return;
        }
        try {
            if (mPresenter.getModel().getSubInfo() != null) {
                mPresenter.onSplitRefer(1);
            } else {
                MessageBox.Show(mContext, "本体条码不能为空");
            }

        } catch (Exception ex) {
            MessageBox.Show(mContext, ex.toString());
        }
    }

    /**
     * @desc: 扫描本体
     * @param:
     * @return:
     * @author:
     * @time 2020/4/21 11:00
     */
    @Event(value = R.id.edt_jian, type = View.OnKeyListener.class)
    private boolean edtjianClick(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            try {
                //2-整箱或者托发货 3-零数发货
                String code = mSubBarcode.getText().toString().trim();
                mPresenter.scanSubBarcode(code);
            } catch (Exception ex) {
                MessageBox.Show(mContext, ex.toString());
            }
            return false;
        }
        return false;
    }

    /**
     * @desc: 如果是外箱模式 隐藏  拆零模式中的 本体和数量 模式按钮，以及本体和数量扫描框;
     * 如果是拆零模式下，优先显示本体扫描框，数量扫描框隐藏
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/4/21 9:29
     */
    void ShowUnboxing() {
        txtUnboxing.setVisibility(View.VISIBLE);
        mNumber.setVisibility(View.VISIBLE);
        mInputQtyButton.setVisibility(View.VISIBLE);
        mInnerBarcodeButton.setVisibility(View.VISIBLE);
        mSplitCommitButton.setVisibility(View.GONE);
        txtjian.setVisibility(View.GONE);
        mSubBarcode.setVisibility(View.GONE);
        CommonUtil.setEditFocus(mFatherBarcode);

    }

    /**
     * @desc: 拆零模式
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2020/4/21 10:29
     */
    void ShowSplitButton() {
        if (mInnerBarcodeButton.isChecked()) {
            txtUnboxing.setVisibility(View.GONE);
            mNumber.setVisibility(View.GONE);
            txtjian.setVisibility(View.VISIBLE);
            mSubBarcode.setVisibility(View.VISIBLE);
            mSplitCommitButton.setVisibility(View.VISIBLE);

        } else {
            txtUnboxing.setVisibility(View.VISIBLE);
            mNumber.setVisibility(View.VISIBLE);
            txtjian.setVisibility(View.GONE);
            mSubBarcode.setVisibility(View.GONE);
            mSplitCommitButton.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_outstock, menu);
        MenuItem item = menu.findItem(R.id.action_filter);
        if (item != null) {
            item.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
        } else if (item.getItemId() == R.id.action_bluetooth) {
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, 1);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void requestFatherBarcodeFocus() {
        CommonUtil.setEditFocus(mFatherBarcode);
    }

    @Override
    public void requestSubBarcodeFocus() {
        CommonUtil.setEditFocus(mSubBarcode);
    }

    @Override
    public void requestNumberFocus() {
        CommonUtil.setEditFocus(mNumber);
    }

    @Override
    public void setNumber(int qty) {
        mNumber.setText(qty + "");
    }

//    @Override
//    public void setSubBarcode(String barcode) {
//        mSubBarcode.setText("");
//    }


    @Override
    public void bindFatherBarcodeInfo(StockInfo_Model info) {
        if (info != null) {
            txtCompany.setText(info.getStrongHoldName() + "(" + info.getStrongHoldCode() + ")");
            txtBatch.setText(info.getBatchNo());
            mQty.setText(info.getQty() + "");
            txtMaterialName.setText(info.getMaterialDesc() + "(" + info.getMaterialNo() + ")");
        } else {
            txtCompany.setText("");
            txtBatch.setText("");
            mQty.setText("");
            txtMaterialName.setText("");
        }

    }

    @Override
    public void onClear() {
        requestFatherBarcodeFocus();
        bindFatherBarcodeInfo(null);


    }

    @Override
    public void showNumberEditText(boolean isShow) {
        if (isShow) {
            mNumber.setVisibility(View.VISIBLE);
            requestNumberFocus();
        } else {
//            mCheckBox.setChecked(false);
            mNumber.setVisibility(View.GONE);
        }
    }

    @Override
    public void showModuleView(int qty) {
        ShowSplitButton();
        if (mInnerBarcodeButton.isChecked()) {
            CommonUtil.setEditFocus(mSubBarcode);
        }
        //拆零输数量
        if (mInputQtyButton.isChecked()) {
            mNumber.setText(qty + "");
            CommonUtil.setEditFocus(mNumber);
        }

    }

    @Override
    public void onNewBarcodePrint(PrintCallBackListener listener) {
//        onPrint(listener);
    }


    @Override
    public boolean CheckBluetooth() {
        try {
            boolean flag = CheckBluetoothBase();
            return flag;
        } catch (Exception ex) {
            return false;
        }

    }
}
