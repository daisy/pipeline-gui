package org.daisy.pipeline.gui;

import org.daisy.common.messaging.Message;
import org.daisy.pipeline.gui.handlers.AboutAction;
import org.daisy.pipeline.gui.handlers.DeleteJobAction;
import org.daisy.pipeline.gui.handlers.ExitAction;
import org.daisy.pipeline.gui.handlers.NewJobAction;
import org.daisy.pipeline.gui.handlers.PreferencesAction;
import org.daisy.pipeline.gui.handlers.QuitListener;
import org.daisy.pipeline.gui.handlers.RefreshJobsAction;
import org.daisy.pipeline.gui.utils.CocoaUIEnhancer;
import org.daisy.pipeline.gui.utils.PlatformUtils;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobUUIDGenerator;
import org.daisy.pipeline.job.StatusMessage;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.XProcScript;
import org.daisy.pipeline.script.XProcScriptService;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GuiController {

	// the panel to show when no job is selected
	JobPanelEmptyView jobPanelEmptyView;
	// the panel to show when the user wants to create a new job
	private JobPanelNewJobView jobPanelNewJobView = null;
	// the panel to show when the user has selected a job from the sidebar
	JobPanelDetailView jobPanelDetailView;
	
	// layout components
	StackLayout jobDetailParentStackLayout;
	Composite jobDetailParentComposite;
	
	// the application window
	private MainWindow window;
	//private MenuItem newJobMenuItem;
	//private MenuItem deleteJobMenuItem;
	//private Menu fileMenu = null;
	DeleteJobAction deleteJobAction;
	PreferencesAction preferencesAction;
	private EventBusListener eventBusListener;
	private JobTable jobTable;
	
	
	private static final Logger logger = LoggerFactory.getLogger(GuiController.class);
	
	public MainWindow getWindow() {
		return window;
	}
	
	public EventBusListener getEventBusListener() {
		return this.eventBusListener;
	}
	public void setEventBusListener(EventBusListener eventBusListener) {
		this.eventBusListener = eventBusListener;
	}
	
	public void buildGui(MainWindow window){
		logger.debug("Initializing application layout");
		this.window = window;
		Shell shell = window.getShell();
		shell.setText("DAISY Pipeline 2");
        shell.setSize(2048, 1024);
        
        shell.setLayout(new FillLayout());
	    SashForm sashForm = new SashForm(shell, SWT.HORIZONTAL);
		
        // jobs list area
		Composite jobTableComposite = new Composite(sashForm, SWT.PUSH);
		setJobTable(new JobTable(jobTableComposite, this));
		
        // jobs detail area
        jobDetailParentStackLayout = new StackLayout();
        jobDetailParentComposite = new Composite(sashForm, SWT.PUSH);
        jobDetailParentComposite.setLayout(jobDetailParentStackLayout);
        
        // this should work...but it doesn't
//        final ScrolledComposite sc1 = new ScrolledComposite(jobDetailParentComposite.getParent(), SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
//        sc1.setContent(jobDetailParentComposite);
//		
		sashForm.setWeights(new int[] {1, 2});

        Label label = new Label(jobTableComposite, 0);
		label.setText("Jobs");
		
        jobPanelEmptyView = new JobPanelEmptyView(jobDetailParentComposite);
        jobPanelDetailView = new JobPanelDetailView(jobDetailParentComposite, this);
        
        deleteJobAction = new DeleteJobAction(this);
        deleteJobAction.setEnabled(false);
        preferencesAction = new PreferencesAction(this);
        preferencesAction.setEnabled(false);
        
        buildMenus();
        
        final MainWindow window_ = window; 
        window_.getShell().addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  try {
					window_.exit();
				} catch (BundleException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	  event.doit = false;
		      }
		    });
		
        
        getJobTable().refreshJobs();
	}
	
	public void showJobDetailView(Job job){
		// also enable the job-specific menu items
		//deleteJobMenuItem.setEnabled(true);
		deleteJobAction.setEnabled(true);
		
		// make sure the job object is up to date
		Job theJob = this.getWindow().getJobManager().getJob(job.getId()).get();
    	// update with job data
    	jobPanelDetailView.refreshData(theJob);
    	// bring panel into view
    	jobDetailParentStackLayout.topControl = jobPanelDetailView;
    	jobPanelDetailView.pack();
		jobDetailParentComposite.layout();
		
    }
    public void showEmptyView(){
    	//deleteJobMenuItem.setEnabled(false);
    	deleteJobAction.setEnabled(false);
    	jobPanelEmptyView.setLabel(JobPanelEmptyView.SELECT_A_JOB);
    	// bring panel into view
    	jobDetailParentStackLayout.topControl = jobPanelEmptyView;
    	jobPanelEmptyView.pack();
    	jobDetailParentComposite.layout();
    }
    
    public void showMultipleSelectedView() {
		//deleteJobMenuItem.setEnabled(true);
    	deleteJobAction.setEnabled(true);
		this.jobPanelEmptyView.setLabel(JobPanelEmptyView.MULTIPLE_SELECTED);
    	// bring panel into view
    	jobDetailParentStackLayout.topControl = jobPanelEmptyView;
		jobDetailParentComposite.layout();
	}
	
	public void showNewJobView(XProcScript script) {
		//deleteJobMenuItem.setEnabled(false);
		deleteJobAction.setEnabled(false);
		
		// create the panel from scratch
		if (getJobPanelNewJobView() != null && !getJobPanelNewJobView().isDisposed()) {
			getJobPanelNewJobView().dispose();
		}
		setJobPanelNewJobView(new JobPanelNewJobView(jobDetailParentComposite, script, this));
		// bring into view
		jobDetailParentStackLayout.topControl = getJobPanelNewJobView();
		getJobPanelNewJobView().pack();
		getJobPanelNewJobView().layout(true);
		jobDetailParentComposite.layout(true);
		getWindow().getShell().layout(true);
	    
	}

	// in lieu of a perfectly working event bus, here's a manual refresh method
	public void refreshAll() {

		getJobTable().refreshJobs();
		
		// update the view if we are looking at this job currently
		if (this.jobDetailParentStackLayout.topControl == this.jobPanelDetailView) {
			Job job = jobPanelDetailView.getJob();
			// get the most up to date version of this job
			Job theJob = window.getJobManager().getJob(job.getId()).get();
			jobPanelDetailView.refreshData(theJob);
		}
	}

	private void buildMenus() {
		MenuManager menuBar = new MenuManager();
		MenuManager fileMenu = new MenuManager("&File");
		MenuManager newJobMenu = new MenuManager("&New job");
		menuBar.add(fileMenu);
		fileMenu.add(newJobMenu);
		fileMenu.add(new RefreshJobsAction(this));
		fileMenu.add(deleteJobAction);
		
		
		// add scripts to the "new job" menu
		ScriptRegistry scriptRegistry = window.getScriptRegistry();
        Iterable<XProcScriptService> scripts = scriptRegistry.getScripts();
        for (XProcScriptService scriptServ : scripts) {
        	XProcScript script = scriptServ.load();
        	NewJobAction action = new NewJobAction(this, script);
        	newJobMenu.add(action);
        }
        
        if (PlatformUtils.isMac()) {
        	// TODO make this work
//            CocoaUIEnhancer enhancer = new CocoaUIEnhancer( "DAISY Pipeline 2" );
//            enhancer.hookApplicationMenu(window.getShell().getDisplay(), 
//            		new QuitListener(this), 
//            		new AboutAction(this), 
//            		preferencesAction);
        } 
        else {
        	// TODO add preferences (when we have some)
        	fileMenu.add(new ExitAction(this));
        	MenuManager helpMenu = new MenuManager("&Help");
        	helpMenu.add(new AboutAction(this));
        	menuBar.add(helpMenu);
        }
        
        window.getShell().setMenuBar(menuBar.createMenuBar((Decorations)window.getShell()));
		
	}
    
    
	public void statusUpdate(StatusMessage msg) {
		Job job = window.getJobManager().getJob(msg.getJobId()).get();
		System.out.println("+GUI STATUS CHANGE to " + job.getStatus().toString() + "(" + job.getId().toString() + ")");
		// just for testing: see if DONE updates are coming through
		if (job.getStatus() == Job.Status.DONE) {
		
			refreshDataThreadSafe(job);
		}
	}
	
	public void messageUpdate(Message msg) {
//		JobUUIDGenerator gen = new JobUUIDGenerator();
//		JobId jobId = gen.generateIdFromString(msg.getJobId());
//		Job job = window.getJobManager().getJob(jobId).get();
//		refreshDataThreadSafe(job);
	}

	private void refreshDataThreadSafe(Job job) {
		final Job job_ = job;
		Display display = window.getShell().getDisplay();
		if (!(display==null || display.isDisposed())) {
			display.asyncExec(new Runnable () {
				public void run() {
					// refresh the sidebar
					getJobTable().refreshJobs();
					
					// refresh the detail view if we are looking at the job that the update is concerning
					Job currentJob = jobPanelDetailView.getJob();
					if (currentJob.getId().toString().equals(job_.getId().toString())) {
						jobPanelDetailView.refreshData(job_);
					}
					
				}
			});
		}
	}
	
	public JobTable getJobTable() {
		return jobTable;
	}

	public void setJobTable(JobTable jobTable) {
		this.jobTable = jobTable;
	}

	public JobPanelNewJobView getJobPanelNewJobView() {
		return jobPanelNewJobView;
	}

	public void setJobPanelNewJobView(JobPanelNewJobView jobPanelNewJobView) {
		this.jobPanelNewJobView = jobPanelNewJobView;
	}

}
