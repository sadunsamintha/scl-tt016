package custo.tt016.d900

beans {

	def cameraBehavior=props['d900.behavior'].toUpperCase()

	importBeans('spring/custo/tt016/d900/camera-d900-common.groovy')

	if(cameraBehavior == "SIMULATOR") {
		//importBeans('spring/d900/camera-d900-simulator.groovy')
	}
	else if(cameraBehavior == "STANDARD") {
		importBeans('spring/custo/tt016/d900/camera-d900-standard.groovy')
	}
}

