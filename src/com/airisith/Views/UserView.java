package com.airisith.Views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.airisith.airisith.R;
import com.airisith.modle.Contants;
import com.airisith.modle.User.UserInfo;
import com.airisith.modle.WeiboData.InfoOfWeibo;
import com.airisith.oauth.AccessTokenKeeper;
import com.airisith.util.AsyncImageLoader;
import com.airisith.util.CallbackImpl;
import com.airisith.util.GetUserWeibolistTask;
import com.airisith.util.ListViewUtility;
import com.airisith.util.WebiInfoAdapter;
import com.airisith.util.WeiboClient;
import com.airisith.util.WebiInfoAdapter.HighlightTextCallback;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserView extends ListActivity {

	private TextView leftTextView = null;
	private TextView rightTextView = null;
	private ImageView backImageView = null;
	private RelativeLayout user_info_layout = null;
	private UserInfo userInfo = null;
	private Long userId = null;
	private AsyncImageLoader loader = null;

	private ImageView capView;
	private TextView userNickTextView;
	private TextView showTextView;
	private TextView followsTextView;
	private TextView friendsTextView;
	private TextView statuesTextView;

	/* 高亮参数 */
	private Highlight highlight;
	SpannableString spannableString;
	final String START = "start";
	final String END = "end";
	String str = "";
	final String TOPIC = "#.+?#";
	final String NAMEH = "@([\u4e00-\u9fa5A-Za-z0-9_]*)";
	final String URLH = "http://.*";

	private ArrayList<HashMap<String, Object>> list;
	private UpUserTextCallback updataText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_user);
		leftTextView = (TextView) findViewById(R.id.userView_left);
		rightTextView = (TextView) findViewById(R.id.userView_right);
		backImageView = (ImageView) findViewById(R.id.user_Back);
		user_info_layout = (RelativeLayout) findViewById(R.id.user_infoOfImageArea);

		capView = (ImageView) findViewById(R.id.user_user_Cap);
		userNickTextView = (TextView) findViewById(R.id.user_nameText);
		showTextView = (TextView) findViewById(R.id.user_showText);
		followsTextView = (TextView) findViewById(R.id.user_followNum);
		friendsTextView = (TextView) findViewById(R.id.user_followerNum);
		statuesTextView = (TextView) findViewById(R.id.user_weiboNumTextView);

		final Oauth2AccessToken myToken = AccessTokenKeeper
				.readAccessToken(UserView.this);
		final UserInfoHandler userInfoHandler = new UserInfoHandler();
		loader = new AsyncImageLoader();

		// 重新设置图片上的布局
		setUserBackLayout(backImageView, user_info_layout);

		// 高亮微博关键文字接口
		highlight = new Highlight();
		list = new ArrayList<HashMap<String, Object>>();
		
		//响应下方按钮刷新的广播
		IntentFilter intentfFilter = new IntentFilter();
		intentfFilter.addAction(Contants.ACTION_HOME_BUTTON_USER);
		registerReceiver(new UserButtonReceiver(userInfoHandler, myToken), intentfFilter);
		
		// 右上方按钮：关注博主等功能
		rightTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		rightTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						SettingView.class));
			}
		});
		leftTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ListViewUtility.setListViewHeightBasedOnChildren(getListView());
			}
		});

		upUi(userInfoHandler, myToken);
		
		//解决了显示不全的问题，实时设置ListView刷新
		getListView().setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				ListViewUtility.setListViewHeightBasedOnChildren((ListView)v);
				return false;
			}
		});
	}

	private void upUi(final UserInfoHandler userhandler, final Oauth2AccessToken token){
		// 新线程获取用户信息
				new Thread(new Runnable() {

					@Override
					public void run() {
						userId = WeiboClient.getCurrentUserId(token);
						Message msg = userhandler.obtainMessage();
						if ((null == token) || (null == userId)) {
							msg.what = -1;
						} else {
							userInfo = WeiboClient.getUserInfo(token, userId);
							if (null != userInfo) {
								msg.what = 1;
							} else {
								msg.what = 0;
							}
						}
						userhandler.sendMessage(msg);
						// 刷新微博
						updataWeiboList(token, userId);
					}
				}).start();
	}
	
	/**
	 * 根据背景图片重新设置user_info部分的尺寸，让它刚好位于背景图片上
	 * 
	 * @para imgView:背景图片的ImageView， userInfoLayout：user信息部分的layout
	 */
	public void setUserBackLayout(ImageView imgView,
			RelativeLayout userInfoLayout) {
		// 获取背景图片尺寸
		imgView.setDrawingCacheEnabled(true);
		BitmapDrawable mDrawable = (BitmapDrawable) imgView.getDrawable();
		Bitmap mBitmap = mDrawable.getBitmap();
		int bitmapHeight = mBitmap.getHeight();
		int bitmapWidth = mBitmap.getWidth();
		// 获取屏幕尺寸
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		// 计算缩放后的图片高度
		int scalHeight = screenHeight * screenWidth / bitmapWidth;
		imgView.setDrawingCacheEnabled(false);
		// 设置layout的高度为图片缩放后的高度
		RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) userInfoLayout
				.getLayoutParams();
		linearParams.height = scalHeight;
	}

	/**
	 * 获取用户信息Handler
	 */
	private class UserInfoHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			if (-1 == msg.what) {
				Toast.makeText(getApplicationContext(), "授权或者用户ID无效",
						Toast.LENGTH_SHORT).show();
			} else if (0 == msg.what) {
				Toast.makeText(getApplicationContext(), "获取用户信息失败",
						Toast.LENGTH_SHORT).show();
			} else if (1 == msg.what) {
				// private ImageView capView ;

				if (null != userInfo.getName()) {
					userNickTextView.setText(userInfo.getScreen_name());
				} else {
					userNickTextView.setText(userInfo.getScreen_name());
				}
				showTextView.setText(userInfo.getDescription());
				followsTextView.setText(userInfo.getFollowers_count() + "");
				friendsTextView.setText(userInfo.getBi_followers_count() + "");
				statuesTextView.setText(userInfo.getStatuses_count() + "");
				loadImage(userInfo.getProfile_image_url(), capView);
			}
		}
	}

	/**
	 * 异步加载头像
	 * 
	 * @param url
	 * @param imageView
	 */
	private void loadImage(final String url, ImageView imageView) {
		// 如果缓存过就会从缓存中取出图像，ImageCallback接口中方法也不会被执行
		CallbackImpl callbackImpl = new CallbackImpl(imageView);
		Drawable cacheImage = loader.loadDrawable(url, callbackImpl);
		if (cacheImage != null) {
			imageView.setImageDrawable(cacheImage);
		}
	}

	// 刷新微博List
	public void updataWeiboList(Oauth2AccessToken token, Long userId) {
		// 设置文本更新事件，实现回调接口
		updataText = new UpUserTextCallback();

		// 启动刷新微博list任务
		final GetUserWeibolistTask updaWeibotask = new GetUserWeibolistTask(
				list, userId, updataText);
		updaWeibotask.execute(token);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	// 更新text的实现类，继承了回调接口
	private class UpUserTextCallback implements
			GetUserWeibolistTask.UpdataUserText {
		@Override
		public void updataText(List<InfoOfWeibo> mlist) {

			WebiInfoAdapter listInfoAdapter = new WebiInfoAdapter(
					getApplicationContext(), mlist, highlight);
			setListAdapter(listInfoAdapter);
			// 重设listview高度，出现显示不完全，估计是此时图像未加载，后面加载图像完成后，listview的size变大，导致显示不全
			ListViewUtility.setListViewHeightBasedOnChildren(getListView());
		}
	}

	// 高亮关键字回调接口
	class Highlight implements HighlightTextCallback {

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
	
	/**
	 * 接收来自homeactivity的广播，看看此时HomeView是否在前面，如果是，则刷新页面，否则不刷新
	 * @author Administrator
	 *
	 */
	private class UserButtonReceiver extends BroadcastReceiver {
		
		private UserInfoHandler hadler;
		private Oauth2AccessToken mToken;
		public UserButtonReceiver(UserInfoHandler handler, Oauth2AccessToken token){
			this.hadler = handler;
			this.mToken = token;
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			// (View.VISIBLE != views.get("home").getVisibility())
			Bundle bundle = intent.getBundleExtra("Bundle");
			String str = bundle.getString("currentSubView");
			if (null != str) {
				if (str.equals("Psnl")) {
					// 启动刷新微博list任务
					upUi(hadler, mToken);
				}
			}
		}
	}
}
