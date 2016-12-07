====================
HOW TO CONFIGURE BRS
====================

In "global.properties":

brs.behavior=standard


Make sure that in the productionConfig-SCL folder, the device is enabled:
<BrsConfig />            <---- Enabled
<!--<BrsConfig /> -->    <---- Disabled


In the "brsConfig.xml", depending on the number of BRS cameras to use, comment or uncomment:

Using 4 cameras:
<ip1>192.168.1.121</ip1>
<ip2>192.168.1.122</ip2>
<ip3>192.168.1.123</ip3>
<ip4>192.168.1.124</ip4>


Using 1 camera:
<ip1>192.168.1.121</ip1>
<!--
<ip2>192.168.1.122</ip2>
<ip3>192.168.1.123</ip3>
<ip4>192.168.1.124</ip4>
-->