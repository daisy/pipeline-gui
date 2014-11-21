package org.daisy.pipeline.gui;

import java.util.ArrayList;

import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPipelineInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.gui.handlers.CancelNewJobListener;
import org.daisy.pipeline.gui.handlers.RunNewJobListener;
import org.daisy.pipeline.gui.utils.GridDataUtil;
import org.daisy.pipeline.gui.utils.GridLayoutUtil;
import org.daisy.pipeline.script.XProcOptionMetadata;
import org.daisy.pipeline.script.XProcPortMetadata;
import org.daisy.pipeline.script.XProcScript;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;


public class JobPanelNewJobView extends Composite{

	XProcScript script;
	ArrayList<Text> inputArguments;
	ArrayList<Widget> optionArguments;
	ArrayList<Text> outputArguments;
	GuiController guiController;
	
	private ArrayList<Text> requiredArguments; // the required arguments; used later in validation
	
	// standard vertical indents
	static int VINDENT = 8; 
	static int VINDENT_GROUP = 1; // used to keep descriptions close to what they're describing
	
	static int TEXTBOX_MIN_WIDTH = 300;
		
	public JobPanelNewJobView(Composite parent, XProcScript script, GuiController guiController) {
		
		super(parent, SWT.NONE);
		this.script = script;
		this.guiController = guiController;
		inputArguments = new ArrayList<Text>();
		optionArguments = new ArrayList<Widget>();
		outputArguments = new ArrayList<Text>();
		setRequiredArguments(new ArrayList<Text>());
		
		GridLayoutUtil.applyGridLayout(this).numColumns(3);
		
		Label header = new Label(this, 0);
		header.setText("New Job: " + script.getName());
		header.setFont(makeBoldFont(header));
		GridDataUtil.applyGridData(header).horizontalSpan(3).verticalIndent(VINDENT);
		
		Label desc = new Label(this, 0);
		desc.setText(script.getDescription());
		desc.setFont(makeItalicFont(desc));
		GridDataUtil.applyGridData(desc).horizontalSpan(3).verticalIndent(VINDENT_GROUP);

		XProcPipelineInfo scriptInfo = script.getXProcPipelineInfo();
		Iterable<XProcPortInfo> inputPorts = scriptInfo.getInputPorts();
		Iterable<XProcPortInfo> outputPorts = scriptInfo.getOutputPorts();
		Iterable<XProcOptionInfo> options = scriptInfo.getOptions();
		
		for (XProcPortInfo input : inputPorts) {
			XProcPortMetadata meta = script.getPortMetadata(input.getName());
			Text fileName = addFilePicker(input.getName(), meta.getNiceName(), meta.getDescription(), false, 
					meta.isRequired());
			inputArguments.add(fileName);
			if (meta.isRequired()) {
				getRequiredArguments().add(fileName);
			}
		}
		
		for (XProcOptionInfo option: options) {
			XProcOptionMetadata meta = script.getOptionMetadata(option.getName());
			String type = meta.getType();
			
			if (type.equals("anyURI")) {
				Text fileName = addFilePicker(option.getName().toString(), 
						meta.getNiceName(), meta.getDescription(), false, option.isRequired());
				optionArguments.add(fileName);
				if (option.isRequired()) {
					getRequiredArguments().add(fileName);
				}
			}
			else if (type.equals("anyDirURI")) {
				Text fileName = addFilePicker(option.getName().toString(), 
						meta.getNiceName(), meta.getDescription(), true, option.isRequired());
				optionArguments.add(fileName);
				if (option.isRequired()) {
					getRequiredArguments().add(fileName);
				}
			}
			else if (type.equals("boolean")) {
				Button cb = addCheckbox(option.getName().toString(), meta.getNiceName(), meta.getDescription(),
						option.isRequired());
				optionArguments.add(cb);
				// don't need to add to required args list here - a checkbox has a value no matter what
			}
			else { // treat all other types as strings
				Text text = addText(option.getName().toString(), meta.getNiceName(), meta.getDescription(),
						option.isRequired());
				optionArguments.add(text);
				if (option.isRequired()) {
					getRequiredArguments().add(text);
				}
			}
		}
		
		for (XProcPortInfo output : outputPorts) {
			XProcPortMetadata meta = script.getPortMetadata(output.getName());
			Text fileName = addFilePicker(output.getName(), meta.getNiceName(), meta.getDescription(), false,
					meta.isRequired());
			outputArguments.add(fileName);
			if (meta.isRequired()) {
				getRequiredArguments().add(fileName);
			}
		}
	
		Button cancelButton = new Button(this, 0);
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new CancelNewJobListener(guiController));
		GridDataUtil.applyGridData(cancelButton).horizontalSpan(1).verticalIndent(VINDENT);
		
