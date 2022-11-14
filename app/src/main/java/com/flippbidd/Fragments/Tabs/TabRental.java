package com.flippbidd.Fragments.Tabs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.flippbidd.Adapter.LikesList.LikesRentalListing;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.RefreshListener;
import com.flippbidd.Model.Response.CommonResponse;
import com.flippbidd.Model.Response.Likes.LikesData;
import com.flippbidd.Model.Response.Likes.LikesResponse;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.activity.Details.PropertyDetails;
import com.flippbidd.baseclass.BaseFragment;

import java.util.LinkedHashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import cn.refactor.lib.colordialog.PromptDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class TabRental extends BaseFragment implements RefreshListener, LikesRentalListing.onItemClickLisnear {

    public static final String TAG = LogUtils.makeLogTag(TabRental.class);

    @BindView(R.id.recyclerViewLikesList)
    RecyclerView recyclerViewLikesList;
    @BindView(R.id.tvNoItems)
    CustomTextView loTvNoItems;

    LikesRentalListing mLikesPropertyListing;
    Disposable disposable;
    LinearLayoutManager mLinearLayoutManager;
    //chat code
    LikesData mLikeSelectedDetails;
    private String mScreenName, mCommonDialogId;
    private int myQbID, QbIdOfRecUser;

    @Override
    protected int getLayoutResource() {
        return R.layout.tab_common_layout;
    }

    @Override
    public void refresh(boolean b) {
        if (!b) {
            /*if (mLikesPropertyListing != null)
                mLikesPropertyListing.notifyDataSetChanged();*/
            openComingSonDialog();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        serRecyclerViewlayout();
    }

    private void openComingSonDialog() {
        //coming soon
        PromptDialog mPromptDialog = new PromptDialog(getActivity());
        mPromptDialog.setCanceledOnTouchOutside(false);
        mPromptDialog.setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS);
        mPromptDialog.setAnimationEnable(true);
        mPromptDialog.setTitleText("Flippbidd");
        mPromptDialog.setContentText("Coming soon");
        mPromptDialog.setPositiveListener(mBaseAppCompatActivity.getString(R.string.string_ok), new PromptDialog.OnPositiveListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
            }
        });
        mPromptDialog.setNegativeListener(mBaseAppCompatActivity.getString(R.string.string_Cancel), new PromptDialog.OnNegativeListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
            }
        });
        mPromptDialog.show();
    }

    public void serRecyclerViewlayout() {
        mLikesPropertyListing = new LikesRentalListing(mBaseAppCompatActivity, Constants.SCREEN_SELCTION_NAME.SELECTE_LIKES_RENTAL);

        mLinearLayoutManager = new LinearLayoutManager(mBaseAppCompatActivity);
        recyclerViewLikesList.setLayoutManager(mLinearLayoutManager);
        recyclerViewLikesList.setItemAnimator(new DefaultItemAnimator());
        //set data in adapter
        recyclerViewLikesList.setAdapter(mLikesPropertyListing);

        callLikesDataApi(true);

        if (mLikesPropertyListing != null) {
            mLikesPropertyListing.setItemOnClickEvent(this);
        }


    }

    private void callLikesDataApi(boolean isProgress) {

        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }

        //token,login_id,limit,offset
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
//        hashMap.put("limit", RequestBody.create(MediaType.parse("multipart/form-data"), "100"));
//        hashMap.put("offset", RequestBody.create(MediaType.parse("multipart/form-data"), "0"));
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), "rental"));

        showProgressDialog(isProgress);

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<LikesResponse> observable;

        observable = userService.likesList(hashMap);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<LikesResponse>() {
            @Override
            public void onSuccess(LikesResponse response) {
                hideProgressDialog();
                if (response.getSuccess()) {
                    if(response.getData()!=null && response.getData().size()!=0) {
                        loTvNoItems.setVisibility(View.GONE);
                        mLikesPropertyListing.addAll(response.getData());
                    }else {
                        loTvNoItems.setVisibility(View.VISIBLE);
                    }
                }else {
                    loTvNoItems.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                loTvNoItems.setVisibility(View.VISIBLE);
                hideProgressDialog();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposeCall();
    }

    private void disposeCall() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onClickEvent(int position, LikesData mLikeData, String mActionType) {
        switch (mActionType) {
            case Constants.ACTION.VIEW_ACTION:
//                mBaseAppCompatActivity.startActivity(CommonDetailsActivity.getIntentActivity(mBaseAppCompatActivity, mLikeData.getPropertyDetails().getRentalId(), mLikeData.getLoginId(), Constants.SCREEN_SELCTION_NAME.SELECTE_RENTAL,false), true);
                mBaseAppCompatActivity.startActivity(PropertyDetails.getIntentActivity(mBaseAppCompatActivity, mLikeData.getCommonId(), mLikeData.getPropertyDetails().getLoginId(), Constants.SCREEN_SELCTION_NAME.SELECTE_RENTAL,mLikeData.getPropertyDetails().isExpiriedStatus()), true);
                break;
            case Constants.ACTION.PROPERTY_UNLIKES_ACTION:
                callRentalLikesUnLikesApi(position, mLikeData, "0");
                break;
            case Constants.ACTION.MESSAGES_VIEW_ACTION:
                if (!UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId().equalsIgnoreCase(mLikeData.getUserDetails().getLoginId())) {
                    if (!mLikeData.getPropertyDetails().isExpiriedStatus()) {
                        mLikeSelectedDetails = null;
                        mLikeSelectedDetails = mLikeData;
                        mScreenName = "rental";
                       // singToQB();
                    } else {
                        openMessageLimiOver();
                    }
                }


                break;
        }
    }

    //show Message Limit Over Dialog
    private void openMessageLimiOver() {

/*

        PromptDialog mPromptDialog = new PromptDialog(mBaseAppCompatActivity);
        mPromptDialog.setCanceledOnTouchOutside(false);
        mPromptDialog.setDialogType(PromptDialog.DIALOG_TYPE_INFO);
        mPromptDialog.setAnimationEnable(true);
        mPromptDialog.setTitleText("Credit Buy");
        mPromptDialog.setContentText(mBaseAppCompatActivity.getResources().getString(R.string.messages_limit_error));
        mPromptDialog.setPositiveListener(mBaseAppCompatActivity.getString(R.string.string_Cancel), new PromptDialog.OnPositiveListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
            }
        });
        mPromptDialog.setNegativeListener(mBaseAppCompatActivity.getString(R.string.string_Buy), new PromptDialog.OnNegativeListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
                mBaseAppCompatActivity.startActivity(new Intent(mBaseAppCompatActivity, CrediteActivity.class), true);
            }
        });
        mPromptDialog.show();
*/
        PromptDialog mPromptDialog = new PromptDialog(mBaseAppCompatActivity);
        mPromptDialog.setCanceledOnTouchOutside(false);
        mPromptDialog.setDialogType(PromptDialog.DIALOG_TYPE_INFO);
        mPromptDialog.setAnimationEnable(true);
        mPromptDialog.setTitleText("Flippbidd");
        mPromptDialog.setContentText(mBaseAppCompatActivity.getResources().getString(R.string.messages_limit_error));
        mPromptDialog.setPositiveListener(mBaseAppCompatActivity.getString(R.string.string_Cancel), new PromptDialog.OnPositiveListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
            }
        });
        mPromptDialog.setNegativeListener(mBaseAppCompatActivity.getString(R.string.string_ok), new PromptDialog.OnNegativeListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
            }
        });
        mPromptDialog.show();
    }


    private void callRentalLikesUnLikesApi(final int position, LikesData mRentalData, String isLikes) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        hashMap.put("rental_id", RequestBody.create(MediaType.parse("multipart/form-data"), mRentalData.getCommonId()));
        hashMap.put("is_like", RequestBody.create(MediaType.parse("multipart/form-data"), isLikes));

        showProgressDialog(true);
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.likesRental(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                if (response.getSuccess()) {
                    mLikesPropertyListing.deleteAdapter(position);
                } else {
                    openErorrDialog(response.getText());
                }
                LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });

    }

