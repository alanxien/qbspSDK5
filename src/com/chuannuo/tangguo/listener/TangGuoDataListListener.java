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
import java.util.Map;

/** 
 * TODO<请描述这个类是干什么的> 
 * @author  xie.xin 
 * @data:  2015-12-3 下午10:32:21 
 * @version:  V1.0 
 */
public interface TangGuoDataListListener {

	/** 
	* @Title: getAdList 
	* @Description: TODO
	* @author  xie.xin
	* @param @param status
	* @param @param list
	* @return void 
	* @throws 
	*/
	void getAdList(int status,List<Object> list);
	void getDeptList(int status,List<Object> list);
}
