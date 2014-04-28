package com.sicpa.standard.sasscl.monitoring.mbean.sas;

import com.sicpa.standard.client.common.utils.ConfigUtils;

public class DefaultSaveRemotelyUpdateBeanTask implements ISaveRemotlyUpdatedBeanTask {

	@Override
	public void save(String beanName,Object remoteModifedObject, Object currentObject) throws Exception {
		ConfigUtils.copyProperty(remoteModifedObject, currentObject);
		ConfigUtils.save(currentObject);
	}
}
