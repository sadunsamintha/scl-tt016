import com.sicpa.standard.sasscl.controller.skuselection.impl.FullOperatorSelectionBehavior
import com.sicpa.standard.sasscl.controller.skuselection.impl.SkuSelectionDuringProduction


beans{

	def skuBehavior=props['skuSelection.behavior'].trim().toUpperCase()

	switch(skuBehavior) {
		case 'OPERATOR_FULL':
			skuSelectionBehavior(FullOperatorSelectionBehavior)
			break
		case 'OPERATOR_PARTIAL':
			skuSelectionBehavior(SkuSelectionDuringProduction){
				productionParameters=ref('productionParameters')
				unknownSkuProvider=ref('unknownSkuProvider')
			}
			break
		default:
			throw new RuntimeException('skuSelection.behavior:'+skuBehavior)
	}
}