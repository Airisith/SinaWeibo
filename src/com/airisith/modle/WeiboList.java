package com.airisith.modle;


public class WeiboList {

	private WeiboListData data;

	public WeiboList(WeiboListData data) {
		super();
		this.data = data;
	}

	public WeiboList() {
		super();
	}

	public WeiboListData getData() {
		return data;
	}

	public void setData(WeiboListData data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "WeiBoList [data=" + data + "]";
	}
}
