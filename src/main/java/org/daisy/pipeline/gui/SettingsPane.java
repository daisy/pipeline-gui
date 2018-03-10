package org.daisy.pipeline.gui;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.daisy.pipeline.gui.utils.Settings;
import org.daisy.pipeline.gui.utils.Settings.PrefCategories;
import org.daisy.pipeline.gui.utils.Settings.Prefs;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class SettingsPane extends BorderPane {
    
    private SettingsListPane listPane;
    private MainPane mainPane;

    public void build() {
        List<String> categoryNames = Arrays.asList(PrefCategories.values()).stream()
                .map(PrefCategories::val)
                .collect(Collectors.toList());
        
        listPane = new SettingsListPane(FXCollections.observableList(categoryNames));
        mainPane = new MainPane(categoryNames);
        
        this.setLeft(listPane);
        this.setCenter(mainPane);
    }
    
    
/*-------------------------------------------------------------------------------------------------------*/
    
    private class SettingsListPane extends ScrollPane {
        private ListView<String> categoryList;
        
        public SettingsListPane(ObservableList<String> categoryNames) {
            categoryList = new ListView<String>();
            categoryList.setItems(categoryNames);
            categoryList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
                @Override
                public ListCell<String> call(ListView<String> param) {
                    return new CategoryCell();
                }
            });
            categoryList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            categoryList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue,
                        String newValue) {
                    mainPane.showSettingsBox(newValue);
                }
            });
            
            this.setContent(categoryList);
        }
        
    }
    
    static class CategoryCell extends ListCell<String> {
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null)
                this.setText(item);
        }
    }
    
    private class MainPane extends ScrollPane {
        
        private StackPane stackPane;
        
        public MainPane(List<String> categoryNames) {
            stackPane = new StackPane();
            for (String categoryName: categoryNames)
                addSettingsBox(categoryName);
            this.setContent(stackPane);
        }
        
        private void addSettingsBox(String categoryName) {
            VBox box = new VBox();
            box.setUserData(categoryName);
            layoutSettingsBox(box, categoryName);
            stackPane.getChildren().add(box);
        }
        
        private void layoutSettingsBox(VBox box, String categoryName) {
            if (categoryName.equals(PrefCategories.JOB_OPTIONS.val())) {
                Label defDirLbl = new Label(Prefs.DEF_OUT_DIR.key());
                TextField defDirFld = new TextField(Settings.getString(Prefs.DEF_OUT_DIR));
                defDirFld.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        Settings.putString(Prefs.DEF_OUT_DIR, defDirFld.getText());
                    }
                });
                box.getChildren().addAll(defDirLbl, defDirFld);
            }
        }
        
        void showSettingsBox(String categoryName) {
            for (Node n: stackPane.getChildren())
                if (n.getUserData().equals(categoryName))
                    n.toFront();
        }
    
    }
    
}
