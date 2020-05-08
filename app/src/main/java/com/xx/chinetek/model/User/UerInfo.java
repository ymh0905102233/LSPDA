package com.xx.chinetek.model.User;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by GHOST on 2017/2/6.
 */

public class UerInfo extends User implements Parcelable ,Cloneable{
    private boolean BIsAdmin;
    private String StrIsAdmin;
    private int IsOnline;
    private boolean BIsOnline;
    private String WarehouseName;
    private String StrUserType;
    private String StrUserStatus;
    private String StrSex;
    private String ReceiveWareHouseNo;
    private String ReceiveAreaNo;
    private String ToSampWareHouseNo;
    private String ToSampAreaNo;
    private String PickWareHouseNo ;
    private String ErpVoucherNo;

    private String ReceiveHouseNo;
    private String ReceiveWareHouseName;

    private String PickHouseNo;
    private String PickWareHouseName ;
    private  int ISVWAREHOUSE; //0.不上架  1.上架
    private String  StrongHoldCode ;

    public String getStrongHoldCode() {
        return StrongHoldCode;
    }

    public void setStrongHoldCode(String strongHoldCode) {
        StrongHoldCode = strongHoldCode;
    }

    public String getReceiveHouseNo() {
        return ReceiveHouseNo;
    }

    public void setReceiveHouseNo(String receiveHouseNo) {
        ReceiveHouseNo = receiveHouseNo;
    }

    public String getReceiveWareHouseName() {
        return ReceiveWareHouseName;
    }

    public void setReceiveWareHouseName(String receiveWareHouseName) {
        ReceiveWareHouseName = receiveWareHouseName;
    }

    public String getPickHouseNo() {
        return PickHouseNo;
    }

    public void setPickHouseNo(String pickHouseNo) {
        PickHouseNo = pickHouseNo;
    }

    public String getPickWareHouseName() {
        return PickWareHouseName;
    }

    public void setPickWareHouseName(String pickWareHouseName) {
        PickWareHouseName = pickWareHouseName;
    }

    public static Creator<UerInfo> getCREATOR() {
        return CREATOR;
    }

    public String getErpVoucherNo() {
        return ErpVoucherNo;
    }

    public void setErpVoucherNo(String erpVoucherNo) {
        ErpVoucherNo = erpVoucherNo;
    }

    public String getQuanUserName() {
        return QuanUserName;
    }

    public void setQuanUserName(String quanUserName) {
        QuanUserName = quanUserName;
    }

    private String PickAreaNo;
    private String QuanUserName;

    private List<UserGroupInfo> lstUserGroup;
    private List<MenuInfo> lstMenu;
    private List<WareHouseInfo> lstWarehouse;
    private List<QuanUserModel> lstQuanUser;
    /// <summary>
    /// 取样人编号
    /// </summary>
    private String QuanUserNo;

    public String getPickWareHouseNo() {
        return PickWareHouseNo;
    }

    public void setPickWareHouseNo(String pickWareHouseNo) {
        PickWareHouseNo = pickWareHouseNo;
    }

    public String getPickAreaNo() {
        return PickAreaNo;
    }

    public void setPickAreaNo(String pickAreaNo) {
        PickAreaNo = pickAreaNo;
    }

    public String getToSampWareHouseNo() {
        return ToSampWareHouseNo;
    }

    public void setToSampWareHouseNo(String toSampWareHouseNo) {
        ToSampWareHouseNo = toSampWareHouseNo;
    }

    public List<QuanUserModel> getLstQuanUser() {
        return lstQuanUser;
    }

    public void setLstQuanUser(List<QuanUserModel> lstQuanUser) {
        this.lstQuanUser = lstQuanUser;
    }

    public String getToSampAreaNo() {
        return ToSampAreaNo;
    }

    public void setToSampAreaNo(String toSampAreaNo) {
        ToSampAreaNo = toSampAreaNo;
    }

    public String getQuanUserNo() {
        return QuanUserNo;
    }

    public void setQuanUserNo(String quanUserNo) {
        QuanUserNo = quanUserNo;
    }

    public String getReceiveWareHouseNo() {
        return ReceiveWareHouseNo;
    }

    public void setReceiveWareHouseNo(String receiveWareHouseNo) {
        ReceiveWareHouseNo = receiveWareHouseNo;
    }

    public String getReceiveAreaNo() {
        return ReceiveAreaNo;
    }

    public void setReceiveAreaNo(String receiveAreaNo) {
        ReceiveAreaNo = receiveAreaNo;
    }

    public boolean isBIsAdmin() {
        return BIsAdmin;
    }

    public void setBIsAdmin(boolean BIsAdmin) {
        this.BIsAdmin = BIsAdmin;
    }

    public boolean isBIsOnline() {
        return BIsOnline;
    }

    public void setBIsOnline(boolean BIsOnline) {
        this.BIsOnline = BIsOnline;
    }

    public int getIsOnline() {
        return IsOnline;
    }

    public void setIsOnline(int isOnline) {
        IsOnline = isOnline;
    }

