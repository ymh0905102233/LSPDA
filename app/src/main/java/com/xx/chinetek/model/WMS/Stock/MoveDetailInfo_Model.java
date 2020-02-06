package com.xx.chinetek.model.WMS.Stock;

import com.xx.chinetek.model.Base_Model;

/**
 * Created by 86988 on 2019-08-29.
 */

public class MoveDetailInfo_Model extends Base_Model {
    public MoveDetailInfo_Model(){

    }
    private String MaterialNo;
    private String MaterialDesc;
    private String Unit;
    private float MoveQty;
    private float RemainQty;//最低库存量
    private String FromErpWarehouse;//仓库ID
    private String ToErpWarehouse;

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

    public String getFromErpWarehouse() {
        return FromErpWarehouse;
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

}
