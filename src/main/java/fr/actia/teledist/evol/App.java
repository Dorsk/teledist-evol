package fr.actia.teledist.evol;

import fr.actia.teledist.evol.login.LoginView;
import fr.actia.teledist.evol.views.download.DownloadGammeView;
import fr.actia.teledist.evol.views.gammes.CreateGammeView;
import fr.actia.teledist.evol.views.gammes.UpdateGammeView;
import fr.actia.teledist.evol.views.usines.UsinesPreviewView;
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
    private LoginView loginView;
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)  {

        this.primaryStage = primaryStage;
         // Initialize main layout
        mainLayout = new BorderPane();
        MenuBar menuBar = new MenuBar();
        Menu gammeMenu = new Menu("Gamme");
        MenuItem createGammeMenuItem = new MenuItem("Créer une gamme");
        createGammeMenuItem.setOnAction(e -> showCreateGammeView());
        MenuItem updateGammeMenuItem = new MenuItem("Update gamme");
        updateGammeMenuItem.setOnAction(e -> showUpdateGammeView());
        gammeMenu.getItems().addAll(createGammeMenuItem, updateGammeMenuItem);

        Menu downloadGammeMenu = new Menu("Download");
        MenuItem downloadGammeMenuItem = new MenuItem("Download gamme");
        downloadGammeMenuItem.setOnAction(e -> showDownloadGammeView());
        downloadGammeMenu.getItems().addAll(downloadGammeMenuItem);

        Menu usineMenu = new Menu("Usines");
        MenuItem usineMenuItem = new MenuItem("Preview");
        usineMenuItem.setOnAction(e -> showUsinesPreviewView());
        usineMenu.getItems().addAll(usineMenuItem);

        menuBar.getMenus().add(gammeMenu);
        menuBar.getMenus().add(downloadGammeMenu);
        menuBar.getMenus().add(usineMenu);
        mainLayout.setTop(menuBar);

        // Show initial view
        showLoginView();

        Scene scene = new Scene(mainLayout, 1300, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Teledist Evolution");
        primaryStage.show();

    }

    private void showLoginView(){
        loginView = new LoginView();
        mainLayout.setCenter(loginView.getView(mainLayout));
    }

    private void showUpdateGammeView(){
        UpdateGammeView updateGammeView = new UpdateGammeView();
        mainLayout.setCenter(updateGammeView.getView());
    }
 
    private void showCreateGammeView() {
        CreateGammeView updateGammeView = new CreateGammeView();
        mainLayout.setCenter(updateGammeView.getView());
    }

    private void showDownloadGammeView() {
        DownloadGammeView downloadGammeView = new DownloadGammeView();
        mainLayout.setCenter(downloadGammeView.getView(this.primaryStage));
    }

    private void showUsinesPreviewView() {
        UsinesPreviewView usinesPreviewView = new UsinesPreviewView();
        mainLayout.setCenter(usinesPreviewView.getView(this.primaryStage));
    }    
}
