package org.daisy.pipeline.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.daisy.common.messaging.Message;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.job.JobUUIDGenerator;
import org.daisy.pipeline.job.StatusMessage;
import org.daisy.pipeline.job.Job.Status;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;

// listen to events and update the GUI
// also keep track of the messages for each job
public class EventBusListener {

	GuiController guiController;
	JobManager jobManager;
	private MessageList messages = new MessageList();
    //private List<StatusHolder> statusList= Collections.synchronizedList(new LinkedList<StatusHolder>());

	public EventBusListener(EventBusProvider eventBusProvider, GuiController guiController, JobManager jobManager) {
		this.jobManager = jobManager;
		this.guiController = guiController;
		eventBusProvider.get().register(this);
		this.guiController.setEventBusListener(this);
	}
	
	public synchronized List<Message> getMessages(JobId jobId) {
		return messages.getMessages(jobId);
	}
	
	@Subscribe
    public synchronized void handleMessage(Message msg) {
    	guiController.messageUpdate(msg);
		
    }

    @Subscribe
    public void handleStatus(StatusMessage message) {
        System.out.println(String.format("+++++++Getting status %s %s",message.getJobId(),message.getStatus()));
        System.out.println("I'M HEREEEEEEEEEEEEe--------========================================");
    	guiController.statusUpdate(message);
//    	StatusHolder holder= new StatusHolder();
//    	holder.status=message.getStatus();
//    	Optional<Job> job = jobManager.getJob(message.getJobId());
//    	if(job.isPresent()){
//    		holder.job=job.get();
//    	}
//    	statusList.add(holder);
    }


	
	private class MessageList {
        HashMap<JobId, List<Message>> messages;

        public MessageList() {
                messages = new HashMap<JobId, List<Message>>();
        }
        public synchronized List<Message> getMessages(JobId jobId) {
                return messages.get(jobId);
        }
        public synchronized MessageList copy(){
                MessageList copy=new MessageList();     
                for (Map.Entry<JobId,List<Message>> entry:this.messages.entrySet()){
                        copy.messages.put(entry.getKey(),new LinkedList<Message>(entry.getValue()));    
                }
                return copy;
        }

        public synchronized void addMessage(JobId jobId, Message msg) {
                List<Message> list;
                if (containsJob(jobId)) {
                        list = messages.get(jobId);
                }
                else {
                        list = new ArrayList<Message>();
                        messages.put(jobId, list);
                }
                list.add(msg);
        }
        public synchronized Set<JobId> getJobs() {
                return Sets.newHashSet(messages.keySet());
        }

        public synchronized void removeJob(JobId jobId) {
                messages.remove(jobId);
        }

        public synchronized boolean containsJob(JobId jobId) {
                return messages.containsKey(jobId);
        }

        public synchronized boolean isEmpty() {
                return messages.isEmpty();
        }

        // for debugging
        public synchronized void printList(JobId jobId) {
                for (Message msg : messages.get(jobId)) {
                        System.out.println("#" + msg.getSequence() + ", job #" + msg.getJobId());
                }
        }
	}
	/*
     * In order to not lose the reference 
     * to the job if it's been deleted
     */
//    private class StatusHolder{
//            Status status;
//            Job job;
//    }
}
