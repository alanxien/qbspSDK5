package com.chuannuo.tangguo;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chuannuo.tangguo.imageLoader.ImageLoader;

public class RecommendTaskAdapter extends BaseAdapter {

	public static String TAG = "RecommendAdapter";

	private ArrayList<AppInfo> infoList;
	private Context context;
	private AppInfo appInfo; // 钱包锁屏资源

	private RelativeLayout relativeLayout;
	private ImageView imageView;
	private LinearLayout linearLayout;
	private LinearLayout lLayout;
	private TextView tvTitle;
	private TextView tvSize;
	private TextView tvDesc;
	private TextView tvScore;
	private TextView tvAlert;

	private ListView mListView;
	private ImageLoader mImageLoader;

	public RecommendTaskAdapter(Context context, ArrayList<AppInfo> list,
			ListView listView) {
		this.context = context;
		this.infoList = list;
		this.mListView = listView;
		mImageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.infoList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return infoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = getConverView();

			holder = new ViewHolder();
			holder.app_name = tvTitle;
			holder.app_icon = imageView;
			holder.app_desc = tvDesc;
			holder.app_xb = tvScore;
			holder.app_size = tvSize;
			holder.alert = tvAlert;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		appInfo = this.infoList.get(position);

		if (appInfo.getIsShow()==0) {
			holder.app_xb.setVisibility(View.VISIBLE);
			holder.app_xb.setText("赚" + appInfo.getTotalScore()
					+ appInfo.getTextName());
		} else {
			holder.app_xb.setVisibility(View.GONE);
		}

		holder.app_name.setText(appInfo.getTitle());
		holder.app_desc.setText(appInfo.getDescription());
		holder.alert.setText(appInfo.getAlert());

		holder.app_size.setText(appInfo.getResource_size() + "M");
		String url = this.infoList.get(position).getIcon();
		holder.app_icon.setTag(url);

		mImageLoader.loadImage(url, holder.app_icon, true,false);
		return convertView;
	}

	/**
	 * @Title: getConverView
	 * @Description: TODO
	 * @param @return
	 * @return View
	 * @throws
	 */
	private View getConverView() {
		relativeLayout = new RelativeLayout(context);
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		relativeLayout.setLayoutParams(lp);
		relativeLayout.setPadding(0, 10, 0, 10);

		imageView = new ImageView(context);
		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(130,
				130);
		lp1.setMargins(10, 0, 10, 10);
		imageView.setLayoutParams(lp1);
		imageView.setImageBitmap(ResourceUtil.getImageFromAssetsFile(context,
				"tangguo.png"));
		imageView.setId(Constant.IDValues.IV_LOGO);

		linearLayout = new LinearLayout(context);
		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lp2.leftMargin = 150;
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setLayoutParams(lp2);
		linearLayout.setId(Constant.IDValues.LL1);

		tvTitle = new TextView(context);
		LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(400,
				LayoutParams.WRAP_CONTENT);
		tvTitle.setLayoutParams(lp3);
		tvTitle.setTextSize(15);
		tvTitle.setTextColor(Color.parseColor(Constant.ColorValues.TITLE_COLOR));
		tvTitle.setText(Constant.StringValues.APP_NAME);
		tvTitle.setEllipsize(TruncateAt.END);
		tvTitle.setSingleLine(true);
		tvTitle.setId(Constant.IDValues.TV_APP_NAME);

		lLayout = new LinearLayout(context);
		
		tvSize = new TextView(context);
		LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		lLayout.setLayoutParams(lp2);
		
		tvSize.setLayoutParams(lp4);
		tvSize.setTextSize(15);
		tvSize.setTextColor(Color.parseColor(Constant.ColorValues.SIZE_COLOR));
		tvSize.setText("大小：6.8M");
		tvSize.setEllipsize(TruncateAt.END);
		tvSize.setSingleLine(true);
		tvSize.setId(Constant.IDValues.TV_APP_SIZE);
		
		tvAlert = new TextView(context);
		
		tvAlert.setLayoutParams(lp4);
		tvAlert.setTextSize(15);
		tvAlert.setTextColor(Color.parseColor(Constant.ColorValues.GREEN_THEME));
		tvAlert.setText("");
		tvAlert.setEllipsize(TruncateAt.END);
		tvAlert.setSingleLine(true);
		tvAlert.setPadding(20, 0, 0, 0);
		tvAlert.setId(Constant.IDValues.ALERT);
		
		lLayout.addView(tvSize);
		lLayout.addView(tvAlert);

		tvDesc = new TextView(context);
		tvDesc.setLayoutParams(lp4);
		tvDesc.setTextSize(15);
		tvDesc.setTextColor(Color.parseColor(Constant.ColorValues.SIZE_COLOR));
		tvDesc.setText("描述");
		tvDesc.setEllipsize(TruncateAt.END);
		tvDesc.setSingleLine(true);
		tvDesc.setId(Constant.IDValues.TV_APP_DESC);

		linearLayout.addView(tvTitle);
		linearLayout.addView(lLayout);
		linearLayout.addView(tvDesc);

		tvScore = new TextView(context);
		RelativeLayout.LayoutParams lp5 = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp5.rightMargin = 10;
		lp5.addRule(RelativeLayout.ALIGN_TOP, Constant.IDValues.LL1);
		lp5.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		tvScore.setLayoutParams(lp5);
		tvScore.setTextSize(17);
		tvScore.setTextColor(Color
				.parseColor(Constant.ColorValues.BTN_NORMAL_COLOR));
		tvScore.setText("赚 2000 积分");
		tvScore.setEllipsize(TruncateAt.END);
		tvScore.setSingleLine(true);
		tvScore.setId(Constant.IDValues.TV_SCORE);

		relativeLayout.addView(imageView);
		relativeLayout.addView(linearLayout);
		relativeLayout.addView(tvScore);
		return relativeLayout;
	}

	class ViewHolder {
		public ImageView app_icon;
		public TextView app_name;
		public TextView app_desc;
		public TextView app_xb;
		public TextView app_size;
		public TextView alert;
	}
}
