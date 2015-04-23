package org.daisy.pipeline.gui.databridge;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.daisy.pipeline.gui.databridge.ScriptFieldAnswer;

public class BoundScript {
	
	private Script script;
	private ObservableList<ScriptFieldAnswer> inputAnswers;
	private ObservableList<ScriptFieldAnswer> outputAnswers;
	private ObservableList<ScriptFieldAnswer> optionAnswers;
	
	public BoundScript(Script script) {
		this.script = script;
		this.inputAnswers = FXCollections.observableArrayList();
		this.outputAnswers = FXCollections.observableArrayList();
		this.optionAnswers = FXCollections.observableArrayList();
		createAnswers();
	}
	public Script getScript() {
		return script;
	}
	public Iterable<ScriptFieldAnswer> getInputFields() {
		return inputAnswers;
	}
	public Iterable<ScriptFieldAnswer> getOutputFields() {
		return outputAnswers;
	}
	public Iterable<ScriptFieldAnswer> getOptionFields() {
		return optionAnswers;
	}
	
	public ScriptFieldAnswer getInputByName(String name) {
		return findByName(inputAnswers, name);
	}
	public ScriptFieldAnswer getOptionByName(String name) {
		return findByName(optionAnswers, name);
	}
	public ScriptFieldAnswer getOutputByName(String name) {
		return findByName(outputAnswers, name);
	}
	
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
		for (ScriptField field : script.getOutputFields()) {
			ScriptFieldAnswer answer = new ScriptFieldAnswer(field);
			outputAnswers.add(answer);
		}
		for (ScriptField field : script.getOptionFields()) {
			ScriptFieldAnswer answer = new ScriptFieldAnswer(field);
			optionAnswers.add(answer);
		}
	}
}
