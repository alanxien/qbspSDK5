package com.chuannuo.tangguo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chuannuo.tangguo.net.RequestParams;
import com.chuannuo.tangguo.net.TGHttpResponseHandler;

/**
 * TODO<请描述这个类是干什么的>
 * 
 * @author xie.xin
 * @data: 2015-7-1 下午10:56:19
 * @version: V1.0
 */
public class TangGuoActivity extends FragmentActivity implements
		OnClickListener, BaseFragment.BtnClickListener {

	private LinearLayout rootLinearLayout;
	private FrameLayout fLinearLayout;
	private LinearLayout rLinearLayout;
	private LinearLayout dLinearLayout;
	private TextView tv1;
	private TextView tv2;
	private TextView tvRecomm;
	private TextView tvDepth;
	private TextView tvTitle;
	private ImageView ivBack;
	private ProgressDialog progressDialog;

	private LinearLayout containerLayout;

	private FragmentRecomm fragmentRecomm;
	private FragmentDepth fragmentDepth;
	private FragmentDownLoad fragmentDownLoad;
	private FragmentManager mFragmentManager;

	private String color = Constant.ColorValues.BTN_NORMAL_COLOR;
	public static Drawable icon;
	private AlertDialog aDialog;
	private AlertDialog.Builder mAlertDialog;
	protected SharedPreferences pref;
	protected Editor editor;
	private DisplayMetrics dm;
	private File imgeDir = null;
	private File imageFile = null;
	private List<TGData> dataList = new ArrayList<TGData>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (null == pref) {
			pref = getSharedPreferences(Constant.PREF_QIANBAO_SDK,
					Context.MODE_PRIVATE);
		}
		if (null == editor) {
			editor = pref.edit();
		}
		initView();
		setContentView(this.rootLinearLayout);
		try {
			icon = this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0).applicationInfo
					.loadIcon(getPackageManager());
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PhoneInformation.initTelephonyManager(this);
		mFragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		fragmentRecomm = new FragmentRecomm();
		fragmentDepth = new FragmentDepth();

		if (PhoneInformation.isSimReady()) {
			transaction.add(Constant.IDValues.CONTAINER, fragmentRecomm);
			transaction.commit();
		} else {
			Toast.makeText(this, "请插入SIM卡", Toast.LENGTH_SHORT).show();
		}

		imageCheckAlert();
	}

	/**
	 * @Title: imageCheckAlert
	 * @Description: 图片审核
	 * @param
	 * @return void
	 * @throws
	 */
	private void imageCheckAlert() {
		mAlertDialog = new AlertDialog.Builder(this).setTitle("图片审核信息")
				.setPositiveButton("知道了", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		
		RequestParams params = new RequestParams();
		params.put("app_id", pref.getString(Constant.APP_ID, "0"));
		HttpUtils.get(Constant.URL.GET_AD_ALERT, params,
				new TGHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, content);
						JSONObject response = null;
						try {
							response = new JSONObject(content);
							if (response.getString("code").equals("1")) {
								dataList.clear();
								JSONObject obj = response.getJSONObject("data");
								if (obj != null) {
									JSONArray passArray = obj.getJSONArray("pass");
									JSONArray failArray = obj.getJSONArray("fail");
									JSONObject passObject;
									JSONObject failObject;
									String passInfo = "";
									String failInfo = "";
									String ids ="";
									
									DecimalFormat df = new DecimalFormat("0.00");//格式化小数
									df.setRoundingMode(RoundingMode.DOWN);
									if(passArray !=null && !passArray.equals("[]") && passArray.length()>0){
										for(int i=0; i<passArray.length();i++){
											TGData data = new TGData();
											passObject = passArray.getJSONObject(i);
											if(passObject != null){
												double money = passObject.getInt("photo_integral")*Double.parseDouble(pref.getString(Constant.VC_PRICE, "1"));
												passInfo = passInfo + passObject.getString("title")+" （审核通过）  +"+money+pref.getString(Constant.TEXT_NAME, "积分")+"\n";
												ids = ids+passObject.getInt("ad_install_id")+",";
												
												data.setPass(true);
												data.setRemarks("");
												data.setScore(money);
												data.setTitle(passObject.getString("title"));
												dataList.add(data);
											}
											
										}
										
									}
									
									if(failArray !=null && !failArray.equals("[]") && failArray.length()>0){
										for(int i=0; i<failArray.length();i++){
											TGData data = new TGData();
											failObject = failArray.getJSONObject(i);
											if(failObject != null){
												failInfo = failInfo + failObject.getString("title")+failObject.getString("photo_remarks")+" （审核失败）\n";
												ids = ids+failObject.getInt("ad_install_id")+",";
												data.setPass(false);
												data.setRemarks(failObject.getString("photo_remarks"));
												data.setScore(0.0);
												data.setTitle(failObject.getString("title"));
												dataList.add(data);
											}
											
										}
										
									}

									if(passInfo.length() > 0 || failInfo.length()>0){
										mAlertDialog.setMessage(passInfo+failInfo);
										mAlertDialog.show();
									}
									if(ids.length() > 0){
										Message msg = mHandler.obtainMessage();
										msg.what = 1;
										msg.obj = ids.substring(0,ids.length()-1);;
										mHandler.sendMessage(msg);
									}
									
								}
								
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(Throwable error, String content) {
						// TODO Auto-generated method stub
						super.onFailure(error, content);
					}
				});
	}

	private void initView() {
		Intent intent = getIntent();
		String c = intent.getExtras().getString("color");
		if (c != null && !c.equals("")) {
			color = c;
		}

		rootLinearLayout = new LinearLayout(this);
		containerLayout = new LinearLayout(this);
		fLinearLayout = new FrameLayout(this);
		rLinearLayout = new LinearLayout(this);
		dLinearLayout = new LinearLayout(this);
		initRLinearLayout();
		initDLinearLayout();
		initHeadLinearLayout();
		initContainerLayout();
		initRootLinearLayout();

	}

	/**
	 * @Title: initRLinearLayout
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initRLinearLayout() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		rLinearLayout.setLayoutParams(lp);
		rLinearLayout.setPadding(0, 12, 0, 12);
		rLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		rLinearLayout.setBackgroundColor(Color
				.parseColor(Constant.ColorValues.BLUE));

		tvRecomm = new TextView(this);
		tvDepth = new TextView(this);

		LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tlp.weight = 1;
		tvRecomm.setLayoutParams(tlp);
		tvRecomm.setPadding(10, 0, 10, 0);
		tvRecomm.setGravity(Gravity.CENTER);
		tvRecomm.setTextSize(15);
		tvRecomm.setText(Constant.StringValues.RECOMM_TASK);
		tvRecomm.setTextColor(Color.parseColor(Constant.ColorValues.WHITE));
		tvRecomm.setId(Constant.IDValues.TV_RECOMM);
		tvRecomm.setOnClickListener(this);

		tvDepth.setLayoutParams(tlp);
		tvDepth.setPadding(10, 0, 10, 0);
		tvDepth.setGravity(Gravity.CENTER);
		tvDepth.setTextSize(15);
		tvDepth.setText(Constant.StringValues.UNFINISHED_TASK);
		tvDepth.setTextColor(Color.parseColor(Constant.ColorValues.WHITE));
		tvDepth.setId(Constant.IDValues.TV_DEPTH);
		tvDepth.setOnClickListener(this);

		rLinearLayout.addView(tvRecomm);
		rLinearLayout.addView(tvDepth);
	}

	/**
	 * @Title: initDLinearLayout
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initDLinearLayout() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		dLinearLayout.setLayoutParams(lp);
		dLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		dLinearLayout.setBackgroundColor(Color
				.parseColor(Constant.ColorValues.BLUE));

		tv1 = new TextView(this);
		tv2 = new TextView(this);

		LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, 6);
		tlp.weight = 1;
		tv1.setLayoutParams(tlp);
		tv2.setLayoutParams(tlp);
		tv1.setBackgroundColor(Color
				.parseColor(Constant.ColorValues.BTN_NORMAL_COLOR));
		tv2.setBackgroundColor(Color.parseColor(Constant.ColorValues.BLUE));

		dLinearLayout.addView(tv1);
		dLinearLayout.addView(tv2);

	}

	/**
	 * @Title: initHeadLinearLayout
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initHeadLinearLayout() {
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		fLinearLayout.setLayoutParams(lp);
		fLinearLayout.setPadding(13, 13, 13, 13);
		fLinearLayout.setBackgroundColor(Color.parseColor(color));

		tvTitle = new TextView(this);
		ivBack = new ImageView(this);

		LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		ivBack.setLayoutParams(ilp);
		ivBack.setPadding(20, 0, 20, 0);
		ivBack.setId(Constant.IDValues.BACK);
		ivBack.setImageBitmap(ResourceUtil.getImageFromAssetsFile(this,
				"back.png"));
		ivBack.setOnClickListener(this);

		LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tvTitle.setLayoutParams(tlp);
		tvTitle.setTextSize(17);
		tvTitle.setPadding(10, 10, 10, 10);
		tvTitle.setGravity(Gravity.CENTER);
		tvTitle.setTextColor(Color.parseColor(Constant.ColorValues.WHITE));
		tvTitle.setText(Constant.StringValues.TITLE);

		fLinearLayout.addView(ivBack);
		fLinearLayout.addView(tvTitle);
	}

	/**
	 * @Title: initRootLinearLayout
	 * @Description: 初始化跟布局
	 * @param
	 * @return void
	 * @throws
	 */
	private void initRootLinearLayout() {
		LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		rootLinearLayout.setOrientation(LinearLayout.VERTICAL);
		rootLinearLayout.setLayoutParams(rlp);
		rootLinearLayout.setBackgroundColor(Color
				.parseColor(Constant.ColorValues.WHITE));
		rootLinearLayout.addView(fLinearLayout);
		rootLinearLayout.addView(rLinearLayout);
		rootLinearLayout.addView(dLinearLayout);
		rootLinearLayout.addView(containerLayout);
	}

	/**
	 * @Title: initContainerLayout
	 * @Description: fragment容器
	 * @param
	 * @return void
	 * @throws
	 */
	private void initContainerLayout() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		containerLayout.setId(Constant.IDValues.CONTAINER);
		containerLayout.setOrientation(LinearLayout.VERTICAL);
		containerLayout.setLayoutParams(lp);
	}

	@Override
	public void onClick(View v) {
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		switch (v.getId()) {
		case Constant.IDValues.TV_RECOMM:
			tv1.setBackgroundColor(Color
					.parseColor(Constant.ColorValues.BTN_NORMAL_COLOR));
			tv2.setBackgroundColor(Color.parseColor(Constant.ColorValues.BLUE));
			if (PhoneInformation.isSimReady()) {
				if (!fragmentRecomm.isAdded()) { // 先判断是否被add过
					transaction.hide(fragmentDepth)
							.add(Constant.IDValues.CONTAINER, fragmentRecomm)
							.commit();
				} else {
					transaction.hide(fragmentDepth).show(fragmentRecomm)
							.commit();
				}
			} else {
				Toast.makeText(this, "请插入SIM卡", Toast.LENGTH_SHORT).show();
			}

			break;
		case Constant.IDValues.TV_DEPTH:
			tv1.setBackgroundColor(Color.parseColor(Constant.ColorValues.BLUE));
			tv2.setBackgroundColor(Color
					.parseColor(Constant.ColorValues.BTN_NORMAL_COLOR));

			if (PhoneInformation.isSimReady()) {
				if(pref.getBoolean(Constant.IS_REFRESH, false)){
					fragmentDepth = new FragmentDepth();
				}
				
				if (!fragmentDepth.isAdded()) { // 先判断是否被add过
					transaction.hide(fragmentRecomm)
							.add(Constant.IDValues.CONTAINER, fragmentDepth)
							.commit();
				} else {
					transaction.hide(fragmentRecomm).show(fragmentDepth)
							.commit();
					fragmentDepth.refresh();
				}
			} else {
				Toast.makeText(this, "请插入SIM卡", Toast.LENGTH_SHORT).show();
			}
			editor.putBoolean(Constant.IS_REFRESH, false);
			editor.commit();
			break;
		case Constant.IDValues.BACK:
			if (rLinearLayout.getVisibility() == View.GONE) {
				mFragmentManager.popBackStack();
				if (!pref.getBoolean(Constant.IS_SIGN, false)) {
					fragmentRecomm.refreshData();
				} else {
					fragmentDepth.refreshData();
				}
				rLinearLayout.setVisibility(View.VISIBLE);
				dLinearLayout.setVisibility(View.VISIBLE);
				tvTitle.setText(Constant.StringValues.TITLE);
			} else {
				this.finish();
			}

			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (fragmentDownLoad != null
					&& fragmentDownLoad.popupWindow != null
					&& fragmentDownLoad.popupWindow.isShowing()) {
				fragmentDownLoad.popupWindow.dismiss();
				return true;
			} else if (rLinearLayout.getVisibility() == View.GONE) {
				if (!pref.getBoolean(Constant.IS_SIGN, false)) {
					fragmentRecomm.refreshData();
				} else {
					fragmentDepth.refreshData();
				}
				rLinearLayout.setVisibility(View.VISIBLE);
				dLinearLayout.setVisibility(View.VISIBLE);
				tvTitle.setText(Constant.StringValues.TITLE);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBtnClickListener(int step, AppInfo appInfo) {
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		switch (step) {
		case Constant.STEP_1:
			fragmentDownLoad = new FragmentDownLoad();
			transaction.addToBackStack(null);
			Bundle bundle = new Bundle();
			bundle.putSerializable("item", appInfo);
			fragmentDownLoad.setArguments(bundle);
			// transaction.replace(Constant.IDValues.CONTAINER,
			// fragmentDownLoad);
			// transaction.commit();
			transaction.hide(fragmentRecomm)
					.add(Constant.IDValues.CONTAINER, fragmentDownLoad)
					.commit();

			rLinearLayout.setVisibility(View.GONE);
			dLinearLayout.setVisibility(View.GONE);
			tvTitle.setText(Constant.StringValues.APP_DETAIL);
			break;
		case Constant.STEP_2:
			fragmentDownLoad = new FragmentDownLoad();
			transaction.addToBackStack(null);
			Bundle bundle1 = new Bundle();
			bundle1.putSerializable("item", appInfo);
			fragmentDownLoad.setArguments(bundle1);
			transaction.hide(fragmentDepth)
					.add(Constant.IDValues.CONTAINER, fragmentDownLoad)
					.commit();

			rLinearLayout.setVisibility(View.GONE);
			dLinearLayout.setVisibility(View.GONE);
			tvTitle.setText(Constant.StringValues.APP_DETAIL);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && data != null) {

			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			final String picturePath = cursor.getString(columnIndex);
			cursor.close();
			ImageView view = new ImageView(this);
			try {
				view.setImageBitmap(MediaStore.Images.Media.getBitmap(
						this.getContentResolver(), selectedImage));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			aDialog = new AlertDialog.Builder(this)
					.setTitle("确认上传这张图片？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 点击“确认”后的上传图片
									uploadFile(picturePath);
								}
							})
					.setNegativeButton("返回",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 点击“返回”
									aDialog.dismiss();
								}
							}).setView(view).show();
		}
	}

	/**
	 * @Title: uploadFile
	 * @Description: TODO
	 * @author xie.xin
	 * @param
	 * @return void
	 * @throws
	 */
	private void uploadFile(String srcPath) {
		AppInfo appInfo = fragmentDownLoad.appInfo;
		if (appInfo != null) {
			if (null == progressDialog) {
				progressDialog = new ProgressDialog(this);
			}
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage(Constant.StringValues.COMPRESS);
			progressDialog.show();

			File file = compress(srcPath, appInfo.getResource_id());
			progressDialog.setMessage(Constant.StringValues.UPLODING);
			if (appInfo.getClicktype() == 1) {
				RequestParams params = new RequestParams();
				try {
					params.put("photo", file);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				params.put("app_id", pref.getString(Constant.APP_ID, "0"));
				params.put("code", pref.getString(Constant.CODE, "0"));
				params.put("key", PhoneInformation.getMetaData(this,
						Constant.TANGGUO_APPKEY));
				params.put("channel_id", PhoneInformation.getMetaData(this,
						Constant.TANGGUO_APPID));
				PhoneInformation.initTelephonyManager(this);
				params.put("imei", PhoneInformation.getImei());
				params.put("imsi", PhoneInformation.getImsi());
				params.put("machineType", PhoneInformation.getMachineType());
				params.put("net_type", PhoneInformation.getNetType() + "");
				params.put("macaddress", PhoneInformation.getMacAddress());
				params.put("androidid", Secure.getString(
						this.getContentResolver(), Secure.ANDROID_ID));
				params.put("resource_id", appInfo.getResource_id() + "");
				params.put("ad_id", appInfo.getAdId() + "");
				params.put("ip", pref.getString(Constant.IP, "0.0.0.0"));
				if (TangGuoWall.getUserId() == null) {
					params.put("app_user_id", TangGuoWall.getUserId() + "");
				} else {
					params.put("app_user_id", TangGuoWall.getUserId() + "");
				}

				upLoading(Constant.URL.UPLOADS_PHOTO_H5, params);
			} else {
				RequestParams params = new RequestParams();
				try {
					params.put("photo", file);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				params.put("ad_install_id", appInfo.getInstall_id() + "");
				params.put("ip", pref.getString(Constant.IP, "0.0.0.0"));
				params.put("code", pref.getString(Constant.CODE, ""));

				upLoading(Constant.URL.UPLOADS_PHOTO, params);
			}
		}

	}

	private void upLoading(String url, RequestParams params) {
		// TODO Auto-generated method stub
		// 上传文件
		HttpUtils.post(url, params, new TGHttpResponseHandler() {

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
					if (obj != null && obj.getInt("code") == 1) {
//						if (fragmentDownLoad.appInfo.isSign()) {
//							editor.putInt(Constant.S_RESOURCE_ID,
//									fragmentDownLoad.appInfo.getResource_id());
//						} else {
//							editor.putInt(Constant.RESOURCE_ID,
//									fragmentDownLoad.appInfo.getResource_id());
//						}
						//editor.commit();

						progressDialog.dismiss();
						//fragmentDownLoad.linearLayout9.setVisibility(View.GONE);
						Toast.makeText(TangGuoActivity.this, "图片上传成功",
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(TangGuoActivity.this,
								"图片上传失败！" + obj.getString("info"),
								Toast.LENGTH_LONG).show();
						progressDialog.dismiss();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
				progressDialog.dismiss();
				Toast.makeText(TangGuoActivity.this, "当前网络不佳，图片上传失败。",
						Toast.LENGTH_LONG).show();
			}
		});
	}

	/**
	 * @Title: compress
	 * @Description: 图片压缩 50K
	 * @param @param srcPath
	 * @return void
	 * @throws
	 */
	public File compress(String srcPath, int rId) {
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			imgeDir = new File(Environment.getExternalStorageDirectory(),
					Constant.IMG_DIR);
			imageFile = new File(imgeDir.getPath(), System.currentTimeMillis()
					+ "_" + rId + ".jpg");
		}

		if (!imgeDir.exists()) {
			imgeDir.mkdirs();
		}
		if (!imageFile.exists()) {
			try {
				imageFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		float hh = dm.heightPixels;
		float ww = dm.widthPixels;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, opts);
		opts.inJustDecodeBounds = false;
		int w = opts.outWidth;
		int h = opts.outHeight;
		int size = 0;
		if (w <= ww && h <= hh) {
			size = 1;
		} else {
			double scale = w >= h ? w / ww : h / hh;
			double log = Math.log(scale) / Math.log(2);
			double logCeil = Math.ceil(log);
			size = (int) Math.pow(2, logCeil);
		}
		opts.inSampleSize = size;
		bitmap = BitmapFactory.decodeFile(srcPath, opts);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int quality = 100;
		bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
		System.out.println(baos.toByteArray().length);
		while (baos.toByteArray().length > 45 * 1024 && quality>0) {
			baos.reset();
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
			quality -= 20;
			System.out.println(baos.toByteArray().length);
		}
		try {
			baos.writeTo(new FileOutputStream(imageFile));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				baos.flush();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return imageFile;
	}

	Handler mHandler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if(msg.obj  != null){
					modifyAdAlert(msg.obj);
				}
				break;
			default:
				break;
			}
		};
	};
	
	private void modifyAdAlert(Object obj){
		String adIds = (String) obj;
		RequestParams p = new RequestParams();
		p.put("app_id", pref.getString(Constant.APP_ID, "0"));
		p.put("ad_install_id_list", adIds);
		HttpUtils.post(Constant.URL.MOD_AD_ALERT, p, new TGHttpResponseHandler(){
			/* (non-Javadoc)
			 * @see com.chuannuo.tangguo.net.TGHttpResponseHandler#onSuccess(int, org.apache.http.Header[], java.lang.String)
			 */
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					String content) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, content);
				JSONObject obj = null;
				try {
					obj = new JSONObject(content);
					if (obj!=null && obj.getString("code").equals("1")) {
						//回调
						if(TangGuoWall.tangGuoWallListener != null){
							TangGuoWall.tangGuoWallListener.onUploadImgs(dataList);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
	}
}