/*
    //chat login with Qb
    private void singToQB() {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(true);
        QBUser qbUser = new QBUser(getAppendString(), Consts.userPassword);
        qbUser.setFullName(UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getFullName());

        QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                hideProgressDialog();
                if (qbUser.getId() != null && !qbUser.getId().equals("")) {
                    LogUtils.LOGD(TAG, "onSuccess() called with: response QbID= [" + qbUser.getId() + "]");
                    callUpdateQBID(qbUser.getId());
                }
            }

            @Override
            public void onError(QBResponseException e) {
                hideProgressDialog();
                //get Qb_id
                getQbID();
            }
        });
       // SubscribeService.subscribeToPushes(mBaseAppCompatActivity, true);
    }

    private void callUpdateQBID(final Integer qbId) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(true);
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable = userService.updateQBID(UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()
                , UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()
                , qbId
                , UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getUsername());

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                if (response.getSuccess()) {
                    myQbID = qbId;
                    loginToQBs();
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }


    private void getQbID() {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(true);
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        linkedHashMap.put("username", RequestBody.create(MediaType.parse("multipart/form-data"), getAppendString()));


        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<AddResponse> observable = userService.getQBID(linkedHashMap);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<AddResponse>() {
            @Override
            public void onSuccess(AddResponse response) {
                hideProgressDialog();
                if (response.getSuccess()) {
                    myQbID = response.getId();
                    loginToQBs();
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }

    private void loginToQBs() {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(true);
        QBChatService chatService = QBChatService.getInstance();
        boolean isLoggedIn = chatService.isLoggedIn();
        if (isLoggedIn) {
            ChatHelper.getInstance().logout(mBaseAppCompatActivity);
        }
        QBUser qbUser = new QBUser(getAppendString(), Consts.userPassword);
        QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                hideProgressDialog();
                ChatHelper.getInstance();
                createDialog();
            }

            @Override
            public void onError(QBResponseException e) {
                hideProgressDialog();
            }
        });
        //SubscribeService.subscribeToPushes(mBaseAppCompatActivity, true);
    }

    private String getAppendString() {

        String mString = mLikeSelectedDetails.getCommonId() + "_" + UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId() + "_" + mScreenName;
        return mString;
    }

    private void createDialog() {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(true);
        QbIdOfRecUser = Integer.parseInt(mLikeSelectedDetails.getPropertyDetails().getSenderQbId());

        ArrayList<Integer> occupantIdsList = new ArrayList<Integer>();
        occupantIdsList.add(myQbID);//my id
        occupantIdsList.add(QbIdOfRecUser);
        QBChatDialog dialog = DialogUtils.buildDialog(mLikeSelectedDetails.getUserDetails().getFullName(), QBDialogType.PRIVATE, occupantIdsList);

        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog result, Bundle params) {
                hideProgressDialog();
                mCommonDialogId = "";
                mCommonDialogId = result.getDialogId();
                createCallApi(result.getDialogId());
            }

            @Override
            public void onError(QBResponseException responseException) {
                hideProgressDialog();
            }
        });

    }

    private void createCallApi(String dialogId) {
        //token, sender_id, receiver_id, sender_qb_id, receiver_qb_id, dialog_id, dialog_type
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        //token, sender_id, receiver_id, sender_qb_id, receiver_qb_id, dialog_id, dialog_type
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        linkedHashMap.put("sender_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        linkedHashMap.put("receiver_id", RequestBody.create(MediaType.parse("multipart/form-data"), mLikeSelectedDetails.getLoginId()));
        linkedHashMap.put("sender_qb_id", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(myQbID)));
        linkedHashMap.put("receiver_qb_id", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(QbIdOfRecUser)));
        linkedHashMap.put("dialog_id", RequestBody.create(MediaType.parse("multipart/form-data"), dialogId));
        linkedHashMap.put("dialog_type", RequestBody.create(MediaType.parse("multipart/form-data"), "GROUP"));
        linkedHashMap.put("common_id", RequestBody.create(MediaType.parse("multipart/form-data"), mLikeSelectedDetails.getCommonId()));
        linkedHashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), mScreenName));


        showProgressDialog(true);
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);

        Observable<CommonResponse> observable;

        observable = userService.chat(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
                loginToQB();
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }

    private void loginToQB() {
        //current user
        ArrayList<QBUser> qbUsers = new ArrayList<>();
        QBUser qbUser = new QBUser();
        qbUser.setId(myQbID);
        qbUser.setPassword(Consts.userPassword);
        qbUser.setFullName(getAppendString());
        qbUsers.add(qbUser);

        // user
        QBUser qbUser1 = new QBUser();
        qbUser1.setId(QbIdOfRecUser);
        qbUser1.setPassword(Consts.userPassword);
        qbUsers.add(qbUser1);


        NewChatActivity.startForResult(mBaseAppCompatActivity, qbUsers, mCommonDialogId, mLikeSelectedDetails.getCommonId(), mScreenName,mLikeSelectedDetails.getLoginId(),mLikeSelectedDetails.getUserDetails().getFullName(),165);

    }
*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
