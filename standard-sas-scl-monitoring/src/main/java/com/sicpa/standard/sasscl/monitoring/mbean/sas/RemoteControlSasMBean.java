package com.sicpa.standard.sasscl.monitoring.mbean.sas;

import ch.qos.logback.classic.Level;
import com.sicpa.standard.sasscl.monitoring.statistics.incremental.IncrementalStatistics;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.monitoring.utils.FileInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface RemoteControlSasMBean extends SasAppMBean {

	Map<String, Level> getLoggersList();

	void setLoggerLevel(String logger, Level level);

	String getXMLBean(final String beanName);

	void saveXMLBean(String beanName, String xml);

	List<BasicSystemEvent> getSystemEventList(Date from, Date to);

	List<IncrementalStatistics> getIncrementalStatistics(Date from, Date to);

	List<FileInfo> getFolderInfo(String folder);

	byte[] getFileContent(String fileName);
	
	String createThreadsDump();
}