		Button runButton = new Button(this, 0);
		runButton.setText("Run");
		runButton.addSelectionListener(new RunNewJobListener(guiController));
		GridDataUtil.applyGridData(runButton).horizontalSpan(1).verticalIndent(VINDENT);
		
	}
	
	private Button addCheckbox(String name, String nicename, String description, boolean isRequired) {
		Button cb = new Button(this, SWT.CHECK);
		if (isRequired) {
			cb.setText("* " + nicename);
		}
		else {
			cb.setText(nicename);
		}
		cb.setData(name);
		cb.setToolTipText(description);
		GridDataUtil.applyGridData(cb).horizontalSpan(3).verticalIndent(VINDENT);
		
		Label descLabel = new Label(this, 0);
		descLabel.setText(description);
		descLabel.setFont(makeItalicFont(descLabel));
		GridDataUtil.applyGridData(descLabel).horizontalSpan(3).verticalIndent(VINDENT_GROUP);
		
		
		return cb;
	}
	
	private Text addText(String name, String nicename, String description, boolean isRequired) {
		Label label = new Label(this, 0);
		if (isRequired) {
			label.setText("* " + nicename);
		}
		else {
			label.setText(nicename);
		}
		GridDataUtil.applyGridData(label).horizontalSpan(1).verticalIndent(VINDENT);
		
		Text text = new Text(this, 0);
		GridDataUtil.applyGridData(text).horizontalSpan(2).
			grabExcessHorizontalSpace(true).verticalIndent(VINDENT).minimumWidth(TEXTBOX_MIN_WIDTH);
		text.setData(name);
		text.setToolTipText(description);
		
		Label descLabel = new Label(this, 0);
		descLabel.setText(description);
		descLabel.setFont(makeItalicFont(descLabel));
		GridDataUtil.applyGridData(descLabel).horizontalSpan(3).verticalIndent(VINDENT_GROUP);
		
		return text;
	}

	
	private Text addFilePicker(String name, String nicename, String description, boolean isDir, boolean isRequired) {
		Label label = new Label(this, 0);
		if (isRequired) {
			label.setText("* " + nicename);
		}
		else {
			label.setText(nicename);
		}
		GridDataUtil.applyGridData(label).horizontalSpan(1).verticalIndent(5);
		
		Text fileName = new Text(this, 0);
		fileName.setData(name);
		fileName.setToolTipText(description);
		GridDataUtil.applyGridData(fileName).horizontalSpan(1).
			grabExcessHorizontalSpace(true).verticalIndent(VINDENT).minimumWidth(TEXTBOX_MIN_WIDTH);
		
		Button filePicker = new Button(this, 0);
		filePicker.setText("...");
		filePicker.setData(fileName);
		GridDataUtil.applyGridData(filePicker).horizontalSpan(1).horizontalAlignment(SWT.LEFT).verticalIndent(VINDENT);
		
		// show a directory dialog
		if (isDir) {
			filePicker.addSelectionListener(new SelectionAdapter() {
			      public void widgetSelected(SelectionEvent e) {
			    	  DirectoryDialog dialog = new DirectoryDialog(guiController.getWindow().getShell(), SWT.NULL);
			    	  String selectedFile = dialog.open();
			          if (selectedFile != null) {
			        	  ((Text)e.widget.getData()).setText(selectedFile);
			          }
			      }
			});
			
		}
		// show a file dialog
		else {
			filePicker.addSelectionListener(new SelectionAdapter() {
			      public void widgetSelected(SelectionEvent e) {
			    	  FileDialog dialog = new FileDialog(guiController.getWindow().getShell(), SWT.NULL);
			    	  String selectedFile = dialog.open();
			          if (selectedFile != null) {
			        	  ((Text)e.widget.getData()).setText(selectedFile);
			          }
			      }
			});
			
		}
		
		Label descLabel = new Label(this, 0);
		descLabel.setText(description);
		descLabel.setFont(makeItalicFont(descLabel));
		GridDataUtil.applyGridData(descLabel).horizontalSpan(3).verticalIndent(VINDENT_GROUP);
		
		return fileName;
	}
	
	
	private Font makeBoldFont(Label label) {
		FontDescriptor boldDescriptor = FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD);
		Font boldFont = boldDescriptor.createFont(label.getDisplay());
		return boldFont;
	}
	
	private Font makeItalicFont(Label label) {
		FontDescriptor italicDescriptor = FontDescriptor.createFrom(label.getFont()).setStyle(SWT.ITALIC);
		Font italicFont = italicDescriptor.createFont(label.getDisplay());
		return italicFont;
	}

	public ArrayList<Text> getRequiredArguments() {
		return requiredArguments;
	}

	public void setRequiredArguments(ArrayList<Text> requiredArguments) {
		this.requiredArguments = requiredArguments;
	}
	
	
}
