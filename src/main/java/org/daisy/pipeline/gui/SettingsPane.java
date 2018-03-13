package org.daisy.pipeline.gui;

import java.io.File;
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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
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
            VBox vbox = new VBox();
            vbox.setUserData(categoryName);
            layoutSettingsBox(vbox, categoryName);
            stackPane.getChildren().add(vbox);
        }
        
        private void layoutSettingsBox(VBox vbox, String categoryName) {
            if (categoryName.equals(PrefCategories.JOB_OPTIONS.val())) {
                Label defInDirLbl = new Label(Prefs.DEF_IN_DIR.key());
                
                HBox defInDirHBox = new HBox();
                CheckBox defInDirChk = new CheckBox();
                TextField defInDirFld = new TextField();
                Button defInDirBtn = new Button("Browse");
                Button copyToOutDirBtn = new Button("▼");
                
                Label defOutDirLbl = new Label(Prefs.DEF_OUT_DIR.key());
                
                HBox defOutDirHBox = new HBox();
                CheckBox defOutDirChk = new CheckBox();
                TextField defOutDirFld = new TextField();
                Button defOutDirBtn = new Button("Browse");
                Button copyToInDirBtn = new Button("▲");
                
                /* Default Input Directory */
                //actions
                defInDirChk.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Settings.putBoolean(Prefs.DEF_IN_DIR_ENABLED, defInDirChk.isSelected());
                        defInDirFld.setDisable(!defInDirChk.isSelected());
                        defInDirBtn.setDisable(!defInDirChk.isSelected());
                        copyToOutDirBtn.setDisable(!defInDirChk.isSelected());
                    }
                });
                defInDirBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        DirectoryChooser dirChooser = new DirectoryChooser();
                        if (Settings.getBoolean(Prefs.DEF_IN_DIR_ENABLED))
                            dirChooser.setInitialDirectory(new File(Settings.getString(Prefs.DEF_IN_DIR)));
                        File f = dirChooser.showDialog(null);
                        if (f != null) {
                            defInDirFld.setText(f.getAbsolutePath());
                            Settings.putString(Prefs.DEF_IN_DIR, defInDirFld.getText());
                        }
                    }
                });
                copyToOutDirBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        defOutDirFld.setText(defInDirFld.getText());
                        Settings.putString(Prefs.DEF_OUT_DIR, defInDirFld.getText());
                    }
                });
                
                //default vals
                defInDirChk.setSelected(Settings.getBoolean(Prefs.DEF_IN_DIR_ENABLED));
                defInDirFld.setText(Settings.getString(Prefs.DEF_IN_DIR));
                defInDirFld.setDisable(!defInDirChk.isSelected());
                defInDirFld.setEditable(false);
                defInDirBtn.setDisable(!defInDirChk.isSelected());
                
                //accessibility
                defInDirChk.setAccessibleText("Toggle default input directory.");
                defInDirChk.setTooltip(new Tooltip("Enable usage of default input directory for input file browsers in Job Options."));
                defInDirChk.selectedProperty().addListener(new CheckBoxToggleListener(defInDirChk));
                defInDirFld.setAccessibleText(defInDirLbl.getText());
                defInDirFld.setTooltip(new Tooltip("Default directory used for input file browsers in Job Options."));
                defInDirBtn.setTooltip(new Tooltip("Browse for folder."));
                copyToOutDirBtn.setTooltip(new Tooltip("Copy path to output directory setting below."));
                
                //add
                vbox.getChildren().add(defInDirLbl);
                defInDirHBox.getChildren().addAll(defInDirChk, defInDirFld, defInDirBtn, copyToOutDirBtn);
                vbox.getChildren().add(defInDirHBox);
                
                /* Default Output Directory */
                //actions
                defOutDirChk.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Settings.putBoolean(Prefs.DEF_OUT_DIR_ENABLED, defOutDirChk.isSelected());
                        defOutDirFld.setDisable(!defOutDirChk.isSelected());
                        defOutDirBtn.setDisable(!defOutDirChk.isSelected());
                        copyToOutDirBtn.setDisable(!defOutDirChk.isSelected());
                    }
                });
                defOutDirBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        DirectoryChooser dirChooser = new DirectoryChooser();
                        if (Settings.getBoolean(Prefs.DEF_OUT_DIR_ENABLED))
                            dirChooser.setInitialDirectory(new File(Settings.getString(Prefs.DEF_OUT_DIR)));
                        File f = dirChooser.showDialog(null);
                        if (f != null) {
                            defOutDirFld.setText(f.getAbsolutePath());
                            Settings.putString(Prefs.DEF_OUT_DIR, defOutDirFld.getText());
                        }
                    }
                });
                copyToInDirBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        defInDirFld.setText(defOutDirFld.getText());
                        Settings.putString(Prefs.DEF_IN_DIR, defOutDirFld.getText());
                    }
                });
                
                //default vals
                defOutDirChk.setSelected(Settings.getBoolean(Prefs.DEF_OUT_DIR_ENABLED));
                defOutDirFld.setText(Settings.getString(Prefs.DEF_OUT_DIR));
                defOutDirFld.setDisable(!defOutDirChk.isSelected());
                defOutDirFld.setEditable(false);
                defOutDirBtn.setDisable(!defOutDirChk.isSelected());
                
                //accessibility
                defOutDirChk.setAccessibleText("Toggle default output directory.");
                defOutDirChk.setTooltip(new Tooltip("Enable usage of default output directory for output file browsers in Job Options."));
                defOutDirChk.selectedProperty().addListener(new CheckBoxToggleListener(defOutDirChk));
                defOutDirFld.setAccessibleText(defOutDirLbl.getText());
                defOutDirFld.setTooltip(new Tooltip("Default directory used for output file browsers in Job Options."));
                defOutDirBtn.setTooltip(new Tooltip("Browse for folder."));
                copyToInDirBtn.setTooltip(new Tooltip("Copy path to input directory setting above."));
                
                //add
                vbox.getChildren().add(defOutDirLbl);
                defOutDirHBox.getChildren().addAll(defOutDirChk, defOutDirFld, defOutDirBtn, copyToInDirBtn);
                vbox.getChildren().add(defOutDirHBox);
            }
        }
        
        void showSettingsBox(String categoryName) {
            for (Node n: stackPane.getChildren())
                if (n.getUserData().equals(categoryName))
                    n.toFront();
        }
        
        class CheckBoxToggleListener implements ChangeListener<Boolean> {
            private CheckBox chk;
            
            CheckBoxToggleListener(CheckBox chk) {
                this.chk = chk;
            }
            
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                    Boolean newValue) {
                if (newValue) 
                    chk.setTooltip(new Tooltip(chk.getTooltip().getText().replaceAll("Enable", "Disable")));
                else
                    chk.setTooltip(new Tooltip(chk.getTooltip().getText().replaceAll("Disable", "Enable")));
            }
        }
    
    }
    
}
