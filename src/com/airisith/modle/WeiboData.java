package com.airisith.modle;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import android.net.ParseException;
import android.util.Log;

public class WeiboData {
	
	private static final String TAG = "WeiboData";
	
	private String created_at;
	private long id;
	private long mid;
	private String idstr;
	private String text;
	private String source;
	private Boolean favorited;
	private Boolean truncated;
	private String thumbnail_pic;
	private String bmiddle_pic;
	private String original_pic;
//	private Geo geo;
	private User user;
	private WeiboData retweeted_status;
	private int reposts_count;
	private int comments_count;
	private int attitudes_count;
//	private Visible visible;
//	private PicId pic_ids;
//	private Ad[] ad;
	
	public WeiboData(){
		this.user = new User();
	}
	public void newWeiboData(){
		this.retweeted_status = new WeiboData();
	}
	
	// 解析方法
	public String getCreated_at(){
		return created_at;
	}
	public void setCreated_at(String created_at){
		this.created_at = created_at;
	}
	
	public long getId(){
		return id;
	}
	public void setId(long id){
		this.id = id;
	}
	
	public long getMid(){
		return mid;
	}
	public void setMid(long mid){
		this.mid = mid;
	}
	
	public String getIdStr(){
		return idstr;
	}
	public void setIdStr(String idstr){
		this.idstr = idstr;
	}

	public String getText(){
		return text;
	}
	public void setText(String text){
		this.text = text;
	}
	
	public String getSource(){
		return source;
	}
	public void setSource(String source){
		this.source = source;
	}
	
	public Boolean getFavorited(){
		return favorited;
	}
	public void setFavorited(Boolean favorited){
		this.favorited = favorited;
	}
	
	public Boolean getTruncated(){
		return truncated;
	}
	public void setTruncated(Boolean truncated){
		this.truncated = truncated;
	}
	
	public String getThumbnail_pic(){
		return thumbnail_pic;
	}
	public void setThumbnail_pic(String thumbnail_pic){
		this.thumbnail_pic = thumbnail_pic;
	}
	
	public String getBmiddle_pic(){
		return bmiddle_pic;
	}
	public void setBmiddle_pic(String bmiddle_pic){
		this.bmiddle_pic = bmiddle_pic;
	}
	
	public String getOriginal_pic(){
		return original_pic;
	}
	public void setOriginal_pic(String original_pic){
		this.original_pic = original_pic;
	}
	
	public User getUser(){
		return user;
	}
	public void setUser(User user){
		this.user = user;
	}
	
	public WeiboData getRetweeted_status(){
		return retweeted_status;
	}
	public void setRetweeted_status(WeiboData retweeted_status){
		this.retweeted_status = retweeted_status;
	}
	
	public int getReposts_count(){
		return reposts_count;
	}
	public void setReposts_count(int reposts_count){
		this.reposts_count = reposts_count;
	}
	
	public int getComments_count(){
		return comments_count;
	}
	public void setComments_count(int comments_count){
		this.comments_count = comments_count;
	}
	
