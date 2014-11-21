package org.daisy.pipeline.gui.handlers;

import org.daisy.pipeline.gui.GuiController;
import org.eclipse.jface.action.Action;

public abstract class GuiActionAbstract extends Action {

	
	protected GuiController guiController;
	public GuiActionAbstract(GuiController guiController) {
		super("");
		this.guiController = guiController;
	}
	public void setInfo(String label, String description,int accelerator) {
		setText(label);
		setDescription(description);
		setToolTipText(description);
		setAccelerator(accelerator);
	}
}
