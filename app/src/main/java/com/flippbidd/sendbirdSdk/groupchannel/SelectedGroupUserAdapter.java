package com.flippbidd.sendbirdSdk.groupchannel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ContactList.Users;
import com.flippbidd.R;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class SelectedGroupUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Users> mUsers;
    private Context mContext;
    private SelectableUserHolder mSelectableUserHolder;
    boolean isCheckBoxShow = false;

    // For the adapter to track which users have been selected
    private OnItemCheckedChangeListener mCheckedChangeListener;

    public interface OnItemCheckedChangeListener {
        void OnItemChecked(Users user, boolean checked);
    }

    public SelectedGroupUserAdapter(Context context, boolean isShow) {
        mContext = context;
        mUsers = new ArrayList<>();
        isCheckBoxShow = isShow;
    }

    public void setItemCheckedChangeListener(OnItemCheckedChangeListener listener) {
        mCheckedChangeListener = listener;
    }

    public void setUserList(List<Users> users) {
        mUsers = users;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_invite_contact_user, parent, false);
        mSelectableUserHolder = new SelectableUserHolder(view);
        return mSelectableUserHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SelectableUserHolder) holder).bind(
                mContext,
                mUsers.get(position),
                mCheckedChangeListener);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    private class SelectableUserHolder extends RecyclerView.ViewHolder {
        private CustomTextView nameText;
        private CustomTextView inviteText;
        private ImageView profileImage;
        private CheckBox checkbox;
        private boolean mShowCheckBox;

        public SelectableUserHolder(View itemView) {
            super(itemView);

            this.setIsRecyclable(false);
            nameText = (CustomTextView) itemView.findViewById(R.id.text_selectable_user_list_nickname);
            inviteText = (CustomTextView) itemView.findViewById(R.id.text_user_invite);
            inviteText.setVisibility(View.VISIBLE);
            inviteText.setText("Cancel");
            profileImage = (ImageView) itemView.findViewById(R.id.image_selectable_user_list_profile);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox_selectable_user_list);
        }

        public void setShowCheckBox(boolean showCheckBox) {
            mShowCheckBox = showCheckBox;
        }

        private void bind(final Context context, Users user, final OnItemCheckedChangeListener listener) {
            Glide.with(context)
                    .load(R.drawable.profile_thumbnail)
                    .into(profileImage);

            nameText.setText(user.getNickname());

            if (isCheckBoxShow) {
                checkbox.setVisibility(View.VISIBLE);
            } else {
                checkbox.setVisibility(View.GONE);
            }

            //click
            inviteText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemChecked(user, false);
                }
            });
        }
    }
}
