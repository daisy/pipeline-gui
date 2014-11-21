package org.daisy.pipeline.gui;

import java.util.ArrayList;
import java.util.Iterator;

import org.daisy.pipeline.gui.handlers.JobSelectionListener;
import org.daisy.pipeline.job.Job;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

public class JobTable extends TableViewer {

	GuiController guiController;
	TableViewerColumn statusColumn;
	TableViewerColumn jobNameColumn;
	
	public JobTable(Composite parent, GuiController guiController) {
		super(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		parent.setLayout(tableColumnLayout);

		Table table = this.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		statusColumn = new TableViewerColumn(this, SWT.NONE);
		tableColumnLayout.setColumnData(statusColumn.getColumn(), 
				new ColumnWeightData(2, ColumnWeightData.MINIMUM_WIDTH, true));
		statusColumn.getColumn().setText("Status");

		jobNameColumn = new TableViewerColumn(this, SWT.NONE);
		//Specify width using weights
		tableColumnLayout.setColumnData(jobNameColumn.getColumn(), 
				new ColumnWeightData(2, ColumnWeightData.MINIMUM_WIDTH, true));
		jobNameColumn.getColumn().setText("Job");
		
		this.guiController = guiController;
        this.setContentProvider(ArrayContentProvider.getInstance());
        this.setLabelProvider(new TableLabelProvider());
        this.addSelectionChangedListener(new JobSelectionListener(guiController));
	}
	
	public void select(Job job) {
		this.setSelection(new StructuredSelection(job));
	}
	
	public void refreshJobs(){
		Iterable<Job> jobs = guiController.getWindow().getJobManager().getJobs();
		// for some reason, just passing the Iterable collection to setInput doesn't work
		ArrayList<Job> jobslist = new ArrayList<Job>();
		for (Job job : jobs) {
			jobslist.add(job);
		}
		this.setInput(jobslist);
		this.refresh();
	}
	
	public boolean multipleSelected() {
		IStructuredSelection selection = (IStructuredSelection)this.getSelection();
		if (selection == null) {
			return false;
		}
		return selection.size() > 1;
	}
	
	public Iterable<Job> getCurrentSelection() {
		IStructuredSelection selection = (IStructuredSelection)this.getSelection();
		if (selection == null) {
			return null;
		}
		Iterator<IStructuredSelection> it = selection.iterator();
		ArrayList<Job> jobs = new ArrayList<Job>();
		while (it.hasNext()) {
			jobs.add((Job)it.next());
		}
		return jobs;
		
	}
	
	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
		public String getColumnText(Object element, int columnIndex) {
			Job job = (Job) element;
			String result = "";
			switch(columnIndex){
			case 0:
				result = job.getStatus().toString();
				break;
			case 1:
				result = job.getContext().getScript().getName();
				break;
			default:
				//should not reach here
				result = "";
			}
			return result;
		}
	}
}
