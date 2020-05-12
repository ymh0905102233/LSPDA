package com.xx.chinetek.model.WMS.Stock;

import android.os.Parcel;
import android.os.Parcelable;

import com.xx.chinetek.model.Base_Model;
import com.xx.chinetek.model.Material.BarCodeInfo;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by GHOST on 2017/1/13.
 */

public class StockInfo_Model extends Base_Model implements Parcelable, Cloneable {//

    public StockInfo_Model() {

    }

    public StockInfo_Model(String Barcode, String serialNo) {
        this.Barcode = Barcode;
        this.SerialNo = serialNo;
    }

    private String  Barcode;
    private String  SerialNo;
    private String  MaterialNo;
    private String  MaterialDesc;
    private String  WarehouseNo;
    private String  HouseNo;
    private String  AreaNo;
    private Float   Qty;
    private String  TMaterialNo;
    private String  TMaterialDesc;
    private String  PickAreaNo;
    private String  CelareaNo;
    private int     IsDel;
    private String  BatchNo;
    private String  SN;
    private String  ReturnSupCode;
    private String  ReturnReson;
    private String  ReturnSupName;
    private int     OldStockID;
    private int     TaskDetailesID;
    private int     CheckID;
    private int     TransferDetailsID;
    private int     ReturnType;
    private String  ReturnTypeDesc;
    private String  Unit;
    private String  SaleName;
    private String  UnitName;
    private String  PalletNo;
    private Float   PalletQty;
    private int     ReceiveStatus;
    private String  FromAreaNo;
    private String  FromHouseNo;
    private String  FromWareHouseNo;
    private int     FromAreaID;
    private int     FromHouseID;
    private int     FromWareHouseID;
    private int     WareHouseID;
    private int     HouseID;
    private int     AreaID;
    private int     StockBarCodeStatus = 0;//发货复核使用  0：未组托 1：已组托
    private String  PartNo;
    private int     PickModel; //下架方式 1-整托 2-整箱 3-零数
    private Float   AmountQty;//零数
    private String  TaskNo;
    private int     AreaType;
    private String  ToErpAreaNo;
    private String  ToErpWarehouse;
    private String  FromErpAreaNo;
    private String  FromErpWarehouse;
    private String  FromBatchNo;
    private int     OutstockDetailID;
    private int     OutstockHeaderID;
    private Boolean OKSelect;
    private int     IsLimitStock;
    private String  UnitTypeCode;
    private String  DecimalLngth;
    private Date    ProductDate;
    private String  SupPrdBatch;
    private Date    SupPrdDate;
    private int     HouseProp;
    private String  EAN;
    private int     ScanType;
    private String  ISJ;//1-是 2-否
    private String  JBarCode;
    ArrayList<BarCodeInfo>     lstBarCode;
    ArrayList<StockInfo_Model> lstJBarCode;//J箱条码
    ArrayList<StockInfo_Model> lstHBarCode;//H箱条码

    private int    BarCodeType;
    private int    IsAmount;
    private String fserialno;
    public  int    IsPalletOrBox;
    private String ProjectNo;
    private String TracNo;
    private String RowNo;
    private  String standardbox;
    private  String Spec;
    public int getIsPalletOrBox() {
        return IsPalletOrBox;
    }

    public void setIsPalletOrBox(int isPalletOrBox) {
        IsPalletOrBox = isPalletOrBox;
    }

    public String getFserialno() {
        return fserialno;
    }

    public void setFserialno(String fserialno) {
        this.fserialno = fserialno;
    }

    public ArrayList<StockInfo_Model> getLstHBarCode() {
        return lstHBarCode;
    }

    public void setLstHBarCode(ArrayList<StockInfo_Model> lstHBarCode) {
        this.lstHBarCode = lstHBarCode;
    }

    public int getIsAmount() {
        return IsAmount;
    }

    public void setIsAmount(int isAmount) {
        IsAmount = isAmount;
    }

    public int getBarCodeType() {
        return BarCodeType;
    }

    public void setBarCodeType(int barCodeType) {
        BarCodeType = barCodeType;
    }

    public ArrayList<StockInfo_Model> getLstJBarCode() {
        return lstJBarCode;
    }

    public void setLstJBarCode(ArrayList<StockInfo_Model> lstJBarCode) {
        this.lstJBarCode = lstJBarCode;
    }

    public int getIsDel() {
        return IsDel;
    }

    public void setIsDel(int isDel) {
        IsDel = isDel;
    }

    public int getOldStockID() {
        return OldStockID;
    }

    public int getTaskDetailesID() {
        return TaskDetailesID;
    }

