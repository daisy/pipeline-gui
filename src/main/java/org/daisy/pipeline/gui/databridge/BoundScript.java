package org.daisy.pipeline.gui.databridge;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.daisy.pipeline.gui.databridge.ScriptFieldAnswer;
import org.daisy.pipeline.gui.databridge.ScriptField.DataType;

public class BoundScript {
	
	private Script script;
	private ObservableList<ScriptFieldAnswer> inputAnswers;
	//private ObservableList<ScriptFieldAnswer> outputAnswers;
	private ObservableList<ScriptFieldAnswer> requiredOptionAnswers;
	private ObservableList<ScriptFieldAnswer> optionalOptionAnswers;
	
	public BoundScript(Script script) {
		this.script = script;
		this.inputAnswers = FXCollections.observableArrayList();
		//this.outputAnswers = FXCollections.observableArrayList();
		this.requiredOptionAnswers = FXCollections.observableArrayList();
		this.optionalOptionAnswers = FXCollections.observableArrayList();
		createAnswers();
	}
	public Script getScript() {
		return script;
	}
	public Iterable<ScriptFieldAnswer> getInputFields() {
		return inputAnswers;
	}
//	public Iterable<ScriptFieldAnswer> getOutputFields() {
//		return outputAnswers;
//	}
	public Iterable<ScriptFieldAnswer> getRequiredOptionFields() {
		return requiredOptionAnswers;
	}
	public Iterable<ScriptFieldAnswer> getOptionalOptionFields() {
		return optionalOptionAnswers;
	}
	
	public ScriptFieldAnswer getInputByName(String name) {
		return findByName(inputAnswers, name);
	}
	public ScriptFieldAnswer getOptionByName(String name) {
		// look in both lists
		ScriptFieldAnswer answer = findByName(requiredOptionAnswers, name);
		if (answer == null) {
			return findByName(optionalOptionAnswers, name);
		}
		else {
			return answer;
		}
	}
//	public ScriptFieldAnswer getOutputByName(String name) {
//		return findByName(outputAnswers, name);
//	}
	
	private ScriptFieldAnswer findByName(Iterable<ScriptFieldAnswer> list, String name) {
		for (ScriptFieldAnswer answer : list) {
			if (answer.getField().getName().equals(name)) {
				return answer;
			}
		}
		return null;
	}
	
	private void createAnswers() {
		for (ScriptField field : script.getInputFields()) {
			ScriptFieldAnswer answer = new ScriptFieldAnswer(field);
			inputAnswers.add(answer);
		}
//		for (ScriptField field : script.getOutputFields()) {
//			ScriptFieldAnswer answer = new ScriptFieldAnswer(field);
//			outputAnswers.add(answer);
//		}
		for (ScriptField field : script.getRequiredOptionFields()) {
			ScriptFieldAnswer answer = new ScriptFieldAnswer(field);
			// default to true for boolean fields
			if (field.getDataType() == DataType.BOOLEAN) {
				answer.booleanAnswerProperty().set(true);
			}
			requiredOptionAnswers.add(answer);
		}
		for (ScriptField field : script.getOptionalOptionFields()) {
			ScriptFieldAnswer answer = new ScriptFieldAnswer(field);
			optionalOptionAnswers.add(answer);
		}
	}
}
