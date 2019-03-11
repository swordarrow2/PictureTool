package com.meng.pixivGifDownloader;

import android.app.*;
import android.content.*;
import android.os.*;
import android.webkit.*;
import com.meng.picTools.*;

public class login extends Activity {

    private WebView webView;
    private String loginUrl = "";
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Intent i = getIntent();
        loginUrl = i.getStringExtra(Data.intentKeys.url);

        webView = (WebView) findViewById(R.id.loginWebview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                isFirst = false;
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                CookieManager cookieManager = CookieManager.getInstance();
                String CookieStr = cookieManager.getCookie(url) == null ? "null" : cookieManager.getCookie(url);
                PixivDownloadMain.sp.putValue(Data.preferenceKeys.cookievalue, CookieStr);
                if (loginUrl.equalsIgnoreCase(url) && !isFirst) {
                    Intent i = new Intent();
                    i.putExtra(Data.intentKeys.result, Data.status.success);
                    setResult(1, i);
                    finish();
                    login.this.finish();
                }
            }
        });
        webView.loadUrl(loginUrl);
    }
}
