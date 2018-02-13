package org.daisy.pipeline.gui;

import com.google.common.util.concurrent.Monitor;

import org.daisy.pipeline.datatypes.DatatypeRegistry;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.job.JobManagerFactory;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.webserviceutils.storage.WebserviceStorage;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* This is a hack to make OSGi services available to objects that are not instantiated by
 * the OSGi framework, such as PipelineApplication.
 */
public class ServiceRegistry {

        private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

        private static ServiceRegistry instance = null;
        
        private ServiceRegistry() {
        }

        static ServiceRegistry getInstance() {
                if (instance == null)
                        instance = new ServiceRegistry();
                return instance;
        }

        private ScriptRegistry scriptRegistry = null;
        private JobManagerFactory jobManagerFactory = null;
        private EventBusProvider eventBusProvider = null;
        private WebserviceStorage webserviceStorage = null;
        private DatatypeRegistry datatypeRegistry = null;
        private GUIService guiService = null;

        private final Monitor monitor = new Monitor();
        private final Monitor.Guard servicesAvailable = new Monitor.Guard(monitor) {
                public boolean isSatisfied() {
                        return instance != null &&
                                ServiceRegistry.this.scriptRegistry != null &&
                                ServiceRegistry.this.jobManagerFactory != null &&
                                ServiceRegistry.this.eventBusProvider != null &&
                                ServiceRegistry.this.webserviceStorage != null &&
                                ServiceRegistry.this.datatypeRegistry != null &&
                                ServiceRegistry.this.guiService != null;
                }
        };

        public boolean isReady() {
                return servicesAvailable.isSatisfied();
        }
        
        public void waitUntilReady() throws InterruptedException {
                try {
                        monitor.enterWhen(this.servicesAvailable);
                        logger.debug("setting serviceregistry");
                } catch (InterruptedException ie) {
                        throw ie;
                } finally {
                        monitor.leave();
                }
        }

        /**
         * @return the scriptRegistry
         */
        public ScriptRegistry getScriptRegistry() {
                return scriptRegistry;
        }

        /**
         * @param scriptRegistry the scriptRegistry to set
         */
        public void setScriptRegistry(ScriptRegistry scriptRegistry) {
                this.monitor.enter();
                this.scriptRegistry = scriptRegistry;
                this.monitor.leave();
        }

        /**
         * @return the jobManagerFactory
         */
        public JobManagerFactory getJobManagerFactory() {
                return jobManagerFactory;
        }

        /**
         * @param jobManagerFactory the jobManagerFactory to set
         */
        public void setJobManagerFactory(JobManagerFactory jobManagerFactory) {
                this.monitor.enter();
                this.jobManagerFactory = jobManagerFactory;
                this.monitor.leave();
        }

        /**
         * @return the eventBusProvider
         */
        public EventBusProvider getEventBusProvider() {
                return eventBusProvider;
        }

        /**
         * @param eventBusProvider the eventBusProvider to set
         */
        public void setEventBusProvider(EventBusProvider eventBusProvider) {
                this.monitor.enter();
                this.eventBusProvider = eventBusProvider;
                this.monitor.leave();
        }

        /**
         * @return the webserviceStorage
         */
        public WebserviceStorage getWebserviceStorage() {
                return webserviceStorage;
        }

        /**
         * @param webserviceStorage the webserviceStorage to set
         */
        public void setWebserviceStorage(WebserviceStorage webserviceStorage) {
                this.monitor.enter();
                this.webserviceStorage = webserviceStorage;
                this.monitor.leave();
        }

        /**
         * @return the datatypeRegistry
         */
        public DatatypeRegistry getDatatypeRegistry() {
                return datatypeRegistry;
        }

        /**
         * @param datatypeRegistry the datatypeRegistry to set
         */
        public void setDatatypeRegistry(DatatypeRegistry datatypeRegistry) {
                this.monitor.enter();
                this.datatypeRegistry = datatypeRegistry;
                this.monitor.leave();
        }

        public void setGUIService(GUIService guiService) {
                this.monitor.enter();
                this.guiService = guiService;
                this.monitor.leave();
        }

        public GUIService getGUIService() {
                return this.guiService;
        }

        @Component
        public static class ServiceBinder {

                @Reference(
                        name = "script-registry",
                        unbind = "-",
                        service = ScriptRegistry.class,
                        cardinality = ReferenceCardinality.MANDATORY,
                        policy = ReferencePolicy.STATIC
                )
                public void setScriptRegistry(ScriptRegistry scriptRegistry) {
                        ServiceRegistry.getInstance().setScriptRegistry(scriptRegistry);
                }

                @Reference(
                        name = "job-manager-factory",
                        unbind = "-",
                        service = JobManagerFactory.class,
                        cardinality = ReferenceCardinality.MANDATORY,
                        policy = ReferencePolicy.STATIC
                )
                public void setJobManagerFactory(JobManagerFactory jobManagerFactory) {
                        ServiceRegistry.getInstance().setJobManagerFactory(jobManagerFactory);
                }

                @Reference(
                        name = "event-bus-provider",
                        unbind = "-",
                        service = EventBusProvider.class,
                        cardinality = ReferenceCardinality.MANDATORY,
                        policy = ReferencePolicy.STATIC
                )
                public void setEventBusProvider(EventBusProvider eventBusProvider) {
                        ServiceRegistry.getInstance().setEventBusProvider(eventBusProvider);
                }

                @Reference(
                        name = "webservice-storage",
                        unbind = "-",
                        service = WebserviceStorage.class,
                        cardinality = ReferenceCardinality.MANDATORY,
                        policy = ReferencePolicy.STATIC
                )
                public void setWebserviceStorage(WebserviceStorage webserviceStorage) {
                        ServiceRegistry.getInstance().setWebserviceStorage(webserviceStorage);
                }

                @Reference(
                        name = "datatype-registry",
                        unbind = "-",
                        service = DatatypeRegistry.class,
                        cardinality = ReferenceCardinality.MANDATORY,
                        policy = ReferencePolicy.STATIC
                )
                public void setDatatypeRegistry(DatatypeRegistry datatypeRegistry) {
                        ServiceRegistry.getInstance().setDatatypeRegistry(datatypeRegistry);
                }
        }
}
