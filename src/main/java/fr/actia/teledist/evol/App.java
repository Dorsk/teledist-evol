package fr.actia.teledist.evol;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.actia.teledist.evol.tools.DatabaseTool;
import fr.actia.teledist.evol.treeviewer.ArtifactItem;
import fr.actia.teledist.evol.treeviewer.CheckboxesUsines;


/**
 * JavaFX App
 */
public class App extends Application {

    private BorderPane mainLayout;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)  {

         // Initialize main layout
        mainLayout = new BorderPane();
        MenuBar menuBar = new MenuBar();
        Menu gammeMenu = new Menu("Gamme");
        MenuItem createGammeMenuItem = new MenuItem("Créer une gamme");
        createGammeMenuItem.setOnAction(e -> showCreateGammeView());
        MenuItem updateGammeMenuItem = new MenuItem("Update gamme");
        updateGammeMenuItem.setOnAction(e -> showUpdateGammeView());

        gammeMenu.getItems().addAll(createGammeMenuItem, updateGammeMenuItem);

        menuBar.getMenus().add(gammeMenu);
        mainLayout.setTop(menuBar);

        // Show initial view
        showCreateGammeView();

        Scene scene = new Scene(mainLayout, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Teledist Evolution");
        primaryStage.show();

    }

    private void showUpdateGammeView(){
        UpdateGammeView updateGammeView = new UpdateGammeView();
        mainLayout.setCenter(updateGammeView.getView());
    }
 
    private void showCreateGammeView() {
        CreateGammeView updateGammeView = new CreateGammeView();
        mainLayout.setCenter(updateGammeView.getView());
    }

}
