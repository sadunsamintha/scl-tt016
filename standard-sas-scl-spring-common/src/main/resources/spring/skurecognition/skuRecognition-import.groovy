beans{

	def skuRecognitionBehavior=props['sku.recognition.behavior'].toUpperCase()
	if(skuRecognitionBehavior != "NONE") {

		if(skuRecognitionBehavior == "CHECK") {
			importBeans('spring/skurecognition/skuCheck.groovy')
		}else if(skuRecognitionBehavior == "SELECTOR"){
			importBeans('spring/skurecognition/skuSelector.groovy')
		}else{
			throw new IllegalArgumentException("unknown skuRecognitionBehavior:"+skuRecognitionBehavior);
		}
	}
}