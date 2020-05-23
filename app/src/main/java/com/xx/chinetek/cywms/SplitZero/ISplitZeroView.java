package com.xx.chinetek.cywms.SplitZero;


import com.xx.chinetek.model.WMS.Stock.StockInfo_Model;
import com.xx.chinetek.util.printutils.interfaces.PrintCallBackListener;

/**
 * @ Des:
 * @ Created by yangyiqing on 2019/11/14.
 */
public interface ISplitZeroView {

   void  requestFatherBarcodeFocus();

   void  requestSubBarcodeFocus();

   void  requestNumberFocus();
    void  setNumber(int qty);

   void bindFatherBarcodeInfo(StockInfo_Model info);

   void onClear();

   void showNumberEditText(boolean isShow);
   void showModuleView(int qty);
   void  onNewBarcodePrint(final PrintCallBackListener listener);
   boolean CheckBluetooth();
}
