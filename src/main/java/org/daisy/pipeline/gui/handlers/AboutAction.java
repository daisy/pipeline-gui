package org.daisy.pipeline.gui.handlers;

import org.daisy.pipeline.gui.GuiController;
import org.daisy.pipeline.gui.utils.PlatformUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

public class AboutAction extends GuiActionAbstract {

	private static String version = "1.0 Alpha";
	private static String date = "December 2014";
	private static String label = "&About";
	private static String description = "About the DAISY Pipeline 2";
	private static int accelerator = SWT.CTRL + 'A';
	
	
	
	public AboutAction(GuiController guiController) {
		super(guiController);
		if (!PlatformUtils.isMac()) {
			setInfo(label, description, accelerator);
		}
	}

	@Override 
    public void run() { 
		MessageBox dialog = new MessageBox(guiController.getWindow().getShell(),
				SWT.ICON_INFORMATION | SWT.OK);
		dialog.setText("DAISY Pipeline 2");
		dialog.setMessage("DAISY Pipeline 2 GUI\nVersion: " + version + "\nDate: " + date);
		dialog.open(); 
	}
	
}
