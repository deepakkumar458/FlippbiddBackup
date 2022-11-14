package com.flippbidd.Fragments.Tabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.flippbidd.Adapter.LikesList.LikesPropertyListing;
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
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.baseclass.BaseFragment;

import java.util.LinkedHashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import cn.refactor.lib.colordialog.ColorDialog;
import cn.refactor.lib.colordialog.PromptDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class TabProperty extends BaseFragment implements RefreshListener, LikesPropertyListing.onItemClickLisnear {

    public static final String TAG = LogUtils.makeLogTag(TabProperty.class);

    @BindView(R.id.recyclerViewLikesList)
    RecyclerView recyclerViewLikesList;
    @BindView(R.id.tvNoItems)
    CustomTextView loTvNoItems;

    LikesPropertyListing mLikesPropertyListing;
    Disposable disposable;
    LinearLayoutManager mLinearLayoutManager;

    //chat code
    LikesData mLikeSelectedDetails;
    private String mScreenName;

    @Override
    protected int getLayoutResource() {
        return R.layout.tab_common_layout;
    }

    @Override
    public void refresh(boolean b) {
        if (!b) {
            if (mLikesPropertyListing != null)
                mLikesPropertyListing.notifyDataSetChanged();
        }
    }

    public void serRecyclerViewlayout() {
        mLikesPropertyListing = new LikesPropertyListing(mBaseAppCompatActivity, Constants.SCREEN_SELCTION_NAME.SELECTE_LIKES_PROPERTY);

        mLinearLayoutManager = new LinearLayoutManager(mBaseAppCompatActivity);
        recyclerViewLikesList.setLayoutManager(mLinearLayoutManager);
        recyclerViewLikesList.setItemAnimator(new DefaultItemAnimator());
        //set data in adapter
        recyclerViewLikesList.setAdapter(mLikesPropertyListing);

        callLikesDataApi(true);

        if (mLikesPropertyListing != null)
            mLikesPropertyListing.setItemOnClickEvent(this);


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        serRecyclerViewlayout();
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

        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), "property"));

        showProgressDialog(isProgress);

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<LikesResponse> observable;

        observable = userService.likesList(hashMap);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<LikesResponse>() {
            @Override
            public void onSuccess(LikesResponse response) {
                hideProgressDialog();
                if (response.getSuccess()) {
                    if (response.getData() != null && response.getData().size() != 0) {
                        loTvNoItems.setVisibility(View.GONE);
                        mLikesPropertyListing.addAll(response.getData());
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
    public void onClickEvent(int position, LikesData mLikeData, String mActionType) {

        switch (mActionType) {
            case Constants.ACTION.VIEW_ACTION:
                if (mLikeData != null && !mLikeData.equals("")) {
                    if (mLikeData.getPropertyDetails().getPropertyId() != null && !mLikeData.getPropertyDetails().getPropertyId().equalsIgnoreCase("")) {
                        if (mLikeData.getPropertyDetails().getIsAvailable().equalsIgnoreCase("1")) {
                            mBaseAppCompatActivity.startActivityIfNeeded(PropertyDetails.getIntentActivity(mBaseAppCompatActivity, mLikeData.getPropertyDetails().getPropertyId(), mLikeData.getPropertyDetails().getLoginId(), Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY, mLikeData.getPropertyDetails().isExpiriedStatus()), Constants.REQUEST_UNAVILABLE);
                        } else {
                            if (mLikeData.getLoginId().equalsIgnoreCase(BaseApplication.getInstance().getLoginID())) {
                                mBaseAppCompatActivity.startActivityIfNeeded(PropertyDetails.getIntentActivity(mBaseAppCompatActivity, mLikeData.getPropertyDetails().getPropertyId(), mLikeData.getPropertyDetails().getLoginId(), Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY, mLikeData.getPropertyDetails().isExpiriedStatus()), Constants.REQUEST_UNAVILABLE);
                            } else {
                                showNotAvailable();
                            }
                        }
                    } else {
                        showNotAvailable();
                    }
                } else {
                    showNotAvailable();
                }
                break;
            case Constants.ACTION.PROPERTY_UNLIKES_ACTION:
                callLikesUnLikesApi(position, mLikeData, "0");
                break;
            case Constants.ACTION.MESSAGES_VIEW_ACTION:

                if (!UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId().equalsIgnoreCase(mLikeData.getUserDetails().getLoginId())) {
                    if (!mLikeData.getPropertyDetails().isExpiriedStatus()) {
                        mLikeSelectedDetails = null;
                        mLikeSelectedDetails = mLikeData;
                        mScreenName = "property";
                        //singToQB();
                    } else {
                        openMessageLimiOver();
                    }
                }

                break;
        }
    }

    private void showNotAvailable() {
        ColorDialog dialog = new ColorDialog(getActivity());
        dialog.setTitle("Flippbidd");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentText(getString(R.string.String_property_not_available));
        dialog.setPositiveListener("OK", new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                dialog.dismiss();
            }
        }).show();
    }

    //show Message Limit Over Dialog
    private void openMessageLimiOver() {
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

    private void callLikesUnLikesApi(final int position, LikesData mPropertyData, String isLikes) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        hashMap.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), mPropertyData.getCommonId()));
        hashMap.put("is_like", RequestBody.create(MediaType.parse("multipart/form-data"), isLikes));

        showProgressDialog(true);
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.propertyLikes(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                if (response.getSuccess()) {
                    mLikesPropertyListing.deleteAdapter(position);
                } else {
                    openErorrDialog(response.getText());
                }
                //LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_UNAVILABLE) {
            callLikesDataApi(true);
        }
    }
}
