package org.daisy.pipeline.gui;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class RefreshJobsListener extends SelectionAdapter {
	GuiController guiController;
	public RefreshJobsListener(GuiController guiController){
		this.guiController = guiController;
	}
	public void widgetSelected(SelectionEvent event) {
		guiController.refreshAll();
	}
}
