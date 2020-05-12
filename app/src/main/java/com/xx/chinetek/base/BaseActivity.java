package com.xx.chinetek.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.mylibrary.LPK130;
import com.xx.chinetek.cywms.R;
import com.xx.chinetek.model.Box.Boxing;
import com.xx.chinetek.model.CheckNumRefMaterial;
import com.xx.chinetek.model.URLModel;
import com.xx.chinetek.model.WMS.OffShelf.OutStockTaskInfo_Model;
import com.xx.chinetek.model.WMS.Review.OutStock_Model;
import com.xx.chinetek.model.WMS.Stock.StockInfo_Model;
import com.xx.chinetek.util.UpdateVersionService;
import com.xx.chinetek.util.dialog.ToastUtil;
import com.xx.chinetek.util.function.CommonUtil;
import com.xx.chinetek.util.hander.IHandleMessage;
import com.xx.chinetek.util.hander.MyHandler;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.xx.chinetek.base.BaseApplication.context;

/**
 * Created by GHOST on 2017/3/15.
 */

public abstract class BaseActivity extends AppCompatActivity implements IHandleMessage {
    private ToolBarHelper mToolBarHelper;
    public Toolbar toolbar;
    public static final String ACTION_UPDATEUI = "action.updateUI";
    public MyHandler<BaseActivity> mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //屏幕保持竖屏
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //隐藏输入法
        AppManager.getAppManager().addActivity(this); //添加当前Activity到avtivity管理类
        mHandler = new MyHandler<>(this);
        BaseApplication.isCloseActivity=true;
        updateVersionService = new UpdateVersionService(context);// 创建更新业务对象
        initViews(); //自定义的方法
        initData();
    }

    /**
     * 初始化控件
     */
    protected void initViews() {

    }

    /**
     * 初始化数据
     */
    protected void initData() {
        if(BaseApplication.isCloseActivity){
            checkUpdate();
//            new UpdateAppManager
//                    .Builder()
//                    //当前Activity
//                    .setActivity(this)
//                    //更新地址
//                    .setUpdateUrl(UpdateVersionService.UPDATEVERSIONXMLPATH())
//                    //实现httpManager接口的对象
////                    .setHttpManager(new UpdateAppHttpUtil())
//                    .build()
//                    .update();
        }
//

    }



    @Override
    public void onHandleMessage(Message msg) {

    }
