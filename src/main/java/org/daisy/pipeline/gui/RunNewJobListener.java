package org.daisy.pipeline.gui;

import org.daisy.pipeline.job.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;


public class RunNewJobListener extends SelectionAdapter {

	GuiController guiController;
	public RunNewJobListener(GuiController guiController){
		this.guiController = guiController;
	}
	
	@Override
	public void widgetSelected(SelectionEvent arg0) {
		if (!isValid(guiController.jobPanelNewJobView)) {
			MessageBox messageBox = new MessageBox(guiController.getWindow().getShell(), 
					SWT.ICON_WARNING | SWT.OK);
	        
	        messageBox.setText("Cannot run job");
	        messageBox.setMessage("Please complete all required fields.");
	        messageBox.open();
	        return;
		}
		
		
		Job job = JobExecutor.runJob(guiController.getWindow().getJobManager(), 
				guiController.jobPanelNewJobView);
		guiController.jobTable.refreshJobs();
		guiController.jobTable.select(job);
	}
	
	private boolean isValid(JobPanelNewJobView panel) {
		for (Text text : panel.requiredArguments) {
			if (text.getText().isEmpty()) {
				return false;
			}
		}
		return true;
	}

}
