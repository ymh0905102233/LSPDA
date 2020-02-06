package com.xx.chinetek.model.Box;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by GHOST on 2017/6/13.
 */

public class Boxing implements Parcelable{

    public int ID ;
    public String SerialNo ;
    public String MaterialNo;
    public String MaterialName;
    public Float Qty;
    public String TaskNo;
    public String Creater;
    public Date CreateTime;
    public String Modifyer;
    public Date ModifyTime;
    public int IsDel;
    public String Remark;
    public String Remark1;
    public String Remark2;
    public int Status;
    public String ErpVoucherNo;
    public String CustomerNo;

    public String getCustomerNo() {
        return CustomerNo;
    }

    public void setCustomerNo(String customerNo) {
        CustomerNo = customerNo;
    }

    public String getErpVoucherNo() {
        return ErpVoucherNo;
    }

    public void setErpVoucherNo(String erpVoucherNo) {
        ErpVoucherNo = erpVoucherNo;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(String serialNo) {
        SerialNo = serialNo;
    }

    public String getMaterialNo() {
        return MaterialNo;
    }

    public void setMaterialNo(String materialNo) {
        MaterialNo = materialNo;
    }

    public String getMaterialName() {
        return MaterialName;
    }

    public void setMaterialName(String materialName) {
        MaterialName = materialName;
    }

    public Float getQty() {
        return Qty;
    }

    public void setQty(Float qty) {
        Qty = qty;
    }

    public String getTaskNo() {
        return TaskNo;
    }

    public void setTaskNo(String taskNo) {
        TaskNo = taskNo;
    }

    public String getCreater() {
        return Creater;
    }

    public void setCreater(String creater) {
        Creater = creater;
    }

    public Date getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Date createTime) {
        CreateTime = createTime;
    }

    public String getModifyer() {
        return Modifyer;
    }

    public void setModifyer(String modifyer) {
        Modifyer = modifyer;
    }

    public Date getModifyTime() {
        return ModifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        ModifyTime = modifyTime;
    }

    public int getIsDel() {
        return IsDel;
    }

    public void setIsDel(int isDel) {
        IsDel = isDel;
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

    public static Creator<Boxing> getCREATOR() {
        return CREATOR;
    }

    public Boxing() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ID);
        dest.writeString(this.SerialNo);
        dest.writeString(this.MaterialNo);
        dest.writeString(this.MaterialName);
        dest.writeValue(this.Qty);
        dest.writeString(this.TaskNo);
        dest.writeString(this.Creater);
        dest.writeLong(this.CreateTime != null ? this.CreateTime.getTime() : -1);
        dest.writeString(this.Modifyer);
        dest.writeLong(this.ModifyTime != null ? this.ModifyTime.getTime() : -1);
        dest.writeInt(this.IsDel);
        dest.writeString(this.Remark);
        dest.writeString(this.Remark1);
        dest.writeString(this.Remark2);
        dest.writeInt(this.Status);
        dest.writeString(this.ErpVoucherNo);
        dest.writeString(this.CustomerNo);
    }

    protected Boxing(Parcel in) {
        this.ID = in.readInt();
        this.SerialNo = in.readString();
        this.MaterialNo = in.readString();
        this.MaterialName = in.readString();
        this.Qty = (Float) in.readValue(Float.class.getClassLoader());
        this.TaskNo = in.readString();
        this.Creater = in.readString();
        long tmpCreateTime = in.readLong();
        this.CreateTime = tmpCreateTime == -1 ? null : new Date(tmpCreateTime);
        this.Modifyer = in.readString();
        long tmpModifyTime = in.readLong();
        this.ModifyTime = tmpModifyTime == -1 ? null : new Date(tmpModifyTime);
        this.IsDel = in.readInt();
        this.Remark = in.readString();
        this.Remark1 = in.readString();
        this.Remark2 = in.readString();
        this.Status = in.readInt();
        this.ErpVoucherNo = in.readString();
        this.CustomerNo = in.readString();
    }

    public static final Creator<Boxing> CREATOR = new Creator<Boxing>() {
        @Override
        public Boxing createFromParcel(Parcel source) {
            return new Boxing(source);
        }

        @Override
        public Boxing[] newArray(int size) {
            return new Boxing[size];
        }
    };
}
