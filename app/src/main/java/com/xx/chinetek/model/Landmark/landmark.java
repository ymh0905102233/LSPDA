package com.xx.chinetek.model.Landmark;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by GHOST on 2017/6/13.
 */

public class landmark implements Parcelable{

    public int ID ;
    private String LandMarkNo;
    private String Remark;
    private String Remark2;
    private int IsDel;


    public landmark() {
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getLandMarkNo() {
        return LandMarkNo;
    }

    public void setLandMarkNo(String landMarkNo) {
        LandMarkNo = landMarkNo;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getRemark2() {
        return Remark2;
    }

    public void setRemark2(String remark2) {
        Remark2 = remark2;
    }

    public int getIsDel() {
        return IsDel;
    }

    public void setIsDel(int isDel) {
        IsDel = isDel;
    }

    public static Creator<landmark> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ID);
        dest.writeString(this.LandMarkNo);
        dest.writeString(this.Remark);
        dest.writeString(this.Remark2);
        dest.writeInt(this.IsDel);
    }

    protected landmark(Parcel in) {
        this.ID = in.readInt();
        this.LandMarkNo = in.readString();
        this.Remark = in.readString();
        this.Remark2 = in.readString();
        this.IsDel = in.readInt();
    }

    public static final Creator<landmark> CREATOR = new Creator<landmark>() {
        @Override
        public landmark createFromParcel(Parcel source) {
            return new landmark(source);
        }

        @Override
        public landmark[] newArray(int size) {
            return new landmark[size];
        }
    };
}
