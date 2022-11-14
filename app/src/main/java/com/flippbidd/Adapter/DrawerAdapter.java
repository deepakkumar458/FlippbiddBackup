package com.flippbidd.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ModelDrawerLeft;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseViewHolder;

import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;


public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder> {

    protected List<ModelDrawerLeft> al_drawerleft;
    private Activity act;
    private OnItemSelecteListener mListener;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    public String valCounts = "0";


    public DrawerAdapter(Activity act, List<ModelDrawerLeft> al_drawerleft) {
        this.al_drawerleft = al_drawerleft;
        this.act = act;

        mPref = act.getSharedPreferences("person", Context.MODE_PRIVATE);
        mEditor = mPref.edit();
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_drawer, parent, false);
        return new DrawerViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(DrawerViewHolder viewHolder, int position) {
        final int pos = position;
        ModelDrawerLeft modelDrawerLeft = al_drawerleft.get(pos);
        viewHolder.tvTitle.setText(modelDrawerLeft.getTitle());
        int resID = act.getResources().getIdentifier(modelDrawerLeft.getIcon(), "drawable", act.getPackageName());
        viewHolder.ivIcon.setImageResource(resID);
        viewHolder.ivIcon.setColorFilter(ContextCompat.getColor(act, R.color.icon_color));

        if (al_drawerleft.get(position).isSelected()) {
            viewHolder.tvTitle.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            viewHolder.tvTitle.setTextColor(Color.parseColor("#FFFFFF"));
        }

        if (pos == 2) {
            if(valCounts.equalsIgnoreCase("0")){
                viewHolder.relativeCounts.setVisibility(View.INVISIBLE);
            }else {
                viewHolder.relativeCounts.setVisibility(View.VISIBLE);
                viewHolder.tvCounts.setText(valCounts);
            }
        } else {
            viewHolder.relativeCounts.setVisibility(View.INVISIBLE);
        }
    }

    public void updateCounts(String counts) {
        valCounts = counts;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return al_drawerleft.size();
    }

    public class DrawerViewHolder extends BaseViewHolder {

        @BindView(R.id.tvTitle)
        CustomTextView tvTitle;
        @BindView(R.id.ivIcon)
        ImageView ivIcon;
        @BindView(R.id.relativeCounts)
        RelativeLayout relativeCounts;
        @BindView(R.id.tvCounts)
        CustomTextView tvCounts;

        public DrawerViewHolder(View itemView, int viewType) {
            super(itemView);

            try {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mListener != null) {
                            mListener.onItemSelected(view, getAdapterPosition());
                        }
                    }
                });
            } catch (Exception ex) {

            }
        }
    }

    public void setOnItemClickLister(OnItemSelecteListener mListener) {
        this.mListener = mListener;
    }

    public interface OnItemSelecteListener {
        public void onItemSelected(View v, int position);
    }

    public void setSelected(int pos) {
        try {
            if (al_drawerleft.size() > 1) {
                al_drawerleft.get(mPref.getInt("position", 0)).setSelected(false);
                mEditor.putInt("position", pos);
                mEditor.commit();
            }
            al_drawerleft.get(pos).setSelected(true);
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateData(List<ModelDrawerLeft> viewModels) {
        al_drawerleft.clear();
        al_drawerleft.addAll(viewModels);
        notifyDataSetChanged();
    }

}