import com.sicpa.standard.sasscl.controller.skuselection.fulloperator.FullOperatorSelectionBehavior

beans{

	def skuBehavior=props['skuSelection.behavior'].toUpperCase()

	switch(skuBehavior) {
		case 'OPERATOR_FULL':
			skuSelectionBehavior(FullOperatorSelectionBehavior)
			break
		default:
			throw new RuntimeException('skuSelection.behavior:'+skuBehavior)
	}
}