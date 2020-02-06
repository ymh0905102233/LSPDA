package com.xx.chinetek.model.Landmark;

import android.os.Parcel;
import android.os.Parcelable;

import com.xx.chinetek.model.Base_Model;

/**
 * Created by GHOST on 2017/6/13.
 */

public class landmarkwithtaskno extends Base_Model implements Parcelable{

    private float Landmarkid;
    private String CarNo;
    private String TaskNo;
    private Float IsDel;
    private String Remark;
    private String Remark1;
    private String Remark2;
    private String Remark3;
    public String landmarkno;

    public float getLandmarkid() {
        return Landmarkid;
    }

    public void setLandmarkid(float landmarkid) {
        Landmarkid = landmarkid;
    }

    public String getCarNo() {
        return CarNo;
    }

    public void setCarNo(String carNo) {
        CarNo = carNo;
    }



    public String getTaskNo() {
        return TaskNo;
    }

    public void setTaskNo(String taskNo) {
        TaskNo = taskNo;
    }

    public Float getIsDel() {
        return IsDel;
    }

    public void setIsDel(Float isDel) {
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

    public String getRemark3() {
        return Remark3;
    }

    public void setRemark3(String remark3) {
        Remark3 = remark3;
    }

    public String getLandmarkno() {
        return landmarkno;
    }

    public void setLandmarkno(String landmarkno) {
        this.landmarkno = landmarkno;
    }

    public static Creator<landmarkwithtaskno> getCREATOR() {
        return CREATOR;
    }

    public landmarkwithtaskno() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeFloat(this.Landmarkid);
        dest.writeString(this.CarNo);
        dest.writeString(this.TaskNo);
        dest.writeValue(this.IsDel);
        dest.writeString(this.Remark);
        dest.writeString(this.Remark1);
        dest.writeString(this.Remark2);
        dest.writeString(this.Remark3);
        dest.writeString(this.landmarkno);
    }

    protected landmarkwithtaskno(Parcel in) {
        super(in);
        this.Landmarkid = in.readFloat();
        this.CarNo = in.readString();
        this.TaskNo = in.readString();
        this.IsDel = (Float) in.readValue(Float.class.getClassLoader());
        this.Remark = in.readString();
        this.Remark1 = in.readString();
        this.Remark2 = in.readString();
        this.Remark3 = in.readString();
        this.landmarkno = in.readString();
    }

    public static final Creator<landmarkwithtaskno> CREATOR = new Creator<landmarkwithtaskno>() {
        @Override
        public landmarkwithtaskno createFromParcel(Parcel source) {
            return new landmarkwithtaskno(source);
        }

        @Override
        public landmarkwithtaskno[] newArray(int size) {
            return new landmarkwithtaskno[size];
        }
    };
}
