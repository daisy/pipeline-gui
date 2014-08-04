package org.daisy.pipeline.gui;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.daisy.pipeline.gui.MainWindow;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * OSGI activator for the GUI
 */
public class Activator implements BundleActivator, Runnable {

	public void run() {
		/*MainWindow window = new MainWindow(null);
        window.setBlockOnOpen(true);
        window.open();
        Display.getCurrent().dispose();
        */
		System.out.println("Hello from the GUI");
		try {
			Display display = new Display ();
			Shell shell = new Shell(display);
			shell.open ();
			while (!shell.isDisposed ()) {
				if (!display.readAndDispatch ()) display.sleep ();
			}
			display.dispose ();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start(BundleContext context) throws Exception {
		context.registerService(Runnable.class.getName(), this, null);
	}

	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
