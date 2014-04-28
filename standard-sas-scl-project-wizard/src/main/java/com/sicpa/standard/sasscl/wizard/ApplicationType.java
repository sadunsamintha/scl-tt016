package com.sicpa.standard.sasscl.wizard;

public enum ApplicationType {

	SAS("sas", "standard-sas-app", "SpringConfigSAS", "SASDefaultMessagesMapping"),

	SCL("scl", "standard-scl-app", "SpringConfigSCL", "SCLDefaultMessagesMapping");

	private String packageName;
	private String projectName;
	private String springConfigFile;
	private String messageMappingClass;

	private ApplicationType(String packageName, String projectName, String springConfigFile, String messageMappingClass) {
		this.packageName = packageName;
		this.projectName = projectName;
		this.springConfigFile = springConfigFile;
		this.messageMappingClass = messageMappingClass;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getSpringConfigFile() {
		return springConfigFile;
	}

	public String getMessageMappingClass() {
		return messageMappingClass;
	}
}
