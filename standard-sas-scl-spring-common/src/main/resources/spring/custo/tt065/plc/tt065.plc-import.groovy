beans {

	def plcBehavior=props['plc.behavior'].toUpperCase()

	importBeans('spring/custo/tt065/plc/tt065.plcVars.groovy')
	importBeans('spring/plc/plc-common.groovy')
	importBeans('spring/alert/alertNoCapsTask.groovy')

	if(plcBehavior == "SIMULATOR") {
		importBeans('spring/plc/plc-simulator.groovy')
	}
	else if(plcBehavior == "STANDARD") {
		importBeans('spring/plc/plc-standard.groovy')
	}
}

