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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPipelineInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.gui.databridge.JobExecutor;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.script.XProcPortMetadata;
import org.daisy.pipeline.script.XProcScript;

public class NewJobPane extends GridPane {
	
	private GridPane subGrid;
	private MainWindow main;
	private ObservableList<XProcScript> scripts;
	
	public HashMap<String, TextField> inputArguments;
	
	public NewJobPane(MainWindow main) {
		this.main = main;
		initControls();
		scripts = main.getScripts();
	}
	
	// just have one of each type of control
	private void initControls() {
		this.setPadding(new Insets(10));
		this.setHgap(10);
	    this.setVgap(10);
	    
		Text title = new Text("Choose a script");
		this.add(title,  0,  0);
		
		final ComboBox<XProcScript> scriptsCombo = new ComboBox<XProcScript>(scripts);
		scriptsCombo.setCellFactory(new Callback<ListView<XProcScript>,ListCell<XProcScript>>(){
			 
            public ListCell<XProcScript> call(ListView<XProcScript> p) {
                final ListCell<XProcScript> cell = new ListCell<XProcScript>(){
 
                    @Override
                    protected void updateItem(XProcScript t, boolean bln) {
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
		this.add(scriptsCombo, 1, 0);
		
		scriptsCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<XProcScript>() {

			public void changed(ObservableValue<? extends XProcScript> observable,
					XProcScript oldValue, XProcScript newValue) {
				
				newScriptSelected(newValue);
			}
		});
		
		subGrid = new GridPane();
		this.add(subGrid, 0, 1);
		
	}
	
	private void newScriptSelected(XProcScript script) {
		clearSubGrid();
		populateSubGrid(script);
	}
	
	private void clearSubGrid() {
		int sz = subGrid.getChildren().size();
		if (sz > 0) {
			subGrid.getChildren().remove(0, sz - 1);
		}
	}
	
	// in the real gui, this function would change based on the script that was selected
	// for this, we'll just add an example of the types of controls that you'd find there
	private void populateSubGrid(final XProcScript script) {
		XProcPipelineInfo scriptInfo = script.getXProcPipelineInfo();
		Iterable<XProcPortInfo> inputPorts = scriptInfo.getInputPorts();
		Iterable<XProcPortInfo> outputPorts = scriptInfo.getOutputPorts();
		Iterable<XProcOptionInfo> options = scriptInfo.getOptions();
		
		inputArguments = new HashMap<String, TextField>();
		
		for (XProcPortInfo input : inputPorts) {
			XProcPortMetadata meta = script.getPortMetadata(input.getName());
			final TextField inputFileText = new TextField();
			inputArguments.put(input.getName(), inputFileText);
			subGrid.add(inputFileText, 1, 0);
			
			Button inputFileButton = new Button("Browse");
			subGrid.add(inputFileButton, 2, 0);
			
			inputFileButton.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
	                FileChooser fileChooser = new FileChooser();
	                fileChooser.setTitle("Select File");
	                File file = fileChooser.showOpenDialog(null);
	                if(file != null) {
	                	inputFileText.setText(file.getPath());
	                }
				}
			});
		}
		
				
		Button cancel = new Button("Cancel");
		subGrid.add(cancel, 0, 3);
		Button run = new Button("Run");
		subGrid.add(run, 1, 3);
		
		run.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Job newJob = JobExecutor.runJob(main, script);
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
