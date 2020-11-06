import com.sicpa.standard.sasscl.devices.printer.TTTHExtendedCodeFactory
import com.sicpa.standard.sasscl.devices.printer.xcode.mapping.MappingExtendedCodeBehavior

beans {
    extendedCodeBehavior(TTTHExtendedCodeFactory) {
        dmFormat = props['dm.format'].trim()
        dmEncoding = props['dm.encoding'].trim()
    }
    mappingExtendedCodeBehavior(MappingExtendedCodeBehavior) { defaultBehavior = ref('extendedCodeBehavior') }
}
