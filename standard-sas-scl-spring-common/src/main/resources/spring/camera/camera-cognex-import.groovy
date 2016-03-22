beans {
	def cameraBehavior=props['camera.behavior'].toUpperCase()

	importBeans('spring/camera/camera-cognex-common.groovy')	if(cameraBehavior == "SIMULATOR") {		importBeans('spring/camera/camera-cognex-simulator.groovy')
	}	else if(cameraBehavior == "STANDARD") {		importBeans('spring/camera/camera-cognex-standard.groovy')
	}
}

