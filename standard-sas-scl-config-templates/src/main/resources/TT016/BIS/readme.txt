====================
HOW TO CONFIGURE BIS
====================

In "global.properties":

bis.behavior=standard
sku.recognition.behavior=selector
skuSelection.behavior=operator_partial
alert.sku.recognition.unknown.enabled=true
production.behavior=with_unknown_buffer


Make sure that in the productionConfig-SCL folder, the device is enabled:
<BisConfig />            <---- Enabled
<!--<BisConfig /> -->    <---- Disabled


Don't forget to define the SKU Id for the unknown SKU for both Domestic and Export modes - check MSCL SKU table for IDs

(warning: values defined below might not be correct):
bis.unknownSkuId.domestic=3164761
bis.unknownSkuId.export=3164991