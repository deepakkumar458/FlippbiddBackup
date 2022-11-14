package com.flippbidd.Fragments.Tabs;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.flippbidd.Adapter.LikesList.LikesServiceListing;
import com.flippbidd.CustomClass.CustomTextView;

import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.RefreshListener;
import com.flippbidd.Model.Response.AddCommon.AddResponse;
import com.flippbidd.Model.Response.CommonResponse;
import com.flippbidd.Model.Response.Likes.Service.ServiceLikeDetails;
import com.flippbidd.Model.Response.Likes.Service.ServiceLikeListResponse;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.activity.Details.CommonDetails.CommonDetailsActivity;
import com.flippbidd.activity.Details.PropertyDetails;
import com.flippbidd.baseclass.BaseFragment;
import java.util.LinkedHashMap;

import butterknife.BindView;
import cn.refactor.lib.colordialog.PromptDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class TabService extends BaseFragment implements RefreshListener, LikesServiceListing.onItemClickLisnear {

    public static final String TAG = LogUtils.makeLogTag(TabService.class);

    @BindView(R.id.recyclerViewLikesList)
    RecyclerView recyclerViewLikesList;
    @BindView(R.id.tvNoItems)
    CustomTextView loTvNoItems;

    LinearLayoutManager mLinearLayoutManager;
    LikesServiceListing mLikesServiceListing;
    Disposable disposable;

    ServiceLikeDetails mLikeSelectedDetails;
    private String mScreenName, mCommonDialogId;
    private int myQbID, QbIdOfRecUser;


    @Override
    protected int getLayoutResource() {
        return R.layout.tab_common_layout;
    }

    @Override
    public void refresh(boolean b) {
        if (!b) {
            openComingSonDialog();
            /*if (mLikesServiceListing != null)
                mLikesServiceListing.notifyDataSetChanged();*/
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //serRecyclerViewlayout();
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
        mLikesServiceListing = new LikesServiceListing(mBaseAppCompatActivity, Constants.SCREEN_SELCTION_NAME.SELECTE_LIKES_SERVICE);

        mLinearLayoutManager = new GridLayoutManager(mBaseAppCompatActivity, 1);
        recyclerViewLikesList.setLayoutManager(mLinearLayoutManager);
        recyclerViewLikesList.setItemAnimator(new DefaultItemAnimator());
        //set data in adapter
        recyclerViewLikesList.setAdapter(mLikesServiceListing);

        callLikesDataApi(true);

        if (mLikesServiceListing != null)
            mLikesServiceListing.setItemOnClickEvent(this);

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
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), "service"));

        showProgressDialog(isProgress);

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<ServiceLikeListResponse> observable;

        observable = userService.likesserviceList(hashMap);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<ServiceLikeListResponse>() {
            @Override
            public void onSuccess(ServiceLikeListResponse response) {
                hideProgressDialog();

                if (response.getSuccess()) {
                    if (response.getData() != null && response.getData().size() != 0) {
                        loTvNoItems.setVisibility(View.GONE);
                        mLikesServiceListing.addAll(response.getData());
                    } else {
                        loTvNoItems.setVisibility(View.VISIBLE);
                    }
                } else {
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
    public void onClickEvent(int position, ServiceLikeDetails mServiceListData, String mActionType) {
        switch (mActionType) {
            case Constants.ACTION.VIEW_ACTION:
//                mBaseAppCompatActivity.startActivity(CommonDetailsActivity.getIntentActivity(mBaseAppCompatActivity, mServiceListData.getCommonId(), mServiceListData.getLoginId(), Constants.SCREEN_SELCTION_NAME.SELECTE_SERVICE,false), true);
                //needed today
                mBaseAppCompatActivity.startActivity(PropertyDetails.getIntentActivity(mBaseAppCompatActivity, mServiceListData.getCommonId(), mServiceListData.getPropertyDetails().getLoginId(), Constants.SCREEN_SELCTION_NAME.SELECTE_SERVICE, false), true);
                break;
            case Constants.ACTION.PROPERTY_UNLIKES_ACTION:
                callServiceLikesUnLikesApi(position, mServiceListData, "0");
                break;
            case Constants.ACTION.MESSAGES_VIEW_ACTION:
                mLikeSelectedDetails = null;
                mLikeSelectedDetails = mServiceListData;
                mScreenName = "service";
                //singToQB();
                break;
        }
    }

    private void callServiceLikesUnLikesApi(final int position, ServiceLikeDetails mServiceData, String isLikes) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        //token,login_id,service_id,is_like
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        hashMap.put("service_id", RequestBody.create(MediaType.parse("multipart/form-data"), mServiceData.getCommonId()));
        hashMap.put("is_like", RequestBody.create(MediaType.parse("multipart/form-data"), isLikes));

        showProgressDialog(true);
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.isServiceLike(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                if (response.getSuccess()) {
                    mLikesServiceListing.deleteAdapter(position);
                } else {
                    openErorrDialog(response.getText());
                }

                // LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });

    }


    //chat login with Qb
    /*private void singToQB() {
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
                    // LogUtils.LOGD(TAG, "onSuccess() called with: response QbID= [" + qbUser.getId() + "]");
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
        //SubscribeService.subscribeToPushes(mBaseAppCompatActivity, true);
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
        // SubscribeService.subscribeToPushes(mBaseAppCompatActivity, true);
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
                //LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
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


        NewChatActivity.startForResult(mBaseAppCompatActivity, qbUsers, mCommonDialogId, mLikeSelectedDetails.getCommonId(), mScreenName, mLikeSelectedDetails.getLoginId(), mLikeSelectedDetails.getUserDetails().getFullName(),165);

    }*/
}
