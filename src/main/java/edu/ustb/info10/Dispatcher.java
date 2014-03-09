package edu.ustb.info10;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ustb.info10.dbsource.domain.User;
import edu.ustb.info10.vo.PostVo;

public class Dispatcher {
	final Logger logger = LoggerFactory.getLogger(Dispatcher.class);
	private PostVo postVo;
	private int totalNum;
	private volatile boolean flag = true;
	public List<String> tokens = new ArrayList<String>();
	private List<JobExcuter> threads = new ArrayList<JobExcuter>();
	
	//用于树的宽度搜索，每次爬取到符合要求的用户，则把他的信息保存下来
	public BlockingQueue<User> users = new LinkedBlockingQueue<User>();
	
	public Dispatcher() {
		init();
	}
	
	public static void main(String[] args) {
		Dispatcher d = new Dispatcher();
		d.run();
		while (true) {
			if (!d.flag) {
				d.stopAll();
				break;
			}
		}
	}
	  
	private void init() {
		postVo = new PostVo();
		postVo.setCount(5);
		postVo.setCursor(0);
		postVo.setUid(0);
		totalNum = 100;
		
		tokens.add("2.00lKvonB0e__RBa48ae2b812dwVgkD");
		tokens.add("2.001sXvcD0aU_DB8ee0d6571d08kSfu");
		tokens.add("2.00M_tzcDhdRlVEae83321c48XHADGE");
		tokens.add("2.007CLAdD0eoGAb33f81f1e33fzrM1D");
		tokens.add("2.00GTdvcDvhfFtCb3856831f3HdPCPB");
		tokens.add("2.00AVyzcD05SqVW827ec20006JmwTxD");
		tokens.add("2.00umevcDAMZGaB2f9eff29b1d61TGC");
		tokens.add("2.00qbzzcDy26YNC641f1df9090h8TOE");
		tokens.add("2.00qYfvcD09yx6q1deae6525bKGM8oD");
		tokens.add("2.003UAAdDYUU2LD272c21f574TyfLuC");
		tokens.add("2.00i7gvcDA92DdB8b55a2434d0kd6pQ");
		
		User kaifulee = new User();
		kaifulee.setId(1197161814L);
		// kaifulee.setId(1652309471L);
		kaifulee.setIdstr("1197161814");
		kaifulee.setName("李开复");
		kaifulee.setScreen_name("李开复");
		kaifulee.setProvince(11);
		kaifulee.setCity(1);
		kaifulee.setLocation("北京 东城区");
		kaifulee.setDescription("创新工场CEO，媒体联系：press@chuangxin.com");
		kaifulee.setUrl("http://blog.sina.com.cn/kaifulee");
		kaifulee.setProfile_image_url("http://tp3.sinaimg.cn/1197161814/50/1290146312/1");
		kaifulee.setCover_image("http://ww1.sinaimg.cn/crop.0.0.980.300/475b3d56jw1dzz068lotgj.jpg");
		kaifulee.setProfile_url("kaifulee");
		kaifulee.setDomain("kaifulee");
		kaifulee.setGender("m");
		kaifulee.setFollowers_count(38522520);
		kaifulee.setFriends_count(472);
		kaifulee.setStatuses_count(11004);
		kaifulee.setFavourites_count(661);
		kaifulee.setCreated_at("Fri Aug 28 16:35:46 +0800 2009");
		kaifulee.setFollowing(true);
		kaifulee.setAllow_all_act_msg(false);
		kaifulee.setGeo_enabled(true);
		kaifulee.setVerified(true);
		kaifulee.setVerified_type(0);
		kaifulee.setStatus_id(3583335039693383L);
		kaifulee.setAllow_all_comment(true);
		kaifulee.setAvatar_large("http://tp3.sinaimg.cn/1197161814/180/1290146312/1");
		kaifulee.setVerified_reason("创新工场董事长兼首席执行官");
		kaifulee.setFollow_me(false);
		kaifulee.setBi_followers_count(444);
		kaifulee.setLang("zh-cn");
		kaifulee.setStar(0);
		kaifulee.setMbtype(12);
		kaifulee.setMbrank(4);
		kaifulee.setBlock_word(0);
	}
	public void run() {
		/**
		for (String token : tokens) {
			JobExcuter j = new JobExcuter(token, this);
			new Thread(j).start();
			threads.add(j);
		}
		**/
	}
	public synchronized PostVo getPostVo() {
		if (postVo.getCursor() >= totalNum) {
			flag = false;
			return null;
		}
		PostVo copy = new PostVo();
		copy.setCursor(postVo.getCursor());
		copy.setCount(postVo.getCount());
		copy.setUid(postVo.getUid());
		postVo.setCursor(postVo.getCursor() + postVo.getCount());
		return copy;
	}

	public void stopAll() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (JobExcuter j : threads) {
//			j.stopRunning();
		}
	}
}
