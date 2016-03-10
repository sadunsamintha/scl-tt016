package com.sicpa.standard.sasscl.doc;

import com.sicpa.standard.client.common.messages.MessageType;
import com.sicpa.standard.sasscl.messages.ActionMessageType;
import com.sicpa.standard.sasscl.messages.SASDefaultMessagesMapping;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class MessageListFileCreator {

	private static class ErrorListEntry {

		private ErrorListEntry() {
		}

		private ErrorListEntry(String langKey, String code) {
			this.langKey = langKey;
			this.code = code;
		}

		String langKey;
		String code;
		String type;
		String defMsg;
		String reason;
		String action;
	}

	public static void main(String[] args) throws Exception {
		SASDefaultMessagesMapping mapping = new SASDefaultMessagesMapping();
		Map<String, ErrorListEntry> output = new TreeMap<>();
		loadCodes(mapping, output);
		loadTypes(mapping, output);
		loadDefaultMessage(output);
		loadAction(output);
		loadReason(output);
		writeOutput(output);
	}

	private static String MESSAGES_FOLDER = "doc/";
	private static String MESSAGES_FILE = "messages.csv";
	private static String LANGUAGE_FILE = "language/sasscl_en.properties";
	private static String ACTION_FILE = "doc/errorAction.properties";
	private static String REASON_FILE = "doc/errorReason.properties";

	private static void writeOutput(Map<String, ErrorListEntry> output) throws IOException {
		new File(MESSAGES_FOLDER).mkdirs();
		try (FileWriter fw = new FileWriter(MESSAGES_FOLDER + MESSAGES_FILE)) {
			fw.write("key;code;type;default message;reason;action\n");

			for (ErrorListEntry ele : output.values()) {
				String lineFormatted = formatLine(ele);
				fw.write(lineFormatted);
				fw.write("\n");
			}
		}
	}

	private static String formatLine(ErrorListEntry ele) {
		return MessageFormat.format("{0};{1};{2};{3};{4};{5}", ele.langKey, ele.code, ele.type, format(ele.defMsg),
				format(ele.reason), format(ele.action));
	}

	private static void loadTypes(SASDefaultMessagesMapping mapping, Map<String, ErrorListEntry> output) {
		for (Map.Entry<String, MessageType> entry : mapping.getMessageTypeByKey().entrySet()) {
			String langKey = entry.getKey();
			String type = format(entry.getValue());

			if (shouldFilterType(entry.getValue())) {
				output.remove(langKey);
			} else {
				ErrorListEntry ele = output.get(langKey);
				if (ele == null) {
					ele = new ErrorListEntry();
					ele.langKey = langKey;
					output.put(langKey, ele);
				}
				ele.type = type;
			}
		}
	}

	private static boolean shouldFilterType(MessageType type) {
		return type == ActionMessageType.IGNORE || type == ActionMessageType.LOG;
	}

	private static void loadCodes(SASDefaultMessagesMapping mapping, Map<String, ErrorListEntry> output) {
		mapping.getCodeByKey().entrySet()
				.forEach(k -> output.put(k.getKey(), new ErrorListEntry(k.getKey(), k.getValue())));
	}

	private static void loadDefaultMessage(Map<String, ErrorListEntry> output) throws IOException {
		Properties translation = new Properties();
		ClassPathResource cpr = new ClassPathResource(LANGUAGE_FILE);
		translation.load(new FileInputStream(cpr.getFile()));

		for (ErrorListEntry ele : output.values()) {
			ele.defMsg = format(translation.getProperty(ele.langKey));
		}
	}

	private static void loadAction(Map<String, ErrorListEntry> output) throws IOException {
		Properties translation = new Properties();
		ClassPathResource cpr = new ClassPathResource(ACTION_FILE);
		translation.load(cpr.getInputStream());

		for (ErrorListEntry ele : output.values()) {
			ele.action = format(translation.getProperty(ele.langKey));
		}
	}

	private static void loadReason(Map<String, ErrorListEntry> output) throws IOException {
		Properties translation = new Properties();
		ClassPathResource cpr = new ClassPathResource(REASON_FILE);
		translation.load(cpr.getInputStream());

		for (ErrorListEntry ele : output.values()) {
			ele.reason = format(translation.getProperty(ele.langKey));
		}
	}

	private static String format(Object in) {
		if (in == null || in.toString().isEmpty()) {
			return "TODO";
		}
		String res = in.toString().trim();
		res = res.replace('\n', ' ');

		return res;
	}

}
