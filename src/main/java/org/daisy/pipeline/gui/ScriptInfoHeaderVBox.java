package org.daisy.pipeline.gui;

import org.daisy.pipeline.gui.databridge.Script;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ScriptInfoHeaderVBox extends VBox {

	MainWindow main;
	public ScriptInfoHeaderVBox(MainWindow main) {
		super();
		this.main = main;
		this.getStyleClass().add("script-info");
		
	}
	
	public void populate(Script script) {
		Text name = new Text(script.getName());
		name.getStyleClass().add("subtitle");
		this.getChildren().add(name);
		
		Text desc = new Text(script.getDescription());
		this.getChildren().add(desc);
		
		
		final String documentationPage = script.getXProcScript().getHomepage();
		
		if (documentationPage != null && documentationPage.isEmpty() == false) { 
			Hyperlink link = new Hyperlink();
		    link.setText("Read online documentation");
		    
		    
		    final MainWindow main_ = main;
	    	link.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	HostServices hostServices = main_.getHostServices();
	            	if (hostServices != null) {
	            		hostServices.showDocument(documentationPage);
	            	}
	            	else {
	            		System.out.println("$$$$$$$$$$$$$$$$$$$$$$ GUI: error launching hyperlink");
	            	}
	            }
	        });
	    	this.getChildren().add(link);
		}
	}
	public void clearControls() {
		int sz = getChildren().size();
		if (sz > 0) {
			getChildren().remove(0, sz); // removes all controls from 0 to sz
		}
	}
}
