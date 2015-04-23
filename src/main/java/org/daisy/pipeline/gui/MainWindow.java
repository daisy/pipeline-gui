package org.daisy.pipeline.gui;

import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.gui.databridge.DataManager;
import org.daisy.pipeline.gui.databridge.EventBusListener;
import org.daisy.pipeline.gui.databridge.ObservableJob;
import org.daisy.pipeline.gui.databridge.Script;
import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.job.JobManagerFactory;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.XProcScript;
import org.daisy.pipeline.script.XProcScriptService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.launch.Framework;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainWindow extends BorderPane {
    

	private JobManager jobManager;
    private ScriptRegistry scriptRegistry;    
    private EventBusListener eventBusListener;
    private BundleContext bundleContext;
    private EventBusProvider eventBusProvider;
    private DataManager dataManager;
    private ObservableList<ObservableJob> jobData;
    private ObservableList<Script> scriptData;
    	
	private Sidebar sidebar;
	private DetailsPane detailsPane;
	private MessagesPane messagesPane;
	private AppMenu menubar;
	private NewJobPane newJobPane;
	private Scene scene;
	
    public MainWindow(ScriptRegistry scriptRegistry, 
			JobManagerFactory jobManagerFactory, Client client, EventBusProvider eventBusProvider,
			BundleContext context) {
		super();
		
		this.eventBusProvider = eventBusProvider;
		this.bundleContext = context;
		this.scriptRegistry = scriptRegistry;
		this.jobManager = jobManagerFactory.createFor(client);
		
		
		
		
		jobData = FXCollections.observableArrayList();
		scriptData = FXCollections.observableArrayList();
		dataManager = new DataManager(this);
		this.eventBusListener = new EventBusListener(this);	
		eventBusProvider.get().register(eventBusListener);
		
		buildWindow();	
    }
    	
	
	
    public JobManager getJobManager() {
    	return jobManager;
    }
    
    public ScriptRegistry getScriptRegistry() {
    	return scriptRegistry;
    }

    public EventBusProvider getEventBusProvider() {
    	return eventBusProvider;
    }
    
    public BundleContext getBundleContext() {
    	return bundleContext;
    }
	public DataManager getDataManager() {
		return dataManager;
	}
	public ObservableList<ObservableJob> getJobData() {
		return jobData;
	}
	public ObservableList<Script> getScriptData() {
		return scriptData;
	}
	public NewJobPane getNewJobPane() {
		return newJobPane;
	}
    private void buildWindow() {
    	scene = new Scene(this ,1024, 768);
		//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
		sidebar = new Sidebar(this);
		this.setLeft(sidebar);
		
		menubar = new AppMenu(this);
		this.getChildren().addAll(menubar);
		
		detailsPane = new DetailsPane(this);
		this.setCenter(detailsPane);
		
		newJobPane = new NewJobPane(this);
		
		messagesPane = new MessagesPane(this);
		this.setBottom(messagesPane);
		
		messagesPane.setPrefHeight(150);
		
    }
    
    /* GUI EVENTS */
    public void notifySidebarSelectChange(ObservableJob job) {
    	if (job == null) {
    		menubar.enableDeleteJob(false);
    		return;
    	}
		if (this.getCenter() != detailsPane) {
			this.setCenter(detailsPane);
		}
		detailsPane.setJob(job);
		messagesPane.setJob(job);
		menubar.enableDeleteJob(true);
	}
	
	public void newJob() {
		this.setCenter(newJobPane);
	}
    public void deleteSelectedJob() {
    	ObservableJob job = sidebar.getSelectedJob();
    	if (job == null) {
    		return;
    	}
    	
    	jobManager.deleteJob(job.getJob().getId());
    	jobData.remove(job);
    }
}
