package com.flippbidd.sendbirdSdk.groupchannel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.R;
import com.github.tamir7.contacts.Contact;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class InviteContactUserAdapter extends RecyclerView.Adapter<InviteContactUserAdapter.SelectableUserHolder> implements Filterable {

    private List<Contact> originalData;
    private List<Contact> filteredData = null;
    private static List<Contact> mSelectedContactIds;
    private Context mContext;
    private SelectableUserHolder mSelectableUserHolder;
    private boolean mShowCheckBox;

    // For the adapter to track which users have been selected
    private OnItemCheckedChangeListener mCheckedChangeListener;
    private ItemFilter mFilter = new ItemFilter();

    public interface OnItemCheckedChangeListener {
        void OnItemChecked(Contact user, boolean checked);
    }

    public void setShowCheckBox(boolean showCheckBox) {
        mShowCheckBox = showCheckBox;
        if (mSelectableUserHolder != null) {
            mSelectableUserHolder.setShowCheckBox(showCheckBox);
        }
        notifyDataSetChanged();
    }

    public InviteContactUserAdapter(Context context, List<Contact> addData) {
        mContext = context;
        originalData = new ArrayList<>();
        filteredData = new ArrayList<>();
        mSelectedContactIds = new ArrayList<>();

        //add data
        originalData.addAll(addData);
        filteredData.addAll(addData);
    }

    public void setItemCheckedChangeListener(OnItemCheckedChangeListener listener) {
        mCheckedChangeListener = listener;
    }

    public boolean isSelected(Contact user) {
        return mSelectedContactIds.contains(user);
    }

    public void clearSelection() {
        mSelectedContactIds.clear();
        notifyDataSetChanged();
    }

    public void setUserList(List<Contact> users) {
        originalData.addAll(users);
        filteredData.addAll(users);
        notifyDataSetChanged();
    }


    @Override
    public SelectableUserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_invite_contact_user, parent, false);
        mSelectableUserHolder = new SelectableUserHolder(view);
        return mSelectableUserHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectableUserHolder holder, int position) {

        ((SelectableUserHolder) holder).bind(
                mContext,
                filteredData.get(position),
                isSelected(filteredData.get(position)),
                mCheckedChangeListener);
    }


    @Override
    public int getItemCount() {
        return filteredData.size();
    }


    public class SelectableUserHolder extends RecyclerView.ViewHolder {
        private CustomTextView nameText;
        //private TextView inviteText;
        private ImageView profileImage;
        private CheckBox checkbox;


        private boolean mIsBlockedList;
        private boolean mShowCheckBox;

        public SelectableUserHolder(View itemView) {
            super(itemView);

            this.setIsRecyclable(false);
            nameText = (CustomTextView) itemView.findViewById(R.id.text_selectable_user_list_nickname);
            //inviteText = (TextView) itemView.findViewById(R.id.text_user_invite);
            profileImage = (ImageView) itemView.findViewById(R.id.image_selectable_user_list_profile);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox_selectable_user_list);
        }

        public void setShowCheckBox(boolean showCheckBox) {
            mShowCheckBox = showCheckBox;
        }

        private void bind(final Context context, Contact mContactData, boolean isSelected, final OnItemCheckedChangeListener listener) {


            Glide.with(context)
                    .load(R.drawable.profile_thumbnail)
                    .into(profileImage);
            nameText.setText(mContactData.getDisplayName());

            if (isSelected) {
                checkbox.setChecked(true);
            } else {
                checkbox.setChecked(false);
            }

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    listener.OnItemChecked(mContactData, isChecked);
                    if (isChecked) {
                        mSelectedContactIds.add(mContactData);
                    } else {
                        mSelectedContactIds.remove(mContactData);
                    }
                }
            });
        }
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            try {
                final List<Contact> list = originalData;
                int count = list.size();
                final ArrayList<Contact> nlist = new ArrayList<Contact>(count);

                Contact filterableString;

                for (int i = 0; i < count; i++) {
                    filterableString = list.get(i);
                    if (filterableString.getDisplayName().toLowerCase().contains(filterString)) {
                        nlist.add(filterableString);
                    }
                }

                results.values = nlist;
                results.count = nlist.size();
            } catch (Exception e) {

            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            try {
                filteredData = (ArrayList<Contact>) results.values;
            } catch (Exception e) {
            }
            notifyDataSetChanged();
        }

    }
}
