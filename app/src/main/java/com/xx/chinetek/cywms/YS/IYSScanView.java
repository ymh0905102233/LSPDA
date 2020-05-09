package com.xx.chinetek.cywms.YS;

import com.xx.chinetek.model.Material.BarCodeInfo;
import com.xx.chinetek.model.Receiption.ReceiptDetail_Model;

import java.util.List;

/**
 * @ Des:
 * @ Created by yangyiqing on 2019/12/28.
 */
public interface IYSScanView {
    void requestScanBarcodeFocus();
    void onClear();
    void bindListView(List<ReceiptDetail_Model> list);
    void closeActivity();
    void   setOrderNo(String orderNo);
    void  setBarcodeInfo(BarCodeInfo info);

}
