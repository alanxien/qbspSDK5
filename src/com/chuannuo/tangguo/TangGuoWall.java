package com.chuannuo.tangguo;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chuannuo.tangguo.listener.AddPointListener;
import com.chuannuo.tangguo.listener.CreaterUserListener;
import com.chuannuo.tangguo.listener.DownLoadListener;
import com.chuannuo.tangguo.listener.GetTotalPointListener;
import com.chuannuo.tangguo.listener.ReportListener;
import com.chuannuo.tangguo.listener.ResponseStateListener;
import com.chuannuo.tangguo.listener.SignInListener;
import com.chuannuo.tangguo.listener.SpendPointListener;
import com.chuannuo.tangguo.listener.TangGuoDataListListener;
import com.chuannuo.tangguo.listener.TangGuoWallListener;
import com.chuannuo.tangguo.task.AddPointTask;
import com.chuannuo.tangguo.task.CreateUserTask;
import com.chuannuo.tangguo.task.ReportTask;
import com.chuannuo.tangguo.task.SpendPointTask;

/** 
 * TODO<请描述这个类是干什么的> 
 * @author  xie.xin 
 * @data:  2015-7-1 下午10:56:32 
 * @version:  V1.0 
 */
public class TangGuoWall {
	private static String TAG = TangGuoWall.class.getSimpleName();
	
	private static Context context;
	private static String theme ;
	public static TangGuoWallListener tangGuoWallListener;
	private static TangGuoDataListListener tangGuoDataListListener;
	public static DownLoadListener downLoadListener;
	public static SignInListener signInListener;
	private static SharedPreferences pref;
	private static String USERID;
	private static boolean isFirstIn; // 是否第一次启动
	protected static Editor editor;
	
	protected static String APPKEY;
	protected static String APPID;
	private static List<PackageInfo> paklist;
	private static boolean isDataList = false;
	private static boolean isDeptList = false;
	
	private static double score = 1;
	private static int isShow = 0;
	private static String textName = "积分";
	private static List<Object> dataList;
	private static List<Object> deptList;
	
	private static int adId;

	/** 
	* @Title: initWall 
	* @Description: TODO
	* @param @param ctx
	* @param @param id
	* @return void 
	* @throws 
	*/
	public static void initWall(Context ctx,String id){
		context = ctx;
		if (null == pref) {
			pref = context.getSharedPreferences(Constant.PREF_QIANBAO_SDK,
					Context.MODE_PRIVATE);
		}
		USERID = id;
		APPKEY = PhoneInformation.getMetaData(context, Constant.TANGGUO_APPKEY);
		APPID = PhoneInformation.getMetaData(context, Constant.TANGGUO_APPID);

	}
	
	public static void init(Context ctx,String id){
		context = ctx;
		if (null == pref) {
			pref = context.getSharedPreferences(Constant.PREF_QIANBAO_SDK,
					Context.MODE_PRIVATE);
		}
		isFirstIn = pref.getBoolean(Constant.IS_FIRST_IN, true);
		
		if(null == editor){
			editor = pref.edit();
		}
		USERID = id;
		APPKEY = PhoneInformation.getMetaData(context, Constant.TANGGUO_APPKEY);
		APPID = PhoneInformation.getMetaData(context, Constant.TANGGUO_APPID);
		PhoneInformation.initTelephonyManager(context);
	}

	public static String getUserId(){
		return USERID;
	}

	public static void setTangGuoWallListener(TangGuoWallListener listener) {
		tangGuoWallListener = listener;
	}
	
	public static void setTangGuoDataListListener(TangGuoDataListListener listener){
		tangGuoDataListListener = listener;
	}
	
	public static void registerDownLoadListener(DownLoadListener listener){
		downLoadListener = listener;
	}
	
	public static void registerSignInListenter(SignInListener listener){
		signInListener = listener;
	}

