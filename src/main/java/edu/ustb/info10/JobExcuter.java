package edu.ustb.info10;

import java.util.Set;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ustb.info10.dbsource.MongoService;
import edu.ustb.info10.dbsource.domain.User;
import edu.ustb.info10.vo.FriendsDto;
import edu.ustb.info10.vo.PostVo;

public class JobExcuter implements Callable<Set<User>> {
	final Logger logger = LoggerFactory.getLogger(JobExcuter.class);
	private MongoService mongoService;
	private PostVo postApi;
	private DispatcherWithBFS dispatcher;
	private int count;
	private String access_token;
	private int minuteToSleep;
	private int followersCount;

	public Crawler crawler = new Crawler();
	private FriendsDto friendsDto;
	// 表示当前token访问次数是否达到了限制
	private volatile boolean flag = false;

	public volatile boolean enoughToShutdown = false;

	public JobExcuter(String access_token, DispatcherWithBFS main) {
		this.access_token = access_token;
		this.dispatcher = main;
	}

	public JobExcuter() {
		crawler.job = this;
	};

	@Override
	public Set<User> call() {
		friendsDto.setCount(count);
		// 得到满足条件的用户集合，把他们添入队列，继续爬取他们的信息
		Set<User> users = crawler.crawl(friendsDto);
		User currentUser = friendsDto.getUser();
		// 转化成可爬取的数据
		for (User user : users) {
			// 重复的不再爬取
			if (mongoService.getUser(user) != null) {
				continue;
			}
			FriendsDto f = new FriendsDto();
			f.setCount(count);
			f.setId(user.getId());
			f.setUser(user);
			f.setNext_cursor(0);
			// 绑定该用户和他关注列表的用户id
			currentUser.getFriendsIds().add(user.getId());
			// 把符合条件的用户添加到队列中
			try {
				dispatcher.friendsDto.put(f);
			} catch (InterruptedException e) {
				logger.error(e + "");
			}
		}
		
		// 如果当前用户的关注列表已经爬取完，则把他的数据保存到数据库中
		if (Crawler.enough(friendsDto)) {
			if (mongoService.getUser(currentUser) == null) {
				// 把该的用户信息保存到数据库中
				mongoService.insertOrUpdateUser(currentUser);
				logger.info("save user" + friendsDto.getNum() + ":"
						+ currentUser.getId() + " with token:"
						+ friendsDto.getAccess_token());
			} else {
				logger.info("user(" + currentUser.getId() + ") has already been stored into mongodb!");
				dispatcher.count.decrementAndGet();
			}
		} else {
			// 如果还没爬完，把他添到队尾
			try {
				dispatcher.friendsDto.put(friendsDto);
			} catch (InterruptedException e) {
				logger.error(e + "");
			}
			dispatcher.count.decrementAndGet();
		}
		// 如果该token已经达到了限制次数，则暂停10分钟
		if (isFlag()) {
			// 暂停minuteToSleep分钟
			try {
				Thread.sleep(minuteToSleep*1000*60);
			} catch (InterruptedException e) {
				logger.error(e + "");
			}
		}
		// 把用完后的token返回缓存池
		dispatcher.getAvailTokens().add(friendsDto.getAccess_token());
		return users;
	}

	public MongoService getMongoService() {
		return mongoService;
	}

	public void setMongoService(MongoService mongoService) {
		this.mongoService = mongoService;
	}

	public PostVo getPostApi() {
		return postApi;
	}

	public void setPostApi(PostVo postApi) {
		this.postApi = postApi;
	}

	public DispatcherWithBFS getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(DispatcherWithBFS dispatcher) {
		this.dispatcher = dispatcher;
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


	public int getMinuteToSleep() {
		return minuteToSleep;
	}

	public void setMinuteToSleep(int minuteToSleep) {
		this.minuteToSleep = minuteToSleep;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}

	public FriendsDto getFriendsDto() {
		return friendsDto;
	}

	public void setFriendsDto(FriendsDto friendsDto) {
		this.friendsDto = friendsDto;
	}

}