    public List<MenuInfo> getLstMenu() {
        return lstMenu;
    }

    public void setLstMenu(List<MenuInfo> lstMenu) {
        this.lstMenu = lstMenu;
    }

    public List<UserGroupInfo> getLstUserGroup() {
        return lstUserGroup;
    }

    public void setLstUserGroup(List<UserGroupInfo> lstUserGroup) {
        this.lstUserGroup = lstUserGroup;
    }

    public List<WareHouseInfo> getLstWarehouse() {
        return lstWarehouse;
    }

    public void setLstWarehouse(List<WareHouseInfo> lstWarehouse) {
        this.lstWarehouse = lstWarehouse;
    }

    public String getStrIsAdmin() {
        return StrIsAdmin;
    }

    public void setStrIsAdmin(String strIsAdmin) {
        StrIsAdmin = strIsAdmin;
    }

    public String getStrSex() {
        return StrSex;
    }

    public void setStrSex(String strSex) {
        StrSex = strSex;
    }

    public String getStrUserStatus() {
        return StrUserStatus;
    }

    public void setStrUserStatus(String strUserStatus) {
        StrUserStatus = strUserStatus;
    }

    public String getStrUserType() {
        return StrUserType;
    }

    public void setStrUserType(String strUserType) {
        StrUserType = strUserType;
    }

    public String getWarehouseName() {
        return WarehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        WarehouseName = warehouseName;
    }


    public UerInfo() {
    }

    public int getISVWAREHOUSE() {
        return ISVWAREHOUSE;
    }

    public void setISVWAREHOUSE(int ISVWAREHOUSE) {
        this.ISVWAREHOUSE = ISVWAREHOUSE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte(this.BIsAdmin ? (byte) 1 : (byte) 0);
        dest.writeString(this.StrIsAdmin);
        dest.writeInt(this.IsOnline);
        dest.writeByte(this.BIsOnline ? (byte) 1 : (byte) 0);
        dest.writeString(this.WarehouseName);
        dest.writeString(this.StrUserType);
        dest.writeString(this.StrUserStatus);
        dest.writeString(this.StrSex);
        dest.writeString(this.ReceiveWareHouseNo);
        dest.writeString(this.ReceiveAreaNo);
        dest.writeString(this.ToSampWareHouseNo);
        dest.writeString(this.ToSampAreaNo);
        dest.writeString(this.PickWareHouseNo);
        dest.writeString(this.ErpVoucherNo);
        dest.writeString(this.ReceiveHouseNo);
        dest.writeString(this.ReceiveWareHouseName);
        dest.writeString(this.PickHouseNo);
        dest.writeString(this.PickWareHouseName);
        dest.writeString(this.PickAreaNo);
        dest.writeString(this.QuanUserName);
        dest.writeTypedList(this.lstUserGroup);
        dest.writeTypedList(this.lstMenu);
        dest.writeTypedList(this.lstWarehouse);
        dest.writeTypedList(this.lstQuanUser);
        dest.writeString(this.QuanUserNo);
        dest.writeInt(this.ISVWAREHOUSE);
        dest.writeString(this.StrongHoldCode);

    }

    protected UerInfo(Parcel in) {
        super(in);
        this.BIsAdmin = in.readByte() != 0;
        this.StrIsAdmin = in.readString();
        this.IsOnline = in.readInt();
        this.BIsOnline = in.readByte() != 0;
        this.WarehouseName = in.readString();
        this.StrUserType = in.readString();
        this.StrUserStatus = in.readString();
        this.StrSex = in.readString();
        this.ReceiveWareHouseNo = in.readString();
        this.ReceiveAreaNo = in.readString();
        this.ToSampWareHouseNo = in.readString();
        this.ToSampAreaNo = in.readString();
        this.PickWareHouseNo = in.readString();
        this.ErpVoucherNo = in.readString();
        this.ReceiveHouseNo = in.readString();
        this.ReceiveWareHouseName = in.readString();
        this.PickHouseNo = in.readString();
        this.PickWareHouseName = in.readString();
        this.PickAreaNo = in.readString();
        this.QuanUserName = in.readString();
        this.lstUserGroup = in.createTypedArrayList(UserGroupInfo.CREATOR);
        this.lstMenu = in.createTypedArrayList(MenuInfo.CREATOR);
        this.lstWarehouse = in.createTypedArrayList(WareHouseInfo.CREATOR);
        this.lstQuanUser = in.createTypedArrayList(QuanUserModel.CREATOR);
        this.QuanUserNo = in.readString();
        this.ISVWAREHOUSE= in.readInt();
        this.StrongHoldCode=in.readString();
    }
    @Override
    public UerInfo clone() throws CloneNotSupportedException {
        UerInfo userInfo = null;
        userInfo = (UerInfo) super.clone();
        return userInfo;
    }

    public static final Creator<UerInfo> CREATOR = new Creator<UerInfo>() {
        @Override
        public UerInfo createFromParcel(Parcel source) {
            return new UerInfo(source);
        }

        @Override
        public UerInfo[] newArray(int size) {
            return new UerInfo[size];
        }
    };
}
