package com.airisith.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.airisith.Views.HomeView;
import com.airisith.modle.WeiboData;
import com.airisith.modle.WeiboData.InfoOfWeibo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import android.os.AsyncTask;
import android.widget.ImageView;

public class GetWeibolistTask extends
		AsyncTask<Oauth2AccessToken, Iterator<InfoOfWeibo>, List<InfoOfWeibo>> {

	private List<InfoOfWeibo> weiboInfoList = null;
	private ArrayList<HashMap<String, Object>> list = null;
	private ArrayList<WeiboData> weiboDataList = null;
	private UpdataTextCallback updataText;
	private HomeView activity;
	private String groupUrl;
	private int page;

	/**
	 * 请求微博数据的异步任务
	 * @param act： Activity
	 * @param list：存放	微博数据的list，现在使用weiboInfoList，此处无用
	 * @param groupTimelineUrl ：请求的GoupUrl，不同分组对应不同URL
	 * @param updataText： 回调接口，更新UI
	 * @param weibPage :更新第几页,从第一页开始
	 */
	public GetWeibolistTask(HomeView act,
			ArrayList<HashMap<String, Object>> list, String groupTimelineUrl,
			UpdataTextCallback updataText, int weiboPage) {
		// 传递UI
		this.activity = act;
		// 创建list准备显示更新UI中的listView
		this.list = list;
		// 创建data的list用来保存收到的数据
		weiboInfoList = new ArrayList<InfoOfWeibo>();
		this.updataText = updataText;
		this.groupUrl = groupTimelineUrl;
		this.page = weiboPage;
	}

	@Override
	protected List<InfoOfWeibo> doInBackground(Oauth2AccessToken... params) {
		InfoOfWeibo dataInfo;
		Oauth2AccessToken token = params[0];
		WeiboClient weiboClient = new WeiboClient();
		weiboDataList = (ArrayList<WeiboData>) weiboClient.getWeiboList(token,
				groupUrl, weiboInfoList, page);

		// 将所有微博信息对象放在迭代器中
		Iterator<InfoOfWeibo> iteratorInfoList = weiboInfoList.iterator();
		// 更新text信息
		publishProgress(iteratorInfoList);
		// 在Activity中更新图片
		// activity.updataUicap();
		return weiboInfoList;
	}

	// 更新文本
	@Override
	protected void onProgressUpdate(Iterator<InfoOfWeibo>... infoListIterators) {
		// 通知activity更新text Ui
		updataText.updataText(weiboInfoList);
		// activity.updataUiText();
	}

	// 执行完毕，resultlist中有完整的值
	@Override
	protected void onPostExecute(List<InfoOfWeibo> result) {
		super.onPostExecute(result);

	}

	// 三个回调接口，用于任务完成后通知UI更新Text，图片等,更新text在activi实现，加载图片在Adapter实现
	public interface UpdataTextCallback {
		public void updataText(List<InfoOfWeibo> infoList);
	}

	public interface UpdataCapCallback {
		public void updataCap(final String url, final ImageView cap);
	}

	public interface UpdataImageCallback {
		public void updataImage(final String url, final ImageView img);
	}
}
