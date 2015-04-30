package org.daisy.pipeline.gui.databridge;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.daisy.pipeline.gui.databridge.ScriptField.DataType;
import org.daisy.pipeline.gui.databridge.ScriptField.FieldType;
import org.daisy.pipeline.gui.databridge.ScriptFieldAnswer;

public class ScriptValidator {
	
	private BoundScript boundScript;
	private ObservableList<String> messages;
	
	public ScriptValidator(BoundScript boundScript) {
		this.boundScript = boundScript;
		messages = FXCollections.observableArrayList();
	}
	public boolean validate() {
		boolean isValid = checkFields(boundScript.getInputFields())
				&& checkFields(boundScript.getOptionFields())
				&& checkFields(boundScript.getOutputFields());
		
		return isValid;
		
	}
	public ObservableList<String> getMessages() {
		return messages;
	}
	private boolean checkFields(Iterable<ScriptFieldAnswer> fields) {
		boolean isValid = true;
		ScriptFieldValidator validator = new ScriptFieldValidator();
		for (ScriptFieldAnswer answer : fields) {
			if (!validator.validate(answer)) {
				messages.add(validator.getMessage());
				isValid = false;
			}
		}
		return isValid;
	}
	public class ScriptFieldValidator {
		
		private String EMPTYSTRING = "ERROR: Required value is empty for ";
		private String BADPATH = "ERROR: Path invalid for ";
		private String NOTANUM = "ERROR: Numeric value required for ";
		
	
		String message;
		public boolean validate(ScriptFieldAnswer answer) {
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
		
		public String getMessage() {
			return this.message;
		}
		private boolean validateBoolean(ScriptFieldAnswer answer) {
			return true;
		}
		
		private boolean validateString(ScriptFieldAnswer answer) {
			String answerString = answer.getAnswer();
			if (answer.getField().isRequired() && answerString.isEmpty()) {
				message = EMPTYSTRING + answer.getField().getNiceName();
				return false;
			}
			return true;
		}
		
		private boolean validateFile(ScriptFieldAnswer answer) {
			if (!validateString(answer)) {
				return false;
			}
			File file = new File(answer.getAnswer());
			// for input files: check that the file exists
			if (answer.getField().getFieldType() == FieldType.INPUT ||
				answer.getField().getFieldType() == FieldType.OPTION) {
				if (!file.exists()) {
					message = BADPATH + answer.getField().getNiceName();
					return false;
				}
			}
			// for output files: check that the parent directory exists
			else {
				if (file.getParentFile() == null) {
					message = BADPATH + answer.getField().getNiceName();
					return false;
				}
				if (!file.getParentFile().isDirectory()) {
					message = BADPATH + answer.getField().getNiceName();
					return false;
				}
			}
			return true;
			
		}
		
		private boolean validateDirectory(ScriptFieldAnswer answer) {
			
			if (!validateString(answer)) {
				return false;
			}
			File file = new File(answer.getAnswer());
			if (!file.isDirectory()) {
				message = BADPATH + answer.getField().getNiceName();
				return false;
			}
			return true;
			
		}
		
		private boolean validateInteger(ScriptFieldAnswer answer) {
			String num = answer.getAnswer();
			try {
				Integer.parseInt(num);
			}
			catch (NumberFormatException e) {
				message = NOTANUM + answer.getField().getNiceName();
				return false;
			}
			return true;
		}
	}
}