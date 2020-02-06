package com.xx.chinetek.model.WMS.AdvInStock;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ListView;

import com.xx.chinetek.model.Base_Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 86988 on 2019-08-08.
 */

public class AdvInStockInfo_Model extends Base_Model implements Parcelable {

    private String VoucherNo;
    private String SupplierNo;
    private String SupplierName;
    private double IsDel;
    private int WarehouseID;
    private ArrayList<AdvInStockDetail_Model> lstDetail;

    public AdvInStockInfo_Model(){

    }

    protected AdvInStockInfo_Model(Parcel in) {
        VoucherNo = in.readString();
        SupplierNo = in.readString();
        SupplierName = in.readString();
        IsDel = in.readDouble();
        WarehouseID = in.readInt();
    }

    public static final Creator<AdvInStockInfo_Model> CREATOR = new Creator<AdvInStockInfo_Model>() {
        @Override
        public AdvInStockInfo_Model createFromParcel(Parcel in) {
            return new AdvInStockInfo_Model(in);
        }

        @Override
        public AdvInStockInfo_Model[] newArray(int size) {
            return new AdvInStockInfo_Model[size];
        }
    };

    public String getVoucherNo() {
        return VoucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        VoucherNo = voucherNo;
    }

    public String getSupplierNo() {
        return SupplierNo;
    }

    public void setSupplierNo(String supplierNo) {
        SupplierNo = supplierNo;
    }

    public String getSupplierName() {
        return SupplierName;
    }

    public void setSupplierName(String supplierName) {
        SupplierName = supplierName;
    }

    public double getIsDel() {
        return IsDel;
    }

    public void setIsDel(double isDel) {
        IsDel = isDel;
    }

    public int getWarehouseID() {
        return WarehouseID;
    }

    public void setWarehouseID(int warehouseID) {
        WarehouseID = warehouseID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(VoucherNo);
        parcel.writeString(SupplierNo);
        parcel.writeString(SupplierName);
        parcel.writeDouble(IsDel);
        parcel.writeInt(WarehouseID);
    }

    public ArrayList<AdvInStockDetail_Model> getLstDetail() {
        return lstDetail;
    }

    public void setLstDetail(ArrayList<AdvInStockDetail_Model> lstDetail) {
        this.lstDetail = lstDetail;
    }
}
