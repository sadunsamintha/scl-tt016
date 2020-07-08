import com.sicpa.ttth.view.main.systemInfo.TTTHSystemInfoView
import com.sicpa.ttth.view.report.TTTHReportScreen
import com.sicpa.ttth.view.flow.TTTHDefaultScreensFlow
import com.sicpa.ttth.view.sku.batch.BatchIdSkuView
import com.sicpa.ttth.view.sku.batch.BatchIdSkuViewController
import com.sicpa.ttth.view.sku.barcode.BarcodeSkuView
import com.sicpa.ttth.view.sku.barcode.BarcodeSkuViewController
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

    batchIdSkuView(BatchIdSkuView) {
        controller = ref('batchIdSkuViewController')
        model = "#{batchIdSkuViewController.model}"
        batchIdSize = props['sku.batch.id.maximum.length']
    }

    batchIdSkuViewController(BatchIdSkuViewController) {
        view = ref('batchIdSkuView')
        viewController = ref('mainFrameController')
        screensFlow = ref('screensFlow')
        batchIdSize = props['sku.batch.id.maximum.length']
    }

    barcodeSkuView(BarcodeSkuView) {
        controller = ref('barcodeSkuViewController')
        model = "#{barcodeSkuViewController.model}"
        barcodeSize = props['sku.barcode.length']
    }

    barcodeSkuViewController(BarcodeSkuViewController) {
        view = ref('barcodeSkuView')
        viewController = ref('mainFrameController')
        screensFlow = ref('screensFlow')
        barcodeSize = props['sku.barcode.length']
    }

    screensFlow(TTTHDefaultScreensFlow) { b ->
        b.initMethod = 'buildScreensFlow'
        initialScreen = ref('mainPanelGetter')
        selectionScreen = ref('selectProductionParametersViewController')
        batchIdScreen = ref('batchIdSkuViewController')
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
    }

}