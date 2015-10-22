package com.airisith.airisith;

import java.util.HashMap;
import java.util.List;

import com.airisith.Views.AddView;
import com.airisith.Views.FindView;
import com.airisith.Views.HomeView;
import com.airisith.Views.MsgView;
import com.airisith.Views.OtherOfUserView;
import com.airisith.Views.UserView;
import com.airisith.Views.WeiboInfoView;
import com.airisith.modle.Contants;
import com.airisith.oauth.AccessTokenKeeper;
import com.airisith.util.WeiboClient;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import android.annotation.SuppressLint;
import android.app.ActivityGroup;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class HomeActivity extends ActivityGroup {

	private static final String TAG = "HomeActivity";
	private static final int commentDialogId = 0;
	/* tab按钮 */
	private ImageView homeTabButton;
	private ImageView messageTaButton;
	private ImageView addTaButton;
	private ImageView findTaButton;
	private ImageView psnlTaButton;
	/* 中间的Tab Activity页面 */
	private RelativeLayout container = null;
	private HashMap<String, View> views;
	//设置Flag，标记当前现实的Sub View
	private String vsibleViewString;

	private Oauth2AccessToken myToken = null;
	private Long clickdeWeiboId = null;
	CmtResutHandler cmtHandler = null;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设置无标题，在子view中自定义顶部layout
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.activity_home);
		/* 获取token，控件 */
		myToken = AccessTokenKeeper.readAccessToken(HomeActivity.this);
		container = (RelativeLayout) findViewById(R.id.containerBody);
		homeTabButton = (ImageView) findViewById(R.id.btnModuleHome);
		messageTaButton = (ImageView) findViewById(R.id.btnModuleMessage);
		addTaButton = (ImageView) findViewById(R.id.btnModuleAdd);
		findTaButton = (ImageView) findViewById(R.id.btnModuleFind);
		psnlTaButton = (ImageView) findViewById(R.id.btnModulePersonal);

		/* 将页面信息保存，避免每次切换都重绘 */
		views = new HashMap<String, View>();

		/* 默认打开home页 */
		container.removeAllViews();

		View homeView = getLocalActivityManager().startActivity(
				"HomeActivity",
				new Intent(getApplicationContext(), HomeView.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();

		container.addView(homeView);
		views.put("home", homeView);
		vsibleViewString = "Home";

		// 来自Adapter的广播，点击了某条微博中的控件，响应该事件
		IntentFilter intentfFilter = new IntentFilter();
		intentfFilter.addAction(Contants.ACTION_WEIBO_VIEW_INFO);
		intentfFilter.addAction(Contants.ACTION_WEIBO_VIEW_USER);
		intentfFilter.addAction(Contants.ACTION_WEIBO_VIEW_COMMENT);
		registerReceiver(new ChangeViewBroadcast(), intentfFilter);
		// 评论结果
		cmtHandler = new CmtResutHandler();

		/* 设置点击底部按钮切换activity */
		/* 将各个子View保存在缓存中，避免每次都加载。如果当前Activity位于前面，点击该按钮才会刷新动作 */
		/* intent里面添加了当前显示的子View，用于子activity判断是否执行刷新动作 */
		homeTabButton.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi") public void onClick(View v) {
				container.removeAllViews();
				if (null != views.get("home")) {
					container.addView(views.get("home"));
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("currentSubView", vsibleViewString);
					intent.putExtra("Bundle", bundle);
					intent.setAction(Contants.ACTION_HOME_BUTTON_HOME);
					sendBroadcast(intent);
					vsibleViewString = "Home";
				} else {
					View homeView = getLocalActivityManager().startActivity(
							"HomeActivity",
							new Intent(getApplicationContext(), HomeView.class)
									.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
							.getDecorView();
					container.addView(homeView);
					views.put("home", homeView);
					vsibleViewString = "Home";
				}
			}
		});

		messageTaButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				container.removeAllViews();
				if (null != views.get("message")) {
					container.addView(views.get("message"));
					Intent intent = new Intent();
					intent.setAction(Contants.ACTION_HOME_BUTTON_MESSAGE);
					Bundle bundle = new Bundle();
					bundle.putString("currentSubView", vsibleViewString);
					intent.putExtra("Bundle", bundle);
					sendBroadcast(intent);
					vsibleViewString = "Message";
				} else {
					View msgView = getLocalActivityManager().startActivity(
							"MessageActivity",
							new Intent(getApplicationContext(), MsgView.class)
									.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
							.getDecorView();
					container.addView(msgView);
					views.put("message", msgView);
					vsibleViewString = "Message";
				}
			}
		});

		addTaButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				container.removeAllViews();
				if (null != views.get("add")) {
					container.addView(views.get("add"));
					vsibleViewString = "Add";
				} else {
					View addView = getLocalActivityManager().startActivity(
							"AddActivity",
							new Intent(getApplicationContext(), AddView.class)
									.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
							.getDecorView();
					container.addView(addView);
					views.put("add", addView);
					vsibleViewString = "Add";
				}
			}
		});

		findTaButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				container.removeAllViews();
				if (null != views.get("find")) {
					container.addView(views.get("find"));
					Intent intent = new Intent();
					intent.setAction(Contants.ACTION_HOME_BUTTON_FIND);
					Bundle bundle = new Bundle();
					bundle.putString("currentSubView", vsibleViewString);
					intent.putExtra("Bundle", bundle);
					sendBroadcast(intent);
					vsibleViewString = "Find";
				} else {
					View findView = getLocalActivityManager().startActivity(
							"FindActivity",
							new Intent(getApplicationContext(), FindView.class)
									.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
							.getDecorView();
					container.addView(findView);
					views.put("find", findView);
					vsibleViewString = "Find";
				}
			}
		});

		psnlTaButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				container.removeAllViews();
				if (null != views.get("psnl")) {
					container.addView(views.get("psnl"));
					Intent intent = new Intent();
					intent.setAction(Contants.ACTION_HOME_BUTTON_USER);
					Bundle bundle = new Bundle();
					bundle.putString("currentSubView", vsibleViewString);
					intent.putExtra("Bundle", bundle);
					sendBroadcast(intent);
					vsibleViewString = "Psnl";
				} else {
					View psnlView = getLocalActivityManager().startActivity(
							"PersonalActivity",
							new Intent(getApplicationContext(), UserView.class)
									.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
							.getDecorView();
					container.addView(psnlView);
					views.put("psnl", psnlView);
					vsibleViewString = "Psnl";
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	/**
	 * 
	 * 这是来自HomeView中的adapter的广播
	 * @param          
	**/
	class ChangeViewBroadcast extends BroadcastReceiver {

		@SuppressWarnings("deprecation")
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Contants.ACTION_WEIBO_VIEW_INFO)) {
				container.removeAllViews();
				//bundle含有一条微博的信息
				Bundle bundle = intent.getBundleExtra("weiboinfo");
				Intent sendIntent = new Intent(getApplicationContext(),
						WeiboInfoView.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				sendIntent.putExtra("weiboinfo", bundle);

				container.addView(getLocalActivityManager().startActivity(
						"weiboInfoActivity", sendIntent).getDecorView());

			}
			if (intent.getAction().equals(Contants.ACTION_WEIBO_VIEW_USER)) {
				container.removeAllViews();
				//bundle只含有userId
				Bundle bundle = intent.getBundleExtra("userinfo");
				Intent sendIntent = new Intent(getApplicationContext(),
						OtherOfUserView.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				sendIntent.putExtra("userinfo", bundle);

				container.addView(getLocalActivityManager().startActivity(
						"userInfoActivity", sendIntent).getDecorView());
			}
			if (intent.getAction().equals(Contants.ACTION_WEIBO_VIEW_COMMENT)) {
				//bundle只含有userId
				Bundle bundle = intent.getBundleExtra("weiboinfo");
				Long weiboId = bundle.getLong("id"); 
				clickdeWeiboId = weiboId;
				showDialog(commentDialogId);
			}
		}
	}

	/**
	 * 判断某个界面是否在前台,由于这里HomeActivity才是栈顶，所以list[0]为homeactivity
	 * 而通过debug看到list中只有一个activity，所以此处行不通
	 * @param context
	 * @param className 某个界面名称          
	**/
	public boolean isForeground(Context context, String className) {
		if (context == null || TextUtils.isEmpty(className)) {
			return false;
		}

		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(1);
		if (list != null && list.size() > 0) {
			ComponentName cpn = list.get(0).topActivity;
			if (className.equals(cpn.getClassName())) {
				return true;
			}
		}

		return false;
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		if(id==commentDialogId){
			AlertDialog dialog;
			EditText editText = new EditText(this);
			
			Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("请输入");
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setView(editText);
			builder.setPositiveButton("确定", new OnDialogOkClickedListener(editText));
			builder.setNegativeButton("取消", null);
			dialog = builder.show();
  
            return dialog;  
        }  
        return null; 
	}
	
	/**
	 * 给评论弹出的对话框设置监听器
	 * @author Administrator
	 *
	 */
	private class OnDialogOkClickedListener implements DialogInterface.OnClickListener{

		EditText editText = null;
		public OnDialogOkClickedListener(EditText editText){
			this.editText = editText;
		}
		@Override
		public void onClick(DialogInterface dialog, int which) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					Boolean cmtResult = false;
					cmtResult = WeiboClient.commentWeibo(myToken, editText, clickdeWeiboId);
					Message msg = cmtHandler.obtainMessage();
					if (null != editText.getText()) {
						if (cmtResult) {
							msg.what = 1;
						} else {
							msg.what = 0;
						}
						editText.setText("");
					} else {
							msg.what = 2;
					}
					
					cmtHandler.sendMessage(msg);
				}
			}).start();
		}		
	}
	
	/**
	 * 评论结果通过Hadler发送
	 * @author Administrator
	 *
	 */
	private class CmtResutHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			if (1 == msg.what) {
				Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
			} else if(0 == msg.what){
				Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
			}
			
		}
	}
	
}
