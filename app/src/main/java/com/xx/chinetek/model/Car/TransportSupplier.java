package com.xx.chinetek.model.Car;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by GHOST on 2017/6/13.
 */

public class TransportSupplier  implements Parcelable{

    public int ID ;
    public String PlateNumber ;
    public float IsDel;
    public String PalletNo ;
    public String BoxCount ;
    public String OutBoxCount ;
    public String Creater ;
    public String ErpVoucherNo;
    public String CustomerName;
    public String Feight;
    public String VoucherNo;
    public String Type ;
    public String Remark ;
    public String Remark1;
    public String Remark2;
    public String Remark3;
    public String TradingConditionsCode;
    public String GUID;

    private  String Contact;
    private  String Address;
    private  String Address1;
    private  String Phone;

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getAddress1() {
        return Address1;
    }

    public void setAddress1(String address1) {
        Address1 = address1;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getPlateNumber() {
        return PlateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        PlateNumber = plateNumber;
    }

    public float getIsDel() {
        return IsDel;
    }

    public void setIsDel(float isDel) {
        IsDel = isDel;
    }

    public String getPalletNo() {
        return PalletNo;
    }

    public void setPalletNo(String palletNo) {
        PalletNo = palletNo;
    }

    public String getBoxCount() {
        return BoxCount;
    }

    public void setBoxCount(String boxCount) {
        BoxCount = boxCount;
    }

    public String getOutBoxCount() {
        return OutBoxCount;
    }

    public void setOutBoxCount(String outBoxCount) {
        OutBoxCount = outBoxCount;
    }

    public String getCreater() {
        return Creater;
    }

    public void setCreater(String creater) {
        Creater = creater;
    }

    public String getErpVoucherNo() {
        return ErpVoucherNo;
    }

    public void setErpVoucherNo(String erpVoucherNo) {
        ErpVoucherNo = erpVoucherNo;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getFeight() {
        return Feight;
    }

    public void setFeight(String feight) {
        Feight = feight;
    }

    public String getVoucherNo() {
        return VoucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        VoucherNo = voucherNo;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getRemark1() {
        return Remark1;
    }

    public void setRemark1(String remark1) {
        Remark1 = remark1;
    }

    public String getRemark2() {
        return Remark2;
    }

    public void setRemark2(String remark2) {
        Remark2 = remark2;
    }

    public String getRemark3() {
        return Remark3;
    }

    public void setRemark3(String remark3) {
        Remark3 = remark3;
    }

    public String getTradingConditionsCode() {
        return TradingConditionsCode;
    }

    public void setTradingConditionsCode(String tradingConditionsCode) {
        TradingConditionsCode = tradingConditionsCode;
    }

    public static Creator<TransportSupplier> getCREATOR() {
        return CREATOR;
    }

    public TransportSupplier() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ID);
        dest.writeString(this.PlateNumber);
        dest.writeFloat(this.IsDel);
        dest.writeString(this.PalletNo);
        dest.writeString(this.BoxCount);
        dest.writeString(this.OutBoxCount);
        dest.writeString(this.Creater);
        dest.writeString(this.ErpVoucherNo);
        dest.writeString(this.CustomerName);
        dest.writeString(this.Feight);
        dest.writeString(this.VoucherNo);
        dest.writeString(this.Type);
        dest.writeString(this.Remark);
        dest.writeString(this.Remark1);
        dest.writeString(this.Remark2);
        dest.writeString(this.Remark3);
        dest.writeString(this.TradingConditionsCode);
        dest.writeString(this.GUID);
        dest.writeString(this.Contact);
        dest.writeString(this.Address);
        dest.writeString(this.Address1);
        dest.writeString(this.Phone);
    }

    protected TransportSupplier(Parcel in) {
        this.ID = in.readInt();
        this.PlateNumber = in.readString();
        this.IsDel = in.readFloat();
        this.PalletNo = in.readString();
        this.BoxCount = in.readString();
        this.OutBoxCount = in.readString();
        this.Creater = in.readString();
        this.ErpVoucherNo = in.readString();
        this.CustomerName = in.readString();
        this.Feight = in.readString();
        this.VoucherNo = in.readString();
        this.Type = in.readString();
        this.Remark = in.readString();
        this.Remark1 = in.readString();
        this.Remark2 = in.readString();
        this.Remark3 = in.readString();
        this.TradingConditionsCode = in.readString();
        this.GUID = in.readString();
        this.Contact = in.readString();
        this.Address = in.readString();
        this.Address1 = in.readString();
        this.Phone = in.readString();
    }

    public static final Creator<TransportSupplier> CREATOR = new Creator<TransportSupplier>() {
        @Override
        public TransportSupplier createFromParcel(Parcel source) {
            return new TransportSupplier(source);
        }

        @Override
        public TransportSupplier[] newArray(int size) {
            return new TransportSupplier[size];
        }
    };
}
