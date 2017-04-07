package com.sg.view.main;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.demo_html_rul.R;
import com.sg.view.core.LinkBean;
import com.sg.view.core.ViewCore;

public class MainActivity extends Activity implements OnClickListener {

	private WebView mWebView;
	private Button bnt;
	private Button ua1;
	private Button ua2;
	private EditText urlTxt;
	public String htmlStr;
	private ExecutorService mExecutorService;

	public static Handler handler;
	public static Context context;

	private static final int RESULT_SUCCESS = 1;
	private static final int RESULT_FAIL = 2;
	private static final int RESULT_SEND = 3;
	public static final int RESULT_LOADED = 4;
	public static final int INIT_ANDROID = 5;
	public static final int INIT_IOS = 6; 

	public static long startTime = 0;
	public static long endTime = 0;
	public String plateform = "";
	public static int INT_SINGLE = 1; // 单数
	public static int TIMER_NUM_START = 1;	//起始执行任务数
	
	private static String DRIVER_URL = "http://hostname/CatchUrlServlet/CatchUrlServlet";
	// ios的UA
	private String uaStr = "Mozilla/5.0 (iPad; U; CPU OS 3_2_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B500 Safari/531.21.10";
			

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		mWebView = (WebView) findViewById(R.id.webView);
		bnt = (Button) findViewById(R.id.bnt);
		ua1 = (Button) findViewById(R.id.ua1);
		ua2 = (Button) findViewById(R.id.ua2);
		urlTxt = (EditText) findViewById(R.id.urlTxt);
		urlTxt.setText("http://m.video.baomihua.com/m/v/36492497");
		bnt.setOnClickListener(this);
		ua1.setOnClickListener(this);
		ua2.setOnClickListener(this);
		

		mExecutorService = Executors.newSingleThreadExecutor(); // 单任务线程池
		// jsoupHtml();
		init(null,"Android");
		
		// 定时器任务处理
		timerTask(1000, 1000*60*5);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.d("test", "handleMessage,what:" + msg.what);
				switch (msg.what) {
				case RESULT_LOADED:
					Log.d("test", "网页加载完成,准备读取html...,what:" + RESULT_LOADED);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					mExecutorService.execute(new Runnable() {
						@Override
						public void run() {
							try {
								List<LinkBean> list1 = ViewCore.getLinkList(htmlStr, plateform);
								List<LinkBean> list2 = ViewCore.getJumpUrlList(list1); // 落地页抓取
								List<LinkBean> list3 = ViewCore.getJump2UrlList(list2); // 落地页抓取

								ViewCore.requestHttp(DRIVER_URL, list3);
//								ViewCore.writeListFile("jump", list3);
								handler.sendEmptyMessage(RESULT_SUCCESS);
							} catch (Exception e) {
								handler.sendEmptyMessage(RESULT_FAIL);
								e.printStackTrace();
							}
						}
					});

					break;
				case RESULT_SEND:
					Toast.makeText(context, "链接抓取开始", Toast.LENGTH_LONG).show();
					Log.d("test", "链接抓取开始,what:" + RESULT_SEND);
					mWebView.loadUrl(urlTxt.getText().toString());
					break;
				case RESULT_SUCCESS:
					Toast.makeText(context, "抓取链接完成", Toast.LENGTH_LONG).show();
					Log.d("test", "抓取链接完成,what:" + RESULT_SUCCESS);
					bnt.setEnabled(true);
					break;
				case RESULT_FAIL:
					Toast.makeText(context, "抓取链接失败", Toast.LENGTH_LONG).show();
					Log.d("test", "抓取链接失败,what:" + RESULT_FAIL);
					bnt.setEnabled(true);
					break;
				case INIT_ANDROID:
					init(null, "Android");
					break;
				case INIT_IOS:
					init(uaStr, "IOS");
					break;
				default:
					break;
				}
			}
		};
	}

	private void init(String uaStr, String plateform) {
		this.plateform = plateform;
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setJavaScriptEnabled(true);
		// webSettings.setUserAgentString("Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_2_1 like Mac OS X; fi-fi) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8C148a Safari/6533.18.5");
		// ipad
		// String uaStr =
		// "Mozilla/5.0 (iPad; U; CPU OS 3_2_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B500 Safari/531.21.10";
		webSettings.setUserAgentString(uaStr);
		webSettings.setUseWideViewPort(true);// 适应分辨率
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

		mWebView.setWebViewClient(new MyWebViewClient());

		mWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
		// mWebView.loadUrl("http://m.video.baomihua.com/m/v/36492497");
		Log.d("test", "init Finish");
	}

	final class InJavaScriptLocalObj {
		// 初始此处注解缺失导致出现加载js失败
		@JavascriptInterface
		public void showSource(String html) {
			Log.d("test", "showSource html");
			htmlStr = html;
//			ViewCore.writeFile("html", html);
			MainActivity.handler.sendEmptyMessage(MainActivity.RESULT_LOADED);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bnt:
			bnt.setEnabled(false);

			startTime = System.currentTimeMillis();
			handler.sendEmptyMessage(RESULT_SEND);
			Log.i("test", "网页加载解析=====================================================");
			// mWebView.loadUrl("http://m.video.baomihua.com/m/v/36492497");
			
			
			break;
		case R.id.ua1:
			ua1.setEnabled(false);
			ua2.setEnabled(true);
			init(null,"Android");
			break;
		case R.id.ua2:
			ua2.setEnabled(false);
			ua1.setEnabled(true);
			 init(uaStr, "IOS");
			break;

		default:
			break;
		}

	}
	
	
	public void timerTask(long delayTime, long betweenTime) {
		Timer timer = new Timer();
		try {
			// 5分钟执行一次
			timer.schedule(new MeTimerTask(), delayTime, betweenTime);
		} catch (Exception e) {
			e.printStackTrace();
			timer.cancel();
			
		}
	}
	
	private class MeTimerTask extends TimerTask{
		@Override
		public void run() {
			TIMER_NUM_START++;
			if(TIMER_NUM_START % 2 == INT_SINGLE){
				handler.sendEmptyMessage(INIT_ANDROID);
//				init(null, "Android");
			} else{
				handler.sendEmptyMessage(INIT_IOS);
//				init(uaStr, "IOS");
			}
			startTime = System.currentTimeMillis();
			handler.sendEmptyMessage(RESULT_SEND);
			Log.i("test", "网页加载解析=====================================================");
			// mWebView.loadUrl("http://m.video.baomihua.com/m/v/36492497");
			// 加载网页开始
			handler.sendEmptyMessage(RESULT_SEND);
//			mWebView.loadUrl(urlTxt.getText().toString());
		}
	}

}
