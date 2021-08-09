package com.sicpa.tt016.monitoring.impl;

import com.sicpa.standard.model.MonitorType;
import com.sicpa.standard.sasscl.monitoring.impl.DefaultMonitoringSerializerMapping;
import com.sicpa.standard.storage.FileHandler;
import com.sicpa.standard.storage.IFileHandler;
import com.sicpa.standard.storage.ISerializer;
import com.sicpa.standard.storage.Serializer;
import com.sicpa.standard.storage.simplifiedcsv.SimplifiedCSVReader;
import com.sicpa.standard.storage.simplifiedcsv.SimplifiedCSVWriter;
import com.sicpa.standard.util.FieldToString;
import com.sicpa.tt016.monitoring.statistics.incremental.TT016IncrementalStatistics;

public class TT016MonitoringSerializerMapping extends DefaultMonitoringSerializerMapping {

    public TT016MonitoringSerializerMapping(String folder) {
        super(folder);
    }

    @Override
    protected ISerializer getIncrementalStatisticsSerializer() {
        IFileHandler fileHandler = new FileHandler(MonitorType.INCREMENTAL_STATISTICS.getFileName(), storageDays,
            folder);
        SimplifiedCSVWriter writer = new SimplifiedCSVWriter();
        return new Serializer(TT016IncrementalStatistics.class, writer, new SimplifiedCSVReader(),
            fileHandler, FieldToString.getFieldsName(TT016IncrementalStatistics.class));
    }
}
