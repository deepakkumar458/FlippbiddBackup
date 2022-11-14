package com.flippbidd.sendbirdSdk.groupchannel;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.R;
import com.flippbidd.utils.DateUtils;
import com.flippbidd.utils.SyncManagerUtils;
import com.flippbidd.utils.TextUtils;
import com.flippbidd.utils.TypingIndicator;
import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.Member;
import com.sendbird.android.UserMessage;
import com.stfalcon.multiimageview.MultiImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Displays a list of Group Channels within a SendBird application.
 */
class GroupChannelListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GroupChannel> mChannelList;
    private Context mContext;
    private ConcurrentHashMap<SimpleTarget<Bitmap>, Integer> mSimpleTargetIndexMap;
    private ConcurrentHashMap<SimpleTarget<Bitmap>, GroupChannel> mSimpleTargetGroupChannelMap;
    private ConcurrentHashMap<String, Integer> mChannelImageNumMap;
    private ConcurrentHashMap<String, ImageView> mChannelImageViewMap;
    private ConcurrentHashMap<String, SparseArray<Bitmap>> mChannelBitmapMap;

    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    interface OnItemClickListener {
        void onItemClick(GroupChannel channel);
    }

    interface OnItemLongClickListener {
        void onItemLongClick(GroupChannel channel);
    }

    GroupChannelListAdapter(Context context) {
        mContext = context;

        mSimpleTargetIndexMap = new ConcurrentHashMap<>();
        mSimpleTargetGroupChannelMap = new ConcurrentHashMap<>();
        mChannelImageNumMap = new ConcurrentHashMap<>();
        mChannelImageViewMap = new ConcurrentHashMap<>();
        mChannelBitmapMap = new ConcurrentHashMap<>();

        mChannelList = new ArrayList<>();
    }

    void clearMap() {
        mSimpleTargetIndexMap.clear();
        mSimpleTargetGroupChannelMap.clear();
        mChannelImageNumMap.clear();
        mChannelImageViewMap.clear();
        mChannelBitmapMap.clear();
    }

    void clearChannelList() {
        mChannelList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

        if (mChannelList.get(position).getHiddenState() == GroupChannel.HiddenState.UNHIDDEN) {
            // The channel is displayed in the list.
            return 0;
        } else if (mChannelList.get(position).getHiddenState() == GroupChannel.HiddenState.HIDDEN_ALLOW_AUTO_UNHIDE) {
            // The channel is hidden from the list, and get appeared back on condition.
            return 1;
        } else if (mChannelList.get(position).getHiddenState() == GroupChannel.HiddenState.HIDDEN_PREVENT_AUTO_UNHIDE) {
            // The channel is archived, and get appeared back in the list only when the unhide() is called.
            return 0;
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_channel, parent, false);
                return new ChannelHolder(view);

            case 1:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_empty_view, parent, false);
                return new EmptyView(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case 0:
                ((ChannelHolder) holder).bind(mContext, position, mChannelList.get(position), mItemClickListener, mItemLongClickListener);
                break;
            case 1:
                ((EmptyView) holder).bindView();
                break;
        }


    }

    @Override
    public int getItemCount() {
        return mChannelList.size();
    }

    void insertChannels(List<GroupChannel> channels, GroupChannelListQuery.Order order) {
        for (GroupChannel newChannel : channels) {
            int index = SyncManagerUtils.findIndexOfChannel(mChannelList, newChannel, order);
            mChannelList.add(index, newChannel);
        }

        notifyDataSetChanged();
    }

    void updateChannels(List<GroupChannel> channels) {
        for (GroupChannel updatedChannel : channels) {
            int index = SyncManagerUtils.getIndexOfChannel(mChannelList, updatedChannel);
            if (index != -1) {
                mChannelList.set(index, updatedChannel);
                notifyItemChanged(index);
            }
        }
    }

    void moveChannels(List<GroupChannel> channels, GroupChannelListQuery.Order order) {
        for (GroupChannel movedChannel : channels) {
            int fromIndex = SyncManagerUtils.getIndexOfChannel(mChannelList, movedChannel);
            int toIndex = SyncManagerUtils.findIndexOfChannel(mChannelList, movedChannel, order);
            if (fromIndex != -1) {
                mChannelList.remove(fromIndex);
                mChannelList.add(toIndex, movedChannel);
                notifyItemMoved(fromIndex, toIndex);
                notifyItemChanged(toIndex);
            }
        }
    }

    void removeChannels(List<GroupChannel> channels) {
        for (GroupChannel removedChannel : channels) {
            int index = SyncManagerUtils.getIndexOfChannel(mChannelList, removedChannel);
            if (index != -1) {
                mChannelList.remove(index);
                notifyItemRemoved(index);
            }
        }
    }

    void deleteChat(GroupChannel mGroupChannel) {
        mChannelList.remove(mGroupChannel);
        notifyDataSetChanged();

    }

    // If the channel is not in the list yet, adds it.
    // If it is, finds the channel in current list, and replaces it.
    // Moves the updated channel to the front of the list.
    void updateOrInsert(BaseChannel channel) {
        if (!(channel instanceof GroupChannel)) {
            return;
        }

        GroupChannel groupChannel = (GroupChannel) channel;

        for (int i = 0; i < mChannelList.size(); i++) {
            if (mChannelList.get(i).getUrl().equals(groupChannel.getUrl())) {
                mChannelList.remove(mChannelList.get(i));
                mChannelList.add(0, groupChannel);
                notifyDataSetChanged();
                Log.v(GroupChannelListAdapter.class.getSimpleName(), "Channel replaced.");
                return;
            }
        }

        mChannelList.add(0, groupChannel);
        notifyDataSetChanged();
    }

    void update(List<GroupChannel> channels) {
        for (GroupChannel channel : channels) {
            for (int i = 0; i < mChannelList.size(); i++) {
                if (mChannelList.get(i).getUrl().equals(channel.getUrl())) {
                    notifyItemChanged(i);
                }
            }
        }
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mItemLongClickListener = listener;
    }

    /**
     * A ViewHolder that contains UI to display information about a GroupChannel.
     */
    private class ChannelHolder extends RecyclerView.ViewHolder {

        CustomTextView topicText, lastMessageText, unreadCountText, dateText, memberCountText;
        ImageView coverImage;
        MultiImageView coverImage1;
        LinearLayout typingIndicatorContainer;
        RelativeLayout rlViewChatBox;

        ChannelHolder(View itemView) {
            super(itemView);

            rlViewChatBox = (RelativeLayout) itemView.findViewById(R.id.rlViewChatBox);
            topicText = (CustomTextView) itemView.findViewById(R.id.text_group_channel_list_topic);
            lastMessageText = (CustomTextView) itemView.findViewById(R.id.text_group_channel_list_message);
            unreadCountText = (CustomTextView) itemView.findViewById(R.id.text_group_channel_list_unread_count);
            dateText = (CustomTextView) itemView.findViewById(R.id.text_group_channel_list_date);
            memberCountText = (CustomTextView) itemView.findViewById(R.id.text_group_channel_list_member_count);
            coverImage = (ImageView) itemView.findViewById(R.id.image_group_channel_list_cover);
            coverImage1 = (MultiImageView) itemView.findViewById(R.id.image_group_channel_list_cover1);
            coverImage1.setShape(MultiImageView.Shape.CIRCLE);

            typingIndicatorContainer = (LinearLayout) itemView.findViewById(R.id.container_group_channel_list_typing_indicator);
        }

        /**
         * Binds views in the ViewHolder to information contained within the Group Channel.
         *
         * @param context
         * @param channel
         * @param clickListener     A listener that handles simple clicks.
         * @param longClickListener A listener that handles long clicks.
         */
        void bind(final Context context, int position, final GroupChannel channel,
                  @Nullable final OnItemClickListener clickListener,
                  @Nullable final OnItemLongClickListener longClickListener) {


            //checl hidenn state
            if (channel.getHiddenState().equals(GroupChannel.HiddenState.UNHIDDEN)) {
                itemView.setVisibility(View.VISIBLE);
                //check group
                /*if (channel.getMembers().size() > 2) {
                    //check group title
                    if (channel.getCustomType().equalsIgnoreCase("new_c_group")) {
                        topicText.setText(channel.getName());
                    } else {
                        topicText.setText(channel.getName());
                    }

                } else {
                    topicText.setText(TextUtils.getGroupChannelTitle(channel));
                }*/

                if (channel.getName().equalsIgnoreCase("Group Channel")) {
                    if (channel.getMembers().size() > 2) {
                        topicText.setText(TextUtils.getGroupChannelTitle(channel));
                    } else {
                        topicText.setText("None");
                    }
                } else {
                    topicText.setText(channel.getName());
                }


                memberCountText.setText(String.valueOf(channel.getMemberCount()));

                setChannelImage(context, position, channel, coverImage, coverImage1);

                int unreadCount = channel.getUnreadMessageCount();
                // If there are no unread messages, hide the unread count badge.
                if (unreadCount == 0) {
                    unreadCountText.setVisibility(View.INVISIBLE);
                } else {
                    unreadCountText.setVisibility(View.VISIBLE);
                    unreadCountText.setText(String.valueOf(channel.getUnreadMessageCount()));
                }

                BaseMessage lastMessage = channel.getLastMessage();
                if (lastMessage != null) {
                    dateText.setVisibility(View.VISIBLE);
                    lastMessageText.setVisibility(View.VISIBLE);

                    // Display information about the most recently sent message in the channel.
                    dateText.setText(String.valueOf(DateUtils.formatDateTime(lastMessage.getCreatedAt())));

                    // Bind last message text according to the type of message. Specifically, if
                    // the last message is a File Message, there must be special formatting.
                    if (lastMessage instanceof UserMessage) {
                        lastMessageText.setText(((UserMessage) lastMessage).getMessage());
                    } else if (lastMessage instanceof AdminMessage) {
                        lastMessageText.setText(((AdminMessage) lastMessage).getMessage());
                    } else {
                        String lastMessageString = String.format(
                                context.getString(R.string.group_channel_list_file_message_text),
                                ((FileMessage) lastMessage).getSender().getNickname());
                        lastMessageText.setText(lastMessageString);
                    }
                } else {
                    dateText.setVisibility(View.INVISIBLE);
                    lastMessageText.setVisibility(View.INVISIBLE);
                }

                /*
                 * Set up the typing indicator.
                 * A typing indicator is basically just three dots contained within the layout
                 * that animates. The animation is implemented in the {@link TypingIndicator#animate() class}
                 */
                ArrayList<ImageView> indicatorImages = new ArrayList<>();
                indicatorImages.add((ImageView) typingIndicatorContainer.findViewById(R.id.typing_indicator_dot_1));
                indicatorImages.add((ImageView) typingIndicatorContainer.findViewById(R.id.typing_indicator_dot_2));
                indicatorImages.add((ImageView) typingIndicatorContainer.findViewById(R.id.typing_indicator_dot_3));

                TypingIndicator indicator = new TypingIndicator(indicatorImages, 600);
                indicator.animate();

                // debug
//            typingIndicatorContainer.setVisibility(View.VISIBLE);
//            lastMessageText.setText(("Someone is typing"));

                // If someone in the channel is typing, display the typing indicator.
                if (channel.isTyping()) {
                    typingIndicatorContainer.setVisibility(View.VISIBLE);
                    lastMessageText.setText(mContext.getString(R.string.typing_status));
                } else {
                    // Display typing indicator only when someone is typing
                    typingIndicatorContainer.setVisibility(View.GONE);
                }

                // Set an OnClickListener to this item.
                if (clickListener != null) {
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickListener.onItemClick(channel);
                        }
                    });
                }

                // Set an OnLongClickListener to this item.
                if (longClickListener != null) {
                    itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            longClickListener.onItemLongClick(channel);

                            // return true if the callback consumed the long click
                            return true;
                        }
                    });
                }
            } else {
                itemView.setVisibility(View.VISIBLE);
            }

        }

        private void setChannelImage(Context context, int position, GroupChannel channel, ImageView multiImageView, MultiImageView multiImageView1) {

            if (channel.getCoverUrl() != null && !channel.getCoverUrl().equalsIgnoreCase("")) {
                //hide signle imahe
                multiImageView.setVisibility(View.VISIBLE);
                multiImageView1.setVisibility(View.GONE);
                //set cover image
                RequestOptions myOptions = new RequestOptions()
                        .dontAnimate()
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

                if (channel.getCoverUrl().contains("https://static.sendbird.com/sample/cover")) {
                    Glide.with(context)
                            .load(R.mipmap.ic_launcher_round)
                            .apply(myOptions)
                            .into(multiImageView);
                } else {
                    Glide.with(context)
                            .load(channel.getCoverUrl())
                            .apply(myOptions)
                            .into(multiImageView);
                }

            } else {
                //set member icon
                List<Member> members = channel.getMembers();
                int size = members.size();
                if (size >= 1) {
                    int imageNum = size;
                    if (size >= 4) {
                        imageNum = 4;
                    }

                    if (!mChannelImageNumMap.containsKey(channel.getUrl())) {
                        mChannelImageNumMap.put(channel.getUrl(), imageNum);
                        mChannelImageViewMap.put(channel.getUrl(), multiImageView1);

                        //hide signle imahe
                        multiImageView.setVisibility(View.GONE);
                        multiImageView1.setVisibility(View.VISIBLE);


                        multiImageView1.clear();

                        for (int index = 0; index < imageNum; index++) {
                            SimpleTarget<Bitmap> simpleTarget = new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap bitmap, Transition<? super Bitmap> glideAnimation) {
                                    GroupChannel channel = mSimpleTargetGroupChannelMap.get(this);
                                    Integer index = mSimpleTargetIndexMap.get(this);
                                    if (channel != null && index != null) {
                                        SparseArray<Bitmap> bitmapSparseArray = mChannelBitmapMap.get(channel.getUrl());
                                        if (bitmapSparseArray == null) {
                                            bitmapSparseArray = new SparseArray<>();
                                            mChannelBitmapMap.put(channel.getUrl(), bitmapSparseArray);
                                        }
                                        bitmapSparseArray.put(index, bitmap);

                                        Integer num = mChannelImageNumMap.get(channel.getUrl());
                                        if (num != null && num == bitmapSparseArray.size()) {
                                            MultiImageView multiImageView = (MultiImageView) mChannelImageViewMap.get(channel.getUrl());
                                            if (multiImageView != null) {
                                                for (int i = 0; i < bitmapSparseArray.size(); i++) {
                                                    multiImageView.addImage(bitmapSparseArray.get(i));
                                                }
                                            }
                                        }
                                    }
                                }
                            };

                            mSimpleTargetIndexMap.put(simpleTarget, index);
                            mSimpleTargetGroupChannelMap.put(simpleTarget, channel);

                            RequestOptions myOptions = new RequestOptions()
                                    .dontAnimate()
                                    .circleCrop()
                                    .override(65)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

                            Glide.with(context)
                                    .asBitmap()
                                    .load(members.get(index).getProfileUrl())
                                    .apply(myOptions)
                                    .into(simpleTarget);
                            //R.mipmap.ic_launcher_round
                        }
                    } else {
                        //hide signle imahe
                        multiImageView.setVisibility(View.GONE);
                        multiImageView1.setVisibility(View.VISIBLE);

                        SparseArray<Bitmap> bitmapSparseArray = mChannelBitmapMap.get(channel.getUrl());
                        if (bitmapSparseArray != null) {
                            Integer num = mChannelImageNumMap.get(channel.getUrl());
                            if (num != null && num == bitmapSparseArray.size()) {
                                multiImageView1.clear();

                                for (int i = 0; i < bitmapSparseArray.size(); i++) {
                                    multiImageView1.addImage(bitmapSparseArray.get(i));
                                }
                            }
                        } else {
                            //set cover image
                            //hide signle imahe
                            multiImageView.setVisibility(View.VISIBLE);
                            multiImageView1.setVisibility(View.GONE);
                            RequestOptions myOptions = new RequestOptions()
                                    .dontAnimate()
                                    .circleCrop()
                                    .override(65)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

                            Glide.with(context)
                                    .load(R.mipmap.ic_launcher_round)
                                    .apply(myOptions)
                                    .into(multiImageView);
                        }
                    }
                }
                {
                    RequestOptions myOptions = new RequestOptions()
                            .dontAnimate()
                            .circleCrop()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                    Glide.with(context)
                            .load(R.mipmap.ic_launcher_round)
                            .apply(myOptions)
                            .into(multiImageView);
                }
            }
        }
    }

    /*Empty View*/
    private class EmptyView extends RecyclerView.ViewHolder {
        EmptyView(View itemView) {
            super(itemView);
        }

        void bindView() {
        }
    }
}
