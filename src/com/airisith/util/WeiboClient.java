package com.airisith.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ParseException;
import android.util.Log;
import android.widget.EditText;

import com.airisith.modle.CommentsInfo;
import com.airisith.modle.Contants;
import com.airisith.modle.NewMsg;
import com.airisith.modle.User;
import com.airisith.modle.User.UserInfo;
import com.airisith.modle.WeiboData;
import com.airisith.modle.WeiboData.InfoOfWeibo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

public class WeiboClient {

	private static final String TAG = "WeiboClient";
	
	/**
	 * 发送微博
	 * @param token
	 * @param editText
	 * @return
	 */
	public static Boolean sendWeibo(Oauth2AccessToken token, EditText editText, byte[] imgBuffer)
	{
		HttpPost postMethod;
		
		//组织post参数:此处只实现最简单的发送消息，只填两个参数，其他见http://open.weibo.com/wiki/2/statuses/friends_timeline
		HttpClient httpClient = new DefaultHttpClient();
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
        String messageString = editText.getText().toString();
        params.add(new BasicNameValuePair("status", messageString));
        params.add(new BasicNameValuePair("access_token", token.getToken()));
        //判断是否有图片，选择不同的接口
        if (null != imgBuffer) {
        	Log.w(TAG, "Image file include!");
			params.add(new BasicNameValuePair("pic", imgBuffer.toString()));
			postMethod = new HttpPost(Contants.STATUESES_UPDATA_WITH_IMG_URL);
        }else {
        	Log.w(TAG, "No images select!");
        	postMethod = new HttpPost(Contants.STATUESES_UPDATA_URL);
		}
        	       	            
        try {
            postMethod.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
            HttpResponse httpResponse = httpClient.execute(postMethod);

            //将返回结果转为字符串，通过文档可知返回结果为json字符串，结构请参考文档
            String resultStr=EntityUtils.toString(httpResponse.getEntity());
            Log.w(TAG, resultStr);

            //从json字符串中建立JSONObject
            JSONObject resultJson = new JSONObject(resultStr);

            //如果发送微博失败的话，返回字段中有"error"字段，通过判断是否存在该字段即可知道是否发送成功
            if (resultJson.has("error")) {
            	return false;
            } else {
            	return true;
            }

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
        
        return false;
    }	
	
	/**
	 * 评论一条微博
	 * @param token ：OauthToken
	 * @param editText ：输入文字的控件
	 * @return true:评论成功  ，false:评论失败
	 */
	public static Boolean commentWeibo(Oauth2AccessToken token, EditText editText, Long weiboId)
	{
		HttpPost postMethod;
		
		//组织post参数:此处只实现最简单的发送消息，只填两个参数，其他见http://open.weibo.com/wiki/2/statuses/friends_timeline
		HttpClient httpClient = new DefaultHttpClient();
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
        String messageString = editText.getText().toString();
        params.add(new BasicNameValuePair("comment", messageString));
        params.add(new BasicNameValuePair("access_token", token.getToken()));
        params.add(new BasicNameValuePair("id", weiboId.toString()));
       
    	Log.w(TAG, "No images select!");
    	postMethod = new HttpPost(Contants.COMMENT_WEIBO_URL);
		
        	       	            
        try {
            postMethod.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
            HttpResponse httpResponse = httpClient.execute(postMethod);

            //将返回结果转为字符串，通过文档可知返回结果为json字符串，结构请参考文档
            String resultStr=EntityUtils.toString(httpResponse.getEntity());
            Log.w(TAG, resultStr);

            //从json字符串中建立JSONObject
            JSONObject resultJson = new JSONObject(resultStr);

            //如果发送微博失败的话，返回字段中有"error"字段，通过判断是否存在该字段即可知道是否发送成功
            if (resultJson.has("error")) {
            	return false;
            } else {
            	return true;
            }

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
        
        return false;
    }
	
	/**
	 * 查询accesstoken是否过期
	 * @param context
	 * @param token
	 * @return
	 */
	public static Boolean isTokenOverTime(Context context, Oauth2AccessToken token){
				//组织post参数:此处只实现最简单的发送消息，只填两个参数，其他见http://open.weibo.com/wiki/2/statuses/friends_timeline
        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("access_token", token.getToken()));
        try {
        	JSONObject resultJSON = doPost(context, params, Contants.GET_TOKEN_INFO_URL);
            try {
    			String timeRest = resultJSON.getString("expire_in");
    			Log.w(TAG, "Token rest time :"+ timeRest);
    			long time =Integer.parseInt(timeRest);
    			if (time < 1000) {
    				return false;
    			}
    		} catch (JSONException e) {
    			e.printStackTrace();
    		}
		} catch (Exception e) {
		}
		return true;
	}
	
	/**
	 * 使用psot方法发送http请求
	 * @param context
	 * @param params
	 * @param baseUrl
	 * @return
	 */
	public static JSONObject doPost(Context context, List<BasicNameValuePair> params, String baseUrl)
	{
		JSONObject resultJson = null;
		HttpClient httpClient = new DefaultHttpClient();

        //传入post方法的请求地址，即发送微博的api接口
        HttpPost postMethod = new HttpPost(baseUrl);
        try {
            postMethod.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
            HttpResponse httpResponse = httpClient.execute(postMethod);

            //将返回结果转为字符串，通过文档可知返回结果为json字符串，结构请参考文档
            String resultStr=EntityUtils.toString(httpResponse.getEntity());
            Log.w(TAG, resultStr);

            //从json字符串中建立JSONObject
            resultJson = new JSONObject(resultStr);

            //如果发送微博失败的话，返回字段中有"error"字段，通过判断是否存在该字段即可知道是否发送成功
            if (resultJson.has("error")) {
                Log.w(TAG, "post success");
            } else {
            	Log.w(TAG, "post fali");
            }

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
	 * 获取微博列表,page为页码，1-->, 每页20条
	 * @param myToken
	 * @param baseUrl
	 * @param WeiboInfoList
	 * @param page
	 * @return
	 */
	public List<WeiboData> getWeiboList(Oauth2AccessToken myToken, String baseUrl ,List<InfoOfWeibo> WeiboInfoList, int page){
		
		List<WeiboData> weiboList = new ArrayList<WeiboData>();
		WeiboData data;
		JSONObject homeTimelineJson = WeiboData.getWeiboList(myToken, baseUrl, page);
		try {
			Log.w(TAG,"get home timeline success:");
		} catch (Exception e) {
		}
		for(int i=0; i<20; i++){
			if (null != homeTimelineJson) {
				// 每次都要新建一个对象，因为它是引用类型，否则每次操作的是同一个对象，保存的也是同一个对象
				InfoOfWeibo weiboinfo = new InfoOfWeibo();
				data = getWeiboData(homeTimelineJson, weiboinfo, i);
				if (null != data) {
					weiboList.add(data);
					WeiboInfoList.add(weiboinfo);
				}
			}
		}
		return weiboList;
	}
	
	/**
	 * 获取微博列表,page为页码，1-->, 每页20条
	 * @param myToken
	 * @param baseUrl
	 * @param WeiboInfoList
	 * @param page
	 * @return
	 */
	public List<WeiboData> getUserWeiboList(Oauth2AccessToken myToken, long userId ,List<InfoOfWeibo> WeiboInfoList, int page){
		
		List<WeiboData> weiboList = new ArrayList<WeiboData>();
		WeiboData data;
		JSONObject userTimelineJson = WeiboData.getUserWeiboList(myToken, userId, page);
		try {
			Log.w(TAG,"get home timeline success:" + userTimelineJson.toString());
		} catch (Exception e) {
		}
		for(int i=0; i<20; i++){
			if (null != userTimelineJson) {
				// 每次都要新建一个对象，因为它是引用类型，否则每次操作的是同一个对象，保存的也是同一个对象
				InfoOfWeibo weiboinfo = new InfoOfWeibo();
				data = getWeiboData(userTimelineJson, weiboinfo, i);
				if (null != data) {
					weiboList.add(data);
					WeiboInfoList.add(weiboinfo);
				}
			}
		}
		return weiboList;
	}
	
	/**
	 * 获每条取微博基本信息，这里因为使用了weiboData，其中的数据解析不方便，所以要将数据放在infoofdata里
	 * @param timeline
	 * @param weiboinfo
	 * @param position
	 * @return
	 */
	public WeiboData getWeiboData(JSONObject timeline, InfoOfWeibo weiboinfo, int position){
		try {
			WeiboData weiboData = WeiboData.getWeiboData(timeline.toString(), position);
			weiboinfo.setName(weiboData.getUser().getName());
			weiboinfo.settextInfo(weiboData.getText());
			weiboinfo.setTimestamp(weiboData.getCreated_at());
			weiboinfo.setCapUrlString(weiboData.getUser().getProfile_image_url());
			weiboinfo.setReposts(weiboData.getReposts_count());
			weiboinfo.setComments(weiboData.getComments_count());
			weiboinfo.setAttitudes(weiboData.getAttitudes_count());
			weiboinfo.setSource(weiboData.getSource());
			weiboinfo.setFavorited(weiboData.getFavorited());
			weiboinfo.setId(weiboData.getId());
			weiboinfo.setUserId(weiboData.getUser().getId());
			
			if (null != weiboData.getThumbnail_pic()) {
				weiboinfo.setImgUrl(weiboData.getBmiddle_pic());
			}
			getRetweeted(weiboData, weiboinfo);
			Log.w(TAG, "WeiboInfo:"+weiboinfo.getName()+":"+weiboinfo.gettextInfo());
			return weiboData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取转发自---微博的text---递归调用
	 * @param weiboData
	 * @param weiboinfo
	 */
	public void getRetweeted(WeiboData weiboData, InfoOfWeibo weiboinfo)
	{
		if(null != weiboData.getRetweeted_status()){
			String str = weiboinfo.gettextInfo();
			str = str + "//@"+weiboData.getRetweeted_status().getUser().getName()
					+ ":" +weiboData.getRetweeted_status().getText();
			weiboinfo.settextInfo(str);
			if (null != weiboData.getThumbnail_pic()) {
				weiboinfo.setImgUrl(weiboData.getThumbnail_pic());
			}
			
			getRetweeted(weiboData.getRetweeted_status(), weiboinfo);
		}
	}
	
	/**
	 * 获取评论列表的对象
	 * @param myToken
	 * @param weiboId
	 * @param page
	 * @return
	 */
	public List<CommentsInfo> getCommentsList(Oauth2AccessToken myToken, Long weiboId, int page){
			
		List<CommentsInfo> commentList = new ArrayList<CommentsInfo>();
		JSONObject commentsJson = CommentsInfo.getCommentsListJson(myToken, weiboId, page);
		
		if (null != commentsJson) {
			for(int i=0; i<50; i++){
				CommentsInfo cmDataInfo;
				// 将得到的Json字符串中的信息拿出来保存到list中		
				try {
					cmDataInfo = CommentsInfo.getCommentsData(commentsJson.toString(), i);
					if (null != cmDataInfo) {
						commentList.add(cmDataInfo);
					} else {
						break;
					}
				} catch (Exception e) {
				}				
			}
		}

		return commentList;
	}
	
	/**
	 * 获取用户信息详情
	 * @param token
	 * @param userId
	 * @return
	 */
	public static UserInfo getUserInfo(Oauth2AccessToken token, Long userId){
		UserInfo userInfo = null;
		JSONObject userInfoJson = User.getUserInfoJson(token, userId);
		try {
			userInfo = User.getUserInfo(userInfoJson.toString());
		} catch (Exception e) {
			Log.w(TAG, "获取用户信息失败");
		}
		return userInfo;
	}
	
	public static long getCurrentUserId(Oauth2AccessToken token){
		JSONObject resultJson = null;
		HttpResponse httpResponse;
        HttpClient httpClient = new DefaultHttpClient();
        
        //传入get方法的请求地址和参数
        HttpGet getMethod = new HttpGet(Contants.GET_CURRENT_USER_INFO_URL + "?"+
        		"access_token=" + token.getToken());
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
		try {
			return resultJson.getLong("uid");
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		return 0;
	}
	
	/**
	 * 获取所有新消息数,UID为当前登陆用户
	 * @return
	 */
	public static NewMsg getNewMessageCounts(Oauth2AccessToken token, Long currentUID){
		NewMsg newMsg = new NewMsg();
		JSONObject resultJson = null;
		HttpResponse httpResponse;
        HttpClient httpClient = new DefaultHttpClient();
        
        //传入get方法的请求地址和参数
        HttpGet getMethod = new HttpGet(Contants.GET_MESSAGE_COUNT_URL + "?"+
        		"access_token=" + token.getToken() + "&true=" + currentUID);
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
		
        // 保存消息数
        try {
        	newMsg.setStatus(resultJson.getInt("status"));
		} catch (Exception e) {
		}
        try {
        	newMsg.setFollower(resultJson.getInt("follower"));
		} catch (Exception e) {
		}
        try {
        	newMsg.setCmt(resultJson.getInt("cmt"));
		} catch (Exception e) {
		}
        try {
        	newMsg.setDm(resultJson.getInt("dm"));
		} catch (Exception e) {
		}
        try {
        	newMsg.setMention(resultJson.getInt("mention_status") + resultJson.getInt("mention_cmt"));
		} catch (Exception e) {
		}
		
		return newMsg;
	}
	
	/**
	 * 获取我的评论列表的对象
	 * @param myToken
	 * @param page
	 * @return
	 */
	public List<CommentsInfo> getMyCommentsList(Oauth2AccessToken myToken, int page){
			
		List<CommentsInfo> commentList = new ArrayList<CommentsInfo>();
		JSONObject commentsJson = CommentsInfo.getMyCommentsListJson(myToken, page);
		
		if (null != commentsJson) {
			for(int i=0; i<50; i++){
				CommentsInfo cmDataInfo;
				// 将得到的Json字符串中的信息拿出来保存到list中		
				try {
					cmDataInfo = CommentsInfo.getCommentsData(commentsJson.toString(), i);
					if (null != cmDataInfo) {
						commentList.add(cmDataInfo);
						Log.w(TAG, "comment："+cmDataInfo.getUser_name()+":"+cmDataInfo.getText());
					} else {
						break;
					}
				} catch (Exception e) {
				}				
			}
		}

		return commentList;
	}

	
}
