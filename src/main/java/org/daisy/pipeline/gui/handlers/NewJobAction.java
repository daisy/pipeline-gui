package org.daisy.pipeline.gui.handlers;

import org.daisy.pipeline.gui.GuiController;
import org.daisy.pipeline.script.XProcScript;
import org.eclipse.swt.SWT;

public class NewJobAction extends GuiActionAbstract {

	private XProcScript script;
	public NewJobAction(GuiController guiController, XProcScript script) {
		super(guiController);
		this.script = script;
		String label = script.getName();
		String description = script.getDescription();
		// although we could use a numbering scheme for the accelerators, like
		// CMD + 0, CMD + 1, etc; we are purposely using no accelerator for two reasons:
		// 1. the scripts don't always show up in the same order, so it might get confusing for the user
		// 2. there could easily be more than 10 scripts
		int accelerator = SWT.NONE;  
		setInfo(label, description, accelerator);
	}

	@Override 
    public void run() { 
		guiController.showNewJobView(script);
	}
	
}
