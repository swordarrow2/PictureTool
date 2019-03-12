package com.meng.picTools.pixivGifDownloader;

import android.app.*;
import android.os.*;
import android.webkit.*;

import com.meng.picTools.*;

public class login extends Activity {

    private WebView webView;
    private String loginUrl = "https://www.pixiv.net";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pixiv_login);
        webView = (WebView) findViewById(R.id.loginWebview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                CookieManager cookieManager = CookieManager.getInstance();
                String CookieStr = cookieManager.getCookie(url) == null ? "null" : cookieManager.getCookie(url);
                MainActivity.instence.sharedPreference.putValue(Data.preferenceKeys.cookievalue, CookieStr);
            }
        });
        webView.loadUrl(loginUrl);
    }
}
