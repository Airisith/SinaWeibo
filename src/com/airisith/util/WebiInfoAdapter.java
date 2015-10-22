package com.airisith.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.airisith.airisith.R;
import com.airisith.modle.Contants;
import com.airisith.modle.WeiboData.InfoOfWeibo;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WebiInfoAdapter extends BaseAdapter{
	
	private HighlightTextCallback hl;
	//WeiBoList对象当中的数据，代表了服务器端所返回的所有数据
	private ArrayList<HashMap<String, Object>> weiboList;
	//info当中存储了一次所取回的所有微博数据
	private List<InfoOfWeibo> info = null;
	//View对象的缓存
	private Map<Integer,View> rowViews = new HashMap<Integer,View>();
	private Context context = null;
	private AsyncImageLoader loader;
		
	public WebiInfoAdapter(Context context ,List<InfoOfWeibo> info, HighlightTextCallback hl){
		this.context = context;
		this.info = info;
		loader = new AsyncImageLoader();
		this.hl = hl;
				
	}
	
	//返回当中的Adapter当中，共包含多少个item
	@Override
	public int getCount() {
		return info.size();
	}

	//根据位置，得到相应的item对象
	@Override
	public Object getItem(int position) {
		return info.get(position);
	}

	//根据位置，得到相应的item对象的ID
	@Override
	public long getItemId(int position) {
		return position;
	}

	//ListView通过调用getView()方法，得到相应的View对象，并将其显示在Activity当中
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = rowViews.get(position);
		if(rowView == null){
			//生成一个LayoutInflater对象
			LayoutInflater layoutInflater = LayoutInflater.from(context);
			//调用LayoutInflater对象的inflate方法，可以生成一个View对象
			rowView = layoutInflater.inflate(R.layout.listweibo_item, null);
			//得到该View当中的两个控件
			TextView nameView = (TextView)rowView.findViewById(R.id.nikename);
			TextView timestampView = (TextView)rowView.findViewById(R.id.timestamp);
			TextView textView = (TextView)rowView.findViewById(R.id.text);
			ImageView capView = (ImageView)rowView.findViewById(R.id.cap);
			ImageView imgView = (ImageView)rowView.findViewById(R.id.img);
			TextView repostsView = (TextView)rowView.findViewById(R.id.reposts);
			TextView commentsView = (TextView)rowView.findViewById(R.id.comments);
			TextView attitudesView = (TextView)rowView.findViewById(R.id.like);

			//调用getItem（）方法，得到对应位置的weiBoData对象
			final InfoOfWeibo weiboInfo = (InfoOfWeibo)getItem(position);
			try {
				nameView.setText(weiboInfo.getName());
				timestampView.setText(weiboInfo.getTimestamp()+ "   来自：" +weiboInfo.getSource());
				try {
					hl.highlight(textView, weiboInfo.gettextInfo());
				} catch (Exception e) {
				}
				//textView.setText(weiboInfo.gettextInfo());
				
				if(0 != weiboInfo.getReposts()){
					if (weiboInfo.getReposts() <= 10000) {
						repostsView.setText(weiboInfo.getReposts()+"");
					} else {
						int times = weiboInfo.getReposts()/10000;
						repostsView.setText(times+"万");
					}				
				}
				if(0 != weiboInfo.getComments()){
					if (weiboInfo.getReposts() <= 10000) {
						commentsView.setText(weiboInfo.getComments()+"");
					} else {
						int times = weiboInfo.getReposts()/10000;
						repostsView.setText(times+"万");
					}
				}
				if(0 != weiboInfo.getAttitudes()){
					if (weiboInfo.getReposts() <= 10000) {
						attitudesView.setText(weiboInfo.getAttitudes()+"");
					} else {
						int times = weiboInfo.getReposts()/10000;
						repostsView.setText(times+"万");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
						
			// 加载图像
			String capUrl = weiboInfo.getCapUrlString();
			String imgUrl = weiboInfo.getImgUrl();
			if (null != capUrl) {
				new UpCap().updataCap(capUrl, capView);
			}
			if(null != imgUrl){
				new UpImage().updataImage(imgUrl, imgView);
			}
			
			//注册监听器,点击文本和图片执行相同的动作：转到微博详情页
			textView.setOnClickListener(new OnclickeInfoListener(weiboInfo));
			imgView.setOnClickListener(new OnclickeInfoListener(weiboInfo));
			capView.setOnClickListener(new OnclickeUserListener(weiboInfo));
			nameView.setOnClickListener(new OnclickeUserListener(weiboInfo));
			commentsView.setOnClickListener(new OnclickeCommentListener(weiboInfo));
			
			rowViews.put(position, rowView);
		}
		return rowView;
	}
	
	/**
	 * 监听器，如果点击文本区或者图片，则给HomeActivity发送广播，转到微博评论列表页
	 * 
	**/
	public class OnclickeInfoListener implements OnClickListener{
		private InfoOfWeibo weiboInfo;
		
		public OnclickeInfoListener(InfoOfWeibo weiboInfo){
			this.weiboInfo = weiboInfo;
		}
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			// 将所有信息传递给Activity，用于新的view显示
            intent.setAction(Contants.ACTION_WEIBO_VIEW_INFO);
            bundle.putString("name", weiboInfo.getName());
            bundle.putString("timestamp", weiboInfo.getTimestamp()+ "   来自：" +weiboInfo.getSource());
            bundle.putString("text", weiboInfo.gettextInfo());
            bundle.putString("reports", weiboInfo.getReposts()+"");
            bundle.putString("comments", weiboInfo.getComments()+"");
            bundle.putString("attitudes", weiboInfo.getAttitudes()+"");
            bundle.putString("cap", weiboInfo.getCapUrlString());
            bundle.putString("img", weiboInfo.getImgUrl());
            bundle.putLong("id", weiboInfo.getId());
            bundle.putLong("userId", weiboInfo.getUserId());
            intent.putExtra("weiboinfo", bundle);
            context.sendBroadcast(intent);
		}
	}
	
	/**
	 * 监听器，点击头像或者昵称都转到个人资料页
	 * 
	**/
	public class OnclickeUserListener implements OnClickListener{
		private InfoOfWeibo weiboInfo;
		
		public OnclickeUserListener(InfoOfWeibo weiboInfo){
			this.weiboInfo = weiboInfo;
		}
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			// 将usesr信息传递给Activity，用于新的view显示
            intent.setAction(Contants.ACTION_WEIBO_VIEW_USER);           
            bundle.putLong("userId", weiboInfo.getUserId());
            intent.putExtra("userinfo", bundle);
            context.sendBroadcast(intent);
		}
	}

	public class OnclickeCommentListener implements OnClickListener{
		private InfoOfWeibo weiboInfo;
		
		public OnclickeCommentListener(InfoOfWeibo weiboInfo){
			this.weiboInfo = weiboInfo;
		}
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			// 将所有信息传递给Activity，用于新的view显示
            intent.setAction(Contants.ACTION_WEIBO_VIEW_COMMENT);
            bundle.putLong("id", weiboInfo.getId());
            intent.putExtra("weiboinfo", bundle);
            context.sendBroadcast(intent);
		}
	}
	
	/**
	 * 下载网络图片
	 * 
	 * @param          
	**/
    private void loadImage(final String url,final ImageView iv) {
        // 如果缓存中存在，就会从缓存中取出图像, ImageCallback 也不会被执行
        CallbackImpl  callback = new CallbackImpl(iv);
        
    	try {
    		Drawable cacheImg = loader.loadDrawable(url,callback);
            if (null != cacheImg) {
                iv.setImageDrawable(cacheImg);         
            }
		} catch (Exception e) {
		}
		
    }
    
    //更新头像的实现类
    public class UpCap implements GetWeibolistTask.UpdataCapCallback{
		@Override
		public void updataCap(String url, ImageView cap) {
			loadImage(url, cap);
		}
    	
    }
    
    //更新图像的实现类
    public class UpImage implements GetWeibolistTask.UpdataImageCallback{
		@Override
		public void updataImage(String url, ImageView img) {
			loadImage(url, img);
		} 	
    }
    
    //回调接口，高亮
    public interface HighlightTextCallback{
    	public void highlight(TextView tv, String str);
    }
    
}
