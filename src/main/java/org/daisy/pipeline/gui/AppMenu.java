package org.daisy.pipeline.gui;

import org.daisy.pipeline.gui.utils.PlatformUtils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class AppMenu extends MenuBar {

	private MainWindow main;
	private MenuItem deleteJob;
	
	public AppMenu(MainWindow main) {
		super();
		this.main = main;
		initControls();
	}
	
	private void initControls() {
		Menu menuFile = new Menu("File");
        
        this.getMenus().addAll(menuFile);
 
        MenuItem newjob = new MenuItem("New job");
        newjob.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
        menuFile.getItems().addAll(newjob);
        newjob.setOnAction(new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent t) {
        		main.newJob();
        	}
        });
        
        if (PlatformUtils.isMac()) {
        	this.setUseSystemMenuBar(true);
        }
        
        else {
        	MenuItem exit = new MenuItem("Exit");
        	exit.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN));
        	exit.setOnAction(new EventHandler<ActionEvent>() {
        	    public void handle(ActionEvent t) {
        	        System.exit(0);
        	    }
        	});
        	menuFile.getItems().add(exit);
        }
        
        deleteJob = new MenuItem("Delete job");
        deleteJob.setOnAction(new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent t) {
        		main.deleteSelectedJob();
        	}
        });
        menuFile.getItems().add(deleteJob);
        deleteJob.setDisable(true);
        
	}
	
	public void enableDeleteJob(boolean value) {
		deleteJob.setDisable(!value);
	}
}
