package com.sicpa.standard.sasscl.common.storage;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.sicpa.standard.sasscl.security.UserId;
import com.sicpa.standard.sasscl.security.UserIdRegistry;

public class UserIdStorageTest {
	
	private UserIdStorage storage;
	
	@Before
	public void init() {
		storage = new UserIdStorage();
		storage.setFileName("UserDatabase.csv");
	}
	
	@Test
	public void saveLoad() throws Exception {
		UserIdRegistry registry = createUserRegistry();
		storage.save(registry);
		UserIdRegistry loaded = storage.load();
		Assert.assertEquals(registry, loaded);
	}
	
//	@Test
	public void load() throws Exception {
		storage.setFileName("Z:\\UserDatabase.csv");
		UserIdRegistry loaded = storage.load();
		System.out.println(loaded);
	}

	private UserIdRegistry createUserRegistry() {
		UserIdRegistry registry = new UserIdRegistry();
		String[] versionLine = {"Version", "1", "0", "0"};
		registry.setVersion(versionLine );
		List<UserId> userIdList = new ArrayList<UserId>();
		UserId u1 = new UserId("login1", "name1", "surname1", 1, "100");
		UserId u2 = new UserId("login2", "name2", "surname2", 1, "200");
		UserId u3 = new UserId("login3", "name3", "surname3", 1, "300");
		userIdList.add(u1);
		userIdList.add(u2);
		userIdList.add(u3);
		registry.setUserIdList(userIdList);
		return registry;
	}

}
