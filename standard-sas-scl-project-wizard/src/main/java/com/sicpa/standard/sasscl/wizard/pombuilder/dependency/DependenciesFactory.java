package com.sicpa.standard.sasscl.wizard.pombuilder.dependency;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sicpa.standard.sasscl.wizard.ApplicationType;
import com.sicpa.standard.sasscl.wizard.context.ProjectContext;

public class DependenciesFactory {

	private List<Dependency> customDependencies = new ArrayList<Dependency>();

	public Dependencies create(ApplicationType type) throws IOException {

		Dependencies res = null;
		switch (type) {
		case SAS:
			res = createSASDependencies();
			break;
		case SCL:
			res = createSCLDependencies();
			break;
		}

		if (res != null) {
			res.addAllDependencies(customDependencies);
		}

		return res;
	}

	private Dependencies createSASDependencies() throws IOException {
		Dependencies dep = new Dependencies();
		dep.addAllDependencies(createCommonDependencies());
		dep.addDependency(new Dependency("com.sicpa.standard.sasscl", "spring-sas", ProjectContext.getVersion()));
		return dep;
	}

	private Dependencies createSCLDependencies() throws IOException {
		Dependencies dep = new Dependencies();
		dep.addAllDependencies(createCommonDependencies());
		dep.addDependency(new Dependency("com.sicpa.standard.sasscl", "spring-scl", ProjectContext.getVersion()));
		dep.addDependency(new Dependency("com.sicpa.standard.sasscl", "business-coding", ProjectContext.getVersion()));

		return dep;
	}

	private List<Dependency> createCommonDependencies() throws IOException {
		List<Dependency> res = new ArrayList<Dependency>();
		res.add(new Dependency("com.sicpa.standard.sasscl", "common-app", ProjectContext.getVersion()));
		res.add(new Dependency("com.sicpa.standard.sasscl", "device-remoteServer-impl", ProjectContext.getVersion()));
		
		res.add(new Dependency("org.mockito", "mockito-all", "1.8.5","test"));
		res.add(new Dependency("org.easytesting", "fest-bundle", "1.2","test"));
		res.add(new Dependency("org.powermock", "powermock-module-junit4", "1.4.10","test"));
		res.add(new Dependency("org.powermock", "powermock-api-mockito", "1.4.10","test"));			
		
		return res;
	}

	public void addDependency(String groupId, String artifactId, String version) {
		customDependencies.add(new Dependency(groupId, artifactId, version));
	}

}
