package org.daisy.pipeline.gui;

import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.clients.ClientStorage;
import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.job.JobManagerFactory;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.XProcScriptService;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.BundleContext;

import com.google.common.base.Supplier;

import org.daisy.pipeline.gui.MainWindow;
 

public class EntryPoint{

    
    /** The job manager. */
    private JobManagerFactory jobManagerFactory;

    /** The script registry. */
    private ScriptRegistry scriptRegistry;
   // private ClientStorage clientStorage;

    public void init(BundleContext ctxt) {
        System.out.println("GUI init");
        MainWindow window = new MainWindow(null, this);
        window.setBlockOnOpen(true);
        window.open();
        Display.getCurrent().dispose();
    }
    
    // there was a naming conflict with "close()" so i used "uninit()" 
    public void uninit() throws Exception {
        System.out.println("GUI uninit");
    }
    
//    
//    private Supplier<JobManager> jobManager= new Supplier<JobManager>() {
//
//        @Override
//        public JobManager get() {
//                return jobManagerFactory.createFor(clientStorage.defaultClient());
//        }
//
//};

    /**
     * Gets the job manager.
     *
     * @return the job manager
     */
    public JobManager getJobManager(Client client) {
            return jobManagerFactory.createFor(client);
    }

    /**
     * Sets the job manager.
     *
     * @param jobManager the new job manager
     */
    public void setJobManagerFactory(JobManagerFactory jobManagerFactory) {
        System.out.println("Setting job manager factory");
            this.jobManagerFactory = jobManagerFactory;
    }
    
    
    /**
     * Gets the script registry.
     *
     * @return the script registry
     */
    public ScriptRegistry getScriptRegistry() {
            return scriptRegistry;
    }

    /**
     * Sets the script registry.
     *
     * @param scriptRegistry the new script registry
     */
    public void setScriptRegistry(ScriptRegistry scriptRegistry) {
        System.out.println("setting script registry for GUI");
            this.scriptRegistry = scriptRegistry;
    }
//    public void setWebserviceStorage(WebserviceStorage storage) {
//        this.clientStorage = storage.getClientStorage();
//        
//}
    
    
}
