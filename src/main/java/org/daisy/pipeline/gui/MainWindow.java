package org.daisy.pipeline.gui;

import javafx.application.HostServices;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.gui.databridge.BoundScript;
import org.daisy.pipeline.gui.databridge.DataManager;
import org.daisy.pipeline.gui.databridge.EventBusListener;
import org.daisy.pipeline.gui.databridge.ObservableJob;
import org.daisy.pipeline.gui.databridge.Script;
import org.daisy.pipeline.gui.utils.PlatformUtils;
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
	private ScrollPane scrollPane;
	
	public SimpleObjectProperty<ObservableJob> currentJobProperty;
	private ChangeListener<ObservableJob> currentJobChangeListener;
	
	
	public MainWindow(ScriptRegistry scriptRegistry, 
			JobManagerFactory jobManagerFactory, Client client, EventBusProvider eventBusProvider,
			BundleContext context, HostServices hostServices) {
		super();
		
		this.eventBusProvider = eventBusProvider;
		this.bundleContext = context;
		this.scriptRegistry = scriptRegistry;
		this.jobManager = jobManagerFactory.createFor(client);
		this.hostServices = hostServices;
		
		currentJobProperty = new SimpleObjectProperty<ObservableJob>();
		addCurrentJobChangeListener();
		
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
	public SimpleObjectProperty<ObservableJob> getCurrentJobProperty() {
		return currentJobProperty;
	}
    private void buildWindow() {
    	scene = new Scene(this ,1024, 768);
    	String css = getClass().getResource("/org/daisy/pipeline/gui/resources/application.css").toExternalForm();
		scene.getStylesheets().add(css);
    		
		sidebar = new Sidebar(this);
		this.setLeft(sidebar);
		
		menubar = new AppMenu(this);
		if (PlatformUtils.isMac()) {
			this.getChildren().addAll(menubar);
		}
		else {
			this.setTop(menubar);
		}
		
		scrollPane = new ScrollPane();
		scrollPane.getStyleClass().add("center-scroll");
		
		detailsPane = new DetailsPane(this);
		
		newJobPane = new NewJobPane(this);
		
		messagesPane = new MessagesPane(this);
		this.setBottom(messagesPane);
		
		blankPane = new VBox();
		blankPane.getChildren().add(new Text("No job selected"));
		blankPane.getStyleClass().add("blank");
		this.setCenter(scrollPane);
		
		scrollPane.setContent(blankPane);
		
    }
    
    private void addCurrentJobChangeListener() {
    	final MainWindow thiz = this;
    	currentJobChangeListener = new ChangeListener<ObservableJob>() {

			public void changed(
					ObservableValue<? extends ObservableJob> observable,
					ObservableJob oldValue, ObservableJob newValue) {
				if (newValue == null) {
					//thiz.setCenter(blankPane);
					thiz.scrollPane.setContent(blankPane);
					return;
					
				}
				else {
//					if (thiz.getCenter() != detailsPane) {
//						thiz.setCenter(detailsPane);
//					}
					if (thiz.scrollPane.getContent() != detailsPane) {
						thiz.scrollPane.setContent(detailsPane);
					}
				}
				
			}
    		
    	};
    	currentJobProperty.addListener(currentJobChangeListener);
    }

    // convenience functions to add/clear validation messages
    public void addValidationMessages(ObservableList<String> messages) {
    	messagesPane.addMessages(messages);
    }
    public void clearValidationMessages() {
    	messagesPane.clearMessages();
    }
    
    /* GUI EVENTS */
    public void newJob() {
		currentJobProperty.set(null);
		//this.setCenter(newJobPane);
		scrollPane.setContent(newJobPane);
	}
	
    public void deleteSelectedJob() {
    	ObservableJob job = currentJobProperty.get();
    	if (job == null) {
    		return;
    	}
    	
    	jobManager.deleteJob(job.getJob().getId());
    	jobData.remove(job);
    	currentJobProperty.set(null);
    }
    
        
    // create a new job based on the currently-selected job
 	// display the new job pane
	public void runSelectedJobAgain() {
		ObservableJob job = currentJobProperty.get();
		// this shouldn't happen... 
		if (job == null) {
			return;
		}
		BoundScript boundScript = dataManager.cloneBoundScript(job.getBoundScript());
		newJobPane.newFromBoundScript(boundScript);
		currentJobProperty.set(null);
//		if (this.getCenter() != newJobPane) {
//			this.setCenter(newJobPane);
//		}
		if (this.scrollPane.getContent() != newJobPane) {
			this.scrollPane.setContent(newJobPane);
		}
		
	}
	
	// copy the messages to the clipboard
	public void copyMessages() {
		Iterable<String> messages = messagesPane.getMessages();
		final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        String clipboardString = "";
		
		for (String message : messages) {
			clipboardString += message + "\n";
		}
		content.putString(clipboardString);
		clipboard.setContent(content);
		
	}
}
