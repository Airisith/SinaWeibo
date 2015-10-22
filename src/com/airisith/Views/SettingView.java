package com.airisith.Views;

import com.airisith.airisith.R;
import com.airisith.oauth.OauthActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SettingView extends Activity{

	private TextView oauthTextView;
	private TextView setting1TextView;
	private TextView setting2TextView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.user_setting);
		oauthTextView = (TextView)findViewById(R.id.setting_ouath);
		setting1TextView = (TextView)findViewById(R.id.setting_setting1);
		setting2TextView = (TextView)findViewById(R.id.setting_setting2);
		
		oauthTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), OauthActivity.class));
			}
		});
	}
	
}
