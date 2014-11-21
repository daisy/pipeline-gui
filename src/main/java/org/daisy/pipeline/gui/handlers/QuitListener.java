package org.daisy.pipeline.gui.handlers;

import org.daisy.pipeline.gui.GuiController;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.osgi.framework.BundleException;

// CocoaUIEnhancer hooks into a Listener, not an Action; whereas Windows/Linux will hook into the ExitAction
public class QuitListener implements Listener {
	GuiController guiController;
	public QuitListener(GuiController guiController){
		this.guiController = guiController;
	}
	
	public void handleEvent(Event arg0) {
		try {
			guiController.getWindow().exit();
		} 
		catch (BundleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}