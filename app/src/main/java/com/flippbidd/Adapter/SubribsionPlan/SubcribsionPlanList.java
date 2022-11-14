package com.flippbidd.Adapter.SubribsionPlan;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.Model.Response.Plan.Plandata;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubcribsionPlanList extends RecyclerView.Adapter<SubcribsionPlanList.MyViewHolder> {

    private List<Plandata> mPlanListdata;
    private Context mContexts;
    onItemClickLisnear mOnItemClickLisnear;

    public SubcribsionPlanList(Context mContext) {
        this.mContexts = mContext;
        mPlanListdata = new ArrayList<>();
    }

    public void addAll(List<Plandata> mPlandata) {
        mPlanListdata.clear();
        mPlanListdata.addAll(mPlandata);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_subcribsion_plan_list_layout, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        final Plandata mDataOfPlan = mPlanListdata.get(i);
        if (myViewHolder != null) {
            myViewHolder.btnOfPlanView.setText("$" + mDataOfPlan.getPlanPrice() + "/Month " + mDataOfPlan.getPlanName());


            myViewHolder.btnOfPlanView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickLisnear != null) {
                        mOnItemClickLisnear.onClickEvent(i, mDataOfPlan, "");
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mPlanListdata.size();
    }


    public interface onItemClickLisnear {
        void onClickEvent(int position, Plandata mPlanData, String mActionType);
    }

    public void setItemOnClickEvent(onItemClickLisnear mOnItemClickLisnear) {
        this.mOnItemClickLisnear = mOnItemClickLisnear;
    }


    public class MyViewHolder extends BaseViewHolder {

        @BindView(R.id.btnOfPlanView)
        CustomAppCompatButton btnOfPlanView;

        public MyViewHolder(View view) {
            super(view);
            //ID OF TEXT VIEW
            ButterKnife.bind(this, view);
        }

    }
}
