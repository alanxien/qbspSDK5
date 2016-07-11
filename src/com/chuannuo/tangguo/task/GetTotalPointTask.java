/* 
 * @Title:  a.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2015-8-6 下午11:10:46 
 * @version:  V1.0 
 */
package com.chuannuo.tangguo.task;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

import com.chuannuo.tangguo.Constant;
import com.chuannuo.tangguo.HttpUtil;
import com.chuannuo.tangguo.listener.ResponseStateListener;

/** 
 * TODO<请描述这个类是干什么的> 
 * @author  xie.xin 
 * @data:  2015-8-6 下午11:10:46 
 * @version:  V1.0 
 */
public class GetTotalPointTask extends AsyncTask<Object, Object, Object> {
	
	private ResponseStateListener responseListener;
	
	@Override
	protected Object doInBackground(Object... params) {
		responseListener = (ResponseStateListener) params[1];
		try {
			HttpPost httpRequest = new HttpPost(params[0].toString());
			HttpEntity httpentity = new UrlEncodedFormEntity(HttpUtil.paramsl, Constant.CODING);
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
	
	@Override
	protected void onPostExecute(Object result) {
		responseListener.onSuccess(result);
		super.onPostExecute(result);
	}

}
