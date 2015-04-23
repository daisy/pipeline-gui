package org.daisy.pipeline.gui.databridge;

import java.util.ArrayList;
import java.util.HashMap;

import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPipelineInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.script.XProcOptionMetadata;
import org.daisy.pipeline.script.XProcPortMetadata;
import org.daisy.pipeline.script.XProcScript;
import org.daisy.pipeline.script.XProcOptionMetadata.Output;

// representation of a pipeline script in a GUI-friendly way
public class Script {
	private String name;
	private String description;
	// included in this list: input ports and options that act as inputs (@px:type=anyDirURI)
	private ArrayList<ScriptField> inputFields;
	private ArrayList<ScriptField> optionFields;
	// included in this list: output ports and options that act as outputs (@px:output=result)
	private ArrayList<ScriptField> outputFields;
	private XProcScript xprocScript;
	
	public Script(XProcScript script) {
		inputFields = new ArrayList<ScriptField>();
		outputFields = new ArrayList<ScriptField>();
		optionFields = new ArrayList<ScriptField>();
		xprocScript = script;
		
		name = script.getName();
		description = script.getDescription();
		
		XProcPipelineInfo scriptInfo = script.getXProcPipelineInfo();
		for (XProcPortInfo portInfo : scriptInfo.getInputPorts()) {
			XProcPortMetadata metadata = script.getPortMetadata(portInfo.getName());
			ScriptField field = new ScriptField(portInfo, metadata, ScriptField.FieldType.INPUT);
			inputFields.add(field);
		}
		
		for (XProcPortInfo portInfo : scriptInfo.getOutputPorts()) {
			XProcPortMetadata metadata = script.getPortMetadata(portInfo.getName());
			ScriptField field = new ScriptField(portInfo, metadata, ScriptField.FieldType.OUTPUT);
			outputFields.add(field);
		}
		
		for (XProcOptionInfo optionInfo : scriptInfo.getOptions()) {
			XProcOptionMetadata metadata = script.getOptionMetadata(optionInfo.getName());
			ScriptField field = new ScriptField(optionInfo, metadata);

			// keep it simple for now; more complex approach commented out below
			optionFields.add(field);
			
			// IF an option is a result type, then call it an output
//			if (metadata.getOutput() == Output.RESULT ) {
//				outputFields.add(field);
//			}
//			else {
//				// IF an option field takes anyUri, then call it an input
//				if (field.dataType == ScriptField.dataTypeMap.get(ScriptField.DataType.FILE)) {
//					inputFields.add(field);
//				}
//				else {
//					optionFields.add(field);
//				}
//			}
		}
	}
	
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public Iterable<ScriptField> getInputFields() {
		return inputFields;
	}
	public Iterable<ScriptField> getOptionFields() {
		return optionFields;
	}
	public Iterable<ScriptField> getOutputFields() {
		return outputFields;
	}
	
	public XProcScript getXProcScript() {
		return xprocScript;
	}
	
}
