package org.daisy.pipeline.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


public class JobPanelEmptyView extends Composite{

	private Label label;
	static String MULTIPLE_SELECTED = "Multiple jobs selected";
	static String SELECT_A_JOB = "Select a job from the list";
	
	public JobPanelEmptyView(Composite parent) {
		super(parent, SWT.NONE);
		this.setLayout(new FillLayout());
		label = new Label(this, SWT.NONE);
		label.setText(SELECT_A_JOB);
	}
	
	public void setLabel(String text) {
		label.setText(text);
		this.layout();
	}
}
