=========================
HOW TO ENABLE REFEED MODE
=========================

In the "global.properties", make sure that the "heuftSystem" is enabled (heuftSystem=true)

======================================
WHY REFEED I CAN'T CHOOSE REFEED MODE?
======================================

In addition to setting the "heuftSystem" to "true", the property "refeedAvailable" in the "global.properties" must also
be set to "true". This property shouldn't be manually defined, it is automatically set by the application when it
connects to the MSCL. Each line needs to be refeed authorized in the webapp.
