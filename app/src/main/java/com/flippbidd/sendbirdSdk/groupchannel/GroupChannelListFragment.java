package com.flippbidd.sendbirdSdk.groupchannel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.CommonResponse;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.R;
import com.flippbidd.utils.PreferenceUtils;
import com.flippbidd.utils.SyncManagerUtils;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.Member;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.syncmanager.ChannelCollection;
import com.sendbird.syncmanager.ChannelEventAction;
import com.sendbird.syncmanager.handler.ChannelCollectionHandler;
import com.sendbird.syncmanager.handler.CompletionHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;

public class GroupChannelListFragment extends Fragment {

    public static final String EXTRA_GROUP_CHANNEL_MAIN_ID = "GROUP_CHANNEL_MAIN_ID";
    public static final String EXTRA_PROPERTY_ID = "EXTRA_PROPERTY_ID";
    public static final String EXTRA_OWNER_EMAIL = "EXTRA_OWNER_EMAIL";
    public static final String EXTRA_GROUP_CHANNEL_URL = "GROUP_CHANNEL_URL";
    private static final int INTENT_REQUEST_NEW_GROUP_CHANNEL = 302;
    private static final String CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_LIST";

    private int CHANNEL_LIST_LIMIT = 15;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private GroupChannelListAdapter mChannelListAdapter;
    private CustomTextView moTvNoChatList;
    //    private FloatingActionButton mCreateChannelFab;
    private SwipeRefreshLayout mSwipeRefresh;
    private ChannelCollection mChannelCollection;


    OnFragmentInteractionListener mListener;

    public static GroupChannelListFragment newInstance() {
        GroupChannelListFragment fragment = new GroupChannelListFragment();
        return fragment;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String messageCounts);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("LIFECYCLE", "GroupChannelListFragment onCreateView()");

        View rootView = inflater.inflate(R.layout.fragment_group_channel_list, container, false);
//        if(PreferenceUtils.getPlanVersionStatus()){
//            CHANNEL_LIST_LIMIT = 15;
//        }else {
//            CHANNEL_LIST_LIMIT = 5;
//        }


        setRetainInstance(true);

        // Change action bar title
//        ((GroupChannelActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.all_group_channels));

        moTvNoChatList = (CustomTextView) rootView.findViewById(R.id.tvNoChatList);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_group_channel_list);
//        mCreateChannelFab = (FloatingActionButton) rootView.findViewById(R.id.fab_group_channel_list);
        mSwipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout_group_channel_list);

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefresh.setRefreshing(true);
                refresh();
            }
        });


        mChannelListAdapter = new GroupChannelListAdapter(getActivity());

        setUpRecyclerView();
        setUpChannelListAdapter();

        refresh();

        return rootView;
    }

    @Override
    public void onResume() {
        Log.d("LIFECYCLE", "GroupChannelListFragment onResume()");

        SendBird.addChannelHandler(CHANNEL_HANDLER_ID, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
            }

            @Override
            public void onChannelChanged(BaseChannel channel) {
            }

            @Override
            public void onTypingStatusUpdated(GroupChannel channel) {
                mChannelListAdapter.notifyDataSetChanged();
            }
        });

        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d("LIFECYCLE", "GroupChannelListFragment onPause()");

        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID);

        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mChannelCollection != null) {
            mChannelCollection.setCollectionHandler(null);
            mChannelCollection.remove();
        }

        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INTENT_REQUEST_NEW_GROUP_CHANNEL) {
            if (resultCode == RESULT_OK) {
                // Channel successfully created
                // Enter the newly created channel.
                String newChannelUrl = data.getStringExtra(NewChatCreateActivity.EXTRA_NEW_CHANNEL_URL);
                if (newChannelUrl != null) {
                    enterGroupChannel(newChannelUrl);
                }
            } else {
                Log.d("GrChLIST", "resultCode not STATUS_OK");
            }
        }
    }

    // Sets up recycler view
    private void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mChannelListAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        // If user scrolls to bottom of the list, loads more channels.
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                if(PreferenceUtils.getPlanVersionStatus()){
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (mLayoutManager.findLastVisibleItemPosition() == mChannelListAdapter.getItemCount() - 1) {
                            if (mChannelCollection != null) {
                                mChannelCollection.fetch(new CompletionHandler() {
                                    @Override
                                    public void onCompleted(SendBirdException e) {
                                        if (mSwipeRefresh.isRefreshing()) {
                                            mSwipeRefresh.setRefreshing(false);
                                        }
                                    }
                                });
                            }
                        }
                    }
