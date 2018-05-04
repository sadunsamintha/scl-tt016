import com.sicpa.standard.sasscl.devices.printer.TT053SicpaDataAndBlobExCodeBehavior
import com.sicpa.standard.sasscl.devices.printer.xcode.mapping.MappingExtendedCodeBehavior
beans{

	extendedCodeBehavior(TT053SicpaDataAndBlobExCodeBehavior) {
		dmFormat=props['dm.format']
		dmEncoding=props['dm.encoding']
		dmOrientation=props['dm.orientation']
		blobPosition=props['blob.position']
		blobType=props['blob.type']
		blobUtils=ref('blobDetectionUtils')
		productionParameters=ref('productionParameters')
	}
	mappingExtendedCodeBehavior(MappingExtendedCodeBehavior){ defaultBehavior=ref('extendedCodeBehavior') }
}
