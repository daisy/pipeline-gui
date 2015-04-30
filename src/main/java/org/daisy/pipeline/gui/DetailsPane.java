package org.daisy.pipeline.gui;



import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import org.daisy.pipeline.gui.databridge.BoundScript;
import org.daisy.pipeline.gui.databridge.ObservableJob;
import org.daisy.pipeline.gui.databridge.ScriptFieldAnswer;
import org.daisy.pipeline.job.Job.Status;
import org.daisy.pipeline.job.JobResult;

public class DetailsPane extends GridPaneHelper {

	private ObservableJob job;
	private MainWindow main;
	
	public DetailsPane(MainWindow main) {
		this.main = main;
	}
	public void setJob(ObservableJob job) {
		this.job = job;
		clearControls();
		displayJobInfo();
	}
	
	private void displayJobInfo() {
		this.getStyleClass().add("details");
		Text title = new Text("Job details");
		title.getStyleClass().add("title");
		addRow(title);
		
		final BoundScript boundScript = job.getBoundScript();

		Text script = new Text(boundScript.getScript().getName());
		script.getStyleClass().add("subtitle");
		addRow(script);
		Text desc = new Text(boundScript.getScript().getDescription());
		addRow(desc);
		
		addWebpageLinkRow("Read online documentation", boundScript.getScript().getXProcScript().getHomepage());
		
		Text statusLabel = new Text("Status:");
		statusLabel.getStyleClass().add("subtitle");
		Text statusValue = new Text();
		statusValue.getStyleClass().add("subtitle");
		
		// binding this causes a thread error
		statusValue.textProperty().bind(job.statusProperty());
		addRow(statusLabel, statusValue);
		
		
		
		Text settingsLabel = new Text("Settings:");
		settingsLabel.getStyleClass().add("subtitle");
		addRow(settingsLabel);
		
		addNameValuePair("ID", job.getJob().getId().toString());
		
		for (ScriptFieldAnswer answer : boundScript.getInputFields()) {
			addNameValuePair(answer.getField().getNiceName(), answer.getAnswer());
		}
		
		for (ScriptFieldAnswer answer : boundScript.getOptionFields()) {
			addNameValuePair(answer.getField().getNiceName(), answer.getAnswer());
		}
		
		for (ScriptFieldAnswer answer : boundScript.getOutputFields()) {
			addNameValuePair(answer.getField().getNiceName(), answer.getAnswer());
		}
		
		
		Status status = job.getJob().getStatus();
		if (status == Status.DONE || status == Status.ERROR || status == Status.VALIDATION_FAIL) {
			Text resultsLabel = new Text("Results");
			resultsLabel.getStyleClass().add("subtitle");
	    	addRow(resultsLabel);
	    	
	    	this.addFinderLinkRow("Log file", job.getJob().getContext().getLogFile().toString());
	    	
	    	Iterable<JobResult> results = job.getJob().getContext().getResults().getResults();
	    	for (JobResult result : results) {
	    		this.addFinderLinkRow(result.getPath().toString(), result.getPath().toString());
	    	}   	
	    }
	}
}
