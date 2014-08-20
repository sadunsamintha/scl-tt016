package com.sicpa.standard.sasscl.ioc;

import java.io.File;

public class SpringConfigSCL extends SpringConfig {

	public static final String PRINTER = PREFIX + "printer";
    public static final String PRINTER_DOMINO = PREFIX + "printer-domino";
    public static final String PRINTER_LEIBINGER = PREFIX + "printer-leibinger";
	public static final String CODING = PREFIX + "coding";
	public static final String POST_PACKAGE = PREFIX + "postPackage";
	public static final String SCHEDULER_SCL_ADDITIONAL_TASKS = PREFIX + "schedulerSCL";

	public SpringConfigSCL() {
		this.config.put(CODING, "spring" + File.separator + "coding.xml");
		this.config.put(PRINTER, "spring" + File.separator + "printer.xml");
        config.put(PRINTER_DOMINO, "spring/printer-domino.xml");
        config.put(PRINTER_LEIBINGER, "spring/printer-leibinger.xml");

		this.config.put(SCHEDULER_SCL_ADDITIONAL_TASKS, "spring" + File.separator + "schedulerSCL.xml");
	}
}
