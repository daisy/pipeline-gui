package org.daisy.pipeline.gui.databridge;

import org.daisy.common.messaging.Message.Level;
import org.daisy.pipeline.gui.MainWindow;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.Job.Status;
import org.daisy.pipeline.script.XProcScript;
import org.daisy.pipeline.script.XProcScriptService;

import javafx.collections.ObservableList;

// communicate with the gui-friendly list of ObservableJob objects
// represent the scripts in a gui-friendly way
public class DataManager {
	
	MainWindow main;
	
	public DataManager(MainWindow main) {
		this.main = main;
		initData();
	}
	
	
	public void updateStatus(Job job, Status status) {
		int i = findJob(job);
		if (i == -1) {
			return;
		}
		main.getJobData().get(i).setStatus(status);
		
	}
	
	public void addMessage(Job job, String message, Level level) {
		int i = findJob(job);
		if (i == -1) {
			return;
		}
		main.getJobData().get(i).addMessage(message, level);
	}
	
	public void addJob(Job job) {
		ObservableJob objob = new ObservableJob(job);
		main.getJobData().add(objob);
	}
	
	public void removeJob(Job job) {
		int i = findJob(job);
		if (i == -1) {
			return;
		}
		main.getJobData().remove(i);
	}
	
	public int findJob(Job job) {
		ObservableList<ObservableJob> jobData = main.getJobData();
		for (ObservableJob objob : jobData) {
			if (objob.getJob().getId().toString().equals(job.getId().toString())) {
				return jobData.indexOf(objob);
			}
		}
		return -1;
	}
	
	private void addScript(XProcScript xprocScript) {
		Script script = new Script(xprocScript);
		main.getScriptData().add(script);
	}
	
	// called once at startup
	// put any jobs already in the pipeline into the list
	// read the list of scripts
	private void initData() {
		for (Job job : main.getJobManager().getJobs()) {
			addJob(job);
		}
		for (XProcScriptService scriptService : main.getScriptRegistry().getScripts()) {
			XProcScript xprocScript = scriptService.load();
			addScript(xprocScript);
		}
		
	}
	
	
}
