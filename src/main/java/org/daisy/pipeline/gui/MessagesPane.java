package org.daisy.pipeline.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import org.daisy.pipeline.gui.databridge.ObservableJob;

public class MessagesPane extends VBox {

	private ListView<String> messages;
	private MainWindow main;
	private ChangeListener<ObservableJob> currentJobChangeListener;
	
	public MessagesPane(MainWindow main) {
		super();
		this.main = main;
		initControls();
		addCurrentJobChangeListener();
	}
	
	
	// for job details, the messages come from the pipeline
	// for a new job, the messages pane shows validation messages
	// so, provide a simple way to add and clear messages
	public void addMessages(ObservableList<String> messageList) {
		messages.setItems(messageList);
	}
	public void clearMessages() {
		messages.setItems(null);
	}
	
	private void initControls() {
		this.getStyleClass().add("messages");
		
		Text title = new Text("Messages");
	    title.getStyleClass().add("subtitle");
	    
	    messages = new ListView<String>();
	    
		this.getChildren().add(title);
		this.getChildren().add(messages);
	
	}
	
	// listen for when the currently selected job changes
		private void addCurrentJobChangeListener() {
	    	currentJobChangeListener = new ChangeListener<ObservableJob>() {

				public void changed(
						ObservableValue<? extends ObservableJob> observable,
						ObservableJob oldValue, ObservableJob newValue) {
					if (newValue == null) {
						messages.setItems(null);
					}
					else {
						messages.setItems(newValue.getMessages());
					}
				}
	    		
	    	};
	    	main.getCurrentJobProperty().addListener(currentJobChangeListener);
	    }

	
}
