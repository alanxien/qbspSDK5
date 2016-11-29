package com.chuannuo.tangguo;

import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.chuannuo.tangguo.R;
import com.chuannuo.tangguo.listener.DownLoadListener;
import com.chuannuo.tangguo.listener.SignInListener;
import com.chuannuo.tangguo.listener.TangGuoDataListListener;
import com.chuannuo.tangguo.listener.TangGuoWallListener;
import com.chuannuo.tangguo.net.RequestParams;
import com.chuannuo.tangguo.net.TGHttpResponseHandler;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

public class MainActivity extends Activity implements TangGuoWallListener,
TangGuoDataListListener,DownLoadListener,SignInListener{

	Button btn_show_wall;
	Button btn_get_data_list;
	Button btn_get_dept_list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		 
		TangGuoWall.init(this,"123");
		btn_show_wall = (Button) findViewById(R.id.btn_show_wall);
		btn_get_data_list = (Button) findViewById(R.id.btn_get_data_list);
		btn_get_dept_list = (Button) findViewById(R.id.btn_get_dept_list);
		
		btn_show_wall.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// QbspWall.setColor("#aaABCB");
				TangGuoWall.initWall(MainActivity.this, "123");
				TangGuoWall.setTangGuoWallListener(MainActivity.this);
				TangGuoWall.show();
			}
		});
		
		btn_get_data_list.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TangGuoWall.setTangGuoDataListListener(MainActivity.this);
				TangGuoWall.getDataList();
			}
		});
		
		btn_get_dept_list.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TangGuoWall.setTangGuoDataListListener(MainActivity.this);
				TangGuoWall.getDeptList();
			}
		});
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chuannuo.tangguo.TangGuoWallListener#onAddScoreSuccess(java.lang.
	 * String, int)
	 */
	@Override
	public void onAddPoint(int status, String appName, int score) {
		if(status == 1){
			Toast.makeText(MainActivity.this, "体验应用成功"+score, Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(MainActivity.this, "体验应用失败"+score, Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chuannuo.tangguo.listener.TangGuoWallListener#onSign(int, int)
	 */
	@Override
	public void onSign(int status, String appName, int point) {
		if (status == 1) {
			Toast.makeText(this, "签到成功------"+point, Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(this, "签到失败------"+point, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void getAdList(int status, List<Object> list) {
		if (status == 1) {
			Toast.makeText(this, "数据获取成功------", Toast.LENGTH_LONG).show();
//			TangGuoWall.registerDownLoadListener(this);
//			AppInfo a = (AppInfo)list.get(2);
//			TangGuoWall.download(a);
		}else{
			Toast.makeText(this, "数据获取失败------", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void getDeptList(int status, List<Object> list) {
		if (status == 1) {
			Toast.makeText(this, "数据获取成功------", Toast.LENGTH_LONG).show();
			AppInfo a = (AppInfo) list.get(0);
			TangGuoWall.registerSignInListenter(this);
			TangGuoWall.signIn(a);
		}else{
			Toast.makeText(this, "数据获取失败------", Toast.LENGTH_LONG).show();
		}
	}

	/* (non-Javadoc)
	 * @see com.chuannuo.tangguo.listener.DownLoadListener#onDownloadStart(int)
	 */
	@Override
	public void onDownloadStart(int id) {
		Toast.makeText(this, "开始下载------", Toast.LENGTH_LONG).show();
	}

	/* (non-Javadoc)
	 * @see com.chuannuo.tangguo.listener.DownLoadListener#onDownloadSuccess(int)
	 */
	@Override
	public void onDownloadSuccess(int id) {
		Toast.makeText(this, "下载成功------", Toast.LENGTH_LONG).show();
	}

	/* (non-Javadoc)
	 * @see com.chuannuo.tangguo.listener.DownLoadListener#onDownloadFailed(int)
	 */
	@Override
	public void onDownloadFailed(int id) {
		Toast.makeText(this, "下载失败------", Toast.LENGTH_LONG).show();
	}

	/* (non-Javadoc)
	 * @see com.chuannuo.tangguo.listener.DownLoadListener#onInstallSuccess(int)
	 */
	@Override
	public void onInstallSuccess(int id) {
		Toast.makeText(this, "安装成功------", Toast.LENGTH_LONG).show();
	}

	/* (non-Javadoc)
	 * @see com.chuannuo.tangguo.listener.SignInListener#onSignIn(int, int)
	 */
	@Override
	public void onSignIn(int status, int id,double point) {
		if (status == 1) {
			Toast.makeText(this, "签到成功------"+point, Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(this, "签到失败------"+point, Toast.LENGTH_LONG).show();
		}
	}

	/* (non-Javadoc)
	 * @see com.chuannuo.tangguo.listener.TangGuoWallListener#onUploadImgs(java.util.List)
	 */
	@Override
	public void onUploadImgs(List<TGData> dataList) {
		if(dataList.size()>0){
			
		}
	}
	
	

}
