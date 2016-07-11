/* 
 * @Title:  NetWorkUtils.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2016-2-27 下午2:07:25 
 * @version:  V1.0 
 */
package com.chuannuo.tangguo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * TODO<请描述这个类是干什么的>
 * 
 * @author xie.xin
 * @data: 2016-2-27 下午2:07:25
 * @version: V1.0
 */
public class NetWorkUtils {
	public static String GetNetIp()
	{
	String IP = "";
	try
	{
	String address = "http://ip.taobao.com/service/getIpInfo2.php?ip=myip";
	URL url = new URL(address);
	HttpURLConnection connection = (HttpURLConnection) url
	.openConnection();
	connection.setUseCaches(false);
	 
	if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
	{
	InputStream in = connection.getInputStream();
	 
	// 将流转化为字符串
	BufferedReader reader = new BufferedReader(
	new InputStreamReader(in));
	 
	String tmpString = "";
	StringBuilder retJSON = new StringBuilder();
	while ((tmpString = reader.readLine()) != null)
	{
	retJSON.append(tmpString + ""
	);
	}
	 
	JSONObject jsonObject = new JSONObject(retJSON.toString());
	String code = jsonObject.getString("code");
	if (code.equals(0))
	{
	JSONObject data = jsonObject.getJSONObject("data");
	IP = data.getString("ip") + (""+ data.getString("country")
	+ data.getString("area") + "区"
	+ data.getString("region") + data.getString("city")
	+ data.getString("isp") + "");
	 
	}
	}
	
	} catch (Exception e){
		
	}
	return IP;
}
}
