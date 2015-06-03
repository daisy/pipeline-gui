package org.daisy.pipeline.gui;

import java.io.IOException;

import org.daisy.pipeline.gui.databridge.Script;
import org.daisy.pipeline.gui.utils.PlatformUtils;

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
		    
		    final boolean useHostServices = !PlatformUtils.isUnix();
		    final MainWindow main_ = main;
	    	link.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	if (useHostServices) {
		            	HostServices hostServices = main_.getHostServices();
		            	if (hostServices != null) {
		            		hostServices.showDocument(documentationPage);
		            	}
		            	else {
		            		System.out.println("$$$$$$$$$$$$$$$$$$$$$$ GUI: error launching hyperlink");
		            	}
	            	}
	            	else {
	            		String cmd = PlatformUtils.getFileBrowserCommand() + " " + documentationPage;
	    				try {
							Runtime.getRuntime().exec(cmd);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
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
