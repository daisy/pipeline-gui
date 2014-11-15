package org.daisy.pipeline.gui;

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

/**
 * OSGI activator for the GUI
 */
public class Activator implements BundleActivator, Runnable {

	private MainWindow window = null;
	private ScriptRegistry scriptRegistry = null;
	private JobManagerFactory jobManagerFactory = null;
	private EventBusProvider eventBusProvider = null;
	private WebserviceStorage webserviceStorage = null;
	private BundleContext bundleContext = null;

	private static final Logger logger = LoggerFactory.getLogger(Activator.class);
	
	private final Monitor monitor = new Monitor();
	private final Monitor.Guard pipelineServicesAvailable = new Monitor.Guard(monitor) {
		public boolean isSatisfied() {
			return scriptRegistry != null && jobManagerFactory != null 
					&& webserviceStorage != null && eventBusProvider != null;
		}
	};
	
	
	public void run() {
		try {
			monitor.enterWhen(pipelineServicesAvailable);
			logger.debug("Running GUI");
			window = new MainWindow(null, scriptRegistry, jobManagerFactory, 
					webserviceStorage.getClientStorage().defaultClient(), eventBusProvider,
					bundleContext);
			window.setBlockOnOpen(true);
		    window.open();
		    Display.getCurrent().dispose();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		finally {
			monitor.leave();
		}
		
	}

	


	public void start(final BundleContext context) throws Exception {
		bundleContext = context;
		context.registerService(Runnable.class.getName(), this, null);
		
		ServiceTracker<ScriptRegistry, ScriptRegistry> scriptRegistryTracker = new ServiceTracker<ScriptRegistry, ScriptRegistry>(
				context, ScriptRegistry.class,
				new ServiceTrackerCustomizer<ScriptRegistry, ScriptRegistry>() {

					@Override
					public ScriptRegistry addingService(ServiceReference<ScriptRegistry> reference) {
						logger.debug("Setting script registry");
						monitor.enter();
						try {
							scriptRegistry = context.getService(reference);
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
						logger.debug("Setting job manager factory");
						monitor.enter();
						try {
							jobManagerFactory  = context.getService(reference);
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
						logger.debug("Setting web service storage");
						monitor.enter();
						try {
							webserviceStorage  = context.getService(reference);
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
						logger.debug("Setting web service storage");
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

	public void stop(BundleContext context) throws Exception {
		this.window.exit();
	}

}
