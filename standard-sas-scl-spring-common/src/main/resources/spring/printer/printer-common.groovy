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
		text1Length=props['text1.length']
		text1Position=props['text1.position']
		text2Length=props['text2.length']
		text2Position=props['text2.position']
		hrdEnable=props['hrd.enable']
	}
	mappingExtendedCodeBehavior(MappingExtendedCodeBehavior){ defaultBehavior=ref('extendedCodeBehavior') }
}
