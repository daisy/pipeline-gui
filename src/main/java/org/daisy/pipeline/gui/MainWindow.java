package org.daisy.pipeline.gui;

import java.util.Iterator;

import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.clients.ClientStorage;
import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.job.JobManagerFactory;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.XProcScriptService;
//import org.daisy.pipeline.webserviceutils.storage.WebserviceStorage;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.BundleContext;

import com.google.common.base.Supplier;
 

public class MainWindow extends ApplicationWindow {

	/** The job manager. */
    private JobManagerFactory jobManagerFactory;

    /** The script registry. */
    private ScriptRegistry scriptRegistry;
   // private ClientStorage clientStorage;

    public void init(BundleContext ctxt) {
    	
    }
    
    // there was a naming conflict with "close()" so i used "uninit()" 
    public void uninit() throws Exception {
    	
    }
    
//    
//    private Supplier<JobManager> jobManager= new Supplier<JobManager>() {
//
//        @Override
//        public JobManager get() {
//                return jobManagerFactory.createFor(clientStorage.defaultClient());
//        }
//
//};

    /**
     * Gets the job manager.
     *
     * @return the job manager
     */
    public JobManager getJobManager(Client client) {
            return jobManagerFactory.createFor(client);
    }

    /**
     * Sets the job manager.
     *
     * @param jobManager the new job manager
     */
    public void setJobManagerFactory(JobManagerFactory jobManagerFactory) {
    	System.out.println("Setting job manager factory");
            this.jobManagerFactory = jobManagerFactory;
    }
    
    
    /**
     * Gets the script registry.
     *
     * @return the script registry
     */
    public ScriptRegistry getScriptRegistry() {
    	System.out.println("setting script registry for GUI");
            return scriptRegistry;
    }

    /**
     * Sets the script registry.
     *
     * @param scriptRegistry the new script registry
     */
    public void setScriptRegistry(ScriptRegistry scriptRegistry) {
            this.scriptRegistry = scriptRegistry;
    }
//    public void setWebserviceStorage(WebserviceStorage storage) {
//        this.clientStorage = storage.getClientStorage();
//        
//}
	public MainWindow(Shell parentShell) {
		super(parentShell);
	}
	
	protected Control createContents(Composite parent) {
        Shell shell = getShell();
        shell.setText("JFace experiment");
        shell.setSize(300, 200);
        shell.setLayout(new FillLayout(SWT.HORIZONTAL));
        addList(shell);
        shell.pack();
	    return parent;
	}

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("New Application");
    }
    protected Point getInitialSize() {
        return new Point(500, 375);
    }
    private void addList(Shell shell){
        final List list = new List (shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        
        if (this.scriptRegistry != null) {
	        Iterable<XProcScriptService> scripts = this.scriptRegistry.getScripts();
	        Iterator<XProcScriptService> it = scripts.iterator();
	        while (it.hasNext()) {
	        	XProcScriptService script = it.next();
	        	list.add(script.getDescription());
	        }
        }
        
        
        //Rectangle clientArea = shell.getClientArea();
        //list.setBounds (clientArea.x, clientArea.y, 100, 100);
        list.addListener (SWT.Selection, new Listener () {
            @Override
            public void handleEvent (Event e) {
            	
            }
        });
    }
}
