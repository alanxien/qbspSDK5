package com.chuannuo.tangguo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

import com.chuannuo.tangguo.androidprocess.AndroidAppProcess;
import com.chuannuo.tangguo.androidprocess.AndroidAppProcessLoader;
import com.chuannuo.tangguo.androidprocess.Listener;
import com.chuannuo.tangguo.imageLoader.ImageLoader;
import com.chuannuo.tangguo.listener.ResponseStateListener;

/**
 * @author alan.xie
 * @date 2014-12-3 下午4:44:25
 * @Description: 下载服务
 */
public class DownloadService extends Service implements Listener{

	private static String TAG = "DownloadService";
	// 标题
	private int titleId = 0;
	// 文件存储
	private File downloadDir = null;
	private File downloadFile = null;
	// 通知栏
	private NotificationManager downloadNotificationManager = null;
	// 通知栏跳转Intent
	// private Intent downloadIntent = null;
	private PendingIntent downloadPendingIntent = null;
	private Builder builder;

	private AppInfo appInfo;

	private SharedPreferences pref;
	private Editor editor;
	private int ad_install_id;

	private Timer timer;
	private boolean isRepeatDown;
	private boolean  isData;
	private Context  context;
	private int APILevel = 4;
	
	private List<AndroidAppProcess> aliList;
	private int count = 0;

	@Override
	public void onCreate() {
		Log.i("DownloadService", "service 已经启动，，，，");
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		context = getApplicationContext();
		// 获取传值
		if(intent == null){
			return 0;
		}
		appInfo = (AppInfo) intent.getSerializableExtra(Constant.ITEM);
		if(appInfo == null){
			return 0;
		}
		isRepeatDown = intent.getBooleanExtra("isRepeatDown", false);
		isData = intent.getBooleanExtra("isData", false);
		// 创建文件
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			downloadDir = new File(Environment.getExternalStorageDirectory(),
					Constant.DOWNLOAD_DIR);
			downloadFile = new File(downloadDir.getPath(),
					appInfo.getPackage_name() + ".apk");
		}

		// 注册 安装广播
		IntentFilter appInstallFilter = new IntentFilter();
		appInstallFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		appInstallFilter.addDataScheme("package");
		getApplicationContext().registerReceiver(appInstallReceiver,
				appInstallFilter);

		builder = new Notification.Builder(this);

		String s = android.os.Build.VERSION.RELEASE;
		String t[] = s.split(".");
		if (t.length > 0) {
			APILevel = Integer.parseInt(t[0]);
		}
		if (APILevel > 3) {
			builder.setProgress(100, 2, false)
					.setContentTitle(appInfo.getTitle() + "-正在下载")
					// 设置通知栏标题
					.setContentText("5%")
					.setContentIntent(null)
					// 设置通知栏点击意图
					// .setNumber(number) //设置通知集合的数量
					.setTicker("开始下载")
					// 通知首次出现在通知栏，
					.setWhen(System.currentTimeMillis())
					// 设置该通知优先级
					.setAutoCancel(false)
					// 设置这个标志当用户单击面板就可以让通知将自动取消
					.setOngoing(false)
					// ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
					.setDefaults(Notification.DEFAULT_VIBRATE)
					// 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
					.setSmallIcon(android.R.drawable.stat_sys_download)
					.setOnlyAlertOnce(true);// 设置通知ICON
		} else {
			builder.setContentTitle(appInfo.getTitle() + "-正在下载")
					// 设置通知栏标题
					.setContentText("5%")
					.setContentIntent(null)
					// 设置通知栏点击意图
					// .setNumber(number) //设置通知集合的数量
					.setTicker("开始下载")
					// 通知首次出现在通知栏，
					.setWhen(System.currentTimeMillis())
					// 设置该通知优先级
					.setAutoCancel(false)
					// 设置这个标志当用户单击面板就可以让通知将自动取消
					.setOngoing(false)
					// ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
					.setDefaults(Notification.DEFAULT_VIBRATE)
					// 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
					.setSmallIcon(android.R.drawable.stat_sys_download)
					.setOnlyAlertOnce(true);// 设置通知ICON
		}
		

		this.downloadNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// 发出通知
		downloadNotificationManager.notify(0, builder.getNotification());

