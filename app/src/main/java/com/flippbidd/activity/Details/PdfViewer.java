package com.flippbidd.activity.Details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseAppCompatActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class PdfViewer extends BaseAppCompatActivity {


    private WebView webView;
    private String mURL, mFileViewer;

    @BindView(R.id.image_toolbar)
    ImageView image_toolbar;
    @BindView(R.id.txt_title_toolbar)
    CustomTextView txt_title_toolbar;


    public static Intent getIntentActivity(Context mContext, String mUrl, String fileViewer) {
        Intent mIntent = new Intent(mContext, PdfViewer.class);
        mIntent.putExtra(Constants.EXTRA.DATA, mUrl);
        mIntent.putExtra(Constants.EXTRA.TITLE, fileViewer);
        return mIntent;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }

        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mURL = mBundle.getString(Constants.EXTRA.DATA);
            mFileViewer = mBundle.getString(Constants.EXTRA.TITLE);
        }

        showProgressDialog(true);
        webView = (WebView) findViewById(R.id.webview_compontent);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new Callback());
        //following lines are to show the loader untile downloading the pdf file for view.
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
            }
        });
        if (mFileViewer.isEmpty()) {
            webView.loadUrl(mURL);   // webview loader to load the URL of file
        } else {
            txt_title_toolbar.setText(mFileViewer);
            webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + mURL);

        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                hideProgressDialog();
            }
        }, 3000);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.pdf_viewer_layout;
    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view, String url) {
            return (false);
        }
    }

    @OnClick(R.id.image_toolbar)
    void backEvent() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(true);
    }

    @Override
    protected void onPermissionGranted() {

    }

    @Override
    protected void onLocationEnable() {

    }
}
