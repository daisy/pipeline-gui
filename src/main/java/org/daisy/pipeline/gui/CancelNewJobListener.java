package org.daisy.pipeline.gui;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class CancelNewJobListener extends SelectionAdapter {

	GuiController guiController;
	
	public CancelNewJobListener(GuiController guiController) {
		this.guiController = guiController;
	}
	
	@Override
	public void widgetSelected(SelectionEvent arg0) {
		if (guiController.jobPanelNewJobView != null) {
			guiController.jobPanelNewJobView.dispose();
			guiController.jobPanelNewJobView = null;
		}
		guiController.showEmptyView();

	}

}
