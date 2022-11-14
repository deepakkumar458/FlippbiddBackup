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
import com.flippbidd.Adapter.Spinner.SearchableAdapter;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ContactList.Users;
import com.flippbidd.Model.Response.TypeList.CommonListData;
import com.flippbidd.R;
import com.flippbidd.utils.ImageUtils;
import com.flippbidd.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Populates a RecyclerView with a list of users, each with a checkbox.
 */

public class NewSelectableUserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private List<Users> originalData;
    private List<Users> filteredData = null;

    private Context mContext;
    private static List<Users> mSelectedUserIds;
    private boolean mIsBlockedList;
    private boolean mShowCheckBox;
    private boolean mIsViewCLick;


    private ItemFilter mFilter = new ItemFilter();


    private SelectableUserHolder mSelectableUserHolder;

    // For the adapter to track which users have been selected
    private OnItemCheckedChangeListener mCheckedChangeListener;



    public interface OnItemCheckedChangeListener {
        void OnItemChecked(Users user, boolean checked);
    }

    public NewSelectableUserListAdapter(Context context, boolean isBlockedList, boolean showCheckBox, boolean isViewClick) {
        mContext = context;
        originalData = new ArrayList<>();
        filteredData = new ArrayList<>();
        mSelectedUserIds = new ArrayList<>();
        mIsBlockedList = isBlockedList;
        mShowCheckBox = showCheckBox;
        mIsViewCLick = isViewClick;
    }

    public void setItemCheckedChangeListener(OnItemCheckedChangeListener listener) {
        mCheckedChangeListener = listener;
    }

    public void setUserList(List<Users> users) {
        originalData = users;
        filteredData = users;
        notifyDataSetChanged();
    }

    public void setShowCheckBox(boolean showCheckBox) {
        mShowCheckBox = showCheckBox;
        if (mSelectableUserHolder != null) {
            mSelectableUserHolder.setShowCheckBox(showCheckBox);
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_selectable_user, parent, false);
        mSelectableUserHolder = new SelectableUserHolder(view, mIsBlockedList, mShowCheckBox);
        return mSelectableUserHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
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

    public boolean isSelected(Users user) {
        return mSelectedUserIds.contains(user);
    }

    public void addLast(Users user) {
        filteredData.add(user);
        notifyDataSetChanged();
    }

    private class SelectableUserHolder extends RecyclerView.ViewHolder {
        private CustomTextView nameText;
        private ImageView profileImage;
        private ImageView blockedImage;
        private CheckBox checkbox;

        private boolean mIsBlockedList;
        private boolean mShowCheckBox;

        public SelectableUserHolder(View itemView, boolean isBlockedList, boolean hideCheckBox) {
            super(itemView);

            this.setIsRecyclable(false);
            mIsBlockedList = isBlockedList;
            mShowCheckBox = hideCheckBox;

            nameText = (CustomTextView) itemView.findViewById(R.id.text_selectable_user_list_nickname);
            profileImage = (ImageView) itemView.findViewById(R.id.image_selectable_user_list_profile);
            blockedImage = (ImageView) itemView.findViewById(R.id.image_user_list_blocked);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox_selectable_user_list);
        }

        public void setShowCheckBox(boolean showCheckBox) {
            mShowCheckBox = showCheckBox;
        }

        private void bind(final Context context, final Users user, boolean isSelected, final OnItemCheckedChangeListener listener) {
            nameText.setText(user.getNickname());
            if(user.getProfilePic()!=null && !user.getProfilePic().equalsIgnoreCase("")){
                ImageUtils.displayRoundImageFromUrl(context, user.getProfilePic(), profileImage);
            }else {
            Glide.with(context)
                    .load(R.drawable.profile_thumbnail)
                    .into(profileImage);
            }

            if (mIsBlockedList) {
                blockedImage.setVisibility(View.VISIBLE);
            } else {
                blockedImage.setVisibility(View.GONE);
            }

            if (mShowCheckBox) {
                if (PreferenceUtils.getUserId().equalsIgnoreCase(user.getUserId())
                        || user.getUserId().equalsIgnoreCase("")) {
                    checkbox.setVisibility(View.GONE);
                } else {
                    checkbox.setVisibility(View.VISIBLE);
                }
            } else {
                checkbox.setVisibility(View.GONE);
            }

            if (isSelected) {
                checkbox.setChecked(true);
            } else {
                checkbox.setChecked(false);
            }

            if (mShowCheckBox) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (PreferenceUtils.getUserId().equalsIgnoreCase(user.getUserId())
                                || user.getUserId().equalsIgnoreCase("")) {
                        } else {
                            if (mShowCheckBox) {
                                checkbox.setChecked(!checkbox.isChecked());
                            }
                        }

                    }
                });
            } else {
                if (mIsViewCLick) {
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (PreferenceUtils.getUserId().equalsIgnoreCase(user.getUserId())
                                    || user.getUserId().equalsIgnoreCase("")) {
                            } else {
                                checkbox.setChecked(!checkbox.isChecked());
                            }

                        }
                    });
                }
            }

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    listener.OnItemChecked(user, isChecked);
                    if (isChecked) {
                        mSelectedUserIds.add(user);
                    } else {
                        mSelectedUserIds.remove(user);
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

            final List<Users> list = originalData;

            int count = list.size();
            final ArrayList<Users> nlist = new ArrayList<Users>(count);

            Users filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.getNickname().toLowerCase().contains(filterString)) {
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
            filteredData = (ArrayList<Users>) results.values;
            notifyDataSetChanged();
        }

    }
}
