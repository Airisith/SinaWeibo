package com.airisith.Views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.airisith.airisith.R;
import com.airisith.modle.CommentsInfo;
import com.airisith.oauth.AccessTokenKeeper;
import com.airisith.util.AsyncImageLoader;
import com.airisith.util.CallbackImpl;
import com.airisith.util.CommentsAdapter;
import com.airisith.util.CommentsAdapter.HighlightComments;
import com.airisith.util.GetCommentsTask;
import com.airisith.util.ListViewUtility;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class WeiboInfoView extends ListActivity {

	/* 高亮参数 */
	private HighlightCm highlight;
	SpannableString spannableString;
	final String START = "start";
	final String END = "end";
	String str = "";
	final String TOPIC = "#.+?#";
	final String NAMEH = "@([\u4e00-\u9fa5A-Za-z0-9_]*)";
	final String URLH = "http://.*";

	private final String TAG = "WeiboInfoView";
	private TextView nameView;
	private TextView timestampView;
	private TextView textinfoView;
	private TextView reportsvView;
	private TextView commentsView;
	private TextView atttudesView;
	private ImageView capView;
	private ImageView imgView;

	private Long id, userId;

	private AsyncImageLoader loader = null;

	private Oauth2AccessToken token;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_weiboinfo);
		token = AccessTokenKeeper.readAccessToken(WeiboInfoView.this);
		loader = new AsyncImageLoader();
		
		nameView = (TextView) findViewById(R.id.nikename);
		timestampView = (TextView) findViewById(R.id.timestamp);
		textinfoView = (TextView) findViewById(R.id.text);
		reportsvView = (TextView) findViewById(R.id.reposts);
		commentsView = (TextView) findViewById(R.id.comments);
		atttudesView = (TextView) findViewById(R.id.like);
		capView = (ImageView) findViewById(R.id.cap);
		imgView = (ImageView) findViewById(R.id.img);

		// 高亮显示关键字接口
		highlight = new HighlightCm();

		// 获取信息
		Intent intent = getIntent();
		Bundle infoBundle = null;
		try {
			infoBundle = intent.getBundleExtra("weiboinfo");
		} catch (Exception e) {
		}

		try {
			String name = infoBundle.getString("name");
			str = infoBundle.getString("text");
			String time = infoBundle.getString("timestamp");
			String cap = infoBundle.getString("cap");
			nameView.setText(name);
			timestampView.setText(time);
			reportsvView.setText(infoBundle.getString("reports"));
			commentsView.setText(infoBundle.getString("comments"));
			atttudesView.setText(infoBundle.getString("attitudes"));

			// 文本做高亮处理
			spannableString = new SpannableString(str);
			heightLight(TOPIC, Color.BLUE);
			heightLight(NAMEH, Color.BLUE);
			heightLight(URLH, Color.BLUE);
			textinfoView.setText(spannableString);

			id = infoBundle.getLong("id");
			userId = infoBundle.getLong("userId");

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			String capUrl = infoBundle.getString("cap");
			String imgUrl = infoBundle.getString("img");
			// 异步加载图片
			loadImage(capUrl, capView);
			if (null != imgUrl) {
				loadImage(imgUrl, imgView);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 设置UI更新事件，实现回调接口
		UpUiCallback updataUi = new UpUiCallback();
		// 启动刷新微博list任务
		Log.w(TAG, "try to start get comments list task");
		GetCommentsTask updaWeibotask = new GetCommentsTask(WeiboInfoView.this,
				id, updataUi);
		updaWeibotask.execute(token);
	}

	private void loadImage(final String url, ImageView imageView) {
		// 如果缓存过就会从缓存中取出图像，ImageCallback接口中方法也不会被执行
		CallbackImpl callbackImpl = new CallbackImpl(imageView);
		Drawable cacheImage = loader.loadDrawable(url, callbackImpl);
		if (cacheImage != null) {
			imageView.setImageDrawable(cacheImage);
		}
	}

	/**
	 * 更新text的实现类，继承了回调接口
	 * @author Administrator
	 * 
	 */
	private class UpUiCallback implements GetCommentsTask.UpdataTextCallback {


		@Override
		public void updataText(List<CommentsInfo> infoList) {

			// 刷新微博评论列表，为listview设置适配器
			if (null == infoList) {
				Log.w(TAG, "list is null");
			} else {
				CommentsAdapter listInfoAdapter = new CommentsAdapter(
						getApplicationContext(), infoList, highlight);
				setListAdapter(listInfoAdapter);
				ListViewUtility.setListViewHeightBasedOnChildren(getListView());
			}
		}
	}

	/**
	 * 高亮
	 * @author Administrator
	 *
	 */
	class HighlightCm implements HighlightComments {

		@Override
		public void highlight(TextView tv, String strring) {
			str = strring;
			spannableString = new SpannableString(str);
			heightLight(TOPIC, Color.BLUE);
			heightLight(NAMEH, Color.BLUE);
			heightLight(URLH, Color.BLUE);

			tv.setText(spannableString);
		}

	}

	private void heightLight(String pattern, int color) {
		ArrayList<Map<String, String>> lists = getStartAndEnd(Pattern
				.compile(pattern));
		for (Map<String, String> str : lists) {
			ForegroundColorSpan span = new ForegroundColorSpan(color);
			spannableString.setSpan(span, Integer.parseInt(str.get(START)),
					Integer.parseInt(str.get(END)),
					Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		}
	}

	private ArrayList<Map<String, String>> getStartAndEnd(Pattern pattern) {
		ArrayList<Map<String, String>> lists = new ArrayList<Map<String, String>>(
				0);

		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			Map<String, String> map = new HashMap<String, String>(0);
			map.put(START, matcher.start() + "");
			map.put(END, matcher.end() + "");
			lists.add(map);
		}
		return lists;
	}
}
