import com.sicpa.tt016.monitoring.impl.TT016Monitoring
beans {
    monitoring(TT016Monitoring, ref('monitoringModel')) {
        saveIncrPeriod = props['monitoringSaveIncrementalStatisticsTimer_sec'].trim()
        subsystemIdProvider=ref('subsystemIdProvider')
        mbeanStatistics=ref('MBeanStatsDelegate')
        productionParameters=ref('productionParameters')
    }
    
//    productionStatisticsAggregatorFactory(TTTHProductionStatisticsAggregatorFactory) {
//       language=props['language'].trim()
//    }

    importBeans('spring/custo/tt016/tt016-monitoring.xml')
}