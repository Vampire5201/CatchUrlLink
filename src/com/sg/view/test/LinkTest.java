package com.sg.view.test;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.content.ContentResolver;
import android.test.AndroidTestCase;
import android.text.format.DateFormat;
import android.util.Log;

import com.sg.view.core.LinkBean;
import com.sg.view.core.ViewCore;
import com.sg.view.main.MainActivity;

public class LinkTest extends AndroidTestCase {

	public void test1() {
		String url = "http://go.baomihua.com/eemiddle/middle?params=4|54157301|1|%25E4%25B8%25AD%25E5%259B%25BD|%25E5%25B9%25BF%25E4%25B8%259C|%25E6%25B7%25B1%25E5%259C%25B3||1489057081027|0|0||2|1|E45704C477B2218A30F9C937251972002CABC134F482B76F4B98E202D8C98357312F13D95B089700D80F83C2E065637F9671BAFA633BE927CD8DF2C2763AA97B5BCF8EF2DAC845DE51479A86B8783C0400DADD9E4F1AA4BD1D7333EA81D2D2DC|WAP|android|36492497|1|793|755|45|1|8|0|0|0|183.14.30.95||null|null|d10d8cfc9b7b8d13a58a619248a1b279";
		String url302 = ViewCore.getJumpUrl(url);
		Log.i("test", "302Link:" + url302);
		String url302302 = ViewCore.getJumpUrl(url);
		Log.i("test", "302302Link:" + url302302);
	}

	public void test2() {

		Log.i("test", "302Link:" + 1);
		System.out.println(1);
	}

	public void test3() {

		String url = "http://zl.at98.com/rd/cr.ashx?u=324877&d=110368&sb=0&oid=1&b=16&os=1";
		String url302 = ViewCore.getJumpUrl(url);
		Log.i("test", "302Link: " + url302);
	}

	public void test4() {

		String url = "http://m.video.baomihua.com/m/v/36492497";
		ViewCore.getLinkList(url, "IOS");
		// Log.i("test", "302Link: " + url302);
	}

	public void test6() {

	}

	public void test5() {
		for (int i = 0; i < 10; i++) {
			Log.i("test", "i:" + i + "");

			for (int j = 0; j < 10; j++) {
				if (i == j) {
					Log.w("test", "j: " + j + "");
					// break;
				}
				Log.i("test", "j: " + j + "");
			}
		}

	}

	public void test7() throws JSONException {
		String clickStr = "MobileBMHAdv.prototype.beeAdClick(event,{'adid':'812','loaction':'1','adtypeid':'2','subadtypeid':'4','imgurl':'http://facdn01.resource.baomihua.com/waptg/pt/20173/20170310160449_8926','materialid':'862','title':'寂寞小姨子在喝酒了以后，居然。。。','desc':'广告','aduserid':'43','linkurl2':'7A46DFCDAB8B919693FC7BE88B741ED8FCDDAF7BF6FE721E2EBBCF8F0C7C49D2E9757DA0CA2D9B4F0A666CDDA1817907627D7610A749FB9727E10A656BDFEE82|151b985fca2fedb7aaff24c2e8baa266','linkurl':'http://go.baomihua.com/eemiddle/middle?params=4|54157301|1|%25E4%25B8%25AD%25E5%259B%25BD|%25E5%25B9%25BF%25E4%25B8%259C|%25E6%25B7%25B1%25E5%259C%25B3||1489373957854|0|0||2|1|7A46DFCDAB8B919693FC7BE88B741ED8FCDDAF7BF6FE721E2EBBCF8F0C7C49D2E9757DA0CA2D9B4F0A666CDDA1817907627D7610A749FB9727E10A656BDFEE82|WAP|android|36492497|1|862|812|43|1|8|0|0|0|183.14.134.185||PajBQ5tM-1488510069781-34134|http%253A%252F%252Fm.video.baomihua.com%252Fm%252Fv%252F36492497|151b985fca2fedb7aaff24c2e8baa266'})";
		String temp = clickStr.substring(clickStr.indexOf("{") + 1, clickStr.lastIndexOf("}"));

		String adid = temp.split(",")[0].split(":")[1].split("'")[1];

		System.out.println(adid);
	}

	public void test8() throws InterruptedException {

		long time1 = System.currentTimeMillis();
		Thread.sleep(1000);
		long time2 = System.currentTimeMillis();
		Log.i("test", "" + (time2 - time1));

	}

	// 测试json传servlet
	public void test9() {
		List<LinkBean> list = new ArrayList<LinkBean>();
		LinkBean bean = new LinkBean();
		bean.setAdid("123");
		bean.setJumpLink("wwww.baidu.com");

		LinkBean bean1 = new LinkBean();
		bean1.setAdid("1234");
		bean1.setJumpLink("wwww.baidu.com");
		list.add(bean);
		list.add(bean1);

		ViewCore.requestHttp("http://hostname/A_TestServlet/CatchUrlServlet", list);
	}

	public void test10() {

		ContentResolver cv = MainActivity.context.getContentResolver();
		String strTimeFormat = android.provider.Settings.System.getString(cv,
				android.provider.Settings.System.TIME_12_24);

		if (strTimeFormat.equals("24"))

		{
			Log.i("activity", "24");
		}

	}

	public void test() {
	}

}
