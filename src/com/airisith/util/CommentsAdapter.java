package com.airisith.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.airisith.airisith.R;
import com.airisith.modle.CommentsInfo;
import com.airisith.modle.Contants;

public class CommentsAdapter extends BaseAdapter{

	private HighlightComments hl;
	
	private final String TAG = "CommentsAdapter";
	//WeiBoList对象当中的数据，代表了服务器端所返回的所有数据
	private ArrayList<HashMap<String, Object>> commentsList;
	//info当中存储了一次所取回的所有微博数据
	private List<CommentsInfo> info = null;
	//View对象的缓存
	private Map<Integer,View> rowViews = new HashMap<Integer,View>();
	private Context context = null;
	private AsyncImageLoader loader;
	
	public  CommentsAdapter(Context context ,List<CommentsInfo> info, HighlightComments hl){
		this.context = context;
		this.info = info;
		loader = new AsyncImageLoader();
		this.hl = hl;
	}
	@Override
	public int getCount() {
		return info.size();
	}

	@Override
	public Object getItem(int position) {
		return info.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = rowViews.get(position);
		if(rowView == null){
			//生成一个LayoutInflater对象
			LayoutInflater layoutInflater = LayoutInflater.from(context);
			//调用LayoutInflater对象的inflate方法，可以生成一个View对象
			rowView = layoutInflater.inflate(R.layout.listcomments_item, null);
			//得到该View当中的两个控件
			TextView nameView = (TextView)rowView.findViewById(R.id.commentNikename);
			TextView timestampView = (TextView)rowView.findViewById(R.id.commentTime);
			TextView textView = (TextView)rowView.findViewById(R.id.commentText);
			ImageView capView = (ImageView)rowView.findViewById(R.id.commentCap);
			TextView repostsView = (TextView)rowView.findViewById(R.id.commentLikeTimes);
			
			//调用getItem（）方法，得到对应位置的comments对象
			CommentsInfo weiboInfo = (CommentsInfo)getItem(position);
			try {
				Log.w(TAG, "comments :get comments data"+position);
			} catch (Exception e) {
			}
			try {
				nameView.setText(weiboInfo.getUser_name());
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				timestampView.setText(weiboInfo.getTimeStamp()+ "   来自：" +weiboInfo.getSource());
			} catch (Exception e) {
			}
			try {
				String string = weiboInfo.getText();
				hl.highlight(textView, string);
				//textView.setText(weiboInfo.getText());
			} catch (Exception e) {
			}
						
			// 加载图像
			try {
				if ( null!= weiboInfo.getUser_img()) {
					String capUrl = weiboInfo.getUser_img();
					new UpCap().updataCap(capUrl, capView);
				}
			} catch (Exception e) {
			}
			
			//文本控件注册监听器（评论部分）
			textView.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					
				}
			});			
			capView.setOnClickListener(new OnUserClickedListener(weiboInfo));
			nameView.setOnClickListener(new OnUserClickedListener(weiboInfo));
			
			rowViews.put(position, rowView);
		}
		return rowView;
	}
	
	/**
	 * 下载网络图片
	 * 
	 * @param          
	**/
    private void loadImage(final String url,final ImageView iv) {
        // 如果缓存中存在，就会从缓存中取出图像, ImageCallback 也不会被执行
        CallbackImpl  callback = new CallbackImpl(iv);
        Drawable cacheImg = loader.loadDrawable(url,callback);
        if (null != cacheImg) {
            iv.setImageDrawable(cacheImg);
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
    
    //回调接口，高亮id等
    public interface HighlightComments{
    	public void highlight(TextView tv, String str);
    }

    /**
     * 点击头像和昵称触发事件:转到用户该个人页面
     * @author Administrator
     *
     */
    private class OnUserClickedListener implements OnClickListener{

    	private CommentsInfo commentData;
    	public OnUserClickedListener(CommentsInfo commentData){
    		this.commentData = commentData;
    	}
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			// 将usesr信息传递给Activity，用于新的view显示
            intent.setAction(Contants.ACTION_WEIBO_VIEW_USER);           
            bundle.putLong("userId", commentData.getUser_id());
            intent.putExtra("userinfo", bundle);
            context.sendBroadcast(intent);
		}
    	
    }
}
