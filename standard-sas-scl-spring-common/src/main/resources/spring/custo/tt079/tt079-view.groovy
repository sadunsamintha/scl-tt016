import com.sicpa.standard.client.common.view.screensflow.ScreensFlow
//import com.sicpa.tt079.view.controller.TT079MainFrameController
import com.sicpa.tt079.view.sku.batch_exp.BatchIdExpDtSkuView
import com.sicpa.tt079.view.sku.batch_exp.BatchIdExpDtSkuViewController
import com.sicpa.tt079.view.flow.TT079DefaultScreensFlow
import com.sicpa.tt079.view.selection.select.productionparameters.TT079SelectProductionParametersViewController
import com.sicpa.tt079.view.selection.display.TT079SelectionDisplayView
import com.sicpa.tt079.monitoring.TT079ProductionStatisticsAggregatorFactory
import com.sicpa.tt079.view.report.TT079ReportScreen


beans{
	
	batchIdExpDtSkuView(BatchIdExpDtSkuView){
		controller=ref('batchIdExpDtSkuViewController') 
		model="#{batchIdExpDtSkuViewController.model}"
		batchIdSize=props['sku.batch.id.maximum.length'].trim()
		expiryDateMaxBound=props['sku.exp.date.maxBound'].trim()
		productionParameters=ref('productionParameters')
	}
	
	batchIdExpDtSkuViewController(BatchIdExpDtSkuViewController){
		view=ref('batchIdExpDtSkuView')
		viewController=ref('mainFrameController')
		screensFlow=ref('screensFlow')
		batchIdSize=props['sku.batch.id.maximum.length'].trim()
		pp=ref('productionParameters')
	}
	
	screensFlow(TT079DefaultScreensFlow){b->
		b.initMethod='buildScreensFlow'
		initialScreen=ref('mainPanelGetter')
		selectionScreen=ref('selectProductionParametersViewController')
		batchIdScreen=ref('batchIdExpDtSkuViewController')
		mainScreen=ref('mainPanelGetter')
		exitScreen=ref('productionSendingViewController')
	}
	
	//SELECT PARAMETER
	selectProductionParametersViewController(TT079SelectProductionParametersViewController){
		handPickingView=ref('selectProductionParametersHandPickingView')
		barcodeView=ref('selectProductionParametersBarcodeView')
		viewController=ref('mainFrameController')
		skuListProvider=ref('skuListProvider')
		useBarcodeReader=props['useBarcodeReader'].trim()
		screensFlow=ref('screensFlow')
		mainFrameController=ref('mainFrameController')
	}
	
	//SELECTION DISPLAY
	selectionDisplayView(TT079SelectionDisplayView){
		controller=ref('selectionDisplayViewController')
		model="#{selectionDisplayViewController.model}"
	}
	
	//PRODUCTION STATISTICS AGGREGATOR
	productionStatisticsAggregatorFactory(TT079ProductionStatisticsAggregatorFactory){
		language=props['language'].trim()
	}
	
	//REPORT SCREEN
	reportScreen(TT079ReportScreen){
		productionStatisticsAggregatorFactory=ref('productionStatisticsAggregatorFactory')
	}

}