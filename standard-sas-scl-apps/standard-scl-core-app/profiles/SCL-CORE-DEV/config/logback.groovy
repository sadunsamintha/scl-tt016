package log

import ch.qos.logback.core.ConsoleAppender
import com.sicpa.standard.client.common.logger.LogbackStatusFileAppender
import com.sicpa.standard.client.common.logger.RepeatedMessageThreadFilter
import com.sicpa.standard.client.common.logger.ThreadLogFilter
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import ch.qos.logback.classic.PatternLayout

import static ch.qos.logback.classic.Level.*


scan("20 seconds")

turboFilter(RepeatedMessageThreadFilter) {
	threadName = "JBPLC-LifeCheck"
	minRepetitionPeriodInMs = 200
}

repeatedMessageThreadFilters = [
	"updateSysUsage",
	"Camera Life Check Worker",
	"SCD2 UDP",
	"LifeCheckWorker of Scd2Connector",
	"PrtConnStatus-Notifier",
]

repeatedMessageThreadFilters.each { name ->
	turboFilter(RepeatedMessageThreadFilter) { threadName = name }
}


appender("CONSOLE", ConsoleAppender) {
	layout(PatternLayout)  { pattern = "[%d{HH:mm:ss.SSS}] %-5p [%t %X{FILTERED}] [%c{1}] [%M:%L] %m%n" }
}

rollingFileAppendersTime = [
	[id: "SASSCL",       file:"log/day.%d{yyyy-MM-dd}-sasscl.log.zip"],
	[id: "STDPLC",       file:"log/day.%d{yyyy-MM-dd}-plc.log.zip"]
]
rollingFileAppendersSize = [
	[id: "STDCAMERA",    file:"log/camera.log",    fileZip: "log/camera-.%i.log.zip"],
	[id: "STDPRINTER",   file:"log/printer.log",   fileZip: "log/printer-.%i.log.zip"],
	[id: "STDOPERATOR",  file:"log/operator.log",  fileZip: "log/operator-.%i.log.zip"],
	[id: "BRS", 		 file:"log/brs.log",  fileZip: "log/brs-.%i.log.zip"],
	[id: "PRINTERMONITORING",   file:"log/printer_monitoring.log",   fileZip: "log/printer_monitoring-.%i.log.zip"],
	[id: "PRINTER_MONITORING_DOMINO",   file:"log/printer_monitoring_domino.log",   fileZip: "log/printer_monitoring_domino-.%i.log.zip"],
]


rollingFileAppendersTime.each { c ->
	appender(c.id, RollingFileAppender) {
		rollingPolicy(TimeBasedRollingPolicy) {
			fileNamePattern = c.file
			maxHistory = 30
		}
		layout(PatternLayout) { pattern = "[%d{yyyy-MM-dd - HH:mm:ss.SSS}] %-5p [%t] [%c{1}] %m%n" }
	}
}

rollingFileAppendersSize.each { c ->
	appender(c.id, RollingFileAppender) {
		file = c.file
		append = true
		rollingPolicy(FixedWindowRollingPolicy) {
			fileNamePattern = c.fileZip
			minIndex = 1
			maxIndex = 10
		}
		triggeringPolicy(SizeBasedTriggeringPolicy) { maxFileSize = "50MB" }
		layout(PatternLayout) { pattern = "[%d{yyyy-MM-dd - HH:mm:ss.SSS}] %-5p [%t] [%c{1}] %m%n" }
	}
}

logger("com.sicpa.standard.sasscl",     	                    DEBUG,  ["SASSCL"],      true)
logger("com.sicpa.standard.sasscl.common.log",   				INFO,   ["STDOPERATOR"],  true)
logger("com.sicpa.standard.sasscl.devices.brs",                 DEBUG,  ["BRS"],		  true)
logger("com.sicpa.standard.camera",                             INFO,   ["STDCAMERA"],   true)
logger("com.sicpa.standard.printer",                            WARN,   ["STDPRINTER"],  true)
logger("com.sicpa.standard.plc",                                INFO,   ["STDPLC"],      true)
logger("com.sicpa.standard.printer.leibinger.monitoring",       INFO,   ["PRINTERMONITORING"], true)
logger("com.sicpa.standard.printer.domino.monitoring",          INFO,   ["PRINTER_MONITORING_DOMINO"],      true)


root(DEBUG, ["CONSOLE"])