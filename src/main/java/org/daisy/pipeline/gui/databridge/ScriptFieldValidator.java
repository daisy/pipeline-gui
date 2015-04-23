package org.daisy.pipeline.gui.databridge;

import org.daisy.pipeline.gui.databridge.ScriptField.DataType;
import org.daisy.pipeline.gui.databridge.ScriptFieldAnswer;

public class ScriptFieldValidator {

	public static boolean validate(ScriptFieldAnswer answer) {
		DataType dataType = answer.getField().getDataType();
		
		if (dataType == DataType.BOOLEAN) {
			return validateBoolean(answer);
		}
		if (dataType == DataType.DIRECTORY) {
			return validateDirectory(answer);
		}
		if (dataType == DataType.FILE) {
			return validateFile(answer);
		}
		if (dataType == DataType.INTEGER) {
			return validateInteger(answer);
		}
		if (dataType == DataType.STRING) {
			return validateString(answer);
		}
		return validateString(answer); // default to string
	}
	
	private static boolean validateBoolean(ScriptFieldAnswer answer) {
		return true;
	}
	
	private static boolean validateString(ScriptFieldAnswer answer) {
		if (answer.getField().isRequired() && answer.getAnswer().isEmpty()) {
			return false;
		}
		return true;
	}
	
	private static boolean validateFile(ScriptFieldAnswer answer) {
		// TODO check file URI
		return validateString(answer);
	}
	
	private static boolean validateDirectory(ScriptFieldAnswer answer) {
		// TODO check dir URI
		return validateString(answer);
	}
	
	private static boolean validateInteger(ScriptFieldAnswer answer) {
		String num = answer.getAnswer();
		try {
			Integer.parseInt(num);
		}
		catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
