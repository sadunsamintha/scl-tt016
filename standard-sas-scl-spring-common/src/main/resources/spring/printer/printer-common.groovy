import com.sicpa.standard.sasscl.devices.printer.xcode.SicpaDataAndBlobExCodeBehavior
import com.sicpa.standard.sasscl.devices.printer.xcode.mapping.MappingExtendedCodeBehavior
beans{

	extendedCodeBehavior(SicpaDataAndBlobExCodeBehavior) {
		dmFormat=props['dm.format']
		dmEncoding=props['dm.encoding']
		dmOrientation=props['dm.orientation']
		blobPosition=props['blob.position']
		blobType=props['blob.type']
		blobUtils=ref('blobDetectionUtils')
	}
	mappingExtendedCodeBehavior(MappingExtendedCodeBehavior){ defaultBehavior=ref('extendedCodeBehavior') }
}
