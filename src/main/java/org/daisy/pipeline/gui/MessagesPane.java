package org.daisy.pipeline.gui;

import org.daisy.pipeline.gui.databridge.ObservableJob;

import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MessagesPane extends VBox {

	private ListView<String> messages;
	private MainWindow main;
	
	public MessagesPane(MainWindow main) {
		super();
		this.main = main;
		initControls();
	}
	
	public void setJob(ObservableJob job) {
		messages.setItems(job.getMessages());
	}
	private void initControls() {
		Text title = new Text("Messages");
	    title.setFont(Font.font("Arial", FontWeight.BOLD, 15));
	    
	    messages = new ListView<String>();
	    
		this.getChildren().add(title);
		this.getChildren().add(messages);
	
	}
	
}
