package com.xx.chinetek.model.WMS.Stock;

import android.os.Parcel;

import com.xx.chinetek.model.Base_Model;

import java.util.Date;

/**
 * Created by 86988 on 2019-08-29.
 */

public class MoveTaskDetailInfo_Model extends Base_Model {
    public MoveTaskDetailInfo_Model(){

    }

    public float InScanQty;
    public float OutScanQty;
    public float MoveQty;
    public float RemainQty;
    public String FromStorageLoc;
    public String CloseOweUser;
    public Date CloseOweDate;
    public String CloseOweRemark;
    public float IsDel;
    public String VoucherNo;
    public String FromBatchNo;
    public String FromErpAreaNo;
    public String FromErpWarehouse;
    public String ToBatchNo;
    public String ToErpAreaNo;
    public String ToErpWarehouse;
    public String MaterialNo;
    public String MaterialDesc;
    public String Unit;
    public String UnitName ;
    public String EAN;

    public float getInScanQty() {
        return InScanQty;
    }

    public void setInScanQty(float inScanQty) {
        InScanQty = inScanQty;
    }

    public float getOutScanQty() {
        return OutScanQty;
    }

    public void setOutScanQty(float outScanQty) {
        OutScanQty = outScanQty;
    }

    public float getMoveQty() {
        return MoveQty;
    }

    public void setMoveQty(float moveQty) {
        MoveQty = moveQty;
    }

    public float getRemainQty() {
        return RemainQty;
    }

    public void setRemainQty(float remainQty) {
        RemainQty = remainQty;
    }

    public String getFromStorageLoc() {
        return FromStorageLoc;
    }

    public void setFromStorageLoc(String fromStorageLoc) {
        FromStorageLoc = fromStorageLoc;
    }

    public String getCloseOweUser() {
        return CloseOweUser;
    }

    public void setCloseOweUser(String closeOweUser) {
        CloseOweUser = closeOweUser;
    }

    public Date getCloseOweDate() {
        return CloseOweDate;
    }

    public void setCloseOweDate(Date closeOweDate) {
        CloseOweDate = closeOweDate;
    }

    public String getCloseOweRemark() {
        return CloseOweRemark;
    }

    public void setCloseOweRemark(String closeOweRemark) {
        CloseOweRemark = closeOweRemark;
    }

    public float getIsDel() {
        return IsDel;
    }

    public void setIsDel(float isDel) {
        IsDel = isDel;
    }

    public String getVoucherNo() {
        return VoucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        VoucherNo = voucherNo;
    }

    public String getFromBatchNo() {
        return FromBatchNo;
    }

    public void setFromBatchNo(String fromBatchNo) {
        FromBatchNo = fromBatchNo;
    }

    public String getFromErpAreaNo() {
        return FromErpAreaNo;
    }

    public void setFromErpAreaNo(String fromErpAreaNo) {
        FromErpAreaNo = fromErpAreaNo;
    }

    public String getFromErpWarehouse() {
        return FromErpWarehouse;
    }

    public String getToBatchNo() {
        return ToBatchNo;
    }

    public void setToBatchNo(String toBatchNo) {
        ToBatchNo = toBatchNo;
    }

    public String getToErpAreaNo() {
        return ToErpAreaNo;
    }

    public void setToErpAreaNo(String toErpAreaNo) {
        ToErpAreaNo = toErpAreaNo;
    }

    public String getMaterialNo() {
        return MaterialNo;
    }

    public void setMaterialNo(String materialNo) {
        MaterialNo = materialNo;
    }

    public String getMaterialDesc() {
        return MaterialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        MaterialDesc = materialDesc;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getUnitName() {
        return UnitName;
    }

    public void setUnitName(String unitName) {
        UnitName = unitName;
    }

    public String getEAN() {
        return EAN;
    }

    public void setEAN(String EAN) {
        this.EAN = EAN;
    }

    public void setFromErpWarehouse(String fromErpWarehouse) {
        FromErpWarehouse = fromErpWarehouse;
    }

    public String getToErpWarehouse() {
        return ToErpWarehouse;
    }

    public void setToErpWarehouse(String toErpWarehouse) {
        ToErpWarehouse = toErpWarehouse;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeFloat(this.InScanQty);
        dest.writeFloat(this.OutScanQty);
        dest.writeFloat(this.MoveQty);
        dest.writeFloat(this.RemainQty);
        dest.writeString(this.FromStorageLoc);
        dest.writeString(this.CloseOweUser);
        dest.writeLong(this.CloseOweDate != null ? this.CloseOweDate.getTime() : -1);
        dest.writeString(this.CloseOweRemark);
        dest.writeFloat(this.IsDel);
        dest.writeString(this.VoucherNo);
        dest.writeString(this.FromBatchNo);
        dest.writeString(this.FromErpAreaNo);
        dest.writeString(this.FromErpWarehouse);
        dest.writeString(this.ToBatchNo);
        dest.writeString(this.ToErpAreaNo);
        dest.writeString(this.ToErpWarehouse);
        dest.writeString(this.MaterialNo);
        dest.writeString(this.MaterialDesc);
        dest.writeString(this.Unit);
        dest.writeString(this.UnitName);
        dest.writeString(this.EAN);
    }

    protected MoveTaskDetailInfo_Model(Parcel in) {
        super(in);
        this.InScanQty = in.readFloat();
        this.OutScanQty = in.readFloat();
        this.MoveQty = in.readFloat();
        this.RemainQty = in.readFloat();
        this.FromStorageLoc = in.readString();
        this.CloseOweUser = in.readString();
        long tmpCloseOweDate = in.readLong();
        this.CloseOweDate = tmpCloseOweDate == -1 ? null : new Date(tmpCloseOweDate);
        this.CloseOweRemark = in.readString();
        this.IsDel = in.readFloat();
        this.VoucherNo = in.readString();
        this.FromBatchNo = in.readString();
        this.FromErpAreaNo = in.readString();
        this.FromErpWarehouse = in.readString();
        this.ToBatchNo = in.readString();
        this.ToErpAreaNo = in.readString();
        this.ToErpWarehouse = in.readString();
        this.MaterialNo = in.readString();
        this.MaterialDesc = in.readString();
        this.Unit = in.readString();
        this.UnitName = in.readString();
        this.EAN = in.readString();
    }

    public static final Creator<MoveTaskDetailInfo_Model> CREATOR = new Creator<MoveTaskDetailInfo_Model>() {
        @Override
        public MoveTaskDetailInfo_Model createFromParcel(Parcel source) {
            return new MoveTaskDetailInfo_Model(source);
        }

        @Override
        public MoveTaskDetailInfo_Model[] newArray(int size) {
            return new MoveTaskDetailInfo_Model[size];
        }
    };
}