//
    @Override
    public void setContentView(int layoutResID) {
        if(layoutResID==R.layout.activity_login)
            getDelegate().setContentView(layoutResID);
        else {
            mToolBarHelper = new ToolBarHelper(this, layoutResID);
            toolbar = mToolBarHelper.getToolBar();
            setContentView(mToolBarHelper.getContentView());
            if (!TextUtils.isEmpty(BaseApplication.toolBarTitle.Title))
                setTitle(BaseApplication.toolBarTitle.Title);
//        if (!TextUtils.isEmpty(BaseApplication.toolBarTitle.subTitle))
//            toolbar.setSubtitle(BaseApplication.toolBarTitle.subTitle);
//        //toolbar.setLogo(R.mipmap.ic_launcher);
            if (BaseApplication.toolBarTitle.isShowBack)
                toolbar.setNavigationIcon(R.drawable.back);
        /*把 toolbar 设置到Activity 中*/
            setSupportActionBar(toolbar);
        /*自定义的一些操作*/
        if(BaseApplication.toolBarTitle!=null) {
            onCreateCustomToolBar(toolbar);}
        }

    }

    public void onCreateCustomToolBar(Toolbar toolbar) {
            toolbar.setContentInsetsRelative(0, 0);
            toolbar.showOverflowMenu();
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (!context.getClass().getName().equals("com.xx.chinetek.cywms.MainActivity") ||
//                    !context.getClass().getName().equals("com.xx.chinetek.cyproduct.MainActivity")
//                    ) {
//                        BackAlter();
//                    }
                    if(BaseApplication.isCloseActivity)
                        closeActiviry();
                    else
                        BackAlter();
                }
            });
    }



    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_UP) {
//            if(!(context.getClass().getName().equals("com.xx.chinetek.cywms.MainActivity") ||
//                    context.getClass().getName().equals("com.xx.chinetek.cyproduct.MainActivity") ||
//                    context.getClass().getName().equals("com.xx.chinetek.Login")))
//                BackAlter();
//            else{
//                closeActiviry();
//            }
            if(BaseApplication.isCloseActivity)
               closeActiviry();
            else
                BackAlter();
        }
        return true;
    }


    public void BackAlter(){
        new AlertDialog.Builder(context).setTitle("提示").setCancelable(false).setIcon(android.R.drawable.ic_dialog_info).setMessage("是否返回上一页面？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 自动生成的方法
                        closeActiviry();
                    }
                }).setNegativeButton("取消", null).show();
    }

    /**
     * 隐藏键盘
     */
    public void keyBoardCancle() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public  void closeActiviry(){
        AppManager.getAppManager().finishActivity();
        BaseApplication.isCloseActivity=true;
        if(AppManager.getAppManager().GetActivityCount()!=0)
            context = AppManager.getAppManager().currentActivity();
    }

    /**
     * 左右推动跳转
     *
     * @param intent
     */
    protected void startActivityLeft(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }



    /*
       判断单位对应数字输入规则
     */
   public CheckNumRefMaterial CheckMaterialNumFormat(String qty, String UnitTypeCode, String DecimalLngth){
        CheckNumRefMaterial checkNumRefMaterial=new CheckNumRefMaterial();
       try {
           int unitTypeCode = Integer.parseInt(UnitTypeCode);
           int decimalLngth = Integer.parseInt(DecimalLngth);

           if (unitTypeCode == 4) {
               if (CommonUtil.isNumeric(qty)) {
                   checkNumRefMaterial.setIscheck(true);
                   checkNumRefMaterial.setCheckQty(Float.parseFloat(qty));
               } else {
                   checkNumRefMaterial.setIscheck(false);
                   checkNumRefMaterial.setErrMsg(getString(R.string.Error_IntRequire));
               }
           } else {
               if (CommonUtil.isFloat(qty)) {
                   checkNumRefMaterial.setIscheck(true);
                   BigDecimal mData = new BigDecimal(qty).setScale(decimalLngth, BigDecimal.ROUND_HALF_UP);
                   checkNumRefMaterial.setCheckQty(mData.floatValue());
               } else {
                   checkNumRefMaterial.setIscheck(false);
                   checkNumRefMaterial.setErrMsg(getString(R.string.Error_isnotnum));
               }
           }
       }catch (Exception ex){
           ToastUtil.show(ex.getMessage());
       }
        return checkNumRefMaterial;
    }

    public UpdateVersionService updateVersionService;

    /**
     * 检查更新
     */
    public void checkUpdate() {

        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                if (updateVersionService.isUpdate()) {
                    handler.sendEmptyMessage(0);
                }// 调用检查更新的方法,如果可以更新.就更新.不能更新就提示已经是最新的版本了
                else {
                    handler.sendEmptyMessage(1);
                }
            }
        }.start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    updateVersionService.showDownloadDialog();
                    break;
            }
        };
    };


    //检查蓝牙打印机是否连上
    public boolean CheckBluetoothBase(){
        try{
            LPK130 lpk130 = new LPK130();
            lpk130.closeDevice();
            if (lpk130.openDevice(URLModel.MacAdress) >= 0) {
                return true;
            }else{
                return false;
            }
        }catch(Exception ex){
            return false;
        }
    }


    public void LPK130DEMO(OutStockTaskInfo_Model model, ArrayList<Boxing> modellist,StockInfo_Model stockmodel,String flag) {
        LPK130 lpk130 = new LPK130();
        lpk130.closeDevice();
        if (lpk130.openDevice(URLModel.MacAdress) >= 0) {
            try {
                if (flag=="LList") {
                    lpk130.NFCP_setLeftMargin((byte) 12);
                    lpk130.NFCP_setSnapMode((byte) 1);
                    lpk130.NFCP_setLineSpace(40);
                    lpk130.NFCP_setFontBold((byte) 1);
                    lpk130.NFCP_fontSize((byte) 1, (byte) 1);
                    lpk130.NFCP_printStrLine("装箱清单");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_fontSize((byte) 1, (byte) 1);
                    lpk130.NFCP_setSnapMode((byte) 0);
                    lpk130.NFCP_printStr("订单号：");
                    lpk130.NFCP_printStrLine(model.getErpVoucherNo());
                    lpk130.NFCP_feed(7);


                    lpk130.NFCP_printStr("客户：");
                    lpk130.NFCP_printStrLine(model.getCarNo());
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("仓库：");
                    lpk130.NFCP_printStrLine(model.getCustomerName());
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("操作人：");
                    lpk130.NFCP_printStrLine(BaseApplication.userInfo.getUserName());
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("备注：");
                    lpk130.NFCP_printStrLine(model.getERPNote());
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("产品编号              品名             数量");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);

                    Float SumQty=0f;
                    for (int i=0;i<modellist.size();i++){
                        SumQty=SumQty+modellist.get(i).getQty();
                        lpk130.NFCP_printStr("                  "+modellist.get(i).getMaterialNo()+"               "+modellist.get(i).getQty());
                        lpk130.NFCP_printStrLine("");
                        lpk130.NFCP_feed(7);
                        lpk130.NFCP_printStr(modellist.get(i).getMaterialName());
                        lpk130.NFCP_printStrLine("");
                        lpk130.NFCP_feed(7);
                        lpk130.NFCP_printStr("-----------------------------------------------");
                        lpk130.NFCP_feed(7);
                    }
                    lpk130.NFCP_printStr("合计：                                 "+SumQty);
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                    Date curDate =  new Date(System.currentTimeMillis());
                    String str = formatter.format(curDate);

                    lpk130.NFCP_printStr("      发货时间："+str);
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("扫一扫获取装箱清单编号");
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printQRcode(6, 2,modellist.get(0).getSerialNo());
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr(modellist.get(0).getSerialNo());
                    lpk130.NFCP_feed(150);

                }else if (flag=="ZList"){
                    lpk130.NFCP_setLeftMargin((byte) 12);
                    lpk130.NFCP_setSnapMode((byte) 1);
                    lpk130.NFCP_setLineSpace(40);
                    lpk130.NFCP_setFontBold((byte) 1);
                    lpk130.NFCP_fontSize((byte) 1, (byte) 1);
                    lpk130.NFCP_printStrLine("装箱清单");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_fontSize((byte) 1, (byte) 1);
                    lpk130.NFCP_setSnapMode((byte) 0);
                    lpk130.NFCP_printStr("订单号：");
                    lpk130.NFCP_printStrLine(model.getErpVoucherNo());
                    lpk130.NFCP_feed(7);


                    lpk130.NFCP_printStr("客户：");
                    lpk130.NFCP_printStrLine(model.getCustomerName());
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("仓库：");
                    lpk130.NFCP_printStrLine("实品仓");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("操作人：");
                    lpk130.NFCP_printStrLine(BaseApplication.userInfo.getUserName());
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("备注：备注1111111");
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("产品编号              品名             数量");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);


                    lpk130.NFCP_printStr("                  "+stockmodel.getMaterialNo()+"               "+stockmodel.getQty());
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr(stockmodel.getMaterialDesc());
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("合计：                                 "+stockmodel.getQty());
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);

                    SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日   HH:mm:ss");
                    Date curDate =  new Date(System.currentTimeMillis());
                    String str = formatter.format(curDate);

                    lpk130.NFCP_printStr("       发货时间："+str);
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_feed(150);
                }else{
                    lpk130.NFCP_setLeftMargin((byte) 12);
                    lpk130.NFCP_setSnapMode((byte) 1);
                    lpk130.NFCP_setLineSpace(40);
                    lpk130.NFCP_setFontBold((byte) 1);
                    lpk130.NFCP_fontSize((byte) 1, (byte) 1);
                    lpk130.NFCP_printStrLine("装箱清单");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_fontSize((byte) 1, (byte) 1);
                    lpk130.NFCP_setSnapMode((byte) 0);
                    lpk130.NFCP_printStr("订单号：");
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);


                    lpk130.NFCP_printStr("客户：上海办公室");
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("仓库：");
                    lpk130.NFCP_printStrLine("实品仓");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("操作人：李敏");
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("备注：备注1111111");
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("产品编号              品名             数量");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);

                    for (int i=1;i<2;i++){
                        lpk130.NFCP_printStr("                A215562               33");
                        lpk130.NFCP_printStrLine("");
                        lpk130.NFCP_feed(7);
                        lpk130.NFCP_printStr("高斯化妆品产品）111款式");
                        lpk130.NFCP_printStrLine("");
                        lpk130.NFCP_feed(7);
                        lpk130.NFCP_printStr("-----------------------------------------------");
                        lpk130.NFCP_feed(7);
                    }
                    lpk130.NFCP_printStr("合计：                                 11");
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("       发货时间：2019/11/20 08:09:11");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("扫一扫获取装箱清单编号");
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printQRcode(6, 2,"0000000000000000000000000");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("123456789");
                    lpk130.NFCP_feed(120);

                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "设备连接失败，请重新连接！",
                    Toast.LENGTH_SHORT).show();
        }
    }


    public void LPK130DEMO1(OutStock_Model model, ArrayList<Boxing> modellist, StockInfo_Model stockmodel, String flag) {
        LPK130 lpk130 = new LPK130();
        lpk130.closeDevice();
        if (lpk130.openDevice(URLModel.MacAdress) >= 0) {
            try {
                if (flag=="LList") {


                }else if (flag=="ZList"){
                    lpk130.NFCP_setLeftMargin((byte) 12);
                    lpk130.NFCP_setSnapMode((byte) 1);
                    lpk130.NFCP_setLineSpace(40);
                    lpk130.NFCP_setFontBold((byte) 1);
                    lpk130.NFCP_fontSize((byte) 1, (byte) 1);
                    lpk130.NFCP_printStrLine("装箱清单");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_fontSize((byte) 1, (byte) 1);
                    lpk130.NFCP_setSnapMode((byte) 0);
                    lpk130.NFCP_printStr("订单号：");
                    lpk130.NFCP_printStrLine(model.getErpVoucherNo());
                    lpk130.NFCP_feed(7);


                    lpk130.NFCP_printStr("客户：");
                    lpk130.NFCP_printStrLine(model.getStrLandmark());
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("仓库：");
                    lpk130.NFCP_printStrLine(model.getCustomerName());
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("操作人：");
                    lpk130.NFCP_printStrLine(BaseApplication.userInfo.getUserName());
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("备注：");
                    lpk130.NFCP_printStrLine(model.getERPNote());
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("产品编号              品名             数量");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);


                    lpk130.NFCP_printStr("                  "+stockmodel.getMaterialNo()+"               "+stockmodel.getQty());
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr(stockmodel.getMaterialDesc());
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("合计：                                 "+stockmodel.getQty());
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);

                    SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日   HH:mm:ss");
                    Date curDate =  new Date(System.currentTimeMillis());
                    String str = formatter.format(curDate);

                    lpk130.NFCP_printStr("       发货时间："+str);
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("序列号："+stockmodel.getSerialNo());
                    lpk130.NFCP_feed(150);
                }else{


                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "设备连接失败，请重新连接！",
                    Toast.LENGTH_SHORT).show();
        }
    }


    public void LPK130DEMO1Pallet( String palletno) {
        LPK130 lpk130 = new LPK130();
        lpk130.closeDevice();
        if (lpk130.openDevice(URLModel.MacAdress) >= 0) {
            try {
                lpk130.NFCP_setLeftMargin((byte) 12);
                lpk130.NFCP_setSnapMode((byte) 1);
                lpk130.NFCP_setLineSpace(40);
                lpk130.NFCP_setFontBold((byte) 1);
                lpk130.NFCP_fontSize((byte) 1, (byte) 1);
                lpk130.NFCP_printStrLine("托盘标签");

                lpk130.NFCP_feed(7);
                lpk130.NFCP_printQRcode(6, 2,palletno);
                lpk130.NFCP_feed(7);
                lpk130.NFCP_printStr(palletno);
                lpk130.NFCP_feed(200);


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "设备连接失败，请重新连接！",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void LPK130DEMO1Scan(String palletno) {
        LPK130 lpk130 = new LPK130();
        lpk130.closeDevice();
        if (lpk130.openDevice(URLModel.MacAdress) >= 0) {
            try {
                lpk130.NFCP_setLeftMargin((byte) 12);
                lpk130.NFCP_setSnapMode((byte) 1);
                lpk130.NFCP_setLineSpace(40);
                lpk130.NFCP_setFontBold((byte) 1);
                lpk130.NFCP_fontSize((byte) 1, (byte) 1);
                lpk130.NFCP_printStrLine("");

                lpk130.NFCP_feed(7);
                lpk130.NFCP_printQRcode(6, 2,palletno);
                lpk130.NFCP_feed(7);
                lpk130.NFCP_printStr(palletno);
                lpk130.NFCP_feed(200);


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "设备连接失败，请重新连接！",
                    Toast.LENGTH_SHORT).show();
        }
    }


    public void LPK130Pallet(OutStockTaskInfo_Model model, ArrayList<Boxing> modellist,StockInfo_Model stockmodel,String flag) {
        LPK130 lpk130 = new LPK130();
        lpk130.closeDevice();
        if (lpk130.openDevice(URLModel.MacAdress) >= 0) {
            try {
                if (flag=="LList") {
                    lpk130.NFCP_setLeftMargin((byte) 12);
                    lpk130.NFCP_setSnapMode((byte) 1);
                    lpk130.NFCP_setLineSpace(40);
                    lpk130.NFCP_setFontBold((byte) 1);
                    lpk130.NFCP_fontSize((byte) 1, (byte) 1);
                    lpk130.NFCP_printStrLine("装箱清单");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_fontSize((byte) 1, (byte) 1);
                    lpk130.NFCP_setSnapMode((byte) 0);
                    lpk130.NFCP_printStr("订单号：");
                    lpk130.NFCP_printStrLine(model.getErpVoucherNo());
                    lpk130.NFCP_feed(7);


                    lpk130.NFCP_printStr("客户：");
                    lpk130.NFCP_printStrLine(model.getCarNo());
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("仓库：");
                    lpk130.NFCP_printStrLine(model.getCustomerName());
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("操作人：");
                    lpk130.NFCP_printStrLine(BaseApplication.userInfo.getUserName());
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("备注：");
                    lpk130.NFCP_printStrLine(model.getERPNote());
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("产品编号              品名             数量");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);

                    Float SumQty=0f;
                    for (int i=0;i<modellist.size();i++){
                        SumQty=SumQty+modellist.get(i).getQty();
                        lpk130.NFCP_printStr("                  "+modellist.get(i).getMaterialNo()+"               "+modellist.get(i).getQty());
                        lpk130.NFCP_printStrLine("");
                        lpk130.NFCP_feed(7);
                        lpk130.NFCP_printStr(modellist.get(i).getMaterialName());
                        lpk130.NFCP_printStrLine("");
                        lpk130.NFCP_feed(7);
                        lpk130.NFCP_printStr("-----------------------------------------------");
                        lpk130.NFCP_feed(7);
                    }
                    lpk130.NFCP_printStr("合计：                                 "+SumQty);
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                    Date curDate =  new Date(System.currentTimeMillis());
                    String str = formatter.format(curDate);

                    lpk130.NFCP_printStr("      发货时间："+str);
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("扫一扫获取装箱清单编号");
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printQRcode(6, 2,modellist.get(0).getSerialNo());
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr(modellist.get(0).getSerialNo());
                    lpk130.NFCP_feed(150);

                }else if (flag=="ZList"){
                    lpk130.NFCP_setLeftMargin((byte) 12);
                    lpk130.NFCP_setSnapMode((byte) 1);
                    lpk130.NFCP_setLineSpace(40);
                    lpk130.NFCP_setFontBold((byte) 1);
                    lpk130.NFCP_fontSize((byte) 1, (byte) 1);
                    lpk130.NFCP_printStrLine("装箱清单");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_fontSize((byte) 1, (byte) 1);
                    lpk130.NFCP_setSnapMode((byte) 0);
                    lpk130.NFCP_printStr("订单号：");
                    lpk130.NFCP_printStrLine(model.getErpVoucherNo());
                    lpk130.NFCP_feed(7);


                    lpk130.NFCP_printStr("客户：");
                    lpk130.NFCP_printStrLine(model.getCustomerName());
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("仓库：");
                    lpk130.NFCP_printStrLine("实品仓");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("操作人：");
                    lpk130.NFCP_printStrLine(BaseApplication.userInfo.getUserName());
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("备注：备注1111111");
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("产品编号              品名             数量");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);


                    lpk130.NFCP_printStr("                  "+stockmodel.getMaterialNo()+"               "+stockmodel.getQty());
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr(stockmodel.getMaterialDesc());
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("合计：                                 "+stockmodel.getQty());
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);

                    SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日   HH:mm:ss");
                    Date curDate =  new Date(System.currentTimeMillis());
                    String str = formatter.format(curDate);

                    lpk130.NFCP_printStr("       发货时间："+str);
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_feed(150);
                }else{
                    lpk130.NFCP_setLeftMargin((byte) 12);
                    lpk130.NFCP_setSnapMode((byte) 1);
                    lpk130.NFCP_setLineSpace(40);
                    lpk130.NFCP_setFontBold((byte) 1);
                    lpk130.NFCP_fontSize((byte) 1, (byte) 1);
                    lpk130.NFCP_printStrLine("装箱清单");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_fontSize((byte) 1, (byte) 1);
                    lpk130.NFCP_setSnapMode((byte) 0);
                    lpk130.NFCP_printStr("订单号：");
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);


                    lpk130.NFCP_printStr("客户：上海办公室");
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("仓库：");
                    lpk130.NFCP_printStrLine("实品仓");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("操作人：李敏");
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("备注：备注1111111");
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("产品编号              品名             数量");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);

                    for (int i=1;i<2;i++){
                        lpk130.NFCP_printStr("                A215562               33");
                        lpk130.NFCP_printStrLine("");
                        lpk130.NFCP_feed(7);
                        lpk130.NFCP_printStr("高斯化妆品产品）111款式");
                        lpk130.NFCP_printStrLine("");
                        lpk130.NFCP_feed(7);
                        lpk130.NFCP_printStr("-----------------------------------------------");
                        lpk130.NFCP_feed(7);
                    }
                    lpk130.NFCP_printStr("合计：                                 11");
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("       发货时间：2019/11/20 08:09:11");
                    lpk130.NFCP_feed(7);

                    lpk130.NFCP_printStr("-----------------------------------------------");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("扫一扫获取装箱清单编号");
                    lpk130.NFCP_printStrLine("");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printQRcode(6, 2,"0000000000000000000000000");
                    lpk130.NFCP_feed(7);
                    lpk130.NFCP_printStr("123456789");
                    lpk130.NFCP_feed(120);

                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "设备连接失败，请重新连接！",
                    Toast.LENGTH_SHORT).show();
        }
    }
