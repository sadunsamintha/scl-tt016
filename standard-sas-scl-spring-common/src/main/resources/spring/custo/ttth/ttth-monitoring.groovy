import com.sicpa.ttth.monitoring.impl.TTTHMonitoring
import com.sicpa.ttth.monitoring.TTTHProductionStatisticsAggregatorFactory

beans {
    monitoring(TTTHMonitoring, ref('monitoringModel')) {
        saveIncrPeriod = props['monitoringSaveIncrementalStatisticsTimer_sec']
        subsystemIdProvider=ref('subsystemIdProvider')
        mbeanStatistics=ref('MBeanStatsDelegate')
        productionParameters=ref('productionParameters')
    }
    productionStatisticsAggregatorFactory(TTTHProductionStatisticsAggregatorFactory) {
        language=props['language']
    }
}