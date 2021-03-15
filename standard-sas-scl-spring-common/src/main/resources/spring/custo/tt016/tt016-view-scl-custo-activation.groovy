import com.sicpa.tt016.monitoring.TT016ProductionStatisticsAggregatorFactory
import com.sicpa.tt016.view.report.TT016ReportScreen

beans{
	productionStatisticsAggregatorFactory(TT016ProductionStatisticsAggregatorFactory){
		language=props['language'].trim()
	}
	reportScreen(TT016ReportScreen){
		productionStatisticsAggregatorFactory=ref('productionStatisticsAggregatorFactory')
	}
}