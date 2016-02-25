beans {
	def plcBehavior=props['plcSecure.behavior'].toUpperCase()

	importBeans('spring/plc//secure/plcSecureVarMap.xml')
	importBeans('spring/descriptors/plcSecureVariablesDescriptors.xml')
	if(plcBehavior == "SIMULATOR") {
		importBeans('spring/plc/secure/plc-secure-simulator.groovy')
	}
	else if(plcBehavior == "STANDARD") {
		importBeans('spring/plc/secure/plc-secure-standard.groovy')
	}
}

