package com.flippbidd.sendbirdSdk.groupchannel;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.CommonResponse_;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.utils.ImageUtils;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * A simple adapter that displays a list of Users.
 */
public class UserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private List<String> mUserRole;
    private String mChannelUrl, mProID, mOwnEmail;
    private boolean mIsGroupChannel;
    private boolean adminUserId;
    String adminID = "";

    public UserListAdapter(Context context, String channelUrl, boolean isGroupChannel) {
        mContext = context;
        mUsers = new ArrayList<>();
        mChannelUrl = channelUrl;
        mIsGroupChannel = isGroupChannel;
        mUserRole = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((UserHolder) holder).bind(mContext, (UserHolder) holder, mUsers.get(position), mUserRole.get(position));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void setUserList(List<? extends User> users, List<String> mRole, boolean isView, String admindUserId, String mPropertyID, String mOwnerEmail) {
        adminUserId = isView;
        adminID = admindUserId;
        mProID = mPropertyID;
        mOwnEmail = mOwnerEmail;
        mUsers.clear();
        mUsers.addAll(users);
        mUserRole.clear();
        mUserRole.addAll(mRole);
        notifyDataSetChanged();
    }

    private class UserHolder extends RecyclerView.ViewHolder {
        private View view;
        private CustomTextView nameText;
        private ImageView profileImage, imageUserListPock;
        private ImageView blockedImage;
        private RelativeLayout relativeLayoutBlock;
        private CustomTextView textViewBlocked, textViewRemove, textViewAdmin;

        UserHolder(View itemView) {
            super(itemView);

            view = itemView;
            nameText = itemView.findViewById(R.id.text_user_list_nickname);
            profileImage = itemView.findViewById(R.id.image_user_list_profile);
            blockedImage = itemView.findViewById(R.id.image_user_list_blocked);
            relativeLayoutBlock = itemView.findViewById(R.id.relative_layout_blocked_by_me);
            textViewBlocked = itemView.findViewById(R.id.text_view_blocked);
            textViewRemove = itemView.findViewById(R.id.text_view_remove);
            imageUserListPock = itemView.findViewById(R.id.image_user_list_pock);
            textViewAdmin = itemView.findViewById(R.id.text_view_admin);
        }

        private void bind(final Context context, final UserHolder holder, final User user, String userRole) {


            if (user.getProfileUrl() != null && !user.getProfileUrl().equalsIgnoreCase("")) {
                ImageUtils.displayRoundImageFromUrl(context, user.getProfileUrl(), profileImage);
            } else {
                Glide.with(context)
                        .load(R.drawable.profile_thumbnail)
                        .into(profileImage);
            }
            Log.e("TAG", "Email User " + adminID);
            Log.e("TAG", "Other User " + user.getUserId());
            Log.e("TAG", "Current User " + SendBird.getCurrentUser().getUserId());

            if (SendBird.getCurrentUser() != null && SendBird.getCurrentUser().getUserId().equals(user.getUserId())) {
                relativeLayoutBlock.setVisibility(View.GONE);
                textViewBlocked.setVisibility(View.GONE);
                nameText.setText("You");

            } else {
                nameText.setText(user.getNickname());
                if (adminUserId) {
                    textViewRemove.setVisibility(View.VISIBLE);
                    if (!mProID.equalsIgnoreCase("")) {
                        imageUserListPock.setVisibility(View.VISIBLE);
                    } else {
                        imageUserListPock.setVisibility(View.GONE);
                    }
                } else {
                    textViewRemove.setVisibility(View.GONE);
                    imageUserListPock.setVisibility(View.GONE);
                }
                textViewAdmin.setVisibility(View.GONE);
                relativeLayoutBlock.setVisibility(View.VISIBLE);
                holder.textViewRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GroupChannel.getChannel(mChannelUrl, new GroupChannel.GroupChannelGetHandler() {
                            @Override
                            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                                groupChannel.banUser(user, "remove user from admin", 2, new GroupChannel.GroupChannelBanHandler() {
                                    @Override
                                    public void onResult(SendBirdException e) {
                                        if (e != null) {    // Error.
                                            return;
                                        }
                                        mUsers.remove(user);
                                        notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                    }
                });

                holder.imageUserListPock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //token, login_id, user_id, property_id
                        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
                        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
                        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
                        linkedHashMap.put("email_address", RequestBody.create(MediaType.parse("multipart/form-data"), user.getUserId()));
                        linkedHashMap.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), mProID));
                        linkedHashMap.put("channel_url", RequestBody.create(MediaType.parse("multipart/form-data"), mChannelUrl));

                        UserServices userService = ApiFactory.getInstance(context).provideService(UserServices.class);
                        Observable<CommonResponse_> observable;
                        observable = userService.pushPock(linkedHashMap);
                        Disposable disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse_>() {
                            @Override
                            public void onSuccess(CommonResponse_ response) {
                                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                                if (response.getSuccess()) {
                                    // Re-query message list
                                    Log.e("TAG", "Push sent");
                                } else {
                                    Log.e("TAG", "Push error");
                                }
                            }

                            @Override
                            public void onFailed(Throwable throwable) {
                            }
                        });

                        //vibrate on click
                        Vibrator vb = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vb.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            vb.vibrate(500);
                        }
                    }
                });
            }

            if (adminID.equalsIgnoreCase(user.getUserId())) {
                textViewAdmin.setVisibility(View.VISIBLE);
            } else {
                textViewAdmin.setVisibility(View.GONE);
            }


            final boolean isBlockedByMe = ((Member) user).isBlockedByMe();
            if (isBlockedByMe) {
                blockedImage.setVisibility(View.VISIBLE);
                textViewBlocked.setVisibility(View.VISIBLE);
            } else {
                blockedImage.setVisibility(View.GONE);
                textViewBlocked.setVisibility(View.GONE);
            }
        }
    }

}

