package com.rtoosh.provider.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Config;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TermsConditionsActivity extends AppBaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbarTitle) TextView toolbarTitle;
    @BindView(R.id.wvTerms) WebView wvTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle.setText(R.string.terms_conditions);

        initWebView();
        showDialog();
        wvTerms.loadUrl(Config.TERMS_CONDITIONS_URL);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        wvTerms.setWebChromeClient(new MyWebChromeClient(this));
        wvTerms.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            /*@Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                wvTerms.loadUrl(url);
                return true;
            }*/

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dismissDialog();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                dismissDialog();
            }
        });

      //  wvTerms.clearCache(true);
        wvTerms.clearHistory();
        wvTerms.getSettings().setJavaScriptEnabled(true);
        wvTerms.setHorizontalScrollBarEnabled(false);
    }

    private class MyWebChromeClient extends WebChromeClient {
        Context context;

        private MyWebChromeClient(Context context) {
            super();
            this.context = context;
        }
    }
}
