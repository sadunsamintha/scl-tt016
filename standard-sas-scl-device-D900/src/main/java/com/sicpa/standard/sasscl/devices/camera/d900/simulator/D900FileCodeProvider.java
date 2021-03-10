package com.sicpa.standard.sasscl.devices.camera.d900.simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.sicpa.standard.sasscl.devices.d900.simulator.ID900CodeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class D900FileCodeProvider implements ID900CodeProvider {

	private static final Logger logger = LoggerFactory.getLogger(D900FileCodeProvider.class);

	/**
	 * store the codes read from file
	 */
	protected List<String> storedCodes;

	public D900FileCodeProvider(final String file) {
		storedCodes = new ArrayList<String>();
		loadCodesFromFile(file);
	}

	@Override
	public String requestCode() {
		String code = null;
		if (!storedCodes.isEmpty()) {
			code = storedCodes.remove(0);
		}
		return code;
	}

	/**
	 * Load the temporary storage with codes from a file
	 * 
	 * @param dataFile
	 *            file that contains code
	 */
	protected void loadCodesFromFile(final String file) {
		BufferedReader input = null;
		try {
			// use buffering, reading one line at a time
			File f = new File(file);
			if (f.exists()) {
				input = new BufferedReader(new FileReader(file));

				String line = null;
				while ((line = input.readLine()) != null) {
					storedCodes.add(line);
				}
			}
		} catch (Exception e) {
			logger.error(MessageFormat.format("Error when reading file containing codes: {}", file), e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
