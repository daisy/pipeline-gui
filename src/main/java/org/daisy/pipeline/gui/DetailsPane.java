package org.daisy.pipeline.gui;

import org.daisy.pipeline.gui.databridge.ObservableJob;
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
	private Text script;
	private Text desc;
	private Hyperlink loglink;
	private Hyperlink resultslink;
	private ListView<String> params;	
	private MainWindow main;
	
	public DetailsPane(MainWindow main) {
		this.main = main;
		initControls();
	}
	public void setJob(ObservableJob job) {
		this.job = job;
		clearControls();
		displayJobInfo();
	}
	
	// clear job-specific data
	private void clearControls() {
		if (loglink != null) {
			this.getChildren().remove(loglink);
		}
		if (resultslink != null) {
			this.getChildren().remove(resultslink);
		}
		if (params != null) {
			this.getChildren().remove(params);
		}
//		int sz = params.getItems().size();
//		if (sz > 0) {
//			params.getItems().remove(0, sz-1);
//		}
//		
		script.setText("");
		desc.setText("");
	}
	
	// fill in the blanks with job-specific info
	private void displayJobInfo() {
		params = new ListView<String>();
		this.add(params, 0, 3);
		script.setText(job.getJob().getContext().getScript().getName());
		desc.setText("Description goes here");
		
		if (job.getJob().getStatus() == Status.DONE) {
			loglink = new Hyperlink();
		    resultslink = new Hyperlink();
	    	
		    loglink.setText("Log file");
	    	resultslink.setText("Results");
		    
	    	this.add(loglink, 0, 4);
		    this.add(resultslink, 0, 5);
	    }
		
		// TODO translate params
		//params.setItems(job.getParamsAsStrings());
	}
	
	// leave blanks in the form
	private void initControls() {
			
		this.setPadding(new Insets(10));
		this.setHgap(10);
	    this.setVgap(10);
		
		Text title = new Text("Job details");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 25));
		
	    script = new Text();
	    script.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
	    
	    desc = new Text();
	    desc.setFont(Font.font("Arial", FontPosture.ITALIC, 15));
	    
	    this.add(title, 0, 0);
	    this.add(script, 0, 1);
	    this.add(desc, 0, 2);
	    
	     
	}
    
}