	/** 
	* @Title: show 
	* @Description: TODO
	* @param 
	* @return void 
	* @throws 
	*/
	public static void show(){
		isDataList = false;
		isDeptList = false;
		if (isFirstIn) {
			createUser();
		} else {
			paklist = context.getPackageManager().getInstalledPackages(0);
			/*
			 * 上报手机已经安装的软件 各个应用包名用，隔开eg:
			 * cn.winads.studentsearn,cn.winads.ldbatterySteward
			 */
			reportInstalledApp();
		}
		Intent intent = new Intent();
		intent.putExtra("color", theme);
		intent.setClass(context, TangGuoActivity.class);
		context.startActivity(intent);
	}
	
	/** 
	* @Title: setColor 
	* @Description: 设置主题
	* @param @param color
	* @return void 
	* @throws 
	*/
	public static void setColor(String color){
		theme = color;
	}
	
	/** 
	* @Title: getTotalPoint 
	* @Description: 获取虚拟货币总额
	* @param @return
	* @return int 
	* @throws 
	*/
	public static void getTotalPoint(final GetTotalPointListener listener){
		HttpUtil.setParams("id", pref.getString(Constant.APP_ID, "0"));
		HttpUtil.setParams("channel_id", PhoneInformation.getMetaData(context, Constant.TANGGUO_APPID));
		new SpendPointTask().execute(Constant.URL.USER_INFO_URL,new ResponseStateListener() {

			@Override
			public void onSuccess(Object result) {
				try {
					if (null != result
							&& !result
									.equals(Constant.NET_ERROR)) {
						JSONObject jsonObject;
						jsonObject = new JSONObject(result
								.toString());
						String code = jsonObject
								.getString("code");
						JSONObject data = jsonObject.getJSONObject("data");
						if (code.equals("1")) {
							
							if(null != data){
								listener.getTotalPoint(Constant.ACCESS_SUCCESS, data.getString("txt_name"),data.getInt("integral"));
							}else{
								listener.getTotalPoint(Constant.ACCESS_FAILURE,"", 0);
							}
							
						}else{
							listener.getTotalPoint(Constant.ACCESS_FAILURE,"", 0);
						}
					}else{
						listener.getTotalPoint(Constant.ACCESS_FAILURE,"", 0);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			};
		});

	}
	
	/** 
	* @Title: spendPoint 
	* @Description: 消费虚拟货币
	* @param @param listener
	* @return void 
	* @throws 
	*/
	public static void spendPoint(final int point,final SpendPointListener listener){
		HttpUtil.setParams("appid", pref.getString(Constant.APP_ID, "0"));
		HttpUtil.setParams("code", pref.getString(Constant.CODE, "0"));
		HttpUtil.setParams("integral", point+"");
		HttpUtil.setParams("key", PhoneInformation.getMetaData(context, Constant.TANGGUO_APPKEY));
		HttpUtil.setParams("channel_id", PhoneInformation.getMetaData(context, Constant.TANGGUO_APPID));
		PhoneInformation.initTelephonyManager(context);
		HttpUtil.setParams("imei", PhoneInformation.getImei());
		HttpUtil.setParams("imsi", PhoneInformation.getImsi());
		HttpUtil.setParams("machineType", PhoneInformation.getMachineType());
		HttpUtil.setParams("net_type", PhoneInformation.getNetType()+"");
		HttpUtil.setParams("macaddress", PhoneInformation.getMacAddress());
		HttpUtil.setParams("androidid",Secure.getString(context.getContentResolver(),Secure.ANDROID_ID));
	
		new SpendPointTask().execute(Constant.URL.EXCHANGE_URL,
				new ResponseStateListener() {

					@Override
					public void onSuccess(Object result) {
						try {
							if (null != result
									&& !result
											.equals(Constant.NET_ERROR)) {
								JSONObject jsonObject;
								jsonObject = new JSONObject(result
										.toString());
								String code = jsonObject
										.getString("code");
								if (code.equals("1")) {
									listener.spendPoint(Constant.ACCESS_SUCCESS, point);
								}else{
									listener.spendPoint(Constant.ACCESS_FAILURE,point);
								}
							}else{
								listener.spendPoint(Constant.ACCESS_FAILURE,point);
							}
						} catch (Exception e) {
							// TODO: handle exception
						}

					};
				});
	}
	
	/** 
	* @Title: addPoint 
	* @Description: 客户端增加积分
	* @param @param point
	* @param @param listener
	* @return void 
	* @throws 
	*/
	public static void addPoint(final int point,final AddPointListener listener){
		HttpUtil.setParams("appid", pref.getString(Constant.APP_ID, "0"));
		HttpUtil.setParams("code", pref.getString(Constant.CODE, "0"));
		HttpUtil.setParams("integral", point+"");
		HttpUtil.setParams("key", PhoneInformation.getMetaData(context, Constant.TANGGUO_APPKEY));
		HttpUtil.setParams("channel_id", PhoneInformation.getMetaData(context, Constant.TANGGUO_APPID));
		PhoneInformation.initTelephonyManager(context);
		HttpUtil.setParams("imei", PhoneInformation.getImei());
		HttpUtil.setParams("imsi", PhoneInformation.getImsi());
		HttpUtil.setParams("machineType", PhoneInformation.getMachineType());
		HttpUtil.setParams("net_type", PhoneInformation.getNetType()+"");
		HttpUtil.setParams("macaddress", PhoneInformation.getMacAddress());
		HttpUtil.setParams("androidid",Secure.getString(context.getContentResolver(),Secure.ANDROID_ID));
	
		new AddPointTask().execute(Constant.URL.EXCHANGE_ADD_URL,
				new ResponseStateListener() {

					@Override
					public void onSuccess(Object result) {
						try {
							if (null != result
									&& !result
											.equals(Constant.NET_ERROR)) {
								JSONObject jsonObject;
								jsonObject = new JSONObject(result
										.toString());
								String code = jsonObject
										.getString("code");
								if (code.equals("1")) {
									listener.addPoint(Constant.ACCESS_SUCCESS, point);
								}else{
									listener.addPoint(Constant.ACCESS_FAILURE,point);
								}
							}else{
								listener.addPoint(Constant.ACCESS_FAILURE,point);
							}
						} catch (Exception e) {
							// TODO: handle exception
						}

					};
				});
	}
	
	private static void createUser() {

		PhoneInformation.initTelephonyManager(context);
		HttpUtil.setParams("key", TangGuoWall.APPKEY);
		HttpUtil.setParams("channel_id", TangGuoWall.APPKEY);
		HttpUtil.setParams("imei", PhoneInformation.getImei());
		HttpUtil.setParams("imsi", PhoneInformation.getImsi());
		HttpUtil.setParams("machineType", PhoneInformation.getMachineType());
		HttpUtil.setParams("os_vision", PhoneInformation.getOsVersion());
		HttpUtil.setParams("longt_latl", PhoneInformation.getLatitLongit());
		HttpUtil.setParams("resolution", ((Activity) context).getWindowManager()
				.getDefaultDisplay().getWidth()
				+ "x"
				+ ((Activity) context).getWindowManager().getDefaultDisplay()
						.getHeight());
		HttpUtil.setParams("net_type", PhoneInformation.getNetType() + "");
		HttpUtil.setParams("language", PhoneInformation.getLanguage());
		HttpUtil.setParams("macaddress", PhoneInformation.getMacAddress());
		HttpUtil.setParams("physical_size", PhoneInformation.getTotalMemory());
		HttpUtil.setParams("androidid", Secure.getString(context
				.getContentResolver(), Secure.ANDROID_ID));

		new CreateUserTask().execute(Constant.URL.CREATE_USER, new CreaterUserListener() {

			@Override
			public void onSuccess(Object result) {
				Message msg = mHandler.obtainMessage();
				if (null != result && !result.equals(Constant.NET_ERROR)) {
					JSONObject jsonObject;
					JSONObject data;
					try {
						jsonObject = new JSONObject(result.toString());
						String code = jsonObject.getString("code");
						if (code.equals("1")) {
							data = jsonObject.getJSONObject("data");
							editor.putString(Constant.APP_ID,
									data.getString("appid"));
							editor.putString(Constant.CODE,
									data.getString("code"));
							editor.putBoolean(Constant.IS_FIRST_IN, false);
							editor.commit();

							msg.what = 1;
							mHandler.sendMessage(msg);
						} else {
							if(isDataList){
								msg.what = 3;
								mHandler.sendMessage(msg);
								Log.e(TAG, "创建用户失败导致获取积分墙数据失败");
							}else if(isDeptList){
								msg.what = 5;
								mHandler.sendMessage(msg);
								Log.e(TAG, "创建用户失败导致获取深度任务数据失败");
							}else{
								Toast.makeText(context,
										jsonObject.getString("info"),
										Toast.LENGTH_SHORT).show();
							}
							
						}
					} catch (JSONException e) {
						if(isDataList){
							msg.what = 3;
							mHandler.sendMessage(msg);
							Log.e(TAG, "创建用户异常导致获取积分墙数据失败");
						}else if(isDeptList){
							msg.what = 5;
							mHandler.sendMessage(msg);
							Log.e(TAG, "创建用户异常导致获取深度任务数据失败");
						}else{
							Toast.makeText(context,
									"获取数据失败",
									Toast.LENGTH_SHORT).show();
						}
						e.printStackTrace();
					}

				} else {
					if(isDataList){
						msg.what = 3;
						mHandler.sendMessage(msg);
						Log.e(TAG, "访问失败");
					}else if(isDeptList){
						msg.what = 5;
						mHandler.sendMessage(msg);
						Log.e(TAG, "访问失败");
					}else{
						Toast.makeText(context, "获取数据失败", Toast.LENGTH_SHORT)
						.show();
					}
					
				}
			}

		});
	}
	
	static Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// 获取手机内所有应用
				paklist = context.getPackageManager()
						.getInstalledPackages(0);
				/*
				 * 上报手机已经安装的软件 各个应用包名用，隔开eg:
				 * cn.winads.studentsearn,cn.winads.ldbatterySteward
				 */
				isDataList = false;
				reportInstalledApp();
				break;
			case 2:
				//开始获取dataList数据
				getRecomList();
				break;
			case 3:
				//获取dataList数据失败（创建用户 或者上报应用失败）
				tangGuoDataListListener.getAdList(0, null);
				break;
			case 4:
				//开始获取DeptList
				getDeptTaskList();
				break;
			case 5:
				//获取DeptList失败
				tangGuoDataListListener.getDeptList(0, null);
				break;
			case 6:
				//获取dataList数据成功
				tangGuoDataListListener.getAdList(1, dataList);
				break;
			case 7:
				//获取DeptList成功
				tangGuoDataListListener.getDeptList(1, deptList);
				break;
			case 8:
				final AppInfo res = (AppInfo) msg.obj;
				sign(res);
				break;
			case 9:
				signInListener.onSignIn(0,adId,0);
				break;
			default:
				break;
			}
		}

	};
	
	
	/** 
	* @Title: reportInstalledApp 
	* @Description: TODO
	* @author  xie.xin
	* @param 
	* @return void 
	* @throws 
	*/
	private static void reportInstalledApp() {
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
		HttpUtil.setParams("app_id", pref.getString(Constant.APP_ID, "0"));
		HttpUtil.setParams("package_names", pakStr.toString());
		new ReportTask().execute(Constant.URL.REPORT_URL, new ReportListener() {

			@Override
			public void onSuccess(Object result) {
				Message msg = mHandler.obtainMessage();
				if (null != result && !result.equals(Constant.NET_ERROR)) {
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(result.toString());
						String code = jsonObject.getString("code");
						if (!code.equals("1")) {
							if(isDataList){
								msg.what = 3;
								mHandler.sendMessage(msg);
								Log.e(TAG, "上报失败导致获取数据失败");
							}else{
								Toast.makeText(context, "获取数据失败",
										Toast.LENGTH_SHORT).show();
							}
						}else{
							if(isDataList){
								msg.what = 2;
								mHandler.sendMessage(msg);
							}
							
						}
					} catch (Exception e) {
						if(isDataList){
							msg.what = 3;
							mHandler.sendMessage(msg);
							Log.e(TAG, "上报发生异常导致获取数据失败");
							e.printStackTrace();
						}else{
							Toast.makeText(context, "获取数据失败！",
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					if(isDataList){
						msg.what = 3;
						mHandler.sendMessage(msg);
						Log.e(TAG, "访问网络获取数据失败");
					}else{
						Toast.makeText(context, "获取数据失败！", Toast.LENGTH_SHORT)
						.show();
					}
				}
			}
		});
	}
	
	/** 
	* @Title: getDataList 
	* @Description: 获取任务列表
	* @author  xie.xin
	* @param 
	* @return void 
	* @throws 
	*/
	public static void getDataList(){
		if(!PhoneInformation.isSimReady()){
			Toast.makeText(context, "请插入SIM卡", Toast.LENGTH_SHORT).show();
		}else{
			isDataList = true;
			if (isFirstIn) {
				createUser();
			} else {
				paklist = context.getPackageManager().getInstalledPackages(0);
				/*
				 * 上报手机已经安装的软件 各个应用包名用，隔开eg:
				 * cn.winads.studentsearn,cn.winads.ldbatterySteward
				 */
				reportInstalledApp();
			}
		}
		
	}
	
	/** 
	* @Title: getDeptList 
	* @Description: 获取未完成任务列表
	* @author  xie.xin
	* @param 
	* @return void 
	* @throws 
	*/
	public static void getDeptList(){
		if(!PhoneInformation.isSimReady()){
			Toast.makeText(context, "请插入SIM卡", Toast.LENGTH_SHORT).show();
		}else{
			isDeptList = true;
			if (isFirstIn) {
				createUser();
			}else{
				getDeptTaskList();
			}
		}
		
	}
	
	private static void getRecomList(){
		HttpUtil.setParams("app_id", pref.getString(Constant.APP_ID, "0"));
		HttpUtil.setParams("channel_id", TangGuoWall.APPID);
		HttpUtil.setParams("ip", pref.getString(Constant.IP, "0.0.0.0"));
		HttpUtil.setParams("city", pref.getString(Constant.CITY, ""));
		HttpUtil.setParams("isp", pref.getString(Constant.ISP, "0.0.0.0"));
		HttpUtil.setParams("imsi", pref.getString(Constant.CODE, ""));
		PhoneInformation.initTelephonyManager(context);
		HttpUtil.setParams("imsi", PhoneInformation.getImsi());
		HttpUtil.post(Constant.URL.DOWNLOAD_URL,
				new ResponseStateListener() {

					@Override
					public void onSuccess(Object result) {
						if (null != result
								&& !result.equals(Constant.NET_ERROR)) {
							JSONObject jsonObject;
							JSONObject infoObject;
							dataList = new ArrayList<Object>();
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
												appInfo.setTotalScore((int) ((obj
														.getInt("score") + obj
														.getInt("sign_number") * 10) * score));
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
//												String dUrl = Constant.PREF_TANGGUO_DATA+appInfo.getAdId();
//												editor.putString(dUrl, fileUrl);
//												editor.commit();
												appInfo.setH5_big_url(h5Url);
												appInfo.setIcon(iconUrl);

												dataList.add(appInfo);
											}
										}
									}

									Message msg = mHandler.obtainMessage();
									msg.what = 6;
									mHandler.sendMessage(msg);
								} else {
									Message msg = mHandler.obtainMessage();
									msg.what = 3;
									mHandler.sendMessage(msg);
									Log.e(TAG, "获取数据失败"+code);
								}
							} catch (JSONException e) {
								Message msg = mHandler.obtainMessage();
								msg.what = 3;
								mHandler.sendMessage(msg);
								Log.e(TAG, "异常导致获取数据失败");
								e.printStackTrace();
							}

						} else {
							Message msg = mHandler.obtainMessage();
							msg.what = 3;
							mHandler.sendMessage(msg);
							Log.e(TAG, "网络异常导致获取数据失败");
						}
					}

				});
	}
	
	private static void getDeptTaskList(){
		HttpUtil.setParams("app_id", pref.getString(Constant.APP_ID, "0"));
		HttpUtil.post(Constant.URL.UN_FINISHED_TASK,
				new ResponseStateListener() {

					@Override
					public void onSuccess(Object result) {
						deptList = new ArrayList<Object>();
						Message msg = mHandler.obtainMessage();
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
													appInfo.setResource_id(childObj
															.getInt("id"));
													appInfo.setAdId(childObj
															.getInt("ad_id"));
													appInfo.setResource_size(childObj
															.getString("resource_size"));
													appInfo.setB_type(childObj
															.getInt("btype"));

													String fileUrl = childObj
															.getString("file");
													String iconUrl = childObj
															.getString("icon");
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

													appInfo.setFile(fileUrl);
//													String dUrl = Constant.PREF_TANGGUO_DATA+appInfo.getAdId();
//													editor.putString(dUrl, fileUrl);
//													editor.commit();
													appInfo.setIcon(iconUrl);
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
												appInfo.setScore(obj
														.getInt("integral"));

												if (appInfo.getIsAddIntegral() == 0
														|| isSignTime(obj)) {
													deptList.add(appInfo);
												}

											}
										}
									}

									msg.what = 7;
									mHandler.sendMessage(msg);
								}
							} catch (JSONException e) {
								msg.what = 5;
								mHandler.sendMessage(msg);
								Log.e(TAG, "异常获取数据失败");
								e.printStackTrace();
							} 

						} else {
							msg.what = 5;
							mHandler.sendMessage(msg);
							Log.e(TAG, "网络异常获取数据失败");
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

				});
	}
	
	/** 
	* @Title: download 
	* @Description: 下载广告
	* @author  xie.xin
	* @param @param adId
	* @return void 
	* @throws 
	*/
	public static void download(AppInfo appInfo){
		if(appInfo != null){
			Intent intent = new Intent(context,DownloadService.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(Constant.ITEM, appInfo);
			intent.putExtras(bundle);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(intent);
		}
	}
	
	/** 
	* @Title: signIn 
	* @Description: 签到
	* @author  xie.xin
	* @param @param appInfo
	* @return void 
	* @throws 
	*/
	public static void signIn(final AppInfo appInfo){
		adId = appInfo.getAdId();
		// 应用被卸载，重新安装
		if (checkPackage(appInfo.getPackage_name())) {// 如果
																		// 应用已经安装
			doStartApplicationWithPackageName(appInfo.getPackage_name());
			
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					if (isTopActivity(appInfo.getPackage_name())) {
						Message msg = mHandler.obtainMessage(8,
								appInfo);
						msg.arg1 = appInfo.getSign_rules();
						msg.arg2 = appInfo.getIsAddIntegral();
						mHandler.sendMessage(msg);
					} else {
						Message msg = mHandler.obtainMessage();
						msg.what = 9;
						mHandler.sendMessage(msg);
					}
				}

			}, 3 * 60 * 1000);
		} else {
			Intent intent = new Intent(context, DownloadService.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(Constant.ITEM,
					appInfo);
			bundle.putBoolean("isRepeatDown", true);
			bundle.putBoolean("isData", true);
			intent.putExtras(bundle);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(intent);
		}
	}
	
	private static boolean checkPackage(String packageName) {
		if (packageName == null || "".equals(packageName)) {
			return false;
		}
		try {
			context.getPackageManager().getApplicationInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
	
	private static void doStartApplicationWithPackageName(String packagename) {

		// 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
		PackageInfo packageinfo = null;
		try {
			packageinfo = context.getPackageManager().getPackageInfo(
					packagename, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (packageinfo == null) {
			return;
		}

		// 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageinfo.packageName);

		// 通过getPackageManager()的queryIntentActivities方法遍历
		List<ResolveInfo> resolveinfoList = context.getPackageManager()
				.queryIntentActivities(resolveIntent, 0);

		ResolveInfo resolveinfo = resolveinfoList.iterator().next();
		if (resolveinfo != null) {
			// packagename = 参数packname
			String packageName = resolveinfo.activityInfo.packageName;
			// 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
			String className = resolveinfo.activityInfo.name;
			// LAUNCHER Intent
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);

			// 设置ComponentName参数1:packagename参数2:MainActivity路径
			ComponentName cn = new ComponentName(packageName, className);

			intent.setComponent(cn);
			context.startActivity(intent);
		}
	}
	
	private static boolean isTopActivity(String packageName) {

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			Log.i(TAG, "---------------包名-----------" + packageName);
			// 应用程序位于堆栈的顶层
			if (packageName.equals(tasksInfo.get(0).topActivity
					.getPackageName())) {
				Log.i(TAG, packageName);
				return true;
			}
		}
		return false;
	}
	
	private static void sign(final AppInfo appInfo){
		HttpUtil.setParams("app_id",
				pref.getString(Constant.APP_ID, "0"));
		HttpUtil.setParams("ad_install_id", appInfo.getInstall_id() + "");
		HttpUtil.setParams("key", PhoneInformation.getMetaData(context,
				Constant.TANGGUO_APPKEY));
		HttpUtil.setParams("channel_id", PhoneInformation.getMetaData(
				context, Constant.TANGGUO_APPID));
		PhoneInformation.initTelephonyManager(context);
		HttpUtil.setParams("imei", PhoneInformation.getImei());
		HttpUtil.setParams("imsi", PhoneInformation.getImsi());
		HttpUtil.setParams("machineType",
				PhoneInformation.getMachineType());
		HttpUtil.setParams("net_type", PhoneInformation.getNetType()
				+ "");
		HttpUtil.setParams("macaddress",
				PhoneInformation.getMacAddress());
		HttpUtil.setParams("androidid", Secure.getString(
				context.getContentResolver(), Secure.ANDROID_ID));
		HttpUtil.setParams("ip", pref.getString(Constant.IP, "0.0.0.0"));
		HttpUtil.setParams("code", pref.getString(Constant.CODE, ""));
		/*
		 * 增加积分（防止用户第一次下载应用没有体验足够长时间，而没有加积分）
		 */
		if (appInfo.getIsAddIntegral() == 0) {

			Toast.makeText(context, appInfo.getAlert(), Toast.LENGTH_LONG)
					.show();
			HttpUtil.post(Constant.URL.CONFIRM_INSTALL_INTEGRAL,
					new ResponseStateListener() {

						@Override
						public void onSuccess(Object result) {
							try {
								if (null != result
										&& !result
												.equals(Constant.NET_ERROR)) {
									JSONObject jsonObject;
									jsonObject = new JSONObject(result
											.toString());
									String code = jsonObject
											.getString("code");

									if (code.equals("1")) {
										JSONObject data = jsonObject
												.getJSONObject("data");
										signInListener.onSignIn(1, adId,10*appInfo.getVcPrice());
									} else {
										signInListener.onSignIn(0, adId,0);
									}
								}
							} catch (Exception e) {
								signInListener.onSignIn(0, adId,0);
							}

						};
					});
		} else {

			Toast.makeText(context, "试玩两分钟即完成签到", Toast.LENGTH_LONG)
					.show();
			/*
			 * 签到
			 */
			HttpUtil.post(Constant.URL.REPEAT_SIGN_URL,
					new ResponseStateListener() {

						@Override
						public void onSuccess(Object result) {
							try {
								if (null != result
										&& !result
												.equals(Constant.NET_ERROR)) {
									JSONObject jsonObject;
									jsonObject = new JSONObject(result
											.toString());
									String code = jsonObject
											.getString("code");
									if (code.equals("1")) {
										JSONObject data = jsonObject
												.getJSONObject("data");
										signInListener.onSignIn(1, adId,10*appInfo.getVcPrice());
									} else {
										signInListener.onSignIn(0, adId,0);
									}
								}
							} catch (Exception e) {
								signInListener.onSignIn(0, adId,0);
							}

						};
					});
		}
	}
}
