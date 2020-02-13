beans{

	def custoId=props['custo.id'].toUpperCase()
	switch(custoId) {
		case 'SASSCL-CORE':
			importBeans('spring/custo/core/core.groovy')
			break
		case 'TT017':
			importBeans('spring/custo/tt017/tt017.groovy')
			break
		case 'TT018':
			importBeans('spring/custo/tt018/tt018.groovy')
			break
		case 'TT016':
			importBeans('spring/custo/tt016/tt016.groovy')
			break
		case 'TT053':
			importBeans('spring/custo/tt053/tt053.groovy')
			break
		case 'TT053SAS':
			importBeans('spring/custo/tt053SAS/tt053SAS.groovy')
			break
		case 'TT077':
			importBeans('spring/custo/tt077/tt077.groovy')
			break;
		case 'TT079':
			importBeans('spring/custo/tt079/tt079.groovy')
			break;
		case 'TT080':
			importBeans('spring/custo/tt080/tt080.groovy')
			break;
		case 'TT021':
			importBeans('spring/custo/tt021/tt021.groovy')
			break;
		case 'NONE':
			break
		default:
			throw new RuntimeException('unkown custo id:'+custoId)
	}
}