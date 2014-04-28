package com.sicpa.standard.sasscl.wizard.context;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.sicpa.standard.sasscl.wizard.ApplicationType;
import com.sicpa.standard.sasscl.wizard.SpringConfigHandler;
import com.sicpa.standard.sasscl.wizard.pombuilder.dependency.Dependencies;
import com.sicpa.standard.sasscl.wizard.pombuilder.dependency.DependenciesFactory;

public class ProjectContext {

	private final static List<IContextChangeListener> listeners = new ArrayList<IContextChangeListener>();

	private static ProjectContextInternal instance;
	static {
		prepare();
	}

	// to reset the context everytime we generate a project
	public static void prepare() {
		instance = new ProjectContextInternal();
	}

	public static String getProjectPath() {
		return instance.getProjectPath();
	}

	public static void setProjectPath(String projectPath) {
		instance.setProjectPath(projectPath);
	}

	public static String getProjectName() {
		return instance.getProjectName();
	}

	public static void setProjectName(String projectName) {
		instance.setProjectName(projectName);
	}

	public static ApplicationType getApplicationType() {
		return instance.getApplicationType();
	}

	public static void setApplicationType(ApplicationType applicationType) {
		instance.setApplicationType(applicationType);
		fireContextChanged("applicationType", applicationType);
	}

	public static String getRootPackagePath() {
		return instance.getRootPackagePath();
	}

	public static String getRootPackageName() {
		return instance.getRootPackageName();
	}

	public static void addSpringConfigItem(String key, String fileName) {
		instance.addSpringConfigItem(key, fileName);
	}

	public static String getSpringConfigString() {
		return instance.getSpringConfigString();
	}

	public static Dependencies createDependency(ApplicationType type) throws IOException {
		return instance.createDependency(type);
	}

	public static void addDependency(String groupId, String artifactId, String version) {
		instance.addDependency(groupId, artifactId, version);
	}

	public static File getCustomBeansFile() {
		return instance.getCustomBeansFile();
	}

	public static String getVersion() throws IOException {
		return instance.getVersion();
	}

	public static void addListener(IContextChangeListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	protected static void fireContextChanged(String propertyName, Object value) {
		synchronized (listeners) {
			for (IContextChangeListener lis : listeners) {
				lis.contextChanged(new PropertyChangeEvent(instance, propertyName, null, value));
			}
		}
	}

	private static class ProjectContextInternal {
		private String projectPath;
		private String projectName;
		private ApplicationType applicationType;
		private SpringConfigHandler springConfigHandler = new SpringConfigHandler();
		private DependenciesFactory dependenciesFactory = new DependenciesFactory();

		public String getProjectPath() {
			return projectPath;
		}

		public void setProjectPath(String projectPath) {
			this.projectPath = projectPath;
		}

		public String getProjectName() {
			return projectName;
		}

		public void setProjectName(String projectName) {
			this.projectName = projectName;
		}

		public ApplicationType getApplicationType() {
			return applicationType;
		}

		public void setApplicationType(ApplicationType applicationType) {
			this.applicationType = applicationType;
		}

		public String getRootPackagePath() {
			return MessageFormat.format("com/sicpa/{0}/{1}", getProjectName(), getApplicationType().getPackageName());
		}

		public String getRootPackageName() {
			return MessageFormat.format("com.sicpa.{0}.{1}", getProjectName(), getApplicationType().getPackageName());
		}

		public void addSpringConfigItem(String key, String fileName) {
			springConfigHandler.addItem(key, fileName);
		}

		public String getSpringConfigString() {
			return springConfigHandler.getConfigString();
		}

		public Dependencies createDependency(ApplicationType type) throws IOException {
			return dependenciesFactory.create(type);
		}

		public void addDependency(String groupId, String artifactId, String version) {
			dependenciesFactory.addDependency(groupId, artifactId, version);
		}

		public File getCustomBeansFile() {
			return new File(ProjectContext.getProjectPath() + "/src/main/resources/spring/customBeans.properties");
		}

		String version = null;

		public String getVersion() throws IOException {
			InputStream inputStream = null;
			try {
				if (version == null) {
					inputStream = ClassLoader.getSystemResourceAsStream("version");
					version = IOUtils.toString(inputStream);
				}
				return version;
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Exception e) {
					}
				}
			}
		}
	}
}
