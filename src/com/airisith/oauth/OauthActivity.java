package com.airisith.oauth;

import com.airisith.airisith.HomeActivity;
import com.airisith.airisith.R;
import com.airisith.modle.Contants;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class OauthActivity extends Activity{

	protected static final String TAG = "OauthActivity";
	private Button requestButton;
	private AuthInfo mWeiboAuthInfo;
	private SsoHandler mSsoHandler;
	private Oauth2AccessToken mAccessToken;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.oauth);
		mWeiboAuthInfo = new AuthInfo(this, Contants.APP_KEY, Contants.REDIRECT_URL, Contants.SCOPE);
		mSsoHandler = new SsoHandler(OauthActivity.this, mWeiboAuthInfo);
		
		requestButton = (Button)findViewById(R.id.gettokenbutton);
		requestButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.w(TAG, "授权开始");
				mSsoHandler.authorize(new AuthListener());
			}
		});
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mSsoHandler != null) {
			 mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}
	
	/**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {
        
        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // 显示 Token
                //updateTokenView(false);
                
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(OauthActivity.this, mAccessToken);
                Toast.makeText(OauthActivity.this, 
                        R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();
                Log.w(TAG, "授权完成");
                
                //打开主页
                startActivity(new Intent(OauthActivity.this, HomeActivity.class));
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                System.out.println("授权失败--code："+code);
                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
               }
                Toast.makeText(OauthActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

		@Override
		public void onCancel() {
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
		}
    }

}
