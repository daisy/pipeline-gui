package org.daisy.pipeline.gui;

import org.daisy.pipeline.gui.databridge.BoundScript;
import org.daisy.pipeline.gui.databridge.ObservableJob;
import org.daisy.pipeline.gui.databridge.ScriptFieldAnswer;
import org.daisy.pipeline.job.Job.Status;

import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

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
		BoundScript boundScript = job.getBoundScript();
		Text script = new Text(boundScript.getScript().getName());
		script.setFont(h2);
		Text desc = new Text(boundScript.getScript().getDescription());
		rowcount = 1;
		this.add(script, 0, rowcount);
		rowcount++;
		this.add(desc, 0, rowcount);
		rowcount++;
		
		Text statusLabel = new Text("Status:");
		statusLabel.setFont(h2);
		Text statusValue = new Text();
		statusValue.setFont(h2);
		// binding this causes a thread error
		//statusValue.textProperty().bind(job.statusProperty());
		this.add(statusLabel, 0, rowcount);
		this.add(statusValue, 1, rowcount);
		rowcount++;
		
		Text settingsLabel = new Text("Settings:");
		settingsLabel.setFont(h2);
		this.add(settingsLabel, 0, rowcount);
		rowcount++;
		
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
		
		
		
		if (job.getJob().getStatus() == Status.DONE) {
			Hyperlink loglink = new Hyperlink();
		    Hyperlink resultslink = new Hyperlink();
	    	
		    loglink.setText("Log file");
	    	resultslink.setText("Results");
	    	
	    	// TODO link these links
		    
	    	this.add(loglink, 0, rowcount);
	    	rowcount++;
		    this.add(resultslink, 0, rowcount);
	    }
		
		
	}
	
	// leave blanks in the form
	private void initControls() {
			
		this.setPadding(new Insets(10));
		this.setHgap(10);
	    this.setVgap(10);
		
		Text title = new Text("Job details");
		title.setFont(h1);
		
	    this.add(title, 0, 0);
	    
	     
	}
	
	private void addNameValuePair(String name, String value) {
		Text nameTxt = new Text(name + ":");
		Text valueTxt = new Text(value);
		this.add(nameTxt, 0, rowcount);
		this.add(valueTxt, 1, rowcount);
		rowcount++;
	}
    
}
