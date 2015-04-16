package org.daisy.pipeline.gui.databridge;


import java.util.Iterator;

import javafx.scene.control.TextField;

import org.daisy.common.transform.LazySaxSourceProvider;
import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOutput;
import org.daisy.common.xproc.XProcPipelineInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.gui.MainWindow;
import org.daisy.pipeline.gui.NewJobPane;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.script.BoundXProcScript;
import org.daisy.pipeline.script.XProcScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

// TODO adapt to NewJobPane instead of old swt code
public class JobExecutor {

	private static final Logger logger = LoggerFactory.getLogger(JobExecutor.class);
	
	public static Job runJob(MainWindow main, XProcScript script) {
		
		NewJobPane newJobPane = main.getNewJobPane();
		if (newJobPane == null) {
			logger.error("Job could not be created: new job view panel is null");
        	return null;
		}
        if (script == null) {
        	logger.error("Job could not be created: script is null");
        	return null;
        }
        
        XProcInput.Builder inBuilder = new XProcInput.Builder();
        XProcOutput.Builder outBuilder = new XProcOutput.Builder();
        XProcPipelineInfo scriptInfo = script.getXProcPipelineInfo();
        
        // add inputs
        Iterator<XProcPortInfo> itInput = scriptInfo.getInputPorts().iterator();
        while (itInput.hasNext()) {
            XProcPortInfo input = itInput.next();
            String inputName = input.getName();
            TextField textField = newJobPane.inputArguments.get(input.getName());
            String src = textField.getText();
            LazySaxSourceProvider prov = new LazySaxSourceProvider(src);
            inBuilder.withInput(inputName, prov);
        }
        
        BoundXProcScript bound= BoundXProcScript.from(script, inBuilder.build(), outBuilder.build());

        Optional<Job> newJob = main.getJobManager().newJob(bound).isMapping(true).withNiceName("TODO").build();
        
        // TODO what does isPresent() do?
//        if(!newJob.isPresent()){
//        	return Optional.absent();
//        }
		
        return newJob.get();
    }
	
	
}
