package org.daisy.pipeline.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.HostServices;
import javafx.stage.Stage;

import org.daisy.pipeline.clients.Client;

import com.google.common.util.concurrent.Monitor;

public class PipelineApplication extends Application {

        
        private ServiceRegistry services=null;
        private final Monitor monitor = new Monitor();
        private final Monitor.Guard isReady= new Monitor.Guard(monitor) {
                public boolean isSatisfied() {
                        return services!=null;
                }
        };


        public void setServiceRegistry(ServiceRegistry services){
                monitor.enter();
                this.services=services;
                monitor.leave();
        }
        
        @Override
        public void start(final Stage stage) {
                Platform.runLater(new Runnable() {
                        @Override
                        public void run(){
                                try {
                                        ServiceRegistry.getInstance().notifyReady(PipelineApplication.this);
                                        ServiceRegistry services=PipelineApplication.this.services;
                                        monitor.enterWhen(PipelineApplication.this.isReady);
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
                                        e.printStackTrace();
                                }
                                finally {
                                        monitor.leave();
                                }

                        }
                });
        }


}
