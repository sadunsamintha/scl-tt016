package com.sicpa.standard.sasscl.wizard.pombuilder.dependency;

public class Dependency {

	private String groupId;
	private String artifactId;
	private String version;
	private String scope;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public Dependency(String groupId, String artifactId, String version) {
		this(groupId, artifactId, version, "compile");
	}

	public Dependency(String groupId, String artifactId, String version, String scope) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.scope = scope;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

}
