package com.airisith.airisith;

import com.airisith.oauth.AccessTokenKeeper;
import com.airisith.oauth.OauthActivity;
import com.airisith.util.WeiboClient;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends Activity {

	private ProgressBar oauthProgressBar;
	private TextView oauthTextView;
	private TextView skipTextView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        oauthProgressBar = (ProgressBar)findViewById(R.id.checkOauthProgressBar);
        oauthTextView = (TextView)findViewById(R.id.checkOauthText);
        skipTextView = (TextView) findViewById(R.id.skipCheckOauthView);
        
        final Oauth2AccessToken myToken = AccessTokenKeeper.readAccessToken(StartActivity.this);
        
        final OauthHandler oauthHandler = new OauthHandler();
        
        //子线程检查授权是否过期
        oauthProgressBar.setVisibility(View.VISIBLE);
        oauthTextView.setText("检查授权是否过期");
        new Thread(new Runnable() {
			@Override
			public void run() {
				Boolean checkOauth = WeiboClient.isTokenOverTime(getApplicationContext(), myToken);
				Message msg = oauthHandler.obtainMessage();
				if(true == checkOauth){
					msg.what = 1;
				} else {
					msg.what = 0;
				}
				oauthHandler.sendMessage(msg);
			}
		}).start();
        
        skipTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), HomeActivity.class));
			}
		});
		      
    }
    
    @Override
	protected void onRestart() {
		super.onRestart();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		startActivity(new Intent(getApplicationContext(), HomeActivity.class));
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }
	
	/**
	 * handler 接收授权检查结果
	 * @author Administrator
	 *
	 */
	private class OauthHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			if (1 == msg.what) {
				startActivity(new Intent(getApplicationContext(), HomeActivity.class));
			} else {
				Toast.makeText(getApplicationContext(), "授权过期，请重新登陆", Toast.LENGTH_SHORT).show();
	        	startActivity(new Intent(getApplicationContext(), OauthActivity.class));
			}
		}
	}
}
