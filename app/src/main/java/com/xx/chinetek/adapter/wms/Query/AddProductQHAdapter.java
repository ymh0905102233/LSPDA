package com.xx.chinetek.adapter.wms.Query;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xx.chinetek.cywms.R;
import com.xx.chinetek.model.WMS.Stock.MoveDetailInfo_Model;
import com.xx.chinetek.model.WMS.Stock.MoveTaskDetailInfo_Model;

import java.util.List;

/**
 * Created by 86988 on 2019-08-28.
 */

public class AddProductQHAdapter extends BaseAdapter {
    private Context context; // 运行上下文
    private List<MoveTaskDetailInfo_Model> moveInfoModels; // 信息集合
    private LayoutInflater listContainer; // 视图容器

    public final class ListItemView { // 自定义控件集合

        public TextView txtMaterialNo;
        public TextView txtMaterialDec;
      //  public TextView txtMiniQty;
        public TextView txtStrongHoldCode;
        public TextView txtMoveQty;
    }


    public AddProductQHAdapter(Context context, List<MoveTaskDetailInfo_Model> moveInfoModels) {
        this.context = context;
        listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
        this.moveInfoModels = moveInfoModels;
    }

    @Override
    public int getCount() {
        if(moveInfoModels==null){
            return 0;
        }else{
           return moveInfoModels.size();
        }
    }

    @Override
    public Object getItem(int i) {
        return moveInfoModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int selectID = i;
        ListItemView listItemView = null;
        if (view == null) {
            listItemView = new ListItemView();
            view = listContainer.inflate(R.layout.item_qcmaterialchoice_listview,null);
            listItemView.txtMaterialNo = (TextView)view.findViewById(R.id.txtMaterialNo);
            listItemView.txtStrongHoldCode = (TextView)view.findViewById(R.id.txtReceiptNum);
            listItemView.txtMaterialDec = (TextView)view.findViewById(R.id.txtMaterialName);
            listItemView.txtMoveQty = (TextView)view.findViewById(R.id.txtReceiptNo);
            view.setTag(listItemView);
        }else {
            listItemView = (ListItemView) view.getTag();
        }
        MoveTaskDetailInfo_Model moveTaskDetailInfo_Model = moveInfoModels.get(selectID);
        listItemView.txtMaterialNo.setText(moveTaskDetailInfo_Model.getMaterialNo());
        listItemView.txtStrongHoldCode.setText(moveTaskDetailInfo_Model.getStrongHoldCode());
        listItemView.txtMaterialDec.setText(moveTaskDetailInfo_Model.getMaterialDesc());
        listItemView.txtMoveQty.setText("最低量:"+moveTaskDetailInfo_Model.getRemainQty()+"     待补货:"+moveTaskDetailInfo_Model.getMoveQty());
        return view;
    }
}
