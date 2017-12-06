package org.daisy.pipeline.gui.databridge;

import org.daisy.pipeline.gui.MainWindow;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobIdFactory;
import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.job.StatusMessage;

import com.google.common.eventbus.Subscribe;

// listen to changes coming from the pipeline framework
public class EventBusListener {

	JobManager jobManager;
	DataManager dataManager;
	public EventBusListener(MainWindow main) {
		this.jobManager = main.getJobManager();
		this.dataManager = main.getDataManager();
	}
	
    @Subscribe
    public void handleStatus(StatusMessage message) {
    	//System.out.println("##################### GUI EVENT BUS STATUS");
    	JobId jobId =  message.getJobId();
    	Job job = jobManager.getJob(jobId).get();
    	dataManager.updateStatus(job, message.getStatus());
    }
	
}
