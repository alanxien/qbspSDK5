/**
 * 
 */
package com.chuannuo.tangguo;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;

/**
 * @author alan.xie
 * @date 2015-5-4 上午11:26:27 
 * @Description: TODO
 */
public class ResourceUtil {


	/** 
	* @author alan.xie 
	* @date 2015年4月20日 上午11:26:58
	* @Description: 读取assets图片文件 
	* @param @param fileName
	* @param @return
	* @return Bitmap
	* @throws 
	*/
	public static Bitmap getImageFromAssetsFile(Context context,String fileName)  
	  {  
	      Bitmap image = null;  
	      AssetManager am = context.getResources().getAssets();  
	      try  
	      {  
	          InputStream is = am.open("qianBaoRes/"+fileName);  
	          image = BitmapFactory.decodeStream(is);  
	          is.close();  
	      }  
	      catch (IOException e)  
	      {  
	          e.printStackTrace();  
	      }  
	  
	      return image;  
	  
	  } 
	
    public static Bitmap drawable2Bitmap(Drawable drawable) {  
        if (drawable instanceof BitmapDrawable) {  
            return ((BitmapDrawable) drawable).getBitmap();  
        } else if (drawable instanceof NinePatchDrawable) {  
            Bitmap bitmap = Bitmap  
                    .createBitmap(  
                            drawable.getIntrinsicWidth(),  
                            drawable.getIntrinsicHeight(),  
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
                                    : Bitmap.Config.RGB_565);  
            Canvas canvas = new Canvas(bitmap);  
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),  
                    drawable.getIntrinsicHeight());  
            drawable.draw(canvas);  
            return bitmap;  
        } else {  
            return null;  
        }  
    } 
	
	/**
	 * @author xin.xie
	 * @date 2015-5-16 下午4:15:18 
	 * @Description: 获取资源图片
	 * @param paramContext
	 * @param paramString
	 * @return
	 * @return int
	 * @throws
	 */ 
	public static int getDrawableId(Context paramContext, String paramString) { 
        return paramContext.getResources().getIdentifier(paramString, 
                "drawable", paramContext.getPackageName()); 
    } 
}
