package com.meng.picTools.pixivPictureDownloader;

import android.app.*;
import android.os.*;
import android.webkit.*;

import com.meng.picTools.helpers.SharedPreferenceHelper;

public class LoginPixivActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webView = new WebView(this);
        setContentView(webView);
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
                if (url.equals("https://www.pixiv.net/")) {
                    CookieManager cookieManager = CookieManager.getInstance();
                    String CookieStr = cookieManager.getCookie(url) == null ? "null" : cookieManager.getCookie(url);
                    SharedPreferenceHelper.putValue(Data.preferenceKeys.cookieValue, CookieStr);
                    finish();
                }
            }
        });
        webView.loadUrl("https://accounts.pixiv.net/login?lang=zh&source=pc&view_type=page&ref=wwwtop_accounts_index");
    }
}
