package com.sicpa.standard.sasscl.common.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.security.UserId;
import com.sicpa.standard.sasscl.security.UserIdRegistry;

public class UserIdStorage {
	
	private static Logger logger = LoggerFactory.getLogger(UserIdStorage.class);
	
	private String delimiter = ";";
	
	private String fileName;
	
	private BufferedWriter writer;
	
	private BufferedReader reader;
	
	private UserIdRegistry userIdRegistry;
	
	public String END_FILE = "EndFile";
	
	public void save(UserIdRegistry userIdRegistry) throws IOException {
		
		getWriter();
		
		writeRecord(userIdRegistry.getVersion());
		
		String[] fieldsName = {"Firstname","Surname","UserId","UserLevelAccess","Login"};
		writeRecord(fieldsName);
		
		for (UserId userId : userIdRegistry.getUserIdList()) {
			writeUserId(userId);
		}
		
		String[] endFile = {END_FILE};
		writeRecord(endFile);
	}

	public UserIdRegistry load() throws Exception {
		
		getReader();
		
		userIdRegistry = new UserIdRegistry();
		userIdRegistry.setVersion(readRecord());
		
		//skip header
		reader.readLine();
		
		List<UserId> userIdList = new ArrayList<UserId>();
		while (true) {
			UserId userId = readUserId();
			if (userId != null) {
				userIdList.add(userId);
			}
			else {
				break;
			}
		}
		userIdRegistry.setUserIdList(userIdList);
		
		return userIdRegistry;
	}
	


	public UserIdRegistry getUserIdRegistry() {
		if (userIdRegistry == null) {
			try {
				load();
			} catch (Exception e) {
				logger.error("Exception when loading user registry {}", e.getMessage());
				return null;
			}
		}
		return userIdRegistry;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
	
	private BufferedWriter getWriter() throws IOException {
		
		File file = new File(fileName);
		writer = new BufferedWriter(new FileWriter(file, false));
		return writer;
	}

	public BufferedReader getReader() throws Exception {
		
		File f = null;
		URL url = ClassLoader.getSystemResource(fileName);
		if (url != null) {
			f = new File(url.toURI());
		} else {
			f = new File(fileName);
		}
		
		reader = new BufferedReader(new FileReader(f));
		return reader;
	}
	
	private void writeUserId(UserId userId) throws IOException {
		String[] fields = {	userId.getFirstname(),
							userId.getSurname(),
							String.valueOf(userId.getUserID()),
							String.valueOf(userId.getUserLevelAccess()),
							userId.getLogin()};
		writeRecord(fields);
	}
	
	private void writeRecord(final String[] fields) throws IOException {
		
		if (writer != null) {
			for (int i = 0; i < fields.length; i++) {
				// do not allow to write a new line
				writer.write(fields[i].replaceAll("\n", " ").replaceAll("\r", " "));
				if (i + 1 < fields.length) {
					writer.write(this.delimiter);
					writer.flush();
				}
			}

			if (fields.length > 0) {
				writer.newLine();
				writer.flush();
			}
		}
	}
	
	private UserId readUserId() throws IOException {
		UserId userId = new UserId();
		String[] fields = readRecord();
		if (fields.length==1 && fields[0].equals(END_FILE)) return null;
		try {
			setFieldsValue(userId, getAllFields(userId), fields);
		} catch (Exception e) {
			logger.info("Exception when reading userId {}", e.getMessage());
			return null;
		}
		return userId;		
	}
	
	public String[] readRecord() throws IOException {
		String[] fields = new String[0];
		if (reader != null) {
			String record = this.reader.readLine();
			if (record != null) {
				fields = record.split(delimiter, -1);
			} else {
				return null;
			}
			for (int i=0; i< fields.length; i++) {
				fields[i] = StringUtils.deleteWhitespace(fields[i]);
			}
		}
		return fields;
	}
	
	private static void setFieldsValue(Object object, Field[] fieldList, String[] values) throws IllegalArgumentException, IllegalAccessException {

		if (fieldList.length != values.length) {
			throw new IllegalArgumentException("field names and values lenght should match " + fieldList.length + "!="
					+ values.length);
		}

		for (int i = 0; i < fieldList.length; i++) {
			fieldList[i].setAccessible(true);
			String fieldTypeClassName = fieldList[i].getType().getName();
			if (!fieldTypeClassName.equals(object.getClass().getName())) {

				if (fieldTypeClassName.equals(long.class.getName())) {
					fieldList[i].setLong(object, Long.valueOf(values[i]));
				} else if (fieldTypeClassName.equals(int.class.getName())) {
					fieldList[i].setInt(object, Integer.valueOf(values[i]));
				} else if (fieldTypeClassName.equals(boolean.class.getName())) {
					fieldList[i].setBoolean(object, Boolean.valueOf(values[i]));
				} else if (fieldTypeClassName.equals(String.class.getName())) {
					fieldList[i].set(object, String.valueOf(values[i]));
				} else {
					throw new IllegalArgumentException(fieldTypeClassName + " is not supported");
				}
			}
		}
	}
	
	private static Field[] getAllFields(Object object) {
		Class<?> cls = object.getClass();

		List<Field> accum = new ArrayList<Field>();
		while (cls != null) {
			Field[] fields = cls.getDeclaredFields();
			for (Field aField : fields) {
				if (!Modifier.isStatic(aField.getModifiers()) && !Modifier.isTransient(aField.getModifiers())) {
					accum.add(aField);
				}
			}
			cls = cls.getSuperclass();
		}
		Field[] allFields = (Field[]) accum.toArray(new Field[accum.size()]);

		return allFields;
	}
}
