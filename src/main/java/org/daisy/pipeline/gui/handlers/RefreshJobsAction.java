package org.daisy.pipeline.gui.handlers;

import org.daisy.pipeline.gui.GuiController;
import org.daisy.pipeline.gui.utils.PlatformUtils;
import org.eclipse.swt.SWT;

public class RefreshJobsAction extends GuiActionAbstract {

	private static String label = "&Refresh";
	private static String description = "Refresh the job data";
	private static int accelerator_mac = SWT.COMMAND + 'R';
	private static int accelerator_other = SWT.CTRL + 'R';
	
	public RefreshJobsAction(GuiController guiController) {
		super(guiController);
		int accelerator = PlatformUtils.isMac() ? accelerator_mac : accelerator_other;
		setInfo(label, description, accelerator);
	}

	@Override 
    public void run() { 
		guiController.refreshAll();
	}
}
