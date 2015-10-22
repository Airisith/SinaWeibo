package com.airisith.modle;

import com.airisith.util.WeiboClient;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

public class NewMsg {

	private int status; //新微博
	private int follower; //新粉丝
	private int cmt; //新评论
	private int dm; //新私信
	private int mention; //新提及我
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getFollower() {
		return follower;
	}
	public void setFollower(int follower) {
		this.follower = follower;
	}
	public int getCmt() {
		return cmt;
	}
	public void setCmt(int cmt) {
		this.cmt = cmt;
	}
	public int getMention() {
		return mention;
	}
	public void setMention(int mention) {
		this.mention = mention;
	}
	public int getDm() {
		return dm;
	}
	public void setDm(int dm) {
		this.dm = dm;
	}
	
	/**
	 * 
	 * @param token
	 * @param currentUID
	 * @return
	 */
	public NewMsg getNewMessagesCount(Oauth2AccessToken token, Long currentUID){
		return WeiboClient.getNewMessageCounts(token, currentUID);
	}
}
