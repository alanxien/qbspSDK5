/* 
 * @Title:  BaseFragment.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2015-7-18 上午1:45:07 
 * @version:  V1.0 
 */
package com.chuannuo.tangguo;


import com.chuannuo.tangguo.imageLoader.ImageLoader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

/** 
 * TODO<请描述这个类是干什么的> 
 * @author  xie.xin 
 * @data:  2015-7-18 上午1:45:07 
 * @version:  V1.0 
 */
public class BaseFragment extends Fragment {

	/**   
	 * @Fields rootLinearLayout : 根布局   
	 */
	public LinearLayout rootLinearLayout;
	public ProgressDialog progressDialog;
	public BtnClickListener mListener;
	
	protected SharedPreferences pref;
	protected Editor editor;
	protected ImageLoader mImageLoader;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		if(null == pref){
			pref = getActivity().getSharedPreferences(Constant.PREF_QIANBAO_SDK, Context.MODE_PRIVATE);
		}
		if(null == editor){
			editor = pref.edit();
		}
		mImageLoader = ImageLoader.getInstance();
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onAttach(Activity activity) {
		mListener = (BtnClickListener) activity;
		super.onAttach(activity);
	}

	/** 
	* @Title: getRootLinearLayout 
	* @Description: TODO
	* @param @return
	* @return LinearLayout 
	* @throws 
	*/
	public LinearLayout getRootLinearLayout() {
		rootLinearLayout = new LinearLayout(getActivity());
		LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		rootLinearLayout.setOrientation(LinearLayout.VERTICAL);
		rootLinearLayout.setLayoutParams(rlp);
		rootLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		//rootLinearLayout.setBackgroundColor(Color.parseColor(Constant.ColorValues.WHITE));
		return rootLinearLayout;
	}
	
	/**
	 * @author xin.xie
	 * @date 2015-5-12 下午9:03:33 
	 * @Description: 初始化加载框
	 * @return void
	 * @throws
	 */ 
	public void initProgressDialog(String msg){
		if(null == progressDialog){
			progressDialog = new ProgressDialog(getActivity());
		}
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(msg);
	}
	
	public interface BtnClickListener{
        public void onBtnClickListener(int step,AppInfo appInfo);
    }
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		if(progressDialog != null){
			progressDialog.dismiss();
		}
		super.onDestroy();
	}
}










