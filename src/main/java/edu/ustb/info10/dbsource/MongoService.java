package edu.ustb.info10.dbsource;

import java.util.Collection;
import java.util.List;

import edu.ustb.info10.dbsource.domain.User;

public interface MongoService {
	public void insertOrUpdateUser(User user);
	public User getUser(User user);
	public List<User> getUsersByPage();
	public void insertAll(Collection<User> users);
	public List<User> getFromUserCache();
}
