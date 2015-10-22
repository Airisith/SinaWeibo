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

import android.net.ParseException;
import android.util.Log;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;


public class CommentsInfo {
	
	private static final String TAG = "CommentsInfo";
	
	private Long id = null;
	private String user_img = null;
	private String user_name = null;
	private Long user_id = null;
	private String timeStamp = null;
	private String source = null;
	private String text = null;
	private int attitude = 0;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUser_img() {
		return user_img;
	}
	public void setUser_img(String user_img) {
		this.user_img = user_img;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public String getTimeStamp() {
		return timestampInterFilter(timeStamp);
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getSource() {
		return sourceInterfilter(source);
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getAttitude() {
		return attitude;
	}
	public void setAttitude(int attitude) {
		this.attitude = attitude;
	}
	
	//字符串解析方法，解析source段，解析creat_at段
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
	
	/**
	 * 获取一条微博的评论列表的json对象,每页50条
	 * @param token
	 * @param weiboId
	 * @param page
	 * @return
	 */
	public static JSONObject getCommentsListJson(Oauth2AccessToken token, Long weiboId ,int page)
	{
		JSONObject resultJson = null;
		HttpResponse httpResponse;
	    HttpClient httpClient = new DefaultHttpClient();
	    
	    //传入get方法的请求地址和参数
	    HttpGet getMethod = new HttpGet(Contants.GET_COMMENTS_LIST_URL + "?"+
	    		"access_token=" + token.getToken() +"&id=" +weiboId + "&page=" + page);
	    try {
	    	// execute返回一个响应对象
	        httpResponse = httpClient.execute(getMethod);
	        // 响应的内容对象
	        // 读取内容，用inputstream读取
	        String resultStr=EntityUtils.toString(httpResponse.getEntity());
	        resultJson = new JSONObject(resultStr);
	        
	        //如果发送微博失败的话，返回字段中有"error"字段，通过判断是否存在该字段即可知道是否发送成功
	        if (resultJson.has("error")) {
	            Log.w(TAG, "获取评论列表失败:"+resultJson.toString());
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
	 * 获取当前登录用户的最新评论包括接收到的与发出的
	 * @return
	 */
	/**
	 * 获取一条微博的评论列表的json对象,每页50条
	 * @param token
	 * @param weiboId
	 * @param page
	 * @return
	 */
	public static JSONObject getMyCommentsListJson(Oauth2AccessToken token,int page)
	{
		JSONObject resultJson = null;
		HttpResponse httpResponse;
	    HttpClient httpClient = new DefaultHttpClient();
	    
	    //传入get方法的请求地址和参数
	    HttpGet getMethod = new HttpGet(Contants.GET_MY_COMMENTS_URL + "?"+
	    		"access_token=" + token.getToken() + "&page=" + page);
	    try {
	    	// execute返回一个响应对象
	        httpResponse = httpClient.execute(getMethod);
	        // 响应的内容对象
	        // 读取内容，用inputstream读取
	        String resultStr=EntityUtils.toString(httpResponse.getEntity());
	        resultJson = new JSONObject(resultStr);
	        
	        //如果发送微博失败的话，返回字段中有"error"字段，通过判断是否存在该字段即可知道是否发送成功
	        if (resultJson.has("error")) {
	            Log.w(TAG, "获取我的评论列表失败");
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
	 * 获取微博评论信息
	 * @param statusesObj
	 * @return
	 */
	public static CommentsInfo getCommentsInfo(JSONObject statusesObj) {
	
		CommentsInfo commentInfo = new CommentsInfo();
		// 保存数据 // statusesObj.getString("created_at")
		try {
			commentInfo.setTimeStamp(statusesObj.getString("created_at")); // 获取created_at数据：发表时间
		} catch (Exception e) {
		}
		try {
			commentInfo.setId(statusesObj.getLong("id"));
	
		} catch (Exception e) {
		}
		try {
			commentInfo.setSource(statusesObj.getString("source"));
		} catch (Exception e) {
		}
		try {
			commentInfo.setText(statusesObj.getString("text"));
		} catch (Exception e) {
		}
	
		// 获取user信息,weibodata中的user未实例化，先创user建实例对象
		String user;
		try {
			user = statusesObj.getString("user");
			JSONObject userObj = new JSONObject(user); // 将其转化为JSONObject
			try {
				commentInfo.setUser_name(userObj.getString("name"));
			} catch (Exception e) {
			}
			try {
				commentInfo.setUser_img(userObj.getString("profile_image_url"));
			} catch (Exception e) {
			}
			try {
				commentInfo.setUser_id(userObj.getLong("id"));
			} catch (Exception e) {
			}
	
		} catch (JSONException e1) {
			e1.printStackTrace();
		} 
		
		// 获取retweeted_status信息，递归调用,由测试发现原来这下面的值其实就是原微博，所以省去
		// try {
		// String retweetedStatus = statusesObj.getString("status");
		// if(null != retweetedStatus){
		// String textStr = commentInfo.getText();
		// JSONObject retweetedObj = new JSONObject(retweetedStatus);
		// CommentsInfo retweeteData;
		// if(null != (retweeteData = getCommentsInfo(retweetedObj))){
		// commentInfo.setText(textStr + "//@" + retweeteData.getUser_name()+
		// ":" + retweeteData.getText());
		// }
		// }
		// } catch (Exception e) {
		// }
		return commentInfo;
	}
	/**
	 * 将数据保存到weibodata中
	 * @param commentString
	 * @param commentsPosition
	 * @return
	 */
	public static CommentsInfo getCommentsData(String commentString,
			int commentsPosition) {
		CommentsInfo commentsInfo = null;
		try {
			// 获取最外层json对象
			JSONObject weibolist = new JSONObject(commentString);
			// 获取 key为comments的json数组：评论数组
			try {
				JSONArray commentsArr = weibolist.getJSONArray("comments");
				// 这里获取一条评论，数组中的某一个json对象
				JSONObject commentsObj = commentsArr
						.getJSONObject(commentsPosition); // 这里的0代表的就是第一个{}，以此类推
				commentsInfo = CommentsInfo.getCommentsInfo(commentsObj);
				Log.w(TAG, "get commentInfo"+commentsPosition);
			} catch (Exception e) {
			}
	
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			return commentsInfo;
		}
	}
}
