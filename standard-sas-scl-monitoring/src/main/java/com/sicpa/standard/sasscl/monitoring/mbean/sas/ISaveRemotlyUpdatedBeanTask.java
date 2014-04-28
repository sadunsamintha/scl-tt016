package com.sicpa.standard.sasscl.monitoring.mbean.sas;

public interface ISaveRemotlyUpdatedBeanTask {

	void save(String beanName,Object newObject, Object oldObject) throws Exception;

}
