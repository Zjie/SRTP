package edu.ustb.info10.vo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import edu.ustb.info10.dbsource.domain.User;

public class FriendsDto implements Serializable {

	private static final long serialVersionUID = 390613019147219769L;
	private String name = "";
	private long id;
	private int next_cursor = 0;
	private int previous_cursor;
	private int total_number = 1;
	private Set<User> users = new HashSet<User>();
	private User user;
	private static String url = "https://api.weibo.com/2/friendships/friends.json";
	private int count = 200;
	private String access_token;
	// 当前第N个用户
	private int num;

	@SuppressWarnings("static-access")
	public static FriendsDto phaseUserInfo(String json) {
		FriendsDto friendDsto = new FriendsDto();
		JSONObject jsonObject = JSONObject.fromObject(json);
		friendDsto.setTotal_number(jsonObject.getInt("total_number"));
		friendDsto.setPrevious_cursor(jsonObject.getInt("previous_cursor"));
		Set<User> users = friendDsto.getUsers() == null ? new HashSet<User>()
				: friendDsto.getUsers();
		// 把原来已经爬下来的数据，更新到新的dto上
		JSONArray friends = jsonObject.getJSONArray("users");
		for (int i = 0; i < friends.size(); i++) {
			JSONObject user = friends.getJSONObject(i);
			User u = (User) user.toBean(user, User.class);
			users.add(u);
		}
		friendDsto.setNext_cursor(jsonObject.getInt("previous_cursor")
				+ users.size());
		friendDsto.setUsers(users);
		return friendDsto;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getNext_cursor() {
		return next_cursor;
	}

	public void setNext_cursor(int next_cursor) {
		this.next_cursor = next_cursor;
	}

	public int getPrevious_cursor() {
		return previous_cursor;
	}

	public void setPrevious_cursor(int previous_cursor) {
		this.previous_cursor = previous_cursor;
	}

	public int getTotal_number() {
		return total_number;
	}

	public void setTotal_number(int total_number) {
		this.total_number = total_number;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public static String getUrl() {
		return url;
	}

	public static void setUrl(String url) {
		FriendsDto.url = url;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

}
