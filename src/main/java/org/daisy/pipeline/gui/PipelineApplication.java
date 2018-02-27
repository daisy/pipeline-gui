package org.daisy.pipeline.gui;

import java.util.Optional;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipelineApplication extends Application {

        public static String USERGUIDE_URL = "https://github.com/daisy/pipeline-gui/wiki";

        private static final Logger logger = LoggerFactory.getLogger(PipelineApplication.class);

        @Override
        public void start(final Stage stage) {
                
                // Show a splash screen while Pipeline is starting up
                if (!ServiceRegistry.getInstance().isReady()) {
                        createSplash().run(
                                new Task<Runnable>() {
                                        @Override
                                        protected Runnable call() throws InterruptedException {
                                                try {
                                                        updateMessage("Starting application...");
                                                        Optional<ServiceRegistry> services = loadServices();
                                                        if (services.isPresent())
                                                                return () -> showMainWindow(stage, services.get());
                                                        else
                                                                throw new RuntimeException("Gave up waiting for the Pipeline services");
                                                } catch (RuntimeException e) {
                                                        e.printStackTrace();
                                                        throw e;
                                                }
                                        }
                                }
                        );
                } else {
                        showMainWindow(stage, ServiceRegistry.getInstance());
                }
        }

        public interface Splash {
                public void run(Task<Runnable> mainWindow);
        }
        
        public static Splash createSplash() {
                Stage splashStage = new Stage(StageStyle.TRANSPARENT);
                String SPLASH_IMAGE = PipelineApplication.class
                        .getResource("/org/daisy/pipeline/gui/resources/pipeline-logo.png").toExternalForm();
                int SPLASH_WIDTH = 600;
                int SPLASH_HEIGHT = 300;
                Pane splashLayout = new VBox();
                ImageView splashBackGround = new ImageView(new Image(SPLASH_IMAGE, 200, 200, true, true));
                Label splashText = new Label();
                ProgressBar progress = new ProgressBar();
                progress.setPrefWidth(SPLASH_WIDTH - 9);
                splashLayout.getChildren().addAll(splashBackGround, progress, splashText);
                splashText.setAlignment(Pos.CENTER);
                splashLayout.setStyle(
                        "-fx-padding: 5;" +
                        "-fx-background-color: #5577FF;" +
                        "-fx-border-width: 2; " +
                        "-fx-border-color: #0000FF;");
                splashBackGround.setAccessibleText("DAISY Pipeline 2: starting");
                Scene splashScene = new Scene(splashLayout, Color.TRANSPARENT);
                splashStage.setScene(splashScene);
                Rectangle2D bounds = Screen.getPrimary().getBounds();
                splashStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
                splashStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
                splashStage.setAlwaysOnTop(true);
                return mainWindow -> {
                        splashStage.show();
                        final long started = System.currentTimeMillis();
                        splashText.textProperty().bind(mainWindow.messageProperty());
                        progress.progressProperty().bind(mainWindow.progressProperty());
                        mainWindow.stateProperty().addListener((observableValue, oldState, newState) -> {
                                if (newState == Worker.State.SUCCEEDED) {
                                        progress.progressProperty().unbind();
                                        progress.setProgress(1);
                                        splashText.textProperty().unbind();
                                        splashText.setText("");
                                        mainWindow.getValue().run();
                                        long keepVisibleForAtLeast = 2000; // 2 seconds
                                        long visibleFor = System.currentTimeMillis() - started;
                                        if (visibleFor < keepVisibleForAtLeast)
                                                try {
                                                        Thread.sleep(keepVisibleForAtLeast - visibleFor); }
                                                catch (InterruptedException e) {
                                                        throw new RuntimeException(e); }
                                        splashStage.hide();
                                } else if (newState == Worker.State.FAILED) {
                                        // FIXME: show that there has been an error
                                        splashStage.hide();
                                }
                        });
                        new Thread(mainWindow).start();
                };
        }

        private void showMainWindow(Stage stage, ServiceRegistry services) {
                MainWindow mainWindow = new MainWindow(services, getHostServices());
                stage.setScene(mainWindow.getScene());
                stage.setTitle("DAISY Pipeline 2");
                stage.show();
        }

        private static Optional<ServiceRegistry> loadServices() {
                try {
                        ServiceRegistry services = ServiceRegistry.getInstance();
                        // timeout after 20 seconds
                        if (services.waitUntilReady(20000))
                                return Optional.of(services);
                        else
                                return Optional.empty();
                } catch (InterruptedException e) {
                        throw new RuntimeException("Interrupted while waiting for services", e);
                }
        }
        
        public void stop() {
                ServiceRegistry.getInstance().getGUIService().stop();
        }
}
