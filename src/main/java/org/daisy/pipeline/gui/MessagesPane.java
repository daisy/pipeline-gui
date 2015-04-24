package org.daisy.pipeline.gui;

import org.daisy.pipeline.gui.databridge.ObservableJob;

import javafx.collections.ObservableList;
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
	public void clearMessages() {
		messages.setItems(null);
	}
	// for job details, the messages come from the pipeline
	// for a new job, the messages pane shows validation messages
	// so, provide a simple way to add messages
	public void addMessages(ObservableList<String> messageList) {
		messages.setItems(messageList);
	}
	private void initControls() {
		Text title = new Text("Messages");
	    title.setFont(Font.font("Arial", FontWeight.BOLD, 15));
	    
	    messages = new ListView<String>();
	    
		this.getChildren().add(title);
		this.getChildren().add(messages);
	
	}
	
}