		/*
		 * 异步加载图标
		 */
//		Drawable cachedImage = SyncImageLoader.getInstance().loadDrawable(
//				appInfo.getIcon(), new ImageCallback() {
//					public void imageLoaded(Drawable imageDrawable,
//							String imageUrl) {
//						builder.setLargeIcon(ResourceUtil
//								.drawable2Bitmap(imageDrawable));
//						// 发出通知
//						downloadNotificationManager.notify(0,
//								builder.getNotification());
//					}
//				});
//		if (cachedImage == null) {
//			builder.setLargeIcon(ResourceUtil.getImageFromAssetsFile(this,
//					"tangguo.png"));
//		} else {
//			builder.setLargeIcon(ResourceUtil.drawable2Bitmap(cachedImage));
//		}
		//builder.setLargeIcon();

		if(null != TangGuoWall.downLoadListener){
			TangGuoWall.downLoadListener.onDownloadStart(appInfo.getAdId());
		}
		
		// 开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
		new Thread(new downloadRunnable()).start();// 这个是下载的重点，是下载的过程

		return super.onStartCommand(intent, Service.START_REDELIVER_INTENT,
				startId);
	}

	/**
	 * @author alan.xie
	 * @date 2014-12-3 下午5:35:42
	 * @Description: 下载app
	 * @param @param downloadUrl
	 * @param @param saveFile
	 * @param @return
	 * @param @throws Exception
	 * @return long
	 */
	public long downloadFile(String downloadUrl, File saveFile)
			throws Exception {
		int downloadCount = 0;
		int currentSize = 0;
		long totalSize = 0;
		int updateTotalSize = 0;

		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			URL url = new URL(downloadUrl);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection
					.setRequestProperty("User-Agent", "PacificHttpClient");
			if (currentSize > 0) {
				httpConnection.setRequestProperty("RANGE", "bytes="
						+ currentSize + "-");
			}
			httpConnection.setConnectTimeout(10000);
			httpConnection.setReadTimeout(20000);
			updateTotalSize = httpConnection.getContentLength();
			is = httpConnection.getInputStream();
			fos = new FileOutputStream(saveFile, false);
			byte buffer[] = new byte[4096];

			int readsize = 0;
			while ((readsize = is.read(buffer)) > 0) {
				fos.write(buffer, 0, readsize);
				totalSize += readsize;
				// 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
				if ((downloadCount == 0)
						|| (int) (totalSize * 100 / updateTotalSize) - 1 > downloadCount) {
					downloadCount += 1;
					if (APILevel > 3) {
						builder.setProgress(100, downloadCount, false)
								.setContentText(
										totalSize * 100 / updateTotalSize + "%");
					}

					downloadNotificationManager.notify(0,
							builder.getNotification());
				}
			}
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		return totalSize;
	}

	// 下载状态
	private final static int DOWNLOAD_COMPLETE = 0;
	private final static int DOWNLOAD_FAIL = 1;

	private Handler downloadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOAD_COMPLETE:
				// 点击安装PendingIntent
				Uri uri = Uri.fromFile(downloadFile);
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				installIntent.setDataAndType(uri,
						"application/vnd.android.package-archive");
				downloadPendingIntent = PendingIntent.getActivity(
						DownloadService.this, 0, installIntent, 0);
				installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				DownloadService.this.startActivity(installIntent);

				Log.i(TAG, "---开始安装---");
				if(null != TangGuoWall.downLoadListener){
					TangGuoWall.downLoadListener.onDownloadSuccess(appInfo.getAdId());
				}
				break;
			case DOWNLOAD_FAIL:
				// 下载失败
				if (APILevel > 3) {
					builder.setProgress(100, 99, false).setTicker("下载失败");
				}
				downloadNotificationManager
						.notify(0, builder.getNotification());
				// 停止服务
				downloadNotificationManager.cancelAll();
				stopSelf();
				if(null != TangGuoWall.downLoadListener){
					TangGuoWall.downLoadListener.onDownloadFailed(appInfo.getAdId());
				}
				break;
			default:
				// 停止服务
				downloadNotificationManager.cancelAll();
				stopSelf();
				break;
			}
		}
	};

	class downloadRunnable implements Runnable {
		Message message = downloadHandler.obtainMessage();

		public void run() {
			message.what = DOWNLOAD_COMPLETE;
			try {
				// 增加权限;
				if (!downloadDir.exists()) {
					downloadDir.mkdirs();
				}
				if (!downloadFile.exists()) {
					downloadFile.createNewFile();
				}
				// 下载函数，以QQ为例子
				// 增加权限;
				//String dUrl = Constant.PREF_TANGGUO_DATA+appInfo.getAdId();
				if(null == pref){
					pref = getSharedPreferences(Constant.PREF_QIANBAO_SDK,
							MODE_PRIVATE);	
				}
				editor = pref.edit();
				long downloadSize = downloadFile(appInfo.getFile(),
						downloadFile);
				if (downloadSize > 0) {
					// 下载成功
					downloadHandler.sendMessage(message);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				message.what = DOWNLOAD_FAIL;
				// 下载失败
				downloadHandler.sendMessage(message);
			}
		}
	}

	private BroadcastReceiver appInstallReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("intent: " + intent.getAction());
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
				// String packageName =
				// intent.getData().getSchemeSpecificPart();
				if(null != TangGuoWall.downLoadListener){
					TangGuoWall.downLoadListener.onInstallSuccess(appInfo.getAdId());
				}
				pref = getSharedPreferences(Constant.PREF_QIANBAO_SDK,
						MODE_PRIVATE);
				editor = pref.edit();
				if (isRepeatDown) {
					monitoring(); // 签到
				} else {
					editor.putLong(Constant.DOWNLOAD_APP_TIME,
							System.currentTimeMillis());
					editor.commit();
					adInstall();// 安装完成上报应用信息
				}
			}
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
				String packageName = intent.getData().getSchemeSpecificPart();
				Toast.makeText(context, "卸载成功" + packageName, Toast.LENGTH_LONG)
						.show();
			}
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
				String packageName = intent.getData().getSchemeSpecificPart();
				Toast.makeText(context, "替换成功" + packageName, Toast.LENGTH_LONG)
						.show();
			}
		}
	};

	/**
	 * @author alan.xie
	 * @date 2014-12-8 上午10:58:26
	 * @Description: 过滤已经下载安装过的app,并且上报信息
	 * @param
	 * @return void
	 */
	private void adInstall() {
		if(downloadFile.exists()){
			downloadFile.delete();
		}
		Log.i(TAG, "---开始上报---");
		if (pref.getInt(Constant.DOWNLOAD_TIMES, 0) == 0) {
			editor.putInt(Constant.DOWNLOAD_TIMES, 1);
			editor.putLong(Constant.DOWN_TIME, System.currentTimeMillis());
		} else {
			editor.putInt(Constant.DOWNLOAD_TIMES,
					pref.getInt(Constant.DOWNLOAD_TIMES, 0) + 1);
		}
		editor.putInt(Constant.RESOURCE_ID, appInfo.getResource_id());
		editor.commit();

		if (appInfo.getB_type() == 1) {
			Toast.makeText(this, "应用安装成功,试玩3分钟即可获的积分！", Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(this, "应用安装成功,注册即可获的积分！", Toast.LENGTH_LONG).show();
		}

		HttpUtil.setParams("ad_id", appInfo.getAdId() + "");
		HttpUtil.setParams("app_user_id", TangGuoWall.getUserId());
		HttpUtil.setParams("resource_id", appInfo.getResource_id() + "");
		HttpUtil.setParams("package_name", appInfo.getPackage_name());
		HttpUtil.setParams("integral", appInfo.getScore() + "");
		HttpUtil.setParams("app_id", pref.getString(Constant.APP_ID, "0"));
		HttpUtil.setParams("ip", pref.getString(Constant.IP, "0.0.0.0"));
		HttpUtil.setParams("code", pref.getString(Constant.CODE, ""));
		HttpUtil.setParams("key",
				PhoneInformation.getMetaData(this, Constant.TANGGUO_APPKEY));
		HttpUtil.setParams("channel_id",
				PhoneInformation.getMetaData(this, Constant.TANGGUO_APPID));
		PhoneInformation.initTelephonyManager(this);
		HttpUtil.setParams("imei", PhoneInformation.getImei());
		HttpUtil.setParams("imsi", PhoneInformation.getImsi());
		HttpUtil.setParams("machineType", PhoneInformation.getMachineType());
		HttpUtil.setParams("net_type", PhoneInformation.getNetType() + "");
		HttpUtil.setParams("macaddress", PhoneInformation.getMacAddress());
		HttpUtil.setParams("androidid",
				Secure.getString(this.getContentResolver(), Secure.ANDROID_ID));

		HttpUtil.post(Constant.URL.ADINSTALL_URL, new ResponseStateListener() {

			@Override
			public void onSuccess(Object result) {
				if (null != result && !result.equals(Constant.NET_ERROR)) {
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(result.toString());
						String code = jsonObject.getString("code");
						if (code.equals("1")) {
							JSONObject json = jsonObject.getJSONObject("data");
							ad_install_id = json.getInt("id");
							Log.i(TAG, "---上报成功---");
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		});
		monitoring();
	}

	/**
	 * @author alan.xie
	 * @date 2015-1-21 下午2:27:12
	 * @Description: 监控程序运行时间
	 * @param
	 * @return void
	 */
	public void monitoring() {
		timer = new Timer();
		ad_install_id = appInfo.getInstall_id();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				new AndroidAppProcessLoader(context, DownloadService.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		}, 10000, 30000);
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				HttpUtil.setParams("ad_install_id", ad_install_id + "");
				HttpUtil.setParams("app_id",
						pref.getString(Constant.APP_ID, "0"));
				HttpUtil.setParams("key", PhoneInformation.getMetaData(
						getApplicationContext(), Constant.TANGGUO_APPKEY));
				HttpUtil.setParams("channel_id", PhoneInformation.getMetaData(
						getApplicationContext(), Constant.TANGGUO_APPID));
				PhoneInformation.initTelephonyManager(getApplicationContext());
				HttpUtil.setParams("imei", PhoneInformation.getImei());
				HttpUtil.setParams("imsi", PhoneInformation.getImsi());
				HttpUtil.setParams("machineType",
						PhoneInformation.getMachineType());
				HttpUtil.setParams("net_type", PhoneInformation.getNetType()
						+ "");
				HttpUtil.setParams("macaddress",
						PhoneInformation.getMacAddress());
				HttpUtil.setParams("androidid", Secure.getString(
						getApplicationContext().getContentResolver(),
						Secure.ANDROID_ID));
				HttpUtil.setParams("ip", pref.getString(Constant.IP, "0.0.0.0"));
				HttpUtil.setParams("code", pref.getString(Constant.CODE, ""));
				HttpUtil.post(Constant.URL.CONFIRM_INSTALL_INTEGRAL,
						new ResponseStateListener() {

							@Override
							public void onSuccess(Object result) {
								if (null != result
										&& !result.equals(Constant.NET_ERROR)) {
									JSONObject jsonObject;
									try {
										jsonObject = new JSONObject(result
												.toString());
										String code = jsonObject
												.getString("code");
										if (code.equals("1")) {
											Log.i(TAG, "增加积分 ---- success");
											TangGuoWall.tangGuoWallListener
													.onAddPoint(
															Constant.ACCESS_SUCCESS,
															appInfo.getTitle(),
															appInfo.getScore());
										} else {
											TangGuoWall.tangGuoWallListener
													.onAddPoint(
															Constant.ACCESS_FAILURE,
															appInfo.getTitle(),
															appInfo.getScore());
										}

									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								} else {
									TangGuoWall.tangGuoWallListener.onAddPoint(
											Constant.ACCESS_FAILURE,
											appInfo.getTitle(),
											appInfo.getScore());
								}
								// 停止服务
								downloadNotificationManager.cancelAll();
								stopSelf();
							}
						});
				break;
			case 2:
//				TangGuoWall.tangGuoWallListener.onAddPoint(
//						Constant.ACCESS_FAILURE, appInfo.getTitle(),
//						appInfo.getScore());
				// 停止服务
				downloadNotificationManager.cancelAll();
				stopSelf();
				break;
			case 3:
				HttpUtil.setParams("app_id", pref.getString(Constant.APP_ID, "0"));
				HttpUtil.setParams("ad_install_id", ad_install_id + "");
				HttpUtil.setParams("key", PhoneInformation.getMetaData(getApplicationContext(), Constant.TANGGUO_APPKEY));
				HttpUtil.setParams("channel_id", PhoneInformation.getMetaData(getApplicationContext(), Constant.TANGGUO_APPID));
				PhoneInformation.initTelephonyManager(getApplicationContext());
				HttpUtil.setParams("imei", PhoneInformation.getImei());
				HttpUtil.setParams("imsi", PhoneInformation.getImsi());
				HttpUtil.setParams("machineType", PhoneInformation.getMachineType());
				HttpUtil.setParams("net_type", PhoneInformation.getNetType()+"");
				HttpUtil.setParams("macaddress", PhoneInformation.getMacAddress());
				HttpUtil.setParams("ip", pref.getString(Constant.IP, "0.0.0.0"));
				HttpUtil.setParams("code", pref.getString(Constant.CODE, ""));
				HttpUtil.setParams("androidid",Secure.getString(getApplicationContext().getContentResolver(),Secure.ANDROID_ID));
				/*
				 * 增加积分（防止用户第一次下载应用没有体验足够长时间，而没有加积分）
				 */
				if (appInfo.getIsAddIntegral() == 0) {
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
												if(isData){
													TangGuoWall.signInListener.onSignIn(1,appInfo.getAdId(),10*appInfo.getVcPrice());
												}else{
													JSONObject data = jsonObject.getJSONObject("data");
													TangGuoWall.tangGuoWallListener.onSign(Constant.ACCESS_SUCCESS, data.getString("ad_name"),data.getInt("integral"));
													editor.putInt(Constant.S_RESOURCE_ID, ad_install_id);
													editor.commit();
												}
											} else {
												if(isData){
													TangGuoWall.signInListener.onSignIn(0,appInfo.getAdId(),10*appInfo.getVcPrice());
												}else{
													TangGuoWall.tangGuoWallListener.onSign(Constant.ACCESS_FAILURE, "",0);
												}
											}
										}
									} catch (Exception e) {
										if(isData){
											TangGuoWall.signInListener.onSignIn(0,appInfo.getAdId(),10*appInfo.getVcPrice());
										}else{
											TangGuoWall.tangGuoWallListener.onSign(Constant.ACCESS_FAILURE, "",0);
										}
									}

								};
							});
				} else {
				/*
				 * 签到
				 */
				HttpUtil.post(Constant.URL.REPEAT_SIGN_URL,
						new ResponseStateListener() {

							@Override
							public void onSuccess(Object result) {
								try {
									if (null != result
											&& !result.equals(Constant.NET_ERROR)) {
										JSONObject jsonObject;
										jsonObject = new JSONObject(result
												.toString());
										String code = jsonObject.getString("code");
										if (code.equals("1")) {
											if(isData){
												TangGuoWall.signInListener.onSignIn(1,appInfo.getAdId(),10*appInfo.getVcPrice());
											}else{
												JSONObject data = jsonObject.getJSONObject("data");
												TangGuoWall.tangGuoWallListener.onSign(Constant.ACCESS_SUCCESS, data.getString("ad_name"),data.getInt("integral"));
												editor.putInt(Constant.S_RESOURCE_ID, ad_install_id);
												editor.commit();
											}
											
										} else {
											if(isData){
												TangGuoWall.signInListener.onSignIn(0,appInfo.getAdId(),10*appInfo.getVcPrice());
											}else{
												TangGuoWall.tangGuoWallListener.onSign(Constant.ACCESS_FAILURE, "",0);
											}
										}
										editor.commit();
									}
								} catch (Exception e) {
									if(isData){
										TangGuoWall.signInListener.onSignIn(0,appInfo.getAdId(),10*appInfo.getVcPrice());
									}else{
										TangGuoWall.tangGuoWallListener.onSign(Constant.ACCESS_FAILURE, "",0);
									}
								}

							};
						});
				}
				break;
			default:
				break;
			}
		};
	};

	/**
	 * @author alan.xie
	 * @date 2014-12-18 下午5:42:49
	 * @Description: 检测应用程序是否在前台运行
	 * @param @param packageName
	 * @param @return
	 * @return boolean
	 */
