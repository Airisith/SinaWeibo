package com.airisith.Views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.airisith.airisith.R;
import com.airisith.modle.Contants;
import com.airisith.modle.WeiboData.InfoOfWeibo;
import com.airisith.oauth.AccessTokenKeeper;
import com.airisith.util.GetWeibolistTask;
import com.airisith.util.WebiInfoAdapter;
import com.airisith.util.WebiInfoAdapter.HighlightTextCallback;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class HomeView extends ListActivity {

	/* 高亮参数 */
	private Highlight highlight;
	SpannableString spannableString;
	final String START = "start";
	final String END = "end";
	String str = "";
	final String TOPIC = "#.+?#";
	final String NAMEH = "@([\u4e00-\u9fa5A-Za-z0-9_]*)";
	final String URLH = "http://.*";

	private Oauth2AccessToken token;
	private ArrayList<HashMap<String, Object>> list;

	private UpTextCallback updataText;
	/* 顶层标题 */
	private Spinner spinner;
	private int groupNum;
	private String[] groups;
	
	private String[] groupUrls;
	private String groupUrl;

	private static int page;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_home);
		page = 1;
	
		groupUrl = Contants.STATUESES_HOME_TIMELINE_URL;
		
		//标题栏布局
		spinner = (Spinner)findViewById(R.id.title_home);
		groupNum = 2;
		//设置微博分组和各个分组的URL
		groups = new String[groupNum];
		groupUrls = new String[groupNum];
		groups[0] = "全部微博";
		groups[1] = "公共微博";
		groupUrls[0] = Contants.STATUESES_HOME_TIMELINE_URL;
		groupUrls[1] = Contants.STATUESES_PUBLIC_TIMELINE_URL;
		List<String> spinnerList = new ArrayList<String>();
		//添加spinner的item，即分组
		for(int num=0; num<groupNum; num++ ){
			spinnerList.add(groups[num]);
		}
		ArrayAdapter titledApter = new ArrayAdapter(this, R.layout.home_title_item,
	                R.id.home_title_item, spinnerList);
		spinner.setAdapter(titledApter);
		spinner.setPrompt("分组");
		
		//高亮微博关键文字接口
		highlight = new Highlight();
		token = AccessTokenKeeper.readAccessToken(HomeView.this);
		list = new ArrayList<HashMap<String, Object>>();

		// 广播，对于点击下方Home按钮的响应
		IntentFilter intentfFilter = new IntentFilter();
		intentfFilter.addAction(Contants.ACTION_HOME_BUTTON_HOME);
		registerReceiver(new HomeButtonReceiver(), intentfFilter);
		
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			// positon就是顶层item的位置，0为第一个，即全部微博
			@Override
			public void onItemSelected(AdapterView<?> view, View arg1,
					int position, long id) {
				//选择哪个分组，就指定URL为哪个分组的URL，再更新该分组的list
				groupUrl = groupUrls[position];
				updataWeiboList(token, groupUrl, 1);
				page = 1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> view) {
				
			}
			});
		
		//上一页
		findViewById(R.id.home_lastPage).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (1 == page) {
					Toast.makeText(getApplicationContext(), "已经是第一页了", Toast.LENGTH_SHORT).show();
				} else if(page > 1){
					page = page -1;
					HomeView.this.updataWeiboList(token, groupUrl, page);
				}
			}
		});
		//下一页
		findViewById(R.id.home_nextPage).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				page = page +1;
				HomeView.this.updataWeiboList(token, groupUrl, page);
			}
		});

	}

	/**
	 * 刷新微博List
	 * @param token
	 * @param groupBaseUrl
	 * @param page :更新第几页
	 */
	public void updataWeiboList(Oauth2AccessToken token ,String groupBaseUrl, int page) {
		// 设置文本更新事件，实现回调接口
		updataText = new UpTextCallback();

		// 启动刷新微博list任务
		final GetWeibolistTask updaWeibotask = new GetWeibolistTask(this, list, groupBaseUrl,
				updataText, page);
		updaWeibotask.execute(token);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	// 更新text的实现类，继承了回调接口
	private class UpTextCallback implements GetWeibolistTask.UpdataTextCallback {
		@Override
		public void updataText(List<InfoOfWeibo> mlist) {

			WebiInfoAdapter listInfoAdapter = new WebiInfoAdapter(
					getApplicationContext(), mlist, highlight);
			setListAdapter(listInfoAdapter);
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

	// 接收来自homeactivity的广播，看看此时HomeView是否在前面，如果是，则刷新页面，否则不刷新
	private class HomeButtonReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// (View.VISIBLE != views.get("home").getVisibility())
			Bundle bundle = intent.getBundleExtra("Bundle");
			String str = bundle.getString("currentSubView");
			if (null != str) {
				if (str.equals("Home")) {
					// 启动刷新微博list任务
					updataWeiboList(token, groupUrl, 1);
					page = 1;
				}
			}
		}
	}
}
