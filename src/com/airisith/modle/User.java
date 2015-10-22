package com.airisith.modle;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ParseException;
import android.util.Log;

import com.airisith.util.WeiboClient;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;


public class User {
	private static final String TAG = "User";
	
	private String screen_name;
	private String name;
	private String profile_image_url;
	private Long id;
	
	public User() {
		
	}
	
	public String getScreen_name(){
		return screen_name;
	}
	public void setScreen_name(String screen_name){
		this.screen_name = screen_name;
	}
	
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	
	public String getProfile_image_url(){
		return profile_image_url;
	}
	public void serProfile_image_url(String profile_image_url){
		this.profile_image_url = profile_image_url;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * 获取用户信息
	 * @param token
	 * @param userId
	 * @return
	 */
	public static JSONObject getUserInfoJson(Oauth2AccessToken token, Long userId){
		
		JSONObject resultJson = null;
		HttpResponse httpResponse;
	    HttpClient httpClient = new DefaultHttpClient();
	    
	    //传入get方法的请求地址和参数
	    HttpGet getMethod = new HttpGet(Contants.GET_USER_INFO_URL + "?"+
	    		"access_token=" + token.getToken() +"&uid=" +userId);
	    try {
	    	// execute返回一个响应对象
	        httpResponse = httpClient.execute(getMethod);
	        // 响应的内容对象
	        // 读取内容，用inputstream读取
	        String resultStr=EntityUtils.toString(httpResponse.getEntity());
	        resultJson = new JSONObject(resultStr);
	        
	        //如果发送微博失败的话，返回字段中有"error"字段，通过判断是否存在该字段即可知道是否发送成功
	        if (resultJson.has("error")) {
	            Log.w(TAG, "获取用户信息失败");
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
	 * 获取用户信息对象
	 * @param userInfoJson
	 * @return
	 */
	public static UserInfo getUserInfo(String userInfoJsonString){
		Log.w(TAG, "get user's info JSON success");
		UserInfo userInfo = new UserInfo();
		try {
			JSONObject userJsonObject = new JSONObject(userInfoJsonString);
			try {
				userInfo.setName(userJsonObject.getString("name"));
			} catch (Exception e) {
			}
			try {
				userInfo.setScreen_name(userJsonObject.getString("screen_name"));
			} catch (Exception e) {
			}
			try {
				userInfo.setDescription(userJsonObject.getString("description"));
			} catch (Exception e) {
			}
			try {
				userInfo.setProfile_image_url(userJsonObject.getString("profile_image_url"));
			} catch (Exception e) {
			}
			try {
				userInfo.setStatuses_count(userJsonObject.getInt("statuses_count"));
			} catch (Exception e) {
			}
			try {
				userInfo.setFollowers_count(userJsonObject.getInt("followers_count"));
			} catch (Exception e) {
			}
			try {
				userInfo.setBi_followers_count(userJsonObject.getInt("bi_followers_count"));
			} catch (Exception e) {
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return userInfo;
	}
	
	/**
	 * 用户详情：用于显示的User部分有用信息
	 * @author Administrator
	 *
	 */
	public static class UserInfo{
		private String screen_name; //昵称
		private String name; //友好名称
		private String description; //个人描述
		private String profile_image_url; //头像地址
		private int followers_count; //关注数
		private int bi_followers_count; //被关注数
		private int statuses_count; //微博数
		public String getScreen_name() {
			return screen_name;
		}
		public void setScreen_name(String screen_name) {
			this.screen_name = screen_name;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getProfile_image_url() {
			return profile_image_url;
		}
		public void setProfile_image_url(String profile_image_url) {
			this.profile_image_url = profile_image_url;
		}
		public int getFollowers_count() {
			return followers_count;
		}
		public void setFollowers_count(int followers_count) {
			this.followers_count = followers_count;
		}
		public int getBi_followers_count() {
			return bi_followers_count;
		}
		public void setBi_followers_count(int friends_count) {
			this.bi_followers_count = friends_count;
		}
		public int getStatuses_count() {
			return statuses_count;
		}
		public void setStatuses_count(int statuses_count) {
			this.statuses_count = statuses_count;
		}
		
	}
}
