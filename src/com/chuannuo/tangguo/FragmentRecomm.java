/* 
 * @Title:  FragmentRecomm.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2015-7-18 上午1:45:47 
 * @version:  V1.0 
 */
package com.chuannuo.tangguo;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.chuannuo.tangguo.listener.ResponseStateListener;
import com.chuannuo.tangguo.net.RequestParams;
import com.chuannuo.tangguo.net.TGHttpResponseHandler;

/**
 * TODO<请描述这个类是干什么的>
 * 
 * @author xie.xin
 * @data: 2015-7-18 上午1:45:47
 * @version: V1.0
 */
public class FragmentRecomm extends BaseFragment {

	private ListView myListView;
	private LinearLayout view;

	private ArrayList<AppInfo> recommList;
	private RecommendTaskAdapter adapter;
	// 获取手机内所有应用
	private List<PackageInfo> paklist;
	private double score = 1;
	private int isShow = 0;
	private String textName = "积分";
	private boolean isFirst = false;
	
	private String ip;
	private String isp;
	private String city;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		isFirst = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		initView();
		if(isFirst){
			initProgressDialog(Constant.StringValues.LOADING);
			if (null == recommList) {
				recommList = new ArrayList<AppInfo>();
			}
			paklist = getActivity().getPackageManager().getInstalledPackages(0);
			/*
			 * 上报手机已经安装的软件 各个应用包名用，隔开eg:
			 * cn.winads.studentsearn,cn.winads.ldbatterySteward
			 */
			reportInstalledApp();
			isFirst = false;
		}else{
			myListView.setAdapter(adapter);
		}
		return view;
	}
	
	@Override
	public void onResume() {
		if (recommList != null && recommList.size() > 0) {
			AppInfo app = new AppInfo();
			for (int i = recommList.size() - 1; i >= 0; i--) {
				app = recommList.get(i);
				if (app.getResource_id() == pref
						.getInt(Constant.RESOURCE_ID, 0) && app.getClicktype()!=1) {
					recommList.remove(i);
					editor.putInt(Constant.RESOURCE_ID, 0);
					editor.commit();
					break;
				}
			}
			if (null == adapter) {
				adapter = new RecommendTaskAdapter(getActivity(), recommList,
						myListView);
				myListView.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}
		}
		super.onResume();
	}
	
	/** 
	 * @Title: refreshData 
	 * @Description: 数据更新 
	 * @param  
	 * @return void
	 * @throws 
	 */
	public void refreshData(){
		if (recommList != null && recommList.size() > 0) {
			AppInfo app = new AppInfo();
			for (int i = recommList.size() - 1; i >= 0; i--) {
				app = recommList.get(i);
				if (app.getResource_id() == pref
						.getInt(Constant.RESOURCE_ID, 0)) {
					recommList.remove(i);
					break;
				}
			}
			if (null == adapter) {
				adapter = new RecommendTaskAdapter(getActivity(), recommList,
						myListView);
				myListView.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}

		}else{
			
		}
	}

	private void initView() {
		view = super.getRootLinearLayout();

		myListView = new ListView(getActivity());
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		myListView.setDivider(new ColorDrawable(Color
				.parseColor(Constant.ColorValues.DIVIDER_COLOR)));
		myListView.setDividerHeight(1);
		myListView.setId(Constant.IDValues.LV_RECOMM);
		myListView.setLayoutParams(lp);

		myListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mListener.onBtnClickListener(Constant.STEP_1,
						recommList.get(position));
			}
		});
		view.addView(myListView);
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				getIp();
				break;
			case 2:
				initData();
				break;
			case 3:
				if (null == adapter) {
					adapter = new RecommendTaskAdapter(getActivity(),
							recommList, myListView);
					myListView.setAdapter(adapter);
				} else {
					adapter.notifyDataSetChanged();
				}
				getIp();
				break;
			default:
				break;
			}
		}

	};

	/**
	 * @Title: reportInstalledApp
	 * @Description: 上报应用
	 * @param
	 * @return void
	 * @throws
	 */
	private void reportInstalledApp() {
		StringBuilder pakStr = new StringBuilder();
		for (int i = 0; i < paklist.size(); i++) {
			PackageInfo pak = (PackageInfo) paklist.get(i);
			// 判断是否为非系统预装的应用程序
			if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
				pakStr.append(pak.applicationInfo.packageName);
				pakStr.append(",");
			}
		}
		if (pakStr.length() > 0) {
			pakStr.deleteCharAt(pakStr.length() - 1);
		}
		progressDialog.show();
		HttpUtil.setParams("app_id", pref.getString(Constant.APP_ID, "0"));
		HttpUtil.setParams("package_names", pakStr.toString());
		HttpUtil.post(Constant.URL.REPORT_URL, new ResponseStateListener() {

			@Override
			public void onSuccess(Object result) {
				if (null != result && !result.equals(Constant.NET_ERROR)) {
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(result.toString());
						String code = jsonObject.getString("code");
						if (code.equals("1")) {
							Message msg = mHandler.obtainMessage();
							msg.what = 1;
							mHandler.sendMessage(msg);
						} else {
							if(getActivity() != null){
								Toast.makeText(getActivity(), "获取数据失败",
										Toast.LENGTH_SHORT).show();
							}
							
							if (progressDialog != null) {
								progressDialog.dismiss();
							}
						}
					} catch (Exception e) {
						if(getActivity() != null){
							Toast.makeText(getActivity(), "获取数据失败！",
									Toast.LENGTH_SHORT).show();
						}
						
						if (progressDialog != null) {
							progressDialog.dismiss();
						}
					}
				} else {
					if(getActivity() != null){
						Toast.makeText(getActivity(), "获取数据失败！", Toast.LENGTH_SHORT)
						.show();
					}
					
					if (progressDialog != null) {
						progressDialog.dismiss();
					}
				}
			}
		});
	}

	private void initData() {

		if (recommList!=null && recommList.size() <= 0) {
			HttpUtil.setParams("app_id", pref.getString(Constant.APP_ID, "0"));
			HttpUtil.setParams("channel_id", TangGuoWall.APPID);
			HttpUtil.setParams("ip", pref.getString(Constant.IP, "0.0.0.0"));
			HttpUtil.setParams("city", pref.getString(Constant.CITY, ""));
			HttpUtil.setParams("isp", pref.getString(Constant.ISP, "0.0.0.0"));
			HttpUtil.setParams("code", pref.getString(Constant.CODE, ""));
			PhoneInformation.initTelephonyManager(getActivity());
			HttpUtil.setParams("imsi", PhoneInformation.getImsi());
			HttpUtil.post(Constant.URL.DOWNLOAD_URL,
					new ResponseStateListener() {

						@Override
						public void onSuccess(Object result) {
							if (null != result
									&& !result.equals(Constant.NET_ERROR)) {
								JSONObject jsonObject;
								JSONObject infoObject;
								try {
									jsonObject = new JSONObject(result
											.toString());
									String code = jsonObject.getString("code");
									if (code.equals("1")) {
										infoObject = jsonObject
												.getJSONObject("info");
										if (null != infoObject) {
											score = infoObject
													.getDouble("txt_vc_price");
											isShow = infoObject
													.getInt("is_show_integral");
											textName = infoObject
													.getString("txt_name");
											editor.putString(Constant.TEXT_NAME, textName);
											editor.putString(Constant.VC_PRICE, score+"");
											editor.putInt(Constant.IS_SHOW, isShow);
											editor.commit();
										}
										JSONArray jArray = jsonObject
												.getJSONArray("data");

										if (null != jArray
												&& jArray.length() > 0) {
											for (int i = 0; i < jArray.length(); i++) {
												JSONObject obj = jArray
														.getJSONObject(i);
												if (null != obj) {
													AppInfo appInfo = new AppInfo();
													appInfo.setIsShow(isShow);
													appInfo.setTextName(textName);

													appInfo.setVcPrice(score);
													appInfo.setResource_id(obj
															.getInt("id"));
													appInfo.setAdId(obj
															.getInt("ad_id"));
													appInfo.setTitle(obj
															.getString("title"));
													appInfo.setName(obj
															.getString("name"));
													if(appInfo.getTitle().equals("")){
														appInfo.setTitle(appInfo.getName());
													}
													
													appInfo.setIsCustom(obj.getInt("is_custom"));
													appInfo.setCustomField1(obj.getString("custom1"));
													appInfo.setCustomField2(obj.getString("custom2"));
													
													appInfo.setDescription(obj
															.getString("description"));
													appInfo.setPackage_name(obj
															.getString("package_name"));
													appInfo.setBrief(obj
															.getString("brief"));
													appInfo.setScore((int) (obj
															.getInt("score") * score));
													appInfo.setResource_size(obj
															.getString("resource_size"));
													appInfo.setB_type(obj
															.getInt("btype"));
													appInfo.setSign_rules(obj
															.getInt("reportsigntime"));
													appInfo.setNeedSign_times(obj
															.getInt("sign_number"));
													String fileUrl = obj
															.getString("file");
													String iconUrl = obj
															.getString("icon");
													String h5Url = obj
															.getString("h5_big_url");
													appInfo.setAlert(obj
															.getString("alert") == null ? ""
															: obj.getString("alert"));

													if (!fileUrl
															.contains("http")) {
														fileUrl = Constant.URL.ROOT_URL
																+ fileUrl;
													}
													if (!iconUrl
															.contains("http")) {
														iconUrl = Constant.URL.ROOT_URL
																+ iconUrl;
													}
													if (!h5Url.contains("http")) {
														h5Url = Constant.URL.ROOT_URL
																+ h5Url;
													}

													appInfo.setFile(fileUrl);
//													String dUrl = Constant.PREF_TANGGUO_DATA+appInfo.getAdId();
//													editor.putString(dUrl, fileUrl);
//													editor.commit();
													appInfo.setH5_big_url(h5Url);
													
													JSONArray imgArray = obj.getJSONArray("picture_list");
													if(imgArray!=null && imgArray.length()>0){
														int len = imgArray.length();
														List<String> imgs = new ArrayList<String>();
														for(int k=0; k<len; k++){
															String url = imgArray.getString(k);
															if (!url.contains("http")) {
																url = Constant.URL.ROOT_URL
																		+ url;
															}
															imgs.add(url);
														}
														appInfo.setImgsList(imgs);
													}
													
													appInfo.setIcon(iconUrl);
													
													appInfo.setBigPushUrl(obj.getString("big_push_url").isEmpty()?"":Constant.URL.ROOT_URL+obj.getString("big_push_url"));
													appInfo.setClicktype(obj.getInt("clicktype"));
													appInfo.setIs_photo(obj.getInt("is_phopo"));
													appInfo.setPhoto_remarks(obj.getString("photo_remarks"));
													appInfo.setPhoto_integral((int)(obj.getInt("photo_integral")*score));
													appInfo.setTotalScore((int) ((obj
															.getInt("score")+obj.getInt("photo_integral") + obj
															.getInt("sign_number") * 10) * score));
													if(recommList != null){
														recommList.add(appInfo);
													}
													
												}
											}
										}

										Message msg = mHandler.obtainMessage();
										msg.what = 3;
										mHandler.sendMessage(msg);
									} else {
										if(getActivity() != null){
											Toast.makeText(getActivity(), "获取数据失败",
													Toast.LENGTH_SHORT).show();
										}
										if(progressDialog != null){
											progressDialog.dismiss();
										}
										
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} finally {
									if(progressDialog != null){
										progressDialog.dismiss();
									}
									if(adapter!=null){
										
										adapter.notifyDataSetChanged();
									}
								}

							} else {
								if(getActivity() != null){
									
									Toast.makeText(getActivity(), "获取数据失败！",
											Toast.LENGTH_SHORT).show();
								}
								if(progressDialog != null){
									progressDialog.dismiss();
								}
							}
						}

					});
		} else {
			if(progressDialog != null){
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}
			
			if (null == adapter) {
				adapter = new RecommendTaskAdapter(getActivity(), recommList,
						myListView);
			} else {
				adapter.notifyDataSetChanged();
			}
			myListView.setAdapter(adapter);
		}
	}
	
	private void getIp(){
		HttpUtils.post("http://ip.taobao.com/service/getIpInfo2.php?ip=myip", new RequestParams(), new TGHttpResponseHandler() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.chuannuo.tangguo.net.HCKHttpResponseHandler#onSuccess(int,
			 * org.apache.http.Header[], java.lang.String)
			 */
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					String content) {
				JSONObject obj = null;
				try {
					obj = new JSONObject(content);
					if (obj != null && obj.getInt("code") == 0) {
						JSONObject data = obj.getJSONObject("data");
						city = data.getString("region") + data.getString("city");
						ip = data.getString("ip");
						isp = data.getString("isp");
						
						editor.putString(Constant.IP, ip);
						editor.putString(Constant.ISP, isp);
						editor.putString(Constant.CITY, city);
						editor.commit();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					Message msg = mHandler.obtainMessage();
					msg.what = 2;
					mHandler.sendMessage(msg);
				}

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.chuannuo.tangguo.net.HCKHttpResponseHandler#onFailure(java
			 * .lang.Throwable, java.lang.String)
			 */
			@Override
			public void onFailure(Throwable error, String content) {
				Message msg = mHandler.obtainMessage();
				msg.what = 2;
				mHandler.sendMessage(msg);
			}
		});
	}
}
