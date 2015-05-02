package org.daisy.pipeline.gui.databridge;


import java.util.Iterator;

import javax.xml.namespace.QName;

import org.daisy.common.transform.LazySaxResultProvider;
import org.daisy.common.transform.LazySaxSourceProvider;
import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOptionInfo;
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

public class JobExecutor {

	private static final Logger logger = LoggerFactory.getLogger(JobExecutor.class);
	
	public static Job runJob(MainWindow main, BoundScript boundScript) {
		
		NewJobPane newJobPane = main.getNewJobPane();
		if (newJobPane == null) {
			logger.error("Job could not be created: new job view panel is null");
        	return null;
		}
        if (boundScript == null) {
        	logger.error("Job could not be created: script is null");
        	return null;
        }
        
        XProcScript script = boundScript.getScript().getXProcScript();
        XProcInput.Builder inBuilder = new XProcInput.Builder();
        XProcOutput.Builder outBuilder = new XProcOutput.Builder();
        XProcPipelineInfo scriptInfo = script.getXProcPipelineInfo();
        
        // add inputs
        Iterator<XProcPortInfo> itInput = scriptInfo.getInputPorts().iterator();
        while (itInput.hasNext()) {
            XProcPortInfo input = itInput.next();
            String inputName = input.getName();
            String src = (String)boundScript.getInputByName(inputName).getAnswer();
            LazySaxSourceProvider prov = new LazySaxSourceProvider(src);
            inBuilder.withInput(inputName, prov);
        }
        
        //add options
        Iterator<XProcOptionInfo> itOption = scriptInfo.getOptions().iterator();
        while(itOption.hasNext()) {
        	XProcOptionInfo option = itOption.next();
        	String optionName = option.getName().toString();
        	ScriptFieldAnswer optionAnswer = boundScript.getOptionByName(optionName);
        	String value = optionAnswer.getAnswer();
        	inBuilder.withOption(new QName(optionName), value);
        }
        
        // add outputs
//        Iterable<XProcPortInfo> outputs = scriptInfo.getOutputPorts();
//        Iterator<XProcPortInfo> itOutput = outputs.iterator();
//        while (itOutput.hasNext()) {
//        	XProcPortInfo output = itOutput.next();
//        	String outputName = output.getName();
//        	ScriptFieldAnswer answer = boundScript.getOutputByName(outputName);
//            String src = answer.getAnswer();
//            LazySaxResultProvider prov= new LazySaxResultProvider(src);
//            outBuilder.withOutput(outputName, prov);
//        }
        BoundXProcScript bound = BoundXProcScript.from(script, inBuilder.build(), outBuilder.build());

        Optional<Job> newJob = main.getJobManager().newJob(bound).isMapping(true).withNiceName("TODO").build();
        
        // TODO what does isPresent() do?
//        if(!newJob.isPresent()){
//        	return Optional.absent();
//        }
		
        return newJob.get();
    }
	
	
}
