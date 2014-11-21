package org.daisy.pipeline.gui.handlers;

import org.daisy.pipeline.gui.GuiController;
import org.daisy.pipeline.job.Job;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;


public class JobSelectionListener implements ISelectionChangedListener {

	GuiController guiController;
	
	public JobSelectionListener(GuiController guiController) {
		this.guiController = guiController;
	}
	//@Override
	public void selectionChanged(SelectionChangedEvent event) {
		// update the GUI with job info
		IStructuredSelection selection = (IStructuredSelection)event.getSelection();
		if (selection.isEmpty()) {
			guiController.showEmptyView();
		}
		else {
			if (selection.size() == 1) {
				Job job = (Job)selection.getFirstElement();
				guiController.showJobDetailView(job);
			}
			else {
				guiController.showMultipleSelectedView();
			}
		}
		
	}

}
