package org.daisy.pipeline.gui;

import org.daisy.common.messaging.Message;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.StatusMessage;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.XProcScript;
import org.daisy.pipeline.script.XProcScriptService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GuiController {

	// the panel to show when no job is selected
	JobPanelEmptyView jobPanelEmptyView;
	// the panel to show when the user wants to create a new job
	JobPanelNewJobView jobPanelNewJobView = null;
	// the panel to show when the user has selected a job from the sidebar
	JobPanelDetailView jobPanelDetailView;
	
	// layout components
	StackLayout jobDetailParentStackLayout;
	Composite jobDetailParentComposite;
	
	// the application window
	private MainWindow window;
	private MenuItem newJobMenuItem;
	private MenuItem deleteJobMenuItem;
	private Menu fileMenu = null;
	private EventBusListener eventBusListener;
	JobTable jobTable;
	
	
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
		jobTable = new JobTable(jobTableComposite, this);
		
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
        
        buildMenus();
        jobTable.refreshJobs();
	}
	
	public void showJobDetailView(Job job){
		// also enable the job-specific menu items
		deleteJobMenuItem.setEnabled(true);
		
		// make sure the job object is up to date
		Job theJob = this.getWindow().getJobManager().getJob(job.getId()).get();
    	// update with job data
    	jobPanelDetailView.refreshData(theJob);
    	// bring panel into view
    	jobDetailParentStackLayout.topControl = jobPanelDetailView;
		jobDetailParentComposite.layout();
		
    }
    public void showEmptyView(){
    	deleteJobMenuItem.setEnabled(false);
    	jobPanelEmptyView.setLabel(JobPanelEmptyView.SELECT_A_JOB);
    	// bring panel into view
    	jobDetailParentStackLayout.topControl = jobPanelEmptyView;
		jobDetailParentComposite.layout();
    }
    
    public void showMultipleSelectedView() {
		deleteJobMenuItem.setEnabled(true);
		this.jobPanelEmptyView.setLabel(JobPanelEmptyView.MULTIPLE_SELECTED);
    	// bring panel into view
    	jobDetailParentStackLayout.topControl = jobPanelEmptyView;
		jobDetailParentComposite.layout();
	}
	
	public void showNewJobPanel(XProcScript script) {
		deleteJobMenuItem.setEnabled(false);
		
		// create the panel from scratch
		if (jobPanelNewJobView != null && !jobPanelNewJobView.isDisposed()) {
			jobPanelNewJobView.dispose();
		}
		jobPanelNewJobView = new JobPanelNewJobView(jobDetailParentComposite, script, this);
		// bring into view
		jobDetailParentStackLayout.topControl = jobPanelNewJobView;
		jobPanelNewJobView.layout(true);
		jobDetailParentComposite.layout(true);
		getWindow().getShell().layout(true);
	    
	}

	// in lieu of a perfectly working event bus, here's a manual refresh method
	public void refreshAll() {

		jobTable.refreshJobs();
		
		// update the view if we are looking at this job currently
		if (this.jobDetailParentStackLayout.topControl == this.jobPanelDetailView) {
			Job job = jobPanelDetailView.getJob();
			// get the most up to date version of this job
			Job theJob = window.getJobManager().getJob(job.getId()).get();
			jobPanelDetailView.refreshData(theJob);
		}
	}

	
    
    private void buildMenus(){
    	Shell shell = window.getShell();
    	Menu menuBar = new Menu(shell, SWT.BAR);
    	shell.setMenuBar(menuBar);
    	
    	MenuItem fileMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
        fileMenuHeader.setText("&File");

        fileMenu = new Menu(shell, SWT.DROP_DOWN);
        fileMenuHeader.setMenu(fileMenu);

        newJobMenuItem = new MenuItem(fileMenu, SWT.CASCADE);
        newJobMenuItem.setText("&New job");
        
        MenuItem refreshMenuItem = new MenuItem(fileMenu, SWT.CASCADE);
        refreshMenuItem.setText("&Refresh");
        refreshMenuItem.addSelectionListener(new RefreshJobsListener(this));
        
        deleteJobMenuItem = new MenuItem(fileMenu, SWT.PUSH);
        deleteJobMenuItem.setText("&Delete job");
        deleteJobMenuItem.addSelectionListener(new DeleteJobListener(this));
        deleteJobMenuItem.setEnabled(false);
    	
        MenuItem fileExitMenuItem = new MenuItem(fileMenu, SWT.PUSH);
        fileExitMenuItem.setText("E&xit");
        
        fileExitMenuItem.addSelectionListener(new ExitListener(this));
        
    	Menu scriptChoices = new Menu(window.getShell(), SWT.DROP_DOWN);
        newJobMenuItem.setMenu(scriptChoices);
        
        ScriptRegistry scriptRegistry = window.getScriptRegistry();
        
        Iterable<XProcScriptService> scripts = scriptRegistry.getScripts();
        for (XProcScriptService scriptServ : scripts) {
        	MenuItem scriptChoice = new MenuItem(scriptChoices, SWT.PUSH);
        	XProcScript script = scriptServ.load();
            scriptChoice.setText(script.getName()); 
            scriptChoice.setData(script);
            scriptChoice.addSelectionListener(new NewJobListener(this));
        }
        
        menuBar.setVisible(true);
        menuBar.setEnabled(true);
        
    }
    
	// these two methods come from the event bus and cause an 'invalid thread access' error
	public void statusUpdate(StatusMessage msg) {
		Job job = jobPanelDetailView.getJob();
		Job theJob = window.getJobManager().getJob(job.getId()).get();
		jobTable.refreshJobs();
		
		// update the view if we are looking at this job currently
		if (msg.getJobId().equals(job.getId().toString())) {
			jobPanelDetailView.refreshData(theJob);
		}
	}
	
	public void messageUpdate(Message msg) {
		Job job = jobPanelDetailView.getJob();
		Job theJob = window.getJobManager().getJob(job.getId()).get();
		//jobList.update(theJob, null);
		// update the view if we are looking at this job currently
		if (msg.getJobId().equals(job.getId().toString())) {
			jobPanelDetailView.refreshData(theJob);
		}
	}

}
