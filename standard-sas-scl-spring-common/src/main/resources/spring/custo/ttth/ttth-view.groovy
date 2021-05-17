import com.sicpa.ttth.view.main.systemInfo.TTTHSystemInfoView
import com.sicpa.ttth.view.report.TTTHReportScreen
import com.sicpa.ttth.view.flow.TTTHDefaultScreensFlow
import com.sicpa.ttth.view.sku.batch.BatchJobIdSkuView
import com.sicpa.ttth.view.sku.batch.BatchJobIdSkuViewController
import com.sicpa.ttth.view.sku.barcode.BarcodeSkuView
import com.sicpa.ttth.view.sku.barcode.BarcodeSkuViewController
import com.sicpa.ttth.view.startstop.TTTHStartStopView
import com.sicpa.standard.sasscl.productionParameterSelection.TTTHDefaultSelectionModelFactory
import com.sicpa.ttth.view.selection.select.TTTHSelectProductionParametersViewController

beans {
    systemInfoView(TTTHSystemInfoView) {
        controller = ref('systemInfoViewController')
        model = '#{systemInfoViewController.model}'
    }

    reportScreen(TTTHReportScreen) {
        productionStatisticsAggregatorFactory = ref('productionStatisticsAggregatorFactory')
    }

    batchJobIdSkuView(BatchJobIdSkuView) {
        controller = ref('batchJobIdSkuViewController')
        model = "#{batchJobIdSkuViewController.model}"
        dailyBatchRequestRepository = ref('dailyBatchRequestRepository')
        productionParameters = ref('productionParameters')
    }

    batchJobIdSkuViewController(BatchJobIdSkuViewController) {
        view = ref('batchJobIdSkuView')
        viewController = ref('mainFrameController')
        screensFlow = ref('screensFlow')
        batchJobIdSkuView = ref('batchJobIdSkuView')
        dailyBatchRequestRepository = ref('dailyBatchRequestRepository')
    }

    barcodeSkuView(BarcodeSkuView) {
        controller = ref('barcodeSkuViewController')
        model = "#{barcodeSkuViewController.model}"
    }

    barcodeSkuViewController(BarcodeSkuViewController) {
        view = ref('barcodeSkuView')
        viewController = ref('mainFrameController')
        screensFlow = ref('screensFlow')
    }

    screensFlow(TTTHDefaultScreensFlow) { b ->
        b.initMethod = 'buildScreensFlow'
        initialScreen = ref('mainPanelGetter')
        selectionScreen = ref('selectProductionParametersViewController')
        batchJobIdScreen = ref('batchJobIdSkuViewController')
        barcodeScreen = ref('barcodeSkuViewController')
        mainScreen = ref('mainPanelGetter')
        exitScreen = ref('productionSendingViewController')
    }

    selectionModelFactory(TTTHDefaultSelectionModelFactory) {
    }

    selectProductionParametersViewController(TTTHSelectProductionParametersViewController) {
        handPickingView = ref('selectProductionParametersHandPickingView')
        barcodeView = ref('selectProductionParametersBarcodeView')
        viewController = ref('mainFrameController')
        skuListProvider = ref('skuListProvider')
        useBarcodeReader = '${useBarcodeReader}'
        screensFlow = ref('screensFlow')
        mainFrameController = ref('mainFrameController')
        pp = ref('productionParameters')
        barcodeEnabled = props['barcode.verification'].trim()
        flowControl=ref('flowControl')
    }

    startStopView(TTTHStartStopView) { b ->
        b.initMethod = 'initGUI'
        controller = ref('startStopViewController')
        model = '#{startStopViewController.model}'
        productionStatusView = ref('productionStatusView')
        productionListener = ref('production')
    }
    
}