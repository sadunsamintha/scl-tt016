beans{

	def rb=props['remoteServer.behavior'].trim().toUpperCase()

	importBeans('spring/custo/tt018/server/aop-lifechecker.xml')
	importBeans('spring/custo/tt018/server/tt018-remoteserver-common.xml')

	switch(rb) {
		case 'STANDARD':
			importBeans('spring/custo/tt018/server/tt018-remoteserver-standard.xml')
			break
		case 'SIMULATOR':
		importBeans('spring/custo/tt018/server/tt018-remoteserver-simulator.groovy')
			break
		case 'NONE':
			break
		default:
			throw new RuntimeException('remoteserver.behavior :'+rb)
	}
}