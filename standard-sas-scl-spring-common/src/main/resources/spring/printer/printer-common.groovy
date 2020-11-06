import com.sicpa.standard.sasscl.devices.printer.xcode.SicpaDataAndBlobExCodeBehavior
import com.sicpa.standard.sasscl.devices.printer.xcode.mapping.MappingExtendedCodeBehavior
beans{

	extendedCodeBehavior(SicpaDataAndBlobExCodeBehavior) {
		dmFormat=props['dm.format'].trim()
		dmEncoding=props['dm.encoding'].trim()
		blobPosition=props['blob.position'].trim()
		blobType=props['blob.type'].trim()
		blobUtils=ref('blobDetectionUtils')
		text1Length=props['text1.length'].trim()
		text1Position=props['text1.position'].trim()
		text2Length=props['text2.length'].trim()
		text2Position=props['text2.position'].trim()
		hrdEnable=props['hrd.enable'].trim()
	}
	mappingExtendedCodeBehavior(MappingExtendedCodeBehavior){ defaultBehavior=ref('extendedCodeBehavior') }
}
