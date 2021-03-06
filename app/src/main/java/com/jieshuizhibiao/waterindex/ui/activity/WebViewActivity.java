package com.jieshuizhibiao.waterindex.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.jieshuizhibiao.waterindex.R;
import com.jieshuizhibiao.waterindex.base.BaseActivity;
import com.jieshuizhibiao.waterindex.beans.GetUrlResponseBean;
import com.jieshuizhibiao.waterindex.contract.model.GetUrlModel;
import com.jieshuizhibiao.waterindex.contract.presenter.GetUrlPresenter;
import com.jieshuizhibiao.waterindex.contract.view.GetUrlViewImpl;
import com.jieshuizhibiao.waterindex.ui.widget.WaterIndexWebview;
import com.jieshuizhibiao.waterindex.utils.AndroidBug5497Workaround;
import com.jieshuizhibiao.waterindex.utils.InputMethodFix;
import com.jieshuizhibiao.waterindex.utils.StatusBarUtil;
import com.jieshuizhibiao.waterindex.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by songxiaotao on 2019/1/5.
 * Class Note:
 */

public class WebViewActivity extends BaseActivity implements GetUrlViewImpl {


    @BindView(R.id.left_ll)
    LinearLayout left_ll;
    @BindView(R.id.tv_title_center)
    TextView tvTitleCenter;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    String loadUrl;
    @BindView(R.id.progressBarLoading)
    ProgressBar mLoadingProgress;
    @BindView(R.id.webView)
    WaterIndexWebview webView;
    @BindView(R.id.content)
    LinearLayout rootLayout;

    private String type;
    private String title;
    //    private static final String URL = "URL";
    public static final String GET_URL_TYPE = "get_url_type";
    public static final String WEBVIEW_ACT_TITLE="webview_act_title";
    private GetUrlPresenter getUrlPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersionStatus(this, titleBar);
        getIntentExtra();
        getUrlPresenter = new GetUrlPresenter(new GetUrlModel());
        getUrlPresenter.attachView(this);
        getUrlPresenter.getUrl(this, type);
    }

    private void getIntentExtra() {
        Intent intent=getIntent();
        if(intent!=null){
            type = intent.getStringExtra(GET_URL_TYPE);
            title=intent.getStringExtra(WEBVIEW_ACT_TITLE);
            tvTitleCenter.setText(title);
        }}

    @Override
    public void initContentView() {
        setContentView(R.layout.activity_webview);

    }

    @Override
    public void onReNetRefreshData(int viewId) {

    }

    @OnClick({R.id.left_ll})
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.left_ll:
                goBack(v);
                break;
            default:
                break;
        }

    }

    protected void initData() {

        tvTitleCenter.setText(title);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                //设置加载进度条
                view.setWebChromeClient(new WebChromeClientProgress());
                return true;

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.getSettings().setBlockNetworkImage(false);

            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        webView.loadUrl(loadUrl);
        AndroidBug5497Workaround.assistActivity(this);//解决webview中的输入框获得焦点后输入法盖住了输入框
    }

    @Override
    public void onGetUrlSucc(GetUrlResponseBean getUrlResponseBean) {
        if (getUrlResponseBean != null) {
            loadUrl = getUrlResponseBean.getUrl();
            initData();
        }
    }

    @Override
    public void onGetUrlFail(String msg) {
        ToastUtils.showCustomToast(msg, 0);
    }

    private class WebChromeClientProgress extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int progress) {
            if (mLoadingProgress != null) {
                mLoadingProgress.setProgress(progress);
                if (progress == 100) mLoadingProgress.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, progress);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return true;
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams
                fileChooserParams) {
            return true;
        }

        //<3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        }

        //>3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        }

        //>4.1.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView != null && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        /**
         * 但是注意：webview调用destory时,webview仍绑定在Activity上
         * 这是由于自定义webview构建时传入了该Activity的context对象
         * 因此需要先从父容器中移除webview,然后再销毁webview:
         */
        rootLayout.removeView(webView);
        if (null != webView) {
            webView.onDestroy();
        }
        InputMethodFix.fixFocusedViewLeak(getApplication());
        InputMethodFix.fixInputMethodManagerLeak(getApplicationContext());
        super.onDestroy();
    }

//    public static void launch(final Context context, final String url) {
//        launch(context, url, "");
//    }
//
//    public static void launch(final Context context, final String url, String title) {
//        if (!NetworkUtils.isConnected()) {
//            ToastUtils.showCustomToastMsg("网络无效", 150);
//            return;
//        }
//        if (TextUtils.isEmpty(url)) {
//            Toast.makeText(context, "没有h5地址", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        goToWebViewActivity(context, url, title);
//    }
//
//    private static void goToWebViewActivity(Context context, String url, String title) {
//        final Intent intent = new Intent(context, WebViewActivity.class);
//        intent.putExtra(URL, url);
//        intent.putExtra("title", title);
//        context.startActivity(intent);
//    }

}
