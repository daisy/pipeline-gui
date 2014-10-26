package org.daisy.pipeline.gui;

import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.job.JobManagerFactory;
import org.daisy.pipeline.script.ScriptRegistry;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
 

public class MainWindow extends ApplicationWindow {

	
    private JobManager jobManager = null;
    private ScriptRegistry scriptRegistry = null;    
    private Client client = null;
    private EventBusListener eventBusListener = null;
    private GuiController guiController;
	
    
	public MainWindow(Shell parentShell, ScriptRegistry scriptRegistry, 
			JobManagerFactory jobManagerFactory, Client client, EventBusProvider eventBusProvider) {
		super(parentShell);
		Display.setAppName("DAISY Pipeline 2");
		this.scriptRegistry = scriptRegistry;
		this.jobManager = jobManagerFactory.createFor(client);
		this.client = client; 
		guiController = new GuiController();
		eventBusListener = new EventBusListener(eventBusProvider, guiController, getJobManager());
				
	}
	
	protected Control createContents(Composite parent) {
		guiController.buildGui(this);
        this.getShell().pack();
	    return parent;
	}
    
    public JobManager getJobManager() {
    	return jobManager;
    }
    
    public ScriptRegistry getScriptRegistry() {
    	return scriptRegistry;
    }
    
    public GuiController getGuiController() {
    	return guiController;
    }
        	
}
