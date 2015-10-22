package com.airisith.util;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

//回调接口，将图片显示到控件上，类似于onclickListener的实现类
public class CallbackImpl implements AsyncImageLoader.ImageCallback{
	private ImageView imageView ;
	
	public CallbackImpl(ImageView imageView) {
		super();
		this.imageView = imageView;
	}

	@Override
	public void imageLoaded(Drawable imageDrawable) {
		imageView.setImageDrawable(imageDrawable);
	}

}