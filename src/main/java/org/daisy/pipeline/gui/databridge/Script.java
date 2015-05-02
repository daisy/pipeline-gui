package org.daisy.pipeline.gui.databridge;

import java.util.ArrayList;

import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPipelineInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.script.XProcOptionMetadata;
import org.daisy.pipeline.script.XProcPortMetadata;
import org.daisy.pipeline.script.XProcScript;

// representation of a pipeline script in a GUI-friendly way
public class Script {
	private String name;
	private String description;
	private ArrayList<ScriptField> inputFields;
	private ArrayList<ScriptField> requiredOptionFields;
	private ArrayList<ScriptField> optionalOptionFields;
	//private ArrayList<ScriptField> outputFields;
	private XProcScript xprocScript;
	
	public Script(XProcScript script) {
		inputFields = new ArrayList<ScriptField>();
		//outputFields = new ArrayList<ScriptField>();
		requiredOptionFields = new ArrayList<ScriptField>();
		optionalOptionFields = new ArrayList<ScriptField>();
		xprocScript = script;
		
		name = script.getName();
		description = script.getDescription();
		
		XProcPipelineInfo scriptInfo = script.getXProcPipelineInfo();
		for (XProcPortInfo portInfo : scriptInfo.getInputPorts()) {
			XProcPortMetadata metadata = script.getPortMetadata(portInfo.getName());
			ScriptField field = new ScriptField(portInfo, metadata, ScriptField.FieldType.INPUT);
			inputFields.add(field);
		}
		
//		for (XProcPortInfo portInfo : scriptInfo.getOutputPorts()) {
//			XProcPortMetadata metadata = script.getPortMetadata(portInfo.getName());
//			ScriptField field = new ScriptField(portInfo, metadata, ScriptField.FieldType.OUTPUT);
//			outputFields.add(field);
//		}
		
		for (XProcOptionInfo optionInfo : scriptInfo.getOptions()) {
			XProcOptionMetadata metadata = script.getOptionMetadata(optionInfo.getName());
			ScriptField field = new ScriptField(optionInfo, metadata);
			if (field.isRequired()) {
				requiredOptionFields.add(field);
			}
			else {
				optionalOptionFields.add(field);
			}
				
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
	public Iterable<ScriptField> getRequiredOptionFields() {
		return requiredOptionFields;
	}
	public Iterable<ScriptField> getOptionalOptionFields() {
		return optionalOptionFields;
	}
//	public Iterable<ScriptField> getOutputFields() {
//		return outputFields;
//	}
	
	public XProcScript getXProcScript() {
		return xprocScript;
	}
	
}
