beans {
	def printerBehavior=props['printer.behavior'].toUpperCase()

	importBeans('spring/printer/printer-common.groovy')	if(printerBehavior == "SIMULATOR") {		importBeans('spring/printer/printer-simulator.groovy')
	}	else if(printerBehavior == "STANDARD") {		importBeans('spring/printer/printer-domino.xml')
		importBeans('spring/printer/printer-leibinger.xml')
		importBeans('spring/printer/leibinger-protocol.xml')
	}
}

