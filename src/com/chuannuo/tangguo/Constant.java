package com.chuannuo.tangguo;

/**
 * @author alan.xie
 * @date 2015年4月17日 下午3:19:57
 * @Description: 常量
 */
public class Constant {
	
	/**   
	 * @Fields DOWNLOAD_DIR : app下载路径   
	 */
	public static final String DOWNLOAD_DIR = "tangguo/download/";
	public static final String IMG_DIR = "tangguo/download/image/";
	/**   
	 * @Fields PREF_QIANBAO_PAY : sharedPreferenced,文件名   
	 */
	public static final String PREF_QIANBAO_SDK = "qianbaosuopingsdk";
	//public static final String PREF_TANGGUO_DATA = "tangguodata";
	
	/**   
	 * @Fields PHONE_NUMBER : 记录电话号码，判断用户是否登陆过   
	 */
	public static final String PHONE_NUMBER = "phone";
	public static final String APP_ID = "appId";
	public static final String CODE = "code";
	public static final String METADATA = "metAData";
	public static final String ITEM = "item";
	public static final String DOWNLOAD_APP_TIME = "downLoadAppTime";//用户下载app的当前时间
	public static final String DOWNLOAD_TIMES = "downLoadTimes"; //每天能下载多少次钱包锁屏app
	public static final String DOWN_TIME = "downLoadTime";//记录下载时间，超过一天可重新下载
	public static final String APP_RUNNING_TIME = "appRunningTime";//app运行时间
	//public static final String APP_SIGN_IS_SUCCESS = "appSignIsSuccess"; //app签到是否成功，0没开始签到，1未完成的签到，2签到成功
	public static final String VC_PRICE = "vcPrice";
	public static final String IS_SHOW = "isShow";
	public static final String TEXT_NAME = "textName";
	public static final String IS_REFRESH = "isRefresh";
	public static final String IP = "ip";
	public static final String ISP = "isp";
	public static final String CITY = "city";
	
	/**   
	 * @Fields NET_ERROR : 网络返回错误   
	 */
	public static final String NET_ERROR = "0";
	public static final int ACCESS_SUCCESS = 1;
	public static final int ACCESS_FAILURE = 0;
	public static final String CODING = "utf8";
	public static final String RESOURCE_ID = "resourceId";
	public static final String S_RESOURCE_ID = "sResourceId";
	public static final String TANGGUO_APPKEY = "TANGGUO_APPKEY";
	public static final String TANGGUO_APPID = "TANGGUO_APPID";
	public static final String IS_FIRST_IN = "IS_FIRST_IN";
	public static final String IS_REPORT = "IS_REPORT";
	public static final String IS_SIGN = "ISSIGN";
	public static final int STEP_1 = 1; 
	public static final int STEP_2 = 2;
	//public static final int STEP_3 = 3;
	
	/**
	 * @author alan.xie
	 * @date 2015年4月17日 下午3:20:23
	 * @Description: 服务器接口
	 */
	public class URL{
		
		public static final String ROOT_URL = "http://apk.jiequbao.com";
		public static final String SCREEN_SHOT_RUL = "http://m.baidu.com/s?word=";
		
		/** 
		* @Fields BASE_URL : 网站根目录 
		*/
		public static final String BASE_URL = ROOT_URL+"/index.php?r=qianbao/";
		public static final String ADINSTALL_URL = BASE_URL + "adInstall";             //过滤已经下载安装过的app
		public static final String CONFIRM_INSTALL_INTEGRAL = BASE_URL + "confirmInstallIntegral";//确认应用安装 添加积分
		public static final String REPEAT_SIGN_URL = BASE_URL + "reportSign";//深度任务 签到
		public static final String UN_FINISHED_TASK = BASE_URL + "unfinishedSignList";//任务签到
		//public static final String RESOURCE_URL = BASE_URL + "getResourceList";
		public static final String CREATE_USER = BASE_URL + "signup";
		public static final String REPORT_URL = BASE_URL + "reportPackageNames";
		public static final String USER_INFO_URL = BASE_URL + "userInfo";
		public static final String EXCHANGE_URL = BASE_URL + "exchangeIntegral";
		public static final String EXCHANGE_ADD_URL = BASE_URL + "exchangeAddIntegral";
		
