package org.daisy.pipeline.gui;

import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.job.JobManagerFactory;
import org.daisy.pipeline.job.impl.DefaultJobExecutionService;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.webserviceutils.storage.WebserviceStorage;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.Monitor;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import javafx.application.Application;

public class Activator extends Application implements BundleActivator,Runnable
{
        private Stage stage;
        private MainWindow mainWindow = null;
        private static ScriptRegistry scriptRegistry = null;
        private static JobManagerFactory jobManagerFactory = null;
        private static EventBusProvider eventBusProvider = null;
        private static WebserviceStorage webserviceStorage = null;
        private BundleContext bundleContext = null;
        private static final Logger logger = LoggerFactory.getLogger(Activator.class);
        private static final Monitor monitor = new Monitor();
        private final Monitor.Guard pipelineServicesAvailable = new Monitor.Guard(monitor) {
                public boolean isSatisfied() {
                        System.out.println("Checking satisfied!");
                        System.out.println(scriptRegistry);
                        System.out.println(jobManagerFactory);
                        System.out.println(webserviceStorage);
                        System.out.println(eventBusProvider);
                        return scriptRegistry != null && jobManagerFactory != null
                                && webserviceStorage != null && eventBusProvider != null;
                }
        };
        public void start(final Stage stage) {
                Platform.runLater(new Runnable()
                                {
                                        @Override
                                        public void run()
                {
                        try {
                        	// this causes things not to work at all
	                        monitor.enterWhen(pipelineServicesAvailable);
	                        Activator.this.stage = stage;
	                        System.out.println("Gui run");
	                        // the real GUI
//	                        Client client = webserviceStorage.getClientStorage().defaultClient();
//	                        System.out.println("HELLO");
//	                        mainWindow = new MainWindow(scriptRegistry, jobManagerFactory,
//	                        		client, 
//	                        		eventBusProvider, bundleContext);
//	                        
//	                        stage.setScene(mainWindow.getScene());
//	                		stage.setTitle("DAISY Pipeline 2");
//	                		stage.show();
	                		
	                        // the test GUI
	                        // first check if the services are available
	                        if (scriptRegistry == null) {
	                        	System.out.println("!!!!!!!!!!!!!!!!!SCRIPT REG IS NULL!");
	                        }
	                        if (jobManagerFactory == null) {
	                        	System.out.println("!!!!!!!!!!!!!!!!!JOB MAN FACTORY IS NULL");
	                        }
	                        if (webserviceStorage == null) {
	                        	System.out.println("!!!!!!!!!!!!!!!!! WEB SERV STORAGE IS NULL");
	                        }
	                        if (eventBusProvider == null) {
	                        	System.out.println("!!!!!!!!!!!!!!!!! EVENT BUS PROVIDER IS NULL");
	                        }
	                        BorderPane pane = new BorderPane();
	                        Scene scene = new Scene(pane, 400, 200);
	                        pane.setCenter(new Label("This is a JavaFX Scene in a Stage"));
	                        stage.setScene(scene);
	                        stage.show();
                        }
                        catch (InterruptedException e) {
                                e.printStackTrace();
                        }
                        finally {
                                monitor.leave();
                        }
                        
                }
                });
        }
        @Override
        public void run() 
        {
                //Otherwise launch will block
                new Thread(){
                        public void run(){
                                javafx.application.Application.launch(Activator.class);
                        }
                }.start();
                System.out.println("Main Module is loaded!");
        }
        public void start(final BundleContext context) throws Exception {
                bundleContext = context;
                context.registerService(Runnable.class.getName(), this, null);

                logger.debug("+++++++++++++++++++ in start");
                ServiceTracker<ScriptRegistry, ScriptRegistry> scriptRegistryTracker = new ServiceTracker<ScriptRegistry, ScriptRegistry>(
                                context, ScriptRegistry.class,
                                new ServiceTrackerCustomizer<ScriptRegistry, ScriptRegistry>() {

                                        @Override
                                        public ScriptRegistry addingService(ServiceReference<ScriptRegistry> reference) {
                                                logger.debug("++++++++++++++++++++++++++++++Setting script registry");
                                                monitor.enter();
                                                try {
                                                        scriptRegistry = context.getService(reference);
                                                }catch(Exception e){
                                                        logger.error("Error setting service"+e.getMessage());
                                                }
                                                finally {
                                                        monitor.leave();
                                                }

                                                return scriptRegistry;
                                        }

                @Override
                public void modifiedService(ServiceReference<ScriptRegistry> reference, ScriptRegistry service) {
                        // TODO replace script registry ?

                }

                @Override
                public void removedService(ServiceReference<ScriptRegistry> reference, ScriptRegistry service) {
                        // TODO close window ?
                }
                });
                scriptRegistryTracker.open();

                ServiceTracker<JobManagerFactory, JobManagerFactory> jobManagerFactoryTracker = new ServiceTracker<JobManagerFactory, JobManagerFactory>(
                                context,
                                JobManagerFactory.class,
                                new ServiceTrackerCustomizer<JobManagerFactory, JobManagerFactory>() {

                                        @Override
                                        public JobManagerFactory addingService(ServiceReference<JobManagerFactory> reference) {
                                                logger.debug("+++++Setting job manager factory");
                                                monitor.enter();
                                                try {
                                                       jobManagerFactory  = context.getService(reference);
                                                       System.out.println("JOB MANAGER: "+jobManagerFactory);
                                                }catch(Exception e){
                                                        logger.error("Error setting service"+e.getMessage());
                                                }
                                                finally {
                                                        monitor.leave();
                                                }

                                                return jobManagerFactory;
                                        }

                @Override
                public void modifiedService(ServiceReference<JobManagerFactory> reference, JobManagerFactory service) {
                        // TODO replace job manager ?

                }

                @Override
                public void removedService(ServiceReference<JobManagerFactory> reference, JobManagerFactory service) {
                        // TODO close window ?
                }
                });
                jobManagerFactoryTracker.open();

                ServiceTracker<WebserviceStorage, WebserviceStorage> webserviceStorageTracker = new ServiceTracker<WebserviceStorage, WebserviceStorage>(
                                context,
                                WebserviceStorage.class,
                                new ServiceTrackerCustomizer<WebserviceStorage, WebserviceStorage>() {

                                        @Override
                                        public WebserviceStorage addingService(ServiceReference<WebserviceStorage> reference) {
                                                logger.debug("++++ Setting web service storage");
                                                monitor.enter();
                                                try {
                                                        webserviceStorage  = context.getService(reference);
                                                }catch(Exception e){
                                                        logger.error("Error setting service"+e.getMessage());
                                                }
                                                finally {
                                                        monitor.leave();
                                                }

                                                return webserviceStorage;
                                        }

                @Override
                public void modifiedService(ServiceReference<WebserviceStorage> reference, WebserviceStorage service) {
                        // TODO replace webservice storage?

                }

                @Override
                public void removedService(ServiceReference<WebserviceStorage> reference, WebserviceStorage service) {
                        // TODO close window ?
                }
                });
                webserviceStorageTracker.open();

                ServiceTracker<EventBusProvider, EventBusProvider> eventBusProviderTracker = new ServiceTracker<EventBusProvider, EventBusProvider>(
                                context,
                                EventBusProvider.class,
                                new ServiceTrackerCustomizer<EventBusProvider, EventBusProvider>() {

                                        @Override
                                        public EventBusProvider addingService(ServiceReference<EventBusProvider> reference) {
                                                logger.debug("+++Setting event bus provider");
                                                monitor.enter();
                                                try {
                                                        eventBusProvider  = context.getService(reference);
                                                } 
                                                finally {
                                                        monitor.leave();
                                                }

                                                return eventBusProvider;
                                        }

                @Override
                public void modifiedService(ServiceReference<EventBusProvider> reference, EventBusProvider service) {
                        // TODO replace event bus provider?

                }

                @Override
                public void removedService(ServiceReference<EventBusProvider> reference, EventBusProvider service) {
                        // TODO close window ?
                }
                });
                eventBusProviderTracker.open();

        }
        @Override
        public void stop(BundleContext context) throws Exception
        {
                Platform.runLater(new Runnable()
                                {
                                        @Override
                                        public void run()
                {
                        stage.close();
                }
                });
                System.out.println("Main Module is unloaded!");
        }


}
