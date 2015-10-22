package com.airisith.modle;

public class Contants {
	/* app key */
	public final static String APP_KEY = "3549075417";
	/* 回调页 */
	public final static String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
	/* 申请的权限 */
	public final static String SCOPE ="all";
	
	/* 获取获取当前登录用户及其所关注用户的最新微博url */
	public final static String STATUESES_HOME_TIMELINE_URL = "https://api.weibo.com/2/statuses/home_timeline.json";
	
	/* 获取最新的公共微博 */
	public final static String STATUESES_PUBLIC_TIMELINE_URL = "https://api.weibo.com/2/statuses/public_timeline.json";
	/*发送微博*/
	public final static String STATUESES_UPDATA_URL = "https://api.weibo.com/2/statuses/update.json";
	
	/* 发带有图片的微博 */
	public final static String STATUESES_UPDATA_WITH_IMG_URL = "https://upload.api.weibo.com/2/statuses/upload.json";
	
	/* 查询token信息 */
	public final static String GET_TOKEN_INFO_URL = "https://api.weibo.com/oauth2/get_token_info";
	
	/* 获取评论列表 */
	public final static String GET_COMMENTS_LIST_URL = "https://api.weibo.com/2/comments/show.json";
	
	/* 获取用户信息 */
	public final static String GET_USER_INFO_URL = "https://api.weibo.com/2/users/show.json";
	
	/* 获取当前登陆用户信息 */
	public final static String GET_CURRENT_USER_INFO_URL ="https://api.weibo.com/2/account/get_uid.json";
	
	/* 获取用户发布的微博,现在只能获取当前用户的微博 */
	public final static String GET_USER_WEIBO_URL = "https://api.weibo.com/2/statuses/user_timeline.json";
	
	/* 评论微博URL */
	public final static String COMMENT_WEIBO_URL = "https://api.weibo.com/2/comments/create.json";
	
	/* 获取评论我的消息 */
	public final static String GET_MY_COMMENTS_URL = "https://api.weibo.com/2/comments/timeline.json";
	
	/* 获取各种消息未读数 */
	public final static String GET_MESSAGE_COUNT_URL = "https://rm.api.weibo.com/2/remind/unread_count.json";
	
	/* 搜索话题下的微博:需要高级权限，暂未实现 */
	public final static String SEARCH_TOPIC_WEIBOLIST_URL = "https://api.weibo.com/2/search/topics.json";
	
	/**/
	
	/* 广播Action */
	/* HOME 页button点击事件 */
	public final static String ACTION_HOME_BUTTON_HOME = "airisith.button.home";
	public final static String ACTION_HOME_BUTTON_MESSAGE = "airisith.button.message";
	public final static String ACTION_HOME_BUTTON_ADD = "airisith.button.add";
	public final static String ACTION_HOME_BUTTON_FIND = "airisith.button.find";
	public final static String ACTION_HOME_BUTTON_USER = "airisith.button.psnl";
	
	/* Adapter的view点击事件 */
	public final static String ACTION_WEIBO_VIEW_INFO = "airisith.weiboview.info";
	public final static String ACTION_WEIBO_VIEW_USER = "airisith.weiboview.user";
	public final static String ACTION_WEIBO_VIEW_COMMENT = "airisith.weiboview.comment";
	
	/* 图片加载完成 */
	public final static String ACTION_IMG_LOAD_SUCCESS = "airisith.weiboview.loadimgsuccess";
	
}
