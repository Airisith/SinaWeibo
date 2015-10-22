package com.airisith.util;

import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

/*** 图片的异步加载类, 这个类是示例项目中最重要的类 **/
public class AsyncImageLoader {

	// 定义一个Map类型的 缓存对象,键是图片的url, 值是图片对象(bitmap),以SoftReference(软引用)表示
	private Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();

	// 实现图片异步加载
	public Drawable loadDrawable(final String imageUrl,
			final ImageCallback callback) {
		// 查询缓存，查看当前需要下载的图片是否已存在于缓存当中
		/***
		 * 流程如下: 1.先判断图片url 在缓存中是否存在,如果存在说明曾经下载过 2. 如果存在时,从缓存Map中根据 key 取出 3.
		 * 如果不存在，就要新建一个线程，执行下载过程
		 */
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> sf = imageCache.get(imageUrl);
			/**
			 * * sf.get()方法返回这个sf对象所指向的堆内存的那个对象 如果为空，说明软引用所指向的对象已经被gc回收了,也就是没有了
			 * 如果不为空，直接将图片从缓存中取出然后返回
			 */
			if (null != sf.get())
				return sf.get();

		}
		final Handler handler = new Handler() {

			// 这个方法处理发送来的消息,由图片下载子线程发来的，表示此时图片下载完毕
			@Override
			public void handleMessage(Message msg) {
				callback.imageLoaded((Drawable) msg.obj);
			}
		};
		new Thread() {
			public void run() {
				Drawable drawable = loadImageFromUrl(imageUrl);
				// 这步表示，下载完要把图片放到缓存里
				imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
				// 从handler 获得请求消息,实际上这里有设定的含义
				Message msg = handler.obtainMessage(0, drawable);
				// 这步用 handler 发送消息给异步的线程
				handler.sendMessage(msg);
			}
		}.start();
		return null;
	}

	// 该方法用于根据图片的Url, 从网络上 下载图片
	protected Drawable loadImageFromUrl(String imageUrl) {
		try {
			return Drawable.createFromStream(new URL(imageUrl).openStream(),
					"src");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	//回调方法，图片加载完成后使用
	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable);
	}
}
