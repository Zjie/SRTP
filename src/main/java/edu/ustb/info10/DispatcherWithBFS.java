package edu.ustb.info10;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.ustb.info10.dbsource.MongoService;
import edu.ustb.info10.dbsource.domain.User;
import edu.ustb.info10.vo.FriendsDto;

public class DispatcherWithBFS implements ApplicationContextAware {
	final Logger logger = LoggerFactory.getLogger(DispatcherWithBFS.class);
	// 待爬取的用户信息
	private List<Long> uids;
	private List<String> tokens;

	private int threadNum;
	private ApplicationContext context;
	public int numOfUser;
	public AtomicInteger count = new AtomicInteger(0);
	public volatile boolean flag = true;
	private MongoService mongoService;
	private ExecutorService es;
	private long beginId;
	// 用于树的宽度搜索，每次爬取到符合要求的用户，则把他的信息保存下来
	public BlockingQueue<FriendsDto> friendsDto = new LinkedBlockingQueue<FriendsDto>();
	// 当前可用的key
	private BlockingQueue<String> availTokens = new LinkedBlockingQueue<String>();

	public void init() {
		// 把可用的token全部加到队列汇总
		availTokens.addAll(tokens);
		// 初始化线程池
		es = Executors.newFixedThreadPool(threadNum);
		// 初始化起始节点，从上次爬取的历史记录中获取
		/*
		List<User> users = mongoService.getFromUserCache();
		for (User u : users) {
			FriendsDto f = new FriendsDto();
			f.setUser(u);
			f.setId(u.getId());
			friendsDto.add(f);
		}
		*/
		User u = new User();
		u.setId(beginId);
		FriendsDto f = new FriendsDto();
		f.setUser(u);
		f.setId(u.getId());
		friendsDto.add(f);
	}

	public void run() {
		while (true) {
			try {
				FriendsDto fr = friendsDto.take();
				// 当前第idx个用户
				int idx = count.incrementAndGet();
				if (idx > numOfUser) {
					es.shutdown();
					break;
				}
				String token = getAvailToken();
				fr.setAccess_token(token);
				fr.setNum(idx);
				es.submit(getJobExcuter(fr));
			} catch (InterruptedException e) {
				logger.error(e + "");
			}
		}
		try {
			es.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			logger.error(e + "");
		}
	}

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "spring.xml" });
		DispatcherWithBFS dw = (DispatcherWithBFS) context
				.getBean("dispatcher");
		dw.run();
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		context = arg0;
	}

	public List<Long> getUids() {
		return uids;
	}

	public void setUids(List<Long> uids) {
		this.uids = uids;
	}

	public List<String> getTokens() {
		return tokens;
	}

	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}

	public int getNumOfUser() {
		return numOfUser;
	}

	public void setNumOfUser(int numOfUser) {
		this.numOfUser = numOfUser;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	/**
	 * 获取可用的jobexcuter
	 * 
	 * @return
	 */
	private JobExcuter getJobExcuter(FriendsDto friends) {
		JobExcuter job = (JobExcuter) context.getBean("jobExcuter");
		job.setAccess_token(friends.getAccess_token());
		job.setDispatcher(this);
		job.setFriendsDto(friends);
		return job;
	}

	/**
	 * 获取可用的key
	 * 
	 * @return
	 * @throws InterruptedException 
	 */
	private String getAvailToken() throws InterruptedException {
		return availTokens.take();
	}

	public BlockingQueue<String> getAvailTokens() {
		return availTokens;
	}

	public void setAvailTokens(BlockingQueue<String> availTokens) {
		this.availTokens = availTokens;
	}

	public void setMongoService(MongoService mongoService) {
		this.mongoService = mongoService;
	}

	public void setBeginId(long beginId) {
		this.beginId = beginId;
	}

}
