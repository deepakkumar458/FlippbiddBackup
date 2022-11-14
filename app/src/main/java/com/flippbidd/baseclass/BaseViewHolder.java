package com.flippbidd.baseclass;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by Pranay on 14/4/17.
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {

    public View itemView;

    public BaseViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        ButterKnife.bind(this, itemView);
    }

    public View getItemView() {
        return itemView;
    }
}
