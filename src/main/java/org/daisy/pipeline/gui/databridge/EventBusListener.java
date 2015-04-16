package org.daisy.pipeline.gui.databridge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.daisy.common.messaging.Message;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.gui.MainWindow;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobIdFactory;
import org.daisy.pipeline.job.JobIdGenerator;
import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.job.JobUUIDGenerator;
import org.daisy.pipeline.job.StatusMessage;
import org.daisy.pipeline.job.Job.Status;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
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
    public synchronized void handleMessage(Message msg) {
    	String jobId = msg.getJobId();
    	Job job = jobManager.getJob(JobIdFactory.newIdFromString(jobId)).get();
    	dataManager.addMessage(job, msg.getText(), msg.getLevel());
    }

    @Subscribe
    public void handleStatus(StatusMessage message) {
    	JobId jobId =  message.getJobId();
    	Job job = jobManager.getJob(jobId).get();
    	dataManager.updateStatus(job, message.getStatus());
    }
	
}
