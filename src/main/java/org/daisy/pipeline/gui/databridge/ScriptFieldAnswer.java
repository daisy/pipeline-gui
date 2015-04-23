package org.daisy.pipeline.gui.databridge;

import org.daisy.pipeline.gui.databridge.ScriptField.DataType;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class ScriptFieldAnswer {
	// only one of these properties will be used
	// it's horrible design but actually simpler than the alternatives
	private SimpleStringProperty stringAnswer;
	// this is just for binding to a checkbox; it gets translated to a string value in the end
	private SimpleBooleanProperty booleanAnswer; 
	
	private ScriptField field;
	
	public ScriptFieldAnswer(ScriptField field) {
		stringAnswer = new SimpleStringProperty();
		booleanAnswer = new SimpleBooleanProperty();
		this.field = field;
	}
	public ScriptField getField() {
		return field;
	}
	public String getAnswer() {
		return stringAnswer.get();
	}
	public void setAnswer(String answer) {
		stringAnswer.set(answer);
	}
	public SimpleStringProperty answerProperty() {
		// the pipeline wants bools to be true/false strings
		if (field.getDataType() == DataType.BOOLEAN) {
			if (booleanAnswer.get() == true) {
				setAnswer("true");
			}
			else {
				setAnswer("false");
			}
		}
		return stringAnswer;
	}
	
	public SimpleBooleanProperty booleanAnswerProperty() {
		return booleanAnswer;
	}
	public boolean validate() {
		return ScriptFieldValidator.validate(this);
	}
}

