package org.daisy.pipeline.gui;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class ExitListener extends SelectionAdapter {
	GuiController guiController;
	public ExitListener(GuiController guiController){
		this.guiController = guiController;
	}
	public void widgetSelected(SelectionEvent event) {
		guiController.getWindow().getShell().close();
	}
}
