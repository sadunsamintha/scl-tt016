
import com.sicpa.tt077.view.report.TT077ReportScreen
import com.sicpa.standard.sasscl.view.monitoring.TT077SystemEventPanel
import com.sicpa.standard.sasscl.view.monitoring.TT077ProductionStatisticsPanel

beans {
    reportScreen(TT077ReportScreen) {
        productionStatisticsAggregatorFactory = ref('productionStatisticsAggregatorFactory')
    }
    productionStatisticsPanel(TT077ProductionStatisticsPanel) {}
    systemEventPanel(TT077SystemEventPanel) {}
}