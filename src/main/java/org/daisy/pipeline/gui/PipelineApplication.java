package org.daisy.pipeline.gui;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;

import org.daisy.pipeline.clients.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipelineApplication extends Application {

        private static final Logger logger = LoggerFactory.getLogger(PipelineApplication.class);

        
        private ServiceRegistry services=null;


        public void setServiceRegistry(ServiceRegistry services){
                this.services=services;
        }
        
        @Override
        public void start(final Stage stage) {
                Platform.runLater(new Runnable() {
                        @Override
                        public void run(){
                                try {
                                        ServiceRegistry.getInstance().notifyReady(PipelineApplication.this);
                                        ServiceRegistry services=PipelineApplication.this.services;
                                        HostServices hostServices = getHostServices();
                                        System.out.println("Gui run");
                                        Client client = services.getWebserviceStorage().getClientStorage().defaultClient();
                                        System.out.println("HELLO");
                                        MainWindow mainWindow = new MainWindow(services.getScriptRegistry(), services.getJobManagerFactory(),
                                                client, 
                                                services.getEventBusProvider(), 
                                                hostServices);

                                        stage.setScene(mainWindow.getScene());
                                        stage.setTitle("DAISY Pipeline 2");
                                        stage.show();

                                }
                                catch (InterruptedException e) {
                                        logger.error("Interrupted while wating for services",e);
                                }

                        }
                });
        }


}
