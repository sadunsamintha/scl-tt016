package com.sicpa.standard.sasscl.devices.bis.skucheck;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.devices.bis.IBisModel;

public class UnreadSkuHandler implements IUnreadSkuHandler{
	
	private static final Logger logger = LoggerFactory.getLogger(UnreadSkuHandler.class);

	final List<Boolean> readList = new ArrayList<Boolean>();
	protected IBisModel bisModel;
	private int count = 0;
	
	@Override
	public void addRead() {
		add(true);
	}

	@Override
	public void addUnread() {
		add(false);
	}

	@Override
	public boolean isThresholdReached() {
		synchronized (readList) {
			if (readList.size() < bisModel.getUnreadWindowSize()) {
				return false;
			}

			count = 0;
			for (boolean b : readList) {
				if (!b) {
					count++;
				}
			}
			int percentUnread = 100 * count / readList.size();
			boolean res = percentUnread > bisModel.getUnreadWindowThreshold();
			
			logger.error("BIS detect too many unread SKU :" + percentUnread + "%");
			trim();
			return res;
		}
	}
	
	public void add(boolean read) {
		synchronized (readList) {
			readList.add(read);
		}
	}
	
	public void trim() {
		while (readList.size() > bisModel.getUnreadWindowSize()) {
			readList.remove(0);
		}
	}

	public void setBisModel(IBisModel bisModel) {
		this.bisModel = bisModel;
	}

}
