package com.airisith.Views;

import com.airisith.airisith.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class FindView extends Activity{

	private TextView rightTextView;
	private EditText findText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_find);
		
		rightTextView = (TextView)findViewById(R.id.find_rightView);
		findText = (EditText)findViewById(R.id.findText);
		
		rightTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	}
	
}
