
package org.daisy.pipeline.gui;

import java.io.File;
import java.util.HashMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;

import org.daisy.pipeline.gui.databridge.BoundScript;
import org.daisy.pipeline.gui.databridge.JobExecutor;
import org.daisy.pipeline.gui.databridge.Script;
import org.daisy.pipeline.gui.databridge.ScriptField.DataType;
import org.daisy.pipeline.gui.databridge.ScriptFieldAnswer;
import org.daisy.pipeline.job.Job;


public class NewJobPane extends GridPane {
	
	private GridPane subGrid;
	private MainWindow main;
	private ObservableList<Script> scripts;
	private BoundScript boundScript;
	private int subgridRowCount;
	
	public NewJobPane(MainWindow main) {
		this.main = main;
		scripts = main.getScriptData();
		initControls();
		
	}
	
	public BoundScript getBoundScript() {
		return boundScript;
	}
	
	private void initControls() {
		this.setPadding(new Insets(10));
		this.setHgap(10);
	    this.setVgap(10);
	    
		Text title = new Text("Choose a script:");
		this.add(title,  0,  0);
		
		final ComboBox<Script> scriptsCombo = new ComboBox<Script>(scripts);
		scriptsCombo.setCellFactory(new Callback<ListView<Script>,ListCell<Script>>(){
			 
            public ListCell<Script> call(ListView<Script> p) {
                final ListCell<Script> cell = new ListCell<Script>(){
                    @Override
                    protected void updateItem(Script t, boolean bln) {
                        super.updateItem(t, bln);
                         
                        if(t != null) {
                            setText(t.getName());
                        }
                        else {
                            setText(null);
                        }
                    }
                };
                return cell;
            }			
        });
		scriptsCombo.setConverter(new StringConverter<Script>() {
            @Override
            public String toString(Script script) {
              if (script == null){
                return null;
              } 
              else {
                return script.getName();
              }
            }

			@Override
			public Script fromString(String string) {
				// TODO Auto-generated method stub
				return null;
			}

          
      });
		this.add(scriptsCombo, 1, 0);
		
		scriptsCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Script>() {

			public void changed(ObservableValue<? extends Script> observable,
					Script oldValue, Script newValue) {
				
				newScriptSelected(newValue);
			}
		});
		
		subGrid = new GridPane();
		this.add(subGrid, 0, 1);
		
	}
	
	private void newScriptSelected(Script script) {
		clearSubGrid();
		boundScript = new BoundScript(script);
		populateSubGrid();
	}
	
	private void clearSubGrid() {
		int sz = subGrid.getChildren().size();
		if (sz > 0) {
			subGrid.getChildren().remove(0, sz - 1);
		}
	}
	
	private void populateSubGrid() {
		
		subgridRowCount = 1;
		for (ScriptFieldAnswer input : boundScript.getInputFields()) {
			addInputField(input);
		}
		for (ScriptFieldAnswer option : boundScript.getOptionFields()) {
			addOptionField(option);
		}
		for (ScriptFieldAnswer output : boundScript.getOutputFields()) {
			addOutputField(output);
		}
		addStandardButtons();
			
	}
	
	
	private void addInputField(ScriptFieldAnswer answer) {
		addFileDirPicker(answer);
	}

	private void addOptionField(ScriptFieldAnswer answer) {
		DataType fieldDataType = answer.getField().getDataType();
		if (fieldDataType == DataType.FILE) {
			addFileDirPicker(answer);
		}
		else if (fieldDataType == DataType.DIRECTORY) {
			addFileDirPicker(answer);
		}
		else if (fieldDataType == DataType.BOOLEAN) {
			addCheckbox(answer);
		}
		else {
			addTextField(answer);
		}
	}
	
	private void addOutputField(ScriptFieldAnswer answer) {
		addFileDirPicker(answer);
	}
	
	private void addFileDirPicker(ScriptFieldAnswer answer) {
		Text label = new Text();
		label.setText(answer.getField().getNiceName() + ":");
		subGrid.add(label, 0, subgridRowCount);
		final TextField inputFileText = new TextField();
		inputFileText.textProperty().bindBidirectional(answer.answerProperty());
		subGrid.add(inputFileText, 1, subgridRowCount);
		Button inputFileButton = new Button("Browse");
		subGrid.add(inputFileButton, 2, subgridRowCount);
		subgridRowCount++;
		
		final ScriptFieldAnswer answer_ = answer;
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
	private void addCheckbox(ScriptFieldAnswer answer) {
		CheckBox cb = new CheckBox(answer.getField().getNiceName());
		cb.selectedProperty().bindBidirectional(answer.booleanAnswerProperty());
		subGrid.add(cb, 0, subgridRowCount);
		subgridRowCount++;
		
	}
	private void addTextField(ScriptFieldAnswer answer) {
		Text label = new Text();
		label.setText(answer.getField().getNiceName() + ":");
		subGrid.add(label, 0, subgridRowCount);
		final TextField textField = new TextField();
		textField.textProperty().bindBidirectional(answer.answerProperty());
		subGrid.add(textField, 1, subgridRowCount);
		subgridRowCount++;
	}
	private void addStandardButtons() {
		Button cancel = new Button("Cancel");
		subGrid.add(cancel, 0, subgridRowCount);
		Button run = new Button("Run");
		subGrid.add(run, 1, subgridRowCount);
		subgridRowCount++; // no controls are added after these buttons but we'll increment anyway for good measure
		
		run.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Job newJob = JobExecutor.runJob(main, boundScript);
				if (newJob != null) {
					main.getDataManager().addJob(newJob);
				}
				else {
					System.out.println("NEW JOB ERROR");
				}
			}
		});
	
	}
}
