package com.xx.chinetek.model.WMS.AdvInStock;

import android.os.Parcel;
import android.os.Parcelable;

import com.xx.chinetek.model.Base_Model;

/**
 * Created by 86988 on 2019-08-08.
 */

public class AdvInStockDetail_Model extends Base_Model implements Parcelable {

    private String VOUCHERNO;
    private  String MaterialNo;
    private  String MaterialDesc;
    private  double AdvQty;
    private  String  Unit;
    private  int IsDel;
    private String EAN;
    private String SupBatch;
    private int QualityType;
    private String RowNO;
    private String RowNODel;
    private String remark;

    public AdvInStockDetail_Model() {
    }

    public AdvInStockDetail_Model(Parcel in) {
        super(in);
        VOUCHERNO = in.readString();
        MaterialNo = in.readString();
        MaterialDesc = in.readString();
        AdvQty = in.readDouble();
        Unit = in.readString();
        IsDel = in.readInt();
        EAN = in.readString();
        SupBatch = in.readString();
        QualityType = in.readInt();
        RowNO =in.readString();
        RowNODel=in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(VOUCHERNO);
        dest.writeString(MaterialNo);
        dest.writeString(MaterialDesc);
        dest.writeDouble(AdvQty);
        dest.writeString(Unit);
        dest.writeInt(IsDel);
        dest.writeString(EAN);
        dest.writeString(SupBatch);
        dest.writeInt(QualityType);
        dest.writeString(RowNO);
        dest.writeString(RowNODel);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AdvInStockDetail_Model> CREATOR = new Creator<AdvInStockDetail_Model>() {
        @Override
        public AdvInStockDetail_Model createFromParcel(Parcel in) {
            return new AdvInStockDetail_Model(in);
        }

        @Override
        public AdvInStockDetail_Model[] newArray(int size) {
            return new AdvInStockDetail_Model[size];
        }
    };

    public String getVOUCHERNO() {
        return VOUCHERNO;
    }

    public void setVOUCHERNO(String VOUCHERNO) {
        this.VOUCHERNO = VOUCHERNO;
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

    public double getAdvQty() {
        return AdvQty;
    }

    public void setAdvQty(double advQty) {
        AdvQty = advQty;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public int getIsDel() {
        return IsDel;
    }

    public void setIsDel(int isDel) {
        IsDel = isDel;
    }

    public String getEAN() {
        return EAN;
    }

    public void setEAN(String EAN) {
        this.EAN = EAN;
    }

    public String getSupBatch() {
        return SupBatch;
    }

    public void setSupBatch(String supBatch) {
        SupBatch = supBatch;
    }

    public int getQualityType() {
        return QualityType;
    }

    public void setQualityType(int qualityType) {
        QualityType = qualityType;
    }

    public String getRowNO() {
        return RowNO;
    }

    public void setRowNO(String rowNO) {
        RowNO = rowNO;
    }

    public String getRowNODel() {
        return RowNODel;
    }

    public void setRowNODel(String rowNODel) {
        RowNODel = rowNODel;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
