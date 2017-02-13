==========================
HOW TO ENABLE REFEED MODE?
==========================

In the "global.properties", make sure that the "refeedAvailable" is true

===============================================================
WHY I CAN'T CHOOSE REFEED MODE OR REFEED MODE IS NOT DISPLAYED?
===============================================================

The "refeedAvailable" property in the "global.properties" must be set to "true".

In addition the property "refeedSkuIds" MUST specify at least one SKU id. This SKU id must exist in the list of SKUs
downloaded from the MSCL.

Also, the user which is logged in, must have the permission to access this mode. In order to grant him/her this
permission, add the permission "<Permission name="PRODUCTION_MODE_REFEED_NORMAL"/>" for the corresponding user in the
file "security.xml".