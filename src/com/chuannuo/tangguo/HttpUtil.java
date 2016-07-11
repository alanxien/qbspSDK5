/**
 * 
 */
package com.chuannuo.tangguo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.chuannuo.tangguo.listener.ResponseStateListener;


import android.os.AsyncTask;

/**
 * @author xin.xie
 * @date 2015-5-12 下午8:25:30 
 * @Description: TODO
 */
public class HttpUtil {
	public static List<NameValuePair> paramsl = new ArrayList<NameValuePair>();
	/**   
	 * @Fields ResponseListener : 回调接口   
	 */
	private static ResponseStateListener responseListener;
	/**
	 * @author xin.xie
	 * @date 2015-5-12 下午8:51:57 
	 * @Description: 发送请求
	 * @param url
	 * @return void
	 * @throws
	 */ 
	public static void post(String url,ResponseStateListener listener){
		responseListener = listener;
		new HTask().execute(url);
	}
	
	/**
	 * @author xin.xie
	 * @date 2015-5-12 下午8:52:05 
	 * @Description: 设置参数
	 * @param key
	 * @param values
	 * @return void
	 * @throws
	 */ 
	public static void setParams(String key,String values){
		NameValuePair param = new BasicNameValuePair(key, values);
		paramsl.add(param);
	}
	
	public static Object doHttp(Object... params){
		try {
			HttpPost httpRequest = new HttpPost(params[0].toString());
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.addAll(paramsl);
			HttpEntity httpentity = new UrlEncodedFormEntity(list, Constant.CODING);
			httpRequest.setEntity(httpentity);
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			
			if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				String strResult = EntityUtils.toString(httpResponse.getEntity());
                return strResult;
			}else{
				/*
				 * 返回数据失败
				 */
				return Constant.NET_ERROR;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	static class HTask extends AsyncTask<Object, Object, Object> {
		
		@Override
		protected Object doInBackground(Object... params) {
			return doHttp(params);
		}
		
		@Override
		protected void onPostExecute(Object result) {
			responseListener.onSuccess(result);
			super.onPostExecute(result);
		}

	}
}

