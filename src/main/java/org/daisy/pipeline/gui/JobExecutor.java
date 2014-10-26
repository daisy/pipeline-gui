package org.daisy.pipeline.gui;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.daisy.common.transform.LazySaxResultProvider;
import org.daisy.common.transform.LazySaxSourceProvider;
import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcOutput;
import org.daisy.common.xproc.XProcPipelineInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.script.BoundXProcScript;
import org.daisy.pipeline.script.XProcOptionMetadata;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;


public class JobExecutor {

	private static final Logger logger = LoggerFactory.getLogger(JobExecutor.class);
	
	public static Job runJob(JobManager jobManager, JobPanelNewJobView panel) {
		
		if (panel == null) {
			logger.error("Job could not be created: new job view panel is null");
        	return null;
		}
        if (panel.script == null) {
        	logger.error("Job could not be created: script is null");
        	return null;
        }
        
        XProcInput.Builder inBuilder = new XProcInput.Builder();
        XProcOutput.Builder outBuilder = new XProcOutput.Builder();
        XProcPipelineInfo scriptInfo = panel.script.getXProcPipelineInfo();
        
        // add inputs
        Iterator<XProcPortInfo> itInput = scriptInfo.getInputPorts().iterator();
        while (itInput.hasNext()) {
            XProcPortInfo input = itInput.next();
            String inputName = input.getName();
            Iterator<Text> itWidget = panel.inputArguments.iterator();
            while (itWidget.hasNext()) {
            	Text widget = itWidget.next();
            	String name = (String)widget.getData();
            	if (name.equals(inputName)) {
            		String src = widget.getText();
            		LazySaxSourceProvider prov= new LazySaxSourceProvider(src);
                    inBuilder.withInput(inputName, prov);
            	}
            }
        }
        
        // add options - note that we're not filtering them
        Iterable<XProcOptionInfo> options = scriptInfo.getOptions();

        // TODO support sequences of options
        Iterator<XProcOptionInfo> itOption = options.iterator();
        while(itOption.hasNext()) {
        	XProcOptionInfo option = itOption.next();
        	String optionName = option.getName().toString();
        	Iterator<Widget> itWidget = panel.optionArguments.iterator();
        	while (itWidget.hasNext()) {
        		Widget widget = itWidget.next();
        		String name = (String)widget.getData();
        		if (name.equals(optionName)) {
        			XProcOptionMetadata metadata = panel.script.getOptionMetadata(new QName(optionName));
        			String value = "";
        			if (metadata.getType().equals("boolean")) {
        				// the widget is a button (checkbox)
        				Button cb = (Button)widget;
        				if (cb.getSelection()) {
        					value = "true";
        				}
        				else {
        					value = "false";
        				}
        			}
        			else {
        				// the widget is a text field
        				Text text = (Text)widget;
        				value = text.getText();
        			}
        			inBuilder.withOption(new QName(optionName), value);
        		}
        	}
        }
        
        // add outputs
        Iterable<XProcPortInfo> outputs = scriptInfo.getOutputPorts();
        Iterator<XProcPortInfo> itOutput = outputs.iterator();
        while (itOutput.hasNext()) {
        	XProcPortInfo output = itOutput.next();
        	String outputName = output.getName();
            Iterator<Text> itWidget = panel.outputArguments.iterator();
            while (itWidget.hasNext()) {
            	Text widget = itWidget.next();
            	String name = (String)widget.getData();
            	if (name.equals(outputName)) {
            		String value = widget.getText();
            		LazySaxResultProvider prov= new LazySaxResultProvider(value);
            		outBuilder.withOutput(outputName, prov);
            	}
            }
        }
        

        BoundXProcScript bound= BoundXProcScript.from(panel.script, inBuilder.build(), outBuilder.build());

        // TODO use a nice name
        // TODO support priorities
        Optional<Job> newJob = jobManager.newJob(bound).isMapping(true).withNiceName("TODO").build();
        
        // TODO what does isPresent() do?
//        if(!newJob.isPresent()){
//        	return Optional.absent();
//        }
		
        return newJob.get();
	}
	
	
}
