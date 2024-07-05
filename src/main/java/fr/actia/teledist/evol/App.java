package fr.actia.teledist.evol;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
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


/**
 * JavaFX App
 */
public class App extends Application {

    private static final String GITHUB_TOKEN = "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"; // Replace with your GitHub personal access token
    private String GITHUB_URL = "https://api.github.com/repos/Dorsk/react-tracerun/git/trees/main?recursive=1"; // Replace with your GitHub API URL for artifacts
    private static final String GITHUB_CLIENT_ID = "Dorsk"; // Replace with your GitHub Client ID
    private static final String GITHUB_CLIENT_SECRET = "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"; // Replace with your GitHub Client Secret
    private static final String GITHUB_REDIRECT_URI = "http://localhost:8080"; // Replace with your redirect URI
    
    CheckBoxTreeItem<ArtifactItem> currentItem = null;
    TreeView<ArtifactItem> treeView = new TreeView<>();
    private List<CheckBoxTreeItem> checkBoxes = new ArrayList<>();
    private List<CheckBox> checkBoxesUsines = new ArrayList<>();
    private TextField gammeField;
    private TextField versionField;
    private TextField vehicleField;
    private ComboBox urlComboBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)  {

        treeView = new TreeView<>();
        getArtifactsFromGitHub();

        Button submitButton = new Button("Enregistrer");
        submitButton.setOnAction(event -> handleSubmit());

        // panneau de gauche
        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10));
        gammeField = new TextField();
        gammeField.setPromptText("Nom de gamme");
        versionField = new TextField();
        versionField.setPromptText("Version");
        vehicleField = new TextField();
        vehicleField.setPromptText("Véhicule");

        // "Usines" Section
        Label usinesLabel = new Label("Usines:");
        VBox usinesBox = new VBox(5);
        for (int i = 1; i <= 10; i++) {
            CheckBox usineCheckBox = new CheckBox("Usine " + i);
            usineCheckBox.setId("Usine " + i);
            usinesBox.getChildren().add(usineCheckBox);
            checkBoxesUsines.add(usineCheckBox);
        }

        leftPane.getChildren().addAll(new Label("Nom de gamme:"), 
            gammeField, new Label("Version:"), versionField, 
            new Label("Véhicule:"), vehicleField, usinesLabel, usinesBox, submitButton);


        // rendre dynamique
        urlComboBox = new ComboBox();
        urlComboBox.getItems().addAll(
            "https://api.github.com/repos/Dorsk/react-tracerun/git/trees/main?recursive=1",
            "https://api.github.com/repos/Dorsk/teledist-evol/git/trees/main?recursive=1",
            "https://api.github.com/repos/Dorsk/react-tracerun/git/trees/main?recursive=1"
        );
 
        urlComboBox.setPromptText(urlComboBox.getItems().get(0).toString());
        urlComboBox.setOnAction(e -> {
            try {
                handleChangeCombo();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        VBox rightPane = new VBox(10, urlComboBox, treeView);
        rightPane.setPadding(new Insets(10));
        VBox.setVgrow(treeView, Priority.ALWAYS);

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(leftPane, rightPane);
        splitPane.setDividerPositions(0.3);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setCenter(splitPane);

        Scene scene = new Scene(mainLayout, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Teledist Evolution");
        primaryStage.show();
    }

    protected void getArtifactsFromGitHub() {
        
        String jsonResponse = null;
        // Get artifacts
        ArtifactoryClient artiClient = new ArtifactoryClient();
        try {
            jsonResponse = artiClient.getArtifacts(GITHUB_URL);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Handle the response, assuming it's JSON
        if (jsonResponse != null) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
            JsonArray artifactsArray = jsonObject.getAsJsonArray("tree");

            // Creation du treeviewer
            treeView.setRoot(buildTree(artifactsArray));
            treeView.setCellFactory(CheckBoxTreeCell.<ArtifactItem>forTreeView());
        } else {
            System.err.println("Error fetching artifacts: jsonResponse is " + jsonResponse);
        }
    }

    private CheckBoxTreeItem<ArtifactItem> buildTree(JsonArray  nodes) {
        CheckBoxTreeItem<ArtifactItem> rootItem = new CheckBoxTreeItem(new ArtifactItem("Repository" + GITHUB_URL, "tree", ""));
        rootItem.setExpanded(true);
        Map<String, CheckBoxTreeItem<ArtifactItem>> pathMap = new HashMap<>();

        for (JsonElement nodeJS : nodes) {
            JsonObject node = nodeJS.getAsJsonObject();
            String path = node.get("path").getAsString();
            String type = node.get("type").getAsString();
            String url = node.get("url").getAsString();
            addPathToTree(rootItem, pathMap, path, type, url);
        }
        return rootItem;
    }

    private void addPathToTree(CheckBoxTreeItem<ArtifactItem> rootItem, Map<String, CheckBoxTreeItem<ArtifactItem>> pathMap, String path, String type, String url) {
        String[] parts = path.split("/");
        currentItem = rootItem;

        for (String part : parts) {
            String fullPath = getFullPath(currentItem, part);
            currentItem = pathMap.computeIfAbsent(fullPath, k -> {
                CheckBoxTreeItem<ArtifactItem> newItem = createTreeItem(part, type, url);
                newItem.selectedProperty().addListener((obs, wasChecked, isNowChecked) -> {
                    if (isNowChecked) {
                        checkBoxes.add(newItem);
                    } else {
                        checkBoxes.remove(newItem);
                    }
                });
                currentItem.getChildren().add(newItem);
                return newItem;
            });
        }
    }

    private CheckBoxTreeItem<ArtifactItem> createTreeItem(String part, String type, String url) {
        return new CheckBoxTreeItem(new ArtifactItem(part, type, url)) {
            @Override
            public String getValue() {
                return checkBox.getText();
            }
        };
    }

    private String getFullPath(CheckBoxTreeItem<ArtifactItem> item, String part) {
        StringBuilder fullPath = new StringBuilder(part);
        TreeItem<ArtifactItem> current = item;

        while (current != null && current.getParent() != null) {
            fullPath.insert(0, current.getValue() + "/");
            current = current.getParent();
        }

        return fullPath.toString();
    }

    private void handleChangeCombo() throws Exception {
        GITHUB_URL = urlComboBox.getValue().toString();
        getArtifactsFromGitHub();
    }

    private void handleSubmit() {
        List<ArtifactItem> selectedItems = new ArrayList<>();
        for (CheckBoxTreeItem checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                ArtifactItem artiItem =  (ArtifactItem)checkBox.getValue();
                if(!artiItem.getType().equals("tree")){
                    selectedItems.add((ArtifactItem)checkBox.getValue());
                }
            }
        }
       

        // ToDo : envoyer dans la base de données
        System.out.println("Gamme : " + gammeField.getText());
        System.out.println("version : " + versionField.getText());
        System.out.println("Vehicule : " + vehicleField.getText());

        String insertQuery = "INSERT INTO gamme (nom, vehicule) VALUES (?, ?)";
        DatabaseTool dbTool = new DatabaseTool();
        int idRowsGamme = dbTool.executeInsertQuery(insertQuery, gammeField.getText(), vehicleField.getText());


        System.out.println("Liste des usines : ");
        for (CheckBox usine : checkBoxesUsines) {
            if (usine.isSelected()) {
                System.out.println(" - " + usine.getId());
            }
        }

        for (ArtifactItem item : selectedItems) {
            System.out.println("Selected items: " + item.getName() + " | " + item.getType() + " | " + item.getUrl());
        }
    }
}