    public int getCheckID() {
        return CheckID;
    }

    public int getTransferDetailsID() {
        return TransferDetailsID;
    }

    public int getReturnType() {
        return ReturnType;
    }

    public int getReceiveStatus() {
        return ReceiveStatus;
    }

    public void setOldStockID(int oldStockID) {
        OldStockID = oldStockID;
    }

    public void setTaskDetailesID(int taskDetailesID) {
        TaskDetailesID = taskDetailesID;
    }

    public void setCheckID(int checkID) {
        CheckID = checkID;
    }

    public void setTransferDetailsID(int transferDetailsID) {
        TransferDetailsID = transferDetailsID;
    }

    public void setReturnType(int returnType) {
        ReturnType = returnType;
    }

    public void setReceiveStatus(int receiveStatus) {
        ReceiveStatus = receiveStatus;
    }

    public String getISJ() {
        return ISJ;
    }

    public void setISJ(String ISJ) {
        this.ISJ = ISJ;
    }

    public String getJBarCode() {
        return JBarCode;
    }

    public void setJBarCode(String JBarCode) {
        this.JBarCode = JBarCode;
    }

    public ArrayList<BarCodeInfo> getLstBarCode() {
        return lstBarCode;
    }

    public void setLstBarCode(ArrayList<BarCodeInfo> lstBarCode) {
        this.lstBarCode = lstBarCode;
    }

    public int getHouseProp() {
        return HouseProp;
    }

    public void setHouseProp(int houseProp) {
        HouseProp = houseProp;
    }

    public String getEAN() {
        return EAN;
    }

    public void setEAN(String EAN) {
        this.EAN = EAN;
    }

    public int getScanType() {
        return ScanType;
    }

    public void setScanType(int scanType) {
        ScanType = scanType;
    }

    public static Creator<StockInfo_Model> getCREATOR() {
        return CREATOR;
    }

    public Date getProductDate() {
        return ProductDate;
    }

    public void setProductDate(Date productDate) {
        ProductDate = productDate;
    }

    public String getSupPrdBatch() {
        return SupPrdBatch;
    }

    public void setSupPrdBatch(String supPrdBatch) {
        SupPrdBatch = supPrdBatch;
    }

    public Date getSupPrdDate() {
        return SupPrdDate;
    }

    public void setSupPrdDate(Date supPrdDate) {
        SupPrdDate = supPrdDate;
    }

    public String getUnitTypeCode() {
        return UnitTypeCode;
    }

    public void setUnitTypeCode(String unitTypeCode) {
        UnitTypeCode = unitTypeCode;
    }

    public String getDecimalLngth() {
        return DecimalLngth;
    }

    public void setDecimalLngth(String decimalLngth) {
        DecimalLngth = decimalLngth;
    }

    public int getIsLimitStock() {
        return IsLimitStock;
    }

    public void setIsLimitStock(int isLimitStock) {
        IsLimitStock = isLimitStock;
    }

    public Boolean getOKSelect() {
        return OKSelect;
    }

    public void setOKSelect(Boolean OKSelect) {
        this.OKSelect = OKSelect;
    }

    public int getOutstockHeaderID() {
        return OutstockHeaderID;
    }

    public void setOutstockHeaderID(int outstockHeaderID) {
        OutstockHeaderID = outstockHeaderID;
    }

    public int getOutstockDetailID() {
        return OutstockDetailID;
    }

    public void setOutstockDetailID(int outstockDetailID) {
        OutstockDetailID = outstockDetailID;
    }

    public String getToErpAreaNo() {
        return ToErpAreaNo;
    }

    public void setToErpAreaNo(String toErpAreaNo) {
        ToErpAreaNo = toErpAreaNo;
    }

    public String getToErpWarehouse() {
        return ToErpWarehouse;
    }

