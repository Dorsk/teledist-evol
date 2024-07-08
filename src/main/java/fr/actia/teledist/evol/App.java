package fr.actia.teledist.evol;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;



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
