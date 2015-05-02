package org.daisy.pipeline.gui;



import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.text.Text;

import org.daisy.pipeline.gui.databridge.BoundScript;
import org.daisy.pipeline.gui.databridge.ObservableJob;
import org.daisy.pipeline.gui.databridge.ScriptFieldAnswer;
import org.daisy.pipeline.job.Job.Status;
import org.daisy.pipeline.job.JobResult;

public class DetailsPane extends GridPaneHelper {

	private MainWindow main;
	private GridPaneHelper resultsGrid; 
	ChangeListener<String> jobStatusListener;
	private ChangeListener<ObservableJob> currentJobChangeListener;
	
	public DetailsPane(MainWindow main) {
		this.main = main;
		resultsGrid = new GridPaneHelper();
		addCurrentJobChangeListener();
	}
	
	private void displayJobInfo() {
		ObservableJob job = this.main.getCurrentJobProperty().get();
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
		
		addNameValuePair("ID", job.getJob().getId().toString());
		
		Text settingsLabel = new Text("Settings:");
		settingsLabel.getStyleClass().add("subtitle");
		addRow(settingsLabel);
		
		for (ScriptFieldAnswer answer : boundScript.getInputFields()) {
			addNameValuePair(answer.getField().getNiceName(), answer.answerProperty().get());
		}
		
		for (ScriptFieldAnswer answer : boundScript.getRequiredOptionFields()) {
			addNameValuePair(answer.getField().getNiceName(), answer.answerProperty().get());
		}
		for (ScriptFieldAnswer answer : boundScript.getOptionalOptionFields()) {
			addNameValuePair(answer.getField().getNiceName(), answer.answerProperty().get());
		}
		
//		for (ScriptFieldAnswer answer : boundScript.getOutputFields()) {
//			addNameValuePair(answer.getField().getNiceName(), answer.getAnswer());
//		}
		
		addRow(resultsGrid);
		refreshLinks();
		
	}
	
	private void refreshLinks() {
		ObservableJob job = this.main.getCurrentJobProperty().get();
		resultsGrid.clearControls();
		
		Status status = job.getJob().getStatus();
		if (status == Status.DONE || status == Status.ERROR || status == Status.VALIDATION_FAIL) {
			Text resultsLabel = new Text("Results");
			resultsLabel.getStyleClass().add("subtitle");
	    	resultsGrid.addRow(resultsLabel);
	    	
	    	resultsGrid.addFinderLinkRow("Log file", job.getJob().getContext().getLogFile().toString());
	    	
	    	Iterable<JobResult> results = job.getJob().getContext().getResults().getResults();
	    	for (JobResult result : results) {
	    		resultsGrid.addFinderLinkRow(result.getPath().toString(), result.getPath().toString());
	    	}   	
	    }
		
	}
	
	// listen for when the currently selected job changes
	private void addCurrentJobChangeListener() {
    	currentJobChangeListener = new ChangeListener<ObservableJob>() {

			public void changed(
					ObservableValue<? extends ObservableJob> observable,
					ObservableJob oldValue, ObservableJob newValue) {
				setJobStatusListeners();
				clearControls();
				if (newValue != null) {
					displayJobInfo();
				}
			}
    	};
    	main.getCurrentJobProperty().addListener(currentJobChangeListener);
    }
	
	private void setJobStatusListeners() {
		ObservableJob job = this.main.getCurrentJobProperty().get();
		// unhook the old listener
		if (job != null && jobStatusListener != null) {
			job.statusProperty().removeListener(jobStatusListener);
		}
		
		if (job != null) {
			// hook up a new one
			// other job changes (status, messages) are kept up-to-date by having the widget
			// directly observe the relevant property, but in this case we need to observe from the outside
			// and add widgets accordingly
			final DetailsPane thiz = this;
			jobStatusListener = new ChangeListener<String>() {
				public void changed(ObservableValue<? extends String> observable,
						String oldValue, String newValue) {
					
					// need this to avoid "you're on the wrong thread" errors
					Platform.runLater(new Runnable(){
						public void run() {
							thiz.refreshLinks();
						}
					});
				}
			};
			
			job.statusProperty().addListener(jobStatusListener);
		}
		
	}
}
