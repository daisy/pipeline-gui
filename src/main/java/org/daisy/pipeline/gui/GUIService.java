package org.daisy.pipeline.gui;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.job.JobManagerFactory;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.webserviceutils.storage.WebserviceStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GUIService implements Runnable
{
        private static final Logger logger = LoggerFactory.getLogger(GUIService.class);
        @Override
        public void run() 
        {
                //Otherwise launch will block
                new Thread(){
                        public void run(){
                                javafx.application.Application.launch(PipelineApplication.class);
                                
                        }
                }.start();
                logger.debug("Main Module is loaded!");
        }


        public void setScriptRegistry(ScriptRegistry scriptRegistry) {
                ServiceRegistry.getInstance().setScriptRegistry(scriptRegistry);
        }
        public void setJobManagerFactory(JobManagerFactory jobManagerFactory) {
                ServiceRegistry.getInstance().setJobManagerFactory(jobManagerFactory);
        }
        public void setEventBusProvider(EventBusProvider eventBusProvider) {
                ServiceRegistry.getInstance().setEventBusProvider(eventBusProvider);
        }
        public void setWebserviceStorage(WebserviceStorage webserviceStorage) {
                ServiceRegistry.getInstance().setWebserviceStorage(webserviceStorage);
        }

}
