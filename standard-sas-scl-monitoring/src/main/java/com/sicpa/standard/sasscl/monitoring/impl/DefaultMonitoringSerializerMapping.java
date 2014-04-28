package com.sicpa.standard.sasscl.monitoring.impl;

import java.io.File;

import com.sicpa.standard.model.MonitorType;
import com.sicpa.standard.monitor.DefaultMonitorTypesMapping;
import com.sicpa.standard.sasscl.monitoring.statistics.incremental.IncrementalStatistics;
import com.sicpa.standard.sasscl.monitoring.statistics.production.ProductionStatistics;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.storage.FileHandler;
import com.sicpa.standard.storage.IFileHandler;
import com.sicpa.standard.storage.ISerializer;
import com.sicpa.standard.storage.Serializer;
import com.sicpa.standard.storage.simplifiedcsv.SimplifiedCSVReader;
import com.sicpa.standard.storage.simplifiedcsv.SimplifiedCSVWriter;
import com.sicpa.standard.util.FieldToString;

public class DefaultMonitoringSerializerMapping extends DefaultMonitorTypesMapping {

	protected String folder;
	protected int storageDays;

	public DefaultMonitoringSerializerMapping() {
		folder = "monitoring/";
		storageDays = 999;
		new File(folder).mkdirs();

		addMonitorType(MonitorType.INCREMENTAL_STATISTICS, getIncrementalStatisticsSerializer());
		addMonitorType(MonitorType.PRODUCTION_STATISTICS, getProductionStatisticsSerializer());
		addMonitorType(MonitorType.SYSTEM_EVENT, getSystemEventSerializer());
	}

	protected ISerializer getIncrementalStatisticsSerializer() {
		IFileHandler fileHandler = new FileHandler(MonitorType.INCREMENTAL_STATISTICS.getFileName(), storageDays,
				folder);
		SimplifiedCSVWriter writer = new SimplifiedCSVWriter();
		ISerializer serializer1 = new Serializer(IncrementalStatistics.class, writer, new SimplifiedCSVReader(),
				fileHandler, FieldToString.getFieldsName(IncrementalStatistics.class));

		return serializer1;
	}

	protected ISerializer getSystemEventSerializer() {
		IFileHandler fileHandler = new FileHandler(MonitorType.SYSTEM_EVENT.getFileName(), storageDays, folder);
		SimplifiedCSVWriter writer = new SimplifiedCSVWriter();
		ISerializer serializer = new Serializer(BasicSystemEvent.class, writer, new SimplifiedCSVReader(), fileHandler,
				FieldToString.getFieldsName(BasicSystemEvent.class));
		return serializer;
	}

	protected ISerializer getProductionStatisticsSerializer() {

		IFileHandler fileHandler = new FileHandler(MonitorType.PRODUCTION_STATISTICS.getFileName(), storageDays, folder);
		SimplifiedCSVWriter writer = new SimplifiedCSVWriter();
		ISerializer serializer = new Serializer(ProductionStatistics.class, writer, new SimplifiedCSVReader(),
				fileHandler, FieldToString.getFieldsName(ProductionStatistics.class));

		return serializer;
	}

}
