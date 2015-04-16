package org.daisy.pipeline.gui.databridge;

import org.daisy.common.messaging.Message.Level;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.Job.Status;

import javafx.collections.ObservableList;

// communicate with the gui-friendly list of ObservableJob objects
public class DataManager {
	
	ObservableList<ObservableJob> jobData;
	
	public DataManager(ObservableList<ObservableJob> jobData) {
		this.jobData = jobData;
	}
	
	
	public void updateStatus(Job job, Status status) {
		int i = findJob(job);
		if (i == -1) {
			return;
		}
		jobData.get(i).setStatus(status);
		
	}
	
	public void addMessage(Job job, String message, Level level) {
		int i = findJob(job);
		if (i == -1) {
			return;
		}
		jobData.get(i).addMessage(message, level);
	}
	
	public void addJob(Job job) {
		ObservableJob objob = new ObservableJob(job);
		jobData.add(objob);
	}
	
	public void removeJob(Job job) {
		int i = findJob(job);
		if (i == -1) {
			return;
		}
		jobData.remove(i);
	}
	
	public int findJob(Job job) {
		for (ObservableJob objob : jobData) {
			if (objob.getJob() == job) {
				return jobData.indexOf(objob);
			}
		}
		return -1;
	}
	
	
	
}
