package com.sicpa.standard.sasscl.profile;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.app.profile.Profile;

public class SasSclProfile extends Profile {
	
	private static final Logger logger = LoggerFactory.getLogger(SasSclProfile.class);

	public SasSclProfile(String name, String path) {
		super(name, path);
	}
	
	public static List<Profile> getAllAvailableProfiles() {
		List<Profile> profiles = new ArrayList<>();

		File root = new File("profiles");

		File[] profilesFolder = root.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				// filter needed to avoid svn folder
				return !pathname.getName().equals(".svn");
			}
		});
		if (profilesFolder != null) {
			for (File profileFolder : profilesFolder) {
				if (profileFolder.isDirectory()){
					Profile p = new Profile(profileFolder.getName(), profileFolder.getAbsolutePath());
					try {
						File descFile = new File(profileFolder.getAbsoluteFile() + "/" + p.getName() + ".txt");
						p.setDescription(FileUtils.readFileToString(descFile));
					} catch (IOException e) {
						logger.error("File description is empty.", e);
					}
					profiles.add(p);
				}
			}
		}
		return profiles;
	}

}
