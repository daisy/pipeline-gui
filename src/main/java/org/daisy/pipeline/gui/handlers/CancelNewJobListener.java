package org.daisy.pipeline.gui.handlers;

import org.daisy.pipeline.gui.GuiController;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class CancelNewJobListener extends SelectionAdapter {

	GuiController guiController;
	
	public CancelNewJobListener(GuiController guiController) {
		this.guiController = guiController;
	}
	
	@Override
	public void widgetSelected(SelectionEvent arg0) {
		if (guiController.getJobPanelNewJobView() != null) {
			guiController.getJobPanelNewJobView().dispose();
			guiController.setJobPanelNewJobView(null);
		}
		guiController.showEmptyView();

	}

}
