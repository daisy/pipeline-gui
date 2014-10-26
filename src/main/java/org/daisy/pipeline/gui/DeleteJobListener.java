package org.daisy.pipeline.gui;

import org.daisy.pipeline.job.Job;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class DeleteJobListener extends SelectionAdapter{

	GuiController guiController;
	public DeleteJobListener(GuiController guiController){
		this.guiController = guiController;
	}
	public void widgetSelected(SelectionEvent event) {
		Iterable<Job> jobs = guiController.jobTable.getCurrentSelection();
		
		for (Job job : jobs) {
			guiController.getWindow().getJobManager().deleteJob(job.getId());
			guiController.jobTable.refreshJobs();
		}		
	}
}
