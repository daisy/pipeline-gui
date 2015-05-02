
package org.daisy.pipeline.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
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

import com.google.common.collect.Iterators;


public class NewJobPane extends GridPane {
	
	private GridPaneHelper scriptDetailsGrid;
	private MainWindow main;
	private ObservableList<Script> scripts;
	private BoundScript boundScript;
	
	
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
		scriptDetailsGrid.clearControls();
		main.clearValidationMessages();
	}
	public void newFromBoundScript(BoundScript boundScript) {
		scriptsCombo.getSelectionModel().select(boundScript.getScript());
		
	}
	
	private void initControls() {
		this.getStyleClass().add("new-job");
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
		
		scriptDetailsGrid = new GridPaneHelper();
		this.add(scriptDetailsGrid, 0, 1);
		
	}
	
	private void newScriptSelected(Script script) {
		if (script == null) {
			return;
		}
		main.clearValidationMessages();
		scriptDetailsGrid.clearControls();
		boundScript = new BoundScript(script);
		populateScriptDetailsGrid();
	}
	
	private void populateScriptDetailsGrid() {
		
		Text title = new Text();
		title.setText(boundScript.getScript().getName());
		title.getStyleClass().add("subtitle");
		scriptDetailsGrid.addRow(title);
		
		Text label = new Text();
		label.setText(boundScript.getScript().getDescription());
		scriptDetailsGrid.addRow(label);
		
		scriptDetailsGrid.addWebpageLinkRow("Read online documentation", 
				boundScript.getScript().getXProcScript().getHomepage());
		
		
		for (ScriptFieldAnswer input : boundScript.getInputFields()) {
			addInputField(input);
		}
		for (ScriptFieldAnswer option : boundScript.getRequiredOptionFields()) {
			addOptionField(option);
		}
		
		if (Iterators.size(boundScript.getOptionalOptionFields().iterator()) > 0) {
			Text options = new Text("Options:");
			options.getStyleClass().add("subtitle");
			scriptDetailsGrid.addRow(options);
		}
		for (ScriptFieldAnswer option : boundScript.getOptionalOptionFields()) {
			addOptionField(option);
		}
		
//		for (ScriptFieldAnswer output : boundScript.getOutputFields()) {
//			addOutputField(output);
//		}
		addStandardButtons();
			
	}
	
	
	private void addInputField(ScriptFieldAnswer answer) {
		scriptDetailsGrid.addFileDirPicker(answer);
	}

	private void addOptionField(ScriptFieldAnswer answer) {
		DataType fieldDataType = answer.getField().getDataType();
		if (fieldDataType == DataType.FILE) {
			scriptDetailsGrid.addFileDirPicker(answer);
		}
		else if (fieldDataType == DataType.DIRECTORY) {
			scriptDetailsGrid.addFileDirPicker(answer);
		}
		else if (fieldDataType == DataType.BOOLEAN) {
			scriptDetailsGrid.addCheckbox(answer);
		}
		else {
			scriptDetailsGrid.addTextField(answer);
		}
	}
	
//	private void addOutputField(ScriptFieldAnswer answer) {
//		scriptDetailsGrid.addFileDirPicker(answer);
//	}
//	
	private void addStandardButtons() {
		Button run = new Button("Run");
		run.getStyleClass().add("run-button");
		scriptDetailsGrid.addRow(run);
		
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
						main.getCurrentJobProperty().set(objob);
					}
					else {
						System.out.println("NEW JOB ERROR");
					}
				}
			}
		});
	
	}
}
