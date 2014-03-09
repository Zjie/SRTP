package edu.ustb.info10;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ustb.info10.dbsource.domain.User;
import edu.ustb.info10.vo.FriendsDto;

public class Crawler {
	private HttpClient httpclient = new DefaultHttpClient();
	final Logger logger = LoggerFactory.getLogger(Crawler.class);
	public JobExcuter job;

	public Set<User> crawl(FriendsDto friendsDto) {
		Set<User> users = recursiveCrawl(friendsDto);
		return users == null ? new HashSet<User>() : users;
	}

	private Set<User> recursiveCrawl(FriendsDto friendsDto) {
		if (enough(friendsDto)) {
			// 如果爬取数据完成，则返回符合条件的用户列表
			Set<User> satisfiedUsers = new HashSet<User>();
			Set<User> users = friendsDto.getUsers() == null ? new HashSet<User>() : friendsDto.getUsers();
			// 绑定他们之间的关联
			for (User u : users) {
				if (isSatisified(u)) {
					satisfiedUsers.add(u);
				}
			}
			return satisfiedUsers;
		} else {
			Set<User> users = null;
			HttpGet getMethod = new HttpGet(getUrl(friendsDto));
			String json = "";
			try {
				// 成功的状态码为200
				HttpResponse response = httpclient.execute(getMethod);
				HttpEntity entity = response.getEntity();
				json = getRespBody(entity);
				FriendsDto newfriendsDto = FriendsDto.phaseUserInfo(json);
				newfriendsDto.setId(friendsDto.getId());
				if (friendsDto != null) {
					// 把原有的拷贝到新的上面
					newfriendsDto.getUsers().addAll(friendsDto.getUsers());
				}
				// 接口限制，cursor必须小于2000，因此不继续往下爬
				if (newfriendsDto.getNext_cursor() < 2000) {
					friendsDto.setId(newfriendsDto.getId());
					friendsDto.setNext_cursor(newfriendsDto.getNext_cursor());
					friendsDto.setTotal_number(newfriendsDto.getTotal_number());
					friendsDto.setUsers(newfriendsDto.getUsers());
					users = recursiveCrawl(friendsDto);
				}

			} catch (Exception e) {
				logger.error(friendsDto.getId() + " " + friendsDto.getNext_cursor());
				logger.info("the key:" + friendsDto.getAccess_token()
						+ " is out of limits, please wait!");
				logger.info(e + "");
				getMethod.releaseConnection();
				job.setFlag(true);
				// 如果爬取数据完成，则返回符合条件的用户列表
				Set<User> satisfiedUsers = new HashSet<User>();
				users = friendsDto.getUsers() == null ? new HashSet<User>() : friendsDto.getUsers();
				// 绑定他们之间的关联
				for (User u : users) {
					if (isSatisified(u)) {
						satisfiedUsers.add(u);
					}
				}
				return satisfiedUsers;
			}
			return users;
		}
	}
	/**
	 * 判断是否爬完一个用户的关注列表
	 * @param friendsDto
	 * @return
	 */
	public static boolean enough(FriendsDto friendsDto) {
		if (friendsDto == null) {
			return false;
		}
		float percent = (float) (friendsDto.getUsers().size() * 1.0 / friendsDto
				.getTotal_number());
		if (percent > 0.9) {
			return true;
		}
		return false;
	}
	/**
	 * 获取返回来的json字符串
	 * @param entity
	 * @return
	 */
	private String getRespBody(HttpEntity entity) {
		StringBuilder sb = new StringBuilder();
		if (entity != null) {
			try {
				InputStream instream = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(instream, "utf-8"));
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
			} catch (IllegalStateException e) {
				logger.error(e + "");
			} catch (IOException e) {
				logger.error(e + "");
			}

		}
		return sb.toString();
	}
	/**
	 * 根据postVo来生成url
	 * @param postVo
	 * @return
	 */
	private String getUrl(FriendsDto friendsDto) {
		StringBuilder url = new StringBuilder(FriendsDto.getUrl() + "?");
		url.append("access_token=").append(friendsDto.getAccess_token());
		if (friendsDto.getId() > 0) {
			url.append("&uid=").append(friendsDto.getId());
		}
		if (friendsDto.getCount() > 0) {
			url.append("&count=").append(friendsDto.getCount());
		}
		if (friendsDto.getNext_cursor() > 0) {
			url.append("&cursor=").append(friendsDto.getNext_cursor());
		}
		return url.toString();
	}
	/**
	 * 判断该用户是否符合条件
	 * @param u
	 * @return
	 */
	private boolean isSatisified(User u) {
		if (u.getFollowers_count() > job.getFollowersCount()) {
			return true;
		}
		return false;
	}
}