		public static final String DOWNLOAD_URL = BASE_URL+"getResourceListHtml"; //截图任务接口
		public static final String UPLOADS_PHOTO = BASE_URL +"uploadsPhotoHtmls";//上传图片
		
		public static final String GET_AD_ALERT = BASE_URL + "getUserAdAlert";//图片审核
		public static final String MOD_AD_ALERT = BASE_URL + "modifyUserAdAlert";//图片审核
		public static final String RESOURCE_PHOTO = BASE_URL + "resourcePhoto";//图片列表
		public static final String APPEAL = BASE_URL + "appeal";//申诉
	}
	
	/**
	 * @author alan.xie
	 * @date 2015年4月17日 下午3:23:49
	 * @Description: 字符常量，string.xml
	 */
	public class StringValues{
		public static final String APP_NAME = "钱包夺宝";
		public static final String TITLE = "精品应用推荐";
		public static final String ADD_SCORE_SUCCESS = "增加积分成功";
		public static final String RECOMM_TASK = "任务列表";
		public static final String UNFINISHED_TASK = "未完成任务";
		public static final String APP_DETAIL = "应用详情";
		public static final String LOADING = "数据加载中...";
		public static final String UPLODING = "正在上传...";
		public static final String COMPRESS = "正在压缩...";
		public static final String IMM_DOWN = "立即下载";
		public static final String DEPTH_TIPS = "亲！您还木有未完成的任务。";
	}
	
	/**
	 * @author alan.xie
	 * @date 2015年4月17日 下午3:23:46
	 * @Description: 颜色值，color.xml
	 */
	public class ColorValues{
		
		public static final String WHITE = "#ffffffff";
		public static final String TRANSPARENT = "#00000000";
		public static final String BTN_NORMAL_COLOR = "#ffef4136";//"#ffee7c1b";
		public static final String BTN_PRESSED_COLOR = "#ffc63c26";
		public static final String FONT_COLOR = "#ff7b7b7b";
		public static final String TIPS_BACK_COLOR = "#fffeeeed";
		public static final String LINK_COLOR = "#ff0000ff";
		public static final String DIVIDER_COLOR = "#ffececec";
		public static final String TITLE_COLOR = "#ff58616d";
		public static final String SIZE_COLOR = "#ff888888";
		public static final String YELLOW_BACK_COLOR = "#ffFEF5CC";
		public static final String HOW_TO_DO = "#ffE18165";
		public static final String GREEN_THEME = "#ff2dcc70";
		public static final String LIGHT_RED = "#ffff5252";
		public static final String BLUE = "#ff12ABCB";
		public static final String UPLOAD_IMG_BCAK = "#ffff7464";
	}
	
	/**
	 * @author alan.xie
	 * @date 2015年4月17日 下午4:30:56
	 * @Description: view id
	 */
	public class IDValues{
		public static final int TV_RECOMM = 0x7f040001;
		public static final int TV_GAME = 0x7f040002;
		public static final int TV_DEPTH = 0x7f040003;
		public static final int LV_RECOMM = 0x7f040004;
		public static final int LV_GAME = 0x7f040005;
		public static final int LV_DEPTH = 0x7f040006;
		public static final int IV_LOGO = 0x7f040007;
		public static final int TV_APP_NAME = 0x7f040008;
		public static final int CONTAINER = 0x7f040009;
		public static final int TV_APP_SIZE = 0x7f04000a;
		public static final int TV_APP_DESC = 0x7f04000b;
		public static final int TV_SCORE = 0x7f04000c;
		public static final int LL1 = 0x7f04000d;
		public static final int BACK = 0x7f04000e;
		public static final int D_LOGO = 0x7f04000f;
		public static final int D_TITLE = 0x7f040010;
		public static final int D_SIZE = 0x7f040011;
		public static final int D_SCORE = 0x7f040012;
		public static final int D_TIPS1 = 0x7f040013;
		public static final int D_TIPS2 = 0x7f040014;
		public static final int D_TIPS3 = 0x7f040015;
		public static final int D_HOW_DO = 0x7f040016;
		public static final int D_DESC = 0x7f040017;
		public static final int D_DOWN = 0x7f040018;
		public static final int SIGN = 0x7f040019;
		public static final int SIGN_RELUS = 0x7f04001a;
		public static final int SIGN_TIMES = 0x7f04001b;
		public static final int ALERT = 0x7f04001c;
	}
	

}
