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
	
	public ObservableJob addJob(Job job) {
		ObservableJob objob = new ObservableJob(job);
		main.getJobData().add(objob);
		return objob;
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
			// for some reason, comparing the job objects directly doesn't work
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
		for (XProcScriptService scriptService : main.getScriptRegistry().getScripts()) {
			XProcScript xprocScript = scriptService.load();
			addScript(xprocScript);
		}
		
		// these jobs are already in the pipeline so we need to create a BoundScript representation
		for (Job job : main.getJobManager().getJobs()) {
			ObservableJob objob = addJob(job);
			for (Script script : main.getScriptData()) {
				if (script.getName().equals(job.getContext().getScript().getName())) {
					createBoundScriptForExistingJob(script, objob);
				}
			}
		}
		
		
	}
	
	private void createBoundScriptForExistingJob(Script script, ObservableJob objob) {
		BoundScript boundScript = new BoundScript(script);
		Job job = objob.getJob();
		
		// TODO fill in the bound script parameters (input URIs etc)
		objob.setBoundScript(boundScript);
	}
	
	public BoundScript cloneBoundScript(BoundScript boundScript) {
		BoundScript newBoundScript = new BoundScript(boundScript.getScript());
		
		for (ScriptFieldAnswer answer : boundScript.getInputFields()) {
			ScriptFieldAnswer newAnswer = newBoundScript.getInputByName(answer.getField().getName());
			newAnswer.setAnswer(answer.getAnswer());
		}
		for (ScriptFieldAnswer answer : boundScript.getOptionFields()) {
			ScriptFieldAnswer newAnswer = newBoundScript.getOptionByName(answer.getField().getName());
			newAnswer.setAnswer(answer.getAnswer());
		}
		for (ScriptFieldAnswer answer : boundScript.getOutputFields()) {
			ScriptFieldAnswer newAnswer = newBoundScript.getOutputByName(answer.getField().getName());
			newAnswer.setAnswer(answer.getAnswer());
		}
		return newBoundScript;
	}
	
}
