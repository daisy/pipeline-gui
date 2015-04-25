package org.daisy.pipeline.gui;

import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.gui.databridge.DataManager;
import org.daisy.pipeline.gui.databridge.EventBusListener;
import org.daisy.pipeline.gui.databridge.ObservableJob;
import org.daisy.pipeline.gui.databridge.Script;
import org.daisy.pipeline.job.Job.Status;
import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.job.JobManagerFactory;
import org.daisy.pipeline.script.ScriptRegistry;
import org.osgi.framework.BundleContext;

public class MainWindow extends BorderPane {
    

	private JobManager jobManager;
    private ScriptRegistry scriptRegistry;    
    private EventBusListener eventBusListener;
    private BundleContext bundleContext;
    private EventBusProvider eventBusProvider;
    private HostServices hostServices;
    private DataManager dataManager;
    private ObservableList<ObservableJob> jobData;
    private ObservableList<Script> scriptData;
    	
	private Sidebar sidebar;
	private DetailsPane detailsPane;
	private MessagesPane messagesPane;
	private AppMenu menubar;
	private NewJobPane newJobPane;
	private Scene scene;
	private VBox blankPane;
	
	
    public MainWindow(ScriptRegistry scriptRegistry, 
			JobManagerFactory jobManagerFactory, Client client, EventBusProvider eventBusProvider,
			BundleContext context, HostServices hostServices) {
		super();
		
		this.eventBusProvider = eventBusProvider;
		this.bundleContext = context;
		this.scriptRegistry = scriptRegistry;
		this.jobManager = jobManagerFactory.createFor(client);
		this.hostServices = hostServices;
		
		
		
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
	public HostServices getHostServices() {
		return hostServices;
	}
    private void buildWindow() {
    	scene = new Scene(this ,1024, 768);
		//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
		sidebar = new Sidebar(this);
		this.setLeft(sidebar);
		
		menubar = new AppMenu(this);
		this.getChildren().addAll(menubar);
		
		detailsPane = new DetailsPane(this);
		
		newJobPane = new NewJobPane(this);
		
		messagesPane = new MessagesPane(this);
		this.setBottom(messagesPane);
		
		messagesPane.setPrefHeight(150);
		
		blankPane = new VBox();
		blankPane.getChildren().add(new Text("No job selected"));
		this.setCenter(blankPane);
		
    }
    
    /* GUI EVENTS */
    public void notifySidebarSelectChange(ObservableJob job) {
    	if (job == null) {
    		showBlank();
    	}
    	showJob(job);
	}
	
	public void newJob() {
		sidebar.clearSelection();
		newJobPane.clearScriptDetails();
		messagesPane.clearMessages();
		this.setCenter(newJobPane);
		
		//this.setBottom(null); // remove the messages pane
	}
    public void deleteSelectedJob() {
    	ObservableJob job = sidebar.getSelectedJob();
    	if (job == null) {
    		return;
    	}
    	
    	jobManager.deleteJob(job.getJob().getId());
    	jobData.remove(job);
    }
    public void selectJob(ObservableJob job) {
    	sidebar.setSelectedJob(job);
    }
    private void showJob(ObservableJob job) {
    	if (job == null) {
    		menubar.enableDeleteJob(false);
    		return;
    	}
		if (this.getCenter() != detailsPane) {
			this.setCenter(detailsPane);
		}
		this.setBottom(messagesPane);
		detailsPane.setJob(job);
		messagesPane.setJob(job);
		Status status = job.getJob().getStatus();
		if (status == Status.DONE || status == Status.ERROR || status == Status.VALIDATION_FAIL) {
			menubar.enableDeleteJob(true);
		}
    }
    public void addValidationMessages(ObservableList<String> messages) {
    	messagesPane.addMessages(messages);
    }
    private void showBlank() {
    	this.setCenter(blankPane);
    }
}
