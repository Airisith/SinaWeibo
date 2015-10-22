package com.airisith.util;

import java.util.Iterator;
import java.util.List;

import com.airisith.modle.CommentsInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class GetCommentsTask extends AsyncTask<Oauth2AccessToken,  Iterator<CommentsInfo>, List<CommentsInfo>>{
	private final String TAG = "||";
	private List<CommentsInfo> commentsInfoList = null;
	private UpdataTextCallback updataCommentsText;
	private Activity activity;
	private Long weiboId;
	
	public GetCommentsTask(Activity act, long weiboId, UpdataTextCallback updataText){
		// 传递UI
		this.activity = act;
		this.weiboId = weiboId;
		//创建data的list用来保存收到的数据
		this.updataCommentsText = updataText;
	}
	
	@Override
	protected void onProgressUpdate(Iterator<CommentsInfo>... values) {
		Iterator<CommentsInfo> iteratorInfoList = values[0];
	}

	@Override
	protected List<CommentsInfo> doInBackground(Oauth2AccessToken... params) {
		
		Oauth2AccessToken token = params[0];
		WeiboClient weiboClient = new WeiboClient();
		Log.e(TAG, "get comments list task is begin");
		if (0 != weiboId) {
			try {
				commentsInfoList = weiboClient.getCommentsList(token, weiboId, 1);
				Log.e(TAG, "get commentsList success frome clinent:have "+commentsInfoList.size()+" comments");
			} catch (Exception e) {
			}
			
			
			try {
				//将所有微博信息对象放在迭代器中
				Iterator<CommentsInfo> iteratorInfoList = commentsInfoList.iterator();
				// 更新text信息
				publishProgress(iteratorInfoList);
			} catch (Exception e) {
			}
		}else {
			try {
				commentsInfoList = weiboClient.getMyCommentsList(token, 1);
				Log.e(TAG, "get commentsList success frome clinent:have "+commentsInfoList.size()+" comments");
			} catch (Exception e) {
			}
		}
		

		return commentsInfoList;
	}
	
	// 执行完毕，resultlist中有完整的值
	@Override
	protected void onPostExecute(List<CommentsInfo> result) {
		super.onPostExecute(result);
		//当获取list完成后再去更新UI，此时的list传回去
		updataCommentsText.updataText(commentsInfoList);
		
	}

    
    //三个回调接口，用于任务完成后通知UI更新Text，图片等,更新text在activi实现，加载图片在Adapter实现
    public interface UpdataTextCallback{
    	public void updataText(List<CommentsInfo> infoList);
    }

    public interface UpdataCapCallback{
    	public void updataCap(final String url,final ImageView cap);
    }
    
    public interface UpdataImageCallback{
    	public void updataImage(final String url,final ImageView img);
    }

}
