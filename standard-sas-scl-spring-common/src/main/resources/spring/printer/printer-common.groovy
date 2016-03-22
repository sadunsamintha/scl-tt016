import com.sicpa.standard.sasscl.devices.printer.xcode.SicpaDataOnlyExCodeBehavior
import com.sicpa.standard.sasscl.devices.printer.xcode.mapping.MappingExtendedCodeBehavior
beans{

	sicpaDataOnly8_18OExtendedCodeBehavior(SicpaDataOnlyExCodeBehavior)

	mappingExtendedCodeBehavior(MappingExtendedCodeBehavior){ defaultBehavior=ref('sicpaDataOnly8_18OExtendedCodeBehavior') }
}
