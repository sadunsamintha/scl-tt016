package com.sicpa.standard.sasscl.security;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UserIdRegistry {
	
	private String[] version;

	private List<UserId> userIdList;
	
	private Map<Integer, UserId> userIdMap = new HashMap<Integer, UserId>();
	
	public String[] getVersion() {
		return version;
	}

	public void setVersion(String[] versionLine) {
		this.version = versionLine;
	}

	public List<UserId> getUserIdList() {
		return userIdList;
	}

	public void setUserIdList(List<UserId> userIdList) {
		this.userIdList = userIdList;
		userIdMap.clear();
		for (UserId userid : userIdList) {
			userIdMap.put(userid.getUserID(), userid);
		}
	}

	public UserId getUserId(int userId) {
		if (userIdMap == null) return null;
		
		return userIdMap.get(userId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserIdRegistry other = (UserIdRegistry) obj;
		if (userIdList == null) {
			if (other.userIdList != null)
				return false;
		} else if (!listEquals(userIdList, other.userIdList))
			return false;
		if (!Arrays.equals(version, other.version))
			return false;
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	public boolean listEquals(List l1, List l2) {
		if (l1 == l2)
		    return true;
		
		Iterator e1 = l1.iterator();
		Iterator e2 = l1.iterator();
		
		while(e1.hasNext() && e2.hasNext()) {
		    Object o1 = e1.next();
		    Object o2 = e2.next();
		    if (!(o1==null ? o2==null : o1.equals(o2)))
			return false;
		}
		return !(e1.hasNext() || e2.hasNext());
	    }
}
