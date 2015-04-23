package org.daisy.pipeline.gui;

import org.daisy.pipeline.gui.databridge.ObservableJob;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class Sidebar extends VBox {
	TableView<ObservableJob> table;
	MainWindow main;
	
	
	public Sidebar(MainWindow main) {
		super();
		this.main = main;
		initControls();
	}
	
	public ObservableJob getSelectedJob() {
		return table.getSelectionModel().getSelectedItem();
	}
	
	private void initControls() {
		table = new TableView<ObservableJob>();
		this.setPadding(new Insets(10));
	    this.setSpacing(8);
	
	    Text title = new Text("Jobs");
	    title.setFont(Font.font("Arial", FontWeight.BOLD, 25));
	    this.getChildren().add(title);
	
	    VBox.setVgrow(table, Priority.ALWAYS);
	    
	    table.setItems(main.getJobData());
	    TableColumn<ObservableJob,String> idCol = new TableColumn<ObservableJob,String>("ID");
	    TableColumn<ObservableJob,String> statusCol = new TableColumn<ObservableJob,String>("Status");
	    
	    idCol.setCellValueFactory(new Callback<CellDataFeatures<ObservableJob, String>, ObservableValue<String>>() {
	        public ObservableValue<String> call(CellDataFeatures<ObservableJob, String> celldata) {
	            return new ReadOnlyObjectWrapper<String>(celldata.getValue().getJob().getId().toString());
	        }
	     });
	    
	    statusCol.setCellValueFactory(new PropertyValueFactory<ObservableJob, String>("status"));
	    
	    table.getColumns().setAll(idCol, statusCol);

	    table.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<ObservableJob>() {
                public void changed(ObservableValue<? extends ObservableJob> ov, 
                    ObservableJob old_val, ObservableJob new_val) {
                    main.notifySidebarSelectChange(new_val);
            }
        });
    
	    
	    this.getChildren().add(table);
	    
	}
	
}
