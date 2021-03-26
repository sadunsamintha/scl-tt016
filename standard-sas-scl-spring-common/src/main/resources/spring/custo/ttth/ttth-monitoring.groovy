import com.sicpa.ttth.monitoring.impl.TTTHMonitoring
import com.sicpa.ttth.monitoring.TTTHProductionStatisticsAggregatorFactory

beans {
    monitoring(TTTHMonitoring, ref('monitoringModel')) {
        saveIncrPeriod = props['monitoringSaveIncrementalStatisticsTimer_sec'].trim()
        subsystemIdProvider=ref('subsystemIdProvider')
        mbeanStatistics=ref('MBeanStatsDelegate')
        productionParameters=ref('productionParameters')
    }
    productionStatisticsAggregatorFactory(TTTHProductionStatisticsAggregatorFactory) {
        language=props['language'].trim()
    }

    importBeans('spring/custo/ttth/ttth-monitoring.xml')
}