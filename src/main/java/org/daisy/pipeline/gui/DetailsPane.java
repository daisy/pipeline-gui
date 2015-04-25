package org.daisy.pipeline.gui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import org.daisy.pipeline.gui.databridge.BoundScript;
import org.daisy.pipeline.gui.databridge.ObservableJob;
import org.daisy.pipeline.gui.databridge.ScriptFieldAnswer;
import org.daisy.pipeline.gui.utils.PlatformUtils;
import org.daisy.pipeline.job.Job.Status;
import org.daisy.pipeline.job.JobResult;

public class DetailsPane extends GridPane {

	private ObservableJob job;
	private MainWindow main;
	int rowcount;
	// these style settings could move to CSS or some shared class
	private Font h1 = Font.font("Arial", FontWeight.BOLD, 25);
	private Font h2 = Font.font("Arial", FontWeight.BOLD, 15);
	private Font h3 = Font.font("Arial", FontWeight.BOLD, 12);
	
	public DetailsPane(MainWindow main) {
		this.main = main;
		initControls();
	}
	public void setJob(ObservableJob job) {
		this.job = job;
		clearControls();
		displayJobInfo();
	}
	
	private void clearControls() {
		int sz = getChildren().size();
		if (sz > 0) {
			getChildren().remove(0, sz - 1);
		}
	}
	
	// fill in the blanks with job-specific info
	private void displayJobInfo() {
		final BoundScript boundScript = job.getBoundScript();

		rowcount = 1;

		Text script = new Text(boundScript.getScript().getName());
		script.setFont(h2);
		addRow(script);
		Text desc = new Text(boundScript.getScript().getDescription());
		addRow(desc);
		
		addWebpageLinkRow("Read online documentation", boundScript.getScript().getXProcScript().getHomepage());

		
		Text statusLabel = new Text("Status:");
		statusLabel.setFont(h2);
		Text statusValue = new Text();
		statusValue.setFont(h2);
		// binding this causes a thread error
		//statusValue.textProperty().bind(job.statusProperty());
		addRow(statusLabel, statusValue);
		
		Text settingsLabel = new Text("Settings:");
		settingsLabel.setFont(h2);
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
	    	resultsLabel.setFont(h2);
	    	addRow(resultsLabel);
	    	
	    	this.addFinderLinkRow("Log file", job.getJob().getContext().getLogFile().toString());
	    	
	    	Iterable<JobResult> results = job.getJob().getContext().getResults().getResults();
	    	for (JobResult result : results) {
	    		this.addFinderLinkRow(result.getPath().toString(), result.getPath().toString());
	    	}
	    	
	    }
		
		
	}
	
	// leave blanks in the form
	private void initControls() {
			
		this.setPadding(new Insets(10));
		this.setHgap(10);
	    this.setVgap(10);
		
		Text title = new Text("Job details");
		title.setFont(h1);
		
		addRow(title);
		 
	}
	
	private void addNameValuePair(String name, String value) {
		Text nameTxt = new Text(name + ":");
		Text valueTxt = new Text(value);
		addRow(nameTxt, valueTxt);
	}
	
	private void addRow(Node... nodes) {
		int colcount = 0;
		for (Node n : nodes) {
			add(n, colcount, rowcount);
			colcount++;
		}
		rowcount++;
	}
	
	private void addWebpageLinkRow(String label, final String path) {
		Hyperlink link = new Hyperlink();
	    link.setText(label);
    	link.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent t) {
            	try {
					Runtime.getRuntime().exec(PlatformUtils.getFileBrowserCommand() + " " + path);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    	
    	addRow(link);
	}
	
	private void addFinderLinkRow(String label, final String path) {
		Hyperlink link = new Hyperlink();
	    link.setText(label);
    	link.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent t) {
            	try {
					Runtime.getRuntime().exec(PlatformUtils.getFileBrowserCommand() + " " + path);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    	
    	addRow(link);
	}
    
}
