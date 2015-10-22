package com.airisith.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.airisith.modle.WeiboData;
import com.airisith.modle.WeiboData.InfoOfWeibo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import android.os.AsyncTask;
import android.widget.ImageView;

public class GetUserWeibolistTask extends AsyncTask<Oauth2AccessToken, Iterator<InfoOfWeibo>, List<InfoOfWeibo>>{

	private List<InfoOfWeibo> weiboInfoList = null;
	private ArrayList<HashMap<String, Object>> list = null;
	private ArrayList<WeiboData> weiboDataList = null;
	private UpdataUserText updataText;
	private Long userId;
	public GetUserWeibolistTask(ArrayList<HashMap<String, Object>> list,Long userId , UpdataUserText updataText){
		// 传递UI
		//创建list准备显示更新UI中的listView
		this.list = list;
		//创建data的list用来保存收到的数据
		weiboInfoList = new ArrayList<InfoOfWeibo>();
		this.updataText = updataText;
		this.userId = userId;
	}
	
	@Override
	protected List<InfoOfWeibo> doInBackground(Oauth2AccessToken... params) {
		InfoOfWeibo dataInfo;
		Oauth2AccessToken token = params[0];
		WeiboClient weiboClient = new WeiboClient();
		weiboDataList = (ArrayList<WeiboData>) weiboClient.getUserWeiboList(token, userId ,weiboInfoList, 1);
		
		//将所有微博信息对象放在迭代器中
		Iterator<InfoOfWeibo> iteratorInfoList = weiboInfoList.iterator();
		// 更新text信息
		publishProgress(iteratorInfoList);
		//在Activity中更新图片
		//activity.updataUicap();
		return weiboInfoList;
	}

	// 更新文本
	@Override
	protected void onProgressUpdate(Iterator<InfoOfWeibo>... infoListIterators) {

		updataText.updataText(weiboInfoList);
	}

	// 执行完毕，resultlist中有完整的值
	@Override
	protected void onPostExecute(List<InfoOfWeibo> result) {
		super.onPostExecute(result);
		
	}
    
    //三个回调接口，用于任务完成后通知UI更新Text，图片等,更新text在activi实现，加载图片在Adapter实现
    public interface UpdataUserText{
    	public void updataText(List<InfoOfWeibo> infoList);
    }

    public interface UpdataUserCap{
    	public void updataCap(final String url,final ImageView cap);
    }
    
    public interface UpdataUserImage{
    	public void updataImage(final String url,final ImageView img);
    }
}
