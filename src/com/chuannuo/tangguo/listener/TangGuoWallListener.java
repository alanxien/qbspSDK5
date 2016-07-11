/* 
 * @Title:  TangGuoWallListener.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2015-7-29 上午2:08:52 
 * @version:  V1.0 
 */
package com.chuannuo.tangguo.listener;

import java.util.List;

import com.chuannuo.tangguo.TGData;

/** 
 * TODO<请描述这个类是干什么的> 
 * @author  xie.xin 
 * @data:  2015-7-29 上午2:08:52 
 * @version:  V1.0 
 */
public interface TangGuoWallListener {

	/** 
	* @Title: onAddScoreSuccess 
	* @Description: TODO
	* @param @param appName
	* @param @param score
	* @return void 
	* @throws 
	*/
	void onAddPoint(int status,String appName,int point);
	void onSign(int status,String appName,int point);
	void onUploadImgs(List<TGData> dataList);
}
