
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
import org.daisy.pipeline.gui.databridge.ObservableJob;
import org.daisy.pipeline.gui.databridge.Script;
import org.daisy.pipeline.gui.databridge.ScriptField.DataType;
import org.daisy.pipeline.gui.databridge.ScriptFieldAnswer;
import org.daisy.pipeline.gui.databridge.ScriptValidator;
import org.daisy.pipeline.job.Job;


public class NewJobPane extends GridPane {
	
	private GridPane scriptDetailsGrid;
	private MainWindow main;
	private ObservableList<Script> scripts;
	private BoundScript boundScript;
	private int subgridRowCount;
	private final ComboBox<Script> scriptsCombo = new ComboBox<Script>();
	
	public NewJobPane(MainWindow main) {
		this.main = main;
		scripts = main.getScriptData();
		initControls();
		
	}
	
	public BoundScript getBoundScript() {
		return boundScript;
	}
	
	// reset the combo selection and clear the script details grid
	public void clearScriptDetails() {
		scriptsCombo.getSelectionModel().clearSelection();
		clearScriptDetailsGrid();
	}
	
	private void initControls() {
		this.setPadding(new Insets(10));
		this.setHgap(10);
	    this.setVgap(10);
	    GridPane topGrid = new GridPane();
	    this.add(topGrid, 0, 0);
	    
		Text title = new Text("Choose a script:");
		topGrid.add(title,  0,  0);
		
		scriptsCombo.setItems(scripts);
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
		
		topGrid.add(scriptsCombo, 1, 0);
		
		
		scriptsCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Script>() {

			public void changed(ObservableValue<? extends Script> observable,
					Script oldValue, Script newValue) {
				
				newScriptSelected(newValue);
			}
		});
		
		scriptDetailsGrid = new GridPane();
		this.add(scriptDetailsGrid, 0, 1);
		
	}
	
	private void newScriptSelected(Script script) {
		if (script == null) {
			return;
		}
		clearScriptDetailsGrid();
		boundScript = new BoundScript(script);
		populateScriptDetailsGrid();
	}
	
	// clear the script details grid (not including the combo)
	private void clearScriptDetailsGrid() {
		int sz = scriptDetailsGrid.getChildren().size();
		if (sz > 0) {
			scriptDetailsGrid.getChildren().remove(0, sz - 1);
		}
		
	}
	
	private void populateScriptDetailsGrid() {
		
		subgridRowCount = 1;
		
		Text label = new Text();
		label.setText(boundScript.getScript().getDescription());
		scriptDetailsGrid.add(label, 0, subgridRowCount);
		subgridRowCount++;
		
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
		scriptDetailsGrid.add(label, 0, subgridRowCount);
		final TextField inputFileText = new TextField();
		inputFileText.textProperty().bindBidirectional(answer.answerProperty());
		scriptDetailsGrid.add(inputFileText, 1, subgridRowCount);
		Button inputFileButton = new Button("Browse");
		scriptDetailsGrid.add(inputFileButton, 2, subgridRowCount);
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
		scriptDetailsGrid.add(cb, 0, subgridRowCount);
		subgridRowCount++;
		
	}
	private void addTextField(ScriptFieldAnswer answer) {
		Text label = new Text();
		label.setText(answer.getField().getNiceName() + ":");
		scriptDetailsGrid.add(label, 0, subgridRowCount);
		final TextField textField = new TextField();
		textField.textProperty().bindBidirectional(answer.answerProperty());
		scriptDetailsGrid.add(textField, 1, subgridRowCount);
		subgridRowCount++;
	}
	private void addStandardButtons() {
		Button run = new Button("Run");
		
		scriptDetailsGrid.add(run, 1, subgridRowCount);
		subgridRowCount++; // no controls are added after these buttons but we'll increment anyway for good measure
		
		run.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				ScriptValidator validator = new ScriptValidator(boundScript);
				if (!validator.validate()) {
					ObservableList<String> messages = validator.getMessages();
					main.addValidationMessages(messages);
				}
				else {
					Job newJob = JobExecutor.runJob(main, boundScript);
					if (newJob != null) {
						ObservableJob objob = main.getDataManager().addJob(newJob);
						objob.setBoundScript(boundScript);
						main.selectJob(objob);
					}
					else {
						System.out.println("NEW JOB ERROR");
					}
				}
			}
		});
	
	}
}
