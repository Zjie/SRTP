package edu.ustb.info10.vo;

/**
 * 每个线程都会从朱进程里面拷贝一份最新的PostApi，把自己的access_token设置到拷贝出来的PostApi中
 * 
 * @author joller
 * 
 */
public class PostVo {
	private String url = "https://api.weibo.com/2/friendships/friends.json";
	private long uid;
	private int count;
	private long cursor;
	private String access_token;
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public long getCursor() {
		return cursor;
	}
	public void setCursor(long cursor) {
		this.cursor = cursor;
	}
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

}
