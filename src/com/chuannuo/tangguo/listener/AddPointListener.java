/* 
 * @Title:  TangGuoWallListener.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2015-7-29 上午2:08:52 
 * @version:  V1.0 
 */
package com.chuannuo.tangguo.listener;

/** 
 * TODO<请描述这个类是干什么的> 
 * @author  xie.xin 
 * @data:  2015-7-29 上午2:08:52 
 * @version:  V1.0 
 */
public interface AddPointListener {

	/** 
	* @Title: onAddScoreSuccess 
	* @Description: TODO
	* @param @param appName
	* @param @param score
	* @return void 
	* @throws 
	*/
	void addPoint(int status,int point);
}
