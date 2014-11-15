package org.daisy.pipeline.gui;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.osgi.framework.BundleException;


public class ExitListener extends SelectionAdapter {
	GuiController guiController;
	public ExitListener(GuiController guiController){
		this.guiController = guiController;
	}
	public void widgetSelected(SelectionEvent event) {
		try {
			guiController.getWindow().exit();
		} catch (BundleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
