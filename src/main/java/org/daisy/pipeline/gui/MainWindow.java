package org.daisy.pipeline.gui;

import java.util.Iterator;

import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.clients.ClientStorage;
import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.job.JobManagerFactory;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.XProcScriptService;
//import org.daisy.pipeline.webserviceutils.storage.WebserviceStorage;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.BundleContext;

import com.google.common.base.Supplier;
 

public class MainWindow extends ApplicationWindow {

    EntryPoint entryPoint; // DI data in here

	
	public MainWindow(Shell parentShell, EntryPoint entryPoint) {
		super(parentShell);
        this.entryPoint = entryPoint;
	}
	
	protected Control createContents(Composite parent) {
        Shell shell = getShell();
        shell.setText("JFace experiment");
        shell.setSize(300, 200);
        shell.setLayout(new FillLayout(SWT.HORIZONTAL));
        addList(shell);
        shell.pack();
	    return parent;
	}

    private void addList(Shell shell){
        final List list = new List (shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        
        ScriptRegistry scriptRegistry = this.entryPoint.getScriptRegistry();
        if (scriptRegistry != null) {
	        Iterable<XProcScriptService> scripts = scriptRegistry.getScripts();
	        Iterator<XProcScriptService> it = scripts.iterator();
	        while (it.hasNext()) {
	        	XProcScriptService script = it.next();
	        	list.add(script.getDescription());
	        }
        }
        
    }
}
