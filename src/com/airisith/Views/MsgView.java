package com.airisith.Views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.airisith.airisith.R;
import com.airisith.modle.CommentsInfo;
import com.airisith.modle.Contants;
import com.airisith.modle.NewMsg;
import com.airisith.oauth.AccessTokenKeeper;
import com.airisith.util.CommentsAdapter;
import com.airisith.util.GetCommentsTask;
import com.airisith.util.ListViewUtility;
import com.airisith.util.WeiboClient;
import com.airisith.util.CommentsAdapter.HighlightComments;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

public class MsgView extends ListActivity {

	protected static final String TAG = "MsgView";
	private TextView weiboCountView; // 新微博
	private TextView follwerCountView; // 新粉丝
	private TextView commentsCountView; // 新评论
	private TextView msgCountView; // 新私信
	private TextView aiteCountView; // 新@
	private NewMsg myMsgs = null;
	private Oauth2AccessToken myToken = null;

	/* 高亮参数 */
	private HighlightCm highlight;
	SpannableString spannableString;
	final String START = "start";
	final String END = "end";
	String str = "";
	final String TOPIC = "#.+?#";
	final String NAMEH = "@([\u4e00-\u9fa5A-Za-z0-9_]*)";
	final String URLH = "http://.*";

	// 接收子线程message更新UI
	final Handler handler = new MsgHandler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_message);
		weiboCountView = (TextView) findViewById(R.id.message_newWeiboCount);
		follwerCountView = (TextView) findViewById(R.id.message_newFollowerCount);
		commentsCountView = (TextView) findViewById(R.id.message_newCommentsCount);
		msgCountView = (TextView) findViewById(R.id.message_newMsgCount);
		aiteCountView = (TextView) findViewById(R.id.message_newAiteCount);

		myToken = AccessTokenKeeper.readAccessToken(MsgView.this);
		// 高亮显示关键字接口
		highlight = new HighlightCm();

		// 广播，对于点击下方Home按钮的响应
		IntentFilter intentfFilter = new IntentFilter();
		intentfFilter.addAction(Contants.ACTION_HOME_BUTTON_MESSAGE);
		registerReceiver(new MsgButtonReceiver(), intentfFilter);

		// 刷新UI
		refreshUi();		
	}

	/**
	 * 启动线程更新UI
	 */
	private void refreshUi(){
		new Thread(new Runnable() {
			//先更新消息数，完成后通过hadler发消息给UI线程，更新评论
			@Override
			public void run() {
				Long myUID = WeiboClient.getCurrentUserId(myToken);
				myMsgs = WeiboClient.getNewMessageCounts(myToken, myUID);
				if (null != myMsgs) {
					Message msg = handler.obtainMessage();
					handler.sendMessage(msg);
				}
			}
		}).start();
	}
	/**
	 * 更新text的实现类，继承了回调接口
	 * 
	 * @author Administrator
	 * 
	 */
	private class UpMyUiCallback implements GetCommentsTask.UpdataTextCallback {

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
	 * 
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

	/**
	 * handler接收子线程消息，表示获取用户未读消息数完成
	 * 
	 * @author Administrator
	 * 
	 */
	private class MsgHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			weiboCountView.setText(myMsgs.getStatus() + "");
			follwerCountView.setText(myMsgs.getFollower() + "");
			commentsCountView.setText(myMsgs.getCmt() + "");
			msgCountView.setText(myMsgs.getDm() + "");
			aiteCountView.setText(myMsgs.getMention() + "");
			// 刷新微博列表
			// 设置UI更新事件，实现回调接口
			UpMyUiCallback updataUi = new UpMyUiCallback();
			// 启动刷新微博list任务
			Log.w(TAG, "try to start get comments list task");
			GetCommentsTask updaWeibotask = new GetCommentsTask(MsgView.this,
					0, updataUi);
			updaWeibotask.execute(myToken);
		}

	}

	/**
	 * 接收来自homeactivity的广播，看看此时HomeView是否在前面，如果是，则刷新页面，否则不刷新
	 * @author Administrator
	 *
	 */
	private class MsgButtonReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// (View.VISIBLE != views.get("home").getVisibility())
			Bundle bundle = intent.getBundleExtra("Bundle");
			String str = bundle.getString("currentSubView");
			if (null != str) {
				if (str.equals("Message")) {
					//更新UI
					refreshUi();
				}
			}
		}
	}
}
