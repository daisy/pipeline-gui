package org.daisy.pipeline.gui;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.daisy.common.messaging.Message;
import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPipelineInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.gui.utils.GridDataUtil;
import org.daisy.pipeline.gui.utils.GridLayoutUtil;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobResult;
import org.daisy.pipeline.job.JobResultSet;
import org.daisy.pipeline.script.XProcOptionMetadata;
import org.daisy.pipeline.script.XProcPortMetadata;
import org.daisy.pipeline.script.XProcScript;
import org.daisy.pipeline.webserviceutils.xml.XmlUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Element;

public class JobPanelDetailView extends Composite{

		Label jobIdText;
    	Label scriptNameText;
    	Label jobStatusText;
    	List jobMessagesList;
    	GuiController guiController;
    	Composite jobResultsComposite;
    	private Job job = null;
    	
		public JobPanelDetailView(Composite parent, GuiController guiController) {
			super(parent, SWT.NONE);
			
			this.guiController = guiController;
			GridLayoutUtil.applyGridLayout(this).numColumns(2);
			
			Label jobIdLabel = new Label(this, SWT.NONE);
			jobIdLabel.setText("Job ID:");
			GridDataUtil.applyGridData(jobIdLabel).horizontalSpan(1).verticalIndent(5);
			
			jobIdText = new Label(this, SWT.NONE);
			GridDataUtil.applyGridData(jobIdText).horizontalSpan(1).verticalIndent(5);
			
			Label scriptNameLabel = new Label(this, SWT.NONE);
			scriptNameLabel.setText("Script:");
			GridDataUtil.applyGridData(scriptNameLabel).horizontalSpan(1).verticalIndent(5);
			
			scriptNameText = new Label(this, SWT.NONE);
			GridDataUtil.applyGridData(scriptNameText).horizontalSpan(1).verticalIndent(5);

			Label jobStatusLabel = new Label(this, SWT.NONE);
			jobStatusLabel.setText("Status:");
			GridDataUtil.applyGridData(jobStatusLabel).horizontalSpan(1).verticalIndent(5);
			
			jobStatusText = new Label(this, SWT.NONE);
			GridDataUtil.applyGridData(jobStatusText).horizontalSpan(1).verticalIndent(5);
			
			jobResultsComposite = new Composite(this, SWT.NONE);
			jobResultsComposite.setLayout(new FillLayout(SWT.VERTICAL));
			GridDataUtil.applyGridData(jobResultsComposite).horizontalSpan(2).verticalIndent(5);
			
			Label jobMessagesLabel = new Label(this, SWT.NONE);
			jobMessagesLabel.setText("Messages:");
			GridDataUtil.applyGridData(jobMessagesLabel).horizontalSpan(2).verticalIndent(5);
			
			jobMessagesList = new List(this, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL);
			jobMessagesList.setSize(500, 300);
			GridDataUtil.applyGridData(jobMessagesList).horizontalSpan(2).verticalIndent(2);
			
		}

		public void refreshData(Job job) {
			this.job = job;
			jobIdText.setText(job.getId().toString());
			jobStatusText.setText(job.getStatus().toString());
			scriptNameText.setText(job.getContext().getScript().getName());
			
			jobMessagesList.removeAll();
			
			Iterable<Message> messages = job.getContext().getMonitor().getMessageAccessor().getAll();
			
			if (messages != null) {
				for (Message message : messages) {
					jobMessagesList.add(message.getText());
				}		
			}
			
			// remove all the labels in the jobResultsComposite
			Control[] controls = jobResultsComposite.getChildren();
			for (Control c : controls) {
				c.dispose();
			}
			
			JobResultSet results = job.getContext().getResults();
			if (results != null && job.getStatus() == Job.Status.DONE) {
			
				for (String port : this.job.getContext().getResults().getPorts()) {
	                String nicename = job.getContext().getScript().getPortMetadata(port).getNiceName();
	                addResultLabel(nicename, this.job.getContext().getResults().getResults(port));
				}
	
				for(QName option: this.job.getContext().getResults().getOptions()){
	                String nicename = job.getContext().getScript().getOptionMetadata(option).getNiceName();
	                addResultLabel(nicename, this.job.getContext().getResults().getResults(option));
				}
	
			}
			else {
				Label resultsLabel = new Label(jobResultsComposite, SWT.NONE);
				resultsLabel.setText("Not available");
			}
			
			this.getParent().pack();
			
		}
		
		private void addResultLabel(String nicename, Collection<JobResult> results) {
			String resultsStr = "";
            for(JobResult result: results){
            	String resultStr = result.getPath().toString();
            	resultStr += " (";
                if (result.getMediaType()!= null && !result.getMediaType().isEmpty()){
                    resultStr += result.getMediaType() + ", ";
                }
                resultStr += result.getSize() + " bytes)";
                resultsStr += resultStr + "\n";
            }
            
            Label resultLabel = new Label(jobResultsComposite, SWT.NONE);
            resultLabel.setText(nicename + ":\n" + resultsStr);
			
		}

		public Job getJob() {
			return job;
		}	
		
		

}