package com.sicpa.standard.sasscl.devices.brs;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TooManyUnreadHandler implements ITooManyUnreadHandler {

	private static final Logger logger = LoggerFactory.getLogger(TooManyUnreadHandler.class);

	final List<Boolean> readList = new ArrayList<Boolean>();
	protected BrsConfigBean configBean;

	public void addRead(int count) {
		add(count, true);
	}

	public void addUnread(int count) {
		add(count, false);
	}

	public void add(int count, boolean read) {
		synchronized (readList) {
			for (int i = 0; i < count; i++) {
				readList.add(read);
			}
		}
	}

	public boolean isThresholdReached() {
		synchronized (readList) {

			if (readList.size() < configBean.getUnreadWindowSize()) {
				return false;
			}

			int ucount = 0;
			for (boolean b : readList) {
				if (!b) {
					ucount++;
				}
			}
			int percentUnread = 100 * ucount / readList.size();
			boolean res = percentUnread > configBean.getUnreadThreshold();
			logger.error("toomany unread " + percentUnread + "%");
			trim();
			return res;
		}
	}

	public void trim() {
		while (readList.size() > configBean.getUnreadWindowSize()) {
			readList.remove(0);
		}
	}

	public void setConfigBean(BrsConfigBean configBean) {
		this.configBean = configBean;
	}

}