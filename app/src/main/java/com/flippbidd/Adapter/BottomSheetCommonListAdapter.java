package com.flippbidd.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Thems on 26-03-2018.
 */

public class BottomSheetCommonListAdapter extends RecyclerView.Adapter<BottomSheetCommonListAdapter.MyViewHolder> {


    //define object of Activity
    private Context mContext;
    private List<String> mList = new ArrayList<>();
    OnClickListener mClickListener;

    //Array List Define

    public BottomSheetCommonListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void addAll(List<String> arrayList) {
        mList.clear();
        mList.addAll(arrayList);
        notifyDataSetChanged();
    }

    @Override
    public BottomSheetCommonListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bottom_collection_sheet_layout_adapter, parent, false);

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (holder != null) {
            holder.textCollectionName.setText(mList.get(position));
        }

        holder.textCollectionName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null)
                    mClickListener.OnClickListener(position, mList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends BaseViewHolder {

        //Define Components
        @BindView(R.id.textCollectionName)
        CustomTextView textCollectionName;

        public MyViewHolder(View view) {
            super(view);
            //ID OF TEXT VIEW
            ButterKnife.bind(this, view);
        }
    }

    public void OnClickListener(OnClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    public interface OnClickListener {
        void OnClickListener(int mPosition, String actionType);
    }
}