//	private boolean isTopActivity(String packageName) {
//
//		ActivityManager activityManager = (ActivityManager) this
//				.getSystemService(Context.ACTIVITY_SERVICE);
//		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
//		if (tasksInfo.size() > 0) {
//			Log.i(TAG, "---------------包名-----------" + packageName);
//			// 应用程序位于堆栈的顶层
//			if (packageName.equals(tasksInfo.get(0).topActivity
//					.getPackageName())) {
//				Log.i(TAG, packageName);
//				return true;
//			}
//		}
//		return false;
//	}

	/**
	 * @author alan.xie
	 * @date 2014-10-29 上午9:40:47
	 * @Description: 计算时间间隔是否大于days
	 * @param @return
	 * @return Boolean
	 */
//	public Boolean moreThanTimes(String type, long currTimes, int minute) {
//		long times = pref.getLong(type, 0);
//		if (currTimes - times > minute * 60 * 1000) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	public void signIn() {
////		editor.putLong(Constant.APP_RUNNING_TIME, System.currentTimeMillis());
////		editor.commit();
//
//		// Toast.makeText(this, "试玩两分钟即完成签到！", Toast.LENGTH_SHORT).show();
////		timer = new Timer();
////
////		timer.schedule(new TimerTask() {
////
////			@Override
////			public void run() {
////				if (isTopActivity(appInfo.getPackage_name())) {
////					Log.i(TAG, "---开始签到。。。---");
////					timer.cancel();
////					ad_install_id = appInfo.getInstall_id();
////					Message msg = mHandler.obtainMessage(2);
////					mHandler.sendMessage(msg);
////				} else {
////					Log.i(TAG, "---退出签到---");
////					timer.cancel();
////				}
////				editor.commit();
////			}
////		}, 3000, 60 * 1000);
//		
////		new Handler().postDelayed(new Runnable() {
////			public void run() {
////				if (isTopActivity(appInfo.getPackage_name())) {
////					Log.i(TAG, "应用签到成功----timer.cancel()");
////					ad_install_id = appInfo.getInstall_id();
////					Message msg = mHandler.obtainMessage(3);
////					mHandler.sendMessage(msg);
////				} else {
////					Log.i(TAG, "应用签到失败----timer.cancel()");
////				}
////			}
////		}, 60*3* 1000);
//		
//	}

	/* (non-Javadoc)
	 * @see com.chuannuo.tangguo.androidprocess.Listener#onComplete(java.util.List)
	 */
	@Override
	public void onComplete(List<AndroidAppProcess> processes) {
		if(aliList == null){
			aliList = new ArrayList<AndroidAppProcess>();
		}else{
			aliList.clear();
		}
		int size = processes.size();
		AndroidAppProcess aProcess;
		count++;
		for(int i=0;i<size;i++){
			aProcess = processes.get(i);
			if(aProcess.getPackageName() != null && aProcess.getPackageName().equals(appInfo.getPackage_name().trim())){
				aliList.add(aProcess);
			}
		}
		
		int l =aliList.size();
		if(l==0){
			editor.putBoolean(Constant.IS_REFRESH, true);
			editor.commit();
			Message msg = mHandler.obtainMessage();
			msg.what = 2;
			mHandler.sendMessage(msg);
			//体验失败
		}else {
			Boolean isFg = false;
			for(int i=0; i<l;i++){
				if(aliList.get(i).foreground){
					isFg = true;
				}
			}
			
			if(isFg){
				if(count == 5){
					Toast.makeText(this.context, "您已经体验了2分钟，继续体验3分钟！即可获得积分！", Toast.LENGTH_SHORT).show();
				}else if(count == 7){
					//体验成功
					if(isRepeatDown){
						editor.putBoolean(Constant.IS_REFRESH, false);
						editor.commit();
						Message msg = mHandler.obtainMessage();
						msg.what = 3;
						mHandler.sendMessage(msg);
					}else{
						if(appInfo.getIs_photo() == 1){
							editor.putBoolean(Constant.IS_REFRESH, true);
							editor.commit();
						}else{
							editor.putBoolean(Constant.IS_REFRESH, false);
							editor.commit();
						}
						Message msg = mHandler.obtainMessage();
						msg.what = 1;
						mHandler.sendMessage(msg);
					}
					
				}
			}else{
				//体验失败
				editor.putBoolean(Constant.IS_REFRESH, true);
				editor.commit();
				Message msg = mHandler.obtainMessage();
				msg.what = 2;
				mHandler.sendMessage(msg);
			}
		}
		
		if(count >=7){
			timer.cancel();
			count=0;
		}
	}

}
