package com.xx.chinetek;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;

import com.xx.chinetek.base.BaseActivity;
import com.zebra.android.devdemo.util.SettingsHelper;
import com.xx.chinetek.util.printutils.DialogUtil;
import com.xx.chinetek.util.printutils.interfaces.PrintCallBackListener;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @ Des: 蓝牙打印连接抽象类
 * @ Created by yangyiqing on 2019/5/30.
 */
public abstract class PrintConnectActivity extends BaseActivity {

    public static final String                bluetoothAddressKey = "ZEBRA_DEMO_BLUETOOTH_ADDRESS";
    public static final String                tcpAddressKey       = "ZEBRA_DEMO_TCP_ADDRESS";
    public static final String                tcpPortKey          = "ZEBRA_DEMO_TCP_PORT";
    public static final String                tcpStatusPortKey    = "ZEBRA_DEMO_TCP_STATUS_PORT";
    public static final String                PREFS_NAME          = "OurSavedAddress";
    public              String                mMac                = "";
    private             PrintCallBackListener mPrintListener;


   /**
    * @desc:  设置监听  打印完执行操作
    * @param:
    * @return:
    * @author: Nietzsche
    * @time 2019/6/25 17:42
    */
    public void  setPrintCallBackListener(PrintCallBackListener listener){
         mPrintListener=listener;
    }
    /**
     * @desc:  打印方法
     * @param:
     * @return:
     * @author: Nietzsche
     * @time 2019/6/25 17:04
     */
    public void onPrint(final String command) {

        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                sendFile(command);
                Looper.loop();
                Looper.myLooper().quit();
            }
        }).start();
    }

    public void sendFile(String command) {
        try {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            mMac = settings.getString(bluetoothAddressKey, "");
            Connection connection = null;
            if(mMac==null||mMac.equals("")) {
                DialogUtil.createDialog(PrintConnectActivity.this, "蓝牙地址不存在，是否已保存?", 2, null);
                return;
            }

            connection = new BluetoothConnection(mMac);
            connection.open();
            ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
            sendFile(printer,command);
            connection.close();
            if (mPrintListener!=null){
                mPrintListener.afterPrint();  //打印完执行操作
            }
        } catch (ConnectionException e) {
            DialogUtil.createDialog(PrintConnectActivity.this, e.getMessage(), 2, null);
        } catch (ZebraPrinterLanguageUnknownException e) {
            DialogUtil.createDialog(PrintConnectActivity.this, e.getMessage(), 2, null);


        }
    }

    private void sendFile(ZebraPrinter printer, String command) {
        try {
            File filepath = getFileStreamPath("TEST.LBL");
            createFile("TEST.LBL",command);
            printer.sendFileContents(filepath.getAbsolutePath());
            SettingsHelper.saveBluetoothAddress(this, mMac);
        } catch (ConnectionException e1) {
            DialogUtil.createDialog(PrintConnectActivity.this, "Error creating file", 2, null);

        } catch (IOException e) {
            DialogUtil.createDialog(PrintConnectActivity.this, "Error creating file", 2, null);

        }
    }

    private void createFile(String fileName, String command) throws IOException {
        FileOutputStream os = this.openFileOutput(fileName, Context.MODE_PRIVATE);
        byte[] configLabel = null;
        String cpclConfigLabel = command;
        configLabel=cpclConfigLabel.getBytes();
        os.write(configLabel);
        os.flush();
        os.close();
    }










}