//    public void LPK130OutBarcode(OutStockTaskInfo_Model model, ArrayList<Boxing> modellist,StockInfo_Model stockmodel,String flag) {
//        LPK130 lpk130 = new LPK130();
//        lpk130.closeDevice();
//        if (lpk130.openDevice(URLModel.MacAdress) >= 0) {
//            try {
//                if (flag=="LList") {
//                    int pageHeight = 600;
//                    int fontHeight = 24;
//                    int currentY = 20;
//                    if (lpk130 == null) return;
//                    lpk130.NFCP_createPage(576, pageHeight);//起始设置
////        lpk130.NFCP_setLeftMargin((byte) 12);
////        lpk130.NFCP_setSnapMode((byte) 1);
////        lpk130.NFCP_setLineSpace(31);
////        lpk130.NFCP_setFontBold((byte) 1);
//                    lpk130.NFCP_Page_setText(240,0,"装箱单",2, 0, 1, false, false);
//                    String packingNo="2020-5-5-15";
//                    if (packingNo!=null){
//                        lpk130.NFCP_Page_printQrCode(430,0,0,3, 2,packingNo );
//                    }
//
//                    currentY += fontHeight + 6;
//                    lpk130.NFCP_Page_setText(80,currentY,"装箱编码:"+packingNo,2, 0, 1, false, false);
//                    currentY += fontHeight + 6;
//                    currentY += fontHeight + 20;
////        lpk130.NFCP_setFontBold((byte) 0);
////        lpk130.NFCP_fontSize((byte) 1, (byte) 1);
////        lpk130.NFCP_setSnapMode((byte) 0);
////        lpk130.NFCP_setLeftMargin((byte) 60);
////        lpk130.NFCP_printStrLine("装箱编码:" + bean.getPackingNo());
////
////        lpk130.NFCP_printStrLine("---------------------------------");
//                    lpk130.NFCP_Page_setText(150, currentY, "物料描述", 2, 0, 1, false, false);
//                    lpk130.NFCP_Page_setText(480, currentY, "数量", 2, 0, 1, false, false);
//                    lpk130.NFCP_feed(10);
//                    for (PackingMaterialBean item : bean.getList()) {
//                        currentY += fontHeight + 6;
//                        for (int i = 0; i < item.getMaterialDescPartList().size(); i++) {
//                            if (i == 0) {
//                                lpk130.NFCP_Page_setText(60, currentY, item.getMaterialDescPartList().get(i), 2, 0, 1, false, false);
//                                lpk130.NFCP_Page_setText(500, currentY, item.getMaterialInfo().getQTY() + "", 2, 0, 1, false, false);
//
//                            } else {
//                                lpk130.NFCP_Page_setText(60, currentY, item.getMaterialDescPartList().get(i), 2, 0, 1, false, false);
//
//                            }
//                            currentY += fontHeight + 6;
//                        }
//
//                    }
//
//                    lpk130.NFCP_printPage(0, 1);
//
//
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            Toast.makeText(this, "设备连接失败，请重新连接！",
//                    Toast.LENGTH_SHORT).show();
//        }
//    }


}
