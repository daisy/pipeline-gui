package org.daisy.pipeline.gui;

import org.daisy.pipeline.script.XProcScript;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class NewJobListener extends SelectionAdapter {

	GuiController guiController;
	public NewJobListener(GuiController guiController){
		this.guiController = guiController;
	}
	public void widgetSelected(SelectionEvent event) {
		XProcScript script = (XProcScript)event.widget.getData();
		guiController.showNewJobPanel(script);
	}
}
