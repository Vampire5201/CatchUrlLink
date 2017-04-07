package com.sg.view.core;

import java.io.DataOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.Environment;
import android.util.Log;

public class ViewCore {

	// 打印并返回<a>标签的链接内容
	public static Elements jsoupHtml(String html) {
		Elements hrefEle = new Elements();
		try {
			Document doc = Jsoup.parse(html);
			// Elements e = doc.getAllElements();
			Elements eles = doc.getElementsByTag("a");
			for (Element element : eles) {
				// System.out.println(element.text());
				Log.i("test", element.text());
				Log.i("test", element.attr("href"));
				hrefEle.add(element);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hrefEle;

	}

	// 打印获取页面广告链接
	public static List<LinkBean> getLinkList(String html, String plateform) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String catchTime = sdf.format(new Date()).toString();

		ArrayList<LinkBean> list = new ArrayList<LinkBean>();
		Elements linkElements = new Elements();
		try {
			Document doc = Jsoup.parse(html);
			linkElements = doc.getElementsByTag("a");
			for (Element element : linkElements) {
				LinkBean bean = new LinkBean();
				String adid = "";
				String clickStr = element.attr("onclick");
				if (!"".equals(clickStr) && null != clickStr) {
					try {
						String temp = clickStr.substring(clickStr.indexOf("{") + 1, clickStr.lastIndexOf("}"));
						adid = temp.split(",")[0].split(":")[1].split("'")[1];
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				// if (element.attr("href").startsWith("http") &&
				// !element.text().contains("App专享")) {
				if (!"".equals(adid)) {
					bean.setLinkName(element.text());
					bean.setLink(element.attr("href"));
					bean.setAdid(adid);
					bean.setPlateform(plateform);
					bean.setCatchTime(catchTime);
					list.add(bean);
					Log.i("test", "clickStr: " + clickStr);
					Log.i("test", element.text());
					Log.i("test", element.attr("href"));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	// 根据传入URL列表获取302跳转链接
	public static List<LinkBean> getJumpUrlList(List<LinkBean> list) {

		Log.i("testWrite", "获取所有落地页");
		List<LinkBean> targetList = new ArrayList<LinkBean>();
		try {
			for (LinkBean linkBean : list) {
				// LinkBean bean302 = new LinkBean();
				String jumpUrl = "";

				jumpUrl = getJumpUrl(linkBean.getLink());
				linkBean.setJumpLink(jumpUrl);
				Log.i("test", "第一次跳转：urlName: " + linkBean.getLinkName() + "===");
				Log.i("test", "当前链接: " + linkBean.getLink());
				Log.i("testWrite", "落地页: " + jumpUrl);

				// 跳转链接超时也抓取
				// if (jumpUrl != null && !"".equals(jumpUrl)) {
				targetList.add(linkBean);
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return targetList;
	}

	// 根据传入URL列表获取302跳转链接
	public static List<LinkBean> getJump2UrlList(List<LinkBean> list) {

		// List<LinkBean> jump = new ArrayList<LinkBean>();
		for (LinkBean linkBean : list) {
			// LinkBean bean302 = new LinkBean();
			String jumpUrl = getJumpUrl(linkBean.getJumpLink());
			linkBean.setJump2Link(jumpUrl);
			Log.i("test", "302/301跳转名称：urlName: " + linkBean.getLinkName() + "===");
			Log.i("test", "当前链接: " + linkBean.getLink());
			Log.i("test", "跳转链接: " + jumpUrl);
		}
		return list;
	}

	// 根据传入URL列表获取302跳转链接
	public static List<LinkBean> getJump3UrlList(List<LinkBean> list) {

		// List<LinkBean> jump = new ArrayList<LinkBean>();
		for (LinkBean linkBean : list) {
			// LinkBean bean302 = new LinkBean();
			String jumpUrl = getJumpUrl(linkBean.getJump2Link());
			linkBean.setJump3Link(jumpUrl);
			Log.i("test", "第三次跳转：urlName: " + linkBean.getLinkName() + "===");
			Log.i("test", "url: " + linkBean.getLink());
			Log.i("test", "jumpUrl: " + linkBean.getJumpLink());
			Log.i("test", "jump2Url: " + linkBean.getJump2Link());
			Log.i("test", "jump3Url: " + jumpUrl);
		}
		return list;
	}

	/**
	 * 根据链接合并去重List列表
	 * 
	 * @param targetList
	 *            合并后的list
	 * @param list
	 *            用于合并的list
	 * @return
	 */
	public static List<LinkBean> getUnionList(List<LinkBean> targetList, List<LinkBean> list) {
		if (targetList == null) {
			targetList = new ArrayList<LinkBean>();
		}
		if (list.size() == 0) {
			return targetList;
		}
		for (LinkBean linkBean : list) {
			boolean isEquals = false;
			if (targetList.size() != 0) {
				for (LinkBean targetBean : targetList) {
					if (targetBean.getJumpLink().equals(linkBean.getJumpLink())) {
						isEquals = true;
						break;
					}
				}
			}
			if (!isEquals) {
				targetList.add(linkBean);
			}
		}
		return targetList;
	}

	public static List<LinkBean> getUnionList(List<LinkBean> list) {

		if (list.size() == 0) {
			return null;
		}
		List<LinkBean> targetList = new ArrayList<LinkBean>();
		for (LinkBean linkBean : list) {
			boolean isEquals = false;
			if (targetList.size() == 0) {
				// targetList.add(linkBean);
			} else {
				for (LinkBean targetBean : targetList) {
					if (targetBean.getJumpLink().equals(linkBean.getJumpLink())) {
						isEquals = true;
						break;
					}
				}
				if (!isEquals) {
					targetList.add(linkBean);
				}
			}
		}
		return targetList;
	}

	// 获取广告302链接
	public static String getJumpUrl(String url) {
		try {
			if ("".equals(url) || null == url) {
				return null;
			}

			@SuppressWarnings("unused")
			StringBuffer buffer = new StringBuffer();

			// String url = "http://localhost:8080/istock/login?u=name&p=pass";
			System.out.println("访问地址:" + url);

			// 发送get请求
			URL serverUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
			conn.setRequestMethod("GET");
			// 必须设置false，否则会自动redirect到重定向后的地址
			conn.setInstanceFollowRedirects(false);
			conn.addRequestProperty("Accept-Charset", "UTF-8;");
			conn.addRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");
			conn.addRequestProperty("Referer", "http://matols.com/");
			conn.setConnectTimeout(3000);
			conn.connect();

			// 判定是否会进行302重定向
			if (conn.getResponseCode() == 302) {
				// 如果会重定向，保存302重定向地址，以及Cookies,然后重新发送请求(模拟请求)
				String location = conn.getHeaderField("Location");
				String cookies = conn.getHeaderField("Set-Cookie");

				serverUrl = new URL(location);
				conn = (HttpURLConnection) serverUrl.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Cookie", cookies);
				conn.addRequestProperty("Accept-Charset", "UTF-8;");
				conn.addRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");
				conn.addRequestProperty("Referer", "http://matols.com/");
				conn.setConnectTimeout(3000);
				conn.connect();
				System.out.println("跳转地址:" + location);

				return location;
			} else if (conn.getResponseCode() == 301) {
				// 如果会重定向，保存302重定向地址，以及Cookies,然后重新发送请求(模拟请求)
				String location = conn.getHeaderField("Location");
				String cookies = conn.getHeaderField("Set-Cookie");

				serverUrl = new URL(location);
				conn = (HttpURLConnection) serverUrl.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Cookie", cookies);
				conn.addRequestProperty("Accept-Charset", "UTF-8;");
				conn.addRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");
				conn.addRequestProperty("Referer", "http://matols.com/");
				conn.setConnectTimeout(3000);
				conn.connect();
				System.out.println("跳转地址:" + location);
			} else {
				return null;
			}

			/*
			 * // 将返回的输入流转换成字符串 InputStream inputStream = conn.getInputStream();
			 * InputStreamReader inputStreamReader = new
			 * InputStreamReader(inputStream, "utf-8"); BufferedReader
			 * bufferedReader = new BufferedReader(inputStreamReader); String
			 * str = null; while ((str = bufferedReader.readLine()) != null) {
			 * buffer.append(str); } bufferedReader.close();
			 * inputStreamReader.close(); // 释放资源 inputStream.close();
			 * inputStream = null;
			 * 
			 * System.out.println(buffer.toString());
			 */
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public static boolean requestHttp(String url, List<LinkBean> list) {
		try {
			// String url = "http://localhost:8080/istock/login?u=name&p=pass";
			System.out.println("访问地址:" + url);

			// 发送get请求
			URL serverUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
			conn.setConnectTimeout(10000);
			// 设置是否向connection输出，因为这个是post请求，参数要放在
			// http正文内，因此需要设为true
			conn.setDoOutput(true);
			// Read from the connection. Default is true.
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setInstanceFollowRedirects(true);
			conn.addRequestProperty("Accept-Charset", "UTF-8;");
			// conn.setRequestProperty("Connection", "Keep-Alive");
			// 必须设置false，否则会自动redirect到重定向后的地址
			conn.setInstanceFollowRedirects(false);

			conn.connect();

			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			String content = "listJson=" + URLEncoder.encode(getJsonStr(list), "UTF-8");
			out.writeBytes(content);
			out.flush();
			out.close();

			if (conn.getResponseCode() == 200) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	private static String getJsonStr(List<LinkBean> list) throws JSONException {

		JSONArray linkArray = new JSONArray();
		for (LinkBean bean : list) {
			JSONObject linkObj = new JSONObject();
			linkObj.putOpt("tag", bean.getTag());
			linkObj.putOpt("adid", bean.getAdid());
			linkObj.putOpt("linkName", bean.getLinkName());
			// linkObj.putOpt("link", bean.getLink()); // 链接太长，设置不传送
			linkObj.putOpt("jumpLink", bean.getJumpLink());
			linkObj.putOpt("jump2Link", bean.getJump2Link());
			linkObj.putOpt("plateform", bean.getPlateform());
			linkObj.putOpt("catchTime", bean.getCatchTime());
			linkArray.put(linkObj);
		}
		JSONObject obj = new JSONObject();
		obj.put("linkArray", linkArray);

		return obj.toString();
	}

	public static void writeFile(String fileName, String writeStr) {
		try {
			// 判断实际是否有SD卡，且应用程序是否有读写SD卡的能力，有则返回true
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				// 获取SD卡的目录
				File sdCardDir = Environment.getExternalStorageDirectory();
				String path = "/" + fileName + "/";
				File dir = new File(Environment.getExternalStorageDirectory() + path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				String filePath = sdCardDir.getCanonicalPath() + path + fileName + ".txt";
				Log.i("test", filePath);

				File deleteFile = new File(filePath);
				if (deleteFile.exists()) {
					deleteFile.delete();
				}

				File targetFile = new File(filePath);
				// 使用RandomAccessFile是在原有的文件基础之上追加内容，
				// 而使用outputstream则是要先清空内容再写入
				RandomAccessFile raf = new RandomAccessFile(targetFile, "rw");
				// 光标移到原始文件最后，再执行写入
				raf.seek(targetFile.length());
				raf.write(writeStr.getBytes());
				raf.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeListFile(String fileName, List<LinkBean> list) {
		try {
			// 判断实际是否有SD卡，且应用程序是否有读写SD卡的能力，有则返回true
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				// 获取SD卡的目录
				File sdCardDir = Environment.getExternalStorageDirectory();
				String path = "/" + "jump" + "/";
				File dir = new File(Environment.getExternalStorageDirectory() + path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				String filePath = sdCardDir.getCanonicalPath() + path + fileName + ".txt";
				Log.i("test", filePath);

				File deleteFile = new File(filePath);
				if (deleteFile.exists()) {
					deleteFile.delete();
				}

				File targetFile = new File(filePath);

				// 使用RandomAccessFile是在原有的文件基础之上追加内容，
				// 而使用outputstream则是要先清空内容再写入
				RandomAccessFile raf = new RandomAccessFile(targetFile, "rw");
				Log.i("testWrite", "过滤合并后的落地页");

				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
				System.out.println(df.format(new Date()));// new Date()为获取当前系统时间

				raf.seek(targetFile.length());
				raf.write(("时间： " + df.format(new Date())).getBytes());
				raf.writeBytes("\r\n");

				int i = 1;
				for (LinkBean linkBean : list) {
					// 光标移到原始文件最后，再执行写入
					raf.seek(targetFile.length());
					raf.write((i + ".页面名称： " + linkBean.getLinkName()).getBytes());
					Log.i("testWrite", "页面名称： " + linkBean.getLinkName());
					raf.writeBytes("\r\n");

					raf.seek(targetFile.length());
					raf.write(("页面链接： " + linkBean.getLink()).getBytes());
					Log.i("testWrite", "页面链接： " + linkBean.getLink());
					raf.writeBytes("\r\n");

					raf.seek(targetFile.length());
					raf.write(("链接adid： " + linkBean.getAdid()).getBytes());
					Log.i("testWrite", "链接adid： " + linkBean.getAdid());
					raf.writeBytes("\r\n");

					raf.seek(targetFile.length());
					raf.write(("落地页链接： " + linkBean.getJumpLink()).getBytes());
					Log.i("testWrite", "落地页链接： " + linkBean.getJumpLink());
					raf.writeBytes("\r\n");

					raf.seek(targetFile.length());
					raf.write(("落地页跳转链接： " + linkBean.getJump2Link()).getBytes());
					Log.i("testWrite", "落地页跳转链接： " + linkBean.getJump2Link());
					// raf.write(("--------------------------").getBytes());
					Log.i("testWrite", "---------------");
					raf.writeBytes("\r\n");
					raf.writeBytes("\r\n");
					raf.writeBytes("\r\n");
					i++;
				}
				raf.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}