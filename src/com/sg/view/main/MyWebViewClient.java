package com.sg.view.main;

import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient {

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		Log.d("test", "onPageStarted");
		super.onPageStarted(view, url, favicon);
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		Log.d("test", "shouldOverrideUrlLoading");
		view.loadUrl(url);
		return true;
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
		// 此处通常会在js加载完之前执行
		Log.d("test", "onPageFinished");
		/*String js = "window.local_obj.showSource('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');";
		Log.d("test", "2:sleep(10')");
		if (VERSION.SDK_INT >= 19) {
			view.evaluateJavascript(js, null);
		} else {
			view.loadUrl("javascript:" + js);
		}*/
	}

	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		super.onReceivedError(view, errorCode, description, failingUrl);
		Log.d("test", "onReceivedError");
	}
	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
		Log.d("test", "shouldInterceptRequest");
		return super.shouldInterceptRequest(view, request);
	}
	
	@Override
	public void onLoadResource(WebView view, String url) {
//		Log.d("test", "在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次");
		MainActivity.endTime = System.currentTimeMillis();
		if(MainActivity.endTime - MainActivity.startTime > 20000){
			Log.d("test", "大于30秒加载资源: " + view.getTextAlignment() + ", url: " + url);
			String js = "window.local_obj.showSource('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');";
			if (VERSION.SDK_INT >= 19) {
				view.evaluateJavascript(js, null);
			} else {
				view.loadUrl("javascript:" + js);
			}
		}
		super.onLoadResource(view, url);
	}
	
	@Override
	public void onScaleChanged(WebView view, float oldScale, float newScale) {
		Log.d("test", "onScaleChanged:" + "WebView发生改变时调用");
		super.onScaleChanged(view, oldScale, newScale);
	}
	
	@Override
	public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
		Log.d("test", "shouldOverrideKeyEvent:" + "重写此方法才能够处理在浏览器中的按键事件");
		return super.shouldOverrideKeyEvent(view, event);
	}
	
}
