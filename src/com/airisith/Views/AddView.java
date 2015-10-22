package com.airisith.Views;

import java.util.HashMap;
import java.util.Map;

import com.airisith.airisith.R;
import com.airisith.modle.OpenImageFile;
import com.airisith.oauth.AccessTokenKeeper;
import com.airisith.util.OpenFileDialog;
import com.airisith.util.OpenFileDialog.FileOpenCallbackBundle;
import com.airisith.util.WeiboClient;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddView extends Activity{

	private EditText sendText;
	private TextView sendButton;
	private TextView cancelButton;
	private ImageView addimage;
	private byte[] imgBuffer = null;
	
	// 打开文件对话框参数
	private ImgOpenCallback imgOpenCallback;
	static private int openfileDialogId = 0; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_add);
		sendText = (EditText)findViewById(R.id.sendWeiboText);
		sendButton = (TextView)findViewById(R.id.add_rightView);
		cancelButton = (TextView)findViewById(R.id.add_leftView);
		addimage = (ImageView)findViewById(R.id.add_addimage);
		final Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(AddView.this);
		
		imgOpenCallback = new ImgOpenCallback();
		final SendWeiboHandler sendHandler = new SendWeiboHandler();
		
		sendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						int result = 0;
						Message msg = sendHandler.obtainMessage();
						Boolean resultBoolean = WeiboClient.sendWeibo(token, sendText, imgBuffer);
						if (true == resultBoolean) {
							result = 1;
						} else {
							result = 0;
						}
						msg.what = result;
						//发送完成通知主线程
						sendHandler.sendMessage(msg);
					}
				}).start();
			}
		});
		
		addimage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(openfileDialogId);
			}
		});
		
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	}

	/**
	 * 处理微博发送结果
	 * @author Administrator
	 *
	 */
	class SendWeiboHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			if (1 == msg.what) {
				Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
				sendText.setText("");
			} else {
				Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/**
	 * 文件选择回调接口
	 * @author Administrator
	 *
	 */
	private class ImgOpenCallback implements FileOpenCallbackBundle{

		@Override
		public void callback(Bundle bundle) {
			String filepath = bundle.getString("path"); 
            setTitle(filepath); // 把文件路径显示在标题上 
            imgBuffer = OpenImageFile.getImageBytes(filepath);
		}
		
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		if(id==openfileDialogId){  
            Map<String, Integer> images = new HashMap<String, Integer>();  
            // 下面几句设置各文件类型的图标， 需要你先把图标添加到资源文件夹  
            images.put(OpenFileDialog.sRoot, R.drawable.filedialog_root);   // 根目录图标  
            images.put(OpenFileDialog.sParent, R.drawable.filedialog_folder_up);    //返回上一层的图标  
            images.put(OpenFileDialog.sFolder, R.drawable.filedialog_folder);   //文件夹图标  
            images.put("jpg", R.drawable.filedialog_imgfile);   //文件图标  
            images.put("png", R.drawable.filedialog_imgfile);
            images.put("jpeg", R.drawable.filedialog_imgfile);
            images.put("gif", R.drawable.filedialog_imgfile);
            images.put(OpenFileDialog.sEmpty, R.drawable.filedialog_root);  
            Dialog dialog = OpenFileDialog.createDialog(id, this, "选择文件", new ImgOpenCallback(),   
            ".jpg;.png;.jpeg;.gif;",  
            images);  
            return dialog;  
        }  
        return null; 
	}
	
}
