import com.sicpa.standard.sasscl.devices.printer.TT053SicpaDataAndBlobExCodeBehavior
import com.sicpa.standard.sasscl.devices.printer.xcode.mapping.MappingExtendedCodeBehavior
beans{

	extendedCodeBehavior(TT053SicpaDataAndBlobExCodeBehavior) {
		dmFormat=props['dm.format'].trim()
		dmEncoding=props['dm.encoding'].trim()
		blobPosition=props['blob.position'].trim()
		blobType=props['blob.type'].trim()
		blobUtils=ref('blobDetectionUtils')
		productionParameters=ref('productionParameters')
	}
	mappingExtendedCodeBehavior(MappingExtendedCodeBehavior){ defaultBehavior=ref('extendedCodeBehavior') }
}
