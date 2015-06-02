package org.daisy.pipeline.gui;

import java.io.File;
import java.io.IOException;

import org.daisy.pipeline.gui.databridge.ObservableJob;
import org.daisy.pipeline.gui.databridge.ScriptFieldAnswer;
import org.daisy.pipeline.gui.databridge.ScriptField.DataType;
import org.daisy.pipeline.gui.utils.PlatformUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;



// helps with adding common controls to a grid
// keeps track of the row count
public class GridPaneHelper extends GridPane {

	private int rowcount = 0;
	private MainWindow main;
	
	public GridPaneHelper(MainWindow main) {
		super();
		this.main = main;
		this.getStyleClass().add("grid");
	}
	
	// widths is a series of percent width values
	public void setColumnWidths(int... widths) {		
		for (int width : widths) {
			ColumnConstraints constraints = new ColumnConstraints();
			constraints.setPercentWidth(width);
			this.getColumnConstraints().add(constraints);
		}
	}
	// pass in null values for row spacing
	public void addRow(Node... nodes) {
		int colcount = 0;
		for (Node n : nodes) {
			if (n == null) {
				colcount++;
			}
			else {
				n.getStyleClass().add("row");
				add(n, colcount, rowcount);
				colcount++;
			}
		}
		rowcount++;
	}
	
	public void addRow(Node node, int colspan) {
		node.getStyleClass().add("row");
		add(node, 0, rowcount, colspan, 1);
		rowcount++;
	}
	// remove all controls from the grid
	public void clearControls() {
		int sz = getChildren().size();
		
		if (sz > 0) {
			getChildren().remove(0, sz); // removes all controls from 0 to sz
		}
		rowcount = 0;
	}
	
