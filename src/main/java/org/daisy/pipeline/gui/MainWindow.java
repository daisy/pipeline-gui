package org.daisy.pipeline.gui;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class MainWindow extends ApplicationWindow {

	public MainWindow(Shell parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}
	
	protected Control createContents(Composite parent) {
		getShell().setText("JFace");
	    setStatus( "IN YOUR FACE!" );
	    return parent;
	}
	

	
}
