package com.flippbidd.Adapter.Spinner;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.TypeList.CommonListData;
import com.flippbidd.R;

import java.util.ArrayList;
import java.util.List;

public class SearchableAdapter extends BaseAdapter implements Filterable {

    private LayoutInflater mInflater;
    private List<CommonListData> originalData = null;
    private List<CommonListData>filteredData = null;
    private ItemFilter mFilter = new ItemFilter();

    public SearchableAdapter(Context context, List<CommonListData> data) {
        this.filteredData = data ;
        this.originalData = data ;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return filteredData.size();
    }

    public CommonListData getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row.
        ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_spinner_layout, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.text =  convertView.findViewById(R.id.textViewTitle);

            // Bind the data efficiently with the holder.

            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        // If weren't re-ordering this you could rely on what you set last time
        holder.text.setText(filteredData.get(position).getCommonName());

        return convertView;
    }

    static class ViewHolder {
        CustomTextView text;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<CommonListData> list = originalData;

            int count = list.size();
            final ArrayList<CommonListData> nlist = new ArrayList<CommonListData>(count);

            CommonListData filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.getCommonName().toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<CommonListData>) results.values;
            notifyDataSetChanged();
        }

    }
}