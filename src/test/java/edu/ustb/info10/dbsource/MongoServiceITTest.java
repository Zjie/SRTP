package edu.ustb.info10.dbsource;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import edu.ustb.info10.BaseITTest;
import edu.ustb.info10.dbsource.domain.User;

public class MongoServiceITTest extends BaseITTest {
	@Autowired
	MongoService mongoService;

	@Before
	public void init() {
		assertNotNull(mongoService);
	}

	@Test
	public void testInsertAUserInfo() {
		User u = new User();
		u.setId(41071041L);
		u.setFriendsIds(new ArrayList<Long>());
		u.getFriendsIds().add(123L);
		u.getFriendsIds().add(456L);
		u.setName("zhoujie");
		mongoService.insertOrUpdateUser(u);
	}

	@Test
	public void testFindUserByID() {
		User user = new User();
		user.setId(123456L);
		User u = mongoService.getUser(user);
		assertNotNull(u);
	}
	@Test
	public void testInsertAllUser() {
		Set<User> users = new HashSet<User>();
		User u1 = new User();
		u1.setId(123456L);
		u1.setFriendsIds(new ArrayList<Long>());
		u1.getFriendsIds().add(123L);
		u1.getFriendsIds().add(456L);
		
		User u2 = new User();
		u2.setId(12345L);
		u2.setFriendsIds(new ArrayList<Long>());
		u2.getFriendsIds().add(1243L);
		u2.getFriendsIds().add(456L);
		
		users.add(u1);
		users.add(u2);
		
		mongoService.insertAll(users);
		User u = mongoService.getUser(u1);
		assertNotNull(u);
	}
	@Test
	public void testGetAllFromUserCache() {
		List<User> users = mongoService.getFromUserCache();
		assertThat(users.size(), Matchers.greaterThan(0));
	}
}
