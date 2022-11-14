package com.flippbidd.activity.Rating;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.flippbidd.Adapter.Rating.RateAndReviewListAdapter;
import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.RefreshListener;
import com.flippbidd.Model.Response.Rating.RetingResponseList;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseAppCompatActivity;
import com.flippbidd.baseclass.BaseApplication;

import java.util.LinkedHashMap;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RatingList extends BaseAppCompatActivity implements RefreshListener {

    @BindView(R.id.imageViewBack)
    ImageView imageViewBack;
    @BindView(R.id.rateandreviewRecyclerView)
    RecyclerView rateandreviewRecyclerView;
    @BindView(R.id.btnGiveFeedback)
    CustomAppCompatButton btnGiveFeedback;
    @BindView(R.id.linearlayoutOfFeedbackBtn)
    LinearLayout linearlayoutOfFeedbackBtn;

    @BindView(R.id.textViewNoDataMessage)
    CustomTextView textViewNoDataMessage;
    @BindView(R.id.noRateView)
    View noRateView;

    RateAndReviewListAdapter mRateAndReviewListAdapter;
    LinearLayoutManager linearLayoutManager;
    private String userId;
    Disposable disposable;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_rating_layout;
    }


    private void statusBarColorChanged() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#3FA3D1"));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        statusBarColorChanged();
        super.onCreate(savedInstanceState);
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            userId = mBundle.getString("user_id");
            //check user
            if (userId.equalsIgnoreCase(BaseApplication.getInstance().getLoginID())) {
                linearlayoutOfFeedbackBtn.setVisibility(View.GONE);
            } else {
                linearlayoutOfFeedbackBtn.setVisibility(View.VISIBLE);
            }
        }

        mRateAndReviewListAdapter = new RateAndReviewListAdapter(mBaseAppCompatActivity);
        linearLayoutManager = new LinearLayoutManager(mBaseAppCompatActivity);
        rateandreviewRecyclerView.setLayoutManager(linearLayoutManager);
        rateandreviewRecyclerView.setAdapter(mRateAndReviewListAdapter);

        getDetailsProperty(true);
    }

    @Override
    protected void onPermissionGranted() {

    }

    @Override
    protected void onLocationEnable() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
        finish(true);
    }

    @OnClick(R.id.imageViewBack)
    void viewBackClick() {
        setResult(RESULT_OK);
        onBackPressed();
    }

    @OnClick(R.id.btnGiveFeedback)
    void viewClickBtn() {
        startActivityIfNeeded(new Intent(RatingList.this, RateAndReview.class).putExtra("user_id", userId), 807);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (resultCode == 807) {
                getDetailsProperty(false);
                setResult(RESULT_OK);
            }
        }
    }

    private void getDetailsProperty(boolean isProgress) {
        //token, sender_id, receiver_id, sender_qb_id, receiver_qb_id, dialog_id, dialog_type
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }

        //token,username,qb_id
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
//        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        linkedHashMap.put("user_id", RequestBody.create(MediaType.parse("multipart/form-data"), userId));


        showProgressDialog(isProgress);

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<RetingResponseList> observable;
        observable = userService.getRatingList(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<RetingResponseList>() {
            @Override
            public void onSuccess(RetingResponseList response) {
                hideProgressDialog();
                LogUtils.LOGD("List", "onSuccess() called with: response = [" + response.toString() + "]");

                if (response.getSuccess()) {
                    noRateView.setVisibility(View.GONE);
                    mRateAndReviewListAdapter.addAll(response.getData());
                } else {
                    noRateView.setVisibility(View.VISIBLE);
                    textViewNoDataMessage.setText("No Rate & Review available.");
                }

            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();

            }
        });
    }

    private void disposeCall() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposeCall();
    }


    @Override
    public void refresh(boolean b) {
        if (!b) {
            getDetailsProperty(true);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        getDetailsProperty(false);
    }
}
