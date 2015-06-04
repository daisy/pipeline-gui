package org.daisy.pipeline.gui.databridge;

import java.io.File;
import java.io.IOException;

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
		boolean inputsAreValid = checkFields(boundScript.getInputFields());
		boolean reqOptionsAreValid = checkFields(boundScript.getRequiredOptionFields());
		// validate the optional options just to get any messages about their values
		// for example, a file path might be expected
		checkFields(boundScript.getOptionalOptionFields());
		
		return inputsAreValid && reqOptionsAreValid;
		
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
		
		private String EMPTYSTRING = "ERROR: Value is empty for ";
		private String BADPATH = "ERROR: File not found: ";
		private String NOTANUM = "ERROR: Numeric value required for ";
		private String CANTCREATEDIR = "ERROR: Could not create directory ";
		private String NOTADIR = "ERROR: Not a directory: ";
		
	
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
			if (! (answer instanceof ScriptFieldAnswer.ScriptFieldAnswerString) ) {
				return false;
			}
			ScriptFieldAnswer.ScriptFieldAnswerString answer_ = (ScriptFieldAnswer.ScriptFieldAnswerString)answer;
			
			String answerString = answer_.answerProperty().get();
			if (answer.getField().isRequired() && answerString.isEmpty()) {
				message = EMPTYSTRING + answer.getField().getNiceName();
				return false;
			}
			return true;
		}
		
		// validate file paths
		private boolean validateFile(ScriptFieldAnswer answer) {
			if (!validateString(answer)) {
				return false;
			}
			
			ScriptFieldAnswer.ScriptFieldAnswerString answer_ = (ScriptFieldAnswer.ScriptFieldAnswerString)answer;
			String answerString = answer_.answerProperty().get();
			
			// optional fields can have empty values; but if it's not empty, proceed to make sure it's valid
			if (answer.getField().isRequired() == false && answerString.isEmpty()) {
				return true;
			}
			
			File file = new File(answerString);
			// for input files: check that the file exists
			if (answer.getField().getFieldType() == FieldType.INPUT || 
					answer.getField().getFieldType() == FieldType.OPTION) {
				if (!file.exists()) {
					message = BADPATH + answer.getField().getNiceName();
					return false;
				}
			}
			return true;
		}
		
		// validate directory paths
		// attempt to create directories for result or temp options
		private boolean validateDirectory(ScriptFieldAnswer answer) {
			
			if (!validateString(answer)) {
				return false;
			}
			ScriptFieldAnswer.ScriptFieldAnswerString answer_ = (ScriptFieldAnswer.ScriptFieldAnswerString)answer;
			String answerString = answer_.answerProperty().get();
			
			// optional fields can have empty values; but if it's not empty, proceed to make sure it's valid
			if (answer.getField().isRequired() == false && answerString.isEmpty()) {
				return true;
			}
			
			File file = new File(answerString);
			
			
			if (answer.getField().isResult() || answer.getField().isTemp()) {
				// try to create if it doesn't exist
				if (!file.exists()) {
					boolean couldCreateDir = file.mkdirs();
					if (!couldCreateDir) {
						message = CANTCREATEDIR + answerString;
						return false;
					}
				}
			}
			else {
				if (!file.exists()) {
					message = BADPATH + answer.getField().getNiceName();
					return false;
				}
				if (!file.isDirectory()) {
					message = NOTADIR + answer.getField().getNiceName();
					return false;
				}
			}
			return true;
			
		}
		// we'll treat ints like strings with special rules
		private boolean validateInteger(ScriptFieldAnswer answer) {
			if (!validateString(answer)) {
				return false;
			}
			ScriptFieldAnswer.ScriptFieldAnswerString answer_ = (ScriptFieldAnswer.ScriptFieldAnswerString)answer;
			String answerString = answer_.answerProperty().get();
			
			// optional fields can have empty values; but if it's not empty, proceed to make sure it's valid
			if (answer.getField().isRequired() == false && answerString.isEmpty()) {
				return true;
			}
			
			try {
				Integer.parseInt(answerString);
			}
			catch (NumberFormatException e) {
				message = NOTANUM + answer.getField().getNiceName();
				return false;
			}
			return true;
		}
	}
}