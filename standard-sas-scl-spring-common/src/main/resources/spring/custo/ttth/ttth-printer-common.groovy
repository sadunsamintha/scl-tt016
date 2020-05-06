import com.sicpa.standard.sasscl.devices.printer.TTTHExtendedCodeFactory
import com.sicpa.standard.sasscl.devices.printer.xcode.mapping.MappingExtendedCodeBehavior

beans {
    extendedCodeBehavior(TTTHExtendedCodeFactory) {
        dmFormat = props['dm.format']
        dmEncoding = props['dm.encoding']
    }
    mappingExtendedCodeBehavior(MappingExtendedCodeBehavior) { defaultBehavior = ref('extendedCodeBehavior') }
}
