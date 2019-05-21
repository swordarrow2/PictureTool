package com.meng.picTools.pixivPictureDownloader;

import android.app.*;
import android.os.*;
import android.webkit.*;

import com.meng.picTools.helpers.SharedPreferenceHelper;

public class login extends Activity{

	@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
		WebView webView = new WebView(this);
		setContentView(webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient() {
			  @Override
			  public boolean shouldOverrideUrlLoading(WebView view,String url){
				  view.loadUrl(url);
				  return true;
				}

			  @Override
			  public void onPageFinished(WebView view,String url){
				  super.onPageFinished(view,url);
				  CookieManager cookieManager = CookieManager.getInstance();
				  String CookieStr = cookieManager.getCookie(url)==null? "null" :cookieManager.getCookie(url);
				  SharedPreferenceHelper.putValue(Data.preferenceKeys.cookievalue,CookieStr);
				}
			});
		webView.loadUrl( "https://www.pixiv.net");
	  }
  }
