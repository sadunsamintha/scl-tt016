import com.sicpa.ttth.view.main.systemInfo.TTTHSystemInfoView
import com.sicpa.ttth.view.report.TTTHReportScreen

beans {
    systemInfoView(TTTHSystemInfoView) {
        controller = ref('systemInfoViewController')
        model = '#{systemInfoViewController.model}'
    }

    reportScreen(TTTHReportScreen) {
        productionStatisticsAggregatorFactory = ref('productionStatisticsAggregatorFactory')
    }
}