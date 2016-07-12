/* 
 * @Title:  FragmentRecomm.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2015-7-18 上午1:45:47 
 * @version:  V1.0 
 */
package com.chuannuo.tangguo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chuannuo.tangguo.DepthTaskAdapter.SignClickListener;
import com.chuannuo.tangguo.listener.ResponseStateListener;
import com.chuannuo.tangguo.task.SpendPointTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * TODO<请描述这个类是干什么的>
 * 
 * @author xie.xin
 * @data: 2015-7-18 上午1:45:47
 * @version: V1.0
 */
public class FragmentDepth extends BaseFragment{

	private LinearLayout view;
	private ListView myListView;
	private ArrayList<AppInfo> depthList;
	private DepthTaskAdapter adapter;
	private TextView tvTips;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		initView();
		if (PhoneInformation.isSimReady()) {
			initData();
		}
		return view;
	}

	@Override
	public void onResume() {
		
		if (depthList != null && depthList.size() > 0) {
			AppInfo app = new AppInfo();
			for (int i = depthList.size() - 1; i >= 0; i--) {
				app = depthList.get(i);
				if (app.getResource_id() == pref
						.getInt(Constant.S_RESOURCE_ID, 0)) {
					depthList.remove(i);
					break;
				}
			}
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}

		}
		super.onResume();
	}
	
	/** 
	 * @Title: refresh 
	 * @Description: 刷新数据 
	 * @param  
	 * @return void
	 * @throws 
	 */
	public void refreshData(){
		if (depthList != null && depthList.size() > 0) {
			AppInfo app = new AppInfo();
			for (int i = depthList.size() - 1; i >= 0; i--) {
				app = depthList.get(i);
				if (app.getResource_id() == pref
						.getInt(Constant.S_RESOURCE_ID, 0)) {
					depthList.remove(i);
					break;
				}
			}
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}

		}
	}

	/**
	 * @Title: initView
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
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
		myListView.setCacheColorHint(0);

		tvTips = new TextView(getActivity());
		tvTips.setLayoutParams(lp);
		tvTips.setText(Constant.StringValues.DEPTH_TIPS);
		tvTips.setTextSize(17);
		tvTips.setTextColor(Color.parseColor(Constant.ColorValues.SIZE_COLOR));
		tvTips.setPadding(10, 30, 10, 0);
		tvTips.setVisibility(View.GONE);

		view.addView(tvTips);
		view.addView(myListView);
	}

	/**
	 * @Title: initData
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	public void initData() {
		if (null == depthList) {
			depthList = new ArrayList<AppInfo>();
		}
//		if(depthList.size() <= 0){
			initProgressDialog(Constant.StringValues.LOADING);
			progressDialog.show();

			HttpUtil.setParams("app_id", pref.getString(Constant.APP_ID, "0"));
			HttpUtil.post(Constant.URL.UN_FINISHED_TASK,
					new ResponseStateListener() {

						@Override
						public void onSuccess(Object result) {
							if (null != result
									&& !result.equals(Constant.NET_ERROR)) {
								JSONObject jsonObject;
								try {
									jsonObject = new JSONObject(result.toString());
									String code = jsonObject.getString("code");
									if (code.equals("1")) {
										JSONArray jArray = jsonObject
												.getJSONArray("data");
										if (null != jArray && jArray.length() > 0) {
											for (int i = 0; i < jArray.length(); i++) {
												JSONObject obj = jArray
														.getJSONObject(i);
												String s = obj
														.getString("resourceArr");
												if (!s.equals("[]")) {
													JSONObject childObj = obj
															.getJSONObject("resourceArr");
													// if(checkPackage(obj.getString("package_name"))){
													// //判断用户是否已经安装该软件
													AppInfo appInfo = new AppInfo();
													appInfo.setInstall_id(obj
															.getInt("ad_install_id"));
													if (null != childObj) {
														appInfo.setTitle(childObj
																.getString("title"));
														if(appInfo.getTitle().equals("")){
															appInfo.setTitle(childObj
																	.getString("name"));
														}
														appInfo.setIsShow(pref.getInt(Constant.IS_SHOW, 1));
														appInfo.setTextName(pref.getString(Constant.TEXT_NAME, "积分"));

														appInfo.setVcPrice(Double.parseDouble(pref.getString(Constant.VC_PRICE, "1")));
														
														appInfo.setResource_id(childObj
																.getInt("id"));
														appInfo.setAdId(childObj
																.getInt("ad_id"));
														appInfo.setResource_size(childObj
																.getString("resource_size"));
														appInfo.setB_type(childObj
																.getInt("btype"));
														appInfo.setScore((int)(childObj
																.getInt("score")*appInfo.getVcPrice()));
														appInfo.setClicktype(childObj.getInt("clicktype"));
														String h5Url = childObj
																.getString("h5_big_url");
														
														appInfo.setInstall_id(obj
																.getInt("ad_install_id"));
														appInfo.setIs_photo(obj.getInt("is_photo"));
														appInfo.setPhoto_integral((int)(obj.getInt("photo_integral")*appInfo.getVcPrice()));
														appInfo.setPhoto_status(obj.getInt("photo_status"));
														appInfo.setIs_photo_task(obj.getInt("is_photo_task"));
														appInfo.setUpload_photo(obj.getInt("photo_upload_number"));
														appInfo.setCurr_upload_photo(obj.getInt("upload_photo_number"));
														appInfo.setPhoto_remarks(childObj.getString("photo_remarks"));
														appInfo.setCheck_remarks(obj.getString("photo_remarks"));
														
														appInfo.setBigPushUrl(childObj.getString("big_push_url").isEmpty()?"":Constant.URL.ROOT_URL+childObj.getString("big_push_url"));
														
														String photo = obj.getString("photo");
														if(!photo.isEmpty()&&!photo.contains("http")){
															photo = Constant.URL.ROOT_URL
																	+ photo;
														}
														
														String fileUrl = childObj
																.getString("file");
														String iconUrl = childObj
																.getString("icon");
														if (!fileUrl.isEmpty()&&!fileUrl
																.contains("http")) {
															fileUrl = Constant.URL.ROOT_URL
																	+ fileUrl;
														}
														if (!iconUrl.isEmpty()&&!iconUrl
																.contains("http")) {
															iconUrl = Constant.URL.ROOT_URL
																	+ iconUrl;
														}
														if (!h5Url.isEmpty()&&!h5Url.contains("http")) {
															h5Url = Constant.URL.ROOT_URL
																	+ h5Url;
														}

														appInfo.setFile(fileUrl);
														appInfo.setIcon(iconUrl);
														appInfo.setH5_big_url(h5Url);
														appInfo.setPhoto(photo);
													}
													appInfo.setSign_times(obj
															.getInt("sign_count"));
													appInfo.setNeedSign_times(obj
															.getInt("sign_number"));
													appInfo.setSign_rules(obj
															.getInt("reportsigntime"));
													appInfo.setPackage_name(obj
															.getString("package_name"));
													appInfo.setIsAddIntegral(obj
															.getInt("is_add_integral"));
													appInfo.setAppeal(obj.getInt("appeal"));
													appInfo.setSign(true);
													
													JSONArray plJson = childObj.getJSONArray("picture_list");
													if(plJson!=null && plJson.length()>0){
														List<String> l = new ArrayList<String>();
														for(int j=0;j<plJson.length();j++){
															String url = plJson.getString(j);
															if (!url.contains("http")) {
																url = Constant.URL.ROOT_URL
																		+ url;
															}
															l.add(url);
														}
														appInfo.setImgsList(l);
													}
													
													if(appInfo.getCurr_upload_photo()>0){
														JSONArray ulJson = obj.getJSONArray("upload_picture_list");
														if(plJson!=null && ulJson.length()>0){
															List<String> l = new ArrayList<String>();
															for(int j=0;j<ulJson.length();j++){
																String url = ulJson.getString(j);
																if (!url.contains("http")) {
																	url = Constant.URL.ROOT_URL
																			+ url;
																}
																l.add(url);
															}
															appInfo.setUpImgList(l);
														}
													}
													
													if(isSignTime(obj) || appInfo.getClicktype()==1){
														appInfo.setSignTime(true);
													}
													
													//if (appInfo.getIs_photo_task() != 1) {
													if(appInfo.getPhoto_status() != 3){
														depthList.add(appInfo);
													}else{
														String date = (null == obj.getString("update_date") || obj
																.getString("update_date").equals("null")) ? "" : obj
																.getString("update_date");
														if(canAppeal(date)){
															depthList.add(appInfo);
														}
													}
//													}else{
//														if(appInfo.getIs_photo() == 0 || appInfo.getPhoto_status() == 2){
//															depthList.add(appInfo);
//														}
//													}

												}
											}
										}

										Message msg = mHandler.obtainMessage();
										msg.what = 1;
										mHandler.sendMessage(msg);
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} finally {
									progressDialog.dismiss();
								}

							} else {
								Toast.makeText(getActivity(), "获取数据失败",
										Toast.LENGTH_SHORT).show();
								progressDialog.dismiss();
							}
						}

						@SuppressLint("SimpleDateFormat")
						private boolean isSignTime(JSONObject obj) {
							SimpleDateFormat df = new SimpleDateFormat(
									"yyyy-MM-dd hh:mm:ss");
							String date;
							long time;
							try {
								date = (null == obj.getString("update_date") || obj
										.getString("update_date").equals("null")) ? obj
										.getString("create_date") : obj
										.getString("update_date");

								time = df.parse(date).getTime()
										+ obj.getLong("reportsigntime") * 24 * 60
										* 60 * 1000;
								if (time < System.currentTimeMillis()) {
									return true;
								}
							} catch (ParseException | JSONException e) {
								// TODO Auto-generated catch
								// block
								e.printStackTrace();
							}

							return false;
						}
						
						@SuppressLint("SimpleDateFormat")
						private boolean canAppeal(String date) {
							SimpleDateFormat df = new SimpleDateFormat(
									"yyyy-MM-dd hh:mm:ss");
							long time;
							try {

								time = df.parse(date).getTime();
								if (time + 24 * 60
										* 60 * 1000 > System.currentTimeMillis()) {
									return true;
								}
							} catch (ParseException e) {
								// TODO Auto-generated catch
								// block
								e.printStackTrace();
							}

							return false;
						}

					});
		//}
		
	}
	
	public void refresh(){
		if(depthList==null){
			depthList = new ArrayList<>();
		}else{
			depthList.clear();
		}
		
		initProgressDialog(Constant.StringValues.LOADING);
		progressDialog.show();
		HttpUtil.setParams("app_id", pref.getString(Constant.APP_ID, "0"));
		HttpUtil.post(Constant.URL.UN_FINISHED_TASK,
				new ResponseStateListener() {

					@Override
					public void onSuccess(Object result) {
						if (null != result
								&& !result.equals(Constant.NET_ERROR)) {
							JSONObject jsonObject;
							try {
								jsonObject = new JSONObject(result.toString());
								String code = jsonObject.getString("code");
								if (code.equals("1")) {
									JSONArray jArray = jsonObject
											.getJSONArray("data");
									if (null != jArray && jArray.length() > 0) {
										for (int i = 0; i < jArray.length(); i++) {
											JSONObject obj = jArray
													.getJSONObject(i);
											String s = obj
													.getString("resourceArr");
											if (!s.equals("[]")) {
												JSONObject childObj = obj
														.getJSONObject("resourceArr");
												// if(checkPackage(obj.getString("package_name"))){
												// //判断用户是否已经安装该软件
												AppInfo appInfo = new AppInfo();
												appInfo.setInstall_id(obj
														.getInt("ad_install_id"));
												if (null != childObj) {
													appInfo.setTitle(childObj
															.getString("title"));
													if(appInfo.getTitle().equals("")){
														appInfo.setTitle(childObj
																.getString("name"));
													}
													appInfo.setIsShow(pref.getInt(Constant.IS_SHOW, 1));
													appInfo.setTextName(pref.getString(Constant.TEXT_NAME, "积分"));

													appInfo.setVcPrice(Double.parseDouble(pref.getString(Constant.VC_PRICE, "1")));
													
													appInfo.setResource_id(childObj
															.getInt("id"));
													appInfo.setAdId(childObj
															.getInt("ad_id"));
													appInfo.setResource_size(childObj
															.getString("resource_size"));
													appInfo.setB_type(childObj
															.getInt("btype"));
													appInfo.setScore((int)(childObj
															.getInt("score")*appInfo.getVcPrice()));
													appInfo.setClicktype(childObj.getInt("clicktype"));
													String h5Url = childObj
															.getString("h5_big_url");
													
													appInfo.setInstall_id(obj
															.getInt("ad_install_id"));
													appInfo.setIs_photo(obj.getInt("is_photo"));
													appInfo.setPhoto_integral((int)(obj.getInt("photo_integral")*appInfo.getVcPrice()));
													appInfo.setPhoto_status(obj.getInt("photo_status"));
													appInfo.setIs_photo_task(obj.getInt("is_photo_task"));
													appInfo.setUpload_photo(obj.getInt("photo_upload_number"));
													appInfo.setCurr_upload_photo(obj.getInt("upload_photo_number"));
													appInfo.setPhoto_remarks(childObj.getString("photo_remarks"));
													appInfo.setCheck_remarks(obj.getString("photo_remarks"));
													appInfo.setBigPushUrl(childObj.getString("big_push_url").isEmpty()?"":Constant.URL.ROOT_URL+childObj.getString("big_push_url"));
													
													String photo = obj.getString("photo");
													if(!photo.isEmpty()&&!photo.contains("http")){
														photo = Constant.URL.ROOT_URL
																+ photo;
													}
													
													String fileUrl = childObj
															.getString("file");
													String iconUrl = childObj
															.getString("icon");
													if (!fileUrl.isEmpty()&&!fileUrl
															.contains("http")) {
														fileUrl = Constant.URL.ROOT_URL
																+ fileUrl;
													}
													if (!iconUrl.isEmpty()&&!iconUrl
															.contains("http")) {
														iconUrl = Constant.URL.ROOT_URL
																+ iconUrl;
													}
													if (!h5Url.isEmpty()&&!h5Url.contains("http")) {
														h5Url = Constant.URL.ROOT_URL
																+ h5Url;
													}

													appInfo.setFile(fileUrl);
													appInfo.setIcon(iconUrl);
													appInfo.setH5_big_url(h5Url);
													appInfo.setPhoto(photo);
												}
												appInfo.setSign_times(obj
														.getInt("sign_count"));
												appInfo.setNeedSign_times(obj
														.getInt("sign_number"));
												appInfo.setSign_rules(obj
														.getInt("reportsigntime"));
												appInfo.setPackage_name(obj
														.getString("package_name"));
												appInfo.setIsAddIntegral(obj
														.getInt("is_add_integral"));
												appInfo.setAppeal(obj.getInt("appeal"));
												appInfo.setSign(true);
												
												JSONArray plJson = childObj.getJSONArray("picture_list");
												if(plJson!=null && plJson.length()>0){
													List<String> l = new ArrayList<String>();
													for(int j=0;j<plJson.length();j++){
														String url = plJson.getString(j);
														if (!url.contains("http")) {
															url = Constant.URL.ROOT_URL
																	+ url;
														}
														l.add(url);
													}
													appInfo.setImgsList(l);
												}
												
												if(appInfo.getCurr_upload_photo()>0){
													JSONArray ulJson = obj.getJSONArray("upload_picture_list");
													if(plJson!=null && ulJson.length()>0){
														List<String> l = new ArrayList<String>();
														for(int j=0;j<ulJson.length();j++){
															String url = ulJson.getString(j);
															if (!url.contains("http")) {
																url = Constant.URL.ROOT_URL
																		+ url;
															}
															l.add(url);
														}
														appInfo.setUpImgList(l);
													}
												}
												
												if(isSignTime(obj) || appInfo.getClicktype()==1){
													appInfo.setSignTime(true);
												}
												
												//if (appInfo.getIs_photo_task() != 1) {
												if(appInfo.getPhoto_status() != 3){
													depthList.add(appInfo);
												}else{
													String date = (null == obj.getString("update_date") || obj
															.getString("update_date").equals("null")) ? "" : obj
															.getString("update_date");
													if(canAppeal(date)){
														depthList.add(appInfo);
													}
												}
//												}else{
//													if(appInfo.getIs_photo() == 0 || appInfo.getPhoto_status() == 2){
//														depthList.add(appInfo);
//													}
//												}

											}
										}
									}

									Message msg = mHandler.obtainMessage();
									msg.what = 1;
									mHandler.sendMessage(msg);
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally {
								progressDialog.dismiss();
							}

						} else {
							progressDialog.dismiss();
						}
					}

					@SuppressLint("SimpleDateFormat")
					private boolean isSignTime(JSONObject obj) {
						SimpleDateFormat df = new SimpleDateFormat(
								"yyyy-MM-dd hh:mm:ss");
						String date;
						long time;
						try {
							date = (null == obj.getString("update_date") || obj
									.getString("update_date").equals("null")) ? obj
									.getString("create_date") : obj
									.getString("update_date");

							time = df.parse(date).getTime()
									+ obj.getLong("reportsigntime") * 24 * 60
									* 60 * 1000;
							if (time < System.currentTimeMillis()) {
								return true;
							}
						} catch (ParseException | JSONException e) {
							// TODO Auto-generated catch
							// block
							e.printStackTrace();
						}

						return false;
					}
					
					@SuppressLint("SimpleDateFormat")
					private boolean canAppeal(String date) {
						SimpleDateFormat df = new SimpleDateFormat(
								"yyyy-MM-dd hh:mm:ss");
						long time;
						try {

							time = df.parse(date).getTime();
							if (time + 24 * 60
									* 60 * 1000 > System.currentTimeMillis()) {
								return true;
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch
							// block
							e.printStackTrace();
						}

						return false;
					}

				});
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (depthList.size() > 0) {
					tvTips.setVisibility(View.GONE);
					myListView.setVisibility(View.VISIBLE);
					if (null == adapter) {
						adapter = new DepthTaskAdapter(getActivity(),
								depthList, myListView,new SignClickListener() {
									
									@Override
									public void onSignClickListener(AppInfo appInfo) {
										mListener.onBtnClickListener(Constant.STEP_2, appInfo);
									}
								});
					} else {
						adapter.notifyDataSetChanged();
					}
					myListView.setAdapter(adapter);
				} else {
					tvTips.setVisibility(View.VISIBLE);
					myListView.setVisibility(View.GONE);
				}

				break;
			default:
				break;
			}
		}
	};
}
