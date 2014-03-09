package edu.ustb.info10.dbsource.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;

import edu.ustb.info10.dbsource.MongoService;
import edu.ustb.info10.dbsource.domain.User;

public class MongoServiceImpl implements MongoService {
	@Autowired
	MongoOperations mongoOperations;

	@Override
	public void insertOrUpdateUser(User user) {
		// 保存一份到主表
		mongoOperations.save(user);
		// 保存一份到缓存表
		mongoOperations.save(user, "usercache");
	}

	@Override
	public User getUser(User user) {
		return mongoOperations.findOne(new BasicQuery("{_id:" + user.getId()
				+ "}"), User.class);
	}

	@Override
	public List<User> getUsersByPage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insertAll(Collection<User> users) {
		mongoOperations.insertAll(users);
		mongoOperations.insert(users, "usercache");
	}

	@Override
	public List<User> getFromUserCache() {
		// 从缓存表里面取出所有的记录做为起始点
		List<User> users = mongoOperations.findAll(User.class, "usercache");
		// 清空缓存表和备份表
		mongoOperations.remove(new BasicQuery("{}"), "usercache");
		mongoOperations.remove(new BasicQuery("{}"), "backup");
		mongoOperations.insert(users, "backup");
		return users;
	}
}
