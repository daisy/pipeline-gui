package org.daisy.pipeline.gui.handlers;

import org.daisy.pipeline.gui.GuiController;
import org.daisy.pipeline.gui.utils.PlatformUtils;
import org.daisy.pipeline.job.Job;
import org.eclipse.swt.SWT;

public class DeleteJobAction extends GuiActionAbstract {

	private static String label = "&Delete";
	private static String description = "Delete the selected job(s)";
	private static int accelerator_mac = SWT.COMMAND + 'D';
	private static int accelerator_other = SWT.CTRL + 'D';
			
	public DeleteJobAction(GuiController guiController) {
		super(guiController);
		int accelerator = PlatformUtils.isMac() ? accelerator_mac : accelerator_other;
		setInfo(label, description, accelerator);
	}

	@Override 
    public void run() { 
		Iterable<Job> jobs = guiController.getJobTable().getCurrentSelection();
		
		for (Job job : jobs) {
			guiController.getWindow().getJobManager().deleteJob(job.getId());
			guiController.getJobTable().refreshJobs();
		}		
	}
}
