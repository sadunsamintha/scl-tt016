package com.sicpa.standard.sasscl.monitoring.impl;

import com.sicpa.standard.model.MonitorType;
import com.sicpa.standard.storage.FileHandler;
import com.sicpa.standard.storage.IFileHandler;
import com.sicpa.standard.storage.ISerializer;
import com.sicpa.standard.storage.Serializer;
import com.sicpa.standard.storage.simplifiedcsv.SimplifiedCSVReader;
import com.sicpa.standard.storage.simplifiedcsv.SimplifiedCSVWriter;
import com.sicpa.standard.util.FieldToString;
import com.sicpa.ttth.monitoring.statistics.incremental.TTTHIncrementalStatistics;

public class TTTHMonitoringSerializerMapping extends DefaultMonitoringSerializerMapping {

    public TTTHMonitoringSerializerMapping(String folder) {
        super(folder);
    }

    @Override
    protected ISerializer getIncrementalStatisticsSerializer() {
        IFileHandler fileHandler = new FileHandler(MonitorType.INCREMENTAL_STATISTICS.getFileName(), storageDays,
            folder);
        SimplifiedCSVWriter writer = new SimplifiedCSVWriter();
        return new Serializer(TTTHIncrementalStatistics.class, writer, new SimplifiedCSVReader(),
            fileHandler, FieldToString.getFieldsName(TTTHIncrementalStatistics.class));
    }
}
