========================
HOW TO CONFIGURE 2 LINES
========================

Make sure that on the "productionConfig-SCL" folder, the production mode files specify that both lines are active.

...
<line index="1">
    <property key="PARAM_LINE_IS_ACTIVE" value="true" />
</line>
<line index="2">
    <property key="PARAM_LINE_IS_ACTIVE" value="true" />
</line>
...


2 lines, means we have a pair (printer + camera) per line. So that needs to be reflected in the same production mode
file. In the example below we are using 2 Leibinger printers, each being validated by a Cognex camera.

<PrinterConfig deviceType="LEIBINGER" id="leibinger_1" validatedBy="qc_scl_1" />
<PrinterConfig deviceType="LEIBINGER" id="leibinger_2" validatedBy="qc_scl_2" />

<CameraConfig deviceType="COGNEX" id="qc_scl_1" />
<CameraConfig deviceType="COGNEX" id="qc_scl_2" />


In the "global.properties" set the parameter "line.count" to "2".
