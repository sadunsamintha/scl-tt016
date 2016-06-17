import com.sicpa.standard.sasscl.business.sku.selector.SkuSelector
import com.sicpa.standard.sasscl.business.sku.selector.buffer.SkuRecognizedBuffer

beans{

	skuRecognizedBuffer(SkuRecognizedBuffer){ bufferSize=props['sku.recognition.input.buffer'] }

	skuSelector(SkuSelector){
		productionParameters=ref('productionParameters')
		productionChangeDetector=ref('productionChangeDetector')
		skuBuffer=ref('skuRecognizedBuffer')
	}
}