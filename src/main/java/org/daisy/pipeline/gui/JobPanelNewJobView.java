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
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;


public class JobPanelNewJobView extends Composite{

	XProcScript script;
	ArrayList<Widget> inputArguments;
	ArrayList<Widget> optionArguments;
	ArrayList<Text> outputArguments;
	GuiController guiController;
	
	private ArrayList<Widget> requiredArguments; // the required arguments; used later in validation
	
	// standard vertical indents
	static int VINDENT = 8; 
	static int VINDENT_GROUP = 1; // used to keep descriptions close to what they're describing
	
	static int TEXTBOX_MIN_WIDTH = 300;
		
	public JobPanelNewJobView(Composite parent, XProcScript script, GuiController guiController) {
		
		super(parent, SWT.NONE);
		this.script = script;
		this.guiController = guiController;
		inputArguments = new ArrayList<Widget>();
		optionArguments = new ArrayList<Widget>();
		outputArguments = new ArrayList<Text>();
		requiredArguments = new ArrayList<Widget>();
		
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
			Widget fileName = null;
			if (input.isSequence()) {
				 fileName = addFileSequence(input.getName(), meta.getNiceName(), meta.getDescription(), 
						 false, meta.isRequired());
			}
			else {
				fileName = addFilePicker(input.getName(), meta.getNiceName(), meta.getDescription(), false, meta.isRequired());
			}
			inputArguments.add(fileName);
			if (meta.isRequired()) {
				getRequiredArguments().add(fileName);
			}
		}
		
		for (XProcOptionInfo option: options) {
			XProcOptionMetadata meta = script.getOptionMetadata(option.getName());
			String type = meta.getType();
			
			if (type.equals("anyURI") || type.equals("anyFileURI")) {
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
						option.isRequired(), meta.isSequence(), meta.getSeparator());
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
	
	private Text addText(String name, String nicename, String description, boolean isRequired, 
			boolean isSequence, String separator) {
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
		
		if (isSequence) {
			Label sequenceLabel = new Label(this, 0);
			sequenceLabel.setText("Separate multiple values with \"" + separator + "\"");
			sequenceLabel.setFont(makeItalicFont(sequenceLabel));
			GridDataUtil.applyGridData(sequenceLabel).horizontalSpan(3).verticalIndent(VINDENT_GROUP);
		}
		
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
		final boolean _isDir = isDir;
		GridDataUtil.applyGridData(label).horizontalSpan(1).verticalIndent(5);
		
		final Text fileName = new Text(this, 0);
		fileName.setData(name);
		fileName.setToolTipText(description);
		GridDataUtil.applyGridData(fileName).horizontalSpan(1).
			grabExcessHorizontalSpace(true).verticalIndent(VINDENT).minimumWidth(TEXTBOX_MIN_WIDTH);
		
		Button filePicker = new Button(this, 0);
		filePicker.setText("...");
		GridDataUtil.applyGridData(filePicker).horizontalSpan(1).horizontalAlignment(SWT.LEFT).verticalIndent(VINDENT);
		
		filePicker.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent e) {
		    	  if (_isDir) {
		    		  // show a directory dialog
			    	  DirectoryDialog dialog = new DirectoryDialog(guiController.getWindow().getShell(), SWT.NULL);
			    	  String selectedFile = dialog.open();
			          if (selectedFile != null) {
			        	  fileName.setText(selectedFile);
			          }
		    	  }
		    	  // show a file dialog
		    	  else {
		    		  FileDialog dialog = new FileDialog(guiController.getWindow().getShell(), SWT.NULL);
			    	  String selectedFile = dialog.open();
			          if (selectedFile != null) {
			        	  fileName.setText(selectedFile);
			          }
		    	  }
		      }
		});
		
		
		Label descLabel = new Label(this, 0);
		descLabel.setText(description);
		descLabel.setFont(makeItalicFont(descLabel));
		GridDataUtil.applyGridData(descLabel).horizontalSpan(3).verticalIndent(VINDENT_GROUP);
		
		return fileName;
	}
	
	private org.eclipse.swt.widgets.List addFileSequence(String name, String nicename, String description, 
			boolean isDir, boolean isRequired) {
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		//RowLayout rowLayout = new RowLayout();
		//rowLayout.
		//composite.setLayout(new RowLayout())
		GridDataUtil.applyGridData(composite).horizontalSpan(3).verticalIndent(VINDENT_GROUP);
		
		Label label = new Label(composite, 0);
		if (isRequired) {
			label.setText("* " + nicename);
		}
		else {
			label.setText(nicename);
		}
		
		Label descLabel = new Label(composite, 0);
		descLabel.setText(description);
		descLabel.setFont(makeItalicFont(descLabel));
		
		final org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List(composite, SWT.SINGLE);
		list.setSize(TEXTBOX_MIN_WIDTH, TEXTBOX_MIN_WIDTH-50);
		list.setData(name);
		
		Composite buttonsComposite = new Composite(composite, SWT.NONE);
		buttonsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		final Button addFileButton = new Button(buttonsComposite, SWT.NONE);
		addFileButton.setText("Add");
		addFileButton.setSize(50, 50);

		final Button removeFileButton = new Button(buttonsComposite, SWT.NONE);
		removeFileButton.setText("Remove");
		removeFileButton.setEnabled(false);
		removeFileButton.setSize(50, 50);

//		final Button moveUpFileButton = new Button(buttonsComposite, SWT.NONE);
//		moveUpFileButton.setText("Move up");
//		moveUpFileButton.setEnabled(false);
//
//		final Button moveDownFileButton = new Button(buttonsComposite, SWT.NONE);
//		moveDownFileButton.setText("Move down");
//		moveDownFileButton.setEnabled(false);

		
		final boolean _isDir = isDir;
		addFileButton.addSelectionListener(new SelectionAdapter() {
			 public void widgetSelected(SelectionEvent e) {
				  if (_isDir) {
					  DirectoryDialog dialog = new DirectoryDialog(guiController.getWindow().getShell(), SWT.NULL);
			    	  String selectedFile = dialog.open();
			          if (selectedFile != null) {
			        	  list.add(selectedFile);
			          }
				  }
				  else {
					  FileDialog dialog = new FileDialog(guiController.getWindow().getShell(), SWT.NULL);
			    	  String selectedFile = dialog.open();
			          if (selectedFile != null) {
			        	  list.add(selectedFile);
			          }
				  }
		      }
		});
		
		list.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (list.getSelectionCount() > 0) {
					removeFileButton.setEnabled(true);
//					if (list.getItemCount() > 1) {
//						moveUpFileButton.setEnabled(true);
//						moveDownFileButton.setEnabled(true);
//					}
					
				}
			}
		});
		
		removeFileButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (list.getSelectionCount() > 0) {
					int idx = list.getSelectionIndex();
					list.remove(idx);
					removeFileButton.setEnabled(false);
//					moveUpFileButton.setEnabled(false);
//					moveDownFileButton.setEnabled(false);
				}
			}
		});
		
		composite.layout();
		return list;
		
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

	public ArrayList<Widget> getRequiredArguments() {
		return requiredArguments;
	}

	
	
}
