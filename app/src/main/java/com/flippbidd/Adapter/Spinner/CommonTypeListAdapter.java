package com.flippbidd.Adapter.Spinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.Response.TypeList.CommonListData;
import com.flippbidd.R;

import java.util.List;

public class CommonTypeListAdapter extends ArrayAdapter {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<CommonListData> items;
    private final int mResource;

    public CommonTypeListAdapter(Context context, int resource, List<CommonListData> data) {
        super(context, resource, data);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        items = data;
    }

    public void updateData(){
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        final View view = mInflater.inflate(mResource, parent, false);
        CustomTextView textViewName = view.findViewById(R.id.textViewTitle);
        final CommonListData mCommonListData = items.get(position);
        textViewName.setText(mCommonListData.getCommonName());

        return view;
    }
}
