package com.flippbidd.Adapter.Notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.Response.Notification.NotificationData;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.TimeAgo;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseViewHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;


public class ListOfNotification extends RecyclerView.Adapter<ListOfNotification.MyViewHolder> {

    //define object of Activity
    private final static int FADE_DURATION = 1000; //FADE_DURATION in milliseconds
    private Context mContext;
    private List<NotificationData> mNotificationData = new ArrayList<>();
    onItemClickLisnear mOnItemClickLisnear;

    public ListOfNotification(Context mContext) {
        this.mContext = mContext;
    }

    public void addAll(List<NotificationData> arrayList) {
        mNotificationData.clear();
        mNotificationData.addAll(arrayList);
        notifyDataSetChanged();
    }

    public void deleteItem(int pos) {
        mNotificationData.remove(pos);
        notifyDataSetChanged();
    }

    public void deleteAdapter() {
        mNotificationData.clear();
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_list_of_notification_layout, parent, false);

        return new MyViewHolder(itemView);

    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        final NotificationData notificationData = mNotificationData.get(position);
        if (holder != null) {

            //check type
            holder.textViewNotificationMessages.setText(notificationData.getActionName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickLisnear != null) {
                        mOnItemClickLisnear.onClickEvent(position, notificationData, Constants.ACTION.VIEW_ACTION);
                    }
                }
            });


            if (notificationData.getActionDateTime() != null && !notificationData.getActionDateTime().equalsIgnoreCase("")) {
                Date date1 = null;
                try {
                    //2019-02-28 03:44:00
                    date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(notificationData.getActionDateTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                TimeAgo mTimeAgo = new TimeAgo(mContext);
                holder.textViewNotificationMessagesTime.setText(mTimeAgo.timeAgo(date1));
            }
        }
    }

    public interface onItemClickLisnear {
        void onClickEvent(int position, NotificationData mNotificationData, String mActionType);
    }

    public void setItemOnClickEvent(onItemClickLisnear mOnItemClickLisnear) {
        this.mOnItemClickLisnear = mOnItemClickLisnear;
    }


    @Override
    public int getItemCount() {
        return mNotificationData.size();
    }

    public class MyViewHolder extends BaseViewHolder {
        @BindView(R.id.textViewNotificationMessages)
        CustomTextView textViewNotificationMessages;
        @BindView(R.id.textViewNotificationMessagesTime)
        CustomTextView textViewNotificationMessagesTime;
        public MyViewHolder(View view) {
            super(view);
            //ID OF TEXT VIEW
            ButterKnife.bind(this, view);
        }


    }
/*
* Add New Bidd
* Ratting
* Document Folder View
* property
* */
}