    public void setToErpWarehouse(String toErpWarehouse) {
        ToErpWarehouse = toErpWarehouse;
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

    public void setFromErpWarehouse(String fromErpWarehouse) {
        FromErpWarehouse = fromErpWarehouse;
    }

    public String getFromBatchNo() {
        return FromBatchNo;
    }

    public void setFromBatchNo(String fromBatchNo) {
        FromBatchNo = fromBatchNo;
    }

    public int getAreaType() {
        return AreaType;
    }

    public void setAreaType(int areaType) {
        AreaType = areaType;
    }

    public String getTaskNo() {
        return TaskNo;
    }

    public void setTaskNo(String taskNo) {
        TaskNo = taskNo;
    }

    public int getStockBarCodeStatus() {
        return StockBarCodeStatus;
    }

    public void setStockBarCodeStatus(int stockBarCodeStatus) {
        StockBarCodeStatus = stockBarCodeStatus;
    }

    public int getPickModel() {
        return PickModel;
    }

    public void setPickModel(int pickModel) {
        PickModel = pickModel;
    }

    public Float getAmountQty() {
        return AmountQty;
    }

    public void setAmountQty(Float amountQty) {
        AmountQty = amountQty;
    }

    public String getPartNo() {
        return PartNo;
    }

    public void setPartNo(String partNo) {
        PartNo = partNo;
    }

    public int getAreaID() {
        return AreaID;
    }

    public void setAreaID(int areaID) {
        AreaID = areaID;
    }

    public int getHouseID() {
        return HouseID;
    }

    public void setHouseID(int houseID) {
        HouseID = houseID;
    }

    public int getWareHouseID() {
        return WareHouseID;
    }

    public void setWareHouseID(int wareHouseID) {
        WareHouseID = wareHouseID;
    }

    public int getFromAreaID() {
        return FromAreaID;
    }

    public void setFromAreaID(int fromAreaID) {
        FromAreaID = fromAreaID;
    }

    public int getFromHouseID() {
        return FromHouseID;
    }

    public void setFromHouseID(int fromHouseID) {
        FromHouseID = fromHouseID;
    }

    public int getFromWareHouseID() {
        return FromWareHouseID;
    }

    public void setFromWareHouseID(int fromWareHouseID) {
        FromWareHouseID = fromWareHouseID;
    }

    public Float getPalletQty() {
        return PalletQty;
    }

    public void setPalletQty(Float palletQty) {
        PalletQty = palletQty;
    }

    public Float getPalletQTY() {
        return PalletQty;
    }

    public void setPalletQTY(Float palletQty) {
        PalletQty = palletQty;
    }

    public String getPickAreaNo() {
        return PickAreaNo;
    }

    public void setPickAreaNo(String pickAreaNo) {
        PickAreaNo = pickAreaNo;
    }

    public String getAreaNo() {
        return AreaNo;
    }

    public void setAreaNo(String areaNo) {
        AreaNo = areaNo;
    }

    public String getBarcode() {
        return Barcode;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }

    public String getBatchNo() {
        return BatchNo;
    }

    public void setBatchNo(String batchNo) {
        BatchNo = batchNo;
    }

    public String getCelareaNo() {
        return CelareaNo;
    }

    public void setCelareaNo(String celareaNo) {
        CelareaNo = celareaNo;
    }

    public String getFromAreaNo() {
        return FromAreaNo;
    }

    public void setFromAreaNo(String fromAreaNo) {
        FromAreaNo = fromAreaNo;
    }

    public String getFromHouseNo() {
        return FromHouseNo;
    }

    public void setFromHouseNo(String fromHouseNo) {
        FromHouseNo = fromHouseNo;
    }

    public String getFromWareHouseNo() {
        return FromWareHouseNo;
    }

    public void setFromWareHouseNo(String fromWareHouseNo) {
        FromWareHouseNo = fromWareHouseNo;
    }

    public String getHouseNo() {
        return HouseNo;
    }

    public void setHouseNo(String houseNo) {
        HouseNo = houseNo;
    }

    public String getMaterialDesc() {
        return MaterialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        MaterialDesc = materialDesc;
    }

    public String getMaterialNo() {
        return MaterialNo;
    }

    public void setMaterialNo(String materialNo) {
        MaterialNo = materialNo;
    }

    public String getPalletNo() {
        return PalletNo;
    }

    public void setPalletNo(String palletNo) {
        PalletNo = palletNo;
    }

    public Float getQty() {
        return Qty;
    }

    public void setQty(Float qty) {
        Qty = qty;
    }


    public String getReturnReson() {
        return ReturnReson;
    }

    public void setReturnReson(String returnReson) {
        ReturnReson = returnReson;
    }

    public String getReturnSupCode() {
        return ReturnSupCode;
    }

    public void setReturnSupCode(String returnSupCode) {
        ReturnSupCode = returnSupCode;
    }

    public String getReturnSupName() {
        return ReturnSupName;
    }

    public void setReturnSupName(String returnSupName) {
        ReturnSupName = returnSupName;
    }

    public String getReturnTypeDesc() {
        return ReturnTypeDesc;
    }

    public void setReturnTypeDesc(String returnTypeDesc) {
        ReturnTypeDesc = returnTypeDesc;
    }

    public String getSaleName() {
        return SaleName;
    }

    public void setSaleName(String saleName) {
        SaleName = saleName;
    }

    public String getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(String serialNo) {
        SerialNo = serialNo;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public String getTMaterialDesc() {
        return TMaterialDesc;
    }

    public void setTMaterialDesc(String TMaterialDesc) {
        this.TMaterialDesc = TMaterialDesc;
    }

    public String getTMaterialNo() {
        return TMaterialNo;
    }

    public void setTMaterialNo(String TMaterialNo) {
        this.TMaterialNo = TMaterialNo;
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

    public String getWarehouseNo() {
        return WarehouseNo;
    }

    public void setWarehouseNo(String warehouseNo) {
        WarehouseNo = warehouseNo;
    }

    public String getProjectNo() {
        return ProjectNo;
    }

    public void setProjectNo(String projectNo) {
        ProjectNo = projectNo;
    }

    public String getTracNo() {
        return TracNo;
    }

    public void setTracNo(String tracNo) {
        TracNo = tracNo;
    }

    public String getRowNo() {
        return RowNo;
    }

    public void setRowNo(String rowNo) {
        RowNo = rowNo;
    }

    public String getStandardbox() {
        return standardbox;
    }

    public void setStandardbox(String standardbox) {
        this.standardbox = standardbox;
    }

    public String getSpec() {
        return Spec;
    }

    public void setSpec(String spec) {
        Spec = spec;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StockInfo_Model that = (StockInfo_Model) o;

        return SerialNo.equals(that.SerialNo) || Barcode.equals(that.Barcode);

    }

    @Override
    public StockInfo_Model clone() throws CloneNotSupportedException {
        StockInfo_Model stockInfoModel = null;
        stockInfoModel = (StockInfo_Model) super.clone();
        return stockInfoModel;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.Barcode);
        dest.writeString(this.SerialNo);
        dest.writeString(this.MaterialNo);
        dest.writeString(this.MaterialDesc);
        dest.writeString(this.WarehouseNo);
        dest.writeString(this.HouseNo);
        dest.writeString(this.AreaNo);
        dest.writeValue(this.Qty);
        dest.writeString(this.TMaterialNo);
        dest.writeString(this.TMaterialDesc);
        dest.writeString(this.PickAreaNo);
        dest.writeString(this.CelareaNo);
        dest.writeInt(this.IsDel);
        dest.writeString(this.BatchNo);
        dest.writeString(this.SN);
        dest.writeString(this.ReturnSupCode);
        dest.writeString(this.ReturnReson);
        dest.writeString(this.ReturnSupName);
        dest.writeInt(this.OldStockID);
        dest.writeInt(this.TaskDetailesID);
        dest.writeInt(this.CheckID);
        dest.writeInt(this.TransferDetailsID);
        dest.writeInt(this.ReturnType);
        dest.writeString(this.ReturnTypeDesc);
        dest.writeString(this.Unit);
        dest.writeString(this.SaleName);
        dest.writeString(this.UnitName);
        dest.writeString(this.PalletNo);
        dest.writeValue(this.PalletQty);
        dest.writeInt(this.ReceiveStatus);
        dest.writeString(this.FromAreaNo);
        dest.writeString(this.FromHouseNo);
        dest.writeString(this.FromWareHouseNo);
        dest.writeInt(this.FromAreaID);
        dest.writeInt(this.FromHouseID);
        dest.writeInt(this.FromWareHouseID);
        dest.writeInt(this.WareHouseID);
        dest.writeInt(this.HouseID);
        dest.writeInt(this.AreaID);
        dest.writeInt(this.StockBarCodeStatus);
        dest.writeString(this.PartNo);
        dest.writeInt(this.PickModel);
        dest.writeValue(this.AmountQty);
        dest.writeString(this.TaskNo);
        dest.writeInt(this.AreaType);
        dest.writeString(this.ToErpAreaNo);
        dest.writeString(this.ToErpWarehouse);
        dest.writeString(this.FromErpAreaNo);
        dest.writeString(this.FromErpWarehouse);
        dest.writeString(this.FromBatchNo);
        dest.writeInt(this.OutstockDetailID);
        dest.writeInt(this.OutstockHeaderID);
        dest.writeValue(this.OKSelect);
        dest.writeInt(this.IsLimitStock);
        dest.writeString(this.UnitTypeCode);
        dest.writeString(this.DecimalLngth);
        dest.writeLong(this.ProductDate != null ? this.ProductDate.getTime() : -1);
        dest.writeString(this.SupPrdBatch);
        dest.writeLong(this.SupPrdDate != null ? this.SupPrdDate.getTime() : -1);
        dest.writeInt(this.HouseProp);
        dest.writeString(this.EAN);
        dest.writeInt(this.ScanType);
        dest.writeString(this.ISJ);
        dest.writeString(this.JBarCode);
        dest.writeTypedList(this.lstBarCode);
        dest.writeTypedList(this.lstJBarCode);
        dest.writeTypedList(this.lstHBarCode);
        dest.writeInt(this.BarCodeType);
        dest.writeInt(this.IsAmount);
        dest.writeString(this.fserialno);
        dest.writeInt(this.IsPalletOrBox);
        dest.writeString(this.ProjectNo);
        dest.writeString(this.TracNo);
        dest.writeString(this.RowNo);
        dest.writeString(this.standardbox);
        dest.writeString(this.Spec);
    }

    protected StockInfo_Model(Parcel in) {
        super(in);
        this.Barcode = in.readString();
        this.SerialNo = in.readString();
        this.MaterialNo = in.readString();
        this.MaterialDesc = in.readString();
        this.WarehouseNo = in.readString();
        this.HouseNo = in.readString();
        this.AreaNo = in.readString();
        this.Qty = (Float) in.readValue(Float.class.getClassLoader());
        this.TMaterialNo = in.readString();
        this.TMaterialDesc = in.readString();
        this.PickAreaNo = in.readString();
        this.CelareaNo = in.readString();
        this.IsDel = in.readInt();
        this.BatchNo = in.readString();
        this.SN = in.readString();
        this.ReturnSupCode = in.readString();
        this.ReturnReson = in.readString();
        this.ReturnSupName = in.readString();
        this.OldStockID = in.readInt();
        this.TaskDetailesID = in.readInt();
        this.CheckID = in.readInt();
        this.TransferDetailsID = in.readInt();
        this.ReturnType = in.readInt();
        this.ReturnTypeDesc = in.readString();
        this.Unit = in.readString();
        this.SaleName = in.readString();
        this.UnitName = in.readString();
        this.PalletNo = in.readString();
        this.PalletQty = (Float) in.readValue(Float.class.getClassLoader());
        this.ReceiveStatus = in.readInt();
        this.FromAreaNo = in.readString();
        this.FromHouseNo = in.readString();
        this.FromWareHouseNo = in.readString();
        this.FromAreaID = in.readInt();
        this.FromHouseID = in.readInt();
        this.FromWareHouseID = in.readInt();
        this.WareHouseID = in.readInt();
        this.HouseID = in.readInt();
        this.AreaID = in.readInt();
        this.StockBarCodeStatus = in.readInt();
        this.PartNo = in.readString();
        this.PickModel = in.readInt();
        this.AmountQty = (Float) in.readValue(Float.class.getClassLoader());
        this.TaskNo = in.readString();
        this.AreaType = in.readInt();
        this.ToErpAreaNo = in.readString();
        this.ToErpWarehouse = in.readString();
        this.FromErpAreaNo = in.readString();
        this.FromErpWarehouse = in.readString();
        this.FromBatchNo = in.readString();
        this.OutstockDetailID = in.readInt();
        this.OutstockHeaderID = in.readInt();
        this.OKSelect = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.IsLimitStock = in.readInt();
        this.UnitTypeCode = in.readString();
        this.DecimalLngth = in.readString();
        long tmpProductDate = in.readLong();
        this.ProductDate = tmpProductDate == -1 ? null : new Date(tmpProductDate);
        this.SupPrdBatch = in.readString();
        long tmpSupPrdDate = in.readLong();
        this.SupPrdDate = tmpSupPrdDate == -1 ? null : new Date(tmpSupPrdDate);
        this.HouseProp = in.readInt();
        this.EAN = in.readString();
        this.ScanType = in.readInt();
        this.ISJ = in.readString();
        this.JBarCode = in.readString();
        this.lstBarCode = in.createTypedArrayList(BarCodeInfo.CREATOR);
        this.lstJBarCode = in.createTypedArrayList(StockInfo_Model.CREATOR);
        this.lstHBarCode = in.createTypedArrayList(StockInfo_Model.CREATOR);
        this.BarCodeType = in.readInt();
        this.IsAmount = in.readInt();
        this.fserialno = in.readString();
        this.IsPalletOrBox = in.readInt();
        this.ProjectNo=in.readString();
        this.TracNo =in.readString();
        this.RowNo = in.readString();
        this.standardbox=in.readString();
        this.Spec=in.readString();
    }

    public static final Creator<StockInfo_Model> CREATOR = new Creator<StockInfo_Model>() {
        @Override
        public StockInfo_Model createFromParcel(Parcel source) {
            return new StockInfo_Model(source);
        }

        @Override
        public StockInfo_Model[] newArray(int size) {
            return new StockInfo_Model[size];
        }
    };
}
