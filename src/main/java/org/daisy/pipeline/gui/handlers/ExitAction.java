package org.daisy.pipeline.gui.handlers;

import org.daisy.pipeline.gui.GuiController;
import org.daisy.pipeline.gui.utils.PlatformUtils;
import org.eclipse.swt.SWT;
import org.osgi.framework.BundleException;

// this does not get used on OSX
public class ExitAction extends GuiActionAbstract{
	
	private static String label = "E&xit";
	private static String description = "Exit the application";
	private static int accelerator = SWT.CTRL + 'X';
	
    public ExitAction(GuiController guiController) {
    	super(guiController);
    	setInfo(label, description, accelerator);
	}

	@Override 
    public void run() { 
		try {
			guiController.getWindow().exit();
		} catch (BundleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }      
}