	public int getAttitudes_count(){
		return attitudes_count;
	}
	public void setAttitudes_count(int attitudes_count){
		this.attitudes_count = attitudes_count;
	}
	/**
	 * 获取当前登录用户及其所关注用户的最新微博，默认每页20个
	 * @param token
	 * @param baseUrl
	 * @param page
	 * @return JSONobject类型，内容为微博list的json类型字符串
	 */
	public static JSONObject getWeiboList(Oauth2AccessToken token, String baseUrl ,int page)
	{
		JSONObject resultJson = null;
		HttpResponse httpResponse;
	    HttpClient httpClient = new DefaultHttpClient();
	    
	    //传入get方法的请求地址和参数
	    HttpGet getMethod = new HttpGet(baseUrl + "?"+
	    		"access_token=" + token.getToken() + "&page=" +page);
	    try {
	    	// execute返回一个响应对象
	        httpResponse = httpClient.execute(getMethod);
	        // 响应的内容对象
	        // 读取内容，用inputstream读取
	        String resultStr=EntityUtils.toString(httpResponse.getEntity());
	        resultJson = new JSONObject(resultStr);
	
	        //如果发送微博失败的话，返回字段中有"error"字段，通过判断是否存在该字段即可知道是否发送成功
	        if (resultJson.has("error")) {
	            Log.w(TAG, "获取微博列表失败");
	        } 
	        //Log.i(TAG, resultJson.toString());
	        
	    } catch (UnsupportedEncodingException e) {
	        Log.w(TAG, e.getLocalizedMessage());
	    } catch (ClientProtocolException e) {
	        Log.w(TAG, e.getLocalizedMessage());
	    } catch (IOException e) {
	        Log.w(TAG, e.getLocalizedMessage());
	    } catch (ParseException e) {
	        Log.w(TAG, e.getLocalizedMessage());
	    } catch (JSONException e) {
	        Log.w(TAG, e.getLocalizedMessage());
	    }
	    return resultJson;
	}
	/**
	 * 获取指定用户最新微博，默认每页20个
	 * @param token
	 * @param baseUrl
	 * @param page
	 * @return JSONobject类型，内容为微博list的json类型字符串
	 */
	public static JSONObject getUserWeiboList(Oauth2AccessToken token, Long userId ,int page)
	{
		JSONObject resultJson = null;
		HttpResponse httpResponse;
	    HttpClient httpClient = new DefaultHttpClient();
	    
	    //传入get方法的请求地址和参数
	    HttpGet getMethod = new HttpGet(Contants.GET_USER_WEIBO_URL + "?"+
	    		"access_token=" + token.getToken() + "&uid=" +userId + "&page=" +page);
	    try {
	    	// execute返回一个响应对象
	        httpResponse = httpClient.execute(getMethod);
	        // 响应的内容对象
	        // 读取内容，用inputstream读取
	        String resultStr=EntityUtils.toString(httpResponse.getEntity());
	        resultJson = new JSONObject(resultStr);
	
	        //如果发送微博失败的话，返回字段中有"error"字段，通过判断是否存在该字段即可知道是否发送成功
	        if (resultJson.has("error")) {
	            Log.w(TAG, "获取微博列表失败");
	        } 
	        //Log.i(TAG, resultJson.toString());
	        
	    } catch (UnsupportedEncodingException e) {
	        Log.w(TAG, e.getLocalizedMessage());
	    } catch (ClientProtocolException e) {
	        Log.w(TAG, e.getLocalizedMessage());
	    } catch (IOException e) {
	        Log.w(TAG, e.getLocalizedMessage());
	    } catch (ParseException e) {
	        Log.w(TAG, e.getLocalizedMessage());
	    } catch (JSONException e) {
	        Log.w(TAG, e.getLocalizedMessage());
	    }
	    return resultJson;
	}
	/**
	 * 获取微博info数据
	 * @param statusesObj
	 * @return
	 */
	public static WeiboData getWeiboInfo(JSONObject statusesObj){
		
		WeiboData weiboData = new WeiboData();
//		if (statusesObj.has("error")) {
//			return null;
//		}
		//保存数据  // statusesObj.getString("created_at")
		try {
			weiboData.setCreated_at(statusesObj.getString("created_at")); //获取created_at数据：发表时间
		} catch (Exception e) {
		}
		try {
			weiboData.setId(statusesObj.getLong("id"));
		} catch (Exception e) {
		}
		try {
			weiboData.setMid(statusesObj.getLong("mid"));
		} catch (Exception e) {
		}
		try {
			weiboData.setIdStr(statusesObj.getString("idstr"));
		} catch (Exception e) {
		}
		try {
			weiboData.setText(statusesObj.getString("text"));
		} catch (Exception e) {
		}
		try {
			weiboData.setSource(statusesObj.getString("source"));
		} catch (Exception e) {
		}
		try {
			weiboData.setFavorited(statusesObj.getBoolean("favorited"));
		} catch (Exception e) {
		}
		try {
			weiboData.setTruncated(statusesObj.getBoolean("truncated"));
		} catch (Exception e) {
		}
		try {
			weiboData.setThumbnail_pic(statusesObj.getString("thumbnail_pic"));
		} catch (Exception e) {
		}
		try {
			weiboData.setBmiddle_pic(statusesObj.getString("bmiddle_pic"));
		} catch (Exception e) {
		}
		try {
			weiboData.setOriginal_pic(statusesObj.getString("original_pic"));
		} catch (Exception e) {
		}
		try {
			weiboData.setReposts_count(statusesObj.getInt("reposts_count"));
		} catch (Exception e) {
		}
		try {
			weiboData.setComments_count(statusesObj.getInt("comments_count"));
		} catch (Exception e) {
		}
		try {
			weiboData.setAttitudes_count(statusesObj.getInt("attitudes_count"));
		} catch (Exception e) {
		}
		
		
		//获取user信息,weibodata中的user未实例化，先创user建实例对象
		User usr = new User();
		String user;
		try {
			user = statusesObj.getString("user");
			JSONObject userObj = new JSONObject(user); // 将其转化为JSONObject
			try {
				usr.setName(userObj.getString("name"));
			} catch (Exception e) {
			}
			try {
				usr.setScreen_name(userObj.getString("screen_name"));
			} catch (Exception e) {
			}
			try {
				usr.serProfile_image_url(userObj.getString("profile_image_url"));
			} catch (Exception e) {
			}
			try {
				usr.setId(userObj.getLong("id"));
			} catch (Exception e) {
			}
			
			weiboData.setUser(usr);
		} catch (JSONException e1) {
			e1.printStackTrace();
		} // 获取位置3的值
		
		//获取retweeted_status信息，递归调用
		try {
			String retweetedStatus = statusesObj.getString("retweeted_status");
			if(null != retweetedStatus){
				//先创建retweetedStatus实例（WeiboData）
				weiboData.newWeiboData();
				JSONObject retweetedObj = new JSONObject(retweetedStatus);
				WeiboData retweeteData;
				if(null != (retweeteData = getWeiboInfo(retweetedObj))){
					weiboData.setRetweeted_status(retweeteData);
				}
			}
		} catch (Exception e) {
		}
		return weiboData;
	}
	/**
	 * 将数据保存到weibodata中
	 * @param timeline
	 * @param weiboPosition
	 * @return
	 */
	public static WeiboData getWeiboData(String timeline, int weiboPosition){
		WeiboData weiboData = null;
		try {
			//获取最外层json对象
			JSONObject weibolist = new JSONObject(timeline);
			//获取 key为statuses的json数组：微博数据，包含多条微博数据
			JSONArray statusesArr = weibolist.getJSONArray("statuses");
			//这里获取一条微博数据，数组中的某一个json对象
			JSONObject statusesObj = statusesArr.getJSONObject(weiboPosition);  // 这里的0代表的就是第一个{}，以此类推
			if (null != statusesObj) {
				weiboData = WeiboData.getWeiboInfo(statusesObj);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		finally{
			return weiboData;
		}
	}
	/**
	 * 获取微博list
	 * @param timeline
	 * @return
	 */
	public static Boolean getWeiboList(String timeline){
		try {
			//获取最外层json对象
			JSONObject weibolist = new JSONObject(timeline);
			//获取 key为statuses的json数组：微博数据，包含多条微博数据
			JSONArray statusesArr = weibolist.getJSONArray("statuses");
			//这里获取第一条微博数据
			JSONObject firstStatusesObj = statusesArr.getJSONObject(0);  // 这里的0代表的就是第一个{}，以此类推
			String timeString = firstStatusesObj.getString("created_at"); //获取created_at数据：发表时间
			String textString = firstStatusesObj.getString("text");
			
			//获取user信息
			String user = firstStatusesObj.getString("user"); // 获取位置3的值
			JSONObject userObj = new JSONObject(user); // 将其转化为JSONObject
			String name = userObj.getString("name"); // 使用get方法获取数据
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * 一条微博信息的部分信息，用于显示到UI等操作，WeiboData太复杂，不再使用
	 * @author Administrator
	 *
	 */
	public static class InfoOfWeibo{
	 	private String name = null;
	 	private String timestamp = null;
	 	private String textInfo = null;
	 	private String capUrlString = null;
	 	private String imgUrl = null;
	 	private int reposts = 0;
	 	private int comments = 0;
	 	private int attitudes = 0;
	 	private String sImgUrl = null;
	 	private String lImageUrl =null;
	 	private String source =null;
	 	private Boolean favorited =null;
	 	private Long id = null;
	 	private Long userId = null;
	 	
	 	public void setName(String name){
	 		this.name = name;
	 	}
	 	public String getName(){
	 		return name;
	 	}
	 	
	 	public void setTimestamp(String timestamp){
	 		this.timestamp = timestamp;
	 	}
	 	public String getTimestamp(){
	 		return timestampInterFilter(timestamp);
	 	}
	 	
	 	public void settextInfo(String textInfo){
	 		this.textInfo = textInfo;
	 	}
	 	public String gettextInfo(){
	 		return textInfo;
	 	}
	 	
	 	public void setCapUrlString(String capUrlString){
	 		this.capUrlString = capUrlString;
	 	}
	 	public String getCapUrlString(){
	 		return capUrlString;
	 	}
	 	
	 	public void setImgUrl(String imgUrl){
	 		this.imgUrl = imgUrl;
	 	}
	 	public String getImgUrl(){
	 		return imgUrl;
	 	}
	 	
	 	public void setReposts(int reposts){
	 		this.reposts = reposts;
	 	}
	 	public int getReposts(){
	 		return reposts;
	 	}
	 	
	 	public void setComments(int comments){
	 		this.comments = comments;
	 	}
	 	public int getComments(){
	 		return comments;
	 	}
	 	
	 	public void setAttitudes(int attitudes){
	 		this.attitudes = attitudes;
	 	}
	 	public int getAttitudes(){
	 		return attitudes;
	 	}
		public String getsImgUrl() {
			return sImgUrl;
		}
		public void setsImgUrl(String sImgUrl) {
			this.sImgUrl = sImgUrl;
		}
		public String getlImageUrl() {
			return lImageUrl;
		}
		public void setlImageUrl(String lImageUrl) {
			this.lImageUrl = lImageUrl;
		}
		public String getSource() {
			return sourceInterfilter(source);
		}
		public void setSource(String source) {
			this.source = source;
		}
		public Boolean getFavorited() {
			return favorited;
		}
		public void setFavorited(Boolean favorited) {
			this.favorited = favorited;
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public Long getUserId() {
			return userId;
		}
		public void setUserId(Long userId) {
			this.userId = userId;
		}
		
		/**
		 * 字符串解析方法，解析source段，解析creat_at段
		 * @param str
		 * @return
		 */
		public String sourceInterfilter(String str){
			//字段格式：<...>有用信息<.>
			String newString = null;
			try {
				int startPosition = str.indexOf(">", 0);
				int endPosition = str.indexOf("<", startPosition);
				newString = str.substring(startPosition+1, endPosition);
			} catch (Exception e) {
				return "source error";
			}
				
			return newString;
		}
		public String timestampInterFilter(String str){
			//字段格式：月  日  时间  +...  年
			String newString = null;
			try {
				int startPosition = str.indexOf("+", 0);
				int endPosititon = str.indexOf(" ", startPosition);
				newString = str.substring(endPosititon+1) + " " + str.subSequence(0, startPosition-1);
			} catch (Exception e) {
				return "time error";
			}
					
			return newString;
		}
	}
}