	// add a link that launches the file or opens the file browser (depending on how the OS interprets the command)
	public void addFinderLinkRow(String label, final String path) {
		Hyperlink link = new Hyperlink();
	    link.setText(label);
	    link.setTooltip(new Tooltip(path));
    	link.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent t) {
            	try {
            		String cmd = PlatformUtils.getFileBrowserCommand() + " " + path;
    				System.out.println("$$$$$$$$$$$$ GUI link clicked: " + cmd);
					Runtime.getRuntime().exec(cmd);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    	
    	addRow(link);
	}
	
	// add two labels in the same row
	public void addNameValuePair(String name, String value) {
		Text nameTxt = new Text(name + ":");
		Text valueTxt = new Text(value);
		addRow(nameTxt, valueTxt);
	}
	
	public void addFileDirPickerSequence(ScriptFieldAnswer.ScriptFieldAnswerList answer) {
		final ListView<String> listbox = new ListView<String>();
		listbox.setItems(answer.answerProperty());
		listbox.getStyleClass().add("files");
		Text label = new Text();
		label.setText(answer.getField().getNiceName());
		Text help = makeHelpText(answer);
		VBox vbox = new VBox();
		vbox.getChildren().addAll(label, help);
		addRow(vbox, listbox);
		wrapCorrectly(vbox);
		vbox.getStyleClass().add("label-helper-vbox");
		
		
		Button addFileButton = new Button("Add");
		final Button removeFileButton = new Button("Remove");
		HBox hbox = new HBox();
		hbox.getChildren().addAll(addFileButton, removeFileButton);
		hbox.setSpacing(30.0);
		addRow(null, hbox);
		
		final ScriptFieldAnswer.ScriptFieldAnswerList answer_ = answer;
		addFileButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				File file;
				if (answer_.getField().getDataType() == DataType.FILE) {
	                FileChooser fileChooser = new FileChooser();
	                fileChooser.setTitle("Select File");
	                file = fileChooser.showOpenDialog(null);
				}
				// assume directory
				else {
					DirectoryChooser dirChooser = new DirectoryChooser();
	                dirChooser.setTitle("Select Directory");
	                file = dirChooser.showDialog(null);
				}
				if(file != null) {
                	answer_.answerProperty().add(file.getPath());
                }
			}
		});
		
		removeFileButton.setDisable(true);
		removeFileButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				if (listbox.getSelectionModel().isEmpty() == false) {
					int selection = listbox.getSelectionModel().getSelectedIndex();
					listbox.getItems().remove(selection);
				}
				
			}
		});
		listbox.getSelectionModel().selectedItemProperty().addListener(
	            new ChangeListener<String>() {
	                public void changed(ObservableValue<? extends String> ov, 
	                    String old_val, String new_val) {
	                	removeFileButton.setDisable(listbox.getSelectionModel().isEmpty());
	            }
	        });
		
		
		
		
	}
	public void addTextFieldSequence(ScriptFieldAnswer.ScriptFieldAnswerList answer) {
		// TODO
	}
	
	// add a text field with a button for file browsing
	public void addFileDirPicker(ScriptFieldAnswer.ScriptFieldAnswerString answer) {
		Text label = new Text();
		label.setText(answer.getField().getNiceName() + ":");
		final TextField inputFileText = new TextField();
		inputFileText.textProperty().bindBidirectional(answer.answerProperty());
		Button inputFileButton = new Button("Browse");
		VBox vbox = new VBox();
		Text help = makeHelpText(answer);
		vbox.getChildren().addAll(label, help);
		addRow(vbox, inputFileText, inputFileButton);
		wrapCorrectly(vbox);
		vbox.getStyleClass().add("label-helper-vbox");
		
		final ScriptFieldAnswer.ScriptFieldAnswerString answer_ = answer;
		inputFileButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				File file;
				if (answer_.getField().getDataType() == DataType.FILE) {
	                FileChooser fileChooser = new FileChooser();
	                fileChooser.setTitle("Select File");
	                file = fileChooser.showOpenDialog(null);
				}
				// assume directory
				else {
					DirectoryChooser dirChooser = new DirectoryChooser();
	                dirChooser.setTitle("Select Directory");
	                file = dirChooser.showDialog(null);
				}
				if(file != null) {
                	inputFileText.setText(file.getPath());
                }
			}
		});
	}
	
	// add the descriptive text to its own row (sometimes it's added in other ways, which is why
	// these help text functions are broken into 3)
	private void addHelpText(ScriptFieldAnswer answer) {
		Text text = makeHelpText(answer);
		addRow(text);
		
		
	}
	// create the descriptive text
	private Text makeHelpText(ScriptFieldAnswer answer) {
		String helpText = answer.getField().getDescription();
		helpText = helpText.trim();
		helpText.replace('\n', ' ');
		helpText.replace('\t', ' ');
		
		Text help = new Text(answer.getField().getDescription());
		help.getStyleClass().add("help");
		
		return help;
	}
	private void wrapCorrectly(VBox vbox) {
		int col = getColumnIndex(vbox);
		if (col < 0) return;
		vbox.prefWidthProperty().bind(this.getColumnConstraints().get(col).prefWidthProperty());
		
		for (Node node : vbox.getChildren()) {
			if (node instanceof Text) {
				((Text)node).wrappingWidthProperty().bind(vbox.prefWidthProperty());
			}
		}
	}
	
	// add a checkbox control
	public void addCheckbox(ScriptFieldAnswer.ScriptFieldAnswerBoolean answer) {
		CheckBox cb = new CheckBox(answer.getField().getNiceName());
		if (answer.answerProperty().get() == true) {
			cb.selectedProperty().set(true);
		}
		cb.selectedProperty().bindBidirectional(answer.answerProperty());
		addRow(cb);
		addHelpText(answer);
		
	}
	
	// add a label and a text field
	public void addTextField(ScriptFieldAnswer.ScriptFieldAnswerString answer) {
		Text label = new Text();
		label.setText(answer.getField().getNiceName() + ":");
		final TextField textField = new TextField();
		textField.textProperty().bindBidirectional(answer.answerProperty());
		addRow(label, textField);
		addHelpText(answer);
		
	}
}
