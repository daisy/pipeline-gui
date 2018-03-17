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
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class SettingsPane extends BorderPane {
    
    private Stage parentStage;
    
    private AnchorPane mainPane;
    
    public SettingsPane(Stage parentStage) {
        if (parentStage == null)
            throw new NullPointerException("parentStage is null: ok and cancel buttons will not be able to close the stage");
        this.parentStage = parentStage;
    }

    public void build() {
        /* CENTER */
        mainPane = new AnchorPane();
        
        List<String> categoryNames = Arrays.asList(PrefCategories.values()).stream()
                .map(PrefCategories::val)
                .collect(Collectors.toList());
        categoryNames.forEach(name -> addSettingsBox(name));
        setCenter(mainPane);
        
        /* BOTTOM */
        HBox buttonRowBox = new HBox();
        Button okBtn = new Button("Ok");
        HBox spacerBox = new HBox();
        Button cancelBtn = new Button("Cancel");
        //actions
        okBtn.setOnAction(a -> parentStage.close());
        cancelBtn.setOnAction(a -> parentStage.close());
        //LAF
        BorderPane.setMargin(buttonRowBox, new Insets(15));
        buttonRowBox.getStyleClass().add("row");
        HBox.setHgrow(spacerBox, Priority.ALWAYS);
        
        buttonRowBox.getChildren().addAll(okBtn, spacerBox, cancelBtn);
        setBottom(buttonRowBox);
    }
    
    private void addSettingsBox(String categoryName) {
        BorderPane categorySet = new BorderPane();
        Label title = new Label(categoryName);
        GridPane settingsGrid = new GridPane();
        
        //LAF
        categorySet.getStyleClass().add("category-set");
        title.getStyleClass().remove("label");
        title.getStyleClass().add("subtitle");
        categorySet.prefWidthProperty().bind(widthProperty());
        
        layoutSettingsBox(settingsGrid, categoryName);
        categorySet.setTop(title);
        categorySet.setCenter(settingsGrid);
        mainPane.getChildren().add(categorySet);
    }
    
    private void layoutSettingsBox(GridPane settingsGrid, String categoryName) {
        if (categoryName.equals(PrefCategories.JOB_OPTIONS.val())) {
            
            Label defInDirLbl = new Label(Prefs.DEF_IN_DIR.key());
            
            CheckBox defInDirChk = new CheckBox();
            TextField defInDirFld = new TextField();
            Button defInDirBtn = new Button("Browse");
            Button copyToOutDirBtn = new Button("▼");
            
            Label defOutDirLbl = new Label(Prefs.DEF_OUT_DIR.key());
            
            CheckBox defOutDirChk = new CheckBox();
            TextField defOutDirFld = new TextField();
            Button defOutDirBtn = new Button("Browse");
            Button copyToInDirBtn = new Button("▲");
            
            /* Default Input Directory */
            //actions
            defInDirChk.setOnAction(a -> {
                Settings.putBoolean(Prefs.DEF_IN_DIR_ENABLED, defInDirChk.isSelected());
                defInDirFld.setDisable(!defInDirChk.isSelected());
                defInDirBtn.setDisable(!defInDirChk.isSelected());
                copyToOutDirBtn.setDisable(!defInDirChk.isSelected());
            });
            defInDirBtn.setOnAction(a -> {
                DirectoryChooser dirChooser = new DirectoryChooser();
                if (Settings.getBoolean(Prefs.DEF_IN_DIR_ENABLED))
                    dirChooser.setInitialDirectory(new File(Settings.getString(Prefs.DEF_IN_DIR)));
                File f = dirChooser.showDialog(null);
                if (f != null) {
                    defInDirFld.setText(f.getAbsolutePath());
                    Settings.putString(Prefs.DEF_IN_DIR, defInDirFld.getText());
                }
            });
            copyToOutDirBtn.setOnAction(a -> {
                defOutDirFld.setText(defInDirFld.getText());
                Settings.putString(Prefs.DEF_OUT_DIR, defInDirFld.getText());
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
            //style
            GridPane.setHgrow(defInDirFld, Priority.ALWAYS);
            
            /* Default Output Directory */
            //actions
            defOutDirChk.setOnAction(a -> {
                Settings.putBoolean(Prefs.DEF_OUT_DIR_ENABLED, defOutDirChk.isSelected());
                defOutDirFld.setDisable(!defOutDirChk.isSelected());
                defOutDirBtn.setDisable(!defOutDirChk.isSelected());
                copyToOutDirBtn.setDisable(!defOutDirChk.isSelected());
            });
            defOutDirBtn.setOnAction(a -> {
                DirectoryChooser dirChooser = new DirectoryChooser();
                if (Settings.getBoolean(Prefs.DEF_OUT_DIR_ENABLED))
                    dirChooser.setInitialDirectory(new File(Settings.getString(Prefs.DEF_OUT_DIR)));
                File f = dirChooser.showDialog(null);
                if (f != null) {
                    defOutDirFld.setText(f.getAbsolutePath());
                    Settings.putString(Prefs.DEF_OUT_DIR, defOutDirFld.getText());
                }
            });
            copyToInDirBtn.setOnAction(a -> {
                defInDirFld.setText(defOutDirFld.getText());
                Settings.putString(Prefs.DEF_IN_DIR, defOutDirFld.getText());
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
            //style
            GridPane.setHgrow(defOutDirFld, Priority.ALWAYS);
            
            /* SettingsGrid */
            //LAF
            settingsGrid.getStyleClass().add("grid-pane");
            settingsGrid.prefWidthProperty().bind(widthProperty());
            GridPane.setHgrow(defOutDirFld, Priority.ALWAYS);
            //add
            settingsGrid.add(defInDirLbl, 0, 0, 4, 1);
            settingsGrid.add(defInDirChk, 0, 1);
            settingsGrid.add(defInDirFld, 1, 1, 1, 1);
            settingsGrid.add(defInDirBtn, 2, 1);
            settingsGrid.add(copyToOutDirBtn, 3, 1);
            
            settingsGrid.add(defOutDirLbl, 0, 2, 4, 1);
            settingsGrid.add(defOutDirChk, 0, 3);
            settingsGrid.add(defOutDirFld, 1, 3, 1, 1);
            settingsGrid.add(defOutDirBtn, 2, 3);
            settingsGrid.add(copyToInDirBtn, 3, 3);
        }
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