//                }
            }
        });

    }

    // Sets up channel list adapter
    private void setUpChannelListAdapter() {
        mChannelListAdapter.setOnItemClickListener(new GroupChannelListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(GroupChannel channel) {
                enterGroupChannel(channel);
            }
        });

        mChannelListAdapter.setOnItemLongClickListener(new GroupChannelListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(GroupChannel channel) {
                showChannelOptionsDialog(channel);
            }
        });

    }

    /**
     * Displays a dialog listing channel-specific options.
     */
    private void showChannelOptionsDialog(final GroupChannel channel) {
        String[] options;
        final boolean pushCurrentlyEnabled = channel.isPushEnabled();

        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.request_delete_channel, channel.getName()))
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        leaveChannel(channel);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .create().show();
    }

    /**
     * Enters a Group Channel. Upon entering, a GroupChatFragment will be inflated
     * to display messages within the channel.
     *
     * @param channel The Group Channel to enter.
     */
    void enterGroupChannel(GroupChannel channel) {
        final String channelUrl = channel.getUrl();

        enterGroupChannel(channelUrl);
    }

    /**
     * Enters a Group Channel with a URL.
     *
     * @param channelUrl The URL of the channel to enter.
     */
    void enterGroupChannel(String channelUrl) {

        Intent mainIntent = new Intent(getActivity(), GroupChatActivity.class);
        mainIntent.putExtra(EXTRA_GROUP_CHANNEL_URL, channelUrl);
        startActivity(mainIntent);
    }

    /**
     * Creates a new query to get the list of the user's Group Channels,
     * then replaces the existing dataset.
     */
    private void refresh() {
        if (mChannelCollection != null) {
            mChannelCollection.remove();
        }


        mChannelListAdapter.clearMap();
        mChannelListAdapter.clearChannelList();
        GroupChannelListQuery query = GroupChannel.createMyGroupChannelListQuery();
        query.setLimit(CHANNEL_LIST_LIMIT);
        mChannelCollection = new ChannelCollection(query);
        mChannelCollection.setCollectionHandler(mChannelCollectionHandler);
        mChannelCollection.fetch(new CompletionHandler() {
            @Override
            public void onCompleted(SendBirdException e) {
                if (mSwipeRefresh.isRefreshing()) {
                    mSwipeRefresh.setRefreshing(false);
                }
            }
        });


    }

    private String isAdmin = "";

    private void setMemberList(List<Member> memberList, GroupChannel channel) {
        List<Member> sortedUserList = new ArrayList<>();
        List<String> sortedRoleList = new ArrayList<>();
        String myUserId = SyncManagerUtils.getMyUserId();
        boolean isUserAdmin = false;
        String userAdminId = "";
        for (Member member : memberList) {
            if (member.getUserId().equals(myUserId)) {
                sortedUserList.add(0, member);
                sortedRoleList.add(0, member.getRole().getValue());

                //check
                if (member.getRole().getValue().equalsIgnoreCase("OPERATOR")) {
                    isUserAdmin = true;
                    userAdminId = member.getUserId();
                }

            } else {
                sortedUserList.add(member);
                sortedRoleList.add(member.getRole().getValue());
            }
        }
        isAdmin = userAdminId;


        //checked state user and admin
        if (isAdmin.equalsIgnoreCase(SendBird.getCurrentUser().getUserId())) {
            //admin
            String channel_Id = channel.getUrl();
            channel.delete(new GroupChannel.GroupChannelDeleteHandler() {
                @Override
                public void onResult(SendBirdException e) {
                    if (e != null) {
                        // Error!
                        return;
                    }
                    //call delete api for server chat delete
                    callApiForDeleteChat(channel_Id);
                }
            });

        } else {
            //other user
            channel.leave(new GroupChannel.GroupChannelLeaveHandler() {
                @Override
                public void onResult(SendBirdException e) {
                    if (e != null) {
                        // Error!
                        return;
                    }
                    // Re-query message list
                    refresh();
                }
            });
        }
    }

    /**
     * Leaves a Group Channel.
     *
     * @param channel The channel to leave.
     */
    private void leaveChannel(final GroupChannel channel) {
        setMemberList(channel.getMembers(), channel);
    }

    private void callApiForDeleteChat(String channel_id) {

        if (!NetworkUtils.isInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            return;
        }

        //buyer_id, channel_id, owner_id, property_id, type, status
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("channel_id", RequestBody.create(MediaType.parse("multipart/form-data"), channel_id));

        UserServices userService = ApiFactory.getInstance(getActivity()).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.deleteChannel(linkedHashMap);
        Disposable disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    //get channel url
                    // Re-query message list
                    refresh();
                } else {
                    Log.e("TAG", "Delete else part");
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
            }
        });
    }

    ChannelCollectionHandler mChannelCollectionHandler = new ChannelCollectionHandler() {
        @Override
        public void onChannelEvent(final ChannelCollection channelCollection, final List<GroupChannel> list, final ChannelEventAction channelEventAction) {
            if (getActivity() == null) {
                return;
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mSwipeRefresh.isRefreshing()) {
                        mSwipeRefresh.setRefreshing(false);
                    }

                    switch (channelEventAction) {
                        case INSERT:
                            mChannelListAdapter.clearMap();
                            mChannelListAdapter.insertChannels(list, channelCollection.getQuery().getOrder());
                            break;

                        case UPDATE:
                            mChannelListAdapter.clearMap();
                            mChannelListAdapter.updateChannels(list);
                            break;

                        case MOVE:
                            mChannelListAdapter.clearMap();
                            mChannelListAdapter.moveChannels(list, channelCollection.getQuery().getOrder());
                            break;

                        case REMOVE:
                            mChannelListAdapter.clearMap();
                            mChannelListAdapter.removeChannels(list);
                            break;

                        case CLEAR:
                            mChannelListAdapter.clearMap();
                            mChannelListAdapter.clearChannelList();
                            break;
                    }
                }
            });

            if (list.size() == 0) {
                mRecyclerView.setVisibility(View.GONE);
                moTvNoChatList.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                moTvNoChatList.setVisibility(View.GONE);
            }

        }
    };
}